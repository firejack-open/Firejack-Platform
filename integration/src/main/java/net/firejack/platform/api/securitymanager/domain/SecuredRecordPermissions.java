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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.api.authority.domain.ContextPermissions;
import net.firejack.platform.api.authority.domain.UserContextPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.web.security.action.ActionDetectorFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SecuredRecordPermissions implements Serializable {

    private static final long serialVersionUID = -68229351298953524L;
    private Long securedRecordId;
    private Map<Long, UserContextPermissions> userContextPermissions;

    /**
     * @param securedRecordId
     * @param userContextPermissions
     */
    public SecuredRecordPermissions(Long securedRecordId, Map<Long, UserContextPermissions> userContextPermissions) {
        this.securedRecordId = securedRecordId;
        this.userContextPermissions = userContextPermissions;
    }

    /**
     * @return
     */
    public Long getSecuredRecordId() {
        return securedRecordId;
    }

    /**
     * @param securedRecordId
     */
    public void setSecuredRecordId(Long securedRecordId) {
        this.securedRecordId = securedRecordId;
    }

    /**
     * @return
     */
    public Map<Long, UserContextPermissions> getUserContextPermissions() {
        return userContextPermissions;
    }

    /**
     * @param userContextPermissions
     */
    public void setUserContextPermissions(Map<Long, UserContextPermissions> userContextPermissions) {
        this.userContextPermissions = userContextPermissions;
    }

    /**
     * @param userId
     * @return
     */
    public List<UserPermission> calculateContextPermissionsForUser(Long userId) {
        List<UserPermission> result;
        if (userId == null) {
            result = null;
        } else {
            UserContextPermissions userContextPermissions = this.userContextPermissions.get(userId);
            if (userContextPermissions == null) {
                result = null;
            } else {
                Map<Long, ContextPermissions> contextRolePermissions = userContextPermissions.getContextPermissions();
                result = new LinkedList<UserPermission>();
                for (ContextPermissions contextPermissions : contextRolePermissions.values()) {
                    result.addAll(contextPermissions.userPermissionsSnapshot());
                }
            }
        }
        return result;
    }

    /**
     * @param userId
     * @return
     */
    public List<UserPermission> getReadPermissions(Long userId) {
        List<UserPermission> result;
        if (userId == null) {
            result = null;
        } else {
            UserContextPermissions userContextPermissions = this.userContextPermissions.get(userId);
            if (userContextPermissions == null) {
                result = null;
            } else {
                Map<Long, ContextPermissions> contextRolePermissions = userContextPermissions.getContextPermissions();
                result = new LinkedList<UserPermission>();
                for (ContextPermissions contextPermissions : contextRolePermissions.values()) {
                    for (UserPermission permission : contextPermissions.userPermissionsSnapshot()) {
                        if (ActionDetectorFactory.isReadPermission(permission)) {
                            result.add(permission);
                        }
                    }
                }
            }
        }
        return result;
    }
}