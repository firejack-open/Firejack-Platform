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
package net.firejack.platform.api.directory;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;

import java.util.List;

public interface IDirectoryService {

	/**
	 * Read Group by id
	 *
	 * @param groupId group id
	 *
	 * @return founded group
	 */
	ServiceResponse<Group> readGroup(Long groupId);

	/**
	 * Search groups by term
	 *
	 * @param term      term expression
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	ServiceResponse<Group> searchAllGroups(String term, List<Long> exceptIds);

	/**
	 * Read all groups
	 *
	 * @param exceptIds exclude ids
	 *
	 * @return founded groups
	 */
	ServiceResponse<Group> readAllGroups(List<Long> exceptIds);

	/**
	 * Create new group
	 *
	 * @param data group data
	 *
	 * @return created group
	 */
	ServiceResponse<RegistryNodeTree> createGroup(Group data);

	/**
	 * Update group by id
	 *
	 * @param groupId group id
	 * @param data    group data
	 *
	 * @return updated group
	 */
	ServiceResponse<RegistryNodeTree> updateGroup(Long groupId, Group data);

	/**
	 * Delete group by id
	 *
	 * @param groupId group id
	 *
	 * @return deleted group
	 */
	ServiceResponse<Group> deleteGroup(Long groupId);

	/**
	 * Read all UserProfileFieldGroup by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded UserProfileFieldGroups
	 */
	ServiceResponse<UserProfileFieldGroupTree> readAllUserProfileFieldGroup(Long registryNodeId);

	/**
	 * Read  User Profile Field Group by id
	 *
	 * @param userProfileFieldGroupId user profile field group id
	 *
	 * @return founded  User Profile Field Group
	 */
	ServiceResponse<UserProfileFieldGroup> readUserProfileFieldGroup(Long userProfileFieldGroupId);

	/**
	 * Create new User Profile Field Group
	 *
	 * @param data User Profile Field Group data
	 *
	 * @return created User Profile Field Group
	 */
	ServiceResponse<UserProfileFieldGroup> createUserProfileFieldGroup(UserProfileFieldGroup data);

	/**
	 * Updated User Profile Field Group by id
	 *
	 * @param userProfileFieldGroupId User Profile Field Group id
	 * @param data                    User Profile Field Group data
	 *
	 * @return updated User Profile Field Group
	 */
	ServiceResponse<UserProfileFieldGroup> updateUserProfileFieldGroup(Long userProfileFieldGroupId, UserProfileFieldGroup data);

	/**
	 * Delete User Profile Field Group by id
	 *
	 * @param userProfileFieldGroupId User Profile Field Group id
	 *
	 * @return deleted User Profile Field Group
	 */
	ServiceResponse<UserProfileFieldGroup> deleteUserProfileFieldGroup(Long userProfileFieldGroupId);

    /**
	 * Read User Profile Field by Id
	 *
	 * @param userProfileFieldId ID for get
	 *
	 * @return founded User Profile Field
	 */
    ServiceResponse<UserProfileField> readUserProfileField(Long userProfileFieldId);

	/**
	 * Read all User Profile Field by parent Id
	 *
	 * @param registryNodeId parent id
	 * @param userProfileFieldGroupId group id
	 *
	 * @return founded User Profile Field
	 */
	ServiceResponse<UserProfileFieldTree> readAllUserProfileField(Long registryNodeId, Long userProfileFieldGroupId);

	/**
	 * Create new User Profile Field
	 *
	 * @param data User Profile Field data
	 *
	 * @return created User Profile Field
	 */
	ServiceResponse<UserProfileFieldTree> createUserProfileField(UserProfileField data);

	/**
	 * Update User Profile Field by id
	 *
	 * @param userProfileFieldId User Profile Field id
	 * @param data               User Profile Field data
	 *
	 * @return updated User Profile Field
	 */
	ServiceResponse<UserProfileFieldTree> updateUserProfileField(Long userProfileFieldId, UserProfileField data);

    /**
	 * Move User Profile Field to other group
	 *
	 * @param userProfileFieldId User Profile Field id which need to move
	 * @param userProfileFieldGroupId new User Profile Field Group Id
	 *
	 * @return updated User Profile Field
	 */
    ServiceResponse<UserProfileFieldTree> moveUserProfileField(Long userProfileFieldId, Long userProfileFieldGroupId);

	/**
	 * Delete User Profile Field by id
	 *
	 * @param userProfileFieldId User Profile Field id
	 *
	 * @return deleted User Profile Field
	 */
	ServiceResponse<UserProfileField> deleteUserProfileField(Long userProfileFieldId);

