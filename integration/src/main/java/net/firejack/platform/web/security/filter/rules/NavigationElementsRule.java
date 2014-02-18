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

import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.filter.IFilterFlowInterceptor;
import net.firejack.platform.web.security.filter.ISecurityFilter;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.navigation.INavElementContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class NavigationElementsRule extends BaseAuthorizationRule<NavigationElement> {

    @Override
    protected NavigationElement detectElement(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        OPFContext context = OPFContext.getContext();
        INavElementContainer navElementContainer = context.getNavElementContainer();
        List<NavigationElement> navElementList = navElementContainer.getNavElementList();
        NavigationElement nav = null;
        if (navElementList != null) {
            String requestUri = WebUtils.getRequestPath(httpRequest);
            for (NavigationElement vo : navElementList) {
                if (normalizeNavigationUrl(requestUri).equals(normalizeNavigationUrl(vo.getUrlPath()))) {
                    nav = vo;
                    OPFContext.getContext().setCurrentNavigationElement(nav);

                    List<IFilterFlowInterceptor> filterFlowInterceptors = securityFilter.getFilterFlowInterceptorList();
                    if (filterFlowInterceptors != null) {
                        for (IFilterFlowInterceptor interceptor : filterFlowInterceptors) {
                            interceptor.beforeExecution(httpRequest);
                        }
                    }
                    break;
                }
            }
        }
        return nav;
    }

    protected String normalizeNavigationUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        url = url.trim().toLowerCase();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

}