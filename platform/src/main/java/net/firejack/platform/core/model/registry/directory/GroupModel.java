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

package net.firejack.platform.core.model.registry.directory;

import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.process.ActorModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;


@Entity
@DiscriminatorValue("GRP")
public class GroupModel extends RegistryNodeModel implements IAllowCreateAutoDescription {

    private static final long serialVersionUID = 7013150861920728169L;
    private DirectoryModel directory;

    private List<ActorModel> actors;
    private List<RoleModel> roles;

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_directory")
    @ForeignKey(name = "FK_DIRECTORY_GROUP")
    public DirectoryModel getDirectory() {
        return directory;
    }

    /**
     * @param directory
     */
    public void setDirectory(DirectoryModel directory) {
        this.directory = directory;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.GROUP;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = ActorModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_actor_group",
            joinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_actor", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_ROLE_PERMISSIONS")
    public List<ActorModel> getActors() {
        return actors;
    }

    /**
     * @param actors
     */
    public void setActors(List<ActorModel> actors) {
        this.actors = actors;
    }

    @ManyToMany(targetEntity = RoleModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_group_role",
            joinColumns = @JoinColumn(name = "id_group", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id")
    )
    @ForeignKey(name = "FK_GROUP_ROLES")
    public List<RoleModel> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleModel> roles) {
        this.roles = roles;
    }

}