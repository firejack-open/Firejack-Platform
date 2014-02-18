/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.registry;

import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.validation.annotation.DomainType;

import javax.ws.rs.PathParam;
import java.io.InputStream;

public interface IRegistryService {

	/**
	 * Load sub nodes
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return children nodes
	 */
	ServiceResponse<RegistryNodeTree> getRegistryNodeChildren(Long registryNodeId, PageType pageType, String packageLookup);

	/**
	 * Load sub tree
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return all sab tree
	 */
	ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByIdChildren(Long registryNodeId, PageType pageType);

    /**
	 * Load sub tree
	 *
	 * @param registryNodeLookup parent node lookup
	 * @param pageType       page type
	 *
	 * @return all sab tree
	 */
	ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByLookupChildren(String registryNodeLookup, PageType pageType);

    /**
     * Get all Domains, Entities and Fields for package lookup
     *
     * @param entityId parent node id
     *
     * @return all tree nodes
     */
	ServiceResponse<RegistryNodeTree> getRegistryNodeFieldsByEntity(Long entityId);

	/**
	 * Move nodes in tree
	 *
	 * @param moveRegistryNodeTree request with move data
	 *
	 * @return new position in tree
	 */
	ServiceResponse<MoveRegistryNodeTree> moveRegistryNode(MoveRegistryNodeTree moveRegistryNodeTree);

	/**
	 * Search  tree nodes element
	 *
	 * @param term           it is a search term
	 * @param registryNodeId sub tree root id
	 * @param lookup         prefix lookup
	 * @param assetType      type nodes
	 * @param paging         paging
	 *
	 * @return founded nodes
	 */
	ServiceResponse<Search> getSearchResult(String term, Long registryNodeId, String lookup, String assetType, Paging paging);

    /**
	 * Retrieves directories from server by provided path
	 *
	 * @param path it is a directory path
	 * @param directoryOnly is directory only
	 *
	 * @return founded nodes
	 */
    ServiceResponse<FileTree> readDirectory(String path, Boolean directoryOnly);

	/**
	 * Read action by id
	 *
	 * @param actionId action Id
	 *
	 * @return found action
	 */
	ServiceResponse<Action> readAction(Long actionId);

	/**
	 * Read all actions
	 *
	 * @return found actions
	 */
	ServiceResponse<Action> readAllActions();

	/**
	 * Found action by lookup
	 *
	 * @param packageLookup action lookup
	 *
	 * @return found action
	 */
	ServiceResponse<Action> readActionsFromCache(String packageLookup);

	/**
	 * Create new action
	 *
	 * @param action Action data
	 *
	 * @return created action
	 */
	ServiceResponse<RegistryNodeTree> createAction(Action action);


	/**
	 * Update Action by id
	 *
	 * @param actionId action id
	 * @param action   action data
	 *
	 * @return updated action
	 */
	ServiceResponse<RegistryNodeTree> updateAction(Long actionId, Action action);

	/**
	 * Delete Action by Id
	 *
	 * @param actionId action d
	 *
	 * @return deleted action
	 */
	ServiceResponse<Action> deleteAction(Long actionId);

	/**
	 * Associate package on system
	 *
	 * @param systemId  system id
	 * @param packageId package id
	 *
	 * @return complete result
	 */
	ServiceResponse associatePackage(Long systemId, Long packageId);

	/**
	 * Remove associate package with system
	 *
	 * @param systemId  system id
	 * @param packageId package id
	 *
	 * @return complete result
	 */
	ServiceResponse removeAssociationPackage(Long systemId, Long packageId);

	/**
	 * Read domain by id
	 *
	 * @param domainId domain id
	 *
	 * @return founded domain
	 */
	ServiceResponse<Domain> readDomain(Long domainId);

	/**
	 * Read all domains
	 *
	 * @return founded domains
	 */
	ServiceResponse<Domain> readAllDomains();

	/**
	 * Create new  domain
	 *
	 * @param data domain data
	 *
	 * @return created domain
	 */
	ServiceResponse<RegistryNodeTree> createDomain(Domain data);

	ServiceResponse<Domain> createDomainByParentLookup(Domain data);


    ServiceResponse<Domain> createDomainDatabase(Domain data, Boolean reverseEngineer);

