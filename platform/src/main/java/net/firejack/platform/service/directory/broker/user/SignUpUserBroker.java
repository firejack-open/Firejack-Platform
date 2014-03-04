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

package net.firejack.platform.service.directory.broker.user;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@TrackDetails
@Component("signUpUserBroker")
public class SignUpUserBroker extends CreateUserBroker {

	@Autowired
	private IRoleStore roleStore;
	@Autowired
	private IDirectoryStore directoryStore;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "User Sign Up successfully";
	}

	@Override
	protected void processArguments(ServiceRequest<User> request) {
		User userVO = request.getData();

        List<Role> roleVOs = userVO.getRoles();
        if (roleVOs == null) {
            roleVOs = new ArrayList<Role>();
        }
        RoleModel role = roleStore.findByLookup(OpenFlame.SIGN_UP_ROLE);
        Role roleVO = factory.convertTo(Role.class, role);
        roleVOs.add(roleVO);

        roleVOs = new ArrayList<Role>(new HashSet<Role>(roleVOs));

        userVO.setRoles(roleVOs);

        if (userVO.getRegistryNodeId() == null) {
            DirectoryModel directory = directoryStore.findByLookup(OpenFlame.SIGN_UP_DIRECTORY);
            userVO.setRegistryNodeId(directory.getId());
        }

		super.processArguments(request);
	}
}
