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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.authority.PermissionElement;
import net.firejack.platform.core.config.meta.element.authority.ResourceLocationElement;
import net.firejack.platform.core.config.meta.element.authority.RoleAssignmentElement;
import net.firejack.platform.core.config.meta.element.authority.RoleElement;
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
import net.firejack.platform.core.model.registry.INameProvider;

import java.util.List;


public class PackageDescriptor implements IPackageDescriptor, IEntityElementContainer, INameProvider {

    private String path;
    private String name;
    private String prefix;
    private String contextUrl;
    private String description;
    private List<IEntityElement> configuredEntities;
    private List<IDomainElement> configuredDomains;
    private List<IRelationshipElement> relationshipElements;
    private List<ActionElement> actionElements;
    private List<NavigationConfigElement> navigationElements;
    private List<DirectoryElement> directoryElements;
    private List<GroupElement> groupElements;
    private List<ScheduleElement> scheduleElements;
    private List<UserElement> userElements;
    private List<RoleElement> roleElements;
    private List<RoleAssignmentElement> roleAssignmentElements;
    private List<PermissionElement> permissionElements;
    private List<ResourceLocationElement> resourceLocationElements;
    private List<FolderElement> folderElements;
    private List<ResourceElement> resourceElements;
    private List<ConfigReference> configsElements;
    private List<ActorElement> actorElements;
    private List<CollectionElement> collectionElements;
    private List<ProcessElement> processElements;
    private List<UserProfileFieldElement> userProfileFields;
    private List<UserProfileFieldGroupElement> userProfileFieldGroups;
    private List<ReportElement> reportElements;
    private List<BIReportElement> biReportElements;
    private List<WizardElement> wizardElements;
    private String version;
    private String dependencies;
    private String uid;

    /**
     * @param path
     * @param name
     * @param version
     */
    public PackageDescriptor(String path, String name, String version) {
        this.path = path;
        this.name = name;
        this.version = version;
    }

    /**
     * @param name
     * @param version
     */
    public PackageDescriptor(String name, String version) {
        this(null, name, version);
    }

    public void setConfiguredEntities(List<IEntityElement> configuredEntities) {
        this.configuredEntities = configuredEntities;
    }

    @Override
    public IEntityElement[] getConfiguredEntities() {
        return DiffUtils.getArray(configuredEntities, IEntityElement.class);
    }

    /**
     * @param configuredDomains
     */
    public void setConfiguredDomains(List<IDomainElement> configuredDomains) {
        this.configuredDomains = configuredDomains;
    }

    @Override
    public IDomainElement[] getConfiguredDomains() {
        return DiffUtils.getArray(configuredDomains, IDomainElement.class);
    }

    public IRelationshipElement[] getRelationships() {
        return DiffUtils.getArray(relationshipElements, IRelationshipElement.class);
    }

    /**
     * @param relationshipElements
     */
    public void setRelationships(List<IRelationshipElement> relationshipElements) {
        this.relationshipElements = relationshipElements;
    }

    @Override
    public ActionElement[] getActionElements() {
        return DiffUtils.getArray(actionElements, ActionElement.class);
    }

    /**
     * @param actionElements
     */
    public void setActionElements(List<ActionElement> actionElements) {
        this.actionElements = actionElements;
    }

    @Override
    public NavigationConfigElement[] getNavigationElements() {
        return DiffUtils.getArray(navigationElements, NavigationConfigElement.class);
    }

    /**
     * @param navigationElements
     */
    public void setNavigationElements(List<NavigationConfigElement> navigationElements) {
        this.navigationElements = navigationElements;
    }

    @Override
    public DirectoryElement[] getDirectoryElements() {
        return DiffUtils.getArray(directoryElements, DirectoryElement.class);
    }

    /**
     * @param directoryElements
     */
    public void setDirectoryElements(List<DirectoryElement> directoryElements) {
        this.directoryElements = directoryElements;
    }

    public GroupElement[] getGroupElements() {
        return DiffUtils.getArray(groupElements, GroupElement.class);
    }

    /**
     * @param groupElements
     */
    public void setGroupElements(List<GroupElement> groupElements) {
        this.groupElements = groupElements;
    }

    @Override
    public ScheduleElement[] getScheduleElements() {
        return DiffUtils.getArray(scheduleElements, ScheduleElement.class);
    }

    /**
     * @param scheduleElements
     */
    public void setScheduleElements(List<ScheduleElement> scheduleElements) {
        this.scheduleElements = scheduleElements;
    }

    @Override
    public UserElement[] getUsers() {
        return DiffUtils.getArray(userElements, UserElement.class);
    }

