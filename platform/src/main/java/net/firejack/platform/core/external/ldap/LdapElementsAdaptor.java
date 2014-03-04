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

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.User;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

import javax.naming.directory.Attributes;

public interface LdapElementsAdaptor {

    AttributesMapper provideUserAttributesMapper();

    Attributes buildUserAttributes(User user);

    DistinguishedName buildUserDn(User user);

    ContextMapper provideUserContextMapper();

    DistinguishedName buildGroupDn(String groupName);

    DirContextOperations buildGroupAttributes(DirContextOperations adapter, Group group);

    ContextMapper provideGroupContextMapper();

    LdapSchemaConfig getSchemaConfig();

}