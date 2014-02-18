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

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import net.firejack.platform.web.security.model.principal.GuestUser;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;
import java.lang.annotation.Annotation;
import java.util.*;


@SuppressWarnings("unused")
@Component("roleStore")
public class RoleStore extends RegistryNodeStore<RoleModel> implements IRoleStore {

	@Autowired
	private IPackageStore packageStore;
    @Autowired
    @Qualifier("permissionAssignmentStore")
    private IPermissionAssignmentStore permissionAssignmentStore;
    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    /***/
    @PostConstruct
    public void init() {
        setClazz(RoleModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleModel findById(Long id) {
        return findSingle("Role.findByIdWithPermissions", "id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleModel findByLookup(String lookup) {
        return findSingle("Role.findByLookupWithPermissions", "lookup", lookup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findByName(String roleName) {
        return find(null, null, "Role.findByName", "name", roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findAll() {
        List<RoleModel> roleList = super.findAll();
        for (RoleModel role : roleList) {
            if (role.getParent() != null) {
                Hibernate.initialize(role.getParent());
            }
        }
        return roleList;
    }

	@Override
	@Transactional(readOnly = true)
	public List<RoleModel> findAllByRegistryNodeIdWithFilter(Long registryNodeId, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		Map<String, String> alias = new HashMap<String, String>();
		if (isGlobalOnly) {
//			Criterion globalRoleCriterion = Restrictions.in("parent.class", new String[]{"DOM", "PKG"});
			Criterion globalRoleCriterion = Restrictions.in("parent.class", new String[]{"PKG"});
			alias.put("parent", "parent");
			criterions.add(globalRoleCriterion);
		}
		if (registryNodeId != null && registryNodeId != 0) {
			Criterion registryNodeIdCriterion = Restrictions.eq("parent.id", registryNodeId);
			criterions.add(registryNodeIdCriterion);
		}
		return findAllWithFilter(criterions, alias, filter);
	}

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly) {
        List<Criterion> criterions = new ArrayList<Criterion>();
	    Map<String, String> alias = new HashMap<String, String>();
        if (isGlobalOnly) {
	        Criterion globalRoleCriterion = Restrictions.in("parent.class", new String[]{"DOM", "PKG"});
	        alias.put("parent", "parent");
            criterions.add(globalRoleCriterion);
        }
        Criterion nameCriterion = Restrictions.like("lookup", StringUtils.isBlank(term) ? "%" : "%" + term + "%");
        criterions.add(nameCriterion);
	    List<RoleModel> roles = findAllWithFilter(criterions, alias, filter);
        removePermissions(roles);
        return roles;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly) {
        List<Criterion> criterions = new ArrayList<Criterion>();
	    Map<String, String> alias = new HashMap<String, String>();
        if (isGlobalOnly) {
	        Criterion globalRoleCriterion = Restrictions.in("parent.class", new String[]{"DOM", "PKG"});
	        alias.put("parent", "parent");
            criterions.add(globalRoleCriterion);
        }
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", StringUtils.isBlank(term) ? "%" : "%" + term + "%");
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
	    List<RoleModel> roles = findAllWithFilter(criterions, alias, filter);
        removePermissions(roles);
        return roles;
    }

    @Override
    public List<RoleModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
        Criterion nameCriterion = Restrictions.like("lookup", '%' + term + '%');
        LogicalExpression expressionAll = Restrictions.and(registryNodeIdCriterion, nameCriterion);
        criterions.add(expressionAll);
        return findAllWithFilter(criterions, filter, paging);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<RoleModel> findAllByParentIdsWithFilter(List<Long> ids, SpecifiedIdsFilter<Long> filter) {
//        List<RoleModel> roles = super.findAllByParentIdsWithFilter(ids, filter, null);
//        removePermissions(roles);
//        return roles;
//    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findAllByRegistryNodeIdsAndTypeWithFilter(List<Long> registryNodeIds, Class registryNodeClass, SpecifiedIdsFilter<Long> filter) {
        Annotation annotation = registryNodeClass.getAnnotation(DiscriminatorValue.class);
        if (annotation != null) {
            List<Criterion> criterions = new ArrayList<Criterion>();
	        Map<String, String> alias = new HashMap<String, String>();
            Criterion registryNodeIdCriterion = Restrictions.in("parent.id", registryNodeIds);
            criterions.add(registryNodeIdCriterion);
	        Criterion globalRoleCriterion = Restrictions.in("parent.class", new String[]{"DOM", "PKG"});
	        alias.put("parent", "parent");
            criterions.add(globalRoleCriterion);
	        return findAllWithFilter(criterions, alias, filter, Order.asc("path"));
        }
        return new ArrayList<RoleModel>();
    }

    @Override
    public List<RoleModel> findAllByRegistryNodeTypeWithFilter(Class registryNodeClass, SpecifiedIdsFilter<Long> filter) {
        Annotation annotation = registryNodeClass.getAnnotation(DiscriminatorValue.class);
        if (annotation != null) {
            DiscriminatorValue discriminatorValue = (DiscriminatorValue) annotation;
            List<Criterion> criterions = new ArrayList<Criterion>();
	        Map<String, String> alias = new HashMap<String, String>();
	        Criterion criterionByRegistryNodeType = Restrictions.eq("parent.class", discriminatorValue.value());
	        alias.put("parent", "parent");
            criterions.add(criterionByRegistryNodeType);
            return findAllWithFilter(criterions, alias,filter, Order.asc("path"));
        }
        return new ArrayList<RoleModel>();
    }

    @Override
    @Transactional
    public String findRegistryNodeRefPath(Long roleId) {
        StringBuilder sb = new StringBuilder();
        RoleModel registryNode = getHibernateTemplate().get(RoleModel.class, roleId);
        if (registryNode != null && registryNode.getParent() != null) {
            RegistryNodeModel rn = getHibernateTemplate().get(
                    RegistryNodeModel.class, registryNode.getParent().getId());
            if (rn != null) {
                sb.append(rn.getName());
                while (rn.getParent() != null) {
                    rn = getHibernateTemplate().get(RegistryNodeModel.class, rn.getParent().getId());
                    if (rn != null) {
                        sb.insert(0, rn.getName()).insert(rn.getName().length(), ".");
                    } else {
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public void saveForGenerator(RoleModel role) {
        boolean isNew = role.getId() == null;
        super.saveForGenerator(role);
        if (isNew) {
            cacheProcessor.addPackageRole(role.getId(), new LinkedList<UserPermission>(), GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
        }
    }

    @Override
    @Transactional
    public RoleModel saveRolePermissions(Long roleId, List<PermissionModel> permissionsList) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId parameter should not be null.");
        }
        if (permissionsList != null && !permissionsList.isEmpty()) {
            RoleModel role = getHibernateTemplate().get(getClazz(), roleId);
            if (role != null) {
                role.setPermissions(permissionsList);
                saveOrUpdate(role);
                cacheProcessor.updateRolePermissions(role.getId(),
                        AuthorizationVOFactory.retrieveLookupList(role.getPermissions()),
                        GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
                return role;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void addRolePermissions(Long sourcePackageId, Map<Long, Set<PermissionModel>> rolePermissionsMap) {
        if (sourcePackageId == null) {
            throw new IllegalArgumentException("sourcePackageId parameter should not be null.");
        }
        if (rolePermissionsMap != null && !rolePermissionsMap.isEmpty()) {
            PackageModel sourcePackage = getHibernateTemplate().get(PackageModel.class, sourcePackageId);
            if (sourcePackage == null) {
                throw new IllegalArgumentException("Source package should not be null.");
            }

            Map<RoleModel, Set<PermissionModel>> assignments = new HashMap<RoleModel, Set<PermissionModel>>();
            for (Map.Entry<Long, Set<PermissionModel>> rolePermissions : rolePermissionsMap.entrySet()) {
                Long roleId = rolePermissions.getKey();
                Set<PermissionModel> permissionSet = rolePermissions.getValue();

                RoleModel role = findById(roleId);
                if (role != null) {
                    Set<PermissionModel> assignedPermissions = new HashSet<PermissionModel>();
                    if (role.getPermissions() != null) {
                        assignedPermissions.addAll(role.getPermissions());
                    }
                    assignedPermissions.addAll(permissionSet);
                    List<PermissionModel> resultPermissions = new ArrayList<PermissionModel>(assignedPermissions);
                    role.setPermissions(resultPermissions);
                    saveOrUpdate(role);

                    assignments.put(role, permissionSet);

                    cacheProcessor.updateRolePermissions(role.getId(),
                            AuthorizationVOFactory.retrieveLookupList(role.getPermissions()),
                            GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
                }
            }
            if (!assignments.isEmpty()) {
                permissionAssignmentStore.saveAssignments(sourcePackage, assignments);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findRolesWithPermissionsByUserId(Long userId) {
        return find(null, null, "Role.findRolesWithPermissionsByUserId", "userId", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findRolesByUserId(Long userId) {
        return find(null, null, "Role.findRolesByUserId", "userId", userId);
    }

    @Override
    @Transactional
    public void save(RoleModel role) {
        saveWithoutUpdateChildren(role);
    }

    @Override
    @Transactional
    public RoleModel mergeForGenerator(RoleModel role) {
        RoleModel oldRole = findByUID(role.getUid().getUid());
        if (oldRole != null) {
            oldRole.setName(role.getName());
            oldRole.setLookup(DiffUtils.lookup(oldRole.getPath(), role.getName()));
            oldRole.setDescription(role.getDescription());
            role = oldRole;
        }
        saveWithoutUpdateChildren(role);
        return role;
    }

    @Override
    @Transactional
    public void saveWithoutUpdateChildren(RoleModel role) {
        boolean isNew = role.getId() == null;
        super.saveWithoutUpdateChildren(role);
        //if (ConsoleAppModel.isAppInstalled() && role.getPermissions() != null) {
        if (isNew) {
            LookupModel parent = getHibernateTemplate().load(RegistryNodeModel.class, role.getParent().getId());
            if (parent.getType().equals(RegistryNodeType.PACKAGE)) {
                cacheProcessor.addPackageRole(
                        role.getId(),
                        role.getPermissions() == null ? null : AuthorizationVOFactory.convertPermissions(role.getPermissions()),
                        GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
            } //else do nothing, as soon as context role permissions will be handled when assigned to user
        } else if (role.getPermissions() != null) {
            cacheProcessor.updateRolePermissions(role.getId(),
                    AuthorizationVOFactory.retrieveLookupList(role.getPermissions()),
                    GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
        }
    }

    @Override
    @Transactional
    public void deleteAllByRegistryNodeId(Long registryNodeId) {
        List<RoleModel> roles = findAllByParentIdWithFilter(registryNodeId, null);
        super.deleteAll(roles);
        for (RoleModel role : roles) {
            cacheProcessor.deleteRole(role.getId());
        }
    }

    @Override
    @Transactional
    public void delete(RoleModel role) {
        super.delete(role);
        cacheProcessor.deleteRole(role.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RoleModel> findContextRoles(List<Long> exceptIds) {
        Criteria criteria = getSession().createCriteria(RoleModel.class);
        if (exceptIds != null && !exceptIds.isEmpty()) {
            criteria.add(Restrictions.not(Restrictions.in("id", exceptIds)));
        }
        criteria.createAlias("parent", "parent");
        criteria.add(Restrictions.ne("parent.class", "PKG"));
        return (List<RoleModel>) criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RoleModel> findContextRolesByLookupList(List<String> lookupList) {
        List<RoleModel> contextRoles;
        if (lookupList == null || lookupList.isEmpty()) {
            contextRoles = new ArrayList<RoleModel>();
        } else {
            Criteria criteria = getSession().createCriteria(RoleModel.class);
            criteria.add(Restrictions.in("lookup", lookupList));
            criteria.createAlias("parent", "parent");
            criteria.add(Restrictions.ne("parent.class", "PKG"));
            contextRoles = (List<RoleModel>) criteria.list();
        }
        return contextRoles;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RoleModel> findContextRolesByLookupListWithPermissions(List<String> lookupList) {
        List<RoleModel> contextRoles;
        if (lookupList == null || lookupList.isEmpty()) {
            contextRoles = new ArrayList<RoleModel>();
        } else {
            Criteria criteria = getSession().createCriteria(RoleModel.class);
            criteria.add(Restrictions.in("lookup", lookupList));
            criteria.createAlias("parent", "parent");
            criteria.add(Restrictions.ne("parent.class", "PKG"));
            contextRoles = (List<RoleModel>) criteria.list();
            if (!contextRoles.isEmpty()) {
                for (RoleModel roleModel : contextRoles) {
                    Hibernate.initialize(roleModel.getPermissions());
                }
            }
        }
        return contextRoles;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RoleModel> findEntityAssociatedContextRoles(Long entityId) {
        EntityModel entityModel = getHibernateTemplate().get(EntityModel.class, entityId);
        Hibernate.initialize(entityModel.getContextRoles());
        List<RoleModel> allowableContextRoles = new ArrayList<RoleModel>();
        if (entityModel.getContextRoles() != null) {
            for (RoleModel roleModel : entityModel.getContextRoles()) {
                Hibernate.initialize(roleModel);
                allowableContextRoles.add(roleModel);
            }
        }
        return allowableContextRoles;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Map<RoleModel, Boolean> findAllAssignedRolesByUserId(Long userId) {
        Map<RoleModel, Boolean> result;
        if (userId == null) {
            result = null;
        } else {
            Criteria criteria = getSession().createCriteria(UserRoleModel.class);
            criteria.setFetchMode("role", FetchMode.JOIN);
            criteria.add(Restrictions.eq("user.id", userId));
            List<UserRoleModel> userRoles = (List<UserRoleModel>) criteria.list();
            result = new HashMap<RoleModel, Boolean>();
            if (userRoles != null) {
                for (UserRoleModel userRole : userRoles) {
                    boolean isGlobal = userRole.getInternalId() == null && userRole.getType() == null;
                    result.put(userRole.getRole(), isGlobal);
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<RoleModel> findAll(Collection<Long> exceptIds) {
        Criteria criteria = getSession().createCriteria(this.getClazz());
        if (exceptIds != null && !exceptIds.isEmpty()) {
            criteria.add(Restrictions.in("id", exceptIds));
        }
        return (List<RoleModel>) criteria.list();
    }

    private void removePermissions(List<RoleModel> roles) {
        for (RoleModel role : roles) {
            role.setPermissions(null);
        }
    }

	@Override
	@Transactional
	public void addPermissionsToCurrentPackageRoles(String lookup, Long userId, List<PermissionModel> permissions) {
		PackageModel packageModel = packageStore.findPackage(lookup);

		String admin = DiffUtils.lookup(packageModel.getLookup(), "admin");
		RoleModel adminRole = findByLookup(admin);

		List<RoleModel> roles = find(null, null, "Role.findRolesByUserIdAndPackage", "parentId", packageModel.getId(), "userId", userId);

		if (adminRole !=null && !roles.contains(adminRole)) {
			roles.add(adminRole);
		}

		for (RoleModel role : roles) {
			List<PermissionModel> permissionModels = role.getPermissions();
			if (permissionModels == null) {
				permissionModels = new ArrayList<PermissionModel>();
				role.setPermissions(permissionModels);
			}
			permissionModels.addAll(permissions);

			cacheProcessor.updateRolePermissions(role.getId(),
					AuthorizationVOFactory.retrieveLookupList(permissionModels),
					GuestUser.GUEST_ROLE_NAME.equals(role.getName()));
		}
	}

    @Override
    @Transactional(readOnly = true)
    public List<RoleModel> findByGroups(Collection<Long> groupIdList) {
        List<RoleModel> result;
        if (groupIdList == null || groupIdList.isEmpty()) {
            result = Collections.emptyList();
        } else {
            result = find(null, null, "Role.findAllMappedToGroups", "groupIdList", groupIdList);
        }
        return result;
    }

}
