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