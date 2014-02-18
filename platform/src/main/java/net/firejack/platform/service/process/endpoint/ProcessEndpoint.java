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

package net.firejack.platform.service.process.endpoint;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class is an implementation of net.firejack.platform.service.process.endpoint.IProcessEndpoint
 */
@Component("processService")
@Path("process")
public class ProcessEndpoint implements IProcessEndpoint {

    //////////PROCESS//////////

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readAllProcess() {
        return OPFEngine.ProcessService.readAllProcess();
    }

    @GET
    @Path("/{processId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readProcess(@PathParam("processId") Long id) {
        return OPFEngine.ProcessService.readProcess(id);
    }

    @GET
    @Path("/my")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readMyProcesses(
            @QueryParam("lookupPrefix") String lookupPrefix) {
        return OPFEngine.ProcessService.readMyProcesses(lookupPrefix);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> createProcess(ServiceRequest<Process> request) {
        return OPFEngine.ProcessService.createProcess(request.getData());
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> updateProcess (
            @PathParam("id") Long id, ServiceRequest<Process> request) {
        return OPFEngine.ProcessService.updateProcess(id, request.getData());
    }

    @DELETE
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> deleteProcess(@PathParam(value = "id") Long id) {
        return OPFEngine.ProcessService.deleteProcess(id);
    }

    @GET
    @Path("/search/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> searchAllProcesses(@QueryParam("term") String term, @QueryParam("humanProcess") Boolean humanProcess) {
        return OPFEngine.ProcessService.searchProcesses(term, humanProcess);
    }

    @GET
    @Path("/tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<RegistryNodeTree> readAllProcessTree() {
        return OPFEngine.ProcessService.readAllProcessTree();
    }

        //////////CUSTOM FIELDS//////////

    @GET
    @Path("/custom-fields/{processId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<ProcessField> readCustomFields(@PathParam("processId") Long processId) {
        return OPFEngine.ProcessService.readCustomFields(processId);
    }

    //////////CASE//////////

    @GET
    @Path("/case/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> readCase(@PathParam("id") Long id) {
        return OPFEngine.ProcessService.readCase(id);
    }

    @POST
    @Path("/case/search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> searchCases(
            ServiceRequest<SearchTaskCaseFilter> request,
            @QueryParam("start") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn,
            @QueryParam("dir") SortOrder sortDirection) {
        Paging paging = new Paging(offset, limit, sortColumn, sortDirection);
        return OPFEngine.ProcessService.searchCases(request, paging);
    }

    @POST
    @Path("/case/start")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<SimpleIdentifier<Long>> startCase(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.startCase(request.getData());
    }

    @POST
    @Path("/case/stop-by-id")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse stopCaseById(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.stopCaseById(request);
    }

    @POST
    @Path("/case/stop-by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse stopCaseByLookup(ServiceRequest<CaseOperationsParams> stopCaseByLookupServiceRequest) {
        return OPFEngine.ProcessService.stopCaseByLookup(stopCaseByLookupServiceRequest);
    }

    @GET
    @Path("/case/next-assignee-candidates/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readNextAssigneeCandidates(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readNextAssigneeCandidates(caseId);
    }

    @GET
    @Path("/case/previous-assignee-candidates/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidates(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readPreviousAssigneeCandidates(caseId);
    }

    @POST
    @Path("/case/perform-by-id")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse performCaseById(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.performCaseById(request);
    }

    @POST
    @Path("/case/perform-by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse performCaseByLookup(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.performCaseByLookup(request);
    }

    @POST
    @Path("/case/rollback-by-id")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse rollbackCaseById(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.rollbackCaseById(request);
    }

    @POST
    @Path("/case/rollback-by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse rollbackCaseByLookup(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.rollbackCaseByLookup(request);
    }

    @POST
    @Path("/case/double-rollback")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse doubleRollbackCase(ServiceRequest<CaseOperationsParams> doubleRollbackCaseServiceRequest) {
        return OPFEngine.ProcessService.doubleRollbackCase(doubleRollbackCaseServiceRequest);
    }

    @GET
    @Path("/case/read-team/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<UserActor> readTeamForCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readTeamForCase(caseId);
    }

    @GET
    @Path("/case/read-next-team-member/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readNextTeamMemberForCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readNextTeamMemberForCase(caseId);
    }

    @GET
    @Path("/case/read-previous-team-member/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readPreviousTeamMemberForCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readPreviousTeamMemberForCase(caseId);
    }

    @POST
    @Path("/case/assign-team")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse assignTeamToCase(ServiceRequest<UserActor> request) {
        return OPFEngine.ProcessService.assignTeamToCase(request);
    }

    @PUT
    @Path("/case/update-description")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse updateCaseDescription(ServiceRequest<CaseOperationsParams> request) {
        return OPFEngine.ProcessService.updateCaseDescription(request);
    }

    @PUT
    @Path("/case/reset")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse resetCase(ServiceRequest<CaseOperationsParams> resetCaseServiceRequest) {
        return OPFEngine.ProcessService.resetCase(resetCaseServiceRequest);
    }

    @GET
    @Path("/case/find-active")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> searchActiveCases(@QueryParam("entityTypeLookup") String entityTypeLookup, @QueryParam("entityId") Long entityId) {
        return OPFEngine.ProcessService.searchActiveCases(entityTypeLookup, entityId);
    }

    @GET
    @Path("/case/find-through-actors")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> searchCasesThroughActors(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix, @QueryParam("active") Boolean active) {
        return OPFEngine.ProcessService.searchCasesThroughActors(offset, limit, sortColumn, sortDirection, lookupPrefix, active);
    }

    @GET
    @Path("/case/find-belonging-to-user-actor")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> searchCasesBelongingToUserActors(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix, @QueryParam("active") Boolean active) {
        return OPFEngine.ProcessService.searchCasesBelongingToUserActors(offset, limit, sortColumn, sortDirection, lookupPrefix, active);
    }

    @GET
    @Path("/case/find-closed-cases-for-current-user")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Case> searchClosedCasesForCurrentUser(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix) {
        return OPFEngine.ProcessService.searchClosedCasesForCurrentUser(offset, limit, sortColumn, sortDirection, lookupPrefix);
    }

    //////////TASK//////////

    @GET
    @Path("/task/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Task> readTask(@PathParam("id") Long id) {
        return OPFEngine.ProcessService.readTask(id);
    }

    @POST
    @Path("/task/perform")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> performTask(ServiceRequest<TaskOperationsParams> performTaskServiceRequest) {
        return OPFEngine.ProcessService.performTask(performTaskServiceRequest);
    }

    @POST
    @Path("/task/rollback")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> rollbackTask(ServiceRequest<TaskOperationsParams> rollbackTaskServiceRequest) {
        return OPFEngine.ProcessService.rollbackTask(rollbackTaskServiceRequest);
    }

    @POST
    @Path("/task/assign")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse assignTask(ServiceRequest<TaskOperationsParams> request) {
        return OPFEngine.ProcessService.assignTask(request);
    }

    @GET
    @Path("/task/read-next-team-member/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readNextTeamMemberForTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readNextTeamMemberForTask(taskId);
    }

    @GET
    @Path("/task/read-previous-team-member/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readPreviousTeamMemberForTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readPreviousTeamMemberForTask(taskId);
    }

    @GET
    @Path("/task/previous-assignee-candidates/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidatesForTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readPreviousAssigneeCandidatesForTask(taskId);
    }

    @GET
    @Path("/task/next-assignee-candidates/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readNextAssigneeCandidatesForTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readNextAssigneeCandidatesForTask(taskId);
    }

    @GET
    @Path("/task/current-assignee-candidates/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readCurrentAssigneeCandidatesForTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readCurrentAssigneeCandidatesForTask(taskId);
    }

    @POST
    @Path("/task/search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> searchTasks(
            ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest,
            @QueryParam("start") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn,
            @QueryParam("dir") SortOrder sortDirection) {
        Paging paging = new Paging(offset, limit, sortColumn, sortDirection);
        return OPFEngine.ProcessService.searchTasks(searchTaskCaseFilterServiceRequest, paging);
    }

    @GET
    @Path("/task/advanced-search")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> advancedSearchTasks(
            @QueryParam("queryParameters") String queryParameters,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sortOrders") String sortOrders,
            @QueryParam("type") String type) {
        return OPFEngine.ProcessService.advancedSearchTasks(queryParameters,  offset,  limit,  sortOrders, type);
    }

    @GET
    @Path("/task/find-through-actors")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> searchTasksThroughActors(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix, @QueryParam("active") Boolean active) {
        return OPFEngine.ProcessService.searchTasksThroughActors(offset, limit, sortColumn, sortDirection, lookupPrefix, active);
    }

    @GET
    @Path("/task/find-belonging-to-user-actor")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> searchTasksBelongingToUserActors(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix, @QueryParam("active") Boolean active) {
        return OPFEngine.ProcessService.searchTasksBelongingToUserActors(offset, limit, sortColumn, sortDirection, lookupPrefix, active);
    }

    @GET
    @Path("/task/find-closed-tasks-for-current-user")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> searchClosedTasksForCurrentUser(
            @QueryParam("start") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn, @QueryParam("dir") String sortDirection,
            @QueryParam("lookupPrefix") String lookupPrefix) {
        return OPFEngine.ProcessService.searchClosedTasksForCurrentUser(offset, limit, sortColumn, sortDirection, lookupPrefix);
    }

    //////////ACTOR//////////

    @GET
    @Path("/actor/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> readActor(@PathParam("id") Long id) {
        return OPFEngine.ProcessService.readActor(id);
    }

    @POST
    @Path("/actor/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> createActor(ServiceRequest<Actor> request) {
        return OPFEngine.ProcessService.createActor(request.getData());
    }

    @PUT
    @Path("/actor/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> updateActor (
            @PathParam("id") Long id, ServiceRequest<Actor> request) {
        return OPFEngine.ProcessService.updateActor(id, request.getData());
    }

    @DELETE
    @Path("/actor/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> deleteActor(@PathParam(value = "id") Long id) {
        return OPFEngine.ProcessService.deleteActor(id);
    }

    @GET
    @Path("/actor/search/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> searchAllActors(
            @QueryParam("term") String term, @QueryParam("processId") Long processId,
            @QueryParam("baseLookup") String baseLookup) {
        return OPFEngine.ProcessService.searchActors(term, processId, baseLookup);
    }

    @GET
    @Path("/actor/check-user-is-actor/{actorLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> checkIfUserIsActor(@PathParam("actorLookup") String actorLookup) {
        return OPFEngine.ProcessService.checkIfUserIsActor(actorLookup);
    }

    @GET
    @Path("/actor/actor-by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Actor> readActorByLookup(@QueryParam("actorLookup") String actorLookup) {
        return OPFEngine.ProcessService.readActorByLookup(actorLookup);
    }

    @PUT
    @Path("/actor/assign-user-to-actor")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse assignUserToActor(
            @QueryParam("actorLookup") String actorLookup, @QueryParam("userId") Long userId,
            @QueryParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.assignUserToActor(actorLookup, userId, caseId);
    }

    @GET
    @Path("/actor/read-candidate-users")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readActorCandidates(@QueryParam("actorId") Long actorId) {
        return OPFEngine.ProcessService.readActorCandidates(actorId);
    }

    //////////STATUS//////////

    @GET
    @Path("/status/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Status> readStatus(@PathParam("id") Long id) {
        return OPFEngine.ProcessService.readStatus(id);
    }

    @POST
    @Path("/status/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Status> createStatus(ServiceRequest<Status> request) {
        return OPFEngine.ProcessService.createStatus(request.getData());
    }

    @PUT
    @Path("/status/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Status> updateStatus (
            @PathParam("id") Long id, ServiceRequest<Status> request) {
        return OPFEngine.ProcessService.updateStatus(id, request.getData());
    }

    @GET
    @Path("/status/process/{processId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Status> readStatusesByProcess(@PathParam("processId") Long processId) {
        return OPFEngine.ProcessService.readStatusesByProcess(processId);
    }

    //////////ACTIVITY//////////

    @GET
    @Path("/activity/process/{processId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Activity> readActivitiesByProcess(@PathParam("processId") Long processId) {
        return OPFEngine.ProcessService.readActivitiesByProcess(processId);
    }

    @GET
    @Path("/activity/previous")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Activity> readPreviousActivities(@QueryParam("caseId") Long caseId, @QueryParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readPreviousActivities(caseId, taskId);
    }

    @GET
    @Path("/activity/assignee-candidates/{activityId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readAssigneeCandidatesForActivity(@PathParam("activityId") Long activityId) {
        return OPFEngine.ProcessService.readAssigneeCandidatesForActivity(activityId);
    }

    //////////PROCESS EXPLANATIONS//////////

    @GET
    @Path("/explanation/search/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseExplanation> searchProcessExplanations(@QueryParam("processId") Long processId, @QueryParam("term") String term) {
        return OPFEngine.ProcessService.searchProcessExplanations(processId, term);
    }

    //////////CASE ACTION//////////

    @GET
    @Path("/action/case/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<CaseAction> readCaseActionsByCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readCaseActionsByCase(caseId);
    }

    //////////CASE NOTE//////////

    @GET
    @Path("/note/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> readCaseNote(@PathParam("id") Long id) {
        return OPFEngine.ProcessService.readCaseNote(id);
    }

    @POST
    @Path("/note/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> createCaseNote(ServiceRequest<CaseNote> request) {
        return OPFEngine.ProcessService.createCaseNote(request.getData());
    }

    @PUT
    @Path("/note/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> updateCaseNote (
            @PathParam("id") Long id, ServiceRequest<CaseNote> request) {
        return OPFEngine.ProcessService.updateCaseNote(id, request.getData());
    }

    @DELETE
    @Path("/note/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> deleteCaseNote(@PathParam(value = "id") Long id) {
        return OPFEngine.ProcessService.deleteCaseNote(id);
    }

    @GET
    @Path("/note/task/{taskId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> readCaseNotesByTask(@PathParam("taskId") Long taskId) {
        return OPFEngine.ProcessService.readCaseNotesByTask(taskId);
    }

    @GET
    @Path("/note/case/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readCaseNotesByCase(caseId);
    }

    @GET
    @Path("/note/case-object/entity/{entityType}/{entityId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCaseObject(@PathParam("entityId") Long entityId, @PathParam("entityType") String entityType) {
        return OPFEngine.ProcessService.readCaseNotesByCaseObject(entityId, entityType);
    }

    //////////CASE ATTACHMENT//////////

    @GET
    @Path("/attachment/download/{caseAttachmentId}")
    @Produces(MediaType.WILDCARD)
    //todo: not valid in terms of OPF action abstraction
    public Response downloadCaseAttachment(@PathParam("caseAttachmentId") Long caseAttachmentId) {
	    ServiceResponse<FileInfo> response = OPFEngine.ProcessService.downloadCaseAttachment(caseAttachmentId);
	    if (response.isSuccess()) {
	        FileInfo fileInfo = response.getItem();
		    ContentDisposition cd = ContentDisposition.type("file").fileName(fileInfo.getFilename()).build();
            Response.ResponseBuilder responseBuilder = Response.ok(fileInfo.getStream());
            responseBuilder = responseBuilder.header("Content-Disposition", cd);
            responseBuilder = responseBuilder.header("OPF-Filename", fileInfo.getFilename());
            return responseBuilder.build();
	    }
	    return null;
    }

	public ServiceResponse<FileInfo> downloadCaseAttachmentWS( Long caseAttachmentId) {
		return OPFEngine.ProcessService.downloadCaseAttachment(caseAttachmentId);
	}
       
    @POST
    @Path("/attachment/upload/{caseId}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //todo: not valid in terms of OPF action abstraction
    public Response uploadCaseAttachment(
            @PathParam("caseId") Long caseId,
            @FormDataParam("name") String name,
            @FormDataParam("description") String description,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition)
            throws IOException {

        String originalFilename = fileDisposition.getFileName();
        ServiceResponse serviceResponse = OPFEngine.ProcessService.uploadCaseAttachment(caseId, name, description, originalFilename, inputStream);

        ObjectMapper mapper = new ObjectMapper();
        String responderData = mapper.writeValueAsString(serviceResponse);

        return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
    }

    //todo: not valid in terms of OPF action abstraction
	public ServiceResponse uploadCaseAttachment(Long caseId, String description, FileInfo info) {
		return OPFEngine.ProcessService.uploadCaseAttachment(caseId, info.getFilename(), description, info.getOrgFilename(), info.getStream());
	}

	@DELETE
    @Path("/attachment/{caseAttachmentId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteCaseAttachment(@PathParam("caseAttachmentId") Long caseAttachmentId) {
        return OPFEngine.ProcessService.deleteCaseAttachment(caseAttachmentId);
    }

    @GET
    @Path("/attachment/case/{caseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<CaseAttachment> readCaseAttachmentsByCase(@PathParam("caseId") Long caseId) {
        return OPFEngine.ProcessService.readCaseAttachmentsByCase(caseId);
    }

    @PUT
    @Path("/attachment/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<CaseAttachment> updateCaseAttachment(
            @PathParam("id") Long id, ServiceRequest<CaseAttachment> request) {
        return OPFEngine.ProcessService.updateCaseAttachment(id, request.getData());
    }

    @POST
    @Path("/workflow")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> createWorkflow(ServiceRequest<Process> request) {
        return OPFEngine.ProcessService.createWorkflow(request.getData());
    }

    @POST
    @Path("/workflow/task/record")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> createWorkflowTaskForRecord(
            @QueryParam("recordId") Long recordId,
            @QueryParam("entityLookup") String entityLookup,
            @QueryParam("processId") Long processId) {
        return OPFEngine.ProcessService.createWorkflowTaskForRecord(recordId, entityLookup, processId);
    }

    @DELETE
    @Path("/workflow/task/record")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> deleteWorkflowTasksForRecord(
            @QueryParam("recordId") Long recordId,
            @QueryParam("entityId") Long entityId) {
        return OPFEngine.ProcessService.deleteWorkflowTasksForRecord(recordId, entityId);
    }

    @GET
    @Path("/workflow/task/record/{entityLookup}/{recordId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Task> readWorkflowTasksForRecord(
            @PathParam("recordId") Long recordId,
            @PathParam("entityLookup") String entityLookup) {
        return OPFEngine.ProcessService.readWorkflowTasksForRecord(recordId, entityLookup);
    }

    @GET
    @Path("/workflow/available/record/{entityLookup}/{recordId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readAvailableProcessesForRecord(
            @PathParam("recordId") Long recordId,
            @PathParam("entityLookup") String entityLookup) {
        return OPFEngine.ProcessService.readAvailableProcessesForRecord(recordId, entityLookup);
    }

    @GET
    @Path("/workflow/activity/start/{processLookup}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readProcessWithStartActivity(
            @PathParam("processLookup") String processLookup) {
        return OPFEngine.ProcessService.readProcessWithStartActivity(processLookup);
    }

    @GET
    @Path("/workflow/activity/by-activity-action/{activityActionId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Process> readProcessWithActionActivity(
            @PathParam("activityActionId") Long activityActionId) {
        return OPFEngine.ProcessService.readProcessWithActionActivity(activityActionId);
    }

    @PUT
    @Path("/workflow/perform-activity-action/{activityActionId}/{entityId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ActivityAction> performActivityAction(
            @PathParam("activityActionId") Long activityActionId,
            @PathParam("entityId") Long entityId, ServiceRequest<Task> request) {
        return OPFEngine.ProcessService.performActivityAction(activityActionId, entityId, request.getData());
    }

}