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

package net.firejack.platform.web.security.spring.openid;

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.utils.SessionManager;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.spring.authenticator.AuthenticatorFactory;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationDetails;
import net.firejack.platform.web.security.spring.token.AuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenIDAuthenticationManager extends AbstractAuthenticationManager {

//    @Autowired
//    private MessageResolver messageResolver;
    @Autowired
    private SessionManager sessionManager;

    private OpenIDAuthenticator authenticator;

    /**
     * @param authenticator
     */
    public void setAuthenticator(OpenIDAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    protected Authentication doAuthentication(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof OpenIDAuthenticationToken) {
            OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) authentication;
            if (!OpenIDAuthenticationStatus.SUCCESS.equals(token.getStatus())) {
                String errorMessage = MessageResolver.messageFormatting("login.wrong.credentials", null);
                throw new BadCredentialsException(errorMessage);
            }
            String email = findAttributeValueByName(SupportedOpenIDAttribute.EMAIL, token.getAttributes());
            if (StringUtils.isBlank(email)) {
                String errorMessage = MessageResolver.messageFormatting("login.wrong.credentials", null);
                throw new BadCredentialsException(errorMessage);
            }

            HttpSession session = ((SessionContainerWebAuthenticationDetails) token.getDetails()).getSession();

            if (authenticator != null) {
                AuthenticatorFactory authenticatorFactory = AuthenticatorFactory.getInstance();
                Map<SupportedOpenIDAttribute, String> attributeValues = findAttributeValues(token.getAttributes());
                OpenIDAuthenticationSource openIDAuthenticationSource =
                        (OpenIDAuthenticationSource) authenticatorFactory.provideOpenIDAuthenticationSource(email, attributeValues);
                IAuthenticationDetails authenticationDetails = authenticator.authenticate(openIDAuthenticationSource);
                if (authenticationDetails != null) {
                    return generateDefaultToken(authenticationDetails, session);
                }
            }
        }

        String errorMessage = MessageResolver.messageFormatting("login.authentication.failure", null);
        throw new BadCredentialsException(errorMessage);
    }

    private Authentication generateDefaultToken(IAuthenticationDetails authenticationDetails, HttpSession session) {
        IUserInfoProvider user = authenticationDetails.getDetails();
        List<GrantedAuthority> authorities = authenticationDetails.getAssignedAuthorities();
        IPermissionContainer permissionContainer = authenticationDetails.getPermissionContainer();
        AuthenticationToken token = new AuthenticationToken(
                user.getUsername(), null, authorities, permissionContainer);
        token.setDetails(user);
        sessionManager.addUserToSession(user, session);
        return token;
    }

    private String findAttributeValueByName(SupportedOpenIDAttribute searchOpenIDAttribute, List<OpenIDAttribute> attributes) {
        String value = null;
        for (OpenIDAttribute attribute : attributes) {
            if (searchOpenIDAttribute.getAttributeName().equals(attribute.getName())) {
                if (attribute.getValues() != null && !attribute.getValues().isEmpty()) {
                    value = attribute.getValues().get(0);
                }
                break;
            }
        }
        return value;
    }

    private Map<SupportedOpenIDAttribute, String> findAttributeValues(List<OpenIDAttribute> attributes) {
        Map<SupportedOpenIDAttribute, String> values = new HashMap<SupportedOpenIDAttribute, String>();
        for (OpenIDAttribute attribute : attributes) {
            String name = attribute.getName();
            SupportedOpenIDAttribute supportedOpenIDAttribute = SupportedOpenIDAttribute.lookForSupportedAttribute(name);
            if (supportedOpenIDAttribute != null &&
                    attribute.getValues() != null && !attribute.getValues().isEmpty()) {
                String value = attribute.getValues().get(0);
                if (value != null) {
                    values.put(supportedOpenIDAttribute, value);
                }
            }
        }
        return values;
    }

}
