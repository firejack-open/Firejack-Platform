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
