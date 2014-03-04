/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.config.upgrader;

import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.SortFileUtils;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.model.upgrader.UpgradeExecutor;
import net.firejack.platform.model.upgrader.Version;
import net.firejack.platform.utils.OpenFlameConfig;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.JdbcUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allow to launch upgrade process for database then application is started.
 * Upgrade process runs before then Hibernate was initialized.
 */
public class SchemaUpgrader {

    private Log logger = LogFactory.getLog(getClass());

    public static final String UPGRADE_SCRIPT_PATTERN = "upgrade_(\\d+(?:\\.\\d+)*)_(\\d+(?:\\.\\d+)*)" + PackageFileType.PACKAGE_UPGRADE.getDotExtension();

    private OpenFlameDataSource dataSource;
    private String upgradeFolderPath;

    private UpgradeExecutor upgradeExecutor;

    /**
     * @param dataSource
     */
    public void setDataSource(OpenFlameDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param upgradeFolderPath
     */
    public void setUpgradeFolderPath(String upgradeFolderPath) {
        this.upgradeFolderPath = upgradeFolderPath;
    }

    /**
     * @param upgradeExecutor
     */
    public void setUpgradeExecutor(UpgradeExecutor upgradeExecutor) {
        this.upgradeExecutor = upgradeExecutor;
    }

    /**
     * This method is start point for upgrade process. Method launch upgrade process only for installed application.
     */
    @PostConstruct
    private void init() {
	    try {
		    startUpgrade();
	    } catch (Exception e) {
		    throw new BusinessFunctionException("Occurred exception!", e);
	    }
    }


	/**
     * The main method which upgrade database
     *
     * @throws JAXBException
     * @throws FileNotFoundException
     */
    private void startUpgrade() throws JAXBException, FileNotFoundException {

	    DataSource source = dataSource.database();
	    String schema = OpenFlameConfig.DB_SCHEMA.getValue();
	    if (source == null || !DBUtils.dbExists(source,schema)) return;

	    dataSource.refreshDBProperties();

	    logger.info("Start upgrade");
        Version databaseVersion = getDatabaseVersion(OpenFlame.PACKAGE);
        logger.info("Database version for '" + OpenFlame.PACKAGE + "' is: " + databaseVersion);

	    Pattern pattern = Pattern.compile(SchemaUpgrader.UPGRADE_SCRIPT_PATTERN);
	    URL scriptFolderPath = getClass().getResource(upgradeFolderPath);
        File updateScriptsFolder = new File(scriptFolderPath.getPath());
        if (updateScriptsFolder.isDirectory() && updateScriptsFolder.exists()) {
            FileFilter fileFilter = new RegexFileFilter(UPGRADE_SCRIPT_PATTERN);
            File[] scripts = updateScriptsFolder.listFiles(fileFilter);
            scripts = SortFileUtils.sortingByName(scripts, false);
            for (File script : scripts) {
                String scriptName = script.getName();
                String scriptPath = script.getAbsolutePath();
                Matcher m = pattern.matcher(scriptName);
                if (m.find()) {
                    String fromVersionStr = m.group(1);
                    String toVersionStr = m.group(2);
                    Integer fromVersion = VersionUtils.convertToNumber(fromVersionStr);
                    Integer toVersion = VersionUtils.convertToNumber(toVersionStr);
                    Version ver = new Version(fromVersion, toVersion, scriptPath);
                    if (ver.compareTo(databaseVersion) == 0) {
                        upgradeExecutor.upgrade(ver, OpenFlame.PACKAGE);
                        databaseVersion = updateDatabaseVersion(OpenFlame.PACKAGE, ver);
                    }
                }
            }
        } else {
            logger.warn("Script folder has not been found by path:[" + upgradeFolderPath + "]");
        }
        logger.info("Finish upgrade");
    }

    /**
     * This method returns current package version by package lookup
     *
     * @param packageLookup
     * @return version
     */
    private Version getDatabaseVersion(String packageLookup) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(true);

                PreparedStatement statement = connection.prepareStatement(
                        "SELECT database_version " +
                                "FROM opf_registry_node " +
                                "WHERE lookup = '" + packageLookup + "'"
                );
                try {
                    ResultSet resultSet = statement.executeQuery();
                    try {
                        if (resultSet.next())
                            return new Version(resultSet.getInt(1));
                        else
                            throw new BusinessFunctionException("Can't find database version number.");
                    } finally {
                        JdbcUtils.closeResultSet(resultSet);
                    }
                } catch (SQLException e) {
                    if (1054 == e.getErrorCode()) {
                        return new Version(1);
                    }
                    throw new RuntimeException(e);
                } finally {
                    JdbcUtils.closeStatement(statement);
                }
            } finally {
                connection.setAutoCommit(false);
                JdbcUtils.closeConnection(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method update database package version and increase version number
     *
     * @param packageLookup - Openflame Package lookup
     * @param version       - database version
     * @return version
     */
    private Version updateDatabaseVersion(String packageLookup, Version version) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(true);

                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.addBatch(
                        "UPDATE opf_registry_node SET database_version = " + version.getToVersion() + " " +
                                "WHERE lookup = '" + packageLookup + "';"
                );
                try {
                    statement.executeBatch();
                    return new Version(version.getToVersion());
                } finally {
                    JdbcUtils.closeStatement(statement);
                }
            } finally {
                connection.setAutoCommit(false);
                JdbcUtils.closeConnection(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
