/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
