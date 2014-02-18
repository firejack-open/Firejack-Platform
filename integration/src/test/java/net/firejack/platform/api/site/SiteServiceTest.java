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

package net.firejack.platform.api.site;

import net.firejack.platform.api.*;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.listener.DomainExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.api.registry.model.EntityType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        DomainExecutionListener.class
})
public class SiteServiceTest extends BaseOpenFlameAPITest {

    private static final Logger logger = Logger.getLogger(SiteServiceTest.class);

    @Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

    private Long time;

    @Before
    public void setUp() {
        time = new Date().getTime();
    }

    @After
    public void tearDown() {

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    NAVIGATION TESTS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void navigationTest() {
        Domain domain = (Domain) testContextAttributes.get(Elements.DOMAIN);

        NavigationElement navigationElement = createNavigationElementInstance("navigation", domain.getId());
        ServiceResponse<RegistryNodeTree> createResponse = OPFEngine.SiteService.createNavigationElement(navigationElement);
        Assert.assertTrue("Navigation Element should be created", createResponse.isSuccess());
        logger.info(createResponse.getMessage());
		Assert.assertNotNull("Can't be create navigation element response null.", createResponse);
		Assert.assertNotNull("Navigation element's item should not be null.", createResponse.getItem());
		navigationElement.setId(createResponse.getItem().getId());
        navigationElement.setLookup(createResponse.getItem().getLookup());

        navigationElement.setName("parent-navigation");
        ServiceResponse<RegistryNodeTree> updateResponse = OPFEngine.SiteService.createNavigationElement(navigationElement);
        Assert.assertTrue("Navigation Element should be created", updateResponse.isSuccess());
        Assert.assertEquals("Name of navigation element should updated.", navigationElement.getName(), updateResponse.getItem().getName());
        navigationElement.setLookup(updateResponse.getItem().getLookup());


        NavigationElement subNavigationElement1 = createNavigationElementInstance("sub-navigation-0", navigationElement.getId());
        ServiceResponse<RegistryNodeTree> createResponse1 = OPFEngine.SiteService.createNavigationElement(subNavigationElement1);
        Assert.assertTrue("Navigation Element should be created", createResponse1.isSuccess());
        logger.info(createResponse1.getMessage());
        subNavigationElement1.setId(createResponse1.getItem().getId());

        NavigationElement subNavigationElement2 = createNavigationElementInstance("sub-navigation-1", navigationElement.getId());
        ServiceResponse<RegistryNodeTree> createResponse2 = OPFEngine.SiteService.createNavigationElement(subNavigationElement2);
        Assert.assertTrue("Navigation Element should be created", createResponse2.isSuccess());
        logger.info(createResponse2.getMessage());
        subNavigationElement2.setId(createResponse2.getItem().getId());

        NavigationElement subNavigationElement3 = createNavigationElementInstance("sub-navigation-2", navigationElement.getId());
        ServiceResponse<RegistryNodeTree> createResponse3 = OPFEngine.SiteService.createNavigationElement(subNavigationElement3);
        Assert.assertTrue("Navigation Element should be created", createResponse3.isSuccess());
        logger.info(createResponse3.getMessage());
        subNavigationElement3.setId(createResponse3.getItem().getId());

        ServiceResponse<NavigationElement> readResponse = OPFEngine.SiteService.readNavigationElementBroker(navigationElement.getId());
        Assert.assertTrue("Navigation Element should be returned", readResponse.isSuccess());
        logger.info(readResponse.getMessage());
        Assert.assertEquals("Returned Navigation Element should be the same.", navigationElement, readResponse.getItem());

        List<Permission> rolePermissions = new ArrayList<Permission>();

        ServiceResponse<Permission> permissionResponse =
                OPFEngine.AuthorityService.readAllPermissionsByRegistryNodeId(navigationElement.getParentId(), null, null);
        Assert.assertTrue("Permission list should not be null", permissionResponse.isSuccess());
        logger.info(permissionResponse.getMessage());
        Assert.assertEquals("Permission list should have four elements", 4, permissionResponse.getData().size());
        rolePermissions.addAll(permissionResponse.getData());

	    ServiceResponse<Search> action = OPFEngine.RegistryService.getSearchResult(
                "net.firejack.platform.site.navigation-element", null,
                "net.firejack.platform.site.navigation-element", "All", new Paging());
	    Long id = action.getItem().getId();
	    ServiceResponse<Permission> permissionServiceResponse =
                OPFEngine.AuthorityService.readAllPermissions(id, null, null, null);

	    rolePermissions.addAll(permissionServiceResponse.getData());

        Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);
        Role role = new Role();
        role.setName("navigation-role");
        role.setPath(PackageExecutionListener.PACKAGE_LOOKUP);
        role.setLookup(PackageExecutionListener.PACKAGE_LOOKUP + ".navigation-role");
        role.setParentId(pkg.getId());
        role.setPermissions(rolePermissions);

        ServiceResponse<Role> roleResponse = OPFEngine.AuthorityService.createRole(new ServiceRequest<Role>(role));
        Assert.assertNotNull("OPFEngine.AuthorityService.createRole(request)", roleResponse);
        Assert.assertTrue("Response has failure status.", roleResponse.isSuccess());
        role = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", role);
        Assert.assertNotNull("Returned role should have id.", role.getId());

        Directory directory = new Directory();
		directory.setParentId(pkg.getId());
		directory.setName("testUserDirectory");
		directory.setStatus(RegistryNodeStatus.UNKNOWN);
		directory.setDirectoryType(DirectoryType.DATABASE);
		ServiceResponse<RegistryNodeTree> directoryResponse = OPFEngine.DirectoryService.createDirectory(directory);
        Assert.assertNotNull("Can't be create response null.", directoryResponse);
        logger.info(directoryResponse.getMessage());
		Assert.assertNotNull("Can't be create item null.", directoryResponse.getItem());

	    User user = new User();
		user.setRegistryNodeId(directoryResponse.getItem().getId());
		user.setUsername("testUser");
		user.setPassword("123123");
		user.setPasswordConfirm("123123");
		user.setEmail("123testUser@mail.ru");
		user.setFirstName("testUser");
        user.setRoles(roleResponse.getData());

		ServiceResponse<User> response = OPFEngine.DirectoryService.createUser(user);
        Assert.assertNotNull("Can't be create response null.", response);
        logger.info(response.getMessage());
		Assert.assertNotNull("Can't be create item null.", response.getItem());

        TestBusinessContext context = new TestBusinessContext();
        context.prepareContext("testUser", "123123");

        ServiceResponse<NavigationElement> treeReadResponse = OPFEngine.SiteService.readTreeNavigationElementsByParentLookup(navigationElement.getLookup());
        Assert.assertTrue("Tree Navigation Elements should be returned", treeReadResponse.isSuccess());
        logger.info(treeReadResponse.getMessage());
        Assert.assertEquals("Should be returned tree navigation element3.", 3, treeReadResponse.getData().size());

        context.releaseContext();

	    OPFEngine.init(OPFServiceTests.ADMIN_LOGIN, OPFServiceTests.ADMIN_PASSWORD);

        ServiceResponse deleteResponse = OPFEngine.SiteService.deleteNavigationElement(navigationElement.getId());
        logger.info(deleteResponse.getMessage());
        Assert.assertTrue("RootDomain should be deleted.", deleteResponse.isSuccess());
    }

