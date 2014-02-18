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

package net.firejack.platform.web.security.ws.callback;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.ws.WSContextDataHolder;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class PasswordCallbackHandler implements CallbackHandler {

    protected final Logger logger = Logger.getLogger(getClass());

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        logger.debug("identifier: " + pc.getIdentifier());

        String token = WSContextDataHolder.getCurrentToken();
        if (token == null) {
            logger.info("WS: Token has not found in request. Checking authentication...");
            processAuthentication(pc);
        } else {
            if (processSTSExpiration(token)) {
                //session has expired. So we need to ask authentication again
                logger.info("Session token [" + token + "] has expired. Trying to ask authentication.");
                processAuthentication(pc);
            } else {
                //user already signed in, session expiration period was prolonged - so we can accept authentication headers
                acceptAuthentication(pc, token);
            }
        }
    }

    protected void processAuthentication(WSPasswordCallback pc) throws IOException {
        logger.info("Asking authentication token from STS using identifier: " + pc.getIdentifier() +
                (StringUtils.isBlank(pc.getPassword()) ? ", and empty password." : ""));
        AuthenticationToken authenticationToken = requestAuthenticationToken(pc.getIdentifier(), pc.getPassword());
        if (authenticationToken == null || StringUtils.isBlank(authenticationToken.getToken())) {
            logger.info("STS has returned empty authentication token for used credentials.");
            processFailureAuthentication(pc);
        } else {
            logger.info("STS has returned authentication token [" + authenticationToken.getToken() + "]");
            processSuccessfulAuthentication(pc, authenticationToken);
        }
    }

    protected void acceptAuthentication(WSPasswordCallback pc, String token) {
        WSContextDataHolder.setUserAuthenticationData(pc.getIdentifier(), token);
        logger.info("WS: Client has authenticated successfully using [" + pc.getIdentifier() + "] account...");
    }

    protected void processFailureAuthentication(WSPasswordCallback pc) throws IOException {
        logger.warn("WS: Failed to authenticate client using [" + pc.getIdentifier() + "].");
        throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
    }

    protected void processSuccessfulAuthentication(WSPasswordCallback pc, AuthenticationToken authenticationToken) {
        String currentRequestPath = WSContextDataHolder.getRequestPath();
        OPFContext.initContext(new UserPrincipal(authenticationToken.getUser()), authenticationToken.getToken());
        OPFContext.getContext().setCurrentRequestPath(currentRequestPath);
        OPFContext.getContext().setBrowserIpAddress(WSContextDataHolder.getClientIpAddress());
        acceptAuthentication(pc, authenticationToken.getToken());
    }

    protected boolean processSTSExpiration(String token) {
        return ContextManager.isSessionExpired(token);
    }

    protected AuthenticationToken requestAuthenticationToken(String userName, String password) {
        ServiceResponse<AuthenticationToken> response;
        if (OpenFlameSecurityConstants.isSiteMinderAuthSupported()) {
            response = OPFEngine.AuthorityService.processSMWSSignIn(
                    userName, password, WSContextDataHolder.getClientIpAddress());
        } else {
            response = OPFEngine.AuthorityService.processSTSSignIn(
                    userName, password, WSContextDataHolder.getClientIpAddress());
        }

        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("AuthorityService API returned failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

}