package net.firejack.platform.core.config.translate;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.*;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.translate.sql.ISqlDialect;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.config.translate.sql.LeadIdPrefixNameResolver;
import net.firejack.platform.core.config.translate.sql.SqlDialectFactory;
import net.firejack.platform.core.config.translate.sql.exception.SqlTranslationException;
import net.firejack.platform.core.config.translate.sql.exception.TypeLookupException;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public abstract class AbstractUpdateTranslator<R, TR extends AbstractTranslationResult<R>>
        extends AbstractTranslator<R, TR> implements ISqlHandler<R, TR> {

    protected ConfigElementFactory factory;
    private DialectType dialectType;
    private ISqlDialect sqlSupport;
    protected ISqlNameResolver sqlNameResolver;
    protected List<IPackageDescriptorElementSqlDecorator<IEntityElement>> entityDecorators;
    protected List<IPackageDescriptorElementSqlDecorator<IRelationshipElement>> relationshipDecorators;
    protected ElementsDiffTransformer elementsDiffTransformer;

    protected AbstractUpdateTranslator(Class<TR> translationResultClass) {
        super(translationResultClass);
        factory = ConfigElementFactory.getInstance();
        entityDecorators = new ArrayList<IPackageDescriptorElementSqlDecorator<IEntityElement>>();
        relationshipDecorators = new ArrayList<IPackageDescriptorElementSqlDecorator<IRelationshipElement>>();
    }

    @Override
    public ISqlNameResolver getSqlNameResolver() {
        if (sqlNameResolver == null) {
            sqlNameResolver = new LeadIdPrefixNameResolver();
        }
        return sqlNameResolver;
    }

    /**
     * @return dialect type
     */
    public DialectType getDialectType() {
        if (dialectType == null) {
            dialectType = DialectType.MySQL5;
        }
        return dialectType;
    }

    /**
     * @param dialectType dialect type
     */
    public void setDialectType(DialectType dialectType) {
        this.dialectType = dialectType;
        sqlSupport = null;
    }

    public void setElementsDiffTransformer(ElementsDiffTransformer elementsDiffTransformer) {
        this.elementsDiffTransformer = elementsDiffTransformer;
    }

    @Override
    public ISqlDialect getSqlSupport() {
        if (sqlSupport == null) {
            sqlSupport = SqlDialectFactory.getInstance().provideSqlDialect(
                    getDialectType(), getSqlNameResolver());
        }
        return sqlSupport;
    }

    /**
     * @param entityDecorator entity decorator which performs additional logic on entities
     */
    public void addEntityDecorator(IPackageDescriptorElementSqlDecorator<IEntityElement> entityDecorator) {
        entityDecorators.add(entityDecorator);
    }

    /**
     * @param relationshipDecorator relationship decorator which performs additional logic on relationships
     */
    public void addRelationshipDecorator(IPackageDescriptorElementSqlDecorator<IRelationshipElement> relationshipDecorator) {
        relationshipDecorators.add(relationshipDecorator);
    }

    @Override
    protected void processFieldDiff(FieldsDiff diff) {
        if (this.elementsDiffTransformer != null) {
            diff = this.elementsDiffTransformer.transformElementsDiff(diff);
        }
        IEntityElement rootEntity = getRootEntity(diff.getTargetParent(), newEntitiesManager);
        if (rootEntity == null) {
            getLogger().error("Could not to handle diff of type [" +
                    diff.getType() + "] for field under entity that doesn't have a root entity.");
        } else {
            IFieldElement diffTarget = diff.getDiffTarget();
            if (diff.getType() == DifferenceType.ADDED) {
                if (!Boolean.TRUE.equals(diffTarget.getProcessed()) && !Boolean.TRUE.equals(rootEntity.getReverseEngineer())) {
                    addColumn(rootEntity, diffTarget);
                }
            } else if (diff.getType() == DifferenceType.REMOVED) {
                //Table related statements running before column statements.
                //So at the moment root entity corresponds to new entities manager.
                //That's why we should use rootEntity calculated using new entities manager
                //Skip auto-generated columns because table should has not less one column
                if (!Boolean.TRUE.equals(rootEntity.getReverseEngineer()) && !diffTarget.isAutoGenerated()) {
                    dropColumn(rootEntity, diffTarget);
                }
            } else {
                IFieldElement newField = diff.getNewElement();
                if (newField == null) {
                    throw new IllegalArgumentException("Diff object does not contain updated field information.");
                }
                modifyColumn(rootEntity, diffTarget, diff.getNewElement());
            }
        }
    }

    @Override
    protected void processIndexDiff(IndexesDiff diff) {
        if (this.elementsDiffTransformer != null) {
            diff = this.elementsDiffTransformer.transformElementsDiff(diff);
        }
        IEntityElement rootEntity = getRootEntity(diff.getTargetParent(), newEntitiesManager);
        if (rootEntity == null) {
            getLogger().error("Could not to handle diff of type [" +
                    diff.getType() + "] for index under entity that doesn't have a root entity.");
        } else if (rootEntity.isTypeEntity()){
            return;
        } else {
            IIndexElement diffTarget = diff.getDiffTarget();
            if (diff.getType() == DifferenceType.ADDED) {
                if (!Boolean.TRUE.equals(rootEntity.getReverseEngineer())) {
                    addIndex(rootEntity, diffTarget);
                }
            } else if (diff.getType() == DifferenceType.REMOVED) {
                //Table related statements running before column statements.
                //So at the moment root entity corresponds to new entities manager.
                //That's why we should use rootEntity calculated using new entities manager
                if (!Boolean.TRUE.equals(rootEntity.getReverseEngineer())) {
                    dropIndex(rootEntity, diffTarget);
                }
            } else {
                IIndexElement indexElement = diff.getNewElement();
                if (indexElement == null) {
                    throw new IllegalArgumentException("Diff object does not contain updated index information.");
                }
                modifyIndex(rootEntity, diffTarget, diff.getNewElement());
            }
        }
    }

    @Override
    protected void processEntityDiff(EntitiesDiff diff) {
        if (this.elementsDiffTransformer != null) {
            diff = this.elementsDiffTransformer.transformElementsDiff(diff);
        }
        if (DifferenceType.REMOVED.equals(diff.getType())) {
            IEntityElement target = diff.getDiffTarget();
            IEntityElement rootEntity = getRootEntity(target, oldEntitiesManager);
            if (rootEntity == null) {
                return;
            }
            CompoundKeyColumnsRule[] columnsRules = target.getCompoundKeyColumnsRules();
            if (columnsRules != null) {
                for (CompoundKeyColumnsRule rule : columnsRules) {
                    IIndexElement indexElement = new IndexConfigElement(rule.getName());
                    dropIndex(rootEntity, indexElement);
                }
            }
            IIndexElement[] indexElements = target.getIndexes();
            if (indexElements != null) {
                for (IIndexElement indexElement : indexElements) {
                    dropIndex(rootEntity, indexElement);
                }
            }
            if (!DiffUtils.isEntityExtension(target)) {
                dropTable(target);
            }
        } else if (DifferenceType.ADDED.equals(diff.getType())) {
            IEntityElement target = diff.getDiffTarget();
            if (target.isTypeEntity() || Boolean.TRUE.equals(target.getReverseEngineer())) {
                return;
            }
            setEntityToPlainState(target, newEntitiesManager);

            IEntityElement rootEntity = getRootEntity(target, newEntitiesManager);
            if (rootEntity == null) {
                return;
            }
            CompoundKeyColumnsRule[] columnsRules = target.getCompoundKeyColumnsRules();
            if (columnsRules != null) {
                for (CompoundKeyColumnsRule rule : columnsRules) {
                    if (!rule.isDetermined()) {
                        Set<IFieldElement> fieldElements = new HashSet<IFieldElement>();
                        for (CompoundKeyParticipantColumn column : rule.getCompoundKeyParticipantColumns()) {
                            if (column.getRef() != null) {
                                String referenceColumn = getSqlNameResolver().resolveReference(column.getRef());
                                fieldElements.add(new FieldConfigElement(referenceColumn));
                            } else if (column.isRefToParent()) {
                                String referenceToParent = getSqlNameResolver().resolveParentColumn();
                                fieldElements.add(new FieldConfigElement(referenceToParent));
                            } else {
                                fieldElements.add(new FieldConfigElement(column.getColumnName()));
                            }
                        }
                        IIndexElement indexElement = new IndexConfigElement(rule.getName());
                        indexElement.setFields(new ArrayList<IFieldElement>(fieldElements));
                        addIndex(rootEntity, indexElement);
                    }
                }
            }
            IIndexElement[] indexElements = target.getIndexes();
            if (indexElements != null) {
                for (IIndexElement indexElement : indexElements) {
                    addIndex(rootEntity, indexElement);
                }
            }
        } else if (DifferenceType.UPDATED.equals(diff.getType())) {
            IEntityElement oldEntity = diff.getDiffTarget();
            IEntityElement newEntity = diff.getNewElement();
            if (oldEntity.isTypeEntity() && newEntity.isTypeEntity()) {
                getLogger().info("Nothing to do with type entities diff.");
            } else if (oldEntity.isTypeEntity() && !newEntity.isTypeEntity()) {
                setEntityToPlainState(newEntity, newEntitiesManager);
            } else if (!oldEntity.isTypeEntity() && newEntity.isTypeEntity()) {//if updated entity become type entity
                //if entity is root of entity hierarchy
                if (oldEntitiesManager.isRootEntity(oldEntity) || !DiffUtils.isEntityExtension(oldEntity)) {
                    dropTable(oldEntity);
                } else {
                    IFieldElement[] fields = oldEntity.getFields();
                    if (fields != null && fields.length > 0) {
                        IEntityElement rootEntity = getRootEntity(oldEntity, oldEntitiesManager);
                        if (rootEntity == null) {
                            getLogger().error("Could not handle entities diff of type [" +
                                    DifferenceType.UPDATED + "] because rootEntity for the entity is null.");
                            return;
                        }
                        //look for entity in new the package descriptor that correspond to old root entity
                        IEntityElement updatedRootEntity;
                        try {
                            updatedRootEntity = newEntitiesManager.lookupByUID(rootEntity.getUid());
                        } catch (TypeLookupException e) {
                            throw new OpenFlameRuntimeException(e);
                        }
                        if (updatedRootEntity != null) {
                            //check if found updatedRootEntity actually is root entity, otherwise look for actual root entity
                            updatedRootEntity = newEntitiesManager.isRootEntity(updatedRootEntity) ?
                                    updatedRootEntity : getRootEntity(updatedRootEntity, newEntitiesManager);
                            if (updatedRootEntity != null) {
                                //use calculated root entity from new package descriptor and drop columns provided by oldEntity
                                //because the oldEntity has become type entity(now it's VO object) and
                                //therefore is not part of the application domain object hierarchy.
                                for (IFieldElement field : fields) {
                                    dropColumn(updatedRootEntity, field);
                                }
                            }
                        }
                    }
                }
            } else if (!oldEntity.getName().equals(newEntity.getName()) && !DiffUtils.isEntityExtension(oldEntity)) {
                // if entity does not extend another entity and old name differentiates from new name then rename entity.
                String oldTableName = getSqlNameResolver().resolveTableName(oldEntity);
                String newTableName = getSqlNameResolver().resolveTableName(newEntity);
                modifyTableName(oldTableName, newTableName);
            }
        }
    }

    private void setEntityToPlainState(IEntityElement entity, EntityElementManager entityManager) {
        if (entityManager.isRootEntity(entity)) {
//                factory.produceField(target, getSqlNameResolver().resolveDiscriminatorColumn(), FieldType.TINY_TEXT, true, null);
            GeneratedFieldContext context = new GeneratedFieldContext(entity, getSqlNameResolver());
            factory.produceGeneratedField(GeneratedFieldType.DISCRIMINATOR, context, true);
            entity = applyDecorator(entity, entityManager, entityDecorators);
            createRootTable(entity, true);
            entityManager.useRootType(entity);
            /*target = factory.produceEnhancedEntity(target);*/
        } else if (!DiffUtils.isEntityExtension(entity)) {
            entity = applyDecorator(entity, entityManager, entityDecorators);
            createTable(entity, true);
        } else {
            IFieldElement[] fields = entity.getFields();
            if (fields != null) {
                IEntityElement rootEntity = getRootEntity(entity, entityManager);
                if (rootEntity == null) {
                    getLogger().error("Could not handle entities diff of type [" +
                            DifferenceType.UPDATED + "] because rootEntity for the entity is null.");
                    return;
                }
                boolean rootTypeUsed = entityManager.isRootTypeUsed(rootEntity);
                for (IFieldElement field : fields) {
                    boolean rootEntityEnhanced = factory.enhanceEntity(rootEntity, field);
                    if (rootEntityEnhanced && rootTypeUsed) {
                        addColumn(rootEntity, field);
                    }
                }
            }
        }
    }

    private String getTableName(Reference ref, EntityElementManager entityManager) throws TypeLookupException {
        IEntityElement source;
        try {
            source = entityManager.lookup(ref);
        } catch (TypeLookupException e) {
            getResultState().addError(22, e.getMessage());
            getLogger().error(e.getMessage(), e);
            throw e;
        }
        IEntityElement sourceRoot;
        try {
            sourceRoot = entityManager.lookupRootEntity(source);
        } catch (TypeLookupException e) {
            getLogger().error(e.getMessage(), e);
            getResultState().addError(31, "Failed to find extended entity by specified path.");
            throw e;
        }
        return getSqlNameResolver().resolveTableName(sourceRoot);
    }

    private String getTableName(IEntityElement entity, EntityElementManager entityManager) throws TypeLookupException {
        IEntityElement entityRoot;
        try {
            entityRoot = entityManager.lookupRootEntity(entity);
        } catch (TypeLookupException e) {
            getLogger().error(e.getMessage(), e);
            getResultState().addError(31, "Failed to find extended entity by specified path.");
            throw e;
        }
        return getSqlNameResolver().resolveTableName(entityRoot);
    }

    @Override
    protected void processRelationshipsDiff(RelationshipsDiff diff) {
        if (this.elementsDiffTransformer != null) {
            diff = this.elementsDiffTransformer.transformElementsDiff(diff);
        }
        IRelationshipElement rel = diff.getDiffTarget();
        INamedPackageDescriptorElement element = this.cache.get(DiffUtils.lookupByRefPath(rel.getSource().getRefPath()));
        if (element instanceof IEntityElement && ((IEntityElement) element).isTypeEntity())
            return;

        if (diff.getType() == DifferenceType.UPDATED) {

            if (diff.getArguments() == null || diff.getArguments().length != 1 ||
                    !(diff.getArguments()[0] instanceof IRelationshipElement)) {
                throw new IllegalStateException("Wrong format of relationship diff.");
            }
            IRelationshipElement newRelationship = (IRelationshipElement) diff.getArguments()[0];
            if (!rel.getType().equals(newRelationship.getType())) {
                getLogger().warn("Update translator does not handle change of relationship type yet.");
                //TODO
            } else {
                IRelationshipElement newRel = diff.getNewElement();
                switch (rel.getType()) {
                    case TYPE:
                    case LINK:
                        IEntityElement oldSource;
                        IEntityElement newSource;
                        IEntityElement newTarget;
                        try {
                            oldSource = oldEntitiesManager.lookup(rel.getSource());
                            newSource = newEntitiesManager.lookup(newRel.getSource());
                            newTarget = newEntitiesManager.lookup(newRel.getTarget());
                        } catch (TypeLookupException e) {
                            getResultState().addError(22, e.getMessage());
                            getLogger().error(e.getMessage(), e);
                            return;
                        }

                        String oldSourceTableName;
                        String newSourceTableName;
                        String newTargetTableName;
                        try {
                            oldSourceTableName = getTableName(oldSource, oldEntitiesManager);
                            newSourceTableName = getTableName(newSource, newEntitiesManager);
                            newTargetTableName = getTableName(newTarget, newEntitiesManager);
                        } catch (TypeLookupException e) {
                            return;
                        }

                        String fkName = rel.getTarget().getConstraintName();

                        //drop previous FK
                        dropForeignKey(oldSourceTableName, fkName);

                        //if source entity war changed, then remove ref field from old source
                        if (!oldSource.getUid().equals(newSource.getUid())) {
                            String referenceFieldName = getSqlNameResolver().resolveReference(rel.getTarget());
                            dropColumn(oldSourceTableName, referenceFieldName);
                        }

                        //populate new name for FK if name of relationship was changed
                        if (!newRelationship.getName().equals(rel.getName())) {
                            fkName = newRelationship.getTarget().getConstraintName();
                        }

                        //add new FK constraint according new relationship
                        String sourceFieldName = getSqlNameResolver().resolveReference(newRel.getTarget());
                        String targetFieldName = getSqlNameResolver().resolveReference(newRel.getSource());

                        addForeignKey(fkName, newSourceTableName, sourceFieldName, newTargetTableName, targetFieldName,
                                rel.getOnUpdateOptions(), rel.getOnDeleteOptions());
                        break;
                    case WEIGHTED_ASSOCIATION:
                    case ASSOCIATION:
                        if (rel.getName().equals(newRelationship.getName())) {
                            //TODO
                            getLogger().warn("Skipping calculation of diff for relationship of type = [" +
                                    rel.getType() + "] - this operation is not supported yet.");
                        } else {//rename table
                            String oldRelTableName = getSqlNameResolver().resolveRelationshipTableName(rel);
                            String newRelTableName = getSqlNameResolver().resolveRelationshipTableName(newRelationship);
                            this.modifyTableName(oldRelTableName, newRelTableName);
                        }
                        break;
                    case PARENT_CHILD:
                    case TREE:
                        if (!rel.getName().equals(newRelationship.getName()) &&
                                rel.getSource().equals(newRelationship.getSource())) {

                            String sourceTableName;
                            try {
                                sourceTableName = getTableName(rel.getSource(), oldEntitiesManager);
                            } catch (TypeLookupException e) {
                                return;
                            }

	                        String relName = getSqlNameResolver().resolveRelationshipDBName(rel.getSource(), newRelationship.getName());
	                        dropForeignKey(sourceTableName, relName);
	                        relName = getSqlNameResolver().resolveRelationshipDBName(newRelationship.getSource(), newRelationship.getName());
	                        addForeignKey(relName, sourceTableName,
                                    getSqlNameResolver().resolveParentColumn(), sourceTableName, null,
                                    newRelationship.getOnUpdateOptions(), newRelationship.getOnDeleteOptions());
                        } else {
                            //TODO
                            getLogger().warn((new StringBuilder(
                                    "Update translator supports only change name for constraint in case of relationship type = ["))
                                    .append(RelationshipType.PARENT_CHILD).append(", ")
                                    .append(RelationshipType.TREE).append("]").toString());
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported type of relationship [" + rel.getTarget() + "]");
                }
            }
        } else {
            boolean added = (diff.getType() == DifferenceType.ADDED);
            if (added && Boolean.TRUE.equals(rel.getReverseEngineer())) {
                return;
            }
            EntityElementManager entityManagerEx = added ? newEntitiesManager : oldEntitiesManager;
            IEntityElement source;
            IEntityElement target;
            try {
                source = entityManagerEx.lookup(rel.getSource());
                target = rel.getTarget() == null ? null : entityManagerEx.lookup(rel.getTarget());
            } catch (TypeLookupException e) {
                getResultState().addError(22, e.getMessage());
                getLogger().error(e.getMessage(), e);
                return;
            }

            IEntityElement sourceRoot;
            IEntityElement targetRoot;
            try {
                sourceRoot = entityManagerEx.lookupRootEntity(source);
                targetRoot = target == null ? null : entityManagerEx.lookupRootEntity(target);
            } catch (TypeLookupException e) {
                getLogger().error(e.getMessage(), e);
                getResultState().addError(31, "Failed to find extended entity by specified path.");
                return;
            }
            GeneratedFieldContext context = new GeneratedFieldContext(sourceRoot, getSqlNameResolver());
            String sourceTableName = getSqlNameResolver().resolveTableName(sourceRoot);
            String targetTableName = targetRoot == null ? null : getSqlNameResolver().resolveTableName(targetRoot);

            if (added) {
                switch (rel.getType()) {
                    case TYPE:
                    case LINK:
                        String keyName = rel.getTarget().getConstraintName();
                        String sourceFieldName;
                        String targetFieldName;
//                        if (StringUtils.isNotBlank(rel.getSource().getRefName()) && StringUtils.isNotBlank(rel.getTarget().getRefName())) {
//                            sourceFieldName = getSqlNameResolver().resolveReference(rel.getTarget());
////                            sourceFieldName = getSqlNameResolver().resolveReference(rel.getSource());
////                            targetFieldName = getSqlNameResolver().resolveReference(rel.getTarget());
//                            targetFieldName = null;
////                            context.setReference(rel.getSource());
//                            context.setReference(rel.getTarget());
//                            IFieldElement refField = factory.produceGeneratedField(GeneratedFieldType.REF, context, rel.isRequired());
//                            if (refField != null) {
//                                addColumn(sourceRoot, refField);
//                            }
//                        } else {
                            sourceFieldName = getSqlNameResolver().resolveReference(rel.getTarget());
                            targetFieldName = null;
                            context.setReference(rel.getTarget());
                            IFieldElement refField = factory.produceGeneratedField(GeneratedFieldType.REF, context, rel.isRequired());
                            if (refField != null) {
                                addColumn(sourceRoot, refField);
                            }
//                        }
                        addForeignKey(keyName, sourceTableName, sourceFieldName, targetTableName, targetFieldName,
                                rel.getOnUpdateOptions(), rel.getOnDeleteOptions());
                        break;
                    case WEIGHTED_ASSOCIATION:
                    case ASSOCIATION:
                        String relName = getSqlNameResolver().resolveRelationshipTableName(rel);
                        IEntityElement relEntity = factory.producePackageEntity(relName);
                        try {
                            factory.assignParent(relEntity, newEntitiesManager.lookup(rel.getSource()));
                        } catch (TypeLookupException e) {
                            getLogger().error(e.getMessage(), e);
                            getResultState().addError(32, "Failed to find referenced entity by specified path.");
                            return;
                        }
                        String relTableName = getSqlNameResolver().resolveTableName(relEntity);
                        String refFieldName1 = getSqlNameResolver().resolveReference(rel.getSource());
                        String refFieldName2 = getSqlNameResolver().resolveReference(rel.getTarget());
                        factory.produceField(relEntity, refFieldName1, FieldType.NUMERIC_ID, true, null);
                        factory.produceField(relEntity, refFieldName2, FieldType.NUMERIC_ID, true, null);
                        if (rel.isSortable())
                            factory.produceField(relEntity, "sort", FieldType.INTEGER_NUMBER, true, null);
                        if (rel.getType().equals(RelationshipType.WEIGHTED_ASSOCIATION)) {// add "Weight" column
                            factory.produceField(relEntity, getSqlNameResolver().resolveWeightColumn(),
                                    FieldType.INTEGER_NUMBER, true, null);
                        }
//                        factory.produceField(relEntity, getSqlNameResolver().resolveCreatedColumn(),
//                                FieldType.CREATION_TIME, true, null);
                        IFieldElement[] relFieldElements = rel.getFields();
                        if (relFieldElements != null && relFieldElements.length != 0) {
                            for (IFieldElement field : relFieldElements) {
                                if (StringUtils.isNotBlank(field.getTypePath())) {
                                    try {
                                        IEntityElement packageEntity = entityManagerEx.lookup(field.getTypePath());
                                        String referencedTable = packageEntity.getName();
                                        String referenceColumn = getSqlNameResolver().resolveReferenceColumn(referencedTable);
                                        factory.produceField(relEntity, referenceColumn, FieldType.NUMERIC_ID, field.isRequired(), null);

                                        String fkToTypedEntityName = getSqlNameResolver().resolveFKName(relName, packageEntity.getName());
//                                        addForeignKey(fkToTypedEntityName, relName, referenceColumn, packageEntity.getName(), null, null);
                                        addForeignKey(fkToTypedEntityName, relTableName, referenceColumn, packageEntity.getName(), null, null, null);
                                    } catch (TypeLookupException e) {
                                        throw new SqlTranslationException(e);
                                    }
                                } else { // duplicate columns in context of fake relEntity
                                    factory.produceField(relEntity, field.getName(),
                                            field.getType(), field.isRequired(), field.getDefaultValue());
                                }
                            }
                        }

                        createStagingTable(relEntity, refFieldName1, refFieldName2);


                        String fkToSourceName = rel.getSource().getConstraintName();
                        addForeignKey(fkToSourceName, relTableName, refFieldName1, sourceTableName, null, rel.getOnUpdateOptions(), rel.getOnDeleteOptions());

                        String fkToTargetName = rel.getTarget().getConstraintName();
                        addForeignKey(fkToTargetName, relTableName, refFieldName2, targetTableName, null, rel.getOnUpdateOptions(), rel.getOnDeleteOptions());
                        break;
                    case PARENT_CHILD:
                    case TREE:
                        IFieldElement parentRefField = factory.produceGeneratedField(
                                GeneratedFieldType.PARENT_REF, context, rel.isRequired());
                        /*IFieldElement parentRefField = factory.produceField(sourceRoot, getSqlNameResolver().resolveParentColumn(),
                                                              FieldType.BIGINT, rel.isRequired(), null);*/
                        if (parentRefField != null) {
                            addColumn(sourceRoot, parentRefField);
                            targetRoot = rel.getType() == RelationshipType.TREE ? sourceRoot : targetRoot;
                            targetTableName = getSqlNameResolver().resolveTableName(targetRoot);
                            relName = rel.getSource().getConstraintName();
                            addForeignKey(relName, sourceTableName, parentRefField.getName(), targetTableName, null, null, null);
                        }
                        /*targetRoot = rel.getType() == RelationshipType.TREE ? sourceRoot : targetRoot;
                                                      addForeignKey(relName, sourceRoot.getName(), parentRefField.getName(), targetRoot.getName(), null, null);*/
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported type of relationship [" + rel.getTarget() + "]");
                }
            } else {
                switch (rel.getType()) {
                    case TYPE:
                    case LINK:
                        String fkName = rel.getTarget().getConstraintName();
                        dropForeignKey(sourceTableName, fkName);
                        dropColumn(sourceTableName, getSqlNameResolver().resolveReference(rel.getTarget()));
                        break;
                    case WEIGHTED_ASSOCIATION:
                    case ASSOCIATION:
                        String relName = getSqlNameResolver().resolveRelationshipTableName(rel);
	                    String fkToSourceName = StringUtils.defaultIfEmpty(rel.getSource().getConstraintName(), getSqlNameResolver().resolveReference(rel.getSource()));
	                    String fkToTargetName = StringUtils.defaultIfEmpty(rel.getTarget().getConstraintName(), getSqlNameResolver().resolveReference(rel.getTarget()));
	                    dropForeignKey(relName, fkToSourceName);
                        dropForeignKey(relName, fkToTargetName);
                        if (rel.getFields() != null && rel.getFields().length != 0) {
                            for (IFieldElement field : rel.getFields()) {
                                if (StringUtils.isNotBlank(field.getTypePath())) {
                                    try {
                                        IEntityElement packageEntity = entityManagerEx.lookup(field.getTypePath());
                                        String fkToTypedEntityName = getSqlNameResolver().resolveFKName(
                                                relName, packageEntity.getName());
                                        dropForeignKey(relName, fkToTypedEntityName);
                                    } catch (TypeLookupException e) {
                                        throw new SqlTranslationException(e);
                                    }
                                }
                            }
                        }
                        dropTable(relName);
                        break;
                    case PARENT_CHILD:
                    case TREE:
                        relName = rel.getSource().getConstraintName();
                        dropForeignKey(sourceTableName, relName);
                        dropColumn(sourceTableName, getSqlNameResolver().resolveParentColumn());
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported type of relationship [" + rel.getTarget() + "]");
                }
            }
        }
    }

    protected <T extends INamedPackageDescriptorElement> T applyDecorator(T entity, EntityElementManager entityManager, List<IPackageDescriptorElementSqlDecorator<T>> decorators) {
        for (IPackageDescriptorElementSqlDecorator<T> entityDecorator : decorators) {
            entity = entityDecorator.decorateElement(entity, this, entityManager);
        }
        return entity;
    }

    protected abstract void addColumn(IEntityElement rootEntity, IFieldElement column);

    protected abstract void dropColumn(IEntityElement rootEntity, IFieldElement column);

    protected abstract void dropColumn(String table, String column);

    protected abstract void modifyColumn(IEntityElement rootEntity, IFieldElement oldColumn, IFieldElement newColumn);

    protected abstract void createTable(IEntityElement rootEntity, boolean createIdColumn);

    protected abstract void createRootTable(IEntityElement rootEntity, boolean createIdColumn);

    protected abstract void createStagingTable(IEntityElement rootEntity, String refFieldName1, String refFieldName2);

    protected abstract void modifyTableName(String oldTableName, String newTableName);

    protected abstract void dropTable(IEntityElement rootEntity);

    protected abstract void dropTable(String tableName);

    protected abstract void dropForeignKey(String tableName, String fkName);

    protected abstract void addIndex(IEntityElement rootEntity, IIndexElement diffTarget);

    protected abstract void dropIndex(IEntityElement rootEntity, IIndexElement diffTarget);

    protected abstract void modifyIndex(IEntityElement rootEntity, IIndexElement diffTarget, IIndexElement newElement);

    private IEntityElement getRootEntity(IEntityElement target, EntityElementManager entityManager) {
        try {
            //return newEntitiesManager.lookupRootEntity(target);
            return entityManager.lookupRootEntity(target);
        } catch (TypeLookupException e) {
            getLogger().error("Failed to find extended entity by path [" + target.getExtendedEntityPath() + "].");
            getLogger().error(e.getMessage(), e);
            getResultState().addError(31, "Failed to find extended entity by specified path.");
            return null;
        }
    }

}
