/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.config.installer;

import com.sun.jersey.api.client.ClientHandlerException;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.DatabaseAction;
import net.firejack.platform.api.registry.model.DatabaseActionType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.utils.ArchiveUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.model.upgrader.UpgradeExecutor;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeployService implements IDeployService {
    private static final Logger logger = Logger.getLogger(DeployService.class);
    @Autowired
    private PackageInstallationService packageInstallationService;
    @Autowired
    private IPackageStore packageStore;
    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IRelationshipStore relationshipStore;
    @Autowired
    private ManuallyProgress progress;
    @Autowired
    private RequestTaskService requestTaskService;
    @Autowired
    private IDatabaseManager databaseManager;
    @Autowired
    @Qualifier("upgradeExecutor")
    private UpgradeExecutor upgradeExecutor;
    @Autowired
    private FileHelper helper;

    @Override
    public void deployPackage(PackageModel packageRN, String token, InputStream stream, Map<String, DatabaseAction> associatedDatabases) {
        try {
            install(stream, packageRN, associatedDatabases);

            String name = packageRN.getUrlPath();
            if (packageRN.getUrlPath() == null || packageRN.getUrlPath().isEmpty()) name = "ROOT";

            name = name + PackageFileType.APP_WAR.getDotExtension();
            String file = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();
            progress.status("... deploying application: " + name, 1, LogLevel.INFO);
            requestTaskService.deploy(packageRN.getId(), name, file);
            progress.status("Archive version", 2, LogLevel.INFO);
            OPFEngine.RegistryService.archive(packageRN.getId());
            progress.status("Installation process has been completed", 2, LogLevel.INFO);
        } catch (ClientHandlerException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                progress.status("Package archive has not been found", 1, LogLevel.ERROR);
            } else if (e.getCause() instanceof ConnectException) {
                progress.status("Connection refused", 1, LogLevel.ERROR);
            } else {
                progress.status("Occurred error", 1, LogLevel.ERROR);
            }
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            progress.status("Occurred error", 1, LogLevel.ERROR);
            logger.error(e.getMessage(), e);
        } finally {
            if (stream != null) {
                IOUtils.closeQuietly(stream);
            }
        }
    }

    @Override
    public void undeployPackage(PackageModel packageRN, String token, Map<String, DatabaseAction> associatedDatabases) {
        try {
            if (!associatedDatabases.isEmpty())
                uninstall(packageRN, associatedDatabases);

            String name = packageRN.getUrlPath();
            if (packageRN.getUrlPath() == null || packageRN.getUrlPath().isEmpty()) name = "ROOT";

            name = name + PackageFileType.APP_WAR.getDotExtension();
            progress.status("... undeploying application: " + name, 1, LogLevel.INFO);
            requestTaskService.undeploy(packageRN.getId(), name);
            progress.status("Uninstallation process has been completed", 1, LogLevel.INFO);
        } catch (Exception e) {
            progress.status("Occurred error" + e.getMessage(), 1, LogLevel.ERROR);
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Deployed> getDeployedPackageNames(SystemModel system) {
        List<ServiceResponse<Deployed>> list = requestTaskService.list(system.getId());
        List<Deployed> deployedList = new ArrayList<Deployed>();
        for (ServiceResponse<Deployed> response : list) {
            deployedList.add(response.getItem());
        }
        return deployedList;
    }

    @Override
    public List<WebArchive> getWarStatus(Long systemId) {
        List<ServiceResponse<WebArchive>> status = requestTaskService.status(systemId);
        List<WebArchive> webArchives = new ArrayList<WebArchive>();
        for (ServiceResponse<WebArchive> response : status) {
            webArchives.addAll(response.getData());
        }
        return webArchives;
    }

    @Override
    public void restart() {
        requestTaskService.restart();
    }

    private void install(InputStream stream, PackageModel packageRN, Map<String, DatabaseAction> associatedDatabases) {
        progress.status("Installation process is running...", 1, LogLevel.INFO);
        try {
            progress.status("... package archive has been downloaded", 1, LogLevel.INFO);
            progress.status("... unzipping package archive", 2, LogLevel.INFO);

            final File packageXmlUploadedFile = File.createTempFile(SecurityHelper.generateRandomSequence(16), null);
            final File upgradeXmlUploadedFile = File.createTempFile(SecurityHelper.generateRandomSequence(16), null);
            final File codeWarUploadedFile = File.createTempFile(SecurityHelper.generateRandomSequence(16), null);

            ArchiveUtils.unzip(stream, new ArchiveUtils.ArchiveCallback() {
                @Override
                public void callback(String dir, String name, InputStream stream) {
                    try {
                        if (PackageFileType.PACKAGE_XML.getOfrFileName().equals(name)) {
                            FileOutputStream packageXmlUploadFileOutputStream = new FileOutputStream(packageXmlUploadedFile);
                            IOUtils.copy(stream, packageXmlUploadFileOutputStream);
                            IOUtils.closeQuietly(packageXmlUploadFileOutputStream);
                            progress.status("... extracted package xml", 1, LogLevel.DEBUG);
                        } else if (PackageFileType.PACKAGE_UPGRADE.getOfrFileName().equals(name)) {
                            FileOutputStream upgradeXmlUploadFileOutputStream = new FileOutputStream(upgradeXmlUploadedFile);
                            IOUtils.copy(stream, upgradeXmlUploadFileOutputStream);
                            IOUtils.closeQuietly(upgradeXmlUploadFileOutputStream);
                            progress.status("... extracted upgrade xml", 1, LogLevel.DEBUG);
                        } else if (PackageFileType.APP_WAR.getOfrFileName().equals(name)) {
                            FileOutputStream codeWarFileOutputStream = new FileOutputStream(codeWarUploadedFile);
                            IOUtils.copy(stream, codeWarFileOutputStream);
                            IOUtils.closeQuietly(codeWarFileOutputStream);
                            progress.status("... extracted war", 1, LogLevel.DEBUG);
                        }
                    } catch (IOException e) {
                        logger.error(e, e);
                    }
                }
            });

            Map<String, DatabaseAction> installDatabaseActionMap = new HashMap<String, DatabaseAction>();
            Map<String, DatabaseAction> upgradeDatabaseActionMap = new HashMap<String, DatabaseAction>();
            for (Map.Entry<String, DatabaseAction> entry : associatedDatabases.entrySet()) {
                String lookup = entry.getKey();
                DatabaseAction databaseAction = entry.getValue();
                if (DatabaseActionType.CREATE.equals(databaseAction.getAction())) {
                    installDatabaseActionMap.put(lookup, databaseAction);
                } else if (DatabaseActionType.UPGRADE.equals(databaseAction.getAction())) {
                    upgradeDatabaseActionMap.put(lookup, databaseAction);
                }
            }

            if (!installDatabaseActionMap.isEmpty() && (!packageXmlUploadedFile.exists() || !codeWarUploadedFile.exists())) {
                progress.status("... not all necessary files has been extracted", 1, LogLevel.ERROR);
                throw new BusinessFunctionException("Package archive is broken.");
            }
            if (!upgradeDatabaseActionMap.isEmpty() && (!upgradeXmlUploadedFile.exists() || !codeWarUploadedFile.exists())) {
                progress.status("... not all necessary files has been extracted", 1, LogLevel.ERROR);
                throw new BusinessFunctionException("Package archive is broken.");
            }

            Map<String, DataSource> installDataSourceMap = new HashMap<String, DataSource>();
            for (Map.Entry<String, DatabaseAction> entry : installDatabaseActionMap.entrySet()) {
                String lookup = entry.getKey();
                DatabaseAction databaseAction = entry.getValue();
                Database database = databaseAction.getDatabase();
                DataSource dataSource = databaseManager.getDataSource(database);
                progress.status("... creating the " + database.getRdbms().name() + " database: " + database.getUrlPath(), 1, LogLevel.INFO);
                boolean hasCreated = databaseManager.createDatabase(database);
                if (hasCreated) {
                    progress.status("... the database has been created", 2, LogLevel.INFO);
                } else {
                    progress.status("... the database has not been created", 2, LogLevel.WARN);
                }
                installDataSourceMap.put(lookup, dataSource);
            }

            if (!installDataSourceMap.isEmpty()) {
                try {
                    progress.status("... creating database tables", 2, LogLevel.INFO);
                    packageInstallationService.install(packageXmlUploadedFile, installDataSourceMap, null);
                    progress.status("... database tables have been created", 2, LogLevel.INFO);
                } catch (Exception e) {
                    progress.status("Database creating: occurred error." + e.getMessage(), 2, LogLevel.ERROR);
                    throw new BusinessFunctionException(e);
                }
            }

            for (Map.Entry<String, DatabaseAction> entry : upgradeDatabaseActionMap.entrySet()) {
                String domainLookup = entry.getKey();
                DatabaseAction databaseAction = entry.getValue();
                Database database = databaseAction.getDatabase();
                DataSource dataSource = databaseManager.getDataSource(database);
                try {
                    progress.status("... upgrading the " + database.getRdbms().name() + " database: " + database.getUrlPath(), 3, LogLevel.INFO);
                    upgradeExecutor.upgrade(upgradeXmlUploadedFile, dataSource, domainLookup);
                    progress.status("... database have been upgraded", 1, LogLevel.INFO);
                } catch (Exception e) {
                    progress.status("Database upgrade process: occurred error" + e.getMessage(), 1, LogLevel.ERROR);
                    throw new BusinessFunctionException(e);
                }
                entityStore.resetReverseEngineerMark(entry.getKey());
                relationshipStore.resetReverseEngineerMark(entry.getKey());
            }

            if (!upgradeDatabaseActionMap.isEmpty()) {
                progress.status("Change database version to: " + VersionUtils.convertToVersion(packageRN.getVersion()), 2, LogLevel.INFO);
                packageStore.save(packageRN, null, packageRN.getVersion(), false);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            progress.status("IOException: downloading package archive" + e.getMessage(), 1, LogLevel.ERROR);
            throw new BusinessFunctionException(e);
        }
    }

    private void uninstall(PackageModel model, Map<String, DatabaseAction> associatedDatabases) {
        progress.status("Uninstallation process is running...", 1, LogLevel.INFO);
        String ofr = model.getName() + PackageFileType.PACKAGE_OFR.getDotExtension();
        try {
            InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, ofr, helper.getVersion(), model.getId().toString(), model.getDatabaseVersion().toString());
            InputStream xml = ArchiveUtils.getFile(stream, PackageFileType.PACKAGE_XML.getOfrFileName());
            IPackageDescriptor descriptor = packageInstallationService.getPackageDescriptor(xml);
            IOUtils.closeQuietly(xml);
            IOUtils.closeQuietly(stream);

            Map<String, List<String>> tablesMap = packageInstallationService.generateSQLTables(descriptor);
            int droppedDatabases = 0;
            for (Map.Entry<String, DatabaseAction> entry : associatedDatabases.entrySet()) {
                String lookup = entry.getKey();
                DatabaseAction databaseAction = entry.getValue();
                if (DatabaseActionType.DROP.equals(databaseAction.getAction())) {
                    Database database = databaseAction.getDatabase();

                    List<String> tables = tablesMap.get(lookup);
                    if (tables != null && tables.size() != 0) {
                        databaseManager.dropTables(database, tables);

                        progress.status("... database '" + database.getUrlPath() + "' dropping process is running", 2, LogLevel.DEBUG);
                        databaseManager.dropDatabase(database);
                        progress.status("database '" + database.getUrlPath() + "' dropping process has been finished", 1, LogLevel.DEBUG);
                        droppedDatabases++;
                    }
                }
            }
            if (droppedDatabases > 0) {
                progress.status("all tables have been dropped.", 1, LogLevel.DEBUG);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
