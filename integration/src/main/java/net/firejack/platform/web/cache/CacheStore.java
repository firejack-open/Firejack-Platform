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

package net.firejack.platform.web.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CacheStore implements ICacheStore {

    @Override
    public Map<String, Object> findByPrefix(String prefix) {
        Set<String> keys = (HashSet<String>) loadFromCache(CACHE_SEARCH_KEY);
        Map<String, Object> results = new HashMap<String, Object>();
        for (String key: keys) {
            if (key.startsWith(prefix)) {
                Object value = loadFromCache(key);
                results.put(key, value);
            }
        }
        return results;
    }
    
    protected void addKey(String key) {
        Set<String> keys = (HashSet<String>) loadFromCache(CACHE_SEARCH_KEY);
        if (keys == null) {
            keys = new HashSet<String>();
        }
        keys.add(key);
        saveToCache(CACHE_SEARCH_KEY, keys);
    }
    
    protected void removeKey(String key) {
        Set<String> keys = (HashSet<String>) loadFromCache(CACHE_SEARCH_KEY);
        if (keys != null) {
            keys.remove(key);
            saveToCache(CACHE_SEARCH_KEY, keys);
        }
    }
    
}
