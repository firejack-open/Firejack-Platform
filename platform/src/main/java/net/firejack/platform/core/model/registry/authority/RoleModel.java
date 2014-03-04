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
