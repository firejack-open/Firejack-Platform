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

package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.patch.EntityElementManager;

import java.util.Collections;
import java.util.List;


public class ElementDiffInfoContainer implements IElementDiffInfoContainer {

    private List<EntitiesDiff> entityDiffList;
    private List<FieldsDiff> fieldDiffList;
    private List<IndexesDiff> indexesDiffList;
    private List<RelationshipsDiff> relationshipDiffList;
    private List<DomainsDiff> domainDiffList;
    private List<ActionsDiff> actionDiffList;
    private List<PermissionsDiff> permissionDiffList;
    private List<RolesDiff> roleDiffList;
    private List<NavigationElementsDiff> navigationElementDiffList;
    private List<ResourceLocationsDiff> resourceLocationDiffList;
    private List<ProcessesDiff> processDiffs;
    private List<ActorsDiff> actorDiffs;
    private List<ConfigsDiff> configDiffs;
    private List<FoldersDiff> folderDiffs;
    private List<DirectoriesDiff> directoryDiffs;
    private List<UsersDiff> userDiffs;
    private List<GroupsDiff> groupDiffs;
    private List<SchedulesDiff> schedulesDiffs;
    private List<RoleAssignmentsDiff> roleAssignmentDiffs;
    private List<ResourcesDiff> resourceDiffs;
    private List<CollectionsDiff> collectionDiffs;
    private List<UserProfileFieldsDiff> userProfileFieldsDiffs;
    private List<UserProfileFieldGroupsDiff> userProfileFieldGroupsDiffs;
	private List<ReportDiff> reportDiffs;
	private List<BIReportDiff> biReportDiffs;
    private List<WizardDiff> wizardDiffs;
    private EntityElementManager registry;
    private EntityElementManager oldTypesRegistry;

    @Override
    public List<CollectionsDiff> getCollectionDiffs() {
        return collectionDiffs;
    }

    public void setCollectionDiffs(List<CollectionsDiff> collectionDiffs) {
        this.collectionDiffs = collectionDiffs;
    }

    @Override
    public List<ResourcesDiff> getResourceDiffs() {
        return resourceDiffs;
    }

    /**
     * @param resourceDiffs
     */
    public void setResourceDiffs(List<ResourcesDiff> resourceDiffs) {
        this.resourceDiffs = resourceDiffs;
    }

    @Override
    public List<RoleAssignmentsDiff> getRoleAssignmentDiffs() {
        return roleAssignmentDiffs;
    }

    /**
     * @param roleAssignmentDiffs
     */
    public void setRoleAssignmentDiffs(List<RoleAssignmentsDiff> roleAssignmentDiffs) {
        this.roleAssignmentDiffs = roleAssignmentDiffs;
    }

    @Override
    public List<DirectoriesDiff> getDirectoryDiffs() {
        return directoryDiffs;
    }

    /**
     * @param directoryDiffs
     */
    public void setDirectoryDiffs(List<DirectoriesDiff> directoryDiffs) {
        this.directoryDiffs = directoryDiffs;
    }

    @Override
    public List<UsersDiff> getUserDiffs() {
        return userDiffs;
    }

    /**
     * @param userDiffs
     */
    public void setUserDiffs(List<UsersDiff> userDiffs) {
        this.userDiffs = userDiffs;
    }

    public List<GroupsDiff> getGroupDiffs() {
        return groupDiffs;
    }

    public void setGroupDiffs(List<GroupsDiff> groupDiffs) {
        this.groupDiffs = groupDiffs;
    }

    public List<SchedulesDiff> getScheduleDiffs() {
        return schedulesDiffs;
    }

    public void setScheduleDiffs(List<SchedulesDiff> schedulesDiffs) {
        this.schedulesDiffs = schedulesDiffs;
    }

    @Override
    public List<FoldersDiff> getFolderDiffs() {
        return folderDiffs;
    }

    /**
     * @param folderDiffs
     */
    public void setFolderDiffs(List<FoldersDiff> folderDiffs) {
        this.folderDiffs = folderDiffs;
    }

    @Override
    public List<ConfigsDiff> getConfigDiffs() {
        return configDiffs;
    }

    /**
     * @param configDiffs
     */
    public void setConfigDiffs(List<ConfigsDiff> configDiffs) {
        this.configDiffs = configDiffs;
    }

    @Override
    public List<ProcessesDiff> getProcessDiffs() {
        return processDiffs;
    }

    /**
     * @param processDiffs
     */
    public void setProcessDiffs(List<ProcessesDiff> processDiffs) {
        this.processDiffs = processDiffs;
    }

    @Override
    public List<ActorsDiff> getActorDiffs() {
        return actorDiffs;
    }

    /**
     * @param actorDiffs
     */
    public void setActorDiffs(List<ActorsDiff> actorDiffs) {
        this.actorDiffs = actorDiffs;
    }

    @Override
    public List<EntitiesDiff> getEntityDiffs() {
        if (entityDiffList == null) {
            entityDiffList = Collections.emptyList();
        }
        return entityDiffList;
    }

    /**
     * @param entityDiffList
     */
    public void setEntityDiffList(List<EntitiesDiff> entityDiffList) {
        this.entityDiffList = entityDiffList;
    }

