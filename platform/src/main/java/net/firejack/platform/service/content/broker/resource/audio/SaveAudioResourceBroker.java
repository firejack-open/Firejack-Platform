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

package net.firejack.platform.service.content.broker.resource.audio;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.AudioResource;
import net.firejack.platform.core.model.registry.resource.AudioResourceModel;
import net.firejack.platform.core.model.registry.resource.AudioResourceVersionModel;
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


@Component("saveAudioResourceBroker")
@TrackDetails
public class SaveAudioResourceBroker
        extends AbstractSaveResourceBroker<AudioResourceModel, AudioResource> {

    @Autowired
    @Qualifier("audioResourceStore")
    private IResourceStore<AudioResourceModel> store;

    @Autowired
    @Qualifier("audioResourceVersionStore")
    private IResourceVersionStore<AudioResourceVersionModel> resourceVersionStore;

    @Override
    protected IResourceStore<AudioResourceModel> getResourceStore() {
        return store;
    }


    @Override
    protected String getSuccessMessage(boolean isNew) {
        return "Audio resource has " + (isNew ? "created" : "updated") +  " successfully";
    }

    @Override
    protected void save(AudioResourceModel audioResourceModel) throws Exception {
        String temporaryUploadedFileName = audioResourceModel.getResourceVersion().getTemporaryFilename();

	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadedFileName, helper.getTemp());
	    if (stream!=null) {
            super.save(audioResourceModel);
            try {
                AudioResourceVersionModel audioResourceVersion = audioResourceModel.getResourceVersion();
                String audioResourceVersionFilename = audioResourceVersion.getId() + "_" +
                                                      audioResourceVersion.getVersion() + "_" +
                                                      audioResourceVersion.getCulture().name();
	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,audioResourceVersionFilename,stream,helper.getAudio(), String.valueOf(audioResourceModel.getId()));

                resourceVersionStore.saveOrUpdate(audioResourceVersion);
                audioResourceVersion.setTemporaryFilename(null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
	            IOUtils.closeQuietly(stream);
            }
        } else {
            super.save(audioResourceModel);
        }
    }

}