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
