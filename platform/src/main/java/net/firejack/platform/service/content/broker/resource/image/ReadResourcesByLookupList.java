/*
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