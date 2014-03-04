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
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;


public class LdapGroupContextMapper implements ContextMapper {

    @Override
    public Object mapFromContext(Object ctx) {
        DirContextOperations dirContext = (DirContextOperations) ctx;
        Group group = new Group();
        group.setName(dirContext.getStringAttribute("cn"));
        /*String distinguishedName = dirContext.getStringAttribute("distinguishedName");
        if (StringUtils.isBlank(distinguishedName)) {
            distinguishedName = dirContext.getDn().toString();
        }
        group.setDistinguishedName(distinguishedName);*/
        group.setDistinguishedName(dirContext.getNameInNamespace());
        /*String[] membersArray = dirContext.getStringAttributes("uniqueMember");
        if (membersArray != null) {
            List list = Arrays.asList(membersArray);
            group.setMembers(new TreeSet(list));
        }*/
        return group;
    }

}