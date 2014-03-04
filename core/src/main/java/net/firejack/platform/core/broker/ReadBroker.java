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


import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.IStore;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ReadBroker<M extends BaseEntityModel, DTO extends BaseEntity>
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<DTO>> {

    @Override
    protected ServiceResponse<DTO> perform(ServiceRequest<SimpleIdentifier<Long>> request)
		    throws Exception {
        Long id = request.getData().getIdentifier();
        ServiceResponse<DTO> response;
        if (id == null) {
            response = new ServiceResponse<DTO>("Id parameter should not be blank.", false);
        } else {
            try {
                M entity = getEntity(id);
                if (entity == null) {
                    response = new ServiceResponse<DTO>("No element found for specified id parameter.", false);
                } else {
                    DTO model = convertToModel(entity);
                    response = new ServiceResponse<DTO>(model, model.getClass().getSimpleName() + " has been found by id: " + id, true);
                }
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
                response = new ServiceResponse<DTO>("Failed to retrieve element. Reason: " + th.getMessage(), false);
            }
        }
        return response;
    }

    protected DTO convertToModel(M entity) {
        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[1];
        return (DTO) factory.convertTo((Class) type, entity);
    }

    protected M getEntity(Long id) {
        return getStore().findById(id);
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    protected abstract IStore<M, Long> getStore();

}
