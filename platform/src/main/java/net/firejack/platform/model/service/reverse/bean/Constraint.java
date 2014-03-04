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

package net.firejack.platform.model.service.reverse.bean;

public class Constraint {

    private String name;
    private Column sourceColumn;
    private Column destinationColumn;
    private Behavior onDelete;
    private Behavior onUpdate;
    private Table table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(Column sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public Column getDestinationColumn() {
        return destinationColumn;
    }

    public void setDestinationColumn(Column destinationColumn) {
        this.destinationColumn = destinationColumn;
    }

    public Behavior getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(Behavior onDelete) {
        this.onDelete = onDelete;
    }

    public Behavior getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(Behavior onUpdate) {
        this.onUpdate = onUpdate;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "table=" + table.getName() +
                ", name='" + name + '\'' +
                ", sourceColumn=" + sourceColumn.getName() +
                ", destinationColumn=" + destinationColumn.getName() +
                ", onDelete=" + onDelete +
                ", onUpdate=" + onUpdate +
                '}';
    }
}
