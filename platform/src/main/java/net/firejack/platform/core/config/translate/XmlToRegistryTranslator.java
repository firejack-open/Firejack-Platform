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

package net.firejack.platform.core.config.translate;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.construct.GeneratedFieldContext;
import net.firejack.platform.core.config.meta.construct.GeneratedFieldType;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.action.ActionParameterElement;
import net.firejack.platform.core.config.meta.element.authority.*;
import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.directory.UserRoleRef;
import net.firejack.platform.core.config.meta.element.process.ActorElement;
import net.firejack.platform.core.config.meta.element.process.GroupAssignElement;
import net.firejack.platform.core.config.meta.element.process.RoleAssignElement;
import net.firejack.platform.core.config.meta.element.process.UserAssignElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.config.meta.element.resource.IStorableResourceVersionDescriptorElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceVersionElement;
import net.firejack.platform.core.config.meta.factory.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.translate.exception.TranslationRuntimeException;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;
import net.firejack.platform.core.config.translate.sql.LeadIdPrefixNameResolver;
import net.firejack.platform.core.config.translate.sql.exception.TypeLookupException;
import net.firejack.platform.core.domain.IdLookup;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexEntityReferenceModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.UserActorModel;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.bi.IBIReportStore;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.type.AllowedFieldValuesListUserType;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.securitymanager.broker.DeleteSecuredRecordsBroker;
import net.firejack.platform.service.securitymanager.broker.SaveSecuredRecordsBroker;
import net.firejack.platform.web.cache.ICacheDataProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@SuppressWarnings("unused")
@Scope("prototype")
@Component("xmlToRegistryTranslator")
public class XmlToRegistryTranslator extends AbstractTranslator<Boolean, StatusProviderTranslationResult> {

    private static final Logger logger = Logger.getLogger(XmlToRegistryTranslator.class);

    private static final String MSG_NAME_ATTRIBUTE_REQUIRED = "Package should have name attribute.";
    private static final String MSG_PACKAGE_IS_NOT_A_CHILD_OF_ROOT_DOMAIN = "Package should be a child of root domain.";
    private static final String MSG_NEW_VERSION_WILL_BE_CREATED =
            "Could not merge the changes to the current version of the package. New version of the package will be created.";
    private static final String MSG_FAILED_TO_RETRIEVE_ROOT_DOMAIN_NAME1 = "Failed to retrieve root domain name from extended entity path: [";
    private static final String MSG_FAILED_TO_RETRIEVE_ROOT_DOMAIN_NAME2 = "]";
    private static final String MSG_WRONG_ROOT_DOMAIN_NAME = "Wrong root domain name.";

    @Autowired
    @Qualifier("domainStore")
    private IRegistryNodeStore<DomainModel> domainStore;

    @Autowired
    @Qualifier("packageStore")
    private IPackageStore packageStore;

    @Autowired
    @Qualifier("fieldStore")
    private IFieldStore fieldStore;

    @Autowired
    @Qualifier("indexStore")
    private IIndexStore indexStore;

    @Autowired
    @Qualifier("entityStore")
    private IEntityStore entityStore;

    @Autowired
    private IReferenceObjectStore referenceObjectStore;

    @Autowired
    @Qualifier("relationshipStore")
    private IRelationshipStore relationshipStore;

    @Autowired
    @Qualifier("actionStore")
    private IActionStore actionStore;

    @Autowired
    @Qualifier("navigationElementStore")
    private INavigationElementStore navigationStore;

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
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("folderStore")
    private IRegistryNodeStore<FolderModel> folderStore;

    @Autowired
    @Qualifier("resourceVersionStore")
    private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore resourceStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    @Qualifier("rootDomainStore")
    private IRootDomainStore rootDomainStore;

    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore resourceLocationStore;

    @Autowired
    @Qualifier("configStore")
    private IConfigStore configStore;

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    @Qualifier("groupStore")
    private IGroupStore groupStore;

    @Autowired
    @Qualifier("scheduleStore")
    private IScheduleStore scheduleStore;

    @Autowired
    @Qualifier("processStore")
    private IProcessStore processStore;
	@Autowired
	private IReportStore reportStore;
	@Autowired
	private IBIReportStore biReportStore;

    @Autowired
	private IWizardStore wizardStore;

    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore profileFieldStore;

    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore profileFieldGroupStore;

    @Autowired
    @Qualifier("roleElementFactory")
    private PackageDescriptorConfigElementFactory<RoleModel, RoleElement> roleElementFactory;

    @Autowired
    @Qualifier("permissionElementFactory")
    private PackageDescriptorConfigElementFactory<PermissionModel, PermissionElement> permissionElementFactory;

    @Autowired
    @Qualifier("actionElementFactory")
    private ActionElementFactory actionElementFactory;

    @Autowired
    @Qualifier("navigationElementFactory")
    private PackageDescriptorConfigElementFactory<NavigationElementModel, NavigationConfigElement> navigationElementFactory;

    @Autowired
    @Qualifier("resourceLocationElementFactory")
    private PackageDescriptorConfigElementFactory<ResourceLocationModel, ResourceLocationElement> resourceLocationFactory;

    @Autowired
    @Qualifier("directoryElementFactory")
    private PackageDescriptorConfigElementFactory<DirectoryModel, DirectoryElement> rnDirectoryFactory;

    @Autowired
    @Qualifier("groupElementFactory")
    private GroupElementFactory groupFactory;

    @Autowired
    @Qualifier("scheduleElementFactory")
    private ScheduleElementFactory scheduleFactory;

    @Autowired
    @Qualifier("userElementFactory")
    private PackageDescriptorConfigElementFactory<UserModel, UserElement> rnUserFactory;

    @Autowired
    @Qualifier("folderElementFactory")
    private PackageDescriptorConfigElementFactory<FolderModel, FolderElement> rnFolderFactory;

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
    @Qualifier("configsElementFactory")
    private ConfigsElementFactory configElementFactory;

    @Autowired
    @Qualifier("actorElementFactory")
    private ActorElementFactory actorElementFactory;

    @Autowired
    @Qualifier("processElementFactory")
    private ProcessElementFactory processElementFactory;

    @Autowired
    @Qualifier("statusElementFactory")
    private StatusElementFactory statusElementFactory;

    @Autowired
    @Qualifier("activityElementFactory")
    private ActivityElementFactory activityElementFactory;

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

    @Autowired
    @Qualifier("cacheProcessor")
    private ICacheDataProcessor cacheProcessor;

    @Autowired
    @Qualifier("subDomainStore")
    private IRegistryNodeStore<SubDomain> subDomainStore;

    @Autowired
    private FileHelper helper;

    @Autowired
    private Factory modelFactory;

    @Autowired
    private SaveSecuredRecordsBroker saveSecuredRecordsBroker;
    @Autowired
    private DeleteSecuredRecordsBroker deleteSecuredRecordsBroker;

    @Autowired
    @Qualifier("securedRecordStore")
    private ISecuredRecordStore securedRecordStore;

    private Map<Long, List<String>> roleAssociationMap;
    private Map<Long, List<String>> userRoleAssociationMap;
    private Map<String, Permission> permissions;
    private List<RoleAssignmentElement> roleAssignments;
    private Map<String, ActionModel> actions;
    private Map<String, NavigationElementModel> navigations;
    private Map<String, ResourceLocation> resourceLocations;
    private Long packageId;
    private SortedSet<String> subDomainPathPrefixes;
    private Map<Long, Set<PermissionModel>> rolePermissionsMap = new HashMap<Long, Set<PermissionModel>>();
    private Map<String, List<String>> entityRoles = new HashMap<String, List<String>>();
    private Map<String, List<UserProfileFieldModel>> userProfileFields = new HashMap<String, List<UserProfileFieldModel>>();
    private Map<String, BaseEntityModel> cache = new HashMap<String, BaseEntityModel>();
    private Map<RegistryNodeType, List<LookupModel<?>>> srCacheForAdd =
            new HashMap<RegistryNodeType, List<LookupModel<?>>>();
    private Map<RegistryNodeType, List<LookupModel<?>>> srCacheForDel =
            new HashMap<RegistryNodeType, List<LookupModel<?>>>();

