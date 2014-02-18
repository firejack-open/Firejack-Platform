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