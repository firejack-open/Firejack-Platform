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
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.oauth.OAuthAccessor;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class OAuthEchoHandler extends BasicOAuthRequestHandler {
    private static final Logger logger = Logger.getLogger(OAuthEchoHandler.class);
    public static final String DEFAULT_ECHO_HANDLER_URL = "/oauth-provider/echo";

    /**
     * @param urlEntryPoint
     */
    public OAuthEchoHandler(String urlEntryPoint) {
        super(urlEntryPoint);
    }

    /***/
    public OAuthEchoHandler() {
        this(DEFAULT_ECHO_HANDLER_URL);
    }

    @Override
    protected void processRequest(OAuthRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.processRequest(request, response);
        try {
            OAuthAccessor accessor = OAuthProcessor.getAccessor(request.getMessage());
            validateMessage(request, accessor);

            AuthenticationToken openFlameToken = (AuthenticationToken) accessor.getProperty(
                    OpenFlameSecurityConstants.OAUTH_OPF_TOKEN_ATTRIBUTE);

            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(openFlameToken == null || openFlameToken.getUser() == null ?
                    "[Your UserId is blank]" :
                    "[Your UserId:" + openFlameToken.getUser().getUsername() + "]");
            for (Map.Entry<String, String[]> parameter : request.getHttpParameters().entrySet()) {
                String[] values = parameter.getValue();
                for (String value : values) {
                    out.println(parameter.getKey() + ": " + value);
                }
            }
            out.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            OAuthProcessor.handleException(e, request.getHttpRequest(), response, false);
        }
    }
}