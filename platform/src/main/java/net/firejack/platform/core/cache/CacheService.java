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

package net.firejack.platform.core.cache;

import net.firejack.platform.core.cache.annotations.key.CacheKey;
import net.firejack.platform.core.model.BaseEntityModel;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.stereotype.Component;



@Component
public abstract class CacheService<O extends BaseEntityModel> implements ICacheService<O> {

    @Autowired
    @Qualifier("ehCacheManager")
    private EhCacheManagerFactoryBean ehCacheManager;

    protected abstract String getCacheName();

    protected Ehcache getEhcache() {
        return ehCacheManager.getObject().getEhcache(getCacheName());
    }

    @Override
    public Object get(CacheKey key) {
        Element element = getEhcache().get(key);
        return element != null ? element.getObjectValue() : null;
    }

    @Override
    public void put(CacheKey key, Object value) {
        getEhcache().put(new Element(key, value));
    }

    @Override
    public void remove(CacheKey key) {
        getEhcache().remove(key);
    }

}
