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

package net.firejack.platform.service.content.broker.collection;

import net.firejack.platform.api.content.domain.Collection;
import net.firejack.platform.core.broker.FilteredListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readCollectionsByLikeLookupBroker")
public class ReadCollectionsByLikeLookupBroker
        extends FilteredListBroker<CollectionModel, Collection, SimpleIdentifier<String>> {

    @Autowired
    private ICollectionStore collectionStore;

    @Override
    protected ILookupStore<CollectionModel, Long> getStore() {
        return collectionStore;
    }

    @Override
    protected List<CollectionModel> getModelList(ServiceRequest<SimpleIdentifier<String>> request) throws BusinessFunctionException {
        String lookup = request.getData().getIdentifier();
        return collectionStore.findAllByLikeLookupWithFilter(lookup, getFilter());
    }

}
