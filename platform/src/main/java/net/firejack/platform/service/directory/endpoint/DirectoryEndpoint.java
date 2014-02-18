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
package net.firejack.platform.service.directory.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.utils.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("directory")
public class DirectoryEndpoint implements IDirectoryEndpoint {

//    READ-ONE-REQUEST:
//    /rest/registry/directory/group/[id] --> GET method

	/**
	 * Read group by id
	 *
	 * @param id group id
	 *
	 * @return founded group
	 */
	@GET
	@Path("/group/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Group> readGroup( @PathParam("id") Long id) {
		return OPFEngine.DirectoryService.readGroup(id);
	}

//    READ-ALL-REQUEST:
//    /rest/registry/directory/group/search/[term] --> GET method

	/**
	 * Search groups by term
	 *
	 * @param term      term expression
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	@GET
	@Path("/group/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Group> searchAllGroups(@QueryParam("term") String term, @QueryParam("exceptIds") List<Long> exceptIds) {
		return OPFEngine.DirectoryService.searchAllGroups(term, exceptIds);
	}

	/**
	 * Read all groups
	 *
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	@GET
	@Path("/group")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Group> readAllGroups(@QueryParam("exceptIds") List<Long> exceptIds) {
		return OPFEngine.DirectoryService.readAllGroups(exceptIds);
	}

//    CREATE-REQUEST
//    /rest/registry/directory/group --> POST method

	/**
	 * Create new group
	 *
	 * @param request group data
	 *
	 * @return created group
	 */
	@POST
	@Path("/group")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createGroup( ServiceRequest<Group> request) {
		return OPFEngine.DirectoryService.createGroup(request.getData());
	}

//    UPDATE-REQUEST
//    /rest/registry/directory/group/[id] --> PUT method

