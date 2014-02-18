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
