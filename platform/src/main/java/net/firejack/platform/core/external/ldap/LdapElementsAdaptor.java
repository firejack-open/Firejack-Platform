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