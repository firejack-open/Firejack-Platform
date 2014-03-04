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

package net.firejack.platform.service.content.broker.resource.image;

import net.firejack.platform.api.content.domain.ImageResourceVersion;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.ResourceFileUtil;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("updateImageResourceVersionBroker")
@TrackDetails
public class UpdateImageResourceVersionBroker
        extends ServiceBroker<ServiceRequest<ImageResourceVersion>, ServiceResponse<ImageResourceVersion>> {

    @Autowired
    @Qualifier("resourceFileUtil")
    private ResourceFileUtil resourceFileUtil;

    @Autowired
    @Qualifier("imageResourceVersionStore")
    private IResourceVersionStore<ImageResourceVersionModel> imageResourceVersionStore;

    @Override
    protected ServiceResponse<ImageResourceVersion> perform(ServiceRequest<ImageResourceVersion> request) throws Exception {
        Long resourceId = request.getData().getResourceId();
        String resourceLookup = request.getData().getResourceLookup();
        Integer version = request.getData().getVersion();
        Cultures culture = request.getData().getCulture();
        String temporaryUploadedFileName = request.getData().getResourceFileTemporaryName();
        String resourceFileOriginalName = request.getData().getResourceFileOriginalName();
        
        try {
            ImageResourceVersionModel imageResourceVersionModel = null;
            if (resourceId != null) {
                imageResourceVersionModel = imageResourceVersionStore.findByResourceIdCultureAndVersion(resourceId, culture, version);
            } else if (resourceLookup != null) {
                imageResourceVersionModel = imageResourceVersionStore.findLastVersionByLookup(resourceLookup);
            }
            if (imageResourceVersionModel == null) {
                imageResourceVersionModel = imageResourceVersionStore.createNewResourceVersion(resourceId, version, culture);
            }

            resourceFileUtil.processTempFile(temporaryUploadedFileName, imageResourceVersionModel);
            imageResourceVersionModel.setOriginalFilename(resourceFileOriginalName);

            imageResourceVersionStore.saveOrUpdate(imageResourceVersionModel);

            ImageResourceVersion imageResourceVersion = factory.convertTo(ImageResourceVersion.class, imageResourceVersionModel);
            return new ServiceResponse<ImageResourceVersion>(imageResourceVersion, "Image resource saved successfully.", true);
        } catch (Exception e) {
            logger.error("Error saving image resource", e);
            return new ServiceResponse("Error saving image resource!", false);
        }
    }
}
