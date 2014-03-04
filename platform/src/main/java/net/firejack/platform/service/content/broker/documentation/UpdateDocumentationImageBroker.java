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

package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.ImageResourceVersion;
import net.firejack.platform.api.content.domain.ResourceContent;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ResourceFileUtil;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("UpdateDocumentationImageBroker")
public class UpdateDocumentationImageBroker extends ServiceBroker<ServiceRequest<ImageResourceVersion>, ServiceResponse<ResourceContent>> {

	@Autowired
	@Qualifier("resourceFileUtil")
	private ResourceFileUtil imageFileUtil;

	@Autowired
	@Qualifier("imageResourceVersionStore")
	private IResourceVersionStore<ImageResourceVersionModel> resourceVersionStore;

	@Override
	protected ServiceResponse<ResourceContent> perform(ServiceRequest<ImageResourceVersion> request) throws Exception {
		ImageResourceVersion vo = request.getData();
		ImageResourceVersionModel imageResourceVersion = resourceVersionStore.findById(vo.getId());

		String temporaryUploadedFileName = vo.getResourceFileTemporaryName();

		try {
			imageFileUtil.processTempFile(temporaryUploadedFileName, imageResourceVersion);
			imageResourceVersion.setOriginalFilename(vo.getResourceFileOriginalName());
			resourceVersionStore.saveOrUpdate(imageResourceVersion);
			return new ServiceResponse<ResourceContent>("Image updated successfully", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ServiceResponse<ResourceContent>("Error updating image!", false);
		}
	}
}
