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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;


public class LdapUserContextMapper implements ContextMapper {

    private static final Logger logger = Logger.getLogger(LdapUserContextMapper.class);
    private LdapElementsAdaptor elementsAdaptor;

    public LdapUserContextMapper(LdapElementsAdaptor elementsAdaptor) {
        this.elementsAdaptor = elementsAdaptor;
    }

    @Override
    public Object mapFromContext(Object o) {
        DirContextOperations dirContext = (DirContextOperations) o;

        User user = new User();
        user.setUsername(dirContext.getStringAttribute(elementsAdaptor.getSchemaConfig().getUidAttributeName()));
        String firstNameAttribute = dirContext.getStringAttribute("givenName");
        if (StringUtils.isNotBlank(firstNameAttribute)) {
            user.setFirstName(firstNameAttribute);
        }
        String lastNameAttribute = dirContext.getStringAttribute("sn");
        if (StringUtils.isNotBlank(lastNameAttribute)) {
            user.setLastName(lastNameAttribute);
        }
        String mailAttribute = dirContext.getStringAttribute(
                elementsAdaptor.getSchemaConfig().getEmailAttributeName());
        if (StringUtils.isNotBlank(mailAttribute)) {
            user.setEmail(mailAttribute);
        }
        user.setDistinguishedName(dirContext.getNameInNamespace());

        return user;
    }

}