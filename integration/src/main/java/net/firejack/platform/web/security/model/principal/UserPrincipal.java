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
 *
 */
public class UserPrincipal extends OpenFlamePrincipal {

    /**
     * Info for current logged user
     */
    private IUserInfoProvider userInfoProvider;

    /**
     * UserPrincipal constructor
     * @param userInfoProvider user details
     */
    public UserPrincipal(IUserInfoProvider userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public IUserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    @Override
    public UserType getType() {
        return UserType.USER;
    }

    /**
     * Grants an action permission specific to an individual entity with a specific
     * identifier. Actions indicate the entity type they pertain to implicitly.
     *
     * @param action String action name (i.e. "user.edit" or "user.create" )
     * @param entityIdentifier String representation of the unique identifier for the entity (i.e. "1245332")
     */
//    public void grantContextSpecificPermission(String action, String entityIdentifier)
//    {
//        String ctxtPermission = generateContextSpecificPermission(action, entityIdentifier);
//    }
    
}