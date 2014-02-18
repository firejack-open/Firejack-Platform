//@tag opf-core
/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.define('OPF.core.utils.RegistryNodeType', {

    childAllowTypes: [],

    constructor: function(type, model, cfg) {
        cfg = cfg || {};
        OPF.core.utils.RegistryNodeType.superclass.constructor.call(Ext.Object.merge(this, cfg), cfg);
        this.type = type;
        this.model = Ext.ModelManager.getModel(model);
    },
    getType: function() {
        return this.type.toUpperCase();
    },
    toString: function() {
        return this.type;
    },
    getModel: function() {
        return this.model;
    },
    getPath: function() {
        return OPF.isNotEmpty(this.model) ? this.model.restSuffixUrl : null
    },
    createEditPanel: function(managerLayout) {
        return Ext.create(this.model.editorClassName, managerLayout, this);
    },
    createModel: function(data) {
        data = data || {};
        return Ext.create(this.model, data);
    },
    getConstraintName: function() {
        return OPF.isNotEmpty(this.model) ? this.model.constraintName : null;
    },
    getPageUrl: function() {
        return OPF.isNotEmpty(this.model) ? this.model.pageSuffixUrl : null;
    },
    getTitlePrefix: function() {
        return this.type.toUpperCase();
    },
    generateId : function(id) {
        return 'xnode-' + this.type + '-' + id;
    },
    generateUrl: function(urlPart) {
        var urlSuffix = this.getPath() + (OPF.isNotBlank(urlPart) ? urlPart : '');
        return OPF.Cfg.restUrl(urlSuffix);
    },
    generateGetUrl: function(id, urlPart) {
        var urlSuffix = this.getPath() + '/' + id + (OPF.isNotBlank(urlPart) ? urlPart : '');
        return OPF.Cfg.restUrl(urlSuffix);
    },
    generatePostUrl: function(urlPart) {
        return this.generateUrl(urlPart);
    },
    generatePutUrl: function(id, urlPart) {
        return this.generateGetUrl(id, urlPart);
    },
    generateDeleteUrl: function(id) {
        return this.generateGetUrl(id);
    },
    getChildAllowTypes: function() {
        return this.childAllowTypes;
    },
    setChildAllowTypes: function(childAllowTypes) {
        this.childAllowTypes = childAllowTypes;
    },
    containsAllowType: function(type, chainTypes) {
        var contains = false;
        Ext.each(this.childAllowTypes, function(allowType) {
            contains |= type.getType() == allowType;
        });
        return contains == 1;
    }
});
OPF.core.utils.RegistryNodeType.findRegistryNodeByType = function(type) {
    if (isEmpty(type)) {
        return null;
    }
    var types = OPF.core.utils.RegistryNodeType.types;
    for (var i = 0; i < types.length; i++) {
        if (type.toLowerCase() == types[i].toString()) {
            return types[i];
        }
    }
    return null;
};
OPF.core.utils.RegistryNodeType.findRegistryNodeById = function(id) {
    if (isEmpty(id)) {
        return null;
    }
    var m = SqParseTreeEntityId(id);
    if (m != null && m.length == 3) {
        var type = m[1];
        return OPF.core.utils.RegistryNodeType.findRegistryNodeByType(type);
    }
    return null;
};

OPF.core.utils.RegistryNodeType.EMPTY = new OPF.core.utils.RegistryNodeType(null, null, null, null);

