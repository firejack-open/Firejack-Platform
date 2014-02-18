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


import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class is an implementation of net.firejack.platform.api.process.IProcessService
 * The services are invoked in RESTful manner
 */
public class ProcessServiceProxy extends AbstractServiceProxy implements IProcessService {

    public ProcessServiceProxy(Class[] classes) {
        super(classes);
    }

    @Override
    public String getServiceUrlSuffix() {
        return "/process";
    }

    //////////PROCESS//////////


    @Override
    public ServiceResponse<Process> readAllProcess() {
        return get("");
    }

    @Override
    public ServiceResponse<Process> readProcess(Long processId) {
        StringBuilder urlBuilder = new StringBuilder("/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Process> createProcess(Process process) {
        return post("", process);
    }

    @Override
    public ServiceResponse<Process> createWorkflow(Process workflow) {
        return post("/workflow", workflow);
    }

    @Override
    public ServiceResponse<Process> updateProcess(Long processId, Process process) {
        StringBuilder urlBuilder = new StringBuilder("/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return put(urlBuilder.toString(), process);
    }

    @Override
    public ServiceResponse<Process> readMyProcesses(String lookupPrefix) {
        return get("/my", "lookupPrefix", lookupPrefix);
    }

    @Override
    public ServiceResponse deleteProcess(Long processId) {
        StringBuilder urlBuilder = new StringBuilder("/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return delete(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Process> searchProcesses(String term, Boolean humanProcess) {
        if (humanProcess == null) {
            return get("/search", "term", term);
        } else {
            return get("/search", "term", term, "humanProcess", humanProcess);
        }
    }

    @Override
    public ServiceResponse<RegistryNodeTree> readAllProcessTree() {
        return get("/tree");
    }

    //////////CUSTOM FIELDS//////////

    @Override
    public ServiceResponse<ProcessField> readCustomFields(Long processId) {
        StringBuilder urlBuilder = new StringBuilder("/custom-fields/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return get(urlBuilder.toString());
    }

    //////////CASE//////////

    @Override
    public ServiceResponse<Case> readCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Case> searchCases(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging) {
        if (paging != null) {
            return post2("/case/search", searchTaskCaseFilterServiceRequest.getData(), "start", paging.getOffset(), "limit", paging.getLimit(), "sort", paging.getSortColumn(), "dir", paging.getSortDirection());
        }
        return post2("/case/search", searchTaskCaseFilterServiceRequest.getData());
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Long>> startCase(CaseOperationsParams startCaseServiceParams) {
        return post2("/case/start", startCaseServiceParams);
    }

    @Override
    public ServiceResponse stopCaseById(ServiceRequest<CaseOperationsParams> stopCaseByIdServiceRequest) {
        return post("/case/stop-by-id", stopCaseByIdServiceRequest.getData());
    }

    @Override
    public ServiceResponse stopCaseByLookup(ServiceRequest<CaseOperationsParams> stopCaseByLookupServiceRequest) {
        return post("/case/stop-by-lookup", stopCaseByLookupServiceRequest.getData());
    }

    @Override
    public ServiceResponse<User> readNextAssigneeCandidates(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/next-assignee-candidates/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidates(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/previous-assignee-candidates/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse performCaseById(ServiceRequest<CaseOperationsParams> performCaseByIdServiceRequest) {
        return post("/case/perform-by-id/", performCaseByIdServiceRequest.getData());
    }

    @Override
    public ServiceResponse performCaseByLookup(ServiceRequest<CaseOperationsParams> performCaseByLookupServiceRequest) {
        return post("/case/perform-by-lookup/", performCaseByLookupServiceRequest.getData());
    }

    @Override
    public ServiceResponse rollbackCaseById(ServiceRequest<CaseOperationsParams> rollbackCaseByIdServiceRequest) {
        return post("/case/rollback-by-id/", rollbackCaseByIdServiceRequest.getData());
    }

    @Override
    public ServiceResponse rollbackCaseByLookup(ServiceRequest<CaseOperationsParams> rollbackCaseByLookupServiceRequest) {
        return post("/case/rollback-by-lookup/", rollbackCaseByLookupServiceRequest.getData());
    }

    @Override
    public ServiceResponse doubleRollbackCase(ServiceRequest<CaseOperationsParams> doubleRollbackCaseServiceRequest) {
        return post("/case/double-rollback/", doubleRollbackCaseServiceRequest.getData());
    }

    @Override
    public ServiceResponse<UserActor> readTeamForCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/read-team/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readNextTeamMemberForCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/read-next-team-member/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readPreviousTeamMemberForCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/case/read-previous-team-member/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse assignTeamToCase(ServiceRequest<UserActor> assignTeamToCaseServiceRequest) {
        return post("/case/assign-team/", assignTeamToCaseServiceRequest.getDataList());
    }

    @Override
    public ServiceResponse updateCaseDescription(ServiceRequest<CaseOperationsParams> updateCaseDescriptionServiceRequest) {
        return put("/case/update-description", updateCaseDescriptionServiceRequest.getData());
    }

    @Override
    public ServiceResponse resetCase(ServiceRequest<CaseOperationsParams> resetCaseServiceRequest) {
        return put("/case/reset", resetCaseServiceRequest.getData());
    }

    @Override
    public ServiceResponse<Case> searchActiveCases(String entityTypeLookup, Long entityId) {
        StringBuilder urlBuilder = new StringBuilder("/case/find-active?");
        if (entityTypeLookup != null) {
            urlBuilder.append("entityTypeLookup=");
            urlBuilder.append(entityTypeLookup);
        }
        if (entityId != null) {
            urlBuilder.append("&entityId=");
            urlBuilder.append(entityId);
        }

        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Case> searchCasesThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        StringBuilder urlBuilder = new StringBuilder("/case/find-through-actors?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        if (active != null) {
            urlBuilder.append("&active=");
            urlBuilder.append(active);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Case> searchCasesBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        StringBuilder urlBuilder = new StringBuilder("/case/find-belonging-to-user-actor?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        if (active != null) {
            urlBuilder.append("&active=");
            urlBuilder.append(active);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Case> searchClosedCasesForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix) {
        StringBuilder urlBuilder = new StringBuilder("/case/find-closed-cases-for-current-user?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        return get(urlBuilder.toString());
    }

    //////////TASK//////////

    @Override
    public ServiceResponse<Task> readTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Task> performTask(ServiceRequest<TaskOperationsParams> performTaskServiceRequest) {
        return post2("/task/perform/", performTaskServiceRequest.getData());
    }

    @Override
    public ServiceResponse<Task> rollbackTask(ServiceRequest<TaskOperationsParams> rollbackTaskServiceRequest) {
        return post2("/task/rollback/", rollbackTaskServiceRequest.getData());
    }

    @Override
    public ServiceResponse assignTask(ServiceRequest<TaskOperationsParams> assignTaskServiceRequest) {
        return post2("/task/assign/", assignTaskServiceRequest.getData());
    }

    @Override
    public ServiceResponse<User> readNextTeamMemberForTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/read-next-team-member/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readPreviousTeamMemberForTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/read-previous-team-member/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidatesForTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/previous-assignee-candidates/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readNextAssigneeCandidatesForTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/next-assignee-candidates/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readCurrentAssigneeCandidatesForTask(Long taskId) {
        StringBuilder urlBuilder = new StringBuilder("/task/current-assignee-candidates/");
        if (taskId != null) {
            urlBuilder.append(taskId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Task> searchTasks(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging) {
        if (paging != null) {
            return post2("/task/search", searchTaskCaseFilterServiceRequest.getData(), "start", paging.getOffset(), "limit", paging.getLimit(), "sort", paging.getSortColumn(), "dir", paging.getSortDirection());
        }
        return post2("/task/search", searchTaskCaseFilterServiceRequest.getData());
    }

    @Override
    public ServiceResponse<Task> advancedSearchTasks(String queryParameters, Integer offset, Integer limit, String sortOrders, String type) {
        return get("/task/advanced-search", "queryParameters", queryParameters, "offset", offset, "limit", limit, "sortOrders", sortOrders, "type", type);
    }

    @Override
    public ServiceResponse<Task> searchTasksThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        StringBuilder urlBuilder = new StringBuilder("/task/find-through-actors?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        if (active != null) {
            urlBuilder.append("&active=");
            urlBuilder.append(active);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Task> searchTasksBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        StringBuilder urlBuilder = new StringBuilder("/task/find-belonging-to-user-actor?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        if (active != null) {
            urlBuilder.append("&active=");
            urlBuilder.append(active);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Task> searchClosedTasksForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix) {
        StringBuilder urlBuilder = new StringBuilder("/task/find-closed-tasks-for-current-user?");
        if (offset != null) {
            urlBuilder.append("start=");
            urlBuilder.append(offset);
        }
        if (limit != null) {
            urlBuilder.append("&limit=");
            urlBuilder.append(limit);
        }
        if (sortColumn != null) {
            urlBuilder.append("&sort=");
            urlBuilder.append(sortColumn);
        }
        if (sortDirection != null) {
            urlBuilder.append("&dir=");
            urlBuilder.append(sortDirection);
        }
        if (lookupPrefix != null) {
            urlBuilder.append("&lookupPrefix=");
            urlBuilder.append(lookupPrefix);
        }
        return get(urlBuilder.toString());
    }

    //////////ACTOR//////////

    @Override
    public ServiceResponse<Actor> readActor(Long actorId) {
        StringBuilder urlBuilder = new StringBuilder("/actor/");
        if (actorId != null) {
            urlBuilder.append(actorId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Actor> createActor(Actor actor) {
        return post("/actor/", actor);
    }

    @Override
    public ServiceResponse<Actor> updateActor(Long actorId, Actor actor) {
        StringBuilder urlBuilder = new StringBuilder("/actor/");
        if (actorId != null) {
            urlBuilder.append(actorId);
        }
        return put(urlBuilder.toString(), actor);
    }

    @Override
    public ServiceResponse deleteActor(Long actorId) {
        StringBuilder urlBuilder = new StringBuilder("/actor/");
        if (actorId != null) {
            urlBuilder.append(actorId);
        }
        return delete(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Actor> searchActors(String term, Long processId, String baseLookup) {
        StringBuilder sb = new StringBuilder("/actor/search");
        boolean paramAdded = false;
        if (StringUtils.isNotBlank(term)) {
            sb.append("term=");
            sb.append(term);
            paramAdded = true;
        }
        if (processId != null) {
            sb.append(paramAdded ? "&processId=" : "?processId=");
            sb.append(processId);
            paramAdded = true;
        }
        if (StringUtils.isNotBlank(baseLookup)) {
            sb.append(paramAdded ? "&baseLookup=" : "?baseLookup=");
        }
        return get(sb.toString());
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> checkIfUserIsActor(String actorLookup) {
        StringBuilder urlBuilder = new StringBuilder("/actor/check-user-is-actor/");
        if (actorLookup != null) {
            urlBuilder.append(actorLookup);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Actor> readActorByLookup(String actorLookup) {
        StringBuilder urlBuilder = new StringBuilder("/actor/actor-by-lookup?");
        if (actorLookup != null) {
            urlBuilder.append("actorLookup=");
            urlBuilder.append(actorLookup);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse assignUserToActor(String actorLookup, Long userId, Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/actor/assign-user-to-actor?");
        if (actorLookup != null) {
            urlBuilder.append("actorLookup=");
            urlBuilder.append(actorLookup);
        }
        if (userId != null) {
            urlBuilder.append("&userId=");
            urlBuilder.append(userId);
        }
        if (caseId != null) {
            urlBuilder.append("&caseId=");
            urlBuilder.append(caseId);
        }
        return put(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<User> readActorCandidates(Long actorId) {
        return actorId == null ?
                new ServiceResponse<User>("Actor Id should not be null.", false) :
                this.<User>get("/actor/read-candidate-users", "actorId", actorId);
    }

    //////////STATUS//////////

    @Override
    public ServiceResponse<Status> readStatus(Long statusId) {
        StringBuilder urlBuilder = new StringBuilder("/status/");
        if (statusId != null) {
            urlBuilder.append(statusId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Status> createStatus(Status status) {
        return post("/status/", status);
    }

    @Override
    public ServiceResponse<Status> updateStatus(Long statusId, Status status) {
        StringBuilder urlBuilder = new StringBuilder("/status/");
        if (statusId != null) {
            urlBuilder.append(statusId);
        }
        return put(urlBuilder.toString(), status);
    }

    @Override
    public ServiceResponse<Status> readStatusesByProcess(Long processId) {
        StringBuilder urlBuilder = new StringBuilder("/status/process/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return get(urlBuilder.toString());
    }

    //////////ACTIVITY//////////

    @Override
    public ServiceResponse<Activity> readActivitiesByProcess(Long processId) {
        StringBuilder urlBuilder = new StringBuilder("/activity/process/");
        if (processId != null) {
            urlBuilder.append(processId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<Activity> readPreviousActivities(Long caseId, Long taskId) {
        ServiceResponse<Activity> response;
        if (caseId == null && taskId == null) {
            response = new ServiceResponse<Activity>("taskId or caseId parameter should be specified.", false);
        } else if (taskId == null) {
            response = get("/activity/previous", "caseId", caseId);
        } else {
            response = get("/activity/previous", "taskId", taskId);
        }
        return response;
    }

    @Override
    public ServiceResponse<User> readAssigneeCandidatesForActivity(Long activityId) {
        return get("/activity/assignee-candidates/" + activityId);
    }

    //////////PROCESS EXPLANATIONS//////////

    @Override
    public ServiceResponse<CaseExplanation> searchProcessExplanations(Long processId, String term) {
        StringBuilder urlBuilder = new StringBuilder("/explanation/search?");
        if (processId != null) {
            urlBuilder.append("&processId=");
            urlBuilder.append(processId);
        }
        if (term != null) {
            urlBuilder.append("&term=");
            urlBuilder.append(term);
        }
        return get(urlBuilder.toString());
    }

    //////////CASE ACTION//////////

    @Override
    public ServiceResponse<CaseAction> readCaseActionsByCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/action/case/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    //////////CASE NOTE//////////

    @Override
    public ServiceResponse<CaseNote> readCaseNote(Long caseNoteId) {
        StringBuilder urlBuilder = new StringBuilder("/note/");
        if (caseNoteId != null) {
            urlBuilder.append(caseNoteId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseNote> createCaseNote(CaseNote caseNote) {
        return post("/note/", caseNote);
    }

    @Override
    public ServiceResponse<CaseNote> updateCaseNote(Long caseNoteId, CaseNote caseNote) {
        StringBuilder urlBuilder = new StringBuilder("/note/");
        if (caseNoteId != null) {
            urlBuilder.append(caseNoteId);
        }
        return put(urlBuilder.toString(), caseNote);
    }

    @Override
    public ServiceResponse deleteCaseNote(Long caseNoteId) {
        StringBuilder urlBuilder = new StringBuilder("/note/");
        if (caseNoteId != null) {
            urlBuilder.append(caseNoteId);
        }
        return delete(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByTask(Long caseNoteId) {
        StringBuilder urlBuilder = new StringBuilder("/note/task/");
        if (caseNoteId != null) {
            urlBuilder.append(caseNoteId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/note/case/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCaseObject(Long entityId, String entityType) {
        return get("/note/case-object/entity/" + entityType + "/" + entityId);
    }

    //////////CASE ATTACHMENT//////////

    @Override
    public ServiceResponse<FileInfo> downloadCaseAttachment(Long caseAttachmentId) {
        return getFile("/attachment/download/" + caseAttachmentId);
    }

    @Override
    public ServiceResponse uploadCaseAttachment(Long caseId, String name, String description, String filename, InputStream inputStream) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ORIGINAL_FILENAME, filename);
        parameters.put("name", name);
        parameters.put("description", description);

        String responderData = upload("/attachment/upload/" + caseId, inputStream, parameters);

        return new ServiceResponse(responderData, true);
    }

    @Override
    public ServiceResponse deleteCaseAttachment(Long caseAttachmentId) {
        StringBuilder urlBuilder = new StringBuilder("/attachment/");
        if (caseAttachmentId != null) {
            urlBuilder.append(caseAttachmentId);
        }
        return delete(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseAttachment> readCaseAttachmentsByCase(Long caseId) {
        StringBuilder urlBuilder = new StringBuilder("/attachment/case/");
        if (caseId != null) {
            urlBuilder.append(caseId);
        }
        return get(urlBuilder.toString());
    }

    @Override
    public ServiceResponse<CaseAttachment> updateCaseAttachment(Long caseAttachmentId, CaseAttachment caseAttachment) {
        StringBuilder urlBuilder = new StringBuilder("/attachment/");
        if (caseAttachmentId != null) {
            urlBuilder.append(caseAttachmentId);
        }
        return put(urlBuilder.toString(), caseAttachment);
    }

    @Override
    public ServiceResponse<Task> createWorkflowTaskForRecord(Long recordId, String entityLookup, Long processId) {
        return post("/workflow/task/record", "recordId", recordId, "entityLookup", entityLookup, "processId", processId);
    }

    @Override
    public ServiceResponse<Task> deleteWorkflowTasksForRecord(Long recordId, Long entityId) {
        return delete("/workflow/task/record", "recordId", recordId, "entityId", entityId);
    }

    @Override
    public ServiceResponse<Task> readWorkflowTasksForRecord(Long recordId, String entityLookup) {
        return get("/workflow/task/record/" + entityLookup + "/" + recordId);
    }

    @Override
    public ServiceResponse<Process> readAvailableProcessesForRecord(Long recordId, String entityLookup) {
        return get("/workflow/available/record/" + entityLookup + "/" + recordId);
    }

    @Override
    public ServiceResponse<Process> readProcessWithStartActivity(String processLookup) {
        return get("/workflow/activity/start/" + processLookup);
    }

    @Override
    public ServiceResponse<Process> readProcessWithActionActivity(Long activityActionId) {
        return get("/workflow/activity/by-activity-action/" + activityActionId);
    }

    @Override
    public ServiceResponse<ActivityAction> performActivityAction(Long activityActionId, Long entityId, Task taskDetails) {
        ServiceResponse<ActivityAction> response;
        if (entityId == null) {
            response = new ServiceResponse<ActivityAction>("Entity Id information is not specified.", false);
        } else if (activityActionId == null) {
            response = new ServiceResponse<ActivityAction>("Activity Action Id information is not specified.", false);
        } else if (taskDetails == null) {
            response = new ServiceResponse<ActivityAction>("Some task details are not specified.", false);
        } else {
            response = put2("/workflow/perform-activity-action/" + activityActionId + "/" + entityId, taskDetails);
        }
        return response;
    }

}