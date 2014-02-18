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
