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

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.api.authority.domain.UserContextPermissions;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordPermissions;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.session.UserSession;
import net.firejack.platform.web.security.session.UserSessionManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CacheManager {

    static final String USER_INFO_KEY_PREFIX = "USER_INFO";
    static final String PERMISSIONS_KEY_PREFIX = "PERMISSIONS";
    static final String CONTEXT_PERMISSIONS_KEY_PREFIX = "CONTEXT_PERMISSIONS";
    static final String GUEST_PERMISSIONS_KEY = "GUEST_PERMISSIONS";
    static final String NAVIGATION_ELEMENTS_KEY_PREFIX = "NAVIGATION_ELEMENTS";
    static final String RESOURCE_LOCATIONS_KEY_PREFIX = "RESOURCE_LOCATIONS";
    static final String ACTIONS_KEY_PREFIX = "ACTIONS";
    static final String SECURED_RECORDS_KEY = "SECURED_RECORDS";
    static final String USER_ROLES_KEY = "USER_ROLES";
    static final String ID_FILTER_KEY = "ID_FILTER";
    static final String CONFIGS_KEY_PREFIX = "CONFIGS";
    static final String SITE_MINDER_SESSIONS_KEY_PREFIX = "SM_SESSIONS";
    static final String SITE_MINDER_SU_KEY_PREFIX = "SM_SUPER_USERS";

    static final String ROLE_PERMISSIONS_KEY = "ROLE_PERMISSIONS";
    static final String SECURED_RECORD_PERMISSIONS_KEY = "SECURED_RECORD_PERMISSIONS";
    static final String PACKAGE_LEVEL_PERMISSIONS_KEY = "PACKAGE_LEVEL_PERMISSIONS";

    static final String USER_SESSION_KEY = "USER_SESSION";
    static final String SYSTEM_SESSIONS_KEY = "SYSTEM_SESSIONS";
    static final String ENTITY_TYPES_KEY = "ENTITY_TYPES";
    static final String ENTITY_TYPES_WITH_SUBCLASSES_KEY = "ENTITY_TYPES_WITH_SUBCLASSES";
    static final String CONTEXT_ATTRIBUTES_KEY = "CONTEXT_ATTRIBUTES";
    static final String SECURITY_ENABLED_KEY = "SECURITY_ENABLED_KEY";

    private static final String MSG_LINE = "------------------------------------------------------------------------------------------------";
    private static final String MSG_MEMCACHED_STORE_NOT_AVAILABLE = "Memcached store is not available, application will use local cache store implementation instead.";
    private static final String MSG_CACHE_MANAGER_ALREADY_INITIALIZED = "Cache manager already initialized.";
    private static final String MSG_OBJECT_FOR_KEY = "Object for key [";
    private static final String MSG_WAS_PUT = "] was put";

    private static final Logger logger = Logger.getLogger(CacheManager.class);
    private static CacheManager instance;
    private static boolean initialized = false;

    private ICacheStore cacheStore;
    private boolean debugMode = false;
    private ICachedEntryProducer cachedEntryProducer;

    private CacheManager(ICacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    /**
     * @return
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * @param sessionToken
     */
    public void registerSessionToken(String sessionToken) {
        if (StringUtils.isNotBlank(sessionToken)) {
            cacheStore.saveToCache(sessionToken, Boolean.TRUE);
        }
    }

    /**
     * @param sessionToken
     */
    public void unRegisterSessionToken(String sessionToken) {
        if (StringUtils.isNotBlank(sessionToken)) {
            cacheStore.saveToCache(sessionToken, Boolean.FALSE);
        }
    }

    /**
     * @param sessionToken
     * @return
     */
    public boolean isSessionTokenActive(String sessionToken) {
        Boolean active = getSessionTokenActiveStatus(sessionToken);
        return active == null ? Boolean.FALSE : active;
    }

    /**
     * @param sessionToken
     * @return
     */
    public Boolean getSessionTokenActiveStatus(String sessionToken) {
        return getFromCache("", sessionToken);
    }

    /**
     * @param sessionToken
     * @param userInfo
     * @param roleIds
     */
    public void setUserInfo(String sessionToken, User userInfo, List<Long> roleIds) {
        putToCache(USER_INFO_KEY_PREFIX, sessionToken, new Tuple<User, List<Long>>(userInfo, roleIds));
    }

    /**
     * @param sessionToken
     * @return
     */
    public Tuple<User, List<Long>> getUserInfo(String sessionToken) {
        return getFromCache(USER_INFO_KEY_PREFIX, sessionToken);
    }

    /**
     * @param sessionToken
     * @param permissions
     */
    public void setPermissions(String sessionToken, Map<Long, List<UserPermission>> permissions) {
        putToCache(PERMISSIONS_KEY_PREFIX, sessionToken, permissions);
    }

    /**
     * @param sessionToken
     * @return
     */
    public Map<Long, List<UserPermission>> getPermissions(String sessionToken) {
        return getFromCache(PERMISSIONS_KEY_PREFIX, sessionToken);
    }

    /**
     * @param userId
     * @param permissions
     */
    public void setSecuredRecordContextPermissions(Long userId, Map<Long, List<UserPermission>> permissions) {
        if (userId != null && permissions != null) {
            putToCache(CONTEXT_PERMISSIONS_KEY_PREFIX, userId.toString(), permissions);
        }
    }

    /**
     * @param userId
     * @return
     */
    public Map<Long, List<UserPermission>> getSecuredRecordContextPermissions(Long userId) {
        if (userId == null) {
            return null;
        }
        return getFromCache(CONTEXT_PERMISSIONS_KEY_PREFIX, userId.toString());
    }

    public void setSecuredRecordPermissions(Long securedRecordId, SecuredRecordPermissions securedRecordPermissions) {
        putToCache(SECURED_RECORD_PERMISSIONS_KEY, securedRecordId.toString(), securedRecordPermissions);
    }

    public SecuredRecordPermissions getSecuredRecordPermissions(Long securedRecordId) {
        return getFromCache(SECURED_RECORD_PERMISSIONS_KEY, securedRecordId.toString());
    }

    public Map<Long, SecuredRecordPermissions> getAllSecuredRecordPermissions() {
        Map<String, Object> values = cacheStore.findByPrefix(SECURED_RECORD_PERMISSIONS_KEY);
        Map<Long, SecuredRecordPermissions> securedRecordPermissions = new HashMap<Long, SecuredRecordPermissions>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Long securedRecordId = Long.parseLong(entry.getKey().substring(SECURED_RECORD_PERMISSIONS_KEY.length()));
            securedRecordPermissions.put(securedRecordId, (SecuredRecordPermissions) entry.getValue());
        }
        return securedRecordPermissions;
    }

    public void setPackageLevelUserPermissions(String packageLookup, Long userId, UserContextPermissions userPermissions) {
        if (StringUtils.isNotBlank(packageLookup) && userId != null) {
            Map<Long, UserContextPermissions> oldPermissions = getFromCache(PACKAGE_LEVEL_PERMISSIONS_KEY, packageLookup);
            if (oldPermissions == null) {
                oldPermissions = new HashMap<Long, UserContextPermissions>();
            }
            oldPermissions.put(userId, userPermissions);
            putToCache(PACKAGE_LEVEL_PERMISSIONS_KEY, packageLookup, oldPermissions);
        }
    }

    public UserContextPermissions getPackageLevelUserPermissions(String packageLookup, Long userId) {
        UserContextPermissions permissions;
        if (StringUtils.isBlank(packageLookup) || userId == null) {
            permissions = null;
        } else {
            Map<Long, UserContextPermissions> existentPermissions =
                    getFromCache(PACKAGE_LEVEL_PERMISSIONS_KEY, packageLookup);
            if (existentPermissions != null) {
                permissions = existentPermissions.get(userId);
            } else {
                permissions = null;
            }
        }
        return permissions;
    }

    /**
     * @param permissions
     */
    public void setGuestPermissions(Map<Long, List<UserPermission>> permissions) {
        putToCache(GUEST_PERMISSIONS_KEY, "", permissions);
    }

    /**
     * @return
     */
    public Map<Long, List<UserPermission>> getGuestPermissions() {
        return getFromCache(GUEST_PERMISSIONS_KEY, "");
    }

    /**
     * @param packageLookup
     * @param packageNavigationElements
     */
    public void setNavigationList(String packageLookup, List<NavigationElement> packageNavigationElements) {
        putToCache(NAVIGATION_ELEMENTS_KEY_PREFIX, packageLookup, packageNavigationElements);
    }

    /**
     * @param packageLookup
     * @return
     */
    public List<NavigationElement> getNavigationList(String packageLookup) {
        return getFromCache(NAVIGATION_ELEMENTS_KEY_PREFIX, packageLookup);
    }

    public Map<String, List<NavigationElement>> getAllNavigationElements() {
        Map<String, Object> values = cacheStore.findByPrefix(NAVIGATION_ELEMENTS_KEY_PREFIX);
        Map<String, List<NavigationElement>> navigationElements = new HashMap<String, List<NavigationElement>>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String lookup = entry.getKey().substring(NAVIGATION_ELEMENTS_KEY_PREFIX.length());
            navigationElements.put(lookup, (List<NavigationElement>) entry.getValue());
        }
        return navigationElements;
    }

    /**
     * @param packageLookup
     */
    public void removeNavigationList(String packageLookup) {
        if (StringUtils.isNotBlank(packageLookup)) {
            String key = NAVIGATION_ELEMENTS_KEY_PREFIX + packageLookup;
            cacheStore.removeFromCache(key);
        }
    }

    /**
     * @param packageLookup
     * @param resourceLocations
     */
    public void setResourceLocations(String packageLookup, List<ResourceLocation> resourceLocations) {
        putToCache(RESOURCE_LOCATIONS_KEY_PREFIX, packageLookup, resourceLocations);
    }

    /**
     * @param packageLookup
     * @return
     */
    public List<ResourceLocation> getResourceLocations(String packageLookup) {
        return getFromCache(RESOURCE_LOCATIONS_KEY_PREFIX, packageLookup);
    }

    public Map<String,List<ResourceLocation>> getAllResourceLocations() {
        Map<String, Object> values = cacheStore.findByPrefix(RESOURCE_LOCATIONS_KEY_PREFIX);
        Map<String, List<ResourceLocation>> resourceLocations = new HashMap<String, List<ResourceLocation>>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String lookup = entry.getKey().substring(RESOURCE_LOCATIONS_KEY_PREFIX.length());
            resourceLocations.put(lookup, (List<ResourceLocation>) entry.getValue());
        }
        return resourceLocations;
    }

    /**
     * @param packageLookup
     */
    public void removeResourceLocations(String packageLookup) {
        if (StringUtils.isNotBlank(packageLookup)) {
            String key = RESOURCE_LOCATIONS_KEY_PREFIX + packageLookup;
            cacheStore.removeFromCache(key);
        }
    }

    /**
     * @param packageLookup
     * @param actions
     */
    public void setActions(String packageLookup, List<Action> actions) {
        putToCache(ACTIONS_KEY_PREFIX, packageLookup, actions);
    }

    /**
     * @param packageLookup
     * @return
     */
    public List<Action> getActions(String packageLookup) {
        return getFromCache(ACTIONS_KEY_PREFIX, packageLookup);
    }

    /**
     * @param packageLookup
     */
    public void removeActions(String packageLookup) {
        if (StringUtils.isNotBlank(packageLookup)) {
            String key = ACTIONS_KEY_PREFIX + packageLookup;
            cacheStore.removeFromCache(key);
        }
    }

    /**
     * @param securedRecords
     */
    public void setSecuredRecords(Map<Long, SecuredRecordNode> securedRecords) {
        putToCache(SECURED_RECORDS_KEY, securedRecords);
    }

    /**
     * @return
     */
    public Map<Long, SecuredRecordNode> getSecuredRecords() {
        return getFromCache(SECURED_RECORDS_KEY);
    }

    public void setUserRoles(Map<Long, List<Long>> userRoles) {
        putToCache(USER_ROLES_KEY, userRoles);
    }

    public Map<Long, List<Long>> getUserRoles() {
        return getFromCache(USER_ROLES_KEY);
    }

    /**
     * @param userId
     * @param userIdFilters
     */
    public void setIdFiltersForUser(Long userId, Map<String, IdFilter> userIdFilters) {
        if (userId != null && userIdFilters != null) {
            putToCache(ID_FILTER_KEY, userId.toString(), userIdFilters);
        }
    }

    /**
     * @param userId
     * @return
     */
    public Map<String, IdFilter> getIdFiltersForUser(Long userId) {
        return getFromCache(ID_FILTER_KEY, userId.toString());
    }

    public Map<Long, Map<String, IdFilter>> getAllIdFiltersForUser() {
        Map<String, Object> values = cacheStore.findByPrefix(ID_FILTER_KEY);
        Map<Long, Map<String, IdFilter>> idFilters = new HashMap<Long, Map<String, IdFilter>>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Long userId = Long.parseLong(entry.getKey().substring(ID_FILTER_KEY.length()));
            idFilters.put(userId, (Map<String, IdFilter>) entry.getValue());
        }
        return idFilters;
    }

    public Map<Long, List<UserPermission>> getAllRolePermissions() {
        Map<String, Object> values = cacheStore.findByPrefix(ROLE_PERMISSIONS_KEY);
        Map<Long, List<UserPermission>> rolePermissions = new HashMap<Long, List<UserPermission>>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Long roleId = Long.parseLong(entry.getKey().substring(ROLE_PERMISSIONS_KEY.length()));
            rolePermissions.put(roleId, (List<UserPermission>) entry.getValue());
        }
        return rolePermissions;
    }
    
    public List<UserPermission> getRolePermissions(Long roleId) {
        return getFromCache(ROLE_PERMISSIONS_KEY, roleId.toString());
    }

    public void setRolePermissions(Long roleId, List<UserPermission> newRolePermissions) {
        putToCache(ROLE_PERMISSIONS_KEY, roleId.toString(), newRolePermissions);
    }

    public void removeRolePermissions(Long roleId) {
        String key = ROLE_PERMISSIONS_KEY + roleId;
        cacheStore.removeFromCache(key);
    }

    public void setEntityTypes(List<String> entityTypes) {
        putToCache(ENTITY_TYPES_KEY, entityTypes);
    }

    public List<String> getEntityTypes() {
        return getFromCache(ENTITY_TYPES_KEY);
    }

    public void setTypeWithSubclasses(String typeLookup, List<Entity> entityWithSubclasses) {
        putToCache(ENTITY_TYPES_WITH_SUBCLASSES_KEY, typeLookup, entityWithSubclasses);
    }

    public List<Entity> getTypeWithSubclasses(String typeLookup) {
        return getFromCache(ENTITY_TYPES_WITH_SUBCLASSES_KEY, typeLookup);
    }

    public void removeTypeWithSubclasses(String typeLookup) {
        cacheStore.removeFromCache(ENTITY_TYPES_WITH_SUBCLASSES_KEY + typeLookup);
    }

    public void setUserSession(String token, UserSession userSession) {
        putToCache(USER_SESSION_KEY, token, userSession);
    }

    public UserSession getUserSession(String token) {
        return getFromCache(USER_SESSION_KEY, token);
    }

    public void setSystemLevelSessions(Set<String> systemLevelSessions) {
        putToCache(SYSTEM_SESSIONS_KEY, systemLevelSessions);
    }

    public Set<String> getSystemLevelSessions() {
        return getFromCache(SYSTEM_SESSIONS_KEY);
    }

    public Map<String, UserSession> getAllUserSessions() {
        Map<String, Object> values = cacheStore.findByPrefix(USER_SESSION_KEY);
        Map<String, UserSession> userSessions = new HashMap<String, UserSession>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String token = entry.getKey().substring(USER_SESSION_KEY.length());
            userSessions.put(token, (UserSession) entry.getValue());
        }
        return userSessions;
    }

    public void removeUserSession(String token) {
        String key = USER_SESSION_KEY + token;
        cacheStore.removeFromCache(key);
    }

    public List<Config> getConfigs() {
        return getFromCache(CONFIGS_KEY_PREFIX);
    }

    public void setConfigs(List<Config> configs) {
        putToCache(CONFIGS_KEY_PREFIX, configs);
    }

    public void setSiteMinderAdmins(Set<String> siteMinderAdmins) {
        putToCache(SITE_MINDER_SU_KEY_PREFIX, siteMinderAdmins);
    }

    public Set<String> getSiteMinderAdmins() {
        return getFromCache(SITE_MINDER_SU_KEY_PREFIX);
    }

    public void setSiteMinderLevelSessions(Set<String> systemLevelSessions) {
        putToCache(SITE_MINDER_SESSIONS_KEY_PREFIX, systemLevelSessions);
    }

    public Set<String> getSiteMinderLevelSessions() {
        return getFromCache(SITE_MINDER_SESSIONS_KEY_PREFIX);
    }

    public Map<String, Serializable> getSessionAttributes(String sessionToken) {
        Map<String, Serializable> attributes;
        if (StringUtils.isBlank(sessionToken)) {
            attributes = null;
        } else {
            Set<String> siteMinderLevelSessions = getSiteMinderLevelSessions();
            if (siteMinderLevelSessions != null && siteMinderLevelSessions.contains(sessionToken)) {
                OPFContext context = OPFContext.getContext();
                String browserIpAddress = context.getBrowserIpAddress();
                String username = context.getPrincipal().getUserInfoProvider().getUsername();
                sessionToken = username + browserIpAddress;
            }
            attributes = getFromCache(CONTEXT_ATTRIBUTES_KEY, sessionToken);
        }

        return attributes;
    }

    public void setSessionAttributes(String sessionToken, Map<String, Serializable> attributes) {
        if (StringUtils.isNotBlank(sessionToken)) {
            Set<String> siteMinderLevelSessions = getSiteMinderLevelSessions();
            if (siteMinderLevelSessions != null && siteMinderLevelSessions.contains(sessionToken)) {
                OPFContext context = OPFContext.getContext();
                String browserIpAddress = context.getBrowserIpAddress();
                String username = context.getPrincipal().getUserInfoProvider().getUsername();
                sessionToken = username + browserIpAddress;
            }
            putToCache(CONTEXT_ATTRIBUTES_KEY, sessionToken, attributes);
        }
    }

    public void setEntityAsSecurityEnabled(String entityLookup, Boolean useInstantSecurity) {
        if (StringUtils.isNotBlank(entityLookup)) {
            useInstantSecurity = useInstantSecurity == null ? Boolean.FALSE : useInstantSecurity;
            putToCache(SECURITY_ENABLED_KEY + entityLookup, useInstantSecurity);
        }
    }

    public Boolean checkIfEntitySecurityEnabled(String entityLookup) {
        if (StringUtils.isBlank(entityLookup)) {
            return null;
        } else {
            return getFromCache(SECURITY_ENABLED_KEY + entityLookup);
        }
    }

    /**
     * @param sessionToken session token
     */
    public void invalidateCachedEntriesForToken(String sessionToken) {//check
        unRegisterSessionToken(sessionToken);
        String key = PERMISSIONS_KEY_PREFIX + sessionToken;
        cacheStore.removeFromCache(key);
        key = USER_INFO_KEY_PREFIX + sessionToken;
        cacheStore.removeFromCache(key);
    }

    /**
     * @param cacheProcessor cache processor
     */
    public void initializeGuestData(ICacheDataProcessor cacheProcessor) {
        Set<Long> guestRoleIds = UserSessionManager.getGuestRoleIds();
        setGuestPermissions(cacheProcessor.getPermissionsByRoles(guestRoleIds.toArray(new Long[guestRoleIds.size()])));
    }

    /**
     * @param sessionToken session token
     * @param userId user id
     */
    public void invalidateLocalData(String sessionToken, Long userId) {
        if (cacheStore.isLocal()) {
            cacheStore.invalidate(sessionToken, userId);
        }
    }

    /**
     * @return
     */
    public boolean isLocal() {
        return cacheStore.isLocal();
    }

    /***/
    public void close() {
        if (cacheStore != null) {
            cacheStore.close();
            cacheStore = null;
        }
    }

    /**
     * @param cachedEntryProducer
     */
    public void setCachedEntryProducer(ICachedEntryProducer cachedEntryProducer) {
        this.cachedEntryProducer = cachedEntryProducer;
    }

    /**
     * @return
     */
    public static CacheManager getInstance() {
        if (instance == null) {
            MemcachedStore cacheStore = new MemcachedStore();
            if (!OpenFlameConfig.MC_SERVER_URL.exist() || !OpenFlameConfig.MC_PORT.exist()) {
                return createDefault();
            }
            String host = OpenFlameConfig.MC_SERVER_URL.getValue();
            Integer port = Integer.parseInt(OpenFlameConfig.MC_PORT.getValue());
            cacheStore.prepareClientPool(new MemcachedClientFactory(host, port));
            if (cacheStore.ping()) {
                instance = new CacheManager(cacheStore);
            } else {
                createDefault();
            }
        }
        return instance;
    }

    private static CacheManager createDefault() {
        if (instance == null) {
            logger.info(MSG_LINE);
            logger.info(MSG_MEMCACHED_STORE_NOT_AVAILABLE);
            logger.info(MSG_LINE);
            instance = new CacheManager(new LocalCacheStore());
        }
        return instance;
    }

    /**
     * @param initializer
     * @throws UnsupportedOperationException
     */
    public static synchronized void initializeIfFirstTime(ICacheManagerInitializer initializer)
            throws UnsupportedOperationException {
        if (initialized) {
            throw new UnsupportedOperationException(MSG_CACHE_MANAGER_ALREADY_INITIALIZED);
        } else {
            UserSessionManager.setGuestRoleIds(initializer.getGuestRoleIds());

            CacheManager cacheManager = getInstance();
            cacheManager.setCachedEntryProducer(initializer.populateCachedEntryProducer());
            cacheManager.setDebugMode(initializer.isDebugMode());

            ICacheDataProcessor cachedDataProcessor = initializer.getCachedDataProcessor();
            cachedDataProcessor.loadData(true);
            int cachedElementExpirationTime = cacheManager.cacheStore.getCachedElementExpirationTime();
            if (cachedElementExpirationTime > 0) {
                int delayTime = cachedElementExpirationTime / 2;
                cachedDataProcessor.scheduleReload(delayTime, delayTime, TimeUnit.SECONDS);
            }

            initialized = true;
        }
    }

    /**
     * @return
     */
    public static boolean isInitialized() {
        return initialized;
    }

    public <T> void putToCache(String key, T object) {
        cacheStore.saveToCache(key, object);
        if (isDebugMode()) {
            StringBuilder sb = new StringBuilder(MSG_OBJECT_FOR_KEY);
            sb.append(key).append(MSG_WAS_PUT);
            logger.debug(sb.toString());
        }
    }

    private <T> void putToCache(String keyPrefix, String sessionToken, T object) {
        String key = keyPrefix + sessionToken;
        cacheStore.saveToCache(key, object);
        if (isDebugMode()) {
            StringBuilder sb = new StringBuilder(MSG_OBJECT_FOR_KEY);
            sb.append(key).append(MSG_WAS_PUT);
            logger.debug(sb.toString());
        }
    }

    public <T> T getFromCache(String key) {
        return (T) cacheStore.loadFromCache(key);
    }

    private <T> T getFromCache(String keyPrefix, String sessionToken) {
        String keyInCache = keyPrefix + sessionToken;
        return (T) cacheStore.loadFromCache(keyInCache);
    }

}