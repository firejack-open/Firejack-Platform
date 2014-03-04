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