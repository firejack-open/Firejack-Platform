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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.content.utils.ResourceProcessor;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;



public class FieldContainerStore<R extends FieldContainerRegistryNode> extends RegistryNodeStore<R> implements IFieldContainerStore<R> {

    @Autowired
    @Qualifier("fieldStore")
    private IFieldStore fieldStore;

    @Autowired
    @Qualifier("resourceProcessor")
    private ResourceProcessor resourceProcessor;

    @Override
    @Transactional(readOnly = true)
    public R findById(final Long id) {
        final R example = instantiate();
        return getHibernateTemplate().execute(new HibernateCallback<R>() {
            @Override
            public R doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.add(Restrictions.eq("id", id));
                criteria.setFetchMode("fields", FetchMode.JOIN);
                return (R) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllByLikeLookupPrefix(final String lookupPrefix) {
        if (StringUtils.isBlank(lookupPrefix)) {
            return Collections.emptyList();
        }
	    return getHibernateTemplate().execute(new HibernateCallback<List<R>>() {
		    public List<R> doInHibernate(Session session) throws HibernateException, SQLException {
			    Criteria criteria = session.createCriteria(getClazz());
			    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			    criteria.setFetchMode("fields", FetchMode.JOIN);
			    criteria.add(Restrictions.like("lookup", lookupPrefix + "%"));
			    return (List<R>) criteria.list();
		    }
	    });
    }

    @Override
    @Transactional(readOnly = true)
    public R findWithInheritedFieldsById(final Long id) {
        final List<Long> parentIds = new ArrayList<Long>();
        List<Object[]> arrayIds = findAllIdAndParentId();
        findCollectionParentIds(parentIds, id, arrayIds);
        parentIds.remove(id);

        R container = findById(id);
        container.getFields();
        for (Long parentId : parentIds) {
            List<FieldModel> fields = fieldStore.findFieldsByRegistryNodeId(parentId);
            container.getFields().addAll(fields);
        }
        return container;
    }

    @Override
    @Transactional
    public void save(R registryNode) {
        saveInternal(registryNode, true);
    }

    @Override
    @Transactional
    public void saveForGenerator(R directory) {
        saveInternal(directory, false);
    }

    protected void saveInternal(R registryNode, final boolean useDefaultRestAction) {
        boolean isNew = registryNode.getId() == null;
        List<FieldModel> fields = registryNode.getFields();
        registryNode.setFields(null);

        externalSave(registryNode, useDefaultRestAction);

        saveFields(registryNode, isNew, fields, new PrepareCallback<FieldModel>() {
            @Override
            public void callback(FieldModel model) {
                List<EntityModel> options = model.getOptions();
                for (EntityModel option : options) {
                    if (option.getId() == null)
                        saveInternal((R) option, useDefaultRestAction);
                }
            }
        });
    }

    protected void externalSave(R registryNode, boolean useDefaultRestAction) {
	    super.save(registryNode, useDefaultRestAction);
    }

    protected boolean saveFields(R registryNode, boolean aNew, List<FieldModel> fields) {
        return saveFields(registryNode, aNew, fields, null);
    }

    protected boolean saveFields(R registryNode, boolean aNew, List<FieldModel> fields, PrepareCallback<FieldModel> callback) {
        boolean hasChangedFields = false;
        List<FieldModel> newFields = new ArrayList<FieldModel>();
        Map<Long, FieldModel> restFields = new HashMap<Long, FieldModel>();
        if (fields != null) {
            for (FieldModel field : fields) {
                field.setParent(registryNode);
                if (field.getId() == null) {
                    newFields.add(field);
                } else {
                    restFields.put(field.getId(), field);
                }
            }
        }

        if (!aNew) {
            List<FieldModel> fieldsWithUpdatedDescriptions = new ArrayList<FieldModel>();
            List<FieldModel> updateFields = new ArrayList<FieldModel>();
            List<FieldModel> removeFields = new ArrayList<FieldModel>();
            List<FieldModel> existFields = fieldStore.findFieldsByRegistryNodeId(registryNode.getId());
            for (FieldModel existField : existFields) {
                if (restFields.keySet().contains(existField.getId())) {
                    FieldModel field = restFields.get(existField.getId());
                    //Update description resource in case if description was changed.
                    //if new description of field is empty do nothing with old description resource
                    if (StringUtils.isNotBlank(field.getDescription()) &&
                            !StringUtils.equals(field.getDescription(), existField.getDescription())) {
                        fieldsWithUpdatedDescriptions.add(field);
                    }
                    ClassUtils.copyProperties(field, existField, false);
                    field = existField;
                    updateFields.add(field);
                } else {
                    removeFields.add(existField);
                }
            }

            for (FieldModel field : removeFields) {
                fieldStore.deleteById(field.getId());
                resourceProcessor.deleteFieldDescriptionContent(field);
                hasChangedFields = true;
            }
            for (FieldModel field : updateFields) {
                field.setParent(registryNode);
                fieldStore.merge(field);
            }
            resourceProcessor.updateFieldsDescriptionContent(Cultures.AMERICAN, fieldsWithUpdatedDescriptions);
        }

        for (FieldModel field : newFields) {
            field.setParent(registryNode);
            if (callback != null && field.getOptions() != null)
                callback.callback(field);
            fieldStore.saveOrUpdate(field);
            hasChangedFields = true;
        }
        resourceProcessor.createFieldsDescriptionContent(Cultures.AMERICAN, newFields);
        registryNode.setFields(fields);
        merge(registryNode);
        return hasChangedFields;
    }

    @Override
    @Transactional(readOnly = true)
    public List<R> findAllWithFilterWithoutFields(SpecifiedIdsFilter<Long> filter) {
        List<R> registryNodes = findAllWithFilter(filter);
        for (R registryNode : registryNodes) {
            registryNode.setFields(null);
        }
        return registryNodes;
    }

    @Override
    @Transactional
    public void delete(R fieldable) {
        fieldStore.deleteAllByRegistryNodeId(fieldable.getId());
        super.delete(fieldable);
    }

}