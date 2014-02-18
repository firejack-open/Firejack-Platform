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

package net.firejack.platform.core.config.export;

import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.construct.PackageDescriptor;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.construct.ReferenceObjectData;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.authority.*;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceVersionElement;
import net.firejack.platform.core.config.meta.factory.*;
import net.firejack.platform.core.config.meta.parse.xml.IPackageXmlProcessor;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexEntityReferenceModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.bi.IBIReportStore;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.core.utils.VersionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component("basicPackageExporter")
@SuppressWarnings("unused")
public class DefaultPackageExporter implements IPackageExporter {

    private static final String MSG_PACKAGE_ID_SHOULD_NOT_BE_NULL = "Package id parameter should not be null.";
    private static final String MSG_PACKAGE_UID_SHOULD_NOT_BE_NULL = "Package uid parameter should not be null.";
    private static final String MSG_PACKAGE_VERSION_ID_SHOULD_NOT_BE_NULL = "Package version id parameter should not be null.";
    private static final String MSG_FAILED_TO_FIND_PACKAGE = "Failed to find package by specified package id parameter.";
    private static final String MSG_FAILED_TO_FIND_PACKAGE_VERSION = "Failed to find package version by specified package version id parameter.";

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;
    @Autowired
    @Qualifier("entityStore")
    protected IEntityStore entityStore;
    @Autowired
    @Qualifier("relationshipStore")
    protected IRelationshipStore relationshipStore;
    @Autowired
    @Qualifier("fieldStore")
    private IFieldStore fieldStore;
    @Autowired
    @Qualifier("indexStore")
    private IIndexStore indexStore;
    @Autowired
    @Qualifier("actionStore")
    private IActionStore actionStore;
    @Autowired
    @Qualifier("navigationElementStore")
    private IRegistryNodeStore<NavigationElementModel> navigationStore;
    @Autowired
    @Qualifier("directoryStore")
    private IRegistryNodeStore<DirectoryModel> directoryStore;
    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;
    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;
    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;
    @Autowired
    @Qualifier("groupStore")
    private IGroupStore groupStore;
    @Autowired
    @Qualifier("scheduleStore")
    private IScheduleStore scheduleStore;
    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    @Qualifier("folderStore")
    private IRegistryNodeStore<FolderModel> folderStore;
    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<ResourceModel> resourceStore;
    @Autowired
    @Qualifier("resourceVersionStore")
    private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;
    @Autowired
    @Qualifier("domainStore")
    private IRegistryNodeStore<DomainModel> domainStore;
	@Autowired
    @Qualifier("configStore")
    private IConfigStore configStore;
    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;
	@Autowired
	@Qualifier("processStore")
	private IProcessStore processStore;
    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore resourceLocationStore;
    @Autowired
    @Qualifier("permissionAssignmentStore")
    private IPermissionAssignmentStore permissionAssignmentStore;
    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;
    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore profileFieldGroupStore;
    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore profileFieldStore;
	@Autowired
    private IReportStore reportStore;
	@Autowired
    private IBIReportStore biReportStore;
    @Autowired
    private IWizardStore wizardStore;

