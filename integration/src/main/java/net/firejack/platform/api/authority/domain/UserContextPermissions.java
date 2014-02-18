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