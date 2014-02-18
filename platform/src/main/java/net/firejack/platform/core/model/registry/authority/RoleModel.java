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

import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.process.ActorModel;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_role")
public class RoleModel extends LookupModel<RegistryNodeModel> {
    private static final long serialVersionUID = 7206458919294880160L;

    private List<PermissionModel> permissions;

    private List<UserRoleModel> userRoles = new LinkedList<UserRoleModel>();

    private List<ActorModel> actors;

    /**
     * @return
     */
    @ManyToMany(targetEntity = PermissionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_role_permission",
            joinColumns = @JoinColumn(name = "id_role", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_ROLE_PERMISSIONS")
    public List<PermissionModel> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions
     */
    public void setPermissions(List<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @Cascade(value = {org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @ForeignKey(name = "FK_ROLE_TO_USER_ROLES")
    public List<UserRoleModel> getUserRoles() {
        return this.userRoles;
    }

    /**
     * @param userRoles
     */
    public void setUserRoles(List<UserRoleModel> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ROLE;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = ActorModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_actor_role",
            joinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_actor", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ROLE_ACTORS")
    public List<ActorModel> getActors() {
        return actors;
    }

    /**
     * @param actors
     */
    public void setActors(List<ActorModel> actors) {
        this.actors = actors;
    }

    /**
     * @return
     */
    @Transient
    public boolean isGlobal() {
        String[] lookupParts = getPath().split("\\.");
        return lookupParts.length == 3;
    }

}
