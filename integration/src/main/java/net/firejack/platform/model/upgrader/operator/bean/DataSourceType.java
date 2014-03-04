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

package net.firejack.platform.model.upgrader.operator.bean;

import net.firejack.platform.model.upgrader.bean.INamedUpgradeModel;
import net.firejack.platform.model.upgrader.bean.IUpgradeModel;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataSourceType", propOrder = {
    "createTableOrModifyColumnOrAddColumn"
})
public class DataSourceType implements INamedUpgradeModel {

    @XmlElements({
        @XmlElement(name = "modify-column", type = ModifyColumnType.class),
        @XmlElement(name = "create-table", type = CreateTableType.class),
        @XmlElement(name = "drop-column", type = DropColumnType.class),
        @XmlElement(name = "add-column", type = AddColumnType.class),
        @XmlElement(name = "drop-table", type = DropTableType.class),
        @XmlElement(name = "rename-table", type = ChangeTableNameType.class),
        @XmlElement(name = "add-foreign-key", type = AddForeignKeyType.class),
        @XmlElement(name = "drop-foreign-key", type = DropForeignKeyType.class),
        @XmlElement(name = "add-index", type = AddIndexType.class),
        @XmlElement(name = "drop-index", type = DropIndexType.class),
        @XmlElement(name = "custom-sql", type = CustomSqlType.class)
    })
    protected List<IUpgradeModel> createTableOrModifyColumnOrAddColumn;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String path;
    @XmlAttribute
    protected String prefix;


    public List<IUpgradeModel> getCreateTableOrModifyColumnOrAddColumn() {
        if (createTableOrModifyColumnOrAddColumn == null) {
            createTableOrModifyColumnOrAddColumn = new ArrayList<IUpgradeModel>();
        }
        return this.createTableOrModifyColumnOrAddColumn;
    }

    public void setCreateTableOrModifyColumnOrAddColumn(List<IUpgradeModel> createTableOrModifyColumnOrAddColumn) {
        this.createTableOrModifyColumnOrAddColumn = createTableOrModifyColumnOrAddColumn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
