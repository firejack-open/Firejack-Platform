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
import net.firejack.platform.model.upgrader.operator.bean.ModifyColumnType;

public class ModifyColumnOperator extends AbstractOperator<ModifyColumnType> {

    @Override
    protected String[] sqlCommands(ModifyColumnType columnType) {
        Table table = new Table(columnType.getTable());
        Column oldColumn = new Column(columnType.getOldName());

        FieldType fieldType = FieldType.findByName(columnType.getType());
        Column newColumn = new Column(columnType.getNewName(), fieldType);
        newColumn.setDefaultValue(columnType.getDefault());
        newColumn.setNotNull(columnType.getRequired() == Boolean.TRUE);

        return new String[] { dialect.modifyColumn(table, oldColumn, newColumn) };
    }

}
