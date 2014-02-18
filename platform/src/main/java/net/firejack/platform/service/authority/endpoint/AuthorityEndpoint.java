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

package net.firejack.platform.service.authority.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.AdvancedSearchParams;
import net.firejack.platform.core.utils.LongList;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.facebook.FacebookIDUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.openid.OpenIDUtils;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("authorityEndpoint")
@Path("authority")
public class AuthorityEndpoint implements IAuthorityEndpoint {

    @Context
    private HttpHeaders headers;
	@Resource
    private WebServiceContext context;

    //============= Authentication/Authorization =============

    @HEAD
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/session/active-status/check/{sessionToken}")
    public Response checkSessionStatus(@PathParam("sessionToken") String sessionToken) {
        ServiceResponse<SimpleIdentifier<Boolean>> response =
                OPFEngine.AuthorityService.isSessionTokenActive(sessionToken);
        NewCookie cookie = new NewCookie(
                OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_EXPIRED_ATTRIBUTE,
                response.getItem().getIdentifier().toString());
        return Response.ok().cookie(cookie).build();
    }

    public ServiceResponse<SimpleIdentifier<Boolean>> checkSessionStatusWS(String sessionToken) {
	    return OPFEngine.AuthorityService.isSessionTokenActive(sessionToken);
    }

    @HEAD
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/session/expiration/check")
    public Response checkSessionExpirationStatus() {
        ServiceResponse<SimpleIdentifier<Boolean>> response =
                OPFEngine.AuthorityService.isSessionExpired(OPFContext.getContext().getSessionToken());
        NewCookie cookie = new NewCookie(
                OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_EXPIRED_ATTRIBUTE,
                response.getItem().getIdentifier().toString());
        return Response.ok().cookie(cookie).build();
    }

	public ServiceResponse<SimpleIdentifier<Boolean>> checkSessionExpirationStatusWS() {
		return OPFEngine.AuthorityService.isSessionExpired(OPFContext.getContext().getSessionToken());
	}

	@POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/sign-in")
    public ServiceResponse<AuthenticationToken> authenticate(
            @HeaderParam(OpenFlameSecurityConstants.USER_NAME_REQUEST_HEADER) String userName,
            @HeaderParam(OpenFlameSecurityConstants.USER_PASSWORD_REQUEST_HEADER) String password,
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress,
            @Context HttpServletRequest request) {
        browserIpAddress = StringUtils.isBlank(browserIpAddress) ?
                (request == null ? null : request.getRemoteAddr()) : browserIpAddress;
        return OPFEngine.AuthorityService.processSTSSignIn(userName, password, browserIpAddress);
    }

