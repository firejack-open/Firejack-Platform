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

package net.firejack.platform.service.site.broker.navigation;

import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.broker.ChildListByParentIdBroker;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("readNavigationElementsByParentIdBroker")
@TrackDetails
public class ReadNavigationElementsByParentIdBroker
        extends ChildListByParentIdBroker<NavigationElementModel, NavigationElement> {

    @Autowired
    @Qualifier("navigationElementStore")
    private IRegistryNodeStore<NavigationElementModel> store;

    @Override
    protected ILookupStore<NavigationElementModel, Long> getStore() {
        return store;
    }

}