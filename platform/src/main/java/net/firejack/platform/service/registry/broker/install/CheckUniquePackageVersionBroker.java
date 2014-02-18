package net.firejack.platform.service.registry.broker.install;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component("checkUniquePackageVersionBroker")
public class CheckUniquePackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get("packageId");
		String versionName = (String) request.getData().get("versionName");
		Integer version = VersionUtils.convertToNumber(versionName);
		boolean exists = packageVersionHelper.existsVersion(packageId, version);
		return new ServiceResponse("Package version is unique: " + !exists, !exists);
	}
}
