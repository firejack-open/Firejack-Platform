/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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