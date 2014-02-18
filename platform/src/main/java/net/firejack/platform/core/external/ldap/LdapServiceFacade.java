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

package net.firejack.platform.core.external.ldap;

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.*;
import org.springframework.ldap.filter.*;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import java.util.*;


public class LdapServiceFacade {

    private static final String ATTR_OBJECT_CLASS = "objectclass";

    private static final Logger logger = Logger.getLogger(LdapServiceFacade.class);
    private LdapElementsAdaptor ldapElementsAdaptor;
    private LdapSchemaConfig schemaConfig;
    private LdapTemplate ldapTemplate;
    private LdapTemplate usersLdapTemplate;
    private LdapTemplate groupsLdapTemplate;

    public LdapServiceFacade(ContextSourceContainer contextSourceContainer) {
        this(contextSourceContainer, new DefaultLdapElementsAdaptor());
    }

    public LdapServiceFacade(ContextSourceContainer contextSourceContainer, LdapElementsAdaptor ldapElementsAdaptor) {
        if (ldapElementsAdaptor == null || ldapElementsAdaptor.getSchemaConfig() == null) {
            throw new IllegalArgumentException("Ldap Configuration is incorrect.");
        }
        this.ldapTemplate = LdapUtils.populateLdapTemplate(contextSourceContainer.getBaseContextSource());
        this.usersLdapTemplate = LdapUtils.populateLdapTemplate(contextSourceContainer.getUsersContextSource());
        this.groupsLdapTemplate = LdapUtils.populateLdapTemplate(contextSourceContainer.getGroupsContextSource());
        this.ldapElementsAdaptor = ldapElementsAdaptor;
        this.schemaConfig = ldapElementsAdaptor.getSchemaConfig();
    }

