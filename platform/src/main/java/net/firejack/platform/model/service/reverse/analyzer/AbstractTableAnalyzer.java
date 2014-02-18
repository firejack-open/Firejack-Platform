/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.model.service.reverse.analyzer;

import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.service.reverse.bean.*;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractTableAnalyzer {

    private static final Logger logger = Logger.getLogger(AbstractTableAnalyzer.class);

    private ManuallyProgress progress;
    private DataSource dataSource;
    private String catalog;
    private String schema;

    protected AbstractTableAnalyzer(DataSource dataSource, String schema) {
        this.dataSource = dataSource;
        this.schema = schema;
    }

    protected AbstractTableAnalyzer(DataSource dataSource, String catalog, String schema) {
        this.dataSource = dataSource;
        this.catalog = catalog;
        this.schema = schema;
    }

    public void setProgress(ManuallyProgress progress) {
        this.progress = progress;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    public List<Table> tableAnalyzing() throws SQLException {
        List<Table> tables = new ArrayList<Table>();
        Connection connection = getDataSource().getConnection();
        DatabaseMetaData metadata = connection.getMetaData();

        progress.status("Getting tables information from database...", 50, LogLevel.INFO);
        ResultSet rs = metadata.getTables(getCatalog(), getSchema(), null, new String[] {"TABLE"});
        while (rs.next()) {
            String tableType = rs.getString("TABLE_TYPE");
            if ("TABLE".equals(tableType)) {
                Table table = createTable(rs);
                tables.add(table);
                logger.info(table);
            }
        }

        int tableSize = tables.size();
        progress.status("Found " + tableSize + " tables...", 50, LogLevel.INFO);
        if (tableSize > 0) {
            int tableWeight = 2000 / tableSize;
            int currentTable = 0;

            for (Table table : tables) {
                currentTable++;
                progress.status("Getting columns information for table '" + table.getName() + "' [" + currentTable + " of " + tableSize + "]...", tableWeight, LogLevel.INFO);
                List<Column> columns = new ArrayList<Column>();
                rs = metadata.getColumns(getCatalog(), getSchema(), table.getName(), null);
                while (rs.next()) {
                    Column column = createColumn(rs, table);
                    columns.add(column);
                    logger.info(column);
                }
                table.setColumns(columns);

                List<Index> indexes = new ArrayList<Index>();
                rs = metadata.getPrimaryKeys(getCatalog(), getSchema(), table.getName());
                while (rs.next()) {
                    String pkColumnName = rs.getString("COLUMN_NAME");
                    Column pkColumn = findColumnByName(columns, pkColumnName);
                    String pkName = rs.getString("PK_NAME");
                    if (StringUtils.isBlank(pkName)) {
                        pkName = IndexType.PRIMARY.name();
                    }
                    Index index = findIndexByName(indexes, pkName);
                    if (index == null) {
                        index = new Index();
                        index.setName(pkName);
                        index.setIndexType(IndexType.PRIMARY);
                        index.setColumns(new ArrayList<Column>(Arrays.asList(pkColumn)));
                        index.setTable(table);
                        indexes.add(index);
                        logger.info(index);
                    } else {
                        index.getColumns().add(pkColumn);
                        logger.info(pkColumn);
                    }
                }

                rs = metadata.getIndexInfo(getCatalog(), getSchema(), table.getName(), false, false);
                while (rs.next()) {
                    String indexColumnName = rs.getString("COLUMN_NAME");
                    if (indexColumnName == null) {
                        continue;
                    }
                    String indexName = rs.getString("INDEX_NAME");
                    if ("PRIMARY_KEY".equals(indexName)) { //TODO hot fix for reverse engineering from platform created tables
                        continue;
                    }
                    Column indexColumn = findColumnByName(columns, indexColumnName);
                    Index index = findIndexByName(indexes, indexName);
                    if (index == null) {
                        index = new Index();
                        index.setName(indexName);
                        index.setTable(table);
                        boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                        if (nonUnique) {
                            index.setIndexType(IndexType.INDEX);
                        } else {
                            index.setIndexType(IndexType.UNIQUE);
                        }
                        index.setColumns(new ArrayList<Column>(Arrays.asList(indexColumn)));
                        indexes.add(index);
                        logger.info(index);
                    } else if (!IndexType.PRIMARY.equals(index.getIndexType())) {
                        index.getColumns().add(indexColumn);
                        logger.info(indexColumn);
                    }
                }
                table.setIndexes(indexes);

            }

            tables = validateTables(tables);

            tableWeight = 900 / tableSize;
            currentTable = 0;
            for (Table table : tables) {
                currentTable++;
                progress.status("Getting constraints information for table '" + table.getName() + "' [" + currentTable + " of " + tableSize + "]...", tableWeight, LogLevel.INFO);
                List<Constraint> constraints = new ArrayList<Constraint>();
                rs = metadata.getImportedKeys(getCatalog(), getSchema(), table.getName());
                while (rs.next()) {
                    String constraintName = rs.getString("FK_NAME");
                    Constraint constraint = findConstraintByName(constraints, constraintName);
                    if (constraint == null) {
                        constraint = new Constraint();
                        constraint.setName(constraintName);

                        String sourceTableName = rs.getString("FKTABLE_NAME");
                        Table sourceTable = findTableByName(tables, sourceTableName);
                        if (sourceTable == null) {
                            progress.status("Can't find source foreign table: '" + sourceTableName + "'.", 0, LogLevel.WARN);
                            continue;
                        }
                        String sourceColumnName = rs.getString("FKCOLUMN_NAME");
                        Column sourceColumn = findColumnByName(sourceTable.getColumns(), sourceColumnName);
                        constraint.setSourceColumn(sourceColumn);

                        String destinationTableName = rs.getString("PKTABLE_NAME");
                        Table destinationTable = findTableByName(tables, destinationTableName);
                        if (destinationTable == null) {
                            progress.status("Can't find destination foreign table: '" + sourceTableName + "'.", 0, LogLevel.WARN);
                            continue;
                        }
                        String destinationColumnName = rs.getString("PKCOLUMN_NAME");
                        Column destinationColumn = findColumnByName(destinationTable.getColumns(), destinationColumnName);
                        constraint.setDestinationColumn(destinationColumn);

                        int deleteRule = rs.getInt("DELETE_RULE");
                        constraint.setOnDelete(Behavior.getByRuleIndex(deleteRule));
                        int updateRule = rs.getInt("UPDATE_RULE");
                        constraint.setOnUpdate(Behavior.getByRuleIndex(updateRule));

                        constraint.setTable(table);
                        constraints.add(constraint);
                        logger.info(constraint);
                    } else {
                        progress.status("Not supported constraint: '" + constraintName + "'.", 0, LogLevel.WARN);
                    }
                }
                table.setConstraints(constraints);
            }
        }
        return tables;
    }

    public List<Table> validateTables(List<Table> tables) {
        progress.status("Validating not supported tables...", 0, LogLevel.INFO);
        List<Table> supportedTables = new ArrayList<Table>();
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            List<Column> columns = table.getColumns();
            List<Index> indexes = table.getIndexes();
            Index primaryKey = findIndexByType(indexes, IndexType.PRIMARY);
            List<Column> primaryKeyColumns = new ArrayList<Column>();
            if (primaryKey != null) {
                primaryKeyColumns = primaryKey.getColumns();
            }
            if (!primaryKeyColumns.isEmpty()) {
                progress.status("Table '" + table.getName() + "' has correct structure for reverse engineering [" + (i + 1) + " of " + tables.size() + "].", 0, LogLevel.INFO);
                if (primaryKeyColumns.size() == 1) {
                    progress.status("\tit is an ordinary table with one primary key.", 0, LogLevel.DEBUG);
                } else if (primaryKeyColumns.size() == 2 && columns.size() == 2) {
                    progress.status("\tit is a many-to-many table.", 0, LogLevel.DEBUG);
                } else {
                    progress.status("\tit is a table with composite primary key.", 0, LogLevel.DEBUG);
                }
                supportedTables.add(table);
            } else {
                progress.status("Table '" + table.getName() + "' is not supported for reverse engineering [" + (i + 1) + " of " + tables.size() + "]. Cause: it has not PRIMARY KEY.", 0, LogLevel.WARN);
            }
//            if (primaryKeyColumns.size() == 1 || (primaryKeyColumns.size() == 2 && columns.size() == 2)) {
//                supportedTables.add(table);
//                progress.status("Table '" + table.getName() + "' has correct structure for reverse engineering [" + (i + 1) + " of " + tables.size() + "].", 0, LogLevel.INFO);
//            } else {
//                if (primaryKeyColumns.isEmpty()) {
//                    progress.status("Table '" + table.getName() + "' is not supported for reverse engineering [" + (i + 1) + " of " + tables.size() + "]. Cause: it has not PRIMARY KEY.", 0, LogLevel.WARN);
//                } else {
//                    progress.status("Table '" + table.getName() + "' is not supported for reverse engineering [" + (i + 1) + " of " + tables.size() + "]. Cause: it has composite PRIMARY KEY.", 0, LogLevel.WARN);
//                }
//            }
        }
        return supportedTables;
    }

    protected Table createTable(ResultSet rs) throws SQLException {
        Table table = new Table();
        table.setName(rs.getString("TABLE_NAME"));
        table.setComment(rs.getString("REMARKS"));
        return table;
    }

    public abstract Column createColumn(ResultSet rs, Table table) throws SQLException;

    protected Column findColumnByName(List<Column> columns, String columnName) {
        Column foundColumn = null;
        for (Column column : columns) {
            if (column.getName().equals(columnName)) {
                foundColumn = column;
                break;
            }
        }
        return foundColumn;
    }

    protected Table findTableByName(List<Table> tables, String tableName) {
        Table foundTable = null;
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                foundTable = table;
                break;
            }
        }
        return foundTable;
    }

    protected Index findIndexByName(List<Index> indexes, String indexName) {
        Index foundIndex = null;
        for (Index index : indexes) {
            if (index.getName().equals(indexName)) {
                foundIndex = index;
                break;
            }
        }
        return foundIndex;
    }

    protected Index findIndexByType(List<Index> indexes, IndexType type) {
        Index foundIndex = null;
        for (Index index : indexes) {
            if (type.equals(index.getIndexType())) {
                foundIndex = index;
                break;
            }
        }
        return foundIndex;
    }

    protected Constraint findConstraintByName(List<Constraint> constraints, String indexName) {
        Constraint foundConstraint = null;
        for (Constraint constraint : constraints) {
            if (constraint.getName().equals(indexName)) {
                foundConstraint = constraint;
                break;
            }
        }
        return foundConstraint;
    }

}
