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

import java.util.List;

public class IndexKey {

    private String name;
    private Table table;
    private List<Column> columns;
    private IndexType type;

    /***/
    public IndexKey() {
    }

    /**
     * @param name
     * @param table
     */
    public IndexKey(String name, Table table) {
        this.name = name;
        this.table = table;
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
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @param columns
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * @return
     */
    public IndexType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(IndexType type) {
        this.type = type;
    }

}
