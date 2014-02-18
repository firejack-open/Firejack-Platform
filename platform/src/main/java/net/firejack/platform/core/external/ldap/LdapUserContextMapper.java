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