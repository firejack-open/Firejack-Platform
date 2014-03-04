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