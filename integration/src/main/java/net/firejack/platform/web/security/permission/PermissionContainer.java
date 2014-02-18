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
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class PermissionContainer implements IPermissionContainer {

    private List<UserPermission> grantedActions;
    private List<UserPermission> deniedActions;

    @Override
    public List<UserPermission> loadGrantedActions(Principal p) {
        if (grantedActions == null) {
            grantedActions = Collections.emptyList();
        }
        return grantedActions;
    }

    /**
     * @param grantedActions
     */
    public void setGrantedActions(List<UserPermission> grantedActions) {
        this.grantedActions = grantedActions;
    }

    @Override
    public List<UserPermission> loadDeniedActions(Principal p) {
        if (deniedActions == null) {
            deniedActions = Collections.emptyList();
        }
        return deniedActions;
    }

    @Override
    public List<UserPermission> loadUserPermissionsBySecuredRecords(Principal p, Long securedRecordId) {
        return Collections.emptyList();
    }

    @Override
    public List<UserPermission> loadPackageLevelUserPermissions(Principal p, String packageLookup) {
        return Collections.emptyList();
    }

    @Override
    public Map<Long, List<UserPermission>> loadSecuredRecordContextPermissions(Long userId) {
        return Collections.emptyMap();
    }

    /**
     * @param deniedActions
     */
    public void setDeniedActions(List<UserPermission> deniedActions) {
        this.deniedActions = deniedActions;
    }
}
