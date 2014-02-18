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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.content.domain.Culture;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.CollectionStore;
import net.firejack.platform.core.store.registry.resource.FolderStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component("readAvailableCulturesBroker")
@TrackDetails
public class ReadAvailableCulturesBroker
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<Culture>> {

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<ResourceModel> resourceStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Override
    public ServiceResponse<Culture> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        Long registryNodeId = request.getData().getIdentifier();
        RegistryNodeModel registryNode = registryNodeStore.findById(registryNodeId);
        String collectionLookup = registryNode.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + CollectionStore.DEFAULT_COLLECTION_DOC_NAME;
        CollectionModel collection = collectionStore.findByLookup(collectionLookup);
        List<Cultures> cultures = resourceStore.findAvailableCulturesByCollectionId(collection.getId());
        List<Culture> cultureList = new ArrayList<Culture>();
        for (Cultures culture : cultures) {
            Culture cultureVO = new Culture(culture, culture.getLocale().getCountry());
            cultureList.add(cultureVO);
        }
        return new ServiceResponse<Culture>(cultureList, "Return list of available cultures.", true);
    }
    
}
