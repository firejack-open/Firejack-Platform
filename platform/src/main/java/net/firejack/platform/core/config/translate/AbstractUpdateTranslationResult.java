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

package net.firejack.platform.core.config.translate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractUpdateTranslationResult<ResultItem, Result> extends AbstractTranslationResult<Result> {

    private List<ResultItem> initialStatements = new ArrayList<ResultItem>();
    private List<ResultItem> dropIndexStatements = new ArrayList<ResultItem>();
    private List<ResultItem> dropColumnStatements = new ArrayList<ResultItem>();
    private List<ResultItem> dropTableStatements = new ArrayList<ResultItem>();
    private List<ResultItem> createTableStatements = new ArrayList<ResultItem>();
    private List<ResultItem> createRootTableStatements = new ArrayList<ResultItem>();
    private List<ResultItem> changeTableNameStatements = new ArrayList<ResultItem>();
    private List<ResultItem> alterTableStatements = new ArrayList<ResultItem>();
    private List<ResultItem> alterIndexStatements = new ArrayList<ResultItem>();
    private List<ResultItem> alterForeignKeyStatements = new ArrayList<ResultItem>();

    @Override
    public Result getResult() {
        List<ResultItem> statements = new ArrayList<ResultItem>();
        statements.addAll(initialStatements);
        statements.addAll(dropIndexStatements);
        statements.addAll(dropColumnStatements);
        statements.addAll(dropTableStatements);
        statements.addAll(createRootTableStatements);
        statements.addAll(createTableStatements);
        statements.addAll(changeTableNameStatements);
        statements.addAll(alterTableStatements);
        statements.addAll(alterIndexStatements);
        statements.addAll(alterForeignKeyStatements);
        return transform(statements);
    }

    public void addInitialStatement(ResultItem statement) {
        initialStatements.add(statement);
    }

    /**
     * @param statement
     */
    public void addDropIndex(ResultItem statement) {
        dropIndexStatements.add(statement);
    }

    /**
     * @param statement
     */
    public void addDropTable(ResultItem statement) {
        dropTableStatements.add(statement);
    }

    /**
     * @param createTable
     */
    public void addCreateTable(ResultItem createTable) {
        createTableStatements.add(createTable);
    }

    /**
     * @param createTable
     */
    public void addCreateRootTable(ResultItem createTable) {
        createRootTableStatements.add(createTable);
    }

    /**
     * @param statement
     */
    public void addAlterTableName(ResultItem statement) {
        changeTableNameStatements.add(statement);
    }

    /**
     * @param statement
     */
    public void addAlterTable(ResultItem statement) {
        alterTableStatements.add(statement);
    }

    /**
     * @param statement
     */
    public void addDropColumn(ResultItem statement) {
        dropColumnStatements.add(statement);
    }

    /**
     * @param statement
     */
    public void addCreateIndex(ResultItem statement) {
        if (statement != null) {
            alterIndexStatements.add(statement);
        }
    }

    /**
     * @param statement
     */
    public void addForeignKey(ResultItem statement) {
        alterForeignKeyStatements.add(statement);
    }

    protected abstract Result transform(List<ResultItem> items);

}