    /**
     * Find All user profile fields mapped to user profile field group tht has specified lookup
     * @param userProfileGroupLookup user profile field group lookup
     * @return user profile fields
     */
    ServiceResponse<UserProfileField> findUserProfileFieldsByGroupLookup(String userProfileGroupLookup);

    /**
     * Find user profile field values by specified user id and list of user profile field id list
     * @param userId user id
     * @param userProfileFieldIdList list of user profile field id
     * @return response with list of corresponded user profile fields values
     */
    ServiceResponse<UserProfileFieldValue> findUserProfileFieldValues(Long userId, List<Long> userProfileFieldIdList);

    /**
     * Save user profile fields values
     * @param profileFieldValues profile fields values
     * @return response with saved user profile fields
     */
    ServiceResponse<UserProfileFieldValue> saveUserProfileFieldsValues(List<UserProfileFieldValue> profileFieldValues);

    /**
     * Load user profiles by specified user id list and list of user profile field id that should be included in the retrieved profiles
     * @param userIdList user id list
     * @param userProfileFields user profile field list
     * @return user profiles by specified user id list
     */
    ServiceResponse<UserProfile> loadUserProfiles(List<Long> userIdList, List<UserProfileField> userProfileFields);

	/**
	 * Read current user
	 *
	 * @return founded user
	 */
	ServiceResponse<User> readCurrentUser();

	/**
	 * Read user by id
	 *
	 * @param userId user id
	 *
	 * @return founded user
	 */
	ServiceResponse<User> readUser(Long userId);

	/**
	 * Found last created users
	 *
	 * @param count count users
	 *
	 * @return founded users
	 */
	ServiceResponse<User> readLastCreatedUsers(int count);

	/**
	 * Search users by term
	 *
	 *
     * @param term      term expression
     * @param exceptIds exclude ids
     *@param offset
     * @param limit
     * @param sortColumn @return founded users
     * @param sortDirection
     * */
	ServiceResponse<User> searchAllUsers(
            String term, List<Long> exceptIds, Integer offset, Integer limit,
            String sortColumn, SortOrder sortDirection);

    ServiceResponse<User> advancedSearchUsers(String queryParameters, Integer offset, Integer limit, String sortOrders);

	/**
	 * Read all user by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded users
	 */
	ServiceResponse<User> readAllUsersByRegistryNodeId(Long registryNodeId);

	/**
	 * Search users by parent id and term expression
	 *
	 * @param registryNodeId parent id
	 * @param term           term expression
	 *
	 * @return founded users
	 */
	ServiceResponse<User> searchUser(Long registryNodeId, String term);

	/**
	 * Create new user
	 *
	 * @param data user data
	 *
	 * @return created user
	 */
	ServiceResponse<User> createUser(User data);

	/**
	 * Sign up user
	 *
	 * @param data user data
	 *
	 * @return created user
	 */
	ServiceResponse<User> signUpUser(User data);

	/**
	 * Update user by id
	 *
	 * @param userId user id
	 * @param data   user data
	 *
	 * @return updated user
	 */
	ServiceResponse<User> updateUser(Long userId, User data);

	/**
	 * Delete user by id
	 *
	 * @param userId user id
	 *
	 * @return deleted user
	 */
	ServiceResponse<User> deleteUser(Long userId);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    SYSTEM USER
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
	 * Read system user by id
	 *
	 * @param userId system user id
	 *
	 * @return founded system user
	 */
	ServiceResponse<SystemUser> readSystemUser(Long userId);

    /**
     * Read all system user by parent id
     *
     * @param registryNodeId parent id
     *
     * @return founded system users
     */
    ServiceResponse<SystemUser> readAllSystemUsersByRegistryNodeId(Long registryNodeId);

    /**
	 * Create new system user
	 *
	 * @param data system user data
	 *
	 * @return created system user
	 */
	ServiceResponse<SystemUser> createSystemUser(SystemUser data);

    /**
	 * Update system user by id
	 *
	 * @param userId system user id
	 * @param data system user data
	 *
	 * @return updated system user
	 */
	ServiceResponse<SystemUser> updateSystemUser(Long userId, SystemUser data);

	/**
	 * Delete system user by id
	 *
	 * @param userId system user id
	 *
	 * @return deleted system user
	 */
	ServiceResponse<SystemUser> deleteSystemUser(Long userId);

    /**
	 * Regenerate Consumer Secret Key for system user by id
	 *
	 * @param userId system user id
	 *
	 * @return updated system user
	 */
    ServiceResponse<SystemUser> generateConsumerSecret(Long userId);

