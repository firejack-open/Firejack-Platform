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
