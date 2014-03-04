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

import net.firejack.platform.core.config.meta.element.authority.PermissionElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;

import java.util.Arrays;


public class PermissionElementFactory extends PackageDescriptorConfigElementFactory<PermissionModel, PermissionElement> {

    /***/
    public PermissionElementFactory() {
        setEntityClass(PermissionModel.class);
        setElementClass(PermissionElement.class);
    }

    public PermissionModel getEntity(PermissionElement permissionElement) {
        configElementFactory.checkElementName(permissionElement);
        PermissionModel permission = populateEntity(permissionElement);
        if (permission == null) {
            throw new OpenFlameRuntimeException("Failed to instantiate entity.");
        }
        permission.setName(permissionElement.getName());
        permission.setDescription(permissionElement.getDescription());
        permission.setPath(DiffUtils.lookupByRefPath(permissionElement.getPath()));
        permission.setLookup(DiffUtils.lookup(permission.getPath(), permission.getName()));
        RegistryNodeModel parent = registryNodeStore.findByLookup(permission.getPath());
        permission.setParent(parent);
        RegistryNodeModel association = registryNodeStore.findByLookup(permission.getLookup());
        if (association instanceof ActionModel) {
            permission.setActions(Arrays.asList((ActionModel) association));
        } else if (association instanceof NavigationElementModel) {
            permission.setNavigationElements(Arrays.asList((NavigationElementModel) association));
        } else if (association instanceof ResourceLocationModel) {
            permission.setResourceLocations(Arrays.asList((ResourceLocationModel) association));
        }
        initializeModelUID(permission, permissionElement);
        return permission;
    }

    @Override
    protected String getRefPath(PermissionModel entity) {
        return entity.getPath();
    }
}