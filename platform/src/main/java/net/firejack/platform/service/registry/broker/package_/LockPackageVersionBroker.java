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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.domain.PackageVersionInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("lockPackageVersionBroker")
public class LockPackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Package>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private FileHelper helper;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<Package> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get("packageId");
		PackageVersionInfo packageVersionInfoVO = (PackageVersionInfo) request.getData().get("data");
		PackageModel packageRN = packageStore.findById(packageId);

		Integer newVersionNumber = VersionUtils.convertToNumber(packageVersionInfoVO.getVersionName());
		if (newVersionNumber == null) {
			throw new BusinessFunctionException("You can't change version number because it contains Alphabetic symbols.");
		}

		Integer maxVersion = packageVersionHelper.getMaxVersion(packageId);
		if (newVersionNumber > maxVersion) {
			Integer oldVersion = packageRN.getVersion();
			packageStore.save(packageRN, newVersionNumber);

			OPFEngine.FileStoreService.renameDirectory(OpenFlame.FILESTORE_BASE,newVersionNumber.toString(), helper.getVersion(), packageRN.getId().toString(), oldVersion.toString());
		} else {
			throw new BusinessFunctionException("You can't change version number to low number.");
		}

		Package dto = factory.convertTo(Package.class, packageRN);

		PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
		dto.setPackageVersion(packageVersionVO);
		return new ServiceResponse<Package>(dto, "Package version lock successfully", true);
	}
}
