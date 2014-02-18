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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("readAssignedUserListByEntityRolesBrokerEx")
public class ReadAssignedUserListByEntityRolesBroker extends ListBroker<UserModel, User, NamedValues<Object>> {

    public static final String PARAM_OBJECT_ID = "PARAM_OBJECT_ID";
    public static final String PARAM_OBJECT_TYPE = "PARAM_OBJECT_TYPE";

    @Autowired
    private IUserStore store;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Override
    protected List<UserModel> getModelList(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
        Long objectId = (Long) request.getData().get(PARAM_OBJECT_ID);
        String objectType = (String) request.getData().get(PARAM_OBJECT_TYPE);
        List<UserModel> users = new ArrayList<UserModel>();
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(objectType);
        if (registryNode != null) {
            users = store.findAllUsersHaveContextRolesForRegistryNodeId(objectId, registryNode, getFilter());
        }
        for (UserModel user : users) {
            user.setUserRoles(null);
        }
        return users;
    }

}