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

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;

public interface IConfigService {

	/**
	 * Read config by id
	 *
	 * @param configId config id
	 *
	 * @return founded config
	 */
	ServiceResponse<Config> readConfig(Long configId);

	/**
	 * Read config by lookup
	 *
	 * @param lookup lookup
	 * @param type config type
	 *
	 * @return founded type
	 */
	ServiceResponse<Config> findByLookup(String lookup, ConfigType type);

    ServiceResponse<Config> findListByLookup(ListLookup listLookup);

	/**
	 * Read all by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded configs
	 */
	ServiceResponse<Config> readAllConfigsByRegistryNodeId(Long registryNodeId);

	/**
	 * Search configs  by parent and term expression
	 *
	 * @param parentId parent id
	 * @param term term expression
	 *
	 * @return founded configs
	 */
	ServiceResponse<Config> searchConfig(Long parentId, String term);

		/**
	 * Create new config
	 *
	 * @param data config data
	 *
	 * @return created config
	 */
	ServiceResponse<Config> createConfig(Config data);

    ServiceResponse<Config> createBatchConfig(List<Config> configs);

	/**
	 * Update config by id
	 *
	 * @param configId config id
	 *
	 * @param data config config
	 *
	 * @return updated config
	 */
	ServiceResponse<Config> updateConfig(Long configId, Config data);

	/**
	 * Delete config by id
	 *
	 * @param configId config id
	 *
	 * @return deleted config
	 */
	ServiceResponse<Config> deleteConfig(Long configId);
}
