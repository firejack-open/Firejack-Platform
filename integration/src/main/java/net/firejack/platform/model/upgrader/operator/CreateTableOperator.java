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
