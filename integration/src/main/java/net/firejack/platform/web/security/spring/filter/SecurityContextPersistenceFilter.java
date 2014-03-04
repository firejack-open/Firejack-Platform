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

package net.firejack.platform.web.security.spring.filter;

import net.firejack.platform.utils.SessionManager;
import net.firejack.platform.utils.WebUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SecurityContextPersistenceFilter extends org.springframework.security.web.context.SecurityContextPersistenceFilter {

    private String signOutHandlerUrl;
    private String signInPage;
    private SessionManager sessionManager;

    /**
     * @param signOutHandlerUrl
     */
    @Required
    public void setSignOutHandlerUrl(String signOutHandlerUrl) {
        this.signOutHandlerUrl = signOutHandlerUrl;
    }

    /**
     * @param sessionManager
     */
    @Required
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * @param signInPage
     */
    @Required
    public void setSignInPage(String signInPage) {
        this.signInPage = signInPage;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        String requestUri = WebUtils.getRequestPath(httpRequest);
        if (requestUri.equalsIgnoreCase(signOutHandlerUrl)) {
            HttpSession session = httpRequest.getSession();
            if (session != null) {
                sessionManager.logout(session.getId());
            }
            HttpServletResponse httpResponse = (HttpServletResponse) res;
            logger.info("-----------  Redirect to url: " + signInPage);
            httpResponse.sendRedirect(signInPage);
        } else {
            super.doFilter(req, res, chain);
        }
    }
}