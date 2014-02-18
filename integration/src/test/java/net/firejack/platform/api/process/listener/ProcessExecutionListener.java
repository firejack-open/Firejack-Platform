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

package net.firejack.platform.api.process.listener;

import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.Search;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.Paging;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProcessExecutionListener extends AbstractTestExecutionListener {

    public static final String PROCESS_NAME = "test_process";
    public static final String USER1_USERNAME = "user1";
    public static final String USER2_USERNAME = "user2";
    public static final String USER_PASSWORD = "123123";

    private static final Logger logger = Logger.getLogger(ProcessExecutionListener.class);
    private Map<Elements, AbstractDTO> testContextAttributes;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        testContextAttributes = (Map<Elements, AbstractDTO>) testContext.getApplicationContext().getBean("testContextAttributes");
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Process process = new Process();
        Long packageId = ((Package) testContextAttributes.get(Elements.PACKAGE)).getId();
        
        process.setParentId(packageId);
        process.setName(PROCESS_NAME);

        Long directoryId = createDirectory();

        User user1 = createUser(directoryId, USER1_USERNAME);
        testContextAttributes.put(Elements.USER1, user1);
        User user2 = createUser(directoryId, USER2_USERNAME);
        testContextAttributes.put(Elements.USER2, user2);

        UserActor userActor1 = new UserActor();
        userActor1.setUser(user1);
        Actor actor1 = new Actor();
        actor1.setName("firstActorNumber1");
        actor1.setDistributionEmail("testmail1@mailx.com");
        actor1.setUserActors(Arrays.asList(userActor1));
        actor1.setParentId(packageId);

        UserActor userActor2 = new UserActor();
        userActor2.setUser(user2);
        Actor actor2 = new Actor();
        actor2.setName("secondActorNumber2");
        actor2.setDistributionEmail("testmail2@mailx.com");
        actor2.setUserActors(Arrays.asList(userActor2));
        actor2.setParentId(packageId);

        ServiceResponse<Actor> createActorResponse1 = OPFEngine.ProcessService.createActor(actor1);
        logger.info(createActorResponse1.getMessage());
        Actor createdActor1 = createActorResponse1.getItem();
        testContextAttributes.put(Elements.ACTOR1, createdActor1);

        ServiceResponse<Actor> createActorResponse2 = OPFEngine.ProcessService.createActor(actor2);
        logger.info(createActorResponse2.getMessage());
        Actor createdActor2 = createActorResponse2.getItem();
        testContextAttributes.put(Elements.ACTOR2, createdActor2);

        List<Activity> activities = new ArrayList<Activity>();
        Activity activity = new Activity();
        activity.setActivityType(ActivityType.HUMAN);
        activity.setSortPosition(1);
        activity.setActor(createdActor1);
        activity.setName("testActivity1");
        activity.setStatus(new Status());
        activities.add(activity);
        Activity activity2 = new Activity();
        activity2.setActivityType(ActivityType.HUMAN);
        activity2.setSortPosition(2);
        activity2.setActor(createdActor2);
        activity2.setName("testActivity2");
        activity2.setStatus(new Status());
        activities.add(activity2);

        List<Status> statuses = new ArrayList<Status>();
        Status status = new Status();
        status.setName("testStatus1");
        status.setSortPosition(1);
        activity.getStatus().setId(-1l); // must match -1 * order
        statuses.add(status);
        Status status2 = new Status();
        status2.setName("testStatus2");
        status2.setSortPosition(2);
        activity2.getStatus().setId(-2l); // must match -1 * order
        statuses.add(status2);

        process.setActivities(activities);
        process.setStatuses(statuses);

        ServiceResponse<Process> response = OPFEngine.ProcessService.createProcess(process);

        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Created process shouldn't be null", response.getItem());
		Assert.assertNotNull("Created process id shouldn't be null", response.getItem().getId());

        ServiceResponse<Process> readResponse = OPFEngine.ProcessService.readProcess(response.getItem().getId());

        testContextAttributes.put(Elements.PROCESS, readResponse.getItem());
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        ServiceResponse response = OPFEngine.ProcessService.deleteProcess(process.getId());
        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
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

	    ServiceResponse<Search> action = OPFEngine.RegistryService.getSearchResult(OpenFlame.PACKAGE, null, OpenFlame.ROOT_DOMAIN, "PACKAGE", new Paging());
	    Long id = action.getItem().getId();
	    ServiceResponse<Permission> permissionServiceResponse = OPFEngine.AuthorityService.readAllPermissionsByRegistryNodeId(id, null, null);

//        ServiceResponse<Permission> permissionResponse = OPFEngine.AuthorityService.readAllPermissionsByRegistryNodeId(navigationElement.getParentId());
//        Assert.assertTrue("Permission list should not be null", permissionResponse.isSuccess());
//        logger.info(permissionResponse.getMessage());
//        Assert.assertEquals("Permission list should have four elements", 4, permissionResponse.getData().size());
//        rolePermissions.addAll(permissionResponse.getData());

        Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);
        Role role = new Role();
        role.setName("test-role" + username);
        role.setPath(PackageExecutionListener.PACKAGE_LOOKUP);
        role.setLookup(PackageExecutionListener.PACKAGE_LOOKUP + ".test-role" + username);
        role.setParentId(pkg.getId());
        role.setPermissions(permissionServiceResponse.getData());

        ServiceResponse<Role> roleResponse = OPFEngine.AuthorityService.createRole(new ServiceRequest<Role>(role));
        Assert.assertNotNull("OPFEngine.AuthorityService.createRole(request)", roleResponse);
        Assert.assertTrue("Response has failure status.", roleResponse.isSuccess());
        role = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", role);
        Assert.assertNotNull("Returned role should have id.", role.getId());

		User user = new User();
		user.setRegistryNodeId(directoryId);
		user.setUsername(username);
		user.setPassword(USER_PASSWORD);
		user.setPasswordConfirm(USER_PASSWORD);
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
