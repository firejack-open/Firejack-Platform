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

package net.firejack.platform.web.security.spring.authenticator;

import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.spring.openid.OpenIDAuthenticationSource;
import org.springframework.security.core.Authentication;

import java.util.Map;

public final class AuthenticatorFactory {

    private static AuthenticatorFactory instance;

    /**
     * @return
     */
    public static AuthenticatorFactory getInstance() {
        if (instance == null) {
            instance = new AuthenticatorFactory();
        }
        return instance;
    }

    /**
     * @param authentication
     * @return
     */
    public IAuthenticationSource provideDefaultAuthenticationSource(Authentication authentication) {
        return new DefaultAuthenticationSource(authentication);
    }

    /**
     * @param userName
     * @param password
     * @return
     */
    public IAuthenticationSource provideDefaultAuthenticationSource(String userName, String password) {
        return new DefaultAuthenticationSource(userName, password);
    }

    /**
     * @param email
     * @param attibutes
     * @return
     */
    public IAuthenticationSource provideOpenIDAuthenticationSource(String email, Map<SupportedOpenIDAttribute, String> attibutes) {
        return new OpenIDAuthenticationSource(email, attibutes);
    }

}