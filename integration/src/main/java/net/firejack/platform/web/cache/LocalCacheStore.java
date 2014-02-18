/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalCacheStore extends CacheStore {

    private final ConcurrentMap<String, Object> localCache = new ConcurrentHashMap<String, Object>();
    private static final Logger logger = Logger.getLogger(LocalCacheStore.class);

    @Override
    public Object loadFromCache(String key) {
        synchronized (localCache) {
            Object result;
            if (StringUtils.isBlank(key)) {
                result = null;
            } else {
                result = localCache.get(key);
            }
            return result;
        }
    }

    @Override
    public void saveToCache(String key, Object value) {
        synchronized (localCache) {
            if (StringUtils.isBlank(key)) {
                logger.warn("Trying to save object with nullable key.");
            } else {
                localCache.put(key, value);
                if (!CACHE_SEARCH_KEY.equals(key)) {
                    addKey(key);
                }
            }
        }
    }

    @Override
    public void removeFromCache(String key) {
        synchronized (localCache) {
            if (StringUtils.isBlank(key)) {
                logger.warn("Trying to delete object using nullable key.");
            } else {
                localCache.remove(key);
                removeKey(key);
            }
        }
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public void invalidate(String sessionToken, Long userId) {
        synchronized (localCache) {
            List<String> entriesToDelete = new ArrayList<String>();
            for (String key : localCache.keySet()) {
                if (key.startsWith(CacheManager.ACTIONS_KEY_PREFIX) ||
                        key.startsWith(CacheManager.RESOURCE_LOCATIONS_KEY_PREFIX) ||
                        key.startsWith(CacheManager.NAVIGATION_ELEMENTS_KEY_PREFIX) ||
                        key.equals(CacheManager.SECURED_RECORDS_KEY) ||
                        key.equals(CacheManager.GUEST_PERMISSIONS_KEY) ||
                        key.equals(CacheManager.PERMISSIONS_KEY_PREFIX + sessionToken) ||
                        key.equals(CacheManager.USER_INFO_KEY_PREFIX + sessionToken) ||
                        (userId != null && key.equals(CacheManager.ID_FILTER_KEY + userId)) ||
                        key.startsWith(CacheManager.CONTEXT_PERMISSIONS_KEY_PREFIX)
                        ) {
                    entriesToDelete.add(key);
                }
            }
            for (String key : entriesToDelete) {
                localCache.remove(key);
            }
        }
    }

	@Override
	public void close() {
		if (localCache != null) {
			localCache.clear();
		}
	}

    @Override
    public int getCachedElementExpirationTime() {
        return -1;
    }

}