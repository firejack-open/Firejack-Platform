/*
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
package net.firejack.platform.service.directory.endpoint;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.QueryParam;
import java.util.List;


@SuppressWarnings("unused")
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
@WebService(endpointInterface = "net.firejack.platform.service.directory.endpoint.IDirectoryEndpoint")
public interface IDirectoryEndpoint {

	/**
	 * Read group by id
	 *
	 * @param id group id
	 *
	 * @return founded group
	 */
	@WebMethod
	ServiceResponse<Group> readGroup(@WebParam(name = "id") Long id);

	/**
	 * Search groups by term
	 *
	 * @param term      term expression
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	@WebMethod
	ServiceResponse<Group> searchAllGroups(@WebParam(name = "term") String term, @WebParam(name = "exceptIds") List<Long> exceptIds);

	/**
	 * Read all groups
	 *
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	@WebMethod
	ServiceResponse<Group> readAllGroups(@WebParam(name = "exceptIds") List<Long> exceptIds);

	/**
	 * Create new group
	 *
	 * @param request group data
	 *
	 * @return created group
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createGroup(@WebParam(name = "request") ServiceRequest<Group> request);

	/**
	 * Update group by id
	 *
	 * @param id group id
	 * @param request group data
	 *
	 * @return updated group
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateGroup(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<Group> request);

	/**
	 * Delete group by id
	 *
	 * @param id group id
	 *
	 * @return deleted group
	 */
	@WebMethod
	ServiceResponse<Group> deleteGroup(@WebParam(name = "id")  Long id);

	/**
	 * Read all User Profile Field Group by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded User Profile Field Groups
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldGroupTree> readAllUserProfileFieldGroup(
			@WebParam(name = "registryNodeId")  Long registryNodeId);

	/**
	 * Read  User Profile Field Group by id
	 *
	 * @param id user profile field group id
	 *
	 * @return founded  User Profile Field Group
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldGroup> readUserProfileFieldGroup(@WebParam(name = "id")  Long id);

	/**
	 * Create new User Profile Field Group
	 *
	 * @param request User Profile Field Group data
	 *
	 * @return created User Profile Field Group
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldGroup> createUserProfileFieldGroup(
            @WebParam(name = "request") ServiceRequest<UserProfileFieldGroup> request);

	/**
	 * Updated User Profile Field Group by id
	 *
	 * @param id User Profile Field Group id
	 * @param request                 User Profile Field Group data
	 *
	 * @return updated User Profile Field Group
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldGroup> updateUserProfileFieldGroup(
			@WebParam(name = "id")  Long id, @WebParam(name = "request") ServiceRequest<UserProfileFieldGroup> request);

	/**
	 * Delete User Profile Field Group by id
	 *
	 * @param id User Profile Field Group id
	 *
	 * @return deleted User Profile Field Group
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldGroup> deleteUserProfileFieldGroup(@WebParam(name = "id")  Long id);

	/**
	 * Read all User Profile Field by parent Id
	 *
	 * @param registryNodeId parent id
	 * @param userProfileFieldGroupId parent group id
	 *
	 * @return founded User Profile Field
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldTree> readAllUserProfileField(
            @WebParam(name = "registryNodeId") Long registryNodeId,
            @WebParam(name = "userProfileFieldGroupId") Long userProfileFieldGroupId);

	/**
	 * Create new User Profile Field
	 *
	 * @param request User Profile Field data
	 *
	 * @return created User Profile Field
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldTree> createUserProfileField(@WebParam(name = "request") ServiceRequest<UserProfileField> request);

	/**
	 * Update User Profile Field by id
	 *
	 * @param id User Profile Field id
	 * @param request            User Profile Field data
	 *
	 * @return updated User Profile Field
	 */
	@WebMethod
	ServiceResponse<UserProfileFieldTree> updateUserProfileField(
			@WebParam(name = "id")  Long id, @WebParam(name = "request") ServiceRequest<UserProfileField> request);

    /**
	 * Move User Profile Field to other group
	 *
	 * @param id User Profile Field id which need to move
	 * @param userProfileFieldGroupId new User Profile Field Group Id
	 *
	 * @return updated User Profile Field
	 */
    ServiceResponse<UserProfileFieldTree> moveUserProfileField(
			 @WebParam(name = "id") Long id,
             @WebParam(name = "userProfileFieldGroupId") Long userProfileFieldGroupId);

	/**
	 * Delete User Profile Field by id
	 *
	 * @param id User Profile Field id
	 *
	 * @return deleted User Profile Field
	 */
	@WebMethod
	ServiceResponse<UserProfileField> deleteUserProfileField(@WebParam(name = "id")  Long id);

    /**
     * Find All user profile fields mapped to user profile field group tht has specified lookup
     * @param userProfileGroupLookup user profile field group lookup
     * @return user profile fields
     */
    @WebMethod
    ServiceResponse<UserProfileField> findUserProfileFieldsByGroupLookup(
            @WebParam(name = "groupLookup") String userProfileGroupLookup);

    /**
     * Find user profile field values by specified user id and list of user profile field id list
     * @param userId user id
     * @param userProfileFieldIdList list of user profile field id
     * @return response with list of corresponded user profile fields values
     */
    @WebMethod
    ServiceResponse<UserProfileFieldValue> findUserProfileFieldValues(
            @WebParam(name = "userId") Long userId, @WebParam(name = "fields") List<Long> userProfileFieldIdList);

    /**
     * Save User Profile Fields Values and return response with list of saved values
     * @param request request with input user profile field values
     * @return list of saved user profile field values
     */
    @WebMethod
    ServiceResponse<UserProfileFieldValue> saveUserProfileFieldsValues(
            @WebParam(name = "request") ServiceRequest<UserProfileFieldValue> request);

    /**
     * Save User Profile Fields Values and return response with list of saved values
     * @param userIdList request with input user profile field values
     * @param request request with input user profile field values
     * @return list of loaded user profiles
     */
    @WebMethod
    ServiceResponse<UserProfile> loadUserProfiles(
            @WebParam(name = "userIdList") List<Long> userIdList,
            @WebParam(name = "request") ServiceRequest<UserProfileField> request);

	/**
	 * Read current user
	 *
	 * @return founded user
	 */
	@WebMethod
	ServiceResponse<User> readCurrentUser();

	/**
	 * Read user by id
	 *
	 * @param id user id
	 *
	 * @return founded user
	 */
	@WebMethod
	ServiceResponse<User> readUser(@WebParam(name = "id")  Long id);

	/**
	 * Found last created users
	 *
	 * @param count count users
	 *
	 * @return founded users
	 */
	@WebMethod
	ServiceResponse<User> readLastCreatedUsers(@WebParam(name = "count")  Integer count);

	/**
	 * Search users by term
	 *
	 *
     * @param term      term expression
     * @param exceptIds exclude ids
     *
     * @param offset
     *@param limit
     * @param sortColumn
     * @param sortDirection @return founded users
	 */
	@WebMethod
	ServiceResponse<User> searchAllUsers(
            @WebParam(name = "term") String term,
            @WebParam(name = "exceptIds") List<Long> exceptIds,
            @WebParam(name = "start") Integer offset,
            @WebParam(name = "limit") Integer limit,
            @WebParam(name = "sort") String sortColumn,
            @WebParam(name = "dir") SortOrder sortDirection);

    public ServiceResponse<User> advancedSearchUsers(
            @QueryParam("queryParameters") String queryParameters,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sortOrders") String sortOrders);

	/**
	 * Read all user by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded users
	 */
	@WebMethod
	ServiceResponse<User> readAllUsersByRegistryNodeId(@WebParam(name = "registryNodeId")  Long registryNodeId);

	/**
	 * Search users by parent id and term expression
	 *
	 * @param registryNodeId parent id
	 * @param term           term expression
	 *
	 * @return founded users
	 */
	@WebMethod
	ServiceResponse<User> searchUser(
			@WebParam(name = "registryNodeId")  Long registryNodeId,
			@WebParam(name = "term")  String term);

	/**
	 * Create new user
	 *
	 * @param request user data
	 *
	 * @return created user
	 */
	@WebMethod
	ServiceResponse<User> createUser(@WebParam(name = "request") ServiceRequest<User> request);

	/**
	 * Sign up user
	 *
	 * @param request user data
	 *
	 * @return created user
	 */
	@WebMethod
	ServiceResponse<User> signUpUser(@WebParam(name = "request") ServiceRequest<User> request);

	/**
	 * Update user by id
	 *
	 * @param id  user id
	 * @param request user data
	 *
	 * @return updated user
	 */
	@WebMethod
	ServiceResponse<User> updateUser(
			@WebParam(name = "id")  Long id,
			@WebParam(name = "request") ServiceRequest<User> request);

	/**
	 * Delete user by id
	 *
	 * @param id user id
	 *
	 * @return deleted user
	 */
	@WebMethod
	ServiceResponse<User> deleteUser(@WebParam(name = "id")  Long id);

    /**
	 * Read system user by id
	 *
	 * @param id system user id
	 *
	 * @return founded system user
	 */
	@WebMethod
	ServiceResponse<SystemUser> readSystemUser(@WebParam(name = "id")  Long id);

    /**
     * Read all system users by parent id
     *
     * @param registryNodeId parent id
     *
     * @return founded system users
     */
    @WebMethod
    ServiceResponse<SystemUser> readAllSystemUsersByRegistryNodeId( @WebParam(name = "registryNodeId")  Long registryNodeId);

    /**
	 * Create new system user
	 *
	 * @param request system user data
	 *
	 * @return created system user
	 */
    @WebMethod
	ServiceResponse<SystemUser> createSystemUser(@WebParam(name = "request") ServiceRequest<SystemUser> request);

	/**
	 * Update system user by id
	 *
	 * @param id system user id
	 * @param request system user data
	 *
	 * @return updated system user
	 */
	@WebMethod
	ServiceResponse<SystemUser> updateSystemUser(
			@WebParam(name = "id")  Long id,
			@WebParam(name = "request") ServiceRequest<SystemUser> request);

	/**
	 * Delete system user by id
	 *
	 * @param id system user id
	 *
	 * @return deleted system user
	 */
	@WebMethod
	ServiceResponse<SystemUser> deleteSystemUser(@WebParam(name = "id")  Long id);

    /**
	 * Regenerate Consumer Secret Key for system user by id
	 *
	 * @param id system user id
	 *
	 * @return updated system user
	 */
    @WebMethod
    ServiceResponse<SystemUser> generateConsumerSecret(@WebParam(name = "id")  Long id);

    /**
	 * Read user Global/Context roles by user id
	 *
	 * @param userId user id
	 *
	 * @return roles found
	 */
    @WebMethod
    ServiceResponse<Role> readAllUserRoles(@WebParam(name = "userId") Long userId);

	/**
	 * Read all Global roles by parent id
	 *
	 * @param registryNodeId parent id
	 * @param exceptIds      except ids
	 *
	 * @return found roles
	 */
	@WebMethod
	ServiceResponse<Role> readAllGlobalRoles(
			@WebParam(name = "node") Long registryNodeId,
			@WebParam(name = "exceptIds") List<Long> exceptIds);

	/**
	 * Search roles
	 *
	 * @param term      term expression
	 * @param exceptIds except ids
	 *
	 * @return founded roles
	 */
	@WebMethod
	ServiceResponse<Role> searchRoles(
			@WebParam(name = "term") String term,
			@WebParam(name = "exceptIds") List<Long> exceptIds);

	/**
	 * Load user identity info
	 *
     * @param sessionToken session token
	 * @return founded user info
	 */
	@WebMethod
	ServiceResponse<UserIdentity> getUserIdentityInfo(@WebParam(name = "sessionToken") String sessionToken);

	/**
	 * Perform search user by email
	 * /registry/directory/user/search/email/{term}
	 * Url
	 *
	 * @param email user email address
	 *
	 * @return response object with UserVO
	 */
	@WebMethod
	ServiceResponse<User> findUserByEmail(@WebParam(name = "email") String email);

    /**
     * Perform search user by username
     *
     * @param username user username
     *
     * @return founded user
     */
    @WebMethod
    ServiceResponse<User> findUserByUsername(@WebParam(name = "username") String username);

	/**
	 * Read directory by id
	 *
	 * @param id directory id
	 *
	 * @return founded directory
	 */
	@WebMethod
	ServiceResponse<Directory> readDirectory(@WebParam(name = "id")  Long id);

	/**
	 * Read all directory
	 *
	 * @return founded directory
	 */
	@WebMethod
	ServiceResponse<Directory> readAllDirectories();

	/**
	 * Read ordered directory
	 *
	 * @return founded ordered directory
	 */
	@WebMethod
	ServiceResponse<Directory> readOrderedDirectories();

	/**
	 * Update directory position
	 *
	 * @param id directory id
	 * @param position new position
	 *
	 * @return new position directory
	 */
	@WebMethod
	ServiceResponse updateDirectoryPosition(
			@WebParam(name = "id")  Long id, @WebParam(name = "position")  Integer position);

	/**
	 * Read directory services
	 *
	 * @return founded directory services
	 */
	@WebMethod
	ServiceResponse<Directory> readDirectoryServices();

	/**
	 * Create new directory
	 *
	 * @param request directory data
	 *
	 * @return created directory
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createDirectory(@WebParam(name = "request") ServiceRequest<Directory> request);

	/**
	 * Update directory by id
	 *
	 * @param id directory id
	 * @param request     directory data
	 *
	 * @return updated directory
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateDirectory(
			@WebParam(name = "id")  Long id, @WebParam(name = "request") ServiceRequest<Directory> request);

	/**
	 * Delete directory
	 *
	 * @param id directory id
	 *
	 * @return deleted directory
	 */
	@WebMethod
	ServiceResponse<Directory> deleteDirectory(@WebParam(name = "id")  Long id);

    @WebMethod
    ServiceResponse<CheckUrl> checkLdapDirectoryConnection(
            @WebParam(name = "request") ServiceRequest<CheckUrl> request);

    @WebMethod
    ServiceResponse<User> searchLdapUsers(
            @WebParam(name = "directoryId") Long ldapDirectoryId,
            @WebParam(name = "term") String term);

    @WebMethod
    ServiceResponse<User> createLdapUser(
            @WebParam(name = "request") ServiceRequest<User> request,
            @WebParam(name = "directoryId") Long directoryId);

    @WebMethod
    ServiceResponse<User> updateLdapUser(
            @WebParam(name = "username") String username,
            @WebParam(name = "directoryId") Long directoryId,
            @WebParam(name = "request") ServiceRequest<User> request);

    @WebMethod
    ServiceResponse<User> deleteLdapUser(
            @WebParam(name = "username") String username,
            @WebParam(name = "directoryId") Long directoryId);


    @WebMethod
    ServiceResponse<Group> searchLdapGroups(
            @WebParam(name = "directoryId") Long ldapDirectoryId,
            @WebParam(name = "term") String vterm);

    @WebMethod
    ServiceResponse<Group> loadLdapGroupWithUsers(
            @WebParam(name = "directoryId") Long directoryId,
            @WebParam(name = "groupName") String groupName);

    @WebMethod
    ServiceResponse<Group> createLdapGroup(
            @WebParam(name = "request") ServiceRequest<Group> request,
            @WebParam(name = "directoryId") Long directoryId);

    @WebMethod
    ServiceResponse<Group> updateLdapGroup(
            @WebParam(name = "groupName") String groupName,
            @WebParam(name = "directoryId")  Long directoryId,
            @WebParam(name = "request") ServiceRequest<Group> request);

    @WebMethod
    ServiceResponse<Group> deleteLdapGroup(
            @WebParam(name = "groupName") String username,
            @WebParam(name = "directoryId") Long directoryId);

    @WebMethod
    ServiceResponse<User> importLdapUserIfNecessary(
            @WebParam(name = "request") ServiceRequest<User> request,
            @WebParam(name = "ldapDirectoryId") Long ldapDirectoryId);

    @WebMethod
    ServiceResponse<GroupMapping> loadGroupMappingsByLdapGroupDN(
            @WebParam(name = "directoryId") Long directoryId,
            @WebParam(name = "groupDN") String groupDN);

    @WebMethod
    ServiceResponse<GroupMapping> updateGroupMappings(
            @WebParam(name = "groupDN") String groupDN,
            @WebParam(name = "directoryId") Long directoryId,
            @WebParam(name = "request") ServiceRequest<GroupMapping> request);

    @WebMethod
    ServiceResponse<GroupMapping> importLdapGroup(
            @WebParam(name = "directoryId") Long directoryId,
            @WebParam(name = "groupDN") String groupDN);

    @WebMethod
    ServiceResponse<Application> readApplication(
            @WebParam(name = "applicationId") Long applicationId);

    @WebMethod
    ServiceResponse<Application> readAllApplications();

    @WebMethod
    ServiceResponse<Application> searchApplications(
            @WebParam(name = "packageLookup") String packageLookup, @WebParam(name = "term") String term);

    @WebMethod
    ServiceResponse<Application> createApplication(
            @WebParam(name = "request") ServiceRequest<Application> application);

    @WebMethod
    ServiceResponse<Application> updateApplication(
            @WebParam(name = "applicationId") Long applicationId,
            @WebParam(name = "request") ServiceRequest<Application> application);

    @WebMethod
    ServiceResponse<Application> deleteApplication(@WebParam(name = "applicationId") Long applicationId);

}