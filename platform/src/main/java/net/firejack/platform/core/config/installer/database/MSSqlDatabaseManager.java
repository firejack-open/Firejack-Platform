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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("msSqlDatabaseManager")
public class MSSqlDatabaseManager extends BaseDatabaseManager {

	@Override
	public boolean createDatabase(Database database) {
		return true;
	}

	@Override
	public boolean dropDatabase(Database database) {
        DataSource dataSource = getDataSource(database);
		List<String> tables = query(dataSource, "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';", new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});

		if (!tables.isEmpty()) return false;

        dropTables(database, tables);
		return true;
	}

    @Override
    public void dropTables(Database database, List<String> tables) {
        DataSource dataSource = getDataSource(database);
        for (String table : tables) {
                String q = "SELECT 'ALTER TABLE ' + OBJECT_NAME(parent_object_id) + ' DROP CONSTRAINT ' + name FROM sys.foreign_keys WHERE referenced_object_id = object_id('" + table +"')";

                List<String> queries = query(dataSource, q, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                });
                for (String query : queries) {
                    executeStatement(dataSource, query);
                }

            executeStatement(dataSource, "DROP TABLE [" + table + "];");
        }
    }

    @Override
    public IDatabaseManager getDatabaseManager(Database database) {
        return this;
    }

    @Override
    public DataSource getDataSource(Database database) {
        String databaseUrl = database.getRdbms().getDbSchemaUrlConnection(
                database.getProtocol(), database.getServerName(), database.getPort().toString(),
                database.getParentPath(), database.getUrlPath());
	    OpenFlameDataSource dataSource = new OpenFlameDataSource();
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        dataSource.setDriverClassName(database.getRdbms().getDriver());
        dataSource.setName(DatabaseName.MSSQL);
        dataSource.setSchema(database.getUrlPath());
        return dataSource;
    }

}
