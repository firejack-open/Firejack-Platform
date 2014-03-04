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