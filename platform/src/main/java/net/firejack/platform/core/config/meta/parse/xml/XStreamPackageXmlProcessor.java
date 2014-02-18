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

package net.firejack.platform.core.config.meta.parse.xml;

import com.thoughtworks.xstream.XStream;
import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.*;
import net.firejack.platform.core.config.meta.element.BaseNavigableRegistryNodeElement;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.NavigationConfigElement;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.action.ActionParameterElement;
import net.firejack.platform.core.config.meta.element.authority.*;
import net.firejack.platform.core.config.meta.element.conf.ConfigReference;
import net.firejack.platform.core.config.meta.element.directory.DirectoryElement;
import net.firejack.platform.core.config.meta.element.directory.GroupElement;
import net.firejack.platform.core.config.meta.element.directory.UserElement;
import net.firejack.platform.core.config.meta.element.directory.UserRoleRef;
import net.firejack.platform.core.config.meta.element.process.*;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.config.meta.element.resource.*;
import net.firejack.platform.core.config.meta.element.schedule.ScheduleElement;
import net.firejack.platform.core.config.meta.expr.*;
import net.firejack.platform.core.utils.Tuple;
import org.apache.log4j.Logger;


class XStreamPackageXmlProcessor implements IConfigClassConsumer {

    private static final Logger logger = Logger.getLogger(XStreamPackageXmlProcessor.class);

    private Class<? extends IEntityElement> packageEntityClass;
    private Class<? extends IFieldElement> fieldClass;
    private Class<? extends IIndexElement> indexClass;
    private Class<? extends IRelationshipElement> relationshipClass;
    private Class<? extends IDomainElement> domainClass;


    @Override
    @SuppressWarnings("unchecked")
    public <T extends IPackageDescriptor> T packageFromXml(String xml, Class<T> packageVersionClass) {
        XStream xStream = prepareXStream(packageVersionClass);
        try {
            return (T) xStream.fromXML(xml);
        } catch (ClassCastException e) {
            logger.warn("Wrong data for XML serialization", e);
            return null;
        }
    }

    @Override
    public <T extends IPackageDescriptor> String packageToXml(T packageVersion) {
        XStream xStream = prepareXStream(packageVersion.getClass());
        return xStream.toXML(packageVersion);
    }

