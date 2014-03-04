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

import net.firejack.platform.core.model.registry.resource.AbstractResourceModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.store.IStore;

import java.util.List;


public interface IResourceVersionStore<RV extends AbstractResourceVersionModel> extends IStore<RV, Long> {

    /**
     * @param id
     * @param culture
     * @param version
     * @return
     */
    RV findByResourceIdCultureAndVersion(Long id, Cultures culture, Integer version);

    /**
     * @param id
     * @param culture
     * @return
     */
    RV findLastVersionByResourceIdCulture(Long id, Cultures culture);

    /**
     * @param id
     * @return
     */
    RV findLastVersionByResourceId(Long id);

    /**
     * @param resourceLookup
     * @return
     */
    RV findLastVersionByLookup(String resourceLookup);

    /**
     * @param resourceId
     * @return
     */
    List<RV> findLastVersionsByResourceId(Long resourceId);

    /**
     * @param resourceId
     * @param version
     * @return
     */
    List<RV> findByResourceIdAndVersion(Long resourceId, Integer version);

    /**
     * @param resourceIds
     * @return
     */
    List<Cultures> findCulturesForLastVersionByResourceIds(final List<Long> resourceIds);

    /**
     * @param resourceId
     * @param version
     * @param culture
     * @return
     */
    RV createNewResourceVersion(long resourceId, int version, Cultures culture);

    /**
     * @param abstractResource
     * @param resourceVersions
     * @return
     */
    List<RV> createNewResourceVersions(AbstractResourceModel abstractResource, List<RV> resourceVersions);

    /**
     * @param resourceId
     * @return
     */
    List<RV> findAllResourceVersions(Long resourceId);

    /**
     * @param resourceId
     */
    void deleteAllByResourceId(Long resourceId);

    Integer deleteResourceVersion(RV entity);

	List<RV> readResourcesByLookupList(List<String> lookup);
}
