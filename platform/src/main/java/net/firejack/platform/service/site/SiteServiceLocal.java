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

package net.firejack.platform.service.site;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.site.ISiteService;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.site.broker.navigation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_SITE_SERVICE)
public class SiteServiceLocal implements ISiteService {

    @Autowired
    @Qualifier("readNavigationElementBroker")
    private ReadNavigationElementBroker readNavigationElementBroker;

    @Autowired
    @Qualifier("readTreeNavigationElementsByParentLookupBroker")
    private ReadTreeNavigationElementsByParentLookupBroker readTreeNavigationElementsByParentLookupBroker;

    @Autowired
    @Qualifier("readNavigationElementsByParentIdBroker")
    private ReadNavigationElementsByParentIdBroker readNavigationElementsByParentIdBroker;

    @Autowired
    @Qualifier("createNavigationElementBroker")
    private CreateNavigationElementBroker createNavigationElementBroker;

    @Autowired
    @Qualifier("updateNavigationElementBroker")
    private UpdateNavigationElementBroker updateNavigationElementBroker;

    @Autowired
    @Qualifier("deleteNavigationElementBroker")
    private DeleteNavigationElementBroker deleteNavigationElementBroker;

    @Autowired
    @Qualifier("getCachedNavigationListBroker")
    private GetCachedNavigationListBroker getCachedNavigationListBroker;

    @Autowired
    @Qualifier("readTreeNavigationMenuListBroker")
    private ReadTreeNavigationMenuListBroker readNavigationMenuListBroker;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    SITE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<NavigationElement> readNavigationElementBroker(Long navigationElementId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(navigationElementId);
        return readNavigationElementBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<NavigationElement> readTreeNavigationElementsByParentLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readTreeNavigationElementsByParentLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<NavigationElement> readNavigationElementsByParentId(Long parentId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(parentId);
        return readNavigationElementsByParentIdBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createNavigationElement(NavigationElement data) {
        return createNavigationElementBroker.execute(new ServiceRequest<NavigationElement>(data));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateNavigationElement(NavigationElement data) {
        return updateNavigationElementBroker.execute(new ServiceRequest<NavigationElement>(data));
    }

    @Override
    public ServiceResponse deleteNavigationElement(Long navigationElementId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(navigationElementId);
        return deleteNavigationElementBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<NavigationElement> readCachedNavigationElements(String packageLookup) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(GetCachedNavigationListBroker.PARAM_PACKAGE_LOOKUP, packageLookup);
        return getCachedNavigationListBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

	@Override
	public ServiceResponse<NavigationElementTree> readTreeNavigationMenu(String packageLookup, boolean lazyLoadResource) {
		NamedValues values = new NamedValues();
		values.put("lookup", packageLookup);
		values.put("lazyLoadResource", lazyLoadResource);
		return readNavigationMenuListBroker.execute(new ServiceRequest<NamedValues>(values));
	}

}