OPF.core.utils.RegistryNodeType.REGISTRY = new OPF.core.utils.RegistryNodeType('registry', 'OPF.core.model.RegistryNodeTreeModel');
OPF.core.utils.RegistryNodeType.ROOT_DOMAIN = new OPF.core.utils.RegistryNodeType('root_domain', 'OPF.console.domain.model.RootDomain');
OPF.core.utils.RegistryNodeType.DOMAIN = new OPF.core.utils.RegistryNodeType('domain', 'OPF.console.domain.model.DomainModel', {
    containsAllowType: function(type, chainTypes) {
        var isPackageExist = false;
        Ext.each(chainTypes, function(chainType) {
            isPackageExist |= chainType == OPF.core.utils.RegistryNodeType.PACKAGE;
        });
        var contains = false;
        if (type == OPF.core.utils.RegistryNodeType.SYSTEM) {
            contains = !isPackageExist;
        } else if (type == OPF.core.utils.RegistryNodeType.ENTITY) {
            contains = isPackageExist;
        } else {
            Ext.each(this.childAllowTypes, function(allowType) {
                contains |= type.getType() == allowType;
            });
        }
        return contains == 1;
    }
});
OPF.core.utils.RegistryNodeType.SUB_DOMAIN = new OPF.core.utils.RegistryNodeType('sub_domain', null);
OPF.core.utils.RegistryNodeType.SYSTEM = new OPF.core.utils.RegistryNodeType('system', 'OPF.console.domain.model.SystemModel');
OPF.core.utils.RegistryNodeType.DATABASE = new OPF.core.utils.RegistryNodeType('database', 'OPF.console.domain.model.Database');
OPF.core.utils.RegistryNodeType.SERVER = new OPF.core.utils.RegistryNodeType('server', 'OPF.console.domain.model.Server');
OPF.core.utils.RegistryNodeType.FILESTORE = new OPF.core.utils.RegistryNodeType('filestore', 'OPF.console.domain.model.FilestoreModel');
OPF.core.utils.RegistryNodeType.PACKAGE = new OPF.core.utils.RegistryNodeType('package', 'OPF.console.domain.model.Package');
OPF.core.utils.RegistryNodeType.ENTITY = new OPF.core.utils.RegistryNodeType('entity', 'OPF.console.domain.model.EntityModel');
OPF.core.utils.RegistryNodeType.SUB_ENTITY = new OPF.core.utils.RegistryNodeType('sub_entity', 'OPF.console.domain.model.EntityModel');
OPF.core.utils.RegistryNodeType.ACTION = new OPF.core.utils.RegistryNodeType('action', 'OPF.console.domain.model.ActionModel');
OPF.core.utils.RegistryNodeType.ACTION_PARAMETER = new OPF.core.utils.RegistryNodeType('action_parameter', 'OPF.console.domain.model.ActionParameterModel');
OPF.core.utils.RegistryNodeType.RELATIONSHIP = new OPF.core.utils.RegistryNodeType('relationship', 'OPF.console.domain.model.RelationshipModel');
OPF.core.utils.RegistryNodeType.PROCESS = new OPF.core.utils.RegistryNodeType('process', 'OPF.console.domain.model.ProcessModel');
OPF.core.utils.RegistryNodeType.PROCESS_FIELD = new OPF.core.utils.RegistryNodeType('process_field', 'OPF.console.domain.model.ProcessFieldModel');
OPF.core.utils.RegistryNodeType.ACTOR = new OPF.core.utils.RegistryNodeType('actor', 'OPF.console.domain.model.ActorModel');
OPF.core.utils.RegistryNodeType.ACTIVITY = new OPF.core.utils.RegistryNodeType('activity', 'OPF.console.domain.model.ActivityModel');
OPF.core.utils.RegistryNodeType.CASE_EXPLANATION = new OPF.core.utils.RegistryNodeType('case_explanation', 'OPF.console.domain.model.CaseExplanationModel');
OPF.core.utils.RegistryNodeType.CASE = new OPF.core.utils.RegistryNodeType('case', 'OPF.console.inbox.model.CaseModel');
OPF.core.utils.RegistryNodeType.CASE_NOTE = new OPF.core.utils.RegistryNodeType('case_note', 'OPF.console.inbox.model.CaseNoteModel');
OPF.core.utils.RegistryNodeType.CASE_ATTACHMENT = new OPF.core.utils.RegistryNodeType('case_attachment', 'OPF.console.inbox.model.CaseAttachmentModel');
OPF.core.utils.RegistryNodeType.CASE_ACTION = new OPF.core.utils.RegistryNodeType('case_action', 'OPF.console.inbox.model.CaseActionModel');
OPF.core.utils.RegistryNodeType.TASK = new OPF.core.utils.RegistryNodeType('task', 'OPF.console.inbox.model.TaskModel');
OPF.core.utils.RegistryNodeType.STATUS = new OPF.core.utils.RegistryNodeType('status', 'OPF.console.domain.model.StatusModel');
OPF.core.utils.RegistryNodeType.REPORT = new OPF.core.utils.RegistryNodeType('report', 'OPF.console.domain.model.ReportModel');
OPF.core.utils.RegistryNodeType.BI_REPORT = new OPF.core.utils.RegistryNodeType('bi_report', 'OPF.console.domain.model.BIReportModel');
OPF.core.utils.RegistryNodeType.WIZARD = new OPF.core.utils.RegistryNodeType('wizard', 'OPF.console.domain.model.WizardModel');

