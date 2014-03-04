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

package net.firejack.platform.web.security.resource;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CachedResourceLocationContainer implements IResourceLocationContainer {

    private String packageLookup;
    protected List<IResourceLocationContainerRule> rules;
    private static final Logger logger = Logger.getLogger(CachedResourceLocationContainer.class);

    /**
     * @param packageLookup package lookup
     */
    public CachedResourceLocationContainer(String packageLookup) {
        this.packageLookup = packageLookup;
    }

    public void setRules(List<IResourceLocationContainerRule> rules) {
        this.rules = rules;
    }

    @Override
    public List<ResourceLocation> getAllowableResourceLocationList() {
        List<ResourceLocation> allowableMaskedResourceList = null;
        List<ResourceLocation> elementList = getResourceLocationList();
        if (elementList != null) {
            OPFContext context = OPFContext.getContext();
            OpenFlamePrincipal openFlamePrincipal = context.getPrincipal();
            allowableMaskedResourceList = new ArrayList<ResourceLocation>();
            for (ResourceLocation vo : elementList) {
                if (openFlamePrincipal.checkUserPermission(vo)) {
                    allowableMaskedResourceList.add(vo);
                }
            }
        }
        return allowableMaskedResourceList;
    }

    @Override
    public List<ResourceLocation> getResourceLocationList() {
        if (rules != null) {
            for (IResourceLocationContainerRule rule : rules) {
                if (rule.isRuleCase()) {
                    List<ResourceLocation> resourceLocations = rule.getResourceLocations();
                    return resourceLocations == null ? Collections.<ResourceLocation>emptyList() : resourceLocations;
                }
            }
        }
        CacheManager cacheManager = CacheManager.getInstance();
        List<ResourceLocation> resourceLocationList = cacheManager.getResourceLocations(packageLookup);
        if (resourceLocationList == null && cacheManager.isLocal()) {
            ServiceResponse<ResourceLocation> response =
                    OPFEngine.AuthorityService.loadCachedResourceLocations(packageLookup);
            if (response.isSuccess()) {
                resourceLocationList = response.getData();
                if (resourceLocationList != null) {
                    cacheManager.setResourceLocations(packageLookup, resourceLocationList);
                }
            } else {
                logger.error("API Response: " + response.getMessage());
            }
        }
        return resourceLocationList;
    }

}