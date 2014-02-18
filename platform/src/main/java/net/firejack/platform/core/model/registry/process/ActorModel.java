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
package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.IAllowDrag;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;

/**
 * Class represents actor entity
 */
@Entity
@DiscriminatorValue("ACR")
public class ActorModel extends RegistryNodeModel implements IAllowDrag, IAllowCreateAutoDescription {

    private static final long serialVersionUID = -5213959415858113339L;

    private String distributionEmail;
    private List<ActivityModel> activities;
    private List<UserActorModel> userActors;
    private List<RoleModel> roles;
    private List<GroupModel> groups;

    /**
     * Gets the type
     *
     * @return registry node type
     */
    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ACTOR;
    }

    /**
     * @return
     */
    @Column(length = 255)
    public String getDistributionEmail() {
        return distributionEmail;
    }

    /**
     * @param distributionEmail
     */
    public void setDistributionEmail(String distributionEmail) {
        this.distributionEmail = distributionEmail;
    }

    /**
     * Gets the activities
     *
     * @return list of the activities for the actor
     */
    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    public List<ActivityModel> getActivities() {
        return activities;
    }

    /**
     * Sets the activities
     *
     * @param activities - list of the activities for the actor
     */
    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
    }

    /**
     * Gets the user actors
     *
     * @return - list of the user actor entities
     */
    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY)
    public List<UserActorModel> getUserActors() {
        return userActors;
    }

    /**
     * Sets the user actors
     *
     * @param userActors - list of the user actor entities
     */
    public void setUserActors(List<UserActorModel> userActors) {
        this.userActors = userActors;
    }

    /**
     * Gets the roles
     *
     * @return - list of the roles for the actor
     */
    @ManyToMany(targetEntity = RoleModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_actor_role",
            joinColumns = @JoinColumn(name = "id_actor", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ACTOR_ROLES")
    public List<RoleModel> getRoles() {
        return roles;
    }

    /**
     * Sets the roles
     *
     * @param roles - list of the roles for the actor
     */
    public void setRoles(List<RoleModel> roles) {
        this.roles = roles;
    }

    /**
     * Gets the groups
     *
     * @return - list of the groups for the actor
     */
    @ManyToMany(targetEntity = GroupModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_actor_group",
            joinColumns = @JoinColumn(name = "id_actor", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ACTOR_GROUP")
    public List<GroupModel> getGroups() {
        return groups;
    }

    /**
     * Sets the groups
     *
     * @param groups - list of the groups for the actor
     */
    public void setGroups(List<GroupModel> groups) {
        this.groups = groups;
    }

}
