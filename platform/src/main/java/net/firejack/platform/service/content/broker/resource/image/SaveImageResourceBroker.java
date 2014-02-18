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