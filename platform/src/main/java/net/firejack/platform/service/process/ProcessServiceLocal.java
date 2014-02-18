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

package net.firejack.platform.service.process;


import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.IProcessService;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.service.process.broker.*;
import net.firejack.platform.service.process.broker.actor.LoadActorUsersCandidatesBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Class is an implementation of net.firejack.platform.api.process.IMailService
 * Business layer is invoked to serve the requests locally
 */
@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_PROCESS_SERVICE)
public class ProcessServiceLocal implements IProcessService {

    @Autowired
    @Qualifier("readProcessListBroker")
    private ReadProcessListBroker readProcessListBroker;

    @Autowired
    @Qualifier("readProcessBroker")
    private ReadProcessBroker readProcessBroker;

    @Autowired
    @Qualifier("createProcessBroker")
    private CreateProcessBroker createProcessBroker;

    @Autowired
    private CreateWorkflowBroker createWorkflowBroker;

    @Autowired
    @Qualifier("updateProcessBroker")
    private UpdateProcessBroker updateProcessBroker;

    @Autowired
    @Qualifier("readMyProcessListBroker")
    private ReadMyProcessListBroker readMyProcessListBroker;

    @Autowired
    @Qualifier("deleteProcessBroker")
    private DeleteProcessBroker deleteProcessBroker;

    @Autowired
    @Qualifier("searchProcessListBroker")
    private SearchProcessListBroker searchProcessListBroker;

    @Autowired
    @Qualifier("readAllProcessTreeBroker")
    private ReadAllProcessTreeBroker readAllProcessTreeBroker;

    @Autowired
    @Qualifier("readCustomFieldsBroker")
    private ReadCustomFieldsBroker readCustomFieldsBroker;

    @Autowired
    @Qualifier("readCaseBroker")
    private ReadCaseBroker readCaseBroker;

    @Autowired
    @Qualifier("searchCaseListBroker")
    private SearchCaseListBroker searchCaseListBroker;

    @Autowired
    @Qualifier("stopCaseByIdBroker")
    private StopCaseByIdBroker stopCaseByIdBroker;

    @Autowired
    @Qualifier("stopCaseByLookupBroker")
    private StopCaseByLookupBroker stopCaseByLookupBroker;

    @Autowired
    @Qualifier("startCaseBroker")
    private StartCaseBroker startCaseBroker;

    @Autowired
    @Qualifier("readNextAssigneeCandidatesBroker")
    private ReadNextAssigneeCandidatesBroker readNextAssigneeCandidatesBroker;

    @Autowired
    @Qualifier("readPreviousAssigneeCandidatesBroker")
    private ReadPreviousAssigneeCandidatesBroker readPreviousAssigneeCandidatesBroker;

    @Autowired
    @Qualifier("performCaseByIdBroker")
    private PerformCaseByIdBroker performCaseByIdBroker;

    @Autowired
    @Qualifier("performCaseByLookupBroker")
    private PerformCaseByLookupBroker performCaseByLookupBroker;

    @Autowired
    @Qualifier("rollbackCaseByIdBroker")
    private RollbackCaseByIdBroker rollbackCaseByIdBroker;

    @Autowired
    @Qualifier("rollbackCaseByLookupBroker")
    private RollbackCaseByLookupBroker rollbackCaseByLookupBroker;

    @Autowired
    @Qualifier("doubleRollbackCaseBroker")
    private DoubleRollbackCaseBroker doubleRollbackCaseBroker;

    @Autowired
    @Qualifier("readTeamForCaseBroker")
    private ReadTeamForCaseBroker readTeamForCaseBroker;

    @Autowired
    @Qualifier("readNextTeamMemberForCaseBroker")
    private ReadNextTeamMemberForCaseBroker readNextTeamMemberForCaseBroker;

    @Autowired
    @Qualifier("readPreviousTeamMemberForCaseBroker")
    private ReadPreviousTeamMemberForCaseBroker readPreviousTeamMemberForCaseBroker;

