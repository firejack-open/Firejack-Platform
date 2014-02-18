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

package net.firejack.platform.service.content.broker.resource.file;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileResource;
import net.firejack.platform.core.model.registry.resource.FileResourceModel;
import net.firejack.platform.core.model.registry.resource.FileResourceVersionModel;
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


@Component("saveFileResourceBroker")
@TrackDetails
public class SaveFileResourceBroker
        extends AbstractSaveResourceBroker<FileResourceModel, FileResource> {

    @Autowired
    @Qualifier("fileResourceStore")
    private IResourceStore<FileResourceModel> store;

    @Autowired
    @Qualifier("fileResourceVersionStore")
    private IResourceVersionStore<FileResourceVersionModel> resourceVersionStore;

    @Override
    protected IResourceStore<FileResourceModel> getResourceStore() {
        return store;
    }


    @Override
    protected String getSuccessMessage(boolean isNew) {
        return "File resource has " + (isNew ? "created" : "updated") +  " successfully";
    }

    @Override
    protected void save(FileResourceModel fileResourceModel) throws Exception {
        String temporaryUploadedFileName = fileResourceModel.getResourceVersion().getTemporaryFilename();

	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadedFileName, helper.getTemp());
	    if (stream!=null) {
            super.save(fileResourceModel);
            try {
                FileResourceVersionModel fileResourceVersion = fileResourceModel.getResourceVersion();
                String fileResourceVersionFilename = fileResourceVersion.getId() + "_" +
                                                      fileResourceVersion.getVersion() + "_" +
                                                      fileResourceVersion.getCulture().name();
	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,fileResourceVersionFilename,stream,helper.getFile(), String.valueOf(fileResourceModel.getId()));

                resourceVersionStore.saveOrUpdate(fileResourceVersion);
                fileResourceVersion.setTemporaryFilename(null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
	            IOUtils.closeQuietly(stream);
            }
        } else {
            super.save(fileResourceModel);
        }
    }

}