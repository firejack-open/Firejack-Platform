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

import net.firejack.platform.api.content.domain.Resource;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("readResourcesByParentIdBroker") //TODO need to change
@TrackDetails
public class ReadResourcesByParentIdBroker
//        extends FilteredListBroker<ResourceModel, Resource, NamedValues> {
        extends ListBroker<ResourceModel, Resource, NamedValues> {

    private RegistryNodeModel registryNode;

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<ResourceModel> resourceStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    SpecifiedIdsFilter<Long> filter;

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get("registryNodeId");

        filter = getFilter();
        List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
        List<Long> exceptResourceIds = ListUtils.removeNullableItems(exceptIds);
        filter.getUnnecessaryIds().addAll(exceptResourceIds);

        Integer total = 0;
        registryNode = registryNodeStore.findById(registryNodeId);
        if (registryNode != null) {
            total = resourceStore.findCountByLikeLookupWithFilter(registryNode.getLookup(), filter);
        }
        return total;
    }

    protected List<ResourceModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Paging paging = (Paging) request.getData().get("paging");
        return resourceStore.findAllByLikeLookupWithFilter(registryNode.getLookup(), filter, paging);
    }

}