	/**
	 * Updated domain by id
	 *
	 * @param domainId domain id
	 * @param data     domain data
	 *
	 * @return updated domain
	 */
	ServiceResponse<RegistryNodeTree> updateDomain(Long domainId, Domain data);

	/**
	 * Delete domain by id
	 *
	 * @param domainId domain id
	 *
	 * @return deleted domain
	 */
	ServiceResponse<Domain> deleteDomain(Long domainId);

    ServiceResponse<Domain> readDomainsByPackageLookup(String lookup);

	/**
	 * Read entity By Id
	 *
	 * @param entityId entity id
	 *
	 * @return founded entity
	 */
	ServiceResponse<Entity> readEntity(Long entityId);

	/**
	 * Read entity By Lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return entity with specified lookup if found, otherwise returns null.
	 */
	ServiceResponse<Entity> readEntityByLookup(String entityLookup);

    ServiceResponse<Entity> searchEntityByDomain(String terms, Long domainId, String packageLookup);

	/**
	 * Read possible parent entity types for entity type that has specified lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return parent entity types if found, otherwise returns null.
	 */
	ServiceResponse<Entity> readParentEntityTypesByEntityLookup(String entityLookup);

    ServiceResponse<Entity> readDirectChildrenTypes(String entityLookup);

	/**
	 * Read all entity types extended from entity type that has specified lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return entity with specified lookup if found, otherwise returns null.
	 */
	ServiceResponse<Entity> readEntitiesUpInHierarchyByLookup(String entityLookup);

	/**
	 * Read all entities by type and super entity
	 *
	 * @param type     entity type
	 * @param exceptId extended entity id
	 *
	 * @return founded  entities
	 */
	ServiceResponse<Entity> readAllEntities(String type, Long exceptId);

    ServiceResponse<Entity> readEntitiesByPackageLookup(String lookup);

	/**
	 * Create new entity
	 *
	 * @param data entity data
	 *
	 * @return created entity
	 */
	ServiceResponse<RegistryNodeTree> createEntity(Entity data);

	/**
	 * Updated entity by id
	 *
	 * @param entityId entity id
	 * @param data     entity data
	 *
	 * @return updated entity
	 */
	ServiceResponse<RegistryNodeTree> updateEntity(Long entityId, Entity data);

	/**
	 * Delete entity by id
	 *
	 * @param entityId entity id
	 *
	 * @return deleted entity
	 */
	ServiceResponse<Entity> deleteEntity(Long entityId);

    ServiceResponse<Form> createForm(Form data);

    /**
     * Read all entities by type lookup. This method loads items from gateway applications as well
     * (using information about package association etc)
     *
     * @param entityTypeId type entity id
     * @return found entities
     */
    ServiceResponse<SecuredEntity> readSecuredEntitiesByType(Long entityTypeId);

    ServiceResponse<SimpleIdentifier<Boolean>> getSecurityEnabledFlagForEntity(String entityLookup);

	/**
	 * Upload package archive (*.ofr)
	 *
	 * @param stream stream data
	 *
	 * @return Information about uploaded package
	 */
	ServiceResponse<UploadPackageArchive> uploadPackageArchive(InputStream stream);

	/**
	 * Installed uploaded package archive
	 *
	 * @param uploadedFilename uploaded file name
	 * @param versionName      version name
	 * @param doAsCurrent      setup  current version
	 * @param doArchive        setup archive version
	 *
	 * @return complete result
	 */
	ServiceResponse performPackageArchive(String uploadedFilename, String versionName, Boolean doAsCurrent, Boolean doArchive);

	/**
	 * Check unique package version
	 *
	 * @param packageId   package id
	 * @param versionName version name
	 *
	 * @return complete result
	 */
	ServiceResponse checkUniquePackageVersion(Long packageId, String versionName);

	/**
	 * Install package archive to remote server
	 *
	 * @param packageAction package id and database actions
	 *
	 * @return install logs
	 */
	ServiceResponse installPackageArchive(PackageAction packageAction);

	/**
	 * Uninstall package archive with remote server
	 *
     * @param packageAction package id and database actions
	 *
	 * @return complete result
	 */
	ServiceResponse uninstallPackageArchive(PackageAction packageAction);

	/**
	 * Read package by id
	 *
	 * @param packageId package id
	 *
	 * @return founded package
	 */
	ServiceResponse<Package> readPackage(Long packageId);

