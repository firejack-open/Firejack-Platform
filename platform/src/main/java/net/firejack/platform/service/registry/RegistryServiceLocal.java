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

package net.firejack.platform.service.registry;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.api.registry.IRegistryService;
import net.firejack.platform.api.registry.domain.*;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.service.registry.broker.action.*;
import net.firejack.platform.service.registry.broker.associate.AssociateDatabaseBroker;
import net.firejack.platform.service.registry.broker.associate.AssociatePackageBroker;
import net.firejack.platform.service.registry.broker.associate.DeleteAssociationPackageBroker;
import net.firejack.platform.service.registry.broker.bi.*;
import net.firejack.platform.service.registry.broker.cluster.RegistrySlaveNodeBroker;
import net.firejack.platform.service.registry.broker.database.*;
import net.firejack.platform.service.registry.broker.domain.*;
import net.firejack.platform.service.registry.broker.entity.*;
import net.firejack.platform.service.registry.broker.filemanager.ReadAllDirectoryBroker;
import net.firejack.platform.service.registry.broker.filestore.CreateFilestoreBroker;
import net.firejack.platform.service.registry.broker.filestore.DeleteFilestoreBroker;
import net.firejack.platform.service.registry.broker.filestore.ReadFilestoreBroker;
import net.firejack.platform.service.registry.broker.filestore.UpdateFilestoreBroker;
import net.firejack.platform.service.registry.broker.install.*;
import net.firejack.platform.service.registry.broker.license.ReadLicenseBroker;
import net.firejack.platform.service.registry.broker.license.VerifyLicenseBroker;
import net.firejack.platform.service.registry.broker.package_.*;
import net.firejack.platform.service.registry.broker.package_.version.UploadPackageVersionFileBroker;
import net.firejack.platform.service.registry.broker.registry.*;
import net.firejack.platform.service.registry.broker.relationship.*;
import net.firejack.platform.service.registry.broker.report.CreateReportBroker;
import net.firejack.platform.service.registry.broker.report.DeleteReportBroker;
import net.firejack.platform.service.registry.broker.report.ReadReportBroker;
import net.firejack.platform.service.registry.broker.report.UpdateReportBroker;
import net.firejack.platform.service.registry.broker.server.CreateServerBroker;
import net.firejack.platform.service.registry.broker.server.DeleteServerBroker;
import net.firejack.platform.service.registry.broker.server.ReadServerBroker;
import net.firejack.platform.service.registry.broker.server.UpdateServerBroker;
import net.firejack.platform.service.registry.broker.system.*;
import net.firejack.platform.service.registry.broker.wizard.CreateWizardBroker;
import net.firejack.platform.service.registry.broker.wizard.DeleteWizardBroker;
import net.firejack.platform.service.registry.broker.wizard.ReadWizardBroker;
import net.firejack.platform.service.registry.broker.wizard.UpdateWizardBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.PathParam;
import java.io.InputStream;
import java.util.Date;

@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_REGISTRY_SERVICE)
public class RegistryServiceLocal implements IRegistryService {

