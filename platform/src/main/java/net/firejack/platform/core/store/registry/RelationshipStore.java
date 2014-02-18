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

import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.sql.DefaultSqlNameResolver;
import net.firejack.platform.core.config.translate.sql.LeadIdPrefixNameResolver;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
@Component("relationshipStore")
public class RelationshipStore extends RegistryNodeStore<RelationshipModel> implements IRelationshipStore {

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;
    @Autowired
    @Qualifier("indexStore")
    private IIndexStore indexStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(RelationshipModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RelationshipModel findById(Long id) {
	    RelationshipModel relationship = findSingle("Relationship.findByIdWithEntities", "id", id);
	    if (relationship != null) {
		    if (relationship.getSourceEntity() != null) {
			    relationship.getSourceEntity().setFields(null);
		    }
		    if (relationship.getTargetEntity() != null) {
			    relationship.getTargetEntity().setFields(null);
		    }
	    }
	    return relationship;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipModel> findAllByLikeLookupPrefix(final String lookupPrefix) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }
        return getHibernateTemplate().execute(new HibernateCallback<List<RelationshipModel>>() {
            public List<RelationshipModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.like("lookup", lookupPrefix + "%"));
                criteria.setFetchMode("sourceEntity", FetchMode.JOIN);
                criteria.setFetchMode("targetEntity", FetchMode.JOIN);
                return (List<RelationshipModel>) criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RelationshipModel> findChildrenByParentId(final Long registryNodeId, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                if (registryNodeId == null) {
                    criteria.add(Restrictions.isNull("parent"));
                } else {
                    criteria.add(Restrictions.eq("parent.id", registryNodeId));
                }
                criteria.setFetchMode("sourceEntity", FetchMode.JOIN);
                criteria.setFetchMode("targetEntity", FetchMode.JOIN);
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipModel> findRelatedEntitiesByEntityId(final Long registryNodeId, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                Criterion sourceEntityCriterion = Restrictions.eq("sourceEntity.id", registryNodeId);
                Criterion targetEntityCriterion = Restrictions.eq("targetEntity.id", registryNodeId);
                criteria.add(Restrictions.or(sourceEntityCriterion, targetEntityCriterion));
                criteria.setFetchMode("sourceEntity", FetchMode.JOIN);
                criteria.setFetchMode("targetEntity", FetchMode.JOIN);
                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipModel> findRelatedEntitiesBySourceEntityId(final Long registryNodeId, final boolean loadSourceField, final boolean loadTargetField, final SpecifiedIdsFilter<Long> filter) {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = createCriteriaForFilter(session, filter);
                criteria.add(Restrictions.eq("sourceEntity.id", registryNodeId));
                criteria.setFetchMode("sourceEntity", FetchMode.JOIN);
                criteria.setFetchMode("targetEntity", FetchMode.JOIN);
                if (loadSourceField)
                    criteria.setFetchMode("sourceEntity.fields", FetchMode.JOIN);
                if (loadTargetField)
                    criteria.setFetchMode("targetEntity.fields", FetchMode.JOIN);
                if (loadSourceField || loadTargetField)
                    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

                return (List<RegistryNodeModel>) criteria.list();
            }
        });
    }

    @Override
	@Transactional
	public void save(RelationshipModel model) {
		EntityModel sourceEntity = model.getSourceEntity();
		EntityModel targetEntity = model.getTargetEntity();

        boolean isNew = model.getId() == null;

        if(isNew && (model.getRelationshipType() == RelationshipType.TREE || model.getRelationshipType() == RelationshipType.PARENT_CHILD)) {
			Criteria criteria = getSession().createCriteria(RegistryNodeModel.class);
			criteria.add(Restrictions.eq("sourceEntity.id", sourceEntity.getId()));
			LogicalExpression expression = Restrictions.or(Restrictions.eq("relationshipType",  RelationshipType.TREE), Restrictions.eq("relationshipType",  RelationshipType.PARENT_CHILD));
			criteria.add(expression);
			Long count = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
			if (count != 0) {
				throw new BusinessFunctionException("Source Entity can't has both relationships like Tree and ParentChild");
			}
		}

		if (isNew && StringUtils.isBlank(model.getSourceConstraintName())) {
			model.setSourceConstraintName("FK" + SecurityHelper.generateRandomSequence(14));
        }
        if (isNew && StringUtils.isBlank(model.getTargetConstraintName())) {
			model.setTargetConstraintName("FK" + SecurityHelper.generateRandomSequence(14));
		}

		if ((isNew && sourceEntity != null && StringUtils.isBlank(model.getSourceEntityRefName()))) {
			EntityModel entityModel = entityStore.findById(sourceEntity.getId());
            String columnName = entityModel.getName().toLowerCase();
            columnName = columnName.replaceAll("\\s+", "_");
            model.setSourceEntityRefName(DefaultSqlNameResolver.ID_PREFIX + columnName);
		}

		if (StringUtils.isBlank(model.getTargetEntityRefName())) {
			if (model.getRelationshipType() == RelationshipType.PARENT_CHILD || model.getRelationshipType() == RelationshipType.TREE) {
				model.setTargetEntityRefName(LeadIdPrefixNameResolver.ID_PARENT);
			} else if (targetEntity != null) {
				EntityModel entityModel = entityStore.findById(targetEntity.getId());
                String columnName = entityModel.getName().toLowerCase();
                columnName = columnName.replaceAll("\\s+", "_");
				model.setTargetEntityRefName(DefaultSqlNameResolver.ID_PREFIX + columnName);
			}
		}

		super.save(model);

        indexStore.createOrUpdateIndex(model);
	}

	@Override
	@Transactional
	public void saveForGenerator(RelationshipModel model) {
		if (StringUtils.isBlank(model.getSourceConstraintName()))
			model.setSourceConstraintName("FK" + SecurityHelper.generateRandomSequence(14));

		if (StringUtils.isBlank(model.getTargetConstraintName()))
			model.setTargetConstraintName("FK" + SecurityHelper.generateRandomSequence(14));

		super.saveForGenerator(model);
	}

	@Override
    @Transactional
    public void mergeForGenerator(String uid, RelationshipModel updatedRel, String sourceEntityRefPath, String targetEntityRefPath) {
        RelationshipModel rel = findByUID(uid);
        rel.setSourceEntity(updatedRel.getSourceEntity());

        if (!updatedRel.getPath().equals(rel.getPath())) {
            Criteria cr = getSession().createCriteria(RegistryNodeModel.class);
            cr.add(Restrictions.eq("lookup", updatedRel.getPath()));
            RegistryNodeModel newParent = (RegistryNodeModel) cr.uniqueResult();

            rel.setPath(updatedRel.getPath());
            rel.setParent(newParent);
        }
        rel.setName(updatedRel.getName());
        rel.setLookup(DiffUtils.lookup(updatedRel.getPath(), updatedRel.getName()));
        rel.setRequired(updatedRel.getRequired());
        rel.setOnDeleteOption(updatedRel.getOnDeleteOption());
        rel.setOnUpdateOption(updatedRel.getOnUpdateOption());
        rel.setRelationshipType(updatedRel.getRelationshipType());

        EntityModel sourceEntity = entityStore.findByLookup(
                DiffUtils.lookupByRefPath(sourceEntityRefPath));
        rel.setSourceEntity(sourceEntity);
        rel.setSourceEntityRefName(sourceEntityRefPath);

        if (updatedRel.getRelationshipType() == RelationshipType.TREE) {
            rel.setTargetEntity(sourceEntity);
            rel.setTargetEntityRefName(sourceEntityRefPath);
        } else {
            EntityModel targetEntity = entityStore.findByLookup(
                    DiffUtils.lookupByRefPath(targetEntityRefPath));
            rel.setTargetEntity(targetEntity);
            rel.setTargetEntityRefName(targetEntityRefPath);
        }
        super.saveForGenerator(rel);
    }

    @Override
    @Transactional
    public void resetReverseEngineerMark(final String parentLookup) {
        update("Relationship.resetReverseEngineerMark", "lookupPrefix", parentLookup + '%');
    }

    @Override
    @Transactional
    public void delete(RelationshipModel relationship) {
        IndexModel indexModel = indexStore.findIndexByRelationship(relationship.getId());
        if (indexModel != null) {
            indexStore.delete(indexModel);
        }
        super.delete(relationship);
    }
}