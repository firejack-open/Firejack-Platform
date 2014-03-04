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
import net.firejack.platform.core.broker.DeleteLookupModelBroker;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.processor.cache.ConfigCacheProcessor;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("deleteConfigBroker")
public class DeleteConfigBroker extends DeleteLookupModelBroker<ConfigModel> {

	@Autowired
	private IConfigStore store;
	@Autowired
    private ConfigCacheProcessor configCacheProcessor;

	@Override
	protected String getSuccessMessage() {
		return "Config has deleted successfully";
	}

	@Override
	protected IStore<ConfigModel, Long> getStore() {
		return store;
	}

	@Override
	protected void delete(Long id) {
        ConfigModel configModel = store.findById(id);
        store.delete(configModel);

        Config config = factory.convertTo(Config.class, configModel);
        configCacheProcessor.removeConfig(config);
	}
}

