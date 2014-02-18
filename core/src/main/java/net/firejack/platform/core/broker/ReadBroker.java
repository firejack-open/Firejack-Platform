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
