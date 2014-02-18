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