    /***/
    public XmlToRegistryTranslator() {
        super(StatusProviderTranslationResult.class);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void beforeTranslate(
            IElementDiffInfoContainer diffContainer, StatusProviderTranslationResult resultState) {
        super.beforeTranslate(diffContainer, resultState);
        clearCachedData();
        EntityElementManager entityManager = diffContainer.getNewEntitiesManager();
        if (StringUtils.isBlank(entityManager.getPackageName())) {
            throw new OpenFlameRuntimeException(MSG_NAME_ATTRIBUTE_REQUIRED);
        }
        String packagePath = entityManager.getPackagePath();
        String lastLookup = "";
        RegistryNodeModel lastRN = null;
        if (StringUtils.isNotBlank(packagePath)) {
            //ensure that all required domains exists
            String[] pathEntries = packagePath.split("\\.");
            if (pathEntries.length < 2) {
                throw new OpenFlameRuntimeException(MSG_PACKAGE_IS_NOT_A_CHILD_OF_ROOT_DOMAIN);
            } else {
                String rootDomainName = pathEntries[1] + "." + pathEntries[0];
                lastLookup = DiffUtils.rootDomainLookup(rootDomainName);
                RootDomainModel rootDomain = rootDomainStore.findByLookup(lastLookup);
                if (rootDomain == null) {
                    rootDomain = new RootDomainModel();
                    rootDomain.setName(rootDomainName);
                    rootDomain.setLookup(lastLookup);
                    rootDomainStore.save(rootDomain);
                    cache.put(rootDomain.getLookup(), rootDomain);
                }
                lastRN = rootDomain;
                if (pathEntries.length > 2) {
                    throw new OpenFlameRuntimeException(MSG_PACKAGE_IS_NOT_A_CHILD_OF_ROOT_DOMAIN);
                }
                SecuredRecordModel securedRecordModel = securedRecordStore.findByIdAndType(
                        rootDomain.getId(), RegistryNodeType.ROOT_DOMAIN.getEntityPath());
                if (securedRecordModel == null) {
                    putToSRCacheADD(rootDomain);
                }
            }
        }

        //todo: change usage for entityLookup!!!
        String packageLookup = DiffUtils.lookup(lastLookup, entityManager.getPackageName());
        PackageModel csPackage = packageStore.findByLookup(packageLookup);
        if (csPackage == null) {
            csPackage = new PackageModel();
            csPackage.setParent(lastRN);
            csPackage.setName(entityManager.getPackageName());
            csPackage.setPrefix(entityManager.getPrefix());
            csPackage.setUrlPath(entityManager.getContextUrl());
            csPackage.setLookup(packageLookup);
            csPackage.setDescription(entityManager.getPackageDescription());
            if (entityManager.getUid() != null) {
                csPackage.setUid(new UID(entityManager.getUid()));
            }
            packageStore.save(csPackage, entityManager.getNewVersion(),
                    entityManager.getNewVersion(), false);
            putToSRCacheADD(csPackage);
        } else  if (StringUtils.isBlank(csPackage.getUrlPath())) {
            csPackage.setUrlPath('/' + csPackage.getName());
            packageStore.saveOrUpdate(csPackage);
        }
        resultState.setVersionNumber(entityManager.getNewVersion());

        packageId = csPackage.getId();
        cache.put(csPackage.getLookup(), csPackage);
        resultState.setPackage(csPackage);
    }

    @Override
    protected void processFieldDiff(FieldsDiff diff) {
        String entityLookup = DiffUtils.lookup(diff.getTargetParent());
        switch (diff.getType()) {
            case ADDED:
                registerField(diff.getDiffTarget(), entityLookup);
                break;
            case REMOVED:
                FieldModel removedField = fieldStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(removedField);
                break;
            case UPDATED:
            default:
                IFieldElement field = diff.getNewElement();
                FieldModel oldField = fieldStore.findByUID(field.getUid());
                if (oldField == null) {
                    throw new OpenFlameRuntimeException(
                            "Failed to find field [uid = " + field.getUid() +
                                    "] for update operation");
                }
                if (!oldField.getPath().equals(entityLookup)) {
                    oldField.setPath(entityLookup);
                    FieldContainerRegistryNode parent = (FieldContainerRegistryNode)
                            registryNodeStore.findByLookup(entityLookup);
                    oldField.setParent(parent);
                }
                oldField.setName(field.getName());
                oldField.setLookup(DiffUtils.lookup(field));
                oldField.setDescription(field.getDescription());
                oldField.setFieldType(field.getType());
                oldField.setRequired(field.isRequired());
	            oldField.setSearchable(field.isSearchable());
		        oldField.setCustomFieldType(field.getCustomType());
		        oldField.setDisplayName(field.getDisplayName());
		        oldField.setDisplayDescription(field.getDisplayDescription());
		        AllowedFieldValuesListUserType userType = new AllowedFieldValuesListUserType();
		        oldField.setAllowedFieldValueList(userType.deserialize(field.getAllowValues()));
                if (field.getDefaultValue() != null) {
                    oldField.setDefaultValue(String.valueOf(field.getDefaultValue()));
                }
                fieldStore.saveOrUpdate(oldField);
        }
    }

    @Override
    protected void processIndexDiff(IndexesDiff diff) {
        String entityLookup = DiffUtils.lookup(diff.getTargetParent());
        switch (diff.getType()) {
            case ADDED:
                registerIndex(diff.getDiffTarget(), entityLookup);
                break;
            case REMOVED:
                IndexModel removedIndex = indexStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(removedIndex);
                break;
            case UPDATED:
            default:
                IIndexElement index = diff.getNewElement();
                IndexModel oldIndex = indexStore.findByUID(index.getUid());
                if (oldIndex == null) {
                    throw new OpenFlameRuntimeException("Failed to find index [uid = " + index.getUid() + "] for update operation");
                }
                registerIndex(diff.getDiffTarget(), entityLookup);
        }
    }

    @Override
    protected void processEntityDiff(EntitiesDiff diff) {
        IEntityElement entity = diff.getDiffTarget();
        List<IndexModel> indexList;
        IIndexElement[] indexes;
        switch (diff.getType()) {
            case ADDED:
                String lookup = DiffUtils.lookup(entity);
                EntityModel ent = (EntityModel) cache.get(lookup);
                if (ent == null)
                    ent = entity.isSubEntity() ? new SubEntityModel() : new EntityModel();


                ent.setName(entity.getName());

                String parentLookup = DiffUtils.lookupByRefPath(entity.getPath());
                RegistryNodeModel parent = registryNodeStore.findByLookup(parentLookup);
                ent.setParent(parent);

                String entityLookup = DiffUtils.lookup(parentLookup, entity.getName());
                ent.setLookup(entityLookup);

                ent.setAbstractEntity(entity.isAbstractEntity());
                ent.setTypeEntity(entity.isTypeEntity());
                ent.setSecurityEnabled(entity.isSecurityEnabled());

                cache.put(ent.getLookup(), ent);

                if (leaveUIDInfo(ent.getLookup())) {//required only for ADDED diff
                    PackageDescriptorConfigElementFactory.initializeModelUID(ent, entity);
                }

                List<FieldModel> fieldList = new ArrayList<FieldModel>();
                IFieldElement[] fields = entity.getFields();
                if (fields != null) {
                    for (IFieldElement field : fields) {
                        fieldList.add(prepareField(field));
                    }
                }
                ent.setFields(fieldList);

//                indexList = new ArrayList<IndexModel>();
//                IIndexElement[] indexes = entity.getIndexes();
//                if (indexes != null) {
//                    for (IIndexElement index : indexes) {
//                        IndexModel indexModel = prepareIndex(index, fieldList);
//                        indexList.add(indexModel);
//                        Reference relationship = index.getRelationship();
//                        if (relationship != null) {
//                            String relationshipPath = DiffUtils.lookupByRefPath(relationship.getRefPath());
//                            String relationshipLookup = DiffUtils.lookup(relationshipPath, relationship.getRefName()) + "#index";
//                            cache.put(relationshipLookup, indexModel);
//                        }
//                    }
//                }
//                ent.setIndexes(indexList);

                List<String> roleLookupList = getPathList(entity.getAllowableContextRoles());
                if (roleLookupList != null) {
                    entityRoles.put(entityLookup, roleLookupList);
                }

                ReferenceObjectModel referenceObjectModel = new ReferenceObjectModel();
                if (entity.getReferenceObject() != null) {
                    referenceObjectModel.setHeading(entity.getReferenceObject().getHeading());
                    referenceObjectModel.setSubHeading(entity.getReferenceObject().getSubHeading());
                    referenceObjectModel.setDescription(entity.getReferenceObject().getDescription());
                }
                ent.setReferenceObject(referenceObjectModel);

                entityStore.saveForGenerator(ent);
                putToSRCacheADD(ent);
                cache.put(ent.getLookup(), ent);
                break;
            case REMOVED:
                fields = entity.getFields();
                if (fields != null) {
                    for (IFieldElement field : fields) {
                        FieldModel fieldModel = fieldStore.deleteByUID(field.getUid());
                        putToSRCacheDEL(fieldModel);
                    }
                }
                indexes = entity.getIndexes();
                if (indexes != null) {
                    for (IIndexElement index : indexes) {
                        IndexModel indexModel = indexStore.deleteByUID(index.getUid());
                        putToSRCacheDEL(indexModel);
                    }
                }

                EntityModel entityModel = entityStore.deleteByUID(entity.getUid());
                putToSRCacheDEL(entityModel);
                break;
            case UPDATED:
                IEntityElement newEntity = diff.getNewElement();
                ent = entity.isSubEntity() ? new SubEntityModel() : new EntityModel();
                ent.setUid(new UID(newEntity.getUid()));
                ent.setName(newEntity.getName());
                ent.setAbstractEntity(newEntity.isAbstractEntity());
                ent.setTypeEntity(newEntity.isTypeEntity());
                ent.setSecurityEnabled(newEntity.isSecurityEnabled());
                //newEntity.getExtendedEntityPath() - > in afterEntityProcessing
                if (newEntity.getFields() == null || newEntity.getFields().length == 0) {
                    fieldList = null;
                } else {
                    fieldList = new ArrayList<FieldModel>();
                    for (IFieldElement fieldElement : newEntity.getFields()) {
                        FieldModel field = prepareField(fieldElement);
                        fieldList.add(field);
                    }
                }

//                if (newEntity.getIndexes() == null || newEntity.getIndexes().length == 0) {
//                    indexList = null;
//                } else {
//                    indexList = new ArrayList<IndexModel>();
//                    for (IIndexElement index : newEntity.getIndexes()) {
//                        IndexModel indexModel = prepareIndex(index, fieldList);
//                        indexList.add(indexModel);
//                        Reference relationship = index.getRelationship();
//                        if (relationship != null) {
//                            String relationshipPath = DiffUtils.lookupByRefPath(relationship.getRefPath());
//                            String relationshipLookup = DiffUtils.lookup(relationshipPath, relationship.getRefName()) + "#index";
////                            cache.put(relationshipLookup, indexModel);
//                        }
//                    }
//                }

                parent = registryNodeStore.findByUID(newEntity.getParent().getUid());
                if (parent != null) {
                    ent.setParent(parent);
                    ent.setPath(parent.getLookup());
                }

                referenceObjectModel = new ReferenceObjectModel();
                if (newEntity.getReferenceObject() != null) {
                    referenceObjectModel.setHeading(newEntity.getReferenceObject().getHeading());
                    referenceObjectModel.setSubHeading(newEntity.getReferenceObject().getSubHeading());
                    referenceObjectModel.setDescription(newEntity.getReferenceObject().getDescription());
                }
                ent.setReferenceObject(referenceObjectModel);

//                entityStore.mergeForGenerator(ent, fieldList, indexList);
                entityStore.mergeForGenerator(ent, fieldList, null);
                entityRoles.put(ent.getLookup(), getPathList(newEntity.getAllowableContextRoles()));
                cache.put(ent.getLookup(), ent);
                break;
        }
    }

    @Override
    protected void processRelationshipsDiff(RelationshipsDiff diff) {
        IRelationshipElement rel = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                EntityModel sourceEntity = getEntity(rel.getSource());
                String relLookup = DiffUtils.lookup(sourceEntity.getLookup(), rel.getName());
                RelationshipModel relationship = new RelationshipModel();
                relationship.setRelationshipType(rel.getType());
                relationship.setName(rel.getName());
                relationship.setParent(sourceEntity);
                relationship.setLookup(relLookup);
                relationship.setSourceEntityRefName(rel.getSource().getRefName());
                relationship.setSourceEntity(sourceEntity);
                relationship.setHint(rel.getHint());

                if (rel.getType() == RelationshipType.TREE) {
                    relationship.setTargetEntity(sourceEntity);
                    relationship.setTargetEntityRefName(rel.getSource().getRefName());
                } else {
                    EntityModel targetEntity = getEntity(rel.getTarget());
                    relationship.setTargetEntity(targetEntity);
                    relationship.setTargetEntityRefName(rel.getTarget().getRefName());
                }
                relationship.setRequired(rel.isRequired() ? Boolean.TRUE : null);
                if (rel.getOnDeleteOptions() != null) {
                    relationship.setOnDeleteOption(rel.getOnDeleteOptions());
                }
                if (rel.getOnUpdateOptions() != null) {
                    relationship.setOnUpdateOption(rel.getOnUpdateOptions());
                }
                PackageDescriptorConfigElementFactory.initializeModelUID(relationship, rel);
                relationshipStore.saveForGenerator(relationship);

//                BaseEntityModel baseEntityModel = cache.get(relationship.getLookup() + "#index");
//                if (baseEntityModel != null && (baseEntityModel instanceof IndexModel)) {
//                    IndexModel indexModel = (IndexModel) baseEntityModel;
//                    indexModel.setRelationship(relationship);
//                    indexStore.save(indexModel);
//                }

                putToSRCacheADD(relationship);
                break;
            case REMOVED:
                RelationshipModel model = relationshipStore.deleteByUID(rel.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                IRelationshipElement newRel = diff.getNewElement();
                RelationshipModel relForUpdate = new RelationshipModel();
                String path = DiffUtils.lookupByRefPath(newRel.getSource().getRefPath());
                relForUpdate.setPath(path);
                relForUpdate.setName(newRel.getName());
                relForUpdate.setLookup(DiffUtils.lookup(newRel.getPath(), newRel.getName()));
                relForUpdate.setRequired(newRel.isRequired() ? Boolean.TRUE : null);
                relForUpdate.setOnDeleteOption(newRel.getOnDeleteOptions());
                relForUpdate.setOnUpdateOption(newRel.getOnUpdateOptions());
                relForUpdate.setRelationshipType(newRel.getType());
                relForUpdate.setHint(newRel.getHint());
                String sourceRefPath = newRel.getSource().getRefPath();
                String targetRefPath = newRel.getType() == RelationshipType.TREE ?
                        sourceRefPath : newRel.getTarget().getRefPath();
                relationshipStore.mergeForGenerator(
                        rel.getUid(), relForUpdate, sourceRefPath, targetRefPath);

//                baseEntityModel = cache.get(relForUpdate.getLookup() + "#index");
//                if (baseEntityModel != null && (baseEntityModel instanceof IndexModel)) {
//                    IndexModel indexModel = (IndexModel) baseEntityModel;
//                    indexModel.setRelationship(relForUpdate);
//                    indexStore.save(indexModel);
//                }
        }
    }

    @Override
    protected void processDomainsDiff(DomainsDiff diff) {
        IDomainElement domainElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                DomainModel domain;
                if (domainElement.isVersionSubDomain()) {
                    SubDomain subDomain = new SubDomain();
                    subDomain.setVersion(this.newEntitiesManager.getNewVersion());
                    domain = subDomain;
                } else {
                    domain = new DomainModel();
                }
                domain.setName(domainElement.getName());
                domain.setLookup(DiffUtils.lookup(domainElement));
                RegistryNodeModel parent = findByLookup(
                        registryNodeStore, DiffUtils.lookupByRefPath(domainElement.getPath()));
                domain.setParent(parent);
                domain.setPrefix(domainElement.getPrefix());
                domain.setWsdlLocation(domainElement.getWsdlLocation());

                if (leaveUIDInfo(domain.getLookup())) {//required only for ADDED diff
                    PackageDescriptorConfigElementFactory.initializeModelUID(domain, domainElement);
                }

                if (domainElement.isVersionSubDomain()) {
                    String domainAsPathPrefix = domain.getLookup() + '.';
                    getSubDomainPathPrefixes().add(domainAsPathPrefix);
                    subDomainStore.saveForGenerator((SubDomain) domain);
                } else {
                    domainStore.saveForGenerator(domain);
                }
                putToSRCacheADD(domain);

                cache.put(domain.getLookup(), domain);
                break;
            case REMOVED:
                DomainModel model = domainStore.deleteByUID(domainElement.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                DomainModel oldDomain = domainStore.findByUID(domainElement.getUid());
                if (oldDomain == null) {
                    throw new OpenFlameRuntimeException(
                            "Failed to find domain [uid = " + domainElement.getUid() +
                                    "] for update operation");
                }
                oldDomain.setName(diff.getNewElement().getName());
                oldDomain.setPath(DiffUtils.lookupByRefPath(diff.getNewElement().getPath()));
                oldDomain.setLookup(DiffUtils.lookup(
                        oldDomain.getPath(), diff.getNewElement().getName()));
                parent = findByLookup(registryNodeStore,
                        DiffUtils.lookupByRefPath(diff.getNewElement().getPath()));
                oldDomain.setParent(parent);
                oldDomain.setDescription(diff.getNewElement().getDescription());
                oldDomain.setPrefix(diff.getNewElement().getPrefix());

                domainStore.saveForGenerator(oldDomain);
        }
    }

    @Override
    protected void processActionsDiff(ActionsDiff diff) {
        ActionElement actionElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                if (!leaveUIDInfo(DiffUtils.lookupByRefPath(
                        actionElement.getPath() + '.' + actionElement.getName()))) {
                    actionElement.setUid(null);
                    List<ActionParameterElement> parameters = actionElement.getParameters();
                    if (parameters != null) {
                        for (ActionParameterElement parameter : parameters) {
                            parameter.setUid(null);
                        }
                    }
                }
                ActionModel action = actionElementFactory.getEntity(actionElement);
                actionStore.saveForGenerator(action);
                putToSRCacheADD(action);
                getActions().put(action.getLookup(), action);
                break;
            case REMOVED:
                ActionModel model = actionStore.deleteByUID(actionElement.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                ActionModel oldAction = actionStore.findByUID(actionElement.getUid());
                if (oldAction == null) {
                    throw new OpenFlameRuntimeException(
                            "Failed to find the action [uid = " + actionElement.getUid() + "] for update operation");
                }
                ActionModel actionFromXml = actionElementFactory.getEntity(diff.getNewElement());
                copyBasicPropertiesForUpdateOperation(oldAction, actionFromXml);
                oldAction.setUrlPath(actionFromXml.getUrlPath());
                oldAction.setMethod(actionFromXml.getMethod());
                oldAction.setSoapUrlPath(actionFromXml.getSoapUrlPath());
                oldAction.setSoapMethod(actionFromXml.getSoapMethod());

                oldAction.setInputVOEntity(actionFromXml.getInputVOEntity());
                oldAction.setOutputVOEntity(actionFromXml.getOutputVOEntity());

                List<ActionParameterModel> oldParameters = oldAction.getActionParameters();
                List<ActionParameterModel> parametersFromXml = actionFromXml.getActionParameters();

                if (oldParameters == null ^ parametersFromXml == null) {
                    if (oldParameters == null) {
                        oldAction.setActionParameters(parametersFromXml);
                    } else {
                        //no action parameters specified for the action,
                        //- so set parameters to null, old parameters will be deleted
                        oldAction.setActionParameters(null);
                    }
                } else if (oldParameters != null) {
                    Map<String, ActionParameterModel> oldParamsMap = new HashMap<String, ActionParameterModel>();
                    for (ActionParameterModel param : oldParameters) {
                        oldParamsMap.put(param.getUid().getUid(), param);
                    }
                    //Map<String, ActionParameter> newParamsMap = prepareParams(parametersFromXml);
                    List<ActionParameterModel> resultParameters = new ArrayList<ActionParameterModel>();
                    for (ActionParameterModel param : parametersFromXml) {
                        ActionParameterModel oldParam;
                        if ((oldParam = oldParamsMap.get(param.getUid().getUid())) == null) {
                            resultParameters.add(param);
                        } else {
                            /*param.setId(oldParam.getId());
                                                               param.setUid(oldParam.getUid());
                                                               resultParameters.add(param);*/
                            oldParam.setFieldType(param.getFieldType());
                            oldParam.setLocation(param.getLocation());
                            oldParam.setOrderPosition(param.getOrderPosition());
                            oldParam.setName(param.getName());
                            resultParameters.add(oldParam);
                        }
                    }
                    oldAction.setActionParameters(resultParameters);
                }

                actionStore.saveForGenerator(oldAction);
        }
    }

    @Override
    protected void processNavigationElementsDiff(NavigationElementsDiff diff) {
        NavigationConfigElement diffTarget = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                NavigationElementModel navElement = navigationElementFactory.getEntity(diffTarget);
                RegistryNodeModel main = navElement.getMain();
                if (main != null) {
                    String lookup = DiffUtils.lookup(main.getPath(), main.getName());
                    main = (RegistryNodeModel) cache.get(lookup);
                    navElement.setMain(main);
                }
                navigationStore.saveForGenerator(navElement);
                getNavigations().put(navElement.getLookup(), navElement);
                putToSRCacheADD(navElement);
                break;
            case REMOVED:
                NavigationElementModel model = navigationStore.deleteByUID(diffTarget.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                NavigationElementModel nav = navigationStore.findByUID(diffTarget.getUid());
                if (nav == null) {
                    throw new OpenFlameRuntimeException(
                            "Failed to find the action [uid = " + diffTarget.getUid() +
                                    "] for update operation");
                }
                NavigationElementModel navFromXml = navigationElementFactory.getEntity(diff.getNewElement());
                copyBasicPropertiesForUpdateOperation(nav, navFromXml);
                nav.setUrlPath(navFromXml.getUrlPath());
                nav.setSortPosition(navFromXml.getSortPosition());

                main = navFromXml.getMain();
                if (main != null) {
                    String lookup = DiffUtils.lookup(main.getPath(), main.getName());
                    main = (RegistryNodeModel) cache.get(lookup);
                    nav.setMain(main);
                }
                navigationStore.save(nav);
        }
    }

    @Override
    protected void processResourceLocationsDiff(ResourceLocationsDiff diff) {
        ResourceLocationElement rlElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                ResourceLocationModel resourceLocation = resourceLocationFactory.getEntity(rlElement);
                resourceLocationStore.saveForGenerator(resourceLocation);
                putToSRCacheADD(resourceLocation);
                ResourceLocation rl = modelFactory.convertTo(ResourceLocation.class, resourceLocation);
                getResourceLocations().put(resourceLocation.getLookup(), rl);
                break;
            case REMOVED:
                ResourceLocationModel model = resourceLocationStore.deleteByUID(rlElement.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                ResourceLocationModel oldRL = resourceLocationStore.findByUID(rlElement.getUid());
                ResourceLocationModel rlFromXml = resourceLocationFactory.getEntity(diff.getNewElement());
                copyBasicPropertiesForUpdateOperation(oldRL, rlFromXml);
                oldRL.setWildcardStyle(diff.getNewElement().getWildcardStyle());
                oldRL.setUrlPath(diff.getNewElement().getUrlPath());

                resourceLocationStore.save(oldRL);
        }
    }

    @Override
    protected void processPermissionsDiff(PermissionsDiff diff) {
        if (diff.getType() == DifferenceType.ADDED) {
            PermissionModel permissionModel = permissionElementFactory.getEntity(diff.getDiffTarget());
            permissionStore.saveForGenerator(permissionModel);
            putToSRCacheADD(permissionModel);
            cache.put(permissionModel.getLookup(), permissionModel);
            Permission permission = modelFactory.convertTo(Permission.class, permissionModel);
            getPermissions().put(permission.getLookup(), permission);
        } else if (diff.getType() == DifferenceType.REMOVED) {
            PermissionModel model = permissionStore.deleteByUID(diff.getDiffTarget().getUid());
            putToSRCacheDEL(model);
        }
    }

    @Override
    protected void processRolesDiff(RolesDiff diff) {
        RoleElement roleElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                RoleModel roleFromTheStore = roleStore.findByUID(roleElement.getUid());
                RoleModel role = roleElementFactory.getEntity(roleElement);
                if (roleFromTheStore == null) {
                    roleStore.saveForGenerator(role);
                    putToSRCacheADD(role);
                } else {
                    RoleModel mergedRole = roleStore.mergeForGenerator(role);
                    role.setId(mergedRole.getId());
                }

                List<String> permissionLookupList = getRolePermissionLookupList(diff.getDiffTarget());
                if (permissionLookupList != null && !permissionLookupList.isEmpty()) {
                    getRoleAssociationMap().put(role.getId(), permissionLookupList);
                }
                break;
            case REMOVED:
                RoleModel model = roleStore.deleteByUID(roleElement.getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                roleFromTheStore = roleStore.findByUID(roleElement.getUid());
                if (roleFromTheStore == null) { //nothing to update because role was deleted before(on parent element deletion etc)
                    logger.info("Role [ uid = " + roleElement.getUid() + " ] already has deleted.");
                } else {
                    role = roleElementFactory.getEntity(diff.getNewElement());
                    RoleModel mergedRole = roleStore.mergeForGenerator(role);
                    role.setId(mergedRole.getId());
                    if (diff.isPermissionAssignmentsUpdated()) {
                        List<String> newPermissionLookupList = getRolePermissionLookupList(diff.getNewElement());
                        if (newPermissionLookupList != null) {//if newPermissionLookupList != null && newPermissionLookupList.isEmpty() - this will delete all role permissions for the role
                            getRoleAssociationMap().put(mergedRole.getId(), newPermissionLookupList);
                        }
                    }
                }
        }
    }

    @Override
    protected void processActorsDiff(ActorsDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                ActorElement actorElement = diff.getDiffTarget();
                List<UserAssignElement> users = actorElement.getUsers();
                List<RoleAssignElement> roles = actorElement.getRoles();
                List<GroupAssignElement> groups = actorElement.getGroups();

                ActorModel actor = actorElementFactory.getEntity(actorElement);

                if (users != null && !users.isEmpty()) {
                    List<UserActorModel> userList = new ArrayList<UserActorModel>();
                    for (UserAssignElement user : users) {
                        UserModel usr = userStore.findUserByUsername(user.getName());
                        if (usr != null) {
                            UserActorModel userActor = new UserActorModel();
                            userActor.setActor(actor);
                            userActor.setUser(usr);
                            userList.add(userActor);
                        }
                    }
                    actor.setUserActors(userList);
                }

                if (roles != null && !roles.isEmpty()) {
                    List<RoleModel> roleList = new ArrayList<RoleModel>();
                    for (RoleAssignElement role : roles) {
                        RoleModel role1 = findByLookup(roleStore, role.getPath());
                        if (role1 != null) {
                            roleList.add(role1);
                        }
                    }
                    actor.setRoles(roleList);
                }

                if (groups != null && !groups.isEmpty()) {
                    List<GroupModel> groupList = new ArrayList<GroupModel>();
                    for (GroupAssignElement group : groups) {
                        GroupModel group1 = findByLookup(groupStore, group.getPath());
                        if (group1 != null) {
                            groupList.add(group1);
                        }
                    }
                    actor.setGroups(groupList);
                }

                actorStore.saveForGenerator(actor);
                putToSRCacheADD(actor);
                cache.put(actor.getLookup(), actor);
                break;
            case REMOVED:
                ActorModel model = actorStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                actorElement = diff.getNewElement();
                users = actorElement.getUsers();
                roles = actorElement.getRoles();
                groups = actorElement.getGroups();

                List<String> userLookupList = getLookupList(users);
                List<String> roleLookupList = getLookupList(roles);
                List<String> groupLookupList = getLookupList(groups);
                actor = actorElementFactory.getEntity(actorElement);
                actorStore.mergeForGenerator(
                        actor, userLookupList, roleLookupList, groupLookupList);
        }
    }

