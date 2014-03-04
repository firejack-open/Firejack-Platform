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
