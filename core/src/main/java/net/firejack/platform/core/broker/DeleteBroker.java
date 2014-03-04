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

package net.firejack.platform.core.broker;


import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.IStore;

public abstract class DeleteBroker<E extends BaseEntityModel> extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse> {

    @Override
    protected ServiceResponse perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        Long id = request.getData().getIdentifier();
        ServiceResponse response;
        if (id == null) {
            response = new ServiceResponse("Id parameter is blank.", false);
        } else {
            try {
                delete(id);
                response = new ServiceResponse(getSuccessMessage(), true);
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
                response = new ServiceResponse(th.getMessage(), false);
            }
        }
        return response;
    }

    protected void delete(Long id) {
        getStore().deleteById(id);
    }

    protected String getSuccessMessage() {
        return "Element was deleted successfully.";
    }

    protected abstract IStore<E, Long> getStore();

}
