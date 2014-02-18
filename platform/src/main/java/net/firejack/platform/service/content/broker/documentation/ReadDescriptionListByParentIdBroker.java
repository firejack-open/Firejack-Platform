package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.api.content.domain.HtmlResource;
import net.firejack.platform.api.content.domain.TextResource;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("ReadDescriptionListByParentIdBroker")
public class ReadDescriptionListByParentIdBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<AbstractResource>> {

	@Autowired
	@Qualifier("resourceStore")
	private IResourceStore<ResourceModel> resourceStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel<ResourceModel>> resourceVersionStore;

	@Override
	protected ServiceResponse<AbstractResource> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long registryNodeId = (Long) request.getData().get("id");
		String country = (String) request.getData().get("country");
		Cultures culture = Cultures.findByCountry(country);

		List<AbstractResource> resourceVOs = new ArrayList<AbstractResource>();

		List<ResourceModel> resources = resourceStore.findChildrenByParentId(registryNodeId, null);
		for (ResourceModel resource : resources) {
			AbstractResourceVersionModel<ResourceModel> resourceVersion = resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), culture);
			resource.setResourceVersion(resourceVersion);
			if (resource.getType() == RegistryNodeType.HTML_RESOURCE) {
				resourceVOs.add(factory.convertTo(HtmlResource.class, resource));
			} else if (resource.getType() == RegistryNodeType.TEXT_RESOURCE) {
				resourceVOs.add(factory.convertTo(TextResource.class, resource));
			}
		}

		return new ServiceResponse<AbstractResource>(resourceVOs, "Load successfully.", true);
	}
}