    @Override
    protected void processConfigsDiff(ConfigsDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                ConfigModel config = configElementFactory.getEntity(diff.getDiffTarget());
                configStore.saveForGenerator(config);
                putToSRCacheADD(config);
                break;
            case REMOVED:
                ConfigModel model = configStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                ConfigReference newConfig = diff.getNewElement();
                ConfigModel oldConfig = configStore.findByUID(newConfig.getUid());
                if (oldConfig == null) {
                    throw new OpenFlameRuntimeException(
                            "Folder to change [uid = " + newConfig.getUid() + "] was not found.");
                }
                ConfigModel configFromXml = configElementFactory.getEntity(newConfig);
                copyBasicPropertiesForUpdateOperation(oldConfig, configFromXml);
                oldConfig.setValue(configFromXml.getValue());
                configStore.save(oldConfig);
        }
    }

    @Override
    protected void processDirectoriesDiff(DirectoriesDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                DirectoryModel directory = rnDirectoryFactory.getEntity(diff.getDiffTarget());
                directoryStore.saveForGenerator(directory);
                putToSRCacheADD(directory);
                break;
            case REMOVED:
                DirectoryModel model = directoryStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                DirectoryElement newDirectory = diff.getNewElement();
                DirectoryModel oldDirectory = directoryStore.findByUID(newDirectory.getUid());
                if (oldDirectory == null) {
                    throw new OpenFlameRuntimeException(
                            "Directory to change [uid = " + newDirectory.getUid() + "] was not found.");
                }
                DirectoryModel directoryFromXml = rnDirectoryFactory.getEntity(newDirectory);
                copyBasicPropertiesForUpdateOperation(oldDirectory, directoryFromXml);
                oldDirectory.setDirectoryType(directoryFromXml.getDirectoryType());
                oldDirectory.setServerName(directoryFromXml.getServerName());//not dynamic properties now, because this property will not change if the PACKAGE will be associated with the SYSTEM
                oldDirectory.setUrlPath(directoryFromXml.getUrlPath());//not dynamic properties now, because this property will not change if the PACKAGE will be associated with the SYSTEM
                oldDirectory.setStatus(directoryFromXml.getStatus());

                directoryStore.save(directoryFromXml);
        }
    }

    @Override
    protected void processGroupsDiff(GroupsDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                GroupModel group = groupFactory.getEntity(diff.getDiffTarget());
                groupStore.saveForGenerator(group);
                putToSRCacheADD(group);
                break;
            case REMOVED:
                GroupModel model = groupStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                GroupModel oldGroup = groupStore.findByUID(diff.getDiffTarget().getUid());
                if (oldGroup == null) {
                    throw new OpenFlameRuntimeException(
                            "Group to change [uid = " + diff.getDiffTarget().getUid() +
                                    "] was not found.");
                }
                GroupModel groupFromXML = groupFactory.getEntity(diff.getNewElement());
                copyBasicPropertiesForUpdateOperation(oldGroup, groupFromXML);
                oldGroup.setDirectory(groupFromXML.getDirectory());
                groupStore.save(oldGroup);
        }
    }

    @Override
    protected void processSchedulesDiff(SchedulesDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                ScheduleModel schedule = scheduleFactory.getEntity(diff.getDiffTarget());
                scheduleStore.save(schedule);
                putToSRCacheADD(schedule);
                break;
            case REMOVED:
                ScheduleModel model = scheduleStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                ScheduleModel oldSchedule = scheduleStore.findByUID(diff.getDiffTarget().getUid());
                if (oldSchedule == null) {
                    throw new OpenFlameRuntimeException(
                            "Schedule to change [uid = " + diff.getDiffTarget().getUid() +
                                    "] was not found.");
                }
                ScheduleModel scheduleFromXML = scheduleFactory.getEntity(diff.getNewElement());
                copyBasicPropertiesForUpdateOperation(oldSchedule, scheduleFromXML);
                oldSchedule.setAction(scheduleFromXML.getAction());
                scheduleStore.save(oldSchedule);
        }
    }

    @Override
    protected void processFoldersDiff(FoldersDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                FolderModel folder = rnFolderFactory.getEntity(diff.getDiffTarget());
                folderStore.saveForGenerator(folder);
                putToSRCacheADD(folder);
                break;
            case REMOVED:
                //consider situations when config are child of folder.
                //By default, list of element's diff are sorted
                try {
                    FolderModel model = folderStore.deleteByUID(diff.getDiffTarget().getUid());
                    putToSRCacheDEL(model);
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
                break;
            case UPDATED:
            default:
                FolderElement newFolder = diff.getNewElement();
                FolderModel oldFolder = folderStore.findByUID(newFolder.getUid());
                if (oldFolder == null) {
                    throw new OpenFlameRuntimeException(
                            (new StringBuilder("Folder to change [uid = "))
                                    .append(newFolder.getUid()).append("; version = ")
                                    .append(this.newEntitiesManager.getNewVersion())
                                    .append("] was not found.").toString());
                }
                FolderModel folderFromXml = rnFolderFactory.getEntity(newFolder);
                copyBasicPropertiesForUpdateOperation(oldFolder, folderFromXml);
                folderStore.save(oldFolder);
        }
    }

    @Override
    protected void processProcessesDiff(ProcessesDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                ProcessModel process = processElementFactory.getEntity(diff.getDiffTarget());
                processStore.saveForGenerator(process);
                putToSRCacheADD(process);
                break;
            case REMOVED:
                ProcessModel model = processStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                process = processElementFactory.getEntity(diff.getNewElement());
                processStore.mergeForGenerator(process);
        }
    }

    @Override
    protected void processResourcesDiff(ResourcesDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                ResourceModel resource = resourceElementFactory.getEntity(diff.getDiffTarget());
                ResourceVersionElement[] resourceVersionElements =
                        resourceElementFactory.getRNResourceVersions(diff.getDiffTarget());
                registryNodeStore.saveForGenerator(resource);
                putToSRCacheADD(resource);
                if (resourceVersionElements != null && resourceVersionElements.length > 0) {
                    List<AbstractResourceVersionModel> resourceVersionList = new ArrayList<AbstractResourceVersionModel>();
                    for (ResourceVersionElement resourceVersionElement : resourceVersionElements) {
                        AbstractResourceVersionModel resourceVersion =
                                resourceVersionElementFactory.getResourceVersion(resourceVersionElement, resource);
                        resourceVersionList.add(resourceVersion);
                    }
                    resourceVersionStore.saveOrUpdateAll(resourceVersionList);
                    ////////////////
                    int i = 0;
                    for (AbstractResourceVersionModel resourceVersion : resourceVersionList) {
                        if (resourceVersion instanceof IStorableResourceVersionModel) {
                            String storedFileName = resourceVersion.getResourceStoredFileName();
                            String resourcePath = String.valueOf(resourceVersion.getResource().getId());
                            String  resourcePathFolder = null;
                            if (resourceVersion instanceof ImageResourceVersionModel) {
                                resourcePathFolder = helper.getImage();
                            } else if (resourceVersion instanceof AudioResourceVersionModel) {
                                resourcePathFolder = helper.getAudio();
                            } else if (resourceVersion instanceof VideoResourceVersionModel) {
                                resourcePathFolder = helper.getVideo();
                            }
                            if (resourcePathFolder != null) {
	                            String resourceFilename = ((IStorableResourceVersionDescriptorElement) resourceVersionElements[i]).getResourceFilename();
	                            InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,resourceFilename, helper.getTemp());
                                if (stream != null) {
                                    OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, storedFileName, stream, resourcePathFolder, resourcePath);
                                    IOUtils.closeQuietly(stream);
                                }
                            }
                        }
                        i++;
                    }
                    ////////////////
                }
                break;
            case REMOVED:
                AbstractResourceModel model = (AbstractResourceModel)
                        resourceStore.deleteByUID(diff.getDiffTarget().getUid());//deletes resource version and resource as well
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                ResourceModel resourceFromXml = resourceElementFactory.getEntity(diff.getNewElement());
                List<AbstractResourceVersionModel> resourceVersionsFromXml;
                resourceVersionElements = resourceElementFactory.getRNResourceVersions(diff.getNewElement());
                if (resourceVersionElements == null || resourceVersionElements.length == 0) {
                    resourceVersionsFromXml = null;
                } else {
                    resourceVersionsFromXml = new ArrayList<AbstractResourceVersionModel>(resourceVersionElements.length);
                    for (ResourceVersionElement rvElement : resourceVersionElements) {
                        AbstractResourceVersionModel resourceVersion =
                                resourceVersionElementFactory.getResourceVersion(rvElement, resourceFromXml);
                        resourceVersionsFromXml.add(resourceVersion);
                    }
                }
                resourceStore.mergeForGenerator(resourceFromXml, resourceVersionsFromXml);
        }
    }

    @Override
    protected void processCollectionsDiff(CollectionsDiff diff) {
        switch (diff.getType()) {
            case ADDED:
                CollectionModel collection = collectionElementFactory.getEntity(diff.getDiffTarget());
                collectionStore.saveForGenerator(collection);
                putToSRCacheADD(collection);
                break;
            case REMOVED:
                CollectionModel model = collectionStore.deleteByUID(diff.getDiffTarget().getUid());
                putToSRCacheDEL(model);
                break;
            case UPDATED:
            default:
                collection = collectionElementFactory.getEntity(diff.getNewElement());
                collectionStore.mergeForGenerator(collection);
        }
    }

    @Override
    protected void processRoleAssignmentsDiff(RoleAssignmentsDiff diff) {
        if (diff.getType() == DifferenceType.ADDED) {//only 'ADDED' diff type supported
            RoleAssignmentElement roleAssignment = diff.getDiffTarget();
            if (StringUtils.isNotBlank(roleAssignment.getPath()) &&
                    roleAssignment.getPermissions() != null && !roleAssignment.getPermissions().isEmpty()) {
                String roleLookup = roleAssignment.getPath();
                RoleModel role = findByLookup(roleStore, roleLookup);
                if (role != null) {
                    List<RolePermissionReference> rolePermissionList = roleAssignment.getPermissions();
                    List<String> permissionLookupList = new ArrayList<String>();
                    for (RolePermissionReference ref : rolePermissionList) {
                        if (StringUtils.isNotBlank(ref.getPath())) {
                            permissionLookupList.add(DiffUtils.lookupByRefPath(ref.getPath()));
                        }
                    }
                    if (!permissionLookupList.isEmpty()) {
                        List<PermissionModel> permissions = findAllByLookupList(permissionStore, permissionLookupList);
                        if (permissions != null && !permissions.isEmpty()) {
                            Set<PermissionModel> permissionSet = getRolePermissionsMap().get(role.getId());
                            if (permissionSet == null) {
                                permissionSet = new HashSet<PermissionModel>();
                                getRolePermissionsMap().put(role.getId(), permissionSet);
                            }
                            permissionSet.addAll(permissions);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void processUsersDiff(UsersDiff diff) {
        UserElement userElement = diff.getDiffTarget();
        if (diff.getType() == DifferenceType.ADDED) {
            UserModel user = rnUserFactory.getEntity(userElement);
            UserModel userInStore = userStore.findUserByUsername(user.getUsername());
            if (userInStore != null) {
                userInStore.setFirstName(user.getFirstName());
                userInStore.setLastName(user.getLastName());
                userInStore.setMiddleName(user.getMiddleName());
                userInStore.setEmail(user.getEmail());
                userInStore.setPassword(user.getPassword());
                user = userInStore;
            }
            userStore.saveOrUpdate(user);

            List<UserRoleRef> userRoleRefs = userElement.getUserRoles();
            if (userRoleRefs != null && !userRoleRefs.isEmpty()) {
                List<String> roleLookupList = new ArrayList<String>();
                for (UserRoleRef ref : userRoleRefs) {
                    if (StringUtils.isNotBlank(ref.getPath())) {
                        roleLookupList.add(DiffUtils.lookupByRefPath(ref.getPath()));
                    }
                }
                if (!roleLookupList.isEmpty()) {
                    getUserRoleAssociationMap().put(user.getId(), roleLookupList);
                }
            }
        }
    }

    @Override
    protected void processUserProfileFieldGroupsDiff(UserProfileFieldGroupsDiff diff) {
        UserProfileFieldGroupElement profileFieldGroupElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                UserProfileFieldGroupModel model = profileFieldGroupElementFactory.getEntity(profileFieldGroupElement);
                profileFieldGroupStore.saveForGenerator(model);
                cache.put(model.getLookup(), model);
                break;
            case REMOVED:
                profileFieldGroupStore.deleteByUID(profileFieldGroupElement.getUid());
                break;
            case UPDATED:
            default:
                model = profileFieldGroupElementFactory.getEntity(profileFieldGroupElement);
                profileFieldGroupStore.mergeForGenerator(model);
        }
    }

    @Override
    protected void processUserProfileFieldsDiff(UserProfileFieldsDiff diff) {
        UserProfileFieldElement profileFieldElement = diff.getDiffTarget();
        switch (diff.getType()) {
            case ADDED:
                UserProfileFieldModel model = profileFieldElementFactory.getEntity(profileFieldElement);
                String groupLookup = profileFieldElement.getGroupLookup();
                if (StringUtils.isBlank(groupLookup)) {
                    profileFieldStore.saveForGenerator(model);
                } else {
                    List<UserProfileFieldModel> userProfileFieldModels = this.userProfileFields.get(groupLookup);
                    if (userProfileFieldModels == null) {
                        userProfileFieldModels = new ArrayList<UserProfileFieldModel>();
                        this.userProfileFields.put(groupLookup, userProfileFieldModels);
                    }
                    userProfileFieldModels.add(model);
                }
                break;
            case REMOVED:
                profileFieldStore.deleteByUID(profileFieldElement.getUid());
                break;
            case UPDATED:
            default:
                model = profileFieldElementFactory.getEntity(profileFieldElement);
                if (model.getUserProfileFieldGroup() == null) {
                    profileFieldStore.mergeForGenerator(model);
                } else {
                    UserProfileFieldModel oldModel = profileFieldStore.findByUID(profileFieldElement.getUid());
                    model.setId(oldModel.getId());
                    String fieldGroupLookup = model.getUserProfileFieldGroup().getLookup();
                    List<UserProfileFieldModel> userProfileFieldModels =
                            this.userProfileFields.get(fieldGroupLookup);
                    if (userProfileFieldModels == null) {
                        userProfileFieldModels = new ArrayList<UserProfileFieldModel>();
                        this.userProfileFields.put(fieldGroupLookup, userProfileFieldModels);
                    }
                    userProfileFieldModels.add(model);
                }
        }
    }

    @Override
    protected void afterEntityProcessing(IElementDiffInfoContainer elementDiffContainer) {
        for (EntitiesDiff diff : elementDiffContainer.getEntityDiffs()) {
            if (diff.getType() == DifferenceType.ADDED) {
                processExtendedEntityProperty(this.newEntitiesManager, diff.getDiffTarget());
            } else if (diff.getType() == DifferenceType.UPDATED) {
                processExtendedEntityProperty(this.newEntitiesManager, diff.getNewElement());
            }
        }
        super.afterEntityProcessing(elementDiffContainer);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void afterTranslate(IElementDiffInfoContainer elementDiffContainer) {
        for (Map.Entry<Long, List<String>> rolePermissionsEntry : getRoleAssociationMap().entrySet()) {
            List<String> permissionLookupList = rolePermissionsEntry.getValue();
            if (permissionLookupList != null && !permissionLookupList.isEmpty()) {
                List<PermissionModel> permissions = findAllByLookupList(permissionStore, permissionLookupList);
                if (permissions != null && !permissions.isEmpty()) {
                    RoleModel role = roleStore.saveRolePermissions(rolePermissionsEntry.getKey(), permissions);
                    cache.put(role.getLookup(), role);
                }
            }
        }
        for (Long userId : getUserRoleAssociationMap().keySet()) {
            List<String> roleLookupList = getUserRoleAssociationMap().get(userId);
            if (roleLookupList != null && !roleLookupList.isEmpty()) {
                List<RoleModel> roles = findAllByLookupList(roleStore, roleLookupList);
                if (roles != null && !roles.isEmpty()) {
                    userStore.saveUserRoles(userId, roles);
                }
            }
        }

        if (!getRolePermissionsMap().isEmpty()) {
            roleStore.addRolePermissions(packageId, getRolePermissionsMap());
        }

        for (Map.Entry<String, List<UserProfileFieldModel>> entry : this.userProfileFields.entrySet()) {
            UserProfileFieldGroupModel fieldGroupModel = findByLookup(profileFieldGroupStore, entry.getKey());
            if (fieldGroupModel == null) {
                logger.warn("Failed to find UserProfileFieldGroupModel by lookup [" + entry.getKey() + "].");
            }
            List<UserProfileFieldModel> newProfileFields = new ArrayList<UserProfileFieldModel>();
            List<UserProfileFieldModel> profileFieldsToMerge = new ArrayList<UserProfileFieldModel>();
            for (UserProfileFieldModel profileFieldModel : entry.getValue()) {
                profileFieldModel.setUserProfileFieldGroup(fieldGroupModel);
                if (profileFieldModel.getId() == null) {
                    newProfileFields.add(profileFieldModel);
                } else {
                    profileFieldsToMerge.add(profileFieldModel);
                }
            }
            if (!newProfileFields.isEmpty()) {
                profileFieldStore.saveForGenerator(newProfileFields);
            }
            if (!profileFieldsToMerge.isEmpty()) {
                profileFieldStore.mergeForGenerator(profileFieldsToMerge);
            }
        }

        List<LookupModel<?>> actorList = srCacheForAdd.get(RegistryNodeType.ACTOR);
        if (actorList != null && !actorList.isEmpty()) {
            List<Long> actorIdList = new ArrayList<Long>();
            for (LookupModel<?> actorModel : actorList) {
                actorIdList.add(actorModel.getId());
            }
            actorStore.assignAdminRoleToActor(actorIdList);
        }

        updateDataInCache();
        processSecuredRecords();
        entityStore.saveEntityRoles(this.entityRoles);
        getResultState().setSuccessStatus(true);
    }

    @Override
    protected void beforeDiffProcessing(IElementDiffInfoContainer elementDiffContainer) {
        super.beforeDiffProcessing(elementDiffContainer);
        processGeneratedFields(elementDiffContainer);
    }

    private <T extends PackageDescriptorElement<?>> List<String> getLookupList(List<T> items) {
        List<String> lookupList;
        if (items == null) {
            lookupList = null;
        } else {
            lookupList = new ArrayList<String>();
            for (T item : items) {
                if (StringUtils.isNotBlank(item.getPath())) {
                    lookupList.add(item.getPath());
                }
            }
        }
        return lookupList;
    }

    private void processExtendedEntityProperty(
            EntityElementManager manager, IEntityElement packageEntity) {
        if (StringUtils.isNotBlank(packageEntity.getExtendedEntityPath())) {
            String entityLookup = DiffUtils.lookup(packageEntity);
            String extendedEntityLookup;
            if (manager.isInternalPath(packageEntity.getExtendedEntityPath())) {
                try {
                    IEntityElement extendedEntity =
                            manager.lookup(packageEntity.getExtendedEntityPath());
                    extendedEntityLookup = DiffUtils.lookup(extendedEntity);
                } catch (TypeLookupException e) {
                    throw new TranslationRuntimeException(e);
                }
            } else {
                String rootDomainName = DiffUtils.findRootDomainName(packageEntity.getExtendedEntityPath());
                if (StringUtils.isBlank(rootDomainName)) {
                    logger.error(MSG_FAILED_TO_RETRIEVE_ROOT_DOMAIN_NAME1 +
                            packageEntity.getExtendedEntityPath() + MSG_FAILED_TO_RETRIEVE_ROOT_DOMAIN_NAME2);
                    throw new TranslationRuntimeException(MSG_WRONG_ROOT_DOMAIN_NAME);
                }
                String rootDomainLookup = DiffUtils.rootDomainLookup(rootDomainName);
                if (findByLookup(rootDomainStore, rootDomainLookup) != null) {
                    extendedEntityLookup = DiffUtils.lookupByRefPath(packageEntity.getExtendedEntityPath());
                } else {
                    return;
                }
            }
            EntityModel entity = findByLookup(entityStore, entityLookup);
            if (entity != null) {
                EntityModel extendedEntity = findByLookup(entityStore, extendedEntityLookup);
                if (extendedEntity != null) {
                    entityStore.saveExtendedEntity(entity, extendedEntity);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void copyBasicPropertiesForUpdateOperation(LookupModel oldModel, LookupModel modelFromXml) {
        oldModel.setName(modelFromXml.getName());
        oldModel.setPath(modelFromXml.getPath());
        oldModel.setLookup(modelFromXml.getLookup());
        oldModel.setParent(modelFromXml.getParent());
        oldModel.setDescription(modelFromXml.getDescription());
    }

    private List<String> getRolePermissionLookupList(RoleElement role) {
        List<String> permissionLookupList;
        List<RolePermissionReference> rolePermissionList = role.getPermissions();
        if (rolePermissionList != null && !rolePermissionList.isEmpty()) {
            permissionLookupList = new ArrayList<String>();
            for (RolePermissionReference ref : rolePermissionList) {
                if (StringUtils.isNotBlank(ref.getPath())) {
                    permissionLookupList.add(DiffUtils.lookupByRefPath(ref.getPath()));
                }
            }
        } else {
            permissionLookupList = null;
        }
        return permissionLookupList;
    }

    private void updateDataInCache() {
        if (ConfigContainer.isAppInstalled()) {
            //update actions in cache
            List<Action> actions = new LinkedList<Action>();
            for (Map.Entry<String, ActionModel> actionEntry : getActions().entrySet()) {
                Action action = modelFactory.convertTo(Action.class, actionEntry.getValue());
                if (getPermissions().containsKey(action.getLookup())) {
                    List<Permission> permissions = new ArrayList<Permission>();
                    Permission permission = getPermissions().get(action.getLookup());
                    permissions.add(permission);
                    action.setPermissions(permissions);
                }
                actions.add(action);
            }
            cacheProcessor.addActions(actions);
            //update navigation elements in cache
            List<NavigationElement> navigationList = new LinkedList<NavigationElement>();
            for (Map.Entry<String, NavigationElementModel> navigationEntry : getNavigations().entrySet()) {
                NavigationElement nav = modelFactory.convertTo(NavigationElement.class, navigationEntry.getValue());
                if (getPermissions().containsKey(nav.getLookup())) {
                    List<Permission> permissions = new ArrayList<Permission>();
                    Permission permission = getPermissions().get(nav.getLookup());
                    permissions.add(permission);
                    nav.setPermissions(permissions);
                }
                navigationList.add(nav);
            }
            cacheProcessor.addNavigationElements(navigationList);

            List<ResourceLocation> rlList = new ArrayList<ResourceLocation>();
            for (Map.Entry<String, ResourceLocation> rlEntry : getResourceLocations().entrySet()) {
                ResourceLocation resourceLocation = rlEntry.getValue();
                if (getPermissions().containsKey(resourceLocation.getLookup())) {
                    List<Permission> permissions = new ArrayList<Permission>();
                    Permission permission = getPermissions().get(resourceLocation.getLookup());
                    permissions.add(permission);
                    resourceLocation.setPermissions(permissions);
                }
                rlList.add(resourceLocation);
            }
            cacheProcessor.addResourceLocations(rlList);
        }
    }

	@Override
	protected void processReportDiff(ReportDiff diff) {
		switch (diff.getType()) {
			case ADDED:
				ReportModel reportModel = reportElementFactory.getEntity(diff.getDiffTarget());
				reportStore.save(reportModel);
				putToSRCacheADD(reportModel);
                cache.put(reportModel.getLookup(), reportModel);
				break;
			case REMOVED:
				ReportModel model = reportStore.deleteByUID(diff.getDiffTarget().getUid());
				putToSRCacheDEL(model);
				break;
			case UPDATED:
			default:
				ReportModel oldReport = reportStore.findByUID(diff.getDiffTarget().getUid());
				if (oldReport == null) {
					throw new OpenFlameRuntimeException(
							"Report to change [uid = " + diff.getDiffTarget().getUid() +
									"] was not found.");
				}
				ReportModel reportFromXML = reportElementFactory.getEntity(diff.getNewElement());
				copyBasicPropertiesForUpdateOperation(oldReport, reportFromXML);
				oldReport.setFields(reportFromXML.getFields());
				reportStore.save(oldReport);
                cache.put(oldReport.getLookup(), oldReport);
		}
	}

	@Override
	protected void processBIReportDiff(BIReportDiff diff) {
		switch (diff.getType()) {
			case ADDED:
				BIReportModel reportModel = biReportElementFactory.getEntity(diff.getDiffTarget());
				biReportStore.save(reportModel);
				putToSRCacheADD(reportModel);
                cache.put(reportModel.getLookup(), reportModel);
				break;
			case REMOVED:
				BIReportModel model = biReportStore.deleteByUID(diff.getDiffTarget().getUid());
				putToSRCacheDEL(model);
				break;
			case UPDATED:
			default:
				BIReportModel oldReport = biReportStore.findByUID(diff.getDiffTarget().getUid());
				if (oldReport == null)
					throw new OpenFlameRuntimeException(
							"BI Report to change [uid = " + diff.getDiffTarget().getUid() +
									"] was not found.");

				BIReportModel reportFromXML = biReportElementFactory.getEntity(diff.getNewElement());
				copyBasicPropertiesForUpdateOperation(oldReport, reportFromXML);
				oldReport.setFields(reportFromXML.getFields());
				biReportStore.save(oldReport);
                cache.put(oldReport.getLookup(), oldReport);
		}
	}

    @Override
   	protected void processWizardDiff(WizardDiff diff) {
   		switch (diff.getType()) {
   			case ADDED:
   				WizardModel wizardModel = wizardElementFactory.getEntity(diff.getDiffTarget());
   				wizardStore.save(wizardModel);
   				putToSRCacheADD(wizardModel);
                cache.put(wizardModel.getLookup(), wizardModel);
   				break;
   			case REMOVED:
                WizardModel model = wizardStore.deleteByUID(diff.getDiffTarget().getUid());
   				putToSRCacheDEL(model);
   				break;
   			case UPDATED:
   			default:
                WizardModel oldWizard = wizardStore.findByUID(diff.getDiffTarget().getUid());
   				if (oldWizard == null) {
   					throw new OpenFlameRuntimeException(
   							"Report to change [uid = " + diff.getDiffTarget().getUid() +
   									"] was not found.");
   				}
                WizardModel wizardFromXML = wizardElementFactory.getEntity(diff.getNewElement());
   				copyBasicPropertiesForUpdateOperation(oldWizard, wizardFromXML);
   				oldWizard.setFields(wizardFromXML.getFields());
                wizardStore.save(oldWizard);
                cache.put(oldWizard.getLookup(), oldWizard);
   		}
   	}

	private FieldModel prepareField(IFieldElement field) {
        FieldModel f = new FieldModel();
        f.setName(field.getName());
        f.setFieldType(field.getType());
        f.setRequired(field.isRequired());
        f.setAutoGenerated(field.isAutoGenerated());
	    f.setCustomFieldType(field.getCustomType());
	    f.setSearchable(field.isSearchable());
		f.setDisplayName(field.getDisplayName());
		f.setDisplayDescription(field.getDisplayDescription());
		AllowedFieldValuesListUserType userType = new AllowedFieldValuesListUserType();
		f.setAllowedFieldValueList(userType.deserialize(field.getAllowValues()));
        if (field.isAutoGenerated()) {
            field.setUid(UUID.randomUUID().toString());
        }
        if (field.getDefaultValue() != null) {
            f.setDefaultValue(String.valueOf(field.getDefaultValue()));
        }

        List<Reference> options = field.getOptions();
        if (options != null) {
            List<EntityModel> models = new ArrayList<EntityModel>(options.size());
            for (Reference option : options) {
                String lookup = DiffUtils.lookup(option.getRefPath(), option.getRefName());
                EntityModel entityModel = (EntityModel) cache.get(lookup);
                if (entityModel == null){
                    entityModel = new EntityModel();
                    cache.put(lookup, entityModel);
                }
                models.add(entityModel);
                f.setOptions(models);
            }
        }

        PackageDescriptorConfigElementFactory.initializeModelUID(f, field);
        return f;
    }

    private void registerField(IFieldElement field, String entityLookup) {
        FieldModel f = fieldStore.findByUID(field.getUid());
        if (f == null) {
            f = new FieldModel();
        }
        f.setName(field.getName());
        f.setFieldType(field.getType());
        f.setCustomFieldType(field.getCustomType());
        f.setRequired(field.isRequired());
        f.setAutoGenerated(field.isAutoGenerated());
	    f.setSearchable(field.isSearchable());
		f.setCustomFieldType(field.getCustomType());
		f.setDisplayName(field.getDisplayName());
		f.setDisplayDescription(field.getDisplayDescription());
		AllowedFieldValuesListUserType userType = new AllowedFieldValuesListUserType();
		f.setAllowedFieldValueList(userType.deserialize(field.getAllowValues()));
        if (field.getDefaultValue() != null) {
            f.setDefaultValue(String.valueOf(field.getDefaultValue()));
        }

        EntityModel entity = findByLookup(entityStore, entityLookup);
        f.setParent(entity);

        if (leaveUIDInfo(entity.getLookup())) {
            PackageDescriptorConfigElementFactory.initializeModelUID(f, field);
        }
        boolean isNew = f.getId() == null;
        fieldStore.saveOrUpdate(f);
        if (isNew) {
            putToSRCacheADD(f);
        }
    }

    private IndexModel prepareIndex(IIndexElement index, List<FieldModel> fieldList) {
        IndexModel indexModel = new IndexModel();
        indexModel.setName(index.getName());
        indexModel.setIndexType(index.getType());

        List<FieldModel> fieldModels = new ArrayList<FieldModel>();
        List<IFieldElement> fieldElements = index.getFields();
        if (fieldElements != null) {
            for (IFieldElement fieldElement : fieldElements) {
                for (FieldModel fieldModel : fieldList) {
                    if (fieldElement.getName().equals(fieldModel.getName())) {
                        fieldModels.add(fieldModel);
                        break;
                    }
                }
            }
            indexModel.setFields(fieldModels);
        }

        List<IndexEntityReferenceModel> indexEntityReferenceModels = new ArrayList<IndexEntityReferenceModel>();
        List<Reference> references = index.getEntities();
        if (references != null) {
            for (Reference reference : references) {
                IndexEntityReferenceModel indexEntityReferenceModel = new IndexEntityReferenceModel();
                indexEntityReferenceModel.setIndex(indexModel);
                String refEntityLookup = DiffUtils.lookupByRefPath(reference.getRefPath()) + "." + StringUtils.normalize(reference.getRefName());
                EntityModel refEntity = findByLookup(entityStore, refEntityLookup);
                indexEntityReferenceModel.setEntityModel(refEntity);
                indexEntityReferenceModel.setColumnName(reference.getConstraintName());
                indexEntityReferenceModels.add(indexEntityReferenceModel);
            }
            indexModel.setReferences(indexEntityReferenceModels);
        }

        PackageDescriptorConfigElementFactory.initializeModelUID(indexModel, index);
        return indexModel;
    }

    private void registerIndex(IIndexElement index, String entityLookup) {
        IndexModel indexModel = indexStore.findByUID(index.getUid());
        if (indexModel == null) {
            indexModel = new IndexModel();
        }
        indexModel.setName(index.getName());
        indexModel.setIndexType(index.getType());

        List<FieldModel> fieldModels = new ArrayList<FieldModel>();
        List<IFieldElement> fieldElements = index.getFields();
        if (fieldElements != null) {
            for (IFieldElement fieldElement : fieldElements) {
                FieldModel fieldModel = fieldStore.findByParentLookupAndName(entityLookup, fieldElement.getName());
                fieldModels.add(fieldModel);
            }
            indexModel.setFields(fieldModels);
        }


        List<Reference> references = index.getEntities();
	    if (references != null) {
		    List<IndexEntityReferenceModel> indexEntityReferenceModels = new ArrayList<IndexEntityReferenceModel>();

		    for (Reference reference : references) {
			    IndexEntityReferenceModel indexEntityReferenceModel = new IndexEntityReferenceModel();
		        indexEntityReferenceModel.setIndex(indexModel);
		        String refEntityLookup = DiffUtils.lookupByRefPath(reference.getRefPath()) + "." + StringUtils.normalize(reference.getRefName());
		        EntityModel refEntity = findByLookup(entityStore, refEntityLookup);
		        indexEntityReferenceModel.setEntityModel(refEntity);
		        indexEntityReferenceModel.setColumnName(reference.getConstraintName());
		        indexEntityReferenceModels.add(indexEntityReferenceModel);
		    }
		    indexModel.setReferences(indexEntityReferenceModels);
	    }

        Reference relationship = index.getRelationship();
        if (relationship != null) {
            String relationshipPath = DiffUtils.lookupByRefPath(relationship.getRefPath());
            String relationshipLookup = DiffUtils.lookup(relationshipPath, relationship.getRefName());
            RelationshipModel relationshipModel = relationshipStore.findByLookup(relationshipLookup);
            indexModel.setRelationship(relationshipModel);
        }

        EntityModel entity = findByLookup(entityStore, entityLookup);
        indexModel.setParent(entity);

        if (leaveUIDInfo(entity.getLookup())) {
            PackageDescriptorConfigElementFactory.initializeModelUID(indexModel, index);
        }
        boolean isNew = indexModel.getId() == null;
        indexStore.save(indexModel);
        if (isNew) {
            putToSRCacheADD(indexModel);
        }
    }

    private EntityModel getEntity(Reference ref) {
        String pathLookup = DiffUtils.lookupByRefPath(ref.getRefPath());
        return findByLookup(entityStore, pathLookup);
    }

    private Map<Long, List<String>> getRoleAssociationMap() {
        if (roleAssociationMap == null) {
            roleAssociationMap = new HashMap<Long, List<String>>();
        }
        return roleAssociationMap;
    }

    private Map<String, Permission> getPermissions() {
        if (permissions == null) {
            permissions = new HashMap<String, Permission>();
        }
        return permissions;
    }

    private Map<String, ActionModel> getActions() {
        if (actions == null) {
            actions = new HashMap<String, ActionModel>();
        }
        return actions;
    }

    private Map<String, NavigationElementModel> getNavigations() {
        if (navigations == null) {
            navigations = new HashMap<String, NavigationElementModel>();
        }
        return navigations;
    }

    private Map<String, ResourceLocation> getResourceLocations() {
        if (resourceLocations == null) {
            this.resourceLocations = new HashMap<String, ResourceLocation>();
        }
        return resourceLocations;
    }

    private SortedSet<String> getSubDomainPathPrefixes() {
        if (subDomainPathPrefixes == null) {
            subDomainPathPrefixes = new TreeSet<String>();
        }
        return subDomainPathPrefixes;
    }

    private Map<Long, List<String>> getUserRoleAssociationMap() {
        if (userRoleAssociationMap == null) {
            userRoleAssociationMap = new HashMap<Long, List<String>>();
        }
        return userRoleAssociationMap;
    }

    /**
     * @return permissions by roles map
     */
    private Map<Long, Set<PermissionModel>> getRolePermissionsMap() {
        if (rolePermissionsMap == null) {
            rolePermissionsMap = new HashMap<Long, Set<PermissionModel>>();
        }
        return rolePermissionsMap;
    }

    private void processGeneratedFields(IElementDiffInfoContainer diffInfo) {
        ConfigElementFactory factory = ConfigElementFactory.getInstance();
        ISqlNameResolver nameResolver = new LeadIdPrefixNameResolver();
        GeneratedFieldContext context = new GeneratedFieldContext(null, nameResolver);
        EntityElementManager entityManager = diffInfo.getNewEntitiesManager();
        List<EntitiesDiff> entityDiffList = diffInfo.getEntityDiffs();
        if (entityDiffList != null) {
            for (EntitiesDiff entityDiff : entityDiffList) {
                if (entityDiff.getType() == DifferenceType.ADDED) {
                    IEntityElement entity = entityDiff.getDiffTarget();
                    context.setEntity(entity);
//	                if ((entityManager.isRootEntity(entity) || !DiffUtils.isEntityExtension(entity)) && !entity.isTypeEntity()) {
//		                factory.produceGeneratedField(GeneratedFieldType.ID, context, true);
//		                factory.produceGeneratedField(GeneratedFieldType.CREATED, context, true);
//	                }
	                if (DiffUtils.REGISTRY_NODE_ENTITY_NAME.equals(entity.getName())) {
                        String entityPath;
                        try {
                            entityPath = entityManager.lookupKeyByEntityName("Entity");
                        } catch (TypeLookupException e) {
                            logger.error(e.getMessage(), e);
                            continue;
                        }

                        Reference ref = new Reference();
                        ref.setRefPath(entityPath);
                        ref.setRefName("extended_entity");

                        context.setReference(ref);

//                        factory.produceGeneratedField(GeneratedFieldType.EXTENDED_ENTITY, context, false);
                    }
                    if (entityManager.isRootEntity(entity)) {
                        factory.produceGeneratedField(GeneratedFieldType.DISCRIMINATOR, context, true);
                    }
                }
            }
        }
        List<RelationshipsDiff> relDiffList = diffInfo.getRelationshipDiffs();
        if (relDiffList != null) {
            for (RelationshipsDiff relDiff : relDiffList) {
                if (relDiff.getType() == DifferenceType.ADDED) {
                    IRelationshipElement rel = relDiff.getDiffTarget();
                    IEntityElement source;
                    try {
                        source = entityManager.lookup(rel.getSource());
                    } catch (TypeLookupException e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }

                    context.setEntity(source);
//
//                    if (rel.getType() == RelationshipType.PARENT_CHILD) {
//                        context.setReference(rel.getTarget());
//                        factory.produceGeneratedField(GeneratedFieldType.REF, context, rel.isRequired());
//                    } else if (rel.getType() == RelationshipType.TREE) {
//                        factory.produceGeneratedField(GeneratedFieldType.PARENT_REF, context, rel.isRequired());
//                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends LookupModel<RegistryNodeModel>> T findByLookup(ILookupStore<T, Long> store, String lookup) {
        T entity = (T) cache.get(lookup);
        if (entity == null) {
            entity = store.findByLookup(lookup);
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    private <T extends LookupModel<RegistryNodeModel>> List<T> findAllByLookupList(IRegistryNodeStore<T> store, List<String> lookups) {
        List<T> list = new ArrayList<T>();
        for (String lookup : lookups) {
            T entity = (T) cache.get(lookup);
            if (entity == null) {
                list.clear();
                break;
            }
            list.add(entity);
        }

        if (list.isEmpty()) {
            list = store.findAllByLookupList(lookups);
        }
        return list;
    }

    private boolean leaveUIDInfo(String lookup) {
        boolean leaveUIDInfo = true;
        for (String subDomainPathPrefix : getSubDomainPathPrefixes()) {
            if (lookup.startsWith(subDomainPathPrefix)) {
                leaveUIDInfo = false;
                break;
            }
        }
        return leaveUIDInfo;
    }

    private void putToSRCacheADD(LookupModel<?> model) {
        if (model != null) {
            List<LookupModel<?>> models = srCacheForAdd.get(model.getType());
            if (models == null) {
                models = new LinkedList<LookupModel<?>>();
                srCacheForAdd.put(model.getType(), models);
            }
            models.add(model);
        }
    }

    private void putToSRCacheDEL(LookupModel<?> model) {
        if (model != null) {
            List<LookupModel<?>> models = srCacheForDel.get(model.getType());
            if (models == null) {
                models = new LinkedList<LookupModel<?>>();
                srCacheForDel.put(model.getType(), models);
            }
            models.add(model);
        }
    }

    private void processSecuredRecords() {
        Set<RegistryNodeType> types = new HashSet<RegistryNodeType>(srCacheForAdd.keySet());
        types.addAll(srCacheForDel.keySet());
        List<String> typeLookupList = new ArrayList<String>();
        for (RegistryNodeType type : types) {
            typeLookupList.add(type.getEntityPath());
        }
        List<EntityModel> securityEnabledEntities = entityStore.getSecurityEnabledEntities(typeLookupList);
        List<String> securityEnabledTypes = new ArrayList<String>();
        if (securityEnabledEntities != null) {
            for (EntityModel type : securityEnabledEntities) {
                securityEnabledTypes.add(type.getLookup());
            }
        }

        List<TreeNodeSecuredRecord> srList = new ArrayList<TreeNodeSecuredRecord>();
        for (Map.Entry<RegistryNodeType, List<LookupModel<?>>> entry : srCacheForAdd.entrySet()) {
            RegistryNodeType entityType = entry.getKey();
            if (securityEnabledTypes.contains(entityType.getEntityPath())) {
                List<LookupModel<?>> modelList = entry.getValue();
                for (LookupModel<?> model : modelList) {
                    TreeNodeSecuredRecord sr = new TreeNodeSecuredRecord();
                    sr.setName(model.getName());
                    sr.setExternalNumberId(model.getId());
                    sr.setRegistryNodeLookup(entityType.getEntityPath());
                    srList.add(sr);
                }
            }
        }
        if (!srList.isEmpty()) {
            //below is not proper usage of broker but this case is special and we could not use just API method call here
            //because this class is used before API actually initialized.
            ServiceResponse response = saveSecuredRecordsBroker.execute(
                    new ServiceRequest<TreeNodeSecuredRecord>(srList));
            if (!response.isSuccess()) {
                logger.error(response.getMessage());
            }
            //OPFEngine.SecurityManagerService.createSecuredRecords(srList);
        }

        List<IdLookup> idLookupList = new ArrayList<IdLookup>();
        for (Map.Entry<RegistryNodeType, List<LookupModel<?>>> entry : srCacheForDel.entrySet()) {
            RegistryNodeType entityType = entry.getKey();
            List<LookupModel<?>> modelList = entry.getValue();
            for (LookupModel<?> model : modelList) {
                idLookupList.add(new IdLookup(model.getId(), entityType.getEntityPath()));
            }
        }
        if (!idLookupList.isEmpty()) {
            //below is not proper usage of broker but this case is special and we could not use just API method call here
            //because this class is used before API actually initialized.
            ServiceResponse response = deleteSecuredRecordsBroker.execute(new ServiceRequest<IdLookup>(idLookupList));
            if (!response.isSuccess()) {
                logger.error(response.getMessage());
            }
            //OPFEngine.SecurityManagerService.removeSecuredRecords(idLookupList);
        }
    }

    private List<String> getPathList(EntityRole[] entityRoles) {
        List<String> roleLookupList;
        if (entityRoles == null || entityRoles.length == 0) {
            roleLookupList = null;
        } else {
            roleLookupList = new ArrayList<String>();
            for (EntityRole role : entityRoles) {
                roleLookupList.add(role.getPath());
            }
        }
        return roleLookupList;
    }

    private void clearCachedData() {
        getUserRoleAssociationMap().clear();
        getRoleAssociationMap().clear();
        getPermissions().clear();
        getActions().clear();
        getNavigations().clear();
        getResourceLocations().clear();
        getSubDomainPathPrefixes().clear();
        getRolePermissionsMap().clear();
        cache.clear();
        srCacheForAdd.clear();
        srCacheForDel.clear();
        entityRoles.clear();
        userProfileFields.clear();
        packageId = null;
    }

}
