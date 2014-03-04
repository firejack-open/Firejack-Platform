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

package net.firejack.platform.api.site;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;

public class SiteServiceProxy extends AbstractServiceProxy implements ISiteService {

	public SiteServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/site";
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    NAVIGATION SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<NavigationElement> readNavigationElementBroker(Long navigationElementId) {
        return get("/navigation/" + navigationElementId);
    }

    @Override
    public ServiceResponse<NavigationElement> readTreeNavigationElementsByParentLookup(String lookup) {
        return get("/navigation/tree-by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<NavigationElement> readNavigationElementsByParentId(Long parentId) {
        return get("/navigation/list-by-parent-id/" + parentId);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createNavigationElement(NavigationElement navigationElement) {
        return post2("/navigation", navigationElement);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateNavigationElement(NavigationElement navigationElement) {
        return put2("/navigation", navigationElement);
    }

    @Override
    public ServiceResponse deleteNavigationElement(Long navigationId) {
        return delete("/navigation/" + navigationId);
    }

    @Override
    public ServiceResponse<NavigationElement> readCachedNavigationElements(String packageLookup) {
        ServiceResponse<NavigationElement> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<NavigationElement>("packageLookup parameter should not be blank", false);
        } else {
            response = get("/navigation/cached/" + packageLookup);
        }
        return response;
    }

    @Override
    public ServiceResponse<NavigationElementTree> readTreeNavigationMenu(String packageLookup, boolean lazyLoadResource) {
	    return get("/navigation/tree/" + packageLookup, "lazyLoadResource", lazyLoadResource);
    }

}
