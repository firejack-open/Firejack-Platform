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