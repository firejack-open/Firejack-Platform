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

package net.firejack.platform.web.security.session;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.user.IUserInfoProvider;

import java.util.List;

/**
 *
 */
public class UserSession extends AbstractDTO {
    private static final long serialVersionUID = -9092154535114978622L;

    private String token;
    private IUserInfoProvider user;
    private long expirationTime;
    private List<Long> roleIds;

    /**
     * @param token
     * @param user
     * @param roleIds
     * @param expirationTime
     */
    public UserSession(String token, IUserInfoProvider user, List<Long> roleIds, long expirationTime) {
        this.token = token;
        this.user = user;
        this.roleIds = roleIds;
        this.expirationTime = expirationTime;
    }

    /**
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * @return
     */
    public IUserInfoProvider getUser() {
        return user;
    }

    /**
     * @return
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @param roleIds
     */
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * @return
     */
    public List<Long> getRoleIds() {
        return roleIds;
    }
}