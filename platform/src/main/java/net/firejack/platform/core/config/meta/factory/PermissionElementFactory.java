/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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