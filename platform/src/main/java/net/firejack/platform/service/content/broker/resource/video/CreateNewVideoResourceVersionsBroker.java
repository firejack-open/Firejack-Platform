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
import net.firejack.platform.service.content.broker.resource.AbstractCreateNewResourceVersionsBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;


@Component("createNewVideoResourceVersionsBroker")
@TrackDetails
public class CreateNewVideoResourceVersionsBroker
        extends AbstractCreateNewResourceVersionsBroker<VideoResourceModel, VideoResourceVersionModel, VideoResource> {

    @Autowired
    @Qualifier("videoResourceStore")
    private IResourceStore<VideoResourceModel> videoResourceStore;

    @Autowired
    @Qualifier("videoResourceVersionStore")
    private IResourceVersionStore<VideoResourceVersionModel> videoResourceVersionStore;


    @Override
    public IResourceStore<VideoResourceModel> getResourceStore() {
        return videoResourceStore;
    }

    @Override
    public IResourceVersionStore<VideoResourceVersionModel> getResourceVersionStore() {
        return videoResourceVersionStore;
    }

    @Override
    public String getResourceName() {
        return "Video";
    }

    @Override
    protected void copyResourceFiles(VideoResourceModel resource,
                                     List<VideoResourceVersionModel> oldResourceVersions,
                                     List<VideoResourceVersionModel> newResourceVersions) {

        if (!newResourceVersions.isEmpty()) {
            for (int i = 0, newResourceVersionsSize = newResourceVersions.size(); i < newResourceVersionsSize; i++) {
                VideoResourceVersionModel oldResourceVersion = oldResourceVersions.get(i);
                VideoResourceVersionModel newResourceVersion = newResourceVersions.get(i);
	            String oldFilename = oldResourceVersion.getId() + "_" +
			            oldResourceVersion.getVersion() + "_" +
			            oldResourceVersion.getCulture().name();

	            String newFilename = newResourceVersion.getId() + "_" +
			            newResourceVersion.getVersion() + "_" +
			            newResourceVersion.getCulture().name();

	            InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT, oldFilename,helper.getVideo(), String.valueOf(resource.getId()));
	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, newFilename, stream, helper.getVideo(), String.valueOf(resource.getId()));
	            IOUtils.closeQuietly(stream);
            }
        }
    }
}