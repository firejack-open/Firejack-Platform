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