/*
 * Firejack Open Flame - Copyright (c) 2013 Firejack Technologies
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

package net.firejack.platform.core.config.installer.database;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.core.config.installer.IDatabaseManager;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("mySqlDatabaseManager")
public class MySqlDatabaseManager extends BaseDatabaseManager {

	@Override
	public boolean createDatabase(Database database) {
        DataSource dataSource = getCreatableDataSource(database);
		return executeStatement(dataSource, "CREATE DATABASE IF NOT EXISTS " + StringUtils.wrapWith("`", database.getUrlPath()) + ";");
	}

	@Override
	public boolean dropDatabase(Database database) {
        DataSource dataSource = getDataSource(database);
		List<String> tables = query(dataSource, "SHOW TABLES;", new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
        return tables.isEmpty() && executeStatement(dataSource, "DROP DATABASE IF EXISTS " + StringUtils.wrapWith("`", database.getUrlPath()) + ";");
    }

	@Override
	public void dropTables(Database database, List<String> tables) {
        DataSource dataSource = getDataSource(database);
		try {
			executeStatement(dataSource, "SET foreign_key_checks = 0;");

			for (String table : tables) {
				executeStatement(dataSource, "DROP TABLE IF EXISTS " + StringUtils.wrapWith("`", table) + ";");
			}
		} finally {
			executeStatement(dataSource, "SET foreign_key_checks = 1;");
		}
	}

    @Override
    public IDatabaseManager getDatabaseManager(Database database) {
        return this;
    }

    @Override
    public DataSource getDataSource(Database database) {
        String databaseUrl = database.getRdbms().getDbSchemaUrlConnection(
                database.getProtocol(), database.getServerName(), database.getPort().toString(), database.getUrlPath());
	    OpenFlameDataSource dataSource = new OpenFlameDataSource();
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
	    dataSource.setName(DatabaseName.MySQL);
        dataSource.setSchema(database.getUrlPath());
        dataSource.setDriverClassName(DatabaseName.MySQL.getDriver());
        return dataSource;
    }

    private DataSource getCreatableDataSource(Database database) {
        String databaseUrl = database.getRdbms().getDbSchemaUrlConnection(
                database.getProtocol(), database.getServerName(), database.getPort().toString(), "mysql");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        return dataSource;
    }

}
