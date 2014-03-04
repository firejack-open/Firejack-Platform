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

package net.firejack.platform.service.filestore.broker.file;

import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

/** Class encapsulates reading the filestore functionality */
@Component
@TrackDetails
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReadFileStoreBroker extends ServiceBroker<ServiceRequest, ServiceResponse<FileStoreInfo>> {

	/**
	 * Reads the filestore
	 *
	 *
	 * @param request - the message passed to the business function with all data required
	 *
	 * @return information about the operation success and filestore value object
	 *
	 * @throws net.firejack.platform.core.exception.BusinessFunctionException
	 *
	 */
	@Override
	protected ServiceResponse<FileStoreInfo> perform(ServiceRequest request) throws Exception {
		File filestore = FileUtils.getTempDirectory();
		return new ServiceResponse<FileStoreInfo>(new FileStoreInfo(filestore.getUsableSpace(), filestore.getTotalSpace(), filestore.getTotalSpace() - filestore.getUsableSpace()), "Filestore info read successfully.", true);

	}
}