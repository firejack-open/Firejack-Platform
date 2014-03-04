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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.content.domain.ImageFileInfo;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ImageUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.content.utils.TTLParser;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;


@Component
@TrackDetails
@ProgressComponent(upload = true)
public class UploadResourceFileBroker
		extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<FileInfo>> {

    public static final String PARAM_INPUT_STREAM = "inputStream";
    public static final String PARAM_FILENAME = "originalFilename";
    public static final String PARAM_RESOURCE_TYPE = "resourceType";
    public static final String PARAM_CUSTOM_TTL = "ttl";

	@Autowired
	private FileHelper helper;

	@Override
	public ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
		InputStream inputStream = (InputStream) request.getData().get(PARAM_INPUT_STREAM);
		String originalFilename = (String) request.getData().get(PARAM_FILENAME);
		String type = (String) request.getData().get(PARAM_RESOURCE_TYPE);
		String ttlSValue = (String) request.getData().get(PARAM_CUSTOM_TTL);
		ResourceType resourceType = ResourceType.findByName(type);
		FileInfo fileInfo;

		Long uploadFileTime = new Date().getTime();
        if (StringUtils.isBlank(ttlSValue)) {
            long ttl = TTLParser.parseTTLValue(ttlSValue);
            if (ttl > 0) {//ttl - 10 min because fileCleaner deletes files that were created equal or more than 10 min ago.
                uploadFileTime = uploadFileTime + (ttl - 600000);
            }
        }
		String randomName = SecurityHelper.generateRandomSequence(16);
		String temporaryUploadFileName = randomName + "." + uploadFileTime;

		byte[] bytes = IOUtils.toByteArray(inputStream);
		OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,temporaryUploadFileName,new ByteArrayInputStream(bytes), helper.getTemp());

		if (ResourceType.IMAGE.equals(resourceType)) {
            try {
                Integer[] size = ImageUtils.getImageSize(bytes);
                fileInfo = new ImageFileInfo(temporaryUploadFileName, uploadFileTime, size[0], size[1]);
            } catch (Exception e) {
                logger.warn("Can't get image size. " + e.getMessage(), e);
                fileInfo = new FileInfo(temporaryUploadFileName, uploadFileTime);
            }
		} else {
			fileInfo = new FileInfo(temporaryUploadFileName, uploadFileTime);
		}
		fileInfo.setOrgFilename(originalFilename);

		return new ServiceResponse<FileInfo>(fileInfo, "File has uploaded successfully.", true);
	}

}