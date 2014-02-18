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