	/**
	 * Read all packages
	 *
	 * @return founded packages
	 */
	ServiceResponse<Package> readAllPackages();

	/**
	 * Create new package
	 *
	 * @param data package data
	 *
	 * @return created package
	 */
	ServiceResponse<RegistryNodeTree> createPackage(Package data);

	/**
	 * Update package by id
	 *
	 * @param packageId package id
	 * @param data      package data
	 *
	 * @return updated package
	 */
	ServiceResponse<RegistryNodeTree> updatePackage(Long packageId, Package data);

	/**
	 * Delete package by id
	 *
	 * @param packageId package id
	 *
	 * @return deleted  package
	 */
	ServiceResponse<Package> deletePackage(Long packageId);

	/**
	 * Download package archive
	 *
	 * @param packageId       package id
	 * @param packageFilename downloaded file name
	 *
	 * @return stream data
	 */
	InputStream getPackageArchive(Long packageId, String packageFilename);

	/**
	 * Generate  project source and war archive
	 *
	 * @param packageId package id
	 *
	 * @return generation package info
	 */
	ServiceResponse<PackageVersion> generateWar(Long packageId);

	/**
	 * Generate upgrade xml
	 *
	 * @param packageId package id
	 * @param version   other version
	 *
	 * @return generation package info
	 */
	ServiceResponse<PackageVersion> generateUpgradeXml(Long packageId, Integer version);

	/**
	 * Get package version info
	 *
	 * @param packageId package id
	 *
	 * @return founded version info
	 */
	ServiceResponse<PackageVersionInfo> getPackageVersionsInfo(Long packageId);

	/**
	 * Lock/Unlock package version
	 *
	 * @param packageId package id
	 * @param data      package version data
	 *
	 * @return package data
	 */
	ServiceResponse<Package> lockPackageVersion(Long packageId, PackageVersionInfo data);

	/**
	 * This method exports the xml for all nodes and data beneath the package and associates it with the newly created version.
	 * It also updates the current version field once the new version is created.
	 * <p/>
	 * CREATE-VERSION-REQUEST: /rest/registry/package --> GET method
	 *
	 * @param packageId - Long
	 *
	 * @return - PackageVersionVO
	 */
	ServiceResponse<PackageVersion> archive(Long packageId);

	/**
	 * Delete package version
	 *
	 * @param packageId package id
	 * @param version   removed version
	 *
	 * @return package data
	 */
	ServiceResponse<Package> deletePackageVersion(Long packageId, Integer version);

	/**
	 * Activate old package version
	 *
	 * @param packageId package id
	 * @param version   activate version
	 *
	 * @return complete result
	 */
	ServiceResponse activatePackageVersion(Long packageId, Integer version);

	/**
	 * Supported other  version in current
	 *
	 * @param packageId package id
	 * @param version   supported version
	 *
	 * @return complete result
	 */
	ServiceResponse supportPackageVersion(Long packageId, Integer version);

	/**
	 * This takes one parameter, a file to add to the current version of the package.
	 * It places the code file on the file system and updates the version's code file location field accordingly.
	 * <p/>
	 * UPLOAD-VERSION-CODE-REQUEST: /rest/registry/package/version/[package-id]/[package-version-id]/[xml|war|zip] --> POST method
	 *
	 * @param fileType    - String, one of values: xml|war|zip
	 * @param packageId   - Long
	 * @param inputStream - uploaded file
	 *
	 * @return - Response with text/html type
	 */
	ServiceResponse<PackageVersion> uploadPackageVersionXML(Long packageId, String fileType, InputStream inputStream);

    /**
     * This should associate a database with the appropriate package in the registry.
     *
     * @param packageId  package id
     * @param databaseId database id
     *
     * @return complete result
     */
    ServiceResponse associateDatabase(Long packageId, Long databaseId);

    ServiceResponse reverseEngineering(Long registryNodeId);

	/**
	 * Read relationship  by id
	 *
	 * @param relationshipId relationship id
	 *
	 * @return founded relationship
	 */
	ServiceResponse<Relationship> readRelationship(Long relationshipId);


	/**
	 * Read all  relationships
	 *
	 * @return founded relationships
	 */
	ServiceResponse<Relationship> readAllRelationships();

