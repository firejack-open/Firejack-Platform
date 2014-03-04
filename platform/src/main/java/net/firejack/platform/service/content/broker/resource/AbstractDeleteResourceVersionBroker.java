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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.AbstractResource;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.model.helper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractDeleteResourceVersionBroker<R extends AbstractResourceModel, RV extends AbstractResourceVersionModel<R>, DTO extends AbstractResource>
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<DTO>> {

    @Autowired
    private FileHelper helper;

    public abstract IResourceVersionStore<RV> getResourceVersionStore();

    public abstract String getResourceVersionName();

    private RV resourceVersion;

    @Override
    protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<NamedValues> request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Long resourceId = (Long) request.getData().get("resourceId");
        Integer version = (Integer) request.getData().get("version");
        String sCulture = (String) request.getData().get("culture");
        Cultures culture = Cultures.findByName(sCulture);
        resourceVersion = getResourceVersionStore().findByResourceIdCultureAndVersion(resourceId, culture, version);
        if (resourceVersion == null) {
            ValidationMessage validationMessage = new ValidationMessage(
                    null, "resource.not.found.by.id", getResourceVersionName(), resourceId);
            validationMessages.add(validationMessage);
        }
        return validationMessages;
    }

    @Override
    public ServiceResponse<DTO> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long resourceId = (Long) request.getData().get("resourceId");
        Integer version = (Integer) request.getData().get("version");

        try {
            deleteResourceVersionFile(resourceVersion);
            Integer currentVersion = getResourceVersionStore().deleteResourceVersion(resourceVersion);

            List<RV> resourceVersionModels = getResourceVersionStore().findByResourceIdAndVersion(resourceId, version);
            if (resourceVersionModels.isEmpty()) {
                resourceVersionModels = getResourceVersionStore().findByResourceIdAndVersion(resourceId, currentVersion);
            }

            DTO resource = null;
            if (!resourceVersionModels.isEmpty()) {
                Type type = ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[2];

                RV resourceVersionModel = resourceVersionModels.get(0);
                R resourceModel = resourceVersionModel.getResource();
                resourceModel.setResourceVersion(resourceVersionModel);
                resourceModel.setSelectedVersion(resourceVersionModel.getVersion());

                resource = factory.convertTo((Class<DTO>) type, resourceModel);
            }
            return new ServiceResponse<DTO>(resource, getResourceVersionName() + " has been deleted successfully.", true);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessFunctionException("delete.failure.has.child",
                    new String[]{getResourceVersionName()}, null);
        } catch (Exception e) {
            throw new BusinessFunctionException("delete.server.error",
                    new String[]{getResourceVersionName()}, null);
        }
    }

    /**
     * @param resourceVersion
     * @throws java.io.IOException
     */
    public void deleteResourceVersionFile(RV resourceVersion) throws IOException {
        String  resourceVersionFolder = null;
        if (resourceVersion instanceof ImageResourceVersionModel) {
            resourceVersionFolder = helper.getImage();
        } else if (resourceVersion instanceof AudioResourceVersionModel) {
            resourceVersionFolder = helper.getAudio();
        } else if (resourceVersion instanceof VideoResourceVersionModel) {
            resourceVersionFolder = helper.getVideo();
        }

	    if (resourceVersionFolder != null) {
		    String audioResourceVersionFilename = resourceVersion.getId() + "_" +
				    resourceVersion.getVersion() + "_" +
				    resourceVersion.getCulture().name();
		    OPFEngine.FileStoreService.deleteFile(OpenFlame.FILESTORE_CONTENT, audioResourceVersionFilename,resourceVersionFolder, String.valueOf(resourceVersion.getResource().getId()));
	    }
    }
}
