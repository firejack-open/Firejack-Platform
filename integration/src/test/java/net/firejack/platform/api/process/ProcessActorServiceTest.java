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
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.process.domain.Actor;
import net.firejack.platform.api.process.domain.UserActor;
import net.firejack.platform.api.process.listener.ProcessExecutionListener;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
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
import java.util.ArrayList;
import java.util.Arrays;
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
public class ProcessActorServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessActorServiceTest.class);

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
	public void crudActor() {
        String testActorName = "testActor";
        Long packageId = ((Package) testContextAttributes.get(Elements.PACKAGE)).getId();

        Long directoryId = createDirectory();

        User user = createUser(directoryId, "username1");

        UserActor userActor = new UserActor();
        userActor.setUser(user);
        Actor actor = new Actor();
        actor.setName(testActorName);
        actor.setDistributionEmail("testmail1@mailx.com");
        actor.setUserActors(Arrays.asList(userActor));
        actor.setParentId(packageId);

        ServiceResponse<Actor> createActorResponse = OPFEngine.ProcessService.createActor(actor);
        logger.info(createActorResponse.getMessage());
        Assert.assertNotNull("Created actor shouldn't be null", createActorResponse.getItem());

        Long actorId = createActorResponse.getItem().getId();

        ServiceResponse<Actor> readActorResponse = OPFEngine.ProcessService.readActor(actorId);
        logger.info(readActorResponse.getMessage());
        Assert.assertNotNull("Read actor shouldn't be null", readActorResponse.getItem());
        Assert.assertEquals("Read actor name should be the same as created one", testActorName, readActorResponse.getItem().getName());

        Actor readActor = readActorResponse.getItem();
        String nameUpdated = "testActor updated";
        readActor.setName(nameUpdated);

        ServiceResponse<Actor> updateActorResponse = OPFEngine.ProcessService.updateActor(actorId, readActor);
        logger.info(updateActorResponse.getMessage());
        Assert.assertNotNull("Updated actor shouldn't be null", updateActorResponse.getItem());

        ServiceResponse<Actor> readActorAfterUpdateResponse = OPFEngine.ProcessService.readActor(actorId);
        logger.info(readActorAfterUpdateResponse.getMessage());
        Assert.assertEquals("Read actor name should be the same as created one", nameUpdated, readActorAfterUpdateResponse.getItem().getName());

        ServiceResponse deleteActorResponse = OPFEngine.ProcessService.deleteActor(actorId);
        logger.info(deleteActorResponse.getMessage());
        Assert.assertTrue("Delete actor response success should be true", deleteActorResponse.isSuccess());
        
        ServiceResponse<Actor> readActorAfterDeleteResponse = OPFEngine.ProcessService.readActor(actorId);
        logger.info(readActorAfterDeleteResponse.getMessage());
        Assert.assertNull("After delete, actor should be null", readActorAfterDeleteResponse.getItem());
	}

    @Test
	public void searchActors() {
        Actor actor = (Actor) testContextAttributes.get(Elements.ACTOR1);

        String subName = actor.getName().substring(1, actor.getName().length() - 1);
        ServiceResponse<Actor> searchActorsResponse = OPFEngine.ProcessService.searchActors(subName, null, null);
        logger.info(searchActorsResponse.getMessage());
        Assert.assertEquals("Number of actors returned should be 1", new Integer(1), searchActorsResponse.getTotal());

        Actor foundActor = searchActorsResponse.getData().iterator().next();
        Assert.assertEquals("Found actor should have the same name as created one", actor.getName(), foundActor.getName());

        ServiceResponse<Actor> actorByLookupResponse = OPFEngine.ProcessService.readActorByLookup(actor.getLookup());
        logger.info(actorByLookupResponse.getMessage());

        Actor foundActorByLookup = actorByLookupResponse.getItem();
        Assert.assertEquals("Found actor should have the same id as created one", actor.getId(), foundActorByLookup.getId());
        Assert.assertEquals("Found actor should have the same name as created one", actor.getName(), foundActorByLookup.getName());
    }

    @Test
	public void assignUser() {
        Actor actor1 = (Actor) testContextAttributes.get(Elements.ACTOR1);
        User user2 = (User) testContextAttributes.get(Elements.USER2);

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext(ProcessExecutionListener.USER2_USERNAME, ProcessExecutionListener.USER_PASSWORD);

        ServiceResponse<SimpleIdentifier<Boolean>> userIsActorResponse = OPFEngine.ProcessService.checkIfUserIsActor(actor1.getLookup());
        logger.info(userIsActorResponse.getMessage());
        Assert.assertFalse("User2 is not supposed to be in actor1", userIsActorResponse.getItem().getIdentifier());

        ServiceResponse assignResponse = OPFEngine.ProcessService.assignUserToActor(actor1.getLookup(), user2.getId(), null);
        logger.info(assignResponse.getMessage());
        Assert.assertTrue("Assign user response success should be true", assignResponse.isSuccess());

        ServiceResponse<SimpleIdentifier<Boolean>> userIsActorAfterAssignResponse = OPFEngine.ProcessService.checkIfUserIsActor(actor1.getLookup());
        logger.info(userIsActorAfterAssignResponse.getMessage());
        Assert.assertTrue("User2 should be in actor1 after assignment", userIsActorAfterAssignResponse.getItem().getIdentifier());
    }

    private Long createDirectory() {
        Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);

        Directory directory = new Directory();
		directory.setParentId(pkg.getId());
		directory.setName("testUserDirectoryForActor");
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

        Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);
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
