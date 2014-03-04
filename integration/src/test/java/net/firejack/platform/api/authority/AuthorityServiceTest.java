/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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