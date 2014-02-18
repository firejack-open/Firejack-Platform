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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Case;
import net.firejack.platform.api.process.domain.CaseSearchTermVO;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.domain.SearchTaskCaseFilter;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class encapsulates the functionality of searching for the cases
 */
@TrackDetails
@Component("searchCaseListBroker")
public class SearchCaseListBroker extends ListBroker<CaseModel, Case, NamedValues> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to search the cases by search parameters
     * @param namedValuesServiceRequest service request containing search parameters
     * @return list of found cases
     * @throws BusinessFunctionException
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<CaseModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        CaseSearchTermVO caseSearchTermVO = getCaseSearchTermVO(namedValuesServiceRequest);
        return caseStore.findAllBySearchParams(caseSearchTermVO, getFilter());
    }

    /**
     * Gets total count for the found cases
     * @param namedValuesServiceRequest service request containing search parameters
     * @return total number of found cases
     * @throws BusinessFunctionException
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Integer getTotal(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        CaseSearchTermVO caseSearchTermVO = getCaseSearchTermVO(namedValuesServiceRequest);
        long l = caseStore.countAllBySearchParams(caseSearchTermVO, getFilter());
        return (int) l;
    }

    /**
     * Constructs search value object from the search parameters
     * @param namedValuesServiceRequest service request containing search parameters
     * @return search value object to be passes to data access layer
     */
    private CaseSearchTermVO getCaseSearchTermVO(ServiceRequest<NamedValues> namedValuesServiceRequest) {
        SearchTaskCaseFilter searchTaskCaseFilter = (SearchTaskCaseFilter) namedValuesServiceRequest.getData().get("searchFilter");
        Paging paging = (Paging) namedValuesServiceRequest.getData().get("paging");

        CaseSearchTermVO caseSearchTermVO = new CaseSearchTermVO();
        caseSearchTermVO.setActive(searchTaskCaseFilter.getActive());
        caseSearchTermVO.setActivityId(searchTaskCaseFilter.getActivityId());
        caseSearchTermVO.setAssigneeId(searchTaskCaseFilter.getAssigneeId());
        caseSearchTermVO.setDescription(searchTaskCaseFilter.getDescription());
        caseSearchTermVO.setEndDate(searchTaskCaseFilter.getEndDate());
        caseSearchTermVO.setLookupPrefix(searchTaskCaseFilter.getLookupPrefix());
        caseSearchTermVO.setProcessId(searchTaskCaseFilter.getProcessId());
        caseSearchTermVO.setStartDate(searchTaskCaseFilter.getStartDate());
        caseSearchTermVO.setStatusId(searchTaskCaseFilter.getStatusId());

        caseSearchTermVO.setLimit(paging.getLimit());
        caseSearchTermVO.setOffset(paging.getOffset());
        SortOrder sortDirection = paging.getSortDirection();
        caseSearchTermVO.setSortDirection(sortDirection != null ? sortDirection.name() : SortOrder.ASC.name());
        caseSearchTermVO.setSortColumn(paging.getSortColumn());

        return caseSearchTermVO;
    }

    /**
     * Converts case entities to the case data transfer object.
     * In addition, the information about the user being able to perform it and having a previous task
     * is set to each case data transfer object.
     * @param entities list of case entities
     * @return list of case data transfer objects
     */
    @Override
    protected List<Case> convertToDTOs(List<CaseModel> entities) {
        List<ActorModel> actorsOfUser = actorStore.getActorsOfUser(ContextManager.getUserInfoProvider().getId());
        Set<Long> actorIds = new HashSet<Long>();
        for (ActorModel actor : actorsOfUser) {
            actorIds.add(actor.getId());
        }

        List<Case> cases = new ArrayList<Case>();
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (CaseModel caseModel : entities) {
            Case processCase = factory.convertTo(Case.class, caseModel);
            for (TaskModel taskModel : caseModel.getTaskModels()) {
                if (taskModel != null && taskModel.getActive()) { // tasks returned by LEFT OUTER JOIN, might be null if there are no active tasks
                    boolean hasPreviousTask = taskModel.getActivity().getSortPosition() > 1;
                    processCase.setHasPreviousTask(hasPreviousTask);
                    boolean userCanPerform = actorIds.contains(taskModel.getActivity().getActor().getId());
                    processCase.setUserCanPerform(userCanPerform);
                    break;
                }
            }
            taskCaseProcessor.registerCaseProcess(processCase, processesMap);
            cases.add(processCase);
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return cases;
    }
    
}