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
