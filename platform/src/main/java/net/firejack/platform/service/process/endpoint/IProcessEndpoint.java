/**
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
package net.firejack.platform.service.process.endpoint;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Class describes process services endpoints
 */
@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.process.endpoint.IProcessEndpoint")
public interface IProcessEndpoint {

    //////////PROCESS//////////

    @WebMethod
    ServiceResponse<Process> readAllProcess();

    /**
     * Service retrieves the process
     * @param id ID of the process
     * @return found process
     */
	@WebMethod
	ServiceResponse<Process> readProcess(@WebParam(name = "id") Long id);

	/**
     * Service searches for the processes for the currently logged in user
     * @param lookupPrefix lookup prefix
     * @return resulting list
     */
	@WebMethod
	ServiceResponse<Process> readMyProcesses(
			@WebParam(name = "lookupPrefix") String lookupPrefix);

	/**
     * Service inserts the process
     * @param request request containing process to be inserted
     * @return newly created process
     */
	@WebMethod
	ServiceResponse<Process> createProcess(@WebParam(name = "request") ServiceRequest<Process> request);

    @WebMethod
    ServiceResponse<Process> createWorkflow(@WebParam(name = "request") ServiceRequest<Process> request);

	/**
     * Service modifies the process
     * @param id ID of the process to be modified
     * @param request request containing process to be modified
     * @return updated process
     */
	@WebMethod
	ServiceResponse<Process> updateProcess(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Process> request);

	/**
     * Service removes the process
     * @param id ID of the process to be removed
     * @return response about the deletion success
     */
	@WebMethod
	ServiceResponse<Process> deleteProcess(@WebParam(name = "id") Long id);

	/**
     * Service searches the processes
     * @param term term to search by
     * @return list of found processes
     */
	@WebMethod
	ServiceResponse<Process> searchAllProcesses(@WebParam(name = "term") String term, @WebParam(name = "humanProcess") Boolean humanProcess);

    @WebMethod
    ServiceResponse<RegistryNodeTree> readAllProcessTree();

    /**
     * Service retrieves custom fields of a process
     * @param processId ID of the process whose custom fields are being retrieved
     * @return list of found process fields
     */
	@WebMethod
	ServiceResponse<ProcessField> readCustomFields(@WebParam(name = "processId") Long processId);

    //////////CASE//////////

    /**
     * Service retrieves the case
     * @param caseId ID of the case
     * @return retrieved case
     */
    @WebMethod
    ServiceResponse<Case> readCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service searches the cases by search filters
     * @param searchTaskCaseFilterServiceRequest request containing search filters
     * @param offset paging offset (item from which to start listing)
     * @param limit paging limit (number of items per page)
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @return list of found cases
     */
    @WebMethod
    ServiceResponse<Case> searchCases(
            @WebParam(name = "request") ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest,
            @WebParam(name = "start") Integer offset,
            @WebParam(name = "limit") Integer limit,
            @WebParam(name = "sort") String sortColumn,
            @WebParam(name = "dir") SortOrder sortDirection);

    /**
     * Service starts the case
     * @param startCaseServiceRequest request containing parameters required for starting the case
     * @return ID of the started case
     */
    @WebMethod
    ServiceResponse<SimpleIdentifier<Long>> startCase(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> startCaseServiceRequest);

    /**
     * Service stops the case specified by its' ID
     * @param stopCaseByIdServiceRequest request containing parameters required for stopping the case
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse stopCaseById(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> stopCaseByIdServiceRequest);

    /**
     * Service stops the case specified by its' lookup (multiple cases)
     * @param stopCaseByLookupServiceRequest request containing parameters required for stopping the case
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse stopCaseByLookup(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> stopCaseByLookupServiceRequest);

    /**
     * Service retrieves the user who is the next assignee
     * @param caseId ID of the case
     * @return retrieved user
     */
    @WebMethod
    ServiceResponse<User> readNextAssigneeCandidates(@WebParam(name = "caseId") Long caseId);

    /**
     * Service retrieves the users who could be the previous assignees
     * @param caseId ID of the case
     * @return list of retrieved users
     */
    @WebMethod
    ServiceResponse<User> readPreviousAssigneeCandidates(@WebParam(name = "caseId") Long caseId);

