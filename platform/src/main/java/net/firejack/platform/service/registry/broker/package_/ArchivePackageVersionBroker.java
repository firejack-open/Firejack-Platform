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

import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.export.IPackageExporter;
import net.firejack.platform.core.config.export.PackageExportResult;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@TrackDetails
@ProgressComponent(weight = 20)
@Component("archivePackageVersionBroker")
public class ArchivePackageVersionBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<PackageVersion>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
    @Qualifier("basicPackageExporter")
	private IPackageExporter packageExporter;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long packageId = request.getData().getIdentifier();

		PackageModel model = packageStore.findById(packageId);
		if (model == null) {
			throw new BusinessFunctionException(null, "package.not.found.by.id", packageId);
		}

		try {
			Integer lastVersion = packageVersionHelper.getMaxVersion(model.getId());
			Integer next = lastVersion + 1;
			String nextVersion = VersionUtils.convertToVersion(next);

			IPackageDescriptor descriptor = packageExporter.exportPackageDescriptor(model.getId());
			PackageExportResult result = packageExporter.exportPackageDescriptor(descriptor);

			descriptor.setVersion(nextVersion);
			result = packageExporter.exportPackageDescriptor(descriptor);

			packageVersionHelper.archiveVersion(model, next, result.getPackageXml());

			packageStore.save(model, next);
			PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(model);
			return new ServiceResponse<PackageVersion>(packageVersionVO, "PackageVersion (" + packageVersionVO.getVersion() + ") has created successfully", true);
		} catch (IOException e) {
			throw new BusinessFunctionException(e);
		}

	}
}