    @Autowired
    @Qualifier("resourceLocationElementFactory")
    private PackageDescriptorConfigElementFactory<ResourceLocationModel, ResourceLocationElement> resourceLocationFactory;
    @Autowired
    @Qualifier("generatePackageXmlHelper")
    private GeneratePackageXmlHelper generatePackageXmlHelper;
    @Autowired
    @Qualifier("actionElementFactory")
    private PackageDescriptorConfigElementFactory<ActionModel, ActionElement> actionElementFactory;
    @Autowired
    @Qualifier("navigationElementFactory")
    private PackageDescriptorConfigElementFactory<NavigationElementModel, NavigationConfigElement> navigationConfigElementFactory;
    @Autowired
    @Qualifier("userElementFactory")
    private PackageDescriptorConfigElementFactory<UserModel, UserElement> userElementFactory;
    @Autowired
    @Qualifier("directoryElementFactory")
    private PackageDescriptorConfigElementFactory<DirectoryModel, DirectoryElement> directoryElementFactory;
    @Autowired
    @Qualifier("groupElementFactory")
    private GroupElementFactory groupElementFactory;
    @Autowired
    @Qualifier("scheduleElementFactory")
    private ScheduleElementFactory scheduleElementFactory;
    @Autowired
    @Qualifier("roleElementFactory")
    private PackageDescriptorConfigElementFactory<RoleModel, RoleElement> roleElementFactory;
    @Autowired
    @Qualifier("permissionElementFactory")
    private PackageDescriptorConfigElementFactory<PermissionModel, PermissionElement> permissionElementFactory;
    @Autowired
    @Qualifier("folderElementFactory")
    private PackageDescriptorConfigElementFactory<FolderModel, FolderElement> folderElementFactory;
	@Autowired
    @Qualifier("configsElementFactory")
    private ConfigsElementFactory configsElementFactory;
	@Autowired
    @Qualifier("actorElementFactory")
    private ActorElementFactory actorElementFactory;
	@Autowired
    @Qualifier("processElementFactory")
    private ProcessElementFactory processElementFactory;
    @Autowired
    @Qualifier("resourceElementFactory")
    private ResourceElementFactory resourceElementFactory;
    @Autowired
    @Qualifier("resourceVersionElementFactory")
    private ResourceVersionElementFactory resourceVersionElementFactory;
    @Autowired
    @Qualifier("collectionElementFactory")
    private CollectionElementFactory collectionElementFactory;
    @Autowired
    @Qualifier("userProfileFieldElementFactory")
    private UserProfileFieldElementFactory profileFieldElementFactory;
    @Autowired
    @Qualifier("userProfileFieldGroupElementFactory")
    private UserProfileFieldGroupElementFactory profileFieldGroupElementFactory;
	@Autowired
    private ReportElementFactory reportElementFactory;
	@Autowired
    private BIReportElementFactory biReportElementFactory;
	@Autowired
    private WizardElementFactory wizardElementFactory;

    private ConfigElementFactory factory = ConfigElementFactory.getInstance();

    @Override
    public PackageExportResult exportPackage(Long packageId) {
        return exportPackage(packageId, null);
    }

	@Override
	public PackageExportResult exportPackage(Long packageId, String version) {
		Tuple<IPackageDescriptor, String> packageDescriptorInfo =
				populatePackageDescriptor(packageId, true);

		IPackageXmlProcessor xmlProcessor = factory.getDefaultPackageXmlProcessor();
		IPackageDescriptor descriptor = packageDescriptorInfo.getKey();
		if (version != null) {
			descriptor.setVersion(version);
		}
		String xml = xmlProcessor.packageToXml(descriptor);

		PackageExportResult exportResult = new PackageExportResult();
		exportResult.setPackageXml(xml);
		exportResult.setGeneratedResourceFilePath(packageDescriptorInfo.getValue());

		return exportResult;
	}

	@Override
    public IPackageDescriptor exportPackageDescriptor(Long packageId) {
        Tuple<IPackageDescriptor, String> packageDescriptorInfo =
                populatePackageDescriptor(packageId, false);
        return packageDescriptorInfo.getKey();
    }

    @Override
    public IPackageDescriptor exportPackageDescriptor(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException(MSG_PACKAGE_UID_SHOULD_NOT_BE_NULL);
        }

