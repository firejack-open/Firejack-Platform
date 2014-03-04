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

package net.firejack.platform.core.external.ldap;

import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import org.springframework.ldap.core.support.LdapContextSource;


public class ContextSourceContainer {

    private LdapContextSource baseContextSource;
    private LdapContextSource usersContextSource;
    private LdapContextSource groupsContextSource;

    public ContextSourceContainer(DirectoryModel directoryModel) {
        LdapSchemaConfig ldapSchemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
        if (ldapSchemaConfig == null) {
            throw new OpenFlameRuntimeException("ldapSchemaConfig property is not configured properly.");
        }
        this.baseContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), directoryModel.getBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
        this.usersContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), ldapSchemaConfig.getPeopleBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
        this.groupsContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), ldapSchemaConfig.getGroupsBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
    }

    public ContextSourceContainer(CheckUrl checkUrl) {
        this.baseContextSource = LdapUtils.populateContextSource(
                checkUrl.getServerName(), checkUrl.getPort(), checkUrl.getUrlPath(),
                checkUrl.getUsername(), checkUrl.getPassword());
    }

    public LdapContextSource getBaseContextSource() {
        return baseContextSource;
    }

    public LdapContextSource getUsersContextSource() {
        return usersContextSource;
    }

    public LdapContextSource getGroupsContextSource() {
        return groupsContextSource;
    }
}