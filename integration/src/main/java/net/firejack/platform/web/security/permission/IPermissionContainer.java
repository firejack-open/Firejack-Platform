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

package net.firejack.platform.web.security.permission;

import net.firejack.platform.api.authority.domain.UserPermission;

import java.security.Principal;
import java.util.List;
import java.util.Map;


public interface IPermissionContainer {

    /**
     * Loads all granted permissions for the provided user.
     *
     * @param p Principal that used for granted permissions load
     * @return List<String> a list of granted permissions for this user
     */
    List<UserPermission> loadGrantedActions(Principal p);

    /**
     * Loads all denied permissions (explicitly denied) for the provided user.
     *
     * @param p Principal that used for denied permissions load
     * @return List<String> a list of explicitly denied permissions for this user
     */
    List<UserPermission> loadDeniedActions(Principal p);

    /**
     * Load user permissions mapped to secured record Ids.
     *
     * @param p Principal that used for permissions load
     * @param securedRecordId securedRecordId
     * @return user permissions mapped to secured records
     */
    List<UserPermission> loadUserPermissionsBySecuredRecords(Principal p, Long securedRecordId);

    /**
     * Load user permissions mapped to secured record Ids.
     *
     * @param p Principal that used for permissions load
     * @param packageLookup packageLookup
     * @return user permissions mapped to secured records
     */
    List<UserPermission> loadPackageLevelUserPermissions(Principal p, String packageLookup);

    /**
     * Load context permissions by secured records map for specified user id
     * @param userId user id
     * @return context permissions by secured records map.
     */
    Map<Long, List<UserPermission>> loadSecuredRecordContextPermissions(Long userId);

}
