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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.IMultiPermissionClaimant;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import org.apache.log4j.Logger;

import java.security.Principal;
import java.util.*;


@SuppressWarnings("unused")
public abstract class OpenFlamePrincipal implements Principal {

    private static final Logger logger = Logger.getLogger(OpenFlamePrincipal.class);
    private static final String MSG_WARN_NO_PERMISSIONS_SPECIFIED_FOR_CLAIMANT = "Permission claimant does not specify required permissions.";

    private IPermissionContainer permissionContainer;

    /**
     * Returns the name of the principal object. This is the username for the current
     * login user.
     *
     * @return String username of the current user
     */
    @Override
    public String getName() {
        return getUserInfoProvider().getUsername();
    }

    /**
     * Checks whether some user has signed in or principal represents Guest user
     *
     * @return true if no user was signed in
     */
    public boolean isGuestPrincipal() {
        return getUserInfoProvider().isGuest();
    }

    public abstract UserType getType();

    /**
     * Checks to see if the current principal has been granted this specific permission by name.
     *
     * @param permission String permission name
     * @return boolean true if the permission is granted, false otherwise
     */
    public boolean hasPermission(UserPermission permission) {
        return this.permissionContainer.loadGrantedActions(this).contains(permission);
    }

    /**
     * Return all granted user permissions
     *
     * @return list of permissions
     */
    public List<UserPermission> getGrantedUserPermissions() {
        return this.permissionContainer.loadGrantedActions(this);
    }

    /**
     * Return all granted user permissions
     *
     * @return list of permissions
     */
    public List<UserPermission> getDeniedUserPermissions() {
        return this.permissionContainer.loadDeniedActions(this);
    }

    /**
     * Checks to see if a specific entity and operation combination is authorized for this user. This
     * method uses the generic hasPermission() method above in combination with the context specific
     * generation method. It also assumes that explicit global grants override missing permissions in the
     * map - meaning that if a user has permission to perform the action "edit.user" and is not
     * explicitly denied permission to perform the action "edit.user" on the user with id "12345"
     * this will be assumed to be authorized via the global privledge grant.
     *
     * @param checkingUserPermission String action name for the action being performed
     * @return boolean whether or not the user has permission to perform this action in context
     */
    public boolean checkUserPermission(UserPermission checkingUserPermission) {
        return checkUserPermission(checkingUserPermission, true);
    }

    /**
     * @param permissionClaimant
     * @return
     */
    public boolean checkUserPermission(IMultiPermissionClaimant permissionClaimant) {
        List<String> permissionLookupList = permissionClaimant.produceRequiredPermissionList();
        if (permissionLookupList == null || permissionLookupList.isEmpty()) {
            logger.warn(MSG_WARN_NO_PERMISSIONS_SPECIFIED_FOR_CLAIMANT);
            return false;
        } else {
            return checkUserPermission(permissionLookupList);
        }
    }

    /**
     * @param permissionsToCheck
     * @return
     */
    public boolean checkUserPermission(List<String> permissionsToCheck) {
        boolean granted = false;
        if (permissionsToCheck.isEmpty()) {
            granted = false;
        } else {
            Set<String> permissionPathSet = new TreeSet<String>();
            permissionPathSet.addAll(permissionsToCheck);
            for (UserPermission assignedUserPermission : this.permissionContainer.loadGrantedActions(this)) {
                if (permissionPathSet.remove(assignedUserPermission.getPermission()) && permissionPathSet.isEmpty()) {
                    granted = true;
                    break;
                }
            }
        }
        if (granted) {//check for deny permission
            List<String> permissionPathSet = new ArrayList<String>();
            permissionPathSet.addAll(permissionsToCheck);
            for (String permissionLookup : permissionPathSet) {
                UserPermission checkingUserPermission = new UserPermission(permissionLookup);
                for (UserPermission deniedUserPermission : this.permissionContainer.loadDeniedActions(this)) {
                    boolean isDetected = isPermissionDetected(checkingUserPermission, deniedUserPermission);
                    if (isDetected) {
                        return false;
                    }
                }
            }
        }
        return granted;
    }

    /**
     * @param checkingUserPermission
     * @return
     */
    public Map<String, Boolean> getPermissionIds(UserPermission checkingUserPermission) {
        Map<String, Boolean> permissionIds = new HashMap<String, Boolean>();
        List<UserPermission> grantedPermissions = this.permissionContainer.loadGrantedActions(this);
        for (UserPermission userPermission : grantedPermissions) {
            if (checkingUserPermission.getPermission().equals(userPermission.getPermission())) {
                if (checkingUserPermission.getEntityId() != null) {
                    permissionIds.put(checkingUserPermission.getEntityId(), true);
                }
            }
        }
        List<UserPermission> deniedPermissions = this.permissionContainer.loadDeniedActions(this);
        for (UserPermission userPermission : deniedPermissions) {
            if (checkingUserPermission.getPermission().equals(userPermission.getPermission())) {
                if (checkingUserPermission.getEntityId() != null) {
                    permissionIds.put(checkingUserPermission.getEntityId(), false);
                }
            }
        }
        return permissionIds;
    }

