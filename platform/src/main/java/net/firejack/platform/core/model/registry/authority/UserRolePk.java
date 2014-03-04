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
