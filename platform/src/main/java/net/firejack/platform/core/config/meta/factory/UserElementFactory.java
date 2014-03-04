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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.directory.UserRoleRef;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public class UserElementFactory extends PackageDescriptorConfigElementFactory<UserModel, UserElement> {

    /***/
    public UserElementFactory() {
        setEntityClass(UserModel.class);
        setElementClass(UserElement.class);
    }

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Override
    protected void initEntitySpecific(UserModel user, UserElement userElement) {
        super.initEntitySpecific(user, userElement);

        RegistryNodeModel parent = registryNodeStore.findByLookup(userElement.getPath());
        user.setRegistryNode(parent);
        user.setUsername(userElement.getName());
        user.setPassword(userElement.getPassword());
        user.setEmail(userElement.getEmail());
        user.setFirstName(userElement.getFirstName());
        user.setLastName(userElement.getLastName());
        user.setMiddleName(userElement.getMiddleName());
    }

    @Override
    protected void initDescriptorElementSpecific(UserElement userElement, UserModel user) {
        super.initDescriptorElementSpecific(userElement, user);

        userElement.setName(user.getUsername());
        userElement.setPassword(user.getPassword());
        userElement.setEmail(user.getEmail());
        userElement.setFirstName(user.getFirstName());
        userElement.setMiddleName(user.getMiddleName());
        userElement.setLastName(user.getLastName());
        String refPath = user.getRegistryNode().getLookup();
        userElement.setPath(refPath);
        if (!user.getUserRoles().isEmpty()) {
            List<UserRoleRef> userRoleRefs = new ArrayList<UserRoleRef>();
            for (UserRoleModel userRole : user.getUserRoles()) {
                refPath = userRole.getRole().getLookup();
                if (StringUtils.isNotBlank(refPath)) {
                    UserRoleRef ref = new UserRoleRef();
                    ref.setPath(refPath);
                    userRoleRefs.add(ref);
                }
            }
            userElement.setUserRoles(userRoleRefs);
        }
    }
}