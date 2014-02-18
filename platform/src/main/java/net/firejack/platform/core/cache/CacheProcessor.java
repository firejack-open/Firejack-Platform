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

package net.firejack.platform.core.cache;

import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordPermissions;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.model.registry.authority.*;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.CollectionUtils;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.processor.cache.ConfigCacheProcessor;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.security.action.ActionComparator;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.session.UserSessionManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Scope("singleton")
@Component("cacheProcessor")
public class CacheProcessor implements ICacheDataProcessor {

    private static final Long EMPTY_SECURED_RECORD_ID = -1L;

//    private final CachedSecurityDataContext cachedData = new CachedSecurityDataContext();

    private static final Logger logger = Logger.getLogger(CacheProcessor.class);

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    @Autowired
    @Qualifier("actionStore")
    private IActionStore actionStore;

    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore resourceLocationStore;

    @Autowired
    private INavigationElementStore navigationElementStore;

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("userRoleStore")
    private IUserRoleStore userRoleStore;

    @Autowired
    @Qualifier("configStore")
    private IConfigStore configStore;

    @Autowired
    private ISecuredRecordStore securedRecordStore;

    @Autowired
    private IEntityStore entityStore;

    @Autowired
    private ConfigCacheProcessor configCacheProcessor;

    @Autowired
    private Factory modelFactory;

    private ScheduledExecutorService executor;

    /***/
    @PreDestroy
    public void destroy() {
//	    cachedData.clear();
        CacheManager.getInstance().close();
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void loadData(boolean forceReload) {
        if (/*cachedData.isEmpty() || */forceReload) {
//                cachedData.clear();
            List<RoleModel> roles = roleStore.findAll();
            if (roles != null) {
                Map<String, PackageModel> pkgByLookup = retrievePackageByLookup();
                List<String> packageLookupList = new LinkedList<String>(pkgByLookup.keySet());

                Map<String, List<NavigationElement>> navigationElementsByPackage = retrieveNavigationElementsByPackage(packageLookupList);
                Map<String, SortedSet<Action>> actionsByPackage = retrieveActionsByPackage();
                Map<String, List<ResourceLocation>> resourceLocationsByPackage = retrieveResourceLocationsByPackage(packageLookupList);
                Map<Long, SecuredRecordNode> securedRecords = retrieveSecuredRecords();
                Map<Long, List<UserPermission>> permissionsByRoles = retrieveGlobalPermissions(roles);
                Map<Long, SecuredRecordPermissions> securedRecordPermissions = retrieveSecuredRecordPermissions();

                Map<Long, List<Long>> userRoles = userStore.findAllRolesByUsers();
                Map<Long, Map<String, IdFilter>> readActionFiltersByUsers = populateReadActionFiltersInfo(
                        securedRecordPermissions, permissionsByRoles, userRoles);
                Map<String, List<Entity>> entitiesMap = retrieveEntityUpHierarchies();
                Map<String, Map<Long, UserContextPermissions>> userPermissionsByPackage =
                        retrievePackageLevelUserPermissions();
                List<ConfigModel> configModels = configStore.findAll();

                CacheManager cacheManager = CacheManager.getInstance();
                for (Map.Entry<String, SortedSet<Action>> packageActions : actionsByPackage.entrySet()) {
                    List<Action> actionList = new LinkedList<Action>(packageActions.getValue());
                    cacheManager.setActions(packageActions.getKey(), actionList);
                }
                for (Map.Entry<String, List<NavigationElement>> navigationElementsEntry : navigationElementsByPackage.entrySet()) {
                    cacheManager.setNavigationList(navigationElementsEntry.getKey(), navigationElementsEntry.getValue());
                }
                for (Map.Entry<String, List<ResourceLocation>> packageResourceLocations : resourceLocationsByPackage.entrySet()) {
                    cacheManager.setResourceLocations(packageResourceLocations.getKey(), packageResourceLocations.getValue());
                }
                //
                for (Map.Entry<Long, List<UserPermission>> permissionsByRole : permissionsByRoles.entrySet()) {
                    cacheManager.setRolePermissions(permissionsByRole.getKey(), permissionsByRole.getValue());
                }
                for (Map.Entry<Long, SecuredRecordPermissions> securedRecordPermission : securedRecordPermissions.entrySet()) {
                    cacheManager.setSecuredRecordPermissions(securedRecordPermission.getKey(), securedRecordPermission.getValue());
                }
                for (Map.Entry<Long, Map<String, IdFilter>> readActionFiltersByUser : readActionFiltersByUsers.entrySet()) {
                    cacheManager.setIdFiltersForUser(readActionFiltersByUser.getKey(), readActionFiltersByUser.getValue());
                }
                for (Map.Entry<String, List<Entity>> entry : entitiesMap.entrySet()) {
                    cacheManager.setTypeWithSubclasses(entry.getKey(), entry.getValue());
                    Boolean securityEnabled = entry.getValue().get(0).getSecurityEnabled();
                    cacheManager.setEntityAsSecurityEnabled(entry.getKey(), securityEnabled != null && securityEnabled);
                }
                for (Map.Entry<String, Map<Long, UserContextPermissions>> entry : userPermissionsByPackage.entrySet()) {
                    Map<Long, UserContextPermissions> userPermissions = entry.getValue();
                    if (userPermissions != null) {
                        for (Map.Entry<Long, UserContextPermissions> userPermissionsEntry : userPermissions.entrySet()) {
                            cacheManager.setPackageLevelUserPermissions(
                                    entry.getKey(), userPermissionsEntry.getKey(), userPermissionsEntry.getValue());
                        }
                    }
                }
                List<Config> configs = modelFactory.convertTo(Config.class, configModels);
                cacheManager.setConfigs(configs);

                cacheManager.setEntityTypes(new ArrayList<String>(entitiesMap.keySet()));
                cacheManager.setSecuredRecords(securedRecords);
                cacheManager.setUserRoles(userRoles);
                cacheManager.initializeGuestData(this);

                configCacheProcessor.initConfigs();
            }
        }
    }

    public void scheduleReload(int initialDelay, int delay, TimeUnit timeUnit) {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, initialDelay, delay, timeUnit);
    }

    @Override
    public void addNewPackage(String lookup) {
        if (StringUtils.isNotBlank(lookup)) {
            CacheManager cacheManager = CacheManager.getInstance();
            List<NavigationElement> navigationElements = cacheManager.getNavigationList(lookup);
            if (navigationElements == null) {
                cacheManager.setNavigationList(lookup, new LinkedList<NavigationElement>());
            }

            List<Action> actions = cacheManager.getActions(lookup);
            if (actions == null) {
                cacheManager.setActions(lookup, new LinkedList<Action>());
            }

            List<ResourceLocation> resourceLocations = cacheManager.getResourceLocations(lookup);
            if (resourceLocations == null) {
                cacheManager.setResourceLocations(lookup, new ArrayList<ResourceLocation>());
            }
        }
    }

    @Override
    public void updatePackage(String oldLookup, String newLookup) {
        if (ConfigContainer.isAppInstalled() && StringUtils.isNotBlank(oldLookup) && StringUtils.isNotBlank(newLookup) && !oldLookup.equals(newLookup)) {
            CacheManager cacheManager = CacheManager.getInstance();

            List<NavigationElement> oldNavigationElements = cacheManager.getNavigationList(oldLookup);
            List<NavigationElement> newNavigationElements = cacheManager.getNavigationList(newLookup);
            if (oldNavigationElements != null && newNavigationElements == null) {
                cacheManager.removeNavigationList(oldLookup);
                cacheManager.setNavigationList(newLookup, oldNavigationElements);
            }

            List<Action> oldActions = cacheManager.getActions(oldLookup);
            List<Action> newActions = cacheManager.getActions(newLookup);
            if (oldActions != null && newActions == null) {
                cacheManager.removeActions(oldLookup);
                cacheManager.setActions(newLookup, oldActions);
            }

            List<ResourceLocation> oldResourceLocations = cacheManager.getResourceLocations(oldLookup);
            List<ResourceLocation> newResourceLocations = cacheManager.getResourceLocations(newLookup);
            if (oldResourceLocations != null && newResourceLocations == null) {
                cacheManager.removeResourceLocations(oldLookup);
                cacheManager.setResourceLocations(newLookup, oldResourceLocations);
            }
        }
    }

    @Override
    public void removePackage(String lookup) {
        if (ConfigContainer.isAppInstalled() && StringUtils.isNotBlank(lookup)) {
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.removeNavigationList(lookup);
            cacheManager.removeActions(lookup);
            cacheManager.removeResourceLocations(lookup);
        }
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        List<Long> result = new ArrayList<Long>();
	    if (userId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<Long>> userRoles = cacheManager.getUserRoles();
            List<Long> roleList;
            if (userRoles != null && ((roleList = userRoles.get(userId)) != null)) {
                result = roleList;
            }
	    }
	    return result;
    }

