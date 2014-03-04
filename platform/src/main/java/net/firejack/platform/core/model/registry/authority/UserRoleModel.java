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

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.BaseUserModel;

import javax.persistence.*;


@Entity
@Table(name = "opf_user_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_user", "id_role", "internal_id"})})
public class UserRoleModel extends BaseEntityModel {

    private static final long serialVersionUID = 5952222598979191428L;
    //	private UserRolePk pk;
    private BaseUserModel user;
    private RoleModel role;

    private String type;
    private String externalId;
    private Long internalId;
    private SecuredRecordModel securedRecord;

    /***/
    public UserRoleModel() {
    }

    /**
     * @param user
     * @param role
     */
    public UserRoleModel(BaseUserModel user, RoleModel role) {
        this.user = user;
        this.role = role;
    }

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user")
    public BaseUserModel getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(BaseUserModel user) {
        this.user = user;
    }

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_role")
    public RoleModel getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(RoleModel role) {
        this.role = role;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "id_secured_record")
    public SecuredRecordModel getSecuredRecord() {
        return securedRecord;
    }

    /**
     * @param securedRecord
     */
    public void setSecuredRecord(SecuredRecordModel securedRecord) {
        this.securedRecord = securedRecord;
    }

    /**
     * @return
     */
    @Column(length = 2047)
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    @Column(name = "external_id")
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @return
     */
    @Column(name = "internal_id")
    public Long getInternalId() {
        return internalId;
    }

    /**
     * @param internalId
     */
    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

}
