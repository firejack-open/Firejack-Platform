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

package net.firejack.platform.api.process.domain;


import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.Match;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.Validate;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static net.firejack.platform.core.validation.annotation.DomainType.*;

@Component
@XmlRootElement
@RuleSource("OPF.process.Actor")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
@Validate(type = ACTOR, parents = {DOMAIN}, unique = {ACTOR, PROCESS, ENTITY, REPORT, DOMAIN})
public class Actor extends Lookup {
    private static final long serialVersionUID = 6233248154943558758L;

    @Property
    private String distributionEmail;
    @Property
    private List<Role> roles;
    @Property
    private List<Group> groups;
    @Property
    private List<UserActor> userActors;

    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^[\\w\\._%+-]+@[\\w\\.-]+\\.\\w{2,6}$", msgKey="validation.parameter.should.be.email")
    public String getDistributionEmail() {
        return distributionEmail;
    }

    public void setDistributionEmail(String distributionEmail) {
        this.distributionEmail = distributionEmail;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<UserActor> getUserActors() {
        return userActors;
    }

    public void setUserActors(List<UserActor> userActors) {
        this.userActors = userActors;
    }

}
