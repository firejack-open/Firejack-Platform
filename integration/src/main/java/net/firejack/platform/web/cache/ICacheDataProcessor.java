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

package net.firejack.platform.web.cache;

import net.firejack.platform.api.authority.domain.ContextPermissions;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.domain.IdFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ICacheDataProcessor {

    /**
     * @param forceReload
     */
    void loadData(boolean forceReload);

    /**
     * @param initialDelay
     * @param delay
     * @param timeUnit
     */
    void scheduleReload(int initialDelay, int delay, TimeUnit timeUnit);

    /**
     * @param roleId
     * @param permissions
     */
    void addPackageRole(Long roleId, List<UserPermission> permissions, boolean isGuest);

    /**
     * @param roleId
     * @param permissions
     */
    void updateRolePermissions(Long roleId, List<String> permissions, boolean isGuest);

    /**
     * @param roleId
     */
    void deleteRole(Long roleId);

    /**
     * @param userId
     * @param roleId
     */
    void deleteUserRole(Long userId, Long roleId);

    void deleteContextUserRoles(Map<Long, Collection<ContextPermissions>> userRoles);

    /**
     * @param roles
     * @return
     */
    Map<Long, List<UserPermission>> getPermissionsByRoles(Long... roles);

    /**
     * @param permissionsToUpdate
     * @param updatedPermissions
     */
    void updatePermissions(List<UserPermission> permissionsToUpdate, List<UserPermission> updatedPermissions);

    /**
     * @param permissionsToDelete
     */
    void removePermissions(List<UserPermission> permissionsToDelete);

    void removePermission(UserPermission permissionToDelete);

    /**
     * @return
     */
    Map<String, List<NavigationElement>> getNavigationElements();

    /**
     * @param navigation
     */
    void addNavigation(NavigationElement navigation);

    /**
     * @param navigationElements
     */
    void addNavigationElements(List<NavigationElement> navigationElements);

    /**
     * @param navigation
     */
    void updateNavigation(NavigationElement navigation);

    /**
     * @param navigation
     */
    void removeNavigation(NavigationElement navigation);

    /**
     * @return
     */
    Map<String, List<ResourceLocation>> getResourceLocations();

    /**
     * @param resourceLocation
     */
    void addResourceLocation(ResourceLocation resourceLocation);

    /**
     * @param rlList
     */
    void addResourceLocations(List<ResourceLocation> rlList);

    /**
     * @param resourceLocation
     */
    void updateResourceLocation(ResourceLocation resourceLocation);

    /**
     * @param resourceLocation
     */
    void removeResourceLocation(ResourceLocation resourceLocation);

    /**
     * @param packageLookup
     * @return
     */
    List<Action> getActions(String packageLookup);

    /**
     * @param actionInfo
     */
    void addAction(Action actionInfo);

    /**
     * @param actions
     */
    void addActions(List<Action> actions);

    /**
     * @param actionInfo
     */
    void updateAction(Action actionInfo);

    /**
     * @param actionInfo
     */
    void removeAction(Action actionInfo);

    /**
     * @param userInfo
     * @param roleIdList
     */
    void setUserRole(User userInfo, List<Long> roleIdList);

    /**
     * @param userId
     * @param roleId
     */
    void addUserRole(Long userId, Long roleId);

    /**
     *
     * @param userId
     * @return
     */
    List<Long> getUserRoles(Long userId);

    /**
     * @param userId
     * @return
     */
    Map<Long, Map<Long, List<UserPermission>>> getUserPermissionsBySecuredRecords(Long userId);

    /**
     * @param userId
     * @return
     */
    Map<Long, List<UserPermission>> getUserContextPermissions(Long userId);

    /**
     * @param userId
     * @param securedRecordId
     * @param roleId
     * @param entityType
     * @param entityId
     * @param permissionLookupList
     */
    void addUserContextPermissions(Long userId, Long securedRecordId, Long roleId, String entityType, String entityId, List<String> permissionLookupList);

    void updatePackageLevelPermissions(String packageLookup, Long userId, Long roleId, List<String> permissionLookupList);

    void deletePackageLevelPermissions(String packageLookup, Long userId, Long roleId);

    /**
     * @param userId
     * @param roleId
     * @param entityType
     * @param entityId
     * @param permissionLookupList
     */
    void addUserContextPermissions(Long userId, Long roleId, String entityType, String entityId, List<String> permissionLookupList);

    /**
     * @param securedRecords
     */
    void refreshSecuredRecords(Map<Long, SecuredRecordNode> securedRecords);

    void addSecuredRecords(List<SecuredRecordNode> securedRecords);

    void addSecuredRecord(SecuredRecordNode securedRecord);

    void deleteSecuredRecord(Long securedRecordId);

    void deleteSecuredRecords(List<Long> securedRecordIdList);

    /**
     * @param userId
     * @return
     */
    Map<String, IdFilter> getFiltersByUser(Long userId);

    /**
     * @param lookup
     */
    void addNewPackage(String lookup);

    /**
     * @param oldLookup
     * @param newLookup
     */
    void updatePackage(String oldLookup, String newLookup);

    /**
     * @param lookup
     */
    void removePackage(String lookup);

    void associatePermissionWithAction(String actionLookup, Permission permission);

    void associatePermissionWithNavigation(String navigationLookup, Permission permission);

    void associatePermissionWithResourceLocation(String resourceLocationLookup, Permission permission);

    void updateEntityLookup(String oldLookup, String newLookup);

    void updateExtendedEntity(String entityToUpdate, String newExtendedEntity);

    void saveNewEntity(Entity entity);

    void saveEntity(Entity entity);

    void deleteEntityByLookup(String entityLookup);

}