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
