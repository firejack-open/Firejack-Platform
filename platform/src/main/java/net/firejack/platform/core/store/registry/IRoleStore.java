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
