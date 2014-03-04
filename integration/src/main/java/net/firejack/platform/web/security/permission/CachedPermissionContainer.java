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

package net.firejack.platform.web.security.permission;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.MappedPermissions;
import net.firejack.platform.api.authority.domain.UserContextPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNodePath;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;
import net.firejack.platform.web.security.sr.ISecuredRecordDataLoader;
import org.apache.log4j.Logger;

import java.security.Principal;
import java.util.*;

public class CachedPermissionContainer implements IPermissionContainer {

    protected List<IPermissionContainerRule> rules;

    private ISecuredRecordDataLoader securedRecordDataLoader;
    private static final Logger logger = Logger.getLogger(CachedPermissionContainer.class);

    /**
     * Set permission container rules
     * @param rules permission container rules
     */
    public void setRules(List<IPermissionContainerRule> rules) {
        this.rules = rules;
    }

    public ISecuredRecordDataLoader getSecuredRecordDataLoader() {
        if (securedRecordDataLoader == null) {
            securedRecordDataLoader = new CachedSecuredRecordDataLoader();
        }
        return securedRecordDataLoader;
    }

    @Override
    public List<UserPermission> loadGrantedActions(Principal p) {
        if (rules != null) {
            for (IPermissionContainerRule rule : rules) {
                List<UserPermission> permissions = rule.loadGrantedActions(p);
                if (permissions != null) {
                    return permissions;
                }
            }
        }
        CacheManager cacheManager = CacheManager.getInstance();
        OPFContext context = OPFContext.getContext();
        String sessionToken = context.getSessionToken();
        Map<Long, List<UserPermission>> permissionsByRole = null;
        if (StringUtils.isNotBlank(sessionToken)) {
            permissionsByRole = cacheManager.getPermissions(sessionToken);
            if (permissionsByRole == null) {
                if (cacheManager.isLocal()) {
                    permissionsByRole = loadPermissionsByRolesForLocalUse();
                    if (permissionsByRole != null) {
                        cacheManager.setPermissions(sessionToken, permissionsByRole);
                    }
                }
                if (permissionsByRole == null) {
                    logger.warn("Trying to load granted permissions using invalid token.");
                }
            }
        }
        if (permissionsByRole == null) {
            permissionsByRole = cacheManager.getGuestPermissions();
            if (permissionsByRole == null) {
                permissionsByRole = loadGuestPermissionsByRolesForLocalUse();
                if (permissionsByRole == null) {
                    logger.error("Failed to load permissions. Returned guest permissions are null. Using empty permissions map...");
                    permissionsByRole = new HashMap<Long, List<UserPermission>>();
                } else {
                    cacheManager.setGuestPermissions(permissionsByRole);
                }
            }
        }
        List<UserPermission> permissions = new LinkedList<UserPermission>();
        for (List<UserPermission> rolePermissions : permissionsByRole.values()) {
            if (rolePermissions != null && !rolePermissions.isEmpty()) {
                permissions.addAll(rolePermissions);
            }
        }
        return permissions;
    }

    @Override
    public List<UserPermission> loadDeniedActions(Principal p) {
        return Collections.emptyList();
    }

    @Override
    public List<UserPermission> loadUserPermissionsBySecuredRecords(Principal p, Long securedRecordId) {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            return Collections.emptyList();
        }
        OpenFlamePrincipal principal = (OpenFlamePrincipal) p;
        Long userId = principal.getUserInfoProvider().getId();
        ////////////////////////
        Map<Long, List<UserPermission>> srContextPermissions = loadSecuredRecordContextPermissions(userId);
        Map<Long, SecuredRecordNode> srMap = loadSecuredRecords();
        ////////////////////////
        List<UserPermission> result = new LinkedList<UserPermission>();
        if (srContextPermissions != null && srMap != null) {
            List<UserPermission> srPermissions = srContextPermissions.get(securedRecordId);
            if (srPermissions != null) {
                result.addAll(srPermissions);
            }
            //collect permissions inherited from secured record paths.
            SecuredRecordNode srInfo = srMap.get(securedRecordId);
            if (srInfo != null && srInfo.getNodePaths() != null) {
                for (SecuredRecordNodePath srPath : srInfo.getNodePaths()) {
                    if (srPath.getPathEntries() != null) {
                        for (Long srId : srPath.getPathEntries()) {
                            List<UserPermission> userPermissions = srContextPermissions.get(srId);
                            if (userPermissions != null) {
                                for (UserPermission permission : userPermissions) {
                                    //context permissions of parent secured records become inherited global permissions
                                    result.add(new UserPermission(permission.getPermission()));
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<UserPermission> loadPackageLevelUserPermissions(Principal p, String packageLookup) {
        if (!OpenFlameSecurityConstants.isClientContext() && !ConfigContainer.isAppInstalled()) {
            return Collections.emptyList();
        }
        OpenFlamePrincipal principal = (OpenFlamePrincipal) p;
        Long userId = principal.getUserInfoProvider().getId();
        UserContextPermissions packageLevelUserPermissions =
                CacheManager.getInstance().getPackageLevelUserPermissions(packageLookup, userId);
        //as soon as package level permissions should only work for OpenFlame Console
        //(they restrict access to elements underneath the package not in gateway itself)
        // - we do not consider Gateway application case, cacheManager.isLocal(), API usage etc
        Set<UserPermission> permissions = new HashSet<UserPermission>();
        if (packageLevelUserPermissions != null) {
            Map<Long, List<UserPermission>> rolePermissions = packageLevelUserPermissions.getRolePermissions();
            if (rolePermissions != null) {
                for (List<UserPermission> userPermissions : rolePermissions.values()) {
                    permissions.addAll(userPermissions);
                }
            }
        }
        return new LinkedList<UserPermission>(permissions);
    }

    @Override
    public Map<Long, List<UserPermission>> loadSecuredRecordContextPermissions(Long userId) {
        return getSecuredRecordDataLoader().loadSecuredRecordContextPermissions(userId);
    }

    protected Map<Long, SecuredRecordNode> loadSecuredRecords() {
        return getSecuredRecordDataLoader().loadSecuredRecords();
    }

    protected Map<Long, List<UserPermission>> loadPermissionsByRolesForLocalUse() {
        ServiceResponse<MappedPermissions> response = OPFEngine.AuthorityService.readPermissionsByRolesMap();
        return processPermissionsByRolesResponse(response);
    }

    protected Map<Long, List<UserPermission>> loadGuestPermissionsByRolesForLocalUse() {
        ServiceResponse<MappedPermissions> response = OPFEngine.AuthorityService.readPermissionsByRolesMapForGuest();
        return processPermissionsByRolesResponse(response);
    }

    private Map<Long, List<UserPermission>> processPermissionsByRolesResponse(
            ServiceResponse<MappedPermissions> response) {
        Map<Long, List<UserPermission>> result;
        if (response.isSuccess()) {
            List<MappedPermissions> responseData = response.getData();
            result = new HashMap<Long, List<UserPermission>>();
            if (responseData != null) {
                for (MappedPermissions mappedPermissions : responseData) {
                    result.put(mappedPermissions.getMappedId(), mappedPermissions.getPermissions());
                }
            }
        } else {
            logger.error("Response has failure status: " + response.getMessage());
            result = null;
        }
        return result;
    }

}