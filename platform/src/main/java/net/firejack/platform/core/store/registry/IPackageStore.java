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