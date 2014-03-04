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

package net.firejack.platform.model.upgrader.dbengine.bean;


import net.firejack.platform.api.registry.model.RelationshipOption;

public class ForeignKey {

    private String name;
    private Table table;
    private Column column;
    private Table referenceTable;
    private Column referenceColumn;
    private RelationshipOption onDelete;
    private RelationshipOption onUpdate;

    /***/
    public ForeignKey() {
    }

    /**
     * @param name
     * @param tableName
     */
    public ForeignKey(String name, String tableName) {
        this.name = name;
        this.table = new Table(tableName);
    }

    /**
     * @param name
     * @param tableName
     * @param columnName
     * @param referenceTableName
     * @param referenceColumnName
     */
    public ForeignKey(String name, String tableName, String columnName, String referenceTableName, String referenceColumnName) {
        this.name = name;
        this.table = new Table(tableName);
        this.column = new Column(columnName);
        this.referenceTable = new Table(referenceTableName);
        this.referenceColumn = new Column(referenceColumnName);
    }

    /**
     * @param name
     * @param table
     * @param column
     * @param referenceTable
     * @param referenceColumn
     */
    public ForeignKey(String name, Table table, Column column, Table referenceTable, Column referenceColumn) {
        this.name = name;
        this.table = table;
        this.column = column;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public Table getTable() {
        return table;
    }

    /**
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * @return
     */
    public Column getColumn() {
        return column;
    }

    /**
     * @param column
     */
    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * @return
     */
    public Table getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable
     */
    public void setReferenceTable(Table referenceTable) {
        this.referenceTable = referenceTable;
    }

    /**
     * @return
     */
    public Column getReferenceColumn() {
        return referenceColumn;
    }

    /**
     * @param referenceColumn
     */
    public void setReferenceColumn(Column referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    /**
     * @return
     */
    public RelationshipOption getOnDelete() {
        return onDelete;
    }

    /**
     * @param onDelete
     */
    public void setOnDelete(RelationshipOption onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * @return
     */
    public RelationshipOption getOnUpdate() {
        return onUpdate;
    }

    /**
     * @param onUpdate
     */
    public void setOnUpdate(RelationshipOption onUpdate) {
        this.onUpdate = onUpdate;
    }

}
