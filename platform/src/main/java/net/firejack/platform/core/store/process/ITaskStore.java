package net.firejack.platform.core.store.process;

import net.firejack.platform.api.process.domain.TaskSearchTermVO;
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SearchQuery;

import java.util.List;
import java.util.Map;

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
 * Interface provides access to task data
 */
public interface ITaskStore extends IStore<TaskModel, Long> {

    /**
     * Searches the tasks by search parameters
     *
     * @param taskSearchTermVO the VO that contains search params
     * @param filter IDs filter
     * @return list of found tasks
     */
    List<TaskModel> findAllBySearchParams(TaskSearchTermVO taskSearchTermVO, SpecifiedIdsFilter filter);

    /**
     * Counts the tasks that are found by search parameters
     *
     * @param taskSearchTermVO the VO that contains search params
     * @param filter IDs filter
     * @return number of found tasks
     */
    long countAllBySearchParams(TaskSearchTermVO taskSearchTermVO, SpecifiedIdsFilter filter);

    /**
     * Finds tasks by user ID
     *
     * @param userId        - ID of the user to search by
     * @param lookupPrefix  - lookup prefix
     * @param active        - flag showing whether the task is active
     * @param filter        - IDs filter
     * @param offset        - parameter specifying where to start the result list from
     * @param limit         - parameter specifying the number of the results
     * @param sortColumn    - column to sort the results by
     * @param sortDirection - sorting direction
     * @return list of found tasks
     */
    List<TaskModel> findAllByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the tasks that are found by user ID
     *
     * @param userId       - ID of the user to search by
     * @param lookupPrefix - lookup prefix
     * @param active       - flag showing whether the task is active
     * @param filter       - IDs filter
     * @return number of found tasks
     */
    long countByUserId(Long userId, String lookupPrefix, Boolean active, SpecifiedIdsFilter filter);

    /**
     * Finds the tasks for which the specified user, their role or the group they belong to is in the actor set
     *
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sorting direction
     * @return list of found tasks
     */
    List<TaskModel> findBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter<Long> filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the tasks for which the specified user, their role or the group they belong to is in the actor set
     *
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return number of found tasks
     */
    long countBelongingToUserActor(Long userId, String lookupPrefix, SpecifiedIdsFilter filter);

