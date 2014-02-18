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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.sql.DefaultSqlNameResolver;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.TreeEntityModel;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.*;


@SuppressWarnings("unused")
@Component("entityStore")
public class EntityStore extends FieldContainerStore<EntityModel> implements IEntityStore {

    @Autowired
    @Qualifier("actionStore")
    private IActionStore actionStore;

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    @Autowired
    @Qualifier("relationshipStore")
    private IRelationshipStore relationshipStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    @Qualifier("domainStore")
    private IDomainStore domainStore;

    @Autowired
    private IPackageStore packageStore;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    private ICacheDataProcessor cacheProcessor;

    @Autowired
    private IIndexStore indexStore;
    @Autowired
    private IWizardStore wizardStore;

    @Autowired
    private IReferenceObjectStore referenceObjectStore;
	@Autowired
	private IReportStore reportStore;
    @Autowired
    private Factory factory;

    @Autowired
    @Qualifier("progressAspect")
    private ManuallyProgress progress;

    /***/
    @PostConstruct
    public void init() {
        setClazz(EntityModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EntityModel findById(final Long id) {
        EntityModel entityModel = getHibernateTemplate().execute(new HibernateCallback<EntityModel>() {
            @Override
            public EntityModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(EntityModel.class);
                criteria.add(Restrictions.eq("id", id));
                criteria.setFetchMode("fields", FetchMode.JOIN);
                criteria.setFetchMode("extendedEntity", FetchMode.JOIN);
                criteria.setFetchMode("referenceObject", FetchMode.JOIN);
                EntityModel entityModel = (EntityModel) criteria.uniqueResult();
                if (entityModel != null) {
                    initializeExtendedEntities(entityModel.getExtendedEntity());
                }
                return entityModel;
            }
        });
        if (entityModel != null) {
            Hibernate.initialize(entityModel.getContextRoles());
            Hibernate.initialize(entityModel.getIndexes());
        }
        return entityModel;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityModel findByLookup(final String lookup) {
        return getHibernateTemplate().execute(new HibernateCallback<EntityModel>() {
            @Override
            public EntityModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(EntityModel.class);
                criteria.add(Restrictions.eq("lookup", lookup));
                criteria.setFetchMode("fields", FetchMode.JOIN);
                criteria.setFetchMode("extendedEntity", FetchMode.JOIN);
                return (EntityModel) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityModel> findParentTypesByEntityLookup(String entityLookup) {
        List<EntityModel> parentTypes = new ArrayList<EntityModel>();
        List<EntityModel> hierarchicalEntities = findEntitiesDownInHierarchy(entityLookup);
        if (hierarchicalEntities != null && !hierarchicalEntities.isEmpty()) {
            Criteria criteria = getSession().createCriteria(RelationshipModel.class);
            criteria.add(Restrictions.eq("relationshipType", RelationshipType.PARENT_CHILD));
            criteria.add(Restrictions.in("sourceEntity", hierarchicalEntities));
            criteria.setFetchMode("targetEntity", FetchMode.JOIN);
            @SuppressWarnings("unchecked")
            List<RelationshipModel> hierarchicalRelationships = (List<RelationshipModel>) criteria.list();
            if (hierarchicalRelationships != null && !hierarchicalRelationships.isEmpty()) {
                for (RelationshipModel rel : hierarchicalRelationships) {
                    parentTypes.add(rel.getTargetEntity());
                }
            }
        }
        return parentTypes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityModel> findEntitiesUpInHierarchy(String entityLookup) {
        List<EntityModel> models;
        if (StringUtils.isBlank(entityLookup)) {
            models = null;
        } else {
            models = new ArrayList<EntityModel>();
            EntityModel entityModel = findByLookup(entityLookup);
            if (entityModel != null) {
                models.add(entityModel);
                processSubClassedEntities(entityModel, models);
            }
        }
        return models;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<EntityModel>> findAllEntitiesUpHierarchies() {
        Criteria criteria = getSession().createCriteria(EntityModel.class);
        criteria.setFetchMode("extendedEntity", FetchMode.JOIN);
        @SuppressWarnings("unchecked")
        List<EntityModel> allEntities = (List<EntityModel>) criteria.list();
        Map<Long, EntityModel> entitiesById = new HashMap<Long, EntityModel>();
        for (EntityModel entity : allEntities) {
            entitiesById.put(entity.getId(), entity);
        }
        Map<String, List<EntityModel>> result = new HashMap<String, List<EntityModel>>();
        for (EntityModel entity : allEntities) {
            List<EntityModel> subTypes = new ArrayList<EntityModel>();
            subTypes.add(entity);
            result.put(entity.getLookup(), subTypes);
            findAllSubTypes(entity, subTypes, entitiesById);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.createAlias("referenceObject", "referenceObject");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("fields", FetchMode.JOIN);
        EntityModel entity = (EntityModel) criteria.uniqueResult();
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            for (FieldModel field : entity.getFields()) {
                Hibernate.initialize(field.getUid());
            }
        }
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityModel findWithInheritedFieldsById(final Long id) {
        EntityModel container = findById(id);
        if (container != null) {
            Hibernate.initialize(container.getContextRoles());
            container.getFields();
            EntityModel extendedEntity = container.getExtendedEntity();
            while (extendedEntity != null) {
                List<FieldModel> fields = extendedEntity.getFields();
                if (container.getFields() == null) {
                    container.setFields(new ArrayList<FieldModel>());
                }
                if (fields != null) {
                    container.getFields().addAll(fields);
                }
                extendedEntity = extendedEntity.getExtendedEntity();
            }
        }
        return container;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityModel findWithInheritedFieldsByLookup(final String lookup) {
        EntityModel container = findByLookup(lookup);
        if (container != null) {
            container.getFields();
            EntityModel extendedEntity = container.getExtendedEntity();
            while (extendedEntity != null) {
                List<FieldModel> fields = extendedEntity.getFields();
                container.getFields().addAll(fields);
                extendedEntity = extendedEntity.getExtendedEntity();
            }
        }
        return container;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityModel> findAllExtendedEntities(final Long id) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<EntityModel>>() {
            @Override
            public List<EntityModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(EntityModel.class);
                criteria.add(Restrictions.eq("extendedEntity.id", id));
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityModel> findAllWithFilter(SpecifiedIdsFilter<Long> filter, Boolean isTypeEntity) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion typeEntityCriterion;
        if (isTypeEntity) {
            typeEntityCriterion = Restrictions.eq("typeEntity", true);
        } else {
            typeEntityCriterion = Restrictions.ne("typeEntity", true);
        }
        criterions.add(typeEntityCriterion);
        List<EntityModel> foundEntities = findAllWithFilter(criterions, filter);
        if (foundEntities != null) {
            for (EntityModel entity : foundEntities) {
                Hibernate.initialize(entity.getFields());
                Hibernate.initialize(entity.getExtendedEntity());
                initializeExtendedEntities(entity.getExtendedEntity());
            }
        }
        return foundEntities;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Object[]> findAllIdAndExtendedId() {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<Object[]>>() {
            @Override
            public List<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(EntityModel.class);
                criteria.add(Restrictions.isNotNull("extendedEntity.id"));
                ProjectionList proList = Projections.projectionList();
                proList.add(Projections.property("id"));
                proList.add(Projections.property("extendedEntity.id"));
                criteria.setProjection(proList);
                return criteria.list();
            }
        });
    }

    @Override
    protected void externalSave(EntityModel entity, boolean useDefaultRestAction) {
        EntityProtocol protocol = entity.getProtocol();
        if (protocol == null) {
            protocol = EntityProtocol.HTTP;
        }
        if (entity.getTypeEntity() == null) {
            throw new BusinessFunctionException("Select Entity Type");
        }
        entity.setProtocol(protocol);
        super.save(entity, useDefaultRestAction);//todo: check
    }

    protected void saveInternal(EntityModel entity, boolean useDefaultRestAction) {
        boolean isNew = entity.getId() == null;

        ReferenceObjectModel referenceObjectToSave = entity.getReferenceObject();
        if (referenceObjectToSave == null) {
            referenceObjectToSave = new ReferenceObjectModel();
        } else if (isNew) {
            referenceObjectToSave.setId(null);
        }
        if (!isNew) {
            EntityModel oldEntity = findById(entity.getId());
            if (oldEntity.getReferenceObject() != null) {
                referenceObjectToSave.setId(oldEntity.getReferenceObject().getId());
            }
        }
        referenceObjectStore.saveOrUpdate(referenceObjectToSave);
        entity.setReferenceObject(referenceObjectToSave);

        List<FieldModel> fields = entity.getFields();
        entity.setFields(null);

        List<IndexModel> indexes = entity.getIndexes();
        entity.setIndexes(null);

        externalSave(entity, useDefaultRestAction);

        saveFields(entity, isNew, fields);
        saveIndexes(entity, isNew, indexes);
    }

    @Override
    @Transactional
    public EntityModel mergeForGenerator(EntityModel ent, List<FieldModel> entityFields, List<IndexModel> indexList) {
        EntityModel oldEntity = findByUID(ent.getUid().getUid());
        if (oldEntity != null) {
            saveFields(oldEntity, false, entityFields);
//            saveIndexes(oldEntity, false, indexList);
            ReferenceObjectModel newReferenceObject = ent.getReferenceObject();
            ReferenceObjectModel refObjectToSave = oldEntity.getReferenceObject();
            boolean isNewRefObject = refObjectToSave == null;
            if (isNewRefObject) {
                refObjectToSave = new ReferenceObjectModel();
            }
            refObjectToSave.setHeading(newReferenceObject == null ? null : newReferenceObject.getHeading());
            refObjectToSave.setSubHeading(newReferenceObject == null ? null : newReferenceObject.getSubHeading());
            refObjectToSave.setDescription(newReferenceObject == null ? null : newReferenceObject.getDescription());
            if (isNewRefObject) {
                referenceObjectStore.saveOrUpdate(refObjectToSave);
            } else {
                referenceObjectStore.merge(refObjectToSave);
            }
            oldEntity.setReferenceObject(refObjectToSave);

            oldEntity.setName(ent.getName());
            oldEntity.setLookup(DiffUtils.lookup(oldEntity.getPath(), ent.getName()));
            oldEntity.setAbstractEntity(ent.getAbstractEntity());
            oldEntity.setTypeEntity(ent.getTypeEntity());
            externalSave(oldEntity, false);
        }
        return oldEntity;
    }

    @Override
    @Transactional
    public void save(EntityModel entity) {
//	    checkUniqueEntity(entity);
        boolean isNew = entity.getId() == null;
        List<FieldModel> fields = entity.getFields();
        entity.setFields(null);

        List<IndexModel> indexes = null;
        if (Hibernate.isInitialized(entity.getIndexes())) {
            indexes = entity.getIndexes();
            entity.setIndexes(null);
        }

        ReferenceObjectModel referenceObject = entity.getReferenceObject();
        if (referenceObject == null) {
            referenceObject = new ReferenceObjectModel();
            entity.setReferenceObject(referenceObject);
        }
        referenceObjectStore.saveOrUpdate(referenceObject);

        externalSave(entity, true);

        if (isNew) {
            if (!Hibernate.isInitialized(entity.getParent())) {
                Hibernate.initialize(entity.getParent());
            }
            if (entity.getType() == RegistryNodeType.ENTITY && entity.getParent().getType() == RegistryNodeType.ENTITY) {
                RelationshipModel parentChildRel = new RelationshipModel();
                parentChildRel.setSourceEntity(entity);
                parentChildRel.setTargetEntity((EntityModel) entity.getParent());
                parentChildRel.setRelationshipType(RelationshipType.PARENT_CHILD);
                String relName = generateParentChildRelationshipName(entity);
                parentChildRel.setName(relName);
                parentChildRel.setPath(entity.getLookup());
                parentChildRel.setParent(entity);

                relationshipStore.save(parentChildRel);
            }
        }

        collectionStore.findOrCreateCollection(entity);

        removeFieldsFromExtendedEntities(entity, fields);

        boolean requiredToRegenerateExamples = saveFields(entity, isNew, fields, new PrepareCallback<FieldModel>() {
            @Override
            public void callback(FieldModel model) {
                List<EntityModel> options = model.getOptions();
                for (EntityModel option : options) {
                    if (option.getId() == null)
                        save(option);
                }
            }
        });

        if (indexes != null) {
            saveIndexes(entity, isNew, indexes);
        }

        if (!entity.getTypeEntity()) {
            if (isNew) {
                for (RESTMethod restMethod : RESTMethod.values()) {
                    actionStore.createWithPermissionByEntity(entity, restMethod);
                }
                entity.setChildCount(RESTMethod.values().length);
                saveOrUpdate(entity);
            } else {
                List<ActionModel> actions = actionStore.findAllByParentIdWithFilter(entity.getId(), null);
                for (ActionModel action : actions) {
                    actionStore.saveWithGeneratingExamples(action, requiredToRegenerateExamples);
                }
            }
        }
        if (ConfigContainer.isAppInstalled()) {
            Entity cachedEntity = factory.convertTo(Entity.class, entity);
            if (isNew) {
                cacheProcessor.saveNewEntity(cachedEntity);
            } else {
                cacheProcessor.saveEntity(cachedEntity);
            }
        }
    }

    @Override
    @Transactional
    public void saveForm(DomainModel domainModel, List<EntityModel> entityModels, List<RelationshipModel> relationshipModels) {
        final Integer DOMAIN_POINTS = 1;
        final Integer ENTITY_POINTS = 10;
        final Integer RELATIONSHIP_POINTS = 3;

        int points = domainModel != null ? DOMAIN_POINTS : 0;
        points += entityModels.size() * ENTITY_POINTS;
        points += relationshipModels.size() * RELATIONSHIP_POINTS;

        int weightPoint = 20000 / points;

        if (domainModel != null) {
            RegistryNodeModel registryNodeModel = getRegistryNodeStore().findByLookup(domainModel.getPath());
            domainModel.setParent(registryNodeModel);
            progress.status("Creating domain '" + domainModel.getName() + "' ...", weightPoint * DOMAIN_POINTS, LogLevel.INFO);
            domainStore.save(domainModel);

            for (EntityModel entityModel : entityModels) {
                entityModel.setParent(domainModel);
            }
        }

        EntityModel sourceEntity = null;
        Map<Long, EntityModel> savedEntities = new HashMap<Long, EntityModel>();
        for (EntityModel entityModel : entityModels) {
            FieldModel idField = null;
            for (FieldModel fieldModel : entityModel.getFields()) {
                if ("id".equalsIgnoreCase(fieldModel.getName())) {
                    idField = fieldModel;
                    break;
                }
            }
            if (idField != null) {
                IndexModel primaryKeyIndexModel = new IndexModel();
                primaryKeyIndexModel.setIndexType(IndexType.PRIMARY);
                primaryKeyIndexModel.setName(IndexType.PRIMARY.name());
                primaryKeyIndexModel.setParent(entityModel);
                primaryKeyIndexModel.setFields(Arrays.asList(idField));
                entityModel.setIndexes(Arrays.asList(primaryKeyIndexModel));
            }

            if (entityModel.getId() == null || entityModel.getId() < 0) {
                Long tmpEntityId = entityModel.getId();
                entityModel.setId(null);
                progress.status("Creating entity '" + entityModel.getName() + "' ...", weightPoint * ENTITY_POINTS, LogLevel.INFO);
                save(entityModel);

                if (tmpEntityId == null) {
                    sourceEntity = entityModel;
                } else {
                    savedEntities.put(tmpEntityId, entityModel);
                }
            }
        }

        if (sourceEntity != null) {
            for (RelationshipModel relationshipModel : relationshipModels) {
                relationshipModel.setParent(sourceEntity);
                relationshipModel.setSourceEntity(sourceEntity);
                EntityModel targetEntity = relationshipModel.getTargetEntity();
                if (targetEntity != null) {
                    Long targetEntityId = targetEntity.getId();
                    if (targetEntityId < 0) {
                        targetEntity = savedEntities.get(targetEntityId);
                        relationshipModel.setTargetEntity(targetEntity);
                    }
                }
                progress.status("Creating relationship '" + relationshipModel.getName() + "' ...", weightPoint * RELATIONSHIP_POINTS, LogLevel.INFO);
                relationshipStore.save(relationshipModel);
            }
        } else {
            throw new BusinessFunctionException("Can't detect Source Entity.");
        }

    }

    private void saveIndexes(EntityModel entity, boolean isNew, List<IndexModel> indexes) {
        List<IndexModel> newIndexes = new ArrayList<IndexModel>();
        Map<Long, IndexModel> restIndexes = new HashMap<Long, IndexModel>();
        if (indexes != null && Hibernate.isInitialized(indexes)) {
            for (IndexModel index : indexes) {
                index.setChildCount(0);
                index.setParent(entity);
                if (index.getId() == null) {
                    newIndexes.add(index);
                } else {
                    restIndexes.put(index.getId(), index);
                }
            }
        }

        List<FieldModel> fields = entity.getFields();

        if (!isNew) {
            List<IndexModel> indexesWithUpdatedDescriptions = new ArrayList<IndexModel>();
            List<IndexModel> updateIndexes = new ArrayList<IndexModel>();
            List<IndexModel> removeIndexes = new ArrayList<IndexModel>();
            List<IndexModel> existIndexes = indexStore.findIndexesByEntityId(entity.getId());
            for (IndexModel existIndex : existIndexes) {
                if (restIndexes.keySet().contains(existIndex.getId())) {
                    IndexModel index = restIndexes.get(existIndex.getId());
                    //Update description resource in case if description was changed.
                    //if new description of index is empty do nothing with old description resource
                    if (StringUtils.isNotBlank(index.getDescription()) &&
                            !StringUtils.equals(index.getDescription(), existIndex.getDescription())) {
                        indexesWithUpdatedDescriptions.add(index);
                    }
                    ClassUtils.copyProperties(index, existIndex, false);
                    updateIndexes.add(existIndex);
                } else {
                    removeIndexes.add(existIndex);
                }
            }

            for (IndexModel index : removeIndexes) {
                indexStore.deleteById(index.getId());
            }
            for (IndexModel index : updateIndexes) {
                List<FieldModel> savedFields = new ArrayList<FieldModel>();
                for (FieldModel field : index.getFields()) {
                    field = findByName(field.getName(), fields);
                    savedFields.add(field);
                }
                index.setFields(savedFields);
                index.setParent(entity);
                indexStore.merge(index);
            }
        }

        for (IndexModel index : newIndexes) {
            List<FieldModel> indexFields = index.getFields();
            if (indexFields != null) {
                List<FieldModel> savedFields = new ArrayList<FieldModel>();
                for (FieldModel field : indexFields) {
                    field = findByName(field.getName(), fields);
                    savedFields.add(field);
                }
                index.setFields(savedFields);
            }
            index.setParent(entity);
            indexStore.saveOrUpdate(index);
        }
        entity.setIndexes(indexes);
    }

    private void removeFieldsFromExtendedEntities(EntityModel entity, List<FieldModel> fields) {
        EntityModel extendedEntity = entity.getExtendedEntity();
        if (extendedEntity != null) {
            extendedEntity = findById(extendedEntity.getId());
            while (extendedEntity != null) {
                List<FieldModel> extendedEntityFields = extendedEntity.getFields();
                for (FieldModel extendedEntityField : extendedEntityFields) {
                    FieldModel field = findByName(extendedEntityField.getName(), fields);
                    if (field != null) {
                        fields.remove(field);
                    }
                }
                extendedEntity = extendedEntity.getExtendedEntity();
            }
        }
    }

    private FieldModel findByName(String fieldName, List<FieldModel> fields) {
        FieldModel fieldModel = null;
        for (FieldModel field : fields) {
            if (field.getName().equals(fieldName)) {
                fieldModel = field;
            }
        }
        return fieldModel;
    }

    @Override
    public void updateResourceAccessFields(EntityModel entity) {
	    List<RegistryNodeModel> parentRegistryNodes = ((RegistryNodeStore) getRegistryNodeStore()).findAllParentsForEntityLookup(entity.getLookup());
        Collections.reverse(parentRegistryNodes);
        PackageModel packageModel = null;
        String urlPath = "";
	    for (RegistryNodeModel parentRegistryNode : parentRegistryNodes) {
            parentRegistryNode = lazyInitializeIfNeed(parentRegistryNode);
		    if (parentRegistryNode instanceof PackageModel) {
			    packageModel = (PackageModel) parentRegistryNode;
			    break;
		    }
            urlPath = "/" + StringUtils.normalize(parentRegistryNode.getName()) + urlPath;
	    }
        if (packageModel != null) {
            entity.setParentPath(packageModel.getUrlPath());
            entity.setServerName(packageModel.getServerName());
            entity.setPort(packageModel.getPort());
            entity.setUrlPath(urlPath + "/" + StringUtils.normalize(entity.getName()));
        }

        if (entity.getId() != null) {
            List<ActionModel> actions = actionStore.findChildrenByParentId(entity.getId(), null);
            for (ActionModel action : actions) {
                action.setParentPath(entity.getParentPath());
	            actionStore.updateActionPath(action);
            }

            List<EntityModel> entities = findAllByParentIdWithFilter(entity.getId(), null);
            for (EntityModel entityModel : entities) {
                updateResourceAccessFields(entityModel);
            }
        }
    }

    @Override
    @Transactional
    public void saveExtendedEntity(EntityModel entity, EntityModel extendedEntity) {
        if (entity == null || extendedEntity == null) {
            return;
        }

        checkUniqueEntity(entity);
        boolean extendedEntityChanged = false;
        if (ConfigContainer.isAppInstalled()) {
            extendedEntityChanged = entity.getExtendedEntity() == null ||
                    entity.getExtendedEntity().getId().equals(extendedEntity.getId());
        }
        entity.setExtendedEntity(extendedEntity);
        update(entity);
        if (ConfigContainer.isAppInstalled() && extendedEntityChanged) {
            cacheProcessor.updateExtendedEntity(entity.getLookup(), extendedEntity.getLookup());
        }
    }

    @Override
    @Transactional
    public void delete(EntityModel entity) {
        permissionStore.deleteAllByRegistryNodeId(entity.getId());
        List<WizardModel> wizardModels = wizardStore.findAllByMainId(entity.getId());
        for (WizardModel wizardModel : wizardModels) {
            wizardStore.delete(wizardModel);
        }

        List<RelationshipModel> relationships = relationshipStore.findRelatedEntitiesByEntityId(entity.getId(), null);
        for (RelationshipModel relationship : relationships) {
            relationshipStore.deleteRecursively(relationship);
        }
        reportStore.deleteAllByRegistryNodeId(entity.getId());
        List<EntityModel> extendedEntities = findAllExtendedEntities(entity.getId());
        for (EntityModel extendedEntity : extendedEntities) {
            extendedEntity.setExtendedEntity(null);
            super.saveOrUpdate(extendedEntity);
        }
        indexStore.deleteAllByEntityId(entity.getId());
        super.delete(entity);
        if (ConfigContainer.isAppInstalled()) {
            cacheProcessor.deleteEntityByLookup(entity.getLookup());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<EntityModel> getSecurityEnabledEntities(List<String> entityLookupList) {
        List<EntityModel> result = new ArrayList<EntityModel>();
        if (entityLookupList != null && !entityLookupList.isEmpty()) {
            Criteria criteria = getSession().createCriteria(EntityModel.class);
            criteria.add(Restrictions.in("lookup", entityLookupList));
            criteria.add(Restrictions.eq("securityEnabled", Boolean.TRUE));
            result = (List<EntityModel>) criteria.list();
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<EntityModel> getSecurityEnabledEntities() {
        Criteria criteria = getSession().createCriteria(EntityModel.class);
        criteria.add(Restrictions.eq("securityEnabled", Boolean.TRUE));
        return (List<EntityModel>) criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public List<EntityModel> findEntriesByParentId(final TreeEntityModel parent, Integer offset, Integer limit) {
        List<EntityModel> entityModels = super.findEntriesByParentId(parent, offset, limit);
        if (entityModels != null) {
            for (EntityModel entityModel : entityModels) {
                if (entityModel.getContextRoles() != null) {
                    Hibernate.initialize(entityModel.getContextRoles());
                    for (RoleModel roleModel : entityModel.getContextRoles()) {
                        Hibernate.initialize(roleModel);
                    }
                }
                if (entityModel.getReferenceObject() != null) {
                    Hibernate.initialize(entityModel.getReferenceObject());
                }
            }
        }
        return entityModels;
    }

    @Override
    @Transactional
    public void saveEntityRoles(Map<String, List<String>> entityRolesInfo) {
        if (entityRolesInfo != null) {
            for (Map.Entry<String, List<String>> entry : entityRolesInfo.entrySet()) {
                EntityModel entityModel = findByLookup(entry.getKey());
                if (entityModel != null) {
                    List<RoleModel> contextRoles = roleStore.findContextRolesByLookupList(entry.getValue());
                    contextRoles = contextRoles == null || contextRoles.isEmpty() ? null : contextRoles;
                    entityModel.setContextRoles(contextRoles);
                    saveOrUpdate(entityModel);
                }
            }
        }
    }

    public String generateEntityTableName(EntityModel entity) {
        String tableName = StringUtils.changeWhiteSpacesWithSymbol(entity.getName(), DefaultSqlNameResolver.TOKEN_UNDERSCORE).toLowerCase();
        List<RegistryNodeModel> parents = ((RegistryNodeStore) getRegistryNodeStore()).findAllParentsForEntityLookup(entity.getLookup());
        Collections.reverse(parents);
        for (RegistryNodeModel parent : parents) {
            parent = lazyInitializeIfNeed(parent);
            String tablePrefix = ((IPrefixContainer) parent).getPrefix();
            if (StringUtils.isNotBlank(tablePrefix)) {
                tableName = tablePrefix + DefaultSqlNameResolver.TOKEN_UNDERSCORE + tableName;
            }
        }
        return tableName;
    }

    @Override
    @Transactional
    public void setSecurityEnabledOnPackage(String packageLookup, Boolean securityEnabled) {
        update("Entity.updateSecurityEnabledFlagOnPackage",
                "securityEnabled", securityEnabled, "packageLookupPattern", packageLookup + '%');
    }

    @Override
    @Transactional
    public void resetReverseEngineerMark(String parentLookup) {
        update("Entity.resetReverseEngineerMark", "lookupPrefix", parentLookup + '%');
    }

    @Override
    @Transactional(readOnly = true)
    public Integer searchCountByDomain(String terms, List<String> domainLookupPrefixes) {
        LinkedList<Criterion> criterions = getSearchByDomainCriterions(terms, domainLookupPrefixes);
        return super.searchCount(criterions, null, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntityModel> searchByDomain(String terms, List<String> domainLookupPrefixes, Paging paging) {
        LinkedList<Criterion> criterions = getSearchByDomainCriterions(terms, domainLookupPrefixes);
        return super.search(criterions, null, paging, false);
    }

    private LinkedList<Criterion> getSearchByDomainCriterions(String terms, List<String> domainLookupPrefixes) {
        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        if (terms != null) {
            String wildcard = '%'+ URLDecoder.decode(terms.trim())+'%';
            criterions.add(Restrictions.like("name", wildcard));
        }

        if (domainLookupPrefixes.size() > 1) {
            Criterion expression = null;
            for (String domainLookupPrefix : domainLookupPrefixes) {
                if (expression == null) {
                    expression = Restrictions.like("lookup", domainLookupPrefix);
                } else {
                    expression = Restrictions.or(expression, Restrictions.like("lookup", domainLookupPrefix));
                }
            }
            criterions.add(expression);
        } else if (domainLookupPrefixes.size() == 1) {
            criterions.add(Restrictions.like("lookup", domainLookupPrefixes.get(0)));
        }
        return criterions;
    }

    private void checkUniqueEntity(EntityModel entity) {
		if (entity.getTypeEntity()) return;

        List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("name", entity.getName()));
		criterions.add(Restrictions.eq("typeEntity", false));
		criterions.add(Restrictions.like("lookup", StringUtils.getPackageLookup(entity.getLookup()) + ".%")); //all in the same package
		if (entity.getId() != null) {
			criterions.add(Restrictions.ne("id", entity.getId()));
		}
		List<EntityModel> list = findAllWithFilter(criterions, null, null, new Order[0]);
		if (list.isEmpty()) return;

        String tableName = generateEntityTableName(entity);
		for (EntityModel model : list) {
            String anotherTableName = generateEntityTableName(model);
            if (tableName.equals(anotherTableName)) {
				throw new BusinessFunctionException("Unique Entity Name conflict. You try to save Entity which the same name like for Entity ['" + model.getLookup() + "']");
			}
		}
	}

    private void initializeExtendedEntities(EntityModel entity) {
        while (entity != null) {
            Hibernate.initialize(entity.getExtendedEntity());
            Hibernate.initialize(entity.getFields());
            entity = entity.getExtendedEntity();
        }
    }

    private List<EntityModel> findEntitiesDownInHierarchy(String entityLookup) {
        List<EntityModel> entities;
        if (StringUtils.isBlank(entityLookup)) {
            entities = null;
        } else {
            entities = new ArrayList<EntityModel>();
            EntityModel entityModel = findByLookup(entityLookup);
            if (entityModel != null) {
                entities.add(entityModel);
                while (entityModel.getExtendedEntity() != null) {
                    Hibernate.initialize(entityModel.getExtendedEntity());
                    entityModel = entityModel.getExtendedEntity();
                    entities.add(entityModel);
                }
            }
        }
        return entities;
    }

    private void processSubClassedEntities(EntityModel entityModel, List<EntityModel> models) {
        Criteria criteria = getSession().createCriteria(EntityModel.class);
        criteria.add(Restrictions.eq("extendedEntity", entityModel));
        @SuppressWarnings("unchecked")
        List<EntityModel> subClassedEntities = criteria.list();
        if (subClassedEntities != null && !subClassedEntities.isEmpty()) {
            for (EntityModel entity : subClassedEntities) {
                models.add(entity);
                processSubClassedEntities(entity, models);
            }
        }
    }

    private void findAllSubTypes(EntityModel entity, List<EntityModel> subTypes, Map<Long, EntityModel> entitiesById) {
        for (EntityModel entityModel : entitiesById.values()) {
            if (entityModel.getExtendedEntity() != null &&
                    entityModel.getExtendedEntity().getId().equals(entity.getId())) {
                subTypes.add(entityModel);
                findAllSubTypes(entityModel, subTypes, entitiesById);
            }
        }
    }

    private String generateParentChildRelationshipName(EntityModel entity) {
        RegistryNodeModel parentEntity = entity.getParent();
        String name = entity.getName() + " " + parentEntity.getName();
        String lookup = DiffUtils.lookup(entity.getLookup(), name);
        LookupModel lookupModel = getRegistryNodeStore().findByLookup(lookup);
        while (lookupModel != null) {
            name += '1';
            lookup = DiffUtils.lookup(entity.getLookup(), name);
            lookupModel = getRegistryNodeStore().findByLookup(lookup);
        }
        return name;
    }

}
