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
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.store.registry.IPermissionStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.permission.IPermissionContainerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

/**
 * This permission container rule applies if request made by Millennial user
 */
public class ExternalUserRule implements IPermissionContainerRule {

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    private String externalUserRoleLookup;

    @Required
    public void setExternalUserRoleLookup(String externalUserRoleLookup) {
        this.externalUserRoleLookup = externalUserRoleLookup;
    }

    /**
     * @see net.firejack.platform.web.security.permission.IPermissionContainerRule#loadGrantedActions(java.security.Principal)
     */
    @Override
    public List<UserPermission> loadGrantedActions(Principal p) {
        if (p instanceof OpenFlamePrincipal) {
            OpenFlamePrincipal opfPrincipal = (OpenFlamePrincipal) p;
            if (!opfPrincipal.isGuestPrincipal() && opfPrincipal.getUserInfoProvider().getId() < 0) {
                return getExternalUserPermissions();
            }
        }
        return null;
    }

    private List<UserPermission> getExternalUserPermissions() {
        RoleModel role = roleStore.findByLookup(externalUserRoleLookup);
        List<PermissionModel> permissions = permissionStore.findRolePermissions(role.getId());
        List<UserPermission> userPermissions = new LinkedList<UserPermission>();
        if (permissions != null) {
            for (PermissionModel permission : permissions) {
                UserPermission userPermission = new UserPermission(permission.getLookup());
                userPermissions.add(userPermission);
            }
        }
        return userPermissions;
    }
}