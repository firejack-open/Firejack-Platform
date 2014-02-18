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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.authority.domain.*;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.AdvancedSearchParams;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.handler.Builder;
import net.firejack.platform.web.handler.ErrorHandler;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.security.facebook.FacebookIDUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.openid.OpenIDUtils;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.permission.CachedPermissionContainer;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuthorityServiceProxy extends AbstractServiceProxy implements IAuthorityService {

    private static CachedPermissionContainer permissionContainer;
    static {
        permissionContainer = new CachedPermissionContainer();
    }

	public AuthorityServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/authority";
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSSignIn(String username, String password, String ipAddress) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(OpenFlameSecurityConstants.USER_NAME_REQUEST_HEADER, username);
        headerMap.put(OpenFlameSecurityConstants.USER_PASSWORD_REQUEST_HEADER, password);
        headerMap.put(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, ipAddress);
        ServiceResponse<AuthenticationToken> response;

        try {
            response = postWithHeaders("/sts/sign-in", headerMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
        }
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<AuthenticationToken> processSMWSSignIn(String username, String password, String ipAddress) {
        ServiceResponse<AuthenticationToken> response;
        if (OpenFlameSecurityConstants.isSiteMinderAuthSupported()) {
            String baseServiceUrl = StringUtils.isBlank(OpenFlameSecurityConstants.getOpfDirectUrl()) ?
                    (baseUrl == null ? Env.FIREJACK_URL.getValue() : baseUrl) :
                    OpenFlameSecurityConstants.getOpfDirectUrl();
            Builder builder = prepareBaseBuilder(baseServiceUrl, "/sts/sm-ws/sign-in",
                    MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
            builder.header(OpenFlameSecurityConstants.USER_NAME_REQUEST_HEADER, username);
            builder.header(OpenFlameSecurityConstants.USER_PASSWORD_REQUEST_HEADER, password);
            builder.header(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, ipAddress);
            try {
                response = doPost(builder, ServiceResponse.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
            }
        } else {
            response = new ServiceResponse<AuthenticationToken>("Site Minder authentication is not enabled.", false);
        }

        return response;
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSForgotPassword(String email, String resetPasswordPageUrl) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(OpenFlameSecurityConstants.EMAIL_REQUEST_HEADER, email);
        headerMap.put(OpenFlameSecurityConstants.RESET_PASSWORD_PAGE_URL_HEADER, resetPasswordPageUrl);
        ServiceResponse<AuthenticationToken> response;

        try {
            response = postWithHeaders("/sts/forgot-password", headerMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
        }
        return response;
    }

    @Override
    public ServiceResponse<AuthenticationToken> processSTSResetPassword(String token) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(OpenFlameSecurityConstants.TOKEN_REQUEST_HEADER, token);
        ServiceResponse<AuthenticationToken> response;

        try {
            response = postWithHeaders("/sts/reset-password", headerMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
        }
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<AuthenticationToken> processSTSCertSignIn(String lookup, String name, String cert) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(OpenFlameSecurityConstants.OPF_CERTIFICATE_HEADER, cert);
        ServiceResponse<AuthenticationToken> response;

        try {
            String serviceUrl = "/sts/sign-in/" + lookup + "/" + name;
            if (StringUtils.isBlank(OpenFlameSecurityConstants.getOpfDirectUrl())) {
                response = postWithHeaders(serviceUrl, headerMap);
            } else {
                String url = OpenFlameSecurityConstants.getOpfDirectUrl() +
                        REST_API_URL_SUFFIX + getServiceUrlSuffix() + serviceUrl;

                WebResource webResource = Client.create(config).resource(url);
                webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
                webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
                WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE)
                        .type(MediaType.APPLICATION_XML_TYPE);

                Builder proxy = ErrorHandler.getProxy(builder);
                addCookie(proxy);
                addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
                addClientIpInfo(proxy);

                proxy.header(OpenFlameSecurityConstants.OPF_CERTIFICATE_HEADER, cert);
                response = doPost(proxy, ServiceResponse.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
        }
        return response;
    }

    @Override
    public ServiceResponse processSTSSignOut(String token) {
        ServiceResponse response;

        try {
            Builder builder;
            String path = "/sts/sign-out";
            if (OpenFlameSecurityConstants.isSiteMinderAuthSupported()) {
                builder = prepare1(getSystemUserSessionToken(), path, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
            } else {
                builder = prepare(path, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
            }

            addHeader(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, token, builder);

            response = builder.get(ServiceResponse.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse(e.getMessage(), false);
        }
        return response;
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionExpired(String sessionToken) {
        return checkStatus(sessionToken, "/sts/session/expiration/check", true);
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken) {
        String url = "/sts/session/active-status/check/";
        if (StringUtils.isNotBlank(sessionToken)) {
            url += sessionToken;
        }
        return checkStatus(sessionToken, url, false);
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isSessionTokenActive(String sessionToken, String browserIpAddress) {
        String url = "/sts/session/active-status/check/";
        if (StringUtils.isNotBlank(sessionToken)) {
            url += sessionToken;
        }
        return checkStatus(sessionToken, browserIpAddress, url);
    }

    @Override
    public ServiceResponse<AuthenticationToken> processOpenIdSignIn(
            String email, Map<SupportedOpenIDAttribute, String> mappedAttributes, String ipAddress) {
        try {
            Map<String, String> headers = OpenIDUtils.prepareOpenIDUserHttpHeaders(email, mappedAttributes, ipAddress);
            return getWithHeaders("/sts/openid-sign-in", headers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<AuthenticationToken>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<AuthenticationToken> processFacebookIdSignIn(Long facebookId, Map<String, String> mappedAttributes, String browserIpAddress) {
        Map<String, String> headers = FacebookIDUtils.prepareFacebookIDUserHttpHeaders(facebookId, mappedAttributes, browserIpAddress);
        return getWithHeaders("/sts/facebook-sign-in", headers);
    }

    @Override
    public ServiceResponse<AuthenticationToken> processTwitterIdSignIn(
            Long twitterId, Map<String, String> mappedAttributes, String browserIpAddress) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("twitterId", String.valueOf(twitterId));
        headers.put(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, browserIpAddress);
        headers.putAll(mappedAttributes);
        return getWithHeaders("/sts/twitter-sign-in", headers);
    }

    @Override
    public ServiceResponse<AuthenticationToken> processLinkedInSignIn(Map<String, String> mappedAttributes, String browserIpAddress) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, browserIpAddress);
        headers.putAll(mappedAttributes);
        return getWithHeaders("/sts/linkedin-sign-in", headers);
    }

    /*@Override
    public ServiceResponse<AuthenticationToken> processSiteMinderSignIn(String standardId, String browserIpAddress) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, browserIpAddress);
        headers.put(OpenFlameSecurityConstants.STANDARD_ID_HEADER, standardId);
        return getWithHeaders("/sts/siteminder-sign-in", headers);
    }*/

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<AuthenticationToken> processSiteMinderSignIn(User user, String browserIpAddress) {
        ServiceResponse<AuthenticationToken> response;
        if (user == null) {
            response = new ServiceResponse<AuthenticationToken>("User parameter is null.", false);
        } else if (StringUtils.isBlank(user.getUsername())) {
            response = new ServiceResponse<AuthenticationToken>("Specified user has blank username.", false);
        } else {
            String url = OpenFlameSecurityConstants.getOpfDirectUrl() +
                    REST_API_URL_SUFFIX + getServiceUrlSuffix() + "/sts/siteminder-sign-in";
            WebResource webResource = Client.create(config).resource(url);
            webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
            webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
            WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE).type(MediaType.APPLICATION_XML_TYPE);

            Builder proxy = ErrorHandler.getProxy(builder);
            Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, getSystemUserSessionToken());
            builder.cookie(cookie);
            addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
            //addClientIpInfo(proxy);

            response = doPost(proxy, ServiceResponse.class, new ServiceRequest<AbstractDTO>(user));
            //response = post2AsSystem(getSystemUserSessionToken(), "/sts/siteminder-sign-in", user);
        }
        return response;
    }

    @Override
    public ServiceResponse<SimpleIdentifier<String>> synchronizeSMAdmins(String smAdminUsers) {
        return post2AsSystem(getSystemUserSessionToken(),
                "/siteminder-synchronize-users", new SimpleIdentifier<String>(smAdminUsers));
    }

    @Override
    public ServiceResponse<User> updateSMUserRoles(User user) {
        ServiceResponse<User> response;
        if (user == null) {
            response = new ServiceResponse<User>("User to update is null.", false);
        } else if (StringUtils.isBlank(user.getUsername())) {
            response = new ServiceResponse<User>("User to update has empty username.", false);
        } else {
            response = put2AsSystem(getSystemUserSessionToken(), "/siteminder-update-user", user);
        }
        return response;
    }

    @Override
    public ServiceResponse<UserPermission> loadGrantedPermissions() {
        try {
            List<UserPermission> userPermissions = permissionContainer.loadGrantedActions(
                    ContextManager.getPrincipal());
            return new ServiceResponse<UserPermission>(userPermissions, null, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<UserPermission>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<UserPermission> loadDeniedPermissions() {
        try {
            List<UserPermission> userPermissions = permissionContainer.loadDeniedActions(
                    ContextManager.getPrincipal());
            return new ServiceResponse<UserPermission>(userPermissions, null, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<UserPermission>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<UserPermission> loadPermissionsBySecuredRecord(Long securedRecordId) {
        ServiceResponse<UserPermission> response;
        if (securedRecordId == null) {
            response = new ServiceResponse<UserPermission>(
                    "securedRecordId parameter should not be null.", false);
        } else {
            try {
                List<UserPermission> userPermissions =
                    permissionContainer.loadUserPermissionsBySecuredRecords(
                            ContextManager.getPrincipal(), securedRecordId);
                response = new ServiceResponse<UserPermission>(userPermissions, null, true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<UserPermission>(e.getMessage(), false);
            }
        }

        return response;
    }

    @Override
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMap() {
        try {
            return get("/permission/map-by-roles");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<MappedPermissions>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<MappedPermissions> readPermissionsByRolesMapForGuest() {
        try {
            return get("/permission/map-by-roles/for-guest");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<MappedPermissions>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<MappedPermissions> readAllSecuredRecordContextualPermissions() {
        try {
            return get("/permission/sr");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ServiceResponse<MappedPermissions>(e.getMessage(), false);
        }
    }

    @Override
    public ServiceResponse<TypeFilter> loadIdFiltersByUser(Long userId) {
        ServiceResponse<TypeFilter> response;
        if (userId == null) {
            response = new ServiceResponse<TypeFilter>("userId parameter should not be null.", false);
        } else {
            try {
                response = get("/permission/id-filters/" + userId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<TypeFilter>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> loadCachedResourceLocations(String packageLookup) {
        ServiceResponse<ResourceLocation> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<ResourceLocation>("packageLookup parameter should not be blank", false);
        } else {
            try {
                response = get("/resource-location/cached/" + packageLookup);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> createPermission(ServiceRequest<Permission> request) {
        ServiceResponse<Permission> response;
        if (request == null || request.getData() == null) {
            response = new ServiceResponse<Permission>("Could not create permission with empty request state.", false);
        } else {
            try {
                response = post("/permission", request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> updatePermission(ServiceRequest<Permission> request) {
        ServiceResponse<Permission> response;
        if (request == null || request.getData() == null || request.getData().getId() == null) {
            response = new ServiceResponse<Permission>("Request to update permission has inappropriate state.", false);
        } else {
            try {
                response = put("/permission/" + request.getData().getId(), request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse deletePermission(Long permissionId) {
        ServiceResponse response;
        if (permissionId == null) {
            response = new ServiceResponse("Id of permission to delete should not be null.", false);
        } else {
            try {
                response = delete("/permission/" + permissionId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> readPermission(Long permissionId) {
        ServiceResponse<Permission> response;
        if (permissionId == null) {
            response = new ServiceResponse<Permission>("Id of permission to load should not be null.", false);
        } else {
            try {
                response = get("/permission/" + permissionId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> readAllPermissions(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit) {
        ServiceResponse<Permission> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<Permission>(
                    "Parameter registryNodeId should not be null.", false);
        } else {
            try {
                response = get("/permission", "node", registryNodeId,
                        "exceptIds", exceptIds, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> readAllPermissionsByRegistryNodeId(
            Long registryNodeId, Integer start, Integer limit) {
        ServiceResponse<Permission> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<Permission>(
                    "Parameter registryNodeId should not be null.", false);
        } else {
            try {
                response = get("/permission/node/" + registryNodeId, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Permission> searchPermissions(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        ServiceResponse<Permission> response;
        if (StringUtils.isBlank(term)) {
            response = new ServiceResponse<Permission>("Parameter term should not be blank", false);
        } else {
            try {
                if (registryNodeId == null) {
                    response = get("/permission/search/" + term,
                            "exceptIds", exceptIds, "start", start, "limit", limit);
                } else {
                    response = get("/permission/node/search/" + registryNodeId + '/' + term,
                            "exceptIds", exceptIds, "start", start, "limit", limit);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Permission>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readRole(Long roleId) {
        ServiceResponse<Role> response;
        if (roleId == null) {
            response = new ServiceResponse<Role>("Parameter 'roleId' should not be null.", false);
        } else {
            try {
                response = get("/role/" + roleId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readAllRoles(Long registryNodeId, List<Long> exceptIds, Boolean isGlobal) {
        ServiceResponse<Role> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<Role>("Parameter 'registryNodeId' should not be null.", false);
        } else {
            try {
                response = get("/role", "node", registryNodeId, "isGlobal", isGlobal, "exceptIds", exceptIds);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readAllRolesByRegistryNodeId(Long registryNodeId, Integer start, Integer limit) {
        ServiceResponse<Role> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<Role>("Parameter 'registryNodeId' should not be null.", false);
        } else {
            try {
                response = get("/role/node/" + registryNodeId, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readAllRolesByParentLookup(String parentLookup) {
        ServiceResponse<Role> response;
        if (StringUtils.isBlank(parentLookup)) {
            response = new ServiceResponse<Role>("Parameter 'parentLookup' should not be blank.", false);
        } else {
            try {
                response = get("/role/by-path/" + parentLookup);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> searchRole(Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        ServiceResponse<Role> response;
        if (term == null) {
            response = new ServiceResponse<Role>("Parameter 'term' should not be null.", false);
        } else {
            StringBuilder sb = new StringBuilder("/role");
            if (registryNodeId == null) {
                sb.append("/search");
            } else {
                sb.append("/node/search/").append(registryNodeId);
            }
            if (StringUtils.isNotBlank(term)) {
                sb.append("?term=").append(term);
            }
            try {
                response = get(sb.toString(), "exceptIds", exceptIds, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> advancedSearchRoles(AdvancedSearchParams searchParams, Integer offset, Integer limit) {
        return post2("/role/advanced-search", searchParams, "offset", offset, "limit", limit);
    }

    public ServiceResponse<Role> advancedSearchRolesAsSystem(AdvancedSearchParams searchParams, Integer offset, Integer limit) {
        return post2AsSystem(getSystemUserSessionToken(), "/role/advanced-search", searchParams, "offset", offset, "limit", limit);
    }

    @Override
    public ServiceResponse<Role> createRole(ServiceRequest<Role> request) {
        ServiceResponse<Role> response;
        if (request == null || request.getData() == null) {
            response = new ServiceResponse<Role>("Request has inappropriate state.", false);
        } else {
            try {
                response = post("/role", request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> updateRole(ServiceRequest<Role> request) {
        ServiceResponse<Role> response;
        if (request == null || request.getData() == null || request.getData().getId() == null) {
            response = new ServiceResponse<Role>("Request has inappropriate state.", false);
        } else {
            try {
                response = put("/role/" + request.getData().getId(), request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> cloneRole(String roleName, Long idOfRoleToClone) {
        ServiceResponse<Role> response;
        if (idOfRoleToClone == null) {
            response = new ServiceResponse<Role>("Id of the role to clone is not specified.", false);
        } else if (StringUtils.isBlank(roleName)) {
            response = new ServiceResponse<Role>();
        } else {
            try {
                response = get("/role/clone", "roleId", idOfRoleToClone, "roleCloneName", roleName);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse deleteRole(Long roleId) {
        ServiceResponse response;
        if (roleId == null) {
            response = new ServiceResponse("Id of role to delete should not be null.", false);
        } else {
            try {
                response = delete("/role/" + roleId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readEntityAssociatedContextRoles(Long entityId) {
        ServiceResponse<Role> response;
        if (entityId == null) {
            response = new ServiceResponse<Role>("EntityId should not be null.", false);
        } else {
            try {
                response = get("/role/context/associated-with-entity", "entityId", entityId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Role>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> readResourceLocation(Long resourceLocationId) {
        ServiceResponse<ResourceLocation> response;
        if (resourceLocationId == null) {
            response = new ServiceResponse<ResourceLocation>("Parameter 'resourceLocationId' should not be null.", false);
        } else {
            try {
                response = get("/resource-location/" + resourceLocationId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> readAllResourceLocations(
            Long registryNodeId, List<Long> exceptIds, Integer start, Integer limit) {
        ServiceResponse<ResourceLocation> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<ResourceLocation>("Parameter 'registryNodeId' should not be null.", false);
        } else {
            try {
                response = get("/resource-location", "exceptIds", exceptIds, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> readAllResourceLocationsByRegistryNodeId(
            Long registryNodeId, Integer start, Integer limit) {
        ServiceResponse<ResourceLocation> response;
        if (registryNodeId == null) {
            response = new ServiceResponse<ResourceLocation>("Parameter 'registryNodeId' should not be null.", false);
        } else {
            try {
                response = get("/resource-location/node/" + registryNodeId, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> searchResourceLocations(
            Long registryNodeId, String term, List<Long> exceptIds, Integer start, Integer limit) {
        ServiceResponse<ResourceLocation> response;
        if (term == null) {
            response = new ServiceResponse<ResourceLocation>("Parameter 'term' should not be null.", false);
        } else {
            StringBuilder sb = new StringBuilder("/resource-location");
            if (registryNodeId == null) {
                sb.append("/search");
            } else {
                sb.append("/node/search/").append(registryNodeId);
            }
            sb.append('/').append(term);
            try {
                response = get(sb.toString(),"exceptIds",exceptIds, "start", start, "limit", limit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> createResourceLocation(ServiceRequest<ResourceLocation> request) {
        ServiceResponse<ResourceLocation> response;
        if (request == null || request.getData() == null) {
            response = new ServiceResponse<ResourceLocation>("Request has inappropriate state.", false);
        } else {
            try {
                response = post("/resource-location", request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<ResourceLocation> updateResourceLocation(ServiceRequest<ResourceLocation> request) {
        ServiceResponse<ResourceLocation> response;
        if (request == null || request.getData() == null || request.getData().getId() == null) {
            response = new ServiceResponse<ResourceLocation>("Request has inappropriate state.", false);
        } else {
            try {
                response = put("/resource-location/" + request.getData().getId(), request.getData());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<ResourceLocation>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse deleteResourceLocation(Long resourceLocationId) {
        ServiceResponse response;
        if (resourceLocationId == null) {
            response = new ServiceResponse("Id of resource location to delete should not be null.", false);
        } else {
            try {
                response = delete("/resource-location/" + resourceLocationId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<User> readUsersByAssignedRoles(Long objectId, String objectType) {
        ServiceResponse<User> response;
        if (objectId == null || StringUtils.isBlank(objectType)) {
            response = new ServiceResponse<User>("Inappropriate request parameters.", false);
        } else {
            StringBuilder sb = new StringBuilder("/role-assignment/users/");
            sb.append(objectId).append('/').append(objectType);
            try {
                response = get(sb.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<User>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<AssignedRole> readAssignedRoles(Long objectId, String objectType) {
        ServiceResponse<AssignedRole> response;
        if (objectId == null || StringUtils.isBlank(objectType)) {
            response = new ServiceResponse<AssignedRole>("Inappropriate request parameters.", false);
        } else {
            StringBuilder sb = new StringBuilder("/role-assignment/");
            sb.append(objectId).append('/').append(objectType);
            try {
                response = get(sb.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<AssignedRole>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<AssignedRole> readAssignedRolesByUser(Long objectId, String objectType, Long userId) {
        ServiceResponse<AssignedRole> response;
        if (objectId == null || StringUtils.isBlank(objectType) || userId == null) {
            response = new ServiceResponse<AssignedRole>("Inappropriate request parameters.", false);
        } else {
            StringBuilder sb = new StringBuilder("/role-assignment/by-user/");
            sb.append(objectId).append('/').append(objectType).append('/').append(userId);
            try {
                response = get(sb.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<AssignedRole>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<AssignedRole> saveAssignedRolesByUser(
            Long objectId, String objectType, Long userId, Long roleId, Boolean assigned) {
        ServiceResponse<AssignedRole> response;
        if (objectId == null || StringUtils.isBlank(objectType) || userId == null ||
                roleId == null || assigned == null) {
            response = new ServiceResponse<AssignedRole>("Inappropriate request parameters.", false);
        } else {
            StringBuilder sb = new StringBuilder("/role-assignment/");
            sb.append(objectId).append('/').append(objectType).append('/')
                    .append(userId).append('?').append("roleId=").append(roleId)
                    .append('&').append("assigned=").append(assigned);
            try {
                response = put(sb.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<AssignedRole>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
    public ServiceResponse<Role> readContextRoles(List<Long> excludeIds) {
        StringBuilder sb = new StringBuilder("/role/context");
        if (excludeIds != null && !excludeIds.isEmpty()) {
            sb.append('?');
            for (int i = 0; i < excludeIds.size(); i++) {
                sb.append("excludeIds=").append(excludeIds.get(i));
                if (i < excludeIds.size() - 1) {
                    sb.append('&');
                }
            }
        }
        return get(sb.toString());
    }

    @Override
    public ServiceResponse makeEntitySecurityEnabled(String entityLookup, Boolean securityEnabled) {
        ServiceResponse response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse("Package information is not specified.", false);
        } else if (securityEnabled == null) {
            response = new ServiceResponse("Allow Instant Security flag is not specified.", false);
        } else {
            response = put("/role/enable-instant-security-on-package",
                    "entityLookup", entityLookup, "securityEnabled", securityEnabled);
        }
        return response;
    }

    @Override
    public ServiceResponse<UserRole> saveContextUserRoles(List<UserRole> userRoles, Boolean syncRoles) {
        ServiceResponse<UserRole> response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            response = put("/user-role/save-context-user-roles", userRoles, "syncRoles", syncRoles);
        }
        return response;
    }

    public ServiceResponse<UserRole> saveContextUserRolesAsSystem(List<UserRole> userRoles, Boolean syncRoles) {
        ServiceResponse<UserRole> response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            response = put2AsSystem(getSystemUserSessionToken(), "/user-role/save-context-user-roles", userRoles, "syncRoles", syncRoles);
        }
        return response;
    }

    @Override
    public ServiceResponse releaseContextUserRoles(List<UserRole> userRoles) {
        ServiceResponse<UserRole> response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            response = put("/user-role/release-context-user-roles", userRoles);
        }
        return response;
    }

    public ServiceResponse releaseContextUserRolesAsSystem(List<UserRole> userRoles) {
        ServiceResponse<UserRole> response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            response = put2AsSystem(getSystemUserSessionToken(), "/user-role/release-context-user-roles", userRoles);
        }
        return response;
    }

    @Override
    public ServiceResponse releaseContextUserRolesByPattern(UserRole userRole) {
        ServiceResponse<UserRole> response;
        if (userRole == null) {
            response = new ServiceResponse<UserRole>("User role information is not specified.", false);
        } else {
            response = put("/user-role/release-context-user-roles-by-pattern", userRole);
        }
        return response;
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> isRecordOwner(UserRole userRole) {
        ServiceResponse<SimpleIdentifier<Boolean>> response;
        if (userRole == null || StringUtils.isBlank(userRole.getTypeLookup()) ||
                (userRole.getModelId() == null && StringUtils.isBlank(userRole.getComplexPK()))) {
            response = new ServiceResponse<SimpleIdentifier<Boolean>>("Not enough input parameters are specified.", false);
        } else {
            response = post2("/user-role/is-owner", userRole);
        }
        return response;
    }

    public ServiceResponse releaseContextUserRolesByPatternAsSystem(UserRole userRole) {
        ServiceResponse<UserRole> response;
        if (userRole == null) {
            response = new ServiceResponse<UserRole>("User role information is not specified.", false);
        } else {
            response = put2AsSystem(getSystemUserSessionToken(),
                    "/user-role/release-context-user-roles-by-pattern", userRole);
        }
        return response;
    }

    @Override
    public ServiceResponse<AssignedRole> searchContextRolesAvailableForUser(UserRole userRole) {
        ServiceResponse<AssignedRole> response;
        if (userRole == null || StringUtils.isBlank(userRole.getTypeLookup()) ||
                (userRole.getModelId() == null && StringUtils.isBlank(userRole.getComplexPK()))) {
            response = new ServiceResponse<AssignedRole>("Not enough input parameters are specified.", false);
        } else {
            response = post2("/user-role/search-by-pattern", userRole);
        }
        return response;
    }

    private ServiceResponse<SimpleIdentifier<Boolean>> checkStatus(String sessionToken, String url, boolean attachCookie) {
        //String urlPrefix = Env.FIREJACK_URL.getValue() + REST_API_URL_SUFFIX + getServiceUrlSuffix();
        String serviceBaseUrl = OpenFlameSecurityConstants.isSiteMinderAuthSupported() &&
                StringUtils.isNotBlank(OpenFlameSecurityConstants.getOpfDirectUrl()) ?
                OpenFlameSecurityConstants.getOpfDirectUrl() : Env.FIREJACK_URL.getValue();
        String urlPrefix = serviceBaseUrl + REST_API_URL_SUFFIX + getServiceUrlSuffix();
        WebResource webResource = Client.create().resource(urlPrefix + url);
	    webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
	    webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
        WebResource.Builder builder;
        if (StringUtils.isBlank(sessionToken)) {
            builder = webResource.accept(MediaType.APPLICATION_XML_TYPE);
        } else {
            if (attachCookie) {
                builder = webResource.cookie(new Cookie(
                        OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, sessionToken))
                        .accept(MediaType.APPLICATION_XML_TYPE);
            } else {
                builder = webResource.accept(MediaType.APPLICATION_XML_TYPE);
            }
            try {
                OPFContext context = OPFContext.getContext();
                if (context != null && context.getBrowserIpAddress() != null) {
                    String key = sessionToken.substring(4, 12);
                    String encryptedIpAddress = SecurityUtils.encryptData(context.getBrowserIpAddress(), key);
                    builder.header(OpenFlameSecurityConstants.CLIENT_INFO_HEADER, encryptedIpAddress);
                }
            } catch (ContextLookupException e) {
                logger.debug(e.getMessage(), e);
            }
        }
        ServiceResponse<SimpleIdentifier<Boolean>> resp;
        try {
            boolean result = true;
            ClientResponse response = builder.head();
            List<NewCookie> cookies = response.getCookies();
            if (cookies == null || cookies.isEmpty()) {
                //todo: implement logging only for debug mode
                logger.error("Failed to retrieve expiration info - no appropriate cookies exist in response.");
            } else {
                for (NewCookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_EXPIRED_ATTRIBUTE)) {
                        String expiredStringValue = cookie.getValue();
                        result = Boolean.parseBoolean(expiredStringValue);
                        break;
                    }
                }
            }
            resp = new ServiceResponse<SimpleIdentifier<Boolean>>(new SimpleIdentifier<Boolean>(result), null, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resp = new ServiceResponse<SimpleIdentifier<Boolean>>(e.getMessage(), false);
        }
        return resp;
    }

    private ServiceResponse<SimpleIdentifier<Boolean>> checkStatus(String sessionToken, String browserIpAddress, String url) {
        //String urlPrefix = Env.FIREJACK_URL.getValue() + REST_API_URL_SUFFIX + getServiceUrlSuffix();
        String serviceBaseUrl = OpenFlameSecurityConstants.isSiteMinderAuthSupported() &&
                StringUtils.isNotBlank(OpenFlameSecurityConstants.getOpfDirectUrl()) ?
                OpenFlameSecurityConstants.getOpfDirectUrl() : Env.FIREJACK_URL.getValue();
        String urlPrefix = serviceBaseUrl + REST_API_URL_SUFFIX + getServiceUrlSuffix();
        WebResource webResource = Client.create().resource(urlPrefix + url);
	    webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
	    webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
        WebResource.Builder builder;
        if (StringUtils.isBlank(sessionToken)) {
            builder = webResource.accept(MediaType.APPLICATION_XML_TYPE);
        } else {
            builder = webResource.accept(MediaType.APPLICATION_XML_TYPE);
            if (StringUtils.isNotBlank(browserIpAddress)) {
                String key = sessionToken.substring(4, 12);
                String encryptedIpAddress = SecurityUtils.encryptData(browserIpAddress, key);
                builder.header(OpenFlameSecurityConstants.CLIENT_INFO_HEADER, encryptedIpAddress);
            }
        }
        ServiceResponse<SimpleIdentifier<Boolean>> resp;
        try {
            Boolean result = true;
            ClientResponse response = builder.head();
            List<NewCookie> cookies = response.getCookies();
            if (cookies == null || cookies.isEmpty()) {
                //todo: implement logging only for debug mode
                logger.error("Failed to retrieve expiration info - no appropriate cookies exist in response.");
            } else {
                for (NewCookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_EXPIRED_ATTRIBUTE)) {
                        String expiredStringValue = cookie.getValue();
                        result = Boolean.valueOf(expiredStringValue);
                        break;
                    }
                }
            }
            resp = new ServiceResponse<SimpleIdentifier<Boolean>>(new SimpleIdentifier<Boolean>(result), null, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resp = new ServiceResponse<SimpleIdentifier<Boolean>>(e.getMessage(), false);
        }
        return resp;
    }

}
