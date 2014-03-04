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
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component("downloadGeneratedPackageBroker")
public class DownloadGeneratedPackageBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileInfo>> {
	@Autowired
	private FileHelper helper;

	@Autowired
	private IPackageStore store;

	@Override
	protected ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues> message) throws Exception {
		Long packageId = (Long) message.getData().get("packageId");
		String packageFilename = (String) message.getData().get("packageFilename");

		PackageModel packageRN = store.findById(packageId);
		ServiceResponse<FileInfo> response = new ServiceResponse<FileInfo>();
		InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageFilename,helper.getVersion(), String.valueOf(packageId), String.valueOf(packageRN.getVersion()));

		FileInfo fileInfo = new FileInfo();
		fileInfo.setStream(stream);
		fileInfo.setFilename(packageFilename);
		response.addItem(fileInfo);
		response.setSuccess(true);

		return response;
	}
}
