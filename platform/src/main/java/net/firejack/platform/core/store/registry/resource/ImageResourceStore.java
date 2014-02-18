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
