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

package net.firejack.platform.service.directory.broker;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.store.user.IBaseUserStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadBaseUserBroker<M extends BaseUserModel, DTO extends BaseUser> extends ReadBroker<M, DTO> {

	@Autowired
    private IUserRoleStore userRoleStore;

	@Override
	protected abstract IBaseUserStore<M> getStore();

    protected M getUserById(Long id) {
        return getStore().findById(id);
    }

	@Override
	protected M getEntity(Long id) {
        return getUserById(id);
		/*M user = getUserById(id);
		List<UserRoleModel> userRoles = userRoleStore.findGlobalRoles(id);
		for (UserRoleModel userRole : userRoles) {
			userRole.getRole().setPermissions(null);
		}
		user.setUserRoles(userRoles);
		return user;*/
	}

    @Override
    protected DTO convertToModel(M entity) {
        DTO dto = super.convertToModel(entity);
        Long userId = entity.getId();
        if (userId != null) {
            /*Map<RoleModel, Boolean> assignedRolesMap = roleStore.findAllAssignedRolesByUserId(userId);
            List<Role> roleList;
            if (assignedRolesMap == null) {
                roleList = null;
            } else {
                roleList = factory.convertTo(Role.class, new ArrayList<RoleModel>(assignedRolesMap.keySet()));
                if (roleList != null && !roleList.isEmpty()) {
                    Map<Long, Boolean> idScopeMap = new HashMap<Long, Boolean>();
                    for (Map.Entry<RoleModel, Boolean> entry : assignedRolesMap.entrySet()) {
                        idScopeMap.put(entry.getKey().getId(), entry.getValue());
                    }
                    for (Role role : roleList) {
                        role.setGlobal(idScopeMap.get(role.getId()));
                    }
                }
            }*/
            List<UserRoleModel> userRoleModels = userRoleStore.findAllByUserId(userId);
            List<UserRole> userRoles;
            if (userRoleModels == null) {
                userRoles = null;
            } else {
                userRoles = new ArrayList<UserRole>();
                for (UserRoleModel userRoleModel : userRoleModels) {
                    UserRole userRole = new UserRole();
                    userRole.setId(userRoleModel.getId());
                    userRole.setCreated(userRoleModel.getCreated());
                    Role role = factory.convertTo(Role.class, userRoleModel.getRole());
                    role.setGlobal(userRoleModel.getInternalId() == null && StringUtils.isBlank(userRoleModel.getType()));
                    userRole.setRole(role);
                    userRole.setModelId(userRoleModel.getInternalId());
                    userRole.setTypeLookup(userRoleModel.getType());
                    if (userRoleModel.getSecuredRecord() != null) {
                        userRole.setSecuredRecordId(userRoleModel.getSecuredRecord().getId());
                    }
                    userRoles.add(userRole);
                }
            }
            dto.setUserRoles(userRoles);
        }

        return dto;
    }
}
