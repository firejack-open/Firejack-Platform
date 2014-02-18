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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.utils.Paging;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface IRoleStore extends IRegistryNodeStore<RoleModel> {

    RoleModel findById(Long id);

    /**
     * @param roleName
     * @return
     */
    List<RoleModel> findByName(String roleName);

    /**
     * @param registryNodeId
     * @param filter
     * @param isGlobalOnly
     * @return
     */
    List<RoleModel> findAllByRegistryNodeIdWithFilter(Long registryNodeId, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly);

    /**
     * @param term
     * @param filter
     * @param isGlobalOnly
     * @return
     */
    List<RoleModel> findAllBySearchTermWithFilter(
            String term, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @param isGlobalOnly
     * @return
     */
    List<RoleModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, boolean isGlobalOnly);

    List<RoleModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param registryNodeIds
     * @param registryNodeClass
     * @param filter
     * @return
     */
    List<RoleModel> findAllByRegistryNodeIdsAndTypeWithFilter(List<Long> registryNodeIds, Class registryNodeClass, SpecifiedIdsFilter<Long> filter);

    /**
     * @param type
     * @param filter
     * @return
     */
    List<RoleModel> findAllByRegistryNodeTypeWithFilter(Class type, SpecifiedIdsFilter<Long> filter);

    /**
     * @param roleId
     * @param permissionsLookupList
     * @return
     */
    RoleModel saveRolePermissions(Long roleId, List<PermissionModel> permissionsLookupList);

    /**
     * @param sourcePackageId
     * @param rolePermissionsList
     */
    void addRolePermissions(Long sourcePackageId, Map<Long, Set<PermissionModel>> rolePermissionsList);

    /**
     * @param userId
     * @return
     */
    List<RoleModel> findRolesWithPermissionsByUserId(Long userId);

    /**
     * @param userId
     * @return
     */
    List<RoleModel> findRolesByUserId(Long userId);

    String findRegistryNodeRefPath(Long permissionId);

    List<RoleModel> findAll();

    void save(RoleModel role);

    /**
     * @param role
     * @return
     */
    RoleModel mergeForGenerator(RoleModel role);

    /**
     * @param registryNodeId
     */
    void deleteAllByRegistryNodeId(Long registryNodeId);

    List<RoleModel> findContextRoles(List<Long> exceptIds);

    List<RoleModel> findContextRolesByLookupList(List<String> lookupList);

    List<RoleModel> findContextRolesByLookupListWithPermissions(List<String> lookupList);

    Map<RoleModel, Boolean> findAllAssignedRolesByUserId(Long userId);

    List<RoleModel> findEntityAssociatedContextRoles(Long entityId);

    List<RoleModel> findAll(Collection<Long> exceptIds);

	void addPermissionsToCurrentPackageRoles(String lookup, Long userId, List<PermissionModel> permissions);

    List<RoleModel> findByGroups(Collection<Long> groupIdList);

}