OPF.core.utils.RegistryNodeType.DIRECTORY = new OPF.core.utils.RegistryNodeType('directory', 'OPF.console.directory.model.Directory');
OPF.core.utils.RegistryNodeType.GROUP = new OPF.core.utils.RegistryNodeType('group', 'OPF.console.directory.model.Group');
OPF.core.utils.RegistryNodeType.USER = new OPF.core.utils.RegistryNodeType('user', 'OPF.console.directory.model.UserModel');
OPF.core.utils.RegistryNodeType.SYSTEM_USER = new OPF.core.utils.RegistryNodeType('system_user', 'OPF.console.directory.model.SystemUserModel');
OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD = new OPF.core.utils.RegistryNodeType('user_profile_field', 'OPF.console.directory.model.UserProfileField');
OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP = new OPF.core.utils.RegistryNodeType('user_profile_field_group', 'OPF.console.directory.model.UserProfileFieldGroup');

OPF.core.utils.RegistryNodeType.ROLE = new OPF.core.utils.RegistryNodeType('role', 'OPF.console.authority.model.Role');
OPF.core.utils.RegistryNodeType.PERMISSION = new OPF.core.utils.RegistryNodeType('permission', 'OPF.console.authority.model.Permission');
OPF.core.utils.RegistryNodeType.RESOURCE_LOCATION = new OPF.core.utils.RegistryNodeType('resource_location', 'OPF.console.authority.model.ResourceLocation');
OPF.core.utils.RegistryNodeType.POLICY = new OPF.core.utils.RegistryNodeType('policy', '', 'OPF.authority.Policy', 'console/authority');
OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE = new OPF.core.utils.RegistryNodeType('assigned_role', 'OPF.console.security.AssignedRoleModel');

OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT = new OPF.core.utils.RegistryNodeType('navigation_element', 'OPF.console.site.model.NavigationElement');

OPF.core.utils.RegistryNodeType.CONFIG = new OPF.core.utils.RegistryNodeType('config', 'OPF.console.resource.model.ConfigModel');
OPF.core.utils.RegistryNodeType.SCHEDULE = new OPF.core.utils.RegistryNodeType('schedule', 'OPF.console.resource.model.ScheduleModel');

OPF.core.utils.RegistryNodeType.FOLDER = new OPF.core.utils.RegistryNodeType('folder', 'OPF.console.resource.model.FolderModel');
OPF.core.utils.RegistryNodeType.COLLECTION = new OPF.core.utils.RegistryNodeType('collection', 'OPF.console.resource.model.CollectionModel');
OPF.core.utils.RegistryNodeType.RESOURCE = new OPF.core.utils.RegistryNodeType('resource', 'OPF.console.resource.model.ResourceModel');
OPF.core.utils.RegistryNodeType.TEXT_RESOURCE = new OPF.core.utils.RegistryNodeType('text_resource', 'OPF.console.resource.model.TextResourceModel');
OPF.core.utils.RegistryNodeType.HTML_RESOURCE = new OPF.core.utils.RegistryNodeType('html_resource', 'OPF.console.resource.model.HtmlResourceModel');
OPF.core.utils.RegistryNodeType.IMAGE_RESOURCE = new OPF.core.utils.RegistryNodeType('image_resource', 'OPF.console.resource.model.ImageResourceModel');
OPF.core.utils.RegistryNodeType.AUDIO_RESOURCE = new OPF.core.utils.RegistryNodeType('audio_resource', 'OPF.console.resource.model.AudioResourceModel');
OPF.core.utils.RegistryNodeType.VIDEO_RESOURCE = new OPF.core.utils.RegistryNodeType('video_resource', 'OPF.console.resource.model.VideoResourceModel');
OPF.core.utils.RegistryNodeType.DOCUMENT_RESOURCE = new OPF.core.utils.RegistryNodeType('document_resource', 'OPF.console.resource.model.DocumentResourceModel');
OPF.core.utils.RegistryNodeType.FILE_RESOURCE = new OPF.core.utils.RegistryNodeType('file_resource', 'OPF.console.resource.model.FileResourceModel');

