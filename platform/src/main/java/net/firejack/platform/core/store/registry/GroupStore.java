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
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component("groupStore")
public class GroupStore extends RegistryNodeStore<GroupModel> implements IGroupStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(GroupModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupModel findById(Long id) {
        GroupModel group = findSingle("Group.findByIdWithDirectory", "id", id);
        if (group.getRoles() != null) {
            Hibernate.initialize(group.getRoles());
            for (RoleModel role : group.getRoles()) {
                Hibernate.initialize(role);
            }
        }
        return group;
    }

    @Override
    public List<GroupModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        if (term != null) {
            Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
            LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
            criterions.add(expressionAll);
        } else {
            criterions.add(registryNodeIdCriterion);
        }
        return findAllWithFilter(criterions, filter);
    }

    @Override
    public List<GroupModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (term != null) {
            Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
            criterions.add(nameCriterion);
        }
        return findAllWithFilter(criterions, filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<GroupModel> findAll(Collection<Long> excludeIds) {
        Criteria criteria = getSession().createCriteria(getClazz());
        if (excludeIds != null && !excludeIds.isEmpty()) {
            criteria.add(Restrictions.not(Restrictions.in("id", excludeIds)));
        }
        return (List<GroupModel>) criteria.list();
    }

}