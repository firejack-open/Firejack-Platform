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