    /**
     * Service performs the case
     * @param performCaseByIdServiceRequest request containing parameters required for performing the case
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse performCaseById(@WebParam(name = "request") ServiceRequest<CaseOperationsParams>  performCaseByIdServiceRequest);

    /**
     * Service performs/rollbacks multiple cases
     * @param performCaseByLookupServiceRequest request containing process and entity lookups,
     *                  explanation, assignee and entities IDs,
     *                  note text, action (perform/rollback)
     * @return message about the success of the operation
     */
    @WebMethod
    ServiceResponse performCaseByLookup(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> performCaseByLookupServiceRequest);

    /**
     * Service rollbacks the case
     * @param rollbackCaseByIdServiceRequest request containing the data required for the case rollback
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse rollbackCaseById(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> rollbackCaseByIdServiceRequest);

    /**
     * Service rollbacks the cases with the given lookup
     * @param rollbackCaseByLookupServiceRequest request containing parameters required for the case rollback
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse rollbackCaseByLookup(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> rollbackCaseByLookupServiceRequest);

    /**
     * Service for double rollback (moving 2 activities backwards at a time)
     * @param doubleRollbackCaseServiceRequest request containing note text, task description and IDs of case, assignee and explanation
     * @return information about the success of the operation
     */
    @WebMethod
    ServiceResponse doubleRollbackCase(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> doubleRollbackCaseServiceRequest);

    /**
     * Service retrieves the team
     * @param caseId ID of the case
     * @return list of user actor objects
     */
    @WebMethod
    ServiceResponse<UserActor> readTeamForCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service retrieves the user who is the next team member
     * @param caseId ID of the case
     * @return retrieved user
     */
    @WebMethod
    ServiceResponse<User> readNextTeamMemberForCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service retrieves the user who is the previous team member
     * @param caseId ID of the case
     * @return retrieved user
     */
    @WebMethod
    ServiceResponse<User> readPreviousTeamMemberForCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service for team assignment
     * @param assignTeamToCaseServiceRequest request containing the list of user actor objects
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse assignTeamToCase(@WebParam(name = "request") ServiceRequest<UserActor> assignTeamToCaseServiceRequest);

    /**
     * Update case description
     * @param updateCaseDescriptionServiceRequest request containing parameters required for updating the case description
     * @return message about the success of the operation
     */
    @WebMethod
    ServiceResponse updateCaseDescription(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> updateCaseDescriptionServiceRequest);

    /**
     * Service resets the case to its' first activity
     * by closing the currently active task and creating a new task (for the first activity)
     * @param resetCaseServiceRequest request containing parameters required for resetting the case
     * @return message about the success of the operation
     */
    @WebMethod
    ServiceResponse resetCase(@WebParam(name = "request") ServiceRequest<CaseOperationsParams> resetCaseServiceRequest);

    /**
     * Service finds all the active cases to which the object with the given ID and type is attached
     * @param entityTypeLookup defines the type of the attached object
     * @param entityId ID of the attached object
     * @return found cases
     */
    @WebMethod
    ServiceResponse<Case> searchActiveCases(@WebParam(name = "entityTypeLookup") String entityTypeLookup, @WebParam(name = "entityId") Long entityId);

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
    @WebMethod
    ServiceResponse<Case> searchCasesThroughActors(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix, @WebParam(name = "active") Boolean active);

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
    @WebMethod
    ServiceResponse<Case> searchCasesBelongingToUserActors(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix, @WebParam(name = "active") Boolean active);

    /**
     * Service finds closed cases that currently logged in user has performed
     * @param offset item from which to start the results listing from
     * @param limit number of the resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @return found cases
     */
    @WebMethod
    ServiceResponse<Case> searchClosedCasesForCurrentUser(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix);

    //////////TASK//////////

    /**
     * Service retrieves the task
     * @param id ID of the task
     * @return found task
     */
    @WebMethod
    ServiceResponse<Task> readTask(@WebParam(name = "id") Long id);

    /**
     * Service performs the task
     * @param performTaskServiceRequest request containing task action
     * @return active task
     */
    @WebMethod
    ServiceResponse<Task> performTask(@WebParam(name = "request") ServiceRequest<TaskOperationsParams>  performTaskServiceRequest);

