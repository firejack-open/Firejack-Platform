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

import net.firejack.platform.core.cache.annotations.key.LookupCacheKey;
import net.firejack.platform.core.cache.annotations.key.ResourceVersionCacheKey;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.sf.ehcache.Ehcache;
import org.springframework.stereotype.Component;



@Component
public class ResourceVersionCacheService<R extends AbstractResourceModel, RV extends AbstractResourceVersionModel> extends CacheService<RV> implements IResourceVersionCacheService<R, RV> {

    @Override
    protected String getCacheName() {
        return "resourceBrokerCache";
    }

    @Override
    public void removeCacheByResourceVersion(RV resourceVersion) {
        AbstractResourceModel resource = resourceVersion.getResource();
        
        ResourceVersionCacheKey resourceVersionCacheKey = new ResourceVersionCacheKey(resource.getId(), resource.getType());
        resourceVersionCacheKey.setLanguage(resourceVersion.getCulture().name());
        resourceVersionCacheKey.setVersion(resourceVersion.getVersion());
        getEhcache().remove(resourceVersionCacheKey);

        LookupCacheKey lookupCacheKey = new LookupCacheKey(resource.getLookup());
        getEhcache().remove(lookupCacheKey);
    }

    @Override
    public void removeCacheByResource(R resource) {
        Ehcache ehcache = getEhcache();
        for (Object obj : ehcache.getKeys()) {
            if (obj instanceof ResourceVersionCacheKey) {
                ResourceVersionCacheKey key  = (ResourceVersionCacheKey) obj;
                if (key.getId().equals(resource.getId()) && key.getType().equals(resource.getType())) {
                    ehcache.remove(key);
                }
            } else if (obj instanceof LookupCacheKey) {
                LookupCacheKey key = (LookupCacheKey) obj;
                if (key.getLookup().equals(resource.getLookup())) {
                    ehcache.remove(key);
                }
            }
        }
    }

}
