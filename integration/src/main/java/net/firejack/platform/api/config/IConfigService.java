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
