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