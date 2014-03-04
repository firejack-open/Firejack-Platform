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

package net.firejack.platform.web.security.extension.authenticator;

import com.sun.jersey.api.client.ClientHandlerException;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.service.authority.utils.ISecurityAuthenticator;
import net.firejack.platform.web.security.directory.DirectoryServiceRepository;
import net.firejack.platform.web.security.directory.IDirectoryService;
import net.firejack.platform.web.security.extension.directory.LdapAuthDirectoryService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * This implementation of security authenticator checks authentication through configured directories.
 */
public class DirectoryListAuthenticator implements ISecurityAuthenticator {

	private static final Logger logger = Logger.getLogger(DirectoryListAuthenticator.class);

	@Autowired
	private IDirectoryStore directoryStore;

	@Autowired
	@Qualifier("directoryServiceRepository")
	private DirectoryServiceRepository directoryServiceRepository;

	/**
	 * @see net.firejack.platform.service.authority.utils.ISecurityAuthenticator#authenticate(String, String)
	 */
	@Override
	public IUserInfoProvider authenticate(String userName, String password) {
		IUserInfoProvider user = null;
		if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			if (ConfigContainer.isAppInstalled()) {
				List<DirectoryModel> orderedDirectoryList = directoryStore.findOrderedDirectoryList();
				if (orderedDirectoryList == null) {
					logger.warn("There is empty directory list loaded during authentication. At least one directory should be configured");
				} else {
					for (DirectoryModel directory : orderedDirectoryList) {
						IDirectoryService service = directoryServiceRepository.getDirectoryService(directory.getLookup());
                        if (service == null && directory.getDirectoryType() == DirectoryType.LDAP) {
                            try {
                                service = LdapAuthDirectoryService.populateInstance(directory);
                            } catch(Throwable th) {
                                logger.error(th.getMessage(), th);
                            }
                        }
                        if (service != null) {
							try {
								user = service.authenticate(userName, password);
							} catch (ClientHandlerException e) {
								logger.error("Can't connect to server", e);
							}
							if (user != null) {
								break;
							}
						}
					}
				}
			} else {
				IDirectoryService directoryService = directoryServiceRepository.getDefault();
				user = directoryService.authenticate(userName, password);
			}
		}
		return user;
	}
}