    /**
     * @param permissionContainer
     */
    public void assignPermissionContainer(IPermissionContainer permissionContainer) {
        this.permissionContainer = permissionContainer;
    }

    /**
     * @param permissionToCheck permission we want to check against assigned permissions
     * @param checkGlobalPermissions flag specifies if we need to check global permissions as well.
     * @return true if authorization was granted
     */
    public boolean checkUserPermission(UserPermission permissionToCheck, boolean checkGlobalPermissions) {
        boolean granted = false;
        if (checkGlobalPermissions) {
            for (UserPermission assignedUserPermission : this.permissionContainer.loadGrantedActions(this)) {
                boolean isDetected = isPermissionDetected(permissionToCheck, assignedUserPermission);
                if (isDetected) {
                    granted = true;
                    break;
                }
            }
        }
        Long securedRecordId = OPFContext.getContext().getSecuredRecordId();
        if (!granted && securedRecordId != null) {
            List<UserPermission> srContextPermissions =
                    this.permissionContainer.loadUserPermissionsBySecuredRecords(this, securedRecordId);
            if (srContextPermissions != null) {
                for (UserPermission assignedUserPermission : srContextPermissions) {
                    boolean isDetected = isPermissionDetected(permissionToCheck, assignedUserPermission);
                    if (isDetected) {
                        granted = true;
                        break;
                    }
                }
            }
        }
        if (granted) {//check for deny permission
            for (UserPermission deniedUserPermission : this.permissionContainer.loadDeniedActions(this)) {
                boolean isDetected = isPermissionDetected(permissionToCheck, deniedUserPermission);
                if (isDetected) {
                    granted = false;
                    break;
                }
            }
        }
        return granted;
    }

    /**
     * Check package level permissions
     * @param packageLookup package lookup
     * @param permissionsToCheck permissions to check
     * @return true if access granted, otherwise false.
     */
    public boolean checkPackageLevelPermissions(String packageLookup, List<UserPermission> permissionsToCheck) {
        boolean granted = false;
        if (permissionsToCheck == null || permissionsToCheck.isEmpty()) {
            granted = true;
        } else {
            Map<String, UserPermission> permissionMap = expand(permissionsToCheck);
            if (permissionMap != null) {
                List<UserPermission> userPermissions =
                        this.permissionContainer.loadPackageLevelUserPermissions(this, packageLookup);
                if (userPermissions == null || userPermissions.isEmpty()) {
                    granted = true;
                } else {
                    for (UserPermission assignedUserPermission : userPermissions) {
                        for (Map.Entry<String, UserPermission> permissionEntry : permissionMap.entrySet()) {
                            UserPermission permissionToCheck = permissionEntry.getValue();
                            boolean isDetected = isPermissionDetected(permissionToCheck, assignedUserPermission);
                            if (isDetected) {
                                permissionMap.remove(permissionEntry.getKey());
                                break;
                            }
                        }
                        if (permissionMap.isEmpty()) {
                            granted = true;
                            break;
                        }
                    }
                }
            }
        }
        return granted;
    }

