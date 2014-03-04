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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;

import java.util.List;


public interface IResourceStore<R extends AbstractResourceModel> extends IRegistryNodeStore<R> {

    /**
     * @param collectionId
     * @return
     */
    List<Cultures> findAvailableCulturesByCollectionId(Long collectionId);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<R> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    /**
     * @param term
     * @param filter
     * @return
     */
    List<R> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    List<R> findAllByLikeLookupPrefix(String lookup);

    void save(R resource);

    List<AbstractResourceVersionModel<R>> save(R resource, List<AbstractResourceVersionModel<R>> resourceVersions);

    void delete(R registryNode);

    void mergeForGenerator(R resource, List<AbstractResourceVersionModel<R>> resourceVersionList);

    Integer setMaxResourceVersion(R resource);

}
