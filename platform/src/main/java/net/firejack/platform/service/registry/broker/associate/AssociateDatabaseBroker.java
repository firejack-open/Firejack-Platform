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

package net.firejack.platform.service.registry.broker.associate;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("associateDatabaseBroker")
public class AssociateDatabaseBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private IPackageStore packageStore;

	@Autowired
	private IDatabaseStore databaseStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> message) throws Exception {
		Long packageId = (Long) message.getData().get("packageId");
		Long databaseId = (Long) message.getData().get("databaseId");

		PackageModel packageModel = packageStore.findById(packageId);
		if (packageModel == null) {
			throw new BusinessFunctionException("Package ID parameter should not be null", "packageId");
		}
        DatabaseModel databaseModel = databaseStore.findById(databaseId);
		if (databaseModel == null) {
			throw new BusinessFunctionException("Database ID parameter should not be null", "databaseId");
		}

		packageModel.setDatabase(databaseModel);
        packageStore.saveOrUpdate(packageModel);
		return new ServiceResponse("Database has been associated successfully.", true);
	}
}
