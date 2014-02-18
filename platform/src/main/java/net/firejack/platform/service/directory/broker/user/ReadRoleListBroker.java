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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readRoleListBroker")
public class ReadRoleListBroker extends ListBroker<RoleModel, Role, NamedValues> {

	@Autowired
	private IRoleStore store;

	@Override
	protected List<RoleModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		Long registryNodeId = (Long) request.getData().get("registryNodeId");
		List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
		Boolean isGlobal = (Boolean) request.getData().get("isGlobal");
		SpecifiedIdsFilter<Long> filter = getFilter(true);

		filter.setAll(true);
		if (isGlobal == null) {
			isGlobal = false;
		}
		List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
		filter.getUnnecessaryIds().addAll(exceptPermissionIds);
		List<RoleModel> roles = store.findAllByRegistryNodeIdWithFilter(registryNodeId, filter, isGlobal);
		for (RoleModel role : roles) {
			role.setPermissions(null);
		}
		return roles;
	}
}
