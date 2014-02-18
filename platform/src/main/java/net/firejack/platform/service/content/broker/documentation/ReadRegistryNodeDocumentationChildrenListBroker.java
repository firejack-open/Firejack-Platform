package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Component("ReadRegistryNodeDocumentationChildrenListBroker")
public class ReadRegistryNodeDocumentationChildrenListBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<Lookup>> {

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected ServiceResponse<Lookup> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long registryNodeId = (Long) request.getData().getIdentifier();
        if (registryNodeId == 0) {
            registryNodeId = null;
        }
	    List<RegistryNodeModel> registryNodes = registryNodeStore.findChildrenByParentIdAndTypes(registryNodeId, null,
			    RootDomainModel.class,
			    DomainModel.class,
			    SystemModel.class,
			    PackageModel.class,
			    EntityModel.class,
			    ActionModel.class);

	    List<Lookup> registryNodeVOs = factory.convertTo(Lookup.class, registryNodes);
		return new ServiceResponse<Lookup>(registryNodeVOs, "Load successfully.", true);
	}
}