OPF.core.utils.RegistryNodeType.METRICS_ENTRY = new OPF.core.utils.RegistryNodeType('metrics_entry', 'OPF.console.statistics.model.MetricsEntry');
OPF.core.utils.RegistryNodeType.LOG_ENTRY = new OPF.core.utils.RegistryNodeType('log_entry', 'OPF.console.statistics.model.LogEntry');
OPF.core.utils.RegistryNodeType.LOG_TRANSACTION = new OPF.core.utils.RegistryNodeType('log_transaction', 'OPF.console.statistics.model.LogTransactionModel');
OPF.core.utils.RegistryNodeType.PACKAGE_VERSION = new OPF.core.utils.RegistryNodeType('package_version', 'OPF.console.domain.model.PackageVersionModel');

OPF.core.utils.RegistryNodeType.types = [
    OPF.core.utils.RegistryNodeType.REGISTRY,
    OPF.core.utils.RegistryNodeType.ROOT_DOMAIN,
    OPF.core.utils.RegistryNodeType.DOMAIN,
    OPF.core.utils.RegistryNodeType.SUB_DOMAIN,
    OPF.core.utils.RegistryNodeType.SYSTEM,
    OPF.core.utils.RegistryNodeType.DATABASE,
    OPF.core.utils.RegistryNodeType.SERVER,
    OPF.core.utils.RegistryNodeType.FILESTORE,
    OPF.core.utils.RegistryNodeType.PACKAGE,
    OPF.core.utils.RegistryNodeType.ENTITY,
    OPF.core.utils.RegistryNodeType.SUB_ENTITY,
    OPF.core.utils.RegistryNodeType.ACTION,
    OPF.core.utils.RegistryNodeType.RELATIONSHIP,
    OPF.core.utils.RegistryNodeType.PROCESS,
    OPF.core.utils.RegistryNodeType.ACTOR,
    OPF.core.utils.RegistryNodeType.REPORT,
    OPF.core.utils.RegistryNodeType.BI_REPORT,
    OPF.core.utils.RegistryNodeType.WIZARD,

    OPF.core.utils.RegistryNodeType.DIRECTORY,
    OPF.core.utils.RegistryNodeType.GROUP,
    OPF.core.utils.RegistryNodeType.USER,
    OPF.core.utils.RegistryNodeType.SYSTEM_USER,
    OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD,
    OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP,

    OPF.core.utils.RegistryNodeType.ROLE,
    OPF.core.utils.RegistryNodeType.PERMISSION,
    OPF.core.utils.RegistryNodeType.RESOURCE_LOCATION,
    OPF.core.utils.RegistryNodeType.POLICY,

    OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT,

    OPF.core.utils.RegistryNodeType.CONFIG,
    OPF.core.utils.RegistryNodeType.SCHEDULE,

    OPF.core.utils.RegistryNodeType.FOLDER,
    OPF.core.utils.RegistryNodeType.COLLECTION,
    OPF.core.utils.RegistryNodeType.RESOURCE,
    OPF.core.utils.RegistryNodeType.TEXT_RESOURCE,
    OPF.core.utils.RegistryNodeType.HTML_RESOURCE,
    OPF.core.utils.RegistryNodeType.IMAGE_RESOURCE,
    OPF.core.utils.RegistryNodeType.AUDIO_RESOURCE,
    OPF.core.utils.RegistryNodeType.VIDEO_RESOURCE,
    OPF.core.utils.RegistryNodeType.DOCUMENT_RESOURCE,
    OPF.core.utils.RegistryNodeType.FILE_RESOURCE,

    OPF.core.utils.RegistryNodeType.METRICS_ENTRY,
    OPF.core.utils.RegistryNodeType.LOG_ENTRY
];

OPF.core.utils.RegistryNodeType.AllowTypes = [];

OPF.core.utils.RegistryNodeType.initializeAllowTypes = function() {
    var allowTypes = OPF.core.utils.RegistryNodeType.AllowTypes;
    for (var type in allowTypes) {
        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(type.toLowerCase());
        if (isNotEmpty(registryNodeType)) {
            registryNodeType.setChildAllowTypes(allowTypes[type]);
        }
    }
};