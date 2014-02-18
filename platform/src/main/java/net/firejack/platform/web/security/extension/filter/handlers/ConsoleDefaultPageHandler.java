/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.extension.filter.handlers;

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.extension.filter.IDefaultTargetUrlProvider;
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import net.firejack.platform.web.security.filter.handlers.IAuthenticationSuccessHandler;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

/**
 * This implementation of authentication success handler provide logic that
 */
public class ConsoleDefaultPageHandler implements IAuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(ConsoleDefaultPageHandler.class);

    private IDefaultTargetUrlProvider defaultTargetUrlProvider;

    /**
     * @param defaultTargetUrlProvider
     */
    public ConsoleDefaultPageHandler(IDefaultTargetUrlProvider defaultTargetUrlProvider) {
        this.defaultTargetUrlProvider = defaultTargetUrlProvider;
    }

    @Override
    public void onSuccessAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse, List<String> notRedirectUrls) throws IOException {
        String defaultTargetUrl = Env.FIREJACK_URL.getValue() + defaultTargetUrlProvider.getDefaultTargetUrl();
        if (ConfigContainer.isAppInstalled()) {
            String redirectUrl = httpRequest.getParameter(OpenFlameFilter.PARAM_REDIRECT);
            if (StringUtils.isNotBlank(redirectUrl)) {
                redirectUrl = URLDecoder.decode(redirectUrl, "utf-8");
                if (notRedirectUrls.contains(redirectUrl)) {
                    redirectUrl = defaultTargetUrl;
                } else {
                    redirectUrl = OpenFlameSecurityConstants.getBaseUrl() + redirectUrl;
                }
            }
            defaultTargetUrl = StringUtils.defaultIfEmpty(redirectUrl, defaultTargetUrl);
        }
        logger.info("-----------  Redirect to url: " + defaultTargetUrl);
        httpResponse.sendRedirect(defaultTargetUrl);
    }

}