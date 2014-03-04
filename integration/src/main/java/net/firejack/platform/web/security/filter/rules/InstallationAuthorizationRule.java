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

package net.firejack.platform.web.security.filter.rules;

import net.firejack.platform.api.authority.domain.SecurityDriven;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.ISecurityFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unchecked")
public class InstallationAuthorizationRule extends BaseAuthorizationRule<InstallationAuthorizationRule.SecurityInfo> {
    private static final String installationPattern = "(?i)^/console/installation.*$";
    private static final String installationRestPattern = "(?i)^/rest/installation.*$";
    private static final String registryInitPattern = "(?i)^/rest/init/registry/allowtypes.*$";
    private static final String ruleFormPattern = "(?i)^/rest/rule/form/initialisation.*$";
    private static final String fileManagerPattern = "(?i)^/rest/filemanager/directory.*$";

    public boolean isRuleCase(ISecurityFilter securityFilter, HttpServletRequest request, EntityProtocol protocol) {
        if (!ConfigContainer.isAppInstalled()) {
            putElement(detectElement(securityFilter, request, protocol));
            return true;
        }
        return false;
    }

    protected SecurityInfo detectElement(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        SecurityInfo securityInfoVO = new SecurityInfo();
        securityInfoVO.setUrlPath(WebUtils.getRequestPath(httpRequest));
        return securityInfoVO;
    }

    public Boolean authorizeAccess(
            ISecurityFilter securityFilter, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws IOException {
        SecurityInfo element = getElement();
        final String path = element.getUrlPath();
        if (path.matches(installationPattern)
            || path.matches(registryInitPattern)
            || path.matches(ruleFormPattern)
            || path.matches(installationRestPattern)
            || path.matches(fileManagerPattern)) {
            onSuccessfulAuthorization(element);
            return true;
        } else {
            // only for logging
            element.lookup = element.getUrlPath();
            onFailureAuthorization(element, securityFilter, httpRequest, httpResponse);
            return false;
        }
    }

    protected void onSuccessfulAuthorization(SecurityInfo element) {
        super.onSuccessfulAuthorization(element);
    }

    protected void onFailureAuthorization(
            SecurityInfo element, ISecurityFilter securityFilter,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException {
        super.onFailureAuthorization(element, securityFilter, httpRequest, httpResponse);
    }

    public class SecurityInfo implements SecurityDriven {
        private String urlPath;
        private String lookup;

        public String getUrlPath() {
            return urlPath;
        }

        public void setUrlPath(String urlPath) {
            this.urlPath = urlPath;
        }

        @Override
        public Long getId() {
            return null;
        }

        @Override
        public String getLookup() {
            return lookup;
        }

        @Override
        public List<String> produceRequiredPermissionList() {
            return null;
        }
    }

}