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

import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.io.IOException;

public class LdapUtils {

    private static final String LDAP_SCHEMA_PREFIX = "ldap://";
    private static final Logger logger = Logger.getLogger(LdapUtils.class);

    public static Tuple<String, String> getLastEntryValueFromDN(String distinguishedName) {
        Tuple<String, String> lastEntry;
        if (StringUtils.isBlank(distinguishedName)) {
            lastEntry = null;
        } else {
            DistinguishedName dn = new DistinguishedName(distinguishedName);
            LdapRdn ldapRdn = (LdapRdn) dn.getNames().get(dn.getNames().size() - 1);
            lastEntry = new Tuple<String, String>(ldapRdn.getKey(), ldapRdn.getValue());
        }
        return lastEntry;
    }

    public static LdapContextSource populateContextSource(
            String serverIpAddress, int port, String baseDn, String userDn, String password) {
        if (StringUtils.isBlank(serverIpAddress) || port < 0 || StringUtils.isBlank(userDn)) {
            throw new IllegalArgumentException("Not all parameters are specified,");
        }
        StringBuilder urlAddressSB = new StringBuilder(LDAP_SCHEMA_PREFIX);
        urlAddressSB.append(serverIpAddress).append(':').append(port);
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(urlAddressSB.toString());
        contextSource.setBase(baseDn);
        if (StringUtils.isNotBlank(userDn)) {
            contextSource.setUserDn(userDn);
            contextSource.setPassword(password);
            //contextSource.setReferral("follow");
        }
        try {
            contextSource.afterPropertiesSet();
        } catch (Exception e) {
            throw new OpenFlameRuntimeException(e.getMessage(), e);
        }
        return contextSource;
    }

    public static LdapTemplate populateLdapTemplate(LdapContextSource contextSource) {
        LdapTemplate template = new LdapTemplate(contextSource);
        template.setIgnorePartialResultException(true);
        return template;
    }

    public static LdapSchemaConfig deSerializeConfig(String source) {
        LdapSchemaConfig config;
        if (StringUtils.isBlank(source)) {
            config = new LdapSchemaConfig();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(source, LdapSchemaConfig.class);
            } catch (IOException e) {
                logger.error("Failed to convert string value to " + LdapSchemaConfig.class.getSimpleName() + " object");
                throw new OpenFlameRuntimeException(e.getMessage(), e);
            }
        }
        return config;
    }

}