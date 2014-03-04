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

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.construct.GeneratedFieldContext;
import net.firejack.platform.core.config.meta.construct.GeneratedFieldType;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.config.translate.sql.exception.TypeLookupException;
import net.firejack.platform.core.utils.StringUtils;


public class AddExtendedEntityAttributeSqlDecorator implements IPackageDescriptorElementSqlDecorator<IEntityElement> {

    private ISqlNameResolver sqlNameResolver;
    private String nameOfEntityToEnhance;

    /**
     * @param sqlNameResolver
     */
    public void setSqlNamesResolver(ISqlNameResolver sqlNameResolver) {
        this.sqlNameResolver = sqlNameResolver;
    }

    /**
     * @return
     */
    public String getNameOfEntityToEnhance() {
        if (StringUtils.isBlank(this.nameOfEntityToEnhance)) {
            this.nameOfEntityToEnhance = DiffUtils.REGISTRY_NODE_ENTITY_NAME;
        }
        return this.nameOfEntityToEnhance;
    }

    /**
     * @param nameOfEntityToEnhance
     */
    public void setNameOfEntityToEnhance(String nameOfEntityToEnhance) {
        this.nameOfEntityToEnhance = nameOfEntityToEnhance;
    }

    @Override
    public IEntityElement decorateElement(
            IEntityElement testedEntity, ISqlHandler sqlHandler, EntityElementManager entityManager) {
        if (testedEntity != null && getNameOfEntityToEnhance().equalsIgnoreCase(testedEntity.getName())) {
            ConfigElementFactory factory = ConfigElementFactory.getInstance();
            String entityPath;
            try {
                entityPath = entityManager.lookupKeyByEntityName("Entity");
            } catch (TypeLookupException e) {
                return testedEntity;
            }
            /*String packagePath = entityManager.getPackagePath() + "." + entityManager.getPackageName();
                           String entityPath = packagePath + ".Entity";*/

            Reference ref = new Reference();
            ref.setRefPath(entityPath);
            ref.setRefName("id_extended_entity");


            String referenceFieldName = sqlHandler.getSqlNameResolver().resolveReference(ref);
            GeneratedFieldContext context = new GeneratedFieldContext(testedEntity, sqlHandler.getSqlNameResolver());
            context.setReference(ref);
            //IFieldElement refField = factory.produceField(testedEntity, referenceFieldName, FieldType.BIGINT, null, null);
            IFieldElement refField = factory.produceGeneratedField(GeneratedFieldType.EXTENDED_ENTITY, context, false);
            if (refField != null) {
                /*sqlHandler.getResultState().addAlterTable(sqlHandler.getSqlSupport().addColumn(testedEntity, refField));*/

                String tableName = sqlNameResolver.resolveTableName(testedEntity);
                sqlHandler.addForeignKey("FK_ENTITY_ENTITY_EXTENSION", tableName, referenceFieldName, tableName, null, null, null);
            }
        }
        return testedEntity;
    }
}