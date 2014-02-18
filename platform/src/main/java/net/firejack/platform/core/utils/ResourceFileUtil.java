/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.utils;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.AudioResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.ImageResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.VideoResourceVersionModel;
import net.firejack.platform.model.helper.FileHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ResourceFileUtil {

    private static final Logger logger = Logger.getLogger(ResourceFileUtil.class);

    @Autowired
    private FileHelper helper;

    /**
     * @param temporaryUploadedFileName
     * @param resourceVersion
     * @throws java.io.IOException
     */
    public void processTempFile(String temporaryUploadedFileName, AbstractResourceVersionModel resourceVersion) throws IOException {

	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,temporaryUploadedFileName,helper.getTemp());
	    byte[] bytes = IOUtils.toByteArray(stream);
	    IOUtils.closeQuietly(stream);

	    String  path = null;
        if (resourceVersion instanceof ImageResourceVersionModel) {
            path = helper.getImage();
        } else if (resourceVersion instanceof AudioResourceVersionModel) {
            path = helper.getAudio();
        } else if (resourceVersion instanceof VideoResourceVersionModel) {
            path = helper.getVideo();
        }

	    String resourceVersionFilename = resourceVersion.getId() + "_" + resourceVersion.getVersion() + "_" + resourceVersion.getCulture().name();
	    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,resourceVersionFilename,new ByteArrayInputStream(bytes), path, String.valueOf(resourceVersion.getResource().getId()));

        if (resourceVersion instanceof ImageResourceVersionModel) {
            Integer[] size = ImageUtils.getImageSize(bytes);
            ((ImageResourceVersionModel) resourceVersion).setWidth(size[0]);
            ((ImageResourceVersionModel) resourceVersion).setHeight(size[1]);
        }
    }
}