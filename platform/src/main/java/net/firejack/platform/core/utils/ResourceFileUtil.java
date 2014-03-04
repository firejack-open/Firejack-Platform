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