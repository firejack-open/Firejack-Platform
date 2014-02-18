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