	@Autowired
	private TestSystemStatusBroker testSystemStatusBroker;
	@Autowired
	private ReadRegistryNodeChildrenBroker readRegistryNodeChildrenListBroker;
	@Autowired
    @Qualifier("readRegistryNodeChildrenExpandedByIdBroker")
	private ReadRegistryNodeChildrenExpandedByIdBroker readRegistryNodeChildrenExpandedByIdBroker;
	@Autowired
    @Qualifier("readRegistryNodeChildrenExpandedByLookupBroker")
	private ReadRegistryNodeChildrenExpandedByLookupBroker readRegistryNodeChildrenExpandedByLookupBroker;
    @Autowired
    private ReadRegistryNodeFieldsByEntityBroker readRegistryNodeFieldsByEntityBroker;
	@Autowired
	private MoveRegistryNodeTreeBroker moveRegistryNodeTreeBroker;
	@Autowired
	private SearchListBroker searchListBroker;
    @Autowired
    private ReadAllDirectoryBroker readAllDirectoryBroker;
	@Autowired
	private ReadActionBroker readActionBroker;
	@Autowired
	private ReadActionListBroker readActionListBroker;
	@Autowired
	private GetCachedActionListBroker getCachedActionListBroker;
	@Autowired
	private CreateActionBroker createActionBroker;
	@Autowired
	private UpdateActionBroker updateActionBroker;
	@Autowired
	private DeleteActionBroker deleteActionBroker;
	@Autowired
	private AssociatePackageBroker associatePackageBroker;
	@Autowired
	private DeleteAssociationPackageBroker deleteAssociationPackageBroker;
	@Autowired
	private ReadDomainBroker readDomainBroker;
	@Autowired
	private ReadDomainListBroker readDomainListBroker;
	@Autowired
	private CreateDomainBroker createDomainBroker;
    @Autowired
	private CreateDomainByParentLookupBroker createDomainByParentLookupBroker;
    @Autowired
	private CreateDomainDatabaseBroker createDomainDatabaseBroker;
	@Autowired
	private UpdateDomainBroker updateDomainBroker;
	@Autowired
	private DeleteDomainBroker deleteDomainBroker;
    @Autowired
    private ReadDomainsByPackageLookupBroker readDomainsByPackageLookupBroker;
	@Autowired
	private ReadEntityBroker readEntityBroker;
	@Autowired
	private ReadEntityByLookupBroker readEntityByLookupBroker;
    @Autowired
    private ReadParentTypesByEntityLookupBroker readParentTypesByEntityLookupBroker;
    @Autowired
    private ReadDirectChildrenTypesBroker readDirectChildrenTypesBroker;
    @Autowired
    private ReadCachedEntitiesFromUpHierarchyBroker readEntitiesFromHierarchyBroker;
	@Autowired
	private ReadEntityListBroker readEntityListBroker;
    @Autowired
    private SearchEntityBroker searchEntityBroker;
    @Autowired
	private ReadEntitiesByPackageLookupBroker readEntitiesByPackageLookupBroker;
    @Autowired
    private ReadSecuredEntitiesByTypeBroker readSecuredEntitiesByTypeBroker;
    @Autowired
    private GetSecurityEnabledInfoBroker getSecurityEnabledInfoBroker;
	@Autowired
	private CreateEntityBroker createEntityBroker;
	@Autowired
	private UpdateEntityBroker updateEntityBroker;
	@Autowired
	private DeleteEntityBroker deleteEntityBroker;
    @Autowired
    private CreateFormBroker createFormBroker;
	@Autowired
	private UploadPackageArchiveBroker uploadPackageArchiveBroker;
	@Autowired
	private PerformPackageArchiveBroker performPackageArchiveBroker;
	@Autowired
	private CheckUniquePackageVersionBroker checkUniquePackageVersionBroker;
	@Autowired
	private InstallPackageArchiveBroker installPackageArchiveBroker;
	@Autowired
	private MigrateDatabasesBroker migrateDatabasesBroker;
	@Autowired
	private UninstallPackageArchiveBroker uninstallPackageArchiveBroker;
	@Autowired
	private ReadPackageBroker readPackageBroker;
	@Autowired
	private ReadPackageListBroker readPackageListBroker;
	@Autowired
	private CreatePackageBroker createPackageBroker;
	@Autowired
	private UpdatePackageBroker updatePackageBroker;
	@Autowired
	private DeletePackageBroker deletePackageBroker;
	@Autowired
	private DownloadGeneratedPackageBroker downloadGeneratedPackageBroker;
	@Autowired
	private GenerateWarBroker generateWarBroker;
	@Autowired
	private GenerateUpgradeXmlBroker generateUpgradeXmlBroker;
	@Autowired
	private GetPackageVersionsInfoBroker getPackageVersionsInfoBroker;
	@Autowired
	private ArchivePackageVersionBroker archivePackageVersionBroker;
	@Autowired
	private LockPackageVersionBroker lockPackageVersionBroker;
	@Autowired
	private DeletePackageVersionBroker deletePackageVersionBroker;
	@Autowired
	private ActivatePackageVersionBroker activatePackageVersionBroker;
	@Autowired
	private SupportPackageVersionBroker supportPackageVersionBroker;
	@Autowired
	private UploadPackageVersionFileBroker uploadPackageVersionFileBroker;
    @Autowired
    private AssociateDatabaseBroker associateDatabaseBroker;
    @Autowired
    private ReverseEngineeringBroker reverseEngineeringBroker;
	@Autowired
	private ReadRelationshipBroker readRelationshipBroker;
	@Autowired
	private ReadRelationshipListBroker readRelationshipListBroker;
	@Autowired
	private CreateRelationshipBroker createRelationshipBroker;
	@Autowired
	private UpdateRelationshipBroker updateRelationshipBroker;
	@Autowired
	private DeleteRelationshipBroker deleteRelationshipBroker;
	@Autowired
	private ReadRootDomainBroker readRootDomainBroker;
	@Autowired
	private ReadRootDomainListBroker readRootDomainListBroker;
	@Autowired
	private CreateRootDomainBroker createRootDomainBroker;
	@Autowired
	private UpdateRootDomainBroker updateRootDomainBroker;
	@Autowired
	private DeleteRootDomainBroker deleteRootDomainBroker;
	@Autowired
	private ReadSystemBroker readSystemBroker;
	@Autowired
	private ReadWarStatusBroker readWarStatusBroker;
	@Autowired
	private RestartSystemBroker restartSystemBroker;
	@Autowired
	private ReadSystemListBroker readSystemListBroker;
	@Autowired
	private CreateSystemBroker createSystemBroker;
	@Autowired
	private UpdateSystemBroker updateSystemBroker;
	@Autowired
	private DeleteSystemBroker deleteSystemBroker;
	@Autowired
	private ImportSystemBroker importSystemBroker;
	@Autowired
	private ExportSystemBroker exportSystemBroker;
	@Autowired
	private ReadDatabaseBroker readDatabaseBroker;
	@Autowired
	private ReadDatabasesByAssociatedSystemByPackageBroker readDatabasesBySystemBroker;
	@Autowired
	private ReadDatabasesByAssociatedPackageBroker readDatabasesByPackageBroker;
    @Autowired
	private ReadNotAssociatedDatabasesBroker readNotAssociatedDatabasesBroker;
	@Autowired
	private CreateDatabaseBroker createDatabaseBroker;
	@Autowired
	private UpdateDatabaseBroker updateDatabaseBroker;
	@Autowired
	private DeleteDatabaseBroker deleteDatabaseBroker;
	@Autowired
	private ReadFilestoreBroker readFilestoreBroker;
	@Autowired
	private CreateFilestoreBroker createFilestoreBroker;
	@Autowired
	private UpdateFilestoreBroker updateFilestoreBroker;
	@Autowired
	private DeleteFilestoreBroker deleteFilestoreBroker;
	@Autowired
	private ReadServerBroker readServerBroker;
	@Autowired
	private CreateServerBroker createServerBroker;
	@Autowired
	private UpdateServerBroker updateServerBroker;
	@Autowired
	private DeleteServerBroker deleteServerBroker;
    @Autowired
    private RegisterServerBroker registerServerBroker;
	@Autowired
	private RegistrySlaveNodeBroker registrySlaveNodeBroker;
	@Autowired
	private CreateReportBroker createReportBroker;
	@Autowired
	private UpdateReportBroker updateReportBroker;
	@Autowired
	private DeleteReportBroker deleteReportBroker;
	@Autowired
	private ReadReportBroker readReportBroker;
    @Autowired
   	private CreateWizardBroker createWizardBroker;
   	@Autowired
   	private UpdateWizardBroker updateWizardBroker;
   	@Autowired
   	private DeleteWizardBroker deleteWizardBroker;
   	@Autowired
   	private ReadWizardBroker readWizardBroker;
    @Autowired
    private RedeployPackageArchiveBroker redeployPackageArchiveBroker;
    @Autowired
    private CheckNameBroker checkNameBroker;
    @Autowired
    private ReadLicenseBroker readLicenseBroker;
    @Autowired
    private VerifyLicenseBroker verifyLicenseBroker;
    @Autowired
    private SocialInfoBroker socialInfoBroker;
    @Autowired
    private ReadBIReportBroker readBIReportBroker;
    @Autowired
    private ReadBIReportUserBroker readBIReportUserBroker;
    @Autowired
    private DeleteBIReportUserBroker deleteBIReportUserBroker;
    @Autowired
    private ReadAllowBIReportUserBroker readAllowBIReportUserBroker;
    @Autowired
    private ReadBIReportByLookupBroker readBIReportByLookupBroker;
    @Autowired
    private CreateBIReportBroker createBIReportBroker;
    @Autowired
    private CreateBIReportUserBroker createBIReportUserBroker;
    @Autowired
    private UpdateBIReportUserBroker updateBIReportUserBroker;

