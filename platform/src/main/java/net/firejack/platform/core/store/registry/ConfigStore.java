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
