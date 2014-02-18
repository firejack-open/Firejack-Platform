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

package net.firejack.platform.api.process;


import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;

import java.io.InputStream;

/**
 * Class is interface to process services
 */
public interface IProcessService {

    //////////PROCESS//////////

    /**
     * Service retrieves the process
     * @return found process
     */
    ServiceResponse<Process> readAllProcess();

    /**
     * Service retrieves the process
     * @param processId ID of the process
     * @return found process
     */
    ServiceResponse<Process> readProcess(Long processId);

    /**
     * Service searches for the processes for the currently logged in user
     * @param lookupPrefix lookup prefix
     * @return resulting list
     */
    ServiceResponse<Process> readMyProcesses(String lookupPrefix);

    /**
     * Service inserts the process
     * @param data process to be inserted
     * @return newly created process
     */
    ServiceResponse<Process> createProcess(Process data);

    ServiceResponse<Process> createWorkflow(Process workflow);

    /**
     * Service modifies the process
     * @param processId ID of the process to be modified
     * @param data process to be modified
     * @return updated process
     */
    ServiceResponse<Process> updateProcess(Long processId, Process data);

    /**
     * Service removes the process
     * @param processId ID of the process to be removed
     * @return response about the deletion success
     */
    ServiceResponse deleteProcess(Long processId);

    /**
     * Service searches the processes
     * @param term term to search by
     * @param humanProcess this flag indicates if process should start from human activity only. Otherwise activity type not checked
     * @return list of found processes
     */
    ServiceResponse<Process> searchProcesses(String term, Boolean humanProcess);

    ServiceResponse<RegistryNodeTree> readAllProcessTree();

    /**
     * Service retrieves custom fields of a process
     * @param processId ID of the process whose custom fields are being retrieved
     * @return list of found process fields
     */
    ServiceResponse<ProcessField> readCustomFields(Long processId);

    //////////CASE//////////

    /**
     * Service retrieves the case
     * @param caseId ID of the case
     * @return retrieved case
     */
    ServiceResponse<Case> readCase(Long caseId);

    /**
     * Service searches the cases by search filters
     * @param searchTaskCaseFilterServiceRequest object containing search filters
     * @param paging object containing paging parameters
     * @return list of found cases
     */
    ServiceResponse<Case> searchCases(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging);

    /**
     * Service starts the case
     * @param startCaseServiceParams request containing parameters required for starting the case
     * @return ID of the started case
     */
    ServiceResponse<SimpleIdentifier<Long>> startCase(CaseOperationsParams startCaseServiceParams);

    /**
     * Service stops the case specified by its' ID
     * @param stopCaseByIdServiceRequest request containing parameters required for stopping the case
     * @return information about the operation success
     */
    ServiceResponse stopCaseById(ServiceRequest<CaseOperationsParams> stopCaseByIdServiceRequest);

    /**
     * Service stops the case specified by its' lookup (multiple cases)
     * @param stopCaseByLookupServiceRequest request containing parameters required for stopping the case
     * @return information about the operation success
     */
    ServiceResponse stopCaseByLookup(ServiceRequest<CaseOperationsParams> stopCaseByLookupServiceRequest);

    /**
     * Service retrieves the user who is the next assignee
     * @param caseId ID of the case
     * @return retrieved user
     */
    ServiceResponse<User> readNextAssigneeCandidates(Long caseId);

    /**
     * Service retrieves the users who could be the previous assignees
     * @param caseId ID of the case
     * @return list of retrieved users
     */
    ServiceResponse<User> readPreviousAssigneeCandidates(Long caseId);

    /**
     * Service performs the case
     * @param performCaseByIdServiceRequest request containing parameters required for performing the case
     * @return information about the operation success
     */
    ServiceResponse performCaseById(ServiceRequest<CaseOperationsParams> performCaseByIdServiceRequest);

    /**
     * Service performs/rollbacks multiple cases
     * @param performCaseByLookupServiceRequest request containing process and entity lookups,
     *                  explanation, assignee and entities IDs,
     *                  note text, action (perform/rollback)
     * @return message about the success of the operation
     */
    ServiceResponse performCaseByLookup(ServiceRequest<CaseOperationsParams> performCaseByLookupServiceRequest);

    /**
     * Service rollbacks the case
     * @param rollbackCaseByIdServiceRequest request containing the data required for the case rollback
     * @return information about the operation success
     */
    ServiceResponse rollbackCaseById(ServiceRequest<CaseOperationsParams> rollbackCaseByIdServiceRequest);

