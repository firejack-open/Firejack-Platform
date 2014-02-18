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

package net.firejack.platform.service.authority;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.authority.IAuthorityService;
import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.AdvancedSearchParams;
import net.firejack.platform.service.authority.broker.*;
import net.firejack.platform.service.authority.broker.permission.*;
import net.firejack.platform.service.authority.broker.resourcelocation.*;
import net.firejack.platform.service.authority.broker.role.*;
import net.firejack.platform.service.authority.broker.roleassignment.AssignRoleToUserWithEntityBroker;
import net.firejack.platform.service.authority.broker.roleassignment.ReadAssignedUserListByEntityRolesBroker;
import net.firejack.platform.service.authority.broker.roleassignment.ReadContextRoleListByEntityBroker;
import net.firejack.platform.service.authority.broker.sts.*;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.permission.CachedPermissionContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_AUTHORITY_SERVICE)
public class AuthorityServiceLocal implements IAuthorityService {

    @Autowired
    @Qualifier("readPermissionsByRolesBrokerEx")
    private ReadPermissionsByRolesBroker readPermissionsByRolesBroker;
    @Autowired
    @Qualifier("readSRPermissionsBrokerEx")
    private ReadSRPermissionsBroker readSRPermissionsBroker;
    @Autowired
    @Qualifier("readTypeFiltersForUserBroker")
    private ReadTypeFiltersForUserBroker readTypeFiltersForUserBroker;
    @Autowired
    @Qualifier("authenticationBrokerEx")
    private AuthenticationBroker authenticationBroker;
    @Autowired
    @Qualifier("forgotPasswordBroker")
    private ForgotPasswordBroker forgotPasswordBroker;
    @Autowired
    @Qualifier("resetPasswordBroker")
    private ResetPasswordBroker resetPasswordBroker;
    @Autowired
    @Qualifier("signOutBrokerEx")
    private SignOutBroker signOutBroker;
    @Autowired
    private CheckSessionActivityStatusBroker checkSessionActivityStatusBroker;
    @Autowired
    private CheckSessionExpirationBroker checkSessionExpirationBroker;
    @Autowired
    @Qualifier("openIDAuthenticationBrokerEx")
    private OpenIDAuthenticationBroker openIDAuthenticationBroker;
    @Autowired
    @Qualifier("facebookIDAuthenticationBrokerEx")
    private FacebookIDAuthenticationBroker facebookIDAuthenticationBroker;
    @Autowired
    private TwitterIDAuthenticationBroker twitterIDAuthenticationBroker;
    @Autowired
    private LinkedInAuthenticationBroker linkedInAuthenticationBroker;
    @Autowired
    private SiteMinderAuthenticationBroker siteMinderAuthenticationBroker;
    @Autowired
    private SynchronizeSMAdminsBroker synchronizeSMAdminsBroker;
    @Autowired
    private UpdateSMUserRolesBroker updateSMUserRolesBroker;
    @Autowired
    @Qualifier("getCachedResourceLocationListBroker")
    private GetCachedResourceLocationListBroker getCachedResourceLocationListBroker;
    @Autowired
    @Qualifier("readPermissionBrokerEx")
    private ReadPermissionBroker readPermissionBroker;
    @Autowired
    @Qualifier("savePermissionBrokerEx")
    private SavePermissionBroker savePermissionBroker;
    @Autowired
    @Qualifier("deletePermissionBrokerEx")
    private DeletePermissionBroker deletePermissionBroker;
    @Autowired
    @Qualifier("searchPermissionListBrokerEx")
    private SearchPermissionListBroker searchPermissionListBroker;
    @Autowired
    @Qualifier("readPermissionListBrokerEx")
    private ReadPermissionListBroker readPermissionListBroker;
    @Autowired
    @Qualifier("readPermissionListByRegistryNodeBrokerEx")
    private ReadPermissionListByRegistryNodeBroker readPermissionListByRegistryNodeBroker;
    @Autowired
    @Qualifier("readRoleBrokerEx")
    private ReadRoleBroker readRoleBroker;
    @Autowired
    @Qualifier("readRoleListBrokerEx")
    private ReadRoleListBroker readRoleListBroker;
    @Autowired
    @Qualifier("readRoleListByRegistryNodeBrokerEx")
    private ReadRoleListByRegistryNodeBroker readRoleListByRegistryNodeBroker;
    @Autowired
    @Qualifier("readRoleListByParentLookupBroker")
    private ReadRoleListByParentLookupBroker readRoleListByParentLookupBroker;
    @Autowired
    @Qualifier("searchRoleListBrokerEx")
    private SearchRoleListBroker searchRoleListBroker;
    @Autowired
    private AdvancedSearchRoleBroker advancedSearchRoleBroker;
    @Autowired
    @Qualifier("saveRoleBrokerEx")
    private SaveRoleBroker saveRoleBroker;
    @Autowired
    private CloneRoleBroker cloneRoleBroker;
    @Autowired
    @Qualifier("deleteRoleBrokerEx")
    private DeleteRoleBroker deleteRoleBroker;
    @Autowired
    private ReadAssociatedRolesByEntityIdBroker associatedRolesByEntityIdBroker;
    @Autowired
    @Qualifier("readResourceLocationBrokerEx")
    private ReadResourceLocationBroker readResourceLocationBroker;
    @Autowired
    @Qualifier("readResourceLocationListBrokerEx")
    private ReadResourceLocationListBroker readResourceLocationListBroker;
    @Autowired
    @Qualifier("searchResourceLocationListBrokerEx")
    private SearchResourceLocationListBroker searchResourceLocationListBroker;
    @Autowired
    @Qualifier("readResourceLocationListByRegistryNodeBrokerEx")
    private ReadResourceLocationListByRegistryNodeBroker readResourceLocationListByRegistryNodeBroker;
    @Autowired
    @Qualifier("saveResourceLocationBrokerEx")
    private SaveResourceLocationBroker saveResourceLocationBroker;
    @Autowired
    @Qualifier("deleteResourceLocationBrokerEx")
    private DeleteResourceLocationBroker deleteResourceLocationBroker;
    @Autowired
    @Qualifier("readAssignedUserListByEntityRolesBrokerEx")
    private ReadAssignedUserListByEntityRolesBroker readAssignedUserListByEntityRolesBroker;
    @Autowired
    @Qualifier("readContextRoleListByEntityBrokerEx")
    private ReadContextRoleListByEntityBroker readContextRoleListByEntityBroker;
    @Autowired
    @Qualifier("assignRoleToUserWithEntityBrokerEx")
    private AssignRoleToUserWithEntityBroker assignRoleToUserWithEntityBroker;
    @Autowired
    private ReadContextRoleListBroker readContextRoleListBroker;
    @Autowired
    private AllowInstantSecurityBroker allowInstantSecurityBroker;
    @Autowired
    private SaveContextUserRolesBroker saveContextUserRolesBroker;
    @Autowired
    private ReleaseContextUserRolesBroker releaseContextUserRolesBroker;
    @Autowired
    private ReleaseContextUserRolesByPatternBroker releaseContextUserRolesByPatternBroker;
    @Autowired
    private CheckUserIsOwnerBroker checkUserIsOwnerBroker;
    @Autowired
    private SearchContextRolesAvailableForUser searchContextRolesAvailableForUser;

