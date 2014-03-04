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