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
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.processor.cache.ConfigCacheProcessor;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createConfigBroker")
public class CreateConfigBroker extends OPFSaveBroker<ConfigModel, Config, Config> {

	@Autowired
	private IConfigStore store;
	@Autowired
    private ConfigCacheProcessor configCacheProcessor;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Config has been created successfully.";
	}

	@Override
	protected ConfigModel convertToEntity(Config model) {
		return factory.convertFrom(ConfigModel.class, model);
	}

	@Override
	protected Config convertToModel(ConfigModel entity) {
		return factory.convertTo(Config.class, entity);
	}

	@Override
	protected void save(ConfigModel model) throws Exception {
		store.save(model);

        Config config = factory.convertTo(Config.class, model);
        configCacheProcessor.saveConfig(config);
	}
}