	/**
	 * Create new relationship
	 *
	 * @param data relationship data
	 *
	 * @return created relationship
	 */
	ServiceResponse<RegistryNodeTree> createRelationship(Relationship data);

	/**
	 * Updated relationship by id
	 *
	 * @param relationshipId relationship id
	 * @param data           relationship data
	 *
	 * @return updated relationship
	 */
	ServiceResponse<RegistryNodeTree> updateRelationship(Long relationshipId, Relationship data);

	/**
	 * Delete relationship by id
	 *
	 * @param relationshipId relationship id
	 *
	 * @return deleted relationship
	 */
	ServiceResponse<Relationship> deleteRelationship(Long relationshipId);

	/**
	 * Service retrieves the root_domain
	 *
	 * @param rootDomainId - ID of the root_domain
	 *
	 * @return found root_domain
	 */
	ServiceResponse<RootDomain> readRootDomain(Long rootDomainId);

	/**
	 * Read all root domains
	 *
	 * @return founded root domains
	 */
	ServiceResponse<RootDomain> readAllRootDomains();

	/**
	 * Create new root domain
	 *
	 * @param data root domain data
	 *
	 * @return created root domain
	 */
	ServiceResponse<RegistryNodeTree> createRootDomain(RootDomain data);

	/**
	 * Update root domain by id
	 *
	 * @param domainId domain id
	 * @param data     root domain data
	 *
	 * @return updated root domain
	 */
	ServiceResponse<RegistryNodeTree> updateRootDomain(Long domainId, RootDomain data);

	/**
	 * Delete root domain
	 *
	 * @param domainId root domain id
	 *
	 * @return deleted root domain
	 */
	ServiceResponse<RootDomain> deleteRootDomain(Long domainId);

	/**
	 * Read system by id
	 *
	 * @param systemId system id
	 *
	 * @return founded system
	 */
	ServiceResponse<System> readSystem(Long systemId);
	/**
	 * Read system by id
	 *
	 * @param systemId system id
	 *
	 * @return founded system
	 */
	ServiceResponse<WebArchive> readWarStatus(Long systemId);

	/**
	 * Read all system
	 *
	 * @return founded systems
	 */
	ServiceResponse<System> readAllSystems(Boolean aliases);


	/**
	 * Create new system
	 *
	 * @param data system data
	 *
	 * @return created system
	 */
	ServiceResponse<RegistryNodeTree> createSystem(System data);

	/**
	 * Update system by id
	 *
	 * @param systemId system id
	 * @param data     system data
	 *
	 * @return updated system
	 */
	ServiceResponse<RegistryNodeTree> updateSystem(Long systemId, System data);

	/**
	 * Delete system by id
	 *
	 * @param systemId system id
	 *
	 * @return deleted system
	 */
	ServiceResponse<System> deleteSystem(Long systemId);

	/**
	 * Export system environments
	 *
	 * @param rootDomainId root domain id
	 * @param name         download file name
	 *
	 * @return stream data
	 */
	InputStream exportXml(Long rootDomainId, String name);

	/**
	 * Upload system environments
	 *
	 * @param rootDomainId root domain id
	 * @param stream       stream data
	 *
	 * @return complete result
	 */
	ServiceResponse importXml(Long rootDomainId, InputStream stream);

	/**
	 * Service retrieves the database
	 *
	 * @param databaseId - ID of the database
	 *
	 * @return found database
	 */
	ServiceResponse<Database> readDatabase(Long databaseId);

	/**
	 * Service searches the databases by associated package by system id
	 *
	 * @param packageId - ID of the package
	 *
	 * @return list of found databases
	 */
	ServiceResponse<Database> readDatabasesBySystem(Long packageId);

    /**
     * Service searches the databases by associated package
     *
     * @param packageId - ID of the package
     *
     * @return list of found databases
     */
    ServiceResponse<Database> readDatabasesByPackage(Long packageId);

    /**
     * Service searches the not-associated databases
     *
     * @return list of found databases
     */
    ServiceResponse<Database> readNotAssociatedDatabases();

	/**
	 * Service inserts the database
	 *
	 * @param data - request containing the database to be inserted
	 *
	 * @return registry tree node with newly created database
	 */
	ServiceResponse<RegistryNodeTree> createDatabase(Database data);

