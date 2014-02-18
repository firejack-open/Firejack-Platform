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