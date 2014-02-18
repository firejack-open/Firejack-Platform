/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.CaseExplanationModel;
import net.firejack.platform.core.store.version.IUIDStore;

import java.util.List;

/**
 * Interface provides access to case explanation data
 */
public interface ICaseExplanationStore extends IUIDStore<CaseExplanationModel, Long> {

    /**
     * Finds case explanation entities by search term
     * @param processId - ID of the process explanations belong to
     * @param term - the term to search by
     * @param sortColumn - column to sort by
     * @param sortDirection - sorting direction
     * @return list of case explanations
     */
    List<CaseExplanationModel> findProcessExplanationsBySearchTerm(Long processId, String term, String sortColumn, String sortDirection);

    /**
     * Deletes case explanation entity by process IF
     * @param processId - process ID
     */
    void deleteByProcessId(Long processId);

}
