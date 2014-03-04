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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.registry.resource.ImageResourceModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.utils.ImageUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public class ImageResourceStore extends ResourceStore<ImageResourceModel> implements IImageResourceStore {

    @PostConstruct
    public void init() {
        setClazz(ImageResourceModel.class);
    }

    @Override
    @Transactional
    public ResourceModel saveImageResource(RegistryNodeModel registryNode, String name, String title, FileInfo fileInfo) throws IOException {
        String lookup = registryNode.getLookup() + "." + StringUtils.normalize(name);
        ImageResourceModel imageResourceModel = findByLookup(lookup);
        if (imageResourceModel == null) {
            imageResourceModel = new ImageResourceModel();
            imageResourceModel.setName(name);
            imageResourceModel.setParent(registryNode);
            imageResourceModel.setLastVersion(1);
            imageResourceModel.setPublishedVersion(1);

            ImageResourceVersionModel imageResourceVersionModel = new ImageResourceVersionModel();
            imageResourceVersionModel.setTitle(title);
            imageResourceVersionModel.setOriginalFilename(fileInfo.getOrgFilename());
            imageResourceModel.setResourceVersion(imageResourceVersionModel);

            imageResourceVersionModel.setResource(imageResourceModel);
            imageResourceVersionModel.setCulture(Cultures.AMERICAN);
            imageResourceVersionModel.setStatus(ResourceStatus.PUBLISHED);
            imageResourceVersionModel.setUpdated(new Date());
            imageResourceVersionModel.setVersion(imageResourceModel.getLastVersion());
            save(imageResourceModel);

            saveImage(imageResourceModel, imageResourceVersionModel, fileInfo);
            resourceVersionStore.saveOrUpdate(imageResourceVersionModel);
        } else {
            ImageResourceVersionModel imageResourceVersionModel = (ImageResourceVersionModel) resourceVersionStore.findByResourceIdCultureAndVersion(
                    imageResourceModel.getId(), Cultures.AMERICAN, imageResourceModel.getLastVersion());
            imageResourceVersionModel.setTitle(title);
            imageResourceVersionModel.setOriginalFilename(fileInfo.getOrgFilename());

            saveImage(imageResourceModel, imageResourceVersionModel, fileInfo);

            imageResourceVersionModel.setUpdated(new Date());
            resourceVersionCacheService.removeCacheByResourceVersion(imageResourceVersionModel);
            resourceVersionStore.saveOrUpdate(imageResourceVersionModel);
        }
        return imageResourceModel;
    }

    private void saveImage(ImageResourceModel imageResourceModel, ImageResourceVersionModel imageResourceVersionModel, FileInfo fileInfo) throws IOException {
        String imageResourceVersionFilename = imageResourceVersionModel.getId() + "_" +
                                              imageResourceVersionModel.getVersion() + "_" +
                                              imageResourceVersionModel.getCulture().name();
	    byte[] data = fileInfo.getData();

	    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,imageResourceVersionFilename,new ByteArrayInputStream(data), helper.getImage(), String.valueOf(imageResourceModel.getId()));

        Integer[] size = ImageUtils.getImageSize(data);
        imageResourceVersionModel.setWidth(size[0]);
        imageResourceVersionModel.setHeight(size[1]);
        resourceVersionStore.saveOrUpdate(imageResourceVersionModel);
    }

}
