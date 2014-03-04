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

package net.firejack.platform.service.authority.utils;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerRule;
import org.springframework.beans.factory.annotation.Required;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;


/**
 * This permission container rule applies in cases when console application was not installed yet
 */
public class InstallerRule implements IPermissionContainerRule {

    private List<String> permissionList;

    /**
     * Set user permission list for admin during installer phase
     * @param permissionList permission list
     */
    @Required
    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    /**
     * @see net.firejack.platform.web.security.permission.IPermissionContainerRule#loadGrantedActions(java.security.Principal)
     */
    @Override
    public List<UserPermission> loadGrantedActions(Principal p) {
        List<UserPermission> userPermissions = null;
        if (!ConfigContainer.isAppInstalled()) {
            userPermissions = new LinkedList<UserPermission>();
            for (String permission : permissionList) {
                userPermissions.add(new UserPermission(permission,
                        RegistryNodeType.RESOURCE_LOCATION.name(), null));
            }
        }
        return userPermissions;
    }

}