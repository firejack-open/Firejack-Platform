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

package net.firejack.platform.service.registry.broker.package_.version;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.installer.RequestTaskService;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.generate.service.IResourceGeneratorService;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@TrackDetails
@Component("uploadPackageVersionFileBroker")
public class UploadPackageVersionFileBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<PackageVersion>> {
	@Autowired
	private FileHelper helper;
	@Autowired
	private IPackageStore store;
	@Autowired
	private IResourceGeneratorService resourceGeneratorService;
	@Autowired
	private PackageVersionHelper packageVersionHelper;
	@Autowired
	private RequestTaskService requestTaskService;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<NamedValues> request) throws Exception {
		String type = (String) request.getData().get("fileType");
		Long packageId = (Long) request.getData().get("packageId");
		InputStream inputStream = (InputStream) request.getData().get("inputStream");

		PackageModel packageRN = store.findById(packageId);
		if (packageRN == null) {
			throw new BusinessFunctionException(null, "Could not found package by id:[" + packageId + "]");
		}
		PackageFileType fileType = PackageFileType.findByExtension(type);
		if (fileType == null || !PackageFileType.APP_WAR.equals(fileType)) {
			throw new BusinessFunctionException(null, "Should be uploaded only application war file.");
		}

		PackageVersion packageVersionVO;
		try {
			String fileName = packageRN.getName() + fileType.getDotExtension();
			OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,fileName,inputStream,helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
			resourceGeneratorService.generateOFR(packageRN);
			packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
		} catch (IOException e) {
			throw new BusinessFunctionException();
		}

		if (fileType == PackageFileType.APP_WAR) {
            String name = packageRN.getUrlPath();
            if (packageRN.getUrlPath() == null || packageRN.getUrlPath().isEmpty()) name = "ROOT";
            name = name + PackageFileType.APP_WAR.getDotExtension();
			String file = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();

			requestTaskService.undeploy(packageRN.getId(), name);
			requestTaskService.deploy(packageRN.getId(), name, file);
		}
		return new ServiceResponse<PackageVersion>(packageVersionVO, "File has uploaded successfully.", true);
	}
}
