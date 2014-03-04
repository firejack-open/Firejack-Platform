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

package net.firejack.platform.core.config.installer;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.core.model.registry.DatabaseName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class DatabaseManager implements IDatabaseManager {

    @Autowired
    @Qualifier("mySqlDatabaseManager")
    private IDatabaseManager mySqlDatabaseManager;
    @Autowired
    @Qualifier("msSqlDatabaseManager")
    private IDatabaseManager msSqlDatabaseManager;
    @Autowired
    @Qualifier("oracleDatabaseManager")
    private IDatabaseManager oracleDatabaseManager;

    @Override
    public boolean createDatabase(Database database) {
        IDatabaseManager databaseManager = getDatabaseManager(database);
        return databaseManager.createDatabase(database);
    }

    @Override
    public boolean dropDatabase(Database database) {
        IDatabaseManager databaseManager = getDatabaseManager(database);
        return databaseManager.dropDatabase(database);
    }

    @Override
    public void dropTables(Database database, List<String> tables) {
        IDatabaseManager databaseManager = getDatabaseManager(database);
        databaseManager.dropTables(database, tables);
    }

    @Override
    public IDatabaseManager getDatabaseManager(Database database) {
        IDatabaseManager databaseManager = null;
        DatabaseName rdbms = database.getRdbms();
        switch (rdbms) {
            case MySQL:
                databaseManager = mySqlDatabaseManager;
                break;
            case MSSQL:
                databaseManager = msSqlDatabaseManager;
                break;
            case Oracle:
                databaseManager = oracleDatabaseManager;
                break;
        }
        return databaseManager;
    }

    @Override
    public DataSource getDataSource(Database database) {
        return getDatabaseManager(database).getDataSource(database);
    }

}
