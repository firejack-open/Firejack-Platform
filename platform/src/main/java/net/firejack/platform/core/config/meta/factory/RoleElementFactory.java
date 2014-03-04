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

import net.firejack.platform.core.config.meta.element.authority.RoleElement;
import net.firejack.platform.core.config.meta.element.authority.RolePermissionReference;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.store.registry.IPermissionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public class RoleElementFactory extends PackageDescriptorConfigElementFactory<RoleModel, RoleElement> {

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    /***/
    public RoleElementFactory() {
        setEntityClass(RoleModel.class);
        setElementClass(RoleElement.class);
    }

    public RoleModel getEntity(RoleElement roleElement) {
        configElementFactory.checkElementName(roleElement);
        RoleModel role = populateEntity(roleElement);
        if (role == null) {
            throw new OpenFlameRuntimeException("Failed to instantiate entity.");
        }
        role.setName(roleElement.getName());
        role.setDescription(roleElement.getDescription());
        role.setPath(DiffUtils.lookupByRefPath(roleElement.getPath()));
        role.setLookup(DiffUtils.lookup(role.getPath(), role.getName()));
        RegistryNodeModel parent = registryNodeStore.findByLookup(role.getPath());
        role.setParent(parent);
        initializeModelUID(role, roleElement);
        return role;
    }


    @Override
    protected void initDescriptorElementSpecific(RoleElement roleElement, RoleModel role) {
        super.initDescriptorElementSpecific(roleElement, role);

        roleElement.setPath(role.getPath());

        List<PermissionModel> permissions = permissionStore.findRolePermissions(role.getId());
        if (permissions != null && !permissions.isEmpty()) {
            List<RolePermissionReference> permissionRefPathList = new ArrayList<RolePermissionReference>();
            for (PermissionModel permission : permissions) {
                RolePermissionReference ref = new RolePermissionReference();
                ref.setPath(permission.getLookup());
                permissionRefPathList.add(ref);
            }
            roleElement.setPermissions(permissionRefPathList);
        }
    }

    @Override
    protected String getRefPath(RoleModel entity) {
        return entity.getPath();
    }

}
