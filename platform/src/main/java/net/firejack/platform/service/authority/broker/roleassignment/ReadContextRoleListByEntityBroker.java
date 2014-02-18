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
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("readContextRoleListByEntityBrokerEx")
public class ReadContextRoleListByEntityBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<AssignedRole>> {

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_OBJECT_ID = "PARAM_OBJECT_ID";
    public static final String PARAM_OBJECT_TYPE = "PARAM_OBJECT_TYPE";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("userRoleStore")
    private IUserRoleStore userRoleStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    private SpecifiedIdsFilter filter;

    @Override
    protected ServiceResponse<AssignedRole> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        Long objectId = (Long) request.getData().get(PARAM_OBJECT_ID);
        String objectType = (String) request.getData().get(PARAM_OBJECT_TYPE);
        List<RoleModel> roles = new ArrayList<RoleModel>();
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(objectType); //TODO need to review next three lines, may be better use lookup instead class
        if (registryNode != null) {
            roles = roleStore.findAllByRegistryNodeTypeWithFilter(registryNode.getClass(), getFilter());
        }
        for (RoleModel role : roles) {
            role.setPermissions(null);
        }
        List<AssignedRole> assignedRoleList = factory.convertTo(AssignedRole.class, roles);

        List<Long> assignedRoleIds = new ArrayList<Long>();
        Long userId = (Long) request.getData().get(PARAM_USER_ID);
        if (userId != null) {
            List<UserRoleModel> assignedRoles = userRoleStore.findContextRolesByUserIdAndRegistryNodeId(userId, objectId, objectType);
            for (UserRoleModel assignedRole : assignedRoles) {
                assignedRoleIds.add(assignedRole.getRole().getId());
            }
        }
        for (AssignedRole assignedRole : assignedRoleList) {
            boolean assigned = assignedRoleIds.contains(assignedRole.getId());
            assignedRole.setAssigned(assigned);
        }
        return new ServiceResponse<AssignedRole>(assignedRoleList);
    }

    public SpecifiedIdsFilter getFilter() {
        SpecifiedIdsFilter filter = getFilter(true);
        if (filter.getAll() == null) {
            filter.setAll(true);
        }
        return filter;
    }

    public SpecifiedIdsFilter getFilter(boolean isInitialize) {
        if (this.filter == null || isInitialize) {
            initFilter();
        }
        return this.filter;
    }

    private void initFilter() {
        this.filter = new SpecifiedIdsFilter();
    }
}