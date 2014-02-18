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
import static net.firejack.platform.core.validation.annotation.DomainType.DOMAIN;

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
