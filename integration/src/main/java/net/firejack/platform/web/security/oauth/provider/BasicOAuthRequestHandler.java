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

import net.oauth.OAuthAccessor;
import org.apache.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BasicOAuthRequestHandler implements IOAuthRequestHandler {

    private String urlEntryPoint;
    private static final Logger logger = Logger.getLogger(BasicOAuthRequestHandler.class);

    protected BasicOAuthRequestHandler(String urlEntryPoint) {
        this.urlEntryPoint = urlEntryPoint;
    }

    public String getUrlEntryPoint() {
        return urlEntryPoint;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void processPOST(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        processRequest(httpRequest, httpResponse);
    }

    @Override
    public void processGET(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        processRequest(httpRequest, httpResponse);
    }

    protected void processRequest(OAuthRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    }

    protected void validateMessage(OAuthRequest request, OAuthAccessor accessor) throws Exception {
        try {
            OAuthProcessor.validateMessage(request.getMessage(), accessor);
        } catch (Exception e) {
            logger.error("Exception occurred during request message validation.");
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}