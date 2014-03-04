/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