    @Autowired
    @Qualifier("assignTeamToCaseBroker")
    private AssignTeamToCaseBroker assignTeamToCaseBroker;

    @Autowired
    @Qualifier("updateCaseDescriptionBroker")
    private UpdateCaseDescriptionBroker updateCaseDescriptionBroker;

    @Autowired
    @Qualifier("resetCaseBroker")
    private ResetCaseBroker resetCaseBroker;

    @Autowired
    @Qualifier("findActiveCasesBroker")
    private FindActiveCasesBroker findActiveCasesBroker;

    @Autowired
    @Qualifier("findCaseThroughActorsBroker")
    private FindCaseThroughActorsBroker findCaseThroughActorsBroker;

    @Autowired
    @Qualifier("findCaseBelongingToUserActorBroker")
    private FindCaseBelongingToUserActorBroker findCaseBelongingToUserActorBroker;

    @Autowired
    @Qualifier("findClosedCasesForCurrentUserBroker")
    private FindClosedCasesForCurrentUserBroker findClosedCasesForCurrentUserBroker;

    @Autowired
    @Qualifier("readTaskBroker")
    private ReadTaskBroker readTaskBroker;

    @Autowired
    @Qualifier("performTaskBroker")
    private PerformTaskBroker performTaskBroker;

    @Autowired
    @Qualifier("rollbackTaskBroker")
    private RollbackTaskBroker rollbackTaskBroker;

    @Autowired
    @Qualifier("assignTaskBroker")
    private AssignTaskBroker assignTaskBroker;

    @Autowired
    @Qualifier("readNextTeamMemberForTaskBroker")
    private ReadNextTeamMemberForTaskBroker readNextTeamMemberForTaskBroker;

    @Autowired
    @Qualifier("readPreviousTeamMemberForTaskBroker")
    private ReadPreviousTeamMemberForTaskBroker readPreviousTeamMemberForTaskBroker;

    @Autowired
    @Qualifier("readPreviousAssigneeCandidatesForTaskBroker")
    private ReadPreviousAssigneeCandidatesForTaskBroker readPreviousAssigneeCandidatesForTaskBroker;

    @Autowired
    @Qualifier("readNextAssigneeCandidatesForTaskBroker")
    private ReadNextAssigneeCandidatesForTaskBroker readNextAssigneeCandidatesForTaskBroker;

    @Autowired
    @Qualifier("readCurrentAssigneeCandidatesForTaskBroker")
    private ReadCurrentAssigneeCandidatesForTaskBroker readCurrentAssigneeCandidatesForTaskBroker;

    @Autowired
    @Qualifier("searchTaskListBroker")
    private SearchTaskListBroker searchTaskListBroker;

    @Autowired
    @Qualifier("advancedSearchTaskBroker")
    private AdvancedSearchTaskBroker advancedSearchTaskBroker;

    @Autowired
    @Qualifier("findTaskThroughActorsBroker")
    private FindTaskThroughActorsBroker findTaskThroughActorsBroker;

    @Autowired
    @Qualifier("findTaskBelongingToUserActorBroker")
    private FindTaskBelongingToUserActorBroker findTaskBelongingToUserActorBroker;

    @Autowired
    @Qualifier("findClosedTasksForCurrentUserBroker")
    private FindClosedTasksForCurrentUserBroker findClosedTasksForCurrentUserBroker;

    @Autowired
    @Qualifier("readActorBroker")
    private ReadActorBroker readActorBroker;

    @Autowired
    @Qualifier("createActorBroker")
    private CreateActorBroker createActorBroker;

    @Autowired
    @Qualifier("updateActorBroker")
    private UpdateActorBroker updateActorBroker;

    @Autowired
    @Qualifier("deleteActorBroker")
    private DeleteActorBroker deleteActorBroker;

    @Autowired
    @Qualifier("searchActorListBroker")
    private SearchActorListBroker searchActorListBroker;

    @Autowired
    @Qualifier("checkIfUserIsActorBroker")
    private CheckIfUserIsActorBroker checkIfUserIsActorBroker;