    /**
     * Service rollbacks the cases with the given lookup
     * @param rollbackCaseByLookupServiceRequest request containing parameters required for the case rollback
     * @return information about the operation success
     */
    ServiceResponse rollbackCaseByLookup(ServiceRequest<CaseOperationsParams> rollbackCaseByLookupServiceRequest);

    /**
     * Service for double rollback (moving 2 activities backwards at a time)
     * @param doubleRollbackCaseServiceRequest request containing note text, task description and IDs of case, assignee and explanation
     * @return information about the success of the operation
     */
    ServiceResponse doubleRollbackCase(ServiceRequest<CaseOperationsParams> doubleRollbackCaseServiceRequest);

    /**
     * Service retrieves the team
     * @param caseId ID of the case
     * @return list of user actor objects
     */
    ServiceResponse<UserActor> readTeamForCase(Long caseId);

    /**
     * Service retrieves the user who is the next team member
     * @param caseId ID of the case
     * @return retrieved user
     */
    ServiceResponse<User> readNextTeamMemberForCase(Long caseId);

    /**
     * Service retrieves the user who is the previous team member
     * @param caseId ID of the case
     * @return retrieved user
     */
    ServiceResponse<User> readPreviousTeamMemberForCase(Long caseId);

    /**
     * Service for team assignment
     * @param assignTeamToCaseServiceRequest request containing the list of user actor objects
     * @return information about the operation success
     */
    ServiceResponse assignTeamToCase(ServiceRequest<UserActor> assignTeamToCaseServiceRequest);

