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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ContextPermissions implements Serializable {

    private static final long serialVersionUID = 7746931283496300636L;
    private Long roleId;
    private String entityType;
    private String entityId;
    private String[] permissions;

    /**
     * @param roleId
     * @param entityType
     * @param entityId
     */
    public ContextPermissions(Long roleId, String entityType, String entityId) {
        this.roleId = roleId;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * @return
     */
    public String[] getPermissions() {
        return permissions;
    }

    /**
     * @param permissions
     */
    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    /**
     * @param permissions
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions == null ? null : permissions.toArray(new String[permissions.size()]);
    }

    /**
     * @return
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @return
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @return
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @return
     */
    public List<UserPermission> userPermissionsSnapshot() {
        List<UserPermission> userPermissions = new LinkedList<UserPermission>();
        if (permissions != null) {
            for (String permission : permissions) {
                UserPermission userPermission = new UserPermission(permission);
                userPermission.setEntityId(entityId);
                userPermission.setEntityType(entityType);
                userPermissions.add(userPermission);
            }
        }
        return userPermissions;
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return roleId == null || permissions == null;
    }

}