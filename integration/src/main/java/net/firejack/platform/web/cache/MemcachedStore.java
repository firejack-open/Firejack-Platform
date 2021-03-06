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

import net.spy.memcached.MemcachedClient;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MemcachedStore extends CacheStore {

    private static final int CACHE_EXPIRATION_DEFAULT_TIME = 60 * 60 * 24 * 3;//3 twenty-four hours

    private static final Logger logger = Logger.getLogger(MemcachedStore.class);
    private MemcachedClientPool clientPool;

    @Override
    public Object loadFromCache(String key) {
        MemcachedClient client = getFromPool();
        Object result;
        try {
            result = client.get(key);
            returnToPool(client);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("Trying to re-obtain connection...");
            //clearPool();// ???
            client = getFromPool();
            try {
                result = client.get(key);
                returnToPool(client);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new IllegalStateException("Application has Lost connection to memcached server.");
            }
        }
        return result;
    }

    @Override
    public void saveToCache(String key, Object value) {
        try {
            MemcachedClient memcachedClient = this.clientPool.borrowObject();
            memcachedClient.set(key, getCachedElementExpirationTime(), value);

            if (!CACHE_SEARCH_KEY.equals(key)) {
                @SuppressWarnings("unchecked")
                Set<String> keys = (HashSet<String>) memcachedClient.get(CACHE_SEARCH_KEY);
                if (keys == null) {
                    keys = new HashSet<String>();
                }
                keys.add(key);
                memcachedClient.set(CACHE_SEARCH_KEY, getCachedElementExpirationTime(), keys);
                //addKey(key);
            }
            this.clientPool.returnObject(memcachedClient);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void removeFromCache(String key) {
        MemcachedClient client = getFromPool();
        client.delete(key);
        @SuppressWarnings("unchecked")
        Set<String> keys = (HashSet<String>) client.get(CACHE_SEARCH_KEY);
        if (keys == null) {
            //let's insure that we store keys set anyway
            keys = new HashSet<String>();
            client.set(CACHE_SEARCH_KEY, getCachedElementExpirationTime(), keys);
        } else if (keys.remove(key)) {
            client.set(CACHE_SEARCH_KEY, getCachedElementExpirationTime(), keys);
        }
        returnToPool(client);
    }

    @Override
    public Map<String, Object> findByPrefix(String prefix) {
        MemcachedClient client = getFromPool();
        @SuppressWarnings("unchecked")
        Set<String> keys = (HashSet<String>) client.get(CACHE_SEARCH_KEY);
        Map<String, Object> results = new HashMap<String, Object>();
        if (keys == null) {
            //let's insure that we store keys set anyway
            keys = new HashSet<String>();
            client.set(CACHE_SEARCH_KEY, getCachedElementExpirationTime(), keys);
        } else {
            for (String key: keys) {
                if (key.startsWith(prefix)) {
                    Object value = client.get(key);
                    results.put(key, value);
                }
            }
        }
        returnToPool(client);
        return results;
    }

    @Override
    public boolean ping() {
        boolean pingResult = true;
        MemcachedClient client = null;
        try {
            client = getFromPool();
            if (client == null) {
                logger.error("Failed to obtain memcached client from pool.");
                pingResult = false;
            }
        } catch (Exception e) {
            logger.error("Failed to ping memcached store. Reason: " + e.getMessage());
            pingResult = false;
        }
        if (pingResult) {
            try {
                client.get("test");
                returnToPool(client);
            } catch (Throwable e) {
                client.shutdown();
                logger.warn("Failed to ping memcached store");
                logger.error(e.getMessage());
                pingResult = false;
            }
        }
        return pingResult;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public void invalidate(String sessionToken, Long userId) {
        //
    }

    public void close() {
        try {
            logger.info("Closing memcached client pool");
            this.clientPool.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public int getCachedElementExpirationTime() {
        return CACHE_EXPIRATION_DEFAULT_TIME;
    }

    public void prepareClientPool(MemcachedClientFactory factory) {
        this.clientPool = new MemcachedClientPool(factory);
    }

    private MemcachedClient getFromPool() {
        try {
            return this.clientPool.borrowObject();
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw new IllegalStateException("Could not obtain a memcached client from the pool.Reason: " + e.getMessage(), e);
        }
    }

    private void returnToPool(MemcachedClient client) {
        try {
            this.clientPool.returnObject(client);
        } catch (Exception e) {
            logger.warn("Shutdown memcached client because of exception appeared while returning it to pool.");
            client.shutdown();
            logger.info(e.getMessage(), e);
        }
    }

    private void clearPool() {
        logger.info("Clearing Memcached Client pool...");
        this.clientPool.clear();
    }

}
