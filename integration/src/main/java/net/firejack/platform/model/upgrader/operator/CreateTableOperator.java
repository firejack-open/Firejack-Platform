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

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.model.upgrader.dbengine.bean.Column;
import net.firejack.platform.model.upgrader.dbengine.bean.Table;
import net.firejack.platform.model.upgrader.operator.bean.ColumnType;
import net.firejack.platform.model.upgrader.operator.bean.CreateTableType;

import java.util.ArrayList;
import java.util.List;

public class CreateTableOperator extends AbstractOperator<CreateTableType> {

    @Override
    protected String[] sqlCommands(CreateTableType createTableType) {
        Table table = new Table(createTableType.getName());
        List<Column> primaryKeyColumns = new ArrayList<Column>();
        List<Column> columns = new ArrayList<Column>();
        for (ColumnType columnType : createTableType.getColumn()) {
            FieldType fieldType = FieldType.findByName(columnType.getType());
            Column column = new Column(columnType.getName(), fieldType);
            column.setDefaultValue(columnType.getDefault());
            column.setAutoIncrement(columnType.getPrimaryKey() == Boolean.TRUE);
            column.setNotNull(columnType.getRequired() == Boolean.TRUE);
            columns.add(column);

            if (columnType.getPrimaryKey() == Boolean.TRUE) {
                primaryKeyColumns.add(column);
            }
        }
        table.setColumns(columns);
        if (primaryKeyColumns.size() > 1) {
            for (Column primaryKeyColumn : primaryKeyColumns) {
                primaryKeyColumn.setAutoIncrement(false);
            }
        }
        table.setPrimaryKeyColumns(primaryKeyColumns);
        return new String[] { dialect.createTable(table) };
    }

}