    @Override
    public ServiceResponse<AuthenticationToken> processSTSSignIn(String username, String password, String browserIpAddress) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(AuthenticationBroker.PARAM_AUTH_USERNAME, username);
        params.put(AuthenticationBroker.PARAM_AUTH_PASSWORD, password);
        params.put(AuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);

        return authenticationBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSMWSSignIn(String username, String password, String ipAddress) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(AuthenticationBroker.PARAM_AUTH_USERNAME, username);
        params.put(AuthenticationBroker.PARAM_AUTH_PASSWORD, password);
        params.put(AuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, ipAddress);

        return authenticationBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSForgotPassword(String email, String resetPasswordPageUrl) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(ForgotPasswordBroker.PARAM_EMAIL, email);
        params.put(ForgotPasswordBroker.PARAM_RESET_PASSWORD_PAGE_URL, resetPasswordPageUrl);
        return forgotPasswordBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSResetPassword(String token) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(ResetPasswordBroker.PARAM_TOKEN, token);
        return resetPasswordBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSCertSignIn(String lookup, String name, String cert) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(AuthenticationBroker.PARAM_PACKAGE_LOOKUP, lookup);
        params.put(AuthenticationBroker.PARAM_SERVER_NAME, name);
        params.put(AuthenticationBroker.PARAM_AUTH_CERT, cert);

