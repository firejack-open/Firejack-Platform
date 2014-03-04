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
import net.firejack.platform.api.content.domain.VideoResource;
import net.firejack.platform.core.model.registry.resource.VideoResourceModel;
import net.firejack.platform.core.model.registry.resource.VideoResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.service.content.broker.resource.AbstractSaveResourceBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component("saveVideoResourceBroker")
@TrackDetails
public class SaveVideoResourceBroker
        extends AbstractSaveResourceBroker<VideoResourceModel, VideoResource> {

    @Autowired
    @Qualifier("videoResourceStore")
    private IResourceStore<VideoResourceModel> store;

    @Autowired
    @Qualifier("videoResourceVersionStore")
    private IResourceVersionStore<VideoResourceVersionModel> resourceVersionStore;

    @Override
    protected IResourceStore<VideoResourceModel> getResourceStore() {
        return store;
    }


    @Override
    protected String getSuccessMessage(boolean isNew) {
        return "Video resource has " + (isNew ? "created" : "updated") +  " successfully";
    }

    @Override
    protected void save(VideoResourceModel videoResourceModel) throws Exception {
        String temporaryUploadedFileName = videoResourceModel.getResourceVersion().getTemporaryFilename();

	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadedFileName, helper.getTemp());

	    if (stream != null) {
		    super.save(videoResourceModel);
		    try {
			    VideoResourceVersionModel videoResourceVersion = videoResourceModel.getResourceVersion();
			    String videoResourceVersionFilename = videoResourceVersion.getId() + "_" +
					    videoResourceVersion.getVersion() + "_" +
					    videoResourceVersion.getCulture().name();
			    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,videoResourceVersionFilename,stream,helper.getVideo(), String.valueOf(videoResourceModel.getId()));
			    IOUtils.closeQuietly(stream);

			    resourceVersionStore.saveOrUpdate(videoResourceVersion);
			    videoResourceVersion.setTemporaryFilename(null);
		    } catch (Exception e) {
			    logger.error(e.getMessage(), e);
		    }
	    } else {
		    super.save(videoResourceModel);
	    }
    }

}