        PackageModel packageToExport = packageStore.findByUID(uid);
        IPackageDescriptor result;
        if (packageToExport == null) {
            result = null;
        } else {
            Tuple<IPackageDescriptor, String> packageDescriptorInfo =
                    populatePackageDescriptor(packageToExport, false);
            result = packageDescriptorInfo.getKey();
        }
        return result;
    }

    @Override
    public PackageExportResult exportPackageDescriptor(IPackageDescriptor descriptor) {
        IPackageXmlProcessor xmlProcessor = factory.getDefaultPackageXmlProcessor();
        String xml = xmlProcessor.packageToXml(descriptor);
        PackageExportResult exportResult = new PackageExportResult();
        exportResult.setPackageXml(xml);

        return exportResult;
    }

    @Override
    public String exportPackageDescriptorToXml(Long packageId) {
        Tuple<IPackageDescriptor, String> packageDescriptorInfo =
                populatePackageDescriptor(packageId, false);
        IPackageXmlProcessor xmlProcessor = factory.getDefaultPackageXmlProcessor();
        return xmlProcessor.packageToXml(packageDescriptorInfo.getKey());
    }

    private Tuple<IPackageDescriptor, String> populatePackageDescriptor(
            Long packageId, boolean generateResourceVersionZip) {
        if (packageId == null) {
            throw new IllegalArgumentException(MSG_PACKAGE_ID_SHOULD_NOT_BE_NULL);
        }

        PackageModel packageToExport = packageStore.findByIdWithUID(packageId);
        return populatePackageDescriptor(packageToExport, generateResourceVersionZip);
    }

    private Tuple<IPackageDescriptor, String> populatePackageDescriptor(
            PackageModel packageToExport, boolean generateResourceVersionZip) {
        if (packageToExport == null) {
            throw new IllegalArgumentException(MSG_FAILED_TO_FIND_PACKAGE);
        }

        String packageLookupPrefix = packageToExport.getLookup() + ".";

        List<UserModel> userList = userStore.findAllByRegistryNodeIdWithRoles(packageToExport.getId());
        List<UserElement> userElementList = userElementFactory.getDescriptorElementList(userList);

	    List<ResourceModel> resourceList = resourceStore.findAllByLikeLookupPrefix(packageLookupPrefix);
        List<ResourceElement> resourceElementList = new ArrayList<ResourceElement>();

        String generatePackageResourceFilePath = null;
        if (resourceList != null && !resourceList.isEmpty()) {
            if (generateResourceVersionZip) {
                List<AbstractResourceVersionModel> resourceVersions = new ArrayList<AbstractResourceVersionModel>();
                List<ResourceVersionElement> resourceVersionElements = new ArrayList<ResourceVersionElement>();
                for (ResourceModel resource : resourceList) {
                    ResourceElement resourceElement =
                            resourceElementFactory.getDescriptorElement(resource);
                    resourceElement.setResourceType(resource.getResourceType());
                    List<AbstractResourceVersionModel> versions =
                            resourceVersionStore.findLastVersionsByResourceId(resource.getId());
                    for (AbstractResourceVersionModel resourceVersion : versions) {
                        ResourceVersionElement resourceVersionElement =
                                resourceVersionElementFactory.getResourceVersionElement(resourceVersion);
                        resourceVersionElementFactory.addTypedResourceVersion(resourceElement, resourceVersionElement);
                        resourceVersions.add(resourceVersion);
                        resourceVersionElements.add(resourceVersionElement);
                    }
                    resourceElementList.add(resourceElement);
                }
	            try {
		            generatePackageResourceFilePath =
		                    generatePackageXmlHelper.generateResourcesZipFile(resourceVersions, resourceVersionElements);
	            } catch (IOException e) {
		            e.printStackTrace();
	            }
            } else {
                for (ResourceModel resource : resourceList) {
                    ResourceElement resourceElement = resourceElementFactory.getDescriptorElement(resource);
                    resourceElement.setResourceType(resource.getResourceType());
                    List<AbstractResourceVersionModel> versions = resourceVersionStore.findLastVersionsByResourceId(resource.getId());
                    for (AbstractResourceVersionModel resourceVersion : versions) {
                        ResourceVersionElement resourceVersionElement =
                                resourceVersionElementFactory.getResourceVersionElement(resourceVersion);
                        resourceVersionElementFactory.addTypedResourceVersion(resourceElement, resourceVersionElement);
                    }
                    resourceElementList.add(resourceElement);
                }
            }
        }

        String packageRefPath = packageStore.findRegistryNodeRefPath(packageToExport.getId());
        PackageDescriptor versionPackage = new PackageDescriptor(
                packageRefPath, packageToExport.getName(),
                VersionUtils.convertToVersion(packageToExport.getVersion()));
        versionPackage.setUid(packageToExport.getUid().getUid());
        versionPackage.setPrefix(packageToExport.getPrefix());
        versionPackage.setContextUrl(packageToExport.getUrlPath());
        Map<Long, IEntityElement> cachedEntities = new HashMap<Long, IEntityElement>();
        versionPackage.setConfiguredDomains(getChildDomains(packageToExport, cachedEntities));
        versionPackage.setConfiguredEntities(getChildEntities(packageToExport, cachedEntities));
        configurateIndexes(cachedEntities);
        versionPackage.setRelationships(getRelationshipElements(packageLookupPrefix));
        versionPackage.setActionElements(
                getConfigElementList(actionStore, actionElementFactory, packageLookupPrefix));
        versionPackage.setNavigationElements(
                getConfigElementList(navigationStore, navigationConfigElementFactory, packageLookupPrefix));
        versionPackage.setDirectoryElements(
                getConfigElementList(directoryStore, directoryElementFactory, packageLookupPrefix));
        versionPackage.setGroupElements(
                getConfigElementList(groupStore, groupElementFactory, packageLookupPrefix));
        versionPackage.setScheduleElements(
                getConfigElementList(scheduleStore, scheduleElementFactory, packageLookupPrefix));
        versionPackage.setUsers(userElementList);
        versionPackage.setPermissions(
                getConfigElementList(permissionStore, permissionElementFactory, packageLookupPrefix));
        versionPackage.setRoles(getConfigElementList(roleStore, roleElementFactory, packageLookupPrefix));
        versionPackage.setFolderElements(
                getConfigElementList(folderStore, folderElementFactory, packageLookupPrefix));
        versionPackage.setResources(resourceElementList);
        versionPackage.setResourceLocationElements(
                getConfigElementList(resourceLocationStore, resourceLocationFactory, packageLookupPrefix));
        versionPackage.setConfigsElements(
                getConfigElementList(configStore, configsElementFactory, packageLookupPrefix));
        versionPackage.setActorElements(
                getConfigElementList(actorStore, actorElementFactory, packageLookupPrefix));
        versionPackage.setProcessElements(
                getConfigElementList(processStore, processElementFactory, packageLookupPrefix));
        versionPackage.setCollectionElements(
                getConfigElementList(collectionStore, collectionElementFactory, packageLookupPrefix));
        versionPackage.setRoleAssignments(getRoleAssignmentElements(packageToExport.getId()));
        versionPackage.setUserProfileFieldGroups(getConfigElementList(
                profileFieldGroupStore, profileFieldGroupElementFactory, packageLookupPrefix));
        versionPackage.setUserProfileFields(getConfigElementList(
                profileFieldStore, profileFieldElementFactory, packageLookupPrefix));
	    versionPackage.setReportElements(
                getConfigElementList(reportStore, reportElementFactory, packageLookupPrefix));
	    versionPackage.setBiReportElements(
             getConfigElementList(biReportStore, biReportElementFactory, packageLookupPrefix));
        versionPackage.setWizardElements(
                     getConfigElementList(wizardStore, wizardElementFactory, packageLookupPrefix));
        versionPackage.setDescription(packageToExport.getDescription());
        return new Tuple<IPackageDescriptor, String>(versionPackage, generatePackageResourceFilePath);
    }

    private void populateFields(IFieldElementContainer fieldElementContainer, Long registryNodeId) {
        List<FieldModel> fields = fieldStore.findFieldsByRegistryNodeId(registryNodeId);
        if (fields != null && !fields.isEmpty()) {
            ConfigElementFactory factory = ConfigElementFactory.getInstance();
	        for (FieldModel field : fields) {
//		        if (field.getName().equalsIgnoreCase("created") || field.getName().equalsIgnoreCase("id") || field.getAutoGenerated() == null || !field.getAutoGenerated()) {
			        IFieldElement element = factory.produceField(fieldElementContainer, field);
			        uid(element, field);
//		        }
	        }
        }
    }

    private <Ent extends LookupModel<RegistryNodeModel>,
            RN extends PackageDescriptorElement> List<RN> getConfigElementList(
            IRegistryNodeStore<Ent> store,
            PackageDescriptorConfigElementFactory<Ent, RN> configElementFactory,
            String lookupPrefix) {
        List<Ent> entityList = store.findAllByLikeLookupPrefix(lookupPrefix);
        return configElementFactory.getDescriptorElementList(entityList);
    }

    private List<IDomainElement> getChildDomains(RegistryNodeModel parentElement, Map<Long, IEntityElement> cachedEntities) {
        List<IDomainElement> domainElementList = new ArrayList<IDomainElement>();
        List<DomainModel> domains = domainStore.findEntriesByParentId(parentElement, null, null);
        if (domains != null) {
            for (DomainModel domain : domains) {
                List<IEntityElement> configuredEntities = getChildEntities(domain, cachedEntities);
                List<IDomainElement> configuredDomains = getChildDomains(domain, cachedEntities);
                IDomainElement domainElement = factory.produceDomain(
		                domain.getName(), configuredDomains, configuredEntities, domain.getPrefix(), domain.getDataSource(), domain.getWsdlLocation());
	            uid(domainElement, domain);
                domainElementList.add(domainElement);
            }
        }
        return domainElementList;
    }

	private void uid(INamedPackageDescriptorElement element, UIDModel model) {
		if (model != null && model.getUid() != null) {
			if (Hibernate.isInitialized(model.getUid())) {
				element.setUid(model.getUid().getUid());
			} else {
				UID uid = packageStore.uidById(model.getUid().getId());
				element.setUid(uid.getUid());
			}
		}
	}

	protected List<IEntityElement> getChildEntities(RegistryNodeModel parent, Map<Long, IEntityElement> cachedEntities) {
        List<IEntityElement> configuredEntities = new ArrayList<IEntityElement>();
        List<EntityModel> entityList = entityStore.findEntriesByParentId(parent, null, null);
        if (entityList != null) {
            for (EntityModel entity : entityList) {
                getChildEntity(cachedEntities, configuredEntities, entity);
            }
        }
        return configuredEntities;
    }

    protected void getChildEntity(Map<Long, IEntityElement> cachedEntities, List<IEntityElement> configuredEntities, EntityModel entity) {
        String extendedEntityRefPath = entity.getExtendedEntity() != null ?
                entityStore.findRegistryNodeRef(entity.getExtendedEntity().getId()) : null;
        String basePath = registryNodeStore.findRegistryNodeRefPath(entity.getId());
        IEntityElement packageEntity = entity.getAbstractEntity() != null && entity.getAbstractEntity() ?
                factory.produceAbstractPackageEntity(entity.getName(), extendedEntityRefPath, null) :
                factory.producePackageEntity(entity.getName(), extendedEntityRefPath, null);
        uid(packageEntity, entity);
        factory.attachPath(packageEntity, basePath);
        packageEntity.setTypeEntity(entity.getTypeEntity());
        packageEntity.setSecurityEnabled(entity.getSecurityEnabled());
        packageEntity.setSubEntity(entity.getType() == RegistryNodeType.SUB_ENTITY);
        packageEntity.setDescription(entity.getDescription());
        packageEntity.setReverseEngineer(entity.getReverseEngineer());
        if (packageEntity instanceof IFieldElementContainer) {
            IFieldElementContainer fieldElementContainer = (IFieldElementContainer) packageEntity;
            populateFields(fieldElementContainer, entity.getId());
        }
        cachedEntities.put(entity.getId(), packageEntity);
        List<IEntityElement> childEntities = getChildEntities(entity, cachedEntities);
        factory.assignEntities(packageEntity, childEntities);
        List<RoleModel> contextRoles = entity.getContextRoles();
        if (contextRoles != null && !contextRoles.isEmpty()) {
            List<EntityRole> entityRoles = new ArrayList<EntityRole>();
            int i = 0;
            for (RoleModel roleModel : contextRoles) {
                String roleParentLookup = roleModel.getPath();
                if (roleParentLookup != null && roleParentLookup.split("\\.").length > 3) {
                    entityRoles.add(new EntityRole(roleModel.getLookup()));
                }
            }
            packageEntity.setAllowableContextRoles(DiffUtils.getArray(entityRoles, EntityRole.class));
        }
        packageEntity.setDatabaseRefName(entity.getDatabaseRefName());
        ReferenceObjectModel refObjectModel = entity.getReferenceObject();
        if (refObjectModel != null && Hibernate.isInitialized(refObjectModel)) {
            ReferenceObjectData refObject = new ReferenceObjectData();
            refObject.setHeading(refObjectModel.getHeading());
            refObject.setSubHeading(refObjectModel.getSubHeading());
            refObject.setDescription(refObjectModel.getDescription());
            packageEntity.setReferenceObject(refObject);
        }
        configuredEntities.add(packageEntity);
    }

    private void configurateIndexes(Map<Long, IEntityElement> cachedEntities) {
        for (Map.Entry<Long, IEntityElement> entry : cachedEntities.entrySet()) {
            Long entityId = entry.getKey();
            IEntityElement packageEntity = entry.getValue();
            List<IIndexElement> indexConfigElements = new ArrayList<IIndexElement>();
            List<IndexModel> indexModels = indexStore.findIndexesByEntityId(entityId);
            for (IndexModel indexModel : indexModels) {
                IIndexElement indexConfigElement = factory.produceIndex(indexModel);
                uid(indexConfigElement, indexModel);
                List<Reference> references = new ArrayList<Reference>();
                List<IndexEntityReferenceModel> referenceModels = indexModel.getReferences();
                for (IndexEntityReferenceModel referenceModel : referenceModels) {
                    EntityModel referenceEntityModel = referenceModel.getEntityModel();
                    if (referenceEntityModel != null) {      // need to remove this condition after refactoring save indexes with entity in XmlToRegistryTranslator
                        IEntityElement referenceEntityElement = cachedEntities.get(referenceEntityModel.getId());
                        Reference entityReference = new Reference();
                        entityReference.setRefPath(referenceEntityElement.getPath());
                        entityReference.setRefName(referenceEntityElement.getName());
                        entityReference.setConstraintName(referenceModel.getColumnName());
                        references.add(entityReference);
                    }
                }
                if (!references.isEmpty()) {
                    indexConfigElement.setEntities(references);
                }

                List<IFieldElement> fieldElements = new ArrayList<IFieldElement>();
                if (packageEntity instanceof IFieldElementContainer) {
                    IFieldElementContainer fieldElementContainer = (IFieldElementContainer) packageEntity;
                    List<FieldModel> fieldModels = indexModel.getFields();
                    for (FieldModel fieldModel : fieldModels) {
                        IFieldElement fieldElement = factory.produceField(fieldModel.getName(), null, null, null);
                        fieldElements.add(fieldElement);
                    }
                }
                if (!fieldElements.isEmpty()) {
                    indexConfigElement.setFields(fieldElements);
                }
                indexConfigElements.add(indexConfigElement);
            }
            if (!indexConfigElements.isEmpty()) {
                packageEntity.setIndexes(indexConfigElements);
            }
        }
    }

    protected List<IRelationshipElement> getRelationshipElements(String packageLookupPrefix) {
        List<RelationshipModel> relationships = relationshipStore.findAllByLikeLookupPrefix(packageLookupPrefix);
        List<IRelationshipElement> relationshipElements = new ArrayList<IRelationshipElement>();
        for (RelationshipModel relationship : relationships) {
            getRelationshipElement(relationshipElements, relationship);
        }
        return relationshipElements;
    }

    protected void getRelationshipElement(List<IRelationshipElement> relationshipElements, RelationshipModel relationship) {
        Long sourceId = relationship.getSourceEntity().getId();
        String sourceRefPath = entityStore.findRegistryNodeRef(sourceId);
        Reference sourceRef = new Reference();
        sourceRef.setRefPath(sourceRefPath);
        sourceRef.setRefName(relationship.getSourceEntityRefName());
        sourceRef.setConstraintName(relationship.getSourceConstraintName());

        Reference targetRef;
        Long targetId = relationship.getTargetEntity().getId();
//            if (sourceId.equals(targetId) &&
//                    (relationship.getRelationshipType() != RelationshipType.ASSOCIATION) &&
//                    (relationship.getRelationshipType() != RelationshipType.WEIGHTED_ASSOCIATION)) {
//	            relationship.setRelationshipType(RelationshipType.TREE);
//                targetRef = null;
//            } else {
        targetRef = new Reference();
        String targetRefPath = entityStore.findRegistryNodeRef(targetId);
        targetRef.setRefPath(targetRefPath);
        targetRef.setRefName(relationship.getTargetEntityRefName());
        targetRef.setConstraintName(relationship.getTargetConstraintName());
//            }
        if (relationship.getRelationshipType() != null) {
            IRelationshipElement rel = null;
            switch (relationship.getRelationshipType()) {
                case TYPE:
                    rel = factory.produceTypeRelationship(relationship.getName(), relationship.getHint(), sourceRef, targetRef);
                    break;
                case LINK:
                    rel = factory.produceLinkRelationship(relationship.getName(), relationship.getHint(), sourceRef, targetRef);
                    break;
                case TREE:
                    rel = factory.produceTreeRelationship(relationship.getName(), relationship.getHint(), sourceRef);
                    break;
                case PARENT_CHILD:
                    rel = factory.produceParentChildRelationship(relationship.getName(), relationship.getHint(), sourceRef, targetRef);
                    break;
                case ASSOCIATION:
                case WEIGHTED_ASSOCIATION:
                    boolean weighted = relationship.getRelationshipType() == RelationshipType.WEIGHTED_ASSOCIATION;
                    rel = factory.produceAssociationRelationship(relationship.getName(), relationship.getHint(), sourceRef, targetRef, weighted);
                    if (rel instanceof IFieldElementContainer) {
                        IFieldElementContainer fieldElementContainer = (IFieldElementContainer) rel;
                        populateFields(fieldElementContainer, relationship.getId());
                    }
            }
            if (rel != null) {
                String basePath = registryNodeStore.findRegistryNodeRefPath(relationship.getId());
                factory.attachPath(rel, basePath);
                factory.setRelationshipRequiredStatus(rel, relationship.getRequired());
                factory.setRelationshipOptions(rel,
                        relationship.getOnUpdateOption(),
                        relationship.getOnDeleteOption());
                uid(rel, relationship);
                rel.setReverseEngineer(relationship.getReverseEngineer());
                relationshipElements.add(rel);
            }
        }
    }

    private List<RoleAssignmentElement> getRoleAssignmentElements(Long sourcePackageId) {
        Map<RoleModel, Set<PermissionModel>> packageRoleAssignedPermissions =
                permissionAssignmentStore.getPackageRoleAssignedPermissions(sourcePackageId);
        List<RoleAssignmentElement> result;
        if (packageRoleAssignedPermissions == null || packageRoleAssignedPermissions.isEmpty()) {
            result = null;
        } else {
            result = new ArrayList<RoleAssignmentElement>();
            for (Map.Entry<RoleModel, Set<PermissionModel>> entry : packageRoleAssignedPermissions.entrySet()) {
                RoleModel role = entry.getKey();
                Set<PermissionModel> permissionSet = entry.getValue();
                if (permissionSet != null && !permissionSet.isEmpty()) {
                    List<RolePermissionReference> permissionReferences = new ArrayList<RolePermissionReference>();
                    for (PermissionModel permission : permissionSet) {
                        RolePermissionReference ref = new RolePermissionReference();
                        ref.setPath(permission.getLookup());

                        permissionReferences.add(ref);
                    }

                    RoleAssignmentElement element = new RoleAssignmentElement();
                    element.setPath(role.getLookup());
                    element.setPermissions(permissionReferences);

                    result.add(element);
                }
            }
        }
        return result;
    }

}
