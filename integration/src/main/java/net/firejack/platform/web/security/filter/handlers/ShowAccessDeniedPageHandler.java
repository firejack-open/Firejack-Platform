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

package net.firejack.platform.web.security.filter.handlers;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShowAccessDeniedPageHandler implements IAccessDeniedHandler {

    private static final Logger logger = Logger.getLogger(ShowAccessDeniedPageHandler.class);

    private String accessDeniedPage;

    /**
     * @param accessDeniedPage
     */
    public ShowAccessDeniedPageHandler(String accessDeniedPage) {
        if (StringUtils.isBlank(accessDeniedPage)) {
            throw new IllegalArgumentException("Access Denied page url should not be empty.");
        }
        this.accessDeniedPage = accessDeniedPage;
    }

    @Override
    public void onAccessDenied(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException {
        OPFContext context = OPFContext.getContext();
        if (StringUtils.isNotBlank(context.getCurrentActionLookup())) {
            String apiCallMarkerHeaderValue = httpRequest.getHeader(OpenFlameSecurityConstants.MARKER_HEADER);
            if (StringUtils.isBlank(apiCallMarkerHeaderValue)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
            } else {
                OpenFlameSecurityConstants.printXmlErrorToResponse(
                        httpResponse, OpenFlameSecurityConstants.API_401_ERROR_RESPONSE);
            }
        } else {
            logger.info("-----------  Redirect to url: " + getAccessDeniedPage());
            httpResponse.sendRedirect(getAccessDeniedPage());
        }
    }

    /**
     * @return
     */
    public String getAccessDeniedPage() {
        return OpenFlameSecurityConstants.getBaseUrl() + accessDeniedPage;
    }
}