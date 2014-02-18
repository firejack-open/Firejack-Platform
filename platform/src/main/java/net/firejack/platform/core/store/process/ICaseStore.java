package net.firejack.platform.core.store.process;

import net.firejack.platform.api.process.domain.CaseSearchTermVO;
import net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.IStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Interface provides access to case data
 */
public interface ICaseStore extends IStore<CaseModel, Long> {

    /**
     * Finds the case by ID
     *
     * @param id - case ID to search by
     * @return found case
     */
    CaseModel findByIdNoNulls(Long id);

    /**
     * Finds the case with the tasks by ID
     *
     * @param id case ID to search by
     * @return found case with the tasks
     */
    CaseModel findByIdWithTasks(Long id);

    /**
     * Searches the cases by search parameters
     * @param caseSearchTermVO value object with search parameters
     * @param filter search filter
     * @return list of found cases
     */
    List<CaseModel> findAllBySearchParams(CaseSearchTermVO caseSearchTermVO, SpecifiedIdsFilter<Long> filter);

    /**
     * Counts the cases found by search parameters
     * @param caseSearchTermVO value object with search parameters
     * @param filter search filter
     * @return number of found cases
     */
    long countAllBySearchParams(CaseSearchTermVO caseSearchTermVO, SpecifiedIdsFilter<Long> filter);

    /**
     * Searches the cases by user ID
     *
     * @param userId        - user ID to search by
     * @param lookupPrefix  - prefix of the lookup
     * @param active        - flag showing whether the case is active
     * @param filter        - search filter
     * @param offset        - parameter specifying where to start the result list from
     * @param limit         - parameter specifying the number of the results
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of the found cases
     */
    List<CaseModel> findAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the cases that are found by user ID
     *
     * @param userId       - user ID to search by
     * @param lookupPrefix - prefix of the lookup
     * @param active       - flag showing whether the case is active
     * @param filter       - search filter
     * @return number of the found cases
     */
    long countAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter);

    /**
     * Finds the cases for which the specified user, their role or the group they belong to is in the actor set
     *
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sotting direction
     * @return list of found cases
     */
    List<CaseModel> findBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the cases for which the specified user, their role or the group they belong to is in the actor set
     *
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return number of found cases
     */
    long countBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter);

    /**
     * Finds the closed cases that the specified user has performed
     *
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sorting direction
     * @return found cases
     */
    List<CaseModel> findClosedCasesForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the closed cases that the specified user has performed
     *
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return number of found cases
     */
    long countClosedCasesForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter);

    /**
     * Starts the case.
     * If the first activity is of system type,
     * the first task and all that follow will be performed until the first non-system activity is reached.
     *
     * @param processLookup     - lookup of the process the case belongs to
     * @param assignee          - user assigned to the case
     * @param currentUser       - user currently logged in
     * @param caseObjectModels       - case object entities
     * @param userActorTeam     - user actor entities
     * @param caseDescription   - description of the case
     * @param allowNullAssignee - flag showing whether assignee is allowed to be set to null
     * @param customFields      - map of custom fields values having field lookups as keys
     * @return ID of the started case
     * @throws UserNotInActorSetException - thrown if the assignee is not in the actor set
     * @throws net.firejack.platform.core.exception.TaskNotActiveException
     *
     */
    Long externalStart(String processLookup, UserModel assignee, UserModel currentUser, Collection<CaseObjectModel> caseObjectModels, Map<Long, Set<Long>> userActorTeam, String caseDescription, boolean allowNullAssignee, Map<String, List<ProcessCustomFieldVO>> customFields) throws UserNotInActorSetException, TaskNotActiveException;

    /**
     * Stops the case
     *
     * @param processLookup - lookup of the process the case belongs to
     * @param entityId      - ID of the entity
     * @param entityType    - type of the entity
     * @return - ID of the stopped case
     */
    Long stop(String processLookup, Long entityId, String entityType);

    /**
     * Stops the case
     * @param caseId the caseId to stop
     * @param explanationId the explanationId to set to stopped case
     * @param comment the comment that will be added to the stopped case
     */
    void stop(Long caseId, Long explanationId, String comment);

    /**
     * Finds the active task of the case
     *
     * @param caseId - ID of the case
     * @return active task
     */
    TaskModel findActiveTask(Long caseId);

    /**
     * Finds all the active cases to which the object with the given ID and type is attached
     *
     * @param entityTypeLookup - defines the type of the attached object
     * @param entityId         - ID of the attached object
     * @param filter           - IDs filter
     * @return list of found cases
     */
    List<CaseModel> findActive(String entityTypeLookup, Long entityId, SpecifiedIdsFilter<Long> filter);

    /**
     * Resets the case to its' first activity
     * by closing the currently active task and creating a new task (for the first activity).
     * If the first activity is of system type,
     * the first task and all that follow will be performed until the first non-system activity is reached.
     *
     * @param caseId          - ID of the case to reset
     * @param comment         - text to be added to the newly created note (comment about case reset)
     * @param taskDescription - description for the newly created task
     * @throws net.firejack.platform.core.exception.TaskNotActiveException
     *
     * @throws net.firejack.platform.core.exception.UserNotInActorSetException
     *
     */
    void reset(Long caseId, String comment, String taskDescription) throws UserNotInActorSetException, TaskNotActiveException;

    /**
     * Update case description
     *
     * @param caseId      case ID
     * @param description description
     */
    void update(Long caseId, String description);

    /**
     * Updates case custom fields values
     * @param processLookup lookup of the process whose case is being updated
     * @param entityLookup lookup of the entity whose fields are being updated
     * @param entityId ID of the entity whose fields are being updated
     * @param customFields list of process custom fields
     */
    void updateCaseCustomFields(String processLookup, String entityLookup, Long entityId, List<ProcessCustomFieldVO> customFields);

    CaseModel moveCaseToActivity(Long entityId, Long activityActionId, Long assigneeId, Long currentUserId, String comment);

}