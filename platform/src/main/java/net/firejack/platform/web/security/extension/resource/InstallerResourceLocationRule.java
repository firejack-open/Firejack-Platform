/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.web.security.extension.resource;

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.authority.WildcardStyle;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.web.security.resource.IResourceLocationContainerRule;

import java.util.ArrayList;
import java.util.List;


public class InstallerResourceLocationRule implements IResourceLocationContainerRule {

    private List<ResourceLocation> installerResourceLocations;

    @Override
    public boolean isRuleCase() {
        return !ConfigContainer.isAppInstalled();
    }

    @Override
    public List<ResourceLocation> getResourceLocations() {
        if (installerResourceLocations == null) {
            installerResourceLocations = new ArrayList<ResourceLocation>(3);
            installerResourceLocations.add(
                    prepareRegExpResourceLocation("Sign Out Entry-point",
                            "(?i)/console/logout$", OpenFlame.SIGN_OUT_ENTITY));
        }
        return installerResourceLocations;
    }

    private ResourceLocation prepareRegExpResourceLocation(String name, String urlPath, String permissionLookup) {
        ResourceLocation rl = new ResourceLocation();
        rl.setName(name);
        rl.setUrlPath(urlPath);
        rl.setWildcardStyle(WildcardStyle.REGEXP);
        rl.setPath(OpenFlame.PACKAGE);
        rl.setLookup(DiffUtils.lookup(rl.getPath(), rl.getName()));
        List<Permission> permissions = new ArrayList<Permission>();
        Permission permission = new Permission();
        permissions.add(permission);
        permission.setLookup(permissionLookup);
        rl.setPermissions(permissions);
        return rl;
    }

}