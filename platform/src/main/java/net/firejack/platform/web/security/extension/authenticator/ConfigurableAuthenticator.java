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