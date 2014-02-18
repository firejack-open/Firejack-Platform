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
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.MessageResolver;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;


public abstract class AbstractReadResourceByIdBroker<R extends AbstractResourceModel, RV extends AbstractResourceVersionModel<R>, DTO extends AbstractResource>
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<DTO>> {

    public abstract IResourceVersionStore<RV> getResourceVersionStore();
    
    public abstract IResourceStore<R> getResourceStore();

    @Override
    public ServiceResponse<DTO> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long id = (Long) request.getData().get("resourceId");
        String cultureName = (String) request.getData().get("culture");
        Integer version = (Integer) request.getData().get("version");

        Cultures culture = null;
        if (StringUtils.isNotBlank(cultureName)) {
            culture = Cultures.findByName(cultureName.toUpperCase());
        }
        if (culture == null) {
            culture = Cultures.AMERICAN;
        }

        RV resourceVersionModel;
        if (version != null) {
            resourceVersionModel = getResourceVersionStore().findByResourceIdCultureAndVersion(id, culture, version);
        } else {
            resourceVersionModel = getResourceVersionStore().findLastVersionByResourceIdCulture(id, culture);
            if (resourceVersionModel == null) {
                resourceVersionModel = getResourceVersionStore().findLastVersionByResourceId(id);
            }
        }

        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[2];
        String className = ((Class) type).getSimpleName();
        R resourceModel;
        String message;
        boolean successful;
        if (resourceVersionModel == null) {
            resourceModel = getResourceStore().findById(id);
            Type resourceVersionType = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[1];
            resourceVersionModel = (RV) ((Class) resourceVersionType).getDeclaredConstructor().newInstance();
            resourceVersionModel.setCulture(culture);
            resourceVersionModel.setVersion(version);
            resourceVersionModel.setResource(resourceModel);
            message = MessageResolver.messageFormatting("resource.version.not.found.by.resource.id.and.culture",
                    Locale.ENGLISH, className, version, culture.name());
            successful = false;
        } else {
            resourceModel = resourceVersionModel.getResource();
            message = MessageResolver.messageFormatting("resource.version.found.by.resource.id.and.culture",
                    Locale.ENGLISH, className, version, culture.name());
            successful = true;
        }
        resourceModel.setResourceVersion(resourceVersionModel);
        resourceModel.setSelectedVersion(resourceVersionModel.getVersion());

        DTO resource = factory.convertTo((Class<DTO>) type, resourceModel);

        return new ServiceResponse<DTO>(resource, message, successful);
    }

}