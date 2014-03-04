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

package net.firejack.platform.service.content.broker.resource.image;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.resource.ImageResourceModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component
@TrackDetails
public class ReadResourceImageStreamBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<InputStream>>> {

	@Autowired
	private FileHelper helper;
	@Autowired
	@Qualifier("imageResourceStore")
	private IResourceStore<ImageResourceModel> resourceStore;

	@Autowired
	@Qualifier("imageResourceVersionStore")
	private IResourceVersionStore<ImageResourceVersionModel> resourceVersionStore;


	@Override
	protected ServiceResponse<SimpleIdentifier<InputStream>> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
		String lookup = request.getData().getIdentifier();
		ImageResourceModel resourceModel = resourceStore.findByLookup(lookup);
		if (resourceModel != null) {
			ImageResourceVersionModel resourceVersionModel = resourceVersionStore.findLastVersionByResourceId(resourceModel.getId());

			resourceModel = resourceVersionModel.getResource();
			resourceModel.setResourceVersion(resourceVersionModel);
			resourceModel.setSelectedVersion(resourceVersionModel.getVersion());

			String imageFilename = resourceVersionModel.getId() + "_" +
					resourceVersionModel.getVersion() + "_" +
					resourceVersionModel.getCulture().name();

			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, imageFilename, helper.getImage(), String.valueOf(resourceModel.getId()));

			return new ServiceResponse<SimpleIdentifier<InputStream>>(new SimpleIdentifier<InputStream>(stream), "Read successfully", true);
		}

		return new ServiceResponse<SimpleIdentifier<InputStream>>("Read image :" + lookup + " failed", false);
	}
}