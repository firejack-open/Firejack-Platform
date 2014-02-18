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

import net.firejack.platform.core.model.registry.authority.PermissionAssignment;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.BaseStore;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@SuppressWarnings("unused")
@Component("permissionAssignmentStore")
public class PermissionAssignmentStore extends BaseStore<PermissionAssignment, Long> implements IPermissionAssignmentStore {

    private static final String MSG_PACKAGE_ID_SHOULD_NOT_BE_NULL = "packageId should not be null.";
    private static final String MSG_PERMISSION_ASSIGNMENTS_IS_NOT_ACTUAL = "Permission assignments meta data is not actual. Updating meta data...";

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    @Override
    @Transactional
    public Map<RoleModel, Set<PermissionModel>> getPackageRoleAssignedPermissions(Long packageId) {
        if (packageId == null) {
            throw new IllegalArgumentException(MSG_PACKAGE_ID_SHOULD_NOT_BE_NULL);
        }
        PackageModel sourcePackage = getHibernateTemplate().get(PackageModel.class, packageId);
        Map<RoleModel, Set<PermissionModel>> result;
        if (sourcePackage == null) {
            result = null;
        } else {
            Criteria criteria = getSession().createCriteria(PermissionAssignment.class);
            criteria.add(Restrictions.eq("sourcePackage", sourcePackage));
            @SuppressWarnings("unchecked")
            List<PermissionAssignment> packageRoleAssignments = (List<PermissionAssignment>) criteria.list();
            result = new HashMap<RoleModel, Set<PermissionModel>>();
            if (packageRoleAssignments != null && !packageRoleAssignments.isEmpty()) {
                for (PermissionAssignment permissionAssignment : packageRoleAssignments) {
                    Set<PermissionModel> permissions = result.get(permissionAssignment.getRole());
                    if (permissions == null) {
                        permissions = new HashSet<PermissionModel>();
                        result.put(permissionAssignment.getRole(), permissions);
                    }
                    permissions.add(permissionAssignment.getPermission());
                }
                //check if all permission assignments is actual
                Set<PermissionModel> existentRolePermission = new HashSet<PermissionModel>();
                for (Map.Entry<RoleModel, Set<PermissionModel>> entry : result.entrySet()) {
                    RoleModel role = entry.getKey();
                    List<PermissionModel> rolePermissions = permissionStore.findRolePermissions(role.getId());
                    existentRolePermission.addAll(rolePermissions);
                }
                criteria.add(Restrictions.in("permission", existentRolePermission));

                @SuppressWarnings("unchecked")
                List<PermissionAssignment> actualPackageRoleAssignments = (List<PermissionAssignment>) criteria.list();
                if (actualPackageRoleAssignments.size() <  packageRoleAssignments.size()) {
                    logger.info(MSG_PERMISSION_ASSIGNMENTS_IS_NOT_ACTUAL);
                    for (PermissionAssignment assignment : packageRoleAssignments) {
                        if (!actualPackageRoleAssignments.contains(assignment)) {
                            Set<PermissionModel> permissions = result.get(assignment.getRole());
                            permissions.remove(assignment.getPermission());
                            if (permissions.isEmpty()) {
                                result.remove(assignment.getRole());
                            }
                            delete(assignment);
                        }
                    }
                }

            }
            //todo: check if there are no any permissions that not assigned to the role but permission assignment record still exist
        }
        return result;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void saveAssignments(PackageModel sourcePackage, Map<RoleModel, Set<PermissionModel>> rolePermissionsMap) {
        List<PermissionAssignment> assignmentList = new ArrayList<PermissionAssignment>();
        for (Map.Entry<RoleModel, Set<PermissionModel>> entry : rolePermissionsMap.entrySet()) {
            RoleModel role = entry.getKey();
            Set<PermissionModel> permissionSet = entry.getValue();

            //as soon as role assignments are significant for export only if permissions assigned to role that is part of another package then
            //check if the role's lookup does not start from the package's lookup.
            if (!role.getLookup().startsWith(sourcePackage.getLookup() + '.')) {
                Criteria criteria = getSession().createCriteria(PermissionAssignment.class);
                criteria.add(Restrictions.eq("sourcePackage.id", sourcePackage.getId()));
                criteria.add(Restrictions.eq("role.id", role.getId()));
                criteria.createAlias("permission", "perm");
                List<Long> permissionIdList = new ArrayList<Long>();
                if (permissionSet != null) {
                    for (PermissionModel permission : permissionSet) {
                        permissionIdList.add(permission.getId());
                    }
                }
                criteria.add(Restrictions.in("perm.id", permissionIdList));
                List<PermissionAssignment> oldPermissionAssignments =
                        (List<PermissionAssignment>) criteria.list();
                List<PermissionAssignment> assignmentsToDelete = new ArrayList<PermissionAssignment>();
                if (permissionSet == null || permissionSet.isEmpty()) {
                    if (oldPermissionAssignments != null) {
                        assignmentsToDelete.addAll(oldPermissionAssignments);
                    }
                } else {
                    Map<Long, PermissionAssignment> oldIdPermissionMap = new HashMap<Long, PermissionAssignment>();
                    if (oldPermissionAssignments != null) {
                        for (PermissionAssignment assignment : oldPermissionAssignments) {
                            oldIdPermissionMap.put(assignment.getPermission().getId(), assignment);
                        }
                    }
                    List<PermissionAssignment> assignmentsToCreate = new ArrayList<PermissionAssignment>();
                    Set<Long> newIdPermissionIdSet = new HashSet<Long>();
                    for (PermissionModel permission : permissionSet) {
                        if (!oldIdPermissionMap.containsKey(permission.getId())) {
                            PermissionAssignment assignment = new PermissionAssignment();
                            assignment.setRole(role);
                            assignment.setSourcePackage(sourcePackage);
                            assignment.setPermission(permission);
                            assignmentList.add(assignment);
                        }
                        newIdPermissionIdSet.add(permission.getId());
                    }
                    for (Map.Entry<Long, PermissionAssignment> permissionEntry : oldIdPermissionMap.entrySet()) {
                        if (!newIdPermissionIdSet.contains(permissionEntry.getKey())) {
                            assignmentsToDelete.add(permissionEntry.getValue());
                        }
                    }
                }
                if (!assignmentsToDelete.isEmpty()) {
                    deleteAll(assignmentsToDelete);
                }
            }
        }
        if (!assignmentList.isEmpty()) {
            saveOrUpdateAll(assignmentList);
        }
    }

}
