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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.process.domain.CaseAttachment;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.process.CaseAttachmentModel;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseAttachmentStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Class encapsulates the functionality of case attachment upload
 */
@TrackDetails
@Component("uploadCaseAttachmentBroker")
public class UploadCaseAttachmentBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<CaseAttachment>> {

    @Autowired
	private FileHelper helper;

    @Autowired
    @Qualifier("caseAttachmentStore")
    private ICaseAttachmentStore caseAttachmentStore;

    /**
     * Invokes data access layer in order to persist an attachment entity constructed out of request parameters.
     * The file is uploaded to the designated directory.
     *
     * @param request service request containing ID of the case, name and description of the attachment, file name and input stream
     * @return service response containing the info about the success of the attachment upload
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<CaseAttachment> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long caseId = (Long) request.getData().get("caseId");
        String name = (String) request.getData().get("name");
        String description = (String) request.getData().get("description");
        InputStream inputStream = (InputStream) request.getData().get("inputStream");
        String filename = (String) request.getData().get("filename");

        CaseAttachmentModel caseAttachmentModel = new CaseAttachmentModel();
        caseAttachmentModel.setName(name);
        caseAttachmentModel.setDescription(description);
        caseAttachmentModel.setFilename(filename);
        caseAttachmentModel.setProcessCase(new CaseModel());
        caseAttachmentModel.getProcessCase().setId(caseId);

        caseAttachmentStore.saveOrUpdate(caseAttachmentModel);
        caseAttachmentStore.refresh(caseAttachmentModel);

	    OPFEngine.FileStoreService.deleteFile(OpenFlame.FILESTORE_CONTENT, String.valueOf(caseAttachmentModel.getId()), helper.getCase(), caseId.toString(), "attachments");
	    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, String.valueOf(caseAttachmentModel.getId()), inputStream, helper.getCase(), caseId.toString(), "attachments");

	    return new ServiceResponse<CaseAttachment>(
                factory.convertTo(CaseAttachment.class, caseAttachmentModel),
                "Case Attachment file has been uploaded successfully.", true);
    }
    
}
