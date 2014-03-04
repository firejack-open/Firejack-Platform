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

package net.firejack.platform.api.authority.domain;

import net.firejack.platform.core.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContextPermissions implements Serializable {

    private Long userId;
    private Map<Long, List<UserPermission>> rolePermissions;
    private Map<Long, ContextPermissions> contextPermissions;

    /**
     * @param userId
     */
    public UserContextPermissions(Long userId) {
        this.userId = userId;
        this.contextPermissions = new HashMap<Long, ContextPermissions>();
    }

    public UserContextPermissions() {
    }

    /**
     * @return
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return
     */
    public Map<Long, List<UserPermission>> getRolePermissions() {
        return rolePermissions;
    }

    /**
     * @param rolePermissions
     */
    public void setRolePermissions(Map<Long, List<UserPermission>> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    /**
     * @return
     */
    public Map<Long, ContextPermissions> getContextPermissions() {
        return contextPermissions;
    }

    /**
     * @param roleId
     * @return
     */
    public ContextPermissions getContextPermissions(Long roleId) {
        return contextPermissions.get(roleId);
    }

    /**
     * @return
     */
    public Map<Long, List<UserPermission>> populateRolePermissions() {
        Map<Long, List<UserPermission>> result = new HashMap<Long, List<UserPermission>>();
        for (ContextPermissions contextPermissions : this.contextPermissions.values()) {
            result.put(contextPermissions.getRoleId(), contextPermissions.userPermissionsSnapshot());
        }
        return result;
    }

    /**
     * @param contextRolePermissions
     */
    public void putContextRolePermissions(ContextPermissions contextRolePermissions) {
        if (contextRolePermissions != null && !contextRolePermissions.isEmpty()) {
            contextPermissions.put(contextRolePermissions.getRoleId(), contextRolePermissions);
        }
    }

    /**
     * @param roleId
     * @return
     */
    public ContextPermissions removeContextRole(Long roleId) {
        ContextPermissions removed;
        if (roleId == null) {
            removed = null;
        } else {
            removed = contextPermissions.remove(roleId);
        }
        return removed;
    }

    public ContextPermissions removeContextRole(ContextPermissions role) {
        ContextPermissions removed = null;
        if (role != null && role.getRoleId() != null && StringUtils.isNotBlank(role.getEntityType()) &&
                StringUtils.isNotBlank(role.getEntityId())) {
            removed = null;
            for (ContextPermissions cp : contextPermissions.values()) {
                if (role.getRoleId().equals(cp.getRoleId()) && role.getEntityType().equals(cp.getEntityType()) &&
                        role.getEntityId().equals(cp.getEntityId())) {
                    removed = contextPermissions.remove(role.getRoleId());//TODO refactor(temporary)
                    break;
                }
            }
        }
        return removed;
    }
}