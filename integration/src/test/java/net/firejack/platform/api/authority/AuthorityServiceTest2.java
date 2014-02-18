/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.authority;


import net.firejack.platform.api.BaseOpenFlameAPITest;
import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.listener.DomainExecutionListener;
import net.firejack.platform.api.registry.listener.EntityExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.authority.WildcardStyle;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        DomainExecutionListener.class,
        EntityExecutionListener.class
})
public class AuthorityServiceTest2 extends BaseOpenFlameAPITest {
    private static final Logger logger = Logger.getLogger(AuthorityServiceTest2.class);
    private static final String PERMISSION_NAME_PREFIX = "test-permission";
    private static final String ROLE_NAME = "test-role";
    private static final String RESOURCE_LOCATION_NAME = "test-resource-location";
    private static final String RESOURCE_LOCATION_URL_PATH = "/test/*";

    @Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

    @Test
    public void createRoleAndPermissionsTest() {
        Entity entity = (Entity) testContextAttributes.get(Elements.ENTITY);
        Package pkg = (Package)testContextAttributes.get(Elements.PACKAGE);

        String firstPermissionName = PERMISSION_NAME_PREFIX + 1;
        ServiceResponse<Permission> permissionResponse = createPermission(firstPermissionName, entity.getId());
        Permission firstPermission = readPermission(firstPermissionName, permissionResponse);

        String secondPermissionName = PERMISSION_NAME_PREFIX + 2;
        permissionResponse = createPermission(secondPermissionName, entity.getId());
        Permission secondPermission = readPermission(secondPermissionName, permissionResponse);

        //trying to search just created permissions
        permissionResponse = OPFEngine.AuthorityService.searchPermissions(null, secondPermission.getLookup(), null, null, null);
        checkResponseStatus(permissionResponse);
        Assert.assertTrue("Permission search API Service had to return one permission.",
                permissionResponse.getData() != null && permissionResponse.getData().size() == 1);

        List<Permission> rolePermissions = new ArrayList<Permission>();
        rolePermissions.add(firstPermission);
        Role testRole = createRole(pkg.getId(), rolePermissions);

        readRole(testRole.getId(), 1);

        logger.info("Trying to update test role by assigning second test permission.");
        testRole.getPermissions().add(secondPermission);
        ServiceResponse<Role> roleResponse =
                OPFEngine.AuthorityService.updateRole(new ServiceRequest<Role>(testRole));
        checkResponseStatus(roleResponse);

        testRole = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", testRole);
        Assert.assertNotNull("Returned role should have id.", testRole.getId());
        Assert.assertTrue("Returned role should have name = " + ROLE_NAME,
                ROLE_NAME.equals(testRole.getName()));
        Assert.assertNotNull("Test Role permissions should not be null.", testRole.getPermissions());
        Assert.assertTrue("Test role should have two permission assigned.", testRole.getPermissions().size() == 2);

        readRole(testRole.getId(), 2);

        logger.info("Trying to replace test role.");
        ServiceResponse statusResponse = OPFEngine.AuthorityService.deleteRole(testRole.getId());
        checkResponseStatus(statusResponse);

        logger.info("Trying to read replaced test role");
        roleResponse = OPFEngine.AuthorityService.readRole(testRole.getId());
        Assert.assertNotNull("OPFEngine.AuthorityService.readRole(testRole.getId())", roleResponse);
        Assert.assertFalse("Response message:" + roleResponse.getMessage(), roleResponse.isSuccess());

        deletePermission(firstPermission.getId());
        deletePermission(secondPermission.getId());
    }

    @Test
    public void crudResourceLocationsTest() {
        Package pkg = (Package) testContextAttributes.get(Elements.PACKAGE);
        if (pkg == null) {
            throw new OpenFlameRuntimeException("Test package should not be null.");
        }
        logger.info("Trying to create test resource location.");
        ResourceLocation resourceLocation = new ResourceLocation();
        resourceLocation.setName(RESOURCE_LOCATION_NAME);
        resourceLocation.setPath(PackageExecutionListener.PACKAGE_LOOKUP);
        resourceLocation.setLookup(PackageExecutionListener.PACKAGE_LOOKUP + "." + RESOURCE_LOCATION_NAME);
        resourceLocation.setWildcardStyle(WildcardStyle.ANT);
        resourceLocation.setUrlPath(RESOURCE_LOCATION_URL_PATH);
        resourceLocation.setParentId(pkg.getId());

        ServiceRequest<ResourceLocation> request = new ServiceRequest<ResourceLocation>(resourceLocation);
        ServiceResponse<ResourceLocation> response = OPFEngine.AuthorityService.createResourceLocation(request);
        checkResponseStatus(response);

        resourceLocation = response.getItem();
        Assert.assertNotNull("Response item property should not be null.", resourceLocation);
        Assert.assertNotNull("Returned resource location should have id.", resourceLocation.getId());
        Assert.assertTrue("Returned resource location should have name = " + RESOURCE_LOCATION_NAME,
                RESOURCE_LOCATION_NAME.equals(resourceLocation.getName()));
        Assert.assertTrue("Returned resource location should have wildcardStyle = " + WildcardStyle.ANT,
                WildcardStyle.ANT == resourceLocation.getWildcardStyle());

        logger.info("Trying to read test resource location.");
        response = OPFEngine.AuthorityService.readResourceLocation(resourceLocation.getId());
        checkResponseStatus(response);

        resourceLocation = response.getItem();
        Assert.assertNotNull("Response item property should not be null.", resourceLocation);
        Assert.assertNotNull("Returned resource location should have id.", resourceLocation.getId());

        logger.info("Trying to update test resource location.");
        resourceLocation.setWildcardStyle(WildcardStyle.REGEXP);
        request.setData(resourceLocation);
        response = OPFEngine.AuthorityService.updateResourceLocation(request);
        checkResponseStatus(response);

        resourceLocation = response.getItem();
        Assert.assertNotNull("Response item property should not be null.", resourceLocation);
        Assert.assertNotNull("Returned resource location should have id.", resourceLocation.getId());
        Assert.assertTrue("Returned resource location wildcardStyle should equal to " + WildcardStyle.REGEXP,
                WildcardStyle.REGEXP == resourceLocation.getWildcardStyle());

        logger.info("Trying to delete test resource location.");
        ServiceResponse statusResponse = OPFEngine.AuthorityService.deleteResourceLocation(resourceLocation.getId());
        checkResponseStatus(statusResponse);
    }

