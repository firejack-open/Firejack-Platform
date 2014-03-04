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

package net.firejack.platform.web.security.spring;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.utils.SessionManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.spring.authenticator.AuthenticatorFactory;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationDetails;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationSource;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticator;
import net.firejack.platform.web.security.spring.token.AuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationManager extends AbstractAuthenticationManager {

//    @Autowired
//    private MessageResolver messageResolver;
    @Autowired
    private SessionManager sessionManager;

    private List<IAuthenticator> authenticators;

    /**
     * @return
     */
    public List<IAuthenticator> getAuthenticators() {
        if (authenticators == null) {
            authenticators = Collections.emptyList();
        }
        return authenticators;
    }

    /**
     * @param authenticators
     */
    public void setAuthenticators(List<IAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }

//    /**
//     * @param messageResolver
//     */
//    public void setMessageResolver(MessageResolver messageResolver) {
//        this.messageResolver = messageResolver;
//    }

    /**
     * @param sessionManager
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Authentication generateDefaultToken(IUserInfoProvider user, List<GrantedAuthority> authorities, HttpSession session) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
        token.setDetails(user);
        sessionManager.addUserToSession(user, session);
        return token;
    }

    private Authentication generateDefaultToken(IAuthenticationDetails authenticationDetails, HttpSession session) {
        IUserInfoProvider user = authenticationDetails.getDetails();
        List<GrantedAuthority> authorities = authenticationDetails.getAssignedAuthorities();
        IPermissionContainer permissionContainer = authenticationDetails.getPermissionContainer();
        AuthenticationToken token = new AuthenticationToken(
                user.getUsername(), user.getPassword(), authorities, permissionContainer);
        token.setDetails(user);
        sessionManager.addUserToSession(user, session);
        return token;
    }

    protected Authentication doAuthentication(Authentication authentication) throws AuthenticationException {
        if (authentication.getPrincipal() == null || authentication.getCredentials() == null || authentication.getDetails() == null) {
            String errorMessage = MessageResolver.messageFormatting("login.wrong.credentials", null);
            throw new BadCredentialsException(errorMessage);
        }

        String userName = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        HttpSession session = ((AuthenticationToken) authentication).getSession();

        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            if (!getAuthenticators().isEmpty()) {
                AuthenticatorFactory authenticatorFactory = AuthenticatorFactory.getInstance();
                IAuthenticationSource authenticationSource = authenticatorFactory.provideDefaultAuthenticationSource(userName, password);
                for (IAuthenticator authenticator : getAuthenticators()) {
                    IAuthenticationDetails authenticationDetails = authenticator.authenticate(authenticationSource);
                    if (authenticationDetails != null) {
                        return generateDefaultToken(authenticationDetails, session);
                    }
                }
            }
        }

        String errorMessage = MessageResolver.messageFormatting("login.authentication.failure", null);
        throw new BadCredentialsException(errorMessage);
    }

    /**
     * @param user
     * @param session
     * @param roles
     * @throws org.springframework.security.core.AuthenticationException
     *
     */
    public void doAuthentication(IUserInfoProvider user, HttpSession session, SpringSecurityAuthenticationRoleType... roles) throws AuthenticationException {
        if (user != null) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (SpringSecurityAuthenticationRoleType role : roles) {
                authorities.add(new GrantedAuthorityImpl(role.name()));
            }
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
            token.setDetails(user);
            sessionManager.addUserToSession(user, session);

            SecurityContextHolder.getContext().setAuthentication(token);
            OpenFlamePrincipal fjkPrincipal = new UserPrincipal(user);
            OPFContext.initContext(fjkPrincipal);
        }
    }
}