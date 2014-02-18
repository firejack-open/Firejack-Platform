/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.extension.spring.authenticator;

import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.service.authority.utils.OpenIDProcessor;
import net.firejack.platform.web.security.spring.openid.OpenIDAuthenticationSource;
import net.firejack.platform.web.security.spring.openid.OpenIDAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConsoleOpenIDAuthenticator extends OpenIDAuthenticator {

    @Autowired
    private OpenIDProcessor openIDProcessor;

    @Override
    protected IUserInfoProvider getOpenIDUser(OpenIDAuthenticationSource openIDAuthenticationSource) {
        String email = openIDAuthenticationSource.getPrincipal();
        IUserInfoProvider user = openIDProcessor.findUserByEmail(email);
        if (user == null) {
            Tuple<UserModel, List<RoleModel>> userRoles =
                    openIDProcessor.createUserFromOpenIDAttributes(openIDAuthenticationSource.getAttributes());
            user = userRoles.getKey();
        }
        return user;
    }

}