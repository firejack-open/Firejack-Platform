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