	@POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/sm-ws/sign-in")
    public ServiceResponse<AuthenticationToken> processSMWSSignIn(
            @HeaderParam(OpenFlameSecurityConstants.USER_NAME_REQUEST_HEADER) String userName,
            @HeaderParam(OpenFlameSecurityConstants.USER_PASSWORD_REQUEST_HEADER) String password,
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress,
            @Context HttpServletRequest request) {
        browserIpAddress = StringUtils.isBlank(browserIpAddress) ?
                (request == null ? null : request.getRemoteAddr()) : browserIpAddress;
        return OPFEngine.AuthorityService.processSMWSSignIn(userName, password, browserIpAddress);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/sign-in/{lookup}/{name}")
    @Override
    public ServiceResponse<AuthenticationToken> authenticateUsingCert(
            @PathParam("lookup") String lookup,
            @PathParam("name") String name,
            @HeaderParam(OpenFlameSecurityConstants.OPF_CERTIFICATE_HEADER) String certificate) {
        return OPFEngine.AuthorityService.processSTSCertSignIn(lookup, name, certificate);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/forgot-password")
    @Override
    public ServiceResponse<AuthenticationToken> forgotPassword(
            @HeaderParam(OpenFlameSecurityConstants.EMAIL_REQUEST_HEADER) String email,
            @HeaderParam(OpenFlameSecurityConstants.RESET_PASSWORD_PAGE_URL_HEADER) String resetPasswordPageUrl) {
        return OPFEngine.AuthorityService.processSTSForgotPassword(email, resetPasswordPageUrl);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/reset-password")
    @Override
    public ServiceResponse<AuthenticationToken> resetPassword(
            @HeaderParam(OpenFlameSecurityConstants.TOKEN_REQUEST_HEADER) String token) {
        return OPFEngine.AuthorityService.processSTSResetPassword(token);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/sign-out")
    public ServiceResponse signOut(
            @HeaderParam(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE) String token) {
        return OPFEngine.AuthorityService.processSTSSignOut(token);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/openid-sign-in")
    @SuppressWarnings("unchecked")
    public ServiceResponse<AuthenticationToken> authenticateThroughOpenID(
            @HeaderParam("email") String email,
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress) {
        Map<SupportedOpenIDAttribute, String> openIDHeaders;
	    if (headers != null) {
		    openIDHeaders = OpenIDUtils.prepareOpenIDUserAttributes(headers.getRequestHeaders());
	    } else {
		    openIDHeaders = OpenIDUtils.prepareOpenIDUserAttributes(
                    (Map<String, List<String>>) context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS));
	    }
	    return OPFEngine.AuthorityService.processOpenIdSignIn(email, openIDHeaders, browserIpAddress);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/facebook-sign-in")
    public ServiceResponse<AuthenticationToken> authenticateThroughFacebookID(
            @HeaderParam("facebookId") Long facebookId,
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress) {
        Map<String, String> facebookIdHeaders = FacebookIDUtils.prepareFacebookIDUserAttributes(headers.getRequestHeaders());
	    return OPFEngine.AuthorityService.processFacebookIdSignIn(facebookId, facebookIdHeaders, browserIpAddress);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/twitter-sign-in")
    public ServiceResponse<AuthenticationToken> authenticateThroughTwitterID(
            @HeaderParam("twitterId") Long twitterId,
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress) {
        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
        Map<String, String> twitterHeaders = new HashMap<String, String>();
        for (MultivaluedMap.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            List<String> valueList = entry.getValue();
            if (entry.getKey().startsWith("tw_") && valueList != null && !valueList.isEmpty()) {
                twitterHeaders.put(entry.getKey(), entry.getValue().get(0));
            }
        }
	    return OPFEngine.AuthorityService.processTwitterIdSignIn(twitterId, twitterHeaders, browserIpAddress);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/linkedin-sign-in")
    public ServiceResponse<AuthenticationToken> authenticateThroughLinkedIn(
            @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress) {
        MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
        Map<String, String> linkedInHeaders = new HashMap<String, String>();
        for (MultivaluedMap.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            List<String> valueList = entry.getValue();
            if (valueList != null && !valueList.isEmpty()) {
                linkedInHeaders.put(entry.getKey(), entry.getValue().get(0));
            }
        }
	    return OPFEngine.AuthorityService.processLinkedInSignIn(linkedInHeaders, browserIpAddress);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/sts/siteminder-sign-in")
    public ServiceResponse<AuthenticationToken> authenticateSiteMinderAccount(
            ServiceRequest<User> request, @HeaderParam(OpenFlameSecurityConstants.IP_ADDRESS_HEADER) String browserIpAddress) {
	    return OPFEngine.AuthorityService.processSiteMinderSignIn(request.getData(), browserIpAddress);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/siteminder-synchronize-users")
    public ServiceResponse<SimpleIdentifier<String>> authenticateSiteMinderAccount(
            ServiceRequest<SimpleIdentifier<String>> request) {
	    return OPFEngine.AuthorityService.synchronizeSMAdmins(request.getData().getIdentifier());
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/siteminder-update-user")
    public ServiceResponse<User> updateSMUserRoles(ServiceRequest<User> request) {
	    return OPFEngine.AuthorityService.updateSMUserRoles(request.getData());
    }

    //============= Permission =============

    @GET
    @Path("/permission/map-by-roles")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMap() {
        return OPFEngine.AuthorityService.readPermissionsByRolesMap();
    }

    @GET
    @Path("/permission/map-by-roles/for-guest")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMapForGuest() {
        return OPFEngine.AuthorityService.readPermissionsByRolesMapForGuest();
    }

    @GET
    @Path("/permission/sr")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<MappedPermissions> readSRPermissions() {
        return OPFEngine.AuthorityService.readPermissionsByRolesMapForGuest();
    }

    @GET
    @Path("/permission/id-filters/{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TypeFilter> readIdFiltersForUser(@PathParam(value = "userId") Long userId) {
        return OPFEngine.AuthorityService.loadIdFiltersByUser(userId);
    }

    @GET
    @Path("/permission/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> readPermission(@PathParam("id") Long id) {
        return OPFEngine.AuthorityService.readPermission(id);
    }

    @GET
    @Path("/permission")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> readAllPermissions(
            @QueryParam("node") Long registryNodeId, @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.readAllPermissions(registryNodeId, exceptIds, start, limit);
    }

    @GET
    @Path("/permission/node/{registryNodeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> readAllPermissionsByRegistryNodeId(
            @PathParam("registryNodeId") Long registryNodeId,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.readAllPermissionsByRegistryNodeId(registryNodeId, start, limit);
    }

    @GET
    @Path("/permission/node/search/{registryNodeId}/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> searchPermissionsByRegistryNode(
            @PathParam("registryNodeId") Long registryNodeId, @PathParam("term") String term,
            @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchPermissions(registryNodeId, term, exceptIds, start, limit);
    }

    @GET
    @Path("/permission/search/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> searchPermissions(
            @PathParam("term") String term, @QueryParam("registryNodeId") Long registryNodeId,
            @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchPermissions(registryNodeId, term, exceptIds, start, limit);
    }

    @POST
    @Path("/permission")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> createPermission(ServiceRequest<Permission> request) {
        return OPFEngine.AuthorityService.createPermission(request);
    }

    @PUT
    @Path("/permission/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Permission> updatePermission(
            @PathParam("id") Long id, ServiceRequest<Permission> request) {
        return OPFEngine.AuthorityService.updatePermission(request);
    }

    @DELETE
    @Path("/permission/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deletePermission(@PathParam("id") Long id) {
        return OPFEngine.AuthorityService.deletePermission(id);
    }

    //================ Role ================
    @GET
    @Path("/role/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readRole(@PathParam("id") Long id) {
        return OPFEngine.AuthorityService.readRole(id);
    }

    @GET
    @Path("/role")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readAllRoles(@QueryParam("node") Long registryNodeId,
                                              @QueryParam("exceptIds") LongList exceptIds,
                                              @QueryParam("isGlobal") Boolean isGlobal) {
        return OPFEngine.AuthorityService.readAllRoles(registryNodeId, exceptIds, isGlobal);
    }

    @GET
    @Path("/role/node/{registryNodeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readAllRolesByRegistryNodeId(
            @PathParam("registryNodeId") Long registryNodeId,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.readAllRolesByRegistryNodeId(registryNodeId, start, limit);
    }

    @GET
    @Path("/role/by-path/{path}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readAllRolesByParentLookup(@PathParam("path") String parentLookup) {
        return OPFEngine.AuthorityService.readAllRolesByParentLookup(parentLookup);
    }

    @GET
    @Path("/role/node/search/{registryNodeId}/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> searchRoleByRegistryNode(
            @PathParam("registryNodeId") Long registryNodeId, @PathParam("term") String term,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchRole(registryNodeId, term, null, start, limit);
    }

    @GET
    @Path("/role/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> searchRole(
            @QueryParam("term") String term, @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchRole(null, term, exceptIds, start, limit);
    }

    @GET
    @Path("/role/context/associated-with-entity")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readAssociatedContextRolesByEntityId(@QueryParam("entityId") Long entityId) {
        return OPFEngine.AuthorityService.readEntityAssociatedContextRoles(entityId);
    }

    @GET
    @Path("/role/clone")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> cloneRole(
            @QueryParam("roleName") String roleName, @QueryParam("idOfRoleToClone") Long idOfRoleToClone) {
        return OPFEngine.AuthorityService.cloneRole(roleName, idOfRoleToClone);
    }

    @POST
    @Path("/role")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> createRole(ServiceRequest<Role> request) {
        return OPFEngine.AuthorityService.createRole(request);
    }

    @PUT
    @Path("/role/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @SuppressWarnings("unused")
    public ServiceResponse<Role> updateRole(@PathParam("id") Long id, ServiceRequest<Role> request) {
        return OPFEngine.AuthorityService.updateRole(request);
    }

    @DELETE
    @Path("/role/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteRole(@PathParam(value = "id") Long id) {
        return OPFEngine.AuthorityService.deleteRole(id);
    }

    //================ Resource Location ================
    @GET
    @Path("/resource-location/cached/{packageLookup}")
    public ServiceResponse<ResourceLocation> getCachedResourceLocations(@PathParam("packageLookup") String packageLookup) {
        return OPFEngine.AuthorityService.loadCachedResourceLocations(packageLookup);
    }

    @GET
    @Path("/resource-location/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> readResourceLocation(@PathParam("id") Long id) {
        return OPFEngine.AuthorityService.readResourceLocation(id);
    }

    @GET
    @Path("/resource-location")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> readAllResourceLocations(
            @QueryParam("node") Long registryNodeId, @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.readAllResourceLocations(registryNodeId, exceptIds, start, limit);
    }

    @GET
    @Path("/resource-location/node/{registryNodeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> readAllResourceLocationsByRegistryNodeId(
            @PathParam("registryNodeId") Long registryNodeId,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.readAllResourceLocationsByRegistryNodeId(registryNodeId, start, limit);
    }

    @GET
    @Path("/resource-location/node/search/{registryNodeId}/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> searchResourceLocationByRegistryNode(
            @PathParam("registryNodeId") Long registryNodeId, @PathParam("term") String term,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchResourceLocations(registryNodeId, term, null, start, limit);
    }

    @GET
    @Path("/resource-location/search/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> searchResourceLocations(
            @PathParam("term") String term, @QueryParam("exceptIds") LongList exceptIds,
            @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.searchResourceLocations(null, term, exceptIds, start, limit);
    }

    @POST
    @Path("/resource-location")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ResourceLocation> createResourceLocation(ServiceRequest<ResourceLocation> request) {
        return OPFEngine.AuthorityService.createResourceLocation(request);
    }

    @PUT
    @Path("/resource-location/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @SuppressWarnings("unused")
    public ServiceResponse<ResourceLocation> updateResourceLocation(
            @PathParam("id") Long id, ServiceRequest<ResourceLocation> request) {
        return OPFEngine.AuthorityService.updateResourceLocation(request);
    }

    @DELETE
    @Path("/resource-location/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteResourceLocation(@PathParam(value = "id") Long id) {
        return OPFEngine.AuthorityService.deleteResourceLocation(id);
    }

    @GET
    @Path("/role-assignment/users/{objectId}/{objectType}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> readUsersByAssignedRoles(
            @PathParam("objectId") Long objectId, @PathParam("objectType") String objectType) {
        return OPFEngine.AuthorityService.readUsersByAssignedRoles(objectId, objectType);
    }

    @GET
    @Path("/role-assignment/{objectId}/{objectType}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<AssignedRole> readAssignedRoles(
            @PathParam("objectId") Long objectId, @PathParam("objectType") String objectType) {
        return OPFEngine.AuthorityService.readAssignedRoles(objectId, objectType);
    }

    @GET
    @Path("/role-assignment/by-user/{objectId}/{objectType}/{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<AssignedRole> readAssignedRolesByUser(
            @PathParam("objectId") Long objectId, @PathParam("objectType") String objectType,
            @PathParam("userId") Long userId) {
        return OPFEngine.AuthorityService.readAssignedRolesByUser(objectId, objectType, userId);
    }

    @PUT
    @Path("/role-assignment/{objectId}/{objectType}/{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<AssignedRole> saveAssignedRolesByUser(
            @PathParam("objectId") Long objectId, @PathParam("objectType") String objectType,
            @PathParam("userId") Long userId, @QueryParam("roleId") Long roleId,
            @QueryParam("assigned") Boolean assigned) {
        return OPFEngine.AuthorityService.saveAssignedRolesByUser(objectId, objectType, userId, roleId, assigned);
    }

    @GET
    @Path("/role/context")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> readContextRoles(@QueryParam("excludeIds") LongList excludeIds) {
        return OPFEngine.AuthorityService.readContextRoles(excludeIds);
    }

    @POST
    @Path("/role/advanced-search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Role> advancedSearchRoles(
            ServiceRequest<AdvancedSearchParams> searchRequest,
            @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
        return OPFEngine.AuthorityService.advancedSearchRoles(searchRequest.getData(), offset, limit);
    }

    @PUT
    @Path("/role/enable-instant-security-on-package")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse makeEntitySecurityEnabled(
            @QueryParam("entityLookup") String entityLookup, @QueryParam("securityEnabled") Boolean securityEnabled) {
        return OPFEngine.AuthorityService.makeEntitySecurityEnabled(entityLookup, securityEnabled);
    }

    @PUT
    @Path("/user-role/save-context-user-roles")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<UserRole> saveContextUserRoles(ServiceRequest<UserRole> request, @QueryParam("syncRoles") Boolean syncRoles) {
        return OPFEngine.AuthorityService.saveContextUserRoles(request.getDataList(), syncRoles);
    }

    @PUT
    @Path("/user-role/release-context-user-roles")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse releaseContextUserRoles(ServiceRequest<UserRole> request) {
        return OPFEngine.AuthorityService.releaseContextUserRoles(request.getDataList());
    }

    @PUT
    @Path("/user-role/release-context-user-roles-by-pattern")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse releaseContextUserRolesByPattern(ServiceRequest<UserRole> request) {
        return OPFEngine.AuthorityService.releaseContextUserRolesByPattern(request.getData());
    }

    @POST
    @Path("/user-role/is-owner")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SimpleIdentifier<Boolean>> isRecordOwner(ServiceRequest<UserRole> request) {
        return OPFEngine.AuthorityService.isRecordOwner(request.getData());
    }

    @POST
    @Path("/user-role/search-by-pattern")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AssignedRole> searchContextRolesAvailableForUser(ServiceRequest<UserRole> request) {
        return OPFEngine.AuthorityService.searchContextRolesAvailableForUser(request.getData());
    }

}