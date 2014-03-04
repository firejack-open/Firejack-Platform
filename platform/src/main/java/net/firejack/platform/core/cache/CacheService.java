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
