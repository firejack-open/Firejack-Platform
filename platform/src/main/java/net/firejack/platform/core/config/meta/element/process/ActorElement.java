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
