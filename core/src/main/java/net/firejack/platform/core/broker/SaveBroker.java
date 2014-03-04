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


import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.exception.LookupNotUniqueException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;

public abstract class SaveBroker<E extends BaseEntityModel, RQDTO extends AbstractDTO, RSDTO extends AbstractDTO>
        extends ServiceBroker<ServiceRequest<RQDTO>, ServiceResponse<RSDTO>> {

    @Override
    protected ServiceResponse<RSDTO> perform(ServiceRequest<RQDTO> request) throws Exception {
        RQDTO inputObject = request.getData();
        ServiceResponse<RSDTO> response;
        if (inputObject == null) {
            response = new ServiceResponse<RSDTO>("Specified entity is null", false);
        } else {
            E entity = null;
            try {
                entity = convertToEntity(inputObject);
                validate(inputObject, entity);
                boolean isNew = entity.getId() == null;
                save(entity);
                RSDTO savedObject = convertToModel(entity);
                response = new ServiceResponse<RSDTO>(savedObject, getSuccessMessage(isNew), true);
            } catch (LookupNotUniqueException e) {
                logger.warn(e.getMessage(), e);
                throw new BusinessFunctionException("save.failure.lookup.not.unique", new String[]{e.getType(), e.getLookup()}, null);
            } catch (BusinessFunctionException e) {
                logger.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                String simpleName = "";
                if (entity != null) {
                    simpleName = entity.getClass().getSimpleName();
                }
                throw new BusinessFunctionException("save.server.error", new String[]{simpleName}, null);
            }

        }
        return response;
    }

    protected void validate(RQDTO inputObject, E entity) {
    }

    protected String getSuccessMessage(boolean isNew) {
        return "Element was saved successfully.";
    }

    protected abstract E convertToEntity(RQDTO dto);

    protected abstract RSDTO convertToModel(E model);

    protected abstract void save(E entity) throws Exception;

}