	/**
	 * Service modifies the database
	 *
	 * @param databaseId - ID of the database
	 * @param data       - request containing the database to be modified
	 *
	 * @return registry tree node with modified database
	 */
	ServiceResponse<RegistryNodeTree> updateDatabase(Long databaseId, Database data);

	/**
	 * Service removes the database
	 *
	 * @param databaseId - ID of the database to be removed
	 *
	 * @return database value object
	 */
	ServiceResponse<Database> deleteDatabase(Long databaseId);

	/**
	 * Service reads the filestore
	 *
	 * @param filestoreId - ID of the filestore
	 *
	 * @return read filestore
	 */
	ServiceResponse<Filestore> readFilestore(Long filestoreId);

		/**
	 * Service creates the filestore
	 *
	 * @param data - request containing the filestore to be created
	 *
	 * @return registry node tree for filestore
	 */
	ServiceResponse<RegistryNodeTree> createFilestore(Filestore data);

	/**
	 * Service updates the filestore
	 *
	 * @param filestoreId - ID of the filestore
	 * @param data     - request containing the filestore to be updated
	 *
	 * @return registry node tree for filestore
	 */
	ServiceResponse<RegistryNodeTree> updateFilestore(Long filestoreId, Filestore data);

	/**
	 * Service deletes the filestore
	 *
	 * @param filestoreId - ID of the filestore to be deleted
	 *
	 * @return filestore value object
	 */
	ServiceResponse<Filestore> deleteFilestore(Long filestoreId);

	/**
	 * Read server by id
	 *
	 * @param serverId server id
	 *
	 * @return founded server
	 */
	ServiceResponse<Server> readServer(Long serverId);


		/**
	 * Create new Server
	 *
	 * @param data server data
	 *
	 * @return created server
	 */
	ServiceResponse<RegistryNodeTree> createServer(Server data);

	/**
	 * Update server  by id
	 *
	 * @param serverId server id
	 * @param data server data
	 *
	 * @return updated server
	 */
	ServiceResponse<RegistryNodeTree> updateServer(Long serverId, Server data);

	/**
	 * Delete server by id
	 *
	 * @param serverId server id
	 *
	 * @return deleted server
	 */
	ServiceResponse<Server> deleteServer(Long serverId);

	/**
	 * Check  valid url
	 *
	 * @param data url data
	 * @return check result
	 */
	ServiceResponse<CheckUrl> testSystemStatus(CheckUrl data);

    ServiceResponse registerServerNode(ServerNodeConfig nodeConfig);

	/**
	 * registration Slave Nodes
	 *
	 * @param config Server node config
	 * @return server environments
	 */
	InputStream registerSlaveNode(ServerNodeConfig config);

	/**
	 * Restart self system
	 *
	 * @return restart status
	 */
	ServiceResponse restart();

    ServiceResponse ping();

	ServiceResponse migrateDatabase(PackageAction data);

	ServiceResponse<Report> readReport(Long reportId);

	ServiceResponse<RegistryNodeTree> createReport(Report report);

	ServiceResponse<RegistryNodeTree> updateReport(Long reportId, Report report);

	ServiceResponse<Report> deleteReport(Long reportId);

    ServiceResponse<Wizard> readWizard(Long wizardId);

	ServiceResponse<RegistryNodeTree> createWizard(Wizard wizard);

	ServiceResponse<RegistryNodeTree> updateWizard(Long wizardId, Wizard wizard);
	ServiceResponse<Wizard> deleteWizard(Long wizardId);

    ServiceResponse redeployPackageArchive(String packageLookup);

    ServiceResponse checkName(String path, String name, DomainType type);

    ServiceResponse<License> readLicense();

    ServiceResponse<License> verify(InputStream stream);

    ServiceResponse<Social> socialEnabled(String packageLookup);

    ServiceResponse<BIReport> readBIReport(Long reportId);

    ServiceResponse<BIReportUser> readBIReportUser(Long reportId);

    ServiceResponse<BIReportUser> deleteBIReportUser(Long reportId);

    ServiceResponse<BIReportUser> readAllowBIReportUser(String lookup);

    ServiceResponse<BIReport> readBIReportByLookup(String lookup);

    ServiceResponse<RegistryNodeTree> createBIReport(BIReport report);

    ServiceResponse<BIReportUser> createBIReportUser(BIReportUser report);

    ServiceResponse<BIReportUser> updateBIReportUser(Long id, BIReportUser report);
}
