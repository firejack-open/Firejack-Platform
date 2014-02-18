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

import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ShowSignInPageHandler implements IAuthenticationFailureHandler {

    private String signInPage;
    private static final Logger logger = Logger.getLogger(ShowSignInPageHandler.class);

    /**
     * @param signInPage
     */
    public ShowSignInPageHandler(String signInPage) {
        if (StringUtils.isBlank(signInPage)) {
            throw new IllegalArgumentException("Sign In page url should not be empty.");
        }
        this.signInPage = signInPage;
    }

    @Override
    public void onFailure(HttpServletRequest httpRequest, HttpServletResponse httpResponse, List<String> notRedirectUrls) throws IOException {
        String url = WebUtils.getNormalizedUrl(httpRequest.getServerName(), httpRequest.getLocalPort(), httpRequest.getContextPath());
        OpenFlameSecurityConstants.setBaseUrl(url);
        String platform = WebUtils.getNormalizedUrl(httpRequest.getServerName(), httpRequest.getLocalPort(), "/platform");
        Env.FIREJACK_URL.setValue(platform);

        String redirectUrl = httpRequest.getServletPath();
        if (notRedirectUrls.contains(redirectUrl)) {
            logger.info("-----------  Redirect to url: " + getSignInPage());
            httpResponse.sendRedirect(getSignInPage());
        } else {
            String queryString = httpRequest.getQueryString();
            if (StringUtils.isNotBlank(queryString)) {
                redirectUrl += "?" + queryString;
            }
            logger.info("-----------  Redirect from url: '" + redirectUrl + "' to url: '" + getSignInPage() + "'");
            httpResponse.sendRedirect(getSignInPage() + "?" + OpenFlameFilter.PARAM_REDIRECT + "=" + URLEncoder.encode(redirectUrl, "UTF-8"));
        }
    }

    /**
     * @return
     */
    public String getSignInPage() {
        return OpenFlameSecurityConstants.getBaseUrl() + signInPage;
    }

//    private boolean isUrlContains(List<String> notRedirectUrls, String redirectUrl) {
//        boolean isContains = false;
//        for (String notRedirectUrl : notRedirectUrls) {
//            isContains |= (OpenFlameSecurityConstants.getBaseUrl() + notRedirectUrl).equals(redirectUrl);
//        }
//        return isContains;
//    }

}