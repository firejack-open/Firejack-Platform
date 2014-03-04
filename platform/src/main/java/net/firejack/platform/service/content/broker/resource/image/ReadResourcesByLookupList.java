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

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.api.content.domain.ImageResource;
import net.firejack.platform.api.content.domain.TextResource;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceModel;
import net.firejack.platform.core.model.registry.resource.TextResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@TrackDetails
public class ReadResourcesByLookupList extends ServiceBroker<ServiceRequest<ListLookup>, ServiceResponse<AbstractResource>> {

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

	@Override
	protected ServiceResponse<AbstractResource> perform(ServiceRequest<ListLookup> request) throws Exception {
		List<String> lookup = request.getData().getLookup();

		List<AbstractResourceVersionModel> models = resourceVersionStore.readResourcesByLookupList(lookup);

		List<AbstractResourceModel>  texts = new ArrayList<AbstractResourceModel>();
		List<AbstractResourceModel> images = new ArrayList<AbstractResourceModel>();

		for (AbstractResourceVersionModel model : models) {
			AbstractResourceModel resource = model.getResource();
			resource.setResourceVersion(model);
			resource.setSelectedVersion(model.getVersion());

			if (resource instanceof  TextResourceModel) {
				texts.add(resource);
			}else if (resource instanceof ImageResourceModel) {
				images.add(resource);
			}
		}
		List<TextResource> textResources = factory.convertTo(TextResource.class, texts);
		List<ImageResource> imageResources = factory.convertTo(ImageResource.class, images);

		ArrayList<AbstractResource> resources = new ArrayList<AbstractResource>();
		resources.addAll(textResources);
		resources.addAll(imageResources);
		return new ServiceResponse<AbstractResource>(resources, "Resource has been found successfully.", true);
	}
}