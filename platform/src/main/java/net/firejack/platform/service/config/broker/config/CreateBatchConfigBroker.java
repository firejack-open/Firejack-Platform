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
