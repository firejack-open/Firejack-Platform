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

package net.firejack.platform.core.store.user;

import net.firejack.platform.api.authority.domain.ContextPermissions;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.*;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.store.registry.AuthorizationVOFactory;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;


@Component("userRoleStore")
public class BasicUserRoleStore extends BaseStore<UserRoleModel, UserRolePk> implements IUserRoleStore {

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(UserRoleModel.class);
    }

    @Transactional(readOnly = true)
    public List<UserRoleModel> findAllByUserId(Long userId) {
        return find(null, null, "UserRole.findAllByUserId", "userId", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleModel> findAllWithPermissionsByUserId(Long userId) {
        return find(null, null, "UserRole.findAllWithPermissionsByUserId", "userId", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleModel> findAllWithPermissionsByUserIdAndBaseLookup(Long userId, String baseLookup) {
        return find(null, null, "UserRole.findAllWithPermissionsByUserIdAndBaseLookup",
                "userId", userId, "lookupPattern", baseLookup + ".%");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleModel> findGlobalRoles(Long userId) {
        return find(null, null, "UserRole.findGlobalRolesByUserId",
                "userId", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleModel findContextRole(Long userId, Long roleId, Long objectId, String objectType) {
        return findSingle("UserRole.findContextRolesByUserIdAndRoleIdAndRegistryNodeId",
                "userId", userId, "roleId", roleId, "objectId", objectId, "objectType", objectType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleModel> findContextRolesByUserIdAndRegistryNodeId(Long userId, Long objectId, String objectType) {
        return find(null, null, "UserRole.findContextRolesByUserIdAndRegistryNodeId",
                "userId", userId, "objectId", objectId, "objectType", objectType);
    }

    @Override
    @Transactional
    public void delete(UserRoleModel userRole) {
        super.delete(userRole);
        deleteUserRoleInCache(userRole);
    }

    @Override
    @Transactional
    public void saveOrUpdate(UserRoleModel userRole) {
        boolean isNew = userRole.getId() == null;
        super.saveOrUpdate(userRole);
        if (isNew) {
            addUserRoleInCache(userRole);
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<UserRoleModel> userRoleModels) {
        super.deleteAll(userRoleModels);
        deleteUserRolesInCache(userRoleModels);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SecuredRecordModel, List<UserRoleModel>> findAllContextRolesBySecuredRecords() {
        final UserRoleModel example = this.instantiate();
        @SuppressWarnings("unchecked")
        List<UserRoleModel> securedRecordUserRoles = getHibernateTemplate().execute(new HibernateCallback<List<UserRoleModel>>() {
            @Override
            public List<UserRoleModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.setFetchMode("role", FetchMode.JOIN);
                criteria.add(Restrictions.isNotNull("securedRecord"));
                return (List<UserRoleModel>) criteria.list();
            }
        });
        Map<SecuredRecordModel, List<UserRoleModel>> result = new HashMap<SecuredRecordModel, List<UserRoleModel>>();
        if (securedRecordUserRoles != null) {
            for (UserRoleModel securedRecordUserRole : securedRecordUserRoles) {
                SecuredRecordModel securedRecord = securedRecordUserRole.getSecuredRecord();

                List<UserRoleModel> contextRoles = result.get(securedRecord);
                if (contextRoles == null) {
                    contextRoles = new ArrayList<UserRoleModel>();
                    result.put(securedRecord, contextRoles);
                }
                contextRoles.add(securedRecordUserRole);

                Hibernate.initialize(securedRecordUserRole.getSecuredRecord());
                Hibernate.initialize(securedRecordUserRole.getRole().getPermissions());
                for (PermissionModel permission : securedRecordUserRole.getRole().getPermissions()) {
                    Hibernate.initialize(permission);
                }
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<UserRoleModel> findAllContextRolesNotBoundToSecuredRecord() {
        final UserRoleModel example = this.instantiate();
        List<UserRoleModel> roleList = getHibernateTemplate().execute(new HibernateCallback<List<UserRoleModel>>() {
            @Override
            public List<UserRoleModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(example.getClass());
                criteria.setFetchMode("role", FetchMode.JOIN);
                criteria.add(Restrictions.isNull("securedRecord"));
                return (List<UserRoleModel>) criteria.list();
            }
        });
        List<UserRoleModel> result = new ArrayList<UserRoleModel>();
        for (UserRoleModel userRole : roleList) {
            if (userRole.getRole().getParent().getType() == RegistryNodeType.ENTITY) {
                result.add(userRole);
            }
        }
        for (UserRoleModel userRole : result) {
            List<PermissionModel> permissions = userRole.getRole().getPermissions();
            Hibernate.initialize(permissions);
            for (PermissionModel permission : permissions) {
                Hibernate.initialize(permission);
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Map<String, List<UserRoleModel>> findAllPackageLevelUserRoles() {
        Criteria pkgCriteria = getSession().createCriteria(PackageModel.class);
        List<PackageModel> packages = pkgCriteria.list();
        Map<String, List<UserRoleModel>> result = new HashMap<String, List<UserRoleModel>>();
        if (packages != null) {
            for (PackageModel pkg : packages) {
                Criteria criteria = getSession().createCriteria(UserRoleModel.class);
                criteria.add(Restrictions.eq("type", RegistryNodeType.PACKAGE.getEntityPath()));
                criteria.add(Restrictions.eq("internalId", pkg.getId()));
                List<UserRoleModel> userRoles = criteria.list();
                result.put(pkg.getLookup(), userRoles);
            }
        }
        for (List<UserRoleModel> packageUserRoles : result.values()) {
            for (UserRoleModel userRole : packageUserRoles) {
                List<PermissionModel> permissions = userRole.getRole().getPermissions();
                Hibernate.initialize(permissions);
                for (PermissionModel permission : permissions) {
                    Hibernate.initialize(permission);
                }
            }
        }
        return result;
    }

    private void deleteUserRoleInCache(UserRoleModel userRole) {
        if (userRole != null) {
            Long userId = userRole.getUser().getId();
            Long roleId = userRole.getRole().getId();
            cacheProcessor.deleteUserRole(userId, roleId);
            //todo: temp package level security
            if (RegistryNodeType.PACKAGE.getEntityPath().equals(userRole.getType()) &&
                    userRole.getInternalId() != null) {
                PackageModel pkg = packageStore.findById(userRole.getInternalId());
                if (pkg != null) {
                    cacheProcessor.deletePackageLevelPermissions(pkg.getLookup(), userId, roleId);
                }
            }
        }
    }

    private void deleteUserRolesInCache(Collection<UserRoleModel> userRoles) {
        if (userRoles != null && !userRoles.isEmpty()) {
            Map<Long, Collection<ContextPermissions>> userRoleEntries = new HashMap<Long, Collection<ContextPermissions>>();
            for (UserRoleModel userRole : userRoles) {
                Long userId = userRole.getUser().getId();
                Collection<ContextPermissions> roles = userRoleEntries.get(userId);
                if (roles == null) {
                    roles = new LinkedList<ContextPermissions>();
                }
                String entityId;
                if (userRole.getInternalId() == null) {
                    entityId = userRole.getExternalId();
                } else {
                    entityId = String.valueOf(userRole.getInternalId());
                }
                roles.add(new ContextPermissions(userRole.getRole().getId(), userRole.getType(), entityId));
            }
            cacheProcessor.deleteContextUserRoles(userRoleEntries);
        }
    }

    private void addUserRoleInCache(UserRoleModel userRole) {
        if (ConfigContainer.isAppInstalled()) {
            if (userRole.getSecuredRecord() == null) {
                Long userId = userRole.getUser().getId();
                if (userRole.getType() == null || userRole.getInternalId() == null) { //usual permissions
                    cacheProcessor.addUserRole(userId, userRole.getRole().getId());
                } else if (userRole.getType() != null && userRole.getInternalId() != null) {// contextual permissions for user roles without assigned secured record
                    RoleModel role = userRole.getRole();
                    cacheProcessor.addUserContextPermissions(userId, role.getId(), userRole.getType(),
                            String.valueOf(userRole.getInternalId()), AuthorizationVOFactory.retrieveLookupList(role.getPermissions()));
                } else {
                    logger.error("Failed to determine user role type.");
                }
            } else if (userRole.getType() != null && userRole.getInternalId() != null) {// contextual permissions that have assigned secured record
                RoleModel role = userRole.getRole();
                Long userId = userRole.getUser().getId();
                List<String> permissionLookupList = AuthorizationVOFactory.retrieveLookupList(role.getPermissions());
                cacheProcessor.addUserContextPermissions(userId, userRole.getSecuredRecord().getId(),
                        role.getId(), userRole.getType(), String.valueOf(userRole.getInternalId()),
                        permissionLookupList);
                //todo: temp package level security
                if (userRole.getType().equals(RegistryNodeType.PACKAGE.getEntityPath())) {
                    PackageModel pkg = packageStore.findById(userRole.getInternalId());
                    if (pkg != null) {
                        cacheProcessor.updatePackageLevelPermissions(pkg.getLookup(), userId, role.getId(), permissionLookupList);
                    }
                }
            } else {
                logger.error("Failed to determine user role type.");
            }
        }
    }

}