    @Override
    public void updateRolePermissions(Long roleId, List<String> permissions, boolean isGuest) {
        if (!ConfigContainer.isAppInstalled()) {
            return;
        }
        if (roleId == null) {
            throw new IllegalArgumentException("Role id should not be null.");
        }
        permissions = permissions == null ? new ArrayList<String>() : permissions;

        CacheManager cacheManager = CacheManager.getInstance();
        List<UserPermission> rolePermissions = cacheManager.getRolePermissions(roleId);

        if (rolePermissions != null) {
            List<UserPermission> newRolePermissions = AuthorizationVOFactory.convertPermissionsEx(permissions);
            cacheManager.setRolePermissions(roleId, newRolePermissions);

            //update id filters
            Map<Long, List<Long>> userRoles = cacheManager.getUserRoles();
            for (Map.Entry<Long, List<Long>> entry : userRoles.entrySet()) {
                if (entry.getValue().contains(roleId)) {
                    updateIdFiltersOnRolesChange(entry.getKey(), entry.getValue());
                }
            }
            //finish update id filters

            UserSessionManager.getInstance().updateRolePermissionsForOpenedSessions(roleId, newRolePermissions);
        } else {
            Map<Long, SecuredRecordPermissions> srPermissionsMap = cacheManager.getAllSecuredRecordPermissions();
            Set<Long> userIdSet = null;
            for (SecuredRecordPermissions srPermissions : srPermissionsMap.values()) {
                for (UserContextPermissions userContextPermissions : srPermissions.getUserContextPermissions().values()) {
                    ContextPermissions contextPermissions = userContextPermissions.getContextPermissions(roleId);
                    if (contextPermissions != null) {
                        contextPermissions.setPermissions(permissions);
                        cacheManager.setSecuredRecordPermissions(srPermissions.getSecuredRecordId(), srPermissions);//!
                        if (userIdSet == null) {
                            userIdSet = new HashSet<Long>();
                        }
                        userIdSet.add(userContextPermissions.getUserId());
                    }
                }
            }
            if (userIdSet != null) {
                for (Long userId : userIdSet) {
                    Map<Long, Map<Long, List<UserPermission>>> userPermissionsBySecuredRecords = getUserPermissionsBySecuredRecords(userId);
                    UserSessionManager.getInstance().refreshUserContextRolePermissions(userId, userPermissionsBySecuredRecords);
                }
            }
            //if roleId was not found in neither in usual roles cache nor in context role cache, then it's a new role and this role with permissions will be added with addUserContextPermissions(  when necessary

            //todo: temp solution while we support package level permissions
            Map<Long, List<Long>> userRoles = cacheManager.getUserRoles();
            List<String> packageLookupList = packageStore.searchWithProjection(null, Projections.property("lookup"), null);

            List<UserPermission> newRolePermissions = null;
            for (Long userId : userRoles.keySet()) {
                for (String packageLookup : packageLookupList) {
                    UserContextPermissions packageLevelPermissions =
                            cacheManager.getPackageLevelUserPermissions(packageLookup, userId);
                    if (packageLevelPermissions != null &&
                            packageLevelPermissions.getRolePermissions().containsKey(roleId)) {
                        if (newRolePermissions == null) {
                            newRolePermissions = AuthorizationVOFactory.convertPermissionsEx(permissions);
                        }
                        packageLevelPermissions.getRolePermissions().put(roleId, newRolePermissions);
                        cacheManager.setPackageLevelUserPermissions(packageLookup, userId, packageLevelPermissions);
                    }

                }
            }
        }
        if (isGuest && UserSessionManager.getGuestRoleIds().add(roleId) ||
            !isGuest && UserSessionManager.getGuestRoleIds().remove(roleId)) {
            cacheManager.initializeGuestData(this);
        }
    }

    @Override
    public void addPackageRole(Long roleId, List<UserPermission> permissions, boolean isGuest) {
        if (ConfigContainer.isAppInstalled() && roleId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            List<UserPermission> rolePermissions = cacheManager.getRolePermissions(roleId);
            if (rolePermissions == null) {
                List<UserPermission> userPermissions = permissions == null ? new LinkedList<UserPermission>() : permissions;
                cacheManager.setRolePermissions(roleId, userPermissions);
                UserSessionManager userSessionManager = UserSessionManager.getInstance();
                userSessionManager.refreshRolePermissions();
                if (isGuest) {
                    UserSessionManager.getGuestRoleIds().add(roleId);
                    cacheManager.initializeGuestData(this);
                }
            }
        }
    }

    @Override
    public void deleteRole(Long roleId) {
        if (ConfigContainer.isAppInstalled() && roleId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.removeRolePermissions(roleId);

            Map<Long, List<Long>> userRoles = cacheManager.getUserRoles();
            for (Map.Entry<Long, List<Long>> entry : userRoles.entrySet()) {
                if (entry.getValue().contains(roleId)) {
                    entry.getValue().remove(roleId);
                    updateIdFiltersOnRolesChange(entry.getKey(), entry.getValue());
                }
            }
            cacheManager.setUserRoles(userRoles);
            UserSessionManager.getInstance().deleteRole(roleId);
            boolean isGuest = UserSessionManager.getGuestRoleIds().remove(roleId);
            if (isGuest) {
                cacheManager.initializeGuestData(this);
            }
        }
    }

    @Override
    public void addNavigation(NavigationElement navigation) {
        if (navigation != null) {
            boolean navigationAdded = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<NavigationElement>> navigationElements = cacheManager.getAllNavigationElements();
            if (navigationElements != null) {
                for (Map.Entry<String, List<NavigationElement>> packageNavigationVOs : navigationElements.entrySet()) {
                    if (navigation.getLookup().startsWith(packageNavigationVOs.getKey() + ".")) {
                        packageNavigationVOs.getValue().add(navigation);
                        cacheManager.setNavigationList(packageNavigationVOs.getKey(), packageNavigationVOs.getValue());
                        navigationAdded = true;
                        break;
                    }
                }
            }
            if (!navigationAdded) {
                logger.error("Failed to update Firejack Platform Node cache with new navigation. Reason: Failed to find suitable package mapping.");
            }
        }
    }

