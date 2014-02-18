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