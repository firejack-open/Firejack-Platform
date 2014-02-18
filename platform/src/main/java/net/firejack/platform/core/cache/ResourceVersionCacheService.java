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
