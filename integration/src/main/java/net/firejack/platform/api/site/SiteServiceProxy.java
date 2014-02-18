/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
