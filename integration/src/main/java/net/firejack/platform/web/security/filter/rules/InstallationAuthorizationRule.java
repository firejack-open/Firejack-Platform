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