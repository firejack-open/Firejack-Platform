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

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNodePath;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.model.context.OPFContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SpecifiedIdsFilterContainer implements ISpecifiedIdsFilterContainer {

    private ISecuredRecordDataLoader securedRecordDataLoader;

    public ISecuredRecordDataLoader getSecuredRecordDataLoader() {
        if (securedRecordDataLoader == null) {
            securedRecordDataLoader = new CachedSecuredRecordDataLoader();
        }
        return securedRecordDataLoader;
    }

    @Override
    public SpecifiedIdsFilter getSpecifiedIdsFilterByType(String entityType) {
        SpecifiedIdsFilter<Long> idFilter;
        OPFContext context = OPFContext.getContext();
        if (context.getPrincipal().isGuestPrincipal()) {
            idFilter = null;
        } else {
            Long userId = context.getPrincipal().getUserInfoProvider().getId();
            Long securedRecordId = context.getSecuredRecordId();
            SpecifiedIdsFilter<Long> filter = getIdFilter(userId, entityType);
            if (securedRecordId == null) {
                idFilter = filter;
            } else {
                //check if any of parents contain appropriate read context permission
                //then treat that context permission as global permission for particular secured record id filter
                Map<Long, SecuredRecordNode> srInfoMap = getSecuredRecordDataLoader().loadSecuredRecords();
                SecuredRecordNode srInfo = srInfoMap.get(securedRecordId);
                if (srInfo != null) {
                    boolean hasGlobalPermissions = false;
                    SecuredRecordNodePath[] srPaths = srInfo.getNodePaths();
                    if (srPaths != null) {
                        for (SecuredRecordNodePath srPath : srPaths) {
                            for (Long srId : srPath.getPathEntries()) {
                                Map<Long, List<UserPermission>> srContextPermissions =
                                        getSecuredRecordDataLoader().loadSecuredRecordContextPermissions(userId);
                                if (srContextPermissions != null) {
                                    List<UserPermission> srPermissionList = srContextPermissions.get(srId);
                                    if (srPermissionList != null) {
                                        for (UserPermission userPermission : srPermissionList) {
                                            if (ActionDetectorFactory.isReadPermission(userPermission)) {//check read permission for specified entity type
                                                String readPermissionEntityType = ActionDetectorFactory.getReadPermissionType(userPermission);
                                                if (entityType.equals(readPermissionEntityType)) {//check if parent secured record provides read permission for particular entity type
                                                    hasGlobalPermissions = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (hasGlobalPermissions) {
                                    break;
                                }
                            }
                            if (hasGlobalPermissions) {
                                break;
                            }
                        }
                    }
                    if (hasGlobalPermissions) {
                        idFilter = new SpecifiedIdsFilter<Long>();
                        idFilter.setAll(Boolean.TRUE);
                        if (filter != null) {
                            idFilter.setUnnecessaryIds(filter.getUnnecessaryIds());
                        }
                    } else {
                        idFilter = filter;
                    }
                } else {
                    idFilter = filter;
                }
            }
        }
        return idFilter;
    }

    private SpecifiedIdsFilter<Long> getIdFilter(Long userId, String entityType) {
        Map<String, IdFilter> idFilterMap = getSecuredRecordDataLoader().loadIdFiltersForUser(userId);
        //todo: add calculation of id global access filters according inherited read-all permissions
        SpecifiedIdsFilter<Long> result;
        if (idFilterMap == null) {
            result = null;
        } else {
            IdFilter filterInfo = idFilterMap.get(entityType);
            if (filterInfo == null) {
                result = null;
            } else {
                result = new SpecifiedIdsFilter<Long>();
                if (filterInfo.isGlobalPermissionGranted()) {
                    result.setAll(Boolean.TRUE);
                }

                List<Long> grantedIdList = new ArrayList<Long>();
                if (filterInfo.getGrantedIdList() != null) {
                    Collections.addAll(grantedIdList, filterInfo.getGrantedIdList());
                }
                result.setNecessaryIds(grantedIdList);

                List<Long> deniedIdList = new ArrayList<Long>();
                if (filterInfo.getDeniedIdList() != null) {
                    Collections.addAll(deniedIdList, filterInfo.getDeniedIdList());
                }
                result.setUnnecessaryIds(deniedIdList);
            }

        }
        return result;
    }
}