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

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.service.authority.utils.ISecurityAuthenticator;
import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class is default implementation of security authenticator. It checks existence of user in store
 * using provided user name and password
 */
public class DefaultOpenFlameAuthenticator implements ISecurityAuthenticator {

    private static final Logger logger = Logger.getLogger(DefaultOpenFlameAuthenticator.class);

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;
    @Autowired
    private IUserStore userStore;

    /**
     *
     * @see net.firejack.platform.service.authority.utils.ISecurityAuthenticator#authenticate(String, String)
     */
    @Override
    public IUserInfoProvider authenticate(String userName, String password) {
        UserModel user = null;
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            String encryptedPassword = passwordEncryptionStrategy.encryptPassword(password);
            user = findUserByUsernameAndPassword(userName, encryptedPassword);
        }
        return user;
    }

    /**
     * Find user using user name and password.
     * 
     * @param username user name
     * @param password password
     * @return user if there was user found using specified user name and password
     *
     * @see net.firejack.platform.core.model.user.UserModel
     */
    protected UserModel findUserByUsernameAndPassword(String username, String password) {
        UserModel user = null;
        if (ConfigContainer.isAppInstalled()) {
            try {
                user = userStore.findUserByUsernameAndPassword(username, password);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        return user;
    }
}