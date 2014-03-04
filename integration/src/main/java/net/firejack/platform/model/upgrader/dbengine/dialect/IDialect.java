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

package net.firejack.platform.model.upgrader.dbengine.dialect;

import net.firejack.platform.model.upgrader.dbengine.bean.Column;
import net.firejack.platform.model.upgrader.dbengine.bean.ForeignKey;
import net.firejack.platform.model.upgrader.dbengine.bean.IndexKey;
import net.firejack.platform.model.upgrader.dbengine.bean.Table;

public interface IDialect {

    /**
     * @param table
     * @return
     */
    String createTable(Table table);

    /**
     * @param table
     * @return
     */
    String dropTable(Table table);

    /**
     * @param table
     * @return
     */
    String renameTable(Table table, Table newTable);

    /**
     * @param table
     * @param column
     * @return
     */
    String addColumn(Table table, Column column);

    /**
     * @param table
     * @param oldColumn
     * @param newColumn
     * @return
     */
    String modifyColumn(Table table, Column oldColumn, Column newColumn);

    /**
     * @param table
     * @param column
     * @return
     */
    String dropColumn(Table table, Column column);

    /**
     * @param foreignKey
     * @return
     */
    String addForeignKey(ForeignKey foreignKey);

    /**
     * @param foreignKey
     * @return
     */
    String dropForeignKey(ForeignKey foreignKey);

    /**
     * @param indexKey
     * @return
     */
    String addIndexKey(IndexKey indexKey);

    /**
     * @param indexKey
     * @return
     */
    String dropIndexKey(IndexKey indexKey);

}
