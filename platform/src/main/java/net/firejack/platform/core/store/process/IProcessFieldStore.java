/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.store.version.IUIDStore;

import java.util.List;


/**
 * Interface provides access to ProcessField data
 */
public interface IProcessFieldStore extends IUIDStore<ProcessFieldModel, Long> {

    /**
     * Finds a process field by process ID.
     * If the ID is null, returns global process fields.
     * @param processId - ID of the process for which the fields are being retrieved
     * @return list of process fields
     */
    List<ProcessFieldModel> findByProcessIdPlusGlobal(Long processId);

    /**
     * Updates value of a process field
     * @param processLookup lookup of the process whose field value is being modified
     * @param entityLookup lookup of the entity which has the field
     * @param fieldLookup lookup of the field whose value is being modified
     * @param value new field value
     * @param caseId ID of the case for which the value is being modified
     */
    void updateProcessFieldValue(String processLookup, String entityLookup, String fieldLookup, Object value, Long caseId);

}