    @Autowired
    @Qualifier("readActorByLookupBroker")
    private ReadActorByLookupBroker readActorByLookupBroker;

    @Autowired
    @Qualifier("assignUserToActorBroker")
    private AssignUserToActorBroker assignUserToActorBroker;

    @Autowired
    private LoadActorUsersCandidatesBroker loadActorUsersCandidatesBroker;

    @Autowired
    @Qualifier("readStatusBroker")
    private ReadStatusBroker readStatusBroker;

    @Autowired
    @Qualifier("createStatusBroker")
    private CreateStatusBroker createStatusBroker;

    @Autowired
    @Qualifier("updateStatusBroker")
    private UpdateStatusBroker updateStatusBroker;

    @Autowired
    @Qualifier("readStatusesByProcessBroker")
    private ReadStatusesByProcessBroker readStatusesByProcessBroker;

    @Autowired
    @Qualifier("readActivitiesByProcessBroker")
    private ReadActivitiesByProcessBroker readActivitiesByProcessBroker;

    @Autowired
    private ReadPreviousActivitiesBroker readPreviousActivitiesBroker;

    @Autowired
    private ReadAssigneeCandidatesForActivityBroker readAssigneeCandidatesForActivityBroker;

    @Autowired
    @Qualifier("searchProcessExplanationsBroker")
    private SearchProcessExplanationsBroker searchProcessExplanationsBroker;

    @Autowired
    @Qualifier("readCaseActionsByCaseBroker")
    private ReadCaseActionsByCaseBroker readCaseActionsByCaseBroker;

    @Autowired
    @Qualifier("readCaseNoteBroker")
    private ReadCaseNoteBroker readCaseNoteBroker;

    @Autowired
    @Qualifier("createCaseNoteBroker")
    private CreateCaseNoteBroker createCaseNoteBroker;

    @Autowired
    @Qualifier("updateCaseNoteBroker")
    private UpdateCaseNoteBroker updateCaseNoteBroker;

    @Autowired
    @Qualifier("deleteCaseNoteBroker")
    private DeleteCaseNoteBroker deleteCaseNoteBroker;

    @Autowired
    @Qualifier("readCaseNotesByTaskBroker")
    private ReadCaseNotesByTaskBroker readCaseNotesByTaskBroker;

    @Autowired
    @Qualifier("readCaseNotesByCaseBroker")
    private ReadCaseNotesByCaseBroker readCaseNotesByCaseBroker;

    @Autowired
    @Qualifier("readCaseNotesByCaseObjectBroker")
    private ReadCaseNotesByCaseObjectBroker readCaseNotesByCaseObjectBroker;

    @Autowired
    @Qualifier("downloadCaseAttachmentBroker")
    private DownloadCaseAttachmentBroker downloadCaseAttachmentBroker;

    @Autowired
    @Qualifier("uploadCaseAttachmentBroker")
    private UploadCaseAttachmentBroker uploadCaseAttachmentBroker;

    @Autowired
    @Qualifier("deleteCaseAttachmentBroker")
    private DeleteCaseAttachmentBroker deleteCaseAttachmentBroker;

    @Autowired
    @Qualifier("readCaseAttachmentsByCaseBroker")
    private ReadCaseAttachmentsByCaseBroker readCaseAttachmentsByCaseBroker;

    @Autowired
    @Qualifier("updateCaseAttachmentBroker")
    private UpdateCaseAttachmentBroker updateCaseAttachmentBroker;

    @Autowired
    @Qualifier("createWorkflowTaskForRecordBroker")
    private CreateWorkflowTaskForRecordBroker createWorkflowTaskForRecordBroker;

    @Autowired
    @Qualifier("deleteWorkflowTasksForRecordBroker")
    private DeleteWorkflowTasksForRecordBroker deleteWorkflowTasksForRecordBroker;

    @Autowired
    @Qualifier("readWorkflowTasksForRecordBroker")
    private ReadWorkflowTasksForRecordBroker readWorkflowTasksForRecordBroker;

