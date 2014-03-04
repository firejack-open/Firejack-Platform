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

package net.firejack.platform.web.security.extension.spring.handler;

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.security.spring.handler.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ConsoleAuthenticationSuccessHandler extends AuthenticationSuccessHandler {

    public static final String DEFAULT_APP_INSTALLATION_URL = "/installation";

    private String installationUrl;

    /**
     * @return
     */
    public String getInstallationUrl() {
        if (installationUrl == null) {
            installationUrl = DEFAULT_APP_INSTALLATION_URL;
        }
        return installationUrl;
    }

    /**
     * @param installationUrl
     */
    public void setInstallationUrl(String installationUrl) {
        this.installationUrl = installationUrl;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (ConfigContainer.isAppInstalled()) {
            return super.determineTargetUrl(request, response);
        }
        return getInstallationUrl();
    }
}