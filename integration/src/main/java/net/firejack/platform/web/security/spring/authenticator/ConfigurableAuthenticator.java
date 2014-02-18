/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.security.spring.authenticator;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import net.firejack.platform.web.security.permission.PermissionContainer;
import net.firejack.platform.web.security.spring.SpringSecurityAuthenticationRoleType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public class ConfigurableAuthenticator extends Authenticator {

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;

    private IUserInfoProvider user;
    private List<SpringSecurityAuthenticationRoleType> roles;
    private List<UserPermission> grantedPermissions;
    private List<UserPermission> deniedPermissions;

    /**
     * @param user
     */
    public void setUser(IUserInfoProvider user) {
        this.user = user;
    }

    /**
     * @param roles
     */
    public void setRoles(List<SpringSecurityAuthenticationRoleType> roles) {
        this.roles = roles;
    }

    /**
     * @param grantedPermissions
     */
    public void setGrantedPermissions(List<UserPermission> grantedPermissions) {
        this.grantedPermissions = grantedPermissions;
    }

    /**
     * @param deniedPermissions
     */
    public void setDeniedPermissions(List<UserPermission> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    /**
     * @return
     */
    public List<SpringSecurityAuthenticationRoleType> getRoles() {
        if (roles == null) {
            roles = new ArrayList<SpringSecurityAuthenticationRoleType>();
            roles.add(SpringSecurityAuthenticationRoleType.ROLE_DEFAULT); //todo: eliminate such usages
        }
        return roles;
    }

    @Override
    public IAuthenticationDetails authenticate(IAuthenticationSource authenticationSource) {
        String userName = authenticationSource.getPrincipal();
        String password = authenticationSource.getCredential();
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            password = passwordEncryptionStrategy.encryptPassword(password);
            if (user != null && user.getUsername().equals(userName) && user.getPassword().equals(password)) {
                PermissionContainer permissionLoader = new PermissionContainer();
                permissionLoader.setGrantedActions(grantedPermissions);
                permissionLoader.setDeniedActions(deniedPermissions);
                return new DefaultAuthenticationDetails(provideGrantedAuthorities(getRoles()), authenticationSource, user, permissionLoader);
            }
        }
        return null;
    }

}
