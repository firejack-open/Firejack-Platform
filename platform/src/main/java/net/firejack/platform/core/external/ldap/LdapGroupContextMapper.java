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