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