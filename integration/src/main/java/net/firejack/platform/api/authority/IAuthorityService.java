/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.authority;


import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.AdvancedSearchParams;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;

import java.util.List;
import java.util.Map;


public interface IAuthorityService {

    //----------AUTHORITY---------------
    ServiceResponse<AuthenticationToken> processSTSSignIn(String username, String password, String ipAddress);

    ServiceResponse<AuthenticationToken> processSMWSSignIn(String username, String password, String ipAddress);

    ServiceResponse<AuthenticationToken> processSTSForgotPassword(String email, String resetPasswordPageUrl);

    ServiceResponse<AuthenticationToken> processSTSResetPassword(String token);

    ServiceResponse<AuthenticationToken> processSTSCertSignIn(String lookup, String name, String cert);

    ServiceResponse processSTSSignOut(String token);

    ServiceResponse<SimpleIdentifier<Boolean>> isSessionExpired(String sessionToken);

    ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken);

    ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken, String browserIpAddress);

    ServiceResponse<AuthenticationToken> processOpenIdSignIn(
            String email, Map<SupportedOpenIDAttribute, String> mappedAttributes, String ipAddress);

    ServiceResponse<AuthenticationToken> processFacebookIdSignIn(
            Long facebookId, Map<String, String> mappedAttributes, String browserIpAddress);

    ServiceResponse<AuthenticationToken> processTwitterIdSignIn(
            Long twitterId, Map<String, String> mappedAttributes, String browserIpAddress);

    ServiceResponse<AuthenticationToken> processLinkedInSignIn(
            Map<String, String> mappedAttributes, String browserIpAddress);

    //ServiceResponse<AuthenticationToken> processSiteMinderSignIn(String standardId, String browserIpAddress);
    ServiceResponse<AuthenticationToken> processSiteMinderSignIn(User user, String browserIpAddress);

    ServiceResponse<SimpleIdentifier<String>> synchronizeSMAdmins(String smAdminUsers);

    ServiceResponse<User> updateSMUserRoles(User user);

    //-----------PERMISSION------------
    ServiceResponse<UserPermission> loadGrantedPermissions();

    ServiceResponse<UserPermission> loadDeniedPermissions();

    ServiceResponse<UserPermission> loadPermissionsBySecuredRecord(Long securedRecordId);

    ServiceResponse<MappedPermissions> readPermissionsByRolesMap();

    ServiceResponse<MappedPermissions> readPermissionsByRolesMapForGuest();

    ServiceResponse<Permission> createPermission(ServiceRequest<Permission> request);

    ServiceResponse<Permission> updatePermission(ServiceRequest<Permission> request);

    ServiceResponse deletePermission(Long permissionId);

    ServiceResponse<Permission> readPermission(Long permissionId);

    ServiceResponse<Permission> readAllPermissions(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit);

    ServiceResponse<Permission> readAllPermissionsByRegistryNodeId(
            Long registryNodeId, Integer start, Integer limit);

    ServiceResponse<Permission> searchPermissions(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit);

    //-------------ROLE-----------------
    ServiceResponse<Role> readRole(Long roleId);

    ServiceResponse<Role> readAllRoles(Long registryNodeId, List<Long> exceptIds, Boolean isGlobal);

    ServiceResponse<Role> readAllRolesByRegistryNodeId(Long registryNodeId, Integer start, Integer offset);

    ServiceResponse<Role> readAllRolesByParentLookup(String parentLookup);

    ServiceResponse<Role> searchRole(
            Long registryNodeId, String term, List<Long> exceptIds,
            Integer start, Integer limit);

    ServiceResponse<Role> advancedSearchRoles(AdvancedSearchParams searchParams, Integer offset, Integer limit);

    ServiceResponse<Role> createRole(ServiceRequest<Role> request);

    ServiceResponse<Role> updateRole(ServiceRequest<Role> request);

    ServiceResponse<Role> cloneRole(String roleName, Long idOfRoleToClone);

    ServiceResponse deleteRole(Long roleId);

    ServiceResponse<Role> readEntityAssociatedContextRoles(Long entityId);

    //--------RESOURCE-LOCATION---------
    ServiceResponse<ResourceLocation> loadCachedResourceLocations(String packageLookup);

    ServiceResponse<ResourceLocation> readResourceLocation(Long resourceLocationId);

    ServiceResponse<ResourceLocation> readAllResourceLocations(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit);

    ServiceResponse<ResourceLocation> readAllResourceLocationsByRegistryNodeId(
            Long registryNodeId, Integer start, Integer limit);

    ServiceResponse<ResourceLocation> searchResourceLocations(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit);

    ServiceResponse<ResourceLocation> createResourceLocation(ServiceRequest<ResourceLocation> request);

    ServiceResponse<ResourceLocation> updateResourceLocation(ServiceRequest<ResourceLocation> request);

    ServiceResponse deleteResourceLocation(Long resourceLocationId);

    //----------MISCELLANEOUS-----------
    ServiceResponse<MappedPermissions> readAllSecuredRecordContextualPermissions();

    ServiceResponse<TypeFilter> loadIdFiltersByUser(Long userId);

    //--------------ROLE-ASSIGNMENT---------------
    ServiceResponse<User> readUsersByAssignedRoles(Long objectId, String objectType);

    ServiceResponse<AssignedRole> readAssignedRoles(Long objectId, String objectType);

    ServiceResponse<AssignedRole> readAssignedRolesByUser(Long objectId, String objectType, Long userId);

    ServiceResponse<AssignedRole> saveAssignedRolesByUser(
            Long objectId, String objectType, Long userId, Long roleId, Boolean assigned);

    //---------------CONTEXT ROLES----------------
    ServiceResponse<Role> readContextRoles(List<Long> excludeIds);

    ServiceResponse makeEntitySecurityEnabled(String entityLookup, Boolean securityEnabled);

    ServiceResponse<UserRole> saveContextUserRoles(List<UserRole> userRoles, Boolean syncRoles);

    ServiceResponse releaseContextUserRoles(List<UserRole> userRoles);

    ServiceResponse releaseContextUserRolesByPattern(UserRole userRole);

    ServiceResponse<SimpleIdentifier<Boolean>> isRecordOwner(UserRole userRole);

    ServiceResponse<AssignedRole> searchContextRolesAvailableForUser(UserRole userRole);

}
