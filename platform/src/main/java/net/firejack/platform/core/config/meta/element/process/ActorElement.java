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

package net.firejack.platform.core.config.meta.element.process;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.process.ActorModel;

import java.util.List;


public class ActorElement extends PackageDescriptorElement<ActorModel> {

    private String distributionEmail;
    private List<UserAssignElement> users;
    private List<RoleAssignElement> roles;
    private List<GroupAssignElement> groups;

    /**
     * @return
     */
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
     * @return
     */
    public List<UserAssignElement> getUsers() {
        return users;
    }

    /**
     * @param users
     */
    public void setUsers(List<UserAssignElement> users) {
        this.users = users;
    }

    /**
     * @return
     */
    public List<RoleAssignElement> getRoles() {
        return roles;
    }

    /**
     * @param roles
     */
    public void setRoles(List<RoleAssignElement> roles) {
        this.roles = roles;
    }

    /**
     * @return
     */
    public List<GroupAssignElement> getGroups() {
        return groups;
    }

    /**
     * @param groups
     */
    public void setGroups(List<GroupAssignElement> groups) {
        this.groups = groups;
    }

    @Override
    public Class<ActorModel> getEntityClass() {
        return ActorModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ActorElement that = (ActorElement) o;

        if (distributionEmail != null ? !distributionEmail.equals(that.distributionEmail) : that.distributionEmail != null)
            return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (users != null ? !users.equals(that.users) : that.users != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (distributionEmail != null ? distributionEmail.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }
}