    /**
     * Service rollbacks the task
     * @param rollbackTaskServiceRequest request containing task action
     * @return active task
     */
    @WebMethod
    ServiceResponse<Task> rollbackTask(@WebParam(name = "request") ServiceRequest<TaskOperationsParams> rollbackTaskServiceRequest);

    /**
     * Service assigns the task
     * @param assignTaskServiceRequest request containing task action
     * @return information about the operation success
     */
    @WebMethod
    ServiceResponse assignTask(@WebParam(name = "request") ServiceRequest<TaskOperationsParams> assignTaskServiceRequest);

    /**
     * Service retrieves the next team member
     * @param taskId ID of the task
     * @return user who is the next team member
     */
    @WebMethod
    ServiceResponse<User> readNextTeamMemberForTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service retrieves the previous team member
     * @param taskId ID of the task
     * @return user who is the previous team member
     */
    @WebMethod
    ServiceResponse<User> readPreviousTeamMemberForTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service retrieves the previous assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    @WebMethod
    ServiceResponse<User> readPreviousAssigneeCandidatesForTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service retrieves the next assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    @WebMethod
    ServiceResponse<User> readNextAssigneeCandidatesForTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service retrieves current assignee candidates
     * @param taskId ID of the task
     * @return list of users who are candidates
     */
    @WebMethod
    ServiceResponse<User> readCurrentAssigneeCandidatesForTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service searches the task by search parameters
     * @param searchTaskCaseFilterServiceRequest request containing search parameters
     * @param offset paging offset (item from which to start listing)
     * @param limit paging limit (number of items per page)
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @return list of found tasks
     */
    @WebMethod
    ServiceResponse<Task> searchTasks(
            @WebParam(name = "request") ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest,
            @WebParam(name = "start") Integer offset,
            @WebParam(name = "limit") Integer limit,
            @WebParam(name = "sort") String sortColumn,
            @WebParam(name = "dir") SortOrder sortDirection);

    @WebMethod
    ServiceResponse<Task> advancedSearchTasks(
            @WebParam(name = "queryParameters") String queryParameters,
            @WebParam(name = "offset") Integer offset,
            @WebParam(name = "limit") Integer limit,
            @WebParam(name = "sortOrders") String sortOrders,
            @WebParam(name = "type") String type);

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
    @WebMethod
    ServiceResponse<Task> searchTasksThroughActors(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix, @WebParam(name = "active") Boolean active);

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
    @WebMethod
    ServiceResponse<Task> searchTasksBelongingToUserActors(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix, @WebParam(name = "active") Boolean active);

    /**
     * Service finds closed tasks that currently logged in user has performed
     * @param offset item from which to start the results listing from
     * @param limit number of the resulting items to be listed
     * @param sortColumn column to sort by
     * @param sortDirection sorting direction
     * @param lookupPrefix lookup prefix
     * @return found tasks
     */
    @WebMethod
    ServiceResponse<Task> searchClosedTasksForCurrentUser(@WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit, @WebParam(name = "sortColumn") String sortColumn, @WebParam(name = "sortDirection") String sortDirection, @WebParam(name = "lookupPrefix") String lookupPrefix);

    @WebMethod
    ServiceResponse<Task> readWorkflowTasksForRecord(@WebParam(name = "recordId") Long recordId, @WebParam(name = "entityLookup") String entityLookup);

    @WebMethod
    ServiceResponse<Process> readAvailableProcessesForRecord(
            @WebParam(name = "recordId") Long recordId,
            @WebParam(name = "entityLookup") String entityLookup);

    //////////ACTOR//////////

    /**
     * Service retrieves the actor
     * @param id ID of the actor
     * @return retrieved actor
     */
    @WebMethod
    ServiceResponse<Actor> readActor(@WebParam(name = "id") Long id);

    /**
     * Service inserts the actor
     * @param request request containing actor to be inserted
     * @return newly created actor
     */
    @WebMethod
	ServiceResponse<Actor> createActor(@WebParam(name = "request") ServiceRequest<Actor> request);

    /**
     * Service modifies the actor
     * @param id ID of the actor to be modified
     * @param request request containing actor object with the properties values to update to
     * @return modified actor
     */
    @WebMethod
	ServiceResponse<Actor> updateActor(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Actor> request);

