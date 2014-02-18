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