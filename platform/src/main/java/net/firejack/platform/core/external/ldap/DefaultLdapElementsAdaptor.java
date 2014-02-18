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

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.PasswordEncodingType;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.io.UnsupportedEncodingException;

public class DefaultLdapElementsAdaptor implements LdapElementsAdaptor {

    private static final Logger logger = Logger.getLogger(DefaultLdapElementsAdaptor.class);

    private LdapUserAttributesMapper userAttributesMapper;
    private LdapUserContextMapper userContextMapper;
    private LdapGroupContextMapper groupContextMapper;
    private LdapSchemaConfig schemaConfig;

    @Override
    public LdapSchemaConfig getSchemaConfig() {
        return schemaConfig;
    }

    public void setSchemaConfig(LdapSchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig;
    }

    @Override
    public AttributesMapper provideUserAttributesMapper() {
        if (this.userAttributesMapper == null) {
            this.userAttributesMapper = new LdapUserAttributesMapper(this);
        }
        return this.userAttributesMapper;
    }

    @Override
    public ContextMapper provideGroupContextMapper() {
        if (this.groupContextMapper == null) {
            this.groupContextMapper = new LdapGroupContextMapper();
        }
        return this.groupContextMapper;
    }

    @Override
    public ContextMapper provideUserContextMapper() {
        if (this.userContextMapper == null) {
            this.userContextMapper = new LdapUserContextMapper(this);
        }
        return this.userContextMapper;
    }

    @Override
    public Attributes buildUserAttributes(User user) {
        Attributes attributes = new BasicAttributes();
        BasicAttribute objectClassAttribute = new BasicAttribute("objectclass");
        objectClassAttribute.add(this.schemaConfig.getPeopleObjectclass());
        objectClassAttribute.add("inetOrgPerson");
        attributes.put(objectClassAttribute);
        attributes.put("givenName", user.getFirstName());
        attributes.put("sn", user.getLastName());
        attributes.put("cn", user.getFirstName() + " " + user.getLastName());
        attributes.put(this.schemaConfig.getUidAttributeName(), user.getUsername());
        //attributes.put("cn", user.getUsername());
        if (StringUtils.isBlank(this.schemaConfig.getPasswordEncodingType())) {
            attributes.put("userPassword", user.getPassword());
        } else if (PasswordEncodingType.SHA.name().equalsIgnoreCase(this.schemaConfig.getPasswordEncodingType())) {
            attributes.put("userPassword", "{SHA}" + SecurityHelper.hashSHA(user.getPassword()));
        } else {
            attributes.put("userPassword", "{MD5}" + SecurityHelper.hashMD5(user.getPassword()));
        }
        attributes.put(this.schemaConfig.getEmailAttributeName(), user.getEmail());

        return attributes;
    }

    @Override
    public DistinguishedName buildUserDn(User user) {
        DistinguishedName dn = new DistinguishedName();
        if ("uid".equalsIgnoreCase(schemaConfig.getUidAttributeName())) {
            dn.add("uid", user.getUsername());
        } else {
            dn.add("cn", user.getFirstName() + " " + user.getLastName());
        }
        return dn;
    }

    @Override
    public DistinguishedName buildGroupDn(String groupName) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("cn", groupName);
        return dn;
    }

    @Override
    public DirContextOperations buildGroupAttributes(DirContextOperations adapter, Group group) {
        adapter.setAttributeValues("objectclass",
                new String[] { "top", this.schemaConfig.getGroupsObjectclass() });
        adapter.setAttributeValue("cn", group.getName());
        return adapter;
    }

    private byte[] encodePassword(String password) {
		String newQuotedPassword = "\"" + password + "\"";
        try {
            return newQuotedPassword.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to encode password.");
            throw new OpenFlameRuntimeException(e.getMessage(), e);
        }
    }

}