    @Autowired
    private ReadAvailableProcessesForRecordBroker readAvailableProcessesForRecordBroker;

    @Autowired
    private ReadProcessWithStartActivityBroker readProcessWithStartActivityBroker;

    @Autowired
    private ReadProcessWithActionActivityBroker readProcessWithActionActivityBroker;

    @Autowired
    private PerformActivityActionBroker performActivityActionBroker;

    //////////PROCESS//////////


    @Override
    public ServiceResponse<Process> readAllProcess() {
        return readProcessListBroker.execute(new ServiceRequest<NamedValues>());
    }

    @Override
    public ServiceResponse<Process> readProcess(Long processId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(processId);
        return readProcessBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Process> createProcess(Process process) {
        return createProcessBroker.execute(new ServiceRequest<Process>(process));
    }

    @Override
    public ServiceResponse<Process> createWorkflow(Process process) {
        return createWorkflowBroker.execute(new ServiceRequest<Process>(process));
    }

    @Override
    public ServiceResponse<Process> updateProcess(Long processId, Process data) {
        return updateProcessBroker.execute(new ServiceRequest<Process>(data));
    }

    @Override
    public ServiceResponse<Process> readMyProcesses(String lookupPrefix) {
        ServiceRequest<NamedValues<String>> parameterizedServiceRequest = new ServiceRequest<NamedValues<String>>();
        NamedValues<String> namedValues = new NamedValues<String>();
        namedValues.put("lookupPrefix", lookupPrefix);
        parameterizedServiceRequest.setData(namedValues);
        return readMyProcessListBroker.execute(parameterizedServiceRequest);
    }

    @Override
    public ServiceResponse<Process> deleteProcess(Long processId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(processId);
        return deleteProcessBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Process> searchProcesses(String term, Boolean humanProcess) {
        NamedValues<Object> parameterized = new NamedValues<Object>();
        parameterized.put(SearchProcessListBroker.PARAM_TERM, term);
        parameterized.put(SearchProcessListBroker.PARAM_HUMAN_PROCESS, humanProcess);
        return searchProcessListBroker.execute(new ServiceRequest<NamedValues>(parameterized));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> readAllProcessTree() {
        return readAllProcessTreeBroker.execute(new ServiceRequest<NamedValues>());
    }

    //////////CUSTOM FIELDS//////////

    @Override
    public ServiceResponse<ProcessField> readCustomFields(Long processId) {
        ServiceRequest<NamedValues<Long>> parameterizedServiceRequest = new ServiceRequest<NamedValues<Long>>();
        NamedValues<Long> namedValues = new NamedValues<Long>();
        namedValues.put("processId", processId);
        parameterizedServiceRequest.setData(namedValues);
        return readCustomFieldsBroker.execute(parameterizedServiceRequest);
    }

    //////////CASE//////////

    @Override
    public ServiceResponse<Case> readCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Case> searchCases(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("searchFilter", searchTaskCaseFilterServiceRequest.getData());
        namedValues.put("paging", paging);
        return searchCaseListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Long>> startCase(CaseOperationsParams startCaseServiceParams) {
        return startCaseBroker.execute(new ServiceRequest<CaseOperationsParams>(startCaseServiceParams));
    }

    @Override
    public ServiceResponse stopCaseById(ServiceRequest<CaseOperationsParams> stopCaseByIdServiceRequest) {
        return stopCaseByIdBroker.execute(stopCaseByIdServiceRequest);
    }

    @Override
    public ServiceResponse stopCaseByLookup(ServiceRequest<CaseOperationsParams> stopCaseByLookupServiceRequest) {
        return stopCaseByLookupBroker.execute(stopCaseByLookupServiceRequest);
    }

    @Override
    public ServiceResponse<User> readNextAssigneeCandidates(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readNextAssigneeCandidatesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidates(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readPreviousAssigneeCandidatesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse performCaseById(ServiceRequest<CaseOperationsParams> performCaseByIdServiceRequest) {
        return performCaseByIdBroker.execute(performCaseByIdServiceRequest);
    }

    @Override
    public ServiceResponse performCaseByLookup(ServiceRequest<CaseOperationsParams> performCaseByLookupServiceRequest) {
        return performCaseByLookupBroker.execute(performCaseByLookupServiceRequest);
    }

    @Override
    public ServiceResponse rollbackCaseById(ServiceRequest<CaseOperationsParams> rollbackCaseByIdServiceRequest) {
        return rollbackCaseByIdBroker.execute(rollbackCaseByIdServiceRequest);
    }

    @Override
    public ServiceResponse rollbackCaseByLookup(ServiceRequest<CaseOperationsParams> rollbackCaseByLookupServiceRequest) {
        return rollbackCaseByLookupBroker.execute(rollbackCaseByLookupServiceRequest);
    }

    @Override
    public ServiceResponse doubleRollbackCase(ServiceRequest<CaseOperationsParams> doubleRollbackCaseServiceRequest) {
        return doubleRollbackCaseBroker.execute(doubleRollbackCaseServiceRequest);
    }

    @Override
    public ServiceResponse<UserActor> readTeamForCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readTeamForCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readNextTeamMemberForCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readNextTeamMemberForCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readPreviousTeamMemberForCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readPreviousTeamMemberForCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse assignTeamToCase(ServiceRequest<UserActor> assignTeamToCaseServiceRequest) {
        return assignTeamToCaseBroker.execute(assignTeamToCaseServiceRequest);
    }

    @Override
    public ServiceResponse updateCaseDescription(ServiceRequest<CaseOperationsParams> updateCaseDescriptionServiceRequest) {
        return updateCaseDescriptionBroker.execute(updateCaseDescriptionServiceRequest);
    }

    @Override
    public ServiceResponse resetCase(ServiceRequest<CaseOperationsParams> resetCaseServiceRequest) {
        return resetCaseBroker.execute(resetCaseServiceRequest);
    }

    @Override
    public ServiceResponse<Case> searchActiveCases(String entityTypeLookup, Long entityId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("entityTypeLookup", entityTypeLookup);
        namedValues.put("entityId", entityId);
        return findActiveCasesBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Case> searchCasesThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        namedValues.put("active", active);
        return findCaseThroughActorsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Case> searchCasesBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        namedValues.put("active", active);
        return findCaseBelongingToUserActorBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Case> searchClosedCasesForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        return findClosedCasesForCurrentUserBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    //////////TASK//////////

    @Override
    public ServiceResponse<Task> readTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Task> performTask(ServiceRequest<TaskOperationsParams> performTaskServiceRequest) {
        return performTaskBroker.execute(performTaskServiceRequest);
    }

    @Override
    public ServiceResponse<Task> rollbackTask(ServiceRequest<TaskOperationsParams> rollbackTaskServiceRequest) {
        return rollbackTaskBroker.execute(rollbackTaskServiceRequest);
    }

    @Override
    public ServiceResponse assignTask(ServiceRequest<TaskOperationsParams> assignTaskServiceRequest) {
        return assignTaskBroker.execute(assignTaskServiceRequest);
    }

    @Override
    public ServiceResponse<User> readNextTeamMemberForTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readNextTeamMemberForTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readPreviousTeamMemberForTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readPreviousTeamMemberForTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readPreviousAssigneeCandidatesForTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readPreviousAssigneeCandidatesForTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readNextAssigneeCandidatesForTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readNextAssigneeCandidatesForTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<User> readCurrentAssigneeCandidatesForTask(Long taskId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(taskId);
        return readCurrentAssigneeCandidatesForTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Task> searchTasks(ServiceRequest<SearchTaskCaseFilter> searchTaskCaseFilterServiceRequest, Paging paging) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("searchFilter", searchTaskCaseFilterServiceRequest.getData());
        namedValues.put("paging", paging);
        return searchTaskListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> advancedSearchTasks(String queryParameters, Integer offset, Integer limit, String sortOrders, String type) {
        NamedValues values = new NamedValues();
        values.put("queryParameters", queryParameters);
        values.put("offset", offset);
        values.put("limit", limit);
        values.put("sortOrders", sortOrders);
        values.put("type", type);
        return advancedSearchTaskBroker.execute(new ServiceRequest<NamedValues>(values));
    }

    @Override
    public ServiceResponse<Task> searchTasksThroughActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        namedValues.put("active", active);
        return findTaskThroughActorsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> searchTasksBelongingToUserActors(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix, Boolean active) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        namedValues.put("active", active);
        return findTaskBelongingToUserActorBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> searchClosedTasksForCurrentUser(Integer offset, Integer limit, String sortColumn, String sortDirection, String lookupPrefix) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("offset", offset);
        namedValues.put("limit", limit);
        namedValues.put("sort", sortColumn);
        namedValues.put("dir", sortDirection);
        namedValues.put("lookupPrefix", lookupPrefix);
        return findClosedTasksForCurrentUserBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    //////////ACTOR//////////

    @Override
    public ServiceResponse<Actor> readActor(Long actorId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(actorId);
        return readActorBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Actor> createActor(Actor actor) {
        return createActorBroker.execute(new ServiceRequest<Actor>(actor));
    }

    @Override
    public ServiceResponse<Actor> updateActor(Long actorId, Actor data) {
        return updateActorBroker.execute(new ServiceRequest<Actor>(data));
    }

    @Override
    public ServiceResponse<Actor> deleteActor(Long actorId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(actorId);
        return deleteActorBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Actor> searchActors(String term, Long processId, String baseLookup) {
        NamedValues parameterized = new NamedValues();
        parameterized.put(SearchActorListBroker.PARAM_TERM, term);
        parameterized.put(SearchActorListBroker.PARAM_PROCESS_ID, processId);
        parameterized.put(SearchActorListBroker.PARAM_BASE_LOOKUP, baseLookup);
        return searchActorListBroker.execute(new ServiceRequest<NamedValues>(parameterized));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> checkIfUserIsActor(String actorLookup) {
        return checkIfUserIsActorBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(actorLookup)));
    }

    @Override
    public ServiceResponse<Actor> readActorByLookup(String actorLookup) {
        return readActorByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(actorLookup)));
    }

    @Override
    public ServiceResponse assignUserToActor(String actorLookup, Long userId, Long caseId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("actorLookup", actorLookup);
        namedValues.put("userId", userId);
        namedValues.put("caseId", caseId);
        return assignUserToActorBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<User> readActorCandidates(Long actorId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(actorId);
        return loadActorUsersCandidatesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    //////////STATUS//////////

    @Override
    public ServiceResponse<Status> readStatus(Long statusId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(statusId);
        return readStatusBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Status> createStatus(Status status) {
        return createStatusBroker.execute(new ServiceRequest<Status>(status));
    }

    @Override
    public ServiceResponse<Status> updateStatus(Long statusId, Status data) {
        return updateStatusBroker.execute(new ServiceRequest<Status>(data));
    }

    @Override
    public ServiceResponse<Status> readStatusesByProcess(Long processId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(processId);
        return readStatusesByProcessBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    //////////ACTIVITIES//////////

    @Override
    public ServiceResponse<Activity> readActivitiesByProcess(Long processId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(processId);
        return readActivitiesByProcessBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Activity> readPreviousActivities(Long caseId, Long taskId) {
        NamedValues<Long> params = new NamedValues<Long>();
        params.put(ReadPreviousActivitiesBroker.PARAM_CASE_ID, caseId);
        params.put(ReadPreviousActivitiesBroker.PARAM_TASK_ID, taskId);
        return readPreviousActivitiesBroker.execute(new ServiceRequest<NamedValues<Long>>(params));
    }

    @Override
    public ServiceResponse<User> readAssigneeCandidatesForActivity(Long activityId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(activityId);
        return readAssigneeCandidatesForActivityBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    //////////PROCESS EXPLANATIONS//////////

    @Override
    public ServiceResponse<CaseExplanation> searchProcessExplanations(Long processId, String term) {
        NamedValues parameterized = new NamedValues();
        parameterized.put("processId", processId);
        parameterized.put("term", term);
        return searchProcessExplanationsBroker.execute(new ServiceRequest<NamedValues>(parameterized));
    }

    //////////CASE ACTIONS//////////

    @Override
    public ServiceResponse<CaseAction> readCaseActionsByCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readCaseActionsByCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    //////////CASE NOTE//////////

    @Override
    public ServiceResponse<CaseNote> readCaseNote(Long caseNoteId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseNoteId);
        return readCaseNoteBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseNote> createCaseNote(CaseNote caseNote) {
        return createCaseNoteBroker.execute(new ServiceRequest<CaseNote>(caseNote));
    }

    @Override
    public ServiceResponse<CaseNote> updateCaseNote(Long caseNoteId, CaseNote data) {
        return updateCaseNoteBroker.execute(new ServiceRequest<CaseNote>(data));
    }

    @Override
    public ServiceResponse<CaseNote> deleteCaseNote(Long caseNoteId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseNoteId);
        return deleteCaseNoteBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByTask(Long caseNoteId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseNoteId);
        return readCaseNotesByTaskBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readCaseNotesByCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseNote> readCaseNotesByCaseObject(Long entityId, String entityType) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("entityId", entityId);
        namedValues.put("entityType", entityType);
        return readCaseNotesByCaseObjectBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    //////////CASE ATTACHMENTS//////////

    @Override
    public ServiceResponse<FileInfo> downloadCaseAttachment(Long caseAttachmentId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseAttachmentId);
        return downloadCaseAttachmentBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse uploadCaseAttachment(Long caseId, String name, String description, String filename, InputStream inputStream) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("caseId", caseId);
        namedValues.put("name", name);
        namedValues.put("description", description);
        namedValues.put("inputStream", inputStream);
        namedValues.put("filename", filename);
        return uploadCaseAttachmentBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse deleteCaseAttachment(Long caseAttachmentId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseAttachmentId);
        return deleteCaseAttachmentBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseAttachment> readCaseAttachmentsByCase(Long caseId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(caseId);
        return readCaseAttachmentsByCaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<CaseAttachment> updateCaseAttachment(Long caseAttachmentId, CaseAttachment caseAttachment) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("caseAttachmentId", caseAttachmentId);
        namedValues.put("caseAttachment", caseAttachment);
        return updateCaseAttachmentBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> createWorkflowTaskForRecord(Long recordId, String entityLookup, Long processId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("recordId", recordId);
        namedValues.put("entityLookup", entityLookup);
        namedValues.put("processId", processId);
        return createWorkflowTaskForRecordBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> deleteWorkflowTasksForRecord(Long recordId, Long entityId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("recordId", recordId);
        namedValues.put("entityId", entityId);
        return deleteWorkflowTasksForRecordBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Task> readWorkflowTasksForRecord(Long recordId, String entityLookup) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("recordId", recordId);
        namedValues.put("entityLookup", entityLookup);
        return readWorkflowTasksForRecordBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Process> readAvailableProcessesForRecord(Long recordId, String entityLookup) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("recordId", recordId);
        namedValues.put("entityLookup", entityLookup);
        return readAvailableProcessesForRecordBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Process> readProcessWithStartActivity(String processLookup) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("processLookup", processLookup);
        return readProcessWithStartActivityBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Process> readProcessWithActionActivity(Long activityActionId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("activityActionId", activityActionId);
        return readProcessWithActionActivityBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ActivityAction> performActivityAction(Long actionActivityId, Long entityId, Task taskDetails) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(PerformActivityActionBroker.PARAM_ACTION_ACTIVITY_ID, actionActivityId);
        params.put(PerformActivityActionBroker.PARAM_ENTITY_ID, entityId);
        params.put(PerformActivityActionBroker.PARAM_TASK, taskDetails);
        return performActivityActionBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }
}