    @Override
    public void addNavigationElements(List<NavigationElement> navigations) {
        if (navigations != null && !navigations.isEmpty()) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<NavigationElement>> navigationElements = cacheManager.getAllNavigationElements();
            if (navigationElements != null) {
                for (Map.Entry<String, List<NavigationElement>> packageNavigationEntry : navigationElements.entrySet()) {
                    int oldSize = packageNavigationEntry.getValue().size();
                    for (NavigationElement navigation : navigations) {
                        if (navigation.getLookup().startsWith(packageNavigationEntry.getKey()) &&
                                !packageNavigationEntry.getValue().contains(navigation)) {
                            packageNavigationEntry.getValue().add(navigation);
                        }
                    }
                    if (oldSize != packageNavigationEntry.getValue().size()) {
                        cacheManager.setNavigationList(packageNavigationEntry.getKey(), packageNavigationEntry.getValue());
                    }
                }
            } else {
                logger.error("Failed to update Firejack Platform Node cache with new navigation elements. Reason: Trying to add navigation elements while no packages registered yet.");
            }
        }
    }

    @Override
    public void addResourceLocations(List<ResourceLocation> rlList) {
        if (rlList != null && !rlList.isEmpty()) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<ResourceLocation>> resourceLocations = cacheManager.getAllResourceLocations();
            if (resourceLocations != null) {
                for (Map.Entry<String, List<ResourceLocation>> packageRLEntry : resourceLocations.entrySet()) {
                    int oldSize = packageRLEntry.getValue().size();
                    for (ResourceLocation resourceLocation : rlList) {
                        if (resourceLocation.getLookup().startsWith(packageRLEntry.getKey()) &&
                                !packageRLEntry.getValue().contains(resourceLocation)) {
                            packageRLEntry.getValue().add(resourceLocation);
                        }
                    }
                    if (oldSize != packageRLEntry.getValue().size()) {
                        cacheManager.setResourceLocations(packageRLEntry.getKey(), packageRLEntry.getValue());
                    }
                }
            } else {
                logger.error("Failed to update Firejack Platform Node cache with new navigation elements. Reason: Trying to add navigation elements while no packages registered yet.");
            }
        }
    }

    @Override
    public void updateNavigation(NavigationElement navigation) {
        if (navigation != null) {
            boolean navigationAdded = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<NavigationElement>> navigationElements = cacheManager.getAllNavigationElements();
            if (navigationElements != null) {
                for (Map.Entry<String, List<NavigationElement>> navigationListEntry : navigationElements.entrySet()) {
                    for (NavigationElement nav : navigationListEntry.getValue()) {
                        if (nav.getId().equals(navigation.getId())) {
                            navigationListEntry.getValue().remove(nav);
                            navigationListEntry.getValue().add(navigation);
                            cacheManager.setNavigationList(navigationListEntry.getKey(), navigationListEntry.getValue());

                            if (nav.getPermissions() != null && navigation.getPermissions() != null) {
                                List<UserPermission> oldPermissionList = new LinkedList<UserPermission>();
                                for (Permission permission : nav.getPermissions()) {
                                    oldPermissionList.add(new UserPermission(permission.getLookup()));
                                }
                                List<UserPermission> newPermissionList = new LinkedList<UserPermission>();
                                for (Permission permission : navigation.getPermissions()) {
                                    newPermissionList.add(new UserPermission(permission.getLookup()));
                                }
                                updatePermissions(oldPermissionList, newPermissionList);
                            }

                            navigationAdded = true;
                            break;
                        }
                    }
                }
            }
            if (!navigationAdded) {
                logger.error("Failed to update Firejack Platform Node cache with updated navigation");
            }
        }
    }

    @Override
    public void removeNavigation(NavigationElement navigation) {
        if (ConfigContainer.isAppInstalled() && navigation != null) {
            boolean navigationRemoved = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<NavigationElement>> navigationElements = cacheManager.getAllNavigationElements();
            if (navigationElements != null) {
                for (Map.Entry<String, List<NavigationElement>> navigationListEntry : navigationElements.entrySet()) {
                    if (navigationListEntry.getValue().contains(navigation)) {
                        if (navigationListEntry.getValue().remove(navigation)) {
                            CacheManager.getInstance().setNavigationList(navigationListEntry.getKey(), navigationListEntry.getValue());
                            navigationRemoved = true;
                            break;
                        } else {
                            logger.error("Failed to remove element in container.");
                        }
                    }
                }
            }
            if (!navigationRemoved) {
                logger.error("Failed to update Firejack Platform Node cache. Navigation for removal was not found.");
            }
        }
    }

    @Override
    public Map<String, List<NavigationElement>> getNavigationElements() {
        CacheManager cacheManager = CacheManager.getInstance();
        return cacheManager.getAllNavigationElements();
    }

    @Override
    public Map<Long, List<UserPermission>> getPermissionsByRoles(Long... roles) {
        Map<Long, List<UserPermission>> permissions = new HashMap<Long, List<UserPermission>>();
        CacheManager cacheManager = CacheManager.getInstance();
        for (Long roleId : roles) {
            List<UserPermission> rolePermissions = cacheManager.getRolePermissions(roleId);
            permissions.put(roleId, rolePermissions);
        }
        return permissions;
    }

    @Override
    public void updatePermissions(List<UserPermission> permissionsToUpdate, List<UserPermission> updatedPermissions) {
        if (permissionsToUpdate != null && updatedPermissions != null && permissionsToUpdate.size() == updatedPermissions.size()) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<UserPermission>> rolePermissions = cacheManager.getAllRolePermissions();
            if (rolePermissions != null) {
                for (int i = 0; i < permissionsToUpdate.size(); i++) {
                    for (Map.Entry<Long, List<UserPermission>> userPermissionEntry : rolePermissions.entrySet()) {
                        Long roleId = userPermissionEntry.getKey();
                        List<UserPermission> userPermissions = userPermissionEntry.getValue();
                        UserPermission permissionToUpdate = permissionsToUpdate.get(i);
                        if (userPermissions.contains(permissionToUpdate)) {
                            userPermissions.remove(permissionToUpdate);
                            UserPermission updatedPermission = updatedPermissions.get(i);
                            userPermissions.add(updatedPermission);
                            String oldEntityType;
                            if ((oldEntityType = getReadPermissionType(permissionToUpdate)) != null) {
                                Map<Long, Map<String, IdFilter>> readActionFiltersByUser = cacheManager.getAllIdFiltersForUser();
                                for (Map.Entry<Long, Map<String, IdFilter>> entry : readActionFiltersByUser.entrySet()) {
                                    if (entry.getValue().containsKey(oldEntityType)) {
                                        IdFilter idFilterInfo = entry.getValue().remove(oldEntityType);
                                        if (idFilterInfo != null) {
                                            String newEntityType = ActionDetectorFactory.isReadPermission(updatedPermission) ?
                                                    ActionDetectorFactory.getReadPermissionType(updatedPermission) :
                                                    ActionDetectorFactory.getReadAllPermissionType(updatedPermission.getPermission());
                                            entry.getValue().put(newEntityType, idFilterInfo);
                                            cacheManager.setIdFiltersForUser(entry.getKey(), entry.getValue());
                                        }
                                    }
                                }
                            }
                            cacheManager.setRolePermissions(roleId, userPermissions);
                        }
                    }
                }
                UserSessionManager.getInstance().refreshRolePermissions();
            }
        }
    }

    @Override
    public void removePermissions(List<UserPermission> permissionsToDelete) {
        if (ConfigContainer.isAppInstalled() && permissionsToDelete != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<UserPermission>> rolePermissions = cacheManager.getAllRolePermissions();
            if (rolePermissions != null) {
                List<Long> updatedRoles = new ArrayList<Long>();
                for (Map.Entry<Long, List<UserPermission>> entry : rolePermissions.entrySet()) {
                    List<UserPermission> permissions = entry.getValue();
                    if (permissions != null && permissions.removeAll(permissionsToDelete)) {
                        updatedRoles.add(entry.getKey());
                    }
                }
                //update id filters
                if (!updatedRoles.isEmpty()) {
                    for (Map.Entry<Long, List<Long>> entry : cacheManager.getUserRoles().entrySet()) {
                        if (CollectionUtils.containsAny(entry.getValue(), updatedRoles)) {
                            updateIdFiltersOnRolesChange(entry.getKey(), entry.getValue());
                        }
                    }
                }
                UserSessionManager.getInstance().refreshRolePermissions();
            }
        }
    }

    @Override
    public void removePermission(UserPermission permissionToDelete) {
        if (ConfigContainer.isAppInstalled() && permissionToDelete != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<UserPermission>> rolePermissions = cacheManager.getAllRolePermissions();
            if (rolePermissions != null) {
                List<Long> updatedRoles = new ArrayList<Long>();
                for (Map.Entry<Long, List<UserPermission>> entry : rolePermissions.entrySet()) {
                    List<UserPermission> permissions = entry.getValue();
                    if (permissions != null && permissions.remove(permissionToDelete)) {
                        updatedRoles.add(entry.getKey());
                    }
                }
                //update id filters
                if (!updatedRoles.isEmpty()) {
                    for (Map.Entry<Long, List<Long>> entry : cacheManager.getUserRoles().entrySet()) {
                        if (CollectionUtils.containsAny(entry.getValue(), updatedRoles)) {
                            updateIdFiltersOnRolesChange(entry.getKey(), entry.getValue());
                        }
                    }
                }
                UserSessionManager.getInstance().refreshRolePermissions();
            }
        }
    }

    public void setUserRole(User userInfo, List<Long> roleIdList) {
        if (ConfigContainer.isAppInstalled() && userInfo != null && roleIdList != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<UserPermission>> rolePermissions = cacheManager.getAllRolePermissions();
            if (rolePermissions != null) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                Map<Long, List<Long>> userRoles = cacheManager.getUserRoles();
                userRoles.put(userInfo.getId(), roleIdList);
                cacheManager.setUserRoles(userRoles);

                //update id filters
                updateIdFiltersOnRolesChange(userInfo.getId(), roleIdList);

                sessionManager.refreshUserRoles(userInfo, roleIdList);
                sessionManager.refreshRolePermissions();
            }
        }
    }

    public void addUserRole(Long userId, Long roleId) {
        if (userId != null && roleId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, List<Long>> rolesByUser = cacheManager.getUserRoles();
            List<Long> userRoles = rolesByUser.get(userId);
            if (userRoles == null) {
                userRoles = new ArrayList<Long>();
                rolesByUser.put(userId, userRoles);
            }
            if (!userRoles.contains(roleId)) {
                userRoles.add(roleId);
                updateIdFiltersOnRolesChange(userId, rolesByUser.get(userId));
            }
            cacheManager.setUserRoles(rolesByUser);
            UserSessionManager.getInstance().addUserRole(userId, roleId, this);
        }
    }

    @Override
    public void deleteUserRole(Long userId, Long roleId) {
        if (userId != null && roleId != null) {
            UserSessionManager userSessionManager = UserSessionManager.getInstance();

            boolean deleted = false;
            CacheManager cacheManager = CacheManager.getInstance();
            //check whether role is contextual and if yes then update contextual roles cache data
            Map<Long, SecuredRecordPermissions> userContextRolePermissions = cacheManager.getAllSecuredRecordPermissions();
            for (SecuredRecordPermissions securedRecordPermissions : userContextRolePermissions.values()) {
                UserContextPermissions userRolePermissions = securedRecordPermissions.getUserContextPermissions().get(userId);
                if (userRolePermissions != null) {
                    ContextPermissions removedContextPermissions = userRolePermissions.removeContextRole(roleId);
                    if (removedContextPermissions != null) {
                        Map<String, IdFilter> userIdFilters = cacheManager.getIdFiltersForUser(userId);
                        if (userIdFilters != null) {
                            boolean updated = false;
                            for (UserPermission permission : removedContextPermissions.userPermissionsSnapshot()) {
                                if (ActionDetectorFactory.isReadPermission(permission)) {
                                    String entityType = ActionDetectorFactory.getReadPermissionType(permission);
                                    IdFilter idFilter = userIdFilters.get(entityType);
                                    if (idFilter != null) {
                                        Long entityId = Long.valueOf(permission.getEntityId());
                                        if (ArrayUtils.contains(idFilter.getGrantedIdList(), entityId)) {
                                            List<Long> grantedIdList = new ArrayList<Long>(Arrays.asList(idFilter.getGrantedIdList()));
                                            grantedIdList.remove(entityId);
                                            idFilter.setGrantedIdList(grantedIdList.toArray(new Long[grantedIdList.size()]));
                                            updated = true;
                                        }
                                    }
                                }
                            }
                            if (updated) {
                                cacheManager.setIdFiltersForUser(userId, userIdFilters);
                            }
                        }

                        userSessionManager.refreshUserContextRolePermissions(userId, getUserPermissionsBySecuredRecords(userId));
                        deleted = true;
                    }
                }
            }

            if (!deleted) {//if role was not found among contextual roles then remove usual role
                userSessionManager.removeUsualUserRole(userId, roleId, this);
                Map<Long, List<Long>> rolesByUser = cacheManager.getUserRoles();
                List<Long> userRoles = rolesByUser.get(userId);
                if (userRoles != null) {
                    userRoles.remove(roleId);
                    cacheManager.setUserRoles(rolesByUser);
                }
            }
        }
    }

    @Override
    public void deleteContextUserRoles(Map<Long, Collection<ContextPermissions>> userRoles) {
        if (userRoles != null && !userRoles.isEmpty()) {
            UserSessionManager userSessionManager = UserSessionManager.getInstance();

            CacheManager cacheManager = CacheManager.getInstance();
            //check whether role is contextual and if yes then update contextual roles cache data
            Map<Long, SecuredRecordPermissions> userContextRolePermissions = cacheManager.getAllSecuredRecordPermissions();
            for (Map.Entry<Long, Collection<ContextPermissions>> userRoleEntry : userRoles.entrySet()) {
                Long userId = userRoleEntry.getKey();
                for (SecuredRecordPermissions securedRecordPermissions : userContextRolePermissions.values()) {
                    UserContextPermissions userRolePermissions = securedRecordPermissions.getUserContextPermissions().get(userId);
                    if (userRolePermissions != null) {
                        Collection<ContextPermissions> roles = userRoleEntry.getValue();
                        if (roles != null) {
                            for (ContextPermissions role : roles) {
                                ContextPermissions removedContextPermissions = userRolePermissions.removeContextRole(role);
                                if (removedContextPermissions != null) {
                                    Map<String, IdFilter> userIdFilters = cacheManager.getIdFiltersForUser(userId);
                                    if (userIdFilters != null) {
                                        boolean updated = false;
                                        for (UserPermission permission : removedContextPermissions.userPermissionsSnapshot()) {
                                            if (ActionDetectorFactory.isReadPermission(permission)) {
                                                String entityType = ActionDetectorFactory.getReadPermissionType(permission);
                                                IdFilter idFilter = userIdFilters.get(entityType);
                                                if (idFilter != null) {
                                                    Long entityId = Long.valueOf(permission.getEntityId());
                                                    if (ArrayUtils.contains(idFilter.getGrantedIdList(), entityId)) {
                                                        List<Long> grantedIdList = new ArrayList<Long>(Arrays.asList(idFilter.getGrantedIdList()));
                                                        grantedIdList.remove(entityId);
                                                        idFilter.setGrantedIdList(grantedIdList.toArray(new Long[grantedIdList.size()]));
                                                        updated = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (updated) {
                                            cacheManager.setIdFiltersForUser(userId, userIdFilters);
                                        }
                                    }
                                    userSessionManager.refreshUserContextRolePermissions(userId, getUserPermissionsBySecuredRecords(userId));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, List<ResourceLocation>> getResourceLocations() {
        CacheManager cacheManager = CacheManager.getInstance();
        return cacheManager.getAllResourceLocations();
    }

    @Override
    public void addResourceLocation(ResourceLocation resourceLocation) {
        if (ConfigContainer.isAppInstalled() && resourceLocation != null) {
            boolean resourceLocationAdded = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<ResourceLocation>> resourceLocations = cacheManager.getAllResourceLocations();
            if (resourceLocations != null) {
                for (Map.Entry<String, List<ResourceLocation>> resourceLocationEntry : resourceLocations.entrySet()) {
                    if (resourceLocation.getLookup().startsWith(resourceLocationEntry.getKey() + ".")) {
                        resourceLocationEntry.getValue().add(resourceLocation);
                        cacheManager.setResourceLocations(resourceLocationEntry.getKey(), resourceLocationEntry.getValue());
                        resourceLocationAdded = true;
                        break;
                    }
                }
            }
            if (!resourceLocationAdded) {
                logger.error("Failed to update Firejack Platform Node cache. Resource Location was not updated.");
            }
        }
    }

    @Override
    public void updateResourceLocation(ResourceLocation resourceLocation) {
        if (ConfigContainer.isAppInstalled() && resourceLocation != null) {
            boolean resourceLocationUpdated = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<ResourceLocation>> resourceLocations = cacheManager.getAllResourceLocations();
            if (resourceLocations != null) {
                for (Map.Entry<String, List<ResourceLocation>> entry : resourceLocations.entrySet()) {
                    for (ResourceLocation rl : entry.getValue()) {
                        if (rl.getId().equals(resourceLocation.getId())) {
                            if (entry.getValue().remove(rl)) {
                                entry.getValue().add(resourceLocation);
                                cacheManager.setResourceLocations(entry.getKey(), entry.getValue());
                                resourceLocationUpdated = true;
                                break;
                            } else {
                                logger.error("Failed to update resource location element. Reason: old element was not removed.");
                            }
                        }
                    }
                }
            }
            if (!resourceLocationUpdated) {
                logger.error("Failed to update Firejack Platform Node cache. Resource Location was not updated.");
            }
        }
    }

    @Override
    public void removeResourceLocation(ResourceLocation resourceLocation) {
        if (ConfigContainer.isAppInstalled()) {
            boolean resourceLocationsRemoved = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, List<ResourceLocation>> resourceLocations = cacheManager.getAllResourceLocations();
            if (resourceLocations != null) {
                for (Map.Entry<String, List<ResourceLocation>> resourceLocationEntry : resourceLocations.entrySet()) {
                    if (resourceLocation.getLookup().startsWith(resourceLocationEntry.getKey() + ".")) {
                        if (resourceLocationEntry.getValue().remove(resourceLocation)) {
                            cacheManager.setResourceLocations(resourceLocationEntry.getKey(), resourceLocationEntry.getValue());
                            resourceLocationsRemoved = true;
                        } else {
                            logger.error("Failed to remove resource location element.");
                        }
                    }
                }
            }
            if (!resourceLocationsRemoved) {
                logger.error("Failed to update Firejack Platform Node cache. Resource Location for removal was not found.");
            }
        }
    }

    @Override
    public List<Action> getActions(String packageLookup) {
        List<Action> actions;
        if (StringUtils.isNotBlank(packageLookup)) {
            actions = CacheManager.getInstance().getActions(packageLookup);
        } else {
            actions = new LinkedList<Action>();
        }
        return actions;
    }

    @Override
    public void addAction(Action actionInfo) {
        if (actionInfo != null) {
            String packageLookup = getPackageLookup(actionInfo.getPath());
            if (StringUtils.isNotBlank(packageLookup)) {
                CacheManager cacheManager = CacheManager.getInstance();
                List<Action> actions = cacheManager.getActions(packageLookup);
                if (actions != null) {
                    actions.add(actionInfo);
                    cacheManager.setActions(packageLookup, actions);
                }
            }
        }
    }

    @Override
    public void addActions(List<Action> actions) {
        if (actions != null && !actions.isEmpty()) {
            Map<String, SortedSet<Action>> packageActions = new HashMap<String, SortedSet<Action>>();
            for (Action action : actions) {
                String packageLookup = getPackageLookup(action.getPath());
                SortedSet<Action> voList = packageActions.get(packageLookup);
                if (voList == null) {
                    voList = new TreeSet<Action>(new ActionComparator());
                    packageActions.put(packageLookup, voList);
                }
                voList.add(action);
            }
            CacheManager cacheManager = CacheManager.getInstance();
            for (Map.Entry<String, SortedSet<Action>> packageActionsEntry : packageActions.entrySet()) {

                List<Action> cachedActions = cacheManager.getActions(packageActionsEntry.getKey());
                if (cachedActions == null) {
                    cachedActions = new LinkedList<Action>();
                }
                for (Action action : packageActionsEntry.getValue()) {
                    cachedActions.add(action);
                }
                cacheManager.setActions(packageActionsEntry.getKey(), cachedActions);
            }
        }
    }

    @Override
    public void updateAction(Action actionInfo) {
        if (actionInfo != null) {
            String packageLookup = getPackageLookup(actionInfo.getPath());
            if (StringUtils.isNotBlank(packageLookup)) {
                boolean actionUpdated = false;
                CacheManager cacheManager = CacheManager.getInstance();
                List<Action> actions = cacheManager.getActions(packageLookup);
                if (actions != null) {
                    for (Action action : actions) {
                        if (action.getId().equals(actionInfo.getId())) {
                            actions.remove(action);
                            actions.add(actionInfo);
                            cacheManager.setActions(packageLookup, actions);
                            actionUpdated = true;
                            break;
                        }
                    }
                }
                if (!actionUpdated) {
                    logger.error("Failed to update Firejack Platform Node cache. Action for update [id = " + actionInfo.getId() + "] was not found.");
                }
            }
        }
    }

    @Override
    public void removeAction(Action actionInfo) {
        if (actionInfo != null) {
            String packageLookup = getPackageLookup(actionInfo.getPath());
            if (StringUtils.isNotBlank(packageLookup)) {
                boolean actionRemoved = false;
                CacheManager cacheManager = CacheManager.getInstance();
                List<Action> actions = cacheManager.getActions(packageLookup);

                if (actions != null && actions.remove(actionInfo)) {
                    List<Action> actionList = new LinkedList<Action>(actions);
                    cacheManager.setActions(packageLookup, actionList);
                    actionRemoved = true;
                }
                if (!actionRemoved) {
                    logger.error("Failed to update Firejack Platform Node cache. Action for removal was not found.");
                }
            }
        }
    }

    @Override
    public void addUserContextPermissions(Long userId, Long securedRecordId, Long roleId, String entityType, String entityId, List<String> permissionLookups) {
        if (userId != null && roleId != null && permissionLookups != null && !permissionLookups.isEmpty()) {
            boolean securedRecordIsNull = securedRecordId == null;
            CacheManager cacheManager = CacheManager.getInstance();
            Long secRecordId = securedRecordIsNull ? EMPTY_SECURED_RECORD_ID : securedRecordId;
            SecuredRecordPermissions contextPermissionsByUsers = cacheManager.getSecuredRecordPermissions(secRecordId);
            if (contextPermissionsByUsers == null) {
                contextPermissionsByUsers = new SecuredRecordPermissions(secRecordId, new HashMap<Long, UserContextPermissions>());
            }
            UserContextPermissions userPermissionsByRoles = contextPermissionsByUsers.getUserContextPermissions().get(userId);
            if (userPermissionsByRoles == null) {
                userPermissionsByRoles = new UserContextPermissions(userId);
                contextPermissionsByUsers.getUserContextPermissions().put(userId, userPermissionsByRoles);
            }
            ContextPermissions contextRolePermissions = userPermissionsByRoles.getContextPermissions(roleId);
            if (contextRolePermissions == null) {
                contextRolePermissions = new ContextPermissions(roleId, entityType, entityId);
            } else {
                logger.warn("Context role permissions for [roleId = " + roleId + ", userId = " + userId + "] already exist. Overriding the context role permissions...");
            }
            contextRolePermissions.setPermissions(permissionLookups);
            userPermissionsByRoles.putContextRolePermissions(contextRolePermissions);

            cacheManager.setSecuredRecordPermissions(secRecordId, contextPermissionsByUsers);

            //add id filters for user using specified context permission if necessary.
            addIdFiltersForContextPermissions(userId, contextRolePermissions.userPermissionsSnapshot());

            //update context permissions info in cache
            UserSessionManager userSessionManager = UserSessionManager.getInstance();
            if (securedRecordIsNull) {
                userSessionManager.addUserRole(userId, roleId, this);
            } else {
                //addSecuredRecordInheritedPermissionsForUser(userId, secRecordId, contextPermissions);

                Map<Long, Map<Long, List<UserPermission>>> userPermissionsBySecuredRecords = getUserPermissionsBySecuredRecords(userId);
                userSessionManager.refreshUserContextRolePermissions(userId, userPermissionsBySecuredRecords);
            }
        }
    }

    @Override
    public void updatePackageLevelPermissions(
            String packageLookup, Long userId, Long roleId, List<String> permissionLookupList) {
        CacheManager cacheManager = CacheManager.getInstance();
        UserContextPermissions pkgLevelPermissions =
                cacheManager.getPackageLevelUserPermissions(packageLookup, userId);
        if (pkgLevelPermissions == null) {
            pkgLevelPermissions = new UserContextPermissions();
            pkgLevelPermissions.setRolePermissions(new HashMap<Long, List<UserPermission>>());
        }
        pkgLevelPermissions.getRolePermissions().put(
                roleId, AuthorizationVOFactory.convertPermissionsEx(permissionLookupList));
        cacheManager.setPackageLevelUserPermissions(packageLookup, userId, pkgLevelPermissions);
    }

    @Override
    public void deletePackageLevelPermissions(String packageLookup, Long userId, Long roleId) {
        CacheManager cacheManager = CacheManager.getInstance();
        UserContextPermissions pkgLevelPermissions =
                cacheManager.getPackageLevelUserPermissions(packageLookup, userId);
        if (pkgLevelPermissions != null) {
            pkgLevelPermissions.getRolePermissions().remove(roleId);
            cacheManager.setPackageLevelUserPermissions(packageLookup, userId, pkgLevelPermissions);
        }
    }

    @Override
    public void addUserContextPermissions(Long userId, Long roleId, String entityType, String entityId, List<String> permissionLookupList) {
        addUserContextPermissions(userId, null, roleId, entityType, entityId, permissionLookupList);
    }

    @Override
    public Map<Long, List<UserPermission>> getUserContextPermissions(Long userId) {
        Map<Long, List<UserPermission>> result = null;
        if (userId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            SecuredRecordPermissions contextPermissionsByUsers = cacheManager.getSecuredRecordPermissions(EMPTY_SECURED_RECORD_ID);
            if (contextPermissionsByUsers != null) {
                UserContextPermissions userRolePermissions = contextPermissionsByUsers.getUserContextPermissions().get(userId);
                if (userRolePermissions != null) {
                    result = userRolePermissions.populateRolePermissions();
                }
            }
        }
        return result == null ? new HashMap<Long, List<UserPermission>>() : result;
    }

    @Override
    public Map<Long, Map<Long, List<UserPermission>>> getUserPermissionsBySecuredRecords(Long userId) {
        Map<Long, Map<Long, List<UserPermission>>> userContextPermissionsBySecuredRecords = new HashMap<Long, Map<Long, List<UserPermission>>>();
        if (userId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<Long, SecuredRecordPermissions> cp = cacheManager.getAllSecuredRecordPermissions();
            for (SecuredRecordPermissions srPermissions : cp.values()) {
                if (!EMPTY_SECURED_RECORD_ID.equals(srPermissions.getSecuredRecordId())) {
                    UserContextPermissions userSecuredRecordPermission = srPermissions.getUserContextPermissions().get(userId);
                    if (userSecuredRecordPermission != null) {
                        userContextPermissionsBySecuredRecords.put(srPermissions.getSecuredRecordId(),
                                userSecuredRecordPermission.populateRolePermissions());
                    }
                }
            }
        }
        return userContextPermissionsBySecuredRecords;
    }

    @Override
    public void refreshSecuredRecords(Map<Long, SecuredRecordNode> securedRecords) {
        if (ConfigContainer.isAppInstalled()) {
            if (securedRecords != null) {
                CacheManager.getInstance().setSecuredRecords(securedRecords);
            }
        }
    }

    @Override
    public void addSecuredRecords(List<SecuredRecordNode> securedRecords) {
        if (ConfigContainer.isAppInstalled() && securedRecords != null) {
            Map<Long, SecuredRecordNode> allSR = CacheManager.getInstance().getSecuredRecords();
            allSR = allSR == null ? new HashMap<Long, SecuredRecordNode>() : allSR;
            for (SecuredRecordNode sr : securedRecords) {
                if (sr.getSecuredRecordId() != null) {
                    if (!allSR.containsKey(sr.getSecuredRecordId())) {
                        allSR.put(sr.getSecuredRecordId(), sr);
                    }
                }
            }
            CacheManager.getInstance().setSecuredRecords(allSR);
        }
    }

    @Override
    public void addSecuredRecord(SecuredRecordNode securedRecord) {
        if (ConfigContainer.isAppInstalled() && securedRecord != null && securedRecord.getSecuredRecordId() != null) {
            Map<Long, SecuredRecordNode> allSR = CacheManager.getInstance().getSecuredRecords();
            allSR = allSR == null ? new HashMap<Long, SecuredRecordNode>() : allSR;
            if (!allSR.containsKey(securedRecord.getSecuredRecordId())) {
                allSR.put(securedRecord.getSecuredRecordId(), securedRecord);
            }
            CacheManager.getInstance().setSecuredRecords(allSR);
        }
    }

    @Override
    public void deleteSecuredRecord(Long securedRecordId) {
        if (ConfigContainer.isAppInstalled() && securedRecordId != null) {
            Map<Long, SecuredRecordNode> allSR = CacheManager.getInstance().getSecuredRecords();
            allSR = allSR == null ? new HashMap<Long, SecuredRecordNode>() : allSR;
            allSR.remove(securedRecordId);
            CacheManager.getInstance().setSecuredRecords(allSR);
        }
    }

    @Override
    public void deleteSecuredRecords(List<Long> securedRecordIdList) {
        if (ConfigContainer.isAppInstalled() && securedRecordIdList != null && !securedRecordIdList.isEmpty()) {
            Map<Long, SecuredRecordNode> allSR = CacheManager.getInstance().getSecuredRecords();
            allSR = allSR == null ? new HashMap<Long, SecuredRecordNode>() : allSR;
            for (Long securedRecordId : securedRecordIdList) {
                allSR.remove(securedRecordId);
            }
            CacheManager.getInstance().setSecuredRecords(allSR);
        }
    }

    @Override
    public Map<String, IdFilter> getFiltersByUser(Long userId) {
        Map<String, IdFilter> result;
        if (userId == null) {
            result = Collections.emptyMap();
        } else {
            result = CacheManager.getInstance().getIdFiltersForUser(userId);
            result = result == null ? Collections.<String, IdFilter>emptyMap() : result;
        }
        return result;
    }

    @Override
    public void associatePermissionWithAction(String actionLookup, Permission permission) {
        if (StringUtils.isBlank(actionLookup) || permission == null) {
            logger.error(StringUtils.isBlank(actionLookup) ?
                    "Action Lookup should not be blank." : "Permission Lookup should not be blank.");
        } else {
            String packageLookup = getPackageLookup(actionLookup);
            CacheManager cacheManager = CacheManager.getInstance();
            List<Action> packageActions = cacheManager.getActions(packageLookup);

            if (packageActions == null) {
                logger.error("Actions for considered package has not registered yet.");
            } else {
                boolean notFound = true;
                for (Action action : packageActions) {
                    if (actionLookup.equals(action.getLookup())) {
                        notFound = false;
                        List<Permission> permissions = action.getPermissions();
                        if (permissions == null) {
                            permissions = new ArrayList<Permission>();
                        }
                        if (!permissions.contains(permission)) {
                            permissions.add(permission);
                            action.setPermissions(permissions);
                            cacheManager.setActions(packageLookup, packageActions);
                            break;
                        }
                    }
                }
                if (notFound) {
                    logger.error("Target action that should be used to associate permission with was not found.");
                }
            }
        }
    }

    @Override
    public void associatePermissionWithNavigation(String navigationLookup, Permission permission) {
        if (StringUtils.isBlank(navigationLookup) || permission == null) {
            logger.error(StringUtils.isBlank(navigationLookup) ?
                    "Navigation Element Lookup should not be blank." : "Permission Lookup should not be blank.");
        } else {
            String packageLookup = getPackageLookup(navigationLookup);
            CacheManager cacheManager = CacheManager.getInstance();
            List<NavigationElement> packageNavigations = cacheManager.getNavigationList(packageLookup);
            if (packageNavigations == null) {
                logger.error("Navigation Elements for considered package has not registered yet.");
            } else {
                boolean notFound = true;
                for (NavigationElement nav : packageNavigations) {
                    if (navigationLookup.equals(nav.getLookup())) {
                        notFound = false;
                        List<Permission> permissions = nav.getPermissions();
                        if (permissions == null) {
                            permissions = new ArrayList<Permission>();
                        }
                        if (!permissions.contains(permission)) {
                            permissions.add(permission);
                            nav.setPermissions(permissions);
                            cacheManager.setNavigationList(packageLookup, packageNavigations);
                            break;
                        }
                    }
                }
                if (notFound) {
                    logger.error("Target action that should be used to associate permission with was not found.");
                }
            }
        }
    }

    @Override
    public void associatePermissionWithResourceLocation(String resourceLocationLookup, Permission permission) {
        if (StringUtils.isBlank(resourceLocationLookup) || permission == null) {
            logger.error(StringUtils.isBlank(resourceLocationLookup) ?
                    "Navigation Element Lookup should not be blank." : "Permission Lookup should not be blank.");
        } else {
            String packageLookup = getPackageLookup(resourceLocationLookup);
            CacheManager cacheManager = CacheManager.getInstance();
            List<ResourceLocation> packageResourceLocations = cacheManager.getResourceLocations(packageLookup);
            if (packageResourceLocations == null) {
                logger.error("Navigation Elements for considered package has not registered yet.");
            } else {
                boolean notFound = true;
                for (ResourceLocation resourceLocation : packageResourceLocations) {
                    if (resourceLocationLookup.equals(resourceLocation.getLookup())) {
                        notFound = false;
                        List<Permission> permissions = resourceLocation.getPermissions();
                        if (permissions == null) {
                            permissions = new ArrayList<Permission>();
                        }
                        if (!permissions.contains(permission)) {
                            permissions.add(permission);
                            resourceLocation.setPermissions(permissions);
                            cacheManager.setResourceLocations(packageLookup, packageResourceLocations);
                            break;
                        }
                    }
                }
                if (notFound) {
                    logger.error("Target action that should be used to associate permission with was not found.");
                }
            }
        }
    }

    @Override
    public void saveNewEntity(Entity entity) {
        if (entity != null) {
            List<Entity> entityList = new ArrayList<Entity>();
            entityList.add(entity);
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.setTypeWithSubclasses(entity.getLookup(), entityList);
            List<String> entityTypes = cacheManager.getEntityTypes();
            if (entityTypes == null) {
                logger.error("No entity types found in cache.");
            } else {
                entityTypes.add(entity.getLookup());
                cacheManager.setEntityTypes(entityTypes);
            }
            Boolean securityEnabled = entity.getSecurityEnabled();
            cacheManager.setEntityAsSecurityEnabled(entity.getLookup(), securityEnabled != null && securityEnabled);
        }
    }

    @Override
    public void saveEntity(Entity entity) {
        if (entity != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            List<String> entityTypes = cacheManager.getEntityTypes();
            if (entityTypes == null) {
                logger.error("No entity types found in cache.");
            } else {
                for (String typeLookup : entityTypes) {
                    List<Entity> typeWithSubclasses = cacheManager.getTypeWithSubclasses(typeLookup);
                    if (typeWithSubclasses == null) {
                        logger.error("Type [" + typeLookup + "] was not found in cache.");
                    } else {
                        for (Entity type : typeWithSubclasses) {
                            if (type.getId().equals(entity.getId())) {
                                typeWithSubclasses.remove(type);
                                typeWithSubclasses.add(entity);
                                cacheManager.setTypeWithSubclasses(typeLookup, typeWithSubclasses);
                                break;
                            }
                        }
                    }
                }
            }
            Boolean securityEnabled = entity.getSecurityEnabled();
            cacheManager.setEntityAsSecurityEnabled(entity.getLookup(), securityEnabled != null && securityEnabled);
        }
    }

    @Override
    public void updateEntityLookup(String oldLookup, String newLookup) {
        if (StringUtils.isBlank(oldLookup) || StringUtils.isBlank(newLookup)) {
            logger.warn("Lookup parameters for entity to update could not be blank.");
        } else {
            CacheManager cacheManager = CacheManager.getInstance();
            List<String> entityTypes = cacheManager.getEntityTypes();

            boolean typeNotUpdated = true;
            for (String typeLookup : entityTypes) {
                List<Entity> typeWithSubclasses = cacheManager.getTypeWithSubclasses(typeLookup);
                boolean updated = false;
                if (typeWithSubclasses == null) {
                    logger.error("Type [" + typeLookup + "] was not found in cache.");
                } else {
                    for (Entity entity : typeWithSubclasses) {
                        if (oldLookup.equals(entity.getLookup())) {
                            //we don't care about other properties of entity, take care only for lookup
                            entity.setLookup(newLookup);
                            updated = true;
                            break;
                        }
                    }
                }
                if (oldLookup.equals(typeLookup)) {
                    if (updated) {
                        cacheManager.removeTypeWithSubclasses(oldLookup);
                        cacheManager.setTypeWithSubclasses(newLookup, typeWithSubclasses);
                        typeNotUpdated = false;
                    } else {
                        logger.warn("Entity Lookup was not updated for type = [" + oldLookup + "]");
                    }
                } else if (updated) {
                    cacheManager.setTypeWithSubclasses(oldLookup, typeWithSubclasses);
                }
            }
            if (typeNotUpdated) {
                logger.error("Type [" + oldLookup + "] was not found in cache.");
                List<Entity> typeWithSubclasses = cacheManager.getTypeWithSubclasses(oldLookup);
                if (typeWithSubclasses == null) {
                    logger.error("Cached type was not found.");
                } else {
                    for (Entity entity : typeWithSubclasses) {
                        if (oldLookup.equals(entity.getLookup())) {
                            //we don't care about other properties of entity, take care only for lookup
                            entity.setLookup(newLookup);
                            break;
                        }
                    }
                    cacheManager.setTypeWithSubclasses(newLookup, typeWithSubclasses);
                }
            }

            entityTypes.remove(oldLookup);
            entityTypes.add(newLookup);
            cacheManager.setEntityTypes(entityTypes);
        }
    }

    @Override
    public void updateExtendedEntity(String entityToUpdate, String newExtendedEntity) {
        if (StringUtils.isNotBlank(entityToUpdate)) {
            CacheManager cacheManager = CacheManager.getInstance();
            List<String> entityTypes = cacheManager.getEntityTypes();
            if (entityTypes == null) {
                logger.error("No entity types found in cache.");
            } else {
                Map<String, List<Entity>> typesIncludesOldSuperType = new HashMap<String, List<Entity>>();
                Map<String, List<Entity>> typesIncludesNewSuperType = new HashMap<String, List<Entity>>();
                List<Entity> oldExtendedTypeEntities = null;
                Entity entityToChange = null;
                List<Entity> newExtendedTypeEntities = null;
                for (String type : entityTypes) {
                    List<Entity> typeWithSubclasses = cacheManager.getTypeWithSubclasses(type);
                    if (type.equals(entityToUpdate)) {
                        oldExtendedTypeEntities = typeWithSubclasses;
                        if (oldExtendedTypeEntities == null) {
                            break;
                        } else {
                            for (Entity entity : oldExtendedTypeEntities) {
                                if (entity.getLookup().equals(entityToUpdate)) {
                                    entityToChange = entity;
                                    break;
                                }
                            }
                        }
                    } else if (type.equals(newExtendedEntity)) {
                        newExtendedTypeEntities = typeWithSubclasses;
                        if (newExtendedTypeEntities == null) {
                            break;
                        }
                    } else if (typeWithSubclasses != null) {
                        boolean oldExtendedEntityFound = false;
                        boolean newExtendedEntityFound = false;
                        for (Entity entity : typeWithSubclasses) {
                            if (entity.getLookup().equals(entityToUpdate)) {
                                oldExtendedEntityFound = true;
                            } else if (entity.getLookup().equals(newExtendedEntity)) {
                                newExtendedEntityFound = true;
                            }
                            if (oldExtendedEntityFound && newExtendedEntityFound) {
                                break;
                            }
                        }
                        if (oldExtendedEntityFound) {
                            typesIncludesOldSuperType.put(type, typeWithSubclasses);
                        }
                        if (newExtendedEntityFound) {
                            typesIncludesNewSuperType.put(type, typeWithSubclasses);
                        }
                    }
                }
                if (oldExtendedTypeEntities == null || entityToChange == null) {
                    logger.error("Could not find old extended entity.");
                } else if (newExtendedTypeEntities == null) {
                    logger.error("Could not find changed extended entity.");
                } else {
                    for (Map.Entry<String, List<Entity>> entry : typesIncludesOldSuperType.entrySet()) {
                        entry.getValue().removeAll(oldExtendedTypeEntities);
                    }
                    for (Map.Entry<String, List<Entity>> entry : typesIncludesNewSuperType.entrySet()) {
                        entry.getValue().addAll(newExtendedTypeEntities);
                        if (!typesIncludesOldSuperType.containsKey(entry.getKey())) {
                            typesIncludesOldSuperType.put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (Map.Entry<String, List<Entity>> entry : typesIncludesOldSuperType.entrySet()) {
                        cacheManager.setTypeWithSubclasses(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void deleteEntityByLookup(String entityLookup) {
        if (StringUtils.isNotBlank(entityLookup)) {
            CacheManager cacheManager = CacheManager.getInstance();
            List<String> entityTypes = cacheManager.getEntityTypes();
            if (entityTypes == null) {
                logger.error("No entity types found in cache.");
            } else {
                boolean typeNotRemoved = true;
                for (String typeLookup : entityTypes) {
                    if (entityLookup.equalsIgnoreCase(typeLookup)) {
                        cacheManager.removeTypeWithSubclasses(typeLookup);
                        typeNotRemoved = false;
                    } else {
                        List<Entity> typeWithSubclasses = cacheManager.getTypeWithSubclasses(typeLookup);
                        if (typeWithSubclasses != null) {
                            for (Entity entity : typeWithSubclasses) {
                                if (entityLookup.equals(entity.getLookup())) {
                                    typeWithSubclasses.remove(entity);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (typeNotRemoved) {
                    logger.warn("Type [" + entityLookup + "] was not found in cache.");
                    cacheManager.removeTypeWithSubclasses(entityLookup);
                }
                entityTypes.remove(entityLookup);
                cacheManager.setEntityTypes(entityTypes);
            }
        }
    }

    private Map<String, IdFilter> getUserIdFilters(Long userId) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, IdFilter> userFilters = cacheManager.getIdFiltersForUser(userId);
        if (userFilters == null || userFilters.getClass().equals(Collections.emptyMap().getClass())) {
            userFilters = new HashMap<String, IdFilter>();
            cacheManager.setIdFiltersForUser(userId, userFilters);
        }
        return userFilters;
    }

    private void updateIdFiltersOnRolesChange(Long userId, List<Long> roleIdList) {
        Set<String> newTypes = new HashSet<String>();
        CacheManager cacheManager = CacheManager.getInstance();
        for (Long roleId : roleIdList) {
            List<UserPermission> userPermissions = cacheManager.getRolePermissions(roleId);
            if (userPermissions != null) {
                for (UserPermission permission : userPermissions) {
                    String entityType;
                    if ((entityType = getReadPermissionType(permission)) != null) {
                        newTypes.add(entityType);
                    }
                }
            }
        }
        Map<String, IdFilter> userFilters = getUserIdFilters(userId);
        boolean changed = false;
        for (Map.Entry<String, IdFilter> entry : userFilters.entrySet()) {
            if (newTypes.contains(entry.getKey())) {
                if (!entry.getValue().isGlobalPermissionGranted()) {
                    entry.getValue().setGlobalPermissionGranted(true);
                    changed = true;
                }
            } else if (entry.getValue().isGlobalPermissionGranted()) {
                entry.getValue().setGlobalPermissionGranted(false);
                changed = true;
            }
        }
        for (String type : newTypes) {
            if (!userFilters.keySet().contains(type)) {
                IdFilter filter = new IdFilter();
                filter.setGlobalPermissionGranted(true);
                userFilters.put(type, filter);
                changed = true;
            }
        }
        if (changed) {
            CacheManager.getInstance().setIdFiltersForUser(userId, userFilters);
        }
    }

    private String getReadPermissionType(UserPermission userPermission) {
        return userPermission == null ? null : getReadPermissionType(userPermission.getPermission());
    }

    private String getReadPermissionType(String userPermission) {
        String entityType;
        if (ActionDetectorFactory.isReadPermission(userPermission) ||
                ActionDetectorFactory.isReadAllPermission(userPermission)) {
            entityType = ActionDetectorFactory.isReadPermission(userPermission) ?
                    ActionDetectorFactory.getReadPermissionType(userPermission) :
                    ActionDetectorFactory.getReadAllPermissionType(userPermission);
        } else {
            entityType = null;
        }
        return entityType;
    }

    private void addIdFiltersForContextPermissions(Long userId, List<UserPermission> contextPermissions) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, IdFilter> idFilters = null;
        for (UserPermission contextPermission : contextPermissions) {
            if (ActionDetectorFactory.isReadPermissionForType(contextPermission)) {
                idFilters = cacheManager.getIdFiltersForUser(userId);
                if (idFilters == null) {
                    idFilters = new HashMap<String, IdFilter>();
                }
                String entityType = contextPermission.getEntityType();
                IdFilter idFilter = idFilters.get(entityType);
                if (idFilter == null) {
                    idFilter = new IdFilter();
                    idFilters.put(entityType, idFilter);
                }
                idFilter.grantId(Long.parseLong(contextPermission.getEntityId()));
            }
        }
        if (idFilters != null) {
            cacheManager.setIdFiltersForUser(userId, idFilters);
        }
    }

    private Map<Long, UserContextPermissions> getContextPermissionsByUsers(List<UserRoleModel> userRoles) {
        //populate map of available UserPermissions by user for considered secured record
        Map<Long, UserContextPermissions> contextPermissionsByUsers = new HashMap<Long, UserContextPermissions>();
        for (UserRoleModel userRole : userRoles) {
            Long userId = userRole.getUser().getId();
            UserContextPermissions userPermissions = contextPermissionsByUsers.get(userId);
            if (userPermissions == null) {
                userPermissions = new UserContextPermissions(userId);
                contextPermissionsByUsers.put(userId, userPermissions);
            }

            ContextPermissions contextPermissions = new ContextPermissions(userRole.getRole().getId(), userRole.getType(), userRole.getInternalId().toString());
            contextPermissions.setPermissions(AuthorizationVOFactory.retrieveLookupList(userRole.getRole().getPermissions()));
            userPermissions.putContextRolePermissions(contextPermissions);
        }
        return contextPermissionsByUsers;
    }

    private String getPackageLookup(String actionPath) {
        String[] actionPathEntries = actionPath.split("\\.");
        if (actionPathEntries == null || actionPathEntries.length < 3) {
            throw new IllegalStateException("Failed to process lookup of action's package: " + actionPath);
        }
        return StringUtils.join(new String[]{actionPathEntries[0], actionPathEntries[1], actionPathEntries[2]}, ".");
    }

    private Map<Long, Map<String, IdFilter>> populateReadActionFiltersInfo(//todo: check
                                                                               Map<Long, SecuredRecordPermissions> securedRecordPermissions,
                                                                               Map<Long, List<UserPermission>> permissionsByRoles, Map<Long, List<Long>> userRoles) {
        Map<Long, Map<String, IdFilter>> filtersByUser = new HashMap<Long, Map<String, IdFilter>>();

        //populate id-filters with global permissions first
        for (Map.Entry<Long, List<UserPermission>> userPermissionsByRole : permissionsByRoles.entrySet()) {
            Long roleId = userPermissionsByRole.getKey();
            for (UserPermission userPermission : userPermissionsByRole.getValue()) {
                if (userPermission.getPermission().endsWith(".read-all")) {
                    String entityType = userPermission.getPermission().substring(0,
                            userPermission.getPermission().length() - ".read-all".length());

                    for (Map.Entry<Long, List<Long>> userRolesEntry : userRoles.entrySet()) {
                        Long userId = userRolesEntry.getKey();
                        List<Long> roleIdList = userRolesEntry.getValue();
                        if (roleIdList.contains(roleId)) {
                            IdFilter filterInfo;
                            Map<String, IdFilter> userFiltersByTypes = filtersByUser.get(userId);
                            if (userFiltersByTypes == null) {
                                userFiltersByTypes = new HashMap<String, IdFilter>();
                                filtersByUser.put(userId, userFiltersByTypes);
                                filterInfo = null;
                            } else {
                                filterInfo = userFiltersByTypes.get(entityType);
                            }
                            if (filterInfo == null) {
                                filterInfo = new IdFilter();
                                userFiltersByTypes.put(entityType, filterInfo);
                            }
                            filterInfo.setGlobalPermissionGranted(Boolean.TRUE);//todo:check
                            break;
                        }
                    }
                }
            }
        }
        for (SecuredRecordPermissions contextRolePermissionsByUser : securedRecordPermissions.values()) {
            for (Map.Entry<Long, UserContextPermissions> contextRolePermissionsByUserEntry : contextRolePermissionsByUser.getUserContextPermissions().entrySet()) {

                Long userId = contextRolePermissionsByUserEntry.getKey();

                Map<String, IdFilter> filtersByEntityType = filtersByUser.get(userId);
                if (filtersByEntityType == null) {
                    filtersByEntityType = new HashMap<String, IdFilter>();
                    filtersByUser.put(userId, filtersByEntityType);
                }

                UserContextPermissions userContextPermissions = contextRolePermissionsByUserEntry.getValue();
                for (List<UserPermission> rolePermissions : userContextPermissions.populateRolePermissions().values()) {
                    for (UserPermission userPermission : rolePermissions) {
                        //if (userPermission.getPermission().endsWith(".read")) {
                        if (ActionDetectorFactory.isReadPermissionForType(userPermission)) {
                            IdFilter idFilterInfo = filtersByEntityType.get(userPermission.getEntityType());
                            if (idFilterInfo == null) {
                                idFilterInfo = new IdFilter();
                                filtersByEntityType.put(userPermission.getEntityType(), idFilterInfo);
                            }
                            if (!idFilterInfo.isGlobalPermissionGranted()) {
                                Long id = Long.valueOf(userPermission.getEntityId());
                                idFilterInfo.grantId(id);
                            }//else filter already exist and has filter.setAll(Boolean.TRUE), so context roles do not considered
                            //todo: process DENY version
                        }
                    }
                }
            }
        }
        return filtersByUser;
    }

    private Map<Long, SecuredRecordPermissions> retrieveSecuredRecordPermissions() {
        //process context user permissions
        Map<SecuredRecordModel, List<UserRoleModel>> securedRecordContextRoles = userRoleStore.findAllContextRolesBySecuredRecords();
        Map<Long, SecuredRecordPermissions> securedRecordPermissions = new HashMap<Long, SecuredRecordPermissions>();
        if (securedRecordContextRoles != null) {
            for (Map.Entry<SecuredRecordModel, List<UserRoleModel>> securedRecordEntry : securedRecordContextRoles.entrySet()) {
                Long securedRecordId = securedRecordEntry.getKey().getId();

                /*Map<Long, UserContextPermissions> contextPermissionsByUsers = getContextPermissionsByUsers(
                                            securedRecordEntry.getValue(), srPathsHelper, securedRecordContextRoles);*/
                Map<Long, UserContextPermissions> contextPermissionsByUsers = getContextPermissionsByUsers(securedRecordEntry.getValue());
                securedRecordPermissions.put(securedRecordId, new SecuredRecordPermissions(securedRecordId, contextPermissionsByUsers));
            }
        }
        //if contextual roles not bound to any secured record were found, then put them into store and assign them to fake secured record id EMPTY_SECURED_RECORD_ID
        List<UserRoleModel> usualContextRoles = userRoleStore.findAllContextRolesNotBoundToSecuredRecord();
        if (usualContextRoles != null) {
            /*Map<Long, UserContextPermissions> contextPermissionsByUsersEx = getContextPermissionsByUsers(usualContextRoles, null, null);*/
            Map<Long, UserContextPermissions> contextPermissionsByUsersEx = getContextPermissionsByUsers(usualContextRoles);
            securedRecordPermissions.put(EMPTY_SECURED_RECORD_ID,
                    new SecuredRecordPermissions(EMPTY_SECURED_RECORD_ID, contextPermissionsByUsersEx));
        }
        return securedRecordPermissions;
    }

    private Map<Long, List<UserPermission>> retrieveGlobalPermissions(List<RoleModel> roles) {
        List<Long> globalRoleIdList = new ArrayList<Long>();
        for (RoleModel role : roles) {
            if (role.isGlobal()) {
                globalRoleIdList.add(role.getId());
            }
        }
        Map<Long, List<PermissionModel>> permissionsByRoleMap = permissionStore.findRolePermissions(globalRoleIdList);
        Map<Long, List<UserPermission>> permissionsByRoles = new HashMap<Long, List<UserPermission>>();
        for (Map.Entry<Long, List<PermissionModel>> rolePermissions : permissionsByRoleMap.entrySet()) {
            List<PermissionModel> permissions = rolePermissions.getValue();
            List<UserPermission> permissionList = new LinkedList<UserPermission>();
            for (PermissionModel permission : permissions) {
                permissionList.add(new UserPermission(permission.getLookup()));
            }
            permissionsByRoles.put(rolePermissions.getKey(), permissionList);
        }
        return permissionsByRoles;
    }

    private Map<String, PackageModel> retrievePackageByLookup() {
        //load package lookup list
        List<PackageModel> packageList = packageStore.findAll();
        Map<String, PackageModel> pkgByLookup = new HashMap<String, PackageModel>();
        if (packageList != null) {
            for (PackageModel p : packageList) {
                pkgByLookup.put(p.getLookup(), p);
            }
        }
        return pkgByLookup;
    }

    private Map<String, List<NavigationElement>> retrieveNavigationElementsByPackage(List<String> packageLookupList) {
        //process navigation elements
        Map<String, List<NavigationElement>> packageNavigationElements = new HashMap<String, List<NavigationElement>>();
        for (String packageLookup : packageLookupList) {
            List<NavigationElementModel> navElements = navigationElementStore.findAllWithPermissions(packageLookup);
            List<NavigationElement> navigationList = this.modelFactory.convertTo(NavigationElement.class, navElements);
            packageNavigationElements.put(packageLookup, navigationList);
        }
        return packageNavigationElements;
    }

    private Map<String, SortedSet<Action>> retrieveActionsByPackage() {
        //process actions
        Map<String, List<ActionModel>> actionList = actionStore.findAllWithPermissionsByPackage();
        Map<String, SortedSet<Action>> actionsMap = new HashMap<String, SortedSet<Action>>();
        for (Map.Entry<String, List<ActionModel>> packageActions : actionList.entrySet()) {
            if (packageActions.getValue() != null) {
                SortedSet<Action> actionSet = new TreeSet<Action>(new ActionComparator());
                for (ActionModel action : packageActions.getValue()) {
                    Action actionInfo = modelFactory.convertTo(Action.class, action);
                    actionSet.add(actionInfo);
                }
                actionsMap.put(packageActions.getKey(), actionSet);
            }
        }
        return actionsMap;
    }

    private Map<String, List<ResourceLocation>> retrieveResourceLocationsByPackage(List<String> packageLookupList) {
        //process resource locations
        Map<String, List<ResourceLocation>> resourceLocations = new HashMap<String, List<ResourceLocation>>();
        for (String packageLookup : packageLookupList) {
            List<ResourceLocationModel> resourceLocationList = resourceLocationStore.findAllWithPermissions(packageLookup);
            List<ResourceLocation> maskedResourceVOList = modelFactory.convertTo(ResourceLocation.class, resourceLocationList);
            resourceLocations.put(packageLookup, maskedResourceVOList);
        }
        return resourceLocations;
    }

    private Map<Long, SecuredRecordNode> retrieveSecuredRecords() {
        List<SecuredRecordModel> securedRecordList = securedRecordStore.findAllWithLoadedRegistryNode();
        return AuthorizationVOFactory.convertSecuredRecords(securedRecordList);
    }

    private Map<String, List<Entity>> retrieveEntityUpHierarchies() {
        Map<String, List<EntityModel>> entitiesUpHierarchies = entityStore.findAllEntitiesUpHierarchies();
        Map<String, List<Entity>> entitiesMap = new HashMap<String, List<Entity>>();
        for (Map.Entry<String, List<EntityModel>> entry : entitiesUpHierarchies.entrySet()) {
            entitiesMap.put(entry.getKey(), this.modelFactory.convertTo(Entity.class, entry.getValue()));
        }
        return entitiesMap;
    }

    private Map<String, Map<Long, UserContextPermissions>> retrievePackageLevelUserPermissions() {
        Map<String, List<UserRoleModel>> userRolesByPackage = userRoleStore.findAllPackageLevelUserRoles();
        Map<String, Map<Long, UserContextPermissions>> result = new HashMap<String, Map<Long, UserContextPermissions>>();
        if (userRolesByPackage != null && !userRolesByPackage.isEmpty()) {
            for (Map.Entry<String, List<UserRoleModel>> entry : userRolesByPackage.entrySet()) {
                String packageLookup = entry.getKey();
                List<UserRoleModel> userRoles = entry.getValue();
                Map<Long, UserContextPermissions> userPermissions = new HashMap<Long, UserContextPermissions>();
                for (UserRoleModel userRole : userRoles) {
                    Long userId = userRole.getUser().getId();
                    UserContextPermissions userContextPermissions = userPermissions.get(userId);
                    if (userContextPermissions == null) {
                        userContextPermissions = new UserContextPermissions();
                        userContextPermissions.setRolePermissions(new HashMap<Long, List<UserPermission>>());
                        userPermissions.put(userId, userContextPermissions);
                    }
                    Long roleId = userRole.getRole().getId();
                    List<UserPermission> rolePermissions =
                            AuthorizationVOFactory.convertPermissions(userRole.getRole().getPermissions());
                    userContextPermissions.getRolePermissions().put(roleId, rolePermissions);
                }
                result.put(packageLookup, userPermissions);
            }
        }
        return result;
    }

}
