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