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

import net.firejack.platform.api.process.domain.CaseAttachment;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseAttachmentModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseAttachmentStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving case attachments by case
 */
@TrackDetails
@Component("readCaseAttachmentsByCaseBroker")
public class ReadCaseAttachmentsByCaseBroker extends ListBroker<CaseAttachmentModel, CaseAttachment, SimpleIdentifier<Long>> {

    @Autowired
    @Qualifier("caseAttachmentStore")
    private ICaseAttachmentStore store;

    /**
     * Invokes data access layer in order to search for the attachments by the case
     * @param simpleIdentifierServiceRequest service request containing ID of the case
     * @return list of found attachments
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseAttachmentModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findByCaseIdAndSearchTerm(simpleIdentifierServiceRequest.getData().getIdentifier(), null);
    }
    
}
