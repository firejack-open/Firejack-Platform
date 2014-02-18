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

package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component("userStore")
public class UserStore extends BaseUserStore<UserModel> implements IUserStore {

	@Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore registryNodeStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(UserModel.class);
    }

	@Override
	@Transactional(readOnly = true)
	public List<UserModel> findAllByRegistryNodeIdWithRoles(Long registryNodeId) {
	    List<Long> registryNodeIds = new ArrayList<Long>();
	    List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
	    registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
	    registryNodeIds.add(registryNodeId);
	    return find(null, null, "User.findAllByRegistryNodeIdsWithRoles",
	            "registryNodeIds", registryNodeIds);
	}

    @Override
    @Transactional(readOnly = true)
    public UserModel findUserByFacebookId(Long facebookId) {
        return findSingle("User.findByFacebookId", "facebookId", facebookId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findUserByTwitterId(Long twitterId) {
        return findSingle("User.findByTwitterId", "twitterId", twitterId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findUserByLinkedInId(String linkedInId) {
        return findSingle("User.findByLinkedInId", "linkedInId", linkedInId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findUserByToken(String token) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion tokenCriterion = Restrictions.eq("resetPasswordToken", token);
        criterions.add(tokenCriterion);
        return findByCriteria(criterions, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findUserByLdapDN(String ldapUserDN) {
        UserModel result;
        if (StringUtils.isBlank(ldapUserDN)) {
            result = null;
        } else {
            Criteria criteria = getSession().createCriteria(UserModel.class);
            criteria.add(Restrictions.eq("ldapUserDN", ldapUserDN));
            result = (UserModel) criteria.uniqueResult();
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<UserModel> findByRoles(List<Long> roleIdList) {
        List<UserModel> users;
        if (roleIdList == null || roleIdList.isEmpty()) {
            users = Collections.emptyList();
        } else {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.createAlias("userRoles", "ur");
            criteria.add(Restrictions.in("ur.role.id", roleIdList));
            users = (List<UserModel>) criteria.list();
        }
        return users;
    }
}