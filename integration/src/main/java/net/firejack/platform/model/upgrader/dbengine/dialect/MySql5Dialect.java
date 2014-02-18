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

package net.firejack.platform.model.upgrader.dbengine.dialect;

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.model.upgrader.dbengine.bean.Column;
import net.firejack.platform.model.upgrader.dbengine.bean.ForeignKey;
import net.firejack.platform.model.upgrader.dbengine.bean.IndexKey;
import net.firejack.platform.model.upgrader.dbengine.bean.Table;

import java.util.List;

public class MySql5Dialect implements IDialect {

    public String createTable(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append("`").append(table.getName()).append("`").append(" (\n");
        List<Column> columns = table.getColumns();
        boolean isPrimaryKey = table.getPrimaryKeyColumns() != null && !table.getPrimaryKeyColumns().isEmpty();
        for (int i = 0, columnSize = columns.size(); i < columnSize; i++) {
            Column column = columns.get(i);
            sb.append(generateColumnDefinition(column));
            if (i < (columns.size() - 1) || (i == (columns.size() - 1) && isPrimaryKey)) {
                sb.append(',');
            }
            sb.append('\n');
        }
        if (isPrimaryKey) {
            sb.append("PRIMARY KEY (");
            List<Column> primaryKeyColumns = table.getPrimaryKeyColumns();
            for (int i = 0, primaryKeyColumnsSize = primaryKeyColumns.size(); i < primaryKeyColumnsSize; i++) {
                Column primaryKeyColumn = primaryKeyColumns.get(i);
                sb.append('`').append(primaryKeyColumn.getName()).append('`');
                if (i < (primaryKeyColumns.size() - 1)) {
                    sb.append(',');
                }
            }
            sb.append(")");
        }
        sb.append(") ENGINE=InnoDB ");
        sb.append("DEFAULT CHARSET=utf8;");
        return sb.toString();
    }

    public String dropTable(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE `").append(table.getName()).append("`;");
        return sb.toString();
    }

    @Override
    public String renameTable(Table table, Table newTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("RENAME TABLE `").append(table.getName()).append("` TO `").append(newTable.getName()).append("`;");
        return sb.toString();
    }

    public String addColumn(Table table, Column column) {
        StringBuilder sb = new StringBuilder();
        alterTable(table, sb);
        sb.append("ADD COLUMN ").append(generateColumnDefinition(column)).append(";");
        return sb.toString();
    }

    public String modifyColumn(Table table, Column oldColumn, Column newColumn) {
        StringBuilder sb = new StringBuilder();
        alterTable(table, sb);
        sb.append("CHANGE COLUMN ");
        sb.append("`").append(oldColumn.getName()).append("` ");
        sb.append(generateColumnDefinition(newColumn)).append(";");
        return sb.toString();
    }

    public String dropColumn(Table table, Column column) {
        StringBuilder sb = new StringBuilder();
        alterTable(table, sb);
        sb.append("DROP COLUMN `").append(column.getName()).append("`;");
        return sb.toString();
    }

    public String addForeignKey(ForeignKey foreignKey) {
        StringBuilder sb = new StringBuilder();
        alterTable(foreignKey.getTable(), sb);
        sb.append("ADD INDEX `").append(foreignKey.getName()).append("` (`").append(foreignKey.getColumn().getName()).append("`), ");
        sb.append("ADD CONSTRAINT `").append(foreignKey.getName()).append("` ");
        sb.append("FOREIGN KEY (`").append(foreignKey.getColumn().getName()).append("`) ");
        sb.append("REFERENCES `").append(foreignKey.getReferenceTable().getName()).append("` (`").append(foreignKey.getReferenceColumn().getName()).append("`) ");
        sb.append("ON DELETE ");
        if (foreignKey.getOnDelete() != null) {
            sb.append(foreignKey.getOnDelete().name().replace("_"," "));
        } else {
            sb.append(RelationshipOption.RESTRICT);
        }
        sb.append(" ");
        sb.append("ON UPDATE ");
        if (foreignKey.getOnUpdate() != null) {
            sb.append(foreignKey.getOnUpdate().name().replace("_"," "));
        } else {
            sb.append(RelationshipOption.RESTRICT);
        }
        sb.append(";");
        return sb.toString();
    }

    public String dropForeignKey(ForeignKey foreignKey) {
        StringBuilder sb = new StringBuilder();
        alterTable(foreignKey.getTable(), sb);
        sb.append("DROP INDEX `").append(foreignKey.getName()).append("`, ");
        sb.append("DROP FOREIGN KEY `").append(foreignKey.getName()).append("`;");
        return sb.toString();
    }

    public String addIndexKey(IndexKey indexKey) {
        StringBuilder sb = new StringBuilder();
        alterTable(indexKey.getTable(), sb);
        sb.append("ADD ").append(indexKey.getType().getAddKey());
        sb.append(" `").append(indexKey.getName()).append("` ");
        sb.append(" (");
        List<Column> columns = indexKey.getColumns();
        for (int i = 0, columnSize = columns.size(); i < columnSize; i++) {
            Column column = columns.get(i);
            sb.append('`').append(column.getName()).append('`');
            if (i < (columns.size() - 1)) {
                sb.append(", ");
            }
        }
        sb.append(");");
        return sb.toString();
    }

    public String dropIndexKey(IndexKey indexKey) {
        StringBuilder sb = new StringBuilder();
        alterTable(indexKey.getTable(), sb);
        sb.append("DROP ").append(indexKey.getType().getDropKey());
        sb.append(" `").append(indexKey.getName()).append("`;");
        return sb.toString();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void alterTable(Table table, StringBuilder sb) {
        sb.append("ALTER TABLE `").append(table.getName()).append("` ");
    }

    private String generateColumnDefinition(Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(column.getName()).append("` ").append(column.getType().getDbtype()).append(" ");
        if (column.isNotNull()) {
            sb.append("NOT NULL ");
        }
        if (column.isAutoIncrement()) {
            sb.append("AUTO_INCREMENT ");
        } else if (column.getDefaultValue() != null) {
            sb.append("DEFAULT ").append(value(column.getDefaultValue()));
        }
        return sb.toString();
    }

    private String value(Object value) {
        StringBuilder sb = new StringBuilder();
        if (value == null) {
            sb.append("NULL");
        } else if (value instanceof String || value instanceof Character) {
            sb.append("'").append(String.valueOf(value)).append("'");
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(String.valueOf(value));
        } else {
            throw new IllegalArgumentException("Can't process value of type [" + value.getClass() + "]");
        }
        return sb.toString();
    }

}
