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

package net.firejack.platform.web.security.openid;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IAuthenticationProcessor;
import net.firejack.platform.web.security.filter.ISignInProcessor;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OpenIDAuthenticationProcessor implements IAuthenticationProcessor {

    private String openIdEntryPointUrl;
    private OpenIDConsumer openIDConsumer;
    private ISignInProcessor signInProcessor;
    private static final Logger logger = Logger.getLogger(OpenIDAuthenticationProcessor.class);

    /**
     * @param openIdEntryPointUrl
     */
    public void setOpenIdEntryPointUrl(String openIdEntryPointUrl) {
        this.openIdEntryPointUrl = openIdEntryPointUrl;
    }

    /**
     * @return
     */
    public String getOpenIdEntryPointUrl() {
        return openIdEntryPointUrl;
    }

    /**
     * @return
     */
    public OpenIDConsumer getOpenIDConsumer() {
        return openIDConsumer;
    }

    /**
     * @param openIDConsumer
     */
    public void setOpenIDConsumer(OpenIDConsumer openIDConsumer) {
        this.openIDConsumer = openIDConsumer;
    }

    /**
     * @return
     */
    public ISignInProcessor getSignInProcessor() {
        return signInProcessor;
    }

    /**
     * @param signInProcessor
     */
    public void setSignInProcessor(ISignInProcessor signInProcessor) {
        this.signInProcessor = signInProcessor;
    }

    @Override
    public boolean isAuthenticationCase(HttpServletRequest request) {
        String requestPath = WebUtils.getRequestPath(request);
        return requestPath.equalsIgnoreCase(getOpenIdEntryPointUrl());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws IOException, ServletException {
        if (getOpenIDConsumer().isResponseReady(httpRequest)) {
            AuthenticationToken authenticationToken = getOpenIDConsumer().processResponse(httpRequest);
            getSignInProcessor().processSignInInternal(httpRequest, httpResponse, authenticationToken);
        } else {
            String openIDIdentifier = httpRequest.getParameter(OpenFlameSecurityConstants.OPENID_PARAMETER_NAME);
            if (StringUtils.isNotBlank(openIDIdentifier)) {
                String destinationUrl = getOpenIDConsumer().processRequest(openIDIdentifier, httpRequest);
                if (StringUtils.isBlank(destinationUrl)) {
                    try {
                        httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new OpenFlameRuntimeException("Failed to send error [" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "].");
                    }
                } else {
                    try {
                        logger.info("-----------  Redirect to url: " + destinationUrl);
                        httpResponse.sendRedirect(destinationUrl);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new OpenFlameRuntimeException("Redirect to " + destinationUrl + " have failed.");
                    }
                }
            } else {
                try {
                    httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Should provide " +
                            OpenFlameSecurityConstants.OPENID_PARAMETER_NAME + " parameter.");
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new OpenFlameRuntimeException("Failed to send error [" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "].");
                }
            }
        }
    }

    @Override
    public void processUnAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // not used
    }

    @Override
    public void initialize(FilterConfig config) throws ServletException {
        //
    }

    @Override
    public void release() {
    }

}