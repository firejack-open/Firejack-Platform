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
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class ListBroker<M extends BaseEntityModel, RSDTO extends AbstractDTO, RQDTO extends AbstractDTO>
        extends ServiceBroker<ServiceRequest<RQDTO>, ServiceResponse<RSDTO>> {

    private SpecifiedIdsFilter filter;

    /**
     * @return
     */
    public SpecifiedIdsFilter getFilter() {
        SpecifiedIdsFilter filter = getFilter(true);
        if (filter.getAll() == null) {
            filter.setAll(true);
        }
        return filter;
    }

    /**
     * @param isInitialize
     * @return
     */
    public SpecifiedIdsFilter getFilter(boolean isInitialize) {
        if (this.filter == null || isInitialize) {
            initFilter();
        }
        return this.filter;
    }

    private void initFilter() {
        this.filter = new SpecifiedIdsFilter();
    }


    @Override
    protected ServiceResponse<RSDTO> perform(ServiceRequest<RQDTO> request)
		    throws Exception {

        ServiceResponse<RSDTO> response;
	    try {
		    Integer total = getTotal(request);
		    List<RSDTO> dtoList = null;
		    if (total == null || total > 0) {
			    List<M> modelList = getModelList(request);
			    dtoList = convertToDTOs(modelList);
			    dtoList = result(dtoList, modelList);
			    if (total == null) {
				    total = modelList == null ? 0 : modelList.size();
			    }
		    }
	        response = new ServiceResponse<RSDTO>(dtoList, getSuccessMessage(), true, total);
        } catch (BusinessFunctionException e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<RSDTO>(
                    e.getMessage() == null ? "Operation failed. See server logs for details." : e.getMessage(), false);
        }
        return response;
    }

    protected String getSuccessMessage() {
        return "List has been found.";
    }

    protected List<RSDTO> convertToDTOs(List<M> entities) {
        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[1];
        return factory.convertTo((Class) type, entities);
    }

	protected Integer getTotal(ServiceRequest<RQDTO> request) throws BusinessFunctionException{
		return null;
	}

	protected List<RSDTO> result(List<RSDTO> dtoList, List<M> modelList){
		return dtoList;
	}

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    protected abstract List<M> getModelList(ServiceRequest<RQDTO> request) throws BusinessFunctionException;
}