	@Override
	public ServiceResponse<CheckUrl> testSystemStatus(CheckUrl data) {
		return testSystemStatusBroker.execute(new ServiceRequest<CheckUrl>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> getRegistryNodeChildren(Long registryNodeId, PageType pageType, String packageLookup) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("pageType", pageType);
		namedValues.put("packageLookup", packageLookup);
		return readRegistryNodeChildrenListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByIdChildren(Long registryNodeId, PageType pageType) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("pageType", pageType);
		return readRegistryNodeChildrenExpandedByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

    @Override
    public ServiceResponse<RegistryNodeTree> getRegistryNodeWithExpandedByLookupChildren(String registryNodeLookup, PageType pageType) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeLookup", registryNodeLookup);
		namedValues.put("pageType", pageType);
		return readRegistryNodeChildrenExpandedByLookupBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> getRegistryNodeFieldsByEntity(Long entityId) {
        return readRegistryNodeFieldsByEntityBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(entityId)));
    }

    @Override
	public ServiceResponse<MoveRegistryNodeTree> moveRegistryNode(MoveRegistryNodeTree moveRegistryNodeTree) {
		return moveRegistryNodeTreeBroker.execute(new ServiceRequest<MoveRegistryNodeTree>(moveRegistryNodeTree));
	}

