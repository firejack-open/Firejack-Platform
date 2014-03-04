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


public class CachedEntry<T> {

    protected String key;
    protected ICacheStore cacheStore;
    protected T cachedObject;
    private long timeToLive;
    private long liveStartTime;

    /**
     * @param key
     * @param cachedObject
     * @param timeToLive
     * @param cacheStore
     */
    public CachedEntry(String key, T cachedObject, long timeToLive, ICacheStore cacheStore) {
        if (cacheStore == null) {
            throw new IllegalArgumentException("Cache Store should not be blank.");
        }
        this.liveStartTime = System.currentTimeMillis();
        this.timeToLive = timeToLive;
        this.key = key;
        this.cachedObject = cachedObject;
        this.cacheStore = cacheStore;
    }

    /**
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * @return
     */
    public T getCachedObject() {
        if (isEntryExpired()) {
            if (this.cacheStore.isLocal()) {
                cachedObject = null;
            } else {
                cachedObject = (T) this.cacheStore.loadFromCache(key);
            }
        }
        return cachedObject;
    }

    protected boolean isEntryExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - liveStartTime > timeToLive;
    }

}