/**
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
package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.registry.domain.Social;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@TrackDetails
public class SocialInfoBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Social>> {

    public static final String[] names = new String[]{"Facebook", "Twitter", "LinkedIn", "Google"};

    @Override
    protected ServiceResponse<Social> perform(ServiceRequest<NamedValues> request) throws Exception {
        NamedValues data = request.getData();
        String packageLookup = (String) data.get("packageLookup");

        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        List<Social> socials = new ArrayList<Social>();
        for (String name : names) {
            Social social = new Social();
            social.setName(name);

            Config config = configCacheManager.getConfig(DiffUtils.lookup(packageLookup, name + " enable"));
            Boolean enabled = config == null ? false : Boolean.valueOf(config.getValue());
            social.setEnabled(enabled);

            socials.add(social);
        }

        return new ServiceResponse<Social>(socials, "Check successfully", true);
    }
}