    /**
     * Service removes the actor
     * @param id ID of the actor
     * @return information about the success of the deletion operation
     */
    @WebMethod
	ServiceResponse<Actor> deleteActor(@WebParam(name = "id") Long id);

    /**
     * This service searches all actors by search term and by processId or/and by base lookup
     * @param term term to search by
     * @param processId ID of the process to search by
     * @return list of found actors
     */
    @WebMethod
	ServiceResponse<Actor> searchAllActors(
            @WebParam(name = "term") String term,
            @WebParam(name = "processId") Long processId,
            @WebParam(name = "baseLookup") String baseLookup);

    /**
     * Service checks if the user is in the actor set
     * @param actorLookup actor lookup
     * @return information whether the user is in the actor set
     */
    @WebMethod
    ServiceResponse<SimpleIdentifier<Boolean>> checkIfUserIsActor(@WebParam(name = "actorLookup") String actorLookup);

    /**
     * Service finds actor by lookup
     * @param actorLookup actor lookup
     * @return found actor
     */
    @WebMethod
    ServiceResponse<Actor> readActorByLookup(@WebParam(name = "actorLookup") String actorLookup);

    /**
     * Service assigns the user to the actor set
     * @param actorLookup actor lookup
     * @param userId ID of the user to be assigned to the actor set
     * @param caseId ID of the case for the actor
     * @return information whether the user is in actor set
     */
    @WebMethod
    ServiceResponse assignUserToActor(@WebParam(name = "actorLookup") String actorLookup, @WebParam(name = "userId") Long userId, @WebParam(name = "caseId") Long caseId);

    @WebMethod
    ServiceResponse<User> readActorCandidates(@WebParam(name = "actorId") Long actorId);

    //////////STATUS//////////

    /**
     * Service retrieves the status
     * @param id ID of the status
     * @return retrieved status
     */
    @WebMethod
    ServiceResponse<Status> readStatus(@WebParam(name = "id") Long id);

    /**
     * Service inserts the status
     * @param request request containing status to be inserted
     * @return newly created status
     */
    @WebMethod
	ServiceResponse<Status> createStatus(@WebParam(name = "request") ServiceRequest<Status> request);

    /**
     * Service modifies the status
     * @param id ID of the status to be modified
     * @param request request containing status to be modified
     * @return updated status
     */
    @WebMethod
	ServiceResponse<Status> updateStatus(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Status> request);

    /**
     * Service searches the statuses by process
     * @param processId ID of the process to search by
     * @return list of found statuses
     */
    @WebMethod
    ServiceResponse<Status> readStatusesByProcess(@WebParam(name = "processId") Long processId);

    //////////ACTIVITY//////////

    /**
     * Service searches the activities by process
     * @param processId ID of the process to search by
     * @return list of found activities
     */
    @WebMethod
    ServiceResponse<Activity> readActivitiesByProcess(@WebParam(name = "processId") Long processId);

    @WebMethod
    ServiceResponse<Activity> readPreviousActivities(
            @WebParam(name = "caseId") Long caseId, @WebParam(name = "taskId") Long taskId);

    @WebMethod
    ServiceResponse<User> readAssigneeCandidatesForActivity(@WebParam(name = "activityId") Long activityId);

    //////////EXPLANATION//////////

    /**
     * Service searches the explanations
     * @param processId ID of the process to search by
     * @param term term to search by
     * @return list of found explanations
     */
    @WebMethod
    ServiceResponse<CaseExplanation> searchProcessExplanations(@WebParam(name = "processId") Long processId, @WebParam(name = "term") String term);

    //////////CASE ACTIONS//////////

    /**
     * Service retrieves case action
     * @param caseId ID of the case whose action is being retrieved
     * @return found case action
     */
    @WebMethod
    ServiceResponse<CaseAction> readCaseActionsByCase(@WebParam(name = "caseId") Long caseId);

    //////////CASE NOTE//////////

    /**
     * Service retrieves the note
     * @param id ID of the case note
     * @return found note
     */
    @WebMethod
    ServiceResponse<CaseNote> readCaseNote(@WebParam(name = "id") Long id);

