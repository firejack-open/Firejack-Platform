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

package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.registry.domain.Search;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SearchModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.HtmlResourceVersionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.lookup.LookupStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("searchListBroker")
public class SearchListBroker
		extends ListBroker<SearchModel, Search, NamedValues> {

	@Autowired
	@Qualifier("lookupStore")
	private LookupStore lookupStore;

    @Autowired
	@Qualifier("htmlResourceVersionStore")
    private IResourceVersionStore<HtmlResourceVersionModel> htmlResourceVersionStore;

	@Override
	protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		String term = (String) request.getData().get("term");
		String lookup = (String) request.getData().get("lookup");
		String assetType = (String) request.getData().get("assetType");

		RegistryNodeType registryNodeType = null;
		if (StringUtils.isNotBlank(assetType)) {
			registryNodeType = RegistryNodeType.find(assetType);
		}

		return lookupStore.searchCount(term, lookup, registryNodeType);
	}

	@Override
	protected List<SearchModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		String term = (String) request.getData().get("term");
		String lookup = (String) request.getData().get("lookup");
		Paging paging = (Paging) request.getData().get("paging");
		String assetType = (String) request.getData().get("assetType");

		RegistryNodeType registryNodeType = null;
		if (StringUtils.isNotBlank(assetType)) {
			registryNodeType = RegistryNodeType.find(assetType);
		}

		List<SearchModel> searchModels = lookupStore.search(term, lookup, registryNodeType, paging);
        for (SearchModel searchModel : searchModels) {
            String descriptionResourceLookup = searchModel.getLookup() + ".opf-doc.description";
            HtmlResourceVersionModel resourceVersion = htmlResourceVersionStore.findLastVersionByLookup(descriptionResourceLookup);
            if (resourceVersion != null) {
                String description = resourceVersion.getHtml();
                searchModel.setDescription(description);
            }
        }
        return searchModels;
	}
}
