/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.user.SystemUserModel;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component("systemUserStore")
public class SystemUserStore extends BaseUserStore<SystemUserModel> implements ISystemUserStore {

    @PostConstruct
    public void init() {
        setClazz(SystemUserModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemUserModel findById(Long id) {
        SystemUserModel systemUserModel = super.findById(id);
        Hibernate.initialize(systemUserModel.getSystem());
        return systemUserModel;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemUserModel> findBySystemId(Long id) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("system.id", id));
        return findAllWithFilter(criterions, null);
    }

}
