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
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

public class ShowDefaultPageHandler implements IAuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(ShowDefaultPageHandler.class);

    private String defaultPage;

    /**
     * @param defaultPage
     */
    public ShowDefaultPageHandler(String defaultPage) {
        if (StringUtils.isBlank(defaultPage)) {
            throw new IllegalArgumentException("Default Page url should not be empty.");
        }
        this.defaultPage = defaultPage;
    }

    @Override
    public void onSuccessAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse, List<String> notRedirectUrls) throws IOException {
        String redirectUrl = httpRequest.getParameter(OpenFlameFilter.PARAM_REDIRECT);
        if (StringUtils.isNotBlank(redirectUrl)) {
            redirectUrl = URLDecoder.decode(redirectUrl, "utf-8");
            if (notRedirectUrls.contains(redirectUrl)) {
                redirectUrl = getDefaultPage();
            } else {
                redirectUrl = OpenFlameSecurityConstants.getBaseUrl() + redirectUrl;
            }
        }
        redirectUrl = StringUtils.defaultIfEmpty(redirectUrl, getDefaultPage());
        logger.info("-----------  Redirect to url: " + redirectUrl);
        httpResponse.sendRedirect(redirectUrl);
    }

    /**
     * @return
     */
    public String getDefaultPage() {
        return OpenFlameSecurityConstants.getBaseUrl() + defaultPage;
    }

}