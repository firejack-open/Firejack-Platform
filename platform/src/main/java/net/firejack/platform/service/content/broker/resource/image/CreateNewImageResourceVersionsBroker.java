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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.ImageResource;
import net.firejack.platform.core.model.registry.resource.ImageResourceModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.service.content.broker.resource.AbstractCreateNewResourceVersionsBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;


@Component("createNewImageResourceVersionsBroker")
@TrackDetails
public class CreateNewImageResourceVersionsBroker
        extends AbstractCreateNewResourceVersionsBroker<ImageResourceModel, ImageResourceVersionModel, ImageResource> {

    @Autowired
    @Qualifier("imageResourceStore")
    private IResourceStore<ImageResourceModel> imageResourceStore;

    @Autowired
    @Qualifier("imageResourceVersionStore")
    private IResourceVersionStore<ImageResourceVersionModel> imageResourceVersionStore;


    @Override
    public IResourceStore<ImageResourceModel> getResourceStore() {
        return imageResourceStore;
    }

    @Override
    public IResourceVersionStore<ImageResourceVersionModel> getResourceVersionStore() {
        return imageResourceVersionStore;
    }

    @Override
    public String getResourceName() {
        return "Image";
    }

    @Override
    protected void copyResourceFiles(ImageResourceModel resource,
                                     List<ImageResourceVersionModel> oldResourceVersions,
                                     List<ImageResourceVersionModel> newResourceVersions) {
        if (!newResourceVersions.isEmpty()) {
            for (int i = 0, newResourceVersionsSize = newResourceVersions.size(); i < newResourceVersionsSize; i++) {
                ImageResourceVersionModel oldResourceVersion = oldResourceVersions.get(i);
                ImageResourceVersionModel newResourceVersion = newResourceVersions.get(i);

	            String oldFilename = oldResourceVersion.getId() + "_" +
			            oldResourceVersion.getVersion() + "_" +
			            oldResourceVersion.getCulture().name();
	            String newFilename = newResourceVersion.getId() + "_" +
			            newResourceVersion.getVersion() + "_" +
			            newResourceVersion.getCulture().name();

	            InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, oldFilename, helper.getImage(), String.valueOf(resource.getId()));
	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, newFilename, stream, helper.getImage(), String.valueOf(resource.getId()));
	            IOUtils.closeQuietly(stream);            }
        }
    }
}