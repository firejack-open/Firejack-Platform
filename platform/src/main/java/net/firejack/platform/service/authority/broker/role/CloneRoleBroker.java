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

package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class CloneRoleBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<Role>> {

    public static final String PARAM_CLONED_ROLE_ID = "id";
    public static final String PARAM_NEW_ROLE_NAME = "name";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<Role> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long roleId = (Long) request.getData().get(PARAM_CLONED_ROLE_ID);
        String roleName = (String) request.getData().get(PARAM_NEW_ROLE_NAME);
        ServiceResponse<Role> response;
        if (roleId == null) {
            response = new ServiceResponse<Role>("Id of the role to clone is not specified.", false);
        } else if (StringUtils.isBlank(roleName)) {
            response = new ServiceResponse<Role>();
        } else {
            RoleModel roleToClone = roleStore.findById(roleId);
            if (roleToClone == null) {
                response = new ServiceResponse<Role>("Role to clone is not found in the database.", false);
            } else {
                Role role = factory.convertTo(Role.class, roleToClone);
                role.setId(null);
                role.setName(roleName);
                role.setLookup(DiffUtils.lookup(role.getPath(), roleName));
                RoleModel newRoleModel = factory.convertFrom(RoleModel.class, role);
                roleStore.save(newRoleModel);
                role = factory.convertTo(Role.class, newRoleModel);
                response = new ServiceResponse<Role>(role, "Role is cloned successfully.", true);
            }
        }
        return response;
    }

}