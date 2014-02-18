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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.web.handler.Builder;
import net.firejack.platform.web.handler.ErrorHandler;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class RegistryServiceProxy extends AbstractServiceProxy implements IRegistryService {

	public RegistryServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
	public String getServiceUrlSuffix() {
		return "/registry";
	}

	@Override
	public ServiceResponse<CheckUrl> testSystemStatus(CheckUrl data) {
		return post("/url/check", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> getRegistryNodeChildren(Long registryNodeId, PageType pageType, String packageLookup) {
		return get("/children/" + registryNodeId, "pageType", pageType, "packageLookup", packageLookup);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByIdChildren(Long registryNodeId, PageType pageType) {
		return get("/children/expanded-by-id/" + registryNodeId, "pageType", pageType);
	}

    @Override
    public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByLookupChildren(String registryNodeLookup, PageType pageType) {
        return get("/children/expanded-by-lookup/" + registryNodeLookup, "pageType", pageType);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> getRegistryNodeFieldsByEntity(Long entityId) {
        return get("/entity-fields/" + entityId);
    }

    @Override
	public ServiceResponse<MoveRegistryNodeTree> moveRegistryNode(MoveRegistryNodeTree moveRegistryNodeTree) {
		return put("/change/position", moveRegistryNodeTree);
	}

	@Override
	public ServiceResponse<Search> getSearchResult(String term, Long registryNodeId, String lookup, String assetType, Paging paging) {
		return get("/search",
				"term", term,
				"registryNodeId", registryNodeId,
				"lookup", lookup,
				"assetType", assetType,
				"start", paging.getOffset(),
				"limit", paging.getLimit(),
				"sortColumn", paging.getSortColumn(),
				"sortDirection", paging.getSortDirection());
	}

    @Override
    public ServiceResponse<FileTree> readDirectory(String path, Boolean directoryOnly) {
        return get("/filemanager/directory", "path", path, "directoryOnly", directoryOnly);
    }

    @Override
	public ServiceResponse<Action> readAction(Long actionId) {
		return get("/action/" + actionId);
	}

	@Override
	public ServiceResponse<Action> readAllActions() {
		return get("/action");
	}

	@Override
	public ServiceResponse<Action> readActionsFromCache(String packageLookup) {
		ServiceResponse<Action> response;
		if (StringUtils.isBlank(packageLookup)) {
			response = new ServiceResponse<Action>("packageLookup parameter should not be blank", false);
		} else {
			response = get("/action/cached/" + packageLookup);
		}
		return response;
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createAction(Action action) {
		return post2("/action", action);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateAction(Long actionId, Action action) {
		return put2("/action/" + actionId, action);
	}

	@Override
	public ServiceResponse<Action> deleteAction(Long actionId) {
		return delete("/action/" + actionId);
	}

	@Override
	public ServiceResponse associatePackage(Long systemId, Long packageId) {
		return post("/system/installed-package/" + systemId + "/" + packageId);
	}

	@Override
	public ServiceResponse removeAssociationPackage(Long systemId, Long packageId) {
		return delete("/system/installed-package/" + systemId + "/" + packageId);
	}

	@Override
	public ServiceResponse<Domain> readDomain(Long domainId) {
		return get("/domain/" + domainId);
	}

	@Override
	public ServiceResponse<Domain> readAllDomains() {
		return get("/domain");
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createDomain(Domain data) {
		return post2("/domain", data);
	}

    @Override
    public ServiceResponse<Domain> createDomainByParentLookup(Domain data) {
        return post("/domain/parent-lookup", data);
    }

    @Override
    public ServiceResponse<Domain> createDomainDatabase(Domain data, Boolean reverseEngineer) {
        return post2("/domain", data, "reverseEngineer", reverseEngineer);
    }

    @Override
	public ServiceResponse<RegistryNodeTree> updateDomain(Long domainId, Domain data) {
		return put2("/domain/" + domainId, data);
	}

	@Override
	public ServiceResponse<Domain> deleteDomain(Long domainId) {
		return delete("/domain/" + domainId);
	}

    @Override
    public ServiceResponse<Domain> readDomainsByPackageLookup(String lookup) {
        return get("/domains/by-lookup/" + lookup);
    }

    @Override
	public ServiceResponse<Entity> readEntity(Long entityId) {
		return get("/entity/" + entityId);
	}

    @Override
    public ServiceResponse<Entity> readEntityByLookup(String entityLookup) {
        ServiceResponse<Entity> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<Entity>("Lookup parameter should not be blank.", false);
        } else {
            response = get("/entity/by-lookup/" + entityLookup);
        }
        return response;
    }

    @Override
    public ServiceResponse<Entity> searchEntityByDomain(String terms, Long domainId, String packageLookup) {
        return get("/entity/search", "terms", terms, "domainId", domainId, "packageLookup", packageLookup);
    }

    @Override
    public ServiceResponse<Entity> readParentEntityTypesByEntityLookup(String entityLookup) {
        ServiceResponse<Entity> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<Entity>("Lookup parameter should not be blank.", false);
        } else {
            response = get("/entity/parent-types/by-lookup/" + entityLookup);
        }
        return response;
    }

    @Override
    public ServiceResponse<Entity> readDirectChildrenTypes(String entityLookup) {
        ServiceResponse<Entity> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<Entity>("Lookup parameter should not be blank.", false);
        } else {
            response = get("/entity/direct-children-types-by-lookup", "entityLookup", entityLookup);
        }
        return response;
    }

    @Override
    public ServiceResponse<Entity> readEntitiesUpInHierarchyByLookup(String entityLookup) {
        ServiceResponse<Entity> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<Entity>("Lookup parameter should not be blank.", false);
        } else {
            try {
                response = get("/entity/hierarchy-types-up/by-lookup/" + entityLookup);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                response = new ServiceResponse<Entity>(e.getMessage(), false);
            }
        }
        return response;
    }

    @Override
	public ServiceResponse<Entity> readAllEntities(String type, Long exceptId) {
		return get("/entity/" + type, "exceptId", exceptId);
	}

    @Override
	public ServiceResponse<Entity> readEntitiesByPackageLookup(String lookup) {
		return get("/entities/by-lookup/" + lookup);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createEntity(Entity data) {
		return post2("/entity", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateEntity(Long entityId, Entity data) {
		return put2("/entity/" + entityId, data);
	}

	@Override
	public ServiceResponse<Entity> deleteEntity(Long entityId) {
		return delete("/entity/" + entityId);
	}

    @Override
    public ServiceResponse<Form> createForm(Form data) {
        return post("/form", data);
    }

    @Override
    public ServiceResponse<SecuredEntity> readSecuredEntitiesByType(Long entityTypeId) {
        ServiceResponse<SecuredEntity> response;
        if (entityTypeId == null) {
            response = new ServiceResponse<SecuredEntity>("Type lookup should not be blank.", false);
        } else {
            response = get("/entity/by-type/" + entityTypeId);
        }
        return response;
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> getSecurityEnabledFlagForEntity(String entityLookup) {
        ServiceResponse<SimpleIdentifier<Boolean>> response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse<SimpleIdentifier<Boolean>>("entityLookup parameter should not be blank.", false);
        } else {
            response = get("/entity/security-enabled-by-lookup", "entityLookup", entityLookup);
        }
        return response;
    }

    @Override
	public ServiceResponse<UploadPackageArchive> uploadPackageArchive(InputStream stream) {
		return upload0("/installation/package/upload", stream, UploadPackageArchive.class);
	}

	@Override
	public ServiceResponse performPackageArchive(String uploadedFilename, String versionName, Boolean doAsCurrent, Boolean doArchive) {
		return get("/installation/package/perform", "uploadedFilename", uploadedFilename, "versionName", versionName, "doAsCurrent", doAsCurrent, "doArchive", doArchive);
	}

	@Override
	public ServiceResponse checkUniquePackageVersion(Long packageId, String versionName) {
		return get("/installation/package/check/unique/version/" + packageId, "versionName", versionName);
	}

	@Override
	public ServiceResponse installPackageArchive(PackageAction packageAction) {
		return post2("/installation/package/install", packageAction);
	}

	@Override
	public ServiceResponse migrateDatabase(PackageAction packageAction) {
		return post2("/installation/package/migrate", packageAction);
	}

	@Override
	public ServiceResponse uninstallPackageArchive(PackageAction packageAction) {
		return post2("/installation/package/uninstall", packageAction);
	}

	@Override
	public ServiceResponse<Package> readPackage(Long packageId) {
		return get("/package/" + packageId);
	}

	@Override
	public ServiceResponse<Package> readAllPackages() {
		return get("/package");
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createPackage(Package data) {
		return post2("/package", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updatePackage(Long packageId, Package data) {
		return put2("/package/" + packageId, data);
	}

	@Override
	public ServiceResponse<Package> deletePackage(Long packageId) {
		return delete("/package/" + packageId);
	}

	@Override
	public InputStream getPackageArchive(Long packageId, String packageFilename) {
		return getStream("/package/download/" + packageId + "/" + packageFilename);
	}

	@Override
	public ServiceResponse<PackageVersion> generateWar(Long packageId) {
		return get("/package/generate/code/" + packageId);
	}

	@Override
	public ServiceResponse<PackageVersion> generateUpgradeXml(Long packageId, Integer version) {
		return get("/package/generate/upgrade/" + packageId + "/" + version);
	}

	@Override
	public ServiceResponse<PackageVersionInfo> getPackageVersionsInfo(Long packageId) {
		return get("/package/versions/" + packageId);
	}

	@Override
	public ServiceResponse<Package> lockPackageVersion(Long packageId, PackageVersionInfo data) {
		return put2("/package/lock/" + packageId, data);
	}

	@Override
	public ServiceResponse<PackageVersion> archive(Long packageId) {
		return get("/package/archive/" + packageId);
	}

	@Override
	public ServiceResponse<Package> deletePackageVersion(Long packageId, Integer version) {
		return delete("/package/version/" + packageId + "/" + version);
	}

	@Override
	public ServiceResponse activatePackageVersion(Long packageId, Integer version) {
		return get("/package/activate/" + packageId + "/" + version);
	}

	@Override
	public ServiceResponse supportPackageVersion(Long packageId, Integer version) {
		return get("/package/support/" + packageId + "/" + version);
	}

	@Override
	public ServiceResponse<PackageVersion> uploadPackageVersionXML(Long packageId, String fileType, InputStream inputStream) {
		return upload0("/package/version/" + packageId + "/" + fileType, inputStream, PackageVersion.class);
	}

    @Override
    public ServiceResponse associateDatabase(Long packageId, Long databaseId) {
        return post("/package/associate-database/" + packageId + "/" + databaseId);
    }

    @Override
    public ServiceResponse reverseEngineering(Long registryNodeId) {
        return get("/reverse-engineering/" + registryNodeId);
    }

    @Override
	public ServiceResponse<Relationship> readRelationship(Long relationshipId) {
		return get("/relationship/" + relationshipId);
	}

	@Override
	public ServiceResponse<Relationship> readAllRelationships() {
		return get("/relationship");
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createRelationship(Relationship data) {
		return post2("/relationship", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateRelationship(Long relationshipId, Relationship data) {
		return put2("/relationship/" + relationshipId, data);
	}

	@Override
	public ServiceResponse<Relationship> deleteRelationship(Long relationshipId) {
		return delete("/relationship/" + relationshipId);
	}

	@Override
	public ServiceResponse<RootDomain> readRootDomain(Long rootDomainId) {
		return get("/root_domain/" + rootDomainId);
	}

	@Override
	public ServiceResponse<RootDomain> readAllRootDomains() {
		return get("/root_domain");
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createRootDomain(RootDomain data) {
		return post2("/root_domain", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateRootDomain(Long domainId, RootDomain data) {
		return put2("/root_domain/" + domainId, data);
	}

	@Override
	public ServiceResponse<RootDomain> deleteRootDomain(Long domainId) {
		return delete("/root_domain/" + domainId);
	}

	@Override
	public ServiceResponse<System> readSystem(Long systemId) {
		return get("/system/" + systemId);
	}

	@Override
	public ServiceResponse<WebArchive> readWarStatus(Long systemId) {
		return get("/system/status/war/" + systemId);
	}

	@Override
	public ServiceResponse<System> readAllSystems(Boolean aliases) {
		return get("/system", "aliases", aliases);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createSystem(System data) {
		return post2("/system", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateSystem(Long systemId, System data) {
		return put2("/system/" + systemId, data);
	}

	@Override
	public ServiceResponse<System> deleteSystem(Long systemId) {
		return delete("/system/" + systemId);
	}

	@Override
	public InputStream exportXml(Long rootDomainId, String name) {
		return getStream("/system/export/" + name, "rootDomainId", rootDomainId);
	}

	@Override
	public ServiceResponse importXml(Long rootDomainId, InputStream stream) {
		return upload0("/system/import", stream, null, "rootDomainId", rootDomainId);
	}

	@Override
	public ServiceResponse<Database> readDatabase(Long databaseId) {
		return get("/database/" + databaseId);
	}

	@Override
	public ServiceResponse<Database> readDatabasesBySystem(Long packageId) {
		return get("/database/associated-system/package/" + packageId);
	}

	@Override
	public ServiceResponse<Database> readDatabasesByPackage(Long packageId) {
		return get("/database/associated-package/" + packageId);
	}

    @Override
    public ServiceResponse<Database> readNotAssociatedDatabases() {
        return get("/database/not-associated");
    }

    @Override
	public ServiceResponse<RegistryNodeTree> createDatabase(Database data) {
		return post2("/database", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateDatabase(Long databaseId, Database data) {
		return put2("/database/" + databaseId, data);
	}

	@Override
	public ServiceResponse<Database> deleteDatabase(Long databaseId) {
		return delete("/database/" + databaseId);
	}

	@Override
	public ServiceResponse<Filestore> readFilestore(Long filestoreId) {
		return get("/filestore/" + filestoreId);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createFilestore(Filestore data) {
		return post2("/filestore", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateFilestore(Long filestoreId, Filestore data) {
		return put2("/filestore/" + filestoreId, data);
	}

	@Override
	public ServiceResponse<Filestore> deleteFilestore(Long filestoreId) {
		return delete("/filestore/" + filestoreId);
	}

	@Override
	public ServiceResponse<Server> readServer(Long serverId) {
		return get("/server/" + serverId);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createServer(Server data) {
		return post2("/server", data);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateServer(Long serverId, Server data) {
		return put2("/server/" + serverId, data);
	}

	@Override
	public ServiceResponse<Server> deleteServer(Long serverId) {
		return delete("/server/" + serverId);
	}

    @Override
    public ServiceResponse registerServerNode(ServerNodeConfig nodeConfig) {
        return post2("/register-node", nodeConfig);
    }

	@Override
	public InputStream registerSlaveNode(ServerNodeConfig nodeConfig) {
        InputStream stream;
        String path = "/slave";
        if (OpenFlameSecurityConstants.isSiteMinderAuthSupported() &&
                StringUtils.isNotBlank(OpenFlameSecurityConstants.getOpfDirectUrl())) {
            String url = OpenFlameSecurityConstants.getOpfDirectUrl() + REST_API_URL_SUFFIX + getServiceUrlSuffix() + path;

            WebResource webResource = Client.create(config).resource(url);
            webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
            webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);//MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.APPLICATION_XML_TYPE
            WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .type(MediaType.APPLICATION_XML_TYPE);

            Builder proxy = ErrorHandler.getProxy(builder);
            addCookie(proxy);
            addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
            addClientIpInfo(proxy);
            stream = doPost(proxy, InputStream.class, new ServiceRequest<ServerNodeConfig>(nodeConfig));
        } else {
            stream = getStream(path, nodeConfig);
        }
		return stream;
	}

	@Override
	public ServiceResponse restart() {
		return get("/system/restart");
	}

    @Override
    public ServiceResponse ping() {
        return get("/system/ping");
    }

	@Override
	public ServiceResponse<Report> readReport(Long reportId) {
		return get("/report/" + reportId);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createReport(Report report) {
		return post2("/report", report);
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateReport(Long reportId, Report report) {
		return post2("/report/" + reportId, report);
	}

	@Override
	public ServiceResponse<Report> deleteReport(Long reportId) {
		return delete("/report/" + reportId);
	}

    @Override
   	public ServiceResponse<Wizard> readWizard(Long wizardId) {
   		return get("/wizard/" + wizardId);
   	}

   	@Override
   	public ServiceResponse<RegistryNodeTree> createWizard(Wizard wizard) {
   		return post2("/wizard", wizard);
   	}

   	@Override
   	public ServiceResponse<RegistryNodeTree> updateWizard(Long wizardId, Wizard wizard) {
   		return post2("/wizard/" + wizardId, wizard);
   	}

   	@Override
   	public ServiceResponse<Wizard> deleteWizard(Long wizardId) {
   		return delete("/wizard/" + wizardId);
   	}

    @Override
    public ServiceResponse redeployPackageArchive(String packageLookup) {
        return get("/installation/package/redeploy/" + packageLookup);
    }

    @Override
    public ServiceResponse checkName(String path, String name, DomainType type) {
        return get("/check/" + path + "/" + name + "/" + type);
    }

    @Override
    public ServiceResponse<License> readLicense() {
        return get("/license");
    }

    @Override
    public ServiceResponse<License> verify(InputStream stream) {
        return upload0("/license", stream, null);
    }

    @Override
    public ServiceResponse<Social> socialEnabled(String packageLookup) {
        return get("/social/" + packageLookup);
    }

    @Override
   	public ServiceResponse<BIReport> readBIReport(Long reportId) {
   		return get("/bi/report/" + reportId);
   	}

    @Override
   	public ServiceResponse<BIReportUser> readBIReportUser(Long reportId) {
   		return get("/bi/report-user/" + reportId);
   	}

    @Override
    public ServiceResponse<BIReportUser> deleteBIReportUser(Long reportId) {
        return delete("/bi/report-user/" + reportId);
    }

    public ServiceResponse<BIReportUser> readAllowBIReportUser(String packageLookup) {
        return get("/bi/report-user/allow/" + packageLookup);
    }

    public ServiceResponse<BIReport> readBIReportByLookup(String lookup) {
        return get("/bi/report/lookup/" + lookup);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createBIReport(BIReport report) {
        return post2("/bi/report", report);
    }

    @Override
    public ServiceResponse<BIReportUser> createBIReportUser(BIReportUser report) {
        return post2("/bi/report-user", report);
    }

    @Override
    public ServiceResponse<BIReportUser> updateBIReportUser(Long id, BIReportUser report) {
        return post2("/bi/report-user/" + id, report);
    }
}
