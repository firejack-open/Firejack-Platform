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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.ActionStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component
public class GenerateDocumentationExampleBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse> {

	@Autowired
	private ActionStore actionStore;
	@Autowired
	private ICollectionStore collectionStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
		List<ActionModel> models = actionStore.findByLookupForExample(request.getData().getIdentifier());
		for (ActionModel action : models) {
			CollectionModel collection = collectionStore.findOrCreateCollection(action);

			actionStore.getHtmlResourceStore().createRestExample(action, collection);
			actionStore.getHtmlResourceStore().createSoapExample(action, collection);
		}

		return new ServiceResponse("Generate example successfully", true);
	}
}
