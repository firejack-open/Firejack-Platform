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

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IOAuthRequestHandler {

    /**
     * @return
     */
    String getUrlEntryPoint();

    /**
     * @param httpRequest
     * @param httpResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    void processPOST(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException;

    /**
     * @param httpRequest
     * @param httpResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    void processGET(OAuthRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException;

    /**
     * @param filterConfig
     * @throws javax.servlet.ServletException
     */
    void init(FilterConfig filterConfig) throws ServletException;

}