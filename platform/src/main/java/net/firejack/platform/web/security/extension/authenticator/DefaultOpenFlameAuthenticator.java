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