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