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

import com.sun.jersey.multipart.FormDataParam;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.core.validation.annotation.DomainType;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
@Path("registry")
public class RegistryEndpoint implements IRegistryEndpoint {

	/**
	 * Check  valid url
	 *
	 * @param request url data
	 * @return check result
	 */
	@POST
	@Path("/url/check")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<CheckUrl> testSystemStatus(ServiceRequest<CheckUrl> request) {
	    return OPFEngine.RegistryService.testSystemStatus(request.getData());
	}

	/**
	 * Load sub nodes
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return children nodes
	 */
	@GET
	@Path("/children/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> getRegistryNodeChildren(
			@PathParam("registryNodeId") Long registryNodeId,
			@QueryParam("pageType") PageType pageType,
            @QueryParam("packageLookup") String packageLookup) {
		return OPFEngine.RegistryService.getRegistryNodeChildren(registryNodeId, pageType, packageLookup);
	}

	@GET
	@Path("/check/{path}/{type}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse checkName(@PathParam("path") String path, @QueryParam("name") String name, @PathParam("type") DomainType type) {
        return OPFEngine.RegistryService.checkName(path, name, type);
    }

    /**
	 * Load sub tree
	 *
	 * @param registryNodeId parent node id
	 * @param pageType       page type
	 *
	 * @return all sab tree
	 */
	@GET
	@Path("/children/expanded-by-id/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByIdChildren(
			@PathParam("registryNodeId") Long registryNodeId,
			@QueryParam("pageType") PageType pageType) {
		return OPFEngine.RegistryService.getRegistryNodeWithExpandedByIdChildren(registryNodeId, pageType);
	}

    /**
	 * Load sub tree
	 *
	 * @param registryNodeLookup parent node lookup
	 * @param pageType       page type
	 *
	 * @return all sub tree
	 */
	@GET
	@Path("/children/expanded-by-lookup/{registryNodeLookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByLookupChildren(
			@PathParam("registryNodeLookup") String registryNodeLookup,
			@QueryParam("pageType") PageType pageType) {
		return OPFEngine.RegistryService.getRegistryNodeWithExpandedByLookupChildren(registryNodeLookup, pageType);
	}

    /**
   	 * Get all Domains, Entities and Fields for package lookup
   	 *
   	 * @param entityId parent node id
   	 *
   	 * @return all tree nodes
   	 */
   	@GET
   	@Path("/entity-fields/{entityId}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<RegistryNodeTree> getRegistryNodeFieldsByEntity(@PathParam("entityId") Long entityId) {
   		return OPFEngine.RegistryService.getRegistryNodeFieldsByEntity(entityId);
   	}

//    CREATE-REQUEST
//    /rest/registry/domain --> POST method

	/**
	 * Move nodes in tree
	 *
	 * @param request request with move data
	 *
	 * @return new position in tree
	 */
	@PUT
	@Path("/change/position")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<MoveRegistryNodeTree> moveRegistryNode(ServiceRequest<MoveRegistryNodeTree> request) {
		return OPFEngine.RegistryService.moveRegistryNode(request.getData());
	}

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
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Search> getSearchResult(
			@QueryParam("term") String term,
			@QueryParam("registryNodeId") Long registryNodeId,
			@QueryParam("lookup") String lookup,
			@QueryParam("assetType") String assetType,
			@QueryParam("start") Integer offset,
			@QueryParam("limit") Integer limit,
			@QueryParam("sort") String sortColumn,
			@QueryParam("dir") SortOrder sortDirection) {
		Paging paging = new Paging(offset, limit, sortColumn, sortDirection);
		return OPFEngine.RegistryService.getSearchResult(term, registryNodeId, lookup, assetType, paging);
	}


    /**
	 * Retrieves directories from server by provided path
	 *
	 * @param path it is a directory path
	 * @param directoryOnly is directory only
	 *
	 * @return founded nodes
	 */
    @GET
    @Path("/filemanager/directory")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileTree> readDirectoryStructure(
            @QueryParam("path") String path,
            @QueryParam("directoryOnly") Boolean directoryOnly) {
        return OPFEngine.RegistryService.readDirectory(path, directoryOnly);
    }


	/**
	 * Read action by id
	 *
	 * @param actionId action Id
	 *
	 * @return found action
	 */
	@GET
	@Path("/action/{actionId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Action> readAction(@PathParam("actionId") Long actionId) {
		return OPFEngine.RegistryService.readAction(actionId);
	}

	/**
	 * Read all actions
	 *
	 * @return found actions
	 */
	@GET
	@Path("/action")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Action> readAllActions() {
		return OPFEngine.RegistryService.readAllActions();
	}

