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

import java.util.Map;

public interface ICacheStore {

    public static final String CACHE_SEARCH_KEY = "CACHE_SEARCH_KEY";
    
    /**
     * @return
     */
    boolean isLocal();

    Map<String, Object> findByPrefix(String prefix);
    
    /**
     * @param key
     * @return
     */
    Object loadFromCache(String key);

    /**
     * @param key
     * @param value
     */
    void saveToCache(String key, Object value);

    /**
     * @param key
     */
    void removeFromCache(String key);

    /**
     * @return
     */
    boolean ping();

    /**
     * @param sessionToken
     * @param userId
     */
    void invalidate(String sessionToken, Long userId);

    /***/
    void close();

    /**
     * @return
     */
    public int getCachedElementExpirationTime();

}                               