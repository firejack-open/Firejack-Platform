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

package net.firejack.platform.web.security.oauth.provider;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.*;

public class OAuthAccessTokenHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthAccessTokenHandler.class);
    public static final String DEFAULT_ACCESS_TOKEN_HANDLER_URL = "/oauth-provider/access_token";

    /**
     * @param urlEntryPoint
     */
    public OAuthAccessTokenHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthAccessTokenHandler() {
        this(DEFAULT_ACCESS_TOKEN_HANDLER_URL);
    }

    protected void processRequest(OAuthRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(request.getMessage());
            validateMessage(request, accessor);

            // make sure token is authorized
            if (accessor.getProperty(OAUTH_OPF_TOKEN_ATTRIBUTE) == null) {
                throw new OAuthProblemException("permission_denied");
            }

            AuthenticationToken authenticationToken = (AuthenticationToken) accessor.getProperty(OAUTH_OPF_TOKEN_ATTRIBUTE);
            /*if (SecurityUtils.isSessionExpired(getSTSBaseUrl() + URL_STS_SESSION_EXPIRATION, authenticationToken.getToken())) {
                               throw new OAuthProblemException("token_expired");
                           }*/
            if (ContextManager.isSessionExpired(authenticationToken.getToken())) {
                throw new OAuthProblemException("token_expired");
            }

            // generate access token and secret
            OAuthProcessor.generateAccessToken(accessor);

            response.setContentType("text/plain");
            OutputStream out = response.getOutputStream();
            OAuth.formEncode(
                    OAuth.newList(OAUTH_TOKEN_PARAMETER, accessor.accessToken,
                            OAUTH_TOKEN_SECRET_PARAMETER, accessor.tokenSecret), out);
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, true);
        }
    }
}