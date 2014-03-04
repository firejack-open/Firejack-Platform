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
