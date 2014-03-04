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

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static net.firejack.platform.web.security.model.OpenFlameSecurityConstants.*;

public class OAuthRequestTokenHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthRequestTokenHandler.class);
    public static final String DEFAULT_REQUEST_TOKEN_HANDLER_URL = "/oauth-provider/request_token";

    /**
     * @param urlEntryPoint
     */
    public OAuthRequestTokenHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthRequestTokenHandler() {
        this(DEFAULT_REQUEST_TOKEN_HANDLER_URL);
    }

    protected void processRequest(OAuthRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            OAuthConsumer consumer = OAuthProcessor.getConsumer(request.getMessage());
            OAuthAccessor accessor = new OAuthAccessor(consumer);

            validateMessage(request, accessor);
            // Support the 'Variable Accessor Secret' extension
            // described in http://oauth.pbwiki.com/AccessorSecret
            String secret = request.getMessage().getParameter(OAUTH_ACCESSOR_SECRET_PARAMETER);
            if (secret != null) {
                accessor.setProperty(OAuthConsumer.ACCESSOR_SECRET, secret);
            }
            // generate request_token and secret
            OAuthProcessor.generateRequestToken(accessor);

            response.setContentType("text/plain");
            OutputStream out = response.getOutputStream();
            OAuth.formEncode(OAuth.newList(OAUTH_TOKEN_PARAMETER, accessor.requestToken,
                    OAUTH_TOKEN_SECRET_PARAMETER, accessor.tokenSecret), out);
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, true);
        }
    }

}