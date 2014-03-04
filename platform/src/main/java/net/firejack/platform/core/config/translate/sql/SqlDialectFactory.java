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