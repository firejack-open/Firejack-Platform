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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.CaseNote;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseNoteStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class encapsulates the functionality of persisting a case note
 */
public abstract class SaveCaseNoteBroker extends SaveBroker<CaseNoteModel, CaseNote, CaseNote> {

    @Autowired
    @Qualifier("caseNoteStore")
    private ICaseNoteStore caseNoteStore;

    /**
     * Converts a note data transfer object to a note entity
     * @param caseNote note data transfer object
     * @return note entity
     */
    @Override
    protected CaseNoteModel convertToEntity(CaseNote caseNote) {
        return factory.convertFrom(CaseNoteModel.class, caseNote);
    }

    /**
     * Converts a note entity to a note data transfer object
     * @param entity note entity
     * @return note data transfer object
     */
    @Override
    protected CaseNote convertToModel(CaseNoteModel entity) {
        CaseNote caseNote = factory.convertTo(CaseNote.class, entity);
        if (caseNote.getUser() == null) {
            IUserInfoProvider currentUser = ContextManager.getUserInfoProvider();
            User user = new User();
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername());
            caseNote.setUser(user);
        }
        return caseNote;
    }

    /**
     * Invokes data access layer in order to save or update a note
     *
     * @param caseNoteModel note entity
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected void save(CaseNoteModel caseNoteModel) throws Exception {
        caseNoteStore.saveOrUpdate(caseNoteModel);
    }

    /**
     * Invokes data access layer in order to find the note by its' ID
     * and check if the currently logged in user is the note creator.
     * In case the check fails, the note won't be persisted.
     *
     * @param caseNoteServiceRequest service request containing the note
     * @return service response containing the info about the success of the operation
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<CaseNote> perform(ServiceRequest<CaseNote> caseNoteServiceRequest) throws Exception {
        if (caseNoteServiceRequest.getData().getId() != null) {
            CaseNoteModel existingCaseNote = caseNoteStore.findById(caseNoteServiceRequest.getData().getId());
            Long caseNoteCreatorId = existingCaseNote.getUser().getId();
            Long currentUserId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();
            if (!currentUserId.equals(caseNoteCreatorId)) {
                return new ServiceResponse<CaseNote>("Cannot save notes that you haven't created", false);
            }
        }
        return super.perform(caseNoteServiceRequest);
    }
    
}
