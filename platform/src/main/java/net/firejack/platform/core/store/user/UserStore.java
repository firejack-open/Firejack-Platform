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