    /**
     * @param userElements
     */
    public void setUsers(List<UserElement> userElements) {
        this.userElements = userElements;
    }

    @Override
    public RoleElement[] getRoles() {
        return DiffUtils.getArray(roleElements, RoleElement.class);
    }

    /**
     * @param roleElements
     */
    public void setRoles(List<RoleElement> roleElements) {
        this.roleElements = roleElements;
    }

    @Override
    public RoleAssignmentElement[] getRoleAssignments() {
        return DiffUtils.getArray(roleAssignmentElements, RoleAssignmentElement.class);
    }

    /**
     * @param roleAssignmentElements
     */
    public void setRoleAssignments(List<RoleAssignmentElement> roleAssignmentElements) {
        this.roleAssignmentElements = roleAssignmentElements;
    }

    @Override
    public PermissionElement[] getPermissions() {
        return DiffUtils.getArray(permissionElements, PermissionElement.class);
    }

    /**
     * @param permissionElements
     */
    public void setPermissions(List<PermissionElement> permissionElements) {
        this.permissionElements = permissionElements;
    }

    @Override
    public ResourceLocationElement[] getResourceLocationElements() {
        return DiffUtils.getArray(resourceLocationElements, ResourceLocationElement.class);
    }

    /**
     * @param resourceLocationElements
     */
    public void setResourceLocationElements(List<ResourceLocationElement> resourceLocationElements) {
        this.resourceLocationElements = resourceLocationElements;
    }

    @Override
    public FolderElement[] getFolderElements() {
        return DiffUtils.getArray(folderElements, FolderElement.class);
    }

    /**
     * @param folderElements
     */
    public void setFolderElements(List<FolderElement> folderElements) {
        this.folderElements = folderElements;
    }

    @Override
    public ResourceElement[] getResources() {
        return DiffUtils.getArray(resourceElements, ResourceElement.class);
    }

    /**
     * @param resourceElements
     */
    public void setResources(List<ResourceElement> resourceElements) {
        this.resourceElements = resourceElements;
    }

	public ReportElement[] getReportElements() {
		return DiffUtils.getArray(reportElements, ReportElement.class);
	}

	public void setReportElements(List<ReportElement> reportElements) {
		this.reportElements = reportElements;
	}

    public BIReportElement[] getBiReportElements() {
        return DiffUtils.getArray(biReportElements, BIReportElement.class);
    }

    public void setBiReportElements(List<BIReportElement> biReportElements) {
        this.biReportElements = biReportElements;
    }

    public WizardElement[] getWizardElements() {
		return DiffUtils.getArray(wizardElements, WizardElement.class);
	}

	public void setWizardElements(List<WizardElement> wizardElements) {
		this.wizardElements = wizardElements;
	}

	@Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDependencies() {
        return dependencies;
    }

    /**
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

	public String getContextUrl() {
		return contextUrl;
	}

	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConfigReference[] getConfigsElements() {
        return DiffUtils.getArray(configsElements, ConfigReference.class);
    }

    /**
     * @param configsElements
     */
    public void setConfigsElements(List<ConfigReference> configsElements) {
        this.configsElements = configsElements;
    }

    public ActorElement[] getActorElements() {
        return DiffUtils.getArray(actorElements, ActorElement.class);
    }

    /**
     * @param actorElements
     */
    public void setActorElements(List<ActorElement> actorElements) {
        this.actorElements = actorElements;
    }

	public CollectionElement[] getCollectionElements() {
		return DiffUtils.getArray(collectionElements, CollectionElement.class);
	}

	/**
	 *
	 * @param collectionElements
	 */
	public void setCollectionElements(List<CollectionElement> collectionElements) {
		this.collectionElements = collectionElements;
	}

	public ProcessElement[] getProcessElements() {
        return DiffUtils.getArray(processElements, ProcessElement.class);
    }

    /**
     * @param processElements
     */
    public void setProcessElements(List<ProcessElement> processElements) {
        this.processElements = processElements;
    }

    @Override
    public UserProfileFieldElement[] getUserProfileFields() {
        return DiffUtils.getArray(userProfileFields, UserProfileFieldElement.class);
    }

    public void setUserProfileFields(List<UserProfileFieldElement> userProfileFields) {
        this.userProfileFields = userProfileFields;
    }

    @Override
    public UserProfileFieldGroupElement[] getUserProfileFieldGroups() {
        return DiffUtils.getArray(userProfileFieldGroups, UserProfileFieldGroupElement.class);
    }

    public void setUserProfileFieldGroups(List<UserProfileFieldGroupElement> userProfileFieldGroups) {
        this.userProfileFieldGroups = userProfileFieldGroups;
    }

}