    @Test
    public void treeNavigationMenuTest() {
        Domain domain = (Domain) testContextAttributes.get(Elements.DOMAIN);

        Entity entity1 = new Entity();
		entity1.setName("Entity1");
		entity1.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity1.setParentId(domain.getId());
		entity1.setStatus(RegistryNodeStatus.UNKNOWN);

		ServiceResponse<RegistryNodeTree> response1 = OPFEngine.RegistryService.createEntity(entity1);
        Assert.assertNotNull("Can't be create entity response null.", response1);
        logger.info(response1.getMessage());
		Assert.assertNotNull("Can't be create entity item null.", response1.getItem());
		entity1.setId(response1.getItem().getId());

        Entity entity2 = new Entity();
		entity2.setName("Entity2");
		entity2.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity2.setStatus(RegistryNodeStatus.UNKNOWN);
		entity2.setParentId(domain.getId());

		ServiceResponse<RegistryNodeTree> response2 = OPFEngine.RegistryService.createEntity(entity2);
        Assert.assertNotNull("Can't be create entity response null.", response2);
        logger.info(response2.getMessage());
		Assert.assertNotNull("Can't be create entity item null.", response2.getItem());
		entity2.setId(response2.getItem().getId());

        Entity entity3 = new Entity();
		entity3.setName("Entity3");
		entity3.setTypeEntity(EntityType.STANDARD.getEntityType());
		entity3.setStatus(RegistryNodeStatus.UNKNOWN);
		entity3.setParentId(domain.getId());

		ServiceResponse<RegistryNodeTree> response3 = OPFEngine.RegistryService.createEntity(entity3);
        Assert.assertNotNull("Can't be create entity response null.", response3);
        logger.info(response3.getMessage());
		Assert.assertNotNull("Can't be create entity item null.", response3.getItem());
		entity3.setId(response3.getItem().getId());

        Package pkg = (Package) testContextAttributes.get(Elements.PACKAGE);

        ServiceResponse<NavigationElementTree> response = OPFEngine.SiteService.readTreeNavigationMenu(pkg.getLookup(), false);
        Assert.assertNotNull("Can't be response null.", response);
        logger.info(response.getMessage());

    }

    private NavigationElement createNavigationElementInstance(String name, Long parentId) {
        NavigationElement navigationElement = new NavigationElement();
        navigationElement.setParentId(parentId);
        navigationElement.setName(name);
        navigationElement.setServerName("localhost");
        navigationElement.setParentPath("/path");
        navigationElement.setUrlPath("/navigation");
        navigationElement.setPort(8000);
        navigationElement.setProtocol(EntityProtocol.HTTP);
        navigationElement.setSortPosition(0);
        navigationElement.setStatus(RegistryNodeStatus.UNKNOWN);
        return navigationElement;
    }

}
