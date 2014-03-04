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

package net.firejack.platform.web.security.ws.interceptor;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserIdentity;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.ContextContainerDelegate;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.ws.WSContextDataHolder;
import net.firejack.platform.web.security.ws.authorization.OpenFlameWSSecurityContext;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.security.SecurityContext;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.UsernameTokenValidator;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 *
 */
public class OpenFlameUsernameTokenValidator extends UsernameTokenValidator {

    private static final Logger logger = Logger.getLogger(OpenFlameUsernameTokenValidator.class);

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
        String user = usernameToken.getName();
        String password = usernameToken.getPassword();
        String pwType = usernameToken.getPasswordType();
        WSPasswordCallback pwCb = new WSPasswordCallback(
                user, password, pwType, WSPasswordCallback.USERNAME_TOKEN, data);
        logger.info("Verify specified credentials...");
        try {
            data.getCallbackHandler().handle(new Callback[]{pwCb});
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION, null, null, e);
        } catch (UnsupportedCallbackException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION, null, null, e);
        }
    }

    public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
        if (credential == null || credential.getUsernametoken() == null) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "noCredential");
        }

        boolean passwordsAreEncoded = false;
        WSSConfig wssConfig = data.getWssConfig();
        if (wssConfig != null) {
            passwordsAreEncoded = wssConfig.getPasswordsAreEncoded();
        }

        UsernameToken usernameToken = credential.getUsernametoken();
        usernameToken.setPasswordsAreEncoded(passwordsAreEncoded);

        String pwType = usernameToken.getPasswordType();

        StringBuilder sb = new StringBuilder("UsernameToken password type [");
        sb.append(StringUtils.isBlank(pwType) ? "No Password" : pwType);
        sb.append("]. UsernameToken user ").append(usernameToken.getName());
        logger.info(sb.toString());

        if (pwType == null || WSConstants.PASSWORD_TEXT.equals(pwType)) {
            logger.info("Password type is supported by OpenFlame platform.");
        } else {
            logger.warn("Password Type is not supported");
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        }

        if (pwType == null) {
            String opfAuthenticationToken = usernameToken.getName();
            prepareContextForSessionToken(data, opfAuthenticationToken);
        } else if (StringUtils.isBlank(usernameToken.getName())) {
            logger.warn("Username information is not available.");
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        } else {
            verifyPlaintextPassword(usernameToken, data);
        }

        return credential;
    }

    protected void prepareContextForSessionToken(RequestData requestData, String sessionToken)
            throws WSSecurityException {
        ContextContainerDelegate contextContainerDelegate = ContextContainerDelegate.getInstance();
        contextContainerDelegate.setCurrentSessionToken(sessionToken);
        if (ContextManager.isSessionActive(sessionToken)) {
            ServiceResponse<UserIdentity> response =
                    OPFEngine.DirectoryService.getUserIdentityInfo(sessionToken);
            User user;
            if (response.isSuccess()) {
                UserIdentity userIdentity = response.getItem();
                if (userIdentity == null) {
                    logger.warn("User identity for session token = [" + sessionToken + "] is null.");
                    throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
                } else {
                    user = new User();
                    user.setId(userIdentity.getId());
                    user.setUsername(userIdentity.getUserName());
                    user.setFirstName(userIdentity.getFirstName());
                    user.setLastName(userIdentity.getLastName());
                }
            } else {
                logger.error("Failed to load user identity information by session token. Reason: " + response.getMessage());
                throw new BusinessFunctionException(response.getMessage());
            }
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null) {
                context = OPFContext.initContext(new UserPrincipal(user), sessionToken);
            }
            context.setCurrentRequestPath(WSContextDataHolder.getRequestPath());
            context.setBrowserIpAddress(WSContextDataHolder.getClientIpAddress());
            SoapMessage msg = (SoapMessage) requestData.getMsgContext();
            msg.put(SecurityContext.class, new OpenFlameWSSecurityContext());
            WSContextDataHolder.setUserAuthenticationData(null, sessionToken);
        } else {
            logger.warn("No such token [" + sessionToken + "] is registered.");
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        }
    }

}