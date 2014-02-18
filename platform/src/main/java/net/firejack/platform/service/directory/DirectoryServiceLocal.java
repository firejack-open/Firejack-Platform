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
package net.firejack.platform.service.directory;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.IDirectoryService;
import net.firejack.platform.api.directory.domain.*;
import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.service.authority.broker.sts.ImportLdapUserIfNecessaryBroker;
import net.firejack.platform.service.directory.broker.ReadBaseUserListBroker;
import net.firejack.platform.service.directory.broker.application.*;
import net.firejack.platform.service.directory.broker.directory.*;
import net.firejack.platform.service.directory.broker.directory.ldap.*;
import net.firejack.platform.service.directory.broker.field.*;
import net.firejack.platform.service.directory.broker.group.*;
import net.firejack.platform.service.directory.broker.profile.*;
import net.firejack.platform.service.directory.broker.systemuser.*;
import net.firejack.platform.service.directory.broker.user.*;
import net.firejack.platform.web.security.model.context.ContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_DIRECTORY_SERVICE)
public class DirectoryServiceLocal implements IDirectoryService {

	@Autowired
	private ReadGroupBroker readGroupBroker;
	@Autowired
	private SearchGroupListBroker searchGroupListBroker;
	@Autowired
	private CreateGroupBroker createGroupBroker;
	@Autowired
	private UpdateGroupBroker updateGroupBroker;
	@Autowired
	private DeleteGroupBroker deleteGroupBroker;
	@Autowired
	private ReadUserProfileFieldGroupsByRegistryNodeBroker fieldGroupsByRegistryNodeBroker;
	@Autowired
	private ReadUserProfileFieldGroupBroker readUserProfileFieldGroupBroker;
	@Autowired
	private CreateUserProfileFieldGroupBroker createUserProfileFieldGroupBroker;
	@Autowired
	private UpdateUserProfileFieldGroupBroker updateUserProfileFieldGroupBroker;
	@Autowired
	private DeleteUserProfileFieldGroupBroker deleteUserProfileFieldGroupBroker;
    @Autowired
    private ReadUserProfileFieldBroker readUserProfileFieldBroker;
    @Autowired
	private ReadUserProfileFieldsByRegistryNodeBroker profileFieldsByRegistryNodeBroker;
    @Autowired
	private CreateUserProfileFieldBroker createUserProfileFieldBroker;
    @Autowired
	private UpdateUserProfileFieldBroker updateUserProfileFieldBroker;
    @Autowired
    private MoveUserProfileFieldBroker moveUserProfileFieldBroker;
    @Autowired
	private DeleteUserProfileFieldBroker deleteUserProfileFieldBroker;
    @Autowired
    private ReadUserProfileFieldsByGroupLookupBroker readUserProfileFieldsByGroupLookupBroker;
    @Autowired
    private FindProfileFieldValuesBroker findProfileFieldValuesBroker;
    @Autowired
    private CreateUserProfileFieldsValuesBroker createUserProfileFieldsValuesBroker;
    @Autowired
    private LoadUserProfilesBroker loadUserProfilesBroker;
	@Autowired
	private ReadUserBroker readUserBroker;
	@Autowired
	private ReadUserByEmailBroker readUserByEmailBroker;
    @Autowired
	private ReadUserByUsernameBroker readUserByUsernameBroker;
	@Autowired
	private ReadUserListBroker readUserListBroker;
    @Autowired
    private AdvancedSearchUserBroker advancedSearchUserBroker;
	@Autowired
	private ReadLastCreatedUserListBroker readLastCreatedUserListBroker;
	@Autowired
	private ReadUserListByRegistryNodeBroker readUserListByRegistryNodeBroker;
	@Autowired
	private SearchUserListBroker searchUserListBroker;
	@Autowired
	@Qualifier("createUserBroker")
	private CreateUserBroker createUserBroker;
	@Autowired
	private SignUpUserBroker signUpUserBroker;
	@Autowired
	private UpdateUserBroker updateUserBroker;
	@Autowired
	private DeleteUserBroker deleteUserBroker;
    @Autowired
	private ReadSystemUserBroker readSystemUserBroker;
    @Autowired
    private ReadSystemUserListByRegistryNodeBroker readSystemUserListByRegistryNodeBroker;
    @Autowired
    private CreateSystemUserBroker createSystemUserBroker;
    @Autowired
	private UpdateSystemUserBroker updateSystemUserBroker;
	@Autowired
	private DeleteSystemUserBroker deleteSystemUserBroker;
    @Autowired
    private GenerateConsumerSecretBroker generateConsumerSecretBroker;
    @Autowired
	private ReadRoleListBroker readRoleListBroker;
    @Autowired
    private ReadAllUserRolesBroker readAllUserRolesBroker;
	@Autowired
	private SearchGlobalRoleListBroker searchGlobalRoleListBroker;
	@Autowired
	private ReadUserIdentityBroker readUserIdentityBroker;
	@Autowired
	private ReadDirectoryBroker readDirectoryBroker;
	@Autowired
	private ReadDirectoryListBroker readDirectoryListBroker;
	@Autowired
	private ReadOrderedDirectoryListBroker readOrderedDirectoryListBroker;
	@Autowired
	private UpdateDirectoryPositionBroker updateDirectoryPositionBroker;
	@Autowired
	private ReadDirectoryServiceListBroker readDirectoryServiceListBroker;
	@Autowired
	private CreateDirectoryBroker createDirectoryBroker;
	@Autowired
	private UpdateDirectoryBroker updateDirectoryBroker;
	@Autowired
	private DeleteDirectoryBroker deleteDirectoryBroker;
    @Autowired
    private CheckLdapConnectionBroker checkLdapConnectionBroker;
    @Autowired
    private SearchLdapUsersBroker searchLdapUsersBroker;
    @Autowired
    private CreateLdapUserBroker createLdapUserBroker;
    @Autowired
    private UpdateLdapUserBroker updateLdapUserBroker;
    @Autowired
    private DeleteLdapUserBroker deleteLdapUserBroker;
    @Autowired
    private SearchLdapGroupsBroker searchLdapGroupsBroker;
    @Autowired
    private CreateLdapGroupBroker createLdapGroupBroker;
    @Autowired
    private UpdateLdapGroupBroker updateLdapGroupBroker;
    @Autowired
    private UpdateGroupMappingsBroker updateGroupMappingsBroker;
    @Autowired
    private DeleteLdapGroupBroker deleteLdapGroupBroker;
    @Autowired
    private LoadLdapGroupWithUsersBroker loadLdapGroupWithUsersBroker;
    @Autowired
    private LoadLdapGroupWithMappingsBroker loadLdapGroupWithMappingsBroker;
    @Autowired
    private ImportLdapGroupBroker importLdapGroupBroker;
    @Autowired
    private ImportLdapUserIfNecessaryBroker importLdapUserBroker;
    @Autowired
    private ReadApplicationBroker readApplicationBroker;
    @Autowired
    private ReadAllApplicationsBroker readAllApplicationsBroker;
    @Autowired
    private SearchApplicationsBroker searchApplicationsBroker;
    @Autowired
    private CreateApplicationBroker createApplicationBroker;
    @Autowired
    private UpdateApplicationBroker updateApplicationBroker;
    @Autowired
    private DeleteApplicationBroker deleteApplicationBroker;

