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
import net.firejack.platform.core.utils.ImageUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.service.content.broker.resource.AbstractSaveResourceBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


@Component("saveImageResourceBroker")
@TrackDetails
public class SaveImageResourceBroker
        extends AbstractSaveResourceBroker<ImageResourceModel, ImageResource> {

    @Autowired
    @Qualifier("imageResourceStore")
    private IResourceStore<ImageResourceModel> store;

    @Autowired
    @Qualifier("imageResourceVersionStore")
    private IResourceVersionStore<ImageResourceVersionModel> resourceVersionStore;

    @Override
    protected IResourceStore<ImageResourceModel> getResourceStore() {
        return store;
    }


    @Override
    protected String getSuccessMessage(boolean isNew) {
        return "Image resource has " + (isNew ? "created" : "updated") +  " successfully";
    }

    @Override
    protected void save(ImageResourceModel imageResourceModel) throws Exception {

        String temporaryUploadedFileName = imageResourceModel.getResourceVersion().getTemporaryFilename();
	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadedFileName, helper.getTemp());

	    if (stream != null) {
		    byte[] bytes = IOUtils.toByteArray(stream);
		    IOUtils.closeQuietly(stream);

		    super.save(imageResourceModel);
            try {
                ImageResourceVersionModel imageResourceVersion = imageResourceModel.getResourceVersion();
                String imageResourceVersionFilename = imageResourceVersion.getId() + "_" +
                                                      imageResourceVersion.getVersion() + "_" +
                                                      imageResourceVersion.getCulture().name();

	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,imageResourceVersionFilename,new ByteArrayInputStream(bytes), helper.getImage(), String.valueOf(imageResourceModel.getId()));

                Integer[] size = ImageUtils.getImageSize(bytes);
                imageResourceVersion.setWidth(size[0]);
                imageResourceVersion.setHeight(size[1]);

                resourceVersionStore.saveOrUpdate(imageResourceVersion);
                imageResourceVersion.setTemporaryFilename(null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            super.save(imageResourceModel);
        }
    }

}