    /**
     * Service inserts the note
     * @param request reequest containing note to be inserted
     * @return newly created note
     */
    @WebMethod
	ServiceResponse<CaseNote> createCaseNote(@WebParam(name = "request") ServiceRequest<CaseNote> request);

    /**
     * Service modifies the note
     * @param caseNoteId ID of the note to be modified
     * @param request request containing the note to be modified
     * @return updated note
     */
    @WebMethod
	ServiceResponse<CaseNote> updateCaseNote(@WebParam(name = "caseNoteId") Long caseNoteId, @WebParam(name = "request") ServiceRequest<CaseNote> request);

    /**
     * Service removes the note
     * @param caseNoteId ID of the note
     * @return information about the deletion operation success
     */
    @WebMethod
	ServiceResponse<CaseNote> deleteCaseNote(@WebParam(name = "caseNoteId") Long caseNoteId);

    /**
     * Service searches the notes by task
     * @param taskId ID of the task to search by
     * @return list of found notes
     */
    @WebMethod
    ServiceResponse<CaseNote> readCaseNotesByTask(@WebParam(name = "taskId") Long taskId);

    /**
     * Service searches the notes by case
     * @param caseId ID of the case to search by
     * @return list of found notes
     */
    @WebMethod
    ServiceResponse<CaseNote> readCaseNotesByCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service searches the case notes by case object
     * @param entityId ID of the entity
     * @param entityType type of entity
     * @return list of found notes
     */
    @WebMethod
    ServiceResponse<CaseNote> readCaseNotesByCaseObject(@WebParam(name = "entityId") Long entityId, @WebParam(name = "entityType") String entityType);

    //////////CASE ATTACHMENT//////////

    /**
     * Service retrieves the case attachment for downloading
     * @param caseAttachmentId ID of the attachment to be downloaded
     * @return object with file and its' info
     */
    @WebMethod(operationName = "downloadCaseAttachment")
    ServiceResponse<FileInfo> downloadCaseAttachmentWS(@WebParam(name = "caseAttachmentId") Long caseAttachmentId);

    /**
     * Service accepts an uploaded file and attaches it to the case
     * @param caseId ID of the case to attach the file to
     * @param description description of the attachment
     * @param info object with file and its' info
     * @return information about the success of the upload
     */
    @WebMethod
    ServiceResponse uploadCaseAttachment(@WebParam(name = "caseId") Long caseId, @WebParam(name = "description") String description, @WebParam(name = "info") FileInfo info);

    /**
     * Service removes the case attachment
     * @param caseAttachmentId ID of the attachment to be removed
     * @return information about the success of the deletion
     */
    @WebMethod
    ServiceResponse deleteCaseAttachment(@WebParam(name = "caseAttachmentId") Long caseAttachmentId);

    /**
     * Service retrieves the attachment by the specified case
     * @param caseId ID of the case whose attachments are being retrieved
     * @return found attachments
     */
    @WebMethod
    ServiceResponse<CaseAttachment> readCaseAttachmentsByCase(@WebParam(name = "caseId") Long caseId);

    /**
     * Service modifies case attachment
     * @param id ID of the attachment to be modified
     * @param request request containing case attachment object with the properties value sto update to
     * @return modified attachment
     */
    @WebMethod
    ServiceResponse<CaseAttachment> updateCaseAttachment(
            @WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<CaseAttachment> request);

    @WebMethod
    ServiceResponse<Task> createWorkflowTaskForRecord(
            @WebParam(name = "recordId") Long recordId,
            @WebParam(name = "entityLookup") String entityLookup,
            @WebParam(name = "processId") Long processId);

    @WebMethod
    ServiceResponse<Task> deleteWorkflowTasksForRecord(
            @WebParam(name = "recordId") Long recordId,
            @WebParam(name = "entityId") Long entityId);

    @WebMethod
    ServiceResponse<Process> readProcessWithStartActivity(
            @WebParam(name = "processLookup") String processLookup);

    @WebMethod
    ServiceResponse<Process> readProcessWithActionActivity(
            @WebParam(name = "activityActionId") Long activityActionId);

    @WebMethod
    ServiceResponse<ActivityAction> performActivityAction(
            @WebParam(name = "activityActionId") Long activityActionId,
            @WebParam(name = "entityid") Long entityId, @WebParam(name = "request") ServiceRequest<Task> request);

}
