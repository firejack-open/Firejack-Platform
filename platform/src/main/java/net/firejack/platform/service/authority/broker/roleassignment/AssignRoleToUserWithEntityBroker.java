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

package net.firejack.platform.service.authority.broker.roleassignment;

import net.firejack.platform.api.authority.domain.AssignedRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.registry.ISecuredRecordStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("assignRoleToUserWithEntityBrokerEx")
public class AssignRoleToUserWithEntityBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<AssignedRole>> {

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_ROLE_ID = "PARAM_ROLE_ID";
    public static final String PARAM_OBJECT_ID = "PARAM_OBJECT_ID";
    public static final String PARAM_OBJECT_TYPE = "PARAM_OBJECT_TYPE";
    public static final String PARAM_ASSIGNED_FLAG = "PARAM_ASSIGNED_FLAG";

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("userRoleStore")
    private IUserRoleStore userRoleStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore securedRecordStore;

    @Override
    protected ServiceResponse<AssignedRole> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        Long userId = (Long) request.getData().get(PARAM_USER_ID);
        Long roleId = (Long) request.getData().get(PARAM_ROLE_ID);
        Long objectId = (Long) request.getData().get(PARAM_OBJECT_ID);
        String objectType = (String) request.getData().get(PARAM_OBJECT_TYPE);
        Boolean assigned = (Boolean) request.getData().get(PARAM_ASSIGNED_FLAG);
        UserRoleModel userRole = userRoleStore.findContextRole(userId, roleId, objectId, objectType);
        RoleModel role = roleStore.findById(roleId);
        if (userRole == null) {
            UserModel user = userStore.findById(userId);
            userRole = new UserRoleModel(user, role);
        }
        if (assigned) {
            RegistryNodeModel registryNode = registryNodeStore.findByLookup(objectType);
            SecuredRecordModel securedRecord = securedRecordStore.findByExternalIdAndRegistryNode(objectId, registryNode);
            userRole.setSecuredRecord(securedRecord);
            userRole.setInternalId(objectId);
            userRole.setType(objectType);
            userRoleStore.saveOrUpdate(userRole);
        } else {
            userRoleStore.delete(userRole);
        }
        role.setPermissions(null);
        AssignedRole assignedRole = factory.convertTo(AssignedRole.class, role);
        assignedRole.setAssigned(assigned);
        return new ServiceResponse<AssignedRole>(assignedRole, "Role has been assigned.", true);
    }
}