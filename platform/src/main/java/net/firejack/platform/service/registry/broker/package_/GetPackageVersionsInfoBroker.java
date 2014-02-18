/**
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


package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.registry.domain.PackageVersionInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("getPackageVersionsInfoBroker")
public class GetPackageVersionsInfoBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<PackageVersionInfo>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<PackageVersionInfo> perform(ServiceRequest<SimpleIdentifier<Long>> message) throws Exception {
		Long packageId = message.getData().getIdentifier();
		PackageModel packageRN = packageStore.findById(packageId);
		List<PackageVersionInfo> packageVersionInfoVOs = packageVersionHelper.populatePackageVersionInfo(packageRN);
		return new ServiceResponse<PackageVersionInfo>(packageVersionInfoVOs, "Load Package Versions Info successfully", true);
	}
}
