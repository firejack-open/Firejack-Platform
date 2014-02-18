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

package net.firejack.platform.web.security;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.AuthorityServiceProxy;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNodePath;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.CustomPK;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.utils.ModelRepository;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.sr.CachedSecuredRecordDataLoader;
import net.firejack.platform.web.security.sr.ISecuredRecordDataLoader;
import org.apache.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


@SuppressWarnings({"unused", "unchecked"})
public class InstantSecurity {

    private static final Logger logger = Logger.getLogger(InstantSecurity.class);

    private static final String ROLE_OWNER = "owner";
    private static final String ROLE_CONTRIBUTOR = "contributor";
    private static final String ROLE_VIEWER = "viewer";

    private static final String PERMISSION_CREATE = "create";
    private static final String PERMISSION_UPDATE = "update";
    private static final String PERMISSION_DELETE = "delete";
    private static final String PERMISSION_READ = "read";
    private static final String PERMISSION_READ_ALL = "read-all";
    private static final String PERMISSION_SEARCH = "search";
    private static final String PERMISSION_ADVANCED_SEARCH = "advanced-search";

    private static final Map<String, String[]> ROLE_PERMISSIONS = new HashMap<String, String[]>();
    static {
        ROLE_PERMISSIONS.put(ROLE_OWNER, new String[]{PERMISSION_CREATE, PERMISSION_UPDATE, PERMISSION_DELETE, PERMISSION_READ, PERMISSION_READ_ALL, PERMISSION_SEARCH, PERMISSION_ADVANCED_SEARCH});
        ROLE_PERMISSIONS.put(ROLE_CONTRIBUTOR, new String[]{PERMISSION_CREATE, PERMISSION_UPDATE, PERMISSION_READ, PERMISSION_READ_ALL, PERMISSION_SEARCH, PERMISSION_ADVANCED_SEARCH});
        ROLE_PERMISSIONS.put(ROLE_VIEWER, new String[]{PERMISSION_READ, PERMISSION_READ_ALL, PERMISSION_SEARCH, PERMISSION_ADVANCED_SEARCH});
    }
    private static String PKG_ADMIN_ROLE_LOOKUP;

    public static boolean securityEnabledForType(String typeLookup) {
        if (StringUtils.isBlank(typeLookup)) {
            return false;
        } else {
            Boolean value = CacheManager.getInstance().checkIfEntitySecurityEnabled(typeLookup);
            return value == null ? false : value;
        }
    }

