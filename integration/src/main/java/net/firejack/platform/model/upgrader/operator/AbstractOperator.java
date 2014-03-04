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

package net.firejack.platform.model.upgrader.operator;

import net.firejack.platform.model.upgrader.dbengine.DialectFactory;
import net.firejack.platform.model.upgrader.dbengine.DialectType;
import net.firejack.platform.model.upgrader.dbengine.dialect.IDialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.JdbcUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.transaction.NotSupportedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract class for upgrade operators
 *
 * @param <T> - Operator class
 */
public abstract class AbstractOperator<T> {

    private Log logger = LogFactory.getLog(getClass());

    protected IDialect dialect;

    protected DataSource dataSource;

    /**
     * @throws javax.transaction.NotSupportedException
     *
     */
    @PostConstruct
    public void init() throws NotSupportedException {
        dialect = DialectFactory.getInstance().getDialect(DialectType.MySQL5);
    }

    /**
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method returns generated sql for executing.
     *
     * @param type - operator class
     * @return - array of sql commands
     */
    protected abstract String[] sqlCommands(T type);

    /**
     * Executor method which execute sql commands
     *
     * @param type - operator class
     */
    public void execute(T type) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(true);
                PreparedStatement statement = null;
                try {
                    String[] sqlCommands = sqlCommands(type);
                    for (String sqlCommand : sqlCommands) {
                        logger.info("Execute sql: \n" + sqlCommand);
                        statement = connection.prepareStatement(sqlCommand);
                        statement.executeUpdate();
                    }
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
