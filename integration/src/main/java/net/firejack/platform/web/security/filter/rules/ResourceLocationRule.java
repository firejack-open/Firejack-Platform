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

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.security.filter.ISecurityFilter;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.resource.IResourceLocationContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ResourceLocationRule extends BaseAuthorizationRule<ResourceLocation> {
    
    @Override
    protected ResourceLocation detectElement(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        ResourceLocation vo = null;
        OPFContext context = OPFContext.getContext();
        IResourceLocationContainer resourceLocationContainer = context.getResourceLocationContainer();
        List<ResourceLocation> wildcardUrlList = resourceLocationContainer.getResourceLocationList();
        if (wildcardUrlList != null && !wildcardUrlList.isEmpty()) {
            String requestUri = WebUtils.getRequestPath(httpRequest);
            for (ResourceLocation resourceLocation : wildcardUrlList) {
                boolean urlMatches = SecurityUtils.matches(requestUri, resourceLocation);
                if (urlMatches) {
                    vo = resourceLocation;
                    break;
                }
            }
        }
        return vo;
    }

}