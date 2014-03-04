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
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;

import java.util.List;
import java.util.Map;


public interface IPackageStore extends IRegistryNodeStore<PackageModel> {

    /**
     * @param id
     * @return
     */
    PackageModel findByIdWithUID(Long id);

    /**
     * @param id
     * @return
     */
    PackageModel findWithSystemById(Long id);

    PackageModel findWithSystemByLookup(String packageLookup);

    /**
     * @param id
     * @return
     */
    PackageModel findWithDatabaseById(Long id);

    PackageModel findWithSystemAndDatabaseById(Long id);

    /**
     * Find package by id. If package found and has database associated then this database is initialised.
     * @param packageId id
     * @return package with associated database
     */
    Map<RegistryNodeModel, DatabaseModel> findAllWithDatabaseById(Long packageId);

    /**
     * Find package by specified uid. If package found and has database associated then this database is initialised.
     * @param packageUID uid
     * @return package with associated database
     */
    Map<String, DatabaseModel> findAllWithDatabaseByUID(String packageUID);

    /**
     * @param registryNodeId
     * @return
     */
    PackageModel findWithSystemByChildrenId(Long registryNodeId);

    void save(PackageModel packageRN);

    /**
     * @param packageRN
     * @param version
     */
    void save(PackageModel packageRN, Integer version);

    /**
     * @param packageRN
     * @param version
     * @param databaseVersion
     */
    void save(PackageModel packageRN, Integer version, Integer databaseVersion);

    /**
     *
     * @param packageRN
     * @param version
     * @param databaseVersion
     * @param createAutoDescription
     */
    void save(PackageModel packageRN, Integer version, Integer databaseVersion, boolean createAutoDescription);

    void delete(PackageModel packageRN);

    /**
     * @param id
     * @param filter
     * @return
     */
    List<PackageModel> findAllInstalledPackagesWithFilter(Long id, SpecifiedIdsFilter<Long> filter);

    /**
     * @param packageRN
     * @param system
     */
    void associate(PackageModel packageRN, SystemModel system);

    /**
     * @param packageRN
     * @param system
     */
    void removeAssociation(PackageModel packageRN, SystemModel system);

    /**
     * @param system
     */
    void removeAllAssociations(SystemModel system);

    /**
     * @param packageId
     * @param version
     * @return
     */
    PackageModel updatePackageVersion(Long packageId, Integer version);

	PackageModel findPackage(String lookup);
}