/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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