package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.ActionServiceLink;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.content.broker.documentation.service.DocumentationService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component("ReadEntityServiceLinksBroker")
public class ReadEntityServiceLinksBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<ActionServiceLink>> {

	@Autowired
	private DocumentationService documentationService;

	@Override
	protected ServiceResponse<ActionServiceLink> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long actionId = request.getData().getIdentifier();
		List<ActionServiceLink> links = documentationService.findLinks(actionId);
		return new ServiceResponse<ActionServiceLink>(links, "Load links successfully.", true);
	}
}
