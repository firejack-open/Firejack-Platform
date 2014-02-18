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
