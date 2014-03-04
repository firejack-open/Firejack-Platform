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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;


public abstract class AbstractDeleteResourceByLookupBroker<R extends AbstractResourceModel>
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse> {

    @Override
    protected ServiceResponse perform(ServiceRequest<SimpleIdentifier<String>> request)
            throws BusinessFunctionException {
        String lookup = request.getData().getIdentifier();
        ServiceResponse response;
        if (lookup == null) {
            response = new ServiceResponse("Id parameter is blank.", false);
        } else {
            try {
                delete(lookup);
                response = new ServiceResponse(getSuccessMessage(), true);
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
                response = new ServiceResponse(th.getMessage(), false);
            }
        }

        return response;
    }

    protected void delete(String lookup) {
        R model = getStore().findByLookup(lookup);
        if (model != null) {
            getStore().delete(model);
        }
    }

    protected String getSuccessMessage() {
        return "Element was deleted successfully.";
    }

    protected abstract IResourceStore<R> getStore();

}
