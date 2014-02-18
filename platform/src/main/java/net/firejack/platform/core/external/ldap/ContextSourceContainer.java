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

import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import org.springframework.ldap.core.support.LdapContextSource;


public class ContextSourceContainer {

    private LdapContextSource baseContextSource;
    private LdapContextSource usersContextSource;
    private LdapContextSource groupsContextSource;

    public ContextSourceContainer(DirectoryModel directoryModel) {
        LdapSchemaConfig ldapSchemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
        if (ldapSchemaConfig == null) {
            throw new OpenFlameRuntimeException("ldapSchemaConfig property is not configured properly.");
        }
        this.baseContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), directoryModel.getBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
        this.usersContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), ldapSchemaConfig.getPeopleBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
        this.groupsContextSource = LdapUtils.populateContextSource(
                directoryModel.getServerName(), directoryModel.getPort(), ldapSchemaConfig.getGroupsBaseDN(),
                directoryModel.getRootDN(), directoryModel.getPassword());
    }

    public ContextSourceContainer(CheckUrl checkUrl) {
        this.baseContextSource = LdapUtils.populateContextSource(
                checkUrl.getServerName(), checkUrl.getPort(), checkUrl.getUrlPath(),
                checkUrl.getUsername(), checkUrl.getPassword());
    }

    public LdapContextSource getBaseContextSource() {
        return baseContextSource;
    }

    public LdapContextSource getUsersContextSource() {
        return usersContextSource;
    }

    public LdapContextSource getGroupsContextSource() {
        return groupsContextSource;
    }
}