	/**
	 * Read user Global and Context roles by specified user id
	 *
	 * @param userId user id
	 *
	 * @return found roles
	 */
	ServiceResponse<Role> readUserRoles(Long userId);

	/**
	 * Read all Global roles by parent id
	 *
	 * @param registryNodeId parent id
	 * @param exceptIds      except ids
	 *
	 * @return found roles
	 */
	ServiceResponse<Role> readAllGlobalRoles(Long registryNodeId, List<Long> exceptIds);

	/**
	 * Search roles
	 *
	 * @param term      term expression
	 * @param exceptIds except ids
	 *
	 * @return founded roles
	 */
	ServiceResponse<Role> searchRoles(String term, List<Long> exceptIds);

	/**
	 * Load user identity info
	 *
     * @param sessionToken session authentication token
	 * @return founded user info
	 */
	ServiceResponse<UserIdentity> getUserIdentityInfo(String sessionToken);

	/**
	 * Perform search user by email
	 * /directory/user/search/email/{username}
	 *
	 * @param email user email address
	 *
	 * @return response object with User
	 */
	ServiceResponse<User> findUserByEmail(String email);

    /**
	 * Perform search user by username
	 * /directory/user/search/email/{username}
	 *
	 * @param username user username
	 *
	 * @return response object with User
	 */
	ServiceResponse<User> findUserByUsername(String username);

    ServiceResponse<CheckUrl> testLdapDirectoryStatus(CheckUrl connectionData);

	/**
	 * Read directory by id
	 *
	 * @param directoryId directory id
	 *
	 * @return founded directory
	 */
	ServiceResponse<Directory> readDirectory(Long directoryId);

	/**
	 * Read all directory
	 *
	 * @return founded directory
	 */
	ServiceResponse<Directory> readAllDirectories();

	/**
	 * Read ordered directory
	 *
	 * @return founded ordered directory
	 */
	ServiceResponse<Directory> readOrderedDirectories();

	/**
	 * Update directory position
	 *
	 *
	 *
	 * @param directoryId directory id
	 * @param newPosition new position
	 *
	 * @return new position directory
	 */
	ServiceResponse updateDirectoryPosition(Long directoryId, Integer newPosition);

	/**
	 * Read directory services
	 *
	 * @return founded directory services
	 */
	ServiceResponse<Directory> readDirectoryServices();

	/**
	 * Create new directory
	 *
	 * @param data directory data
	 *
	 * @return created directory
	 */
	ServiceResponse<RegistryNodeTree> createDirectory(Directory data);

	/**
	 * Update directory by id
	 *
	 * @param directoryId directory id
	 * @param data        directory data
	 *
	 * @return updated directory
	 */
	ServiceResponse<RegistryNodeTree> updateDirectory(Long directoryId, Directory data);

	/**
	 * Delete directory
	 *
	 * @param directoryId directory id
	 *
	 * @return deleted directory
	 */
	ServiceResponse<Directory> deleteDirectory(Long directoryId);

    ServiceResponse<User> searchLdapUsers(Long ldapDirectoryId, String term);

    ServiceResponse<User> createLdapUser(User user, Long directoryId);

    ServiceResponse<User> updateLdapUser(User user, Long directoryId);

    ServiceResponse<User> deleteLdapUser(String username, Long directoryId);

    ServiceResponse<User> importLdapUserIfNecessary(Long ldapDirectoryId, User userFromLdap);

    ServiceResponse<Group> searchLdapGroups(Long ldapDirectoryId, String term);

    ServiceResponse<Group> loadLdapGroupWithUsers(Long directoryId, String groupName);

    ServiceResponse<Group> createLdapGroup(Group group, Long directoryId);

    ServiceResponse<Group> updateLdapGroup(Group group, Long directoryId);

    ServiceResponse<Group> deleteLdapGroup(String groupName, Long directoryId);

    ServiceResponse<GroupMapping> loadGroupMappingsByLdapGroupDN(Long directoryId, String groupDN);

    ServiceResponse<GroupMapping> updateGroupMappings(
            String groupDN, Long directoryId, List<GroupMapping> groupMappings);

    ServiceResponse<GroupMapping> importLdapGroup(Long directoryId, String groupDN);

    ServiceResponse<Application> readApplication(Long applicationId);

    ServiceResponse<Application> readAllApplications();

    ServiceResponse<Application> searchApplications(String packageLookup, String term);

    ServiceResponse<Application> createApplication(Application application);

    ServiceResponse<Application> updateApplication(Long applicationId, Application application);

    ServiceResponse<Application> deleteApplication(Long applicationId);

}
