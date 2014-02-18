/**
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
package net.firejack.platform.service.authority.endpoint;

import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.AdvancedSearchParams;
import net.firejack.platform.core.utils.LongList;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.authority.endpoint.IAuthorityEndpoint")
public interface IAuthorityEndpoint {
	@WebMethod(operationName = "checkSessionStatus")
	ServiceResponse<SimpleIdentifier<Boolean>> checkSessionStatusWS(@WebParam(name = "sessionToken") String sessionToken);

	@WebMethod(operationName = "checkSessionExpirationStatus")
	ServiceResponse<SimpleIdentifier<Boolean>> checkSessionExpirationStatusWS();

	/*@WebMethod
	ServiceResponse<AuthenticationToken> authenticate(
			@WebParam(name = OpenFlameSecurityConstants.USER_NAME_REQUEST_HEADER) String userName,
			@WebParam(name = OpenFlameSecurityConstants.USER_PASSWORD_REQUEST_HEADER) String password);*/

    @WebMethod
    ServiceResponse<AuthenticationToken> authenticateUsingCert(
		    @WebParam(name = "lookup") String  lookup,
		    @WebParam(name = "name") String name,
            @WebParam(name = OpenFlameSecurityConstants.OPF_CERTIFICATE_HEADER) String certificate);

    @WebMethod
    ServiceResponse<AuthenticationToken> forgotPassword(
            @WebParam(name = OpenFlameSecurityConstants.EMAIL_REQUEST_HEADER) String email,
            @WebParam(name = OpenFlameSecurityConstants.RESET_PASSWORD_PAGE_URL_HEADER) String resetPasswordPageUrl);

    @WebMethod
    ServiceResponse<AuthenticationToken> resetPassword(
            @WebParam(name = OpenFlameSecurityConstants.TOKEN_REQUEST_HEADER) String token);

	@WebMethod
	ServiceResponse signOut(@WebParam(name = OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE) String token);

	@WebMethod
	ServiceResponse<AuthenticationToken> authenticateThroughOpenID(
            @WebParam(name = "email") String email, @WebParam(name = "browserIpAddress") String browserIpAddress);

    @WebMethod
    ServiceResponse<AuthenticationToken> authenticateThroughFacebookID(
            @WebParam(name = "facebookId") Long facebookId, @WebParam(name = "browserIpAddress") String browserIpAddress);

    //================== Permission ==================
    @WebMethod
	ServiceResponse<MappedPermissions> readPermissionsByRolesMap();

	@WebMethod
	ServiceResponse<MappedPermissions> readPermissionsByRolesMapForGuest();

	@WebMethod
	ServiceResponse<MappedPermissions> readSRPermissions();

	@WebMethod
	ServiceResponse<TypeFilter> readIdFiltersForUser(@WebParam(name = "userId") Long userId);

	@WebMethod
	ServiceResponse<Permission> readPermission(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<Permission> readAllPermissions(
            @WebParam(name = "node") Long registryNodeId, @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<Permission> readAllPermissionsByRegistryNodeId(
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<Permission> searchPermissionsByRegistryNode(
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "term") String term,
            @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start,
            @WebParam(name = "limit") Integer limit);

    @WebMethod
    public ServiceResponse<Permission> searchPermissions(
            @WebParam(name = "term") String term,
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start,
            @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<Permission> createPermission(@WebParam(name = "request") ServiceRequest<Permission> request);

	@WebMethod
	ServiceResponse<Permission> updatePermission(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Permission> request);

	@WebMethod
	ServiceResponse deletePermission(@WebParam(name = "id") Long id);

    //==================== Role ======================
    @WebMethod
	ServiceResponse<Role> readRole(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<Role> readAllRoles(
			@WebParam(name = "node") Long registryNodeId,
			@WebParam(name = "exceptIds") LongList exceptIds,
			@WebParam(name = "isGlobal") Boolean isGlobal);

	@WebMethod
	ServiceResponse<Role> readAllRolesByRegistryNodeId(
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

    @WebMethod
    ServiceResponse<Role> readAllRolesByParentLookup(@WebParam(name = "path") String parentLookup);

	@WebMethod
	ServiceResponse<Role> searchRoleByRegistryNode(
            @WebParam(name = "registryNodeId") Long registryNodeId, @WebParam(name = "term") String term,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<Role> searchRole(
            @WebParam(name = "term") String term, @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

    @WebMethod
    ServiceResponse<Role> readAssociatedContextRolesByEntityId(@WebParam(name = "entityId") Long entityId);

    @WebMethod
    ServiceResponse<Role> cloneRole(
            @WebParam(name = "roleName") String roleName, @WebParam(name = "idOfRoleToClone") Long idOfRoleToClone);

	@WebMethod
	ServiceResponse<Role> createRole(@WebParam(name = "request") ServiceRequest<Role> request);

	@WebMethod
	ServiceResponse<Role> updateRole(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Role> request);

	@WebMethod
	ServiceResponse deleteRole(@WebParam(name = "id") Long id);

    //====================== Resource Location =======================

    @WebMethod
	ServiceResponse<ResourceLocation> getCachedResourceLocations(
            @WebParam(name = "packageLookup") String packageLookup);

	@WebMethod
	ServiceResponse<ResourceLocation> readResourceLocation(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<ResourceLocation> readAllResourceLocations(
            @WebParam(name = "node") Long registryNodeId, @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<ResourceLocation> readAllResourceLocationsByRegistryNodeId(
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<ResourceLocation> searchResourceLocationByRegistryNode(
            @WebParam(name = "registryNodeId") Long registryNodeId, @WebParam(name = "term") String term,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<ResourceLocation> searchResourceLocations(
            @WebParam(name = "term") String term, @WebParam(name = "exceptIds") LongList exceptIds,
            @WebParam(name = "start") Integer start, @WebParam(name = "limit") Integer limit);

	@WebMethod
	ServiceResponse<ResourceLocation> createResourceLocation(
            @WebParam(name = "request") ServiceRequest<ResourceLocation> request);

	@WebMethod
	ServiceResponse<ResourceLocation> updateResourceLocation(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<ResourceLocation> request);

	@WebMethod
	ServiceResponse deleteResourceLocation(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<User> readUsersByAssignedRoles(
            @WebParam(name = "objectId") Long objectId, @WebParam(name = "objectType") String objectType);

    @WebMethod
    ServiceResponse<AssignedRole> readAssignedRoles(
            @WebParam(name = "objectId") Long objectId, @WebParam(name = "objectType") String objectType);

    @WebMethod
    ServiceResponse<AssignedRole> readAssignedRolesByUser(
            @WebParam(name = "objectId") Long objectId, @WebParam(name = "objectType") String objectType,
            @WebParam(name = "userId") Long userId);

    @WebMethod
    ServiceResponse<AssignedRole> saveAssignedRolesByUser(
            @WebParam(name = "objectId") Long objectId, @WebParam(name = "objectType") String objectType,
            @WebParam(name = "userId") Long userId, @WebParam(name = "roleId") Long roleId,
            @WebParam(name = "assigned") Boolean assigned);

    @WebMethod
    ServiceResponse<Role> readContextRoles(@WebParam(name = "excludeIds") LongList excludeIds);

    @WebMethod
    ServiceResponse<Role> advancedSearchRoles(
            @WebParam(name = "request") ServiceRequest<AdvancedSearchParams> request,
            @WebParam(name = "offset") Integer offset, @WebParam(name = "limit") Integer limit);

    @WebMethod
    ServiceResponse makeEntitySecurityEnabled(
            @WebParam(name = "entityLookup") String entityLookup,
            @WebParam(name = "securityEnabled") Boolean securityEnabled);

    @WebMethod
    ServiceResponse<UserRole> saveContextUserRoles(
            @WebParam(name = "request") ServiceRequest<UserRole> request,
            @WebParam(name = "syncRoles") Boolean syncRoles);

    @WebMethod
    ServiceResponse releaseContextUserRoles(@WebParam(name = "request") ServiceRequest<UserRole> request);

    @WebMethod
    ServiceResponse releaseContextUserRolesByPattern(@WebParam(name = "request") ServiceRequest<UserRole> request);

    @WebMethod
    ServiceResponse<SimpleIdentifier<Boolean>> isRecordOwner(@WebParam(name = "request") ServiceRequest<UserRole> request);

    @WebMethod
    ServiceResponse<AssignedRole> searchContextRolesAvailableForUser(ServiceRequest<UserRole> request);

}