	@Override
	public ServiceResponse<Group> readGroup(Long groupId) {
		return readGroupBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(groupId)));
	}

	@Override
	public ServiceResponse<Group> searchAllGroups(String term, List<Long> exceptIds) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("term", term);
		namedValues.put("exceptIds", exceptIds);
		return searchGroupListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<Group> readAllGroups(List<Long> exceptIds) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("exceptIds", exceptIds);
		return searchGroupListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createGroup(Group data) {
		return createGroupBroker.execute(new ServiceRequest<Group>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateGroup(Long groupId, Group data) {
		return updateGroupBroker.execute(new ServiceRequest<Group>(data));
	}

	@Override
	public ServiceResponse<Group> deleteGroup(Long groupId) {
		return deleteGroupBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(groupId)));
	}

	@Override
	public ServiceResponse<UserProfileFieldGroupTree> readAllUserProfileFieldGroup(Long registryNodeId) {
		return fieldGroupsByRegistryNodeBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> readUserProfileFieldGroup(Long userProfileFieldGroupId) {
		return readUserProfileFieldGroupBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userProfileFieldGroupId)));
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> createUserProfileFieldGroup(UserProfileFieldGroup data) {
		return createUserProfileFieldGroupBroker.execute(new ServiceRequest<UserProfileFieldGroup>(data));
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> updateUserProfileFieldGroup(Long userProfileFieldGroupId, UserProfileFieldGroup data) {
		return updateUserProfileFieldGroupBroker.execute(new ServiceRequest<UserProfileFieldGroup>(data));
	}

	@Override
	public ServiceResponse<UserProfileFieldGroup> deleteUserProfileFieldGroup(Long userProfileFieldGroupId) {
		return deleteUserProfileFieldGroupBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userProfileFieldGroupId)));
	}

    @Override
    public ServiceResponse<UserProfileField> readUserProfileField(Long userProfileFieldId) {
        return readUserProfileFieldBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userProfileFieldId)));
    }

    @Override
	public ServiceResponse<UserProfileFieldTree> readAllUserProfileField(Long registryNodeId, Long userProfileFieldGroupId) {
        NamedValues<Long> parameters = new NamedValues<Long>();
        parameters.put("registryNodeId", registryNodeId);
        parameters.put("userProfileFieldGroupId", userProfileFieldGroupId);
		return profileFieldsByRegistryNodeBroker.execute(new ServiceRequest<NamedValues<Long>>(parameters));
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> createUserProfileField(UserProfileField data) {
		return createUserProfileFieldBroker.execute(new ServiceRequest<UserProfileField>(data));
	}

	@Override
	public ServiceResponse<UserProfileFieldTree> updateUserProfileField(Long userProfileFieldId, UserProfileField data) {
		return updateUserProfileFieldBroker.execute(new ServiceRequest<UserProfileField>(data));
	}

    @Override
    public ServiceResponse<UserProfileFieldTree> moveUserProfileField(Long userProfileFieldId, Long userProfileFieldGroupId) {
        NamedValues<Long> parameters = new NamedValues<Long>();
        parameters.put("userProfileFieldId", userProfileFieldId);
        parameters.put("userProfileFieldGroupId", userProfileFieldGroupId);
		return moveUserProfileFieldBroker.execute(new ServiceRequest<NamedValues<Long>>(parameters));
    }

    @Override
    @SuppressWarnings("unchecked")
	public ServiceResponse<UserProfileField> deleteUserProfileField(Long userProfileFieldId) {
		return deleteUserProfileFieldBroker.execute(
                new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userProfileFieldId)));
	}

    @Override
    public ServiceResponse<UserProfileField> findUserProfileFieldsByGroupLookup(String userProfileGroupLookup) {
        SimpleIdentifier<String> identifier = new SimpleIdentifier<String>(userProfileGroupLookup);
        return readUserProfileFieldsByGroupLookupBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(identifier));
    }

    @Override
    public ServiceResponse<UserProfileFieldValue> findUserProfileFieldValues(
            Long userId, List<Long> userProfileFieldIdList) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(FindProfileFieldValuesBroker.PARAM_USER_ID, userId);
        params.put(FindProfileFieldValuesBroker.PARAM_USER_PROFILE_FIELD_ID_LIST, userProfileFieldIdList);
        return findProfileFieldValuesBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<UserProfileFieldValue> saveUserProfileFieldsValues(
            List<UserProfileFieldValue> profileFieldValues) {
        ServiceRequest<UserProfileFieldValue> request = new ServiceRequest<UserProfileFieldValue>(profileFieldValues);
        return createUserProfileFieldsValuesBroker.execute(request);
    }

    @Override
    public ServiceResponse<UserProfile> loadUserProfiles(List<Long> userIdList, List<UserProfileField> userProfileFields) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(LoadUserProfilesBroker.PARAM_USER_ID_LIST, userIdList);
        params.put(LoadUserProfilesBroker.PARAM_USER_PROFILE_FIELD_LIST, userProfileFields);
        return loadUserProfilesBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
	public ServiceResponse<User> readCurrentUser() {
		Long userId = ContextManager.getUserInfoProvider().getId();
		return readUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
	}

	@Override
	public ServiceResponse<User> readUser(Long userId) {
		return readUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
	}

	@Override
	public ServiceResponse<User> readLastCreatedUsers(int count) {
		return readLastCreatedUserListBroker.execute(new ServiceRequest<SimpleIdentifier<Integer>>(new SimpleIdentifier<Integer>(count)));
	}

	@Override
	public ServiceResponse<User> searchAllUsers(
            String term, List<Long> exceptIds, Integer offset, Integer limit, String sortColumn, SortOrder sortDirection) {
		NamedValues<Object> namedValues = new NamedValues<Object>();
		namedValues.put(ReadBaseUserListBroker.PARAM_TERM, term);
		namedValues.put(ReadBaseUserListBroker.PARAM_EXCEPT_IDS, exceptIds);
		namedValues.put(ReadBaseUserListBroker.PARAM_OFFSET, offset);
		namedValues.put(ReadBaseUserListBroker.PARAM_LIMIT, limit);
		namedValues.put(ReadBaseUserListBroker.PARAM_SORT_COLUMN, sortColumn);
		namedValues.put(ReadBaseUserListBroker.PARAM_SORT_DIRECTION, sortDirection);
		return readUserListBroker.execute(new ServiceRequest<NamedValues<Object>>(namedValues));
	}

    @Override
    public ServiceResponse<User> advancedSearchUsers(String queryParameters, Integer offset, Integer limit, String sortOrders) {
        NamedValues values = new NamedValues();
        values.put("queryParameters",queryParameters);
        values.put("offset",offset);
        values.put("limit",limit);
        values.put("sortOrders",sortOrders);
        return advancedSearchUserBroker.execute(new ServiceRequest<NamedValues>(values));
    }

    @Override
	public ServiceResponse<User> readAllUsersByRegistryNodeId(Long registryNodeId) {
		return readUserListByRegistryNodeBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
	}

	@Override
	public ServiceResponse<User> searchUser(Long registryNodeId, String term) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("term", term);
		return searchUserListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<User> createUser(User data) {
		return createUserBroker.execute(new ServiceRequest<User>(data));
	}

	@Override
	public ServiceResponse<User> signUpUser(User data) {
		return signUpUserBroker.execute(new ServiceRequest<User>(data));
	}

	@Override
	public ServiceResponse<User> updateUser(Long userId, User data) {
		return updateUserBroker.execute(new ServiceRequest<User>(data));
	}

	@Override
	public ServiceResponse<User> deleteUser(Long userId) {
		return deleteUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    SYSTEM USER
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public ServiceResponse<SystemUser> readSystemUser(Long userId) {
        return readSystemUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
    }

    @Override
    public ServiceResponse<SystemUser> readAllSystemUsersByRegistryNodeId(Long registryNodeId) {
        return readSystemUserListByRegistryNodeBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
    }

    @Override
    public ServiceResponse<SystemUser> createSystemUser(SystemUser data) {
        return createSystemUserBroker.execute(new ServiceRequest<SystemUser>(data));
    }

    @Override
    public ServiceResponse<SystemUser> updateSystemUser(Long userId, SystemUser data) {
        return updateSystemUserBroker.execute(new ServiceRequest<SystemUser>(data));
    }

    @Override
    public ServiceResponse<SystemUser> deleteSystemUser(Long userId) {
        return deleteSystemUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
    }

    @Override
    public ServiceResponse<SystemUser> generateConsumerSecret(Long userId) {
        return generateConsumerSecretBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    ROLE
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public ServiceResponse<Role> readUserRoles(Long userId) {
        return readAllUserRolesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(userId)));
    }

    @Override
	public ServiceResponse<Role> readAllGlobalRoles(Long registryNodeId, List<Long> exceptIds) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("exceptIds", exceptIds);
		namedValues.put("isGlobal", true);
		return readRoleListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<Role> searchRoles(String term, List<Long> exceptIds) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("term", term);
		namedValues.put("exceptIds", exceptIds);
		return searchGlobalRoleListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<UserIdentity> getUserIdentityInfo(String sessionToken) {
		return readUserIdentityBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(sessionToken)));
	}

	@Override
	public ServiceResponse<User> findUserByEmail(String email) {
		return readUserByEmailBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(email)));
	}

    @Override
    public ServiceResponse<User> findUserByUsername(String username) {
        return readUserByUsernameBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(username)));
    }

    @Override
	public ServiceResponse<Directory> readDirectory(Long directoryId) {
		return readDirectoryBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(directoryId)));
	}

	@Override
	public ServiceResponse<Directory> readAllDirectories() {
		return readDirectoryListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<Directory> readOrderedDirectories() {
		return readOrderedDirectoryListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse updateDirectoryPosition(Long directoryId, Integer newPosition) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("directoryId", directoryId);
		namedValues.put("newPosition", newPosition);
		return updateDirectoryPositionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<Directory> readDirectoryServices() {
		return readDirectoryServiceListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createDirectory(Directory data) {
		return createDirectoryBroker.execute(new ServiceRequest<Directory>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateDirectory(Long directoryId, Directory data) {
		return updateDirectoryBroker.execute(new ServiceRequest<Directory>(data));
	}

	@Override
    @SuppressWarnings("unchecked")
	public ServiceResponse<Directory> deleteDirectory(Long directoryId) {
		return deleteDirectoryBroker.execute(
                new ServiceRequest<SimpleIdentifier<Long>>(
                        new SimpleIdentifier<Long>(directoryId)));
	}

    @Override
    public ServiceResponse<CheckUrl> testLdapDirectoryStatus(CheckUrl connectionData) {
        return checkLdapConnectionBroker.execute(new ServiceRequest<CheckUrl>(connectionData));
    }

    @Override
    public ServiceResponse<User> searchLdapUsers(Long ldapDirectoryId, String term) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchLdapUsersBroker.PARAM_DIRECTORY_ID, ldapDirectoryId);
        params.put(SearchLdapUsersBroker.PARAM_SEARCH_TERM, term);
        return searchLdapUsersBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<User> createLdapUser(User user, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(CreateLdapUserBroker.PARAM_USER, user);
        params.put(CreateLdapUserBroker.PARAM_DIRECTORY_ID, directoryId);
        return createLdapUserBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<User> updateLdapUser(User user, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(UpdateLdapUserBroker.PARAM_USER, user);
        params.put(UpdateLdapUserBroker.PARAM_DIRECTORY_ID, directoryId);
        return updateLdapUserBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<User> deleteLdapUser(String username, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(DeleteLdapUserBroker.PARAM_USERNAME, username);
        params.put(DeleteLdapUserBroker.PARAM_DIRECTORY_ID, directoryId);
        return deleteLdapUserBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Group> searchLdapGroups(Long ldapDirectoryId, String term) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(SearchLdapGroupsBroker.PARAM_DIRECTORY_ID, ldapDirectoryId);
        params.put(SearchLdapGroupsBroker.PARAM_SEARCH_TERM, term);
        return searchLdapGroupsBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Group> loadLdapGroupWithUsers(Long directoryId, String groupName) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(LoadLdapGroupWithUsersBroker.PARAM_DIRECTORY_ID, directoryId);
        params.put(LoadLdapGroupWithUsersBroker.PARAM_GROUP_NAME, groupName);
        return loadLdapGroupWithUsersBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Group> createLdapGroup(Group group, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(CreateLdapGroupBroker.PARAM_GROUP, group);
        params.put(CreateLdapGroupBroker.PARAM_DIRECTORY_ID, directoryId);
        return createLdapGroupBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Group> updateLdapGroup(Group group, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(UpdateLdapGroupBroker.PARAM_GROUP, group);
        params.put(UpdateLdapGroupBroker.PARAM_DIRECTORY_ID, directoryId);
        return updateLdapGroupBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Group> deleteLdapGroup(String groupName, Long directoryId) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(DeleteLdapGroupBroker.PARAM_GROUP_NAME, groupName);
        params.put(DeleteLdapGroupBroker.PARAM_DIRECTORY_ID, directoryId);
        return deleteLdapGroupBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<User> importLdapUserIfNecessary(Long ldapDirectoryId, User userFromLdap) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ImportLdapUserIfNecessaryBroker.PARAM_DIRECTORY_ID, ldapDirectoryId);
        params.put(ImportLdapUserIfNecessaryBroker.PARAM_LDAP_USER, userFromLdap);
        ServiceRequest<NamedValues<Object>> request = new ServiceRequest<NamedValues<Object>>(params);
        return importLdapUserBroker.execute(request);
    }

    @Override
    public ServiceResponse<GroupMapping> loadGroupMappingsByLdapGroupDN(Long directoryId, String groupDN) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(LoadLdapGroupWithMappingsBroker.PARAM_DIRECTORY_ID, directoryId);
        params.put(LoadLdapGroupWithMappingsBroker.PARAM_GROUP_DN, groupDN);
        return loadLdapGroupWithMappingsBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<GroupMapping> importLdapGroup(Long directoryId, String groupDN) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(ImportLdapGroupBroker.PARAM_DIRECTORY_ID, directoryId);
        params.put(ImportLdapGroupBroker.PARAM_GROUP_DN, groupDN);
        return importLdapGroupBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<GroupMapping> updateGroupMappings(
            String groupDN, Long ldapDirectoryId, List<GroupMapping> groupMappings) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(UpdateGroupMappingsBroker.PARAM_GROUP_DN, groupDN);
        params.put(UpdateGroupMappingsBroker.PARAM_GROUP_MAPPINGS, groupMappings);
        params.put(UpdateGroupMappingsBroker.PARAM_DIRECTORY_ID, ldapDirectoryId);
        return updateGroupMappingsBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Application> readApplication(Long applicationId) {
        SimpleIdentifier<Long> identifier = new SimpleIdentifier<Long>(applicationId);
        return readApplicationBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(identifier));
    }

    @Override
    public ServiceResponse<Application> readAllApplications() {
        return readAllApplicationsBroker.execute(new ServiceRequest());
    }

    @Override
    public ServiceResponse<Application> searchApplications(String packageLookup, String term) {
        NamedValues<String> params = new NamedValues<String>();
        params.put(SearchApplicationsBroker.PARAM_PACKAGE_LOOKUP, packageLookup);
        params.put(SearchApplicationsBroker.PARAM_SEARCH_TERM, term);
        return searchApplicationsBroker.execute(new ServiceRequest<NamedValues<String>>(params));
    }

    @Override
    public ServiceResponse<Application> createApplication(Application application) {
        return createApplicationBroker.execute(new ServiceRequest<Application>(application));
    }

    @Override
    public ServiceResponse<Application> updateApplication(Long applicationId, Application application) {
        NamedValues<Object> params = new NamedValues<Object>();
        params.put(UpdateApplicationBroker.PARAM_ID, applicationId);
        params.put(UpdateApplicationBroker.PARAM_APPLICATION, application);
        return updateApplicationBroker.execute(new ServiceRequest<NamedValues<Object>>(params));
    }

    @Override
    public ServiceResponse<Application> deleteApplication(Long applicationId) {
        SimpleIdentifier<Long> identifier = new SimpleIdentifier<Long>(applicationId);
        return deleteApplicationBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(identifier));
    }

}