	/**
	 * Found action by lookup
	 *
	 * @param packageLookup action lookup
	 *
	 * @return found action
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/action/cached/{packageLookup}")
	public ServiceResponse<Action> readActionsFromCache(@PathParam("packageLookup") String packageLookup) {
		return OPFEngine.RegistryService.readActionsFromCache(packageLookup);
	}

//    CREATE-REQUEST:
//    /rest/registry/action --> POST method
//    this differs from the domain create, because it also adds all the fields for the action. Fields are only saved when the entire action is saved so the administrator can work with and test the configurations before saving.

	/**
	 * Create new action
	 *
	 * @param request Action data
	 *
	 * @return created action
	 */
	@POST
	@Path("/action")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createAction(ServiceRequest<Action> request) {
		return OPFEngine.RegistryService.createAction(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/action/[id] --> PUT method

	/**
	 * Update Action by id
	 *
	 * @param id action id
	 * @param request  action data
	 *
	 * @return updated action
	 */
	@PUT
	@Path("/action/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateAction(
			@PathParam("id") Long id, ServiceRequest<Action> request) {
		return OPFEngine.RegistryService.updateAction(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/action/[id] --> DELETE method

	/**
	 * Delete Action by Id
	 *
	 * @param id action d
	 *
	 * @return deleted action
	 */
	@DELETE
	@Path("/action/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Action> deleteAction(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteAction(id);
	}

//    READ-ONE-REQUEST:
//    /rest/registry/domain/[id] --> GET method

	/**
	 * Read domain by id
	 *
	 * @param id domain id
	 *
	 * @return founded domain
	 */
	@GET
	@Path("/domain/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Domain> readDomain(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readDomain(id);
	}

//    READ-ALL-REQUEST:
//    /rest/registry/domain --> GET method

	/**
	 * Read all domains
	 *
	 * @return founded domains
	 */
	@GET
	@Path("/domain")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Domain> readAllDomains() {
		return OPFEngine.RegistryService.readAllDomains();
	}

//    CREATE-REQUEST
//    /rest/registry/domain --> POST method

	/**
	 * Create new  domain
	 *
	 * @param request domain data
	 *
	 * @return created domain
	 */
	@POST
	@Path("/domain")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createDomain(ServiceRequest<Domain> request) {
		return OPFEngine.RegistryService.createDomain(request.getData());
	}

    //TODO need to add to package.xml
    @POST
   	@Path("/domain/parent-lookup")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Domain> createDomainByParentLookup(ServiceRequest<Domain> request) {
   		return OPFEngine.RegistryService.createDomainByParentLookup(request.getData());
   	}

    /**
   	 * Service creates domain and database and can do reverse engineering
   	 *
   	 * @param request - request containing the domain with database to be created
   	 * @param reverseEngineer - if true then reverse engineer data
     *
   	 * @return domain with newly created database
   	 */
   	@POST
   	@Path("/domain/database")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Domain> createDomainDatabase(ServiceRequest<Domain> request,
                   @QueryParam("reverseEngineer") Boolean reverseEngineer) {
   		return OPFEngine.RegistryService.createDomainDatabase(request.getData(), reverseEngineer);
   	}

//    UPDATE-REQUEST
//    /rest/registry/domain/[id] --> PUT method

	/**
	 * Updated domain by id
	 *
	 * @param id domain id
	 * @param request  domain data
	 *
	 * @return updated domain
	 */
	@PUT
	@Path("/domain/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateDomain(@PathParam("id") Long id, ServiceRequest<Domain> request) {
		return OPFEngine.RegistryService.updateDomain(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/domain/[id] --> DELETE method

	/**
	 * Delete domain by id
	 *
	 * @param id domain id
	 *
	 * @return deleted domain
	 */
	@DELETE
	@Path("/domain/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Domain> deleteDomain(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteDomain(id);
	}

    @GET
	@Path("/domains/by-lookup/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Domain> readDomainsByPackageLookup(@PathParam("lookup") String lookup) {
		return OPFEngine.RegistryService.readDomainsByPackageLookup(lookup);
	}

//    READ-ONE-REQUEST:
//    /rest/registry/entity/[id] --> GET method
//    note: this will bring back the fields for the entity as well.

	/**
	 * Read entity By Id
	 *
	 * @param id entity id
	 *
	 * @return founded entity
	 */
	@GET
	@Path("/entity/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Entity> readEntity(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readEntity(id);
	}

    /**
     * Read entity By Lookup
     * @param entityLookup entity lookup
     *
     * @return entity with specified lookup if found. Otherwise null
     */
    @GET
	@Path("/entity/by-lookup/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Entity> readEntityByLookup(@PathParam("lookup") String entityLookup) {
        return OPFEngine.RegistryService.readEntityByLookup(entityLookup);
    }

    //todo need to describe it in package.xml
    /**
     * Search entity By Domain
     * @param terms
     *
     * @return entity with specified lookup if found. Otherwise null
     */
    @GET
	@Path("/entity/search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Entity> searchEntityByDomain(
            @QueryParam("terms") String terms,
            @QueryParam("domainId") Long domainId,
            @QueryParam("packageLookup") String packageLookup) {
        return OPFEngine.RegistryService.searchEntityByDomain(terms, domainId, packageLookup);
    }

    /**
     * Read entity By Lookup
     * @param entityLookup entity lookup
     *
     * @return entity with specified lookup if found. Otherwise null
     */
    @GET
	@Path("/entity/parent-types/by-lookup/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Entity> readParentEntityTypesByEntityLookup(@PathParam("lookup") String entityLookup) {
        return OPFEngine.RegistryService.readParentEntityTypesByEntityLookup(entityLookup);
    }

    @GET
	@Path("/entity/direct-children-types-by-lookup")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Entity> readDirectChildrenTypes(@QueryParam("entityLookup") String entityLookup) {
        return OPFEngine.RegistryService.readDirectChildrenTypes(entityLookup);
    }

    /**
     * Read entity By Lookup
     * @param entityLookup entity lookup
     *
     * @return entity with specified lookup if found. Otherwise null
     */
    @GET
	@Path("/entity/hierarchy-types-up/by-lookup/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Override
    public ServiceResponse<Entity> readEntitiesFromHierarchyByEntityLookup(@PathParam("lookup") String entityLookup) {
        return OPFEngine.RegistryService.readEntitiesUpInHierarchyByLookup(entityLookup);
    }

    /**
	 * Read all entities by type and super entity
	 *
	 * @param type     entity type
	 * @param exceptId extended entity id
	 *
	 * @return founded  entities
	 */
	@GET
	@Path("/entity/{type:(standard|classifier|data|security-enabled)}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Entity> readAllEntities(@PathParam("type") String type, @QueryParam("exceptId") Long exceptId) {
		return OPFEngine.RegistryService.readAllEntities(type, exceptId);
	}

    @GET
	@Path("/entities/by-lookup/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Entity> readEntitiesByPackageLookup(@PathParam("lookup") String lookup) {
		return OPFEngine.RegistryService.readEntitiesByPackageLookup(lookup);
	}



//    CREATE-REQUEST:
//    /rest/registry/entity --> POST method
//    this differs from the domain create, because it also adds all the fields for the entity. Fields are only saved when the entire entity is saved so the administrator can work with and test the configurations before saving.

	/**
	 * Create new entity
	 *
	 * @param request entity data
	 *
	 * @return created entity
	 */
	@POST
	@Path("/entity")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createEntity(ServiceRequest<Entity> request) {
		return OPFEngine.RegistryService.createEntity(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/entity/[id] --> PUT method

	/**
	 * Updated entity by id
	 *
	 * @param id entity id
	 * @param request  entity data
	 *
	 * @return updated entity
	 */
	@PUT
	@Path("/entity/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateEntity(@PathParam("id") Long id, ServiceRequest<Entity> request) {
		return OPFEngine.RegistryService.updateEntity(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/entity/[id] --> DELETE method

	/**
	 * Delete entity by id
	 *
	 * @param id entity id
	 *
	 * @return deleted entity
	 */
	@DELETE
	@Path("/entity/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Entity> deleteEntity(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteEntity(id);
	}

    //todo need to add to package.xml
    @POST
   	@Path("/form")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Form> createForm(ServiceRequest<Form> request) {
   		return OPFEngine.RegistryService.createForm(request.getData());
   	}

    @GET
	@Path("/entity/by-type/{entityTypeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SecuredEntity> getSecuredEntitiesByType(@PathParam("entityTypeId") Long entityTypeId) {
        return OPFEngine.RegistryService.readSecuredEntitiesByType(entityTypeId);
    }

    @GET
    @Path("/entity/security-enabled-by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<SimpleIdentifier<Boolean>> getSecurityEnabledFlagForEntity(
            @QueryParam("entityLookup") String entityLookup) {
        return OPFEngine.RegistryService.getSecurityEnabledFlagForEntity(entityLookup);
    }

	/**
	 * Upload package archive (*.ofr)
	 *
	 * @param inputStream stream data
	 *
	 * @return Information about uploaded package
	 *
	 * @throws java.io.IOException Can't serialized response data
	 */
    //todo: not valid in terms of OPF action abstraction
	@POST
	@Path("/installation/package/upload")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadPackageArchive(@FormDataParam("file") InputStream inputStream) throws IOException {
		ServiceResponse<UploadPackageArchive> response = OPFEngine.RegistryService.uploadPackageArchive(inputStream);

		String responseData = (new ObjectMapper()).writeValueAsString(response);
		return Response.ok(responseData).type(MediaType.TEXT_HTML_TYPE).build();
	}

	public ServiceResponse<UploadPackageArchive> uploadPackageArchive(FileInfo info) {
		return OPFEngine.RegistryService.uploadPackageArchive(info.getStream());
	}

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
	@GET
	@Path("/installation/package/perform")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse performPackageArchive(
			@QueryParam("uploadedFilename") String uploadedFilename,
			@QueryParam("versionName") String versionName,
			@QueryParam("doAsCurrent") Boolean doAsCurrent,
			@QueryParam("doArchive") Boolean doArchive) {
		return OPFEngine.RegistryService.performPackageArchive(uploadedFilename, versionName, doAsCurrent, doArchive);
	}

	/**
	 * Check unique package version
	 *
	 * @param packageId   package id
	 * @param versionName version name
	 *
	 * @return complete result
	 */
	@GET
	@Path("/installation/package/check/unique/version/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse checkUniquePackageVersion(
			@PathParam("packageId") Long packageId,
			@QueryParam("versionName") String versionName) {
		return OPFEngine.RegistryService.checkUniquePackageVersion(packageId, versionName);
	}

	/**
	 * Install package archive to remote server
	 *
	 * @return install logs
	 */
	@POST
	@Path("/installation/package/install")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse installPackageArchive(ServiceRequest<PackageAction> request) {
		return OPFEngine.RegistryService.installPackageArchive(request.getData());
	}

	@POST
	@Path("/installation/package/migrate")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse migrateDatabase(ServiceRequest<PackageAction> request) {
		return OPFEngine.RegistryService.migrateDatabase(request.getData());
	}

	/**
	 * Uninstall package archive with remote server
	 *
	 * @return complete result
	 */
	@POST
	@Path("/installation/package/uninstall")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse uninstallPackageArchive(ServiceRequest<PackageAction> request) {
		return OPFEngine.RegistryService.uninstallPackageArchive(request.getData());
	}

    @GET
   	@Path("/installation/package/redeploy/{packageLookup}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse redeployPackageArchive(@PathParam("packageLookup") String packageLookup) {
   		return OPFEngine.RegistryService.redeployPackageArchive(packageLookup);
   	}

//    READ-ONE-REQUEST:
//    /rest/registry/package/[id] ?version=[version]--> GET method

	/**
	 * Read package by id
	 *
	 * @param id package id
	 *
	 * @return founded package
	 */
	@GET
	@Path("/package/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> readPackage(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readPackage(id);
	}

//    READ-ALL-REQUEST:
//    /rest/registry/package --> GET method

	/**
	 * Read all packages
	 *
	 * @return founded packages
	 */
	@GET
	@Path("/package")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> readAllPackages() {
		return OPFEngine.RegistryService.readAllPackages();
	}

//    CREATE-REQUEST:
//    /rest/registry/package --> POST method

	/**
	 * Create new package
	 *
	 * @param request package data
	 *
	 * @return created package
	 */
	@POST
	@Path("/package")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createPackage(ServiceRequest<Package> request) {
		return OPFEngine.RegistryService.createPackage(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/package/[id] --> PUT method
//    note this is different than the domain update method (slightly) because it allows the "current-version" field to be set. "current-version" is null for all other types of nodes.

	/**
	 * Update package by id
	 *
	 * @param id package id
	 * @param request   package data
	 *
	 * @return updated package
	 */
	@PUT
	@Path("/package/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updatePackage(@PathParam("id") Long id, ServiceRequest<Package> request) {
		return OPFEngine.RegistryService.updatePackage(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/package/[id] --> DELETE method

	/**
	 * Delete package by id
	 *
	 * @param id package id
	 *
	 * @return deleted  package
	 */
	@DELETE
	@Path("/package/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> deletePackage(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.deletePackage(id);
	}

	/**
	 * Download package archive
	 *
	 * @param packageId       package id
	 * @param packageFilename downloaded file name
	 *
	 * @return stream data
	 */
    //todo: not valid in terms of OPF action abstraction
	@GET
	@Path("/package/download/{packageId}/{packageFilename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput getPackageArchive(@PathParam("packageId") final Long packageId, @PathParam("packageFilename") final String packageFilename) {
		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				InputStream stream = OPFEngine.RegistryService.getPackageArchive(packageId, packageFilename);
				if (stream != null) {
					IOUtils.copy(stream, output);
					IOUtils.closeQuietly(stream);
				}
			}
		};
	}

	public ServiceResponse<FileInfo> getPackageArchiveWS(Long packageId, String packageFilename) {
		InputStream stream = OPFEngine.RegistryService.getPackageArchive(packageId, packageFilename);
		return new ServiceResponse<FileInfo>(new FileInfo(packageFilename, stream), "Package archive successfully", true);
	}

	/**
	 * Generate  project source and war archive
	 *
	 * @param packageId package id
	 *
	 * @return generation package info
	 */
	@GET
	@Path("/package/generate/code/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<PackageVersion> generateWar(@PathParam("packageId") Long packageId) {
		return OPFEngine.RegistryService.generateWar(packageId);
	}

	/**
	 * Generate upgrade xml
	 *
	 * @param packageId package id
	 * @param version   other version
	 *
	 * @return generation package info
	 */
	@GET
	@Path("/package/generate/upgrade/{packageId}/{version}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<PackageVersion> generateUpgradeXml(@PathParam("packageId") Long packageId, @PathParam("version") Integer version) {
		return OPFEngine.RegistryService.generateUpgradeXml(packageId, version);
	}

	/**
	 * Get package version info
	 *
	 * @param packageId package id
	 *
	 * @return founded version info
	 */
	@GET
	@Path("/package/versions/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<PackageVersionInfo> getPackageVersionsInfo(@PathParam("packageId") Long packageId) {
		return OPFEngine.RegistryService.getPackageVersionsInfo(packageId);
	}

	/**
	 * Lock/Unlock package version
	 *
	 * @param packageId package id
	 * @param request   package version data
	 *
	 * @return package data
	 */
	@PUT
	@Path("/package/lock/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> lockPackageVersion(@PathParam("packageId") Long packageId, ServiceRequest<PackageVersionInfo> request) {
		return OPFEngine.RegistryService.lockPackageVersion(packageId, request.getData());
	}

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
	@GET
	@Path("/package/archive/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<PackageVersion> archive(@PathParam("packageId") Long packageId) {
		return OPFEngine.RegistryService.archive(packageId);
	}

	/**
	 * Delete package version
	 *
	 * @param packageId package id
	 * @param version   removed version
	 *
	 * @return package data
	 */
	@DELETE
	@Path("/package/version/{packageId}/{version}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> deletePackageVersion(@PathParam("packageId") Long packageId, @PathParam("version") Integer version) {
		return OPFEngine.RegistryService.deletePackageVersion(packageId, version);
	}

	/**
	 * Activate old package version
	 *
	 * @param packageId package id
	 * @param version   activate version
	 *
	 * @return complete result
	 */
	@GET
	@Path("/package/activate/{packageId}/{version}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse activatePackageVersion(@PathParam("packageId") Long packageId, @PathParam("version") Integer version) {
		return OPFEngine.RegistryService.activatePackageVersion(packageId, version);
	}

	/**
	 * Supported other  version in current
	 *
	 * @param packageId package id
	 * @param version   supported version
	 *
	 * @return complete result
	 */
	@GET
	@Path("/package/support/{packageId}/{version}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse supportPackageVersion(@PathParam("packageId") Long packageId, @PathParam("version") Integer version) {
		return OPFEngine.RegistryService.supportPackageVersion(packageId, version);
	}

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
	 *
	 * @throws java.io.IOException can't serialize response  data
	 */
	@POST
	@Path("/package/version/{packageId}/{fileType: (xml|war|zip)}")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    //todo: not valid in terms of OPF action abstraction
	public Response uploadPackageVersionXML(
			@PathParam("fileType") String fileType,
			@PathParam("packageId") Long packageId,
			@FormDataParam("file") InputStream inputStream) throws IOException {
		ServiceResponse response = OPFEngine.RegistryService.uploadPackageVersionXML(packageId, fileType, inputStream);

		ObjectMapper mapper = new ObjectMapper();
		String responderData = mapper.writeValueAsString(response);

		return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
	}

	public ServiceResponse<PackageVersion> uploadPackageVersionXML(String fileType, Long packageId, FileInfo info) {
		return OPFEngine.RegistryService.uploadPackageVersionXML(packageId, fileType, info.getStream());
	}

    /**
     * This should associate a database with the appropriate package in the registry.
     *
     * @param packageId  package id
     * @param databaseId database id
     *
     * @return complete result
     */
    @POST
    @Path("/package/associate-database/{packageId}/{databaseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse associateDatabase(@PathParam("packageId") Long packageId, @PathParam("databaseId") Long databaseId) {
        return OPFEngine.RegistryService.associateDatabase(packageId, databaseId);
    }

    /**
	 * @param registryNodeId - Long
	 *
	 * @return - PackageVO
	 */
	@GET
	@Path("/reverse-engineering/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Package> reverseEngineering(@PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.RegistryService.reverseEngineering(registryNodeId);
	}

//    READ-ONE-REQUEST:
//    /rest/registry/relationship/[id] --> GET method

	/**
	 * Read relationship  by id
	 *
	 * @param id relationship id
	 *
	 * @return founded relationship
	 */
	@GET
	@Path("/relationship/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Relationship> readRelationship(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readRelationship(id);
	}

//    READ-ALL-REQUEST:
//    /rest/registry/relationship --> GET method

	/**
	 * Read all  relationships
	 *
	 * @return founded relationships
	 */
	@GET
	@Path("/relationship")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Relationship> readAllRelationships() {
		return OPFEngine.RegistryService.readAllRelationships();
	}

//    CREATE-REQUEST:
//    /rest/registry/relationship --> POST method

	/**
	 * Create new relationship
	 *
	 * @param request relationship data
	 *
	 * @return created relationship
	 */
	@POST
	@Path("/relationship")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createRelationship(ServiceRequest<Relationship> request) {
		return OPFEngine.RegistryService.createRelationship(request.getData());
	}

//    UPDATE-REQUEST:
//    /rest/registry/relationship/[id] --> PUT method

	/**
	 * Updated relationship by id
	 *
	 * @param id relationship id
	 * @param request        relationship data
	 *
	 * @return updated relationship
	 */
	@PUT
	@Path("/relationship/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateRelationship(
			@PathParam("id") Long id, ServiceRequest<Relationship> request) {
		return OPFEngine.RegistryService.updateRelationship(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/relationship/[id] --> DELETE method

	/**
	 * Delete relationship by id
	 *
	 * @param id relationship id
	 *
	 * @return deleted relationship
	 */
	@DELETE
	@Path("/relationship/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Relationship> deleteRelationship(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteRelationship(id);
	}

	/**
	 * Service retrieves the root_domain
	 *
	 * @param id - ID of the root_domain
	 *
	 * @return found root_domain
	 */
	@GET
	@Path("/root_domain/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RootDomain> readRootDomain(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readRootDomain(id);
	}

	/**
	 * Read all root domains
	 *
	 * @return founded root domains
	 */
	@GET
	@Path("/root_domain")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RootDomain> readAllRootDomains() {
		return OPFEngine.RegistryService.readAllRootDomains();
	}

//    CREATE-REQUEST
//    /rest/registry/domain --> POST method

	/**
	 * Create new root domain
	 *
	 * @param request root domain data
	 *
	 * @return created root domain
	 */
	@POST
	@Path("/root_domain")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createRootDomain(ServiceRequest<RootDomain> request) {
		return OPFEngine.RegistryService.createRootDomain(request.getData());
	}

//    UPDATE-REQUEST
//    /rest/registry/domain/[id] --> PUT method

	/**
	 * Update root domain by id
	 *
	 * @param id domain id
	 * @param request  root domain data
	 *
	 * @return updated root domain
	 */
	@PUT
	@Path("/root_domain/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateRootDomain(
			@PathParam("id") Long id, ServiceRequest<RootDomain> request) {
		return OPFEngine.RegistryService.updateRootDomain(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/domain/[id] --> DELETE method

	/**
	 * Delete root domain
	 *
	 * @param id root domain id
	 *
	 * @return deleted root domain
	 */
	@DELETE
	@Path("/root_domain/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RootDomain> deleteRootDomain(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteRootDomain(id);
	}

	//    READ-ONE-REQUEST:
//    /rest/registry/system/[id] --> GET method

	/**
	 * Restart self system
	 *
	 * @return restart status
	 */
	@GET
	@Path("/system/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<System> readSystem(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readSystem(id);
	}

	@GET
	@Path("/system/status/war/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<WebArchive> readWarStatus(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readWarStatus(id);
	}

	/**
	 * Restart self system
	 *
	 * @return restart status
	 */
	@GET
	@Path("/system/restart")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse restart() {
		return OPFEngine.RegistryService.restart();
	}

    /**
	 * Ping self system
	 *
	 * @return ping status
	 */
	@GET
	@Path("/system/ping")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse ping() {
		return OPFEngine.RegistryService.ping();
	}


	/**
	 * Read all system
	 *
	 * @return founded systems
	 */
	@GET
	@Path("/system")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<System> readAllSystems(@QueryParam("aliases") Boolean aliases) {
		return OPFEngine.RegistryService.readAllSystems(aliases);
	}

//    CREATE-REQUEST
//    /rest/registry/system --> POST method

	/**
	 * Create new system
	 *
	 * @param request system data
	 *
	 * @return created system
	 */
	@POST
	@Path("/system")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createSystem(ServiceRequest<System> request) {
		return OPFEngine.RegistryService.createSystem(request.getData());
	}

//    UPDATE-REQUEST
//    /rest/registry/system/[id] --> PUT method

	/**
	 * Update system by id
	 *
	 * @param id system id
	 * @param request  system data
	 *
	 * @return updated system
	 */
	@PUT
	@Path("/system/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateSystem(
			@PathParam("id") Long id, ServiceRequest<System> request) {
		return OPFEngine.RegistryService.updateSystem(id, request.getData());
	}

//    DELETE-REQUEST:
//    /rest/registry/system/[id] --> DELETE method

	/**
	 * Delete system by id
	 *
	 * @param id system id
	 *
	 * @return deleted system
	 */
	@DELETE
	@Path("/system/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<System> deleteSystem(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteSystem(id);
	}

//    ASSOCIATE-PACKAGE
//    /platform/registry/registry-node/system/[system-id]/package/[package-id] - POST Method
//    This should associate a package with the appropriate system in the registry and set all package server names to the system server name.

    /**
     * Associate package on system
     *
     * @param systemId  system id
     * @param packageId package id
     *
     * @return complete result
     */
    @POST
    @Path("/system/installed-package/{systemId}/{packageId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse associatePackage(@PathParam("systemId") Long systemId, @PathParam("packageId") Long packageId) {
        return OPFEngine.RegistryService.associatePackage(systemId, packageId);
    }

//    REMOVE-ASSOCIATION
//    ASSOCIATE-PACKAGE
//    /platform/registry/registry-node/system/[system-id]/package/[package-id] - DELETE Method

    /**
     * Remove associate package with system
     *
     * @param systemId  system id
     * @param packageId package id
     *
     * @return complete result
     */
    @DELETE
//	@Path("/registry-node/system/{systemId}/package/{packageId}")
    @Path("/system/installed-package/{systemId}/{packageId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse removeAssociationPackage(@PathParam("systemId") Long systemId, @PathParam("packageId") Long packageId) {
        return OPFEngine.RegistryService.removeAssociationPackage(systemId, packageId);
    }

	/**
	 * Export system environments
	 *
	 * @param rootDomainId root domain id
	 * @param filename     download file name
	 *
	 * @return stream data
	 */
	@GET
	@Path("/system/export/{name}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
    //todo: not valid in terms of OPF action abstraction
	public StreamingOutput exportXml(
			@QueryParam("rootDomainId") final Long rootDomainId,
			@PathParam("name") final String filename) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException, WebApplicationException {
				InputStream stream = OPFEngine.RegistryService.exportXml(rootDomainId, filename);
				if (stream != null) {
					IOUtils.copy(stream, output);
					IOUtils.closeQuietly(stream);
				}
			}
		};
	}

	public ServiceResponse<FileInfo> exportXmlWS(Long rootDomainId, String filename) {
		InputStream stream = OPFEngine.RegistryService.exportXml(rootDomainId, filename);
		return new ServiceResponse<FileInfo>(new FileInfo(filename, stream), "Export xml successfully", true);
	}

	/**
	 * Upload system environments
	 *
	 * @param rootDomainId root domain rootDomainId
	 * @param inputStream  stream data
	 *
	 * @return complete result
	 *
	 * @throws java.io.IOException can't serialize response data
	 */
	@POST
	@Path("/system/import")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    //todo: not valid in terms of OPF action abstraction
	public Response importXml(@QueryParam("rootDomainId") Long rootDomainId, @FormDataParam("file") InputStream inputStream)
            throws IOException {

		ServiceResponse response = OPFEngine.RegistryService.importXml(rootDomainId, inputStream);

		ObjectMapper mapper = new ObjectMapper();
		String responderData = mapper.writeValueAsString(response);

		return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
	}

	public ServiceResponse importXml(Long rootDomainId, FileInfo info) {
		return OPFEngine.RegistryService.importXml(rootDomainId, info.getStream());
	}

	/**
	 * Service retrieves the database
	 *
	 * @param id - ID of the database
	 *
	 * @return found database
	 */
	@GET
	@Path("/database/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Database> readDatabase(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readDatabase(id);
	}

	/**
	 * Service searches the databases by associated package
	 *
	 * @param packageId - ID of the package
	 *
	 * @return list of found databases
	 */
	@GET
	@Path("/database/associated-system/package/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Database> readDatabasesBySystem(@PathParam("packageId") Long packageId) {
		return OPFEngine.RegistryService.readDatabasesBySystem(packageId);
	}

	/**
	 * Service searches the databases by associated package
	 *
	 * @param packageId - ID of the package
	 *
	 * @return list of found databases
	 */
	@GET
	@Path("/database/associated-package/{packageId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Database> readDatabasesByPackage(@PathParam("packageId") Long packageId) {
		return OPFEngine.RegistryService.readDatabasesByPackage(packageId);
	}

    /**
   	 * Service searches the not-associated databases
   	 *
   	 * @return list of found databases
   	 */
   	@GET
   	@Path("/database/not-associated")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Database> readNotAssociatedDatabases() {
   		return OPFEngine.RegistryService.readNotAssociatedDatabases();
   	}

	/**
	 * Service inserts the database
	 *
	 * @param request - request containing the database to be inserted
	 *
	 * @return registry tree node with newly created database
	 */
	@POST
	@Path("/database")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createDatabase(ServiceRequest<Database> request) {
		return OPFEngine.RegistryService.createDatabase(request.getData());
	}

	/**
	 * Service modifies the database
	 *
	 * @param id - ID of the database
	 * @param request    - request containing the database to be modified
	 *
	 * @return registry tree node with modified database
	 */
	@PUT
	@Path("/database/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateDatabase(
			@PathParam("id") Long id, ServiceRequest<Database> request) {
		return OPFEngine.RegistryService.updateDatabase(id, request.getData());
	}

	/**
	 * Service removes the database
	 *
	 * @param id - ID of the database to be removed
	 *
	 * @return database value object
	 */
	@DELETE
	@Path("/database/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Database> deleteDatabase(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteDatabase(id);
	}

	/**
	 * Service reads the filestore
	 *
	 * @param id - ID of the filestore
	 *
	 * @return read filestore
	 */
	@GET
	@Path("/filestore/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Filestore> readFilestore(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readFilestore(id);
	}

	/**
	 * Service creates the filestore
	 *
	 * @param request - request containing the filestore to be created
	 *
	 * @return registry node tree for filestore
	 */
	@POST
	@Path("/filestore")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createFilestore(ServiceRequest<Filestore> request) {
		return OPFEngine.RegistryService.createFilestore(request.getData());
	}

	/**
	 * Service updates the filestore
	 *
	 * @param id - ID of the filestore
	 * @param request     - request containing the filestore to be updated
	 *
	 * @return registry node tree for filestore
	 */
	@PUT
	@Path("/filestore/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateFilestore(
			@PathParam("id") Long id, ServiceRequest<Filestore> request) {
		return OPFEngine.RegistryService.updateFilestore(id, request.getData());
	}

	/**
	 * Service deletes the filestore
	 *
	 * @param id - ID of the filestore to be deleted
	 *
	 * @return filestore value object
	 */
	@DELETE
	@Path("/filestore/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Filestore> deleteFilestore(@PathParam(value = "id") Long id) {
		return OPFEngine.RegistryService.deleteFilestore(id);
	}

	/**
	 * Read server by id
	 *
	 * @param id server id
	 *
	 * @return founded server
	 */
	@GET
	@Path("/server/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Server> readServer(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readServer(id);
	}

	/**
	 * Create new Server
	 *
	 * @param request server data
	 *
	 * @return created server
	 */
	@POST
	@Path("/server")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createServer(ServiceRequest<Server> request) {
		return OPFEngine.RegistryService.createServer(request.getData());
	}

	/**
	 * Update server  by id
	 *
	 * @param id server id
	 * @param request  server data
	 *
	 * @return updated server
	 */
	@PUT
	@Path("/server/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateServer(@PathParam("id") Long id, ServiceRequest<Server> request) {
		return OPFEngine.RegistryService.updateServer(id, request.getData());
	}

	/**
	 * Delete server by id
	 *
	 * @param id server id
	 *
	 * @return deleted server
	 */
	@DELETE
	@Path("/server/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Server> deleteServer(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.deleteServer(id);
	}

    @POST
    @Path("/register-node")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse registerServerNode(ServiceRequest<ServerNodeConfig> request) {
        return OPFEngine.RegistryService.registerServerNode(request.getData());
    }

	/**
	 * registration Slave Nodes
	 *
	 * @param config configuration slave node
	 * @return server environments
	 */
	@POST
	@Path("/slave")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public StreamingOutput registerSlaveNode(final ServiceRequest<ServerNodeConfig> config) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException, WebApplicationException {
				InputStream env = OPFEngine.RegistryService.registerSlaveNode(config.getData());
				if (env != null) {
					IOUtils.copy(env, output);
					IOUtils.closeQuietly(env);
				}
			}
		};
	}

	@GET
	@Path("/report/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Report> readReport(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.readReport(id);
	}

	@POST
	@Path("/report")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> createReport(ServiceRequest<Report> request) {
		return OPFEngine.RegistryService.createReport(request.getData());
	}

	@PUT
	@Path("/report/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<RegistryNodeTree> updateReport(@PathParam("id") Long id, ServiceRequest<Report> request) {
		return OPFEngine.RegistryService.updateReport(id, request.getData());
	}

	@DELETE
	@Path("/report/{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Report> deleteReport(@PathParam("id") Long id) {
		return OPFEngine.RegistryService.deleteReport(id);
	}

    @GET
   	@Path("/wizard/{id}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Wizard> readWizard(@PathParam("id") Long id) {
   		return OPFEngine.RegistryService.readWizard(id);
   	}

   	@POST
   	@Path("/wizard")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<RegistryNodeTree> createWizard(ServiceRequest<Wizard> request) {
   		return OPFEngine.RegistryService.createWizard(request.getData());
   	}

   	@PUT
   	@Path("/wizard/{id}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<RegistryNodeTree> updateWizard(@PathParam("id") Long id, ServiceRequest<Wizard> request) {
   		return OPFEngine.RegistryService.updateWizard(id, request.getData());
   	}

   	@DELETE
   	@Path("/wizard/{id}")
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Wizard> deleteWizard(@PathParam("id") Long id) {
   		return OPFEngine.RegistryService.deleteWizard(id);
   	}


    @GET
    @Path("/license")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<License> readLicense() {
        return OPFEngine.RegistryService.readLicense();
    }

    @POST
    @Path("/license")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response verifyLicense(@FormDataParam("file") InputStream inputStream) throws IOException {
        ServiceResponse<License> response = OPFEngine.RegistryService.verify(inputStream);

        ObjectMapper mapper = new ObjectMapper();
        String responderData = mapper.writeValueAsString(response);

        return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
    }

    public ServiceResponse verifyLicenseWS(FileInfo info) {
        return OPFEngine.RegistryService.verify(info.getStream());
    }

    @GET
    @Path("/social/{packageLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Social> socialEnabled(@PathParam("packageLookup") String packageLookup) {
        return OPFEngine.RegistryService.socialEnabled(packageLookup);
    }

    @GET
   	@Path("/bi/report/{id}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReport> readBIReport(@PathParam("id") Long id) {
   		return OPFEngine.RegistryService.readBIReport(id);
   	}

    @GET
   	@Path("/bi/report-user/{id}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReportUser> readBIReportUser(@PathParam("id") Long id) {
   		return OPFEngine.RegistryService.readBIReportUser(id);
   	}

    @DELETE
   	@Path("/bi/report-user/{id}")
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReportUser> deleteBIReportUser(@PathParam("id") Long id) {
   		return OPFEngine.RegistryService.deleteBIReportUser(id);
   	}

    @GET
   	@Path("/bi/report-user/allow/{lookup}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReportUser> readAllowBIReportUser(@PathParam("lookup") String packageLookup) {
   		return OPFEngine.RegistryService.readAllowBIReportUser(packageLookup);
   	}

    @GET
   	@Path("/bi/report/lookup/{lookup}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReport> readBIReportByLookup(@PathParam("lookup") String lookup) {
   		return OPFEngine.RegistryService.readBIReportByLookup(lookup);
   	}

    @POST
   	@Path("/bi/report")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<RegistryNodeTree> createBIReport(ServiceRequest<BIReport> request) {
   		return OPFEngine.RegistryService.createBIReport(request.getData());
   	}

    @POST
   	@Path("/bi/report-user")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<BIReportUser> createBIReportUser(ServiceRequest<BIReportUser> request) {
   		return OPFEngine.RegistryService.createBIReportUser(request.getData());
   	}

    @PUT
    @Path("/bi/report-user/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<BIReportUser> updateBIReportUser(@PathParam("id") Long id, ServiceRequest<BIReportUser> request) {
        return OPFEngine.RegistryService.updateBIReportUser(id, request.getData());
    }
}