    /**
     * @param permissionsToCheck permission we want to check against assigned permissions
     * @param checkGlobalPermissions flag specifies if we need to check global permissions as well.
     * @return true if authorization was granted
     */
    public boolean checkUserPermission(List<UserPermission> permissionsToCheck, boolean checkGlobalPermissions) {
        boolean granted = false;
        if (permissionsToCheck == null || permissionsToCheck.isEmpty()) {
            granted = true;
        } else {
            if (checkGlobalPermissions) {
                Map<String, UserPermission> permissionMap = expand(permissionsToCheck);
                if (permissionMap != null) {
                    for (UserPermission assignedUserPermission : this.permissionContainer.loadGrantedActions(this)) {
                        for (Map.Entry<String, UserPermission> permissionEntry : permissionMap.entrySet()) {
                            UserPermission permissionToCheck = permissionEntry.getValue();
                            boolean isDetected = isPermissionDetected(permissionToCheck, assignedUserPermission);
                            if (isDetected) {
                                permissionMap.remove(permissionEntry.getKey());
                                break;
                            }
                        }
                        if (permissionMap.isEmpty()) {
                            granted = true;
                            break;
                        }
                    }
                }
            }
            Long securedRecordId = OPFContext.getContext().getSecuredRecordId();
            if (!granted && securedRecordId != null) {
                List<UserPermission> srContextPermissions =
                        this.permissionContainer.loadUserPermissionsBySecuredRecords(this, securedRecordId);
                if (srContextPermissions != null) {
                    Map<String, List<String>> cachedTypes = prepareCachedTypes(permissionsToCheck);
                    Map<String, UserPermission> permissionMap = expand(permissionsToCheck);
                    for (UserPermission assignedUserPermission : srContextPermissions) {
                        for (Map.Entry<String, UserPermission> permissionEntry : permissionMap.entrySet()) {
                            UserPermission permissionToCheck = permissionEntry.getValue();
                            boolean isDetected = isPermissionDetected(
                                    permissionToCheck, assignedUserPermission,
                                    cachedTypes.get(permissionToCheck.getEntityType()));
                            if (isDetected) {
                                permissionMap.remove(permissionEntry.getKey());
                                break;
                            }
                        }
                        if (permissionMap.isEmpty()) {
                            granted = true;
                            break;
                        }
                    }
                }
            }
            if (granted) {//check for deny permission
                List<UserPermission> deniedPermissions = this.permissionContainer.loadDeniedActions(this);
                if (deniedPermissions != null && !deniedPermissions.isEmpty()) {
                    for (UserPermission deniedUserPermission : deniedPermissions) {
                        for (UserPermission permissionToCheck : permissionsToCheck) {
                            boolean isDetected = isPermissionDetected(permissionToCheck, deniedUserPermission);
                            if (isDetected) {
                                granted = false;
                                break;
                            }
                        }
                        if (!granted) {
                            break;
                        }
                    }
                }
            }
        }
        return granted;
    }

    /**
     * Get details of currently logged user
     *
     * @return user info provider
     */
    public abstract IUserInfoProvider getUserInfoProvider();

    private Map<String, UserPermission> expand(List<UserPermission> permissions) {
        Map<String, UserPermission> map;
        if (permissions == null) {
            map = null;
        } else {
            map = new HashMap<String, UserPermission>();
            for (UserPermission userPermission : permissions) {
                map.put(userPermission.getPermission(), userPermission);
            }
        }
        return map;
    }

    private boolean isPermissionDetected(UserPermission checkingUserPermission, UserPermission assignedUserPermission) {
        boolean isDetected = false;
        if (checkingUserPermission.getPermission().equals(assignedUserPermission.getPermission())) {
            if (!(assignedUserPermission.getEntityId() != null && assignedUserPermission.getEntityType() != null)) {
                isDetected = true;
            } else {
                if (assignedUserPermission.getEntityId() != null && assignedUserPermission.getEntityType() != null) {
                    isDetected = checkingUserPermission.getEntityType().startsWith(assignedUserPermission.getEntityType()) &&
                            assignedUserPermission.getEntityId().equals(checkingUserPermission.getEntityId());
                } else {
                    isDetected = true;
                }
            }
        }
        return isDetected;
    }

    private boolean isPermissionDetected(UserPermission checkingUserPermission, UserPermission assignedUserPermission, List<String> cachedTypes) {
        boolean isDetected = false;
        if (checkingUserPermission.getPermission().equals(assignedUserPermission.getPermission())) {
            if (!(assignedUserPermission.getEntityId() != null && assignedUserPermission.getEntityType() != null)) {
                isDetected = true;
            } else {
                if (assignedUserPermission.getEntityId() != null && assignedUserPermission.getEntityType() != null) {
                    isDetected = cachedTypes.contains(checkingUserPermission.getEntityType()) &&
                            assignedUserPermission.getEntityId().equals(checkingUserPermission.getEntityId());
                } else {
                    isDetected = true;
                }
            }
        }
        return isDetected;
    }

    private Map<String, List<String>> prepareCachedTypes(List<UserPermission> permissionsToCheck) {
        Map<String, List<String>> cachedTypes = new HashMap<String, List<String>>();
        for (UserPermission userPermission : permissionsToCheck) {
            ServiceResponse<Entity> response =
                    OPFEngine.RegistryService.readEntitiesUpInHierarchyByLookup(userPermission.getEntityType());
            List<String> types = new ArrayList<String>();
            if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                for (Entity entityType : response.getData()) {
                    types.add(entityType.getLookup());
                }
            } else {
                types.add(userPermission.getEntityType());
            }
            cachedTypes.put(userPermission.getEntityType(), types);
        }
        return cachedTypes;
    }

}