    @Override
    public void setFieldClass(Class<? extends IFieldElement> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public void setIndexClass(Class<? extends IIndexElement> indexClass) {
        this.indexClass = indexClass;
    }

    @Override
    public void setPackageEntityClass(Class<? extends IEntityElement> packageEntityClass) {
        this.packageEntityClass = packageEntityClass;
    }

    @Override
    public void setRelationshipClass(Class<? extends IRelationshipElement> relationshipClass) {
        this.relationshipClass = relationshipClass;
    }

    @Override
    public void setDomainClass(Class<? extends IDomainElement> domainClass) {
        this.domainClass = domainClass;
    }

    private XStream prepareXStream(Class<? extends IPackageDescriptor> versionPackageClass) {
        XStream xStream = new XStream();
        xStream.alias("package", versionPackageClass);
        xStream.useAttributeFor(versionPackageClass, "name");
        xStream.useAttributeFor(versionPackageClass, "path");
        xStream.useAttributeFor(versionPackageClass, "version");
        xStream.useAttributeFor(versionPackageClass, "prefix");
        xStream.useAttributeFor(versionPackageClass, "dependencies");
	    xStream.aliasAttribute(versionPackageClass, "contextUrl", "context-url");
        xStream.useAttributeFor(versionPackageClass, "uid");
        xStream.addImplicitCollection(versionPackageClass,
                "configuredEntities", IEntityElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "configuredDomains", IDomainElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "relationshipElements", IRelationshipElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "actionElements", ActionElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "directoryElements", DirectoryElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "groupElements", GroupElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "scheduleElements", ScheduleElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "userElements", UserElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "navigationElements", NavigationConfigElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "roleElements", RoleElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "roleAssignmentElements", RoleAssignmentElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "permissionElements", PermissionElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "resourceLocationElements", ResourceLocationElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "folderElements", FolderElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "resourceElements", ResourceElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "configsElements", ConfigReference.class);
        xStream.addImplicitCollection(versionPackageClass,
                "actorElements", ActorElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "collectionElements", CollectionElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "processElements", ProcessElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "userProfileFields", UserProfileFieldElement.class);
        xStream.addImplicitCollection(versionPackageClass,
                "userProfileFieldGroups", UserProfileFieldGroupElement.class);
	    xStream.addImplicitCollection(versionPackageClass,
             "reportElements", ReportElement.class);
	    xStream.addImplicitCollection(versionPackageClass,
             "biReportElements", BIReportElement.class);
        xStream.addImplicitCollection(versionPackageClass,
             "wizardElements", WizardElement.class);

        xStream.omitField(BaseConfigElement.class, "path");
        xStream.useAttributeFor(BaseConfigElement.class, "name");
        xStream.useAttributeFor(BaseConfigElement.class, "uid");

        xStream.alias("field", fieldClass);
        xStream.useAttributeFor(fieldClass, "required");
        xStream.useAttributeFor(fieldClass, "searchable");
        xStream.useAttributeFor(fieldClass, "displayName");
        xStream.useAttributeFor(fieldClass, "type");
        xStream.useAttributeFor(fieldClass, "typePath");
        xStream.useAttributeFor(fieldClass, "customType");
        xStream.useAttributeFor(fieldClass, "autoGenerated");
        xStream.addImplicitCollection(fieldClass, "options");
	    xStream.aliasField("description", fieldClass, "description");

        xStream.alias("index", indexClass);
        xStream.useAttributeFor(indexClass, "type");
        xStream.addImplicitCollection(indexClass, "fields", IFieldElement.class);
        xStream.addImplicitCollection(indexClass, "entities", Reference.class);
        xStream.aliasField("relationship", indexClass, "relationship");

        xStream.alias("reference-object", ReferenceObjectData.class);
        xStream.aliasField("sub-heading", ReferenceObjectData.class, "subHeading");

        xStream.alias("context-role", EntityRole.class);
        xStream.useAttributeFor(EntityRole.class, "path");

        xStream.alias("entity", packageEntityClass);
        xStream.useAttributeFor(packageEntityClass, "extendedEntityPath");
        xStream.aliasAttribute(packageEntityClass, "extendedEntityPath", "extends");
        xStream.useAttributeFor(packageEntityClass, "abstractEntity");
        xStream.aliasAttribute(packageEntityClass, "abstractEntity", "abstract");
        xStream.useAttributeFor(packageEntityClass, "typeEntity");
        xStream.aliasAttribute(packageEntityClass, "typeEntity", "vo");
        xStream.useAttributeFor(packageEntityClass, "alias");
        xStream.useAttributeFor(packageEntityClass, "securityEnabled");
        xStream.useAttributeFor(packageEntityClass, "subEntity");
        xStream.useAttributeFor(packageEntityClass, "databaseRefName");
        xStream.addImplicitCollection(packageEntityClass, "fields", IFieldElement.class);
        xStream.addImplicitCollection(packageEntityClass, "indexes", IIndexElement.class);
        xStream.addImplicitCollection(packageEntityClass, "configuredEntities", packageEntityClass);
//        xStream.addImplicitCollection(packageEntityClass, "nestedEntities", packageEntityClass);
        xStream.aliasField("req-data", packageEntityClass, "requiredData");
        xStream.aliasField("unique-set", packageEntityClass, "compoundKeyColumnsRules");
        xStream.omitField(packageEntityClass, "parent");
	    xStream.aliasField("description", packageEntityClass, "description");
        xStream.aliasField("supported-context-roles", packageEntityClass, "allowableContextRoles");
        xStream.aliasField("reference-object", packageEntityClass, "referenceObject");
        xStream.omitField(packageEntityClass, "reverseEngineer");
        //xStream.addImplicitCollection(packageEntityClass, "allowableContextRoles", EntityRole.class);
        ///xStream.addImplicitCollection(packageEntityClass, "compoundKeyColumnsRules", Set.class);

        xStream.alias("relationship", relationshipClass);
        xStream.aliasField("hint", relationshipClass, "hint");
        xStream.useAttributeFor(relationshipClass, "sortable");
        xStream.useAttributeFor(relationshipClass, "type");
        xStream.useAttributeFor(relationshipClass, "onDeleteOptions");
        xStream.useAttributeFor(relationshipClass, "onUpdateOptions");
        xStream.useAttributeFor(relationshipClass, "required");
        xStream.addImplicitCollection(relationshipClass, "fields", IFieldElement.class);
        xStream.omitField(relationshipClass, "reverseEngineer");

        xStream.alias("directory", DirectoryElement.class);
        xStream.aliasField("server-name", DirectoryElement.class, "serverName");
        xStream.aliasField("url-path", DirectoryElement.class, "urlPath");
        xStream.aliasField("directory-type", DirectoryElement.class, "directoryType");
        xStream.aliasField("status", DirectoryElement.class, "status");
        xStream.addImplicitCollection(DirectoryElement.class, "fields", IFieldElement.class);

        xStream.alias("group", GroupElement.class);
        xStream.useAttributeFor(GroupElement.class, "directoryRef");
        xStream.aliasAttribute(GroupElement.class, "directoryRef", "directory-ref");

        xStream.alias("schedule", ScheduleElement.class);
        xStream.useAttributeFor(ScheduleElement.class, "actionRef");
        xStream.aliasAttribute(ScheduleElement.class, "actionRef", "action-ref");
        xStream.aliasField("cron", ScheduleElement.class, "cronExpression");

        xStream.alias("user", UserElement.class);
//        xStream.aliasAttribute(UserElement.class, "name", "name");
        xStream.aliasField("first-name", UserElement.class, "firstName");
        xStream.aliasField("middle-name", UserElement.class, "middleName");
        xStream.aliasField("last-name", UserElement.class, "lastName");
        xStream.addImplicitCollection(UserElement.class, "userRoles", UserRoleRef.class);
        xStream.alias("user-role", UserRoleRef.class);
        xStream.useAttributeFor(UserRoleRef.class, "path");

        xStream.useAttributeFor(PackageDescriptorElement.class, "name");
        xStream.useAttributeFor(PackageDescriptorElement.class, "uid");
        xStream.alias("action", ActionElement.class);
        xStream.addImplicitCollection(ActionElement.class, "parameters", ActionParameterElement.class);
        xStream.alias("parameter", ActionParameterElement.class);
        xStream.useAttributeFor(ActionParameterElement.class, "name");
        xStream.useAttributeFor(ActionParameterElement.class, "uid");
        xStream.useAttributeFor(ActionParameterElement.class, "type");
        xStream.useAttributeFor(ActionParameterElement.class, "location");
        xStream.useAttributeFor(ActionParameterElement.class, "orderPosition");
        xStream.aliasAttribute(ActionParameterElement.class, "orderPosition", "order");
        xStream.alias("role-assignment", RoleAssignmentElement.class);
        xStream.useAttributeFor(RoleAssignmentElement.class, "path");
        xStream.addImplicitCollection(RoleAssignmentElement.class, "permissions", RolePermissionReference.class);

        xStream.alias("navigation", NavigationConfigElement.class);
        xStream.aliasField("url-params", NavigationConfigElement.class, "urlParams");
        xStream.useAttributeFor(NavigationConfigElement.class, "hidden");
        xStream.alias("role", RoleElement.class);
        xStream.addImplicitCollection(RoleElement.class, "permissions", RolePermissionReference.class);
        xStream.alias("role-permission", RolePermissionReference.class);
        xStream.useAttributeFor(RolePermissionReference.class, "path");
        xStream.alias("permission", PermissionElement.class);
        xStream.alias("folder", FolderElement.class);
        xStream.useAttributeFor(ActionElement.class, "method");
        xStream.useAttributeFor(PackageDescriptorElement.class, "path");

        xStream.aliasField("server-name", BaseNavigableRegistryNodeElement.class, "serverName");
        xStream.aliasField("url-path", BaseNavigableRegistryNodeElement.class, "urlPath");
        xStream.aliasField("parent-path", BaseNavigableRegistryNodeElement.class, "parentPath");
        xStream.aliasField("http-method", ActionElement.class, "method");
        xStream.aliasField("soap-url-path", ActionElement.class, "soapUrlPath");
        xStream.aliasField("soap-method", ActionElement.class, "soapMethod");
        xStream.aliasField("input-content-entity", ActionElement.class, "inputVOEntityLookup");
        xStream.aliasField("output-content-entity", ActionElement.class, "outputVOEntityLookup");
	    xStream.aliasField("description", ActionElement.class, "description");

        xStream.alias("reference", Reference.class);
        xStream.useAttributeFor(Reference.class, "refName");
        xStream.useAttributeFor(Reference.class, "refPath");
        xStream.useAttributeFor(Reference.class, "constraintName");

        xStream.alias("tuple", Tuple.class);
        xStream.alias("binaryExpression", BinaryExpression.class);
        xStream.alias("unaryExpression", UnaryExpression.class);
        xStream.alias("functionExpression", FunctionExpression.class);
        xStream.alias("identifiableExpression", IdentifiableExpression.class);
        xStream.alias("wrappedExpression", InBracesExpression.class);
        xStream.alias("itemSetExpression", InExpression.class);
        xStream.alias("valueExpression", ValueExpression.class);
        ////////////////////////////////
        //xStream.alias("set", Set.class);
        //xStream.alias("set", Collections.singleton("").getClass());
        ////////////////////////////////

        xStream.alias("unique-column", CompoundKeyParticipantColumn.class);
        xStream.useAttributeFor(CompoundKeyParticipantColumn.class, "ref");
        xStream.useAttributeFor(CompoundKeyParticipantColumn.class, "columnName");
        xStream.useAttributeFor(CompoundKeyParticipantColumn.class, "refToParent");

        xStream.aliasAttribute(CompoundKeyParticipantColumn.class, "columnName", "name");
        xStream.alias("unique-columns-rule", CompoundKeyColumnsRule.class);
        xStream.aliasAttribute(CompoundKeyColumnsRule.class, "name", "name");
        xStream.addImplicitCollection(
                CompoundKeyColumnsRule.class, "compoundKeyParticipantColumns");

        // Resource
        xStream.alias("resource", ResourceElement.class);
        xStream.addImplicitCollection(ResourceElement.class,
                "textResourceVersionElements", TextResourceVersionElement.class);
        xStream.addImplicitCollection(ResourceElement.class,
                "htmlResourceVersionElements", HtmlResourceVersionElement.class);
        xStream.addImplicitCollection(ResourceElement.class,
                "imageResourceVersionElements", ImageResourceVersionElement.class);
        xStream.addImplicitCollection(ResourceElement.class,
                "audioResourceVersionElements", AudioResourceVersionElement.class);
        xStream.addImplicitCollection(ResourceElement.class,
                "videoResourceVersionElements", VideoResourceVersionElement.class);
        xStream.aliasAttribute(ResourceElement.class, "resourceType", "type");
        xStream.alias("text-resource-version", TextResourceVersionElement.class);
        xStream.alias("html-resource-version", HtmlResourceVersionElement.class);
        xStream.alias("image-resource-version", ImageResourceVersionElement.class);
        xStream.alias("audio-resource-version", AudioResourceVersionElement.class);
        xStream.alias("video-resource-version", VideoResourceVersionElement.class);

        xStream.useAttributeFor(ResourceElement.class, "lastVersion");
        xStream.aliasAttribute(ResourceElement.class, "lastVersion", "last-version");

        //Resource Location
        xStream.alias("resource-location", ResourceLocationElement.class);
        xStream.useAttributeFor("wildcardStyle", ResourceLocationElement.class);
        xStream.aliasAttribute(ResourceLocationElement.class, "wildcardStyle", "style");

		//Configs
	    xStream.alias("config", ConfigReference.class);
	    xStream.aliasField("value", ConfigReference.class, "value");
	    xStream.aliasField("description", ConfigReference.class, "description");

        //Actors
        xStream.alias("actor", ActorElement.class);
        xStream.aliasField("distribution-email", ActorElement.class, "distributionEmail");
	    xStream.aliasField("users", ActorElement.class, "users");
	    xStream.aliasField("roles", ActorElement.class, "roles");
	    xStream.aliasField("groups", ActorElement.class, "groups");

	    xStream.alias("assign-user", UserAssignElement.class);
	    xStream.alias("assign-role", RoleAssignElement.class);
	    xStream.alias("assign-group", GroupAssignElement.class);

        //Collection
        xStream.alias("collection", CollectionElement.class);
        xStream.addImplicitCollection(CollectionElement.class,
                "collectionMembers", CollectionMemberElement.class);

        xStream.alias("collection-member", CollectionMemberElement.class);
        xStream.useAttributeFor("reference", CollectionMemberElement.class);
        xStream.aliasAttribute(CollectionMemberElement.class, "reference", "ref");
        xStream.useAttributeFor(CollectionMemberElement.class, "order");

	    //Process
	    xStream.alias("process", ProcessElement.class);
	    xStream.aliasField("activities", ActivityElement.class, "activities");
	    xStream.aliasField("statuses", StatusElement.class, "statuses");
	    xStream.aliasField("explanations", ExplanationElement.class, "explanations");
	    xStream.aliasField("custom-fields", ProcessFieldElement.class, "customFields");

        xStream.alias("custom-field", ProcessFieldElement.class);
        xStream.useAttributeFor(ProcessFieldElement.class, "displayName");
        xStream.aliasAttribute(ProcessFieldElement.class, "displayName", "display-name");
        xStream.useAttributeFor(ProcessFieldElement.class, "orderPosition");
        xStream.aliasAttribute(ProcessFieldElement.class, "orderPosition", "order-position");
        xStream.useAttributeFor(ProcessFieldElement.class, "global");
        xStream.omitField(ProcessFieldElement.class, "name");
        xStream.omitField(ProcessFieldElement.class, "description");
        xStream.aliasField("value-type", ProcessFieldElement.class, "valueType");
        xStream.aliasField("entity-ref", ProcessFieldElement.class, "entityReference");
        xStream.useAttributeFor(EntityReference.class, "entityUid");
        xStream.aliasField("field-ref", ProcessFieldElement.class, "fieldReference");
        xStream.useAttributeFor(FieldReference.class, "fieldUid");

	    xStream.alias("activity", ActivityElement.class);
	    xStream.alias("status", StatusElement.class);
	    xStream.alias("explanation", ExplanationElement.class);

	    xStream.aliasAttribute(ActivityElement.class, "type", "type");
	    xStream.aliasAttribute(ActivityElement.class, "status", "status");
	    xStream.aliasAttribute(ActivityElement.class, "actor", "actor");
	    xStream.aliasAttribute(ActivityElement.class, "order", "order");
        xStream.aliasAttribute(ActivityElement.class, "notify", "notify");
	    xStream.aliasAttribute(StatusElement.class, "order", "order");
	    xStream.aliasField("short", ExplanationElement.class, "shortDescription");
	    xStream.aliasField("long", ExplanationElement.class, "longDescription");

//        xStream.setMode(XStream.ID_REFERENCES);

        xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);

        //Domain
        xStream.alias("domain", domainClass);
        xStream.addImplicitCollection(domainClass, "configuredDomains", IDomainElement.class);
        xStream.addImplicitCollection(domainClass, "configuredEntities", IEntityElement.class);
        xStream.useAttributeFor(domainClass, "prefix");
        xStream.useAttributeFor(domainClass, "dataSource");
        xStream.omitField(domainClass, "parent");
        xStream.omitField(domainClass, "versionSubDomain");

        //User Profile Fields
        xStream.alias("profile-field", UserProfileFieldElement.class);
        xStream.useAttributeFor(UserProfileFieldElement.class, "type");
        xStream.useAttributeFor(UserProfileFieldElement.class, "groupLookup");

        //User Profile Field Groups
        xStream.alias("profile-field-group", UserProfileFieldGroupElement.class);

	    xStream.alias("report", ReportElement.class);
	    xStream.addImplicitCollection(ReportElement.class, "fields", ReportField.class);

        xStream.alias("report-field", ReportField.class);
        xStream.addImplicitCollection(ReportField.class, "relationships", Reference.class);
        xStream.useAttributeFor(ReportField.class, "field");
        xStream.useAttributeFor(ReportField.class, "displayName");
        xStream.useAttributeFor(ReportField.class, "visible");
        xStream.useAttributeFor(ReportField.class, "searchable");

	    xStream.alias("bi-report", BIReportElement.class);
	    xStream.addImplicitCollection(BIReportElement.class, "fields", BIReportField.class);

        xStream.alias("bi-report-field", BIReportField.class);
        xStream.useAttributeFor(BIReportField.class, "entity");
        xStream.useAttributeFor(BIReportField.class, "field");
        xStream.useAttributeFor(BIReportField.class, "displayName");
        xStream.useAttributeFor(BIReportField.class, "count");

        xStream.alias("wizard", WizardElement.class);
        xStream.useAttributeFor(WizardElement.class, "model");
        xStream.addImplicitCollection(WizardElement.class, "forms", WizardFormElement.class);

        xStream.alias("wizard-form", WizardFormElement.class);
        xStream.useAttributeFor(WizardFormElement.class, "displayName");
        xStream.addImplicitCollection(WizardFormElement.class, "fields", WizardFieldElement.class);

        xStream.alias("wizard-field", WizardFieldElement.class);
        xStream.useAttributeFor(WizardFieldElement.class, "field");
        xStream.useAttributeFor(WizardFieldElement.class, "relationship");
        xStream.useAttributeFor(WizardFieldElement.class, "displayName");
        xStream.useAttributeFor(WizardFieldElement.class, "editable");
        xStream.useAttributeFor(WizardFieldElement.class, "defaultValue");

        return xStream;
    }
}
