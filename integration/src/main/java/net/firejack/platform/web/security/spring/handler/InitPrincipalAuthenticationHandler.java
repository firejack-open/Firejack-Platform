/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
