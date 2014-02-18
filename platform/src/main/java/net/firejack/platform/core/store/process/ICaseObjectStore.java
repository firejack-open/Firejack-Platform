package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.CaseObjectModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.Tuple;

import java.util.List;

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

/**
 * Interface provides access to case object data
 */
public interface ICaseObjectStore extends IStore<CaseObjectModel, Long> {

    /**
     * Finds the case by process and entity
     * @param processLookup - lookup of the process to search by
     * @param entityId - ID of the entity
     * @param entityType - type of the entity
     * @return found case
     */
    CaseModel findCaseByProcessAndEntity(String processLookup, Long entityId, String entityType);

    /**
     * Finds the case by process and entity
     * @param processLookup - lookup of the process to search by
     * @param entityId - ID of the entity
     * @param entityType - type of the entity
     * @return list of found cases and objects IDs
     */
    List<Tuple<Long, CaseModel>> findCaseByProcessAndEntity(String processLookup, List<Long> entityId, String entityType);

    /**
     * Finds case object entities by assignee
     * @param assigneeId - ID of the user assigned to the case
     * @param entityType - type of the entity
     * @param statusId - ID of the case status
     * @return list of found case object entities
     */
    List<CaseObjectModel> findByAssignee(Long assigneeId, String entityType, Long statusId);

    CaseObjectModel findByTask(Long taskId);

}
