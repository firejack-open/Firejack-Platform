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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.BIReportElement;
import net.firejack.platform.core.config.meta.construct.ConfigElementFactory;
import net.firejack.platform.core.config.meta.construct.ReportElement;
import net.firejack.platform.core.config.meta.construct.WizardElement;
import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.authority.*;
import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.process.ActorElement;
import net.firejack.platform.core.config.meta.element.process.ProcessElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.schedule.ScheduleElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.exception.TranslationRuntimeException;
import net.firejack.platform.core.config.translate.sql.exception.TypeLookupException;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.VersionUtils;
import org.apache.log4j.Logger;

import java.util.*;


public class UIDPatchProcessor extends BasePatchProcessor {

    protected ConfigElementFactory factory =
            ConfigElementFactory.getInstance();
    protected DescriptorElementDiffComparator diffComparator =
            new DescriptorElementDiffComparator();

    private static final Logger logger = Logger.getLogger(UIDPatchProcessor.class);
    private EntityElementManager oldEntitiesManager = new EntityElementManager();
    private EntityElementManager entityManager = new EntityElementManager();

    @Override
    public IElementDiffInfoContainer processDifferences(
            IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor) {
        if (newPackageDescriptor == null) {
            throw new IllegalArgumentException("New package descriptor parameter is null.");
        }
        List<DomainsDiff> domainDiffList =
                processDomainDiffs(oldPackageDescriptor, newPackageDescriptor);
        List<EntitiesDiff> entityDiffList = new ArrayList<EntitiesDiff>();
        List<FieldsDiff> fieldDiffList = new ArrayList<FieldsDiff>();
        List<IndexesDiff> indexDiffList = new ArrayList<IndexesDiff>();
        List<RelationshipsDiff> relDiffList = new ArrayList<RelationshipsDiff>();

        List<IEntityElement> newEntitiesList = new ArrayList<IEntityElement>();
        initializeEntities(newPackageDescriptor, newEntitiesList, entityManager);
        IEntityElement[] newEntities = DiffUtils.getArray(newEntitiesList, IEntityElement.class);
        RelationshipsDiffContainer relDiffContainer;
        if (oldPackageDescriptor == null) {//In case when package descriptor has version 1.0.0 - treat all entities as diffs
            if (newEntities != null) {
                // will contain only ADDED entity diffs
                EntitiesDiffContainer elementDiffContainer =
                        getEntityDiffsWithChildren(new IEntityElement[0], newEntities);
                entityDiffList.addAll(elementDiffContainer.getChanges());

                for (EntitiesDiff entitiesDiff : entityDiffList) {
                    IEntityElement diffTarget = entitiesDiff.getDiffTarget();
                    IIndexElement[] indexes = diffTarget.getIndexes();
                    if (indexes != null) {
                        for (IIndexElement entityIndex : indexes) {
                            indexDiffList.add(new IndexesDiff(DifferenceType.ADDED, diffTarget, entityIndex));
                        }
                        diffTarget.setIndexes(null);
                    }
                }
            }
            relDiffContainer = processRelationshipDiffs(domainDiffList, null, newPackageDescriptor.getRelationships());
        } else {
            List<IEntityElement> oldEntities = new ArrayList<IEntityElement>();
            initializeEntities(oldPackageDescriptor, oldEntities, oldEntitiesManager);
            oldEntitiesManager.registerPackageInfo(
                    oldPackageDescriptor.getPath(), oldPackageDescriptor.getName(),
                    oldPackageDescriptor.getPrefix(), oldPackageDescriptor.getContextUrl(),
                    oldPackageDescriptor.getDescription(), oldPackageDescriptor.getUid());

            /////////////////////////////////////////////////
            Map<String, IEntityElement> oldElementsMap = DiffUtils.produceElementByUIDMap(oldEntities);
            Map<String, IEntityElement> newElementsMap = DiffUtils.produceElementByUIDMap(newEntitiesList);

            EntityDiffContainer entityDiffContainer = new EntityDiffContainer();
            for (Map.Entry<String, IEntityElement> entry : newElementsMap.entrySet()) {
                IEntityElement newEntity = entry.getValue();
                IEntityElement oldEntity = oldElementsMap.get(entry.getKey());
                IFieldElement[] fields = newEntity.getFields();
                IIndexElement[] indexes = newEntity.getIndexes();
                if (oldEntity == null) {
                    entityDiffContainer.addEntityDiff(new EntitiesDiff(DifferenceType.ADDED, newEntity));
                    if (fields != null) {
                        for (IFieldElement entityField : fields) {
                            fieldDiffList.add(new FieldsDiff(DifferenceType.ADDED, newEntity, entityField));
                        }
                    }
                    if (indexes != null) {
                        for (IIndexElement entityIndex : indexes) {
                            indexDiffList.add(new IndexesDiff(DifferenceType.ADDED, newEntity, entityIndex));
                        }
                    }
                } else {
                    if (!oldEntity.equals(newEntity)) {
                        entityDiffContainer.addEntityDiff(new EntitiesDiff(oldEntity, newEntity));
                    }
                    IFieldElement[] oldFields = oldEntity.getFields();
                    Map<String, IFieldElement> oldFieldsMap = DiffUtils.produceElementByUIDMap(oldFields);
                    Map<String, IFieldElement> newFieldsMap = DiffUtils.produceElementByUIDMap(fields);
                    for (Map.Entry<String, IFieldElement> fieldEntry : newFieldsMap.entrySet()) {
                        IFieldElement newField = fieldEntry.getValue();
                        IFieldElement oldField = oldFieldsMap.get(fieldEntry.getKey());
                        if (oldField == null) {
                            fieldDiffList.add(new FieldsDiff(DifferenceType.ADDED, newEntity, newField));
                        } else if (!oldField.equals(newField)){
                            fieldDiffList.add(new FieldsDiff(newEntity, oldField, newField));
                        }
                    }
                    for (Map.Entry<String, IFieldElement> fieldEntry : newFieldsMap.entrySet()) {
                        IFieldElement oldField = fieldEntry.getValue();
                        if (newFieldsMap.get(fieldEntry.getKey()) == null) {
                            fieldDiffList.add(new FieldsDiff(DifferenceType.REMOVED, newEntity, oldField));
                        }
                    }
                    IIndexElement[] oldIndexes = oldEntity.getIndexes();
                    Map<String, IIndexElement> oldIndexesMap = DiffUtils.produceElementByUIDMap(oldIndexes);
                    Map<String, IIndexElement> newIndexesMap = DiffUtils.produceElementByUIDMap(indexes);
                    for (Map.Entry<String, IIndexElement> indexEntry : newIndexesMap.entrySet()) {
                        IIndexElement newIndex = indexEntry.getValue();
                        IIndexElement oldIndex = oldIndexesMap.get(indexEntry.getKey());
                        if (oldIndex == null) {
                            indexDiffList.add(new IndexesDiff(DifferenceType.ADDED, newEntity, newIndex));
                        } else if (!oldIndex.equals(newIndex)){
                            indexDiffList.add(new IndexesDiff(newEntity, oldIndex, newIndex));
                        }
                    }
                    for (Map.Entry<String, IIndexElement> indexEntry : newIndexesMap.entrySet()) {
                        IIndexElement oldIndex = indexEntry.getValue();
                        if (newIndexesMap.get(indexEntry.getKey()) == null) {
                            indexDiffList.add(new IndexesDiff(DifferenceType.REMOVED, newEntity, oldIndex));
                        }
                    }
                }
                newEntity.setIndexes(null);
            }
            for (Map.Entry<String, IEntityElement> entry : oldElementsMap.entrySet()) {
                IEntityElement oldEntity = entry.getValue();
                IDomainElement domainElement = parentDomainElement(oldEntity);
                DomainsDiff domainsDiff = findDomainDiffByElement(domainDiffList, domainElement);
                boolean isDomainRemoved = domainsDiff != null && DifferenceType.REMOVED.equals(domainsDiff.getType());
                if (newElementsMap.get(entry.getKey()) == null && !(domainElement.isDataSource() && isDomainRemoved)) {
                    entityDiffContainer.addEntityDiff(new EntitiesDiff(DifferenceType.REMOVED, oldEntity));
                    IFieldElement[] fields = oldEntity.getFields();
                    if (fields != null) {
                        for (IFieldElement entityField : fields) {
                            fieldDiffList.add(new FieldsDiff(DifferenceType.REMOVED, oldEntity, entityField));
                        }
                    }
                }
            }
            entityDiffList.addAll(entityDiffContainer.getEntitiesDiffList());

            //Process Relationships differences
            relDiffContainer = processRelationshipDiffs(
                    domainDiffList,
                    oldPackageDescriptor.getRelationships(),
                    newPackageDescriptor.getRelationships());
        }

        if (relDiffContainer != null && relDiffContainer.getChanges() != null) {
            relDiffList.addAll(relDiffContainer.getChanges());
        }

        //firstly process folder diffs, then config diffs and other should follow only after configs
        List<FoldersDiff> folderDiffs = processFolderDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getFolderElements(),
                newPackageDescriptor.getFolderElements());

