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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.model.helper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractCreateNewResourceVersionsBroker<R extends AbstractResourceModel, RV extends AbstractResourceVersionModel<R>, DTO extends AbstractResource>
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<DTO>> {

    @Autowired
    protected FileHelper helper;

    private R resource;

    public abstract IResourceStore<R> getResourceStore();

    public abstract IResourceVersionStore<RV> getResourceVersionStore();

    public abstract String getResourceName();

    @Override
    protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<NamedValues> request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Long resourceId = (Long) request.getData().get("resourceId");
        resource = getResourceStore().findById(resourceId);
        if (resource == null) {
            ValidationMessage validationMessage = new ValidationMessage(
                    null, "resource.not.found.by.id", getResourceName(), resourceId);
            validationMessages.add(validationMessage);
        }
        Integer version = (Integer) request.getData().get("version");
        if (resource != null && version != null && resource.getLastVersion() < version) {
            ValidationMessage validationMessage = new ValidationMessage(
                    null, "resource.not.found.by.id.and.version", getResourceName(), resourceId, version);
            validationMessages.add(validationMessage);
        }
        return validationMessages;
    }

    @Override
    public ServiceResponse<DTO> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long resourceId = (Long) request.getData().get("resourceId");
        Integer version = (Integer) request.getData().get("version");
        List<RV> newResourceVersions;
        try {
            List<RV> oldResourceVersions;
            if (version != null) {
                oldResourceVersions = getResourceVersionStore().findByResourceIdAndVersion(this.resource.getId(), version);
            } else {
                oldResourceVersions = getResourceVersionStore().findLastVersionsByResourceId(this.resource.getId());
            }
            newResourceVersions = getResourceVersionStore().createNewResourceVersions(this.resource, oldResourceVersions);
            copyResourceFiles(this.resource, oldResourceVersions, newResourceVersions);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessFunctionException("resource.not.create.new.resource.versions",
                    new Object[]{getResourceName(), resourceId}, null);
        }

        RV defaultCultureResourceVersion = null;
        for (RV htmlResourceVersion : newResourceVersions) {
            if (Cultures.getDefault().equals(htmlResourceVersion.getCulture())) {
                defaultCultureResourceVersion = htmlResourceVersion;
                break;
            }
        }
        if (defaultCultureResourceVersion == null) {
            defaultCultureResourceVersion = newResourceVersions.get(0);
        }

        if (defaultCultureResourceVersion == null) {
            throw new BusinessFunctionException("resource.not.create.new.resource.versions",
                    new Object[]{getResourceName(), resourceId}, null);
        }

        R resourceModel = defaultCultureResourceVersion.getResource();
        resourceModel.setResourceVersion(defaultCultureResourceVersion);
        resourceModel.setSelectedVersion(defaultCultureResourceVersion.getVersion());

        Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[2];
        DTO resource = factory.convertTo((Class<DTO>) type, resourceModel);

        return new ServiceResponse<DTO>(resource, "Created new resource versions.", true);
    }

    protected void copyResourceFiles(R resource, List<RV> oldResourceVersions, List<RV> newResourceVersions) {

    }

}
