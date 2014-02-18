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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.external.ldap.LdapUtils;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupMappingModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Component("groupMappingStore")
public class GroupMappingStore extends BaseStore<GroupMappingModel, Long> implements IGroupMappingStore {

    @Autowired
    @Qualifier("groupStore")
    private IGroupStore groupStore;

    @Override
    public Class<GroupMappingModel> getClazz() {
        return GroupMappingModel.class;
    }

    @Override
    @Transactional
    public void updateGroupMappingsForLdapGroup(Long directoryId, String ldapGroupDN, Collection<Long> groupIdList) {
        if (StringUtils.isBlank(ldapGroupDN)) {
            throw new IllegalArgumentException("LDAP groupDN parameter should not be blank.");
        } else if (directoryId == null) {
            throw new IllegalArgumentException("LDAP groupDN parameter should not be blank.");
        }
        DirectoryModel directoryModel = getHibernateTemplate().get(DirectoryModel.class, directoryId);
        if (directoryModel == null) {
            throw new IllegalArgumentException("LDAP groupDN parameter should not be blank.");
        }
        update("GroupMapping.deleteAllByDirectoryIdAndGroupDN", "groupDN", ldapGroupDN, "directoryId", directoryId);
        if (groupIdList != null && !groupIdList.isEmpty()) {
            List<GroupMappingModel> mappingModels = new ArrayList<GroupMappingModel>();
            for (Long groupId : groupIdList) {
                GroupMappingModel model = new GroupMappingModel();
                model.setLdapGroupDN(ldapGroupDN);
                model.setDirectory(directoryModel);
                GroupModel group = new GroupModel();
                group.setId(groupId);
                model.setGroup(group);
                mappingModels.add(model);
            }
            getHibernateTemplate().saveOrUpdateAll(mappingModels);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<GroupMappingModel> loadGroupMappingsForLdapGroup(Long directoryId, String ldapGroupDN) {
        List<GroupMappingModel> mappings;
        if (directoryId == null || StringUtils.isBlank(ldapGroupDN)) {
            mappings = Collections.emptyList();
        } else {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.setFetchMode("group", FetchMode.JOIN);
            criteria.add(Restrictions.eq("ldapGroupDN", ldapGroupDN));
            criteria.add(Restrictions.eq("directory.id", directoryId));
            mappings = criteria.list();
        }
        return mappings;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Map<String, List<GroupMappingModel>> loadGroupMappingsForLdapGroupList(
            Long directoryId, List<String> ldapGroupDNList) {
        List<GroupMappingModel> mappings;
        if (directoryId == null || ldapGroupDNList == null || ldapGroupDNList.isEmpty()) {
            mappings = Collections.emptyList();
        } else {
            Criteria criteria = getSession().createCriteria(getClazz());
            criteria.setFetchMode("group", FetchMode.JOIN);
            criteria.add(Restrictions.in("ldapGroupDN", ldapGroupDNList));
            criteria.add(Restrictions.eq("directory.id", directoryId));
            mappings = criteria.list();
        }
        Map<String, List<GroupMappingModel>> groupMappings = new HashMap<String, List<GroupMappingModel>>();
        if (mappings != null) {
            for (GroupMappingModel mappingModel : mappings) {
                List<GroupMappingModel> groupMappingModels = groupMappings.get(mappingModel.getLdapGroupDN());
                if (groupMappingModels == null) {
                    groupMappingModels = new LinkedList<GroupMappingModel>();
                    groupMappings.put(mappingModel.getLdapGroupDN(), groupMappingModels);
                }
                groupMappingModels.add(mappingModel);
            }
        }
        return groupMappings;
    }

    @Override
    @Transactional
    public GroupMappingModel importMapping(Long directoryId, String ldapGroupDN) {
        if (StringUtils.isBlank(ldapGroupDN)) {
            throw new IllegalArgumentException("LDAP GroupDN parameter should not be blank.");
        }
        DirectoryModel directoryModel = getHibernateTemplate().get(DirectoryModel.class, directoryId);
        if (directoryModel == null) {
            throw new IllegalArgumentException("Failed to find specified directory model.");
        }
        Criteria criteria  = getSession().createCriteria(getClazz());
        criteria.add(Restrictions.eq("directory.id", directoryId));
        criteria.add(Restrictions.eq("ldapGroupDN", ldapGroupDN));
        GroupMappingModel storedMapping = (GroupMappingModel) criteria.uniqueResult();

        if (storedMapping == null) {
            String groupName = LdapUtils.getLastEntryValueFromDN(ldapGroupDN).getValue();
            String groupLookup = directoryModel.getLookup() + '.' + StringUtils.normalize(groupName);
            GroupModel groupModel = groupStore.findByLookup(groupLookup);
            if (groupModel == null) {
                groupModel = new GroupModel();
                groupModel.setName(groupName);
                groupModel.setPath(directoryModel.getLookup());
                groupModel.setLookup(groupLookup);
                groupModel.setParent(directoryModel);
                groupModel.setDirectory(directoryModel);

                groupStore.save(groupModel);
            }

            storedMapping = new GroupMappingModel();
            storedMapping.setGroup(groupModel);
            storedMapping.setDirectory(directoryModel);
            storedMapping.setLdapGroupDN(ldapGroupDN);

            saveOrUpdate(storedMapping);
        }
        return storedMapping;
    }
}