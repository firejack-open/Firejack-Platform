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

import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Component("userProfileFieldGroupStore")
public class BasicUserProfileFieldGroupStore extends RegistryNodeStore<UserProfileFieldGroupModel> implements IUserProfileFieldGroupStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(UserProfileFieldGroupModel.class);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @Transactional(readOnly = true)
    public UserProfileFieldGroupModel findUserProfileFieldGroupById(Long userProfileFieldGroupId, boolean initProfileFields) {
        UserProfileFieldGroupModel group = getHibernateTemplate().get(UserProfileFieldGroupModel.class, userProfileFieldGroupId);
        if (group != null && initProfileFields) {
            Hibernate.initialize(group.getUserProfileFields());
        }
        return group;
    }

    @SuppressWarnings({"JpaQueryApiInspection", "unchecked"})
    @Override
    @Transactional(readOnly = true)
    public List<UserProfileFieldGroupModel> findUserProfileFieldGroupsByRegistryNodeId(Long registryNodeId) {
        return getHibernateTemplate().findByNamedQueryAndNamedParam(
                "UserProfileFieldGroup.findByRegistryNodeId",
                "registryNodeId", registryNodeId
        );
    }

    @SuppressWarnings({"JpaQueryApiInspection"})
    @Override
    @Transactional(readOnly = true)
    public UserProfileFieldGroupModel findUserProfileFieldGroupByRegistryNodeIdAndName(Long registryNodeId, String name) {
        List result = getHibernateTemplate().findByNamedQueryAndNamedParam(
                "UserProfileFieldGroup.findByRegistryNodeIdAndName",
                new String[]{"registryNodeId", "name"},
                new Object[]{registryNodeId, name}
        );
        return result.isEmpty() ? null : (UserProfileFieldGroupModel) result.get(0);
    }

    @Override
    @Transactional
    public void saveOrUpdate(UserProfileFieldGroupModel userProfileFieldGroup) {
        super.saveOrUpdate(userProfileFieldGroup);
    }

    @Override
    @Transactional
    public void delete(UserProfileFieldGroupModel userProfileFieldGroup) {
        super.delete(userProfileFieldGroup);
    }


	@Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
		List<UserProfileFieldGroupModel> groups = findAllByParentIdWithFilter(registryNodeId, null);
		super.deleteAll(groups);
    }

    @Override
    @Transactional
    public void mergeForGenerator(UserProfileFieldGroupModel model) {
        if (model.getUid() != null) {
            UserProfileFieldGroupModel oldModel = findByUID(model.getUid().getUid());
            if (oldModel != null) {
                oldModel.setName(model.getName());
                oldModel.setDescription(model.getDescription());
                LookupModel parent = this.getRegistryNodeStore().findByLookup(model.getPath());
                if (parent != null) {
                    oldModel.setParent((RegistryNodeModel) parent);
                    oldModel.setPath(model.getPath());
                }
                oldModel.setLookup(oldModel.getPath() + '.' + StringUtils.normalize(model.getName()));
                saveOrUpdate(oldModel);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String findRegistryNodeRefPath(Long registryNodeId) {
        UserProfileFieldGroupModel profileFieldGroupModel = findById(registryNodeId);
        String path;
        if (profileFieldGroupModel == null) {
            path = null;
        } else {
            path = profileFieldGroupModel.getPath();
        }
        return path;
    }
}
