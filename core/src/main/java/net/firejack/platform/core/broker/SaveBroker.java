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
