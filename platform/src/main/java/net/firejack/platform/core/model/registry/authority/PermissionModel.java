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
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;


@Entity
@Lookup(parent = RegistryNodeModel.class, suffix = "#permission")
@Table(name = "opf_permission")
public class PermissionModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = -1288649018843541113L;
    private List<RoleModel> roles;
    private List<ActionModel> actions;
    private List<NavigationElementModel> navigationElements;
    private List<ResourceLocationModel> resourceLocations;

    /**
     * @return
     */
    @ManyToMany(targetEntity = RoleModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_role_permission",
            joinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_role", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_PERMISSION_ROLES")
    public List<RoleModel> getRoles() {
        return roles;
    }

    /**
     * @param roles
     */
    public void setRoles(List<RoleModel> roles) {
        this.roles = roles;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = ActionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_action_permission",
            joinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_action", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_PERMISSION_ACTIONS")
    public List<ActionModel> getActions() {
        return actions;
    }

    /**
     * @param actions
     */
    public void setActions(List<ActionModel> actions) {
        this.actions = actions;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = NavigationElementModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_navigation_element_permission",
            joinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_navigation_element", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_PERMISSION_NAV_ELEMENTS")
    public List<NavigationElementModel> getNavigationElements() {
        return navigationElements;
    }

    /**
     * @param navigationElements
     */
    public void setNavigationElements(List<NavigationElementModel> navigationElements) {
        this.navigationElements = navigationElements;
    }

    /**
     * @return
     */
    @ManyToMany(targetEntity = ResourceLocationModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_resource_location_permission",
            joinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_resource_location", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_PERMISSION_RESOURCE_LOCATIONS")
    public List<ResourceLocationModel> getResourceLocations() {
        return resourceLocations;
    }

    /**
     * @param resourceLocations
     */
    public void setResourceLocations(List<ResourceLocationModel> resourceLocations) {
        this.resourceLocations = resourceLocations;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.PERMISSION;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = super.equals(o);
        if (!equal && o instanceof PermissionModel) {
            PermissionModel p = (PermissionModel) o;
            if (StringUtils.isNotBlank(this.getLookup()) &&
                    StringUtils.isNotBlank(p.getLookup()) &&
                    this.getLookup().equals(p.getLookup())) {
                equal = true;
            }
        }
        return equal;
    }

}
