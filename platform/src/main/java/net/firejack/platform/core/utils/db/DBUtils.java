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

package net.firejack.platform.core.utils.db;

import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.service.reverse.analyzer.AbstractTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.MSSQLTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.MySQLTableAnalyzer;
import net.firejack.platform.model.service.reverse.analyzer.OracleTableAnalyzer;
import net.firejack.platform.model.service.reverse.bean.Column;
import net.firejack.platform.model.service.reverse.bean.Table;
import net.firejack.platform.model.service.reverse.bean.TablesMapping;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class DBUtils {

    public static final int DEFAULT_BATCH_SIZE = 50;
    private static final Logger logger = Logger.getLogger(DBUtils.class);
    private static PreparedStatementCallback<Boolean> stubCallback = new StatementCallback();

    /**
     * @param dataSource
     * @param dbName
     * @return
     */
    public static boolean dbExists(DataSource dataSource, String dbName) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        SqlParameterSource namedParameters = new MapSqlParameterSource("dbName", dbName);
        String sql = "SELECT count(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = :dbName";
        int count = template.queryForInt(sql, namedParameters);
        return count > 0;
    }

    /**
     * @param dataSource
     * @param databaseToCreate
     * @return
     */
    public static boolean createDatabase(DataSource dataSource, String databaseToCreate) {
        return executeStatement(dataSource, "CREATE DATABASE IF NOT EXISTS " + StringUtils.wrapWith("`", databaseToCreate) + ";");
    }

    /**
     * @param dataSource
     * @param databaseToDrop
     * @return
     */
    public static boolean dropDatabase(DataSource dataSource, String databaseToDrop) {
        return executeStatement(dataSource, "DROP DATABASE IF EXISTS " + StringUtils.wrapWith("`", databaseToDrop) + ";");
    }

    /**
     * @param dataSource
     * @param sql
     * @return
     */
    public static boolean executeStatement(DataSource dataSource, String sql) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        try {
            template.execute(sql);
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * @param dataSource
     * @param sqlScript
     * @param delimiter
     * @return
     */
    public static boolean executeScript(DataSource dataSource, File sqlScript, String delimiter) {
        if (sqlScript == null || !sqlScript.exists()) {
            throw new IllegalArgumentException("Wrong script file parameter");
        }
        String sql;
        try {
            sql = IOUtils.toString(new FileReader(sqlScript));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        String[] sqls = sql.split(delimiter);
        if (sqls.length == 1) {
            sqls = sql.split(";");
        }
        for (String sqlStatement : sqls) {
            sqlStatement = sqlStatement.trim();
            if (!sqlStatement.isEmpty() && !sqlStatement.startsWith("#")) {
                if (!executeStatement(dataSource, sqlStatement)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param sql
     * @param dataSource
     * @param rsExtractor
     * @return
     * @throws CommonDBAccessException
     */
    public static <T> T query(String sql, DataSource dataSource, ResultSetExtractor<T> rsExtractor) throws CommonDBAccessException {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        try {
            return template.query(sql, rsExtractor);
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            throw new CommonDBAccessException(e);
        }
    }

    /**
     * @param sql
     * @param dataSource
     * @param rowMapper
     * @return
     * @throws CommonDBAccessException
     */
    public static <T> List<T> query(String sql, DataSource dataSource, RowMapper<T> rowMapper) throws CommonDBAccessException {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        try {
            return template.query(sql, rowMapper);
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            throw new CommonDBAccessException(e);
        }
    }

    /**
     * @param sql
     * @param dataSource
     * @return
     * @throws CommonDBAccessException
     */
    public static Integer querySingleInt(String sql, DataSource dataSource) throws CommonDBAccessException {
        return query(sql, dataSource, new SingleIntegerRSExtractor());
    }

    /**
     * @param sql
     * @param dataSource
     * @return
     * @throws CommonDBAccessException
     */
    public static String querySingleString(String sql, DataSource dataSource) throws CommonDBAccessException {
        return query(sql, dataSource, new SingleStringRSExtractor());
    }

    public static void migrateData(OpenFlameDataSource source, OpenFlameDataSource target) {
        logger.info("Starting migration process");
        Connection sourceConnection = null;
        Connection targetConnection = null;
        try {
            sourceConnection = source.getConnection();
            targetConnection = target.getConnection();
            List<Table> mysqlTables = getTables(sourceConnection, source);
            List<Table> oracleTables = getTables(targetConnection, target);
            List<TablesMapping> mappings = mapTables(mysqlTables, oracleTables);

            if (mappings.size() == mysqlTables.size()) {
                logger.info("Tables mapped successfully.");
                for (TablesMapping mapping : mappings) {
                    addLog("Migrate data from [" + mapping.getSourceTable().getName() + "] table.", 1, LogLevel.INFO);
                    insertDataToTargetTable(mapping, sourceConnection, targetConnection);
                    addLog("Data migration from [" + mapping.getSourceTable().getName() + "] completed.", 1, LogLevel.INFO);
                }
            } else {
                logger.warn("Failed to map all tables.");
                addLog("Failed to migrate data - databases are not identical.", 1, LogLevel.ERROR);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            addLog(e.getMessage(),1,LogLevel.ERROR);
        } finally {
            if (sourceConnection != null) {
                try {
                    sourceConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (targetConnection != null) {
                try {
                    targetConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param driverClassName driver classname
     * @param url jdbc url
     * @param user db user
     * @param password db user password
     * @return returns populated data-source
     */
    public static DataSource populateDataSource(
            String driverClassName, String url, String user, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    private static void addLog(String message, int weight, LogLevel logLevel) {
        ManuallyProgress progress = OpenFlameSpringContext.getBean(ManuallyProgress.class);
        if (progress != null) {
            progress.status(message, weight, logLevel);
        }
    }

    private static class StatementCallback implements PreparedStatementCallback<Boolean> {
        @Override
        public Boolean doInPreparedStatement(PreparedStatement preparedStatement)
                throws SQLException, DataAccessException {
            return preparedStatement.execute();
        }
    }

    private static List<TablesMapping> mapTables(List<Table> sourceTables, List<Table> targetTables) {
        List<TablesMapping> mapping = new ArrayList<TablesMapping>(sourceTables.size());
        for (Table sourceTable : sourceTables) {
            Table correspondingTargetTable = null;
            for (Table targetTable : targetTables) {
                if (sourceTable.getName().equalsIgnoreCase(targetTable.getName())) {
                    correspondingTargetTable = targetTable;
                    break;
                }
            }
            if (correspondingTargetTable == null) {
                logger.error("Failed to locate corresponding target table. Source table - [" + sourceTable.getName() + "].");
            } else {
                List<Column> sourceTableColumns = sourceTable.getColumns();
                List<Column> targetTableColumns = correspondingTargetTable.getColumns();
                Map<Column, Column> columnMapping = new HashMap<Column, Column>();
                for (Column sourceColumn : sourceTableColumns) {
                    Column correspondingTargetColumn = null;
                    for (Column targetColumn : targetTableColumns) {
                        if (sourceColumn.getName().equalsIgnoreCase(targetColumn.getName())) {
                            correspondingTargetColumn = targetColumn;
                        }
                    }
                    if (correspondingTargetColumn == null) {
                        logger.error("Failed to locate corresponding target column, Source column - [" + sourceColumn.getName() + "].");
                        columnMapping = null;
                        break;
                    } else {
                        columnMapping.put(sourceColumn, correspondingTargetColumn);
                    }
                }
                if (columnMapping != null) {
                    mapping.add(new TablesMapping(sourceTable, correspondingTargetTable, columnMapping));
                }
            }
        }
        return mapping;
    }

    private static void insertDataToTargetTable(
            TablesMapping mapping, Connection sourceConnection, Connection targetConnection) throws SQLException {
        Map<Column, Column> columnMapping = mapping.getColumnMapping();
        if (columnMapping.isEmpty()) {
            logger.warn("No columns are detected - no data to insert.");
        } else {
            ResultSet rs = selectDataFromSource(sourceConnection, mapping);

            String insertQuery = populateInsertQuery(mapping);
            PreparedStatement insertStatement = targetConnection.prepareStatement(insertQuery);
            targetConnection.setAutoCommit(false);
            try {
                int currentStep = 1;
                while (rs.next()) {
                    for (int i = 1; i <= columnMapping.size(); i++) {
                        insertStatement.setObject(i, rs.getObject(i));
                    }
                    insertStatement.addBatch();
                    if (++currentStep > DEFAULT_BATCH_SIZE) {
                        insertStatement.executeBatch();
                        targetConnection.commit();
                        currentStep = 1;
                    }
                }
                if (currentStep != 1) {
                    insertStatement.executeBatch();
                    targetConnection.commit();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                targetConnection.rollback();
            } finally {
                insertStatement.close();
                rs.close();
            }
        }
    }

    private static ResultSet selectDataFromSource(Connection sourceConnection, TablesMapping mapping) throws SQLException {
        Map<Column, Column> columnMapping = mapping.getColumnMapping();
        StringBuilder selectQuery = new StringBuilder("select ");
        for (Map.Entry<Column, Column> columnEntry : columnMapping.entrySet()) {
            Column sourceColumn = columnEntry.getKey();
            selectQuery.append(sourceColumn.getName()).append(',');
        }
        if (!columnMapping.isEmpty()) {
            selectQuery.replace(selectQuery.length() - 1, selectQuery.length(), "");
        }
        selectQuery.append(" from ").append(mapping.getSourceTable().getName());
        String sql = selectQuery.toString();
        Statement statement = sourceConnection.createStatement(
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(sql);
        rs.setFetchSize(DEFAULT_BATCH_SIZE);
        return rs;
    }

    private static String populateInsertQuery(TablesMapping mapping) {
        Map<Column, Column> columnMapping = mapping.getColumnMapping();
        StringBuilder insertQuery = new StringBuilder("insert into ");
        insertQuery.append(mapping.getTargetTable().getName()).append('(');
        Set<Map.Entry<Column, Column>> columnEntries = columnMapping.entrySet();
        for (Map.Entry<Column, Column> entry : columnEntries) {
            Column targetColumn = entry.getValue();
            insertQuery.append(targetColumn.getName()).append(',');
        }
        if (!columnMapping.isEmpty()) {
            insertQuery.replace(insertQuery.length() - 1, insertQuery.length(), "");
        }
        insertQuery.append(") values (");
        for (int i = 0; i < columnEntries.size(); i++) {
            insertQuery.append(i == 0 ? '?' : ", ?");
        }
        insertQuery.append(')');
        return insertQuery.toString();
    }

    private static List<Table> getTables(Connection connection, OpenFlameDataSource dataSource) throws SQLException {
        List<Table> tableNames = new ArrayList<Table>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs;
        if (dataSource.getName() == DatabaseName.Oracle) {
            rs = metaData.getTables(dataSource.getSid(), dataSource.getSchema(), null, new String[] {"TABLE"});
        } else {
            rs = metaData.getTables(null, null, null, new String[] {"TABLE"});
        }
        while (rs.next()) {
            String tableType = rs.getString("TABLE_TYPE");
            if ("TABLE".equals(tableType)) {
                String tableName = rs.getString("TABLE_NAME");
                Table table = new Table();
                table.setName(tableName);
                List<Column> columns = getColumns(dataSource, metaData, table);
                table.setColumns(columns);
                tableNames.add(table);
            }
        }
        return tableNames;
    }

    private static List<Column> getColumns(OpenFlameDataSource dataSource, DatabaseMetaData metaData, Table table) throws SQLException {
        ResultSet rs;
        if (dataSource.getName() == DatabaseName.Oracle) {
            rs = metaData.getColumns("orcl", "TIMUR", table.getName(), null);
        } else {
            rs = metaData.getColumns(null, null, table.getName(), null);
        }
        List<Column> columns = new ArrayList<Column>();
        AbstractTableAnalyzer dbAnalyzer = getDBAnalyzer(dataSource);
        while (rs.next()) {
            Column column = dbAnalyzer.createColumn(rs, table);
            columns.add(column);
        }
        return columns;
    }

    private static AbstractTableAnalyzer getDBAnalyzer(OpenFlameDataSource ds) {
        AbstractTableAnalyzer dbAnalyzer;
        switch (ds.getName()) {
            case MySQL: dbAnalyzer = new MySQLTableAnalyzer(ds, ds.getSchema()); break;
            case Oracle: dbAnalyzer = new OracleTableAnalyzer(ds, ds.getSid(), ds.getSchema()); break;
            case MSSQL: dbAnalyzer = new MSSQLTableAnalyzer(ds, ds.getSchema(), ds.getSid()); break;
            default: throw new BusinessFunctionException(
                    "Could not produce db analyzer for database of type " + ds.getName().name());
        }
        return dbAnalyzer;
    }

}