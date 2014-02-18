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

package net.firejack.platform.web.security.model.principal;

import net.firejack.platform.core.model.user.IUserInfoProvider;


/**
 * A Principal that has all possible permissions
 */
public class SystemPrincipal extends UserPrincipal {

    public SystemPrincipal(IUserInfoProvider userInfoProvider) {
        super(userInfoProvider);
    }

    @Override
    public UserType getType() {
        return UserType.SYSTEM;
    }

/*@Override
    public boolean hasPermission(UserPermission permission) {
        return true;
    }

    @Override
    public boolean checkUserPermission(UserPermission checkingUserPermission) {
        return true;
    }

    @Override
    public boolean checkUserPermission(IMultiPermissionClaimant permissionClaimant) {
        return true;
    }

    @Override
    public boolean checkUserPermission(List<String> permissionsToCheck) {
        return true;
    }

    @Override
    public boolean checkUserPermission(UserPermission permissionToCheck, boolean checkGlobalPermissions) {
        return true;
    }

    @Override
    public List<UserPermission> getGrantedUserPermissions() {
        return Collections.emptyList();
    }

    @Override
    public List<UserPermission> getDeniedUserPermissions() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Boolean> getPermissionIds(UserPermission checkingUserPermission) {
        return Collections.emptyMap();
    }

    @Override
    public IUserInfoProvider getUserInfoProvider() {
        return new IUserInfoProvider() {
	        private static final long serialVersionUID = 2373413217519602422L;

	        @Override
            public String getUsername() {
                return "system";
            }

            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public boolean isGuest() {
                return false;
            }

            @Override
            public Long getId() {
                return null;
            }

            @Override
            public void setId(Long aLong) {
            }
        };
    }*/

}