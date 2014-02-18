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

package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readRegistryNodeChildrenExpandedByLookupBroker")
public class ReadRegistryNodeChildrenExpandedByLookupBroker
		extends ReadRegistryNodeChildrenExpandedByIdBroker {

    @Override
	protected List<RegistryNodeModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		String lookup = (String) request.getData().get("registryNodeLookup");
		PageType pageType = (PageType) request.getData().get("pageType");

        List<RegistryNodeModel> registryNodesWithChildren = null;
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(lookup);
        if (registryNode != null) {
            registryNodesWithChildren = getModelListById(registryNode.getId(), pageType);
        }
        return registryNodesWithChildren;
	}

}
