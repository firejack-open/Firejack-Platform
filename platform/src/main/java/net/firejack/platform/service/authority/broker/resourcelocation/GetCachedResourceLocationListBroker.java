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
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("getCachedResourceLocationListBroker")
public class GetCachedResourceLocationListBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse<ResourceLocation>> {

    public static final String PARAM_PACKAGE_LOOKUP = "package_lookup";

    @Override
    protected ServiceResponse<ResourceLocation> perform(ServiceRequest<NamedValues<String>> request)
		    throws Exception {
        String packageLookup = request.getData().get(PARAM_PACKAGE_LOOKUP);
        ServiceResponse<ResourceLocation> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<ResourceLocation>("packageLookup parameter should not be blank.", false);
        } else {
            List<ResourceLocation> navList = CacheManager.getInstance().getResourceLocations(packageLookup);
            response = new ServiceResponse<ResourceLocation>(navList);
        }
        return response;
    }

}