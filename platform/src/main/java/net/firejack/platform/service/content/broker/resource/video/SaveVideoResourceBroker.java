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