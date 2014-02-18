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

package net.firejack.platform.web.security.sr;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.MappedPermissions;
import net.firejack.platform.api.authority.domain.TypeFilter;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.cache.CacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CachedSecuredRecordDataLoader implements ISecuredRecordDataLoader {

    @Override
    public Map<Long, SecuredRecordNode> loadSecuredRecords() {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<Long, SecuredRecordNode> srMap = cacheManager.getSecuredRecords();
        if (srMap == null && cacheManager.isLocal()) {
            srMap = loadSecuredRecordsForLocalUse();
            if (srMap != null) {
                cacheManager.setSecuredRecords(srMap);
            }
        }
        return srMap;
    }

    @Override
    public Map<Long, List<UserPermission>> loadSecuredRecordContextPermissions(Long userId) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<Long, List<UserPermission>> srContextPermissions =
                cacheManager.getSecuredRecordContextPermissions(userId);
        if (srContextPermissions == null && cacheManager.isLocal()) {
            srContextPermissions = loadSRContextPermissionsForLocalUse();
            if (srContextPermissions != null) {
                cacheManager.setSecuredRecordContextPermissions(userId, srContextPermissions);
            }
        }
        return srContextPermissions;
    }

    @Override
    public Map<String, IdFilter> loadIdFiltersForUser(Long userId) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, IdFilter> idFilterMap = cacheManager.getIdFiltersForUser(userId);
        if (idFilterMap == null && cacheManager.isLocal()) {
            idFilterMap = prepareIdFiltersForLocalCache(userId);
        }
        return idFilterMap;
    }

    protected Map<Long, SecuredRecordNode> loadSecuredRecordsForLocalUse() {
        ServiceResponse<SecuredRecordNode> response =
                OPFEngine.SecurityManagerService.loadAllSecureRecordNodes();
        Map<Long, SecuredRecordNode> result = new HashMap<Long, SecuredRecordNode>();
        if (response != null && response.getData() != null) {
            for (SecuredRecordNode srInfo : response.getData()) {
                result.put(srInfo.getSecuredRecordId(), srInfo);
            }
        }
        return result;
    }

    protected Map<String, IdFilter> prepareIdFiltersForLocalCache(Long userId) {
        ServiceResponse<TypeFilter> response = OPFEngine.AuthorityService.loadIdFiltersByUser(userId);
        Map<String, IdFilter> result;
        if (response != null && response.getData() != null) {
            result = new HashMap<String, IdFilter>();
            for (TypeFilter typedFilter : response.getData()) {
                result.put(typedFilter.getType(), typedFilter.getIdFilter());
            }
            CacheManager.getInstance().setIdFiltersForUser(userId, result);
        } else {
            result = null;
        }
        return result;
    }

    protected Map<Long, List<UserPermission>> loadSRContextPermissionsForLocalUse() {
        ServiceResponse<MappedPermissions> response =
                OPFEngine.AuthorityService.readAllSecuredRecordContextualPermissions();
        Map<Long, List<UserPermission>> result = new HashMap<Long, List<UserPermission>>();
        if (response != null && response.getData() != null) {
            for (MappedPermissions mappedPermissions : response.getData()) {
                result.put(mappedPermissions.getMappedId(), mappedPermissions.getPermissions());
            }
        }
        return result;
    }

}