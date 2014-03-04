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