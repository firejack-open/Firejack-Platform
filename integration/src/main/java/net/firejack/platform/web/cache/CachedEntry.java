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