    private ServiceResponse<Permission> createPermission(String permissionName, Long entityId) {
        logger.info("Trying to create permission with name = " + permissionName);
        Permission permission = new Permission();
        permission.setName(permissionName);
        permission.setPath(EntityExecutionListener.ENTITY_LOOKUP);
        permission.setLookup(EntityExecutionListener.ENTITY_LOOKUP + "." + permissionName);
        permission.setParentId(entityId);

        ServiceRequest<Permission> request = new ServiceRequest<Permission>(permission);
        ServiceResponse<Permission> permissionResponse = OPFEngine.AuthorityService.createPermission(request);
        checkResponseStatus(permissionResponse);

        Assert.assertNotNull("Returned permission should not be null.", permissionResponse.getItem());
        Assert.assertNotNull("Returned permission should have id.", permissionResponse.getItem().getId());
        permission = permissionResponse.getItem();
        Assert.assertTrue("Returned permission should have name = " + permissionName,
                permissionName.equals(permission.getName()));
        return permissionResponse;
    }

    private Permission readPermission(String permissionName, ServiceResponse<Permission> permissionResponse) {
        permissionResponse = OPFEngine.AuthorityService.readPermission(permissionResponse.getItem().getId());
        checkResponseStatus(permissionResponse);

        Assert.assertNotNull("Returned permission should not be null.", permissionResponse.getItem());
        Assert.assertNotNull("Returned permission should have id.", permissionResponse.getItem().getId());
        Assert.assertTrue("Permission returned should have name = " + permissionName,
                permissionName.equals(permissionResponse.getItem().getName()));

        return permissionResponse.getItem();
    }

    private void deletePermission(Long permissionId) {
        logger.info("Trying to replace permission with id = " + permissionId);
        ServiceResponse statusResponse = OPFEngine.AuthorityService.deletePermission(permissionId);
        checkResponseStatus(statusResponse);

        logger.info("Trying to read replaced permission with id = " + permissionId);
        ServiceResponse<Permission> permissionResponse = OPFEngine.AuthorityService.readPermission(permissionId);
        Assert.assertNotNull("API Service response should not be null", permissionResponse);
        Assert.assertFalse(
                "API Service method should not return information about permission deleted in previous step.",
                permissionResponse.isSuccess());
    }

    private void readRole(Long roleId, int requiredPermissionSize) {
        logger.info("Trying to read test role");
        ServiceResponse<Role> roleResponse = OPFEngine.AuthorityService.readRole(roleId);
        checkResponseStatus(roleResponse);
        Role testRole = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", testRole);
        Assert.assertNotNull("Returned role should have id.", testRole.getId());

        Assert.assertTrue("Returned role should have name = " + ROLE_NAME,
                ROLE_NAME.equals(testRole.getName()));
        Assert.assertNotNull("Test Role permissions should not be null", testRole.getPermissions());
        Assert.assertTrue("Test role should have one permission assigned", testRole.getPermissions().size() == requiredPermissionSize);
    }

    private Role createRole(Long packageId, List<Permission> initialPermissions) {
        //Create Test Role
        logger.info("Trying to create test role.");
        Role testRole = new Role();
        testRole.setName(ROLE_NAME);
        testRole.setPath(PackageExecutionListener.PACKAGE_LOOKUP);
        testRole.setLookup(PackageExecutionListener.PACKAGE_LOOKUP + "." + ROLE_NAME);
        testRole.setParentId(packageId);

        testRole.setPermissions(initialPermissions);

        ServiceResponse<Role> roleResponse = OPFEngine.AuthorityService.createRole(
                new ServiceRequest<Role>(testRole));
        checkResponseStatus(roleResponse);

        testRole = roleResponse.getItem();
        Assert.assertNotNull("Returned role should not be null.", testRole);
        Assert.assertNotNull("Returned role should have id.", testRole.getId());

        Assert.assertTrue("Returned role should have name = " + ROLE_NAME,
                ROLE_NAME.equals(testRole.getName()));
        Assert.assertNotNull("Test Role permissions should not be null", testRole.getPermissions());
        Assert.assertTrue("Test role should have one permission assigned",
                testRole.getPermissions().size() == initialPermissions.size());
        return testRole;
    }

    private void checkResponseStatus(ServiceResponse response) {
        Assert.assertNotNull("API Service response should not be null", response);
        Assert.assertTrue("API Service response has failure status. Reason: " + response.getMessage(), response.isSuccess());
    }

}