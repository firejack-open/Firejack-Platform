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

package net.firejack.platform.service.authority.broker.resourcelocation;

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IResourceLocationStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("readResourceLocationListBrokerEx")
public class ReadResourceLocationListBroker extends ListBroker<ResourceLocationModel, ResourceLocation, NamedValues<Object>> {

    public static final String PARAM_REGISTRY_NODE_ID = "registryNodeId";
    public static final String PARAM_ID_TO_EXCLUDE = "exceptIds";
    public static final String PARAM_START = "PARAM_START";
    public static final String PARAM_LIMIT = "PARAM_LIMIT";

    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore resourceLocationStore;

    private ThreadLocal<SpecifiedIdsFilter<Long>> cachedData = new ThreadLocal<SpecifiedIdsFilter<Long>>();

    @Override
    protected List<ResourceLocationModel> getModelList(ServiceRequest<NamedValues<Object>> request)
            throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        @SuppressWarnings("unchecked")
        Integer start = (Integer) request.getData().get(PARAM_START);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);
        SpecifiedIdsFilter<Long> filter = cachedData.get();
        cachedData.remove();
        List<ResourceLocationModel> resourceLocations =
                resourceLocationStore.findAllByParentIdWithFilter(registryNodeId, filter, new Paging(start, limit));
        for (ResourceLocationModel resourceLocation : resourceLocations) {
            resourceLocation.setPermissions(null);
        }
        return resourceLocations;
    }

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("parent.id", registryNodeId));

        @SuppressWarnings("unchecked")
        SpecifiedIdsFilter<Long> filter = getFilter();
        @SuppressWarnings("unchecked")
        List<Long> exceptIds = (List<Long>) request.getData().get(PARAM_ID_TO_EXCLUDE);
        List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
        filter.getUnnecessaryIds().addAll(exceptPermissionIds);

        cachedData.set(filter);
        try {
            return resourceLocationStore.count(criterions, filter);
        } catch (RuntimeException e) {
            cachedData.remove();
            throw e;
        }
    }
}