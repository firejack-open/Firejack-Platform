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

package net.firejack.platform.service.content.broker.resource.file;

import net.firejack.platform.api.content.domain.FileResourceVersion;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.FileResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ResourceFileUtil;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("updateFileResourceVersionBroker")
@TrackDetails
public class UpdateFileResourceVersionBroker
        extends ServiceBroker<ServiceRequest<FileResourceVersion>, ServiceResponse> {

    @Autowired
    @Qualifier("resourceFileUtil")
    private ResourceFileUtil resourceFileUtil;

    @Autowired
    @Qualifier("fileResourceVersionStore")
    private IResourceVersionStore<FileResourceVersionModel> fileResourceVersionStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<FileResourceVersion> request) throws Exception {
        Long resourceId = request.getData().getResourceId();
        Integer version = request.getData().getVersion();
        Cultures culture = request.getData().getCulture();
        String temporaryUploadedFileName = request.getData().getResourceFileTemporaryName();
        String resourceFileOriginalName = request.getData().getResourceFileOriginalName();
        
        try {
            FileResourceVersionModel fileResourceVersion = fileResourceVersionStore.findByResourceIdCultureAndVersion(resourceId, culture, version);
            if (fileResourceVersion == null) {
                fileResourceVersion = fileResourceVersionStore.createNewResourceVersion(resourceId, version, culture);
            }

            resourceFileUtil.processTempFile(temporaryUploadedFileName, fileResourceVersion);
            fileResourceVersion.setOriginalFilename(resourceFileOriginalName);

            fileResourceVersionStore.saveOrUpdate(fileResourceVersion);

            return new ServiceResponse("File resource saved successfully.", true);
            
        } catch (Exception e) {
            logger.error("Error saving file resource", e);
            return new ServiceResponse("Error saving file resource!", false);
        }
    }
}
