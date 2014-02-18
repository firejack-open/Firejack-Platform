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
package net.firejack.platform.api.directory;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.core.utils.StringUtils;

import java.util.List;

public class DirectoryServiceProxy extends AbstractServiceProxy implements IDirectoryService {

	public DirectoryServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
	public String getServiceUrlSuffix() {
		return "/directory";
	}

	@Override
	public ServiceResponse<Group> readGroup(Long groupId) {
		return get("/group/" + groupId);
	}

	@Override
	public ServiceResponse<Group> searchAllGroups(String term, List<Long> exceptIds) {
        StringBuilder sb = new StringBuilder("/group/search");
        if (StringUtils.isNotBlank(term)) {
            sb.append("?term=").append(term);
        }
		return get(sb.toString(), "exceptIds", exceptIds);
	}

	@Override
	public ServiceResponse<Group> readAllGroups(List<Long> exceptIds) {
		return get("/group", "exceptIds", exceptIds);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createGroup(Group data) {
		return post2("/group", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateGroup(Long groupId, Group data) {
		return put2("/group/" + groupId, data);
	}

	@Override
	public ServiceResponse<Group> deleteGroup(Long groupId) {
		return delete("/group/" + groupId);
	}

	@Override
	public ServiceResponse<UserProfileFieldGroupTree> readAllUserProfileFieldGroup(Long registryNodeId) {
		return get("/user_profile_field_group/node/" + registryNodeId);
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> readUserProfileFieldGroup(Long userProfileFieldGroupId) {
		return get("/user_profile_field_group/" + userProfileFieldGroupId);
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> createUserProfileFieldGroup(UserProfileFieldGroup data) {
		return post2("/user_profile_field_group", data);
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> updateUserProfileFieldGroup(
            Long userProfileFieldGroupId, UserProfileFieldGroup data) {
		return put2("/user_profile_field_group/" + userProfileFieldGroupId, data);
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> deleteUserProfileFieldGroup(Long userProfileFieldGroupId) {
		return delete("/user_profile_field_group/" + userProfileFieldGroupId);
	}

	@Override
	public ServiceResponse<UserProfileField> readUserProfileField(Long userProfileFieldId) {
		return get("/user_profile_field/" + userProfileFieldId);
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> readAllUserProfileField(
            Long registryNodeId, Long userProfileFieldGroupId) {
		return get("/user_profile_field/node/" + registryNodeId + "/" + userProfileFieldGroupId);
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> createUserProfileField(UserProfileField data) {
		return post2("/user_profile_field", data);
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> updateUserProfileField(Long userProfileFieldId, UserProfileField data) {
		return put2("/user_profile_field/" + userProfileFieldId, data);
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> moveUserProfileField(
            Long userProfileFieldId, Long userProfileFieldGroupId) {
		return put("/user_profile_field/move/" + userProfileFieldId + "/" + userProfileFieldGroupId);
	}

	@Override
	public ServiceResponse<UserProfileField> deleteUserProfileField(Long userProfileFieldId) {
		return delete("/user_profile_field/" + userProfileFieldId);
	}

    @Override
    public ServiceResponse<UserProfileField> findUserProfileFieldsByGroupLookup(String userProfileGroupLookup) {
        return get("/user_profile_field/by-group-lookup/" + userProfileGroupLookup);
    }

    @Override
    public ServiceResponse<UserProfileFieldValue> findUserProfileFieldValues(
            Long userId, List<Long> userProfileFieldIdList) {
        ServiceResponse<UserProfileFieldValue> response;
        if (userId == null) {
            response = new ServiceResponse<UserProfileFieldValue>("userId parameter should not be null", false);
        } else if (userProfileFieldIdList == null || userProfileFieldIdList.isEmpty()) {
            response = new ServiceResponse<UserProfileFieldValue>(
                    "userProfileFieldIdList parameter should not be empty", false);
        } else {
            response = get("/user_profile_field/values-by-user-id", "userId", userId, "fields", userProfileFieldIdList);
        }
        return response;
    }

    @Override
    public ServiceResponse<UserProfileFieldValue> saveUserProfileFieldsValues(
            List<UserProfileFieldValue> profileFieldValues) {
        ServiceResponse<UserProfileFieldValue> response;
        if (profileFieldValues == null) {
            response = new ServiceResponse<UserProfileFieldValue>("User Profile Field values parameter should not be null.", false);
        } else if (profileFieldValues.isEmpty()) {
            response = new ServiceResponse<UserProfileFieldValue>("No User Profile Fields values to save.", false);
        } else {
            response = post("/user_profile_field/save-values", profileFieldValues);
        }
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<UserProfile> loadUserProfiles(
            List<Long> userIdList, List<UserProfileField> userProfileFields) {
        ///user_profile_field/load-profiles
        ServiceResponse<UserProfile> response;
        if (userIdList == null) {
            response = new ServiceResponse<UserProfile>("No user id information were specified.", false);
        } else {
            ServiceRequest<UserProfileField> request = new ServiceRequest<UserProfileField>();
            request.setDataList(userProfileFields);
            response = prepare("/user_profile_field/load-profiles", "userIds", userIdList)
                    .post(ServiceResponse.class, request);
        }
        return response;
    }

    @Override
	public ServiceResponse<User> readCurrentUser() {
		return get("/user/me");
	}

	@Override
	public ServiceResponse<User> readUser(Long userId) {
		return get("/user/" + userId);
	}

	@Override
	public ServiceResponse<User> readLastCreatedUsers(int count) {
		return get("/user/last_created", "count", count);
	}

	@Override
	public ServiceResponse<User> searchAllUsers(
            String term, List<Long> exceptIds, Integer offset, Integer limit,
            String sortColumn, SortOrder sortDirection) {
		return get("/user/search", "term", term, "exceptIds", exceptIds,
                "start", offset, "limit", limit, "sort", sortColumn, "dir", sortDirection);
	}

    @Override
   	public ServiceResponse<User> advancedSearchUsers(String queryParameters, Integer offset, Integer limit, String sortOrders) {
   		return get("/user/advanced-search","queryParameters",queryParameters,"offset",offset,"limit",limit,"sortOrders",sortOrders);
   	}

    @Override
	public ServiceResponse<User> readAllUsersByRegistryNodeId(Long registryNodeId) {
		return get("/user/node/" + registryNodeId);
	}

	@Override
	public ServiceResponse<User> searchUser(Long registryNodeId, String term) {
		return get("/user/node/search/" + registryNodeId + "/" + term);
	}

	@Override
	public ServiceResponse<User> createUser(User data) {
		return post2("/user", data);
	}

	@Override
	public ServiceResponse<User> signUpUser(User data) {
		return post2("/user/signup", data);
	}

	@Override
	public ServiceResponse<User> updateUser(Long userId, User data) {
		return put2("/user/" + userId, data);
	}

	@Override
	public ServiceResponse<User> deleteUser(Long userId) {
		return delete("/user/" + userId);
	}

    @Override
    public ServiceResponse<SystemUser> readSystemUser(Long userId) {
        return get("/system_user/" + userId);
    }

    @Override
    public ServiceResponse<SystemUser> readAllSystemUsersByRegistryNodeId(Long registryNodeId) {
        return get("/system_user/node/" + registryNodeId);
    }

    @Override
    public ServiceResponse<SystemUser> createSystemUser(SystemUser data) {
        return post2("/system_user", data);
    }

    @Override
    public ServiceResponse<SystemUser> updateSystemUser(Long userId, SystemUser data) {
        return put2("/system_user/" + userId, data);
    }

    @Override
    public ServiceResponse<SystemUser> deleteSystemUser(Long userId) {
        return delete("/system_user/" + userId);
    }

    @Override
    public ServiceResponse<SystemUser> generateConsumerSecret(Long userId) {
        return put("/system_user/" + userId);
    }

    @Override
    public ServiceResponse<Role> readUserRoles(Long userId) {
        ServiceResponse<Role> response;
        if (userId == null) {
            response = new ServiceResponse<Role>("Specified user id should not be null.", false);
        } else {
            response = getAsSystem(getSystemUserSessionToken(), "/user/roles/" + userId);
        }
        return response;
    }

    @Override
	public ServiceResponse<Role> readAllGlobalRoles(Long registryNodeId, List<Long> exceptIds) {
		return get("/user/global/roles", "node", registryNodeId, "exceptIds", exceptIds);
	}

	@Override
	public ServiceResponse<Role> searchRoles(String term, List<Long> exceptIds) {
		return get("/user/global/roles/search/" + term, "exceptIds", exceptIds);
	}

	@Override
	public ServiceResponse<UserIdentity> getUserIdentityInfo(String sessionToken) {
		ServiceResponse<UserIdentity> response;
		if (StringUtils.isBlank(sessionToken)) {
			response = new ServiceResponse<UserIdentity>("sessionToken parameter should not be blank.", false);
		} else {
			response = getAsSystem(getSystemUserSessionToken(), "/user/identity/info/" + sessionToken);
		}
		return response;
	}

	@Override
	public ServiceResponse<User> findUserByEmail(String email) {
		return get("/user/search/email/" + email);
	}

    @Override
	public ServiceResponse<User> findUserByUsername(String username) {
		return get("/user/search/username/" + username);
	}

    @Override
    public ServiceResponse<CheckUrl> testLdapDirectoryStatus(CheckUrl connectionData) {
        return post("/ldap-connection/check", connectionData);
    }

    @Override
	public ServiceResponse<Directory> readDirectory(Long directoryId) {
		return get("/directory/" + directoryId);
	}

	@Override
	public ServiceResponse<Directory> readAllDirectories() {
		return get("/directory/");
	}

	@Override
	public ServiceResponse<Directory> readOrderedDirectories() {
		return get("/directory/ordered");
	}

	@Override
	public ServiceResponse updateDirectoryPosition(Long directoryId, Integer newPosition) {
		return put("/directory/ordered/" + directoryId + "/" + newPosition);
	}

	@Override
	public ServiceResponse<Directory> readDirectoryServices() {
		return get("/directory/service");
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createDirectory(Directory data) {
		return post2("/directory", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateDirectory(Long directoryId, Directory data) {
		return put2("/directory/" + directoryId, data);
	}

	@Override
	public ServiceResponse<Directory> deleteDirectory(Long directoryId) {
		return delete("/directory/" + directoryId);
	}

    @Override
    public ServiceResponse<User> searchLdapUsers(Long ldapDirectoryId, String term) {
        ServiceResponse<User> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<User>("LDAP directory parameter should not be null.", false);
        } else {
            response = get("/ldap/user/search", "directoryId", ldapDirectoryId, "term", term);
        }
        return response;
    }

    @Override
    public ServiceResponse<User> createLdapUser(User user, Long ldapDirectoryId) {
        ServiceResponse<User> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<User>("LDAP directory parameter should not be null.", false);
        } else if (user == null) {
            response = new ServiceResponse<User>("User should not be null.", false);
        } else {
            response = post("/ldap/user", user, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<User> updateLdapUser(User user, Long ldapDirectoryId) {
        ServiceResponse<User> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<User>("LDAP directory parameter should not be null.", false);
        } else if (user == null) {
            response = new ServiceResponse<User>("User should not be null.", false);
        } else if (StringUtils.isBlank(user.getUsername())) {
            response = new ServiceResponse<User>("Username should not be blank.", false);
        } else {
            response = put("/ldap/user/" + user.getUsername(), user, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<User> deleteLdapUser(String username, Long ldapDirectoryId) {
        ServiceResponse<User> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<User>("LDAP directory parameter should not be null.", false);
        } else if (StringUtils.isBlank(username)) {
            response = new ServiceResponse<User>("Username should not be blank.", false);
        } else {
            response = delete("/ldap/user/" + username, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<Group> searchLdapGroups(Long ldapDirectoryId, String term) {
        ServiceResponse<Group> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<Group>("LDAP directory parameter should not be null.", false);
        } else {
            response = get("/ldap/group/search", "directoryId", ldapDirectoryId, "term", term);
        }
        return response;
    }

    @Override
    public ServiceResponse<Group> loadLdapGroupWithUsers(Long ldapDirectoryId, String groupName) {
        ServiceResponse<Group> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<Group>("LDAP directory parameter should not be null.", false);
        } else {
            response = get("/ldap/group/load-with-users", "directoryId", ldapDirectoryId, "groupName", groupName);
        }
        return response;
    }

    @Override
    public ServiceResponse<Group> createLdapGroup(Group group, Long ldapDirectoryId) {
        ServiceResponse<Group> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<Group>("LDAP directory parameter should not be null.", false);
        } else if (group == null) {
            response = new ServiceResponse<Group>("Group should not be null.", false);
        } else {
            response = post("/ldap/group", group, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<Group> updateLdapGroup(Group group, Long ldapDirectoryId) {
        ServiceResponse<Group> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<Group>("LDAP directory parameter should not be null.", false);
        } else if (group == null) {
            response = new ServiceResponse<Group>("Group should not be null.", false);
        } else if (StringUtils.isBlank(group.getName())) {
            response = new ServiceResponse<Group>("Group name should not be blank.", false);
        } else {
            response = put("/ldap/group/" + group.getName(), group, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<Group> deleteLdapGroup(String groupName, Long ldapDirectoryId) {
        ServiceResponse<Group> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<Group>("LDAP directory parameter should not be null.", false);
        } else if (StringUtils.isBlank(groupName)) {
            response = new ServiceResponse<Group>("Group name should not be blank.", false);
        } else {
            response = delete("/ldap/group/" + groupName, "directoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<User> importLdapUserIfNecessary(Long ldapDirectoryId, User userFromLdap) {
        ServiceResponse<User> response;
        if (userFromLdap == null) {
            response = new ServiceResponse<User>("LDAP user parameter should not be null.", false);
        }else {
            response = post("/ldap/user/import", userFromLdap, "ldapDirectoryId", ldapDirectoryId);
        }
        return response;
    }

    @Override
    public ServiceResponse<GroupMapping> loadGroupMappingsByLdapGroupDN(Long ldapDirectoryId, String groupDN) {
        ServiceResponse<GroupMapping> response;
        if (ldapDirectoryId == null) {
            response = new ServiceResponse<GroupMapping>("LDAP directory parameter should not be null.", false);
        } else {
            response = get("/group-mapping/load-by-ldap-group", "directoryId", ldapDirectoryId, "groupDN", groupDN);
        }
        return response;
    }

    @Override
    public ServiceResponse<GroupMapping> updateGroupMappings(
            String groupDN, Long ldapDirectoryId, List<GroupMapping> groups) {
        ServiceResponse<GroupMapping> response;
        if (StringUtils.isBlank(groupDN)) {
            response = new ServiceResponse<GroupMapping>("LDAP groupDN parameter should not be null.", false);
        } else if (ldapDirectoryId == null) {
            response = new ServiceResponse<GroupMapping>("LDAP directory parameter should not be null.", false);
        } else if (groups == null || groups.isEmpty()) {
            response = new ServiceResponse<GroupMapping>("Group mappings should not be empty.", false);
        } else {
            response = put("/group-mapping/by-group-dn",
                    groups, "directoryId", ldapDirectoryId, "groupDN", groupDN);
        }
        return response;
    }

    @Override
    public ServiceResponse<GroupMapping> importLdapGroup(Long ldapDirectoryId, String groupDN) {
        ServiceResponse<GroupMapping> response;
        if (StringUtils.isBlank(groupDN)) {
            response = new ServiceResponse<GroupMapping>("LDAP groupDN parameter should not be null.", false);
        } else if (ldapDirectoryId == null) {
            response = new ServiceResponse<GroupMapping>("LDAP directory parameter should not be null.", false);
        } else {
            response = post("/group-mapping/import", "directoryId", ldapDirectoryId, "groupDN", groupDN);
        }
        return response;
    }

    @Override
    public ServiceResponse<Application> readAllApplications() {
        return get("/application");
    }

    @Override
    public ServiceResponse<Application> readApplication(Long applicationId) {
        ServiceResponse<Application> response;
        if (applicationId == null) {
            response = new ServiceResponse<Application>("Application Id is null", false);
        } else {
            response = get("/application/" + applicationId);
        }
        return response;
    }

    @Override
    public ServiceResponse<Application> searchApplications(String packageLookup, String term) {
        ServiceResponse<Application> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<Application>("Package lookup should not be null.", false);
        } else if (StringUtils.isBlank(term)) {
            response = get("/application/search", "packageLookup", packageLookup);
        } else {
            response = get("/application/search", "packageLookup", packageLookup, "term", term);
        }
        return response;
    }

    @Override
    public ServiceResponse<Application> createApplication(Application application) {
        ServiceResponse<Application> response;
        if (application == null) {
            response = new ServiceResponse<Application>("Application to create is null", false);
        } else {
            response = post("/application", application);
        }
        return response;
    }

    @Override
    public ServiceResponse<Application> updateApplication(Long applicationId, Application application) {
        ServiceResponse<Application> response;
        if (applicationId == null) {
            response = new ServiceResponse<Application>("Application id is null", false);
        } else if (application == null) {
            response = new ServiceResponse<Application>("Application to update is null", false);
        } else {
            response = put("/application/" + applicationId, application);
        }
        return response;
    }

    @Override
    public ServiceResponse<Application> deleteApplication(Long applicationId) {
        ServiceResponse<Application> response;
        if (applicationId == null) {
            response = new ServiceResponse<Application>("Application Id is null", false);
        } else {
            response = delete("/application/" + applicationId);
        }
        return response;
    }

}