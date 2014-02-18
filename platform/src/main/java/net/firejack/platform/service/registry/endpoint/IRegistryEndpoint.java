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

package net.firejack.platform.service.registry.endpoint;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.core.validation.annotation.DomainType;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.PathParam;

@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.registry.endpoint.IRegistryEndpoint")
public interface IRegistryEndpoint {

	@WebMethod
	ServiceResponse<CheckUrl> testSystemStatus(@WebParam(name = "request") ServiceRequest<CheckUrl> request);

	/**
	 * Load sub nodes
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return children nodes
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> getRegistryNodeChildren(
			@WebParam(name = "registryNodeId") Long registryNodeId,
			@WebParam(name = "pageType") PageType pageType,
            @WebParam(name = "packageLookup") String packageLookup);

    @WebMethod
    ServiceResponse checkName(@WebParam(name = "path") String path, @WebParam(name = "name") String name, @WebParam(name = "type") DomainType type);
	/**
	 * Load sub tree
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return all sab tree
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByIdChildren(
			@WebParam(name = "registryNodeId") Long registryNodeId,
			@WebParam(name = "pageType") PageType pageType);

    /**
	 * Load sub tree
	 *
	 * @param registryNodeLookup parent node lookup
	 * @param pageType       page type
	 *
	 * @return all sab tree
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByLookupChildren(
			@WebParam(name = "registryNodeLookup") String registryNodeLookup,
			@WebParam(name = "pageType") PageType pageType);

    /**
     * Get all Domains, Entities and Fields for package lookup
     *
     * @param entityId parent node id
     *
     * @return all tree nodes
     */
    @WebMethod
    ServiceResponse<RegistryNodeTree> getRegistryNodeFieldsByEntity(@WebParam(name = "entityId") Long entityId);

	/**
	 * Move nodes in tree
	 *
	 * @param request request with move data
	 *
	 * @return new position in tree
	 */
	@WebMethod
	ServiceResponse<MoveRegistryNodeTree> moveRegistryNode(@WebParam(name = "request") ServiceRequest<MoveRegistryNodeTree> request);

	/**
	 * Search  tree nodes element
	 *
	 * @param term           it is a search term
	 * @param registryNodeId sub tree root id
	 * @param lookup         prefix lookup
	 * @param assetType      type nodes
	 * @param offset         index start element
	 * @param limit          result size
	 * @param sortColumn     sort column name
	 * @param sortDirection  sort direction
	 *
	 * @return founded nodes
	 */
	@WebMethod
	ServiceResponse<Search> getSearchResult(
			@WebParam(name = "term") String term,
			@WebParam(name = "registryNodeId") Long registryNodeId,
			@WebParam(name = "lookup") String lookup,
			@WebParam(name = "assetType") String assetType,
			@WebParam(name = "start") Integer offset,
			@WebParam(name = "limit") Integer limit,
			@WebParam(name = "sort") String sortColumn,
			@WebParam(name = "dir") SortOrder sortDirection);


    /**
	 * Retrieves directories from server by provided path
	 *
	 * @param path it is a directory path
	 * @param directoryOnly is directory only
	 *
	 * @return founded nodes
	 */
    @WebMethod
    ServiceResponse<FileTree> readDirectoryStructure(
            @WebParam(name = "path") String path,
            @WebParam(name = "directoryOnly") Boolean directoryOnly);

	/**
	 * Read action by id
	 *
	 * @param id action Id
	 *
	 * @return found action
	 */
	@WebMethod
	ServiceResponse<Action> readAction(@WebParam(name = "id") Long id);

	/**
	 * Read all actions
	 *
	 * @return found actions
	 */
	@WebMethod
	ServiceResponse<Action> readAllActions();

	/**
	 * Found action by lookup
	 *
	 * @param packageLookup action lookup
	 *
	 * @return found action
	 */
	@WebMethod
	ServiceResponse readActionsFromCache(@WebParam(name = "packageLookup") String packageLookup);

	/**
	 * Create new action
	 *
	 * @param request Action data
	 *
	 * @return created action
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createAction(@WebParam(name = "request") ServiceRequest<Action> request);

	/**
	 * Update Action by id
	 *
	 * @param id action id
	 * @param request  action data
	 *
	 * @return updated action
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateAction(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<Action> request);

	/**
	 * Delete Action by Id
	 *
	 * @param id action d
	 *
	 * @return deleted action
	 */
	@WebMethod
	ServiceResponse<Action> deleteAction(@WebParam(name = "id") Long id);

