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

package net.firejack.platform.api.directory;


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.OPFServiceTests;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.RegistryServiceTest;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.EnumMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class
})
public class DirectoryServiceTest {
	protected static Logger logger = Logger.getLogger(DirectoryServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

	private static EnumMap<Elements, Lookup> map;

	@Before
	public void setUp() {
		Env.FIREJACK_URL.setValue(baseUrl);

		OPFEngine.initialize();
		if (map == null) {
			map = RegistryServiceTest.createStandardTree();
		}
	}

	@After
	public void tearDown() {

	}

	@AfterClass
	public static void tearDownClass() {
		OPFEngine.RegistryService.deleteRootDomain(map.get(Elements.ROOT_DOMAIN).getId());
	}

	@Test
	public void crudGroup() {
		ServiceResponse<Directory> directories = OPFEngine.DirectoryService.readAllDirectories();

		Group group = new Group();
		group.setParentId(map.get(Elements.PACKAGE).getId());
		group.setName("testGroup");
		group.setDirectory(directories.getItem());

		ServiceResponse<RegistryNodeTree> response = OPFEngine.DirectoryService.createGroup(group);

        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Group> serviceResponse = OPFEngine.DirectoryService.readGroup(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readAllGroups(null);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.searchAllGroups("test", null);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		group = serviceResponse.getItem();
		group.setName("testGroup2");
		group.setDirectory(directories.getItem());

		response = OPFEngine.DirectoryService.updateGroup(group.getId(), group);

        Assert.assertNotNull("Can't be update response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.DirectoryService.deleteGroup(group.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudUserProfileFieldGroup() {
		Long parentId = map.get(Elements.DOMAIN).getId();

		UserProfileFieldGroup group = new UserProfileFieldGroup();
		group.setParentId(parentId);
		group.setName("testUserProfileFieldGroup");

		ServiceResponse<UserProfileFieldGroup> response = OPFEngine.DirectoryService.createUserProfileFieldGroup(group);

        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<UserProfileFieldGroup> serviceResponse = OPFEngine.DirectoryService.readUserProfileFieldGroup(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		ServiceResponse<UserProfileFieldGroupTree> serviceResponse2 = OPFEngine.DirectoryService.readAllUserProfileFieldGroup(parentId);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse2.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse2.getItem());

		group = serviceResponse.getItem();
		group.setName("testUserProfileFieldGroup2");

		response = OPFEngine.DirectoryService.updateUserProfileFieldGroup(group.getId(), group);

        Assert.assertNotNull("Can't be update response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.DirectoryService.deleteUserProfileFieldGroup(group.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be delete response null.", serviceResponse);
	}

	@Test
	public void crudUserProfileField() {
		Long parentId = map.get(Elements.DOMAIN).getId();

		UserProfileFieldGroup group = new UserProfileFieldGroup();
		group.setParentId(parentId);
		group.setName("fieldTestUserProfileFieldGroup");

		ServiceResponse<UserProfileFieldGroup> parentResponse = OPFEngine.DirectoryService.createUserProfileFieldGroup(group);

        Assert.assertNotNull("Can't be create response null.", parentResponse);
        logger.info(parentResponse.getMessage());
		Assert.assertNotNull("Can't be create item null.", parentResponse.getItem());
        group.setId(parentResponse.getItem().getId());

		UserProfileField field = new UserProfileField();
		field.setParentId(parentId);
		field.setName("testUserProfileField");
		field.setFieldType(FieldType.LARGE_NUMBER);
		field.setUserProfileFieldGroupId(group.getId());

		ServiceResponse<UserProfileFieldTree> response = OPFEngine.DirectoryService.createUserProfileField(field);

        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<UserProfileFieldTree> serviceResponse = OPFEngine.DirectoryService.readAllUserProfileField(parentId, group.getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		UserProfileFieldTree fieldTree = response.getItem();
        field.setId(fieldTree.getId());
		field.setPath(fieldTree.getPath());
		field.setLookup(fieldTree.getLookup());
		field.setName("testUserProfileField2");

		response = OPFEngine.DirectoryService.updateUserProfileField(fieldTree.getId(), field);

        Assert.assertNotNull("Can't be update response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		ServiceResponse<UserProfileField> serviceResponse2 = OPFEngine.DirectoryService.deleteUserProfileField(field.getId());

        Assert.assertNotNull("Can't be delete response null.", serviceResponse2);
        logger.info(serviceResponse2.getMessage());
	}

	@Test
	public void crudDirectory() {
		Directory directory = new Directory();
		directory.setParentId(map.get(Elements.PACKAGE).getId());
		directory.setName("testDirectory");
		directory.setStatus(RegistryNodeStatus.UNKNOWN);
		directory.setDirectoryType(DirectoryType.DATABASE);

		ServiceResponse<RegistryNodeTree> response = OPFEngine.DirectoryService.createDirectory(directory);

		Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
        Assert.assertNotNull("Can't be create item null.", response.getItem());

		ServiceResponse<Directory> serviceResponse = OPFEngine.DirectoryService.readAllDirectories();

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readOrderedDirectories();

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readDirectoryServices();

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readDirectory(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		directory = serviceResponse.getItem();
		directory.setName("testDirectory2");

		response = OPFEngine.DirectoryService.updateDirectory(directory.getId(), directory);

        Assert.assertNotNull("Can't be update response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.DirectoryService.deleteDirectory(directory.getId());

        Assert.assertNotNull("Can't be delete response null.", serviceResponse);
        logger.info(serviceResponse.getMessage());
	}

	@Test
	public void crudUser() {
		Directory directory = new Directory();
		directory.setParentId(map.get(Elements.PACKAGE).getId());
		directory.setName("testUserDirectory");
		directory.setStatus(RegistryNodeStatus.UNKNOWN);
		directory.setDirectoryType(DirectoryType.DATABASE);

		ServiceResponse<RegistryNodeTree> parentResponse = OPFEngine.DirectoryService.createDirectory(directory);

        Assert.assertNotNull("Can't be create response null.", parentResponse);
        logger.info(parentResponse.getMessage());
		Assert.assertNotNull("Can't be create item null.", parentResponse.getItem());

		User user = new User();
		user.setRegistryNodeId(parentResponse.getItem().getId());
		user.setUsername("testUser");
		user.setPassword("123123");
		user.setPasswordConfirm("123123");
		user.setEmail("123testUser@mail.ru");
		user.setFirstName("testUser");


		String sessionToken = OPFContext.getContext().getSessionToken();
		OPFEngine.AuthorityService.processSTSSignOut(sessionToken);

		ServiceResponse<User> response = OPFEngine.DirectoryService.signUpUser(user);

        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);

		ServiceResponse<User> serviceResponse = OPFEngine.DirectoryService.deleteUser(response.getItem().getId());

        Assert.assertNotNull("Can't be delete response null.", serviceResponse);
        logger.info(serviceResponse.getMessage());

		response = OPFEngine.DirectoryService.createUser(user);

        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

		serviceResponse = OPFEngine.DirectoryService.readLastCreatedUsers(5);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.searchAllUsers("adm", null, null, null, null, null);

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readAllUsersByRegistryNodeId(parentResponse.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.searchUser(parentResponse.getItem().getId(), "test");

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.findUserByEmail(user.getEmail());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		serviceResponse = OPFEngine.DirectoryService.readUser(response.getItem().getId());

		logger.info(serviceResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", serviceResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", serviceResponse.getItem());

		user = serviceResponse.getItem();
		user.setUsername("testUser2");

		response = OPFEngine.DirectoryService.updateUser(user.getId(), user);

		logger.info(response.getMessage());
		Assert.assertNotNull("Can't be update response null.", response);
		Assert.assertNotNull("Can't be update item null.", response.getItem());


		serviceResponse = OPFEngine.DirectoryService.deleteUser(user.getId());

        Assert.assertNotNull("Can't be delete response null.", serviceResponse);
        logger.info(serviceResponse.getMessage());

		ServiceResponse<Role> roleResponse = OPFEngine.DirectoryService.readAllGlobalRoles(map.get(Elements.PACKAGE).getId(), null);

		logger.info(roleResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", roleResponse.getData());

		roleResponse = OPFEngine.DirectoryService.searchRoles("adm", null);

		logger.info(roleResponse.getMessage());
		Assert.assertNotNull("Can't be read response null.", roleResponse.getData());
		Assert.assertNotNull("Can't be read item empty.", roleResponse.getItem());

	}
}
