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

package net.firejack.platform.api.config;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;

public class ConfigServiceProxy extends AbstractServiceProxy implements IConfigService {

	public ConfigServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
	public String getServiceUrlSuffix() {
		return "/config";
	}

	@Override
	public ServiceResponse<Config> readConfig(Long configId) {
		return get("/config/" + configId);
	}

	@Override
	public ServiceResponse<Config> findByLookup(String lookup, ConfigType type) {
		return get("/config/by-lookup/" + lookup + "/" + type);
	}

    @Override
    public ServiceResponse<Config> findListByLookup(ListLookup listLookup) {
        return post2("/config/list/by-lookup", listLookup);
    }

    @Override
	public ServiceResponse<Config> readAllConfigsByRegistryNodeId(Long registryNodeId) {
		return get("/config/node/" + registryNodeId);
	}

	@Override
	public ServiceResponse<Config> searchConfig(Long parentId, String term) {
		return get("/config/node/search/" + parentId + "/" + term);
	}

	@Override
	public ServiceResponse<Config> createConfig(Config data) {
		return post2("/config", data);
	}

	@Override
	public ServiceResponse<Config> createBatchConfig(List<Config> data) {
		return post("/config/batch", data);
	}

	@Override
	public ServiceResponse<Config> updateConfig(Long configId, Config data) {
		return put2("/config/" + configId, data);
	}

	@Override
	public ServiceResponse<Config> deleteConfig(Long configId) {
		return delete("/config/" + configId);
	}
}
