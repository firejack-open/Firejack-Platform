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