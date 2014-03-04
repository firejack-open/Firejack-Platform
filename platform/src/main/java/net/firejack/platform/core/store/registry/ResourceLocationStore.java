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

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@SuppressWarnings("unused")
@Component("resourceLocationStore")
public class ResourceLocationStore extends RegistryNodeStore<ResourceLocationModel> implements IResourceLocationStore {

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;
    @Autowired
    private Factory modelFactory;

    @Autowired
    private IPermissionStore permissionStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(ResourceLocationModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceLocationModel findById(Long id) {
        return findSingle("ResourceLocation.findByIdWithPermissions", "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        return findAllBySearchTermWithFilter(term, filter, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        return findAllBySearchTermWithFilter(registryNodeIds, term, filter, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterionList.add(expressionAll);
        return findAllWithFilter(criterionList, filter, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        Criterion nameCriterion = Restrictions.like("lookup", "%" + term + "%");
        criterionList.add(nameCriterion);
        return findAllWithFilter(criterionList, filter, paging);
    }

    @Override
    @Transactional
    public void saveWithPermission(ResourceLocationModel resourceLocation) {
        boolean isNew = resourceLocation.getId() == null;
        save(resourceLocation);
        if (isNew) {
            permissionStore.createPermissionForResourceLocation(resourceLocation);
        }
    }

    @Override
    @Transactional
    public void save(ResourceLocationModel resourceLocation) {
        boolean isNew = resourceLocation.getId() == null;
        super.save(resourceLocation);
        ResourceLocation rl = modelFactory.convertTo(ResourceLocation.class, resourceLocation);
        if (isNew) {
            cacheProcessor.addResourceLocation(rl);
        } else {
            cacheProcessor.updateResourceLocation(rl);
        }
    }

    @Override
    @Transactional
    public void saveForGenerator(ResourceLocationModel resourceLocation) {
        boolean isNew = resourceLocation.getId() == null;
        super.save(resourceLocation, false);
        if (!isNew) {
            ResourceLocation rl = modelFactory.convertTo(ResourceLocation.class, resourceLocation);
            cacheProcessor.updateResourceLocation(rl);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<ResourceLocationModel, List<RoleModel>> findRolesByResourceLocation() {
        List<ResourceLocationModel> resourceLocationList = findAll();
        Map<ResourceLocationModel, List<RoleModel>> result;
        if (resourceLocationList == null || resourceLocationList.isEmpty()) {
            return Collections.emptyMap();
        } else {
            result = new HashMap<ResourceLocationModel, List<RoleModel>>();
            for (ResourceLocationModel resourceLocation : resourceLocationList) {
                List<PermissionModel> permissionList =
                        permissionStore.findResourceLocationPermissions(resourceLocation.getId());
                if (permissionList != null && !permissionList.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<RoleModel> roleList = (List<RoleModel>) getHibernateTemplate().findByNamedQueryAndNamedParam(
                            "Permission.findRolesByPermissions", "permissions", permissionList);
                    result.put(resourceLocation, roleList);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllWithPermissions() {
        return find(null, null, "ResourceLocation.findAllWithPermissions");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceLocationModel> findAllWithPermissions(String baseLookup) {
        return find(null, null, "ResourceLocation.findAllWithPermissionsByBaseLookup", "lookupPattern", baseLookup + ".%");
    }

    @Override
    public void delete(ResourceLocationModel resourceLocation) {
        List<PermissionModel> permissions = resourceLocation.getPermissions();
        for (PermissionModel permission : permissions) {
            getPermissionStore().delete(permission);
        }
        super.delete(resourceLocation);
        ResourceLocation rl = modelFactory.convertTo(ResourceLocation.class, resourceLocation);
        cacheProcessor.removeResourceLocation(rl);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceLocationModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("permissions", FetchMode.JOIN);
        return (ResourceLocationModel) criteria.uniqueResult();
    }

}