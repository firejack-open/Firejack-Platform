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
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.service.authority.utils.ISecurityAuthenticator;
import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This Security Authenticator checks specified user name and password against configured user data
 *
 * @see net.firejack.platform.service.authority.utils.ISecurityAuthenticator
 */
public class ConfigurableAuthenticator implements ISecurityAuthenticator {

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;

    private IUserInfoProvider user;

    /**
     * Set user info that will be used during authentication check
     *
     * @param user user info
     */
//    @Required
    public void setUser(IUserInfoProvider user) {
        this.user = user;
    }

    /**
     * @see net.firejack.platform.service.authority.utils.ISecurityAuthenticator#authenticate(String, String)
     */
    @Override
    public IUserInfoProvider authenticate(String userName, String password) {
        if (!ConfigContainer.isAppInstalled() && this.user != null) {
            if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
                password = passwordEncryptionStrategy.encryptPassword(password);
                if (user.getUsername().equals(userName) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }
}