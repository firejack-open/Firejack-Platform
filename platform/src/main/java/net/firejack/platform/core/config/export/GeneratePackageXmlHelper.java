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

package net.firejack.platform.core.config.export;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.config.meta.element.resource.IStorableResourceVersionDescriptorElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceVersionElement;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.model.helper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("generatePackageXmlHelper")
public class GeneratePackageXmlHelper {
	@Autowired
	private FileHelper helper;

	/**
	 * @param resourceVersions
	 * @param resourceVersionElements
	 *
	 * @return
	 * @throws java.io.IOException
	 */
	public String generateResourcesZipFile(List<AbstractResourceVersionModel> resourceVersions, List<ResourceVersionElement> resourceVersionElements) throws IOException {
		final Map<String, InputStream> filePaths = new HashMap<String, InputStream>();
		Long uploadFileTime = new Date().getTime();

		for (int i = 0; i < resourceVersions.size(); i++) {
			AbstractResourceVersionModel resourceVersion = resourceVersions.get(i);
			String resourcePathFolder = null;
			if (resourceVersion instanceof ImageResourceVersionModel) {
				resourcePathFolder = helper.getImage();
			} else if (resourceVersion instanceof AudioResourceVersionModel) {
				resourcePathFolder = helper.getAudio();
			} else if (resourceVersion instanceof VideoResourceVersionModel) {
				resourcePathFolder = helper.getVideo();
			} else if (resourceVersion instanceof FileResourceVersionModel) {
				resourcePathFolder = helper.getFile();
			}

			if (resourcePathFolder != null) {
				String storedFileName = resourceVersion.getId() + "_" + resourceVersion.getVersion() + "_" + resourceVersion.getCulture().name();
				String resourcePath = String.valueOf(resourceVersion.getResource().getId());
				String temporaryResourceFilename = SecurityHelper.generateRandomSequence(16) + "." + uploadFileTime;

				InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_CONTENT,storedFileName, resourcePathFolder, resourcePath);

				if (stream != null) {
					filePaths.put(temporaryResourceFilename, stream);
				}
				((IStorableResourceVersionDescriptorElement) resourceVersionElements.get(i)).setResourceFilename(temporaryResourceFilename);
				((IStorableResourceVersionDescriptorElement) resourceVersionElements.get(i)).setOriginalFilename(
						((IStorableResourceVersionModel) resourceVersion).getOriginalFilename());

			}
		}
		String temporaryResourceFileName = null;
		if (!filePaths.isEmpty()) {
			uploadFileTime = new Date().getTime();
			temporaryResourceFileName = SecurityHelper.generateRandomSequence(16) + "." + uploadFileTime;
			OPFEngine.FileStoreService.zip(OpenFlame.FILESTORE_BASE, filePaths, helper.getTemp(), temporaryResourceFileName);
		}
		return temporaryResourceFileName;
	}

}
