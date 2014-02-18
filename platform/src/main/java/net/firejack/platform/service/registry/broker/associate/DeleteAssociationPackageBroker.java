package net.firejack.platform.service.registry.broker.associate;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("deleteAssociationPackageBroker")
public class DeleteAssociationPackageBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("systemStore")
	private IRegistryNodeStore<SystemModel> systemStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> message) throws Exception {
		Long systemId = (Long) message.getData().get("systemId");
		Long packageId = (Long) message.getData().get("packageId");

		SystemModel system = systemStore.findById(systemId);
		if (system == null) {
			throw new BusinessFunctionException(null, "systemId");
		}
		PackageModel packageModel = packageStore.findById(packageId);
		if (packageModel == null) {
			throw new BusinessFunctionException(null, "packageId");
		}

		packageStore.removeAssociation(packageModel, system);
		return new ServiceResponse("Package has been removed from associate with system successfully.", true);
	}
}
