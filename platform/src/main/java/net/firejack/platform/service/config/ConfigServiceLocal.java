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
package net.firejack.platform.service.config;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.config.IConfigService;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.config.broker.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_CONFIG_SERVICE)
public class ConfigServiceLocal implements IConfigService {

	@Autowired
	private ReadConfigBroker readConfigBroker;
	@Autowired
	private FindByLookupConfigBroker findByLookupConfigBroker;
    @Autowired
	private FindListByLookupConfigBroker findListByLookupConfigBroker;
	@Autowired
	private ReadConfigListByRegistryNodeBroker readConfigListByRegistryNodeBroker;
	@Autowired
	private SearchConfigListBroker searchConfigListBroker;
	@Autowired
	private CreateConfigBroker createConfigBroker;
    @Autowired
    private CreateBatchConfigBroker createBatchConfigBroker;
	@Autowired
	private UpdateConfigBroker updateConfigBroker;
	@Autowired
	private DeleteConfigBroker deleteConfigBroker;

	@Override
	public ServiceResponse<Config> readConfig(Long configId) {
		return readConfigBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(configId)));
	}

	@Override
	public ServiceResponse findByLookup(String lookup, ConfigType type) {
		NamedValues<Object> parameterized = new NamedValues<Object>();
		parameterized.put("lookup", lookup);
		parameterized.put("type", type);
		return findByLookupConfigBroker.execute(new ServiceRequest<NamedValues<Object>>(parameterized));
	}

    @Override
    public ServiceResponse<Config> findListByLookup(ListLookup listLookup) {
        return findListByLookupConfigBroker.execute(new ServiceRequest<ListLookup>(listLookup));
    }

    @Override
	public ServiceResponse<Config> readAllConfigsByRegistryNodeId(Long registryNodeId) {
		return readConfigListByRegistryNodeBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
	}

	@Override
	public ServiceResponse<Config> searchConfig(Long parentId, String term) {
		NamedValues<Object> parameterized = new NamedValues<Object>();
		parameterized.put("parentId", parentId);
		parameterized.put("term", term);
		return searchConfigListBroker.execute(new ServiceRequest<NamedValues<Object>>(parameterized));
	}

	@Override
	public ServiceResponse<Config> createConfig(Config data) {
		return createConfigBroker.execute(new ServiceRequest<Config>(data));
	}

    @Override
    public ServiceResponse<Config> createBatchConfig(List<Config> configs) {
		return createBatchConfigBroker.execute(new ServiceRequest<Config>(configs));
	}

	@Override
	public ServiceResponse<Config> updateConfig(Long configId, Config data) {
		return updateConfigBroker.execute(new ServiceRequest<Config>(data));
	}

	@Override
	public ServiceResponse<Config> deleteConfig(Long configId) {
		return deleteConfigBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(configId)));
	}
}
