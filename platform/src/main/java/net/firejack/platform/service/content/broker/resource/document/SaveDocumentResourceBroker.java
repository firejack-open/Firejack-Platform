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

package net.firejack.platform.service.content.broker.resource.document;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.DocumentResource;
import net.firejack.platform.core.model.registry.resource.DocumentResourceModel;
import net.firejack.platform.core.model.registry.resource.DocumentResourceVersionModel;
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


@Component("saveDocumentResourceBroker")
@TrackDetails
public class SaveDocumentResourceBroker
        extends AbstractSaveResourceBroker<DocumentResourceModel, DocumentResource> {

    @Autowired
    @Qualifier("documentResourceStore")
    private IResourceStore<DocumentResourceModel> store;

    @Autowired
    @Qualifier("documentResourceVersionStore")
    private IResourceVersionStore<DocumentResourceVersionModel> resourceVersionStore;

    @Override
    protected IResourceStore<DocumentResourceModel> getResourceStore() {
        return store;
    }


    @Override
    protected String getSuccessMessage(boolean isNew) {
        return "Document resource has " + (isNew ? "created" : "updated") +  " successfully";
    }

    @Override
    protected void save(DocumentResourceModel documentResourceModel) throws Exception {
        String temporaryUploadedFileName = documentResourceModel.getResourceVersion().getTemporaryFilename();

	    InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, temporaryUploadedFileName, helper.getTemp());
	    if (stream!=null) {
            super.save(documentResourceModel);
            try {
                DocumentResourceVersionModel documentResourceVersion = documentResourceModel.getResourceVersion();
                String documentResourceVersionFilename = documentResourceVersion.getId() + "_" +
                                                      documentResourceVersion.getVersion() + "_" +
                                                      documentResourceVersion.getCulture().name();
	            OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT,documentResourceVersionFilename,stream,helper.getDocument(), String.valueOf(documentResourceModel.getId()));

                resourceVersionStore.saveOrUpdate(documentResourceVersion);
                documentResourceVersion.setTemporaryFilename(null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
	            IOUtils.closeQuietly(stream);
            }
        } else {
            super.save(documentResourceModel);
        }
    }

}