        return authenticationBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse processSTSSignOut(String token) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(SignOutBroker.PARAM_STS_TOKEN, token);

        return signOutBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionExpired(String sessionToken) {
        return checkSessionExpirationBroker.execute(new ServiceRequest());
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken) {
        return checkSessionActivityStatusBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(sessionToken)));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken, String browserIpAddress) {
        return checkSessionActivityStatusBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(sessionToken)));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processOpenIdSignIn(
            String email, Map<SupportedOpenIDAttribute, String> mappedAttributes, String browserIpAddress) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(OpenIDAuthenticationBroker.PARAM_EMAIL, email);
        params.put(OpenIDAuthenticationBroker.PARAM_OPEN_ID_ATTRIBUTES, mappedAttributes);
        params.put(OpenIDAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return openIDAuthenticationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processFacebookIdSignIn(
            Long facebookId, Map<String, String> mappedAttributes, String browserIpAddress) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(FacebookIDAuthenticationBroker.PARAM_FACEBOOK_ID, facebookId);
        params.put(FacebookIDAuthenticationBroker.PARAM_FACEBOOK_ID_ATTRIBUTES, mappedAttributes);
        params.put(FacebookIDAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return facebookIDAuthenticationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processTwitterIdSignIn(
            Long twitterId, Map<String, String> mappedAttributes, String browserIpAddress) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(TwitterIDAuthenticationBroker.PARAM_TWITTER_ID, twitterId);
        params.put(TwitterIDAuthenticationBroker.PARAM_TWITTER_ID_ATTRIBUTES, mappedAttributes);
        params.put(TwitterIDAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return twitterIDAuthenticationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<AuthenticationToken> processLinkedInSignIn(Map<String, String> mappedAttributes, String browserIpAddress) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(LinkedInAuthenticationBroker.PARAM_LINKEDIN_ATTRIBUTES, mappedAttributes);
        params.put(LinkedInAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return linkedInAuthenticationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    /*@Override
    public ServiceResponse<AuthenticationToken> processSiteMinderSignIn(String standardId, String browserIpAddress) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(SiteMinderAuthenticationBroker.PARAM_STANDARD_ID, standardId);
        params.put(LinkedInAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return siteMinderAuthenticationBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }*/

    @Override
    public ServiceResponse<AuthenticationToken> processSiteMinderSignIn(User user, String browserIpAddress) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SiteMinderAuthenticationBroker.PARAM_USER, user);
        params.put(LinkedInAuthenticationBroker.PARAM_BROWSER_IP_ADDRESS, browserIpAddress);
        return siteMinderAuthenticationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<String>> synchronizeSMAdmins(String smAdminUsers) {
        return synchronizeSMAdminsBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(smAdminUsers)));
    }

    @Override
    public ServiceResponse<User> updateSMUserRoles(User user) {
        return updateSMUserRolesBroker.execute(new ServiceRequest<User>(user));
    }

    @Override
    public ServiceResponse<UserPermission> loadGrantedPermissions() {
        List<UserPermission> userPermissions = getCachedPermissionContainer().loadGrantedActions(ContextManager.getPrincipal());
        return new ServiceResponse<UserPermission>(userPermissions, null, true);
    }

    @Override
    public ServiceResponse<UserPermission> loadDeniedPermissions() {
        List<UserPermission> userPermissions =
                getCachedPermissionContainer().loadDeniedActions(ContextManager.getPrincipal());

        return new ServiceResponse<UserPermission>(userPermissions, null, true);
    }

    @Override
    public ServiceResponse<UserPermission> loadPermissionsBySecuredRecord(Long securedRecordId) {
        List<UserPermission> userPermissions =
                getCachedPermissionContainer().loadUserPermissionsBySecuredRecords(
                        ContextManager.getPrincipal(), securedRecordId);

        return new ServiceResponse<UserPermission>(userPermissions, null, true);
    }

    @Override
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMap() {
        NamedValues<String> params = new NamedValues<String>();
        OPFContext context = OPFContext.getContext();
        params.put(ReadPermissionsByRolesBroker.PARAM_SESSION_TOKEN, context.getSessionToken());
        return readPermissionsByRolesBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMapForGuest() {
        return readPermissionsByRolesBroker.execute(
                new ServiceRequest<NamedValues<String>>(new NamedValues<String>()));
    }

    @Override
    public ServiceResponse<MappedPermissions> readAllSecuredRecordContextualPermissions() {
        return readSRPermissionsBroker.execute(new ServiceRequest());
    }

    @Override
    public ServiceResponse<TypeFilter> loadIdFiltersByUser(Long userId) {
        return readTypeFiltersForUserBroker.execute(
                new ServiceRequest<SimpleIdentifier<Long>>(
                        new SimpleIdentifier<Long>(userId)));
    }

    @Override
    public ServiceResponse<ResourceLocation> loadCachedResourceLocations(String packageLookup) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(GetCachedResourceLocationListBroker.PARAM_PACKAGE_LOOKUP, packageLookup);

        return getCachedResourceLocationListBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<Permission> createPermission(ServiceRequest<Permission> request) {
        return savePermissionBroker.execute(request);
    }

    @Override
    public ServiceResponse<Permission> updatePermission(ServiceRequest<Permission> request) {
        return savePermissionBroker.execute(request);
    }

    @Override
    public ServiceResponse deletePermission(Long permissionId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(permissionId);

        return deletePermissionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    @Override
    public ServiceResponse<Permission> readPermission(Long permissionId) {
        SimpleIdentifier<Long> id = new SimpleIdentifier<Long>(permissionId);

        return readPermissionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(id));
    }

    @Override
    public ServiceResponse<Permission> readAllPermissions(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadPermissionListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadPermissionListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(ReadPermissionListBroker.PARAM_LIMIT, limit);
        params.put(ReadPermissionListBroker.PARAM_START, start);

        return readPermissionListBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse<Permission> readAllPermissionsByRegistryNodeId(
            Long registryNodeId, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadPermissionListByRegistryNodeBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadPermissionListByRegistryNodeBroker.PARAM_LIMIT, limit);
        params.put(ReadPermissionListByRegistryNodeBroker.PARAM_START, start);
        return readPermissionListByRegistryNodeBroker.execute(
                new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Permission> searchPermissions(Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchPermissionListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(SearchPermissionListBroker.PARAM_TERM, term);
        params.put(SearchPermissionListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(SearchPermissionListBroker.PARAM_START, start);
        params.put(SearchPermissionListBroker.PARAM_LIMIT, limit);

        return searchPermissionListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Role> readRole(Long roleId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(roleId);
        return readRoleBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    @Override
    public ServiceResponse<Role> readAllRoles(Long registryNodeId, List<Long> exceptIds, Boolean isGlobal) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadRoleListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadRoleListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(ReadRoleListBroker.PARAM_IS_GLOBAL, isGlobal);

        return readRoleListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Role> readAllRolesByRegistryNodeId(Long registryNodeId, Integer start, Integer offset) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadRoleListByRegistryNodeBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadRoleListByRegistryNodeBroker.PARAM_START, start);
        params.put(ReadRoleListByRegistryNodeBroker.PARAM_LIMIT, offset);
        return readRoleListByRegistryNodeBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Role> readAllRolesByParentLookup(String parentLookup) {
        SimpleIdentifier<String> parentLookupParam = new SimpleIdentifier<String>(parentLookup);
        return readRoleListByParentLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(parentLookupParam));
    }

    @Override
    public ServiceResponse<Role> searchRole(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchRoleListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(SearchRoleListBroker.PARAM_TERM, term);
        params.put(SearchRoleListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(SearchRoleListBroker.PARAM_START, start);
        params.put(SearchRoleListBroker.PARAM_LIMIT, limit);

        return searchRoleListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Role> advancedSearchRoles(AdvancedSearchParams searchParams, Integer offset, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(AdvancedSearchRoleBroker.PARAM_LIMIT, limit);
        params.put(AdvancedSearchRoleBroker.PARAM_OFFSET, offset);
        params.put(AdvancedSearchRoleBroker.PARAM_QUERY_PARAMETERS, searchParams);
        return advancedSearchRoleBroker.execute(new ServiceRequest<NamedValues>(params));
    }

    @Override
    public ServiceResponse<Role> createRole(ServiceRequest<Role> request) {
        return saveRoleBroker.execute(request);
    }

    @Override
    public ServiceResponse<Role> updateRole(ServiceRequest<Role> request) {
        return saveRoleBroker.execute(request);
    }

    @Override
    public ServiceResponse<Role> cloneRole(String roleName, Long idOfRoleToClone) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(CloneRoleBroker.PARAM_CLONED_ROLE_ID, idOfRoleToClone);
        params.put(CloneRoleBroker.PARAM_NEW_ROLE_NAME, roleName);
        return cloneRoleBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse deleteRole(Long roleId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(roleId);
        return deleteRoleBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    @Override
    public ServiceResponse<Role> readEntityAssociatedContextRoles(Long entityId) {
        SimpleIdentifier<Long> entityIdParam = new SimpleIdentifier<Long>(entityId);
        return associatedRolesByEntityIdBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(entityIdParam));
    }

    @Override
    public ServiceResponse<ResourceLocation> readResourceLocation(Long resourceLocationId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(resourceLocationId);
        return readResourceLocationBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    @Override
    public ServiceResponse<ResourceLocation> readAllResourceLocations(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadResourceLocationListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadResourceLocationListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(ReadResourceLocationListBroker.PARAM_START, start);
        params.put(ReadResourceLocationListBroker.PARAM_LIMIT, limit);

        return readResourceLocationListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<ResourceLocation> readAllResourceLocationsByRegistryNodeId(Long registryNodeId, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ReadResourceLocationListByRegistryNodeBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(ReadResourceLocationListByRegistryNodeBroker.PARAM_START, start);
        params.put(ReadResourceLocationListByRegistryNodeBroker.PARAM_LIMIT, limit);
        return readResourceLocationListByRegistryNodeBroker.execute(
                new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<ResourceLocation> searchResourceLocations(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchResourceLocationListBroker.PARAM_REGISTRY_NODE_ID, registryNodeId);
        params.put(SearchResourceLocationListBroker.PARAM_TERM, term);
        params.put(SearchResourceLocationListBroker.PARAM_ID_TO_EXCLUDE, exceptIds);
        params.put(SearchResourceLocationListBroker.PARAM_START, start);
        params.put(SearchResourceLocationListBroker.PARAM_LIMIT, limit);

        return searchResourceLocationListBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<ResourceLocation> createResourceLocation(ServiceRequest<ResourceLocation> request) {
        return saveResourceLocationBroker.execute(request);
    }

    @Override
    public ServiceResponse<ResourceLocation> updateResourceLocation(ServiceRequest<ResourceLocation> request) {
        return saveResourceLocationBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteResourceLocation(Long resourceLocationId) {
        SimpleIdentifier<Long> idParam = new SimpleIdentifier<Long>(resourceLocationId);
        return deleteResourceLocationBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(idParam));
    }

    public CachedPermissionContainer getCachedPermissionContainer() {
        OPFContext context = OPFContext.getContext();
        return (CachedPermissionContainer) context.getPermissionContainer();
    }

    @Override
    public ServiceResponse<User> readUsersByAssignedRoles(Long objectId, String objectType) {
        NamedValues<Object> namedValues = new NamedValues<Object>();
        namedValues.put(ReadAssignedUserListByEntityRolesBroker.PARAM_OBJECT_ID, objectId);
        namedValues.put(ReadAssignedUserListByEntityRolesBroker.PARAM_OBJECT_TYPE, objectType);

        return readAssignedUserListByEntityRolesBroker.execute(
                new ServiceRequest<NamedValues<Object>>(namedValues));
    }

    @Override
    public ServiceResponse<AssignedRole> readAssignedRoles(Long objectId, String objectType) {
        NamedValues<Object> namedValues = new NamedValues<Object>();
        namedValues.put(ReadContextRoleListByEntityBroker.PARAM_OBJECT_ID, objectId);
        namedValues.put(ReadContextRoleListByEntityBroker.PARAM_OBJECT_TYPE, objectType);

        return readContextRoleListByEntityBroker.execute(new ServiceRequest<NamedValues<Object>>(namedValues));
    }

    @Override
    public ServiceResponse<AssignedRole> readAssignedRolesByUser(Long objectId, String objectType, Long userId) {
        NamedValues<Object> namedValues = new NamedValues<Object>();
        namedValues.put(ReadContextRoleListByEntityBroker.PARAM_OBJECT_ID, objectId);
        namedValues.put(ReadContextRoleListByEntityBroker.PARAM_OBJECT_TYPE, objectType);
        namedValues.put(ReadContextRoleListByEntityBroker.PARAM_USER_ID, userId);

        return readContextRoleListByEntityBroker.execute(new ServiceRequest<NamedValues<Object>>(namedValues));
    }

    @Override
    public ServiceResponse<AssignedRole> saveAssignedRolesByUser(
            Long objectId, String objectType, Long userId, Long roleId, Boolean assigned) {
        NamedValues<Object> namedValues = new NamedValues<Object>();
        namedValues.put(AssignRoleToUserWithEntityBroker.PARAM_OBJECT_ID, objectId);
        namedValues.put(AssignRoleToUserWithEntityBroker.PARAM_OBJECT_TYPE, objectType);
        namedValues.put(AssignRoleToUserWithEntityBroker.PARAM_USER_ID, userId);
        namedValues.put(AssignRoleToUserWithEntityBroker.PARAM_ROLE_ID, roleId);
        namedValues.put(AssignRoleToUserWithEntityBroker.PARAM_ASSIGNED_FLAG, assigned);

        return assignRoleToUserWithEntityBroker.execute(new ServiceRequest<NamedValues<Object>>(namedValues));
    }

    @Override
    public ServiceResponse<Role> readContextRoles(List<Long> excludeIds) {
        SimpleIdentifier<List<Long>> data = new SimpleIdentifier<List<Long>>(excludeIds);
        return readContextRoleListBroker.execute(new ServiceRequest<SimpleIdentifier<List<Long>>>(data));
    }

    @Override
    public ServiceResponse makeEntitySecurityEnabled(String entityLookup, Boolean securityEnabled) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(AllowInstantSecurityBroker.PARAM_ENTITY_LOOKUP, entityLookup);
        params.put(AllowInstantSecurityBroker.PARAM_SECURITY_ENABLED, securityEnabled);
        return allowInstantSecurityBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<UserRole> saveContextUserRoles(List<UserRole> userRoles, Boolean syncRoles) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SaveContextUserRolesBroker.PARAM_USER_ROLES, userRoles);
        params.put(SaveContextUserRolesBroker.PARAM_SYNC_ROLES, syncRoles);
        return saveContextUserRolesBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse releaseContextUserRoles(List<UserRole> userRoles) {
        return releaseContextUserRolesBroker.execute(new ServiceRequest<UserRole>(userRoles));
    }

    @Override
    public ServiceResponse releaseContextUserRolesByPattern(UserRole userRole) {
        return releaseContextUserRolesByPatternBroker.execute(new ServiceRequest<UserRole>(userRole));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isRecordOwner(UserRole userRole) {
        return checkUserIsOwnerBroker.execute(new ServiceRequest<UserRole>(userRole));
    }

    @Override
    public ServiceResponse<AssignedRole> searchContextRolesAvailableForUser(UserRole userRole) {
        return searchContextRolesAvailableForUser.execute(new ServiceRequest<UserRole>(userRole));
    }

}