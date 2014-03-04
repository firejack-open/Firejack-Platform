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