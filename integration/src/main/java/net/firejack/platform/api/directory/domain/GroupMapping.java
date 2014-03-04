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