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

@Component("oracleDatabaseManager")
public class OracleDatabaseManager extends BaseDatabaseManager {

	@Override
	public boolean createDatabase(Database database) {
		return true;
	}

	@Override
	public boolean dropDatabase(Database database) {
        DataSource dataSource = getDataSource(database);
		List<String> tables = query(dataSource, "SELECT TABLE_NAME FROM TABS", new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
        return tables.isEmpty();
    }

	@Override
	public void dropTables(Database database, List<String> tables) {
        DataSource dataSource = getDataSource(database);
        for (String table : tables) {
            executeStatement(dataSource, "DROP TABLE " + table + " CASCADE CONSTRAINTS PURGE");
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
	    dataSource.setName(DatabaseName.Oracle);
        dataSource.setDriverClassName(DatabaseName.Oracle.getDriver());
        dataSource.setSid(database.getParentPath());
        dataSource.setSchema(database.getUrlPath());
        return dataSource;
    }
}
