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

package net.firejack.platform.core.model.registry.directory;

import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@Entity
@Table(name = "opf_group_mapping")
public class GroupMappingModel extends BaseEntityModel {

    private DirectoryModel directory;
    private GroupModel group;
    private String ldapGroupDN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_directory")
    @ForeignKey(name = "FK_GROUP_MAPPING_DIRECTORY")
    public DirectoryModel getDirectory() {
        return directory;
    }

    public void setDirectory(DirectoryModel directory) {
        this.directory = directory;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_group")
    @ForeignKey(name = "FK_GROUP_MAPPING_GROUP")
    public GroupModel getGroup() {
        return group;
    }

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    @Column(name="ldap_group_dn", length = 255)
    public String getLdapGroupDN() {
        return ldapGroupDN;
    }

    public void setLdapGroupDN(String ldapGroupDN) {
        this.ldapGroupDN = ldapGroupDN;
    }

}