    /**
     * Service searches the cases by actor
     * @param offset item to start listing from
     * @param limit number of items to list in the results
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @param active flag showing whether the case is active
     * @return list of found cases
     */
    ServiceResponse<Case> searchCasesThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active);

    /**
     * Service finds the cases with the currently logged in user in the actor set
     * @param offset item from which to start the results listing from
     * @param limit number of the resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @param active flag showing whether the case is active
     * @return found cases
     */
    ServiceResponse<Case> searchCasesBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active);

    /**
     * Service finds closed cases that currently logged in user has performed
     * @param offset item from which to start the results listing from
     * @param limit number of the resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @return found cases
     */
    ServiceResponse<Case> searchClosedCasesForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix);

    /**
     * Service finds all the active cases to which the object with the given ID and type is attached
     * @param entityTypeLookup defines the type of the attached object
     * @param entityId ID of the attached object
     * @return found cases
     */
    ServiceResponse<Case> searchActiveCases(String entityTypeLookup, Long entityId);

    /**
     * Service resets the case to its' first activity
     * by closing the currently active task and creating a new task (for the first activity)
     * @param resetCaseServiceRequest request containing parameters required for resetting the case
     * @return message about the success of the operation
     */
    ServiceResponse resetCase(ServiceRequest<CaseOperationsParams> resetCaseServiceRequest);

    /**
     * Update case description
     * @param updateCaseDescriptionServiceRequest request containing parameters required for updating the case description
     * @return message about the success of the operation
     */
    ServiceResponse updateCaseDescription(ServiceRequest<CaseOperationsParams> updateCaseDescriptionServiceRequest);

    //////////TASK//////////

    /**
     * Service retrieves the task
     * @param taskId ID of the task
     * @return found task
     */
    ServiceResponse<Task> readTask(Long taskId);

    /**
     * Service performs the task
     * @param performTaskServiceRequest request containing task action
     * @return active task
     */
    ServiceResponse<Task> performTask(ServiceRequest<TaskOperationsParams> performTaskServiceRequest);

    /**
     * Service rollbacks the task
     * @param rollbackTaskServiceRequest request containing task action
     * @return active task
     */
    ServiceResponse<Task> rollbackTask(ServiceRequest<TaskOperationsParams> rollbackTaskServiceRequest);

    /**
     * Service assigns the task
     * @param assignTaskServiceRequest request containing task action
     * @return information about the operation success
     */
    ServiceResponse assignTask(ServiceRequest<TaskOperationsParams> assignTaskServiceRequest);

    /**
     * Service retrieves the next team member
     * @param taskId ID of the task
     * @return user who is the next team member
     */
    ServiceResponse<User> readNextTeamMemberForTask(Long taskId);

    /**
     * Service retrieves the previous team member
     * @param taskId ID of the task
     * @return user who is the previous team member
     */
    ServiceResponse<User> readPreviousTeamMemberForTask(Long taskId);

    /**
     * Service retrieves the previous assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    ServiceResponse<User> readPreviousAssigneeCandidatesForTask(Long taskId);

    /**
     * Service retrieves the next assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    ServiceResponse<User> readNextAssigneeCandidatesForTask(Long taskId);

    /**
     * Service retrieves current assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    ServiceResponse<User> readCurrentAssigneeCandidatesForTask(Long taskId);

    /**
     * Service searches the task by search parameters
     * @param searchTaskCaseFilterServiceRequest request containing search parameters
     * @param paging object containing paging parameters
     * @return list of found tasks
     */
    ServiceResponse<Task> searchTasks(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging);

    ServiceResponse<Task> advancedSearchTasks(String queryParameters, Integer offset, Integer limit, String sortOrders, String type);

    /**
     * Service searches the task by actor
     * @param offset item to start listing from
     * @param limit number of items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @param active flag showing whether the task is active
     * @return list of found tasks
     */
    ServiceResponse<Task> searchTasksThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active);

    /**
     * Service finds the tasks with the currently logged in user in the actor set
     * @param offset item from which to start the results listing from
     * @param limit number of teh resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @param active flag showing whether the task is active
     * @return found tasks
     */
    ServiceResponse<Task> searchTasksBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active);

    /**
     * Service finds closed tasks that currently logged in user has performed
     * @param offset item from which to start the results listing from
     * @param limit number of the resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @return found tasks
     */
    ServiceResponse<Task> searchClosedTasksForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix);

    //////////ACTOR//////////

    /**
     * Service retrieves the actor
     * @param actorId ID of the actor
     * @return retrieved actor
     */
    ServiceResponse<Actor> readActor(Long actorId);

    /**
     * Service inserts the actor
     * @param data actor to be inserted
     * @return newly created actor
     */
    ServiceResponse<Actor> createActor(Actor data);

    /**
     * Service modifies the actor
     * @param actorId ID of the actor to be modified
     * @param data actor object with the properties values to update to
     * @return modified actor
     */
    ServiceResponse<Actor> updateActor(Long actorId, Actor data);

    /**
     * Service removes the actor
     * @param actorId ID of the actor
     * @return information about the success of the deletion operation
     */
    ServiceResponse deleteActor(Long actorId);

    /**
     * Service searches the actors
     *
     * @param term term to search by
     * @param processId ID of the process to search by
     * @param baseLookup base actor lookup
     * @return list of found actors
     */
    ServiceResponse<Actor> searchActors(String term, Long processId, String baseLookup);

    /**
     * Service checks if the user is in the actor set
     * @param actorLookup actor lookup
     * @return information whether the user is in the actor set
     */
    ServiceResponse<SimpleIdentifier<Boolean>> checkIfUserIsActor(String actorLookup);

    /**
     * Service finds actor by lookup
     * @param actorLookup actor lookup
     * @return found actor
     */
    ServiceResponse<Actor> readActorByLookup(String actorLookup);

    /**
     * Service assigns the user to the actor set
     * @param actorLookup actor lookup
     * @param userId ID of the user to be assigned to the actor set
     * @param caseId ID of the case for the actor
     * @return information whether the user is in actor set
     */
    ServiceResponse assignUserToActor(String actorLookup, Long userId, Long caseId);

    ServiceResponse<User> readActorCandidates(Long actorId);

    //////////STATUS//////////

    /**
     * Service retrieves the status
     * @param statusId ID of the status
     * @return retrieved status
     */
    ServiceResponse<Status> readStatus(Long statusId);

    /**
     * Service inserts the status
     * @param data status to be inserted
     * @return newly created status
     */
    ServiceResponse<Status> createStatus(Status data);

    /**
     * Service modifies the status
     * @param statusId ID of the status to be modified
     * @param data status to be modified
     * @return updated status
     */
    ServiceResponse<Status> updateStatus(Long statusId, Status data);

    /**
     * Service searches the statuses by process
     * @param processId ID of the process to search by
     * @return list of found statuses
     */
    ServiceResponse<Status> readStatusesByProcess(Long processId);

    //////////ACTIVITY//////////

    /**
     * Service searches the activities by process
     * @param processId ID of the process to search by
     * @return list of found activities
     */
    ServiceResponse<Activity> readActivitiesByProcess(Long processId);

    ServiceResponse<Activity> readPreviousActivities(Long caseId, Long taskId);

    ServiceResponse<User> readAssigneeCandidatesForActivity(Long activityId);

    //////////EXPLANATION//////////

    /**
     * Service searches the explanations
     * @param processId ID of the process to search by
     * @param term term to search by
     * @return list of found explanations
     */
    ServiceResponse<CaseExplanation> searchProcessExplanations(Long processId, String term);

    //////////CASE ACTIONS//////////

    /**
     * Service retrieves case action
     * @param caseId ID of the case whose action is being retrieved
     * @return found case action
     */
    ServiceResponse<CaseAction> readCaseActionsByCase(Long caseId);

    //////////CASE NOTE//////////

    /**
     * Service retrieves the note
     * @param caseNoteId ID of the case note
     * @return found note
     */
    ServiceResponse<CaseNote> readCaseNote(Long caseNoteId);

    /**
     * Service inserts the note
     * @param data note to be inserted
     * @return newly created note
     */
    ServiceResponse<CaseNote> createCaseNote(CaseNote data);

    /**
     * Service modifies the note
     * @param caseNoteId ID of the note to be modified
     * @param data the note to be modified
     * @return updated note
     */
    ServiceResponse<CaseNote> updateCaseNote(Long caseNoteId, CaseNote data);

    /**
     * Service removes the note
     * @param caseNoteId ID of the note
     * @return information about the deletion operation success
     */
    ServiceResponse deleteCaseNote(Long caseNoteId);

    /**
     * Service searches the notes by task
     * @param taskId ID of the task to search by
     * @return list of found notes
     */
    ServiceResponse<CaseNote> readCaseNotesByTask(Long taskId);

    /**
     * Service searches the notes by case
     * @param caseId ID of the case to search by
     * @return list of found notes
     */
    ServiceResponse<CaseNote> readCaseNotesByCase(Long caseId);

    /**
     * Service searches the case notes by case object
     * @param entityId ID of the entity
     * @param entityType type of entity
     * @return list of found notes
     */
    ServiceResponse<CaseNote> readCaseNotesByCaseObject(Long entityId, String entityType);

    //////////CASE ATTACHMENT//////////

    /**
     * Service retrieves the case attachment for downloading
     * @param caseAttachmentId ID of the attachment to be downloaded
     * @return object with file and its' info
     */
    ServiceResponse<FileInfo> downloadCaseAttachment(Long caseAttachmentId);

    /**
     * Service accepts an uploaded file and attaches it to the case
     * @param caseId ID of the case to attach the file to
     * @param name name of the attachment
     * @param description description of the attachment
     * @param filename name of the uploaded file
     * @param inputStream uploaded file stream
     * @return information about the success of the upload
     */
    ServiceResponse uploadCaseAttachment(Long caseId, String name, String description, String filename, InputStream inputStream);

    /**
     * Service removes the case attachment
     * @param caseAttachmentId ID of the attachment to be removed
     * @return information about the success of the deletion
     */
    ServiceResponse deleteCaseAttachment(Long caseAttachmentId);

    /**
     * Service retrieves the attachment by the specified case
     * @param caseId ID of the case whose attachments are being retrieved
     * @return found attachments
     */
    ServiceResponse<CaseAttachment> readCaseAttachmentsByCase(Long caseId);

    /**
     * Service modifies case attachment
     * @param caseAttachmentId ID of the attachment to be modified
     * @param caseAttachment object with the properties value sto update to
     * @return modified attachment
     */
    ServiceResponse<CaseAttachment> updateCaseAttachment(Long caseAttachmentId, CaseAttachment caseAttachment);

    ServiceResponse<Task> createWorkflowTaskForRecord(Long recordId, String entityLookup, Long processId);

    ServiceResponse<Task> deleteWorkflowTasksForRecord(Long recordId, Long entityId);

    ServiceResponse<Task> readWorkflowTasksForRecord(Long recordId, String entityLookup);

    ServiceResponse<Process> readAvailableProcessesForRecord(Long recordId, String entityLookup);

    ServiceResponse<Process> readProcessWithStartActivity(String processLookup);

    ServiceResponse<Process> readProcessWithActionActivity(Long activityActionId);

    ServiceResponse<ActivityAction> performActivityAction(Long activityActionId, Long entityId, Task taskDetails);
}