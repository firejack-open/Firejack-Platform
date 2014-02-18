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
import net.firejack.platform.api.process.model.CaseActionType;
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
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        ProcessExecutionListener.class
})
public class ProcessCaseServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessCaseServiceTest.class);

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
	public void startStopCaseById() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        Long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        ServiceResponse<Case> caseSearchResponse = OPFEngine.ProcessService.searchCases(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        Assert.assertNotNull("Response shouldn't be null.", caseSearchResponse);
        Assert.assertEquals("Total count of cases should be 1.", caseSearchResponse.getTotal(), new Integer(1));

        Case foundCase = caseSearchResponse.getData().iterator().next();
        Assert.assertNotNull("First case in result list shouldn't be null.", foundCase);
        Assert.assertEquals("CaseId returned from startCase should be equal to the found caseId by searchCase", caseId, foundCase.getId());

        CaseOperationsParams stopCaseParams = new CaseOperationsParams();
        stopCaseParams.setCaseId(caseId);

        ServiceResponse stopResponse = OPFEngine.ProcessService.stopCaseById(new ServiceRequest<CaseOperationsParams>(stopCaseParams));
        logger.info(stopResponse.getMessage());
        Assert.assertTrue("Response.success should be true", stopResponse.isSuccess());
	}

    @Test
    public void startStopCaseByLookup() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        ServiceResponse<Case> caseSearchResponse = OPFEngine.ProcessService.searchCases(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        Assert.assertNotNull("Response shouldn't be null.", caseSearchResponse);
        Assert.assertEquals("Total count of cases should be 1.", caseSearchResponse.getTotal(), new Integer(1));

        Case foundCase = caseSearchResponse.getData().iterator().next();
        Assert.assertNotNull("First case in result list shouldn't be null.", foundCase);
        Assert.assertEquals("CaseId returned from startCase should be equal to the found caseId by searchCase", caseId, foundCase.getId());

        CaseOperationsParams stopCaseParams = new CaseOperationsParams();
        stopCaseParams.setProcessLookup(processLookup);
        stopCaseParams.setEntityId(entityId);
        stopCaseParams.setEntityType(OpenFlame.SYSTEM);

        ServiceResponse stopResponse = OPFEngine.ProcessService.stopCaseByLookup(new ServiceRequest<CaseOperationsParams>(stopCaseParams));

        Assert.assertNotNull("Response shouldn't be null.", stopResponse);
        logger.info(stopResponse.getMessage());
        Assert.assertTrue("Response.success should be true", stopResponse.isSuccess());
    }

    @Test
    public void performRollbackCaseById() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        ServiceResponse<User> nextAssigneesResponse = OPFEngine.ProcessService.readNextAssigneeCandidates(caseId);
        Assert.assertNotNull("Response shouldn't be null.", nextAssigneesResponse);

        List<User> nextAssignees = nextAssigneesResponse.getData();
        Assert.assertFalse("Next assignees list shouldn't be empty", nextAssignees.isEmpty());
        User nextAssignee = nextAssignees.iterator().next();

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId);
        performCaseByIdParams.setAssigneeId(nextAssignee.getId());
        performCaseByIdParams.setExplanationId(null);
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage());
        Assert.assertTrue("Perform case returned false success", performCaseResponse.isSuccess());

        ServiceResponse<User> prevAssigneesResponse = OPFEngine.ProcessService.readPreviousAssigneeCandidates(caseId);
        Assert.assertNotNull("Response shouldn't be null.", prevAssigneesResponse);

        List<User> prevAssignees = prevAssigneesResponse.getData();
        Assert.assertFalse("Previous assignees list shouldn't be empty", prevAssignees.isEmpty());
        User prevAssignee = prevAssignees.iterator().next();

        CaseOperationsParams rollbackCaseByIdParams = new CaseOperationsParams();
        rollbackCaseByIdParams.setCaseId(caseId);
        rollbackCaseByIdParams.setAssigneeId(prevAssignee.getId());
        rollbackCaseByIdParams.setExplanationId(null);
        rollbackCaseByIdParams.setNoteText("rollbacking test");
        rollbackCaseByIdParams.setTaskDescription("task rolled back by test");

        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse rollbackCaseResponse = OPFEngine.ProcessService.rollbackCaseById(new ServiceRequest<CaseOperationsParams>(rollbackCaseByIdParams));
        logger.info(rollbackCaseResponse.getMessage());
        Assert.assertTrue("Rollback case returned false success", rollbackCaseResponse.isSuccess());
    }

    @Test
    public void performRollbackCaseByLookup() {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        long entityId1 = 12l;
        long entityId2 = 56l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);

        ServiceResponse<User> nextAssigneesResponse = OPFEngine.ProcessService.readNextAssigneeCandidates(caseId1);
        Assert.assertNotNull("Response shouldn't be null.", nextAssigneesResponse);

        List<User> nextAssignees = nextAssigneesResponse.getData();
        Assert.assertFalse("Next assignees list shouldn't be empty", nextAssignees.isEmpty());
        User nextAssignee = nextAssignees.iterator().next();

        CaseOperationsParams performCaseByLookupParams = new CaseOperationsParams();
        performCaseByLookupParams.setProcessLookup(processLookup);
        performCaseByLookupParams.setEntityType(OpenFlame.SYSTEM);
        performCaseByLookupParams.setEntityIds(Arrays.asList(entityId1, entityId2));
        performCaseByLookupParams.setAssigneeId(nextAssignee.getId());
        performCaseByLookupParams.setExplanationId(null);
        performCaseByLookupParams.setNoteText("performing test");

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseByLookup(new ServiceRequest<CaseOperationsParams>(performCaseByLookupParams));
        logger.info(performCaseResponse.getMessage());
        Assert.assertTrue("Perform case returned false success", performCaseResponse.isSuccess());

        ServiceResponse<User> prevAssigneesResponse = OPFEngine.ProcessService.readPreviousAssigneeCandidates(caseId2);
        Assert.assertNotNull("Response shouldn't be null.", prevAssigneesResponse);

        List<User> prevAssignees = prevAssigneesResponse.getData();
        Assert.assertFalse("Previous assignees list shouldn't be empty", prevAssignees.isEmpty());
        User prevAssignee = prevAssignees.iterator().next();

        CaseOperationsParams rollbackCaseByIdParams = new CaseOperationsParams();
        rollbackCaseByIdParams.setProcessLookup(processLookup);
        rollbackCaseByIdParams.setEntityType(OpenFlame.SYSTEM);
        rollbackCaseByIdParams.setEntityIds(Arrays.asList(entityId1, entityId2));
        rollbackCaseByIdParams.setAssigneeId(prevAssignee.getId());
        rollbackCaseByIdParams.setExplanationId(null);
        rollbackCaseByIdParams.setNoteText("rollbacking test");

        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse rollbackCaseResponse = OPFEngine.ProcessService.rollbackCaseByLookup(new ServiceRequest<CaseOperationsParams>(rollbackCaseByIdParams));
        logger.info(rollbackCaseResponse.getMessage());
        Assert.assertTrue("Rollback case returned false success", rollbackCaseResponse.isSuccess());
    }

    @Test
    public void assignReadTeam() {
        User user1 = (User) testContextAttributes.get(Elements.USER1);
        Actor actor1 = (Actor) testContextAttributes.get(Elements.ACTOR1);
        User user2 = (User) testContextAttributes.get(Elements.USER2);
        Actor actor2 = (Actor) testContextAttributes.get(Elements.ACTOR2);

        long entityId = 12l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        UserActor userActor1 = new UserActor();
        userActor1.setUser(user1);
        userActor1.setProcessCase(new Case());
        userActor1.getProcessCase().setId(caseId);
        userActor1.setActor(actor1);

        UserActor userActor2 = new UserActor();
        userActor2.setUser(user2);
        userActor2.setProcessCase(new Case());
        userActor2.getProcessCase().setId(caseId);
        userActor2.setActor(actor2);

        ServiceResponse assignTeamResponse = OPFEngine.ProcessService.assignTeamToCase(new ServiceRequest<UserActor>(Arrays.asList(userActor1, userActor2)));
        logger.info(assignTeamResponse.getMessage());
        Assert.assertTrue("Assign team to case returned false success", assignTeamResponse.isSuccess());

        ServiceResponse<UserActor> readTeamResponse = OPFEngine.ProcessService.readTeamForCase(caseId);
        logger.info(readTeamResponse.getMessage());
        Assert.assertEquals("Read team should contain 2 members", new Integer(2), readTeamResponse.getTotal());
        UserActor readTeamMember1 = readTeamResponse.getData().get(0);
        UserActor readTeamMember2 = readTeamResponse.getData().get(1);
        boolean order1 = readTeamMember1.getUser().getId().equals(user1.getId()) && readTeamMember2.getUser().getId().equals(user2.getId());
        boolean order2 = readTeamMember1.getUser().getId().equals(user2.getId()) && readTeamMember2.getUser().getId().equals(user1.getId());
        Assert.assertTrue("Read team members are not the same as the assigned ones", order1 || order2);

        ServiceResponse<User> nextTeamMemberResponse = OPFEngine.ProcessService.readNextTeamMemberForCase(caseId);
        logger.info(nextTeamMemberResponse.getMessage());
        User nextTeamMember = nextTeamMemberResponse.getItem();
        Assert.assertNotNull("Next team member shouldn't be null", nextTeamMember);
        Assert.assertEquals("Read next team member is not the same as assigned one", user2.getId(), nextTeamMember.getId());

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId);
        performCaseByIdParams.setAssigneeId(nextTeamMember.getId());
        performCaseByIdParams.setExplanationId(null);
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage());
        
        ServiceResponse<User> prevTeamMemberResponse = OPFEngine.ProcessService.readPreviousTeamMemberForCase(caseId);
        logger.info(prevTeamMemberResponse.getMessage());
        User prevTeamMember = prevTeamMemberResponse.getItem();
        Assert.assertNotNull("Previous team member shouldn't be null", prevTeamMember);
        Assert.assertEquals("Read previous team member is not the same as assigned one", user1.getId(), prevTeamMember.getId());
    }

    @Test
    public void searchCasesThroughActors() {
        long entityId1 = 12l;
        long entityId2 = 34l;
        long entityId3 = 56l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);
        Long caseId3 = startCase(OpenFlame.SYSTEM, entityId3);

        Set<Long> initialCaseIds = new HashSet<Long>();
        initialCaseIds.add(caseId1);
        initialCaseIds.add(caseId2);
        initialCaseIds.add(caseId3);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse<Case> searchThroughActorsResponse = OPFEngine.ProcessService.searchCasesThroughActors(0, 10, "", "", "", true);
        logger.info(searchThroughActorsResponse.getMessage());

        Assert.assertEquals("Number of found cases should be the same as number of created cases", new Integer(3), searchThroughActorsResponse.getTotal());

        Set<Long> searchResultCaseIds = new HashSet<Long>();
        List<Case> cases = searchThroughActorsResponse.getData();
        for (Case aCase : cases) {
            searchResultCaseIds.add(aCase.getId());
        }
        Assert.assertEquals("Initial cases should be the same as found ones", initialCaseIds, searchResultCaseIds);
    }

    @Test
    public void searchCasesBelongingToUserActors() {
        long entityId1 = 12l;
        long entityId2 = 34l;
        long entityId3 = 56l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);
        Long caseId3 = startCase(OpenFlame.SYSTEM, entityId3);

        Set<Long> initialCaseIds = new HashSet<Long>();
        initialCaseIds.add(caseId1);
        initialCaseIds.add(caseId2);
        initialCaseIds.add(caseId3);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse<Case> searchThroughActorsResponse = OPFEngine.ProcessService.searchCasesBelongingToUserActors(0, 10, "", "", "", true);
        logger.info(searchThroughActorsResponse.getMessage());

        Assert.assertEquals("Number of found cases should be the same as number of created cases", new Integer(3), searchThroughActorsResponse.getTotal());

        Set<Long> searchResultCaseIds = new HashSet<Long>();
        List<Case> cases = searchThroughActorsResponse.getData();
        for (Case aCase : cases) {
            searchResultCaseIds.add(aCase.getId());
        }
        Assert.assertEquals("Initial cases should be the same as found ones", initialCaseIds, searchResultCaseIds);
    }

    @Test
    public void searchClosedCasesForCurrentUser() {
        long entityId1 = 12l;
        long entityId2 = 34l;
        long entityId3 = 56l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);
        Long caseId3 = startCase(OpenFlame.SYSTEM, entityId3);

        Set<Long> initialCaseIds = new HashSet<Long>();
        initialCaseIds.add(caseId1);
        initialCaseIds.add(caseId2);
        // perform only 2 cases, each 2 times (they will be closed after that, since they have 2 activities)

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        User user2 = (User) testContextAttributes.get(Elements.USER2);

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId1);
        performCaseByIdParams.setAssigneeId(user2.getId());
        performCaseByIdParams.setExplanationId(null);
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage()); // perform case1 first time

        performCaseByIdParams.setCaseId(caseId2);
        performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage()); // perform case2 first time

        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        performCaseByIdParams.setAssigneeId(null);
        performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage()); // perform case2 second time

        performCaseByIdParams.setCaseId(caseId1);
        performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage());// perform case1 second time

        ServiceResponse<Case> searchThroughActorsResponse = OPFEngine.ProcessService.searchClosedCasesForCurrentUser(0, 10, "", "", "");
        logger.info(searchThroughActorsResponse.getMessage());

        Assert.assertEquals("Number of found cases should be the same as number of created cases", new Integer(2), searchThroughActorsResponse.getTotal());

        Set<Long> searchResultCaseIds = new HashSet<Long>();
        List<Case> cases = searchThroughActorsResponse.getData();
        for (Case aCase : cases) {
            searchResultCaseIds.add(aCase.getId());
        }
        Assert.assertEquals("Initial cases should be the same as found ones", initialCaseIds, searchResultCaseIds);
    }

    @Test
    public void searchActiveCases() {
        long entityId1 = 12l;
        long entityId2 = 34l;
        long entityId3 = 56l;

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        Long caseId2 = startCase(OpenFlame.SYSTEM, entityId2);
        Long caseId3 = startCase(OpenFlame.SYSTEM, entityId3);

        Set<Long> initialCaseIds = new HashSet<Long>();
        initialCaseIds.add(caseId1);
        initialCaseIds.add(caseId2);
        initialCaseIds.add(caseId3);

        ServiceResponse<Case> searchActiveCasesResponse = OPFEngine.ProcessService.searchActiveCases(OpenFlame.SYSTEM, entityId1);
        logger.info(searchActiveCasesResponse.getMessage());
        Assert.assertEquals("Number of found cases should be 1", new Integer(1), searchActiveCasesResponse.getTotal());
        Assert.assertEquals("Created caseId should match the found one", caseId1, searchActiveCasesResponse.getData().get(0).getId());

        searchActiveCasesResponse = OPFEngine.ProcessService.searchActiveCases(OpenFlame.SYSTEM, null); // find all with the same entityType
        logger.info(searchActiveCasesResponse.getMessage());
        Assert.assertEquals("Number of found cases should be 3", new Integer(3), searchActiveCasesResponse.getTotal());

        Set<Long> searchResultCaseIds = new HashSet<Long>();
        List<Case> cases = searchActiveCasesResponse.getData();
        for (Case aCase : cases) {
            searchResultCaseIds.add(aCase.getId());
        }
        Assert.assertEquals("Initial cases should be the same as found ones", initialCaseIds, searchResultCaseIds);

    }

    @Test
    public void resetCase() {
        Long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse<Case> readCaseResponse = OPFEngine.ProcessService.readCase(caseId);
        logger.info(readCaseResponse.getMessage());
        Long statusIdInitial = readCaseResponse.getItem().getStatus().getId();

        User user2 = (User) testContextAttributes.get(Elements.USER2);

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId);
        performCaseByIdParams.setAssigneeId(user2.getId());
        performCaseByIdParams.setExplanationId(null);
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage());

        CaseOperationsParams resetCaseParams = new CaseOperationsParams();
        resetCaseParams.setCaseId(caseId);
        resetCaseParams.setNoteText("test reset case");

        ServiceResponse resetCaseResponse = OPFEngine.ProcessService.resetCase(new ServiceRequest<CaseOperationsParams>(resetCaseParams));
        logger.info(resetCaseResponse.getMessage());
        Assert.assertTrue("Reset case returned false success", resetCaseResponse.isSuccess());

        readCaseResponse = OPFEngine.ProcessService.readCase(caseId);
        logger.info(readCaseResponse.getMessage());
        Long statusIdAfterReset = readCaseResponse.getItem().getStatus().getId();

        Assert.assertEquals("Status of case after reset should be equal to initial case status", statusIdInitial, statusIdAfterReset);
    }

    @Test
    public void updateCaseDescription() {
        Long entityId = 1234l;
        String caseDescription = "test updated description";

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        CaseOperationsParams updateCaseParams = new CaseOperationsParams();
        updateCaseParams.setCaseId(caseId);
        updateCaseParams.setCaseDescription(caseDescription);

        ServiceResponse resetCaseResponse = OPFEngine.ProcessService.updateCaseDescription(new ServiceRequest<CaseOperationsParams>(updateCaseParams));
        logger.info(resetCaseResponse.getMessage());
        Assert.assertTrue("Update case description returned false success", resetCaseResponse.isSuccess());

        ServiceResponse<Case> readCaseResponse = OPFEngine.ProcessService.readCase(caseId);
        logger.info(readCaseResponse.getMessage());
        Assert.assertEquals("Updated case description should match the supplied one", caseDescription, readCaseResponse.getItem().getDescription());
    }

    @Test
    public void readCaseActions() {
        long entityId1 = 12l;
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);

        List<CaseActionType> caseActions = new ArrayList<CaseActionType>();

        Long caseId1 = startCase(OpenFlame.SYSTEM, entityId1);
        caseActions.add(CaseActionType.START);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER1_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        User user1 = (User) testContextAttributes.get(Elements.USER1);
        User user2 = (User) testContextAttributes.get(Elements.USER2);

        CaseOperationsParams performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId1);
        performCaseByIdParams.setAssigneeId(user2.getId());
        performCaseByIdParams.setNoteText("performing test");
        performCaseByIdParams.setTaskDescription("task performed by test");

        ServiceResponse performCaseResponse = OPFEngine.ProcessService.performCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(performCaseResponse.getMessage()); // perform case1 first time
        caseActions.add(CaseActionType.PERFORM_ACTIVITY);

        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        performCaseByIdParams = new CaseOperationsParams();
        performCaseByIdParams.setCaseId(caseId1);
        performCaseByIdParams.setAssigneeId(user1.getId());
        performCaseByIdParams.setNoteText("rollbacking test");
        performCaseByIdParams.setTaskDescription("task rollbacked by test");

        ServiceResponse rollbackCaseResponse = OPFEngine.ProcessService.rollbackCaseById(new ServiceRequest<CaseOperationsParams>(performCaseByIdParams));
        logger.info(rollbackCaseResponse.getMessage()); // rollback case1 first time
        caseActions.add(CaseActionType.PREVIOUS_ACTIVITY);

        SearchTaskCaseFilter searchTaskCaseFilter = new SearchTaskCaseFilter();
        searchTaskCaseFilter.setProcessId(process.getId());
        searchTaskCaseFilter.setActive(true);
        ServiceResponse<Task> searchTasksResponse = OPFEngine.ProcessService.searchTasks(new ServiceRequest<SearchTaskCaseFilter>(searchTaskCaseFilter), null);
        logger.info(searchTasksResponse.getMessage());
        Task activeTask = searchTasksResponse.getData().iterator().next(); // should be only one

        TaskOperationsParams assignTaskParams = new TaskOperationsParams();
        assignTaskParams.setTaskId(activeTask.getId());
        assignTaskParams.setAssigneeId(user1.getId());
        assignTaskParams.setNoteText("test assign task");

        ServiceResponse assignTaskResponse = OPFEngine.ProcessService.assignTask(new ServiceRequest<TaskOperationsParams>(assignTaskParams));
        logger.info(assignTaskResponse.getMessage());
        caseActions.add(CaseActionType.ASSIGNMENT);

        CaseOperationsParams resetCaseParams = new CaseOperationsParams();
        resetCaseParams.setCaseId(caseId1);
        resetCaseParams.setNoteText("test reset case");

        ServiceResponse resetCaseResponse = OPFEngine.ProcessService.resetCase(new ServiceRequest<CaseOperationsParams>(resetCaseParams));
        logger.info(resetCaseResponse.getMessage());
        caseActions.add(CaseActionType.RESET);

        CaseOperationsParams stopCaseParams = new CaseOperationsParams();
        stopCaseParams.setCaseId(caseId1);

        ServiceResponse stopResponse = OPFEngine.ProcessService.stopCaseById(new ServiceRequest<CaseOperationsParams>(stopCaseParams));
        logger.info(stopResponse.getMessage());
        caseActions.add(CaseActionType.STOP);


        ServiceResponse<CaseAction> caseActionResponse = OPFEngine.ProcessService.readCaseActionsByCase(caseId1);
        logger.info(caseActionResponse.getMessage());

        for (int i = 0; i < caseActions.size(); i++) {
            CaseActionType caseActionType = caseActions.get(i);
            CaseAction readCaseAction = caseActionResponse.getData().get(i);

            Assert.assertEquals("Read case action doesn't match the expected one", caseActionType, readCaseAction.getType());
        }
    }
}