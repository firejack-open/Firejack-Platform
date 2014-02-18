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

package net.firejack.platform.web.security.session;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.security.SecurityUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 */
public class UserSessionManager {

    private static final Logger logger = Logger.getLogger(UserSessionManager.class);

    private static UserSessionManager instance;
//    private final ConcurrentMap<String, UserSession> tokenMap = new ConcurrentHashMap<String, UserSession>();
    private static Set<Long> guestRoleIds;


    /**
     * Open user session and return token of opened user session.
     * @param user user
     * @param cacheProcessor c
     * @param roleIds roles id list
     * @param ipAddress ip address
     * @return token of opened user session
     */
    public String openUserSession(IUserInfoProvider user, ICacheDataProcessor cacheProcessor, List<Long> roleIds, String ipAddress) {
        String token = SecurityUtils.generateTokenValue(ipAddress);
        long expirationTime = System.currentTimeMillis() + Long.parseLong(OpenFlameConfig.SESSION_EXPIRATION_PERIOD.getValue());
        UserSession userSession = new UserSession(token, user, roleIds, expirationTime);
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setUserSession(token, userSession);

        refreshUserPermissions(token, cacheProcessor, user.getId(), roleIds);

        Map<Long, Map<Long, List<UserPermission>>> securedRecordContextPermissions =
                cacheProcessor.getUserPermissionsBySecuredRecords(user.getId());
        Map<Long, List<UserPermission>> securedRecordPermissions = new HashMap<Long, List<UserPermission>>();
        for (Map.Entry<Long, Map<Long, List<UserPermission>>> entry : securedRecordContextPermissions.entrySet()) {
            List<UserPermission> userPermissions = new LinkedList<UserPermission>();
            for (List<UserPermission> permissions : entry.getValue().values()) {
                userPermissions.addAll(permissions);
            }
            securedRecordPermissions.put(entry.getKey(), userPermissions);
        }

        cacheManager.setSecuredRecordContextPermissions(user.getId(), securedRecordPermissions);

        User userInfo = asUser(user);
        cacheManager.setUserInfo(token, userInfo, roleIds);

        cacheManager.setIdFiltersForUser(user.getId(), cacheProcessor.getFiltersByUser(user.getId()));

        cacheManager.registerSessionToken(token);

        return token;
    }

    /**
     * Open user session and return session token associated with that session.
     * @param user user that should be used for session population
     * @return populated user session token
     */
    public String openUserSession(IUserInfoProvider user) {
        String token = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + Long.parseLong(OpenFlameConfig.SESSION_EXPIRATION_PERIOD.getValue());
        UserSession userSession = new UserSession(token, user, new ArrayList<Long>(), expirationTime);
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setUserSession(token, userSession);
        return token;
    }

    public void markSessionAsSystemLevel(String sessionToken) {
        CacheManager cacheManager = CacheManager.getInstance();
        synchronized (cacheManager) {
            Set<String> systemLevelSessions = cacheManager.getSystemLevelSessions();
            systemLevelSessions = systemLevelSessions == null ? new HashSet<String>() : systemLevelSessions;
            systemLevelSessions.add(sessionToken);
            cacheManager.setSystemLevelSessions(systemLevelSessions);
        }
    }

    /**
     * @param token
     * @return
     */
    public boolean closeUserSession(String token) {
        CacheManager cacheManager = CacheManager.getInstance();
        UserSession userSession = cacheManager.getUserSession(token);
        if (userSession != null) {
            cacheManager.removeUserSession(token);
        }
        if (userSession != null || cacheManager.isSessionTokenActive(token)) {//also, handle cases when cache manager still has entries.
            cacheManager.invalidateCachedEntriesForToken(token);
            if (userSession == null) {
                logger.error("Cache still has token related data while there are no related user session registered.");
                //todo: restart memcached, reload all data and notify all observer nodes.
            }
        }
        return userSession != null;
    }

    /**
     * @param token
     * @param expirationSecondsFromTheMoment
     */
    public void prolongUserSessionExpiration(String token, long expirationSecondsFromTheMoment) {
        CacheManager cacheManager = CacheManager.getInstance();
        UserSession userSession = cacheManager.getUserSession(token);
        if (userSession != null) {
            userSession.setExpirationTime(
                    System.currentTimeMillis() + expirationSecondsFromTheMoment);
            cacheManager.setUserSession(token, userSession);
        }
    }

