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

package net.firejack.platform.core.model.registry.authority;

import net.firejack.platform.core.model.user.UserModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class UserRolePk implements Serializable {
    private static final long serialVersionUID = -3645294133021093151L;

    private Long userId;
    private Long roleId;

    /***/
    public UserRolePk() {
    }

    /**
     * @param user
     * @param role
     */
    public UserRolePk(UserModel user, RoleModel role) {
        this.userId = user.getId();
        this.roleId = role.getId();
    }

    /**
     * @return
     */
    @Column(name = "id_user")
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return
     */
    @Column(name = "id_role")
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRolePk that = (UserRolePk) o;

        return roleId.equals(that.roleId) && userId.equals(that.userId);
    }

    public int hashCode() {
        int result;
        result = userId.hashCode();
        result = 31 * result + roleId.hashCode();
        return result;
    }

}
