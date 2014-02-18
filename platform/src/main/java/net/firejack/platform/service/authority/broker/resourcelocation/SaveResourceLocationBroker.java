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

package net.firejack.platform.service.authority.broker.resourcelocation;

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.store.registry.IResourceLocationStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@TrackDetails
@Component("saveResourceLocationBrokerEx")
public class SaveResourceLocationBroker extends SaveBroker
        <ResourceLocationModel, ResourceLocation, ResourceLocation> {

    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore resourceLocationStore;

    @Override
    protected ResourceLocationModel convertToEntity(ResourceLocation resourceLocation) {
        return factory.convertFrom(ResourceLocationModel.class, resourceLocation);
    }

    @Override
    protected ResourceLocation convertToModel(ResourceLocationModel resourceLocationModel) {
        return factory.convertTo(ResourceLocation.class, resourceLocationModel);
    }

    @Override
    protected void save(ResourceLocationModel resourceLocationModel)
		    throws Exception {
        resourceLocationStore.saveWithPermission(resourceLocationModel);
    }
}