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

import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.store.IStore;

import java.util.List;

public interface ICaseNoteStore extends IStore<CaseNoteModel, Long> {

    /**
     * @param id
     * @param term
     * @return
     */
    List<CaseNoteModel> findByTaskIdAndSearchTerm(Long id, String term);

    /**
     * @param id
     * @param term
     * @return
     */
    List<CaseNoteModel> findByCaseIdAndSearchTerm(Long id, String term);

    /**
     * @param entityId
     * @param entityType
     * @param term
     * @return
     */
    List<CaseNoteModel> findByCaseObjectAndSearchTerm(Long entityId, String entityType, String term);

    @Override
    void saveOrUpdate(CaseNoteModel entity);

}
