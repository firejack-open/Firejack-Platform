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
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.utils.Paging;

import java.util.List;
import java.util.Map;


public interface IPermissionStore extends IRegistryNodeStore<PermissionModel> {

    /**
     * @param id
     * @return
     */
    PermissionModel findByIdWithRolesAndNavElements(Long id);

    /**
     * @param term
     * @param filter
     * @return
     */
    List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<PermissionModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    List<PermissionModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param roleId
     * @return
     */
    List<PermissionModel> findRolePermissions(Long roleId);

    /**
     * @param roleId
     * @param baseLookup
     * @return
     */
    List<PermissionModel> findRolePermissions(Long roleId, String baseLookup);

    /**
     * @param roles
     * @return
     */
    Map<Long, List<PermissionModel>> findRolePermissions(List<Long> roles);

    /**
     * @param action
     */
    void createPermissionByAction(ActionModel action);

    /**
     * @param navigationElement
     */
    void createPermissionByNavigationElement(NavigationElementModel navigationElement);

    /**
     * @param resourceLocation
     */
    void createPermissionForResourceLocation(ResourceLocationModel resourceLocation);

    /**
     * @param resourceLocationId
     * @return
     */
    List<PermissionModel> findResourceLocationPermissions(Long resourceLocationId);

    String findRegistryNodeRefPath(Long permissionId);

    void save(PermissionModel registryNode);

    /**
     * @param registryNodeId
     * @param parentRegistryNodes
     */
    void updateParent(Long registryNodeId);

    /**
     * @param registryNodeId
     */
    void deleteAllByRegistryNodeId(Long registryNodeId);

}
