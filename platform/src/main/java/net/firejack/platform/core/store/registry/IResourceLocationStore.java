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
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.utils.Paging;

import java.util.List;
import java.util.Map;

public interface IResourceLocationStore extends IRegistryNodeStore<ResourceLocationModel> {

    ResourceLocationModel findById(Long id);

    /**
     * @param term
     * @param filter
     * @return
     */
    List<ResourceLocationModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<ResourceLocationModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    List<ResourceLocationModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    List<ResourceLocationModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param resourceLocation
     */
    void saveWithPermission(ResourceLocationModel resourceLocation);

    /**
     * @return
     */
    Map<ResourceLocationModel, List<RoleModel>> findRolesByResourceLocation();

    /**
     * @return
     */
    List<ResourceLocationModel> findAllWithPermissions();

    /**
     * @param packageLookup
     * @return
     */
    List<ResourceLocationModel> findAllWithPermissions(String packageLookup);

    /**
     * @param resourceLocation
     */
    void saveForGenerator(ResourceLocationModel resourceLocation);

    void delete(ResourceLocationModel resourceLocation);

}