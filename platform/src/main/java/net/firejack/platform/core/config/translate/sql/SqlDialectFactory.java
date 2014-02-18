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

package net.firejack.platform.core.config.translate.sql;


import net.firejack.platform.model.upgrader.dbengine.DialectType;

public class SqlDialectFactory {

    private static SqlDialectFactory instance = new SqlDialectFactory();

    private SqlDialectFactory() {
    }

    /**
     * @param type dialect type
     * @param sqlNameResolver sql name resolver
     * @return appropriate sql dialect
     */
    public ISqlDialect provideSqlDialect(DialectType type, ISqlNameResolver sqlNameResolver) {
        ISqlDialect dialect = null;
        if (type == DialectType.MySQL5) {
            MySql5Dialect mySql5Dialect = new MySql5Dialect();
            mySql5Dialect.setSqlNamesResolver(sqlNameResolver);
            dialect = mySql5Dialect;
        } else if (type == DialectType.ORACLE) {
            OracleDialect oracleDialect = new OracleDialect();
            oracleDialect.setSqlNamesResolver(sqlNameResolver);
            dialect = oracleDialect;
        } else if (type == DialectType.MSSQL) {
            MSSqlDialect msSqlDialect = new MSSqlDialect();
            msSqlDialect.setSqlNamesResolver(sqlNameResolver);
            dialect = msSqlDialect;
        }
        return dialect;
    }

    /**
     * @return instance of Sql dialect factory
     */
    public static SqlDialectFactory getInstance() {
        if (instance == null) {
            instance = new SqlDialectFactory();
        }
        return instance;
    }

}