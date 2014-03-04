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

package net.firejack.platform.service.content.broker.resource.video;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.resource.VideoResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component("getVideoResourceVersionFileBroker")
@TrackDetails
public class GetVideoResourceVersionFileBroker
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileInfo>> {

	@Autowired
	private FileHelper helper;

    @Autowired
    @Qualifier("videoResourceVersionStore")
    private IResourceVersionStore<VideoResourceVersionModel> resourceVersionStore;

    @Override
    public ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long resourceId = (Long) request.getData().get("resourceId");
        String videoFilename = (String) request.getData().get("videoFilename");
        ServiceResponse<FileInfo> response = new ServiceResponse<FileInfo>();


	    FileInfo fileInfo = new FileInfo();
	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, videoFilename, helper.getVideo(), String.valueOf(resourceId));
	    fileInfo.setStream(stream);
	    setOriginalFilename(videoFilename, fileInfo);
	    response.addItem(fileInfo);
	    response.setSuccess(true);
	    return response;
    }

    private void setOriginalFilename(String videoFilename, FileInfo fileInfo) {
        String sId = videoFilename.substring(0, videoFilename.indexOf("_"));
        if (StringUtils.isNumeric(sId)) {
            Long resourceVersionId = Long.parseLong(sId);
            VideoResourceVersionModel resourceVersion = resourceVersionStore.findById(resourceVersionId);
            if (resourceVersion != null) {
                fileInfo.setOrgFilename(resourceVersion.getOriginalFilename());
            }
        }
    }

}
