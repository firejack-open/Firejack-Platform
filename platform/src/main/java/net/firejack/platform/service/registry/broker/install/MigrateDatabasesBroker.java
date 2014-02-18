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
import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.DatabaseAction;
import net.firejack.platform.api.registry.domain.PackageAction;
import net.firejack.platform.api.registry.model.DatabaseActionType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.installer.IDatabaseManager;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@TrackDetails
@ProgressComponent(weight = 50, showLogs = true)
public class MigrateDatabasesBroker extends ServiceBroker<ServiceRequest<PackageAction>, ServiceResponse> {

	@Autowired
	private IDatabaseManager databaseManager;
	@Autowired
	private FileHelper helper;
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private IDomainStore domainStore;
	@Autowired
	private IDatabaseStore databaseStore;
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private ManuallyProgress progress;

	@Override
	protected ServiceResponse perform(ServiceRequest<PackageAction> request) throws Exception {
		PackageAction packageAction = request.getData();
        if (packageAction == null || packageAction.getDatabaseActions() == null
                || packageAction.getDatabaseActions().isEmpty() ||
                packageAction.getDatabaseActions().get(0).getAction() != DatabaseActionType.MIGRATE) {
            progress.end(new ServiceResponse("Incorrect input parameters", false));
        } else {
            Long packageId = packageAction.getId();
            PackageModel packageRN = packageStore.findWithSystemById(packageId);
            String name = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();

            InputStream stream = OPFEngine.FileStoreService.download(
                    OpenFlame.FILESTORE_BASE, name, helper.getVersion(), String.valueOf(packageId),
                    String.valueOf(packageRN.getVersion()));

            progress.status("Loading package meta-data.",2, LogLevel.INFO);

            String packageXmlRandomName = SecurityHelper.generateRandomSequence(16);
            File packageXml = File.createTempFile(packageXmlRandomName, null);
            FileOutputStream packageXmlUploadFileOutputStream = new FileOutputStream(packageXml);
            IOUtils.copy(stream, packageXmlUploadFileOutputStream);
            IOUtils.closeQuietly(packageXmlUploadFileOutputStream);

            Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels = packageStore.findAllWithDatabaseById(packageId);

            progress.status("Preparing for data migration.",2, LogLevel.INFO);

            DatabaseAction action = packageAction.getDatabaseActions().get(0);

            executeAsynchronously(associatedDatabaseModels, action, packageRN, packageXml);
        }

		return new ServiceResponse("Preparing for data migration.", true);
	}

    private void executeAsynchronously(final Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels,
                                       final DatabaseAction action, final PackageModel packageRN,
                                       final File packageXml) {
//        final ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
                try {
                    Map.Entry<RegistryNodeModel, DatabaseModel> targetEntry =
                            getDomainLookupByDatabase(associatedDatabaseModels, action.getSourceId());
                    String lookup = targetEntry.getKey().getLookup();

                    Database targetDatabase = factory.convertTo(Database.class, targetEntry.getValue());
                    DataSource targetDataSource = databaseManager.getDataSource(targetDatabase);

                    DatabaseModel sourceModel = databaseStore.findById(action.getId());
                    Database sourceDatabase = factory.convertTo(Database.class, sourceModel);
                    databaseManager.createDatabase(sourceDatabase);

                    DataSource sourceDataSource = databaseManager.getDataSource(sourceDatabase);
                    List<DomainModel> domains = new ArrayList<DomainModel>();
                    if (packageRN.getLookup().equals(lookup)) {
                        packageRN.setDatabase(sourceModel);
                    } else {
                        DomainModel model = domainStore.findByLookup(lookup);
                        model.setDatabase(sourceModel);
                        domains.add(model);
                    }

                    progress.status("Start migration process...",2, LogLevel.INFO);
                    packageInstallationService.migrate(packageXml, targetDataSource, sourceDataSource);
                    progress.status("Migration process has been completed.",2, LogLevel.INFO);

                    packageStore.saveOrUpdate(packageRN);
                    domainStore.saveOrUpdateAll(domains);
                } catch (Throwable th) {
                    logger.error(th.getMessage(), th);
//                    executor.shutdown();
                }
//            }
//        });
    }

	private Map.Entry<RegistryNodeModel, DatabaseModel> getDomainLookupByDatabase(Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels, Long id) {
		for (Map.Entry<RegistryNodeModel, DatabaseModel> associatedDatabaseModel : associatedDatabaseModels.entrySet()) {
			if (associatedDatabaseModel.getValue().getId().equals(id)) {
				return associatedDatabaseModel;
			}
		}
		return null;
	}
}
