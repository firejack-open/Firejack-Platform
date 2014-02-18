/**
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */
package net.firejack.platform.service.registry.broker.install;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.deployment.domain.WarStatus;
import net.firejack.platform.api.registry.domain.DatabaseAction;
import net.firejack.platform.api.registry.domain.PackageAction;
import net.firejack.platform.api.registry.model.DatabaseActionType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.mina.bean.Status;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.firejack.platform.api.deployment.domain.WarStatus.*;

@Component
@TrackDetails
@ProgressComponent(weight = 200, showLogs = true)
public class RedeployPackageArchiveBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<Status>> implements Runnable {
    public static final int MB = 1024 * 1024;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private ManuallyProgress progress;
    private boolean reboot;
    private final Object lock = new Object();

    @Override
    protected ServiceResponse<Status> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String packageLookup = request.getData().getIdentifier();

        Thread check = new Thread(this, "Memory Check");
        check.setDaemon(true);
        check.start();

        try {
            PackageModel packageModel = packageStore.findByLookup(packageLookup);
            if (packageModel == null) {
                throw new BusinessFunctionException("Could not find package by lookup: " + packageLookup);
            }

            Long packageId = packageModel.getId();
            PackageAction packageAction = new PackageAction(packageId);
            List<DatabaseAction> actions = getDatabaseActions(packageId);
            packageAction.setDatabaseActions(actions);

            OPFEngine.RegistryService.uninstallPackageArchive(packageAction);
            OPFEngine.RegistryService.archive(packageId);
            OPFEngine.RegistryService.generateWar(packageId);
            OPFEngine.RegistryService.installPackageArchive(packageAction);

            ping(packageModel);
        } finally {
            check.interrupt();
        }

        return new ServiceResponse("Redeploy war successfully", true);
    }

    private List<DatabaseAction> getDatabaseActions(Long packageId) {
        Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels = packageStore.findAllWithDatabaseById(packageId);

        List<DatabaseAction> actions = new ArrayList<DatabaseAction>(associatedDatabaseModels.size());
        for (Map.Entry<RegistryNodeModel, DatabaseModel> entry : associatedDatabaseModels.entrySet()) {
            DatabaseModel databaseModel = entry.getValue();
            DatabaseAction action = new DatabaseAction();
            action.setId(databaseModel.getId());
            action.setAction(DatabaseActionType.UPGRADE);
            actions.add(action);
        }

        return actions;
    }

    private void ping(PackageModel model) throws IOException {
        if (StringUtils.isBlank(model.getServerName()) || model.getPort() == null) {
            progress.status(model.getName() + " system not assignee", 1, LogLevel.WARN);
            return;
        }

        String s = String.format("http://%s:%d%s/status/war", model.getServerName(), model.getPort(), model.getUrlPath());
        URL url = new URL(s);

        WarStatus status = null;
        int count = 90;
        do {
            try {
                synchronized (lock) {
                    lock.wait(1000);
                }

                URLConnection urlc = url.openConnection();
                urlc.setReadTimeout(1000);
                urlc.setConnectTimeout(1000);

                int code = ((HttpURLConnection) urlc).getResponseCode();
                if (code == HttpServletResponse.SC_CONFLICT) {
                    status = WAIT;
                } else if (code == HttpServletResponse.SC_OK) {
                    status = DONE;
                } else if (code == HttpServletResponse.SC_NOT_FOUND) {
                    status = ERROR;
                }
            } catch (SocketTimeoutException e) {
                status = WAIT;
            } catch (IOException e) {
                status = ERROR;
            } catch (InterruptedException e) {
                status = WAIT;
            }
            count--;
        } while (status != DONE && count > 0);

        if (count <= 0) {
            progress.status(model.getName() + " Startup error please see logs ", 1, LogLevel.ERROR);
        } else {
            String siteUrl = "http://" + model.getServerName() + ":" + String.valueOf(model.getPort()) + model.getUrlPath();
            progress.status(model.getName() + "<a href=\"" + siteUrl + "\" target=\"blank\"> " + siteUrl + "</a> is up ", 1, LogLevel.INFO);
        }
    }

    @Override
    public void run() {
        MemoryPoolMXBean memoryPoolMXBean = null;
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (poolMXBean.getName().contains("Perm Gen"))
                memoryPoolMXBean = poolMXBean;
        }

        Thread thread = Thread.currentThread();

        try {
            while (!reboot) {
                if (thread.isInterrupted())
                    break;

                synchronized (lock) {
                    lock.wait(1000);
                }

                MemoryUsage usage = memoryPoolMXBean.getUsage();
                long fs = (usage.getMax() - usage.getUsed()) / MB;

                if (fs <= 10) {
                    OPFEngine.RegistryService.restart();
                    reboot = true;
                }
            }
        } catch (InterruptedException e) {
            logger.trace(e.getMessage(), e);
        }
    }
}
