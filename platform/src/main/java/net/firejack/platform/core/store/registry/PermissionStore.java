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

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
@Component("permissionStore")
public class PermissionStore extends RegistryNodeStore<PermissionModel> implements IPermissionStore {

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;
    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;
    @Autowired
    private Factory modelFactory;

    /***/
    @PostConstruct
    public void init() {
        setClazz(PermissionModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionModel findByIdWithRolesAndNavElements(Long id) {
        PermissionModel permission = findSingle("Permission.findByIdWithRoles", "id", id);
        Hibernate.initialize(permission.getNavigationElements());
        return permission;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter) {
        return findAllBySearchTermWithFilter(term, filter, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion nameCriterion = Restrictions.like("lookup", '%' + term + '%');
        criterions.add(nameCriterion);
        return findAllWithFilter(criterions, filter, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter) {
        return findAllBySearchTermWithFilter(registryNodeIds, term, filter, null);
    }

    @Override
    public List<PermissionModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", '%' + term + '%');
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
        return findAllWithFilter(criterions, filter, paging);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<PermissionModel> findRolePermissions(Long roleId) {
        return find(null, null, "Permission.findPermissionsByRoleId",
                "roleId", roleId);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<PermissionModel> findRolePermissions(Long roleId, String baseLookup) {
        return find(null, null, "Permission.findPermissionsByRoleIdAndBaseLookup",
                "roleId", roleId, "baseLookup", baseLookup + ".%");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<PermissionModel>> findRolePermissions(List<Long> roles) {
        Map<Long, List<PermissionModel>> rolePermissions = new HashMap<Long, List<PermissionModel>>();
        for (Long roleId : roles) {
            List<PermissionModel> permissions = find(
                    null, null, "Permission.findPermissionsByRoleId", "roleId", roleId);
            rolePermissions.put(roleId, permissions == null ? Collections.<PermissionModel>emptyList() : permissions);
        }
        return rolePermissions;
    }

    @Override
    @Transactional
    public void createPermissionByAction(ActionModel action) {
        PermissionModel permissionModel = new PermissionModel();
        permissionModel.setChildCount(0);
        permissionModel.setName(action.getName());
        permissionModel.setPath(action.getPath());
        permissionModel.setLookup(action.getLookup());
        permissionModel.setParent(action.getParent());
        permissionModel.setActions(Arrays.asList(action));
        saveOrUpdate(permissionModel);
        if (ConfigContainer.isAppInstalled()) {
            Permission permission = modelFactory.convertTo(Permission.class, permissionModel);
            cacheProcessor.associatePermissionWithAction(action.getLookup(), permission);
        }
    }

    @Override
    @Transactional
    public void createPermissionByNavigationElement(NavigationElementModel navigationElement) {
        PermissionModel permissionModel = new PermissionModel();
        permissionModel.setChildCount(0);
        permissionModel.setName(navigationElement.getName());
        permissionModel.setPath(navigationElement.getPath());
        permissionModel.setLookup(navigationElement.getLookup());
        permissionModel.setParent(navigationElement.getParent());
        permissionModel.setNavigationElements(Arrays.asList(navigationElement));
        saveOrUpdate(permissionModel);
        if (ConfigContainer.isAppInstalled()) {
            Permission permission = modelFactory.convertTo(Permission.class, permissionModel);
            cacheProcessor.associatePermissionWithNavigation(navigationElement.getLookup(), permission);
        }
    }

    @Override
    @Transactional
    public void createPermissionForResourceLocation(ResourceLocationModel resourceLocation) {
        PermissionModel permissionModel = new PermissionModel();
        permissionModel.setChildCount(0);
        permissionModel.setParent(resourceLocation.getParent());
        permissionModel.setName(resourceLocation.getName());
        permissionModel.setPath(resourceLocation.getPath());
        permissionModel.setLookup(resourceLocation.getLookup());
        permissionModel.setResourceLocations(Arrays.asList(resourceLocation));
        saveOrUpdate(permissionModel);
        if (ConfigContainer.isAppInstalled()) {
            Permission permission = modelFactory.convertTo(Permission.class, permissionModel);
            cacheProcessor.associatePermissionWithResourceLocation(resourceLocation.getLookup(), permission);
        }
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<PermissionModel> findResourceLocationPermissions(Long resourceLocationId) {
        final ResourceLocationModel resourceLocation =
                getHibernateTemplate().load(ResourceLocationModel.class, resourceLocationId);
        List<PermissionModel> permissionList;
        if (resourceLocation == null) {
            permissionList = null;
        } else {
            permissionList = getHibernateTemplate().execute(new HibernateCallback<List<PermissionModel>>() {
                @Override
                public List<PermissionModel> doInHibernate(Session session) throws HibernateException, SQLException {
                    Criteria criteria = session.createCriteria(getClazz());
                    criteria.add(Restrictions.eq("path", resourceLocation.getLookup()));
                    return criteria.list();
                }
            });
        }
        return permissionList;
    }

    @Override
    @Transactional
    public String findRegistryNodeRefPath(Long permissionId) {
        StringBuilder sb = new StringBuilder();
        PermissionModel registryNode = getHibernateTemplate().get(PermissionModel.class, permissionId);
        if (registryNode != null && registryNode.getParent() != null) {
            RegistryNodeModel rn = getHibernateTemplate().get(
                    RegistryNodeModel.class, registryNode.getParent().getId());
            if (rn != null) {
                String rnName = checkRootDomainName(rn);
                sb.append(rnName);
                while (rn.getParent() != null) {
                    rn = getHibernateTemplate().get(RegistryNodeModel.class, rn.getParent().getId());
                    if (rn != null) {
                        rnName = checkRootDomainName(rn);
                        sb.insert(0, rnName).insert(rnName.length(), '.');
                    } else {
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String findRegistryNodeRef(Long permissionId) {
        String refPath = null;
        if (permissionId != null) {
            PermissionModel permission = getHibernateTemplate().get(getClazz(), permissionId);
            if (permission != null) {
                refPath = findRegistryNodeRefPath(permissionId);
                return (StringUtils.isNotBlank(refPath) ? refPath + '.' : "") + permission.getName();
            }
        }
        return refPath;
    }

    @Override
    @Transactional
    public void save(PermissionModel permission) {//todo:check whether cache should be updated. Because it seems that this method does not change permission lookup
        saveWithoutUpdateChildren(permission);
    }



    @Override
    @Transactional
    public void updateParent(Long registryNodeId) {
        List<PermissionModel> permissions = findChildrenByParentId(registryNodeId, null);
        List<UserPermission> oldPermissions = AuthorizationVOFactory.convertPermissions(permissions);
        for (PermissionModel permission : permissions) {
            getCacheService().removeCacheByRegistryNode(permission);
        }
        if (ConfigContainer.isAppInstalled()) {
            List<UserPermission> newPermissions = AuthorizationVOFactory.convertPermissions(permissions);
            if (!((oldPermissions == null || oldPermissions.isEmpty()) && (newPermissions == null || newPermissions.isEmpty()))) {
                cacheProcessor.updatePermissions(oldPermissions, newPermissions);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<PermissionModel> permissions = findAllByParentIdWithFilter(registryNodeId, null);
        super.deleteAll(permissions);
        cacheProcessor.removePermissions(AuthorizationVOFactory.convertPermissions(permissions));
    }

    @Override
    @Transactional
    public void delete(PermissionModel permission) {
        super.delete(permission);
        cacheProcessor.removePermission(AuthorizationVOFactory.convert(permission));
    }

    private String checkRootDomainName(RegistryNodeModel rootDomainCandidate) {
        String rnName;
        if (rootDomainCandidate.getType() == RegistryNodeType.ROOT_DOMAIN) {
            rnName = rootDomainCandidate.getName();
            String[] firstLevelDomains = rnName.split("\\.");
            if (firstLevelDomains.length != 2) {
                throw new OpenFlameRuntimeException("Wrong format of root domain's name.");
            }
            rnName = firstLevelDomains[1] + "." + firstLevelDomains[0];
        } else {
            rnName = rootDomainCandidate.getName();
        }
        return rnName;
    }

}
