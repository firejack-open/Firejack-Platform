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


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.TestBusinessContext;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.listener.ProcessExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        ProcessExecutionListener.class
})
public class ProcessTaskServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessTaskServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

    @Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

	@Before
	public void setUp() {
    }

	@After
	public void tearDown() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

    private Long startCase(String entityType, Long entityId) {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        User user1 = (User) testContextAttributes.get(Elements.USER1);

        CaseOperationsParams startCaseParams = new CaseOperationsParams();
        startCaseParams.setAssigneeId(user1.getId());
        startCaseParams.setAllowNullAssignee(false);
        startCaseParams.setCaseDescription("test_case");
        startCaseParams.setNoteText("note");
        startCaseParams.setProcessLookup(processLookup);

        List<CaseObject> caseObjects = new ArrayList<CaseObject>();
        CaseObject caseObject = new CaseObject();
        caseObject.setEntityId(entityId);
        caseObject.setEntityType(entityType);
        caseObject.setProcessFields(Collections.<ProcessFieldCaseValue>emptyList());
        caseObjects.add(caseObject);
        startCaseParams.setCaseObjects(caseObjects);

        ServiceResponse<SimpleIdentifier<Long>> response = OPFEngine.ProcessService.startCase(startCaseParams);

        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Long caseId = response.getItem().getIdentifier();
        Assert.assertNotNull("Case id shouldn't be null", caseId);

        return caseId;
    }

	@Test
	public void readPerformRollbackTasks() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        Long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        ServiceResponse<Task> searchTasksResponse = OPFEngine.ProcessService.searchTasks(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        Assert.assertNotNull("Response shouldn't be null.", searchTasksResponse);
        Assert.assertEquals("Total count of tasks should be 1.", searchTasksResponse.getTotal(), new Integer(1));

        Long taskId = searchTasksResponse.getItem().getId();

        ServiceResponse<Task> readTaskResponse = OPFEngine.ProcessService.readTask(taskId);

        Task readTask = readTaskResponse.getData().iterator().next();
        Assert.assertNotNull("First task in result list shouldn't be null.", readTask);
        Assert.assertEquals("TaskId returned from readTask should be equal to the supplied one", taskId, readTask.getId());
        Long firstTaskStatusId = readTask.getActivity().getStatus().getId();

//      doesn't work because processCase property is not copied by factory
//        Assert.assertEquals("CaseId returned from readTask should be equal to the one from startCase", caseId, readTask.getProcessCase().getId());

        User user1 = (User) testContextAttributes.get(Elements.USER1);
        User user2 = (User) testContextAttributes.get(Elements.USER2);

        ServiceResponse<User> nextAssigneesResponse = OPFEngine.ProcessService.readNextAssigneeCandidatesForTask(taskId);
        boolean foundNextAssigneeMatch = false;
        for (User nextAssignee : nextAssigneesResponse.getData()) {
            if (nextAssignee.getId().equals(user2.getId())) {
                foundNextAssigneeMatch = true;
                break;
            }
        }
        Assert.assertTrue("Next assignee should match the one from process config", foundNextAssigneeMatch);

        TaskOperationsParams performTaskParams = new TaskOperationsParams();
        performTaskParams.setTaskId(taskId);
        performTaskParams.setAssigneeId(user2.getId());
        performTaskParams.setNoteText("test perform task");
        performTaskParams.setTaskDescription("test task description");

        ServiceResponse<Task> performTaskResponse = OPFEngine.ProcessService.performTask(new ServiceRequest<TaskOperationsParams>(performTaskParams));
        Assert.assertNotNull("Response shouldn't be null.", performTaskResponse);
        logger.info(performTaskResponse.getMessage());
        Assert.assertTrue("Response.success should be true", performTaskResponse.isSuccess());

        Task nextTask = performTaskResponse.getItem();
        Assert.assertEquals("Assignee id of next task should match the supplied one", user2.getId(), nextTask.getAssignee().getId());

        ServiceResponse<User> currAssigneesResponse = OPFEngine.ProcessService.readCurrentAssigneeCandidatesForTask(nextTask.getId());
        boolean foundCurrAssigneeMatch = false;
        for (User currAssignee : currAssigneesResponse.getData()) {
            if (currAssignee.getId().equals(user2.getId())) {
                foundCurrAssigneeMatch = true;
                break;
            }
        }
        Assert.assertTrue("Current assignee should match the one from process config", foundCurrAssigneeMatch);

        User randomAssignee = currAssigneesResponse.getData().iterator().next();

        TaskOperationsParams assignTaskParams = new TaskOperationsParams();
        assignTaskParams.setTaskId(nextTask.getId());
        assignTaskParams.setAssigneeId(randomAssignee.getId());
        assignTaskParams.setNoteText("test assign task");
        ServiceResponse assignTaskResponse = OPFEngine.ProcessService.assignTask(new ServiceRequest<TaskOperationsParams>(assignTaskParams));
        logger.info(assignTaskResponse.getMessage());
        Assert.assertTrue("Response.success should be true", assignTaskResponse.isSuccess());

        ServiceResponse<User> prevAssigneesResponse = OPFEngine.ProcessService.readPreviousAssigneeCandidatesForTask(nextTask.getId());
        boolean foundPrevAssigneeMatch = false;
        for (User prevAssignee : prevAssigneesResponse.getData()) {
            if (prevAssignee.getId().equals(user1.getId())) {
                foundPrevAssigneeMatch = true;
                break;
            }
        }
        Assert.assertTrue("Previous assignee should match the one from process config", foundPrevAssigneeMatch);

        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        TaskOperationsParams rollbackTaskParams = new TaskOperationsParams();
        rollbackTaskParams.setTaskId(nextTask.getId());
        rollbackTaskParams.setAssigneeId(user1.getId());
        rollbackTaskParams.setNoteText("test rollback task");
        rollbackTaskParams.setTaskDescription("test task description rollback");

        ServiceResponse<Task> rollbackTaskResponse = OPFEngine.ProcessService.rollbackTask(new ServiceRequest<TaskOperationsParams>(rollbackTaskParams));
        logger.info(rollbackTaskResponse.getMessage());
        Assert.assertTrue("Response.success should be true", rollbackTaskResponse.isSuccess());

        ServiceResponse<Task> readTaskResponse2 = OPFEngine.ProcessService.readTask(rollbackTaskResponse.getItem().getId());
        Task activeTaskAfterRollback = readTaskResponse2.getData().iterator().next();
        Long activeTaskAfterRollbackStatusId = activeTaskAfterRollback.getActivity().getStatus().getId();
        Assert.assertEquals("Task status after rollback should match the initial status", firstTaskStatusId, activeTaskAfterRollbackStatusId);
    }


	@Test
	public void searchTasks() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        Long entityId1 = 1234l;
        Long entityId2 = 1234l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);

        User user1 = (User) testContextAttributes.get(Elements.USER1);
        User user2 = (User) testContextAttributes.get(Elements.USER2);

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId1);
        performCaseByIdParams.setAssigneeId(user2.getId());
        performCaseByIdParams.setExplanationId(null);
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage());

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        ServiceResponse<Task> searchTasksResponse = OPFEngine.ProcessService.searchTasks(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        logger.info(searchTasksResponse.getMessage());
        Assert.assertEquals("Total count of tasks should be 3.", new Integer(3), searchTasksResponse.getTotal());

        ServiceResponse<Task> searchTasksThroughActorsResponse = OPFEngine.ProcessService.searchTasksThroughActors(0, 10, "", "", "", true);
        logger.info(searchTasksThroughActorsResponse.getMessage());
        Assert.assertEquals("Total count of tasks should be 2.", new Integer(2), searchTasksThroughActorsResponse.getTotal());

        ServiceResponse<Task> searchTasksBelongingToUserActorsResponse = OPFEngine.ProcessService.searchTasksBelongingToUserActors(0, 10, "", "", "", true);
        logger.info(searchTasksBelongingToUserActorsResponse.getMessage());
        Assert.assertEquals("Total count of tasks should be 2.", new Integer(2), searchTasksThroughActorsResponse.getTotal());

        ServiceResponse<Task> searchClosedTasksForCurrentUserResponse = OPFEngine.ProcessService.searchClosedTasksForCurrentUser(0, 10, "", "", "");
        logger.info(searchClosedTasksForCurrentUserResponse.getMessage());
        Assert.assertEquals("Total count of tasks should be 1.", new Integer(1), searchClosedTasksForCurrentUserResponse.getTotal());

    }
}