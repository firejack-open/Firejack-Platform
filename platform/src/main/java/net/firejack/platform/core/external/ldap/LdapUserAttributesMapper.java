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
import org.apache.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class LdapUserAttributesMapper implements AttributesMapper {

    private static final Logger logger = Logger.getLogger(LdapUserAttributesMapper.class);
    private LdapElementsAdaptor elementsAdaptor;

    public LdapUserAttributesMapper(LdapElementsAdaptor elementsAdaptor) {
        this.elementsAdaptor = elementsAdaptor;
    }

    public Object mapFromAttributes(Attributes attrs)
            throws NamingException {
        User user = new User();
        Attribute attribute = attrs.get(this.elementsAdaptor.getSchemaConfig().getUidAttributeName());
        if (attribute == null) {
            logger.warn("LDAP: No uid attributes were recognized.");
        } else {
            user.setUsername((String) attribute.get());
        }
        /*Attribute usernameAttribute = attrs.get("uid");
        if (usernameAttribute == null) {
            Attribute nameAttribute = attrs.get("sAMAccountName");
            if (nameAttribute == null) {
                logger.warn("LDAP: Neither uid, nor name attributes were recognized.");
            } else {
                user.setUsername((String) nameAttribute.get());
            }
        } else {
            user.setUsername((String) usernameAttribute.get());
        }*/
        Attribute firstNameAttribute = attrs.get("givenName");
        if (firstNameAttribute != null) {
            user.setFirstName((String) firstNameAttribute.get());
        }
        Attribute lastNameAttribute = attrs.get("sn");
        if (lastNameAttribute != null) {
            user.setLastName((String) lastNameAttribute.get());
        }
        Attribute emailAttribute = attrs.get(this.elementsAdaptor.getSchemaConfig().getEmailAttributeName());
        if (emailAttribute != null) {
            user.setEmail((String) emailAttribute.get());
        }
        return user;
    }

}