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

package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.ResourceContent;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.HtmlResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("UpdateResourceDescriptionBroker")
public class UpdateResourceDescriptionBroker extends ServiceBroker<ServiceRequest<ResourceContent>, ServiceResponse<ResourceContent>> {

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

	@Override
	protected ServiceResponse<ResourceContent> perform(ServiceRequest<ResourceContent> request) throws Exception {
		ResourceContent vo = request.getData();
		AbstractResourceVersionModel resourceVersion = resourceVersionStore.findById(vo.getResourceVersionId());
		if (resourceVersion instanceof TextResourceVersionModel) {
			((TextResourceVersionModel) resourceVersion).setText(vo.getValue());
		} else if (resourceVersion instanceof HtmlResourceVersionModel) {
			((HtmlResourceVersionModel) resourceVersion).setHtml(vo.getValue());
		}
		resourceVersionStore.saveOrUpdate(resourceVersion);
		return new ServiceResponse<ResourceContent>(vo, "Documentation  has been updated successfully.", true);
	}
}
