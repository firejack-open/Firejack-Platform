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
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.authority.domain.MappedPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class
})
public class AuthorityServiceTest extends BaseOpenFlameAPITest {
    private static final Logger logger = Logger.getLogger(AuthorityServiceTest.class);

    @Test
    public void loadPermissionsTest() {
        ServiceResponse<UserPermission> grantedPermissions =
                OPFEngine.AuthorityService.loadGrantedPermissions();
        Assert.assertTrue("Failed to load granted permissions. Reason: " +
                grantedPermissions.getMessage(), grantedPermissions.isSuccess());
        logger.info("Granted Permissions list size = " +
                (grantedPermissions.getData() == null ? 0 : grantedPermissions.getData().size()));
        ServiceResponse<UserPermission> deniedPermissions =
                OPFEngine.AuthorityService.loadDeniedPermissions();
        Assert.assertTrue("Failed to load denied permissions. Reason: " +
                deniedPermissions.getMessage(), deniedPermissions.isSuccess());
        logger.info("Denied Permissions list size = " +
                (deniedPermissions.getData() == null ? 0 : deniedPermissions.getData().size()));
        ServiceResponse<MappedPermissions> mappedPermissionsResponse =
                OPFEngine.AuthorityService.readPermissionsByRolesMapForGuest();
        Assert.assertTrue("Failed to load permissions by roles map for guest. Reason: " +
                mappedPermissionsResponse.getMessage(), mappedPermissionsResponse.isSuccess());
        logger.info("Permission By Roles Map size = " +
                (mappedPermissionsResponse.getData() == null ? 0 : mappedPermissionsResponse.getData().size()));
        ServiceResponse<MappedPermissions> srContextualPermissionsResponse =
                OPFEngine.AuthorityService.readAllSecuredRecordContextualPermissions();
        Assert.assertTrue("Failed to load all secured record contextual permissions. Reason: " +
                srContextualPermissionsResponse.getMessage(), srContextualPermissionsResponse.isSuccess());
        logger.info("Secured record contextual permissions map size = " +
                (mappedPermissionsResponse.getData() == null ? 0 : mappedPermissionsResponse.getData().size()));
    }

}