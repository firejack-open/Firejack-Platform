/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