    /**
     * @param token
     * @return
     */
    public boolean isUserSessionExpired(String token) {
        CacheManager cacheManager = CacheManager.getInstance();
        UserSession userSession = cacheManager.getUserSession(token);
        //return userSession == null || userSession.getExpirationTime() < System.currentTimeMillis();

        boolean isExpired;
        if (userSession == null) {
            isExpired = true;
        } else {
            isExpired = userSession.getExpirationTime() < System.currentTimeMillis();
            if (isExpired) {
                Set<String> systemLevelSessions = cacheManager.getSystemLevelSessions();
                if (systemLevelSessions != null && systemLevelSessions.contains(token)) {
                    isExpired = false;
                } else {
                    Set<String> siteMinderLevelSessions = cacheManager.getSiteMinderLevelSessions();
                    if (siteMinderLevelSessions != null && siteMinderLevelSessions.contains(token)) {
                        isExpired = false;
                    }
                }
            }
        }
        return isExpired;
    }

    /**
     * @param roleId
     * @param userPermissions
     */
    public void updateRolePermissionsForOpenedSessions(Long roleId, List<UserPermission> userPermissions) {
        if (roleId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            userPermissions = userPermissions == null ? new LinkedList<UserPermission>() : userPermissions;
            if (getGuestRoleIds().contains(roleId)) {
                Map<Long, List<UserPermission>> guestPermissions = cacheManager.getGuestPermissions();
                guestPermissions.put(roleId, userPermissions);
                cacheManager.setGuestPermissions(guestPermissions);
            } else {
                Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
                for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
                    UserSession userSession = sessionEntry.getValue();
                    if (userSession != null && userSession.getRoleIds().contains(roleId)) {
                        String sessionToken = sessionEntry.getKey();
                        Map<Long, List<UserPermission>> cachedPermissions = cacheManager.getPermissions(sessionToken);
                        Map<Long, List<UserPermission>> rolePermissions = new HashMap<Long, List<UserPermission>>();
                        if (cachedPermissions != null) {
                            rolePermissions.putAll(cachedPermissions);
                        }
                        rolePermissions.put(roleId, userPermissions);
                        cacheManager.setPermissions(sessionToken, rolePermissions);
                    }
                }
            }
        }
    }

    /**
     * @param roleId
     */
    public void deleteRole(Long roleId) {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
        for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
            UserSession userSession = sessionEntry.getValue();
            if (userSession != null && userSession.getRoleIds().contains(roleId)) {
                if (userSession.getRoleIds().remove(roleId)) {
                    cacheManager.setUserSession(sessionEntry.getKey(), userSession);
                    Map<Long, List<UserPermission>> permissionsByRoles = cacheManager.getPermissions(sessionEntry.getKey());
                    permissionsByRoles.remove(roleId);
                    cacheManager.setPermissions(sessionEntry.getKey(), permissionsByRoles);
                    Tuple<User, List<Long>> userInfoFromCache = cacheManager.getUserInfo(sessionEntry.getKey());
	                if (userInfoFromCache != null) {
		                userInfoFromCache.getValue().remove(roleId);
	                }
                }
            }
        }
    }

    public void refreshRolePermissions() {
        CacheManager cacheManager = CacheManager.getInstance();
        Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
        for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
            Map<Long, List<UserPermission>> refreshedPermissions = new HashMap<Long, List<UserPermission>>();
            if (sessionEntry.getValue() != null) {
                for (Long roleId : sessionEntry.getValue().getRoleIds()) {
                    List<UserPermission> userPermissionList = cacheManager.getRolePermissions(roleId);
                    refreshedPermissions.put(roleId, userPermissionList);
                }
            }
            cacheManager.setPermissions(sessionEntry.getKey(), refreshedPermissions);
        }
    }

    /**
     * @param userId
     * @param userPermissionsBySecuredRecord
     */
    public void refreshUserContextRolePermissions(Long userId, Map<Long, Map<Long, List<UserPermission>>> userPermissionsBySecuredRecord) {
        if (userId != null) {
            Map<Long, List<UserPermission>> securedRecordPermissions = new HashMap<Long, List<UserPermission>>();
            for (Map.Entry<Long, Map<Long, List<UserPermission>>> entry : userPermissionsBySecuredRecord.entrySet()) {
                Long securedRecordId = entry.getKey();
                List<UserPermission> userPermissions = new LinkedList<UserPermission>();
                Map<Long, List<UserPermission>> userPermissionsByRoles = entry.getValue();
                for (List<UserPermission> permissions : userPermissionsByRoles.values()) {
                    userPermissions.addAll(permissions);
                }
                securedRecordPermissions.put(securedRecordId, userPermissions);
            }
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
            for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
                UserSession userSession = sessionEntry.getValue();
                if (userSession != null && userSession.getUser() != null) {
                    if (userId.equals(userSession.getUser().getId())) {
                        cacheManager.setSecuredRecordContextPermissions(userId, securedRecordPermissions);
                    }
                }
            }
        }
    }

    /**
     * @param userId
     * @param roleId
     * @param cacheProcessor
     */
    public void addUserRole(Long userId, Long roleId, ICacheDataProcessor cacheProcessor) {
        if (userId != null && roleId != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
            for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
                UserSession userSession = sessionEntry.getValue();
                if (userSession != null && userSession.getUser() != null && userId.equals(userSession.getUser().getId())) {
                    List<Long> userRoleIdList = userSession.getRoleIds();
                    if (!userRoleIdList.contains(roleId)) {
                        User userInfo = asUser(userSession.getUser());

                        userRoleIdList = new ArrayList<Long>(userRoleIdList);
                        userRoleIdList.add(roleId);

                        userSession.setRoleIds(userRoleIdList);
                        cacheManager.setUserSession(sessionEntry.getKey(), userSession);

                        cacheManager.setUserInfo(sessionEntry.getKey(), userInfo, userRoleIdList);

                        refreshUserPermissions(sessionEntry.getKey(), cacheProcessor, userId, userRoleIdList);
                    }
                }
            }
        }
    }

    /**
     * @param userId
     * @param roleId
     * @param cacheProcessor
     */
    public void removeUsualUserRole(Long userId, Long roleId, ICacheDataProcessor cacheProcessor) {
        if (userId != null && roleId != null) {
            boolean refreshed = false;
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
            for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
                UserSession userSession = sessionEntry.getValue();
                if (userSession != null) {
                    IUserInfoProvider userInfoProvider = userSession.getUser();
                    if (userId.equals(userInfoProvider.getId())) {
                        List<Long> roleIdList = new ArrayList<Long>(userSession.getRoleIds());
                        if (roleIdList.remove(roleId)) {
                            User userInfo = asUser(userInfoProvider);

                            userSession.setRoleIds(roleIdList);
                            cacheManager.setUserSession(sessionEntry.getKey(), userSession);

                            cacheManager.setUserInfo(sessionEntry.getKey(), userInfo, roleIdList);
                            refreshUserPermissions(sessionEntry.getKey(), cacheProcessor, userId, roleIdList);
                            refreshed = true;
                        }
                    }
                }
            }
            if (!refreshed) {
                logger.error("User role to delete was not found.");
            }
        }
    }

    /**
     * @param userInfo
     * @param roleIdList
     */
    public void refreshUserRoles(User userInfo, List<Long> roleIdList) {
        if (userInfo != null) {
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, UserSession> userSessions = cacheManager.getAllUserSessions();
            for (Map.Entry<String, UserSession> sessionEntry : userSessions.entrySet()) {
                IUserInfoProvider user = sessionEntry.getValue().getUser();
                if (user.getId() != null && user.getId().equals(userInfo.getId())) {
                    CacheManager.getInstance().setUserInfo(sessionEntry.getKey(), userInfo, roleIdList);
                }
            }
        }
    }

    public void markSessionAsSiteMinderLevel(String sessionToken) {
        CacheManager cacheManager = CacheManager.getInstance();
        synchronized (cacheManager) {
            Set<String> siteMinderLevelSessions = cacheManager.getSiteMinderLevelSessions();
            siteMinderLevelSessions = siteMinderLevelSessions == null ? new HashSet<String>() : siteMinderLevelSessions;
            siteMinderLevelSessions.add(sessionToken);
            cacheManager.setSiteMinderLevelSessions(siteMinderLevelSessions);
        }
    }

    public static Set<Long> getGuestRoleIds() {
        return guestRoleIds;
    }

    public static void setGuestRoleIds(Set<Long> guestRoleIds) {
        UserSessionManager.guestRoleIds = guestRoleIds;
    }

    /**
     * @return
     */
    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    private void refreshUserPermissions(String sessionToken, ICacheDataProcessor cacheProcessor, Long userId, List<Long> userRoleIdList) {
        Map<Long, List<UserPermission>> permissionsByRoles = new HashMap<Long, List<UserPermission>>(
                cacheProcessor.getPermissionsByRoles(userRoleIdList.toArray(new Long[userRoleIdList.size()])));
        Map<Long, List<UserPermission>> userContextPermissions = cacheProcessor.getUserContextPermissions(userId);
        permissionsByRoles.putAll(userContextPermissions);
        //now permissionsByRoles contains ordinary permissions,
        //and contextual permissions excluding contextual permissions that have secured record reference.
        CacheManager.getInstance().setPermissions(sessionToken, permissionsByRoles);
    }

    private User asUser(IUserInfoProvider userInfo) {
        User user = new User();
        user.setId(userInfo.getId());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setUsername(userInfo.getUsername());

        return user;
    }
}
