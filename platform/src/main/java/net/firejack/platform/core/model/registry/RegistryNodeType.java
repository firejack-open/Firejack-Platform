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

package net.firejack.platform.core.model.registry;

import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.bi.BIReportModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.model.registry.directory.ApplicationModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.model.registry.report.ReportModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.model.user.SystemUserModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


public enum RegistryNodeType {

    REGISTRY_NODE(RegistryNodeModel.class, "net.firejack.platform.registry.registry-node", false),

    ROOT_DOMAIN(RootDomainModel.class, "net.firejack.platform.registry.registry-node.root-domain", true),

    DOMAIN(DomainModel.class, "net.firejack.platform.registry.registry-node.domain", true),

    SUB_DOMAIN(SubDomain.class, "net.firejack.platform.registry.registry-node.sub-domain", true),

    SYSTEM(SystemModel.class, "net.firejack.platform.registry.registry-node.system", true),

    SERVER(ServerModel.class, "net.firejack.platform.registry.registry-node.server", true),

    DATABASE(DatabaseModel.class, "net.firejack.platform.registry.registry-node.database", true),

    FILESTORE(FileStoreModel.class, "net.firejack.platform.registry.registry-node.filestore", true),

    PACKAGE(PackageModel.class, "net.firejack.platform.registry.registry-node.package", true),

    ENTITY(EntityModel.class, "net.firejack.platform.registry.registry-node.entity", true),

    SUB_ENTITY(SubEntityModel.class, "net.firejack.platform.registry.registry-node.entity.sub-entity", true),

    FIELD(FieldModel.class, "net.firejack.platform.registry.registry-node.entity.registry-node-field", false),

    INDEX(IndexModel.class, "net.firejack.platform.registry.registry-node.entity.registry-node-index", false),

    RELATIONSHIP(RelationshipModel.class, "net.firejack.platform.registry.registry-node.entity.relationship", true),

    ACTION(ActionModel.class, "net.firejack.platform.registry.registry-node.action", true),

    ACTION_PARAMETER(ActionParameterModel.class, "net.firejack.platform.registry.registry-node.entity.registry-node-field", false),

    DIRECTORY(DirectoryModel.class, "net.firejack.platform.directory.directory", true),

    GROUP(GroupModel.class, "net.firejack.platform.directory.group", true),

    USER(UserModel.class, "net.firejack.platform.directory.base-user.user", false),

    SYSTEM_USER(SystemUserModel.class, "net.firejack.platform.directory.base-user.system-user", false),

    USER_PROFILE_FIELD(UserProfileFieldModel.class, "net.firejack.platform.directory.user-profile-field", false),

    USER_PROFILE_FIELD_GROUP(UserProfileFieldModel.class, "net.firejack.platform.directory.user-profile-field-group", false),

    PERMISSION(PermissionModel.class, "net.firejack.platform.authority.permission", false), // TODO may be need to remove or change

    ROLE(RoleModel.class, "net.firejack.platform.authority.role", false),   // TODO may be need to change entity type

    RESOURCE_LOCATION(ResourceLocationModel.class, "net.firejack.platform.authority.resource-location", false),

    FOLDER(FolderModel.class, "net.firejack.platform.content.folder", false),

    COLLECTION(CollectionModel.class, "net.firejack.platform.content.collection", false),

    NAVIGATION_ELEMENT(NavigationElementModel.class, "net.firejack.platform.site.navigation-element", true),

    TEXT_RESOURCE(TextResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.text-resource", false),

    HTML_RESOURCE(HtmlResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.html-resource", false),

    IMAGE_RESOURCE(ImageResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.image-resource", false),

    AUDIO_RESOURCE(AudioResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.audio-resource", false),

    VIDEO_RESOURCE(VideoResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.video-resource", false),

    DOCUMENT_RESOURCE(DocumentResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.document-resource", false),

    FILE_RESOURCE(FileResourceModel.class, "net.firejack.platform.content.abstract-resource.resource.file-resource", false),

    CONFIG(ConfigModel.class, "net.firejack.platform.config.config", false),

    PROCESS(ProcessModel.class, "net.firejack.platform.process.process", true),

    ACTOR(ActorModel.class, "net.firejack.platform.process.actor", true),

    ACTIVITY(ActivityModel.class, "net.firejack.platform.process.process.activity", false),

    STATUS(StatusModel.class, "net.firejack.platform.process.process.status", false),

    APPLICATION(ApplicationModel.class, "net.firejack.platform.directory.application", false),

    SCHEDULE(ScheduleModel.class, "net.firejack.platform.schedule.schedule", false),

	REPORT(ReportModel.class, "net.firejack.platform.registry.registry-node.report", true),

	BI_REPORT(BIReportModel.class, "net.firejack.platform.registry.registry-node.bi-report", true),

	WIZARD(WizardModel.class, "net.firejack.platform.registry.registry-node.wizard", true);

	private Class clazz;
	private String entityPath;
	private String table;
	private String type;
    private boolean searchable;

	RegistryNodeType(Class<?> clazz, String entityPath, Boolean searchable) {
		this.clazz = clazz;
		this.entityPath = entityPath;
        this.searchable = searchable;
		Table table = clazz.getAnnotation(Table.class);
		DiscriminatorValue discriminatorValue = clazz.getAnnotation(DiscriminatorValue.class);
		if (discriminatorValue != null) {
			this.type = discriminatorValue.value();
		} else {
			this.type = name();
		}
		if (table != null) {
			this.table = table.name();
		}
	}

	/**
     * @return
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * @param clazz
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * @return
     */
    public String getEntityPath() {
        return entityPath;
    }

    /**
     * @param entityPath
     */
    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public String getName() {
        return this.name();
    }

	/**
	 *
	 * @return
	 */
	public String getTable() {
		return table;
	}

	public String getType() {
		return type;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public static RegistryNodeType findByClass(Class<?> entity) {
		for (RegistryNodeType type : values()) {
			if (type.clazz.equals(entity)) {
				return type;
			}
		}
		return null;
	}

	public static RegistryNodeType find(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException e) {
			for (RegistryNodeType type : values()) {
				if (type.type.equals(name)) {
					return type;
				}
			}
		}
		return null;
	}

	public static RegistryNodeType findByEntityType(String entityType) {
        RegistryNodeType registryNodeType = null;
        if (entityType != null) {
            for (RegistryNodeType type : values()) {
                if (type.getEntityPath().equals(entityType)) {
                    registryNodeType = type;
                    break;
                }
            }
        }
        return registryNodeType;
	}

	public static List<String> searchable(){
		ArrayList<String> list = new ArrayList<String>();
		for (RegistryNodeType type : values()) {
			if(type.isSearchable()){
				list.add(type.getType());
			}
		}
		return list;
	}

	public static List<String> excludable(){
		ArrayList<String> list = new ArrayList<String>();
		for (RegistryNodeType type : values()) {
			if(!type.isSearchable()){
				list.add(type.getType());
			}
		}
		return list;
	}
}
