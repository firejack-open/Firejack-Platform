/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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