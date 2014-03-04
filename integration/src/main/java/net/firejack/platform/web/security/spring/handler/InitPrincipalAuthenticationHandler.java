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

package net.firejack.platform.web.security.spring.handler;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.spring.token.AuthenticationToken;
import org.springframework.security.core.Authentication;



public class InitPrincipalAuthenticationHandler implements ISuccessfulAuthenticationHandler {

    @Override
    public void onSuccessfulAuthentication(Authentication authentication) {
        IUserInfoProvider userInfoProvider = (IUserInfoProvider) authentication.getDetails();
        OpenFlamePrincipal fjkPrincipal = new UserPrincipal(userInfoProvider);
        if (authentication instanceof AuthenticationToken) {
            IPermissionContainer permissionContainer = ((AuthenticationToken) authentication).getPermissionHolder();
            //fjkPrincipal.loadPermissions(permissionContainer);
            fjkPrincipal.assignPermissionContainer(permissionContainer);
        }
        OPFContext.initContext(fjkPrincipal);
    }

}