	/**
	 * Update group by id
	 *
	 * @param id group id
	 * @param request group data
	 *
	 * @return updated group
	 */
	@PUT
	@Path("/group/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateGroup(
			 @PathParam("id") Long id, ServiceRequest<Group> request) {
		return OPFEngine.DirectoryService.updateGroup(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/directory/group/[id] --> DELETE method

	/**
	 * Delete group by id
	 *
	 * @param id group id
	 *
	 * @return deleted group
	 */
	@DELETE
	@Path("/group/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Group> deleteGroup( @PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteGroup(id);
	}

//    READ-ALL-BY-REGISTRY-NODE-ID-REQUEST:
//    /rest/registry/directory/user_profile_field_group/{registryNodeId} --> GET method

	/**
	 * Read all User Profile Field Group by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded User Profile Field Groups
	 */
	@GET
	@Path("/user_profile_field_group/node/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldGroupTree> readAllUserProfileFieldGroup(
			 @PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.DirectoryService.readAllUserProfileFieldGroup(registryNodeId);
	}

//  READ-ONE-REQUEST:
//    /rest/registry/directory/user_profile_field_group/{userProfileFieldGroupId} --> GET method

	/**
	 * Read  User Profile Field Group by id
	 *
	 * @param id user profile field group id
	 *
	 * @return founded  User Profile Field Group
	 */
	@GET
	@Path("/user_profile_field_group/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldGroup> readUserProfileFieldGroup(@PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.readUserProfileFieldGroup(id);
	}

//    CREATE-REQUEST:
//    /rest/registry/directory/user_profile_field_group --> POST method

	/**
	 * Create new User Profile Field Group
	 *
	 * @param request User Profile Field Group data
	 *
	 * @return created User Profile Field Group
	 */
	@POST
	@Path("/user_profile_field_group")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldGroup> createUserProfileFieldGroup(
			 ServiceRequest<UserProfileFieldGroup> request) {
		return OPFEngine.DirectoryService.createUserProfileFieldGroup(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/directory/user_profile_field_group --> PUT method

	/**
	 * Updated User Profile Field Group by id
	 *
	 * @param id User Profile Field Group id
	 * @param request                 User Profile Field Group data
	 *
	 * @return updated User Profile Field Group
	 */
	@PUT
	@Path("/user_profile_field_group/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldGroup> updateUserProfileFieldGroup(
			 @PathParam(value = "id") Long id, ServiceRequest<UserProfileFieldGroup> request) {
		return OPFEngine.DirectoryService.updateUserProfileFieldGroup(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/directory/user_profile_field_group/{userProfileFieldGroupId} --> DELETE method

	/**
	 * Delete User Profile Field Group by id
	 *
	 * @param id User Profile Field Group id
	 *
	 * @return deleted User Profile Field Group
	 */
	@DELETE
	@Path("/user_profile_field_group/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldGroup> deleteUserProfileFieldGroup(@PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteUserProfileFieldGroup(id);
	}

    /**
	 * Read User Profile Field by Id
	 *
	 * @param id ID for get
	 *
	 * @return founded User Profile Field
	 */
    @GET
	@Path("/user_profile_field/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileField> readUserProfileField(@PathParam("id") Long id) {
		return OPFEngine.DirectoryService.readUserProfileField(id);
	}

//    READ-ALL-BY-REGISTRY-NODE-ID-REQUEST:
//    /rest/registry/directory/user_profile_field/{registryNodeId} --> GET method

	/**
	 * Read all User Profile Field by parent Id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded User Profile Field
	 */
	@GET
	@Path("/user_profile_field/node/{registryNodeId}/{userProfileFieldGroupId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldTree> readAllUserProfileField(
			 @PathParam("registryNodeId") Long registryNodeId,
             @PathParam("userProfileFieldGroupId") Long userProfileFieldGroupId) {
		return OPFEngine.DirectoryService.readAllUserProfileField(registryNodeId, userProfileFieldGroupId);
	}

//    CREATE-REQUEST:
//    /rest/registry/directory/user_profile_field --> POST method

	/**
	 * Create new User Profile Field
	 *
	 * @param request User Profile Field data
	 *
	 * @return created User Profile Field
	 */
	@POST
	@Path("/user_profile_field")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldTree> createUserProfileField(
			 ServiceRequest<UserProfileField> request) {
		return OPFEngine.DirectoryService.createUserProfileField(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/directory/user_profile_field/{userProfileFieldId} --> PUT method

	/**
	 * Update User Profile Field by id
	 *
	 * @param id User Profile Field id
	 * @param request            User Profile Field data
	 *
	 * @return updated User Profile Field
	 */
	@PUT
	@Path("/user_profile_field/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldTree> updateUserProfileField(
			 @PathParam("id") Long id, ServiceRequest<UserProfileField> request) {
		return OPFEngine.DirectoryService.updateUserProfileField(id, request.getData());
	}

    /**
	 * Move User Profile Field to other group
	 *
	 * @param id User Profile Field id which need to move
	 * @param userProfileFieldGroupId new User Profile Field Group Id
	 *
	 * @return updated User Profile Field
	 */
	@PUT
	@Path("/user_profile_field/move/{id}/{userProfileFieldGroupId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileFieldTree> moveUserProfileField(
			 @PathParam("id") Long id, @PathParam("userProfileFieldGroupId") Long userProfileFieldGroupId) {
		return OPFEngine.DirectoryService.moveUserProfileField(id, userProfileFieldGroupId);
	}

//    DELETE-REQUEST:
//    /rest/registry/directory/user_profile_field/{userProfileFieldId} --> DELETE method

	/**
	 * Delete User Profile Field by id
	 *
	 * @param id User Profile Field id
	 *
	 * @return deleted User Profile Field
	 */
	@DELETE
	@Path("/user_profile_field/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserProfileField> deleteUserProfileField(@PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteUserProfileField(id);
	}

    /**
     * Find All user profile fields mapped to user profile field group tht has specified lookup
     * @param userProfileGroupLookup user profile field group lookup
     * @return user profile fields
     */
    @GET
    @Path("/user_profile_field/by-group-lookup/{groupLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<UserProfileField> findUserProfileFieldsByGroupLookup(
            @PathParam("groupLookup") String userProfileGroupLookup) {
        return OPFEngine.DirectoryService.findUserProfileFieldsByGroupLookup(userProfileGroupLookup);
    }

    /**
     * Find user profile field values by specified user id and list of user profile field id list
     * @param userId user id
     * @param userProfileFieldIdList list of user profile field id
     * @return response with list of corresponded user profile fields values
     */
    @GET
    @Path("/user_profile_field/values-by-user-id")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<UserProfileFieldValue> findUserProfileFieldValues(
            @QueryParam("userId") Long userId, @QueryParam("fields") List<Long> userProfileFieldIdList) {
        return OPFEngine.DirectoryService.findUserProfileFieldValues(userId, userProfileFieldIdList);
    }

    /**
     * Save User Profile Fields Values and return response with list of saved values
     * @param request request with input user profile field values
     * @return list of saved user profile field values
     */
    @POST
    @Path("/user_profile_field/save-values")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<UserProfileFieldValue> saveUserProfileFieldsValues(
            ServiceRequest<UserProfileFieldValue> request) {
        return OPFEngine.DirectoryService.saveUserProfileFieldsValues(request.getDataList());
    }

    /**
     * Save User Profile Fields Values and return response with list of saved values
     * @param userIdList request with input user profile field values
     * @param request request with input user profile field values
     * @return list of loaded user profiles
     */
    @POST
    @Path("/user_profile_field/load-profiles")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<UserProfile> loadUserProfiles(
            @QueryParam("userIds") List<Long> userIdList, ServiceRequest<UserProfileField> request) {
        return OPFEngine.DirectoryService.loadUserProfiles(userIdList, request.getDataList());
    }

    /**
	 * Read current user
	 *
	 * @return founded user
	 */
	@GET
	@Path("/user/me")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> readCurrentUser() {
		return OPFEngine.DirectoryService.readCurrentUser();
	}

	//    READ-ONE-REQUEST:
//    /rest/registry/directory/user/{id} --> GET method

	/**
	 * Read user by id
	 *
	 * @param id user id
	 *
	 * @return founded user
	 */
	@GET
	@Path("/user/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> readUser( @PathParam("id") Long id) {
		return OPFEngine.DirectoryService.readUser(id);
	}

	//    /rest/registry/directory/user/last_created --> GET method

	/**
	 * Found last created users
	 *
	 * @param count count users
	 *
	 * @return founded users
	 */
	@GET
	@Path("/user/last_created")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> readLastCreatedUsers( @QueryParam("count") Integer count) {
		return OPFEngine.DirectoryService.readLastCreatedUsers(count);
	}

	//    READ-ALL-REQUEST:
//    /rest/registry/directory/user/search/[term] --> GET method

	/**
	 * Search users by term
	 *
	 *
     * @param term      term expression
     * @param exceptIds exclude ids
     *
     * @param offset
     * @param limit
     * @param sortColumn
     * @param sortDirection @return founded users
	 */
	@GET
	@Path("/user/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> searchAllUsers(
            @QueryParam("term") String term,
            @QueryParam("exceptIds") List<Long> exceptIds,
            @QueryParam("start") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sort") String sortColumn,
            @QueryParam("dir") SortOrder sortDirection) {
		return OPFEngine.DirectoryService.searchAllUsers(term, exceptIds, offset, limit, sortColumn, sortDirection);
	}

    /**
   	 * Advanced Search users by term
   	 *
   	 *
        * @param queryParameters query expressions
        *
        * @param offset
        * @param limit
        * @param sortOrders
   	 */
   	@GET
   	@Path("/user/advanced-search")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<User> advancedSearchUsers(
            @QueryParam("queryParameters") String queryParameters,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sortOrders") String sortOrders) {
   		return OPFEngine.DirectoryService.advancedSearchUsers(queryParameters, offset, limit, sortOrders);
   	}

	//    READ-ALL-BY-REGISTRY-NODE-ID-REQUEST:
//    /rest/registry/directory/user/{registryNodeId} --> GET method

	/**
	 * Read all user by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded users
	 */
	@GET
	@Path("/user/node/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> readAllUsersByRegistryNodeId( @PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.DirectoryService.readAllUsersByRegistryNodeId(registryNodeId);
	}

	//    SEARCH-REQUEST:
//    /rest/registry/directory/user/search/[term]?explicit filters --> GET method
//    This method takes in a search term that will search across email, username, firstname, and lastname to find any matches. In addition, the method should take in any name / value pairs for explicit filters for the user that correspond to other fields declared for the directory on the query string.

	/**
	 * Search users by parent id and term expression
	 *
	 * @param registryNodeId parent id
	 * @param term           term expression
	 *
	 * @return founded users
	 */
	@GET
	@Path("/user/node/search/{registryNodeId}/{term}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> searchUser(
			 @PathParam("registryNodeId") Long registryNodeId,
			 @PathParam("term") String term) {
		return OPFEngine.DirectoryService.searchUser(registryNodeId, term);
	}

	//    CREATE-REQUEST:
//    /rest/registry/directory/user --> POST method

	/**
	 * Create new user
	 *
	 * @param request user data
	 *
	 * @return created user
	 */
	@POST
	@Path("/user")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> createUser( ServiceRequest<User> request) {
		return OPFEngine.DirectoryService.createUser(request.getData());
	}

	/**
	 * Sign up user
	 *
	 * @param request user data
	 *
	 * @return created user
	 */
	@POST
	@Path("/user/signup")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> signUpUser(ServiceRequest<User> request) {
		return OPFEngine.DirectoryService.signUpUser(request.getData());
	}

	//    UPDATE-REQUEST:
//    /rest/registry/directory/user/[username] --> PUT method

	/**
	 * Update user by id
	 *
	 * @param id  user id
	 * @param request user data
	 *
	 * @return updated user
	 */
	@PUT
	@Path("/user/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> updateUser(
			 @PathParam("id") Long id, ServiceRequest<User> request) {
		return OPFEngine.DirectoryService.updateUser(id, request.getData());
	}

	//    DELETE-REQUEST:
//    /rest/registry/directory/user/[username] --> DELETE method

	/**
	 * Delete user by id
	 *
	 * @param id user id
	 *
	 * @return deleted user
	 */
	@DELETE
	@Path("/user/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> deleteUser( @PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteUser(id);
	}

    /**
	 * Read system user by id
	 *
	 * @param id system user id
	 *
	 * @return founded system user
	 */
	@GET
	@Path("/system_user/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> readSystemUser( @PathParam("id") Long id) {
		return OPFEngine.DirectoryService.readSystemUser(id);
	}

	/**
	 * Read all system users by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded system users
	 */
	@GET
	@Path("/system_user/node/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> readAllSystemUsersByRegistryNodeId( @PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.DirectoryService.readAllSystemUsersByRegistryNodeId(registryNodeId);
	}

    /**
	 * Create new system user
	 *
	 * @param request system user data
	 *
	 * @return created system user
	 */
	@POST
	@Path("/system_user")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> createSystemUser(ServiceRequest<SystemUser> request) {
		return OPFEngine.DirectoryService.createSystemUser(request.getData());
	}

	/**
	 * Update system user by id
	 *
	 * @param id system user id
	 * @param request system user data
	 *
	 * @return updated system user
	 */
	@PUT
	@Path("/system_user/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> updateSystemUser(
			 @PathParam("id") Long id, ServiceRequest<SystemUser> request) {
		return OPFEngine.DirectoryService.updateSystemUser(id, request.getData());
	}

	/**
	 * Delete system user by id
	 *
	 * @param id system user id
	 *
	 * @return deleted system user
	 */
	@DELETE
	@Path("/system_user/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> deleteSystemUser(@PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteSystemUser(id);
	}

    /**
	 * Regenerate Consumer Secret Key for system user by id
	 *
	 * @param id system user id
	 *
	 * @return updated system user
	 */
	@PUT
	@Path("/system_user/generate_consumer_secret/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<SystemUser> generateConsumerSecret(
			 @PathParam("id") Long id) {
		return OPFEngine.DirectoryService.generateConsumerSecret(id);
	}

	/**
	 * Read all Global roles by parent id
	 *
	 * @param userId user id
	 * @return founded roles
	 */
	@GET
	@Path("/user/roles/{userId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Role> readAllUserRoles(@PathParam("userId") Long userId) {
		return OPFEngine.DirectoryService.readUserRoles(userId);
	}

	/**
	 * Read all Global roles by parent id
	 *
	 * @param registryNodeId parent id
	 * @param exceptIds      except ids
	 *
	 * @return founded roles
	 */
	@GET
	@Path("/user/global/roles")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Role> readAllGlobalRoles(
			@QueryParam("node") Long registryNodeId,
			@QueryParam("exceptIds") List<Long> exceptIds) {
		return OPFEngine.DirectoryService.readAllGlobalRoles(registryNodeId, exceptIds);
	}

	//    SEARCH-REQUEST:
//    /rest/registry/directory/user/global/roles/search/[term]?explicit filters --> GET method
//    This method takes in a search term that will search across name to find any matches. In addition, the method should take in any name / value pairs for explicit filters for the user that correspond to other fields declared for the directory on the query string.

	/**
	 * Search roles
	 *
	 * @param term      term expression
	 * @param exceptIds except ids
	 *
	 * @return founded roles
	 */
	@GET
	@Path("/user/global/roles/search/{term}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Role> searchRoles(
			@PathParam("term") String term,
			@QueryParam("exceptIds") List<Long> exceptIds) {
		return OPFEngine.DirectoryService.searchRoles(term, exceptIds);
	}

	//    READ-REQUEST:
//    /rest/registry/directory/user/identity/info --> GET method

	/**
	 * Load user identity info
	 *
	 * @return founded user info
	 */
	@GET
	@Path("/user/identity/info/{sessionToken}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<UserIdentity> getUserIdentityInfo(@PathParam("sessionToken") String sessionToken) {
		return OPFEngine.DirectoryService.getUserIdentityInfo(sessionToken);
	}

	/**
	 * Perform search user by email
	 * /directory/user/search/email/{term}
	 * Url
	 *
	 * @param email user email address
	 *
	 * @return response object with UserVO
	 */
	@GET
	@Path("/user/search/email/{email}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<User> findUserByEmail(@PathParam("email") String email) {
		return OPFEngine.DirectoryService.findUserByEmail(email);
	}

    /**
     * Perform search user by username
     *
     * @param username user username
     *
     * @return founded user
     */
    @GET
    @Path("/user/search/username/{username}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<User> findUserByUsername(@PathParam("username") String username) {
        return OPFEngine.DirectoryService.findUserByUsername(username);
    }


    @POST
	@Path("/ldap-connection/check")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<CheckUrl> checkLdapDirectoryConnection(ServiceRequest<CheckUrl> request) {
	    return OPFEngine.DirectoryService.testLdapDirectoryStatus(request.getData());
	}

//    READ-ONE-REQUEST:
//    /rest/registry/directory/[id] --> GET method

	/**
	 * Read directory by id
	 *
	 * @param id directory id
	 *
	 * @return founded directory
	 */
	@GET
	@Path("/directory/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Directory> readDirectory( @PathParam("id") Long id) {
		return OPFEngine.DirectoryService.readDirectory(id);
	}

//    READ-ALL-REQUEST:
//    /rest/registry/directory --> GET method

	/**
	 * Read all directory
	 *
	 * @return founded directory
	 */
	@GET
	@Path("/directory")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Directory> readAllDirectories() {
		return OPFEngine.DirectoryService.readAllDirectories();
	}

	/**
	 * Read ordered directory
	 *
	 * @return founded ordered directory
	 */
	@GET
	@Path("/directory/ordered")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Directory> readOrderedDirectories() {
		return OPFEngine.DirectoryService.readOrderedDirectories();
	}

	/**
	 * Update directory position
	 *
	 * @param id directory id
	 * @param position new position
	 *
	 * @return new position directory
	 */
	@PUT
	@Path("/directory/ordered/{id}/{position}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse updateDirectoryPosition(
			 @PathParam("id") Long id, @PathParam("position") Integer position) {
		return OPFEngine.DirectoryService.updateDirectoryPosition(id, position);
	}

	/**
	 * Read directory services
	 *
	 * @return founded directory services
	 */
	@GET
	@Path("/directory/service")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Directory> readDirectoryServices() {
		return OPFEngine.DirectoryService.readDirectoryServices();
	}

//    CREATE-REQUEST
//    /rest/registry/directory --> POST method

	/**
	 * Create new directory
	 *
	 * @param request directory data
	 *
	 * @return created directory
	 */
	@POST
	@Path("/directory")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createDirectory(ServiceRequest<Directory> request) {
		return OPFEngine.DirectoryService.createDirectory(request.getData());
	}

//    UPDATE-REQUEST
//    /rest/registry/directory/[id] --> PUT method

	/**
	 * Update directory by id
	 *
	 * @param id directory id
	 * @param request     directory data
	 *
	 * @return updated directory
	 */
	@PUT
	@Path("/directory/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateDirectory(
			 @PathParam("id") Long id, ServiceRequest<Directory> request) {
		return OPFEngine.DirectoryService.updateDirectory(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/directory/[id] --> DELETE method

	/**
	 * Delete directory
	 *
	 * @param id directory id
	 *
	 * @return deleted directory
	 */
	@DELETE
	@Path("/directory/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Directory> deleteDirectory(@PathParam(value = "id") Long id) {
		return OPFEngine.DirectoryService.deleteDirectory(id);
	}

    @GET
	@Path("/ldap/user/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<User> searchLdapUsers(
            @QueryParam("directoryId") Long ldapDirectoryId, @QueryParam("term") String term) {
        return OPFEngine.DirectoryService.searchLdapUsers(ldapDirectoryId, term);
    }

    @POST
	@Path("/ldap/user")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<User> createLdapUser(
            ServiceRequest<User> request, @QueryParam("directoryId") Long directoryId) {
		return OPFEngine.DirectoryService.createLdapUser(request.getData(), directoryId);
	}

    @PUT
	@Path("/ldap/user/{username}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<User> updateLdapUser(
            @PathParam(value = "username") String username,
            @QueryParam("directoryId") Long directoryId,
            ServiceRequest<User> request) {
		return OPFEngine.DirectoryService.updateLdapUser(request.getData(), directoryId);
	}

    @DELETE
	@Path("/ldap/user/{username}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<User> deleteLdapUser(
            @PathParam(value = "username") String username, @QueryParam("directoryId") Long directoryId) {
		return OPFEngine.DirectoryService.deleteLdapUser(username, directoryId);
	}

    @GET
	@Path("/ldap/group/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Group> searchLdapGroups(
            @QueryParam("directoryId") Long ldapDirectoryId, @QueryParam("term") String term) {
        return OPFEngine.DirectoryService.searchLdapGroups(ldapDirectoryId, term);
    }

    @GET
	@Path("/ldap/group/load-with-users")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Group> loadLdapGroupWithUsers(
            @QueryParam("directoryId") Long directoryId, @QueryParam("groupName") String groupName) {
        return OPFEngine.DirectoryService.loadLdapGroupWithUsers(directoryId, groupName);
    }

    @POST
	@Path("/ldap/group")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Group> createLdapGroup(
            ServiceRequest<Group> request, @QueryParam("directoryId") Long directoryId) {
		return OPFEngine.DirectoryService.createLdapGroup(request.getData(), directoryId);
	}

    @PUT
	@Path("/ldap/group/{groupName}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Group> updateLdapGroup(
            @PathParam("groupName") String groupName,
            @QueryParam("directoryId") Long directoryId,
            ServiceRequest<Group> request) {
		return OPFEngine.DirectoryService.updateLdapGroup(request.getData(), directoryId);
	}

    @DELETE
	@Path("/ldap/group/{groupName}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Group> deleteLdapGroup(
            @PathParam("groupName") String groupName, @QueryParam("directoryId") Long directoryId) {
		return OPFEngine.DirectoryService.deleteLdapGroup(groupName, directoryId);
	}

    @POST
	@Path("/ldap/user/import")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<User> importLdapUserIfNecessary(
            ServiceRequest<User> request, @QueryParam("ldapDirectoryId") Long ldapDirectoryId) {
        return OPFEngine.DirectoryService.importLdapUserIfNecessary(ldapDirectoryId, request.getData());
    }

    @PUT
	@Path("/group-mapping/by-group-dn")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<GroupMapping> updateGroupMappings(
            @QueryParam("groupDN") String groupDN,
            @QueryParam("directoryId") Long directoryId,
            ServiceRequest<GroupMapping> request) {
		return OPFEngine.DirectoryService.updateGroupMappings(groupDN, directoryId, request.getDataList());
	}

    @GET
	@Path("/group-mapping/load-by-ldap-group")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<GroupMapping> loadGroupMappingsByLdapGroupDN(
            @QueryParam("directoryId") Long directoryId, @QueryParam("groupDN") String groupDN) {
        return OPFEngine.DirectoryService.loadGroupMappingsByLdapGroupDN(directoryId, groupDN);
    }

    @POST
    @Path("/group-mapping/import")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<GroupMapping> importLdapGroup(
            @QueryParam("directoryId") Long directoryId, @QueryParam("groupDN") String groupDN) {
        return OPFEngine.DirectoryService.importLdapGroup(directoryId, groupDN);
    }

    @GET
    @Path("/application")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> readAllApplications() {
        return OPFEngine.DirectoryService.readAllApplications();
    }

    @GET
    @Path("/application/{applicationId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> readApplication(@PathParam("applicationId") Long applicationId) {
        return OPFEngine.DirectoryService.readApplication(applicationId);
    }

    @GET
    @Path("/application/search")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> searchApplications(
            @QueryParam("packageLookup") String packageLookup, @QueryParam("term") String term) {
        return OPFEngine.DirectoryService.searchApplications(packageLookup, term);
    }

    @POST
    @Path("/application")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> createApplication(ServiceRequest<Application> request) {
        return OPFEngine.DirectoryService.createApplication(request.getData());
    }

    @PUT
    @Path("/application/{applicationId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> updateApplication(
            @PathParam("applicationId") Long applicationId, ServiceRequest<Application> request) {
        return OPFEngine.DirectoryService.updateApplication(applicationId, request.getData());
    }

    @DELETE
    @Path("/application/{applicationId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Application> deleteApplication(@PathParam("applicationId") Long applicationId) {
        return OPFEngine.DirectoryService.deleteApplication(applicationId);
    }

    //temporary code for testing ldap authentication on fly
    @Autowired
    @Qualifier("directoryStore")
    protected IDirectoryStore directoryStore;

    @GET
    @Path("/ldap/user/sign-in")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse checkLdapAuthentication(
            @QueryParam("directoryId") Long directoryId, @QueryParam("username") String username,
            @QueryParam("password") String password) {
        DirectoryModel directoryModel = directoryStore.findById(directoryId);
        ServiceResponse response;
        if (directoryModel == null) {
            response = new ServiceResponse("Failed to find specified directory.", false);
        } else if (directoryModel.getDirectoryType() != DirectoryType.LDAP) {
            response = new ServiceResponse("Specified directory is not an LDAP directory", false);
        } else {
            DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
            LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
            adaptor.setSchemaConfig(schemaConfig);
            LdapServiceFacade ldapService = new LdapServiceFacade(
                    new ContextSourceContainer(directoryModel), adaptor);
            //response = performInternal(request, ldapService);
            User authenticatedUser;
            try {
                authenticatedUser = ldapService.authenticate(username, password);
            } catch (Throwable th) {
                authenticatedUser = null;
            }
            response = authenticatedUser == null ?
                    new ServiceResponse("Authentication failed", false) :
                    new ServiceResponse("Authentication successful", true);
        }
        return response;
    }

}