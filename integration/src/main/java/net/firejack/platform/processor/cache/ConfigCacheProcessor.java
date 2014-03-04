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
