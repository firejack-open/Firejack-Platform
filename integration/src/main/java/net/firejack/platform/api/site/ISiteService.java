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

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.response.ServiceResponse;

public interface ISiteService {

    ServiceResponse<NavigationElement> readNavigationElementBroker(Long navigationElementId);

    ServiceResponse<NavigationElement> readTreeNavigationElementsByParentLookup(String lookup);

    ServiceResponse<NavigationElement> readNavigationElementsByParentId(Long parentId);

    ServiceResponse<RegistryNodeTree> createNavigationElement(NavigationElement data);

    ServiceResponse<RegistryNodeTree> updateNavigationElement(NavigationElement data);

    ServiceResponse deleteNavigationElement(Long navigationId);

    ServiceResponse<NavigationElement> readCachedNavigationElements(String packageLookup);

    ServiceResponse<NavigationElementTree> readTreeNavigationMenu(String packageLookup, boolean lazyLoadResource);

}
