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

package net.firejack.platform.service.config.broker.config;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.processor.cache.ConfigCacheProcessor;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TrackDetails
@Component
public class CreateBatchConfigBroker extends ServiceBroker<ServiceRequest<Config>, ServiceResponse<Config>> {

    @Autowired
    private IConfigStore store;
    @Autowired
    private ConfigCacheProcessor configCacheProcessor;
    @Autowired
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Override
    protected ServiceResponse<Config> perform(ServiceRequest<Config> request) throws Exception {
        List<Config> configs = request.getDataList();
        List<ConfigModel> configModels = factory.convertFrom(ConfigModel.class, configs);

        Map<String, RegistryNodeModel> parentModelMap = new HashMap<String, RegistryNodeModel>();
        for (ConfigModel configModel : configModels) {
            String parentLookup = DiffUtils.extractPathFromLookup(configModel.getLookup());
            RegistryNodeModel parentModel = parentModelMap.get(parentLookup);
            if (parentModel == null) {
                parentModel = registryNodeStore.findByLookup(parentLookup);
                parentModelMap.put(parentLookup, parentModel);
            }
            configModel.setParent(parentModel);
            store.save(configModel);
        }

        configs = factory.convertTo(Config.class, configModels);
        for (Config config : configs) {
            configCacheProcessor.saveConfig(config);
        }
        return new ServiceResponse<Config>(configs, "Configs have been saved successfully.", true);
    }



}
