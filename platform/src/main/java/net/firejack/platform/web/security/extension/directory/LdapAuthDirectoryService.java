/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.extension.directory;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.directory.IDirectoryService;
import net.firejack.platform.web.security.directory.exception.UserAlreadyExistsException;
import org.apache.log4j.Logger;

import java.util.Map;

public class LdapAuthDirectoryService implements IDirectoryService {

    private LdapServiceFacade ldapServiceFacade;
    private static final Logger logger = Logger.getLogger(LdapAuthDirectoryService.class);
    private DirectoryModel directoryModel;

    private LdapAuthDirectoryService(LdapServiceFacade ldapServiceFacade, DirectoryModel directoryModel) {
        this.ldapServiceFacade = ldapServiceFacade;
        this.directoryModel = directoryModel;
    }

    @Override
    public IUserInfoProvider authenticate(String userName, String password) {
        User authenticatedUser;
        try {
            authenticatedUser = this.ldapServiceFacade.authenticate(userName, password);
            if (authenticatedUser != null) {
                ServiceResponse<User> response = OPFEngine.DirectoryService.importLdapUserIfNecessary(
                        this.directoryModel.getId(), authenticatedUser);
                authenticatedUser = response.getItem();
            }
        } catch (Throwable th) {
            logger.error(th.getMessage(), th);
            authenticatedUser = null;
        }
        return authenticatedUser;
    }

    @Override
    public IUserInfoProvider createUser(String userName, String password, String email, Map<String, String> additionalProperties) throws UserAlreadyExistsException {
        throw new OpenFlameRuntimeException("Not supported");
    }

    public static LdapAuthDirectoryService populateInstance(DirectoryModel directoryModel) {
        DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
        LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
        adaptor.setSchemaConfig(schemaConfig);
        LdapServiceFacade ldapService = new LdapServiceFacade(
                new ContextSourceContainer(directoryModel), adaptor);
        return new LdapAuthDirectoryService(ldapService, directoryModel);
    }
}