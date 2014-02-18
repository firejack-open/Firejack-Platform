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

package net.firejack.platform.service.content.broker.resource.text;

import net.firejack.platform.api.content.domain.TextResource;
import net.firejack.platform.core.model.registry.resource.TextResourceModel;
import net.firejack.platform.core.model.registry.resource.TextResourceVersionModel;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.service.content.broker.resource.AbstractReadResourceByLookupBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component("readTextResourceByLookupBroker")
@TrackDetails
public class ReadTextResourceByLookupBroker
        extends AbstractReadResourceByLookupBroker<TextResourceModel, TextResourceVersionModel, TextResource> {

    @Autowired
    @Qualifier("textResourceStore")
    private IResourceStore<TextResourceModel> resourceStore;

    @Autowired
    @Qualifier("textResourceVersionStore")
    private IResourceVersionStore<TextResourceVersionModel> resourceVersionStore;

    @Override
    public IResourceStore<TextResourceModel> getResourceStore() {
        return resourceStore;
    }

    @Override
    public IResourceVersionStore<TextResourceVersionModel> getResourceVersionStore() {
        return resourceVersionStore;
    }

}