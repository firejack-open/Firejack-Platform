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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@TrackDetails
@ProgressComponent(weight = 20)
@Component("activatePackageVersionBroker")
public class ActivatePackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Package>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private FileHelper helper;
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<Package> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get("packageId");
		Integer version = (Integer) request.getData().get("version");

		PackageModel packageRN = packageStore.findById(packageId);

		String packageVersionFolder = FileUtils.construct(helper.getVersion(), packageId.toString(), version.toString());

		InputStream packageXmlFile = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension(),packageVersionFolder);
		InputStream resourceZipFile = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension(),packageVersionFolder);

		StatusProviderTranslationResult result = packageInstallationService.activatePackage(packageXmlFile, resourceZipFile);
		IOUtils.closeQuietly(packageXmlFile);
		IOUtils.closeQuietly(resourceZipFile);

		if (result.getResult()) {
			Integer oldVersion = packageRN.getVersion();
			packageRN = packageStore.updatePackageVersion(packageId, version);
			try {
				packageVersionHelper.archiveVersion(packageRN, oldVersion, result.getOldPackageXml());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		Package packageVO = factory.convertTo(Package.class, packageRN);
		PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
		packageVO.setPackageVersion(packageVersionVO);
		return new ServiceResponse<Package>(packageVO, "activate package version successfully", true);
	}
}