    public User authenticate(String username, String password) {
        String filterValue = userEqualsFilter(this.schemaConfig.getUidAttributeName(), username);
        List<User> foundUsers = searchUsersInternallyUsingMapper(filterValue);
        User foundUser = foundUsers == null || foundUsers.isEmpty() ? null : foundUsers.get(0);
        if (foundUser != null) {
            boolean authenticated;
            try {
                authenticated = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filterValue, password);
            } catch (Exception e) {
                authenticated = false;
                logger.error(e.getMessage(), e);
            }
            logger.info("User found in LDAP. Authentication status = " + authenticated);
            foundUser = authenticated ? foundUser : null;
        }
        return foundUser;
    }

    public User createUser(User user) {
        Name dn = this.ldapElementsAdaptor.buildUserDn(user);
        usersLdapTemplate.bind(dn, null, this.ldapElementsAdaptor.buildUserAttributes(user));
        return user;
    }

    public User updateUser(final User user) {
        String filterValue = userEqualsFilter(this.schemaConfig.getUidAttributeName(), user.getUsername());
        List<User> foundUsers = searchUsersInternallyUsingMapper(filterValue);
        if (foundUsers == null || foundUsers.isEmpty()) {
            throw new OpenFlameRuntimeException("Failed to find user for update operation.");
        } else if (foundUsers.size() > 1) {
            throw new OpenFlameRuntimeException("More than one user were found for update operation.");
        }

        User storedUser = foundUsers.get(0);
        if (storedUser == null) {
            throw new OpenFlameRuntimeException("Failed to find user for update operation.");
        }
        Tuple<String, String> lastDNEntryTuple = getLastEntryValueFromDN(storedUser.getDistinguishedName());
        DistinguishedName userDN = new DistinguishedName();
        userDN.add(lastDNEntryTuple.getKey(), lastDNEntryTuple.getValue());

        ModificationItem givenNameModItem = new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("givenName", user.getFirstName()));
        ModificationItem lastNameModItem = new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", user.getLastName()));
        ModificationItem emailModItem = new ModificationItem(
                DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(
                this.schemaConfig.getEmailAttributeName(), user.getEmail()));

        this.usersLdapTemplate.modifyAttributes(userDN,
                new ModificationItem[] {givenNameModItem, lastNameModItem, emailModItem});

        DistinguishedName newUserDn = new DistinguishedName();
        if ("cn".equalsIgnoreCase(this.schemaConfig.getRdnAttributeName())) {
            newUserDn.add(this.schemaConfig.getRdnAttributeName(), user.getFirstName() + " " + user.getLastName());
            this.usersLdapTemplate.rename(userDN, newUserDn);
        }

        return getUser(user.getUsername());
    }

    public void deleteUser(User user) {
        Name dn = this.ldapElementsAdaptor.buildUserDn(user);
        this.usersLdapTemplate.unbind(dn);
    }

    public User getUser(final String userName) {
        String filterValue = userEqualsFilter(this.schemaConfig.getUidAttributeName(), userName);
        List<User> users = searchUsersInternally(filterValue);
        return users == null || users.isEmpty() ? null : users.get(0);
    }

    public List<User> searchUsers(String pattern) {
        String filterValue = StringUtils.isBlank(pattern) ? userClass().encode() :
                userFilter(new LikeFilter(this.schemaConfig.getUidAttributeName(), "*" + pattern + "*"));
        return searchUsersInternally(filterValue);
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        return searchUsersInternally(userClass().encode());
    }

    public List<Group> getUserGroups(User user) {
        EqualsFilter memberFilter = new EqualsFilter(
                this.schemaConfig.getMemberAttributeName(), user.getDistinguishedName());
        return searchGroupsInternallyUsingMapping(groupFilter(memberFilter));
    }

    public void createGroup(Group group) {
        this.groupsLdapTemplate.bind(this.ldapElementsAdaptor.buildGroupDn(group.getName()),
                this.ldapElementsAdaptor.buildGroupAttributes(new DirContextAdapter(), group), null);
        List<User> assignedUsers = group.getAssignedUsers();
        setGroupUsers(true, group.getName(), assignedUsers);
    }

    public void setGroupUsers(boolean isGroupNew, String groupName, List<User> assignedUsers) {
        if (assignedUsers == null || assignedUsers.isEmpty()) {
            if (!isGroupNew) {
                DirContextOperations groupContextOperations =
                        this.ldapTemplate.lookupContext(this.ldapElementsAdaptor.buildGroupDn(groupName));
                groupContextOperations.setAttributeValues(this.schemaConfig.getMemberAttributeName(), new String[0]);
                ldapTemplate.modifyAttributes(groupContextOperations);
            }
        } else {
            OrFilter uidFilter = new OrFilter();
            for (User assignedUser : assignedUsers) {
                uidFilter.or(new EqualsFilter(this.schemaConfig.getUidAttributeName(), assignedUser.getUsername()));
            }
            String filter = userFilter(uidFilter);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            List<User> foundUsersToAssign = searchUsersInternallyUsingMapper(filter);
            if (foundUsersToAssign == null || foundUsersToAssign.isEmpty()) {
                logger.error("Users to assign were not found.");
            } else {
                String[] members = new String[foundUsersToAssign.size()];
                int i = 0;
                for (User userToAssign : foundUsersToAssign) {
                    members[i++] = userToAssign.getDistinguishedName();
                }
                DistinguishedName groupDn = this.ldapElementsAdaptor.buildGroupDn(groupName);
                DirContextOperations groupContextOperations = this.groupsLdapTemplate.lookupContext(groupDn);
                groupContextOperations.setAttributeValues(this.schemaConfig.getMemberAttributeName(), members);
                this.groupsLdapTemplate.modifyAttributes(groupContextOperations);
            }
        }
    }

    public void deleteGroup(Group group) {
        this.groupsLdapTemplate.unbind(this.ldapElementsAdaptor.buildGroupDn(group.getName()));
    }

    public List<Group> findAllGroups() {
        EqualsFilter filter = groupClass();
        return searchGroupsInternallyUsingMapping(filter.encode());
    }

    public List<Group> searchGroups(String pattern) {
        String filterValue = StringUtils.isBlank(pattern) ?
                groupClass().encode() : groupFilter(new LikeFilter("cn", "*" + pattern + "*"));
        return searchGroupsInternallyUsingMapping(filterValue);
    }

    public Group findGroupByName(String name) {
        String filterValue = groupEqualsFilter("cn", name);
        return (Group) ldapTemplate.searchForObject(DistinguishedName.EMPTY_PATH,
                filterValue, this.ldapElementsAdaptor.provideGroupContextMapper());
    }

    //works on both LDAP servers
    public Group loadGroupWithUsers(String groupName) {
        String filter = groupEqualsFilter("cn", groupName);
        return (Group) ldapTemplate.searchForObject(DistinguishedName.EMPTY_PATH, filter, new ContextMapper() {
            @Override
            public Object mapFromContext(Object ctx) {
                DirContextOperations dirContext = (DirContextOperations) ctx;
                Group group = new Group();
                group.setName(dirContext.getStringAttribute("cn"));

                String[] currentMembers = dirContext.getStringAttributes(schemaConfig.getMemberAttributeName());
                Set<String> memberFullList = new HashSet<String>();
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                Filter filter;
                if (currentMembers == null || currentMembers.length == 0) {
                    filter = userClass();
                } else {
                    memberFullList.addAll(Arrays.asList(currentMembers));

                    AndFilter andFilter = new AndFilter();
                    EqualsFilter equalsFilter = userClass();
                    andFilter.and(equalsFilter);

                    List<User> assignedUsers = new ArrayList<User>();
                    for (String memberDN : memberFullList) {
                        DistinguishedName dn = new DistinguishedName(memberDN);
                        String uid;
                        try {
                            uid = dn.getValue(schemaConfig.getUidAttributeName());
                        } catch (Throwable th) {
                            uid = null;
                        }
                        AndFilter userSearchFilter = new AndFilter();
                        userSearchFilter.and(equalsFilter);
                        EqualsFilter userByDNFilter;
                        if (uid == null) {
                            userByDNFilter = new EqualsFilter("distinguishedname", memberDN);
                        } else {
                            userByDNFilter = new EqualsFilter(schemaConfig.getUidAttributeName(),
                                    dn.getValue(schemaConfig.getUidAttributeName()));
                        }
                        userSearchFilter.and(userByDNFilter);
                        List<User> foundUsers = searchUsersInternallyUsingMapper(userSearchFilter.encode());
                        if (foundUsers != null && !foundUsers.isEmpty()) {
                            assignedUsers.add(foundUsers.get(0));
                            andFilter.and(new NotFilter(userByDNFilter));
                        }
                    }
                    group.setAssignedUsers(assignedUsers);
                    filter = andFilter;
                }

                List<User> availableUsers = searchUsersInternally(filter.encode());
                group.setAvailableUsers(availableUsers);
                return group;
            }
        });
    }

    public void updateGroup(Group group) {
        setGroupUsers(false, group.getName(), group.getAssignedUsers());
    }

    public Tuple<String, String> getLastEntryValueFromDN(String distinguishedName) {
        Tuple<String, String> lastEntry;
        if (StringUtils.isBlank(distinguishedName)) {
            lastEntry = null;
        } else {
            DistinguishedName dn = new DistinguishedName(distinguishedName);
            LdapRdn ldapRdn = (LdapRdn) dn.getNames().get(dn.getNames().size() - 1);
            lastEntry = new Tuple<String, String>(ldapRdn.getKey(), ldapRdn.getValue());
        }
        return lastEntry;
    }

    //=========== Helper methods ===========
    private String userEqualsFilter(String attributeName, String attributeValue) {
        return userFilter(new EqualsFilter(attributeName, attributeValue));
    }

    private String userFilter(AbstractFilter filter) {
        return (new AndFilter()).and(userClass()).and(filter).encode();
    }

    private String groupEqualsFilter(String attributeName, String attributeValue) {
        return groupFilter(new EqualsFilter(attributeName, attributeValue));
    }

    private String groupFilter(AbstractFilter filter) {
        return (new AndFilter()).and(groupClass()).and(filter).encode();
    }

    private EqualsFilter userClass() {
        return new EqualsFilter(ATTR_OBJECT_CLASS, this.schemaConfig.getPeopleObjectclass());
    }

    private EqualsFilter groupClass() {
        return new EqualsFilter(ATTR_OBJECT_CLASS, this.schemaConfig.getGroupsObjectclass());
    }

    @SuppressWarnings("unchecked")
    private List<User> searchUsersInternally(String filter) {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return this.ldapTemplate.search(DistinguishedName.EMPTY_PATH,
                filter, controls, this.ldapElementsAdaptor.provideUserAttributesMapper());
    }

    @SuppressWarnings("unchecked")
    private List<User> searchUsersInternallyUsingMapper(String filter) {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return (List<User>) this.ldapTemplate.search(DistinguishedName.EMPTY_PATH,
                filter, this.ldapElementsAdaptor.provideUserContextMapper());
    }

    @SuppressWarnings("unchecked")
    private List<Group> searchGroupsInternallyUsingMapping(String filter) {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return ldapTemplate.search(DistinguishedName.EMPTY_PATH,
                filter, controls, this.ldapElementsAdaptor.provideGroupContextMapper());
    }

}