    public static void setDefaultInstantPermissionsForNewObject(String typeLookup, Long pk, CustomPK customPK) {
        CacheManager cacheManager = CacheManager.getInstance();
        Boolean securityEnabledForEntity = cacheManager.checkIfEntitySecurityEnabled(typeLookup);
        if (securityEnabledForEntity != null && securityEnabledForEntity && (pk != null || customPK != null)) {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                logger.warn("Application tries to set context permissions under guest user.");
            } else {
                User currentUser = new User();
                currentUser.setId(context.getPrincipal().getUserInfoProvider().getId());
                UserRole owner = populateUserRole(currentUser, typeLookup, ROLE_OWNER);
                UserRole contributor = populateUserRole(typeLookup, ROLE_CONTRIBUTOR);
                UserRole viewer = populateUserRole(typeLookup, ROLE_VIEWER);
                owner.setTypeLookup(typeLookup);
                if (pk != null) {
                    owner.setModelId(pk);
                    contributor.setModelId(pk);
                    viewer.setModelId(pk);
                } else {
                    String complexPKSerialized = customPK.toString();
                    owner.setComplexPK(complexPKSerialized);
                    contributor.setComplexPK(complexPKSerialized);
                    viewer.setComplexPK(complexPKSerialized);
                }

                List<UserRole> defaultRoles = new ArrayList<UserRole>();
                defaultRoles.add(owner);
                defaultRoles.add(contributor);
                defaultRoles.add(viewer);
                AuthorityServiceProxy authorityServiceProxy = (AuthorityServiceProxy) OPFEngine.AuthorityService;
                ServiceResponse<UserRole> response = authorityServiceProxy.saveContextUserRolesAsSystem(defaultRoles, Boolean.TRUE);
                if (response.isSuccess()) {
                    logger.info("Default context roles were saved successfully.");
                } else {
                    logger.warn("Failed to save default context permissions for object. Reason: " + response.getMessage());
                }
            }
        }
    }

    public static void forceDefaultPermissionsCreation(String typeLookup, Long securedRecordId, Long pk) {
        if (StringUtils.isBlank(typeLookup) || pk == null) {
            logger.warn("Failed to create default permissions. Reason: required arguments are blank.");
        } else {
            List<UserRole> defaultRoles = prepareDefaultRoles(typeLookup, securedRecordId);
            if (defaultRoles != null) {
                UserRole ownerRole = defaultRoles.get(0);
                ownerRole.setModelId(pk);
                createDefaultRoles(defaultRoles);
            }
        }
    }

    public static void forceDefaultPermissionsCreation(String typeLookup, Long securedRecordId, String pk) {
        if (StringUtils.isBlank(typeLookup) || StringUtils.isBlank(pk)) {
            logger.warn("Failed to create default permissions. Reason: required arguments are blank.");
        } else {
            List<UserRole> defaultRoles = prepareDefaultRoles(typeLookup, securedRecordId);
            if (defaultRoles != null) {
                UserRole ownerRole = defaultRoles.get(0);
                ownerRole.setComplexPK(pk);
                createDefaultRoles(defaultRoles);
            }
        }
    }

    public static void forceDefaultPermissionsCreation(String typeLookup, Long securedRecordId, CustomPK pk) {
        String serialiezedCustomPK;
        if (StringUtils.isBlank(typeLookup) || pk == null || (serialiezedCustomPK = pk.toString()) == null) {
            logger.warn("Failed to create default permissions. Reason: required arguments are blank.");
        } else {
            List<UserRole> defaultRoles = prepareDefaultRoles(typeLookup, securedRecordId);
            if (defaultRoles != null) {
                UserRole ownerRole = defaultRoles.get(0);
                ownerRole.setComplexPK(serialiezedCustomPK);
                createDefaultRoles(defaultRoles);
            }
        }
    }

    public static void releaseAllContextPermissions(String typeLookup, Long pk, CustomPK customPK) {
        CacheManager cacheManager = CacheManager.getInstance();
        Boolean securityEnabledForEntity = cacheManager.checkIfEntitySecurityEnabled(typeLookup);
        if (securityEnabledForEntity != null && securityEnabledForEntity &&
                StringUtils.isNotBlank(typeLookup) && (pk != null || customPK != null)) {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                logger.warn("Guest user tries to release context permissions.");
            } else {
                User currentUser = new User();
                currentUser.setId(context.getPrincipal().getUserInfoProvider().getId());
                UserRole userRolePattern = new UserRole();
                userRolePattern.setTypeLookup(typeLookup);
                if (pk == null) {
                    String complexPKSerialized = customPK.toString();
                    userRolePattern.setComplexPK(complexPKSerialized);
                } else {
                    userRolePattern.setModelId(pk);
                }

                AuthorityServiceProxy authorityServiceProxy = (AuthorityServiceProxy) OPFEngine.AuthorityService;
                ServiceResponse response = authorityServiceProxy.releaseContextUserRolesByPatternAsSystem(userRolePattern);
                if (response.isSuccess()) {
                    logger.info("Context roles were successfully released by specified pattern.");
                } else {
                    logger.warn("Failed to release context roles by specified pattern. Reason: " + response.getMessage());
                }
            }
        }
    }

    public static void forceReleaseAllContextPermissions(String typeLookup, Long pk) {
        UserRole userRolePattern = prepareForRelease(typeLookup);
        if (userRolePattern != null) {
            userRolePattern.setModelId(pk);
            releaseByPattern(userRolePattern);
        }
    }

    public static void forceReleaseAllContextPermissions(String typeLookup, String pk) {
        UserRole userRolePattern = prepareForRelease(typeLookup);
        if (userRolePattern != null) {
            userRolePattern.setComplexPK(pk);
            releaseByPattern(userRolePattern);
        }
    }

    public static void forceReleaseAllContextPermissions(String typeLookup, CustomPK pk) {
        UserRole userRolePattern = prepareForRelease(typeLookup);
        if (userRolePattern != null) {
            userRolePattern.setComplexPK(pk.toString());
            releaseByPattern(userRolePattern);
        }
    }

    private static UserRole prepareForRelease(String typeLookup) {
        OPFContext context;
        try {
            context = OPFContext.getContext();
        } catch (ContextLookupException e) {
            context = null;
        }
        UserRole userRolePattern;
        if (context == null || context.getPrincipal().isGuestPrincipal()) {
            logger.warn("Guest user tries to release context permissions.");
            userRolePattern = null;
        } else {
            User currentUser = new User();
            currentUser.setId(context.getPrincipal().getUserInfoProvider().getId());
            userRolePattern = new UserRole();
            userRolePattern.setTypeLookup(typeLookup);
        }
        return userRolePattern;
    }

    private static void releaseByPattern(UserRole userRolePattern) {
        AuthorityServiceProxy authorityServiceProxy = (AuthorityServiceProxy) OPFEngine.AuthorityService;
        ServiceResponse response = authorityServiceProxy.releaseContextUserRolesByPatternAsSystem(userRolePattern);
        if (response.isSuccess()) {
            logger.info("Context roles were successfully released by specified pattern.");
        } else {
            logger.warn("Failed to release context roles by specified pattern. Reason: " + response.getMessage());
        }
    }

    public static void releaseContextPermissions(String typeLookup, Long pk, CustomPK customPK, Long userId, String... roleNames) {
        CacheManager cacheManager = CacheManager.getInstance();
        Boolean securityEnabledForEntity = cacheManager.checkIfEntitySecurityEnabled(typeLookup);
        if (securityEnabledForEntity != null && securityEnabledForEntity &&
                StringUtils.isNotBlank(typeLookup) && (pk != null || customPK != null) && roleNames.length > 0) {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                logger.warn("Guest user tries to release context permissions.");
            } else {
                User user = new User();
                user.setId(userId);

                List<UserRole> userRoleList = new ArrayList<UserRole>();
                for (String roleName : roleNames) {
                    UserRole userRole = populatePlainUserRole(user, typeLookup, roleName);
                    if (pk == null) {
                        userRole.setComplexPK(customPK.toString());
                    } else {
                        userRole.setModelId(pk);
                    }
                    userRoleList.add(userRole);
                }

                AuthorityServiceProxy authorityServiceProxy = (AuthorityServiceProxy) OPFEngine.AuthorityService;
                ServiceResponse response = authorityServiceProxy.releaseContextUserRolesAsSystem(userRoleList);
                if (response.isSuccess()) {
                    logger.info("Context roles were released successfully.");
                } else {
                    logger.warn("Failed to release specified context roles for object. Reason: " + response.getMessage());
                }
            }
        }
    }

    private static ISecuredRecordDataLoader securedRecordDataLoader;

    public static ISecuredRecordDataLoader getSecuredRecordDataLoader() {
        if (securedRecordDataLoader == null) {
            securedRecordDataLoader = new CachedSecuredRecordDataLoader();
        }
        return securedRecordDataLoader;
    }

    public static Object[] getParentObjectInfo(AbstractDTO source, Action currentAction) {
        AbstractDTO parentObject = InstantSecurity.getParentObject(source);
        String parentType = null;
        Long parentId = null;
        if (parentObject != null) {
            parentId = ClassUtils.getPropertyValueWithSilence(parentObject, "id", Long.class);
            if (parentId != null) {
                parentType = InstantSecurity.getParentType(currentAction);
            }
        }
        Object[] type;
        if (parentId == null || parentType == null) {
            type = null;
        } else {
            type = new Object[]{parentId, parentType};
        }
        return type;
    }

    public static AbstractDTO getParentObject(AbstractDTO requestData) {
        PropertyDescriptor[] descriptors = ClassUtils.getPropertyDescriptors(requestData.getClass());
        PropertyDescriptor parentPropertyDescriptor = null;
        for (PropertyDescriptor descriptor : descriptors) {
            if ("parent".equals(descriptor.getName())) {
                parentPropertyDescriptor = descriptor;
                break;
            }
        }
        AbstractDTO parentObject;
        if (parentPropertyDescriptor == null) {
            parentObject = null;
        } else {
            try {
                parentObject = (AbstractDTO) parentPropertyDescriptor.getReadMethod().invoke(requestData);
            } catch (IllegalAccessException e) {
                parentObject = null;
            } catch (InvocationTargetException e) {
                parentObject = null;
            }
        }
        return parentObject;
    }

    public static String getParentType(Action currentAction) {
        ServiceResponse<Entity> response = OPFEngine.RegistryService
                .readParentEntityTypesByEntityLookup(currentAction.getPath());
        String parentType;
        if (response.isSuccess()) {
            List<Entity> types = response.getData();
            if (types == null || types.isEmpty()) {
                parentType = null;
                logger.warn("Parent type was not found. Message from server: " + response.getMessage());
            } else if (types.size() == 1) {
                Entity parentEntity = types.get(0);
                parentType = parentEntity.getLookup();
            } else {
                parentType = null;
                logger.warn("Ambiguous information about parent type - more than one parent type was returned from server [count = " + types.size() + "]");
            }
        } else {
            parentType = null;
            logger.warn("Failed to read parent type for entity. Server returned failure response. Reason: " +
                    response.getMessage());
        }
        return parentType;
    }

    public static SpecifiedIdsFilter getFilter(Long parentSecuredRecordId, String type) {
        SpecifiedIdsFilter<Long> filter;
        OPFContext context = OPFContext.getContext();
        if (context.getPrincipal().isGuestPrincipal()) {
            filter = null;
        } else if (parentSecuredRecordId == null) {
            filter = context.findSpecifiedIdsFilterByType(type);
        } else {
            Map<Long, SecuredRecordNode> srMap = getSecuredRecordDataLoader().loadSecuredRecords();
            SecuredRecordNode parentSecuredRecord = srMap.get(parentSecuredRecordId);
            filter = null;
            if (parentSecuredRecord != null) {
                Long userId = context.getPrincipal().getUserInfoProvider().getId();
                Map<Long, List<UserPermission>> srPermissions =
                        getSecuredRecordDataLoader().loadSecuredRecordContextPermissions(userId);
                if (srPermissions != null) {
                    if (hasReadPermissionsForType(parentSecuredRecordId, type, srPermissions)) {
                        filter = new SpecifiedIdsFilter<Long>();
                        filter.setAll(Boolean.TRUE);
                    } else if (parentSecuredRecord.getNodePaths() != null) {
                        for (SecuredRecordNodePath srPath : parentSecuredRecord.getNodePaths()) {
                            boolean found = false;
                            if (srPath.getPathEntries() != null) {
                                for (Long srId : srPath.getPathEntries()) {
                                    if (hasReadPermissionsForType(srId, type, srPermissions)) {
                                        filter = new SpecifiedIdsFilter<Long>();
                                        filter.setAll(Boolean.TRUE);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                    }
                }
            }
            if (filter == null) {
                filter = (SpecifiedIdsFilter<Long>) context.findSpecifiedIdsFilterByType(type);
            }
        }

        return filter;
    }

    public static boolean isDomainAdmin() {
        OPFContext context;
        try {
            context = OPFContext.getContext();
        } catch (ContextLookupException e) {
            context = null;
        }
        boolean isDomainAdmin = false;
        if (context != null && !context.getPrincipal().isGuestPrincipal()) {
            CacheManager cacheManager = CacheManager.getInstance();
            Tuple<User, List<Long>> userRoles = cacheManager.getUserInfo(context.getSessionToken());
            List<Long> roleIdList;
            if (userRoles != null && (roleIdList = userRoles.getValue()) != null && !roleIdList.isEmpty()) {
                List<Long> unknownRoles = new ArrayList<Long>();
                for (Long id : roleIdList) {
                    Role role = ModelRepository.getModel(Role.class, id);
                    if (role == null) {
                        unknownRoles.add(id);
                    } else {
                        if (getPackageAdminRole().equals(role.getLookup())) {
                            isDomainAdmin = true;
                            break;
                        }
                    }
                }

                if (!isDomainAdmin && !unknownRoles.isEmpty()) {
                    List<AdvancedSearchQueryOperand> queries = new ArrayList<AdvancedSearchQueryOperand>(1);
                    AdvancedSearchQueryOperand queryOperand = new AdvancedSearchQueryOperand();
                    queryOperand.setCriteriaList(Collections.singletonList(
                            "{\"field\":\"id\",\"operation\":\"IN\",\"value\":[" + StringUtils.join(unknownRoles, ",") + "]}"));
                    queries.add(queryOperand);
                    AdvancedSearchParams searchParams = new AdvancedSearchParams();
                    searchParams.setSearchQueries(queries);
                    AuthorityServiceProxy authService = (AuthorityServiceProxy) OPFEngine.AuthorityService;
                    ServiceResponse<Role> resp = authService.advancedSearchRolesAsSystem(searchParams, null, null);
                    List<Role> roles = resp.getData();
                    if (resp.isSuccess() && roles != null && roles.size() > 0) {
                        for (Role role : roles) {
                            if (getPackageAdminRole().equals(role.getLookup())) {
                                isDomainAdmin = true;
                            }
                            ModelRepository.saveModel(role);
                        }
                    }
                }

            }
        }
        return isDomainAdmin;
    }

    private static String getPackageAdminRole() {
        if (PKG_ADMIN_ROLE_LOOKUP == null) {
            PKG_ADMIN_ROLE_LOOKUP = OpenFlameSecurityConstants.getPackageLookup() + ".admin";
        }
        return PKG_ADMIN_ROLE_LOOKUP;
    }

    private static boolean hasReadPermissionsForType(Long securedRecordId, String type, Map<Long, List<UserPermission>> srPermissions) {
        boolean result = false;
        if (srPermissions != null) {
            List<UserPermission> permissions = srPermissions.get(securedRecordId);
            if (permissions != null) {
                for (UserPermission permission : permissions) {
                    String entityType = null;
                    if (ActionDetectorFactory.isReadPermission(permission)) {
                        entityType = ActionDetectorFactory.getReadPermissionType(permission);
                    } else if (ActionDetectorFactory.isReadAllPermission(permission)) {
                        entityType = ActionDetectorFactory.getReadAllPermissionType(permission.getPermission());
                    }
                    if (StringUtils.isNotBlank(entityType) && entityType.equalsIgnoreCase(type)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static UserRole populatePlainUserRole(User user, String type, String roleName) {
        UserRole userRole = new UserRole();
        userRole.setTypeLookup(type);
        userRole.setUser(user);
        Role role = new Role();
        role.setName(roleName);
        role.setPath(type);
        role.setLookup(type + '.' + StringUtils.normalize(roleName));
        userRole.setRole(role);
        return userRole;
    }


    private static UserRole populateUserRole(String type, String roleName) {
        UserRole userRole = populatePlainUserRole(null, type, roleName);
        userRole.setRole(userRole.getRole());
        String[] permissionNames = ROLE_PERMISSIONS.get(roleName);
        if (permissionNames != null && permissionNames.length > 0) {
            List<Permission> rolePermissions = new ArrayList<Permission>();
            for (String permissionName : permissionNames) {
                rolePermissions.add(populatePermission(type, permissionName));
                if (PERMISSION_CREATE.equals(permissionName)) {
                    populatePermissionsFromHierarchy(type, rolePermissions);
                }
            }
            userRole.getRole().setPermissions(rolePermissions);
        }
        return userRole;
    }

    private static void populatePermissionsFromHierarchy(String type, List<Permission> rolePermissions) {
        try {
            ServiceResponse<Entity> response = OPFEngine.RegistryService.readDirectChildrenTypes(type);
            if (response.isSuccess()) {
                List<Entity> entityHierarchy = response.getData();
                if (entityHierarchy != null) {
                    for (Entity entity : entityHierarchy) {
                        String[] standardPermissions = ROLE_PERMISSIONS.get(ROLE_OWNER);
                        for (String standardPermission : standardPermissions) {
                            rolePermissions.add(populatePermission(entity.getLookup(), standardPermission));
                        }
                    }
                }
            } else {
                logger.warn("Failed to load type hierarchy. Reason: " + response.getMessage());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private static Permission populatePermission(String type, String permissionName) {
        Permission permission = new Permission();
        permission.setLookup(type + '.' + StringUtils.normalize(permissionName));
        return permission;
    }

    private static UserRole populateUserRole(User user, String type, String roleName) {
        UserRole userRole = populateUserRole(type, roleName);
        userRole.setUser(user);
        return userRole;
    }

    private static UserRole populateUserRole(User user, String type, String roleName, Long securedRecordId) {
        UserRole userRole = populateUserRole(user, type, roleName);
        userRole.setSecuredRecordId(securedRecordId);
        return userRole;
    }

    private static void createDefaultRoles(List<UserRole> defaultRoles) {
        AuthorityServiceProxy authorityServiceProxy = (AuthorityServiceProxy) OPFEngine.AuthorityService;
        ServiceResponse<UserRole> response = authorityServiceProxy.saveContextUserRolesAsSystem(defaultRoles, Boolean.TRUE);
        if (response.isSuccess()) {
            logger.info("Default context roles were saved successfully.");
        } else {
            logger.warn("Failed to save default context permissions for object. Reason: " + response.getMessage());
        }
    }

    private static List<UserRole> prepareDefaultRoles(String typeLookup, Long securedRecordId) {
        OPFContext context;
        try {
            context = OPFContext.getContext();
        } catch (ContextLookupException e) {
            context = null;
        }
        List<UserRole> defaultRoles;
        if (context == null || context.getPrincipal().isGuestPrincipal()) {
            logger.warn("Application tries to set context permissions for guest user.");
            defaultRoles = null;
        } else {
            User currentUser = new User();
            currentUser.setId(context.getPrincipal().getUserInfoProvider().getId());
            UserRole owner = populateUserRole(currentUser, typeLookup, ROLE_OWNER, securedRecordId);

            UserRole contributor = populateUserRole(typeLookup, ROLE_CONTRIBUTOR);
            UserRole viewer = populateUserRole(typeLookup, ROLE_VIEWER);
            owner.setTypeLookup(typeLookup);
            defaultRoles = new ArrayList<UserRole>();
            defaultRoles.add(owner);
            defaultRoles.add(contributor);
            defaultRoles.add(viewer);
        }
        return defaultRoles;
    }

}