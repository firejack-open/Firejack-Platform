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

package net.firejack.platform.core.model.user;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "opf_base_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "UNIQUE_USER_USERNAME", columnNames = {"username"}),
                @UniqueConstraint(name = "UNIQUE_USER_EMAIL", columnNames = {"email"})
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class BaseUserModel extends BaseEntityModel {

    private static final long serialVersionUID = -7201966545912017826L;
    private String username;
    private String email;

    private List<UserRoleModel> userRoles = new LinkedList<UserRoleModel>();


    private RegistryNodeModel registryNode;

    public BaseUserModel() {
    }

    public BaseUserModel(Long id) {
        super(id);
    }

    @Column(length = 255)
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    @Column(length = 255)
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @Cascade(value = {org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @ForeignKey(name = "FK_USER_TO_USER_ROLES")
    public List<UserRoleModel> getUserRoles() {
        return this.userRoles;
    }

    /**
     * @param userRoles
     */
    public void setUserRoles(List<UserRoleModel> userRoles) {
        this.userRoles = userRoles;
    }


    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registry_node")
    @ForeignKey(name = "FK_REGISTRY_NODE_USER")
    public RegistryNodeModel getRegistryNode() {
        return registryNode;
    }

    /**
     * @param registryNode
     */
    public void setRegistryNode(RegistryNodeModel registryNode) {
        this.registryNode = registryNode;
    }

}
