package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@Component("configStore")
public class ConfigStore extends RegistryNodeStore<ConfigModel> implements IConfigStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(ConfigModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigModel findById(final Long id) {
        final ConfigModel example = instantiate();
        return getHibernateTemplate().execute(new HibernateCallback<ConfigModel>() {
            @Override
            public ConfigModel doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.add(Restrictions.eq("id", id));
                criteria.setFetchMode("parent", FetchMode.JOIN);
                return (ConfigModel) criteria.uniqueResult();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        criterions.add(nameCriterion);
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
        return findAllWithFilter(criterions, filter);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, String> findConfigsByLookup(final String lookup) {
        final ConfigModel example = instantiate();
        List<ConfigModel> configs = (List<ConfigModel>) getHibernateTemplate().executeFind(new HibernateCallback<List<ConfigModel>>() {
            @Override
            public List<ConfigModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.add(Restrictions.eq("path", lookup));
                return (List<ConfigModel>) criteria.list();
            }
        });

        Map<String, String> result = new HashMap<String, String>();
        for (ConfigModel config : configs) {
            result.put(config.getName(), config.getValue());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigModel> findListByLookup(List<String> lookup) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.in("lookup", lookup));
        return findAllWithFilter(criterions, null);
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<ConfigModel> configs = findAllByParentIdWithFilter(registryNodeId, null);
        super.deleteAll(configs);
    }

    @Override
    @Transactional
    public void save(ConfigModel config) {
        saveWithoutUpdateChildren(config);
    }

}
