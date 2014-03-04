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

import net.firejack.platform.core.broker.DeleteBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.process.ICaseNoteStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of note deletion
 */
@TrackDetails
@Component("deleteCaseNoteBroker")
public class DeleteCaseNoteBroker extends DeleteBroker<CaseNoteModel> {

    @Autowired
    @Qualifier("caseNoteStore")
    private ICaseNoteStore caseNoteStore;

    @Override
    protected IStore<CaseNoteModel, Long> getStore() {
        return caseNoteStore;
    }

    /**
     * Invokes data access layer in order to remove the note
     * @param id ID of the note
     */
    @Override
    protected void delete(Long id) {
        caseNoteStore.deleteById(id);
    }

    /**
     * Finds the note by its' ID and checks if the currently logged in user is the note creator.
     * If the check fails, the note removal operation is revoked.
     *
     * @param request service request containing ID of the note
     * @return service response containing the information about the success of the deletion operation
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        CaseNoteModel caseNote = caseNoteStore.findById(request.getData().getIdentifier());
        Long caseNoteCreatorId = caseNote.getUser().getId();
        Long currentUserId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();
        if (!currentUserId.equals(caseNoteCreatorId)) {
            return new ServiceResponse("Cannot delete notes that you haven't created", false);
        }
        return super.perform(request);
    }

}
