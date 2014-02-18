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

package net.firejack.platform.processor.cache;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.model.event.ApplicationContextEvent;
import net.firejack.platform.model.event.ChangeConfigEvent;
import net.firejack.platform.model.event.IEvent;
import net.firejack.platform.web.cache.CacheManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("configCacheProcessor")
public class ConfigCacheProcessor implements ApplicationContextAware {

    private static final Logger logger = Logger.getLogger(ConfigCacheProcessor.class);

    private IEvent event;

    @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		event = new ApplicationContextEvent(applicationContext);
	}

	public IEvent getEvent() {
		if (event == null) {
			throw new IllegalStateException("Event publisher was not set.");
		}
		return event;
	}

    public void initConfigs() {
        loadConfigs();
    }

    private Map<String, Config> loadConfigs() {
        Map<String, Config> configs = ConfigCacheManager.getInstance().getConfigs();
        configs.clear();
        CacheManager cacheManager = CacheManager.getInstance();
        List<Config> loadedConfigs = cacheManager.getConfigs();
        if (loadedConfigs != null) {
            for (Config config : loadedConfigs) {
                configs.put(config.getLookup(), config);
            }
        } else {
            logger.warn("Can't get configs from memcached.");
        }
        return configs;
    }

    public void saveConfig(Config config) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, Config> configs = loadConfigs();
        configs.put(config.getLookup(), config);
        List<Config> changedConfigs = new ArrayList<Config>(configs.values());
        cacheManager.setConfigs(changedConfigs);
        getEvent().event(new ChangeConfigEvent(config));
    }

    public void removeConfig(Config config) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, Config> configs = loadConfigs();
        configs.remove(config.getLookup());
        List<Config> changedConfigs = new ArrayList<Config>(configs.values());
        cacheManager.setConfigs(changedConfigs);

        Config removedConfig = new Config();
        removedConfig.setLookup(config.getLookup());
        removedConfig.setValue(config.getValue());
        getEvent().event(new ChangeConfigEvent(removedConfig));
    }

    // need to refactor this method and use someone notification mechanism
    @Scheduled(fixedDelay = 300000)
    public void refreshMessageTemplates() {
        loadConfigs();
    }

}
