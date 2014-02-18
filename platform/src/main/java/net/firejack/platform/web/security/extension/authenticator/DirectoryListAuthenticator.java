/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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