/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.directory.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@Component
@XmlRootElement
@RuleSource("OPF.directory.GroupMapping")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupMapping extends BaseEntity {

    @Property
    private Directory directory;
    @Property
    private Group group;
    @Property
    private String ldapGroupDN;
    private List<Group> mappedGroups;//used for LDAP Manager
    private List<Group> availableGroups;//used for LDAP Manager

    @NotNull
    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    @NotNull
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @NotBlank
    public String getLdapGroupDN() {
        return ldapGroupDN;
    }

    public void setLdapGroupDN(String ldapGroupDN) {
        this.ldapGroupDN = ldapGroupDN;
    }

    public List<Group> getMappedGroups() {
        return mappedGroups;
    }

    public void setMappedGroups(List<Group> mappedGroups) {
        this.mappedGroups = mappedGroups;
    }

    public List<Group> getAvailableGroups() {
        return availableGroups;
    }

    public void setAvailableGroups(List<Group> availableGroups) {
        this.availableGroups = availableGroups;
    }

}