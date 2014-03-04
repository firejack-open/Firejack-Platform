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
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.translate.ITranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component("supportPackageVersionBroker")
public class SupportPackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	public static final String PARAM_PACKAGE_ID = "packageId";
	public static final String PARAM_VERSION = "version";

	private static final String MSG_STATUS_SUCCESSFUL =
			"Support for the version has applied successfully.";
	private static final String MSG_STATUS_FAILURE =
			"Failed to apply support for the version.";

	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private FileHelper helper;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get(PARAM_PACKAGE_ID);
		Integer version = (Integer) request.getData().get(PARAM_VERSION);
		ServiceResponse response;
		InputStream stream=null;
		try {
			PackageModel pkg = packageStore.findById(packageId);

			stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,pkg.getName() + PackageFileType.PACKAGE_XML.getDotExtension(), helper.getVersion(), packageId.toString(), version.toString());
			ITranslationResult<Boolean> result = packageInstallationService.supportVersion(stream);
			if (result.getResult() != null && result.getResult()) {
				response = new ServiceResponse(MSG_STATUS_SUCCESSFUL, true);
			} else {
				response = new ServiceResponse(MSG_STATUS_FAILURE, false);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			response = new ServiceResponse(e.getMessage(), false);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return response;
	}
}
