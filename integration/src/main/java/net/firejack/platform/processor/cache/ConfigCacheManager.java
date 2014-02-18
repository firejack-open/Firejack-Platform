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

import java.util.HashMap;
import java.util.Map;

public class ConfigCacheManager {

    private final Map<String, Config> configs = new HashMap<String, Config>();

    public static ConfigCacheManager manager;

    private ConfigCacheManager() {
    }

    public static ConfigCacheManager getInstance() {
        if (manager == null) {
            manager = new ConfigCacheManager();
        }
        return manager;
    }

    public Config getConfig(String lookup) {
        return configs.get(lookup);
    }

    public Map<String, Config> getConfigs() {
        return configs;
    }
}
