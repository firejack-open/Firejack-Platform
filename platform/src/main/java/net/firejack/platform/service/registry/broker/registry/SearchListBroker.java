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

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
