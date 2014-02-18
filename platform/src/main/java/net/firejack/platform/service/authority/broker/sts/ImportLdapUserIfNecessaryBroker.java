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

package net.firejack.platform.service.authority.broker.sts;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupMappingModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IGroupMappingStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class ImportLdapUserIfNecessaryBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<User>> {

    public static final String PARAM_LDAP_USER = "LDAP_USER";
    public static final String PARAM_DIRECTORY_ID = "DIRECTORY_ID";

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("directoryStore")
    private IRegistryNodeStore<DirectoryModel> directoryStore;

    @Autowired
    private IGroupMappingStore groupMappingStore;

    @Override
    protected ServiceResponse<User> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        User userFromLdap = (User) request.getData().get(PARAM_LDAP_USER);
        Long ldapDirectoryId = (Long) request.getData().get(PARAM_DIRECTORY_ID);
        ServiceResponse<User> response;
        if (StringUtils.isBlank(userFromLdap.getDistinguishedName())) {
            response = new ServiceResponse<User>(
                    "Provided LDAP user has no Distinguished Name information specified", false);
        } else {
            UserModel ldapUserMirror = userStore.findUserByLdapDN(userFromLdap.getDistinguishedName());
            if (ldapUserMirror == null) {
                ldapUserMirror = factory.convertFrom(UserModel.class, userFromLdap);
                String username = ldapUserMirror.getUsername();
                if (StringUtils.isBlank(username)) {
                    username = "ldap_user";
                } else {
                    username = "ldap_" + username;
                }
                int usernameSuffix = 1;
                boolean isUsernameUnique = false;
                do {
                    String checkUsername = username;
                    if (usernameSuffix > 1) {
                        checkUsername += usernameSuffix;
                    }
                    UserModel user = userStore.findUserByUsername(checkUsername);
                    if (user == null) {
                        username = checkUsername;
                        isUsernameUnique = true;
                    } else {
                        usernameSuffix++;
                    }
                } while (!isUsernameUnique);


                if (StringUtils.isBlank(ldapUserMirror.getEmail())) {
                    ldapUserMirror.setEmail(username + "@change.me");
                } else {
                    UserModel userByEmail = userStore.findUserByEmail(ldapUserMirror.getEmail());
                    ldapUserMirror.setEmail(userByEmail == null ? ldapUserMirror.getEmail() : username + "@change.me");
                }


                DirectoryModel directory = directoryStore.findByLookup(OpenFlame.SIGN_UP_DIRECTORY);
                ldapUserMirror.setRegistryNode(directory);
            }
            User resultUser = synchronizeLdapUserRoles(userFromLdap, ldapUserMirror, ldapDirectoryId);
            response = new ServiceResponse<User>(resultUser, "Ldap User Processed successfully", true);
        }
        return response;
    }

    @Override
    protected void validateArguments(ServiceRequest<NamedValues<Object>> request) throws RuleValidationException {
        //
    }

    private User synchronizeLdapUserRoles(User userFromLdap, UserModel ldapUserMirror, Long ldapDirectoryId) {
        List<RoleModel> roleModels = determineRoles(userFromLdap, ldapDirectoryId);
        List<UserRoleModel> userRoleModels = new ArrayList<UserRoleModel>();
        for (RoleModel role : roleModels) {
            UserRoleModel userRole = new UserRoleModel(ldapUserMirror, role);
            userRoleModels.add(userRole);
        }
        ldapUserMirror.setUserRoles(userRoleModels);
        userStore.save(ldapUserMirror);
        User resultUser = factory.convertTo(User.class, ldapUserMirror);
        List<Role> roles = factory.convertTo(Role.class, roleModels);
        resultUser.setRoles(roles);
        if (resultUser.getUserRoles() != null) {
            for (UserRole userRole : resultUser.getUserRoles()) {
                userRole.setUser(null);
            }
        }
        return resultUser;
    }

    private List<RoleModel> determineRoles(User userFromLdap, Long ldapDirectoryId) {
        List<RoleModel> roles;
        try {
            DirectoryModel directoryModel = directoryStore.findById(ldapDirectoryId);
            DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
            LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
            adaptor.setSchemaConfig(schemaConfig);
            LdapServiceFacade ldapServiceFacade = new LdapServiceFacade(
                    new ContextSourceContainer(directoryModel), adaptor);
            List<Group> userGroups = ldapServiceFacade.getUserGroups(userFromLdap);
            if (userGroups == null || userGroups.isEmpty()) {
                logger.info("No Ldap groups were found. Assigning default roles...");
                roles = getDefaultRoles();
            } else {
                List<String> ldapGroupsDNList = new LinkedList<String>();
                for (Group group : userGroups) {
                    if (StringUtils.isBlank(group.getDistinguishedName())) {
                        logger.warn("Ldap group DN is blank.");
                    } else {
                        ldapGroupsDNList.add(group.getDistinguishedName());
                        logger.info("User [" + userFromLdap.getUsername() +
                                "], Group: [" + group.getDistinguishedName() + "]");
                    }
                }
                Map<String, List<GroupMappingModel>> mappingsByGroupDN =
                        groupMappingStore.loadGroupMappingsForLdapGroupList(ldapDirectoryId, ldapGroupsDNList);
                Set<Long> groupIdList = new HashSet<Long>();
                for (List<GroupMappingModel> groupMappings : mappingsByGroupDN.values()) {
                    for (GroupMappingModel mapping : groupMappings) {
                        groupIdList.add(mapping.getGroup().getId());
                    }
                }
                roles = roleStore.findByGroups(groupIdList);
                if (roles == null || roles.isEmpty()) {
                    roles = getDefaultRoles();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.warn("Assigning default roles...");
            roles = getDefaultRoles();
        }

        return roles;
    }

    private List<RoleModel> getDefaultRoles() {
        List<RoleModel> roles = new ArrayList<RoleModel>();
        RoleModel userRole = roleStore.findByLookup(OpenFlame.SIGN_UP_ROLE);
        if (userRole != null) {
            roles.add(userRole);
        }
        return roles;
    }
}