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

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


@Component
public abstract class AbstractReadResourceByLookupBroker<R extends AbstractResourceModel, RV extends AbstractResourceVersionModel<R>, DTO extends AbstractResource>
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<DTO>> {

    /**
     * @return
     */
    public abstract IResourceVersionStore<RV> getResourceVersionStore();

    /**
     * @return
     */
    public abstract IResourceStore<R> getResourceStore();


    @Override
    public ServiceResponse<DTO> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String lookup = request.getData().getIdentifier();
        ServiceResponse<DTO> response;
        R resourceModel = getResourceStore().findByLookup(lookup);
        if (resourceModel != null) {
            RV resourceVersionModel = getResourceVersionStore().findLastVersionByResourceId(resourceModel.getId());

            resourceModel = resourceVersionModel.getResource();
            resourceModel.setResourceVersion(resourceVersionModel);
            resourceModel.setSelectedVersion(resourceVersionModel.getVersion());

            Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[2];
            DTO resource = factory.convertTo((Class<DTO>) type, resourceModel);

            response = new ServiceResponse<DTO>(resource, "Resource has been found successfully.", true);
        } else {
            response = new ServiceResponse<DTO>("Resource has not been found by lookup: " + lookup, false);
        }
        return response;
    }

}