	@Override
	public ServiceResponse<Search> getSearchResult(String term, Long registryNodeId, String lookup, String assetType, Paging paging) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("term", term);
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("lookup", lookup);
		namedValues.put("assetType", assetType);
		namedValues.put("paging", paging);
		return searchListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

    @Override
    public ServiceResponse<FileTree> readDirectory(String path, Boolean directoryOnly) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("path", path);
		namedValues.put("directoryOnly", directoryOnly);

        return readAllDirectoryBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
	public ServiceResponse<Action> readAction(Long actionId) {
		return readActionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(actionId)));
	}

	@Override
	public ServiceResponse<Action> readAllActions() {
		return readActionListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<Action> readActionsFromCache(String packageLookup) {
		NamedValues<String> params = new NamedValues<String>();
		params.put(GetCachedActionListBroker.PARAM_PACKAGE_LOOKUP, packageLookup);
		return getCachedActionListBroker.execute(new ServiceRequest<NamedValues<String>>(params));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createAction(Action action) {
		return createActionBroker.execute(new ServiceRequest<Action>(action));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateAction(Long actionId, Action action) {
		return updateActionBroker.execute(new ServiceRequest<Action>(action));
	}

	@Override
	public ServiceResponse<Action> deleteAction(Long actionId) {
		return deleteActionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(actionId)));
	}

	@Override
	public ServiceResponse associatePackage(Long systemId, Long packageId) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("systemId", systemId);
		namedValues.put("packageId", packageId);
		return associatePackageBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse removeAssociationPackage(Long systemId, Long packageId) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("systemId", systemId);
		namedValues.put("packageId", packageId);
		return deleteAssociationPackageBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<Domain> readDomain(Long domainId) {
		return readDomainBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(domainId)));
	}

	@Override
	public ServiceResponse<Domain> readAllDomains() {
		return readDomainListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createDomain(Domain data) {
		return createDomainBroker.execute(new ServiceRequest<Domain>(data));
	}

    @Override
    public ServiceResponse<Domain> createDomainByParentLookup(Domain data) {
        return createDomainByParentLookupBroker.execute(new ServiceRequest<Domain>(data));
    }

    @Override
    public ServiceResponse<Domain> createDomainDatabase(Domain domain, Boolean reverseEngineer) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("domain", domain);
        namedValues.put("reverseEngineer", reverseEngineer);
        return createDomainDatabaseBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
	public ServiceResponse<RegistryNodeTree> updateDomain(Long domainId, Domain data) {
		return updateDomainBroker.execute(new ServiceRequest<Domain>(data));
	}

	@Override
	public ServiceResponse<Domain> deleteDomain(Long domainId) {
		return deleteDomainBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(domainId)));
	}

    @Override
    public ServiceResponse<Domain> readDomainsByPackageLookup(String lookup) {
        return readDomainsByPackageLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
    }

    @Override
	public ServiceResponse<Entity> readEntity(Long entityId) {
		return readEntityBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(entityId)));
	}

    @Override
    public ServiceResponse<Entity> readEntityByLookup(String entityLookup) {
        return readEntityByLookupBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(
                        new SimpleIdentifier<String>(entityLookup)));
    }

    @Override
    public ServiceResponse<Entity> searchEntityByDomain(String terms, Long domainId, String packageLookup) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("terms", terms);
        namedValues.put("domainId", domainId);
        namedValues.put("packageLookup", packageLookup);
        return searchEntityBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Entity> readParentEntityTypesByEntityLookup(String entityLookup) {
        return readParentTypesByEntityLookupBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(
                        new SimpleIdentifier<String>(entityLookup)));
    }

    @Override
    public ServiceResponse<Entity> readDirectChildrenTypes(String entityLookup) {
        return readDirectChildrenTypesBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(entityLookup)));
    }

    @Override
    public ServiceResponse<Entity> readEntitiesUpInHierarchyByLookup(String entityLookup) {
        return readEntitiesFromHierarchyBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(
                        new SimpleIdentifier<String>(entityLookup)));
    }

    @Override
	public ServiceResponse<Entity> readAllEntities(String type, Long exceptId) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("type", type);
		namedValues.put("exceptId", exceptId);
		return readEntityListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

    @Override
	public ServiceResponse<Entity> readEntitiesByPackageLookup(String lookup) {
		return readEntitiesByPackageLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createEntity(Entity data) {
		return createEntityBroker.execute(new ServiceRequest<Entity>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateEntity(Long entityId, Entity data) {
		return updateEntityBroker.execute(new ServiceRequest<Entity>(data));
	}

	@Override
	public ServiceResponse<Entity> deleteEntity(Long entityId) {
		return deleteEntityBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(entityId)));
	}

    @Override
   	public ServiceResponse<Form> createForm(Form data) {
   		return createFormBroker.execute(new ServiceRequest<Form>(data));
   	}

    @Override
    public ServiceResponse<SecuredEntity> readSecuredEntitiesByType(Long entityTypeId) {
        return readSecuredEntitiesByTypeBroker.execute(
                new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(entityTypeId)));
    }

    @Override
    public ServiceResponse<SimpleIdentifier<Boolean>> getSecurityEnabledFlagForEntity(String entityLookup) {
        return getSecurityEnabledInfoBroker.execute(
                new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(entityLookup)));
    }

    @Override
	public ServiceResponse<UploadPackageArchive> uploadPackageArchive(InputStream stream) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("stream", stream);
		return uploadPackageArchiveBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse performPackageArchive(String uploadedFilename, String versionName, Boolean doAsCurrent, Boolean doArchive) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("uploadedFilename", uploadedFilename);
		namedValues.put("versionName", versionName);
		namedValues.put("doAsCurrent", doAsCurrent);
		namedValues.put("doArchive", doArchive);
		return performPackageArchiveBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse checkUniquePackageVersion(Long packageId, String versionName) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("versionName", versionName);
		return checkUniquePackageVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse installPackageArchive(PackageAction packageAction) {
		return installPackageArchiveBroker.execute(new ServiceRequest<PackageAction>(packageAction));
	}

	@Override
	public ServiceResponse migrateDatabase(PackageAction packageAction) {
		return migrateDatabasesBroker.execute(new ServiceRequest<PackageAction>(packageAction));
	}

	@Override
	public ServiceResponse uninstallPackageArchive(PackageAction packageAction) {
        return uninstallPackageArchiveBroker.execute(new ServiceRequest<PackageAction>(packageAction));
	}

	@Override
	public ServiceResponse<Package> readPackage(Long packageId) {
		return readPackageBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

	@Override
	public ServiceResponse<Package> readAllPackages() {
		return readPackageListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createPackage(Package data) {
		return createPackageBroker.execute(new ServiceRequest<Package>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updatePackage(Long packageId, Package data) {
		return updatePackageBroker.execute(new ServiceRequest<Package>(data));
	}

	@Override
	public ServiceResponse<Package> deletePackage(Long packageId) {
		return deletePackageBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

	@Override
	public InputStream getPackageArchive(Long packageId, String packageFilename) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("packageFilename", packageFilename);
		ServiceResponse<FileInfo> execute = downloadGeneratedPackageBroker.execute(new ServiceRequest<NamedValues>(namedValues));
		return execute.getItem().getStream();
	}

	@Override
	public ServiceResponse<PackageVersion> generateWar(Long packageId) {
		return generateWarBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

	@Override
	public ServiceResponse<PackageVersion> generateUpgradeXml(Long packageId, Integer version) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("version", version);
		return generateUpgradeXmlBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<PackageVersionInfo> getPackageVersionsInfo(Long packageId) {
		return getPackageVersionsInfoBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

	@Override
	public ServiceResponse<Package> lockPackageVersion(Long packageId, PackageVersionInfo data) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("data", data);
		return lockPackageVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<PackageVersion> archive(Long packageId) {
		return archivePackageVersionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

	@Override
	public ServiceResponse<Package> deletePackageVersion(Long packageId, Integer version) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("version", version);
		return deletePackageVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse activatePackageVersion(Long packageId, Integer version) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("version", version);
		return activatePackageVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse supportPackageVersion(Long packageId, Integer version) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("version", version);
		return supportPackageVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<PackageVersion> uploadPackageVersionXML(Long packageId, String fileType, InputStream inputStream) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("fileType", fileType);
		namedValues.put("inputStream", inputStream);
		return uploadPackageVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

    @Override
    public ServiceResponse associateDatabase(Long packageId, Long databaseId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("packageId", packageId);
		namedValues.put("databaseId", databaseId);
        return associateDatabaseBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse reverseEngineering(Long registryNodeId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
        return reverseEngineeringBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
	public ServiceResponse<Relationship> readRelationship(Long relationshipId) {
		return readRelationshipBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(relationshipId)));
	}

	@Override
	public ServiceResponse<Relationship> readAllRelationships() {
		return readRelationshipListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createRelationship(Relationship data) {
		return createRelationshipBroker.execute(new ServiceRequest<Relationship>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateRelationship(Long relationshipId, Relationship data) {
		return updateRelationshipBroker.execute(new ServiceRequest<Relationship>(data));
	}

	@Override
	public ServiceResponse<Relationship> deleteRelationship(Long relationshipId) {
		return deleteRelationshipBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(relationshipId)));
	}

	@Override
	public ServiceResponse<RootDomain> readRootDomain(Long rootDomainId) {
		SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(rootDomainId);
		return readRootDomainBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
	}

	@Override
	public ServiceResponse<RootDomain> readAllRootDomains() {
		return readRootDomainListBroker.execute(new ServiceRequest<NamedValues>());
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createRootDomain(RootDomain data) {
		return createRootDomainBroker.execute(new ServiceRequest<RootDomain>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateRootDomain(Long domainId, RootDomain data) {
		return updateRootDomainBroker.execute(new ServiceRequest<RootDomain>(data));
	}

	@Override
	public ServiceResponse<RootDomain> deleteRootDomain(Long domainId) {
		return deleteRootDomainBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(domainId)));
	}

	@Override
	public ServiceResponse<System> readSystem(Long systemId) {
		return readSystemBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(systemId)));
	}

	@Override
	public ServiceResponse<WebArchive> readWarStatus(Long systemId) {
		return readWarStatusBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(systemId)));
	}

	@Override
	public ServiceResponse<System> readAllSystems(Boolean aliases) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("aliases", aliases);
		return readSystemListBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createSystem(System data) {
		return createSystemBroker.execute(new ServiceRequest<System>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateSystem(Long systemId, System data) {
		return updateSystemBroker.execute(new ServiceRequest<System>(data));
	}

	@Override
	public ServiceResponse<System> deleteSystem(Long systemId) {
		return deleteSystemBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(systemId)));
	}

	@Override
	public InputStream exportXml(Long rootDomainId, String name) {
		ServiceResponse<FileInfo> response = exportSystemBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(rootDomainId)));
		return response.getItem().getStream();
	}

	@Override
	public ServiceResponse importXml(Long rootDomainId, InputStream stream) {
		NamedValues namedValues = new NamedValues();
		namedValues.put("rootDomainId", rootDomainId);
		namedValues.put("stream", stream);
		return importSystemBroker.execute(new ServiceRequest<NamedValues>(namedValues));
	}

	@Override
	public ServiceResponse<Database> readDatabase(Long databaseId) {
		return readDatabaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(databaseId)));
	}

	@Override
	public ServiceResponse<Database> readDatabasesBySystem(Long packageId) {
		return readDatabasesBySystemBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
	}

    @Override
    public ServiceResponse<Database> readDatabasesByPackage(Long packageId) {
        return readDatabasesByPackageBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageId)));
    }

    @Override
    public ServiceResponse<Database> readNotAssociatedDatabases() {
        return readNotAssociatedDatabasesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>());
    }

    @Override
	public ServiceResponse<RegistryNodeTree> createDatabase(Database data) {
		return createDatabaseBroker.execute(new ServiceRequest<Database>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateDatabase(Long databaseId, Database data) {
		return updateDatabaseBroker.execute(new ServiceRequest<Database>(data));
	}

	@Override
	public ServiceResponse<Database> deleteDatabase(Long databaseId) {
		return deleteDatabaseBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(databaseId)));
	}

	@Override
	public ServiceResponse<Filestore> readFilestore(Long filestoreId) {
		return readFilestoreBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(filestoreId)));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createFilestore(Filestore data) {
		return createFilestoreBroker.execute(new ServiceRequest<Filestore>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateFilestore(Long filestoreId, Filestore data) {
		return updateFilestoreBroker.execute(new ServiceRequest<Filestore>(data));
	}

	@Override
	public ServiceResponse<Filestore> deleteFilestore(Long filestoreId) {
		return deleteFilestoreBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(filestoreId)));
	}

	@Override
	public ServiceResponse<Server> readServer(Long serverId) {
		return readServerBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(serverId)));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createServer(Server data) {
		return createServerBroker.execute(new ServiceRequest<Server>(data));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateServer(Long serverId, Server data) {
		return updateServerBroker.execute(new ServiceRequest<Server>(data));
	}

	@Override
	public ServiceResponse<Server> deleteServer(Long serverId) {
		return deleteServerBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(serverId)));
	}

    @Override
    public ServiceResponse registerServerNode(ServerNodeConfig nodeConfig) {
        return registerServerBroker.execute(new ServiceRequest<ServerNodeConfig>(nodeConfig));
    }

	@Override
	public InputStream registerSlaveNode(ServerNodeConfig config) {
		ServiceResponse<SimpleIdentifier<InputStream>> execute = registrySlaveNodeBroker.execute(new ServiceRequest<ServerNodeConfig>(config));
		return execute.getItem().getIdentifier();
	}

	@Override
	public ServiceResponse restart() {
		return restartSystemBroker.execute(new ServiceRequest());
	}

    @Override
    public ServiceResponse ping() {
        return new ServiceResponse("Ping: " + new Date(), true);
    }

	@Override
	public ServiceResponse<Report> readReport(Long reportId) {
		return readReportBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(reportId)));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> createReport(Report report) {
		return createReportBroker.execute(new ServiceRequest<Report>(report));
	}

	@Override
	public ServiceResponse<RegistryNodeTree> updateReport(Long reportId, Report report) {
		return updateReportBroker.execute(new ServiceRequest<Report>(report));
	}

	@Override
	public ServiceResponse<Report> deleteReport(Long reportId) {
		return deleteReportBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(reportId)));
	}

    @Override
   	public ServiceResponse<Wizard> readWizard(Long wizardId) {
   		return readWizardBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(wizardId)));
   	}

   	@Override
   	public ServiceResponse<RegistryNodeTree> createWizard(Wizard wizard) {
   		return createWizardBroker.execute(new ServiceRequest<Wizard>(wizard));
   	}

   	@Override
   	public ServiceResponse<RegistryNodeTree> updateWizard(Long wizardId, Wizard wizard) {
   		return updateWizardBroker.execute(new ServiceRequest<Wizard>(wizard));
   	}

   	@Override
   	public ServiceResponse<Wizard> deleteWizard(Long wizardId) {
   		return deleteWizardBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(wizardId)));
   	}

    @Override
    public ServiceResponse redeployPackageArchive(String packageLookup) {
        return redeployPackageArchiveBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(packageLookup)));
    }

    @Override
    public ServiceResponse checkName(String path, String name, DomainType type) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("path", path);
        namedValues.put("name", name);
        namedValues.put("type", type);
        return checkNameBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<License> readLicense() {
        return readLicenseBroker.execute(new ServiceRequest());
    }

    @Override
    public ServiceResponse<License> verify(InputStream stream) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("stream", stream);
        return verifyLicenseBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Social> socialEnabled(String packageLookup) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("packageLookup", packageLookup);
        return socialInfoBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
   	public ServiceResponse<BIReport> readBIReport(Long reportId) {
   		return readBIReportBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(reportId)));
   	}

    @Override
   	public ServiceResponse<BIReportUser> readBIReportUser(Long reportId) {
   		return readBIReportUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(reportId)));
   	}

    @Override
   	public ServiceResponse<BIReportUser> deleteBIReportUser(Long reportId) {
   		return deleteBIReportUserBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(reportId)));
   	}

    @Override
   	public ServiceResponse<BIReportUser> readAllowBIReportUser(String packageLookup) {
   		return readAllowBIReportUserBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(packageLookup)));
   	}

    @Override
    public ServiceResponse<BIReport> readBIReportByLookup(String lookup) {
   		return readBIReportByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
   	}

    @Override
   	public ServiceResponse<RegistryNodeTree> createBIReport(BIReport report) {
   		return createBIReportBroker.execute(new ServiceRequest<BIReport>(report));
   	}

    @Override
   	public ServiceResponse<BIReportUser> createBIReportUser(BIReportUser report) {
   		return createBIReportUserBroker.execute(new ServiceRequest<BIReportUser>(report));
   	}

    @Override
    public ServiceResponse<BIReportUser> updateBIReportUser(Long id, BIReportUser report) {
        return updateBIReportUserBroker.execute(new ServiceRequest<BIReportUser>(report));
    }

}
