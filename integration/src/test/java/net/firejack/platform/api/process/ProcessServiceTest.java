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
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
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
        PackageExecutionListener.class
})
public class ProcessServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessServiceTest.class);

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

	@Test
	public void crudProcess() {
        String testProcessName = "testProcess";

        Process process = createProcess(testProcessName, "testStatus1", "testStatus2", "testActivity1", "testActivity2", "firstTestExplanation1", "secondTestExplanation2");

        ServiceResponse<Process> response = OPFEngine.ProcessService.readProcess(process.getId());
        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Assert.assertNotNull("Read process shouldn't be null", response.getItem());
        Assert.assertEquals("Read process name doesn't match the created one", response.getItem().getName(), testProcessName);

        Process readProcess = response.getItem();
        String testProcessUpdatedName = "testProcessUpdated";
        readProcess.setName(testProcessUpdatedName);

        response = OPFEngine.ProcessService.updateProcess(readProcess.getId(), readProcess);
        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());

        response = OPFEngine.ProcessService.readProcess(response.getItem().getId());
        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Assert.assertNotNull("Read process shouldn't be null", response.getItem());
        Assert.assertEquals("Read process name doesn't match the updated one", response.getItem().getName(), testProcessUpdatedName);

        response = OPFEngine.ProcessService.deleteProcess(readProcess.getId());
        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());

        response = OPFEngine.ProcessService.readProcess(readProcess.getId());
        logger.info(response.getMessage());
        Assert.assertNull("Read process should be null after deletion", response.getItem());
	}

    @Test
    public void processSubElementsRead() {
        String testProcessName = "testProcess";

        String testStatus1 = "testStatus1";
        String testStatus2 = "testStatus2";
        Set<String> testStatuses = new HashSet<String>();
        testStatuses.add(testStatus1);
        testStatuses.add(testStatus2);

        String testActivity1 = "testActivity1";
        String testActivity2 = "testActivity2";
        Set<String> testActivities = new HashSet<String>();
        testActivities.add(testActivity1);
        testActivities.add(testActivity2);

        String testExplanation1 = "firstTestExplanation1";
        String testExplanation2 = "secondTestExplanation2";
        Set<String> testExplanations = new HashSet<String>();
        testExplanations.add(testExplanation1);
        testExplanations.add(testExplanation2);

        Process process = createProcess(testProcessName, testStatus1, testStatus2, testActivity1, testActivity2, testExplanation1, testExplanation2);

        ServiceResponse<Status> readStatusesResponse = OPFEngine.ProcessService.readStatusesByProcess(process.getId());
        logger.info(readStatusesResponse.getMessage());
        Assert.assertNotNull("Read statuses should return non null data", readStatusesResponse.getData());
        Assert.assertEquals("Read statuses should have the count of 2", new Integer(2), readStatusesResponse.getTotal());
        for (Status status : readStatusesResponse.getData()) {
            Assert.assertTrue("Read status '" + status.getName() + "' exists, but was not created", testStatuses.contains(status.getName()));
        }

        ServiceResponse<Activity> readActivitiesResponse = OPFEngine.ProcessService.readActivitiesByProcess(process.getId());
        logger.info(readActivitiesResponse.getMessage());
        Assert.assertNotNull("Read activities should return non null data", readActivitiesResponse.getData());
        Assert.assertEquals("Read activities should have the count of 2", new Integer(2), readActivitiesResponse.getTotal());
        for (Activity activity : readActivitiesResponse.getData()) {
            Assert.assertTrue("Read activity '" + activity.getName() + "' exists, but was not created", testActivities.contains(activity.getName()));
        }

        ServiceResponse<CaseExplanation> searchExplanationsResponse = OPFEngine.ProcessService.searchProcessExplanations(process.getId(), null);
        logger.info(searchExplanationsResponse.getMessage());
        Assert.assertNotNull("Read explanations should return non null data", searchExplanationsResponse.getData());
        Assert.assertEquals("Read explanations should have the count of 2", new Integer(2), searchExplanationsResponse.getTotal());
        
        ServiceResponse<CaseExplanation> searchExplanations2Response = OPFEngine.ProcessService.searchProcessExplanations(process.getId(), testExplanation1.substring(1));
        logger.info(searchExplanations2Response.getMessage());
        Assert.assertEquals("Read explanations should have the count of 1 (search limited by term)", new Integer(1), searchExplanations2Response.getTotal());
    }

    private Process createProcess(String testProcessName, String testStatus1, String testStatus2, String testActivity1, String testActivity2, String testExplanation1, String testExplanation2) {
        Long packageId = ((Package) testContextAttributes.get(Elements.PACKAGE)).getId();

        Process process = new Process();
        process.setParentId(packageId);

        process.setName(testProcessName);

        Long directoryId = createDirectory();

        User user1 = createUser(directoryId, "username1");
        User user2 = createUser(directoryId, "username2");

        UserActor userActor1 = new UserActor();
        userActor1.setUser(user1);
        Actor actor1 = new Actor();
        actor1.setName("actor1");
        actor1.setDistributionEmail("testmail1@mailx.com");
        actor1.setUserActors(Arrays.asList(userActor1));
        actor1.setParentId(packageId);

        UserActor userActor2 = new UserActor();
        userActor2.setUser(user2);
        Actor actor2 = new Actor();
        actor2.setName("actor2");
        actor2.setDistributionEmail("testmail2@mailx.com");
        actor2.setUserActors(Arrays.asList(userActor2));
        actor2.setParentId(packageId);

        ServiceResponse<Actor> createActorResponse1 = OPFEngine.ProcessService.createActor(actor1);
        logger.info(createActorResponse1.getMessage());
        ServiceResponse<Actor> createActorResponse2 = OPFEngine.ProcessService.createActor(actor2);
        logger.info(createActorResponse2.getMessage());

        List<Activity> activities = new ArrayList<Activity>();
        Activity activity = new Activity();
        activity.setSortPosition(1);
        activity.setActor(createActorResponse1.getItem());
        activity.setName(testActivity1);
        activity.setStatus(new Status());
        activities.add(activity);
        Activity activity2 = new Activity();
        activity2.setSortPosition(2);
        activity2.setActor(createActorResponse2.getItem());
        activity2.setName(testActivity2);
        activity2.setStatus(new Status());
        activities.add(activity2);

        List<Status> statuses = new ArrayList<Status>();
        Status status = new Status();
        status.setName(testStatus1);
        status.setSortPosition(1);
        activity.getStatus().setId(-1l); // must match -1 * order
        statuses.add(status);
        Status status2 = new Status();
        status2.setName(testStatus2);
        status2.setSortPosition(2);
        activity2.getStatus().setId(-2l); // must match -1 * order
        statuses.add(status2);

        List<CaseExplanation> explanations = new ArrayList<CaseExplanation>();
        CaseExplanation explanation1 = new CaseExplanation();
        explanation1.setShortDescription(testExplanation1);
        explanation1.setLongDescription("description of testExplanation1");
        explanations.add(explanation1);
        CaseExplanation explanation2 = new CaseExplanation();
        explanation2.setShortDescription(testExplanation2);
        explanation2.setLongDescription("description of testExplanation2");
        explanations.add(explanation2);
        
        process.setActivities(activities);
        process.setStatuses(statuses);
        process.setExplanations(explanations);

        ServiceResponse<Process> response = OPFEngine.ProcessService.createProcess(process);

        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Assert.assertNotNull("Created process shouldn't be null", response.getItem());
        Assert.assertNotNull("Created process id shouldn't be null", response.getItem().getId());

        return response.getItem();
    }

    private Long createDirectory() {
        net.firejack.platform.api.registry.domain.Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);

        Directory directory = new Directory();
		directory.setParentId(pkg.getId());
		directory.setName("testUserDirectory");
		directory.setStatus(RegistryNodeStatus.UNKNOWN);
		directory.setDirectoryType(DirectoryType.DATABASE);
		ServiceResponse<RegistryNodeTree> directoryResponse = OPFEngine.DirectoryService.createDirectory(directory);
        Assert.assertNotNull("Can't be create response null.", directoryResponse);
        logger.info(directoryResponse.getMessage());
		Assert.assertNotNull("Can't be create item null.", directoryResponse.getItem());

        return directoryResponse.getItem().getId();
    }

    private User createUser(Long directoryId, String username) {

        List<Permission> rolePermissions = new ArrayList<Permission>();

        net.firejack.platform.api.registry.domain.Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);
        Role role = new Role();
        role.setName("test-role" + username);
        role.setPath(PackageExecutionListener.PACKAGE_LOOKUP);
        role.setLookup(PackageExecutionListener.PACKAGE_LOOKUP + ".test-role" + username);
        role.setParentId(pkg.getId());
        role.setPermissions(rolePermissions);

        ServiceResponse<Role> roleResponse = OPFEngine.AuthorityService.createRole(new ServiceRequest<Role>(role));
        Assert.assertNotNull("OPFEngine.AuthorityService.createRole(request)", roleResponse);
        Assert.assertTrue("Response has failure status.", roleResponse.isSuccess());
        role = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", role);
        Assert.assertNotNull("Returned role should have id.", role.getId());

		User user = new User();
		user.setRegistryNodeId(directoryId);
		user.setUsername(username);
		user.setPassword("123123");
		user.setPasswordConfirm("123123");
		user.setEmail("123testUser" + username + "@mail.ru");
		user.setFirstName("testUser" + username);
        user.setRoles(roleResponse.getData());

		ServiceResponse<User> response = OPFEngine.DirectoryService.createUser(user);
        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

        return response.getItem();
    }

}
