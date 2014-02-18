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
