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

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IIndexElement;
import net.firejack.platform.core.config.meta.diff.IElementDiffInfoContainer;
import net.firejack.platform.core.config.patch.IPatchContext;
import net.firejack.platform.core.config.translate.sql.token.ForeignKeyProcessor;
import org.apache.log4j.Logger;

import java.util.List;


public class SqlUpdateTranslator extends AbstractUpdateTranslator<List<String>, SqlTranslationResult> {

    private static final Logger logger = Logger.getLogger(SqlUpdateTranslator.class);

    /***/
    public SqlUpdateTranslator(IPatchContext patchContext) {
        super(SqlTranslationResult.class);
        this.sqlNameResolver = patchContext.getSqlNamesResolver();
    }

    @Override
    protected void beforeTranslate(IElementDiffInfoContainer elementDiffContainer, SqlTranslationResult resultState) {
        super.beforeTranslate(elementDiffContainer, resultState);
        getSqlSupport().initializeScript(resultState);
    }

    @Override
    public void addForeignKey(String keyName, String sourceTableName, String referenceFieldName,
                              String targetTableName, String targetFieldName,
                              RelationshipOption onUpdateOptions, RelationshipOption onDeleteOptions) {
        ForeignKeyProcessor fkProcessor = getSqlSupport().getAddFkToken(
                keyName, sourceTableName, referenceFieldName, targetTableName, targetFieldName);
        if (onDeleteOptions != null) {
            fkProcessor.addOnDeleteOption(onDeleteOptions);
        }
        if (onUpdateOptions != null) {
            fkProcessor.addOnUpdateOption(onUpdateOptions);
        }
        getResultState().addForeignKey(fkProcessor.statement());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void addColumn(IEntityElement rootEntity, IFieldElement column) {
        getResultState().addAlterTable(getSqlSupport().addColumn(rootEntity, column));
    }

    @Override
    protected void dropColumn(IEntityElement rootEntity, IFieldElement column) {
        getResultState().addDropColumn(getSqlSupport().dropColumn(rootEntity, column));
    }

    @Override
    protected void dropColumn(String table, String column) {
        getResultState().addDropColumn(getSqlSupport().dropColumn(table, column));
    }

    @Override
    protected void modifyColumn(IEntityElement rootEntity, IFieldElement oldColumn, IFieldElement newColumn) {
        getResultState().addAlterTable(
                getSqlSupport().changeColumn(rootEntity, oldColumn, newColumn));
    }

    @Override
    protected void createTable(IEntityElement rootEntity, boolean createIdColumn) {
        getResultState().addCreateTable(getSqlSupport().createTable(rootEntity, createIdColumn));
    }

    @Override
    protected void createRootTable(IEntityElement rootEntity, boolean createIdColumn) {
        getResultState().addCreateRootTable(getSqlSupport().createTable(rootEntity, createIdColumn));
    }

    @Override
    protected void createStagingTable(IEntityElement entity, String refFieldName1, String refFieldName2) {
        getResultState().addCreateTable(getSqlSupport().createStagingTable(entity, refFieldName1, refFieldName2));
    }

    @Override
    protected void modifyTableName(String oldTableName, String newTableName) {
        getResultState().addAlterTableName(getSqlSupport().modifyTableName(oldTableName, newTableName));
    }

    @Override
    protected void dropTable(IEntityElement rootEntity) {
        getResultState().addDropTable(getSqlSupport().dropTable(rootEntity));
    }

    @Override
    protected void dropTable(String tableName) {
        getResultState().addDropTable(getSqlSupport().dropTable(tableName));
    }

    @Override
    protected void dropForeignKey(String tableName, String fkName) {
        getResultState().addDropIndex(getSqlSupport().dropForeignKey(tableName, fkName));
    }

    @Override
    protected void addIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        getResultState().addCreateIndex(getSqlSupport().addIndex(rootEntity, diffTarget));
    }

    @Override
    protected void dropIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        getResultState().addDropIndex(getSqlSupport().dropIndex(rootEntity.getName(), diffTarget.getName()));
    }

    @Override
    protected void modifyIndex(IEntityElement rootEntity, IIndexElement diffTarget, IIndexElement newElement) {
        getResultState().addDropIndex(getSqlSupport().dropIndex(rootEntity.getName(), diffTarget.getName()));
        getResultState().addCreateIndex(getSqlSupport().addIndex(rootEntity, newElement));
    }

}