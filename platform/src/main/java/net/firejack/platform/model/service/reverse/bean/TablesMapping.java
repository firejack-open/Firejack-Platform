package net.firejack.platform.model.service.reverse.bean;

import java.util.Map;


public class TablesMapping {

    private Table sourceTable;
    private Table targetTable;
    private Map<Column, Column> columnMapping;

    public TablesMapping(Table sourceTable, Table targetTable, Map<Column, Column> columnMapping) {
        this.sourceTable = sourceTable;
        this.targetTable = targetTable;
        this.columnMapping = columnMapping;
    }

    public Table getSourceTable() {
        return sourceTable;
    }

    public Table getTargetTable() {
        return targetTable;
    }

    public Map<Column, Column> getColumnMapping() {
        return columnMapping;
    }

}