    @Override
    public List<FieldsDiff> getFieldDiffs() {
        if (fieldDiffList == null) {
            fieldDiffList = Collections.emptyList();
        }
        return fieldDiffList;
    }

    /**
     * @param fieldDiffList
     */
    public void setFieldDiffList(List<FieldsDiff> fieldDiffList) {
        this.fieldDiffList = fieldDiffList;
    }

    @Override
    public List<IndexesDiff> getIndexesDiffs() {
        if (indexesDiffList == null) {
            indexesDiffList = Collections.emptyList();
        }
        return indexesDiffList;
    }

    public void setIndexesDiffList(List<IndexesDiff> indexesDiffList) {
        this.indexesDiffList = indexesDiffList;
    }

    @Override
    public List<RelationshipsDiff> getRelationshipDiffs() {
        if (relationshipDiffList == null) {
            relationshipDiffList = Collections.emptyList();
        }
        return relationshipDiffList;
    }

    /**
     * @param relationshipDiffList
     */
    public void setRelationshipDiffList(List<RelationshipsDiff> relationshipDiffList) {
        this.relationshipDiffList = relationshipDiffList;
    }

    @Override
    public List<DomainsDiff> getDomainDiffs() {
        if (domainDiffList == null) {
            domainDiffList = Collections.emptyList();
        }
        return domainDiffList;
    }

    /**
     * @param domainDiffList
     */
    public void setDomainDiffList(List<DomainsDiff> domainDiffList) {
        this.domainDiffList = domainDiffList;
    }

    @Override
    public List<ActionsDiff> getActionDiffs() {
        return actionDiffList;
    }

    /**
     * @param actionDiffList
     */
    public void setActionDiffList(List<ActionsDiff> actionDiffList) {
        this.actionDiffList = actionDiffList;
    }

    @Override
    public List<PermissionsDiff> getPermissionDiffs() {
        return permissionDiffList;
    }

    /**
     * @param permissionDiffList
     */
    public void setPermissionDiffList(List<PermissionsDiff> permissionDiffList) {
        this.permissionDiffList = permissionDiffList;
    }

    @Override
    public List<RolesDiff> getRoleDiffs() {
        return this.roleDiffList;
    }

    /**
     * @param roleDiffList
     */
    public void setRoleDiffs(List<RolesDiff> roleDiffList) {
        this.roleDiffList = roleDiffList;
    }

    @Override
    public List<NavigationElementsDiff> getNavigationElementDiffs() {
        return navigationElementDiffList;
    }

    /**
     * @param navigationElementDiffList
     */
    public void setNavigationElementDiffList(List<NavigationElementsDiff> navigationElementDiffList) {
        this.navigationElementDiffList = navigationElementDiffList;
    }

    @Override
    public List<ResourceLocationsDiff> getResourceLocationDiffs() {
        return resourceLocationDiffList;
    }

    /**
     * @param resourceLocationDiffList
     */
    public void setResourceLocationDiffList(List<ResourceLocationsDiff> resourceLocationDiffList) {
        this.resourceLocationDiffList = resourceLocationDiffList;
    }

    @Override
    public List<UserProfileFieldGroupsDiff> getUserProfileFieldGroupsDiffs() {
        return userProfileFieldGroupsDiffs;
    }

    public void setUserProfileFieldGroupsDiffs(List<UserProfileFieldGroupsDiff> userProfileFieldGroupsDiffs) {
        this.userProfileFieldGroupsDiffs = userProfileFieldGroupsDiffs;
    }

    @Override
    public List<UserProfileFieldsDiff> getUserProfileFieldsDiffs() {
        return userProfileFieldsDiffs;
    }

    public void setUserProfileFieldsDiffs(List<UserProfileFieldsDiff> userProfileFieldsDiffs) {
        this.userProfileFieldsDiffs = userProfileFieldsDiffs;
    }

    @Override
    public EntityElementManager getNewEntitiesManager() {
        return registry;
    }

    /**
     * @param registry
     */
    public void setTypesRegistry(EntityElementManager registry) {
        this.registry = registry;
    }

    @Override
    public EntityElementManager getOldEntitiesManager() {
        return oldTypesRegistry;
    }

    /**
     * @param oldTypesRegistry
     */
    public void setOldTypesRegistry(EntityElementManager oldTypesRegistry) {
        this.oldTypesRegistry = oldTypesRegistry;
    }

	public List<ReportDiff> getReportDiffs() {
		return reportDiffs;
	}

	public void setReportDiffs(List<ReportDiff> reportDiffs) {
		this.reportDiffs = reportDiffs;
	}

    public List<BIReportDiff> getBiReportDiffs() {
        return biReportDiffs;
    }

    public void setBiReportDiffs(List<BIReportDiff> biReportDiffs) {
        this.biReportDiffs = biReportDiffs;
    }

    public List<WizardDiff> getWizardDiffs() {
        return wizardDiffs;
    }

    public void setWizardDiffs(List<WizardDiff> wizardDiffs) {
        this.wizardDiffs = wizardDiffs;
    }
}