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