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
import net.firejack.platform.core.model.registry.domain.PackageModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "opf_permission_assignment")
public class PermissionAssignment extends BaseEntityModel {

    private RoleModel role;
    private PermissionModel permission;
    private PackageModel sourcePackage;

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
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_permission")
    public PermissionModel getPermission() {
        return permission;
    }

    /**
     * @param permission
     */
    public void setPermission(PermissionModel permission) {
        this.permission = permission;
    }

    /**
     * @return
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_source_package")
    public PackageModel getSourcePackage() {
        return sourcePackage;
    }

    /**
     * @param sourcePackage
     */
    public void setSourcePackage(PackageModel sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

}