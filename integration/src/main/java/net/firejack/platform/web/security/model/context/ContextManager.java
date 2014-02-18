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

package net.firejack.platform.web.security.model.context;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The {@code ContextManager} class is a static facade that cannot be instantiated. It provides
 * basic access to various pieces of the context for the running service or application function / page
 * created by the Open Flame front-end filter. Any request fronted by the {@code StandaloneSecurityFilter}
 * will be guaranteed to have access to all the methods provided on this class.<p>
 * <p/>
 * The {@code ContextManager} provides access to the {@code BusinessContext} object (which contains
 * most of the context-specific data for the framework) and access to the current authentication user or
 * {@code OpenFlamePrincipal} object (which is used to check permissions.
 */
public class ContextManager {

    private static final Logger logger = Logger.getLogger(ContextManager.class);

    /**
     * The {@code ContextManager} is a static only class providing links to information
     * about the currently processing request.
     */
    private ContextManager() {

    }

    /**
     * Checks to see if the session for the current context is expired. This method is used by the
     * framework to detect session timeouts and handle them.
     *
     * @param sessionToken String a valid session security token
     * @return boolean true if the session has expired
     */
    public static boolean isSessionExpired(String sessionToken) {
        ServiceResponse<SimpleIdentifier<Boolean>> response =
                OPFEngine.AuthorityService.isSessionExpired(sessionToken);
        if (response.isSuccess()) {
            return response.getItem().getIdentifier();
        } else {
            throw new BusinessFunctionException(response.getMessage());
        }
    }

    /**
     * Checks to see if the session is active for a given session token. This method is used internally
     * to validate the session is active.
     *
     * @param sessionToken String a valid session token issued by the STS
     * @return boolean true if the session is still valid
     */
    public static boolean isSessionActive(String sessionToken) {
        ServiceResponse<SimpleIdentifier<Boolean>> response =
                OPFEngine.AuthorityService.isSessionTokenActive(sessionToken);
        if (response.isSuccess()) {
            return response.getItem().getIdentifier();
        } else {
            throw new BusinessFunctionException(response.getMessage());
        }
    }

    /**
     * Checks to see if the session is active for a given session token. This method is used internally
     * to validate the session is active.
     *
     * @param sessionToken String a valid session token issued by the STS
     * @param browserIpAddress client browser IP address
     * @return boolean true if the session is still valid
     */
    public static boolean isSessionActive(String sessionToken, String browserIpAddress) {
        ServiceResponse<SimpleIdentifier<Boolean>> response =
                OPFEngine.AuthorityService.isSessionTokenActive(sessionToken, browserIpAddress);
        if (response.isSuccess()) {
            return response.getItem().getIdentifier();
        } else {
            throw new BusinessFunctionException(response.getMessage());
        }
    }

    /**
     * This method returns a reference to the user information provider (a strategy for loading user
     * information into the context and principal). This is used by the framework to determine how to load
     * user information.
     *
     * @return IUserInfoProvider the provider implementation
     */
    public static IUserInfoProvider getUserInfoProvider() {
        return getPrincipal().getUserInfoProvider();
    }

    /**
     * Checks whether there is business context initialized
     *
     * @return true if user was authenticated and false if not
     */
    public static boolean isUserAuthenticated() {
        boolean userAuthenticated;
        try {
            OPFContext context = OPFContext.getContext();
            userAuthenticated = context != null && !context.getPrincipal().isGuestPrincipal();
        } catch (ContextLookupException e) {
            logger.warn(e.getMessage(), e);
            userAuthenticated = false;
        }
        return userAuthenticated;
    }

    /**
     * Gets the current Principal object with the user identity and other information
     * provided.
     *
     * @return FjkPrincipal the current user or system conected to the services
     */
    public static OpenFlamePrincipal getPrincipal() {
        return getContext().getPrincipal();
    }

    /**
     * Returns the current action based on the URL being accessed.
     *
     * @return String the name of the action
     */
    public static String getCurrentAction() {
        return OPFContext.getContext().getCurrentActionLookup();
    }

    /**
     * Retrieves the current username of the logged-in user.
     *
     * @return String username of the current user
     */
    public static String getCurrentUserName() {
        return OPFContext.getContext().getPrincipal().getName();
    }

    /**
     * Returns a reference to the current context associated with the thread.
     *
     * @return BusinessContext the current context object
     */
    public static OPFContext getContext() {
        return OPFContext.getContext();
    }

	/**
	 * This method looks at all user permissions based on the lookup and the entityType and returns
	 * the ID fields for all context-sensitive permissions that match the permission and entityType
	 * as a list of Strings.<p>
	 *
	 * An example of this call would be to request a list of IDs for objects that could be read, as
	 * shown here: <BR>
	 * <code>getAllowedIdStrings('com.mycoolmovies.movies.movie.read', 'com.mycoolmovies.movies.movie');<code><br>
	 * This would return a list of the ids of all movies that could be read, NULL
	 * for no read permissions, or an empty list if the user has global permissions.
	 *
	 * @param permissionLookup String the fully qualified permission lookup
	 * @param entityType String the fully qualified permission context entity type
	 * @return List<String> a list of ids as Strings
	 */
	public static List<String> getAllowedIdStringList(String permissionLookup, String entityType)
	{
        List<Long> idList = getAllowedIdNumericList(permissionLookup, entityType);
        List<String> result;
        if (idList == null) {
            result = null;
        } else {
            result = new ArrayList<String>();
            for (Long id : idList) {
                result.add(String.valueOf(id));
            }
        }
        return result;
	}

	/**
	 * This method looks at all user permissions based on the lookup and the entityType and returns
	 * the ID fields for all context-sensitive permissions that match the permission and entityType
	 * as a list of Strings.<p>
	 *
	 * An example of this call would be to request a list of IDs for objects that could be read, as
	 * shown here: <BR>
	 * <code>getAllowedIdStrings('com.mycoolmovies.movies.movie.read', 'com.mycoolmovies.movies.movie');<code><br>
	 * This would return a list of the ids of all movies that could be read, NULL
	 * for no permissions, or an empty list if the user has global permissions. 
	 *
	 * @param permissionLookup String the fully qualified permission lookup
	 * @param entityType String the fully qualified permission context entity type
	 * @return List<Long> a list of ids as Longs
	 */
	public static List<Long> getAllowedIdNumericList(String permissionLookup, String entityType) {
		List<Long> allowedIdList;
        if (StringUtils.isBlank(permissionLookup)) {
            allowedIdList = null;
        } else {
            String type = ActionDetectorFactory.isReadPermission(permissionLookup) ?
                    ActionDetectorFactory.getReadPermissionType(permissionLookup) :
                    ActionDetectorFactory.isReadAllPermission(permissionLookup) ?
                            ActionDetectorFactory.getReadAllPermissionType(permissionLookup) : null;
            if (type == null) {
                allowedIdList = null;
            } else {
                @SuppressWarnings("unchecked")
                SpecifiedIdsFilter<Long> idFilter = (SpecifiedIdsFilter<Long>)
                        getContext().findSpecifiedIdsFilterByType(type);
                allowedIdList = idFilter.getNecessaryIds();
            }
        }
		return allowedIdList;
	}

    public List<Long> getAllowedIdFilter(String permissionName, String entityTypeLookup) {
        List<Long> allowedIdList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(permissionName) && StringUtils.isNotBlank(entityTypeLookup)) {
            Long currentSecuredRecordId = OPFContext.getContext().getSecuredRecordId();
            currentSecuredRecordId = currentSecuredRecordId == null ?
                    OpenFlameSecurityConstants.EMPTY_SECURED_RECORD_ID : currentSecuredRecordId;
            ServiceResponse<UserPermission> permissionsResponse =
                    OPFEngine.AuthorityService.loadPermissionsBySecuredRecord(currentSecuredRecordId);
            List<Long> allowedList = new ArrayList<Long>();
            if (permissionsResponse.isSuccess()) {
                List<UserPermission> srContextPermissions = permissionsResponse.getData();
                if (srContextPermissions != null) {
                    for (UserPermission assignedUserPermission : srContextPermissions) {
                        if (permissionName.equalsIgnoreCase(assignedUserPermission.getPermission()) &&
                                entityTypeLookup.equalsIgnoreCase(assignedUserPermission.getEntityType()) &&
                                assignedUserPermission.getEntityId() != null) {
                            allowedIdList.add(Long.valueOf(assignedUserPermission.getEntityId()));
                        }
                    }
                }
            }
        }
        return allowedIdList;
    }

    /**
     * Checks a specific user permission is available for the current login user.
     *
     * @param userPermission a valid user permission object
     * @return boolean true if the user has the permission
     */
    public static boolean checkUserPermission(UserPermission userPermission) {
        return getContext().getPrincipal().checkUserPermission(userPermission);
    }

    /**
     * @param permissionLookup permission lookup
     * @return true if current user has permission for specified type and specified id. Otherwise return false
     */
    public static boolean checkPermission(String permissionLookup) {
        return getContext().getPrincipal().checkUserPermission(Arrays.asList(permissionLookup));
    }

    /**
     * @param permissionLookup permission lookup
     * @param entityType entity type
     * @param entityId entity id
     *
     * @return true if current user has permission for specified type and specified id. Otherwise return false
     */
    public static boolean checkPermission(String permissionLookup, String entityType, Long entityId) {
        return getContext().getPrincipal().checkUserPermission(
                new UserPermission(permissionLookup, entityType, entityId == null ? null : String.valueOf(entityId)));
    }

    /**
     * Get current request path if any known in context on method execution.
     *
     * @return String current request path if any known
     */
    public static String getCurrentRequestPath() {
        String currentRequestPath;
        try {
            OPFContext context = getContext();
            currentRequestPath = context == null ? null : context.getCurrentRequestPath();
        } catch (ContextLookupException e) {
            currentRequestPath = null;
        }
        return currentRequestPath;
    }

}