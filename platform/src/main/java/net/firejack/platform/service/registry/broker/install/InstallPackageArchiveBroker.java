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
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.installer.IDeployService;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TrackDetails
@Component("installPackageArchiveBroker")
@ProgressComponent(weight = 30, showLogs = true)
public class InstallPackageArchiveBroker extends ServiceBroker<ServiceRequest<PackageAction>, ServiceResponse> {
	@Autowired
	private FileHelper helper;
	@Autowired
	private IPackageStore packageStore;
    @Autowired
    private IDatabaseStore databaseStore;
	@Autowired
	private IDeployService deployService;
	@Autowired
	private ManuallyProgress progress;
    @Autowired
    private Factory factory;

	@Override
	protected ServiceResponse perform(ServiceRequest<PackageAction> request) throws Exception {
        PackageAction packageAction = request.getData();

		Long packageId = packageAction.getId();

        PackageModel packageRN = packageStore.findWithSystemById(packageId);

        Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels = packageStore.findAllWithDatabaseById(packageId);
        Map<String, DatabaseAction> associatedDatabases = new HashMap<String, DatabaseAction>();
        List<DatabaseAction> databaseActions = packageAction.getDatabaseActions();
        for (DatabaseAction databaseAction : databaseActions) {
            DatabaseModel databaseModel = databaseStore.findById(databaseAction.getId());
            Database database = factory.convertTo(Database.class, databaseModel);
            databaseAction.setDatabase(database);
            String domainLookup = getDomainLookupByDatabase(associatedDatabaseModels, database.getId());
            associatedDatabases.put(domainLookup, databaseAction);
        }

        String token = OPFContext.getContext().getSessionToken();

        progress.status("Deployment process is running...", 1, LogLevel.INFO);

//		if (database != null && "upgrade".equals(databaseAction)) {
//			String fromVersion = VersionUtils.convertToVersion(packageRN.getDatabaseVersion());
//			String toVersion = VersionUtils.convertToVersion(packageRN.getVersion());
//
//			String upgradeScriptFile = UPGRADE_FILENAME.format(new String[]{fromVersion, toVersion});
//			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,upgradeScriptFile, helper.getVersion(), packageRN.getId().toString(), packageRN.getVersion().toString());
//			if (stream!=null) {
//				try {
//					Map<String, InputStream> files = new HashMap<String, InputStream>();
//					files.put(PackageFileType.PACKAGE_UPGRADE.getOfrFileName(), stream);
//					OPFEngine.FileStoreService.updatezip(OpenFlame.FILESTORE_BASE,files,helper.getVersion(), packageId.toString(), packageRN.getVersion().toString(),
//					packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension());
//				} catch (Exception e) {
//					logVO = progress.createLog(LogLevel.ERROR, "Can't add upgrade.xml to package OFR file.", true);
//					logBatchVO.addLog(logVO);
//					isError = true;
//				}
//			} else {
//				logBatchVO.addLog(progress.createLog(LogLevel.ERROR, "Can't found `upgrade_" + fromVersion + "_" + toVersion + ".xml`. ", true));
//				logBatchVO.addLog(progress.createLog(LogLevel.INFO, "Please go to Package Editor and generate upgrade.xml for corresponded version.", true));
//				isError = true;
//			}
//		}

        String packageXmlFilename = packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension();
        InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlFilename, helper.getVersion(), packageId.toString(), packageRN.getVersion().toString());
        if (stream != null) {
            deployService.deployPackage(packageRN, token, stream, associatedDatabases);
        } else {
            progress.status("Can't found `" + packageXmlFilename + "`.", 1, LogLevel.ERROR);
            progress.status("Try to regenerate code.", 1, LogLevel.ERROR);
        }

        return new ServiceResponse("Deploy successfully" ,true);
	}

    private String getDomainLookupByDatabase(Map<RegistryNodeModel, DatabaseModel> associatedDatabaseModels, Long id) {
        for (Map.Entry<RegistryNodeModel, DatabaseModel> associatedDatabaseModel : associatedDatabaseModels.entrySet()) {
            if (associatedDatabaseModel.getValue().getId().equals(id)) {
                return associatedDatabaseModel.getKey().getLookup();
            }
        }
        return null;
    }
}