        List<ConfigsDiff> configDiffs = processConfigDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getConfigsElements(),
                newPackageDescriptor.getConfigsElements());

        List<ActionsDiff> actionDiffs = processActionDiffs(oldPackageDescriptor == null ?
                null : oldPackageDescriptor.getActionElements(),
                newPackageDescriptor.getActionElements());

        List<NavigationElementsDiff> navigationDiffs =
                processNavigationDiffs(oldPackageDescriptor == null ? null :
                        oldPackageDescriptor.getNavigationElements(),
                        newPackageDescriptor.getNavigationElements());

        List<ResourceLocationsDiff> resourceLocationDiffs =
                processResourceLocationDiffs(oldPackageDescriptor == null ? null :
                        oldPackageDescriptor.getResourceLocationElements(),
                        newPackageDescriptor.getResourceLocationElements());

        PermissionElement[] oldPermissions =
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getPermissions();
        PermissionElement[] newPermissions = newPackageDescriptor.getPermissions();
        List<PermissionsDiff> permissionDiffs = processPermissionDiffs(oldPermissions, newPermissions);

        List<DirectoriesDiff> directoryDiffs =
                processDirectoryDiffs(oldPackageDescriptor == null ? null :
                        oldPackageDescriptor.getDirectoryElements(),
                        newPackageDescriptor.getDirectoryElements());

        List<GroupsDiff> groupDiffs =
                processGroupDiffs(oldPackageDescriptor == null ? null :
                        oldPackageDescriptor.getGroupElements(),
                        newPackageDescriptor.getGroupElements());

        ScheduleElement[] oldSchedules =
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getScheduleElements();
        ScheduleElement[] newSchedules = newPackageDescriptor.getScheduleElements();
        List<SchedulesDiff> scheduleDiffs = processScheduleDiffs(oldSchedules, newSchedules);

        //user elements does not have uid attribute.
        //That's why we need to use DiffUtils.getTopLevelElementsDiffs() method instead
        List<UsersDiff> userDiffs =
                processUserDiffs(oldPackageDescriptor == null ? null :
                        oldPackageDescriptor.getUsers(), newPackageDescriptor.getUsers());

        List<ActorsDiff> actorDiffs = processActorDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getActorElements(),
                newPackageDescriptor.getActorElements());

        List<ProcessesDiff> processDiffs = handleProcessDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getProcessElements(),
                newPackageDescriptor.getProcessElements());

        List<RoleAssignmentsDiff> roleAssignments =
                processRoleAssignmentDiffs(null, newPackageDescriptor.getRoleAssignments());

        List<ResourcesDiff> resourceDiffs = processResourceDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getResources(),
                newPackageDescriptor.getResources());

        List<CollectionsDiff> collectionDiffs = processCollectionDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getCollectionElements(),
                newPackageDescriptor.getCollectionElements());

        List<UserProfileFieldsDiff> userProfileFieldsDiffs = processUserProfileFieldsDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getUserProfileFields(),
                newPackageDescriptor.getUserProfileFields());

        List<UserProfileFieldGroupsDiff> userProfileFieldGroupsDiffs = processUserProfileFieldGroupsDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getUserProfileFieldGroups(),
                newPackageDescriptor.getUserProfileFieldGroups());

	    ReportElement[] oldReports = oldPackageDescriptor == null ? null : oldPackageDescriptor.getReportElements();
        ReportElement[] newReports = newPackageDescriptor.getReportElements();
        List<ReportDiff> reportDiffs = DiffUtils.getTopLevelElementDiffsByUID(oldReports, newReports, ReportElement.class, ReportDiff.class);

	    BIReportElement[] oldBIReports = oldPackageDescriptor == null ? null : oldPackageDescriptor.getBiReportElements();
        BIReportElement[] newBIReports = newPackageDescriptor.getBiReportElements();
        List<BIReportDiff> biReportDiffs = DiffUtils.getTopLevelElementDiffsByUID(oldBIReports, newBIReports, BIReportElement.class, BIReportDiff.class);

        WizardElement[] oldWizards = oldPackageDescriptor == null ? null : oldPackageDescriptor.getWizardElements();
        WizardElement[] newWizards = newPackageDescriptor.getWizardElements();
        List<WizardDiff> wizardDiffs = DiffUtils.getTopLevelElementDiffsByUID(oldWizards, newWizards, WizardElement.class, WizardDiff.class);

        sort(domainDiffList);
        sort(navigationDiffs);
        sort(entityDiffList);
        sort(processDiffs);
        sort(actorDiffs);
        sort(folderDiffs);
        sort(configDiffs);
        sort(resourceDiffs);
        sort(collectionDiffs);
        sort(userProfileFieldsDiffs);
        sort(userProfileFieldGroupsDiffs);

        prepareEntityManager(newPackageDescriptor, entityManager);
        prepareEntityManager(oldPackageDescriptor, oldEntitiesManager);

        List<RolesDiff> roleDiffs = processRoleDiffs(
                oldPackageDescriptor == null ? null : oldPackageDescriptor.getRoles(), oldPermissions,
                newPackageDescriptor.getRoles(), newPermissions);

        ElementDiffInfoContainer differences = new ElementDiffInfoContainer();
        differences.setEntityDiffList(entityDiffList);
        differences.setRelationshipDiffList(relDiffList);
        differences.setFieldDiffList(fieldDiffList);
        differences.setIndexesDiffList(indexDiffList);
        differences.setDomainDiffList(domainDiffList);
        differences.setActionDiffList(actionDiffs);
        differences.setNavigationElementDiffList(navigationDiffs);
        differences.setResourceLocationDiffList(resourceLocationDiffs);
        differences.setPermissionDiffList(permissionDiffs);
        differences.setRoleDiffs(roleDiffs);
        differences.setProcessDiffs(processDiffs);
        differences.setActorDiffs(actorDiffs);
        differences.setFolderDiffs(folderDiffs);
        differences.setConfigDiffs(configDiffs);
        differences.setDirectoryDiffs(directoryDiffs);
        differences.setGroupDiffs(groupDiffs);
        differences.setScheduleDiffs(scheduleDiffs);
        differences.setUserDiffs(userDiffs);
        differences.setResourceDiffs(resourceDiffs);
        differences.setCollectionDiffs(collectionDiffs);
        differences.setRoleAssignmentDiffs(roleAssignments);
        differences.setUserProfileFieldsDiffs(userProfileFieldsDiffs);
        differences.setUserProfileFieldGroupsDiffs(userProfileFieldGroupsDiffs);
        differences.setReportDiffs(reportDiffs);
        differences.setBiReportDiffs(biReportDiffs);
        differences.setWizardDiffs(wizardDiffs);

        differences.setTypesRegistry(entityManager.clone());
        differences.setOldTypesRegistry(oldEntitiesManager.clone());

        entityManager.clear();
        oldEntitiesManager.clear();

        return differences;
    }

    private DomainsDiff findDomainDiffByElement(List<DomainsDiff> domainDiffList, IDomainElement domainElement) {
        DomainsDiff foundDomainsDiff = null;
        for (DomainsDiff domainsDiff : domainDiffList) {
            if (domainsDiff.getDiffTarget().equals(domainElement)) {
                foundDomainsDiff = domainsDiff;
            }
        }
        return foundDomainsDiff;
    }

    private IDomainElement parentDomainElement(IParentReferenceOwner oldEntity) {
        INamedPackageDescriptorElement parent = oldEntity.getParent();
        if (parent instanceof IDomainElement) {
            return (IDomainElement) parent;
        } else {
            return parentDomainElement((IParentReferenceOwner) parent);
        }
    }

    private <T extends IPackageDescriptorElementDiff
            <?, ? extends INamedPackageDescriptorElement>> void sort(List<T> elements) {
        if (elements != null) {
            Collections.sort(elements, diffComparator);
        }
    }

    protected RelationshipsDiffContainer processRelationshipDiffs(
            List<DomainsDiff> domainDiffList,
            IRelationshipElement[] oldRelationships, IRelationshipElement[] newRelationships) {
        RelationshipsDiffContainer diffContainer = new RelationshipsDiffContainer();
        if (oldRelationships == null ^ newRelationships == null ) {
            boolean add = oldRelationships == null;
            IRelationshipElement[] relationships = add ? newRelationships : oldRelationships;
            for (IRelationshipElement rel : relationships) {
                diffContainer.addRelationshipsDiff(add, rel);
            }
        } else if (oldRelationships != null) {
            Map<String, IRelationshipElement> oldRelMap = DiffUtils.produceElementByUIDMap(oldRelationships);
            Map<String, IRelationshipElement> newRelMap = DiffUtils.produceElementByUIDMap(newRelationships);
            for (Map.Entry<String, IRelationshipElement> newRelEntry : newRelMap.entrySet()) {
                IRelationshipElement oldRel = oldRelMap.get(newRelEntry.getKey());
                if (oldRel == null) {
                    diffContainer.addRelationshipsDiff(true, newRelEntry.getValue());
                } else if (!newRelEntry.getValue().equals(oldRel)) {
                    diffContainer.addRelationshipsDiff(oldRel, newRelEntry.getValue());
                }
            }
            for (Map.Entry<String, IRelationshipElement> oldRelEntry : oldRelMap.entrySet()) {
                if (!newRelMap.containsKey(oldRelEntry.getKey())) {
                    IRelationshipElement relationshipElement = oldRelEntry.getValue();
                    try {
                        IEntityElement oldEntity = oldEntitiesManager.lookup(relationshipElement.getSource().getRefPath());
                        IDomainElement domainElement = parentDomainElement(oldEntity);
                        DomainsDiff domainsDiff = findDomainDiffByElement(domainDiffList, domainElement);
                        boolean isDomainRemoved = domainsDiff != null && DifferenceType.REMOVED.equals(domainsDiff.getType());
                        if (!(domainElement.isDataSource() && isDomainRemoved)) {
                            diffContainer.addRelationshipsDiff(false, relationshipElement);
                        }
                    } catch (TypeLookupException e) {
                        throw new TranslationRuntimeException(e);
                    }
                }
            }
        }// else nothing to do because both - old and new relationships are empty
        return diffContainer;
    }

    protected List<DomainsDiff> processDomainDiffs(
            IPackageDescriptor oldPackageDescriptor, IPackageDescriptor newPackageDescriptor) {
        Map<String, IDomainElement> newDomainsByUID = processDomains(newPackageDescriptor, true);
        Map<String, IDomainElement> oldDomainsByUID = processDomains(oldPackageDescriptor, false);

        List<DomainsDiff> domainDiffs = new ArrayList<DomainsDiff>();
        for (String uid : newDomainsByUID.keySet()) {
            IDomainElement newDomain = newDomainsByUID.get(uid);
            IDomainElement oldDomain = oldDomainsByUID.get(uid);
            if (oldDomain == null) {
                domainDiffs.add(new DomainsDiff(DifferenceType.ADDED, newDomain));
            } else if (!newDomain.equals(oldDomain)) {
                domainDiffs.add(new DomainsDiff(oldDomain, newDomain));
            }
        }

        for (String uid : oldDomainsByUID.keySet()) {
            if (!newDomainsByUID.containsKey(uid)) {
                IDomainElement oldDomain = oldDomainsByUID.get(uid);
                domainDiffs.add(new DomainsDiff(DifferenceType.REMOVED, oldDomain));
            }
        }

        return domainDiffs;
    }

    protected Map<String, IDomainElement> processDomains(
            IPackageDescriptor packageDescriptor, boolean newDescriptor) {
        IDomainElement[] resultDomains;
        if (packageDescriptor == null) {
            if (newDescriptor) {
                throw new IllegalArgumentException();
            }
            resultDomains = null;
        } else {
            List<IDomainElement> oldDomainList = new ArrayList<IDomainElement>();
            initializePackageDomains(packageDescriptor,
                    DiffUtils.getRefPath(packageDescriptor), oldDomainList);
            resultDomains = DiffUtils.getArray(oldDomainList, IDomainElement.class);
        }
        return DiffUtils.produceElementByUIDMap(resultDomains);
    }

    protected FieldsDiffContainer processFieldsDifferences(
            IEntityElement parent, IFieldElement[] oldElements, IFieldElement[] newElements) {
        Map<String, IFieldElement> oldElementsMap = DiffUtils.produceElementByUIDMap(oldElements);
        Map<String, IFieldElement> newElementsMap = DiffUtils.produceElementByUIDMap(newElements);

        FieldsDiffContainer fieldsDiffContainer = new FieldsDiffContainer();
        for (Map.Entry<String, IFieldElement> entry : newElementsMap.entrySet()) {
            IFieldElement newField = entry.getValue();
            IFieldElement oldField = oldElementsMap.get(entry.getKey());
            if (oldField == null) {
                fieldsDiffContainer.addFieldsDiff(DifferenceType.ADDED, parent, newField);
            } else if (!oldField.equals(newField)) {
                fieldsDiffContainer.addFieldsDiff(parent, oldField, newField);
            }
        }
        for (Map.Entry<String, IFieldElement> entry : oldElementsMap.entrySet()) {
            if (newElementsMap.get(entry.getKey()) == null) {
                fieldsDiffContainer.addFieldsDiff(DifferenceType.REMOVED, parent, entry.getValue());
            }
        }
        return fieldsDiffContainer;
    }

    protected List<FoldersDiff> processFolderDiffs(
            FolderElement[] oldFolderElements, FolderElement[] newFolderElements) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldFolderElements, newFolderElements, FolderElement.class, FoldersDiff.class);
    }

    protected List<ConfigsDiff> processConfigDiffs(
            ConfigReference[] oldConfigs, ConfigReference[] newConfigs) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldConfigs, newConfigs, ConfigReference.class, ConfigsDiff.class);
    }

    protected List<ActionsDiff> processActionDiffs(
            ActionElement[] oldActions, ActionElement[] newActions) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldActions, newActions, ActionElement.class, ActionsDiff.class);
    }

    protected List<NavigationElementsDiff> processNavigationDiffs(
            NavigationConfigElement[] oldActions, NavigationConfigElement[] newActions) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldActions, newActions, NavigationConfigElement.class, NavigationElementsDiff.class);
    }

    protected List<ResourceLocationsDiff> processResourceLocationDiffs(
            ResourceLocationElement[] oldResourceLocations,
            ResourceLocationElement[] newResourceLocations) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldResourceLocations, newResourceLocations,
                ResourceLocationElement.class, ResourceLocationsDiff.class);
    }

    protected List<PermissionsDiff> processPermissionDiffs(
            PermissionElement[] oldPermissions,
            PermissionElement[] newPermissions) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldPermissions, newPermissions, PermissionElement.class, PermissionsDiff.class);
    }

    protected List<DirectoriesDiff> processDirectoryDiffs(
            DirectoryElement[] oldDirectories, DirectoryElement[] newDirectories) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldDirectories, newDirectories, DirectoryElement.class, DirectoriesDiff.class);
    }

    protected List<GroupsDiff> processGroupDiffs(
            GroupElement[] oldGroups, GroupElement[] newGroups) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldGroups, newGroups, GroupElement.class, GroupsDiff.class);
    }

    protected List<SchedulesDiff> processScheduleDiffs(
            ScheduleElement[] oldSchedules, ScheduleElement[] newSchedules) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldSchedules, newSchedules, ScheduleElement.class, SchedulesDiff.class);
    }

    protected List<UsersDiff> processUserDiffs(
            UserElement[] oldUsers, UserElement[] newUsers) {
        return DiffUtils.getTopLevelElementsDiffs(
                oldUsers, newUsers, UserElement.class, UsersDiff.class);
    }

    protected List<RolesDiff> processRoleDiffs(
            RoleElement[] oldRoles, PermissionElement[] oldPermissionElements,
            RoleElement[] newRoles, PermissionElement[] newPermissionElements) {
        Map<String, RoleElement> oldRoleMap = DiffUtils.produceElementByUIDMap(oldRoles);
        Map<String, RoleElement> newRoleMap = DiffUtils.produceElementByUIDMap(newRoles);


        List<RolesDiff> diffList = new ArrayList<RolesDiff>();
        Set<String> oldUIDSet = oldRoleMap.keySet();
        Set<String> newUIDSet = newRoleMap.keySet();
        for (String uid : oldUIDSet) {
            if (newUIDSet.contains(uid)) {
                RoleElement oldRole = oldRoleMap.get(uid);
                RoleElement newRole = newRoleMap.get(uid);

                String oldPackageLookup = DiffUtils.lookup(
                        oldEntitiesManager.getPackagePath(), oldEntitiesManager.getPackageName());
                SortedSet<String> oldRolePermissionUidSet = getRolePermissionUIDList(
                        oldPermissionElements, oldRole.getPermissions(), oldPackageLookup);

                String newPackageLookup = DiffUtils.lookup(
                        entityManager.getPackagePath(), entityManager.getPackageName());
                SortedSet<String> newRolePermissionUidSet = getRolePermissionUIDList(
                        newPermissionElements, newRole.getPermissions(), newPackageLookup);

                boolean rolePermissionsChanged = !oldRolePermissionUidSet.equals(newRolePermissionUidSet);

                if (rolePermissionsChanged || !oldRole.equals(newRole)) {
                    diffList.add(new RolesDiff(oldRole, newRole, rolePermissionsChanged));
                }
            } else {
                diffList.add(new RolesDiff(false, oldRoleMap.get(uid)));
            }
        }
        for (String uid : newUIDSet) {
            if (!oldUIDSet.contains(uid)) {
                diffList.add(new RolesDiff(true, newRoleMap.get(uid)));
            }
        }
        return diffList;
    }

    protected SortedSet<String> getRolePermissionUIDList(
            PermissionElement[] allDeclaredPermissions, List<RolePermissionReference> rpReferenceList,
            String packageLookup) {
        SortedSet<String> result = new TreeSet<String>();
        if (isNotEmpty(rpReferenceList) && isNotEmpty(allDeclaredPermissions) &&
                StringUtils.isNotBlank(packageLookup)) {
            Map<String, PermissionElement> permissionsByLookup = new HashMap<String, PermissionElement>();
            for (PermissionElement permission : allDeclaredPermissions) {
                String lookup = DiffUtils.lookup(permission.getPath(), permission.getName());
                permissionsByLookup.put(lookup, permission);
            }
            String packageLookupPrefix = packageLookup + '.';
            for (RolePermissionReference rpReference : rpReferenceList) {
                String permissionLookup = rpReference.getPath().toLowerCase();
                if (permissionLookup.startsWith(packageLookupPrefix)) {
                    PermissionElement permission = permissionsByLookup.get(permissionLookup);
                    if (permission == null) {
                        throw new IllegalStateException(
                                "Role declaration contains mapping for non-registered permission [" +
                                        permissionLookup + "]");
                    }
                    result.add(permission.getUid());
                }
            }
        }
        return result;
    }

    protected List<ActorsDiff> processActorDiffs(ActorElement[] oldActors, ActorElement[] newActors) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldActors, newActors, ActorElement.class, ActorsDiff.class);
    }

    protected List<UserProfileFieldsDiff> processUserProfileFieldsDiffs(
            UserProfileFieldElement[] userProfileFields, UserProfileFieldElement[] newUserProfileFields) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                userProfileFields, newUserProfileFields, UserProfileFieldElement.class, UserProfileFieldsDiff.class);
    }

    protected List<UserProfileFieldGroupsDiff> processUserProfileFieldGroupsDiffs(
            UserProfileFieldGroupElement[] userProfileFieldGroups, UserProfileFieldGroupElement[] newUserProfileFieldGroups) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                userProfileFieldGroups, newUserProfileFieldGroups,
                UserProfileFieldGroupElement.class, UserProfileFieldGroupsDiff.class);
    }

    protected List<ProcessesDiff> handleProcessDiffs(
            ProcessElement[] oldProcesses, ProcessElement[] newProcesses) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldProcesses, newProcesses, ProcessElement.class, ProcessesDiff.class);
    }

    protected List<ResourcesDiff> processResourceDiffs(
            ResourceElement[] oldResources, ResourceElement[] newResources) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldResources, newResources, ResourceElement.class, ResourcesDiff.class);
    }

    @SuppressWarnings("unused")
    protected List<RoleAssignmentsDiff> processRoleAssignmentDiffs(
            RoleAssignmentElement[] oldAssignments, RoleAssignmentElement[] newAssignments) {
        List<RoleAssignmentsDiff> roleAssignments;
        if (newAssignments == null) {
            roleAssignments = Collections.emptyList();
        } else {
            roleAssignments = new ArrayList<RoleAssignmentsDiff>();
            for (RoleAssignmentElement roleAssignment : newAssignments) {
                roleAssignments.add(new RoleAssignmentsDiff(Boolean.TRUE, roleAssignment));
            }
        }
        return roleAssignments;
    }

    protected List<CollectionsDiff> processCollectionDiffs(
            CollectionElement[] oldCollections, CollectionElement[] newCollections) {
        return DiffUtils.getTopLevelElementDiffsByUID(
                oldCollections, newCollections, CollectionElement.class, CollectionsDiff.class);
    }

    private void initializePackageDomains(
            IRootElementContainer elementContainer, String elementsBaseRefPath,
            List<IDomainElement> domainList) {
        if (domainList == null || elementContainer == null) {
            return;
        }
        IDomainElement[] domains = elementContainer.getConfiguredDomains();
        if (domains != null) {
            for (IDomainElement domain : domains) {
                factory.attachPath(domain, elementsBaseRefPath);
                factory.assignParent(domain, elementContainer);
                domainList.add(domain);
                initializePackageDomains(domain, elementsBaseRefPath + "." + domain.getName(), domainList);
            }
        }
        fillEntityParents(elementContainer);
    }

    private void prepareEntityManager(
            IPackageDescriptor packageDescriptor, EntityElementManager entityManager) {
        if (packageDescriptor != null) {
            entityManager.registerPackageInfo(
                    packageDescriptor.getPath(), packageDescriptor.getName(),
                    packageDescriptor.getPrefix(), packageDescriptor.getContextUrl(), packageDescriptor.getDescription(),
                    packageDescriptor.getUid());
            entityManager.setNewVersion(VersionUtils.convertToNumber(packageDescriptor.getVersion()));
            try {
                entityManager.evaluateRootEntityElements();
            } catch (TypeLookupException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    private void fillEntityParents(IEntityProvider entityProvider) {
        IEntityElement[] entities = entityProvider.getConfiguredEntities();
        if (entities != null && entityProvider instanceof INamedPackageDescriptorElement) {
            INamedPackageDescriptorElement parent = (INamedPackageDescriptorElement) entityProvider;
            for (IEntityElement entity : entities) {
                factory.assignParent(entity, parent);
                fillEntityParents(entity);
            }
        }
    }

    private EntitiesDiffContainer getEntityDiffsWithChildren(
            IEntityElement[] oldEntities, IEntityElement[] newEntities) {
        EntitiesDiffContainer elementDiffContainer = processEntitiesSimpleDifferences(oldEntities, newEntities);
        Set<EntitiesDiff> addRemoveInnerChanges = new HashSet<EntitiesDiff>();
        addRemoveInnerChanges.addAll(elementDiffContainer.getChanges());
        for (EntitiesDiff diff : elementDiffContainer.getChanges()) {
            IEntityElement entity = diff.getDiffTarget();
            if (diff.getType() == DifferenceType.ADDED) {
                EntitiesDiffContainer addInnerDiffContainer = getEntityDiffsWithChildren(null, entity.getConfiguredEntities());
                addRemoveInnerChanges.addAll(addInnerDiffContainer.getChanges());
            } else {
                EntitiesDiffContainer removeInnerDiffContainer = getEntityDiffsWithChildren(entity.getConfiguredEntities(), null);
                addRemoveInnerChanges.addAll(removeInnerDiffContainer.getChanges());
            }
        }
        return new EntitiesDiffContainer(addRemoveInnerChanges, elementDiffContainer.getEqualEntitiesDiffMap());
    }

    private void initializeEntities(
            IRootElementContainer elementContainer, List<IEntityElement> configuredEntities,
            EntityElementManager entityManager) {
        String elementsPath = elementContainer.getPath() + "." + elementContainer.getName();
        IEntityElement[] entities = elementContainer.getConfiguredEntities();
        if (entities != null) {
            for (IEntityElement entity : entities) {
                if (configuredEntities != null) {
                    configuredEntities.add(entity);
                }
                initializeNestedEntities(elementsPath, entity, entityManager, configuredEntities);
            }
        }
        IDomainElement[] domains = elementContainer.getConfiguredDomains();
        if (domains != null) {
            for (IDomainElement domain : domains) {
                factory.attachPath(domain, elementsPath);
                initializeEntities(domain, configuredEntities, entityManager);
            }
        }
    }

    private void initializeNestedEntities(
            String entityBasePath, IEntityElement entity,
            EntityElementManager entityElementManager, List<IEntityElement> configuredEntities) {
        String entityMetaLookup = entityBasePath + "." + entity.getName();
        if (entityElementManager.isPathRegistered(entityMetaLookup)) {
            factory.attachPath(entity, entityBasePath);
        } else {
//            entityManager.registerEntityElement(entityMetaLookup, entity, realPath);
            entityElementManager.registerEntityElement(entityMetaLookup, entity);
        }
        IEntityElement[] children = entity.getConfiguredEntities();
        if (children != null) {
            for (IEntityElement child : children) {
                if (configuredEntities != null) {
                    configuredEntities.add(child);
                }
                initializeNestedEntities(entityMetaLookup, child, entityElementManager, configuredEntities);
            }
        }
    }

    private EntitiesDiffContainer processEntitiesSimpleDifferences(
            IEntityElement[] oldElements, IEntityElement[] newElements) {
        Map<String, IEntityElement> oldElementsMap = DiffUtils.produceElementByUIDMap(oldElements);
        Map<String, IEntityElement> newElementsMap = DiffUtils.produceElementByUIDMap(newElements);
        EntitiesDiffContainer entitiesDiffContainer = new EntitiesDiffContainer();

        for (String uid : newElementsMap.keySet()) {
            IEntityElement newEntity = newElementsMap.get(uid);
            IEntityElement oldEntity = oldElementsMap.get(uid);
            if (oldEntity == null) {
                entitiesDiffContainer.addEntitiesDiff(true, newEntity);
            } else {
                entitiesDiffContainer.addEqualEntityCandidates(oldEntity, newEntity);
            }
        }
        for (String uid : oldElementsMap.keySet()) {
            IEntityElement oldEntity = oldElementsMap.get(uid);
            if (newElementsMap.get(uid) == null) {
                entitiesDiffContainer.addEntitiesDiff(false, oldEntity);
            }
        }
        return entitiesDiffContainer;
    }

    private IEntityElement[] getConfiguredEntities(IEntityProvider entityProvider) {
        Set<IEntityElement> configuredEntities = new HashSet<IEntityElement>();
        IEntityElement[] entities = entityProvider.getConfiguredEntities();
        if (entities != null) {
            Collections.addAll(configuredEntities, entities);
            for (IEntityElement entity : entities) {
                entities = getConfiguredEntities(entity);
                if (entities != null) {
                    Collections.addAll(configuredEntities, entities);
                }
            }
        }
        if (entityProvider instanceof IRootElementContainer) {
            IRootElementContainer rootElementContainer = (IRootElementContainer) entityProvider;
            IDomainElement[] domains = rootElementContainer.getConfiguredDomains();
            if (domains != null) {
                for (IDomainElement domain : domains) {
                    entities = getConfiguredEntities(domain);
                    if (entities != null) {
                        Collections.addAll(configuredEntities, entities);
                    }
                }
            }
        }
        return DiffUtils.getArray(configuredEntities, IEntityElement.class);
    }

    private EntityDiffContainer processEntitiesTree(
            EntityDiffContainer diffContainer, String basePath, IEntityProvider oldProvider,
            IEntityProvider newProvider) {
        IEntityElement[] oldInnerEntities = getConfiguredEntities(oldProvider);
        IEntityElement[] newInnerEntities = getConfiguredEntities(newProvider);
        if (isNotEmpty(oldInnerEntities) || isNotEmpty(newInnerEntities)) {// both entity providers are not empty
            EntitiesDiffContainer entitiesDiffContainer =
                    getEntityDiffsWithChildren(oldInnerEntities, newInnerEntities);
            diffContainer.addEntityDiffs(entitiesDiffContainer.getChanges());
            for (Map.Entry<IEntityElement, IEntityElement> entry :  entitiesDiffContainer.getEqualEntitiesDiffMap().entrySet()) {
                IEntityElement oldEntity = entry.getKey();
                IEntityElement newEntity = entry.getValue();
                if (!oldEntity.equals(newEntity)) {
                    processEntityDiffs(diffContainer, basePath, oldEntity, newEntity);
                }
            }
        }
        return diffContainer;
    }

    private EntityDiffContainer processEntityDiffs(
            EntityDiffContainer entityDiffContainer, String entityPath,
            IEntityElement oldEntity, IEntityElement newEntity) {
        entityDiffContainer = entityDiffContainer == null ? new EntityDiffContainer() : entityDiffContainer;

        FieldsDiffContainer fieldsDiffs = processFieldsDifferences(
                newEntity, oldEntity.getFields(), newEntity.getFields());
        entityDiffContainer.addFieldDiffs(fieldsDiffs.getChanges());

        IEntityElement[] newInnerEntities = newEntity.getConfiguredEntities();
        IEntityElement[] oldInnerEntities = oldEntity.getConfiguredEntities();

        if (isEmpty(newInnerEntities) ^ isEmpty(oldInnerEntities)) {// old entity has children entities and at the same time new entity doesn't and vice versa
            if (isEmpty(newInnerEntities)) { // old entity contains inner entities
                for (IEntityElement entity : oldInnerEntities) {
                    try {
                        IEntityElement correspondingNewEntity = this.entityManager.lookupByUID(entity.getUid());
                        if (!entity.equals(correspondingNewEntity)) {
                            entityDiffContainer.addEntityDiff(new EntitiesDiff(entity, correspondingNewEntity));
                        }
                    } catch (TypeLookupException e) {
                        entityDiffContainer.addEntityDiff(new EntitiesDiff(DifferenceType.REMOVED, entity));
                    }
                }
            } else { // only new entity contains inner entities
                for (IEntityElement entity : newInnerEntities) {
                    try {
                        IEntityElement correspondingOldEntity = this.oldEntitiesManager.lookupByUID(entity.getUid());
                        if (!entity.equals(correspondingOldEntity)) {
                            entityDiffContainer.addEntityDiff(new EntitiesDiff(correspondingOldEntity, entity));
                        }
                    } catch (TypeLookupException e) {
                        entityDiffContainer.addEntityDiff(new EntitiesDiff(DifferenceType.ADDED, entity));
                    }
                }
            }
        } else {
            processEntitiesTree(entityDiffContainer, entityPath, oldEntity, newEntity);
        }
        if (oldEntity.equals(newEntity)) {
            entityDiffContainer.addEntityDiff(new EntitiesDiff(oldEntity, newEntity));
        }
        return entityDiffContainer;
    }

}
