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