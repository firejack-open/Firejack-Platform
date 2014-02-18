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
