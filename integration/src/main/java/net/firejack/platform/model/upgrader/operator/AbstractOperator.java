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