    /**
     * Finds the closed tasks that the specified user has performed
     *
     * @param userId        - ID of the user
     * @param lookupPrefix  - lookup prefix
     * @param filter        - IDs filter
     * @param offset        - item from which to start the results listing from
     * @param limit         - number of teh resulting items to be listed
     * @param sortColumn    - column to sort by
     * @param sortDirection - sorting direction
     * @return list of found tasks
     */
    List<TaskModel> findClosedTasksForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter, Integer offset, Integer limit, String sortColumn, String sortDirection);

    /**
     * Counts the closed tasks that the specified user has performed
     *
     * @param userId       - ID of the user
     * @param lookupPrefix - lookup prefix
     * @param filter       - IDs filter
     * @return number of found tasks
     */
    long countClosedTasksForCurrentUser(Long userId, String lookupPrefix, SpecifiedIdsFilter filter);

    /**
     * Assigns the task to a user
     *
     * @param taskId        - ID of the task to be assigned
     * @param assigneeId    - ID of the user to assign to
     * @param explanationId - ID of the explanation of the assignment
     * @param noteText      - text of the note
     * @return task the user is assigned to
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     * @throws TaskNotActiveException     - thrown in case the task is inactive
     */
    TaskModel assign(Long taskId, Long assigneeId, Long explanationId, String noteText) throws UserNotInActorSetException, TaskNotActiveException;

    /**
     * Performs the task
     *
     * @param taskId          - ID of the task to be performed
     * @param assigneeId      - ID of the user to be assigned to the next task
     * @param explanationId   - ID of the explanation
     * @param noteText        - text of the note
     * @param taskDescription - description of the task
     * @return next task
     * @throws TaskNotActiveException     - thrown in case the task is inactive
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     */
    TaskModel perform(Long taskId, Long assigneeId, Long explanationId, String noteText, String taskDescription) throws TaskNotActiveException, UserNotInActorSetException;

    /**
     * Performs the specified task and all the tasks that follow which are of the system activity type.
     * A task is performed by invoking {@code net.firejack.platform.core.store.process.ITaskStore#perform(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)}
     *
     * @param taskId          - ID of the task to be performed
     * @param assigneeId      - ID of the user to be assigned to the next task
     * @param explanationId   - ID of the explanation for the task performing
     * @param noteText        - text of the note user has entered
     * @param taskDescription - description of the task
     * @return next task of the human activity type or null if there are no more activities in the process (case closed)
     * @throws TaskNotActiveException     - thrown in case of an attempt to perform an inactive task
     * @throws UserNotInActorSetException - thrown in case the currently logged in user (user performing the task)
     *                                    or the user to be assigned to the next task
     *                                    are not in the actor set of the corresponding activity
     */
    TaskModel performIncludingFollowingSystemActivities(Long taskId, Long assigneeId, Long explanationId, String noteText, String taskDescription) throws TaskNotActiveException, UserNotInActorSetException;

    /**
     * Rollbacks the task
     *
     * @param taskId               - ID of the task to be performed
     * @param assigneeId           - ID of the user to be assigned to the previous task
     * @param explanationId        - ID of the explanation
     * @param noteText             - text of the note
     * @param taskDescription      - description of the task
     * @param activityId           - activity Id
     * @return previous task
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException      - thrown in case the task is inactive
     * @throws UserNotInActorSetException  - thrown in case the user is not part of the actor set for the task
     */
    TaskModel rollback(Long taskId, Long assigneeId, Long explanationId,
                       String noteText, String taskDescription, Long activityId)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException;

    /**
     * Rollbacks the task
     *
     * @param taskId               - ID of the task to be performed
     * @param assigneeId           - ID of the user to be assigned to the previous task
     * @param explanationId        - ID of the explanation
     * @param noteText             - text of the note
     * @param taskDescription      - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return previous task
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException      - thrown in case the task is inactive
     * @throws UserNotInActorSetException  - thrown in case the user is not part of the actor set for the task
     */
    TaskModel rollback(Long taskId, Long assigneeId, Long explanationId, String noteText,
                       String taskDescription, boolean checkCurrentActorSet)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException;

    /**
     * Rollbacks the task. If the task is of type SYSTEM then rollback all previous tasks until the non-SYSTEM task
     *
     * @param taskId - list of ID of the tasks to be performed
     * @param assigneeId - ID of the user to be assigned to the previous task
     * @param explanationId - ID of the explanation
     * @param noteText - text of the note
     * @param taskDescription - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return previous task
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException - thrown in case the task is inactive
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     */
    TaskModel rollbackIncludingFollowingSystemActivity(
            Long taskId, Long assigneeId, Long explanationId,
            String noteText, String taskDescription, boolean checkCurrentActorSet)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException;

    /**
     * Rollbacks tasks with id from taskIdList. If the task is of type SYSTEM
     * then rollback all previous tasks till the current task is non-SYSTEM task
     *
     * @param taskIdList - list of ID of the tasks to be performed
     * @param assigneeId - ID of the user to be assigned to the previous task
     * @param explanationId - ID of the explanation
     * @param noteText - text of the note
     * @param taskDescription - description of the task
     * @param checkCurrentActorSet check if current user is in actor set
     * @return map of tasks that we receive from rollback operation mapped by id of initial tasks from taskIdList.
     * @throws NoPreviousActivityException - thrown in case there is no previous activity
     * @throws TaskNotActiveException - thrown in case the task is inactive
     * @throws UserNotInActorSetException - thrown in case the user is not part of the actor set for the task
     */
    Map<Long, TaskModel> rollbackIncludingFollowingSystemActivity(
            List<Long> taskIdList, Long assigneeId, Long explanationId,
            String noteText, String taskDescription, boolean checkCurrentActorSet)
            throws NoPreviousActivityException, TaskNotActiveException, UserNotInActorSetException;

    List<TaskModel> advancedSearch(String type, List<List<SearchQuery>> searchQueries, Paging paging);

    Integer advancedSearchCount(String type, List<List<SearchQuery>> searchQueries);

    TaskModel createWorkflowTask(ProcessModel processModel, EntityModel entityModel, Long recordId);

    void deleteWorkflowTask(ProcessModel processModel, EntityModel entityModel, Long recordId);

    TaskModel readWorkflowTask(ProcessModel processModel, EntityModel entityModel, Long recordId);

}