	/**
	 * Associate package on system
	 *
	 * @param systemId  system id
	 * @param packageId package id
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse associatePackage(@WebParam(name = "systemId") Long systemId, @WebParam(name = "packageId") Long packageId);

	/**
	 * Remove associate package with system
	 *
	 * @param systemId  system id
	 * @param packageId package id
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse removeAssociationPackage(@WebParam(name = "systemId") Long systemId, @WebParam(name = "packageId") Long packageId);

	/**
	 * Read domain by id
	 *
	 * @param id domain id
	 *
	 * @return founded domain
	 */
	@WebMethod
	ServiceResponse<Domain> readDomain(@WebParam(name = "id") Long id);

	/**
	 * Read all domains
	 *
	 * @return founded domains
	 */
	@WebMethod
	ServiceResponse<Domain> readAllDomains();

	/**
	 * Create new  domain
	 *
	 * @param request domain data
	 *
	 * @return created domain
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createDomain(@WebParam(name = "request") ServiceRequest<Domain> request);

    @WebMethod
    ServiceResponse<Domain> createDomainByParentLookup(@WebParam(name = "request") ServiceRequest<Domain> request);

	/**
	 * Updated domain by id
	 *
	 * @param id domain id
	 * @param request  domain data
	 *
	 * @return updated domain
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateDomain(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Domain> request);

	/**
	 * Delete domain by id
	 *
	 * @param id domain id
	 *
	 * @return deleted domain
	 */
	@WebMethod
	ServiceResponse<Domain> deleteDomain(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<Domain> readDomainsByPackageLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Read entity By Id
	 *
	 * @param id entity id
	 *
	 * @return founded entity
	 */
	@WebMethod
	ServiceResponse<Entity> readEntity(@WebParam(name = "id") Long id);

	/**
	 * Read entity By Lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return founded entity
	 */
	@WebMethod
	ServiceResponse<Entity> readEntityByLookup(@WebParam(name = "lookup") String entityLookup);

    @WebMethod
    ServiceResponse<Entity> searchEntityByDomain(
            @WebParam(name = "terms") String terms,
            @WebParam(name = "domainId") Long domainId,
            @WebParam(name = "packageLookup") String packageLookup);

    /**
	 * Read entity By Lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return founded entity
	 */
    @WebMethod
    ServiceResponse<Entity> readParentEntityTypesByEntityLookup(@WebParam(name = "lookup") String entityLookup);

    @WebMethod
    ServiceResponse<Entity> readDirectChildrenTypes(@WebParam(name = "lookup") String entityLookup);

    /**
	 * Read entity By Lookup
	 *
	 * @param entityLookup entity lookup
	 *
	 * @return founded entity
	 */
    @WebMethod
    ServiceResponse<Entity> readEntitiesFromHierarchyByEntityLookup(@WebParam(name = "lookup") String entityLookup);

	/**
	 * Read all entities by type and super entity
	 *
	 * @param type     entity type
	 * @param exceptId extended entity id
	 *
	 * @return founded  entities
	 */
	@WebMethod
	ServiceResponse<Entity> readAllEntities(@WebParam(name = "type") String type, @WebParam(name = "exceptId") Long exceptId);

	@WebMethod
	ServiceResponse<Entity> readEntitiesByPackageLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Create new entity
	 *
	 * @param request entity data
	 *
	 * @return created entity
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createEntity(@WebParam(name = "request") ServiceRequest<Entity> request);

	/**
	 * Updated entity by id
	 *
	 * @param id entity id
	 * @param request  entity data
	 *
	 * @return updated entity
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateEntity(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Entity> request);

	/**
	 * Delete entity by id
	 *
	 * @param id entity id
	 *
	 * @return deleted entity
	 */
	@WebMethod
	ServiceResponse<Entity> deleteEntity(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<Form> createForm(@WebParam(name = "request") ServiceRequest<Form> request);

    /**
     * Load elements by specified entity type. This method also make lookup for
     * gateway entity instances using read-all action
     * @param entityTypeId object type id
     * @return list of obbject that contain information about entities of specified type.
     */
    @WebMethod
    ServiceResponse<SecuredEntity> getSecuredEntitiesByType(@WebParam(name = "entityTypeId") Long entityTypeId);

	@WebMethod
	ServiceResponse<UploadPackageArchive> uploadPackageArchive(@WebParam(name = "info") FileInfo info);

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
	@WebMethod
	ServiceResponse performPackageArchive(
			@WebParam(name = "uploadedFilename") String uploadedFilename,
			@WebParam(name = "versionName") String versionName,
			@WebParam(name = "doAsCurrent") Boolean doAsCurrent,
			@WebParam(name = "doArchive") Boolean doArchive);

	/**
	 * Check unique package version
	 *
	 * @param packageId   package id
	 * @param versionName version name
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse checkUniquePackageVersion(
			@WebParam(name = "packageId") Long packageId,
			@WebParam(name = "versionName") String versionName);

	/**
	 * Install package archive to remote server
	 *
	 * @param request package and databases information
	 *
	 * @return install logs
	 */
	@WebMethod
	ServiceResponse installPackageArchive(
			@WebParam(name = "request") ServiceRequest<PackageAction> request);

	@WebMethod
	ServiceResponse migrateDatabase(
			@WebParam(name = "request") ServiceRequest<PackageAction> request);

	/**
	 * Uninstall package archive with remote server
	 *
	 * @param request package and databases information
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse uninstallPackageArchive(
            @WebParam(name = "request") ServiceRequest<PackageAction> request);

    @WebMethod
    ServiceResponse redeployPackageArchive(@WebParam(name = "packageLookup") String packageLookup);

	/**
	 * Read package by id
	 *
	 * @param id package id
	 *
	 * @return founded package
	 */
	@WebMethod
	ServiceResponse<Package> readPackage(@WebParam(name = "id") Long id);

	/**
	 * Read all packages
	 *
	 * @return founded packages
	 */
	@WebMethod
	ServiceResponse<Package> readAllPackages();

	/**
	 * Create new package
	 *
	 * @param request package data
	 *
	 * @return created package
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createPackage(@WebParam(name = "request") ServiceRequest<Package> request);

	/**
	 * Update package by id
	 *
	 * @param id package id
	 * @param request   package data
	 *
	 * @return updated package
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updatePackage(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Package> request);

	/**
	 * Delete package by id
	 *
	 * @param id package id
	 *
	 * @return deleted  package
	 */
	@WebMethod
	ServiceResponse<Package> deletePackage(@WebParam(name = "id") Long id);

	@WebMethod(operationName = "getPackageArchive")
	ServiceResponse<FileInfo> getPackageArchiveWS(@WebParam(name = "packageId") Long packageId, @WebParam(name = "packageFilename") String packageFilename);

	/**
	 * Generate  project source and war archive
	 *
	 * @param packageId package id
	 *
	 * @return generation package info
	 */
	@WebMethod
	ServiceResponse<PackageVersion> generateWar(@WebParam(name = "packageId") Long packageId);

	/**
	 * Generate upgrade xml
	 *
	 * @param packageId package id
	 * @param version   other version
	 *
	 * @return generation package info
	 */
	@WebMethod
	ServiceResponse<PackageVersion> generateUpgradeXml(@WebParam(name = "packageId") Long packageId, @WebParam(name = "version") Integer version);

	/**
	 * Get package version info
	 *
	 * @param packageId package id
	 *
	 * @return founded version info
	 */
	@WebMethod
	ServiceResponse<PackageVersionInfo> getPackageVersionsInfo(@WebParam(name = "packageId") Long packageId);

	/**
	 * Lock/Unlock package version
	 *
	 * @param packageId package id
	 * @param request   package version data
	 *
	 * @return package data
	 */
	@WebMethod
	ServiceResponse<Package> lockPackageVersion(@WebParam(name = "packageId") Long packageId, @WebParam(name = "request") ServiceRequest<PackageVersionInfo> request);

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
	@WebMethod
	ServiceResponse<PackageVersion> archive(@WebParam(name = "packageId") Long packageId);

	/**
	 * Delete package version
	 *
	 * @param packageId package id
	 * @param version   removed version
	 *
	 * @return package data
	 */
	@WebMethod
	ServiceResponse<Package> deletePackageVersion(@WebParam(name = "packageId") Long packageId, @WebParam(name = "version") Integer version);

	/**
	 * Activate old package version
	 *
	 * @param packageId package id
	 * @param version   activate version
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse activatePackageVersion(@WebParam(name = "packageId") Long packageId, @WebParam(name = "version") Integer version);

	/**
	 * Supported other  version in current
	 *
	 * @param packageId package id
	 * @param version   supported version
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse supportPackageVersion(@WebParam(name = "packageId") Long packageId, @WebParam(name = "version") Integer version);

	/**
	 * This takes one parameter, a file to add to the current version of the package.
	 * It places the code file on the file system and updates the version's code file location field accordingly.
	 * <p/>
	 * UPLOAD-VERSION-CODE-REQUEST: /rest/registry/package/version/[package-id]/[package-version-id]/[xml|war|zip] --> POST method
	 *
	 * @param fileType  - String, one of values: xml|war|zip
	 * @param packageId - Long
	 * @param info      - uploaded file
	 *
	 * @return - Response with text/html type
	 *
	 * @throws java.io.IOException can't serialize response  data
	 */
	@WebMethod
	ServiceResponse<PackageVersion> uploadPackageVersionXML(
			@WebParam(name = "fileType") String fileType,
			@WebParam(name = "packageId") Long packageId,
			@WebParam(name = "info") FileInfo info);

    /**
     * This should associate a database with the appropriate package in the registry.
     *
     * @param packageId  package id
     * @param databaseId database id
     *
     * @return complete result
     */
    ServiceResponse associateDatabase(
            @WebParam(name = "packageId") Long packageId,
            @WebParam(name = "databaseId") Long databaseId);

    ServiceResponse<Package> reverseEngineering(@WebParam(name = "registryNodeId") Long registryNodeId);

	/**
	 * Read relationship  by id
	 *
	 * @param id relationship id
	 *
	 * @return founded relationship
	 */
	@WebMethod
	ServiceResponse<Relationship> readRelationship(@WebParam(name = "id") Long id);

	/**
	 * Read all  relationships
	 *
	 * @return founded relationships
	 */
	@WebMethod
	ServiceResponse<Relationship> readAllRelationships();

	/**
	 * Create new relationship
	 *
	 * @param request relationship data
	 *
	 * @return created relationship
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createRelationship(@WebParam(name = "request") ServiceRequest<Relationship> request);

	/**
	 * Updated relationship by id
	 *
	 * @param id relationship id
	 * @param request        relationship data
	 *
	 * @return updated relationship
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateRelationship(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Relationship> request);

	/**
	 * Delete relationship by id
	 *
	 * @param id relationship id
	 *
	 * @return deleted relationship
	 */
	@WebMethod
	ServiceResponse<Relationship> deleteRelationship(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves the root_domain
	 *
	 * @param id - ID of the root_domain
	 *
	 * @return found root_domain
	 */
	@WebMethod
	ServiceResponse<RootDomain> readRootDomain(@WebParam(name = "id") Long id);

	/**
	 * Read all root domains
	 *
	 * @return founded root domains
	 */
	@WebMethod
	ServiceResponse<RootDomain> readAllRootDomains();

	/**
	 * Create new root domain
	 *
	 * @param request root domain data
	 *
	 * @return created root domain
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createRootDomain(@WebParam(name = "request") ServiceRequest<RootDomain> request);

	/**
	 * Update root domain by id
	 *
	 * @param id domain id
	 * @param request  root domain data
	 *
	 * @return updated root domain
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateRootDomain(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<RootDomain> request);

	/**
	 * Delete root domain
	 *
	 * @param id root domain id
	 *
	 * @return deleted root domain
	 */
	@WebMethod
	ServiceResponse<RootDomain> deleteRootDomain(@WebParam(name = "id") Long id);

	/**
	 * Read system by id
	 *
	 * @param id system id
	 *
	 * @return founded system
	 */
	@WebMethod
	ServiceResponse<System> readSystem(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<WebArchive> readWarStatus(@WebParam(name = "systemId") Long systemId);

	/**
	 * Restart self system
	 *
	 * @return restart status
	 */
	@WebMethod
	ServiceResponse restart();

	/**
	 * Read all system
	 *
	 * @return founded systems
	 */
	@WebMethod
	ServiceResponse<System> readAllSystems(@WebParam(name = "aliases") Boolean aliases);

	/**
	 * Create new system
	 *
	 * @param request system data
	 *
	 * @return created system
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createSystem(@WebParam(name = "request") ServiceRequest<System> request);

	/**
	 * Update system by id
	 *
	 * @param id system id
	 * @param request  system data
	 *
	 * @return updated system
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateSystem(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<System> request);

	/**
	 * Delete system by id
	 *
	 * @param id system id
	 *
	 * @return deleted system
	 */
	@WebMethod
	ServiceResponse<System> deleteSystem(@WebParam(name = "id") Long id);

	@WebMethod(operationName = "exportXml")
	ServiceResponse<FileInfo> exportXmlWS(@WebParam(name = "id") Long id, @WebParam(name = "filename") String filename);

	/**
	 * Upload system environments
	 *
	 * @param id root domain id
	 * @param info  stream data
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse importXml(@WebParam(name = "id") Long id, @WebParam(name = "info") FileInfo info);

	/**
	 * Service retrieves the database
	 *
	 * @param id - ID of the database
	 *
	 * @return found database
	 */
	@WebMethod
	ServiceResponse<Database> readDatabase(@WebParam(name = "id") Long id);

	/**
	 * Service searches the databases by associated package
	 *
	 * @param packageId - ID of the package
	 *
	 * @return list of found databases
	 */
	@WebMethod
	ServiceResponse<Database> readDatabasesBySystem(@WebParam(name = "packageId") Long packageId);

	/**
	 * Service inserts the database
	 *
	 * @param request - request containing the database to be inserted
	 *
	 * @return registry tree node with newly created database
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createDatabase(@WebParam(name = "request") ServiceRequest<Database> request);

	/**
	 * Service modifies the database
	 *
	 * @param id - ID of the database
	 * @param request    - request containing the database to be modified
	 *
	 * @return registry tree node with modified database
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateDatabase(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Database> request);

	/**
	 * Service removes the database
	 *
	 * @param id - ID of the database to be removed
	 *
	 * @return database value object
	 */
	@WebMethod
	ServiceResponse<Database> deleteDatabase(@WebParam(name = "id") Long id);

	/**
	 * Service reads the filestore
	 *
	 * @param id - ID of the filestore
	 *
	 * @return read filestore
	 */
	@WebMethod
	ServiceResponse<Filestore> readFilestore(@WebParam(name = "id") Long id);

	/**
	 * Service creates the filestore
	 *
	 * @param request - request containing the filestore to be created
	 *
	 * @return registry node tree for filestore
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createFilestore(@WebParam(name = "request") ServiceRequest<Filestore> request);

	/**
	 * Service updates the filestore
	 *
	 * @param id - ID of the filestore
	 * @param request     - request containing the filestore to be updated
	 *
	 * @return registry node tree for filestore
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateFilestore(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Filestore> request);

	/**
	 * Service deletes the filestore
	 *
	 * @param id - ID of the filestore to be deleted
	 *
	 * @return filestore value object
	 */
	@WebMethod
	ServiceResponse<Filestore> deleteFilestore(@WebParam(name = "id") Long id);

	/**
	 * Read server by id
	 *
	 * @param id server id
	 *
	 * @return founded server
	 */
	@WebMethod
	ServiceResponse<Server> readServer(@WebParam(name = "id") Long id);

	/**
	 * Create new Server
	 *
	 * @param request server data
	 *
	 * @return created server
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createServer(@WebParam(name = "request") ServiceRequest<Server> request);

	/**
	 * Update server  by id
	 *
	 * @param id server id
	 * @param request  server data
	 *
	 * @return updated server
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateServer(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Server> request);

	/**
	 * Delete server by id
	 *
	 * @param id server id
	 *
	 * @return deleted server
	 */
	@WebMethod
	ServiceResponse<Server> deleteServer(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<Report> readReport(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<RegistryNodeTree> createReport(@WebParam(name = "request") ServiceRequest<Report> request);

	@WebMethod
	ServiceResponse<RegistryNodeTree> updateReport(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Report> request);

	@WebMethod
	ServiceResponse<Report> deleteReport(@WebParam(name = "id") Long id);

    @WebMethod
	ServiceResponse<Wizard> readWizard(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<RegistryNodeTree> createWizard(@WebParam(name = "request") ServiceRequest<Wizard> request);

	@WebMethod
	ServiceResponse<RegistryNodeTree> updateWizard(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Wizard> request);

	@WebMethod
	ServiceResponse<Wizard> deleteWizard(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<License> readLicense();

    @WebMethod
   	ServiceResponse verifyLicenseWS(@WebParam(name = "info") FileInfo info);

    @WebMethod
    ServiceResponse<Social> socialEnabled(@WebParam(name = "package") String _package);

    @WebMethod
    ServiceResponse<BIReport> readBIReport(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<BIReportUser> readBIReportUser(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<BIReportUser> deleteBIReportUser(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<BIReportUser> readAllowBIReportUser(@WebParam(name = "lookup") String packageLookup);

    @WebMethod
    ServiceResponse<BIReport> readBIReportByLookup(@PathParam("lookup") String lookup);

    @WebMethod
    ServiceResponse<RegistryNodeTree> createBIReport(@WebParam(name = "request") ServiceRequest<BIReport> request);

    @WebMethod
    ServiceResponse<BIReportUser> createBIReportUser(@WebParam(name = "request") ServiceRequest<BIReportUser> request);

    @WebMethod
    ServiceResponse<BIReportUser> updateBIReportUser(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<BIReportUser> request);
}
