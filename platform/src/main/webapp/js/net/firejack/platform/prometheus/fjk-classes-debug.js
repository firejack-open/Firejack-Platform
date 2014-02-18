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


Ext.define('OPF.console.directory.model.Directory', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/directory',
        editorClassName: 'OPF.console.directory.editor.DirectoryEditor',
        constraintName: 'OPF.directory.Directory'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'serverName', type: 'string' },
        { name: 'urlPath', type: 'string' },
        { name: 'sortPosition', type: 'int' },
        { name: 'directoryType', type: 'string' },
        { name: 'status', type: 'string' },
        { name: 'port', type: 'int', useNull: true },
        { name: 'baseDN', type: 'string', useNull: true },
        { name: 'rootDN', type: 'string', useNull: true },
        { name: 'password', type: 'string', useNull: true },
        { name: 'allowEncodedPassword', type: 'boolean', useNull: true },
        { name: 'passwordEncodingType', type: 'string', useNull: true },
        { name: 'peopleObjectclass', type: 'string', useNull: true },
        { name: 'groupsObjectclass', type: 'string', useNull: true },
        { name: 'directoryServiceTitle', type: 'string' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    hasMany: { model: 'OPF.console.domain.model.FieldModel', name: 'directoryFields', associationKey: 'directoryFields', foreignKey: 'parentId'}














});




Ext.define('OPF.console.directory.model.Group', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/group',
        editorClassName: 'OPF.console.directory.editor.GroupEditor',
        constraintName: 'OPF.directory.Group'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'distinguishedName', type: 'string', useNull: true },

        { name: 'parentId', type: 'int' },

        { name: 'directory', type: 'auto' }
    ]
});




Ext.define('OPF.console.directory.model.SecuredEntity', {
    extend:  Ext.data.Model ,

    statics: {
        restSuffixUrl: 'authority/role'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string'},
        { name: 'description', type: 'string'},
        { name: 'securedRecordId', type: 'int', useNull: true }
    ],

    belongsTo: 'User'
});


Ext.define('OPF.console.directory.model.SystemUserModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/system_user',
        editorClassName: 'OPF.console.directory.editor.SystemUserEditor',
        constraintName: 'OPF.directory.SystemUser'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'username', type: 'string' },
        { name: 'password', type: 'string' },
        { name: 'passwordConfirm', type: 'string' },
        { name: 'email', type: 'string' },
        { name: 'firstName', type: 'string' },
        { name: 'middleName', type: 'string' },
        { name: 'lastName', type: 'string' },
        { name: 'guest', type: 'boolean' },
        { name: 'registryNodeId', type: 'int' },
        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },
        { name: 'roles', persist: false }
    ],

    hasMany: [
        { model: 'OPF.console.authority.model.Role', name: 'roles' }
    ]

});




String.prototype.startsWith = function(str) {
    return (this.match("^"+str)==str)
};
String.prototype.endsWith = function (s) {
  return this.length >= s.length && this.substr(this.length - s.length) == s;
};
String.prototype.trim = function(){
    return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
};

Array.prototype.max = function() {
    var max = this[0];
    var len = this.length;
    for (var i = 1; i < len; i++) if (this[i] > max) max = this[i];
    return max;
};
Array.prototype.min = function() {
    var min = this[0];
    var len = this.length;
    for (var i = 1; i < len; i++) if (this[i] < min) min = this[i];
    return min;
};
Array.prototype.sum = function() {
    var sum = 0;
    var len = this.length;
    for (var i = 0; i < len; i++) sum += this[i];
    return sum;
};
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};
//@require PrototypeUtils.js



Ext.define('OPF.core.utils.Config', {
    alternateClassName: ['OPF.Cfg'],

    OPF_CONSOLE_URL: null,
    DEBUG_MODE: null,
    DEFAULT_LOGIN: null,
    DEFAULT_PASSWORD: null,
    CAN_EDIT_RESOURCE: null,
    PAGE_TYPE: null,
    PAGE_UID: null,

    BASE_URL: null,
    NAVIGATION_LOOKUP: null,
    PACKAGE_ID: null,
    PACKAGE_LOOKUP: null,
    CACHE_VERSION: null,

    EXTRA_PARAMS: null,

    DASHBOARD_PREFIX_URL: null,

    USER_INFO: {
        id: null,
        username: null,
        isLogged: false
    },

    fullUrl: function(urlSuffix, useBaseUrl) {
        var url;
        if (urlSuffix.startsWith('http://')) {
            url = urlSuffix;
        } else {
            url = useBaseUrl ? this.BASE_URL : this.OPF_CONSOLE_URL;
            if (!urlSuffix.startsWith('/')) {
                url += '/';
            }
            url += urlSuffix;
        }
        if (this.DEBUG_MODE == true) {
            url = this.addParameterToURL(url, 'debug', 'true');
        }
        return url;
    },

    restUrl: function(urlSuffix, useBaseUrl) {
        var url;
        if (urlSuffix.startsWith('http://')) {
            url = urlSuffix;
        } else {
            url = url = (useBaseUrl ? this.BASE_URL : this.OPF_CONSOLE_URL) + '/rest';
            if (!urlSuffix.startsWith('/')) {
                url += '/';
            }
            url += urlSuffix;
        }




        return url;
    },

    getBaseUrl: function() {
        return OPF.ifBlank(this.BASE_URL, this.OPF_CONSOLE_URL);
    },

    randomUUID: function () {
        var s = [], itoh = '0123456789ABCDEF', i;
        for (i = 0; i < 36; i++) s[i] = Math.floor(Math.random() * 0x10);
        s[14] = 4;
        s[19] = (s[19] & 0x3) | 0x8;
        for (i = 0; i < 36; i++) s[i] = itoh[s[i]];
        s[8] = s[13] = s[18] = s[23] = '-';
        return s.join('');
    },

    noCacheLoadScriptInit: function() {
        if (this.CACHE_VERSION != undefined && this.CACHE_VERSION != null && this.CACHE_VERSION.length > 0) {
            Ext.Loader.loadScriptFile = function(url, onLoad, onError, scope, synchronous) {
                var me = this,
                    noCacheUrl,
                    fileName = url.split('/').pop(),
                    isCrossOriginRestricted = false,
                    xhr, status, onScriptError;

                if (this.getConfig('disableCaching')) {
                    noCacheUrl = url + '?' + this.getConfig('disableCachingParam') + '=' + Ext.Date.now() + '&v=' + OPF.Cfg.CACHE_VERSION;
                } else {
                    noCacheUrl = url + '?v=' + OPF.Cfg.CACHE_VERSION;
                }

                scope = scope || this;

                this.isLoading = true;

                if (!synchronous) {
                    onScriptError = function() {
                        onError.call(scope, "Failed loading '" + url + "', please verify that the file exists", synchronous);
                    };

                    if (!Ext.isReady && Ext.onDocumentReady) {
                        Ext.onDocumentReady(function() {
                            me.injectScriptElement(noCacheUrl, onLoad, onScriptError, scope);
                        });
                    }
                    else {
                        this.injectScriptElement(noCacheUrl, onLoad, onScriptError, scope);
                    }
                }
                else {
                    if (typeof XMLHttpRequest !== 'undefined') {
                        xhr = new XMLHttpRequest();
                    } else {
                        xhr = new ActiveXObject('Microsoft.XMLHTTP');
                    }

                    try {
                        xhr.open('GET', noCacheUrl, false);
                        xhr.send(null);
                    } catch (e) {
                        isCrossOriginRestricted = true;
                    }

                    status = (xhr.status === 1223) ? 204 : xhr.status;

                    if (!isCrossOriginRestricted) {
                        isCrossOriginRestricted = (status === 0);
                    }

                    if (isCrossOriginRestricted
                        ) {
                        onError.call(this, "Failed loading synchronously via XHR: '" + url + "'; It's likely that the file is either " +
                            "being loaded from a different domain or from the local file system whereby cross origin " +
                            "requests are not allowed due to security reasons. Use asynchronous loading with " +
                            "Ext.require instead.", synchronous);
                    }
                    else if (status >= 200 && status < 300
                        ) {

                        new Function(xhr.responseText + "\n//@ sourceURL=" + fileName)();

                        onLoad.call(scope);
                    }
                    else {
                        onError.call(this, "Failed loading synchronously via XHR: '" + url + "'; please " +
                            "verify that the file exists. " +
                            "XHR status code: " + status, synchronous);
                    }


                    xhr = null;
                }

            };
        }
    },

    addParameterToURL: function(url, key, value) {
        var param = key + '=' + encodeURIComponent(value);

        var sep = '&';
        if (url.indexOf('?') < 0) {
            sep = '?';
        } else {
            var lastChar = url.slice(-1);
            if (lastChar == '&') sep = '';
            if (lastChar == '?') sep = '';
        }
        url += sep + param;

        return url;
    }

});

OPF.Cfg = new OPF.core.utils.Config();
OPF.Cfg.PAGE_UID = OPF.Cfg.randomUUID();



Ext.define('OPF.console.directory.model.UserModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: OPF.Cfg.restUrl('directory/user', false),
        editorClassName: 'OPF.console.directory.editor.UserEditor',
        constraintName: 'OPF.directory.User'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        {
            name: 'username',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'password', type: 'string' },
        { name: 'passwordConfirm', type: 'string' },
        {
            name: 'email',
            type: 'string',
            fieldType: 'NAME'
        },
        {
            name: 'firstName',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'middleName', type: 'string' },
        {
            name: 'lastName',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'guest', type: 'boolean' },
        { name: 'registryNodeId', type: 'int' },
        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },
        { name: 'roles', persist: false }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.console.authority.model.Role',
            name: 'userRoles',
            associatedName: 'userRoles',
            foreignKey: 'id'
        }
    ]

});




Ext.define('OPF.console.directory.model.UserProfileFieldGroup', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/user_profile_field_group',

        constraintName: 'OPF.directory.UserProfileFieldGroup'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});




Ext.define('OPF.console.directory.model.UserProfileField', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/user_profile_field',

        constraintName: 'OPF.directory.UserProfileField'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'userProfileFieldGroupId', type: 'int' },
        { name: 'fieldType', type: 'string' },
        { name: 'fieldTypeName', type: 'string', persist: false },

        { name: 'created', type: 'int', persist: false },
        { name: 'canUpdate', type: 'boolean', persist: false },
        { name: 'canDelete', type: 'boolean', persist: false }
    ]

});




Ext.define('OPF.console.directory.model.UserProfileFieldTree', {
    extend:  Ext.data.Model ,

    idProperty: 'nodeId',

    fields: [
        { name: 'id', type: 'string', mapping: 'nodeId', persist: false },
        { name: 'realId', type: 'int', mapping: 'id', persist: false },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'type', type: 'string'},

        { name: 'parentId', type: 'int' },

        { name: 'userProfileFieldGroupId', type: 'int' },
        { name: 'fieldType', type: 'string' },
        { name: 'fieldTypeName', type: 'string', persist: false },

        { name: 'created', type: 'int', persist: false },
        { name: 'canUpdate', type: 'boolean', persist: false },
        { name: 'canDelete', type: 'boolean', persist: false },


        {
            name: 'text',
            type: 'string',
            convert: function(text, record) {
                return ifBlank(text, record.get('name'));
            }
        },
        { name: 'icon', type: 'string'},
        { name: 'cls', type: 'string'},
        {
            name: 'iconCls',
            type: 'string',
            convert: function(iconCls, record) {
                return ifBlank(iconCls, 'tricon-' + record.get('type').toLowerCase());
            }
        },
        { name: 'leaf', type: 'boolean'},
        { name: 'allowDrag', type: 'boolean'},
        { name: 'allowDrop', type: 'boolean'},
        { name: 'expanded', type: 'boolean'}
    ]

});




Ext.define('OPF.console.domain.model.ActionModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/action',
        editorClassName: 'OPF.console.domain.view.action.ActionEditor',
        constraintName: 'OPF.registry.Action'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'serverName', type: 'string' },
        { name: 'port', type: 'int', useNull: true },
        { name: 'urlPath', type: 'string' },
        { name: 'protocol', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'status', type: 'string' },
        { name: 'soapUrlPath', type: 'string' },
        { name: 'soapMethod', type: 'string' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.ActionParameterModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/action',
        constraintName: 'OPF.registry.ActionParameter'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'location', type: 'string' },
        { name: 'orderPosition', type: 'int' },
        { name: 'fieldType', type: 'string' },
        { name: 'typeName', type: 'string', useNull: true },
        { name: 'description', type: 'string' }
    ]

});


Ext.define('OPF.console.domain.model.ActivityActionModel', {
    extend:  Ext.data.Model ,

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        {
            name: 'name',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'description', type: 'string' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ActivityModel',
            name: 'activityFrom',
            associatedName: 'activityFrom',
            associatedKey: 'activityFrom',
            foreignKey: 'id',
            displayName: 'Activity From',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ActivityModel',
            name: 'activityTo',
            associatedName: 'activityTo',
            associatedKey: 'activityTo',
            foreignKey: 'id',
            displayName: 'Activity To',
            displayDescription: ''
        }
    ]

});


Ext.define('OPF.console.domain.model.ActivityModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/activity',
        constraintName: 'OPF.process.Activity'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        {
            name: 'name',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'sortPosition', type: 'int' },
        { name: 'activityType', type: 'string' },
        { name: 'actorId', type: 'int' },
        { name: 'actorName', mapping: 'string' },
        { name: 'statusId', mapping: 'int' },
        { name: 'statusName', mapping: 'string' },
        { name: 'notify', type: 'boolean' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.ActivityActionModel',
            name: 'activityActions',
            associatedName: 'activityActions',
            associationKey: 'activityActions',
            foreignKey: 'id',
            displayName: 'Activity Actions',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ActorModel',
            name: 'actor',
            associatedName: 'actor',
            associationKey: 'actor',
            foreignKey: 'id',
            displayName: 'Actor',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.StatusModel',
            name: 'status',
            associatedName: 'status',
            associationKey: 'status',
            foreignKey: 'id',
            displayName: 'Status',
            displayDescription: ''
        }
    ]

});


Ext.define('OPF.console.domain.model.ActorModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/actor',
        editorClassName: 'OPF.console.domain.view.process.ActorEditor',
        constraintName: 'OPF.process.Actor'
    },

    idProperty: 'id',
    displayProperty: 'name',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },
        { name: 'userActors', useNull: true },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.AssetFileModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/package'
    },

    fields: [
        { name: 'filename', type: 'string'},
        { name: 'type', type: 'string' },
        { name: 'updated', type: 'int' }
    ]

});


Ext.define('OPF.console.domain.model.BIReportFieldModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/bireport',
        constraintName: 'OPF.registry.BIReportField'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'displayName', type: 'string' },
        { name: 'count', type: 'int' },
        { name: 'enabled', type: 'boolean' },
        { name: 'type', type: 'string' },
        { name: 'parameters' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.BIReportFieldModel',
            name: 'children',
            associatedName: 'children',
            associationKey: 'children',
            foreignKey: 'id'
        },

        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportModel',
            name: 'report',
            associatedName: 'report',
            associatedKey: 'report',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.EntityModel',
            name: 'entity',
            associatedName: 'entity',
            associatedKey: 'entity',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'field',
            associatedName: 'field',
            associatedKey: 'field',
            foreignKey: 'id'
        }
    ]

});


Ext.define('OPF.console.domain.model.BIReportFieldTreeModel', {
    extend:  Ext.data.Model ,


    fields: [
        { name: 'id', type: 'int' },
        { name: 'entityId', type: 'int' },
        { name: 'title', type: 'string'},
        { name: 'type', type: 'string'},
        { name: 'expanded', type: 'boolean'},
        { name: 'alwaysExpanded', type: 'boolean'},
        { name: 'enabled', type: 'boolean'},
        { name: 'order_position', type: 'int' },
        { name: 'leaf', type: 'boolean'},
        { name: 'children', useNull: true}
    ]

});


Ext.define('OPF.console.domain.model.BIReportModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/bi/report',
        editorClassName: 'OPF.console.domain.view.BIReportEditor',
        constraintName: 'OPF.registry.BIReport'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'title', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },
        { name: 'securityEnabled', type: 'boolean' },
        { name: 'fields', type: 'auto' }
    ]











});
//@tag opf-model



Ext.define('OPF.console.domain.model.BIReportUserFieldModel', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'expanded', type: 'boolean' },
        { name: 'order', type: 'int' },
        { name: 'created', type: 'int' },
        { name: 'location', type: 'string' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportUserModel',
            name: 'userReport',
            associatedName: 'userReport', associationKey: 'userReport',
            foreignKey: 'id',
            displayName: 'User Report',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportFieldModel',
            name: 'field',
            associatedName: 'field', associationKey: 'field',
            foreignKey: 'id',
            displayName: 'Field',
            displayDescription: ''
        }
    ]

});
//@tag opf-model



Ext.define('OPF.console.domain.model.BIReportUserFilterModel', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'created', type: 'int' },
        { name: 'values', type: 'auto' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportUserModel',
            name: 'userReport',
            associatedName: 'userReport', associationKey: 'userReport',
            foreignKey: 'id',
            displayName: 'User Report',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportFieldModel',
            name: 'field',
            associatedName: 'field', associationKey: 'field',
            foreignKey: 'id',
            displayName: 'Field',
            displayDescription: ''
        }
    ]

});
//@tag opf-model



Ext.define('OPF.console.domain.model.BIReportUserModel', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'title', type: 'string' },
        { name: 'filter', type: 'string' },
        { name: 'fields', type: 'auto' },
        { name: 'created', type: 'int' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'user',
            associatedName: 'user', associationKey: 'user',
            foreignKey: 'id',
            displayName: 'User',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportModel',
            name: 'report',
            associatedName: 'report', associationKey: 'report',
            foreignKey: 'id',
            displayName: 'Report',
            displayDescription: ''
        }
    ]

});


Ext.define('OPF.console.domain.model.CaseExplanationModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/explanation',
        constraintName: 'OPF.process.CaseExplanation'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'shortDescription', type: 'string' },
        { name: 'longDescription', type: 'string' }
    ]

});


Ext.define('OPF.console.domain.model.CaseObjectModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/case-object',
        constraintName: 'OPF.process.CaseObject'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'entityId', type: 'int' },
        { name: 'entityType', type: 'string' },
        {
            name: 'updateDate',
            type: 'string',
            convert: OPF.convertDate,
            useNull: false,
            persist: true,
            dateFormat: 'time'
        }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.inbox.model.TaskModel',
            name: 'task',
            associatedName: 'task',
            associationKey: 'task',
            foreignKey: 'id',
            displayName: 'Task',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.inbox.model.CaseModel',
            name: 'processCase',
            associatedName: 'processCase',
            associationKey: 'processCase',
            foreignKey: 'id',
            displayName: 'Case',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'createdBy',
            associatedName: 'createdBy',
            associationKey: 'createdBy',
            foreignKey: 'id',
            displayName: 'Created By',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'updatedBy',
            associatedName: 'updatedBy',
            associationKey: 'updatedBy',
            foreignKey: 'id',
            displayName: 'Updated By',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.StatusModel',
            name: 'status',
            associatedName: 'status',
            associationKey: 'status',
            foreignKey: 'id',
            displayName: 'Status',
            displayDescription: ''
        }
    ]

});


Ext.define('OPF.console.domain.model.Database', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/database',
        editorClassName: 'OPF.console.domain.view.system.DatabaseEditor',
        constraintName: 'OPF.registry.Database'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'serverName', type: 'string' },
        { name: 'port', type: 'int' },
        { name: 'urlPath', type: 'string' },
        { name: 'protocol', type: 'string' },
        { name: 'username', type: 'string' },
        { name: 'password', type: 'string' },
        { name: 'status', type: 'string' },
        { name: 'rdbms', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.DomainModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/domain',
        editorClassName: 'OPF.console.domain.view.DomainEditor',
        constraintName: 'OPF.registry.Domain'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.EntityModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/entity',
        editorClassName: 'OPF.console.domain.view.EntityEditor',
        constraintName: 'OPF.registry.Entity'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'typeEntity', type: 'string', useNull: true, defaultValue: null },

        { name: 'parentId', type: 'int' },
        { name: 'fields'},
        { name: 'extendedEntity', type: 'auto', useNull: true, defaultValue: null },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },
        { name: 'securityEnabled', type: 'boolean' }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'fieldModels',
            associatedName: 'fieldModels',
            associationKey: 'fieldModels',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ReferenceObjectModel',
            name: 'referenceObject',
            associatedName: 'referenceObject',
            associationKey: 'referenceObject',
            foreignKey: 'id'
        }
    ]

});
//@tag opf-model



Ext.define('OPF.console.domain.model.FieldModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/package',
        constraintName: 'OPF.registry.Field',

        fieldTypeIsString: function(value) {
            return 'UNIQUE_ID' == value || 'CODE' == value || 'LABEL' == value ||
                'LOOKUP' == value || 'NAME' == value || 'DESCRIPTION' == value ||
                'PASSWORD' == value || 'SECRET_KEY' == value || 'TINY_TEXT' == value ||
                'SHORT_TEXT' == value || 'MEDIUM_TEXT' == value || 'LONG_TEXT' == value ||
                'UNLIMITED_TEXT' == value || 'RICH_TEXT' == value || 'URL' == value ||
                'SSN' == value || 'PHONE_NUMBER' == value ||
                'FILE' == value || 'IMAGE_FILE' == value || 'AUDIO_FILE' == value ||
                'VIDEO_FILE' == value;
        },

        fieldTypeIsNumeric: function(value) {
            return 'NUMERIC_ID' == value || 'LARGE_NUMBER' == value ||
                'INTEGER_NUMBER' == value || 'CURRENCY' == value;
        },

        fieldTypeIsReal: function(value) {
            return 'DECIMAL_NUMBER' == value || 'CURRENCY' == value;
        },

        fieldTypeIsDate: function(value) {
            return 'DATE' == value || 'TIME' == value || 'EVENT_TIME' == value ||
                'CREATION_TIME' == value || 'UPDATE_TIME' == value;
        },

        fieldTypeIsBoolean: function(value) {
            return 'FLAG' == value;
        },

        checkType: function(value, type) {
            var validValue = null;
            if (OPF.isNotBlank(value)) {
                if (OPF.console.domain.model.FieldModel.fieldTypeIsNumeric(type)) {
                    validValue = Ext.isNumeric(value) ? parseInt(value) : null;
                } else if (OPF.console.domain.model.FieldModel.fieldTypeIsReal(type)) {
                    validValue = isFinite(value) ? value : null;
                } else if (OPF.console.domain.model.FieldModel.fieldTypeIsDate(type)) {
                    validValue = Ext.isDate(value) ? Ext.Date.parse(value) : null;
                } else if (OPF.console.domain.model.FieldModel.fieldTypeIsBoolean(type)) {
                    value = value.toLowerCase();
                    validValue = value == 'true';
                } else {
                    validValue = value;
                }
            }
            return validValue;
        }
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'inherited', type: 'boolean' },
        { name: 'autoGenerated', type: 'boolean' },
        { name: 'name', type: 'string' },
        { name: 'displayName', type: 'string' },
        { name: 'displayDescription', type: 'string' },
        { name: 'fieldType', type: 'string' },
        { name: 'fieldTypeName', type: 'string' },
        { name: 'customFieldType', type: 'string' },
        { name: 'required', type: 'boolean' },
        { name: 'searchable', type: 'boolean' },
        { name: 'defaultValue', type: 'string', useNull: true },
        { name: 'description', type: 'string', useNull: true },
        { name: 'allowedValues', useNull: true },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean'},
        { name: 'canDelete', type: 'boolean'}
    ],

    associations: [
        { type: 'belongsTo', model: 'OPF.console.directory.model.Directory', primaryKey: 'id', foreignKey: 'parentId' }
    ]

});


Ext.define('OPF.console.domain.model.FilestoreModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/filestore',
        editorClassName: 'OPF.console.domain.view.system.FilestoreEditor',
        constraintName: 'OPF.registry.Filestore'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'serverName', type: 'string' },
        { name: 'port', type: 'int', defaultValue: 8080 },
        { name: 'urlPath', type: 'string' },
        { name: 'status', type: 'string' },
        { name: 'serverDirectory', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.Package', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/package',
        editorClassName: 'OPF.console.domain.view.PackageEditor',
        constraintName: 'OPF.registry.Package'
    },

    idProperty: 'id',

    fields: [
        { name: 'databaseVersion', type: 'int'},
        { name: 'deployed', type: 'boolean'},
        { name: 'version', type: 'int'},
        { name: 'prefix', type: 'string'},
        { name: 'urlPath', type: 'string'},

        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.PackageVersionModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/package'
    },

    fields: [
        { name: 'versionName', type: 'string'},
        { name: 'version', type: 'int' },
        { name: 'current', type: 'boolean' },
        { name: 'updated', type: 'int', useNull: true }
    ]

});


Ext.define('OPF.console.domain.model.ProcessFieldModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/custom-fields',
        constraintName: 'OPF.process.ProcessField'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'valueType', type: 'string' },
        { name: 'global', type: 'boolean' },
        { name: 'fieldId', type: 'int' },
        { name: 'format', type: 'string'},
        { name: 'registryNodeTypeId', type: 'int'},
        { name: 'orderPosition', type: 'int' }
    ]

});


Ext.define('OPF.console.domain.model.ProcessModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process',
        editorClassName: 'OPF.console.domain.view.process.ProcessEditor',
        constraintName: 'OPF.process.Process'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        {
            name: 'name',
            type: 'string',
            fieldType: 'NAME'
        },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'supportMultiActivities', type: 'boolean' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.ReferenceObjectModel', {
    extend:  Ext.data.Model ,

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'heading', type: 'string' },
        { name: 'subHeading', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]
});


Ext.define('OPF.console.domain.model.RegistryNodeModel', {
    extend:  Ext.data.Model ,

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.RelationshipModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/relationship',
        editorClassName: 'OPF.console.domain.view.RelationshipEditor',
        constraintName: 'OPF.registry.Relationship'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'type', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'relationshipType', type: 'string' },
        { name: 'relationshipTypeName', type: 'string' },
        { name: 'sourceEntityId', type: 'int' },
        { name: 'sourceEntityName', type: 'string' },
        { name: 'targetEntityId', type: 'int', useNull: true },
        { name: 'targetEntityName', type: 'string', useNull: true },
        { name: 'required', type: 'boolean' },


        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.EntityModel',
            name: 'targetEntity',
            associatedName: 'targetEntity',
            associationKey: 'targetEntity',
            foreignKey: 'id'
        }
    ]
});


Ext.define('OPF.console.domain.model.ReportFieldModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/report',
        constraintName: 'OPF.registry.ReportField'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'displayName', type: 'string' },


        { name: 'visible', type: 'boolean' },
        { name: 'searchable', type: 'boolean' },

        { name: 'relationships', type: 'auto' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ReportModel',
            name: 'report',
            associatedName: 'report',
            associatedKey: 'report',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'field',
            associatedName: 'field',
            associatedKey: 'field',
            foreignKey: 'id'
        },
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.RelationshipModel',
            name: 'relationships',
            associatedName: 'relationships',
            associatedKey: 'relationships',
            foreignKey: 'id'
        }
    ]

});


Ext.define('OPF.console.domain.model.ReportModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/report',
        editorClassName: 'OPF.console.domain.view.ReportEditor',
        constraintName: 'OPF.registry.Report'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },
        { name: 'securityEnabled', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.RootDomain', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/root_domain',
        editorClassName: 'OPF.console.domain.view.RootDomainEditor',
        constraintName: 'OPF.registry.RootDomain'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int', useNull: true },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.Server', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/server',
        editorClassName: 'OPF.console.domain.view.system.ServerEditor',
        constraintName: 'OPF.registry.Server'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'serverName', type: 'string' },
        { name: 'port', type: 'int', defaultValue: 8080 },
        { name: 'protocol', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'status', type: 'string' },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.StatusModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/status',
        constraintName: 'OPF.process.Status'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'sortPosition', type: 'int' },
        { name: 'created', type: 'int' }
    ]

});


Ext.define('OPF.console.domain.model.SystemModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/system',
        editorClassName: 'OPF.console.domain.view.system.SystemEditor',
        constraintName: 'OPF.registry.System'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'serverName', type: 'string' },
        { name: 'port', type: 'int', defaultValue: 8080 },

        { name: 'parentId', type: 'int' },
        { name: 'childCount', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.domain.model.UserActorModel', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int'},
        { name: 'username', type: 'string' },
        { name: 'userActorId', type: 'int' }
    ]

});


Ext.define('OPF.console.domain.model.WizardModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/wizard',
        editorClassName: 'OPF.console.domain.view.WizardEditor',
        constraintName: 'OPF.registry.Wizard'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.inbox.model.CaseActionModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/action/case'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'type', type: 'string' },
        { name : 'user' },
        { name: 'userName', type: 'string', useNull: true,
            convert: function(userName, model) {
                var user = model.get('user');
                return OPF.isEmpty(user) ? null : user.username
            }
        },
        { name : 'caseNote'},
        { name: 'noteId', type: 'int', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.id
            }
        },
        { name: 'note', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.text
            }
        },
        { name: 'caseExplanation' },
        { name: 'explanation', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var explanation = model.get('caseExplanation');
                return OPF.isEmpty(explanation) ? null : explanation.shortDescription
            }
        },
        { name: 'processCase', type: 'auto' },
        { name: 'assignee', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var processCase = model.get('processCase');
                return OPF.isEmpty(processCase) || OPF.isEmpty(processCase.assignee) ? '' : processCase.assignee.username
            }
        },
        { name: 'description', type: 'string' },
        { name: 'performedOn', type: 'date', dateFormat: 'time' }, 

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.inbox.model.CaseAttachmentModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/attachment',
        constraintName: 'OPF.process.CaseAttachment'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'filename', type: 'string' },
        { name: 'name', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'processCaseId', type: 'int'},

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


var staticFields = [
    { name: 'id', type: 'int', useNull: true },
    { name: 'description', type: 'string', flex: 1 },
    { name: 'process' },
    { name: 'status' },
    { name: 'assignee' },
    { name: 'processName', type: 'string',
        convert: function(action, model) {
            return model.get('process').name;
        }
    },
    { name: 'assigneeName', type: 'string',
        convert: function(action, model) {
            return model.get('assignee').username;
        }
    },
    { name: 'statusName', type: 'string',
        convert: function(action, model) {
            return model.get('status').name;
        }
    },
    { name: 'data', type: 'string' },
    { name: 'active', type: 'boolean', useNull: true },
    { name: 'startDate', type: 'date', dateFormat: 'time' },
    { name: 'updateDate', type: 'date', dateFormat: 'time' },
    { name: 'completeDate', type: 'date', dateFormat: 'time' },
    { name: 'hasPreviousTask', type: 'boolean' },
    { name: 'userCanPerform', type: 'boolean' },

    { name: 'created', type: 'date', dateFormat: 'time' },
    { name: 'canUpdate', type: 'boolean' },
    { name: 'canDelete', type: 'boolean' }
];

Ext.define('OPF.console.inbox.model.CaseModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/case'
    },
    idProperty: 'id',
    fields: staticFields
});

OPF.console.inbox.model.CaseModel.configuredFields = staticFields;


Ext.define('OPF.console.inbox.model.CaseNoteModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/note/',
        constraintName: 'OPF.process.CaseNote'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'text', type: 'string' },
        { name: 'processCase' },
        { name: 'task' },
        { name: 'user' },
        { name: 'taskId', type: 'int'},
        { name: 'processCaseId', type: 'int'},
        { name: 'note', type: 'string'},
        { name: 'userId', type: 'int'},
        { name: 'userName', type: 'string'},

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


var staticFields = [
    {
        name: 'id',
        type: 'auto',
        useNull: true
    },
    {
        name: 'description',
        type: 'string',
        flex: 1
    },
    {
        name: 'processCase'
    },
    {
        name: 'activity'
    },
    {
        name: 'assignee'
    },
    {
        name: 'processName',
        type: 'string',
        convert: function(action, model) {
            var processCase = model.get('processCase');
            return processCase && processCase.process ? processCase.process.name : null;
        }
    },
    {
        name: 'activityName',
        type: 'string',
        convert: function(action, model) {
            var activity = model.get('activity');
            return activity ? activity.name : null;
        }
    },
    {
        name: 'assigneeName',
        type: 'string',
        convert: function(action, model) {
            var assignee = model.get('assignee');
            return assignee ? assignee.username : null;
        }
    },
    {
        name: 'statusName',
        type: 'string',
        convert: function(action, model) {
            var activity = model.get('activity');
            return activity && activity.status ? activity.status.name : null;
        }
    },
    {
        name: 'userCanPerform',
        type: 'boolean',
        fieldType: 'FLAG'
    },
    {
        name: 'active',
        type: 'boolean',
        fieldType: 'FLAG'
    },
    {
        name: 'updateDate',
        type: 'date',
        dateFormat: 'time',
        fieldType: 'DATE'
    },
    {
        name: 'closeDate',
        type: 'date',
        dateFormat: 'time',
        fieldType: 'DATE'
    },

    {
        name: 'created',
        type: 'date',
        dateFormat: 'time'
    },

    { name: 'canUpdate', type: 'boolean', persist: false },
    { name: 'canDelete', type: 'boolean', persist: false }
];

Ext.define('OPF.console.inbox.model.TaskModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/task'
    },
    idProperty: 'id',
    fields: staticFields,

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ProcessModel',
            name: 'processCase',
            associatedName: 'processCase',

            foreignKey: 'id',
            displayName: 'Process Case',
            displayDescription: '',
            searchMode: 'FIELDS'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ActivityModel',
            name: 'activity',
            associatedName: 'activity',

            foreignKey: 'id',
            displayName: 'Activity',
            displayDescription: '',
            searchMode: 'FIELDS'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'assignee',
            associatedName: 'assignee',

            foreignKey: 'id',
            displayName: 'Assignee',
            displayDescription: '',
            searchMode: 'FIELDS'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.CaseObjectModel',
            name: 'caseObject',
            associatedName: 'caseObject',
            associationKey: 'caseObject',
            foreignKey: 'id',
            displayName: 'Case Object',
            displayDescription: '',
            searchMode: []
        }
    ]
});

OPF.console.inbox.model.TaskModel.configuredFields = staticFields;


Ext.define('OPF.console.inbox.model.UserActorModel', {
    extend:  Ext.data.Model ,

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'user', useNull: true },
        { name: 'actor', useNull: true },
        { name: 'process', useNull: true },
        { name: 'processCase', useNull: true },

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});


Ext.define('OPF.console.site.model.NavigationElement', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/site',
        restSuffixUrl: 'site/navigation',
        editorClassName: 'OPF.console.site.editor.NavigationElementEditor',
        constraintName: 'OPF.site.NavigationElement'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'lookup', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name:'serverName', type: 'string'},
        { name:'port', type: 'string'},
        { name:'parentPath', type: 'string'},
        { name:'urlPath', type: 'string'},
        { name:'protocol', type: 'string'},
        { name:'method', type: 'string'},
        { name:'status', type: 'string'},
        { name:'sortPosition', type: 'string'},
        { name:'elementType', type: 'string'},

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },

        { name: 'navigationElements', type: 'auto' },

        { name: 'permissions', type: 'auto'},

        { name: 'textResourceVersion', type: 'auto'},

        { name: 'imageResourceVersion', type: 'auto'}
    ],

    hasMany: [
        { model: 'NavigationElement', name: 'navigationElements', foreignKey: 'parent_id' }

    ]

});



Ext.define('OPF.core.component.ErrorContainer', {
    extend:  Ext.container.Container ,
    alias: 'widget.error-container',

    cls: 'error-container',
    hidden: true,
    form: null,
    delay: null,

    activeErrorContainerTpl: [
        '<tpl if="errors && errors.length">',
            '<h4><img src="{errorImageUrl}" border="0"/>Error Message!</h4>',
            '<ul><tpl for="errors"><li class="{level.css} <tpl if="xindex == xcount"> last</tpl>">{msg}</li></tpl></ul>',
        '</tpl>'
    ],

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.delay)) {
            var task = new Ext.util.DelayedTask(function() {
                me.initErrorContainer();
            }, this);
            task.delay(this.delay);
        } else {
            this.initErrorContainer();
        }

        this.items = this.items || [];

        this.callParent(arguments);
    },

    initErrorContainer: function() {
        var me = this;

        var fields = this.form.getForm().getFields();
        Ext.each(fields.items, function(field) {

            field.on('errorchange', me.showErrors, me);
        });
    },

    showError: function(level, msg) {
        var errors = [
            {
                level: level,
                msg: msg
            }
        ];
        this.setActiveErrorContainer(errors);
    },

    showErrors: function() {
        var me = this;
        if (OPF.isNotEmpty(this.task)) {
            this.task.cancel();

        }
        this.task = new Ext.util.DelayedTask(function() {
            me.showLastErrors();
        }, this);
        this.task.delay(10);
    },

    showLastErrors: function() {
        var errors = [];
        var fields = this.form.getForm().getFields();
        Ext.each(fields.items, function(field) {
            var activeErrors = field.getActiveErrors();
            Ext.each(activeErrors, function(activeError) {
                if (OPF.isNotBlank(activeError)) {
                    errors.push({
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: activeError
                    });
                }
            });
        });

        this.setActiveErrorContainer(errors);
    },

    setActiveErrorContainer: function(errors) {
        var allActiveError = this.getTpl('activeErrorContainerTpl').apply({
            errors: errors,
            errorImageUrl: OPF.Cfg.fullUrl('images/error.png')
        });
        this.update(allActiveError);
        this.setVisible(OPF.isNotBlank(allActiveError));
    },

    cleanActiveErrors: function() {
        this.update('');
        this.hide();
    }

});



Ext.define('OPF.core.component.ExportButton', {
    extend:  Ext.button.Split ,
    alias: 'widget.opf-export-button',

    buttonConfig: {},
    grid: null,
    strategies: [],

    initComponent: function() {
        var me = this;

        var menuItems = [];
        Ext.each(this.strategies, function(strategy, index) {
            menuItems.push({
                text: strategy.title,
                handler: function() {
                    me.exportData(strategy.strategy, strategy.filename);
                }
            });
            if (index == 0) {
                me.handler = function() {
                    me.exportData(strategy.strategy, strategy.filename);
                };
            }
        });

        this.menu = new Ext.menu.Menu({
            bodyCls: 'export',
            items: menuItems
        });

        this.callParent(arguments);
    },

    getQueryParams: function() {
        return null;
    },

    exportData: function(strategy, fileName) {
        var me = this;

        var fields = [];
        if (OPF.isNotEmpty(this.grid)) {
            Ext.each(this.grid.columns, function(column) {
                fields.push({
                    xtype: 'opf-hidden',
                    name: 'columns',
                    value: column.dataIndex
                });
            });
        }

        var urlQueryParams = '';
        var queryParams = this.getQueryParams();
        if (OPF.isNotEmpty(queryParams)) {
            var index = 0;
            for (name in queryParams) {
                if (index == 0) {
                    urlQueryParams += '?';
                }
                urlQueryParams += name + '=' + queryParams[name] + '&';
                index++;
            }
        }

        var exportForm = Ext.create('Ext.form.Panel', {
            url: OPF.Cfg.restUrl('site/export/' + strategy + '/' + fileName + urlQueryParams, true),
            monitorValid: true,
            standardSubmit: true, 
            items: fields
        });
        exportForm.getForm().submit();
    }

});


Ext.define('CMS.view.FileDownload', {
    extend:  Ext.Component ,
    alias: 'widget.opf-file-downloader',
    autoEl: {
        tag: 'iframe',
        cls: 'x-hidden',
        src: Ext.SSL_SECURE_URL
    },
    load: function(config){
        var e = this.getEl();
        e.dom.src = config.url +
            (config.params ? '?' + Ext.urlEncode(config.params) : '');
        e.dom.onload = function() {
            if(e.dom.contentDocument.body.childNodes[0].wholeText == '404') {
                Ext.Msg.show({
                    title: 'Attachment missing',
                    msg: 'The document you are after can not be found on the server.',
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.ERROR
                })
            }
        }
    }
});


Ext.define('OPF.core.component.FileTreePanel', {
    extend:  Ext.tree.Panel ,
    alias : 'widget.opf-filetreepanel',

    layout: 'fit',

    useArrows: true,
    autoScroll: true,
    animate: true,
    collapsible: false,
    split: true,
    rootVisible: false,

    selectedNode: null,

    managerLayout: null,

    fileServiceUrl: null,
    directoryOnly: false,

    constructor: function(managerLayout, directoryOnly, cfg) {
        cfg = cfg || {};
        OPF.core.component.FileTreePanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            directoryOnly: directoryOnly
        }, cfg));
    },

    
    initComponent: function() {
        var me = this;

        this.store = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.core.model.FileTreeModel',
            proxy: {
                type: 'ajax',
                url: me.fileServiceUrl,
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                text: 'root',
                expanded: true,
                path: ''
            },
            folderSort: true,
            sorters: this.SORTER,
            clearOnLoad: false,
            listeners: {
                beforeload: function(store, operation, eOpts) {
                    store.proxy.url = me.fileServiceUrl + '?path=' + operation.node.get('path') + "&directoryOnly=" + me.directoryOnly;
                }
            }
        });

        this.delayedClick = new Ext.util.DelayedTask(function(doubleClick, record) {
            if(doubleClick) {
                this.fireDblClick(record);
            } else {
                this.fireClick(record);
            }
        }, this),

        this.callParent(arguments);
    },

    listeners: {
        itemclick: function(tree, record) {
            this.delayedClick.delay(200, null, this, [false, record]);
        },
        itemdblclick: function(tree, record) {
            this.delayedClick.delay(200, null, this, [true, record]);
        },
        containerclick: function(tree) {
            tree.getSelectionModel().deselect(this.selectedNode);
            this.selectedNode = null;
            this.managerLayout.selectButton.disable();
        }
    },

    fireClick: function(record) {
        this.selectedNode = record;
        this.managerLayout.selectButton.enable();
    },

    fireDblClick: function(record) {
        this.managerLayout.chooseFile(record);
    }

});

Ext.define('OPF.core.component.FileManagerDialog', {
    extend:  Ext.window.Window ,
    alias : 'widget.opf-filemanager-dlg',

    id: 'fileManagerDialog',
    title: 'File Explorer',

    modal: true,
    closable: true,
    closeAction: 'hide',

    layout: 'fit',
    resizable: false,
    width: 300,
    height: 400,

    directoryOnly: null,
    selectFileField: null,

    constructor: function(directoryOnly, cfg) {
        cfg = cfg || {};
        OPF.core.component.FileManagerDialog.superclass.constructor.call(this, Ext.apply({
            directoryOnly: directoryOnly
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.fileTreePanel = Ext.create('OPF.core.component.FileTreePanel', this, this.directoryOnly, {
            fileServiceUrl: OPF.Cfg.restUrl('/registry/filemanager/directory')
        });

        this.selectButton = new Ext.Button({
            text: 'Select',
            disabled: true,
            handler: function() {
                instance.chooseFile(instance.fileTreePanel.selectedNode);
            }
        });

        this.closeButton = new Ext.Button({
            text: 'Cancel',
            handler: function() {
                instance.close();
            }
        });

        this.items = [
            this.fileTreePanel
        ];

        this.fbar = [
            this.selectButton,
            this.closeButton
        ];

        this.callParent(arguments);
    },

    chooseFile: function(node) {
        this.selectFileField.setValue(node.get('path'));
        this.close();
    },

    setSelectFileField: function(field) {
        this.selectFileField = field;
    }

});

OPF.core.component.FileManagerDialog.init = function(field, directoryOnly) {
    var fileManagerDialog = Ext.WindowMgr.get('fileManagerDialog');
    if (isEmpty(fileManagerDialog)) {
        fileManagerDialog = Ext.create('OPF.core.component.FileManagerDialog', directoryOnly);
        Ext.WindowMgr.register(fileManagerDialog);
    }
    fileManagerDialog.setSelectFileField(field);
    return fileManagerDialog;
};



Ext.define('OPF.core.component.HrefClick', {
    extend:  Ext.Component ,
    alias: 'widget.hrefclick',

    autoEl: 'a',
    onClick: Ext.emptyFn,

    initComponent: function() {
        this.addEvents(
            'click'
        );

        this.callParent(arguments);
    },

    onRender: function() {
        this.callParent(arguments);

        this.mon(this.el, 'click', this.onClick, this);
    }









});


Ext.define('OPF.core.component.ImageContainer', {
    extend:  Ext.container.Container ,
    alias: 'widget.opf-image',

    fieldLabel: null,
    subFieldLabel: null,

    layout: 'anchor',

    imageWidth: null,
    imageHeight: null,

    isDelete: false,
    imageWidthField: null,
    imageHeightField: null,
    resourceFileTemporaryName: null,
    resourceFileOriginalName: null,

    allowDelete: true,

    initComponent: function() {
        var me = this;

        this.resourceLookupField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: this.name,
            listeners: {
                errorchange: function(field, error) {
                    if (me.imageContainer.el) {
                        if (OPF.isNotBlank(error)) {
                            me.imageContainer.addCls('opf-image-invalid');
                            me.imageContainer.addCls('x-form-invalid-field');
                            me.imageContainer.el.dom.setAttribute('data-errorqtip', error || '');
                        } else {
                            me.imageContainer.removeCls('opf-image-invalid');
                            me.imageContainer.removeCls('x-form-invalid-field');
                            me.imageContainer.el.dom.removeAttribute('data-errorqtip');
                        }
                    }
                }
            }
        });

        this.imageContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            html: 'Loading...',
            cls: 'opf-image',
            width: this.imageWidth,
            height: this.imageHeight,
            listeners: {
                render: function(component) {
                    component.getEl().on('click', function() {
                        me.resourceLookupField.validate();
                    });
                }
            }
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            title: 'Upload new image',
            ui: 'wizards',
            width: 540,
            frame: false,
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),
            configs: {
                fileUploadFieldConfigs: {
                    padding: 10,
                    buttonText: '',
                    buttonConfig: {
                        ui: 'upload',
                        width: 60,
                        height: 60,
                        iconCls: 'upload-icon'
                    }
                },
                uploadButtonConfigs: {
                    ui: 'blue',
                    width: 250,
                    height: 60
                },

                cancelButtonConfigs: {
                    ui: 'blue',
                    width: 250,
                    height: 60
                }
            },

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                me.isDelete = false;
                me.resourceFileOriginalName = data.orgFilename;
                me.resourceFileTemporaryName = data.filename;
                me.resourceLookupField.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                me.imageWidthField = data.width;
                me.imageHeightField = data.height;
                me.loadImageContainer(url, data.width, data.height);
            }
        });

        this.uploadButton = new Ext.Button({
            text: 'Upload new image',
            handler: function () {
                me.uploadFileDialog.show();
            }
        });

        this.deleteButton = new Ext.Button({
            text: 'Delete',
            hidden: !this.allowDelete,
            handler: function () {
                me.onDelete();
            }
        });

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="x-form-item-label x-form-item-label-top">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>'
        });

        this.items = [
            this.label,
            this.resourceLookupField,
            this.imageContainer,
            {
                xtype: 'container',
                layout: 'hbox',
                items: [
                    this.uploadButton,
                    { xtype: 'tbspacer', width: 10 },
                    this.deleteButton
                ]
            }
        ];

        this.callParent(arguments);
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtmlWrapper(url, origWidth, origHeight, '', this.imageWidth - 4, this.imageHeight - 4);
        this.imageContainer.update(imageHtml);
    },

    getJsonData: function() {
        return {
            width: this.imageWidthField,
            height: this.imageHeightField,
            resourceFileTemporaryName: this.resourceFileTemporaryName,
            resourceFileOriginalName: this.resourceFileOriginalName
        };
    },

    onDelete: function() {
        this.imageContainer.update('Image not defined');
        this.resourceLookupField.setValue(null);
        this.isDelete = true;
    },

    clean: function() {
        this.isDelete = false;
        this.imageWidthField = null;
        this.imageHeightField = null;
        this.resourceFileTemporaryName = null;
        this.resourceFileOriginalName = null;
        this.imageContainer.update('Image not defined');
        this.idData.lookup = null;
        this.resourceLookupField.setValue(null);
        this.idData.version = null;
    },

    load: function(lookup) {
        var me = this;
        this.clean();
        if (OPF.isNotBlank(lookup)) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + lookup),
                method: 'GET',

                success: function(response){
                    var vo = Ext.decode(response.responseText);


                    if (vo.success) {
                        var resourceVersion = vo.data[0].resourceVersion;
                        me.showImage(resourceVersion);
                    }
                },
                failure: function(response) {
                    var data = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(false, data.message);
                }
            });
        }
    },

    process: function() {
        var lookup;
        if (this.isDelete) {
            lookup = this.remove();
        } else {
            lookup = this.save();
        }
        return lookup;
    },

    save: function() {
        var me = this;

        var jsonData = this.getJsonData();

        if (OPF.isBlank(jsonData.resourceFileTemporaryName)) {
            return this.idData.lookup;
        }

        var url;
        var method;
        if (this.idData.lookup) {
            jsonData.resourceLookup = this.idData.lookup;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl('content/resource/image/version/by-lookup/' + this.idData.lookup);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl('content/resource/image/version/new-by-path/' + this.idData.path);
            method = 'POST';
        }

        jsonData.culture = 'AMERICAN';

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},
            async: false,

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);


                if (vo.success) {
                    var resourceVersion = vo.data[0];
                    me.showImage(resourceVersion);
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while saving image.');
            }
        });

        return this.idData.lookup;
    },

    remove: function() {
        var me = this;
        if (this.idData.lookup) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.idData.lookup),
                method: 'DELETE',
                async: false,

                success: function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(vo.success, vo.message);
                    me.clean();
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, 'Error occurred while saving image.');
                }
            });
        }

        return null;
    },

    showImage: function(resourceVersion) {
        var urlSuffix = 'content/resource/image/by-filename/' + resourceVersion.resourceId + '/' + resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
        var url = OPF.Cfg.restUrl(urlSuffix);
        this.loadImageContainer(url, resourceVersion.width, resourceVersion.height);
        this.idData.lookup = resourceVersion.resourceLookup;
        this.resourceLookupField.setValue(resourceVersion.resourceLookup);
        this.idData.version = resourceVersion.version;
        this.imageWidthField = resourceVersion.width;
        this.imageHeightField = resourceVersion.height;
        this.resourceFileTemporaryName = null;
        this.resourceFileOriginalName = resourceVersion.resourceFileOriginalName;
    }

});



Ext.define('OPF.core.component.LabelContainer', {
    extend:  Ext.container.Container ,
    alias: 'widget.label-container',

    cls: 'fieldset-top-margin',
    labelCls: 'container-label-block',
    labelMargin: '5 0 15 0',

    fieldLabel: null,
    subFieldLabel: null,

    initComponent: function() {
        var instance = this;

        var layout = this.layout;
        this.layout = 'anchor';

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="' + this.labelCls + '">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>',
            margin: this.labelMargin
        });

        var items = this.items;

        this.items = [
            this.label,
            {
                xtype: 'fieldcontainer',
                layout: layout,
                items: items
            }
        ];

        this.callParent(arguments);
    },

    findById: function(id) {
        var me = this;
        var items = me.items.items.slice();
        var item = null;
        for (var i = 0; i < items.length; i++) {
            if (items[i].id == id || items[i].itemId == id) {
                item = items[i];
                break;
            }
        }
        return item;
    }

});



Ext.define('OPF.core.component.LinkButton', {
    extend:  Ext.Component ,
    alias: 'widget.linkbutton',

    autoEl: 'a',
    renderTpl: '<a href=\"javascript:;\" id="{id}-btnEl">{text}</a>',

    config: {
        text: '',
        handler: function () { }
    },

    initComponent: function () {
        var me = this;
        me.callParent(arguments);

        this.renderData = {
            text: this.getText()
        };
    },

    onRender: function(ct, position) {
        var me = this,
            btn;

        me.addChildEls('btnEl');

        me.callParent(arguments);

        btn = me.btnEl;

        me.mon(btn, 'click', me.onClick, me);
    },

    onClick: function(e) {
        var me = this;
        if (me.preventDefault || (me.disabled && me.getHref()) && e) {
            e.preventDefault();
        }
        if (e.button !== 0) {
            return;
        }
        if (!me.disabled) {
            me.fireHandler(e);
        }
    },

    fireHandler: function(e){
        var me = this,
            handler = me.handler;

        me.fireEvent('click', me, e);
        if (handler) {
            handler.call(me.scope || me, me, e);
        }
    }

});


Ext.define('OPF.core.component.MenuItem', {
    extend:  Ext.menu.Item ,
    alias: 'widget.opf-menuitem',

    renderTpl: [
        '<tpl if="plain">',
            '{text}',
        '<tpl else>',
            '<a id="{id}-itemEl" class="' + Ext.baseCSSPrefix + 'menu-item-link" href="{href}" <tpl if="hrefTarget">target="{hrefTarget}"</tpl> hidefocus="true" unselectable="on">',

                '<span id="{id}-textEl" class="' + Ext.baseCSSPrefix + 'menu-item-text header" <tpl if="arrowCls"></tpl> ><b>{text}</b></span><br/>',
                '<span id="{id}-descriptionEl" class="' + Ext.baseCSSPrefix + 'menu-item-text" <tpl if="arrowCls"></tpl> >{description}</span>',

            '</a>',
        '</tpl>'
    ],

    beforeRender: function() {
        var me = this;

        me.callParent();

        Ext.applyIf(me.renderData, {
            description: me.description
        });
    }

});





Ext.ns('OPF.core.component');


Ext.define('OPF.core.component.NavigationMenu', {
    extend:  Ext.toolbar.Toolbar ,
    cls: 'header',
    height: 39,

    alias : 'widget.navigation-menu',

    lookup: '',
    activeButtonLookup: null,
    navigationElements: null,

    
    initComponent: function() {

        this.addNavigationButtons();

        var searchField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            cls: 'search',
            emptyText: 'Search',
            enableKeyEvents: true,
            listeners: {
                keyup: function(field, event) {
                    if (event.keyCode == 13) {
                        document.location = OPF.Cfg.OPF_CONSOLE_URL + '/console/search?term=' + field.value;
                    }
                }
            }
        });

        if (OPF.Cfg.PAGE_TYPE == 'SEARCH' && isNotBlank(SEARCH_PHRASE)) {
            searchField.setValue(SEARCH_PHRASE);
        }

        var logoutButton = Ext.ComponentMgr.create({
            xtype:'button',
            ui: 'logout',
            text: '',
            tooltip: 'Logout',
            width: 12,
            height: 15,
            iconCls: 'logout-ico',
            handler: function() {
                document.location = OPF.Cfg.fullUrl('console/logout');
            }
        });

        this.items = [
            {
                xtype: 'component',
                html:
                    '<a class="logo" href="http://www.firejack.net" title="Firejack Platform">' +
                        '<span class="logo-img_w32"></span>' +
                    '</a>'
            },
            '->',
            searchField,
            {
                xtype: 'component',
                cls: 'user-name',
                width: 100,
                html: OPF.Cfg.USER_INFO.username
            },
            logoutButton
        ];


        this.callParent(arguments);
    },

    addNavigationButtons: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('site/navigation/tree-by-lookup/' + this.lookup),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var activeButton = null;
                var navigationElementButtons = [];
                me.navigationElements = Ext.decode(response.responseText).data;
                Ext.each(me.navigationElements, function(navigationElement, index) {
                    OPF.NavigationMenuRegister.add(navigationElement);
                    var navigationElements  = navigationElement.navigationElements;
                    var menuButtonXType = navigationElements.length > 0 ? 'splitbutton' : 'button';
                    var name = OPF.isNotEmpty(navigationElement.textResourceVersion) ? navigationElement.textResourceVersion.text : navigationElement.name;

                    var navigationElementButton = Ext.ComponentMgr.create({
                        xtype: menuButtonXType,
                        ui: 'main-menu',
                        margin: '0 0 0 0',
                        text: name,
                        width: me.buttonWidth,
                        height: me.buttonHeight,
                        scale: me.buttonScale,
                        iconAlign: me.buttonIconAlign,
                        iconCls: me.buttonIconCls,
                        enableToggle: true,
                        toggleGroup: 'cs-nav',
                        handler: function() {
                            document.location = me.generateUrl(navigationElement);
                        }
                    });
                    me.navigationElements.push(navigationElement);

                    me.addSubNavigationButtons(navigationElements, navigationElementButton);








                    navigationElementButtons.push(navigationElementButton);

                    if (navigationElement.lookup == me.activeButtonLookup) {
                        activeButton = navigationElementButton;
                        activeButton.pressed=true;
                    }
                });

                navigationElementButtons.push(
                    {
                        xtype: 'button',
                        text: 'Knowledge Center',
                        ui: 'main-menu',
                        scale: me.buttonScale,
                        href: 'http://documents.firejack.net',
                        target: '_blank'
                    }
                );

                me.add(1, navigationElementButtons);





            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    addSubNavigationButtons: function(navigationElements, navigationElementButton) {
        var instance = this;

        var menuButtons = [];
        Ext.each(navigationElements, function(navigationElement, index) {
            OPF.NavigationMenuRegister.add(navigationElement);
            var subNavigationElementButton = Ext.ComponentMgr.create({
                xtype: 'menuitem',
                text: navigationElement.name,
                handler: function() {
                    document.location = instance.generateUrl(navigationElement);
                }
            });
            menuButtons.push(subNavigationElementButton);

            instance.addSubNavigationButtons(navigationElement.navigationElements, subNavigationElementButton);
        });
        if (menuButtons.length > 0) {
            navigationElementButton.menu = Ext.menu.MenuMgr.get(menuButtons);
        }
    },

    generateUrl: function(nav) {
        var url = OPF.Cfg.getBaseUrl();
        if (OPF.isNotBlank(nav.serverName) &&
            OPF.isNotEmpty(nav.port) &&
            OPF.isNotBlank(nav.parentPath)) {
            url = 'http://' + nav.serverName + ':' + nav.port + nav.parentPath;
        }
        url += nav.urlPath;
        return url;
    }



});


Ext.define('OPF.core.component.PrintButton', {
    extend:  Ext.button.Button ,
    alias: 'widget.opf-print-button',

    buttonConfig: {},

    printCSS: null,
    printTitle: null,
    printData: null,
    printTpl: null,

    initComponent: function() {
        var me = this;

        this.handler = function() {
            me.print();
        };

        this.callParent(arguments);
    },



    print: function() {
        var me = this;

        if (OPF.isNotEmpty(this.printData) && OPF.isNotEmpty(this.printTpl)) {
            var html = this.printTpl.applyTemplate(this.printData);

            var myWindow = window.open('', '', 'width=600,height=800');
            myWindow.document.write('<html><head>');
            myWindow.document.write('<title>' + this.printTitle + '</title>');

            myWindow.document.write('</head><body>');
            myWindow.document.write(html);
            myWindow.document.write('</body></html>');
            myWindow.print();
        }
    }

});



Ext.define('OPF.core.component.SelectFileField', {
    extend:  OPF.core.component.TextField ,
    alias : 'widget.opf-selectfilefield',

    cls: 'x-form-field-filepath',

    
    buttonOnly: false,

    
    buttonMargin: 3,

    

    

    

    

    


    
    componentLayout: 'filefield',

    
    onRender: function() {
        var me = this,
            inputEl;

        me.callParent(arguments);

        me.createButton();

        
        
        if (me.disabled) {
            me.disableItems();
        }

        inputEl = me.inputEl;
        if (me.buttonOnly) {
            inputEl.setDisplayed(false);
        }
    },

    
    createButton: function() {
        var me = this;

        me.button = Ext.ComponentMgr.create({
            xtype: 'button',
            cls: 'x-form-select-file',
            style: me.buttonOnly ? '' : 'margin-left:' + me.buttonMargin + 'px',
            renderTo: me.bodyEl,
            buttonConfig: me.buttonConfig,
            handler: function() {
                me.onSelectFileClick();
            }
        });
    },

    
    onFileChange: function() {
        this.lastValue = null; 
        Ext.form.field.SelectFileField.superclass.setValue.call(this, this.fileInputEl.dom.value);
    },

    reset : function(){
        var me = this;
        if (me.rendered) {
            if (me.fileInputEl) {
                me.fileInputEl.remove();
                me.createFileInput();
            }
            me.inputEl.dom.value = '';
        }
        me.callParent();
    },

    onDisable: function(){
        this.callParent();
        this.disableItems();
    },

    disableItems: function(){
        var file = this.fileInputEl,
            button = this.button;

        if (file) {
            file.dom.disabled = true;
        }
        if (button) {
            button.disable();
        }
    },

    onEnable: function(){
        var me = this;
        me.callParent();
        me.fileInputEl.dom.disabled = false;
        me.button.enable();
    },

    isFileUpload: function() {
        return true;
    },

    extractFileInput: function() {
        var fileInput = this.fileInputEl.dom;
        this.reset();
        return fileInput;
    },

    onDestroy: function(){
        Ext.destroyMembers(this, 'fileInputEl', 'button');
        this.callParent();
    },

    
    onSelectFileClick: function() {
        var fileManagerDialog = OPF.core.component.FileManagerDialog.init(this, true);
        fileManagerDialog.show();
    },

    getErrors: function(value) {
        return getSQErrors(this, value)
    },

    getSubmitData: function() {
        var me = this, data = null, val;
        if (!me.disabled) {
            val = me.getSubmitValue();
            if (val !== null) {
                data = {};
                data[me.getName()] = val;
            }
        }
        return data;
    }

});






Ext.define('OPF.core.component.ToolTipOnClick', {
    extend:  Ext.tip.ToolTip ,
    alias: 'widget.tooltip-onclick',

    showDelay: 0,

    setTarget: function(target) {
        var me = this,
            t = Ext.get(target),
            tg;

        if (me.target) {
            tg = Ext.get(me.target);
            me.mun(tg, 'click', me.onTargetOver, me);
            me.mun(tg, 'mouseout', me.onTargetOut, me);
            me.mun(tg, 'mousemove', me.onMouseMove, me);
        }

        me.target = t;
        if (t) {

            me.mon(t, {
                freezeEvent: true,

                click: me.onTargetOver,
                mouseout: me.onTargetOut,
                mousemove: me.onMouseMove,
                scope: me
            });
        }
        if (me.anchor) {
            me.anchorTarget = me.target;
        }
    }
});



Ext.define('OPF.core.component.UploadFileDialog', {
    extend:  Ext.window.Window ,

    modal: true,
    closable: true,
    closeAction: 'hide',
    hideMode: 'display',
    resizable: false,
    width: 400,

    packageId: null,
    fileTypes: [],
    editPanel: null,

    additionalFields: [],

    fileLabelAlign: 'top',
    fileLabelWidth: 100,

    uploadUrl: null,

    configs: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        OPF.core.component.UploadFileDialog.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.uploadButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'Upload',
            formBind: true,
            handler: function() {
                instance.uploadFile();
            }
        }, this.configs.uploadButtonConfigs));

        this.cancelButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'Cancel',
            handler: function() {
                Ext.MessageBox.confirm("Cancel Action", "All changes will be lost, are you sure?",
                    function(btn) {
                        if ( btn[0] == 'y' ) {
                            instance.hide();
                        }
                    }
                );
            }
        }, this.configs.cancelButtonConfigs));

        this.fileUploadField = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'opf-file',
            labelAlign: this.fileLabelAlign,
            fieldLabel: 'File',
            labelWidth: this.fileLabelWidth,
            subFieldLabel: '',
            name: 'file',
            anchor: '100%',
            fileTypes: this.fileTypes
        }, this.configs.fileUploadFieldConfigs));

        var formFields = [];
        Ext.each(this.additionalFields, function(additionalField, index) {
            formFields.push(additionalField);
        });
        formFields.push(this.fileUploadField);

        this.form = Ext.create('Ext.form.Panel', {
            url: this.uploadUrl,
            layout: 'anchor',
            padding: 5,
            border: false,
            monitorValid: true,
            header: false,
            items: formFields,
            ui: 'upload-form-ui',
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        this.uploadButton,
                        this.cancelButton
                    ]
                }
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },


    listeners: {
        show: function(dialog) {

            dialog.form.getForm().checkValidity();
        }
    },

    uploadFile: function() {
        var me = this;

        this.form.getEl().mask();

        this.form.getForm().submit({
            method: "POST",

            success: function(fr, action) {
                var jsonData = Ext.JSON.decode(action.response.responseText);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, jsonData.message);

                me.form.getEl().unmask();

                me.successUploaded(jsonData, action);
                me.hide();
            },

            failure: function(fr, action) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Processed file on the server failure.');

                me.form.getEl().unmask();

                me.failureUploaded(action);
            }
        });
    },

    successUploaded: function(jsonData, action) {
    },

    failureUploaded: function(action) {
    }
});


Ext.define('OPF.core.component.calendar.CalendarEvent', {
    extend:  Ext.container.Container ,
    alias: 'widget.calendar-event',

    cls: 'cal-event',

    hourHeight: null,
    dateOffSet:null,
    dayColumn: null,
    calendarEventModel: null,
    scaleOfEvent: 1,
    indentOfEvent: 0,

    initComponent: function() {
        var me = this;

        this.startTime = this.calendarEventModel.get('startTime');
        this.endTime = this.calendarEventModel.get('endTime');
        this.height = ((this.endTime.getTime() - this.startTime.getTime()) / (60 * 60 * 1000)) * this.hourHeight;

        this.data = this.calendarEventModel.data;

        this.callParent(arguments);
    },

    doResize: function() {
        var startDayTime = this.dateOffSet;
        var top = ((this.startTime.getTime() - startDayTime.getTime()) / (60 * 60 * 1000)) * this.hourHeight;
        this.el.setTop(top);
        var width = this.dayColumn.getWidth() / this.scaleOfEvent;
        this.el.setLeft(width * this.indentOfEvent);
        this.setWidth(width);
    },

    getStartTime: function() {
        return this.calendarEventModel.get('startTime');
    },

    getEndTime: function() {
        return this.calendarEventModel.get('endTime');
    }

});


Ext.define('OPF.core.component.calendar.model.CalendarEventModel', {
    extend:  Ext.data.Model ,

    fields: [
        {
            name: 'object',
            type: 'auto',
            useNull: false
        },
        {
            name: 'date',
            type: 'string',
            convert: OPF.convertDate,
            useNull: false,
            dateFormat: 'time'
        },
        {
            name: 'startTime',
            type: 'string',
            convert: OPF.convertTime,
            useNull: false,
            dateFormat: 'time'
        },
        {
            name: 'endTime',
            type: 'string',
            convert: OPF.convertTime,
            useNull: false,
            dateFormat: 'time'
        }
    ]

});


             
                                                          
   

Ext.define('OPF.core.component.calendar.store.CalendarStore', {
    extend:  Ext.data.Store ,
    model: 'OPF.core.component.calendar.model.CalendarEventModel',

    proxy: {
        type: 'ajax',
        reader: {
            type: 'json',
            root: 'data'
        },
        writer: {
            type: 'json'
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.listeners = cfg.listeners || {};
        cfg.listeners = Ext.apply(cfg.listeners, cfg.listeners, this.listeners);

        this.addEvents('preload');

        this.superclass.constructor.call(this, cfg);
    },

    load: function(options) {
        var result = this.callParent(arguments);
        if (this.loading) {
            this.fireEvent('preload', this);
        }
        return result;
    }

});


             
                                                
                                                      
                                                          
   

Ext.define('OPF.core.component.calendar.CalendarPanel', {
    extend:  Ext.panel.Panel ,
    alias: 'widget.calendar-panel',

    layout: {
        type: 'vbox',
        align: 'stretch',
        pack: 'start'
    },

    calendarEvents: [],

    headerHeight: 30,
    hourHeight: 40,

    year: null,
    week: null,

    tpl: new Ext.XTemplate(
        '<h2>{object.name}</h2>',
        '<p>{startTime} - {endTime}</p>'
    ),

    interval: 30, 

    startTimeRange:'12:00 AM',
    endTimeRange:'11:30 PM',

    startHoursFromTheBeginningOfTheDay: null,
    endHoursFromTheBeginningOfTheDay: null,
    dateOffSet:null,


    store: null,

    initComponent: function() {
        var me = this;
        var d, m;

        this.year = new Date().getFullYear();
        this.week = new Date().getWeek();

        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var days = this.calculateDays(dateRangeOfWeek.startDate);

        var headerColumnDays = [{
            cls: 'cal-cell-time-header',
            html: 'Time',
            width: 100
        }];

        Ext.each(days, function(day, index) {

            var dayName = Ext.Date.format(day, 'l');
            var headerColumnDay = {
                cls: 'cal-cell-day-header',
                html: dayName,
                columnWidth: .2
            };
            headerColumnDays.push(headerColumnDay);
        });
        headerColumnDays.push({
            cls: 'cal-last-cell-time-header',
            width: 18
        });

        this.calendarHeader = Ext.create('Ext.container.Container', {
            layout: 'column',
            height: this.headerHeight,
            defaults: {
                xtype: 'container',
                height: this.headerHeight
            },
            items: headerColumnDays
        });
        var timeThresholds = this.calculateStartAndEndThresholds();
        var starTimeThreshold = timeThresholds[0], endTimeThreshold = timeThresholds[1];

        var startDayTime = Ext.Date.clearTime(new Date());
        var schedulerTimeLegendCells = [];
        for (m = starTimeThreshold; m < endTimeThreshold; m++) {
            var schedulerTime = Ext.Date.add(startDayTime, Ext.Date.MINUTE, m * me.interval);
            var timeName = Ext.Date.format(schedulerTime, 'g:i a');
            var schedulerTimeLegendCell = {
                xtype: 'container',
                height: this.hourHeight / 2,
                cls: 'cal-cell-time-legend',
                padding: ((this.hourHeight / 2) - 20) / 2 + ' 0 0 0',

                html: timeName
            };
            schedulerTimeLegendCells.push(schedulerTimeLegendCell);
        }

        this.schedulerDayColumns = [];
        Ext.each(days, function(day, index) {
            var schedulerDayCells = [];
            for (m = starTimeThreshold; m < endTimeThreshold; m++) {
                var schedulerDayCellCls = m % 2 == 1 ? 'cal-cell-day-fullhour' : 'cal-cell-day-halfhour';
                var schedulerDayCell = {
                    xtype: 'container',
                    height: me.hourHeight / 2,
                    cls: schedulerDayCellCls
                };
                schedulerDayCells.push(schedulerDayCell);
            }

            var schedulerDayColumn = Ext.create('Ext.container.Container', {
                columnWidth: .2,
                cls: 'cal-day-column',
                day: day,
                items: schedulerDayCells,
                dayEvents: [],
                starTimeThreshold: starTimeThreshold,
                endTimeThreshold: endTimeThreshold
            });

            me.schedulerDayColumns.push(schedulerDayColumn);
        });

        this.store.on('preload', this.showCalendarSchedulerMask, this);
        this.store.on('load', this.addCalendarEvents, this);

        this.calendarScheduler = Ext.create('Ext.container.Container', {
            xtype: 'container',
            flex: 1,
            autoScroll: true,
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    height: this.hourHeight * (this.endHoursFromTheBeginningOfTheDay - this.startHoursFromTheBeginningOfTheDay + 0.5),
                    defaults: {
                        xtype: 'container'
                    },
                    items: [
                        {
                            width: 100,
                            defaults: {
                                xtype: 'container',
                                style: {
                                    position: 'relative'
                                }
                            },
                            items: schedulerTimeLegendCells
                        },
                        this.schedulerDayColumns
                    ]
                }
            ]
        });

        this.items = [
            this.calendarHeader,
            this.calendarScheduler
        ];

        this.weekTitleText = Ext.create('Ext.toolbar.TextItem', {
            cls: 'cal-date-interval'
        });
        this.getWeekTitle(dateRangeOfWeek);

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'top',
                items: [
                    '->',
                    {
                        xtype: 'button',
                        cls: 'cal-btn cal-prev-btn',
                        text: '<< Prev Week',
                        handler: this.previousWeek,
                        scope: this
                    },
                    this.weekTitleText,
                    {
                        xtype: 'button',
                        cls: 'cal-btn cal-next-btn',
                        text: 'Next Week >>',
                        handler: this.nextWeek,
                        scope: this
                    },
                    '->'
                ]
            }
        ];

        this.callParent(arguments);
    },

    listeners: {
        resize: function(calendar, adjWidth, adjHeight ) {
            Ext.each(this.calendarEvents, function(calendarEvent) {
                calendarEvent.doResize();
            });
        }
    },

    getWeekTitle: function(dateRangeOfWeek) {
        var monday = dateRangeOfWeek.startDate;
        var friday = Ext.Date.clearTime(monday, true);
        friday.setDate(friday.getDate() + 4);
        this.weekTitleText.setText(
            Ext.Date.format(monday, 'F d, Y') +
                ' - ' +
            Ext.Date.format(friday, 'F d, Y'));
    },

    getYear: function() {
        return this.year;
    },

    getWeek: function() {
        return this.week;
    },

    setDate: function(year, week) {
        this.year = year;
        this.week = week;
        this.refreshCalendarEvents();
    },

    getDateRangeOfWeek: function() {
        var date = new Date();
        date.setYear(this.year);
        var numOfDaysPastSinceLastMonday = date.getDay() - 1;
        date.setDate(date.getDate() - numOfDaysPastSinceLastMonday);
        var weekNoToday = date.getWeek();
        var weeksInTheFuture = this.week - weekNoToday;
        var startDate = Ext.Date.clearTime(date, true);
        startDate.setDate(date.getDate() + 7 * weeksInTheFuture);
        var endDate = Ext.Date.clearTime(startDate, true);
        endDate.setDate(startDate.getDate() + 6);
        return {
            startDate: startDate,
            endDate: endDate
        };
    },

    calculateDays: function(startWeekDay) {
        var days = [];
        for (var d = 0; d < 5; d++) {
            var day = Ext.Date.add(startWeekDay, Ext.Date.DAY, d);
            days.push(day);
        }
        return days;
    },

    refreshCalendarEvents: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var startWeekDay = Ext.Date.clearTime(dateRangeOfWeek.startDate);
        var days = this.calculateDays(startWeekDay);
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn, index) {
            schedulerDayColumn.day = days[index];
            Ext.each(schedulerDayColumn.dayEvents, function(dayEvent) {
                schedulerDayColumn.remove(dayEvent);
            });
            schedulerDayColumn.dayEvents = [];
        });
        this.calendarEvents = [];
        this.store.load();
        this.getWeekTitle(dateRangeOfWeek);
    },

    showCalendarSchedulerMask: function() {
        this.calendarSchedulerMask = new Ext.LoadMask(this.calendarScheduler.getEl(), {msg: 'Loading...'});
        this.calendarSchedulerMask.show();
    },

    addCalendarEvents: function() {
        var me = this;
        this.calendarSchedulerMask.hide();
        this.store.sort([
            {
                property : 'startTime',
                direction: 'ASC'
            }
        ]);
        var calendarEventModels = this.store.getRange();
        Ext.each(calendarEventModels, function(calendarEventModel) {
            var startTime =  Ext.Date.parse(calendarEventModel.get('startTime'), 'H:i:s');
            var endTime =  Ext.Date.parse(calendarEventModel.get('endTime'), 'H:i:s');
            if( !me.isValidCalendarEventDate(startTime, endTime) ) return;
            calendarEventModel.set('startTime', startTime);
            calendarEventModel.set('endTime', endTime);
            calendarEventModel.set('date', Ext.Date.parse(calendarEventModel.get('date'), 'Y-m-d'));

            var date = calendarEventModel.get('date');
            var schedulerDayColumn = me.findSchedulerDayColumn(date);
            if (OPF.isNotEmpty(schedulerDayColumn)) {
                var calendarEvent = Ext.create('OPF.core.component.calendar.CalendarEvent', {
                    tpl: me.tpl,
                    hourHeight: me.hourHeight,
                    calendarEventModel: calendarEventModel,
                    dayColumn: schedulerDayColumn,
                    dateOffSet: me.dateOffSet
                });
                schedulerDayColumn.dayEvents.push(calendarEvent);
                me.calendarEvents.push(calendarEvent);
            }
        });
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn) {
            me.renderCalendarEvents(schedulerDayColumn);
        });
    },

    findSchedulerDayColumn: function(date) {
        var dayColumn = null;
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn) {
            if (date.getTime() == schedulerDayColumn.day.getTime()) {
                dayColumn = schedulerDayColumn;
            }
        });
        return dayColumn;
    },

    previousWeek: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var weekDate = dateRangeOfWeek.startDate;
        weekDate.setDate(weekDate.getDate() - 7);
        this.setDate(weekDate.getFullYear(), weekDate.getWeek());
    },

    nextWeek: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var weekDate = dateRangeOfWeek.startDate;
        weekDate.setDate(weekDate.getDate() + 7);
        this.setDate(weekDate.getFullYear(), weekDate.getWeek());
    },

    calculateFullMinutesFromTheBeginningOfTheDay : function(dateObj){
        if( OPF.isNotEmpty(dateObj) ) {
            return (dateObj.getHours() * 60) + dateObj.getMinutes();
        } else {
            return null;
        }
    },

    calculateFullHoursFromTheBeginningOfTheDay : function(dateObj){
        if( OPF.isNotEmpty(dateObj) ) {
            return this.calculateFullMinutesFromTheBeginningOfTheDay(dateObj) / 60;
        } else {
            return null;
        }
    },

    isValidCalendarEventDate: function(startDateObj, endDateObj){
        if( OPF.isEmpty(startDateObj) || OPF.isEmpty(endDateObj) ) return false;
        var startHours =  this.calculateFullHoursFromTheBeginningOfTheDay(startDateObj);
        var endHours = this.calculateFullHoursFromTheBeginningOfTheDay(endDateObj);
        return startHours >= this.startHoursFromTheBeginningOfTheDay && endHours <= this.endHoursFromTheBeginningOfTheDay;
    },

    calculateStartAndEndThresholds:function(){
        var startDate = Ext.Date.parse(this.startTimeRange, "g:i A");
        var endDate = Ext.Date.parse(this.endTimeRange, "g:i A");
        if( OPF.isEmpty(startDate) ) {
            throw new Error("startTime parameter is not valid ");
        } else if( OPF.isEmpty(endDate) ) {
            throw new Error("endTime parameter is not valid ");
        } else if( startDate.getMinutes() != 0 && startDate.getMinutes() != 30 ) {
            throw new Error("startTime minutes should be 00 or 30");
        } else if( endDate.getMinutes() != 0 && endDate.getMinutes() != 30) {
            throw  new Error("endTime minutes should be 00 or 30")
        }
        this.dateOffSet = startDate;
        this.startHoursFromTheBeginningOfTheDay = this.calculateFullHoursFromTheBeginningOfTheDay(startDate);
        this.endHoursFromTheBeginningOfTheDay = this.calculateFullHoursFromTheBeginningOfTheDay(endDate);

        return [(this.startHoursFromTheBeginningOfTheDay * 2), (this.endHoursFromTheBeginningOfTheDay * 2 + 1)];
    },

    renderCalendarEvents: function(schedulerDayColumn) {
        var me = this;

        if (schedulerDayColumn.dayEvents.length > 0) {
            var columnWidth = schedulerDayColumn.getWidth();

            var calendarEventGroups = this.calculateCalendarEventGroup(schedulerDayColumn.dayEvents);
            Ext.each(calendarEventGroups, function(calendarEventGroup){
                var startMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEventGroup.startTime);
                var endMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEventGroup.endTime);
                var positionMatrix = [];
                for (var currentMinute = startMinute; currentMinute < endMinute; currentMinute++) {
                    var indexOfMinute = currentMinute - startMinute;
                    positionMatrix[indexOfMinute] = [];
                    Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                        positionMatrix[indexOfMinute][indexOfEvent] = 0;
                        var startEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getStartTime());
                        var endEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getEndTime());
                        if (startEventMinute <= currentMinute && currentMinute <= endEventMinute) {
                            if (indexOfEvent > 0) {
                                var maxPrevIndentOfEvent = positionMatrix[indexOfMinute].slice(0, indexOfEvent).max();
                                positionMatrix[indexOfMinute][indexOfEvent] = Math.max(1, calendarEvent.indentOfEvent, maxPrevIndentOfEvent + 1);
                                calendarEvent.indentOfEvent = Math.max(1, positionMatrix[indexOfMinute][indexOfEvent]);
                            } else {
                                positionMatrix[indexOfMinute][indexOfEvent] = 1;
                            }
                        }
                    });
                }

                var maxInterceptEventsPerTime = 1;
                Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                    var startEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getStartTime());
                    var endEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getEndTime());
                    var indents = [];
                    for (var currentMinute = startEventMinute; currentMinute < endEventMinute; currentMinute++) {
                        indents[currentMinute - startEventMinute] = positionMatrix[currentMinute - startMinute].slice(indexOfEvent, indexOfEvent + 1);
                    }
                    maxInterceptEventsPerTime = Math.max(maxInterceptEventsPerTime, indents.max());
                    calendarEvent.indentOfEvent = indents.max() - 1;
                });

                schedulerDayColumn.add(calendarEventGroup.calendarEvents);

                Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                    calendarEvent.scaleOfEvent = maxInterceptEventsPerTime;
                    calendarEvent.doResize();
                });
            });
        }
    },

    calculateCalendarEventGroup: function(calendarEvents) {
        var calendarEventGroups = [];
        Ext.each(calendarEvents, function(calendarEvent, m) {
            var foundInterceptEventGroup = null;
            Ext.each(calendarEventGroups, function(calendarEventGroup) {
                var st1 = calendarEventGroup.startTime.getTime();
                var et1 = calendarEventGroup.endTime.getTime();
                var st2 = calendarEvent.getStartTime().getTime();
                var et2 = calendarEvent.getEndTime().getTime();
                if ((st2 < st1 && st1 < et2) || (st1 < st2 && st2 < et1) || st1 == st2) {
                    foundInterceptEventGroup = calendarEventGroup;
                }
            });
            if (foundInterceptEventGroup != null) {
                foundInterceptEventGroup.startTime = new Date(Math.min.apply(null,[foundInterceptEventGroup.startTime, calendarEvent.getStartTime()]));
                foundInterceptEventGroup.endTime = new Date(Math.max.apply(null,[foundInterceptEventGroup.endTime, calendarEvent.getEndTime()]));
                foundInterceptEventGroup.calendarEvents.push(calendarEvent);
            } else {
                var calendarEventGroup = {
                    startTime: calendarEvent.getStartTime(),
                    endTime: calendarEvent.getEndTime(),
                    calendarEvents: [ calendarEvent ]
                };
                calendarEventGroups.push(calendarEventGroup);
            }
        });

        

        return calendarEventGroups;
    }

});
Ext.define('OPF.core.component.form.SubLabelable', {

    initSubLabelable: function() {
        var fieldLabel = '';
        this.fieldOrgLabel = this.fieldLabel;
        if (OPF.isNotBlank(this.fieldLabel)) {
            fieldLabel += '<span class="main-label">' + this.fieldLabel + this.labelSeparator + '</span>';
        }
        if (OPF.isNotBlank(this.subFieldLabel)) {
            fieldLabel += '<span class="sub-label">' + this.subFieldLabel + '</span>';
        }
        this.fieldLabel = fieldLabel;
        this.labelSeparator = '';
    }

});


Ext.define('OPF.core.component.form.ErrorHandler', {

    getValidatorErrors: function(value) {
        var me = this,
            errors = [],
            validator = me.validator,
            customValidator = me.customValidator,
            emptyText = me.emptyText,
            allowBlank = me.allowBlank,
            vtype = me.vtype,
            vtypes = Ext.form.field.VTypes,
            regex = me.regex,
            format = Ext.String.format,
            messages, trimmed;

        value = value || me.processRawValue(me.getRawValue());

        if (Ext.isFunction(customValidator)) {
            messages = customValidator.call(me, value);
            Ext.each(messages, function(message) {
                if (message) {
                    errors.push(message);
                }
            });
        }

        if (Ext.isFunction(validator)) {
            messages = validator.call(me, value);
            Ext.each(messages, function(message){
                errors.push(message);
            });
        }

        trimmed = me.allowOnlyWhitespace ? value : Ext.String.trim(value.toString());

        if (trimmed.length < 1 || (value === me.emptyText && me.valueContainsPlaceholder)) {
            if (!allowBlank) {
                errors.push(me.blankText);
            }

            return errors;
        }

        if (value.length < me.minLength) {
            errors.push(format(me.minLengthText, me.minLength));
        }

        if (value.length > me.maxLength) {
            errors.push(format(me.maxLengthText, me.maxLength));
        }

        if (vtype) {
            if(!vtypes[vtype](value, me)){
                errors.push(me.vtypeText || vtypes[vtype +'Text']);
            }
        }

        if (regex && !regex.test(value)) {
            errors.push(me.regexText || me.invalidText);
        }

        return errors;
    }

});

Ext.define('Ext.ux.form.field.BoxSelect', {
    extend: Ext.form.field.ComboBox ,
    alias: ['widget.comboboxselect', 'widget.boxselect'],
                                                        

    
    
    

    
    multiSelect: true,

    

    
    forceSelection: true,

    
    createNewOnEnter: false,

    
    createNewOnBlur: false,

    
    encodeSubmitValue: false,

    
    
    



    
    
    

    
    triggerOnClick: true,

    
    stacked: false,

    
    pinList: true,

    
    filterPickList: false,

    
    
    



    
    
    
    selectOnFocus: true,

    
    grow: true,

    
    growMin: false,

    
    growMax: false,

    
    

    
    
    


    
    
    

    

    
    
    



    
    
    

    
    fieldSubTpl: [
        '<div id="{cmpId}-listWrapper" class="x-boxselect {fieldCls} {typeCls}">',
        '<ul id="{cmpId}-itemList" class="x-boxselect-list">',
        '<li id="{cmpId}-inputElCt" class="x-boxselect-input">',
        '<input id="{cmpId}-inputEl" type="{type}" ',
        '<tpl if="name">name="{name}" </tpl>',
        '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
        '<tpl if="size">size="{size}" </tpl>',
        '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
        '<tpl if="disabled"> disabled="disabled"</tpl>',
        'class="x-boxselect-input-field {inputElCls}" autocomplete="off">',
        '</li>',
        '</ul>',
        '</div>',
        {
            compiled: true,
            disableFormats: true
        }
    ],

    
    childEls: [ 'listWrapper', 'itemList', 'inputEl', 'inputElCt' ],

    
    componentLayout: 'boxselectfield',

    
    initComponent: function() {
        var me = this,
        typeAhead = me.typeAhead;

        if (typeAhead && !me.editable) {
            Ext.Error.raise('If typeAhead is enabled the combo must be editable: true -- please change one of those settings.');
        }

        Ext.apply(me, {
            typeAhead: false
        });

        me.callParent();

        me.typeAhead = typeAhead;

        me.selectionModel = new Ext.selection.Model({
            store: me.valueStore,
            mode: 'MULTI',
            lastFocused: null,
            onSelectChange: function(record, isSelected, suppressEvent, commitFn) {
                commitFn();
            }
        });

        if (!Ext.isEmpty(me.delimiter) && me.multiSelect) {
            me.delimiterRegexp = new RegExp(String(me.delimiter).replace(/[$%()*+.?\[\\\]{|}]/g, "\\$&"));
        }
    },

    
    initEvents: function() {
        var me = this;

        me.callParent(arguments);

        if (!me.enableKeyEvents) {
            me.mon(me.inputEl, 'keydown', me.onKeyDown, me);
        }
        me.mon(me.inputEl, 'paste', me.onPaste, me);
        me.mon(me.listWrapper, 'click', me.onItemListClick, me);

        
        
        me.mon(me.selectionModel, {
            'selectionchange': function(selModel, selectedRecs) {
                me.applyMultiselectItemMarkup();
                me.fireEvent('valueselectionchange', me, selectedRecs);
            },
            'focuschange': function(selectionModel, oldFocused, newFocused) {
                me.fireEvent('valuefocuschange', me, oldFocused, newFocused);
            },
            scope: me
        });
    },

    
    onBindStore: function(store, initial) {
        var me = this;

        if (store) {
            me.valueStore = new Ext.data.Store({
                model: store.model,
                proxy: {
                    type: 'memory'
                }
            });
            me.mon(me.valueStore, 'datachanged', me.applyMultiselectItemMarkup, me);
            if (me.selectionModel) {
                me.selectionModel.bindStore(me.valueStore);
            }
        }
    },

    
    onUnbindStore: function(store) {
        var me = this,
        valueStore = me.valueStore;

        if (valueStore) {
            if (me.selectionModel) {
                me.selectionModel.setLastFocused(null);
                me.selectionModel.deselectAll();
                me.selectionModel.bindStore(null);
            }
            me.mun(valueStore, 'datachanged', me.applyMultiselectItemMarkup, me);
            valueStore.destroy();
            me.valueStore = null;
        }

        me.callParent(arguments);
    },

    
    createPicker: function() {
        var me = this,
        picker = me.callParent(arguments);

        me.mon(picker, {
            'beforerefresh': me.onBeforeListRefresh,
            scope: me
        });

        if (me.filterPickList) {
            picker.addCls('x-boxselect-hideselections');
        }

        return picker;
    },

    
    onDestroy: function() {
        var me = this;

        Ext.destroyMembers(me, 'valueStore', 'selectionModel');

        me.callParent(arguments);
    },

    
    getSubTplData: function() {
        var me = this,
            data = me.callParent(),
            isEmpty = me.emptyText && data.value.length < 1;

        if (isEmpty) {
            data.value = me.emptyText;
        } else {
            data.value = '';
        }
        data.inputElCls = data.fieldCls.match(me.emptyCls) ? me.emptyCls : '';

        return data;
    },

    
    afterRender: function() {
        var me = this;

        if (Ext.supports.Placeholder && me.inputEl && me.emptyText) {
            delete me.inputEl.dom.placeholder;
        }

        me.bodyEl.applyStyles('vertical-align:top');

        if (me.grow) {
            if (Ext.isNumber(me.growMin) && (me.growMin > 0)) {
                me.listWrapper.applyStyles('min-height:'+me.growMin+'px');
            }
            if (Ext.isNumber(me.growMax) && (me.growMax > 0)) {
                me.listWrapper.applyStyles('max-height:'+me.growMax+'px');
            }
        }

        if (me.stacked === true) {
            me.itemList.addCls('x-boxselect-stacked');
        }

        if (!me.multiSelect) {
            me.itemList.addCls('x-boxselect-singleselect');
        }

        me.applyMultiselectItemMarkup();

        me.callParent(arguments);
    },

    
    findRecord: function(field, value) {
        var ds = this.store,
        matches;

        if (!ds) {
            return false;
        }

        matches = ds.queryBy(function(rec, id) {
            return rec.isEqual(rec.get(field), value);
        });

        return (matches.getCount() > 0) ? matches.first() : false;
    },

    
    onLoad: function() {
        var me = this,
        valueField = me.valueField,
        valueStore = me.valueStore,
        changed = false;

        if (valueStore) {
            if (!Ext.isEmpty(me.value) && (valueStore.getCount() == 0)) {
                me.setValue(me.value, false, true);
            }

            valueStore.suspendEvents();
            valueStore.each(function(rec) {
                var r = me.findRecord(valueField, rec.get(valueField)),
                i = r ? valueStore.indexOf(rec) : -1;
                if (i >= 0) {
                    valueStore.removeAt(i);
                    valueStore.insert(i, r);
                    changed = true;
                }
            });
            valueStore.resumeEvents();
            if (changed) {
                valueStore.fireEvent('datachanged', valueStore);
            }
        }

        me.callParent(arguments);
    },

    
    isFilteredRecord: function(record) {
        var me = this,
        store = me.store,
        valueField = me.valueField,
        storeRecord,
        filtered = false;

        storeRecord = store.findExact(valueField, record.get(valueField));

        filtered = ((storeRecord === -1) && (!store.snapshot || (me.findRecord(valueField, record.get(valueField)) !== false)));

        filtered = filtered || (!filtered && (storeRecord === -1) && (me.forceSelection !== true) &&
            (me.valueStore.findExact(valueField, record.get(valueField)) >= 0));

        return filtered;
    },

    
    doRawQuery: function() {
        var me = this,
        rawValue = me.inputEl.dom.value;

        if (me.multiSelect) {
            rawValue = rawValue.split(me.delimiter).pop();
        }

        this.doQuery(rawValue, false, true);
    },

    
    onBeforeListRefresh: function() {
        this.ignoreSelection++;
    },

    
    onListRefresh: function() {
        this.callParent(arguments);
        if (this.ignoreSelection > 0) {
            --this.ignoreSelection;
        }
    },

    
    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
        valueStore = me.valueStore,
        mergedRecords = [],
        i;

        
        
        if ((me.ignoreSelection <= 0) && me.isExpanded) {
            
            valueStore.each(function(rec) {
                if (Ext.Array.contains(selectedRecords, rec) || me.isFilteredRecord(rec)) {
                    mergedRecords.push(rec);
                }
            });
            mergedRecords = Ext.Array.merge(mergedRecords, selectedRecords);

            i = Ext.Array.intersect(mergedRecords, valueStore.getRange()).length;
            if ((i != mergedRecords.length) || (i != me.valueStore.getCount())) {
                me.setValue(mergedRecords, false);
                if (!me.multiSelect || !me.pinList) {
                    Ext.defer(me.collapse, 1, me);
                }
                if (valueStore.getCount() > 0) {
                    me.fireEvent('select', me, valueStore.getRange());
                }
            }
            me.inputEl.focus();
            if (!me.pinList) {
                me.inputEl.dom.value = '';
            }
            if (me.selectOnFocus) {
                me.inputEl.dom.select();
            }
        }
    },

    
    syncSelection: function() {
        var me = this,
        picker = me.picker,
        valueField = me.valueField,
        pickStore, selection, selModel;

        if (picker) {
            pickStore = picker.store;

            
            selection = [];
            if (me.valueStore) {
                me.valueStore.each(function(rec) {
                    var i = pickStore.findExact(valueField, rec.get(valueField));
                    if (i >= 0) {
                        selection.push(pickStore.getAt(i));
                    }
                });
            }

            
            me.ignoreSelection++;
            selModel = picker.getSelectionModel();
            selModel.deselectAll();
            if (selection.length > 0) {
                selModel.select(selection);
            }
            if (me.ignoreSelection > 0) {
                --me.ignoreSelection;
            }
        }
    },

    
    doAlign: function(){
        var me = this,
            picker = me.picker,
            aboveSfx = '-above',
            isAbove;

        me.picker.alignTo(me.listWrapper, me.pickerAlign, me.pickerOffset);
        
        
        isAbove = picker.el.getY() < me.inputEl.getY();
        me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
        picker[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);
    },

    
    alignPicker: function() {
        var me = this,
            picker = me.picker,
            pickerScrollPos = picker.getTargetEl().dom.scrollTop;

        me.callParent(arguments);

        if (me.isExpanded) {
            if (me.matchFieldWidth) {
                
                picker.setWidth(me.listWrapper.getWidth());
            }

            picker.getTargetEl().dom.scrollTop = pickerScrollPos;
        }
    },

    
    getCursorPosition: function() {
        var cursorPos;
        if (Ext.isIE) {
            cursorPos = document.selection.createRange();
            cursorPos.collapse(true);
            cursorPos.moveStart("character", -this.inputEl.dom.value.length);
            cursorPos = cursorPos.text.length;
        } else {
            cursorPos = this.inputEl.dom.selectionStart;
        }
        return cursorPos;
    },

    
    hasSelectedText: function() {
        var sel, range;
        if (Ext.isIE) {
            sel = document.selection;
            range = sel.createRange();
            return (range.parentElement() == this.inputEl.dom);
        } else {
            return this.inputEl.dom.selectionStart != this.inputEl.dom.selectionEnd;
        }
    },

    
    onKeyDown: function(e, t) {
        var me = this,
        key = e.getKey(),
        rawValue = me.inputEl.dom.value,
        valueStore = me.valueStore,
        selModel = me.selectionModel,
        stopEvent = false;

        if (me.readOnly || me.disabled || !me.editable) {
            return;
        }

        if (me.isExpanded && (key == e.A && e.ctrlKey)) {
            
            me.select(me.getStore().getRange());
            selModel.setLastFocused(null);
            selModel.deselectAll();
            me.collapse();
            me.inputEl.focus();
            stopEvent = true;
        } else if ((valueStore.getCount() > 0) &&
                ((rawValue == '') || ((me.getCursorPosition() === 0) && !me.hasSelectedText()))) {
            
            var lastSelectionIndex = (selModel.getCount() > 0) ? valueStore.indexOf(selModel.getLastSelected() || selModel.getLastFocused()) : -1;

            if ((key == e.BACKSPACE) || (key == e.DELETE)) {
                if (lastSelectionIndex > -1) {
                    if (selModel.getCount() > 1) {
                        lastSelectionIndex = -1;
                    }
                    me.valueStore.remove(selModel.getSelection());
                } else {
                    me.valueStore.remove(me.valueStore.last());
                }
                selModel.clearSelections();
                me.setValue(me.valueStore.getRange());
                if (lastSelectionIndex > 0) {
                    selModel.select(lastSelectionIndex - 1);
                }
                stopEvent = true;
            } else if ((key == e.RIGHT) || (key == e.LEFT)) {
                if ((lastSelectionIndex == -1) && (key == e.LEFT)) {
                    selModel.select(valueStore.last());
                    stopEvent = true;
                } else if (lastSelectionIndex > -1) {
                    if (key == e.RIGHT) {
                        if (lastSelectionIndex < (valueStore.getCount() - 1)) {
                            selModel.select(lastSelectionIndex + 1, e.shiftKey);
                            stopEvent = true;
                        } else if (!e.shiftKey) {
                            selModel.setLastFocused(null);
                            selModel.deselectAll();
                            stopEvent = true;
                        }
                    } else if ((key == e.LEFT) && (lastSelectionIndex > 0)) {
                        selModel.select(lastSelectionIndex - 1, e.shiftKey);
                        stopEvent = true;
                    }
                }
            } else if (key == e.A && e.ctrlKey) {
                selModel.selectAll();
                stopEvent = e.A;
            }
            me.inputEl.focus();
        }

        if (stopEvent) {
            me.preventKeyUpEvent = stopEvent;
            e.stopEvent();
            return;
        }

        
        if (me.isExpanded && (key == e.ENTER) && me.picker.highlightedItem) {
            me.preventKeyUpEvent = true;
        }

        if (me.enableKeyEvents) {
            me.callParent(arguments);
        }

        if (!e.isSpecialKey() && !e.hasModifier()) {
            me.selectionModel.setLastFocused(null);
            me.selectionModel.deselectAll();
            me.inputEl.focus();
        }
    },

    
    onKeyUp: function(e, t) {
        var me = this,
        rawValue = me.inputEl.dom.value;

        if (me.preventKeyUpEvent) {
            e.stopEvent();
            if ((me.preventKeyUpEvent === true) || (e.getKey() === me.preventKeyUpEvent)) {
                delete me.preventKeyUpEvent;
            }
            return;
        }

        if (me.multiSelect && (me.delimiterRegexp && me.delimiterRegexp.test(rawValue)) ||
                ((me.createNewOnEnter === true) && e.getKey() == e.ENTER)) {
            rawValue = Ext.Array.clean(rawValue.split(me.delimiterRegexp));
            me.inputEl.dom.value = '';
            me.setValue(me.valueStore.getRange().concat(rawValue));
            me.inputEl.focus();
        }

        me.callParent([e,t]);
    },

    
    onPaste: function(e, t) {
        var me = this,
            rawValue = me.inputEl.dom.value,
            clipboard = (e && e.browserEvent && e.browserEvent.clipboardData) ? e.browserEvent.clipboardData : false;

        if (me.multiSelect && (me.delimiterRegexp && me.delimiterRegexp.test(rawValue))) {
            if (clipboard && clipboard.getData) {
                if (/text\/plain/.test(clipboard.types)) {
                    rawValue = clipboard.getData('text/plain');
                } else if (/text\/html/.test(clipboard.types)) {
                    rawValue = clipboard.getData('text/html');
                }
            }

            rawValue = Ext.Array.clean(rawValue.split(me.delimiterRegexp));
            me.inputEl.dom.value = '';
            me.setValue(me.valueStore.getRange().concat(rawValue));
            me.inputEl.focus();
        }
    },

    
    onExpand: function() {
        var me = this,
            keyNav = me.listKeyNav;

        me.callParent(arguments);

        if (keyNav || !me.filterPickList) {
            return;
        }
        keyNav = me.listKeyNav;
        keyNav.highlightAt = function(index) {
            var boundList = this.boundList,
                item = boundList.all.item(index),
                len = boundList.all.getCount(),
                direction;

            if (item && item.hasCls('x-boundlist-selected')) {
                if ((index == 0) || !boundList.highlightedItem || (boundList.indexOf(boundList.highlightedItem) < index)) {
                    direction = 1;
                } else {
                    direction = -1;
                }
                do {
                    index = index + direction;
                    item = boundList.all.item(index);
                } while ((index > 0) && (index < len) && item.hasCls('x-boundlist-selected'));

                if (item.hasCls('x-boundlist-selected')) {
                    return;
                }
            }

            if (item) {
                item = item.dom;
                boundList.highlightItem(item);
                boundList.getTargetEl().scrollChildIntoView(item, false);
            }
        };
    },

    
    onTypeAhead: function() {
        var me = this,
        displayField = me.displayField,
        inputElDom = me.inputEl.dom,
        valueStore = me.valueStore,
        boundList = me.getPicker(),
        record, newValue, len, selStart;

        if (me.filterPickList) {
            var fn = this.createFilterFn(displayField, inputElDom.value);
            record = me.store.findBy(function(rec) {
                return ((valueStore.indexOfId(rec.getId()) === -1) && fn(rec));
            });
            record = (record === -1) ? false : me.store.getAt(record);
        } else {
            record = me.store.findRecord(displayField, inputElDom.value);
        }

        if (record) {
            newValue = record.get(displayField);
            len = newValue.length;
            selStart = inputElDom.value.length;
            boundList.highlightItem(boundList.getNode(record));
            if (selStart !== 0 && selStart !== len) {
                inputElDom.value = newValue;
                me.selectText(selStart, newValue.length);
            }
        }
    },

    
    onItemListClick: function(evt, el, o) {
        var me = this,
        itemEl = evt.getTarget('.x-boxselect-item'),
        closeEl = itemEl ? evt.getTarget('.x-boxselect-item-close') : false;

        if (me.readOnly || me.disabled) {
            return;
        }

        evt.stopPropagation();

        if (itemEl) {
            if (closeEl) {
                me.removeByListItemNode(itemEl);
                if (me.valueStore.getCount() > 0) {
                    me.fireEvent('select', me, me.valueStore.getRange());
                }
            } else {
                me.toggleSelectionByListItemNode(itemEl, evt.shiftKey);
            }
            me.inputEl.focus();
        } else {
            if (me.selectionModel.getCount() > 0) {
                me.selectionModel.setLastFocused(null);
                me.selectionModel.deselectAll();
            }
            if (me.triggerOnClick) {
                me.onTriggerClick();
            }
        }
    },

    
    getMultiSelectItemMarkup: function() {
        var me = this;

        if (!me.multiSelectItemTpl) {
            if (!me.labelTpl) {
                me.labelTpl = Ext.create('Ext.XTemplate',
                    '{[values.' + me.displayField + ']}'
                );
            } else if (Ext.isString(me.labelTpl) || Ext.isArray(me.labelTpl)) {
                me.labelTpl = Ext.create('Ext.XTemplate', me.labelTpl);
            }

            me.multiSelectItemTpl = [
            '<tpl for=".">',
            '<li class="x-boxselect-item ',
            '<tpl if="this.isSelected(values.'+ me.valueField + ')">',
            ' selected',
            '</tpl>',
            '" qtip="{[typeof values === "string" ? values : values.' + me.displayField + ']}">' ,
            '<div class="x-boxselect-item-text">{[typeof values === "string" ? values : this.getItemLabel(values)]}</div>',
            '<div class="x-tab-close-btn x-boxselect-item-close"></div>' ,
            '</li>' ,
            '</tpl>',
            {
                compile: true,
                disableFormats: true,
                isSelected: function(value) {
                    var i = me.valueStore.findExact(me.valueField, value);
                    if (i >= 0) {
                        return me.selectionModel.isSelected(me.valueStore.getAt(i));
                    }
                    return false;
                },
                getItemLabel: function(values) {
                    return me.getTpl('labelTpl').apply(values);
                }
            }
            ];
        }

        return this.getTpl('multiSelectItemTpl').apply(Ext.Array.pluck(this.valueStore.getRange(), 'data'));
    },

    
    applyMultiselectItemMarkup: function() {
        var me = this,
        itemList = me.itemList,
        item;

        if (itemList) {
            while ((item = me.inputElCt.prev()) != null) {
                item.remove();
            }
            me.inputElCt.insertHtml('beforeBegin', me.getMultiSelectItemMarkup());
        }

        Ext.Function.defer(function() {
            if (me.picker && me.isExpanded) {
                me.alignPicker();
            }
            if (me.hasFocus) {
                me.inputElCt.scrollIntoView(me.listWrapper);
            }
        }, 15);
    },

    
    getRecordByListItemNode: function(itemEl) {
        var me = this,
        itemIdx = 0,
        searchEl = me.itemList.dom.firstChild;

        while (searchEl && searchEl.nextSibling) {
            if (searchEl == itemEl) {
                break;
            }
            itemIdx++;
            searchEl = searchEl.nextSibling;
        }
        itemIdx = (searchEl == itemEl) ? itemIdx : false;

        if (itemIdx === false) {
            return false;
        }

        return me.valueStore.getAt(itemIdx);
    },

    
    toggleSelectionByListItemNode: function(itemEl, keepExisting) {
        var me = this,
        rec = me.getRecordByListItemNode(itemEl),
        selModel = me.selectionModel;

        if (rec) {
            if (selModel.isSelected(rec)) {
                if (selModel.isFocused(rec)) {
                    selModel.setLastFocused(null);
                }
                selModel.deselect(rec);
            } else {
                selModel.select(rec, keepExisting);
            }
        }
    },

    
    removeByListItemNode: function(itemEl) {
        var me = this,
        rec = me.getRecordByListItemNode(itemEl);

        if (rec) {
            me.valueStore.remove(rec);
            me.setValue(me.valueStore.getRange());
        }
    },

    
    getRawValue: function() {
        var me = this,
        inputEl = me.inputEl,
        result;
        me.inputEl = false;
        result = me.callParent(arguments);
        me.inputEl = inputEl;
        return result;
    },

    
    setRawValue: function(value) {
        var me = this,
        inputEl = me.inputEl,
        result;

        me.inputEl = false;
        result = me.callParent([value]);
        me.inputEl = inputEl;

        return result;
    },

    
    addValue: function(value) {
        var me = this;
        if (value) {
            me.setValue(Ext.Array.merge(me.value, Ext.Array.from(value)));
        }
    },

    
    removeValue: function(value) {
        var me = this;

        if (value) {
            me.setValue(Ext.Array.difference(me.value, Ext.Array.from(value)));
        }
    },

    
    setValue: function(value, doSelect, skipLoad) {
        var me = this,
        valueStore = me.valueStore,
        valueField = me.valueField,
        record, len, i, valueRecord, h,
        unknownValues = [];

        if (Ext.isEmpty(value)) {
            value = null;
        }
        if (Ext.isString(value) && me.multiSelect) {
            value = value.split(me.delimiter);
        }
        value = Ext.Array.from(value, true);

        for (i = 0, len = value.length; i < len; i++) {
            record = value[i];
            if (!record || !record.isModel) {
                valueRecord = valueStore.findExact(valueField, record);
                if (valueRecord >= 0) {
                    value[i] = valueStore.getAt(valueRecord);
                } else {
                    valueRecord = me.findRecord(valueField, record);
                    if (!valueRecord) {
                        if (me.forceSelection) {
                            unknownValues.push(record);
                        } else {
                            valueRecord = {};
                            valueRecord[me.valueField] = record;
                            valueRecord[me.displayField] = record;
                            valueRecord = new me.valueStore.model(valueRecord);
                        }
                    }
                    if (valueRecord) {
                        value[i] = valueRecord;
                    }
                }
            }
        }

        if ((skipLoad !== true) && (unknownValues.length > 0) && (me.queryMode === 'remote')) {
            var params = {};
            params[me.valueField] = unknownValues.join(me.delimiter);
            me.store.load({
                params: params,
                callback: function() {
                    if (me.itemList) {
                        me.itemList.unmask();
                    }
                    me.setValue(value, doSelect, true);
                    me.autoSize();
                }
            });
            return false;
        }

        
        if (!me.multiSelect && (value.length > 0)) {
            for (i = value.length - 1; i >= 0; i--) {
                if (value[i].isModel) {
                    value = value[i];
                    break;
                }
            }
            if (Ext.isArray(value)) {
                value = value[value.length - 1];
            }
        }

        return me.callParent([value, doSelect]);
    },

    
    getValueRecords: function() {
        return this.valueStore.getRange();
    },

    
    getSubmitData: function() {
        var me = this,
        val = me.callParent(arguments);

        if (me.multiSelect && me.encodeSubmitValue && val && val[me.name]) {
            val[me.name] = Ext.encode(val[me.name]);
        }

        return val;
    },

    
    mimicBlur: function() {
        var me = this;

        if (me.selectOnTab && me.picker && me.picker.highlightedItem) {
            me.inputEl.dom.value = '';
        }

        me.callParent(arguments);
    },

    
    assertValue: function() {
        var me = this,
        rawValue = me.inputEl.dom.value,
        rec = !Ext.isEmpty(rawValue) ? me.findRecordByDisplay(rawValue) : false,
        value = false;

        if (!rec && !me.forceSelection && me.createNewOnBlur && !Ext.isEmpty(rawValue)) {
            value = rawValue;
        } else if (rec) {
            value = rec;
        }

        if (value) {
            me.addValue(value);
        }

        me.inputEl.dom.value = '';

        me.collapse();
    },

    
    checkChange: function() {
        if (!this.suspendCheckChange && !this.isDestroyed) {
            var me = this,
            valueStore = me.valueStore,
            lastValue = me.lastValue,
            valueField = me.valueField,
            newValue = Ext.Array.map(Ext.Array.from(me.value), function(val) {
                if (val.isModel) {
                    return val.get(valueField);
                }
                return val;
            }, this).join(this.delimiter),
            isEqual = me.isEqual(newValue, lastValue);

            if (!isEqual || ((newValue.length > 0 && valueStore.getCount() < newValue.length))) {
                valueStore.suspendEvents();
                valueStore.removeAll();
                if (Ext.isArray(me.valueModels)) {
                    valueStore.add(me.valueModels);
                }
                valueStore.resumeEvents();
                valueStore.fireEvent('datachanged', valueStore);

                if (!isEqual) {
                    me.lastValue = newValue;
                    me.fireEvent('change', me, newValue, lastValue);
                    me.onChange(newValue, lastValue);
                }
            }
        }
    },

    
    isEqual: function(v1, v2) {
        var fromArray = Ext.Array.from,
            valueField = this.valueField,
            i, len, t1, t2;

        v1 = fromArray(v1);
        v2 = fromArray(v2);
        len = v1.length;

        if (len !== v2.length) {
            return false;
        }

        for(i = 0; i < len; i++) {
            t1 = v1[i].isModel ? v1[i].get(valueField) : v1[i];
            t2 = v2[i].isModel ? v2[i].get(valueField) : v2[i];
            if (t1 !== t2) {
                return false;
            }
        }

        return true;
    },

    
    applyEmptyText : function() {
        var me = this,
        emptyText = me.emptyText,
        inputEl, isEmpty;

        if (me.rendered && emptyText) {
            isEmpty = Ext.isEmpty(me.value) && !me.hasFocus;
            inputEl = me.inputEl;
            if (isEmpty) {
                inputEl.dom.value = emptyText;
                inputEl.addCls(me.emptyCls);
                me.listWrapper.addCls(me.emptyCls);
            } else {
                if (inputEl.dom.value === emptyText) {
                    inputEl.dom.value = '';
                }
                me.listWrapper.removeCls(me.emptyCls);
                inputEl.removeCls(me.emptyCls);
            }
            me.autoSize();
        }
    },

    
    preFocus : function(){
        var me = this,
        inputEl = me.inputEl,
        emptyText = me.emptyText,
        isEmpty;

        if (emptyText && inputEl.dom.value === emptyText) {
            inputEl.dom.value = '';
            isEmpty = true;
            inputEl.removeCls(me.emptyCls);
            me.listWrapper.removeCls(me.emptyCls);
        }
        if (me.selectOnFocus || isEmpty) {
            inputEl.dom.select();
        }
    },

    
    onFocus: function() {
        var me = this,
        focusCls = me.focusCls,
        itemList = me.itemList;

        if (focusCls && itemList) {
            itemList.addCls(focusCls);
        }

        me.callParent(arguments);
    },

    
    onBlur: function() {
        var me = this,
        focusCls = me.focusCls,
        itemList = me.itemList;

        if (focusCls && itemList) {
            itemList.removeCls(focusCls);
        }

        me.callParent(arguments);
    },

    
    renderActiveError: function() {
        var me = this,
        invalidCls = me.invalidCls,
        itemList = me.itemList,
        hasError = me.hasActiveError();

        if (invalidCls && itemList) {
            itemList[hasError ? 'addCls' : 'removeCls'](me.invalidCls + '-field');
        }

        me.callParent(arguments);
    },

    
    autoSize: function() {
        var me = this,
        height;

        if (me.grow && me.rendered) {
            me.autoSizing = true;
            me.updateLayout();
        }

        return me;
    },

    
    afterComponentLayout: function() {
        var me = this,
            width;

        if (me.autoSizing) {
            height = me.getHeight();
            if (height !== me.lastInputHeight) {
                if (me.isExpanded) {
                    me.alignPicker();
                }
                me.fireEvent('autosize', me, height);
                me.lastInputHeight = height;
                delete me.autoSizing;
            }
        }
    }
});


Ext.define('Ext.ux.layout.component.field.BoxSelectField', {
    
    alias: ['layout.boxselectfield'],
    extend:  Ext.layout.component.field.Trigger ,

    

    type: 'boxselectfield',

    
    waitForOuterWidthInDom:true,

    beginLayout: function(ownerContext) {
        var me = this,
            owner = me.owner;

        me.callParent(arguments);

        ownerContext.inputElCtContext = ownerContext.getEl('inputElCt');
        owner.inputElCt.setStyle('width','');

        me.skipInputGrowth = !owner.grow || !owner.multiSelect;
    },

    beginLayoutFixed: function(ownerContext, width, suffix) {
        var me = this,
            owner = ownerContext.target;

        owner.triggerEl.setStyle('height', '24px');

        me.callParent(arguments);

        if (ownerContext.heightModel.fixed && ownerContext.lastBox) {
            owner.listWrapper.setStyle('height', ownerContext.lastBox.height+'px');
            owner.itemList.setStyle('height', '100%');
        }
        
    },

    
    publishInnerWidth:function(ownerContext) {
        var me = this,
            owner = me.owner,
            width = owner.itemList.getWidth(true) - 10,
            lastEntry = owner.inputElCt.prev(null, true);

        if (lastEntry && !owner.stacked) {
            lastEntry = Ext.fly(lastEntry);
            width = width - lastEntry.getOffsetsTo(lastEntry.up(''))[0] - lastEntry.getWidth();
        }

        if (!me.skipInputGrowth && (width < 35)) {
            width = width - 10;
        } else if (width < 1) {
            width = 1;
        }
        ownerContext.inputElCtContext.setWidth(width);
    }
});


Ext.define('OPF.core.component.form.BoxSelect', {
    extend:  Ext.ux.form.field.BoxSelect ,
    alias : ['widget.opf-boxselect'],

    cls: 'opf-boxselect-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});
Ext.define('OPF.core.component.form.Checkbox', {
    extend:  Ext.form.field.Checkbox ,
    alias : ['widget.opf-form-checkbox', 'widget.opf-checkbox'],

    cls: 'opf-check-box',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});
Ext.define('OPF.core.component.form.ComboBox', {
    extend:  Ext.form.field.ComboBox ,
    alias : ['widget.opf-form-combo', 'widget.opf-combo'],

    cls: 'opf-combo-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
            isMulti = me.multiSelect,
            hasRecords = selectedRecords.length > 0;


        if (!me.ignoreSelection && me.isExpanded) {
            if (!isMulti && hasRecords) {
                Ext.defer(me.collapse, 1, me);
            }

            if (isMulti || (hasRecords && me.picker)) {
                me.setValue(selectedRecords, false);
            }
            if (hasRecords) {
                me.fireEvent('select', me, selectedRecords);
            }
            me.inputEl.focus();
        }
    },

    onExpand: function() {
        this.callParent();
        var picker = this.getPicker();
        if (picker && picker.pagingToolbar) {
            picker.pagingToolbar.doLayout();
        }
    },

    setDisplayField: function(displayField) {
        this.displayField = displayField;
        this.displayTpl = new Ext.XTemplate(
            '<tpl for=".">',
                '{[typeof values === "string" ? values : values["' + this.displayField + '"]]}',
                '<tpl if="xindex < xcount">' + this.delimiter + '</tpl>',
            '</tpl>'
        );
    }

});
Ext.define('OPF.core.component.form.Date', {
    extend:  Ext.form.field.Date ,
    alias : ['widget.opf-form-date', 'widget.opf-date'],

    cls: 'opf-date-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        var me = this,
            format = Ext.String.format,
            clearTime = Ext.Date.clearTime,
            errors = this.getValidatorErrors(value),
            disabledDays = me.disabledDays,
            disabledDatesRE = me.disabledDatesRE,
            minValue = me.minValue,
            maxValue = me.maxValue,
            len = disabledDays ? disabledDays.length : 0,
            i = 0,
            svalue,
            fvalue,
            day,
            time;

        value = me.formatDate(value || me.processRawValue(me.getRawValue()));

        if (value === null || value.length < 1) {
             return errors;
        }

        svalue = value;
        value = me.parseDate(value);
        if (!value) {
            errors.push(format(me.invalidText, svalue, me.format));
            return errors;
        }

        time = value.getTime();
        if (minValue && time < clearTime(minValue).getTime()) {
            errors.push(format(me.minText, me.formatDate(minValue)));
        }

        if (maxValue && time > clearTime(maxValue).getTime()) {
            errors.push(format(me.maxText, me.formatDate(maxValue)));
        }

        if (disabledDays) {
            day = value.getDay();

            for(; i < len; i++) {
                if (day === disabledDays[i]) {
                    errors.push(me.disabledDaysText);
                    break;
                }
            }
        }

        fvalue = me.formatDate(value);
        if (disabledDatesRE && disabledDatesRE.test(fvalue)) {
            errors.push(format(me.disabledDatesText, fvalue));
        }

        return errors;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    }

});


Ext.define('OPF.core.component.form.FieldContainer', {
    extend:  Ext.form.FieldContainer ,
    alias : ['widget.opf-form-fieldcontainer', 'widget.opf-fieldcontainer'],

    cls: 'opf-field-container',

    mixins: {
        field: Ext.form.field.Field ,
        subLabelable:  OPF.core.component.form.SubLabelable 
    },

    
    combineErrors: true,
    
    layout: 'hbox',

    readOnly: false,

    validateOnBlur: true,

    findById: function(id) {
        var me = this;
        var items = me.items.items.slice();
        var item = null;
        for (var i = 0; i < items.length; i++) {
            if (items[i].id == id || items[i].itemId == id) {
                item = items[i];
                break;
            }
        }
        return item;
    }


});


Ext.define('OPF.core.component.form.DateTime', {
    extend:  OPF.core.component.form.FieldContainer ,
    alias: ['widget.opf-form-datetime', 'widget.opf-datetime'],

    
    dateFormat: 'Y-m-d',
    
    timeFormat: 'H:i:s',
    
    dateTimeFormat: 'Y-m-d H:i:s',
    
    dateConfig:{},
    
    timeConfig:{},

    
    dateValue: null, 
    
    dateField: null,
    
    timeField: null,

    initComponent: function() {
        var me = this;
        me.items = me.items || [];

        me.dateField = Ext.create('Ext.form.field.Date', Ext.apply({
            format: me.dateFormat,
            flex: 1,
            submitValue: false,
            margin: '0 5 0 0'
        }, me.dateConfig));
        me.items.push(me.dateField);

        me.timeField = Ext.create('Ext.form.field.Time', Ext.apply({
            format: me.timeFormat,
            flex: 1,
            submitValue: false
        }, me.timeConfig));
        me.items.push(me.timeField);

        for (var i = 0; i < me.items.length; i++) {
            me.items[i].on('change', Ext.bind(me.validateOnChange, me));
            me.items[i].on('focus', Ext.bind(me.onItemFocus, me));
            me.items[i].on('blur', Ext.bind(me.onItemBlur, me));
            me.items[i].on('specialkey', function(field, event){
                var key = event.getKey(),
                    tab = key == event.TAB;

                if (tab && me.focussedItem == me.dateField) {
                    event.stopEvent();
                    me.timeField.focus();
                    return;
                }

                me.fireEvent('specialkey', field, event);
            });
        }

        me.callParent();








    },

    focus: function(){
        this.callParent();
        this.dateField.focus();
    },

    onBlur: function() {
        this.validateOnChange();
    },

    onItemFocus: function(item){
        if (this.blurTask) this.blurTask.cancel();
        this.focussedItem = item;
    },

    onItemBlur: function(item){
        var me = this;
        if (item != me.focussedItem) return;
        
        me.blurTask = new Ext.util.DelayedTask(function(){
            me.fireEvent('blur', me);
            me.onBlur();
        });
        me.blurTask.delay(100);
    },

    validateOnChange: function() {
        if (this.validateOnBlur) {
            this.validate();
        }
    },

    getValue: function() {
        var value = null, date = this.dateField.getSubmitValue(), time = this.timeField.getSubmitValue();

        if (date) {
            if (time) {
                var format = this.getFormat();
                value = Ext.Date.parse(date + ' ' + time, format);
            } else {
                value = this.dateField.getValue();
            }
        }
        return value;
    },

    getSubmitValue: function(){
        var value = this.getValue();
        return value ? Ext.Date.format(value, this.dateTimeFormat) : null;
    },

    setValue: function(value){
        if (Ext.isString(value)) {
            value = Ext.Date.parse(value, this.dateTimeFormat);
        }
        if (OPF.isNotEmpty(value) && !isNaN(value.getTime())) {
            this.dateField.setValue(value);
            this.timeField.setValue(value);
        }
    },

    getFormat: function(){
        return (this.dateField.submitFormat || this.dateField.format) + " " + (this.timeField.submitFormat || this.timeField.format);
    },

    
    getSubmitData: function(){
        var me = this,
        data = null;
        if (!me.disabled && me.submitValue && !me.isFileUpload()) {
            data = {};
            data[me.getName()] = '' + me.getSubmitValue();
        }
        return data;
    },

    getRawValue: function() {
        return this.getValue();
    },

    processRawValue: function(rawValue) {
        return rawValue;
    },

    getErrors: function(value) {
        var errors = [];

        value = value || this.getValue();

        if(Ext.isFunction(this.validator)){
            var msg = this.validator(value);
            if (OPF.isNotBlank(msg)) {
                errors.push(msg);
            }
        }

        return errors;
    },

    isValid : function() {
        var me = this;
        return me.disabled || me.validateValue(me.processRawValue(me.getRawValue()));
    },

    validateValue: function(value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);
        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }

        return isValid;
    },

    markInvalid : function(errors) {
        
        var me = this,
            oldMsg = me.getActiveError();
        me.setActiveErrors(Ext.Array.from(errors));
        if (oldMsg !== me.getActiveError()) {
            me.doComponentLayout();
        }
    },

    clearInvalid : function() {
        
        var me = this,
            hadError = me.hasActiveError();
        me.unsetActiveError();
        if (hadError) {
            me.doComponentLayout();
        }
    }
});

Ext.define('OPF.core.component.form.Display', {
    extend:  Ext.form.field.Display ,
    alias : ['widget.opf-form-display', 'widget.opf-display'],

    cls: 'opf-display',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    }

});
Ext.define('OPF.core.component.form.File', {
    extend:  Ext.form.field.File ,
    alias: ['widget.opf-form-file', 'widget.opf-file'],

    cls: 'opf-text-field',

    allowBlank: false,
    msgTarget: 'side',
    emptyText: 'Select a file to upload',
    buttonConfig: {
        text: '',
        iconCls: 'upload-icon',
        cls: 'upload-btn',
        height: 28,
        width: 28
    },

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.regex = new RegExp('.*?\\.(' + this.fileTypes.join('|') + ')', 'i');
        this.regexText = 'Uploaded file has wrong extension.';

        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});
Ext.define('OPF.core.component.form.Text', {
    extend:  Ext.form.field.Text ,
    alias : ['widget.opf-form-text', 'widget.opf-text', 'widget.opf-textfield'],

    cls: 'opf-text-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});
Ext.define('OPF.core.component.form.Hidden', {
    extend:  OPF.core.component.form.Text ,
    alias : ['widget.opf-form-hidden', 'widget.opf-hidden'],

    
    inputType : 'hidden',
    hideLabel: true,

    initComponent: function(){
        this.formItemCls += '-hidden';
        this.callParent();
    },

    
    isEqual: function(value1, value2) {
        return this.isEqualAsString(value1, value2);
    },

    
    
    setSize : Ext.emptyFn,
    setWidth : Ext.emptyFn,
    setHeight : Ext.emptyFn,
    setPosition : Ext.emptyFn,
    setPagePosition : Ext.emptyFn

});
Ext.define('OPF.core.component.form.HtmlEditor', {
    extend:  Ext.form.field.HtmlEditor ,
    alias : ['widget.opf-form-htmleditor', 'widget.opf-htmleditor'],

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    processRawValue: function(value) {
        return value || '';
    },

    getRawValue: function() {
        return this.getValue();
    }

});

Ext.define('OPF.core.component.form.Number', {
    extend:  Ext.form.field.Number ,
    alias: ['widget.opf-form-number', 'widget.opf-number'],

    currencySymbol: null,
    useThousandSeparator: true,
    thousandSeparator: ',',
    alwaysDisplayDecimals: false,
    cls: 'opf-number-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        if (this.useThousandSeparator && this.decimalSeparator == ',' && this.thousandSeparator == ',')
            this.thousandSeparator = '.';
        else
        if (this.allowDecimals && this.thousandSeparator == '.' && this.decimalSeparator == '.')
            this.decimalSeparator = ',';

        this.initSubLabelable();
        this.callParent(arguments);
    },

    getErrors: function(value) {
        var number = this.removeFormat(value);
        return this.getValidatorErrors(number);
    },

    setValue: function(value) {
        OPF.core.component.form.Number.superclass.setValue.call(this, value != null ? value.toString().replace('.', this.decimalSeparator) : value);

        this.setRawValue(this.getFormattedValue(this.getValue()));
    },

    getFormattedValue: function(value) {
        return OPF.core.component.form.Number.formattedValue(value, this);
    },

    
    parseValue: function(value) {
        
        return OPF.core.component.form.Number.superclass.parseValue.call(this, this.removeFormat(value));
    },

    
    removeFormat: function(value) {
        if (Ext.isEmpty(value)) {
            return null;
        } else if (!this.hasFormat())
            return value;
        else {
            value = value.toString().replace(this.currencySymbol + ' ', '');

            value = this.useThousandSeparator ? value.replace(new RegExp('[' + this.thousandSeparator + ']', 'g'), '') : value;

            return value;
        }
    },

    hasFormat: function() {
        return OPF.core.component.form.Number.hasFormat(this.getRawValue(), this);
    },

    
    onFocus: function() {
        this.setRawValue(this.removeFormat(this.getRawValue()));

        this.callParent(arguments);
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    }
});

OPF.core.component.form.Number.formattedValue = function(value, cfg) {
    if (Ext.isEmpty(value) || !OPF.core.component.form.Number.hasFormat(value, cfg))
        return value;
    else {
        var neg = null;

        value = (neg = value < 0) ? value * -1 : value;
        value = cfg.allowDecimals && cfg.alwaysDisplayDecimals ? value.toFixed(cfg.decimalPrecision) : value;

        if (cfg.useThousandSeparator) {
            if (cfg.useThousandSeparator && Ext.isEmpty(cfg.thousandSeparator))
                throw ('NumberFormatException: invalid thousandSeparator, property must has a valid character.');

            if (cfg.thousandSeparator == cfg.decimalSeparator)
                throw ('NumberFormatException: invalid thousandSeparator, thousand separator must be different from decimalSeparator.');

            value = value.toString();

            var ps = value.split('.');
            ps[1] = ps[1] ? ps[1] : null;

            var whole = ps[0];

            var r = /(\d+)(\d{3})/;

            var ts = cfg.thousandSeparator;

            while (r.test(whole))
                whole = whole.replace(r, '$1' + ts + '$2');

            value = whole + (ps[1] ? cfg.decimalSeparator + ps[1] : '');
        }

        return Ext.String.format('{0}{1}{2}', (neg ? '-' : ''), (Ext.isEmpty(cfg.currencySymbol) ? '' : cfg.currencySymbol + ' '), value);
    }
};

OPF.core.component.form.Number.hasFormat = function(rawValue, cfg) {
    return cfg.decimalSeparator != '.' || (cfg.useThousandSeparator && rawValue != null) || !Ext.isEmpty(cfg.currencySymbol) || cfg.alwaysDisplayDecimals;
};

Ext.define('OPF.core.component.form.Rating', {
    extend:  Ext.form.field.Base ,
    alias : ['widget.opf-form-rating', 'widget.opf-rating'],

    cls: 'opf-rating-field',

    ratingPoints: 10,
    ratingImageUrl: null,
    ratingImageHeight: 32,
    ratingImageWidth: 32,
    ratingImagePadding: {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0
    },

    fieldSubTpl: [
        '<input id="{id}" type="hidden" {inputAttrTpl}',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
        '/>',
        '<div class="rating-column" style="position: relative">',
            '<div class="rating-points" style="height: {ratingHeight}px; width: {ratingWidth}px; background: url({ratingImageUrl}) repeat-x 0 0;"></div>',
            '<div class="rating-active" style="position: absolute; top: 0; left: 0; height: {ratingHeight}px; width: {ratingActiveWidth}px; background: url({ratingImageUrl}) repeat scroll left ',
                '<tpl if="isSelectedRating">center<tpl else>bottom</tpl>',
                ' transparent;"></div>',
        '</div>',
        {
            disableFormats: true
        }
    ],

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    selectedRating: null,
    isSelectedRating: null,

    initComponent: function() {
        this.addEvents(
            'ratingclick'
        );

        this.initSubLabelable();
        this.callParent();

        this.resetTask = new Ext.util.DelayedTask(function() {
            this.resetSelectedRating(false);
        }, this);
    },

    getSubTplData: function() {
        var data = this.callParent();

        this.isSelectedRating = true;
        var value = Ext.isNumeric(this.getSelectedRating()) ? parseFloat(this.getSelectedRating()) : null;
        if (!value) {
            value = Ext.isNumeric(this.getValue()) ? parseFloat(this.getValue()) : 0;
            this.isSelectedRating = false;
        }

        var ratingActiveWidth = this.calculateWidth(value);

        var ratingData = {
            isSelectedRating: this.isSelectedRating,
            ratingHeight: this.ratingImageHeight,
            ratingWidth: this.ratingImageWidth * this.ratingPoints,
            ratingImageUrl: this.ratingImageUrl,
            ratingActiveWidth: ratingActiveWidth,
            tooltip: 'Rating: ' + Math.round(value * 100) / 100
        };

        return Ext.apply(data, ratingData);
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    afterRender: function(container) {
        this.callParent(arguments);

        this.initRatingMouseEvents();
    },

    initRatingMouseEvents: function() {
        this.ratingPointsEl = this.getEl().down('.rating-points');
        this.ratingActiveEl = this.getEl().down('.rating-active');

        if (!this.isSelectedRating && !this.readOnly) {
            this.ratingPointsEl.on({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });

            this.ratingActiveEl.on({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });
        }
    },

    onClick: function() {
        if (this.fireEvent('ratingclick', this, this.getValue(), this.selectedRating) !== false) {
            this.oldValue = this.getValue();
            this.setValue(this.selectedRating);
            this.isSelectedRating = true;

            this.ratingPointsEl.un({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });

            this.ratingActiveEl.un({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });
        }
    },

    onMouseOver: function(event, tag) {
        this.resetTask.cancel();
        var tagXY = Ext.fly(tag).getXY();
        var mouseXY = [event.browserEvent.clientX, event.browserEvent.clientY];
        var width = (mouseXY[0] - tagXY[0]);
        this.selectedRating = this.calculateValue(width);
    },

    onMouseOut: function() {
        this.resetTask.delay(100);
    },

    getSelectedRating: function() {
        return this.selectedRating;
    },

    setSelectedRating: function(value) {
        this.selectedRating = value;
        this.isSelectedRating = true;

        var ratingActiveWidth = this.calculateWidth(value);
        this.ratingActiveEl.setWidth(ratingActiveWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left center'
        });
    },

    refreshRating: function() {
        var value = Ext.isNumeric(this.getValue()) ? parseFloat(this.getValue()) : 0;
        var ratingActiveWidth = this.calculateWidth(value);
        this.ratingActiveEl.setWidth(ratingActiveWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left bottom'
        });
    },

    calculateValue: function(width) {
        var ratingWidth = this.ratingImageWidth * this.ratingPoints;
        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);

        var intPoints = Math.ceil(width / this.ratingImageWidth);
        var selectedPointsWidth = intPoints * this.ratingImageWidth;
        var piecePointWidth = width - selectedPointsWidth;
        if (piecePointWidth > this.ratingImagePadding.left) {
            selectedPointsWidth += this.ratingImageWidth;
            intPoints++;
        }
        this.ratingActiveEl.setWidth(selectedPointsWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left center'
        });
        return intPoints;
    },

    calculateWidth: function(value) {
        var intValue = Math.floor(value);
        var decValue = value - Math.floor(value);

        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);
        return Math.round(this.ratingImageWidth * intValue + this.ratingImagePadding.left + pointWidth * decValue);
    },

    resetSelectedRating: function(force) {
        if (!this.isSelectedRating || force) {
            this.isSelectedRating = false;
            this.refreshRating();
        }
    },

    reset: function() {
        this.setValue(this.oldValue);
        this.resetSelectedRating(true);
        this.initRatingMouseEvents();
    }

});


Ext.define('OPF.console.domain.view.ReferenceHtmlEditor', {
    extend:  Ext.form.field.HtmlEditor ,
    alias: 'widget.opf-reference-htmleditor',

    height: 28,
    cls: 'opf-reference-htmleditor',

    enableFormat : false,
    enableFontSize : false,
    enableColors : false,
    enableAlignments : false,
    enableLists : false,
    enableSourceEdit : false,
    enableLinks : false,
    enableFont : false,
    enableKeyEvents: true,

    keyUpFn: Ext.emptyFn(),

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable 
    },

    listeners: {
        afterrender: function(editor) {
            var formPanelDropTargetEl = editor.iframeEl.dom;

            var formPanelDropTarget = Ext.create('Ext.dd.DropTarget', formPanelDropTargetEl, {
                ddGroup: 'fieldGridDDGroup',
                notifyEnter: function(ddSource, e, data) {
                    editor.iframeEl.stopAnimation();
                    editor.iframeEl.highlight();
                },
                notifyDrop  : function(ddSource, e, data) {

                    var record = data.records[0];

                    var fieldId = record.data.id;
                    var fieldName = record.data.name;
                    var imageData = OPF.textToImage(fieldName, '21px Tahoma');

                    var fieldData = {
                        fieldId: fieldId,
                        fieldName: fieldName
                    };
                    var fieldDataStr = Ext.JSON.encode(fieldData).replace(/"/g, '\'');

                    editor.activated = true;
                    editor.insertAtCursor('<img ' +
                            'src="' + imageData.base64Src + '" ' +
                            'width="' + imageData.width + '" ' +
                            'height="' + imageData.height + '" ' +
                            'style="vertical-align: middle;" ' +
                            'data="' + fieldDataStr + '"' +
                        '/>');
                    editor.keyUpFn();

                    return true;
                }
            });
        }
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.listeners = cfg.listeners || {};
        this.keyUpFn = cfg.listeners.keyup;
        cfg.listeners = Ext.apply(cfg.listeners, cfg.listeners, this.listeners);
        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    initEditor : function() {
        this.callParent(arguments);

        var me = this;
        var doc = me.getDoc();

        if (Ext.isGecko) {
            Ext.EventManager.on(doc, 'keyup', me.keyUpFn, me);
        }

        if (Ext.isIE || Ext.isWebKit || Ext.isOpera) {
            Ext.EventManager.on(doc, 'keyup', me.keyUpFn, me);
        }
    },

    getHtmlValue: function() {
        var me = this,
            value;
        if (!me.sourceEditMode) {
            me.syncValue();
        }
        value = me.rendered ? me.textareaEl.dom.value : me.value;
        me.value = value;
        return value;
    },

    getValue : function() {
        var value = this.callParent(arguments);
        if (OPF.isNotBlank(value)) {
            value = value.replace(/\u200B/, '');
            var re = /(<img)[^<>]*?src=[^<>]*?(data="\{[^{}]*?\}")[^<>]*?(>)/g;
            while(value.search(re) != -1) {
                value = value.replace(re, '$1 $2 $3');
            }
            value = value.replace(/<\/?br\/?>/g, '');
        }
        return value;
    },

    setValue: function(value) {
        if (OPF.isNotBlank(value)) {
            var re = /<img[^<>"]*?data="(\{[^{}]*?\})"[^<>]*?>/i;
            while(value.search(re) != -1) {
                var matchValues = value.match(re);
                var imgTag = matchValues[0];
                var imgTagData = matchValues[1];
                var fieldName = imgTagData.match(/fieldname['"]\s*:\s*['"]([^"]*?)['"]/i)[1];
                var imageData = OPF.textToImage(fieldName, '21px Tahoma');
                var imageHtml =
                    '<img ' +
                        'src="' + imageData.base64Src + '" ' +
                        'width="' + imageData.width + '" ' +
                        'height="' + imageData.height + '" ' +
                        'style="vertical-align: middle;" ' +
                        'data="' + imgTagData + '"' +
                    '/>';
                value = value.replace(imgTag, imageHtml);
            }
        }
        return this.callParent(arguments);
    }

});


Ext.define('OPF.core.component.form.SearchComboBox', {
    extend:  OPF.core.component.form.FieldContainer ,
    alias: ['widget.opf-searchcombo'],

    layout: 'anchor',

    name: null,          
    fieldLabel: null,    
    subFieldLabel: null, 
    multiSelect: true,

    readOnly: false,

    model: null,
    validator: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        this.addEvents(
            'onBeforeStoreLoad'
        );
        OPF.core.component.form.SearchComboBox.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.initializedModel = Ext.create(this.model);

        this.selectedItemsStore = Ext.create('Ext.data.Store', {
            model: this.model
        });

        var tpl;
        var searchFields;
        if (OPF.isNotEmpty(this.initializedModel.self.template)) {
            tpl = this.initializedModel.self.template.join('');
            searchFields = this.getFieldsFromTemplate();
        } else {
            tpl = '{' + this.initializedModel.displayProperty + '}';
            var field = me.findModelField(this.initializedModel, this.initializedModel.displayProperty);
            searchFields = [field];
        }
        var selectedItemTpl = new Ext.XTemplate(

            	'<ul class="x-boxselect-list">',
                    '<tpl for=".">',
                        '<li class="x-boxselect-item item-wrap">',
                            '<div class="x-boxselect-item-text">',
                                tpl,
                            '</div>',

                            '<tpl if="this.allowToDelete()">',
                                '<div class="x-tab-close-btn x-boxselect-item-close"></div>',
                            '</tpl>',

                        '</li>',
                    '</tpl>',
                '</ul>',

            {
                disableFormats: true,
                
                allowToDelete: function(){
                   return !me.readOnly;
                }
            }
        );

        this.selectedItemsView = Ext.create('Ext.view.View', {
            store: this.selectedItemsStore,
            tpl: selectedItemTpl,
            itemSelector: 'li.item-wrap',
            readOnly: this.readOnly,
            listeners: {
                itemclick: function (view, record, el, i, e) {
                    if (e.getTarget('.x-boxselect-item-close')) {
                        me.removeSelectSearchedItem(record);
                    }
                }
            }
        });

        this.searchComboStore = Ext.create('Ext.data.Store', {
            model: this.model,
            proxy: {
                type: 'ajax',
                url : this.initializedModel.self.restSuffixUrl + '/advanced-search',
                reader: {
                    type: 'json',
                    root: 'data'
                },
                startParam: 'offset',
                limitParam: 'limit'
            },
            pageSize: 10,
            autoLoad: false,
            listeners: {
                beforeload: function(store) {
                    var searchTerm = me.searchCombo.getRawValue();
                    me.fireEvent('onBeforeStoreLoad', me, store, searchTerm);
                }
            }
        });

        var comboValidator = OPF.isNotEmpty(this.validator) && Ext.isFunction(this.validator) ? this.validator : null;
        var searchComboConfig = {
            store: this.searchComboStore,
            excludeStore: this.selectedItemsStore,
            cls: 'opf-combo-field',
            anchor: '100%',
            valueField: this.initializedModel.idProperty,
            displayField: this.initializedModel.displayProperty,
            searchFields: searchFields,
            queryMode: 'remote',
            queryCaching: false,
            typeAhead: true,
            typeAheadDelay: 250,
            getParams: this.getParams,
            forceSelection: false,
            triggerAction: 'query',
            minChars: 0,

            pageSize: 10,
            listeners: {
                select: this.selectSearchedItem,
                scope: this
            },
            listConfig: {
                loadingText: 'Searching...',
                emptyText: 'No matching items found.',
                getInnerTpl: function() {
                    return tpl;
                }
            },
            hidden: this.readOnly,
            validator: comboValidator
        };

        this.searchCombo = Ext.create('OPF.core.component.form.ComboBox', searchComboConfig);

        this.items = [
            this.selectedItemsView,
            this.searchCombo
        ];

        me.callParent();
    },

    getParams: function (queryString) {
        var me = this;

        var excludeIds = [];
        var excludeRecords = this.excludeStore.getRange();
        Ext.each(excludeRecords, function(excludeRecord) {
            excludeIds.push(excludeRecord.get(me.valueField));
        });

        var queryParameters = [];
        Ext.each(this.searchFields, function(searchField) {
            if (searchField.type.type == 'string') {
                var queryParam = [];
                if (OPF.isNotBlank(queryString)) {
                    queryParam.push({
                        field: searchField.name,
                        operation: 'LIKE',
                        value: queryString
                    });
                    if (excludeIds.length > 0) {
                        queryParam.push({
                            field: me.valueField,
                            operation: 'NOTIN',
                            value: excludeIds
                        });
                    }
                    queryParameters.push(queryParam);
                }
            }
        });

        if (queryParameters.length == 0 && excludeIds.length > 0) {
            queryParameters.push([
                {
                    field: me.valueField,
                    operation: 'NOTIN',
                    value: excludeIds
                }
            ])
        }

        return {
            queryParameters: Ext.encode(queryParameters)
        };
    },

    selectSearchedItem: function(combo, records, eOpts) {
        var me = this;
        Ext.Array.each(records, function(record) {
            if (!me.multiSelect) {
                me.selectedItemsStore.removeAll();
            }
            me.selectedItemsStore.add(record);
        });
        combo.clearValue();
    },

    removeSelectSearchedItem: function(record) {
        this.selectedItemsStore.remove(record);
    },

    getValue: function() {
        var me = this;
        var values = [];
        var records = this.selectedItemsStore.getRange();
        Ext.Array.each(records, function(record) {
            values.push(record.get(me.initializedModel.idProperty));
        });
        return values;
    },

    getSubmitValue: function(){
        return this.getValue();
    },

    getSubmitData: function(){
        var me = this,
        data = null;
        if (!me.disabled && me.submitValue && !me.isFileUpload()) {
            data = {};
            data[me.getName()] = me.getSubmitValue();
        }
        return data;
    },

    setValue: function(records) {
        this.selectedItemsStore.removeAll();
        if (records) {
            this.selectedItemsStore.add(records);
        }
    },

    setReadOnly: function(readOnly) {
        var me = this,
            old = me.readOnly;

        if (readOnly != old) {
            me.readOnly = readOnly;
            this.searchCombo.setVisible(!readOnly);
            this.selectedItemsView.readOnly = readOnly;
            this.selectedItemsView.refresh();
        }
    },


    getFieldsFromTemplate: function() {
        var me = this;

        var template = this.initializedModel.self.template[0];
        var fields = [];
        var matches = template.match(/\{[\w\.]+\}/gi);
        Ext.each(matches, function(match) {
            var fieldName = match.substring(1, match.length - 1);
            var field = me.findModelField(me.initializedModel, fieldName);
            if (OPF.isNotEmpty(field)) {
                fields.push(field);
            }
        });
        return fields;
    },

    findModelField: function(model, fieldName) {
        var foundField = null;
        Ext.each(model.fields.items, function(field) {
            if (field.name == fieldName) {
                foundField = field;
            }
        });
        return foundField;
    }

});
Ext.define('OPF.core.component.form.TextArea', {
    extend:  Ext.form.field.TextArea ,
    alias : ['widget.opf-form-textarea', 'widget.opf-textarea'],

    cls: 'opf-text-area',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    }

});
Ext.define('OPF.core.component.form.Time', {
    extend:  Ext.form.field.Time ,
    alias : ['widget.opf-form-time', 'widget.opf-time'],

    cls: 'opf-time-field',

    mixins: {
        subLabelable:  OPF.core.component.form.SubLabelable ,
        errorHandler:  OPF.core.component.form.ErrorHandler 
    },

    initComponent: function() {
        this.initSubLabelable();
        this.callParent();
    },

    getErrors: function(value) {
        var me = this,
            format = Ext.String.format,
            errors = this.getValidatorErrors(value),
            minValue = me.minValue,
            maxValue = me.maxValue,
            date;

        value = me.formatDate(value || me.processRawValue(me.getRawValue()));

        if (value === null || value.length < 1) { 
             return errors;
        }

        date = me.parseDate(value);
        if (!date) {
            errors.push(format(me.invalidText, value, me.format));
            return errors;
        }

        if (minValue && date < minValue) {
            errors.push(format(me.minText, me.formatDate(minValue)));
        }

        if (maxValue && date > maxValue) {
            errors.push(format(me.maxText, me.formatDate(maxValue)));
        }

        return errors;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    }

});



Ext.define('OPF.core.component.grid.Column', {
    extend:  Ext.grid.column.Column ,
    alias: 'widget.opf-column',

    

    displayField: 'name',

    statics: {
        renderer: function (value, metadata, record, rowIndex, colIndex, store, view) {
            
            
            
            var column = view.headerCt.getGridColumns()[colIndex];
            var associations = store.model.prototype.associations;
            var associationConfig = associations.get(column.dataIndex);
            if (associationConfig && record[associationConfig.instanceName]) {
                var obj = record[associationConfig.instanceName];

                var fkName = OPF.ModelHelper.getProperty(obj, column.displayField);
                if (fkName || !column.alternatePropertyName) {
                    return fkName;
                }
            }

            if (column.alternatePropertyName) {
                return record.get(column.alternatePropertyName);
            } else {
                return value;
            }
        }
    },

    renderer: function(value, metadata, record, rowIndex, colIndex, store, view) {
        return OPF.core.component.grid.Column.renderer(value, metadata, record, rowIndex, colIndex, store, view);
    },

    
    getSortParam: function() {
        var sortParam = this.dataIndex;
        if (this.displayField) {
            sortParam += '.' + this.displayField;
        }
        return sortParam;
    }

});



Ext.define('OPF.core.component.grid.RatingColumn', {
    extend:  Ext.grid.column.Column ,
    alias: 'widget.opf-rating-column',

    ratingPoints: 10,
    ratingImageUrl: null,
    ratingImageHeight: 32,
    ratingImageWidth: 32,
    ratingImagePadding: {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0
    },

    tpl: new Ext.XTemplate(
        '<div class="rating-column" style="position: relative" data-qtip="{tooltip}">',
            '<div class="rating-points" style="height: {ratingHeight}px; width: {ratingWidth}px; background: url({ratingImageUrl}) repeat-x 0 0;"></div>',
            '<div class="rating-active" style="position: absolute; top: 0; left: 0; height: {ratingHeight}px; width: {ratingActiveWidth}px; background: url({ratingImageUrl}) repeat scroll left bottom transparent;"></div>',
        '</div>'
    ),

    initComponent: function() {
        var me = this;

        me.hasCustomRenderer = true;
        me.callParent(arguments);
    },

    defaultRenderer: function(value, meta, record) {
        var intValue = Math.floor(value);
        var decValue = value - Math.floor(value);

        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);
        var ratingActiveWidth = Math.round(this.ratingImageWidth * intValue + this.ratingImagePadding.left + pointWidth * decValue);

        var data = {
            ratingHeight: this.ratingImageHeight,
            ratingWidth: this.ratingImageWidth * this.ratingPoints,
            ratingImageUrl: this.ratingImageUrl,
            ratingActiveWidth: ratingActiveWidth,
            tooltip: 'Rating: ' + Math.round(value * 100) / 100
        };
        return this.tpl.apply(data);
    }
});


Ext.define('OPF.core.component.grid.RowToolbarGridPlugin', {
    extend:  Ext.AbstractPlugin ,
    alias: 'widget.opf-row-toolbar-grid-plugin',

    buttons: [],

    showBtnWidth: 39,
    showBtnHeight: 60,

    init: function(grid) {
        var me = this;

        grid.on('itemmouseenter', this.gridItemMouseEnter);
        grid.on('itemmouseleave', this.gridItemMouseLeave);

        grid.buttons = this.buttons;
        Ext.each(this.buttons, function(button) {
            grid[button.name + 'Fn'] = me[button.name + 'Fn'];
        });
        grid.showFn = this.showFn;
        grid.showBtnWidth = this.showBtnWidth;
        grid.showBtnHeight = this.showBtnHeight;
        grid.scope = this.scope;
    },

    gridItemMouseEnter: function(view, record, item, index, e) {
        var me = this;

        view.rowElement = Ext.get(item);
        view.rowElement.setStyle('position', 'relative');
        view.currentRecord = record;

        if (OPF.isEmpty(view.rowCmp)) {
            view.buttons = view.buttons || view.ownerCt.buttons;
            Ext.each(view.buttons, function(button) {
                view[button.name + 'RecordButton'] = Ext.create('Ext.button.Button', {
                    tooltip: button.tooltip || Ext.String.capitalize(button.name),
                    ui: 'action',
                    width: button.width || 26,
                    height: button.height || 26,
                    iconCls: button.iconCls || (button.name + '-btn'),
                    hidden: true
                });
            });

            view.showRowToolbarButton = Ext.create('Ext.button.Button', {
                text: '>>',
                width: this.showBtnWidth,
                height: this.showBtnHeight,
                handler: this.showFn,
                scope: view,
                hideMode: 'visibility'
            });

            var buttons = [
                view.showRowToolbarButton
            ];
            Ext.each(view.buttons, function(button) {
                buttons.push(view[button.name + 'RecordButton']);
            });

            view.rowCmp = Ext.create('Ext.container.Container', {
                cls: 'row-action-toolbar',
                renderTo: view.rowElement,
                items: buttons
            });
        } else {
            view.rowCmp.getEl().appendTo(view.rowElement);
        }
        var rowSize = view.rowElement.getSize();
        view.rowCmp.show();
        view.rowCmp.setHeight(rowSize.height);
        view.rowCmp.setPosition(0, rowSize.height * index);

        Ext.each(view.buttons, function(button) {
            view[button.name + 'RecordButton'].on('click', me[button.name + 'Fn'], me.scope, [view, record, index]);
        });
    },

    gridItemMouseLeave: function(view, record, item, index, e) {
        var me = this;
        if (view.rowCmp) {
            view.rowCmp.setWidth(view.showRowToolbarButton.getWidth());
            view.rowCmp.hide();
            view.showRowToolbarButton.show();

            Ext.each(view.buttons, function(button) {
                view[button.name + 'RecordButton'].hide();
                view[button.name + 'RecordButton'].un('click', me[button.name + 'Fn'], me.scope);
            });
        }
    },

    showFn: function() {
        var me = this;

        var rowSize = this.rowElement.getSize();
        this.rowCmp.setWidth(rowSize.width);
        this.showRowToolbarButton.hide();

        Ext.each(this.buttons, function(button) {
            me[button.name + 'RecordButton'].show();
        });
    }

});




Ext.define('OPF.core.component.htmleditor.SimpleHtmlEditor', {
    extend:  Ext.form.field.HtmlEditor ,
    alias: 'widget.simplehtmleditor',

    enableAlignments: false,
    enableColors: false,
    enableFont: false,
    enableFontSize: false,
    enableLinks: false,

    sourceEditMode: true,


    initComponent: function() {
        this.plugins = OPF.core.component.htmleditor.plugins.HtmlEditorPlugins.plugins();

        this.callParent(arguments);
    },











    getValue: function() {
        var value = OPF.core.component.htmleditor.SimpleHtmlEditor.superclass.getValue.call(this);
        value = value.replace(/<link[^<>]*?\/?>/g, '');
        return value;
    },

    setValue: function(value) {
        value = '<link type="text/css" rel="stylesheet" href="' + OPF.Cfg.OPF_CONSOLE_URL + '/css/simple-html-editor.css">' + value;
        OPF.core.component.htmleditor.SimpleHtmlEditor.superclass.setValue.call(this, value);
    }

});




Ext.define('OPF.core.component.htmleditor.plugins.SpecialHeadingFormats', {
    extend:  Ext.util.Observable ,

    formatInstructions: [
        {
            id: 'normal',
            labelName: 'Normal',
            shortName: 'nor',
            tagName: null,
            className: null
        },
        {
            id: 'h1',
            labelName: 'H1',
            shortName: 'h1',
            tagName: 'H1',
            className: null
        },
        {
            id: 'h2',
            labelName: 'H2',
            shortName: 'h2',
            tagName: 'H2',
            className: null
        },
        {
            id: 'h3',
            labelName: 'H3',
            shortName: 'h3',
            tagName: 'H3',
            className: null
        },
        {
            id: 'quote',
            labelName: 'Quote',
            shortName: 'qot',
            tagName: 'DIV',
            className: 'quote'
        },
        {
            id: 'code',
            labelName: 'Code',
            shortName: 'cod',
            tagName: 'DIV',
            className: 'code'
        },
        {
            id: 'warning',
            labelName: 'Warning',
            shortName: 'wrn',
            tagName: 'SPAN',
            className: 'warn'
        },
        {
            id: 'important',
            labelName: 'Important',
            shortName: 'imp',
            tagName: 'SPAN',
            className: 'important'
        },
        {
            id: 'critical',
            labelName: 'Critical',
            shortName: 'crt',
            tagName: 'SPAN',
            className: 'critical'
        },
        {
            id: 'subdued',
            labelName: 'Subdued',
            shortName: 'sub',
            tagName: 'SPAN',
            className: 'subdued'
        }
    ],

    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },

    
    onRender: function(){
        var instance = this;
        var cmp = this.cmp;
        Ext.each(this.formatInstructions, function(instruction) {
            instance.cmp.getToolbar().add(
                {
                    xtype: 'button',
                    iconCls: 'editor-without-image',
    
                    tooltip: instruction.labelName,
                    text: instruction.shortName,
                    handler: function(combo, rec) {
                        instance.applyFormatStyle(instruction);
                    },
                    scope: cmp
                }
            )
        })
    },

    applyFormatStyle: function(instruction) {
        if (this.cmp.win.getSelection || this.cmp.getDoc().getSelection) {
            
            OPF.Msg.setAlert(true, 'FF');
            var sel = this.cmp.win.getSelection();
            if (!sel) {
                sel = this.cmp.getDoc().getSelection();
            }
            var range = this.cmp.win.getSelection().getRangeAt(0);
            var selDocFrag = range.cloneContents();
            var txt, hasHTML = false;
            Ext.each(selDocFrag.childNodes, function(n){
                if (n.nodeType !== 3) {
                    hasHTML = true;
                }
            });
            if (hasHTML) {
                txt = this.cmp.win.getSelection() + '';
            } else {
                txt = selDocFrag.textContent;
            }

            if (instruction.id == 'normal') {
                range.deleteContents();
                range.insertNode(this.cmp.getDoc().createTextNode(txt));
            } else {

                var node = this.cmp.getDoc().createElement(instruction.tagName);
                node.className = instruction.className;
                node.innerHTML = range;
                range.deleteContents();
                range.insertNode(node);
            }
        } else if (this.cmp.getDoc().selection) {
            
            OPF.Msg.setAlert(true, 'IE');
            this.cmp.win.focus();
            range = this.cmp.getDoc().selection.createRange();
            OPF.Msg.setAlert(true, range.text);
            if (instruction.id == 'normal') {
                range.pasteHTML(this.cmp.getDoc().selection);
            } else {
                var openTag =
                        '<' + instruction.tagName +
                        (instruction.className != null ? ' class="' + instruction.className + '"' : '') + '>';
                var closeTag = '</' + instruction.tagName + '>';
                range.pasteHTML(openTag + range.text + closeTag);
            }
        }
    }
});



Ext.ns('OPF.core.component.htmleditor.plugins.HtmlEditorPlugins');

OPF.core.component.htmleditor.plugins.HtmlEditorPlugins.plugins = function(){
    return [
        new OPF.core.component.htmleditor.plugins.SpecialHeadingFormats()















    ];
};




Ext.define('OPF.core.component.htmleditor.plugins.SpecialHeadingFormat', {
    extend:  Ext.util.Observable ,

    formatInstructions: [
        {
            id: 'normal',
            labelName: 'Normal',
            tagName: null,
            className: null
        },
        {
            id: 'h1',
            labelName: 'H1',
            tagName: 'H1',
            className: null
        },
        {
            id: 'h2',
            labelName: 'H2',
            tagName: 'H2',
            className: null
        },
        {
            id: 'h3',
            labelName: 'H3',
            tagName: 'H3',
            className: null
        },
        {
            id: 'quote',
            labelName: 'Quote',
            tagName: 'DIV',
            className: 'quote'
        },
        {
            id: 'code',
            labelName: 'Code',
            tagName: 'DIV',
            className: 'code'
        },
        {
            id: 'warning',
            labelName: 'Warning',
            tagName: 'SPAN',
            className: 'warn'
        },
        {
            id: 'important',
            labelName: 'Important',
            tagName: 'SPAN',
            className: 'important'
        },
        {
            id: 'critical',
            labelName: 'Critical',
            tagName: 'SPAN',
            className: 'critical'
        },
        {
            id: 'subdued',
            labelName: 'Subdued',
            tagName: 'SPAN',
            className: 'subdued'
        }
    ],

    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    
    onRender: function(){
        var instance = this;
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addItem({
            xtype: 'combo',
            displayField: 'display',
            valueField: 'value',
            name: 'headingsize',
            forceSelection: false,
            mode: 'local',
            selectOnFocus: false,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            width: 65,
            emptyText: 'Heading',
            store: {
                xtype: 'arraystore',
                autoDestroy: true,
                fields: ['value','display'],
                data: [
                    ['normal','Normal'],
                    ['h1','H1'],
                    ['h2','H2'],
                    ['h3','H3'],
                    ['quote','Quote'],
                    ['code','Code'],
                    ['warning','Warning'],
                    ['important','Important'],
                    ['critical','Critical'],
                    ['subdued','Subdued']
                ]
            },
            listeners: {
                'select': function(combo, rec) {
                    instance.applyFormatStyle(rec.get('value'));
                    combo.reset();
                },
                scope: cmp
            }
        });
    },

    applyFormatStyle: function(instructionId) {
        var formatInstruction = null;
        Ext.each(this.formatInstructions, function(instruction, index) {
            if (instruction.id == instructionId) {
                formatInstruction = instruction;    
            }
        });

        if (this.cmp.win.getSelection || this.cmp.getDoc().getSelection) {
            
            OPF.Msg.setAlert(true, 'FF');
            var sel = this.cmp.win.getSelection();
            if (!sel) {
                sel = this.cmp.getDoc().getSelection();
            }
            var range = this.cmp.win.getSelection().getRangeAt(0);
            var selDocFrag = range.cloneContents();
            var txt, hasHTML = false;
            Ext.each(selDocFrag.childNodes, function(n){
                if (n.nodeType !== 3) {
                    hasHTML = true;
                }
            });
            if (hasHTML) {
                txt = this.cmp.win.getSelection() + '';
            } else {
                txt = selDocFrag.textContent;
            }

            if (formatInstruction.id == 'normal') {
                range.deleteContents();
                range.insertNode(this.cmp.getDoc().createTextNode(txt));
            } else {

                var node = this.cmp.getDoc().createElement(formatInstruction.tagName);
                node.className = formatInstruction.className;
                node.innerHTML = range;
                range.deleteContents();
                range.insertNode(node);
            }
        } else if (this.cmp.getDoc().selection) {
            
            OPF.Msg.setAlert(true, 'IE');
            this.cmp.win.focus();
            range = this.cmp.getDoc().selection.createRange();
            OPF.Msg.setAlert(true, range.text);
            if (formatInstruction.id == 'normal') {
                range.pasteHTML(this.cmp.getDoc().selection);
            } else {
                var openTag =
                        '<' + formatInstruction.tagName +
                        (formatInstruction.className != null ? ' class="' + formatInstruction.className + '"' : '') + '>';
                var closeTag = '</' + formatInstruction.tagName + '>';
                range.pasteHTML(openTag + range.text + closeTag);
            }
        }
    }
});



Ext.define('OPF.core.component.NoticeContainer', {
    extend:  Ext.container.Container ,
    alias: 'widget.notice-container',

    cls: 'notice-container',
    hidden: true,
    form: null,
    delay: null,

    activeMessageContainerTpl: [
        '<div class="{containerCls}">',
            '<tpl if="messages && messages.length">',
                '<h4><img src="{imageUrl}" border="0"/>{title}</h4>',
                '<ul><tpl for="messages"><li class="{level.css} <tpl if="xindex == xcount"> last</tpl>">{msg}</li></tpl></ul>',
            '</tpl>',
        '</div>'
    ],

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.delay)) {
            var task = new Ext.util.DelayedTask(function() {
                me.initErrorContainer();
            }, this);
            task.delay(this.delay);
        } else {
            this.initErrorContainer();
        }

        this.items = this.items || [];

        this.callParent(arguments);
    },

    initErrorContainer: function() {
        var me = this;
        if (!this.form) {
            this.form = this.up('form');
        }
        if (this.form) {
            var fields = this.form.getForm().getFields();
            Ext.each(fields.items, function(field) {
                field.on('errorchange', me.showErrors, me);
            });
        }
    },

    showInfo: function(level, msg) {
        this.showError(level, msg);
    },

    showError: function(level, msg) {
        var errors = [
            {
                level: level,
                msg: msg
            }
        ];
        this.setNoticeContainer(errors);
    },

    showErrors: function() {
        var me = this;
        if (OPF.isNotEmpty(this.task)) {
            this.task.cancel();

        }
        this.task = new Ext.util.DelayedTask(function() {
            me.showLastErrors();
        }, this);
        this.task.delay(10);
    },

    showLastErrors: function() {
        var errors = [];
        var fields = this.form.getForm().getFields();
        Ext.each(fields.items, function(field) {
            var activeErrors = field.getActiveErrors();
            Ext.each(activeErrors, function(activeError) {
                if (OPF.isNotBlank(activeError)) {
                    errors.push({
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: activeError
                    });
                }
            });
        });
        this.setNoticeContainer(errors);
    },

    setNoticeContainer: function(notices) {
        var errors = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.ERROR) {
                errors.push(notice);
            }
        });
        var allActiveError = '';
        if (errors.length > 0) {
            allActiveError = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Error Message!',
                containerCls: 'error-container',
                messages: errors,
                imageUrl: OPF.Cfg.fullUrl('images/error.png')
            });
        }

        var warns = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.WARN) {
                warns.push(notice);
            }
        });
        var allActiveWarns = '';
        if (warns.length > 0) {
            allActiveWarns = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Warning Message!',
                containerCls: 'warn-container',
                messages: warns,
                imageUrl: OPF.Cfg.fullUrl('images/info.png')
            });
        }

        var infos = [];
        Ext.each(notices, function(notice) {
            if (notice.level == OPF.core.validation.MessageLevel.INFO) {
                infos.push(notice);
            }
        });
        var allActiveInfo = '';
        if (infos.length > 0) {
            allActiveInfo = this.getTpl('activeMessageContainerTpl').apply({
                title: 'Info Message!',
                containerCls: 'info-container',
                messages: infos,
                imageUrl: OPF.Cfg.fullUrl('images/info.png')
            });
        }
        this.update(allActiveError + allActiveWarns + allActiveInfo);
        this.setVisible(OPF.isNotBlank(allActiveError + allActiveWarns + allActiveInfo));
    },

    cleanActiveErrors: function() {  
        this.update('');
        this.hide();
    }

});



Ext.define('OPF.core.component.plugin.TreeViewDragDrop', {
    extend:  Ext.tree.plugin.TreeViewDragDrop ,
    alias: 'plugin.opf-treeviewdragdrop',

    beforeDragEnter: function(target, e, id) {
        return true;
    },

    beforeDragOver: function(target, e, id) {
        return true;
    },

    beforeDragDrop: function(target, e, id){
        return true;
    },

    isValidDropPoint: function(targetNode, sourceNodes) {
        return true;
    },

    onViewRender : function(view) {
        var me = this;

        if (me.enableDrag) {
            me.dragZone = Ext.create('Ext.tree.ViewDragZone', {
                view: view,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText,
                repairHighlightColor: me.nodeHighlightColor,
                repairHighlight: me.nodeHighlightOnRepair,
                beforeDragEnter: me.beforeDragEnter,
                beforeDragOver: me.beforeDragOver,
                beforeDragDrop: me.beforeDragDrop
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('Ext.tree.ViewDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                allowContainerDrops: me.allowContainerDrops,
                appendOnly: me.appendOnly,
                allowParentInserts: me.allowParentInserts,
                expandDelay: me.expandDelay,
                dropHighlightColor: me.nodeHighlightColor,
                dropHighlight: me.nodeHighlightOnDrop,
                isValidDropPoint : function(node, position, dragZone, e, data) {
                    if (!node || !data.item) {
                        return false;
                    }

                    var view = this.view,
                        targetNode = view.getRecord(node),
                        draggedRecords = data.records,
                        dataLength = draggedRecords.length,
                        ln = draggedRecords.length,
                        i, record;

                    if (!(targetNode && position && dataLength)) {
                        return false;
                    }

                    for (i = 0; i < ln; i++) {
                        record = draggedRecords[i];
                        if (record.isNode && record.contains(targetNode)) {
                            return false;
                        }
                    }

                    if (position === 'append' && targetNode.get('allowDrop') === false) {
                        return false;
                    }
                    else if (position != 'append' && targetNode.parentNode.get('allowDrop') === false) {
                        return false;
                    }

                    if (Ext.Array.contains(draggedRecords, targetNode)) {
                        return false;
                    }

                    return me.isValidDropPoint(targetNode, draggedRecords);
                }
            });
        }
    }
});




Ext.define('OPF.core.component.CultureComboBox', {
    extend:  Ext.form.field.ComboBox ,

    triggerAction: 'all',
    editable: false,

    valueField: 'culture',
    displayField: 'country',
    width: 100,

    initComponent: function() {
        this.store = new Ext.data.Store({
            model: 'OPF.core.model.Culture',
            data: [
                {
                    country: "US",
                    culture: "AMERICAN"
                }
            ],
            proxy: {
                type: 'ajax',
                url : OPF.Cfg.restUrl('content/resource/culture'),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.listConfig = {
            getInnerTpl: function() {
                return '<div data-qtip="{culture}"><img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/flag_{countryLC}_16.png" />{country}</div>';
            }
        };

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(combobox) {
            combobox.setValue('AMERICAN');
            combobox.store.proxy.url = OPF.Cfg.restUrl('content/resource/culture');
        }
    }

});




Ext.define('OPF.core.component.resource.DisplayComponent', {
    extend:  Ext.container.Container ,
    alias: 'widget.display-component',

    maxTextLength: null,

    fullHtmlOrData: null,

    getHtmlOrData: function() {
        return this.fullHtmlOrData;
    },

    update: function(htmlOrData, loadScripts, cb) {
        var me = this;

        this.fullHtmlOrData = htmlOrData;

        if (OPF.isNotEmpty(this.maxTextLength)) {
            htmlOrData = OPF.cutting(htmlOrData, this.maxTextLength);
        }

        if (me.tpl && !Ext.isString(htmlOrData)) {
            me.data = htmlOrData;
            if (me.rendered) {
                me.tpl[me.tplWriteMode](me.getTargetEl(), htmlOrData || {});
            }
        } else {
            me.html = Ext.isObject(htmlOrData) ? Ext.DomHelper.markup(htmlOrData) : htmlOrData;
            if (me.rendered) {
                me.getTargetEl().update(me.html, loadScripts, cb);
            }
        }

        if (me.rendered) {
            me.doComponentLayout();
        }
    },

    listeners: {
        afterrender: function(me) {
            Ext.create('Ext.tip.ToolTip', {
                target: me.getId(),
                html: 'Loading...',
                dismissDelay: 0,
                listeners: {
                    beforeshow: function(tip) {
                        tip.update(me.fullHtmlOrData);
                    }
                }
            });
        }
    }

});




Ext.define('OPF.core.component.resource.ImageResourceControl', {
    extend:  Ext.container.Container ,
    alias: 'widget.image-resource-control',

    componentCls: null,
    cls: 'image-resource-control',
    innerCls: 'info-content',

    imgResourceLookup: null,
    imgInnerCls: 'info-image',
    imageWidth: null,
    imageHeight: null,

    autoInit: false,
    allowEdit: true,

    initComponent: function() {
        var me = this;

        this.cls = OPF.isNotBlank(this.componentCls) ? this.cls + ' ' + this.componentCls : this.cls;

        var renderMouseOverListener = {
            render: function(cnt) {
                if (OPF.Cfg.CAN_EDIT_RESOURCE && me.allowEdit) {
                    cnt.getEl().on({
                        'mouseover': function() {
                            me.editButton.addCls('active');
                        },
                        'mouseout': function() {
                            me.editButton.removeCls('active');
                        }
                    });
                }
            }
        };





        this.innerImgCnt = Ext.create('Ext.container.Container', {
            autoEl: 'span',
            cls: this.imgInnerCls,
            listeners: renderMouseOverListener,
            html: 'Loading...'
        });

        this.items = [

            this.innerImgCnt
        ];

        if (OPF.Cfg.CAN_EDIT_RESOURCE && this.allowEdit) {
            this.editButton = Ext.ComponentMgr.create({
                xtype: 'hrefclick',
                cls: 'image-resource-edit hidden',
                html: '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="edit" title="edit"/>',
                onClick: function() {
                    var winId = 'ImageEditorWinId9865';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.ImageResourceEditor(winId, {
                            imageWidth: me.imageWidth,
                            imageHeight: me.imageHeight
                        });
                    }
                    window.setEditingEl(me.innerImgCnt);
                    window.setIdData(me.imageIdData);
                    window.show();
                },
                listeners: renderMouseOverListener
            });
            this.items.push(this.editButton);
        }

        this.callParent(arguments);

    },

    listeners: {
        afterrender: function(container) {
            if (container.autoInit) {
                container.loadResource(true);
            }
        }
    },

    getResourceLookup: function() {
        return this.imgResourceLookup;
    },

    loadResource: function(async) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.imgResourceLookup),
            method: 'GET',
            async: async,

            success: function(response){
                var jsonData = Ext.decode(response.responseText);
                var data = null;
                if (jsonData.data) {
                    data = jsonData.data[0];
                }
                me.initResource(data);
            },
            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    initResource: function(data) {
        var imageHtml;
        var imageContainer;
        if (OPF.isNotEmpty(data)) {
            var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
            var url = OPF.Cfg.restUrl(urlSuffix);

            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, this.imageWidth, this.imageHeight);
            this.innerImgCnt.el.dom.innerHTML = imageHtml;
            this.imageIdData = {
                resourceId: data.id,
                version: data.resourceVersion.version,
                lookup: this.imgResourceLookup
            };
        } else {
            imageHtml = OPF.core.component.resource.ResourceControl.getStubHtml('Image not defined', this.imageWidth, this.imageHeight);
            imageContainer = Ext.create('Ext.container.Container', {
                cls: 'test-image-container',
                html: imageHtml
            });
            this.innerImgCnt.add(imageContainer);
            this.imageIdData = {
                resourceId: null,
                version: null,
                lookup: this.imgResourceLookup
            };
        }

    }

});



Ext.define('OPF.core.component.resource.ImageResourceEditor', {
    extend:  Ext.window.Window ,

    id: 'imageResourceEditorWindow',
    title: 'Edit Image Resource',
    closeAction: 'hide',
    modal: true,
    minWidth: 530,
    minHeight: 200,




    constrainHeader: true,

    imageWidth: null,
    imageHeight: null,

    editingEl: null,

    constructor: function(id, cfg) {
        cfg = cfg || {};
        OPF.core.component.resource.ImageResourceEditor.superclass.constructor.call(this, Ext.apply({
            id: id
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.imageWidthField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageWidthField'
        });

        this.imageHeightField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageHeightField'
        });

        this.resourceFileTemporaryName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileTemporaryName'
        });

        this.resourceFileOriginalName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileOriginalName'
        });

        this.imageContainer = new Ext.Component({
            html: 'Loading...'
        });

        this.cultureCombo = new OPF.core.component.CultureComboBox();

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                instance.resourceFileOriginalName.setValue(data.orgFilename);
                instance.resourceFileTemporaryName.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                instance.imageWidthField.setValue(data.width);
                instance.imageHeightField.setValue(data.height);
                instance.loadImageContainer(url, data.width, data.height);
                instance.saveButton.enable();
            }
        });

        this.saveButton = new Ext.Button({
            text: 'Save',
            disabled: true,
            handler: function () {
                instance.save();
            }
        });

        this.bbar = [
            {
                xtype: 'button',
                text: 'Upload new image',
                handler: function () {
                    instance.uploadFileDialog.show();
                }
            },
            ' ',
            'Language:',
            this.cultureCombo,
            '->',
            this.saveButton,
            ' ',
            {
                xtype: 'button',
                text: 'Close',
                handler: function () {
                    instance.hide();
                }
            }
        ];
        
        this.items = [
            this.imageContainer
        ];

        this.callParent(arguments);
    },

    setEditingEl: function(editingEl) {
        this.editingEl = editingEl;
    },

    setIdData: function(idData) {
        this.idData = idData;
    },

    setSizeAccordingToImg: function() {
        var winWidth = this.imageContainer.getSize().width + 14;
        var winHeight = this.imageContainer.getSize().height + 69;

        if (winWidth < this.minWidth) {
            winWidth = this.minWidth;
        }
        if (winHeight < this.minHeight) {
            winHeight = this.minHeight;
        }

        this.setSize({
            width: winWidth,
            height: winHeight
        });

        this.doLayout();
    },

    show: function() {
        var imgSize = this.editingEl.getSize();
        var component = OPF.core.component.resource.ImageResourceEditor.superclass.show.call(this);
        this.imageContainer.update(this.editingEl.el.dom.innerHTML);
        this.setSizeAccordingToImg();
        return component;
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var me = this;
        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, this.imageWidth, this.imageHeight);
        this.imageContainer.update(imageHtml);
        new Ext.util.DelayedTask(function() {
            me.setSizeAccordingToImg();
        }).delay(200);

    },

    getJsonData: function() {
        return {
            width: this.imageWidthField.getValue(),
            height: this.imageHeightField.getValue(),
            resourceFileTemporaryName: this.resourceFileTemporaryName.getValue(),
            resourceFileOriginalName: this.resourceFileOriginalName.getValue()
        };
    },

    save: function() {
        var instance = this;

        var jsonData = this.getJsonData();

        if (OPF.isEmpty(jsonData.resourceFileOriginalName)) {
            OPF.Msg.setAlert(false, 'Please upload an image.');
            return false;
        }

        var url;
        var method;
        if (this.idData.resourceId) {
            jsonData.resourceId = this.idData.resourceId;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl('content/resource/image/version/' + this.idData.resourceId);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl('content/resource/image/version/new-by-lookup/' + this.idData.lookup);
            method = 'POST';
        }

        jsonData.culture = this.cultureCombo.getValue();

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo;
                try {
                    vo = Ext.decode(response.responseText);
                } catch (e) {
                    OPF.Msg.setAlert(false, 'Error saving resource.');
                    return;
                }

                OPF.Msg.setAlert(vo.success, vo.message);

                if (vo.success) {
                    instance.saveButton.disable();

                    if (instance.cultureCombo.getValue() == 'AMERICAN') { 
                        var element = instance.editingEl.el.dom.firstChild;
                        var imageHtml;
                        if (element.nodeName == 'DIV') {
                            var data = vo.data[0];
                            var urlSuffix = 'content/resource/image/by-filename/' + data.resourceId + '/' + data.storedFilename + '?_dc=' + new Date().getTime();
                            var url = OPF.Cfg.restUrl(urlSuffix);
                            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, instance.imageWidth, instance.imageHeight);
                        } else {
                            var imageSrc = element.src;
                            imageSrc = imageSrc.replace(/_dc=.+/, '');
                            imageSrc += '_dc=' + new Date().getTime();
                            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(imageSrc, instance.imageWidth, instance.imageHeight);
                        }
                        instance.editingEl.update(imageHtml);
                    }
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while saving image.');
            }
        });
    }

});




Ext.define('OPF.core.component.resource.ResourceControl', {
    extend:  Ext.panel.Panel ,
    alias: 'widget.resource-control',

    cls: 'info-resource-content',

    innerCls: 'info-content',

    textResourceLookup: null,
    textInnerCls: 'info-text',
    textMaxLength: null,

    htmlResourceLookup: null,
    htmlInnerCls: 'info-html',

    imgResourceLookup: null,
    imgInnerCls: 'info-image',
    imageWidth: null,
    imageHeight: null,

    border: false,

    initComponent: function() {
        var instance = this;

        this.innerLoadingCmp = new Ext.Component({
            html: 'Loading...'
        });

        var buttonsArray = [];
        var resourcesArray = [this.innerLoadingCmp];

        if (this.imgResourceLookup) {
            this.innerImgCnt = new Ext.Component({
                cls: this.imgInnerCls
            });
            resourcesArray.push(this.innerImgCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT IMAGE',
                onClick: function() {
                    var winId = 'ImageEditorWinId9320';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.ImageResourceEditor(winId, {
                            imageWidth: instance.imageWidth,
                            imageHeight: instance.imageHeight
                        });
                    }
                    window.setEditingEl(instance.innerImgCnt);
                    window.setIdData(instance.imageIdData);
                    window.show();
                }
            });
        }

        if (this.textResourceLookup) {

            if (OPF.isNotEmpty(this.textMaxLength)) {
                this.innerTextCnt = new OPF.core.component.resource.DisplayComponent({
                    cls: this.textInnerCls,
                    maxTextLength: this.textMaxLength
                });
            } else {
                this.innerTextCnt = new Ext.Component({
                    cls: this.textInnerCls
                });
            }
            resourcesArray.push(this.innerTextCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT TEXT',
                onClick: function() {
                    var winId = 'TextEditorWinId5823';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'textarea');
                    }
                    window.setEditingEl(instance.innerTextCnt);
                    window.setIdData(instance.textIdData);
                    window.show();
                }
            });
        }
        
        if (this.htmlResourceLookup) {
            this.innerHtmlCnt = new Ext.Component({
                cls: this.htmlInnerCls
            });
            resourcesArray.push(this.innerHtmlCnt);

            buttonsArray.push({
                xtype: 'hrefclick',
                cls: 'resourceEditLink',
                html: 'EDIT HTML',
                onClick: function() {
                    var winId = 'HtmlEditorWinId9420';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'tinymceeditor');
                    }
                    window.setEditingEl(instance.innerHtmlCnt);
                    window.setIdData(instance.htmlIdData);
                    window.show();
                }
            });
        }

        var renderMouseOverListener = {
            render: function(cnt) {
                cnt.getEl().on({
                    'mouseover': function() {
                        instance.actionsCnt.show();
                    },
                    'mouseout': function() {
                        instance.actionsCnt.hide();
                    }
                });
            }
        };

        this.innerCnt = new Ext.Container({
            cls: this.innerCls,
            items: resourcesArray,
            listeners: renderMouseOverListener
        });

        this.actionsCnt = new Ext.Container({
            xtype: 'container',
            cls: 'actions',
            hidden: true,
            items: buttonsArray,
            listeners: renderMouseOverListener
        });

        this.items = [
            this.innerCnt
        ];

        this.callParent(arguments);

        this.numUpdates = 0;

        if (this.textResourceLookup) {
            this.numUpdates++;
        }
        if (this.htmlResourceLookup) {
            this.numUpdates++;
        }
        if (this.imgResourceLookup) {
            this.numUpdates++;
        }

        if (this.textResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/text/by-lookup/' + this.textResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        instance.innerTextCnt.update(data.resourceVersion.text);
                        instance.textIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    } else {
                        instance.innerTextCnt.update('Not defined');
                    }
                    instance.finishUpdate();
                },
                failure: null
            });
        }

        if (this.htmlResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/html/by-lookup/' + this.htmlResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        instance.innerHtmlCnt.update(data.resourceVersion.html);
                        instance.htmlIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    } else {
                        instance.innerHtmlCnt.update('Not defined');
                    }
                    instance.finishUpdate();
                },
                failure: null
            });
        }

        if (this.imgResourceLookup) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.imgResourceLookup),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);
                    if (jsonData.data) {
                        var data = jsonData.data[0];
                        var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
                        var url = OPF.Cfg.restUrl(urlSuffix);

                        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, instance.imageWidth, instance.imageHeight);

                        instance.innerImgCnt.update(imageHtml);
                        instance.finishUpdate();

                        instance.imageIdData = {
                            resourceId: data.id,
                            version: data.resourceVersion.version
                        };
                    }
                },
                failure: null
            });
        }

    },

    finishUpdate: function() {
        this.numUpdates--;
        if (this.numUpdates == 0) {
            this.innerLoadingCmp.update('');
            if (OPF.Cfg.CAN_EDIT_RESOURCE) {
                this.insert(1, this.actionsCnt);
            }
            this.doLayout();
        }
    }
});

OPF.core.component.resource.ResourceControl.getImgHtml = function(url, width, height, title) {
    var imageHtml = '<img src="' + url + '"';
    if (width) {
        imageHtml += ' width="' + width + '"';
    }
    if (height) {
        imageHtml += ' height="' + height + '"';
    }
    if (title) {
        imageHtml += ' title="' + title + '"';
        imageHtml += ' alt="' + title + '"';
    }
    imageHtml += ' />';
    return imageHtml;
};

OPF.core.component.resource.ResourceControl.getImgHtmlWrapper = function(url, orgWidth, orgHeight, title, maxWidth, maxHeight) {
    var widthCoof = 1;
    var heightCoof = 1;
    if (orgWidth > maxWidth) {
        widthCoof = orgWidth / maxWidth;
    }
    if (orgHeight > maxHeight) {
        heightCoof = orgHeight / maxHeight;
    }
    var width;
    var height;
    if (heightCoof > widthCoof) {
        width = orgWidth / heightCoof;
        height = orgHeight / heightCoof;
    } else if (heightCoof < widthCoof) {
        width = orgWidth / widthCoof;
        height = orgHeight / widthCoof;
    } else {
        width = orgWidth;
        height = orgHeight
    }

    var widthPadding = (maxWidth - width) / 2;
    var xPadding = widthPadding > 0 ? widthPadding + 'px' : '0';
    var heightPadding = (maxHeight - height) / 2;
    var yPadding = heightPadding > 0 ? heightPadding + 'px' : '0';

    var html = '<div style="width: ' + maxWidth + 'px; height: ' + maxHeight + 'px; padding: ' + yPadding + ' ' + xPadding + ' ' + yPadding + ' ' + xPadding + ';">';
    html += '<img src="' + url + '"';
    html += ' width="' + width + '"';
    html += ' height="' + height + '"';
    if (title) {
        html += ' title="' + title + '"';
        html += ' alt="' + title + '"';
    }
    html += ' /></div>';
    return html;
};

OPF.core.component.resource.ResourceControl.getStubHtml = function(title, width, height) {
    var imageHtml = '<div style="display: table-cell; border: 1px solid #999999; text-align: center; vertical-align: middle;';
    if (width) {
        imageHtml += 'width: ' + width + 'px;';
    }
    if (height) {
        imageHtml += 'height: ' + height + 'px;';
    }
    imageHtml += '">';
    if (title) {
        imageHtml += title;
    }
    imageHtml += '</div>';
    return imageHtml;
};




Ext.define('OPF.core.component.resource.TextResourceControl', {
    extend:  Ext.container.Container ,
    alias: 'widget.text-resource-control',

    innerCls: 'info-content',

    textResourceLookup: null,
    textInnerCls: 'info-text',
    maxTextLength: null,

    autoInit: false,
    allowEdit: true,

    initComponent: function() {
        var me = this;

        var renderMouseOverListener = {
            render: function(cnt) {
                if (OPF.Cfg.CAN_EDIT_RESOURCE && me.allowEdit) {
                    cnt.getEl().on({
                        'mouseover': function() {
                            me.editButton.addCls('active');
                        },
                        'mouseout': function() {
                            me.editButton.removeCls('active');
                        }
                    });
                }
            }
        };

        if (OPF.isNotEmpty(this.maxTextLength)) {
            this.innerTextCnt = Ext.create('OPF.core.component.resource.DisplayComponent', {
                autoEl: 'span',
                cls: this.textInnerCls,
                maxTextLength: this.maxTextLength,
                listeners: renderMouseOverListener,
                html: 'Loading...'
            });
        } else {
            this.innerTextCnt = Ext.create('Ext.container.Container', {
                autoEl: 'span',
                cls: this.textInnerCls,
                listeners: renderMouseOverListener,
                html: 'Loading...'
            });
        }

        this.items = [
            this.innerTextCnt
        ];

        if (OPF.Cfg.CAN_EDIT_RESOURCE && this.allowEdit) {
            this.editButton = Ext.ComponentMgr.create({
                xtype: 'hrefclick',
                cls: 'text-resource-edit hidden',
                html: '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="edit" title="edit"/>',
                onClick: function() {
                    var winId = 'TextEditorWinId4582';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'textarea');
                    }
                    window.setEditingEl(me.innerTextCnt);
                    window.setIdData(me.textIdData);
                    window.show();
                },
                listeners: renderMouseOverListener
            });
            this.items.push(this.editButton);
        }

        this.callParent(arguments);

    },

    listeners: {
        afterrender: function(container) {
            if (container.autoInit) {
                container.loadResource(true);
            }
        }
    },

    getResourceLookup: function() {
        return this.textResourceLookup;
    },

    loadResource: function(async) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/text/by-lookup/' + this.textResourceLookup),
            method: 'GET',
            async: async,

            success: function(response){
                var jsonData = Ext.decode(response.responseText);
                var data = null;
                if (jsonData.data) {
                    data = jsonData.data[0];
                }
                me.initResource(data);
            },
            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    initResource: function(data) {
        if (OPF.isNotEmpty(data)) {
            this.innerTextCnt.el.dom.innerHTML = data.resourceVersion.text;
            this.textIdData = {
                resourceId: data.id,
                version: data.resourceVersion.version,
                lookup: this.textResourceLookup
            };
        } else {
            this.innerTextCnt.el.dom.innerHTML = 'Not defined';
            this.textIdData = {
                resourceId: null,
                version: null,
                lookup: this.textResourceLookup
            };
        }
    }

});



Ext.define('OPF.core.component.resource.TextResourceEditor', {
    extend:  Ext.window.Window ,

    id: 'textResourceEditorWindow',
    title: 'Edit Text Resource',
    closeAction: 'hide',
    modal: true,
    minWidth: 400,
    minHeight: 200,
    layout: 'fit',
    constrainHeader: true,

    editorXType: null,
    editingEl: null,

    constructor: function(id, editorXType, cfg) {
        cfg = cfg || {};
        var title;
        if (editorXType == 'textarea') {
            title = 'Edit Text Resource';
        } else if (editorXType == 'tinymceeditor') {
            title = 'Edit HTML Resource';
        }
        OPF.core.component.resource.TextResourceEditor.superclass.constructor.call(this, Ext.apply({
            id: id,
            editorXType: editorXType,
            title: title
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.cultureCombo = new OPF.core.component.CultureComboBox();

        this.bbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                'Language:',
                this.cultureCombo,
                '->',
                {
                    xtype: 'button',
                    text: 'Save',
                    handler: function () {
                        instance.save();
                    }
                },
                ' ',
                {
                    xtype: 'button',
                    text: 'Close',
                    handler: function () {
                        instance.hide();
                    }
                }
            ]
        });

        this.editorComponent = Ext.ComponentMgr.create({
            xtype: this.editorXType
        });

        this.items = [
            this.editorComponent
        ];

        this.callParent(arguments);
    },

    setEditingEl: function(editingEl) {
        this.editingEl = editingEl;
        this.editorComponent.setValue(editingEl.getHtmlOrData ? editingEl.getHtmlOrData() : editingEl.el.dom.innerHTML);
    },

    setIdData: function(idData) {
        this.idData = idData;
    },

    show: function() {
        var elSize = this.editingEl.getSize();
        var winWidth = elSize.width < this.minWidth ? this.minWidth : elSize.width;
        var winHeight = elSize.height < this.minHeight ? this.minHeight : elSize.height;

        this.setSize({
            width: winWidth + 5,
            height: winHeight + 40
        });

        return OPF.core.component.resource.TextResourceEditor.superclass.show.call(this);
    },

    save: function() {
        var me = this;

        var jsonData = {};
        var urlInfix;

        if (this.editorXType == 'textarea') {
            jsonData.text = this.editorComponent.getValue();
            urlInfix = 'content/resource/text/version';
        } else if (this.editorXType == 'tinymceeditor') {
            jsonData.html = this.editorComponent.getValue();
            urlInfix = 'content/resource/html/version';
        }

        var url;
        var method;
        if (this.idData.resourceId) {
            jsonData.resourceId = this.idData.resourceId;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl(urlInfix + '/' + this.idData.resourceId);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl(urlInfix + '/new/' + this.idData.lookup);
            method = 'POST';
        }

        jsonData.culture = this.cultureCombo.getValue();

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo;
                try {
                    vo = Ext.decode(response.responseText);
                } catch (e) {
                    OPF.Msg.setAlert(false, 'Error saving resource.');
                    return;
                }

                OPF.Msg.setAlert(vo.success, vo.message);

                if (vo.success) {
                    me.idData.resourceId = vo.data[0].resourceId;
                    me.idData.version = vo.data[0].version;
                    if (me.cultureCombo.getValue() == 'AMERICAN') { 
                        var value = me.editorComponent.getValue();
                        me.editingEl.update(value);
                    }
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error saving resource.');
            }
        });
    }


});



Ext.define("OPF.core.component.tinymce.WindowManager", {


    constructor: function(config) {
        tinymce.WindowManager.call(this, config.editor);
    },

    alert: function(txt, cb, s) {
        Ext.MessageBox.alert("", txt, function() {
            if (!Ext.isEmpty(cb)) {
                cb.call(this);
            }
        }, s);
    },

    confirm: function(txt, cb, s) {
        Ext.MessageBox.confirm("", txt, function(btn) {
            if (!Ext.isEmpty(cb)) {
                cb.call(this, btn == "yes");
            }
        }, s);
    },

    open: function(s, p) {
        s = s || {};
        p = p || {};
        if (!s.type) {
            this.bookmark = this.editor.selection.getBookmark('simple');
        }
        s.width = parseInt(s.width || 320);
        s.height = parseInt(s.height || 240) + (tinymce.isIE ? 8 : 0);
        s.min_width = parseInt(s.min_width || 150);
        s.min_height = parseInt(s.min_height || 100);
        s.max_width = parseInt(s.max_width || 2000);
        s.max_height = parseInt(s.max_height || 2000);
        s.movable = true;
        s.resizable = true;
        p.mce_width = s.width;
        p.mce_height = s.height;
        p.mce_inline = true;
        this.features = s;
        this.params = p;
        var win = Ext.create("Ext.window.Window", {
            title: s.name,
            width: s.width,
            height: s.height,
            minWidth: s.min_width,
            minHeight: s.min_height,
            resizable: true,
            maximizable: s.maximizable,
            minimizable: s.minimizable,
            modal: true,
            stateful: false,
            constrain: true,
            
            layout: "fit",
            items: [Ext.create("Ext.Component", {
                autoEl: {
                    tag: 'iframe',
                    src: s.url || s.file
                },
                style: 'border-width: 0px;'
            })]
        });

        p.mce_window_id = win.getId();
        win.show(null, function() {
            if (s.left && s.top)
                win.setPagePosition(s.left, s.top);
            var pos = win.getPosition();
            s.left = pos[0];
            s.top = pos[1];
            this.onOpen.dispatch(this, s, p);
        }, this);

        return win;
    },

    close: function(win) {
        
        if (!win.tinyMCEPopup || !win.tinyMCEPopup.id) {
            tinymce.WindowManager.prototype.close.call(this, win);
            return;
        }

        var w = Ext.getCmp(win.tinyMCEPopup.id);

        if (w) {
            this.onClose.dispatch(this);
            w.close();
        }
    },

    setTitle: function(win, ti) {
        if (!win.tinyMCEPopup || !win.tinyMCEPopup.id) {
            tinymce.WindowManager.prototype.setTitle.call(this, win, ti);
            return;
        }

        var w = Ext.getCmp(win.tinyMCEPopup.id);

        if (w)
            w.setTitle(ti);
    },

    resizeBy: function(dw, dh, id) {
        var w = Ext.getCmp(id);

        if (w) {
            var size = w.getSize();
            w.setSize(size.width + dw, size.height + dh);
        }
    },

    focus: function(id) {
        var w = Ext.getCmp(id);
        if (w)
            w.setActive(true);
    }
});

Ext.define("OPF.core.component.tinymce.TinyMCEEditor", {
    extend:  Ext.form.field.TextArea ,
    alias: 'widget.tinymceeditor',

    config: {},

    constructor: function(editor, config) {
        var me = this;
        config = config || {};
        config.height = (config.height && config.height >= me.config.height) ? config.height : me.config.height;

        config.tinymceConfig = {};
        Ext.applyIf(config.tinymceConfig, me.getGlobalSettings());

        
        config.tinymceConfig.mode = 'none';

        me.addEvents({
            "editorcreated": true
        });

        me.callParent([config]);
    },

    getGlobalSettings: function() {
        return {
            accessibility_focus: false,
            language: "en",
            mode: "none",
            theme: "advanced",
            plugins: 'autolink,table',
            theme_advanced_buttons1: 'bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,forecolor,backcolor,|,styleselect,formatselect,fontselect,fontsizeselect,|,link, unlink,|,tablecontrols,removeformat,cleanup,code',
            theme_advanced_buttons2: '',
            theme_advanced_buttons3: '',
            theme_advanced_buttons4: '',

            content_css : [
                OPF.Cfg.fullUrl('css/clds4.css'),
                OPF.Cfg.fullUrl('css/documentation.css'),
                OPF.Cfg.fullUrl('css/simple-html-editor.css'),
                OPF.Cfg.fullUrl('css/tinymce.css')
            ],
            theme_advanced_toolbar_location: 'top',
            theme_advanced_toolbar_align: 'left',
            theme_advanced_statusbar_location: 'none',
            theme_advanced_resizing: false,
            theme_advanced_row_height: 30,

            style_formats : [
                    {title : 'Quote', inline : 'span', classes : 'quote'},
                    {title : 'Code', inline : 'span', classes : 'code'},
                    {title : 'Warning', inline : 'span', classes : 'warn'},
                    {title : 'Important', inline : 'span', classes : 'important'},
                    {title : 'Critical', inline : 'span', classes : 'critical'},
                    {title : 'Subdued', inline : 'span', classes : 'subdued'}
            ]
        }
    },

    afterRender: function() {
        var me = this;
        me.callParent(arguments);
        me.tinymceConfig.height = me.height;
        console.log(me.tinymceConfig.height);
        me.editor = new tinymce.Editor(me.inputEl.id, me.tinymceConfig);

        
        var validateContentTask = Ext.Function.createBuffered(me.validate, 250, this);
        me.editor.onKeyPress.add(validateContentTask);

        me.editor.onPostRender.add(Ext.Function.bind(function(editor, controlManager) {
            editor.windowManager = Ext.create("OPF.core.component.tinymce.WindowManager", {
                editor: me.editor
            });
            me.tableEl = Ext.get(me.editor.id + "_tbl");
            me.iframeEl = Ext.get(me.editor.id + "_ifr");
        }, me));

        window.b = me.editor;
        me.on('resize', me.onResize, me);

        me.editor.render();
        tinyMCE.add(me.editor);
    },

    setSize : function(width, height) {
        console.log([width, height]);
        if (this.editor.theme) {
            this.editor.theme.resizeTo(width, height - 30);
        }
        this.callParent(arguments);
    },

    isDirty: function() {
        var me = this;
        if (me.disabled || !me.rendered) {
            return false;
        }
        return me.editor && me.editor.initialized && me.editor.isDirty();
    },

    getValue: function() {
        return this.editor.getContent();
    },

    setValue: function(value) {
        var me = this;
        if (OPF.isNotEmpty(value)) {
            me.value = value;
            if (me.rendered) {
                me.withEd(function() {
                    me.editor.undoManager.clear();
                    me.editor.setContent(value === null || value === undefined ? '' : value);
                    me.editor.startContent = me.editor.getContent({
                        format: 'raw'
                    });
                    me.validate();
                });
            } else {
                new Ext.util.DelayedTask(function(value) {
                    this.setValue(value);
                }, this).delay(100, null, this, [value]);
            }
        }
    },

    getSubmitData: function() {
        var ret = {};
        ret[this.getName()] = this.getValue();
        return ret;
    },

    insertValueAtCursor: function(value) {
        var me = this;

        if (me.editor && me.editor.initialized) {
            me.editor.execCommand('mceInsertContent', false, value);
        }
    },

    onDestroy: function() {
        var me = this;

        me.editor.remove();
        me.editor.destroy();
        me.callParent(arguments);
    },

    onResize: function(component, adjWidth, adjHeight) {
        var width, bodyWidth = component.bodyEl.getWidth();

        if (component.iframeEl) {
            width = bodyWidth - component.iframeEl.getBorderWidth('lr') - 2;
            component.iframeEl.setWidth(width);
        }

        if (component.tableEl) {
            width = bodyWidth - component.tableEl.getBorderWidth('lr') - 2;
            component.tableEl.setWidth(width);
        }
    },

    getEditor: function() {
        return this.editor;
    },

    getRawValue: function() {
        var me = this;

        return (!me.editor || !me.editor.initialized) ? Ext.valueFrom(me.value, '') : me.editor.getContent();
    },

    disable: function() {
        this.withEd(function() {
            var bodyEl = this.editor.getBody();
            bodyEl = Ext.get(bodyEl);
            if (bodyEl.hasCls('mceContentBody')) {
                bodyEl.removeCls('mceContentBody');
                bodyEl.addCls('mceNonEditable');
            }
        });
    },

    enable: function() {
        this.withEd(function() {
            var bodyEl = this.editor.getBody();
            bodyEl = Ext.get(bodyEl);
            if (bodyEl.hasCls('mceNonEditable')) {
                bodyEl.removeCls('mceNonEditable');
                bodyEl.addCls('mceContentBody');
            }
        });
    },

    withEd: function(func) {
        
        if (!this.editor)
            this.on("editorcreated", function() {
                this.withEd(func);
            }, this);
        
        else if (this.editor.initialized)
            func.call(this);
        
        else
            this.editor.onInit.add(Ext.Function.bind(function() {
                Ext.Function.defer(func, 10, this);
            }, this));
    },

    validateValue: function(value) {
        var me = this;

        if (Ext.isFunction(me.validator)) {
            var msg = me.validator(value);
            if (msg !== true) {
                me.markInvalid(msg);
                return false;
            }
        }

        if (value.length < 1 || value === me.emptyText) { 
            if (me.allowBlank) {
                me.clearInvalid();
                return true;
            }
            else {
                me.markInvalid(me.blankText);
                return false;
            }
        }

        if (value.length < me.minLength) {
            me.markInvalid(Ext.String.format(me.minLengthText, me.minLength));
            return false;
        }
        else
            me.clearInvalid();

        if (value.length > me.maxLength) {
            me.markInvalid(Ext.String.format(me.maxLengthText, me.maxLength));
            return false;
        }
        else
            me.clearInvalid();

        if (me.vtype) {
            var vt = Ext.form.field.VTypes;
            if (!vt[me.vtype](value, me)) {
                me.markInvalid(me.vtypeText || vt[me.vtype + 'Text']);
                return false;
            }
        }

        if (me.regex && !me.regex.test(value)) {
            me.markInvalid(me.regexText);
            return false;
        }
        return true;
    }
});


(function(e){var a=/^\s*|\s*$/g,b,d="B".replace(/A(.)|B/,"$1")==="$1";var c={majorVersion:"3",minorVersion:"5b3",releaseDate:"2012-03-29",_init:function(){var s=this,q=document,o=navigator,g=o.userAgent,m,f,l,k,j,r;s.isOpera=e.opera&&opera.buildNumber;s.isWebKit=/WebKit/.test(g);s.isIE=!s.isWebKit&&!s.isOpera&&(/MSIE/gi).test(g)&&(/Explorer/gi).test(o.appName);s.isIE6=s.isIE&&/MSIE [56]/.test(g);s.isIE7=s.isIE&&/MSIE [7]/.test(g);s.isIE8=s.isIE&&/MSIE [8]/.test(g);s.isIE9=s.isIE&&/MSIE [9]/.test(g);s.isGecko=!s.isWebKit&&/Gecko/.test(g);s.isMac=g.indexOf("Mac")!=-1;s.isAir=/adobeair/i.test(g);s.isIDevice=/(iPad|iPhone)/.test(g);s.isIOS5=s.isIDevice&&g.match(/AppleWebKit\/(\d*)/)[1]>=534;if(e.tinyMCEPreInit){s.suffix=tinyMCEPreInit.suffix;s.baseURL=tinyMCEPreInit.base;s.query=tinyMCEPreInit.query;return}s.suffix="";f=q.getElementsByTagName("base");for(m=0;m<f.length;m++){r=f[m].href;if(r){if(/^https?:\/\/[^\/]+$/.test(r)){r+="/"}k=r?r.match(/.*\//)[0]:""}}function h(i){if(i.src&&/tiny_mce(|_gzip|_jquery|_prototype|_full)(_dev|_src)?.js/.test(i.src)){if(/_(src|dev)\.js/g.test(i.src)){s.suffix="_src"}if((j=i.src.indexOf("?"))!=-1){s.query=i.src.substring(j+1)}s.baseURL=i.src.substring(0,i.src.lastIndexOf("/"));if(k&&s.baseURL.indexOf("://")==-1&&s.baseURL.indexOf("/")!==0){s.baseURL=k+s.baseURL}return s.baseURL}return null}f=q.getElementsByTagName("script");for(m=0;m<f.length;m++){if(h(f[m])){return}}l=q.getElementsByTagName("head")[0];if(l){f=l.getElementsByTagName("script");for(m=0;m<f.length;m++){if(h(f[m])){return}}}return},is:function(g,f){if(!f){return g!==b}if(f=="array"&&(g.hasOwnProperty&&g instanceof Array)){return true}return typeof(g)==f},makeMap:function(f,j,h){var g;f=f||[];j=j||",";if(typeof(f)=="string"){f=f.split(j)}h=h||{};g=f.length;while(g--){h[f[g]]={}}return h},each:function(i,f,h){var j,g;if(!i){return 0}h=h||i;if(i.length!==b){for(j=0,g=i.length;j<g;j++){if(f.call(h,i[j],j,i)===false){return 0}}}else{for(j in i){if(i.hasOwnProperty(j)){if(f.call(h,i[j],j,i)===false){return 0}}}}return 1},map:function(g,h){var i=[];c.each(g,function(f){i.push(h(f))});return i},grep:function(g,h){var i=[];c.each(g,function(f){if(!h||h(f)){i.push(f)}});return i},inArray:function(g,h){var j,f;if(g){for(j=0,f=g.length;j<f;j++){if(g[j]===h){return j}}}return -1},extend:function(n,k){var j,f,h,g=arguments,m;for(j=1,f=g.length;j<f;j++){k=g[j];for(h in k){if(k.hasOwnProperty(h)){m=k[h];if(m!==b){n[h]=m}}}}return n},trim:function(f){return(f?""+f:"").replace(a,"")},create:function(o,f,j){var n=this,g,i,k,l,h,m=0;o=/^((static) )?([\w.]+)(:([\w.]+))?/.exec(o);k=o[3].match(/(^|\.)(\w+)$/i)[2];i=n.createNS(o[3].replace(/\.\w+$/,""),j);if(i[k]){return}if(o[2]=="static"){i[k]=f;if(this.onCreate){this.onCreate(o[2],o[3],i[k])}return}if(!f[k]){f[k]=function(){};m=1}i[k]=f[k];n.extend(i[k].prototype,f);if(o[5]){g=n.resolve(o[5]).prototype;l=o[5].match(/\.(\w+)$/i)[1];h=i[k];if(m){i[k]=function(){return g[l].apply(this,arguments)}}else{i[k]=function(){this.parent=g[l];return h.apply(this,arguments)}}i[k].prototype[k]=i[k];n.each(g,function(p,q){i[k].prototype[q]=g[q]});n.each(f,function(p,q){if(g[q]){i[k].prototype[q]=function(){this.parent=g[q];return p.apply(this,arguments)}}else{if(q!=k){i[k].prototype[q]=p}}})}n.each(f["static"],function(p,q){i[k][q]=p});if(this.onCreate){this.onCreate(o[2],o[3],i[k].prototype)}},walk:function(i,h,j,g){g=g||this;if(i){if(j){i=i[j]}c.each(i,function(k,f){if(h.call(g,k,f,j)===false){return false}c.walk(k,h,j,g)})}},createNS:function(j,h){var g,f;h=h||e;j=j.split(".");for(g=0;g<j.length;g++){f=j[g];if(!h[f]){h[f]={}}h=h[f]}return h},resolve:function(j,h){var g,f;h=h||e;j=j.split(".");for(g=0,f=j.length;g<f;g++){h=h[j[g]];if(!h){break}}return h},addUnload:function(j,i){var h=this,g;g=function(){var f=h.unloads,l,m;if(f){for(m in f){l=f[m];if(l&&l.func){l.func.call(l.scope,1)}}if(e.detachEvent){e.detachEvent("onbeforeunload",k);e.detachEvent("onunload",g)}else{if(e.removeEventListener){e.removeEventListener("unload",g,false)}}h.unloads=l=f=w=g=0;if(e.CollectGarbage){CollectGarbage()}}};function k(){var l=document;function f(){l.detachEvent("onstop",f);if(g){g()}l=0}if(l.readyState=="interactive"){if(l){l.attachEvent("onstop",f)}e.setTimeout(function(){if(l){l.detachEvent("onstop",f)}},0)}}j={func:j,scope:i||this};if(!h.unloads){if(e.attachEvent){e.attachEvent("onunload",g);e.attachEvent("onbeforeunload",k)}else{if(e.addEventListener){e.addEventListener("unload",g,false)}}h.unloads=[j]}else{h.unloads.push(j)}return j},removeUnload:function(i){var g=this.unloads,h=null;c.each(g,function(j,f){if(j&&j.func==i){g.splice(f,1);h=i;return false}});return h},explode:function(f,g){if(!f||c.is(f,"array")){return f}return c.map(f.split(g||","),c.trim)},_addVer:function(g){var f;if(!this.query){return g}f=(g.indexOf("?")==-1?"?":"&")+this.query;if(g.indexOf("#")==-1){return g+f}return g.replace("#",f+"#")},_replace:function(h,f,g){if(d){return g.replace(h,function(){var l=f,j=arguments,k;for(k=0;k<j.length-2;k++){if(j[k]===b){l=l.replace(new RegExp("\\$"+k,"g"),"")}else{l=l.replace(new RegExp("\\$"+k,"g"),j[k])}}return l})}return g.replace(h,f)}};c._init();e.tinymce=e.tinyMCE=c})(window);tinymce.create("tinymce.util.Dispatcher",{scope:null,listeners:null,Dispatcher:function(a){this.scope=a||this;this.listeners=[]},add:function(a,b){this.listeners.push({cb:a,scope:b||this.scope});return a},addToTop:function(a,b){this.listeners.unshift({cb:a,scope:b||this.scope});return a},remove:function(a){var b=this.listeners,c=null;tinymce.each(b,function(e,d){if(a==e.cb){c=a;b.splice(d,1);return false}});return c},dispatch:function(){var f,d=arguments,e,b=this.listeners,g;for(e=0;e<b.length;e++){g=b[e];f=g.cb.apply(g.scope,d.length>0?d:[g.scope]);if(f===false){break}}return f}});(function(){var a=tinymce.each;tinymce.create("tinymce.util.URI",{URI:function(e,g){var f=this,i,d,c,h;e=tinymce.trim(e);g=f.settings=g||{};if(/^([\w\-]+):([^\/]{2})/i.test(e)||/^\s*#/.test(e)){f.source=e;return}if(e.indexOf("/")===0&&e.indexOf("//")!==0){e=(g.base_uri?g.base_uri.protocol||"http":"http")+"://mce_host"+e}if(!/^[\w\-]*:?\/\


Ext.define('OPF.core.model.Culture', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'country', type: 'string' },
        { name: 'culture', type: 'string' },
        {
            name: 'countryLC',
            mapping: 'country',
            convert: function(v, record){
                return v.toLowerCase();
            }
        }
    ]

});



Ext.define('OPF.core.model.Field', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'inherited', type: 'boolean' },
        { name: 'autoGenerated', type: 'boolean' },
        { name: 'name', type: 'string' },
        { name: 'displayName', type: 'string' },
        { name: 'displayDescription', type: 'string' },
        { name: 'fieldType', type: 'string' },
        { name: 'fieldTypeName', type: 'string' },
        { name: 'customFieldType', type: 'string' },
        { name: 'required', type: 'boolean' },
        { name: 'searchable', type: 'boolean' },
        { name: 'defaultValue', type: 'string', useNull: true },
        { name: 'description', type: 'string', useNull: true },
        { name: 'allowedValues', useNull: true },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean'},
        { name: 'canDelete', type: 'boolean'}
    ],

    associations: [
        { type: 'belongsTo', model: 'OPF.console.directory.model.Directory', primaryKey: 'id', foreignKey: 'parentId' }
    ]

});


Ext.define('OPF.core.model.FileTreeModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: null,
        restSuffixUrl: 'registry',
        editorClassName: null,
        constraintName: null
    },

    fields: [
        { name: 'id', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'text', type: 'string' },
        { name: 'icon', type: 'string'},
        { name: 'cls', type: 'string'},
        { name: 'iconCls', type: 'string' },
        { name: 'canUpdate', type: 'boolean'},
        { name: 'canDelete', type: 'boolean'},
        { name: 'leaf', type: 'boolean'},
        { name: 'allowDrag', type: 'boolean'},
        { name: 'allowDrop', type: 'boolean'},
        { name: 'expanded', type: 'boolean'},
        { name: 'children', useNull: true}
    ]

});


Ext.define('OPF.core.model.RegistryNodeTreeModel', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: null,
        restSuffixUrl: 'registry',
        editorClassName: null,
        constraintName: null
    },

    fields: [
        {
            name: 'id',
            type: 'string',
            convert: function(id, record) {
                if (Ext.isNumber(id)) {
                    var type = record.get('type');
                    return 'xnode-' + type.toLowerCase() + '-' + id;
                } else {
                    return id;
                }
            }
        },
        { name: 'name', type: 'string'},
        {
            name: 'text',
            type: 'string',
            convert: function(text, record) {
                return OPF.ifBlank(text, record.get('name'));
            }
        },
        { name: 'icon', type: 'string'},
        { name: 'cls', type: 'string'},
        {
            name: 'iconCls',
            type: 'string',
            convert: function(iconCls, record) {
                return OPF.ifBlank(iconCls, 'tricon-' + record.get('type').toLowerCase());
            }
        },
        { name: 'urlPath', type: 'string', useNull: true },
        { name: 'realId', type: 'int'},
        { name: 'shortDescription', type: 'string'},
        { name: 'lookup', type: 'string'},
        { name: 'path', type: 'string'},
        { name: 'parentId', type: 'int'},
        { name: 'type', type: 'string'},
        { name: 'entityType', type: 'string'},
        { name: 'entitySubType', type: 'string'},
        { name: 'sortPosition', type: 'int'},
        { name: 'dropContainer'},
        { name: 'canUpdate', type: 'boolean'},
        { name: 'canDelete', type: 'boolean'},
        { name: 'leaf', type: 'boolean'},
        { name: 'allowDrag', type: 'boolean'},
        { name: 'allowDrop', type: 'boolean'},
        { name: 'expanded', type: 'boolean'},
        { name: 'children', useNull: true},
        { name: 'parameters', type: 'auto', useNull: true}
    ]

});



var AUTHENTICATION_OPF = 'authentication_opf';
var AUTHENTICATION_OPENID1 = 'authentication_openid1';
var AUTHENTICATION_OPENID2 = 'authentication_openid2';

Ext.define('OPF.core.utils.AuthenticationProvider', {

    constructor: function(longName, shortName, icon, url, cfg) {
        cfg = cfg || {};
        this.longName = longName;
        this.shortName = shortName;
        this.icon =  OPF.Cfg.fullUrl(icon);
        this.url = url;
        this.authenticationType = AUTHENTICATION_OPF;
    },

    getLongName: function() {
        return this.longName;
    },

    getShortName: function() {
        return this.shortName;
    },

    getIcon: function() {
        return this.icon;
    },

    getUrl: function() {
        return this.url;
    },

    getAuthenticationType: function() {
        return this.authenticationType;
    },

    prepareForm: function(formOwner) {
        formOwner.setCurrentAuthenticationProvider(this);
        formOwner.setOpenFlameForm();
    }
});

Ext.define('OPF.core.utils.OpenIdProvider', {
    extend:  OPF.core.utils.AuthenticationProvider ,

    constructor: function(longName, shortName, urlSuffix, idParameter, icon, id, url, urlPrefix, authenticationType, cfg) {
        cfg = cfg || {};
        OPF.core.utils.OpenIdProvider.superclass.constructor.call(this, longName, shortName, icon, url, cfg);
        this.urlSuffix = urlSuffix;
        this.idParameter = idParameter;
        this.urlPrefix = urlPrefix;
        this.authenticationType = authenticationType;
    },

    getUrlSuffix: function() {
        return this.urlSuffix;
    },

    getIdParameter: function() {
        return this.idParameter;
    },

    getUrlPrefix: function() {
        return this.urlPrefix;
    },

    prepareForm: function(formOwner) {
        formOwner.setCurrentAuthenticationProvider(this);
        formOwner.setOpenIdForm();
        if (this.isOpenId2Supported() && !this.isOpenId1Supported()) {
            formOwner.prepareOpenId2FieldSet(this);
        } else {
            formOwner.prepareOpenId1FieldSet(this);
        }
    }
});
//@require PrototypeUtils.js



OPF.capitalise = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};

OPF.isNotEmpty = function(obj) {
    return obj != undefined && obj != null;
};

OPF.isEmpty = function(obj) {
    return obj == undefined || obj == null;
};

OPF.isBlank = function(str) {
    return OPF.isEmpty(str) || str.length == 0
};

OPF.isNotBlank = function(str) {
    return OPF.isNotEmpty(str) && str.length > 0;
};

OPF.ifBlank = function(str, defaultValue) {
    if (OPF.isNotBlank(str)) {
        return str;
    }
    return defaultValue;
};

OPF.ifEmpty = function(value, defaultValue) {
    if (OPF.isNotEmpty(value)) {
        return value;
    }
    return defaultValue;
};

OPF.getSimpleClassName = function(fullClassName) {
    var lastDotPos = fullClassName.lastIndexOf('.');
    return fullClassName.substring(lastDotPos + 1);
};

OPF.findPackageLookup = function(lookup) {
    var pattern = /^[\w\-]+\.[\w\-]+\.[\w\-]+/g;
    return pattern.exec(lookup)[0];
};

OPF.findPathFromLookup = function(lookup) {
    return lookup.substring(0, lookup.lastIndexOf('.'));
};

OPF.generateUrlByLookup = function(lookup, prefixUrl) {
    var packageLookup = OPF.findPackageLookup(lookup);
    var lookupSuffix = lookup.substring(packageLookup.length);
    var urlSuffix = lookupSuffix.replace(/\./g, '/');
    return OPF.Cfg.fullUrl((prefixUrl || '') + urlSuffix, true);
};

OPF.cutting = function(s, length) {
    if (OPF.isNotEmpty(s)) {
        if (s.length > length) {
            s = s.substring(0, length - 3) + '...';
        }
    }
    return s;
};

OPF.convertTime = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'g:i A');
    }
    return value;
};

OPF.convertDate = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'Y-m-d');
    }
    return value;
};

OPF.convertDatetime = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'Y-m-d g:i:s A');
    }
    return value;
};

OPF.calculateColumnWidth = function(name) {
    return name.length * 8 + 25;
};

OPF.getQueryParam = function gup(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.href);
    if (results == null)
        return null;
    else
        return results[1];
};

OPF.copyStore = function(store, filterFn) {
	var records = [];
	var newStore = new Ext.data.ArrayStore({
		model : store.model
    });
	store.each(function (r) {
        if (filterFn(r)) {
            records.push (r.copy());
        }
	});
	newStore.loadRecords(records);
	return newStore;
};

OPF.loadJsFile = function(filename) {
    var fileref = document.createElement('script');
    fileref.setAttribute("type", "text/javascript");
    fileref.setAttribute("src", filename);
    document.getElementsByTagName("head")[0].appendChild(fileref);
};

OPF.loadCssFile = function(filename) {
    var fileref = document.createElement("link");
    fileref.setAttribute("rel", "stylesheet");
    fileref.setAttribute("type", "text/css");
    fileref.setAttribute("href", filename);
    document.getElementsByTagName("head")[0].appendChild(fileref)
};

OPF.windowSize = function() {
    var width = 0, height = 0;
    if (typeof( window.innerWidth ) == 'number') {
        
        width = window.innerWidth;
        height = window.innerHeight;
    } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )) {
        
        width = document.documentElement.clientWidth;
        height = document.documentElement.clientHeight;
    } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
        
        width = document.body.clientWidth;
        height = document.body.clientHeight;
    }
    return {
        width: width,
        height: height
    };
};

OPF.getScrollXY = function() {
    var scrOfX = 0, scrOfY = 0;
    if (typeof( window.pageYOffset ) == 'number') {
        
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if (document.body && ( document.body.scrollLeft || document.body.scrollTop )) {
        
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if (document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop )) {
        
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return {
        scrOfX: scrOfX,
        scrOfY: scrOfY
    };
};

OPF.randomUUID = function() {
  var s = [], itoh = '0123456789ABCDEF';

  
  
  for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);

  
  s[14] = 4;  
  s[19] = (s[19] & 0x3) | 0x8;  

  
  for (var i = 0; i <36; i++) s[i] = itoh[s[i]];

  
  s[8] = s[13] = s[18] = s[23] = '-';

  return s.join('');
};

OPF.textToImage = function(text, fontStyle) {
    text = '{' + text + '}';
    var defineTextWidth = document.createElement('div');
    document.body.appendChild(defineTextWidth);
    defineTextWidth.innerHTML = text;
    defineTextWidth.style.position = 'absolute';
    defineTextWidth.style.visibility = 'hidden';
    defineTextWidth.style.top = '-1000';
    defineTextWidth.style.left = '-1000';

    if (!fontStyle) {
        fontStyle = '12px Tahoma';
    }
    defineTextWidth.style.font = fontStyle;
    var fontSize = defineTextWidth.style.fontSize;
    if (fontSize.match(/\d+px/g)) {
        fontSize = parseInt(fontSize.replace(/px/g, ''));
    }

    var height = defineTextWidth.clientHeight + 1;
    var width = defineTextWidth.clientWidth + 1;

    document.body.removeChild(defineTextWidth);

    var canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    var ctx = canvas.getContext('2d');

    ctx.strokeStyle = '#FF0000';
    ctx.lineWidth = 1;
    ctx.fillStyle = '#DEFCFA';
    ctx.fillRect(0, 0, width, height);

    ctx.fillStyle = '#000000';
    ctx.font = fontStyle;
    ctx.fillText(text, 1, height - fontSize / 3);
    var base64Src = canvas.toDataURL('image/png');

    return {
        base64Src: base64Src,
        width: width,
        height: height
    }
};

OPF.htmlToText = function(html, extensions) {
    if (OPF.isBlank(html)) {
        return html;
    }

    var i, r, text = html;

    if (extensions && extensions['preprocessing'])
        text = extensions['preprocessing'](text);

    text = text
        
        .replace(/(?:\n|\r\n|\r)/ig, " ")
        
        .replace(/<\s*script[^>]*>[\s\S]*?<\/script>/mig, "")
        
        .replace(/<\s*style[^>]*>[\s\S]*?<\/style>/mig, "")
        
        .replace(/<!--.*?-->/mig, "")
        
        .replace(/<!DOCTYPE.*?>/ig, "");

    if (extensions && extensions['tagreplacement'])
        text = extensions['tagreplacement'](text);

    var doubleNewlineTags = ['p', 'h[1-6]', 'dl', 'dt', 'dd', 'ol', 'ul',
        'dir', 'address', 'blockquote', 'center', 'div', 'hr', 'pre', 'form',
        'textarea', 'table'];

    var singleNewlineTags = ['li', 'del', 'ins', 'fieldset', 'legend',
        'tr', 'th', 'caption', 'thead', 'tbody', 'tfoot'];

    for (i = 0; i < doubleNewlineTags.length; i++) {
        r = new RegExp('</?\\s*' + doubleNewlineTags[i] + '[^>]*>', 'ig');
        text = text.replace(r, '\n\n');
    }

    for (i = 0; i < singleNewlineTags.length; i++) {
        r = new RegExp('<\\s*' + singleNewlineTags[i] + '[^>]*>', 'ig');
        text = text.replace(r, '\n');
    }

    
    text = text.replace(/<\s*br[^>]*\/?\s*>/ig, '\n');

    function decodeHtmlEntity(m, n) {
        
        
        
        var code;

        
        
        
        if (n.substr(0, 1) == '#') {
                
                if (n.substr(1, 1) == 'x') {
                        
                        code = parseInt(n.substr(2), 16);
                } else {
                        
                        code = parseInt(n.substr(1), 10);
                }
        } else {
                
                code = OPF.ENTITIES_MAP[n];
        }

        
        return (code === undefined || code === NaN) ?
                '&' + n + ';' : String.fromCharCode(code);
    }

    text = text
        
        .replace(/(<([^>]+)>)/ig,"")
        
        .replace(/([^\n\S]+)\n/g,"\n")
        .replace(/([^\n\S]+)$/,"")
        
        
        .replace(/\n{2,}/g,"\n\n")
        
        .replace(/^\n+/,"")
        
        .replace(/\n+$/,"")
        
        .replace(/&([^;]+);/g, decodeHtmlEntity);

    if (extensions && extensions['postprocessing'])
        text = extensions['postprocessing'](text);

    return text;
};

OPF.calculateLookup = function(path, name) {
    path = OPF.ifBlank(path, '');
    name = OPF.ifBlank(name, '');
    var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
    var lookup = (OPF.isNotBlank(path) ? path + '.' : '') + normalizeName;
    return lookup.toLowerCase();
};

OPF.ENTITIES_MAP = {
  'nbsp' : 160,
  'iexcl' : 161,
  'cent' : 162,
  'pound' : 163,
  'curren' : 164,
  'yen' : 165,
  'brvbar' : 166,
  'sect' : 167,
  'uml' : 168,
  'copy' : 169,
  'ordf' : 170,
  'laquo' : 171,
  'not' : 172,
  'shy' : 173,
  'reg' : 174,
  'macr' : 175,
  'deg' : 176,
  'plusmn' : 177,
  'sup2' : 178,
  'sup3' : 179,
  'acute' : 180,
  'micro' : 181,
  'para' : 182,
  'middot' : 183,
  'cedil' : 184,
  'sup1' : 185,
  'ordm' : 186,
  'raquo' : 187,
  'frac14' : 188,
  'frac12' : 189,
  'frac34' : 190,
  'iquest' : 191,
  'Agrave' : 192,
  'Aacute' : 193,
  'Acirc' : 194,
  'Atilde' : 195,
  'Auml' : 196,
  'Aring' : 197,
  'AElig' : 198,
  'Ccedil' : 199,
  'Egrave' : 200,
  'Eacute' : 201,
  'Ecirc' : 202,
  'Euml' : 203,
  'Igrave' : 204,
  'Iacute' : 205,
  'Icirc' : 206,
  'Iuml' : 207,
  'ETH' : 208,
  'Ntilde' : 209,
  'Ograve' : 210,
  'Oacute' : 211,
  'Ocirc' : 212,
  'Otilde' : 213,
  'Ouml' : 214,
  'times' : 215,
  'Oslash' : 216,
  'Ugrave' : 217,
  'Uacute' : 218,
  'Ucirc' : 219,
  'Uuml' : 220,
  'Yacute' : 221,
  'THORN' : 222,
  'szlig' : 223,
  'agrave' : 224,
  'aacute' : 225,
  'acirc' : 226,
  'atilde' : 227,
  'auml' : 228,
  'aring' : 229,
  'aelig' : 230,
  'ccedil' : 231,
  'egrave' : 232,
  'eacute' : 233,
  'ecirc' : 234,
  'euml' : 235,
  'igrave' : 236,
  'iacute' : 237,
  'icirc' : 238,
  'iuml' : 239,
  'eth' : 240,
  'ntilde' : 241,
  'ograve' : 242,
  'oacute' : 243,
  'ocirc' : 244,
  'otilde' : 245,
  'ouml' : 246,
  'divide' : 247,
  'oslash' : 248,
  'ugrave' : 249,
  'uacute' : 250,
  'ucirc' : 251,
  'uuml' : 252,
  'yacute' : 253,
  'thorn' : 254,
  'yuml' : 255,
  'quot' : 34,
  'amp' : 38,
  'lt' : 60,
  'gt' : 62,
  'OElig' : 338,
  'oelig' : 339,
  'Scaron' : 352,
  'scaron' : 353,
  'Yuml' : 376,
  'circ' : 710,
  'tilde' : 732,
  'ensp' : 8194,
  'emsp' : 8195,
  'thinsp' : 8201,
  'zwnj' : 8204,
  'zwj' : 8205,
  'lrm' : 8206,
  'rlm' : 8207,
  'ndash' : 8211,
  'mdash' : 8212,
  'lsquo' : 8216,
  'rsquo' : 8217,
  'sbquo' : 8218,
  'ldquo' : 8220,
  'rdquo' : 8221,
  'bdquo' : 8222,
  'dagger' : 8224,
  'Dagger' : 8225,
  'permil' : 8240,
  'lsaquo' : 8249,
  'rsaquo' : 8250,
  'euro' : 8364
};


Date.prototype.getWeek = function() {
    var onejan = new Date(this.getFullYear(), 0, 1);
    return Math.ceil((((this - onejan) / 86400000) + onejan.getDay() + 1) / 7);
};

Date.prototype.addDays = function (d) {
    if (d) {
        var t = this.getTime();
        t = t + (d * 86400000);
        this.setTime(t);
    }
};

Date.prototype.getLastDateOfMonth = function() {
    return Ext.Date.getLastDateOfMonth(this);
};


Ext.override(Ext.data.Store, {
    exportExcel: function (options) {
        var me = this;

        options = options || {};

        Ext.applyIf(options, {
            groupers: me.groupers.items,
            page: me.currentPage,
            start: (me.currentPage - 1) * me.pageSize,
            limit: me.pageSize,
            addRecords: false,
            action : 'read',
            filters: me.filters.items,
            sorters: me.getSorters()
        });

        Ext.apply(options.headers, me.proxy.headers);
        Ext.apply(options.headers, {
            'Accept': 'application/xls'
        });

        var operation = Ext.create('Ext.data.Operation', options);

        me.proxy.processResponse = function(success, operation, request, response, callback, scope){
            var proxy = this;
            me.unmask();
            if (typeof callback == 'function') {
                callback(operation, success, response);
            }

            proxy.afterRequest(request, success);
        };

        if (me.fireEvent('beforeload', me, operation) !== false) {
            me.loading = true;
            me.masked = true;
            me.proxy.headers = options.headers;
            me.proxy.read(operation, options.callback, me);










        }

        return me;
    }

});





Ext.define('OPF.core.utils.MessageUtils', {
    extend:  Ext.util.Observable ,
    alternateClassName: ['OPF.Msg'],

    
    STATUS_EXCEPTION :          'exception',
    STATUS_VALIDATION_ERROR :   "validation",
    STATUS_ERROR:               "error",
    STATUS_NOTICE:              "notice",
    STATUS_OK:                  "ok",
    STATUS_HELP:                "help",

    
    msgCt : null,

    constructor: function(cfg) {
        Ext.onReady(this.onReady, this);

        OPF.core.utils.MessageUtils.superclass.constructor.call(this, cfg);
    },

    
    onReady : function() {
        
        this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        this.msgCt.setStyle('position', 'absolute');
        this.msgCt.setStyle('z-index', 99999);
        this.msgCt.setWidth(300);
    },

    
    setAlert : function(status, msg) {
        if (OPF.Cfg.DEBUG_MODE && OPF.isNotBlank(msg)) {
            var message = '<ul>';
            if (Ext.isArray(msg)) {
                Ext.each(msg, function(ms) {
                    message += '<li>' + ms + '</li>';
                })
            } else {
                message = '<li>' + msg + '</li>';
            }
            message += '</ul>';

            
            var delay = msg.length / 13.3;
            if (delay < 3) {
                delay = 3;
            }
            else if (delay > 9) {
                delay = 9;
            }
            delay = delay * 1000;

            this.msgCt.alignTo(document, 't-t');
            Ext.DomHelper.append(this.msgCt, {html:this.buildMessageBox(status, message)}, true).slideIn('t').ghost("t", {delay: delay, remove:true});
        }
    },

    
    buildMessageBox : function(title, msg) {
        switch (title) {
            case true:
                title = this.STATUS_OK;
                break;
            case false:
                title = this.STATUS_ERROR;
                break;
        }
        return [
            '<div class="app-msg">',
            '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
            '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3 class="x-icon-text icon-status-' + title + '">', title, '</h3>', msg, '</div></div></div>',
            '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
            '</div>'
        ].join('');
    },

    
    decodeStatusIcon : function(status) {
        var iconCls = '';
        switch (status) {
            case true:
            case this.STATUS_OK:
                iconCls = this.ICON_OK;
                break;
            case this.STATUS_NOTICE:
                iconCls = this.ICON_NOTICE;
                break;
            case false:
            case this.STATUS_ERROR:
                iconCls = this.ICON_ERROR;
                break;
            case this.STATUS_HELP:
                iconCls = this.ICON_HELP;
                break;
        }
        return iconCls;
    }
});

OPF.Msg = new OPF.core.utils.MessageUtils({});





Ext.define('OPF.core.utils.ModelHelper', {
    statics: {

        getCompositeId: function(record, associationFields, reloadFromRaw) {
            var compositeIdKeys = [];
            if (record) {
                if (reloadFromRaw && record.raw) {
                    record = this.createModelFromData(record, record.raw);
                }

                Ext.each(associationFields, function(associationField) {
                    var association = record.associations.get(associationField);
                    if (association) {
                        compositeIdKeys.push(associationField);
                        var associationModel = Ext.create(association.model);
                        var id = OPF.ModelHelper.getFk(record, associationField, associationModel.idProperty);
                        compositeIdKeys.push(id);
                    }
                });
            }
            return compositeIdKeys.join("/");
        },

        
        getProperty: function (record, complexProperty) {
            if (!complexProperty) {
                Ext.log.warn("Empty complex property sent in for a getRecord call!");
                return;
            }
            var properties;
            if (Ext.isArray(complexProperty)) {
                properties = complexProperty;
            } else {
                properties = complexProperty.split('.');
            }
            if (properties.length == 1) {
                var retVal = record.get(properties[0]);

                
                if (retVal === null || retVal === undefined) {
                    retVal = OPF.ModelHelper.getFk(record, properties[0], null);
                }
                return retVal;
            } else {

                var association = record.associations.get(properties[0]);
                if (association) {
                    var fk = record[association.instanceName];
                    if (fk) {
                        
                        properties = properties.splice(1);
                        return OPF.ModelHelper.getProperty(fk, properties);
                    }
                }
            }
        },

        
        getFk: function(record, foreignKeyName, foreignKeyProperty) {
            if (!record)
                return;
            var association = record.associations.get(foreignKeyName);
            if (association) {
                if (association.instanceName && record[association.instanceName]) {
                    var fk = record['get' + foreignKeyName]();
                    if (fk && foreignKeyProperty) {
                        var retVal = fk.get(foreignKeyProperty);

                        
                        if (retVal === null || retVal === undefined) {
                            retVal = OPF.ModelHelper.getFk(fk, foreignKeyProperty);
                        }
                        return retVal;
                    } else {
                        return fk;
                    }
                }
            }
        },

        setFk: function(record, foreignKeyName, foreignKeyProperty, value) {
            if (!record)
                return;

            var association = record.associations.get(foreignKeyName);
            if (association) {
                if (association.instanceName && record['get' + foreignKeyName]) {
                    var fk = record['get' + foreignKeyName]();
                    fk.set(foreignKeyProperty, value);
                    return;
                }
            }
            Ext.log.warn('Could not find an appropriate record in the store to set!');
        },

        setFkModel: function(record, foreignKeyName, fkData, isModel) {
            if (!record) {
                return;
            }
            var association = record.associations.get(foreignKeyName);
            if (association && association.instanceName && record['get' + foreignKeyName]) {
                if (isModel) {
                    record[association.instanceName] = fkData;
                } else {
                    record[association.instanceName] = OPF.ModelHelper.createModelFromData(association.model, fkData);
                }
            }
        },

        createModelFromData: function(model, jsonData) {
            if (Ext.isString(model)) {
                var reader = Ext.create('Ext.data.reader.Json', {
                    model: model
                });
                var resultSet = reader.read(jsonData);
                model = resultSet.records[0];
            }

            Ext.each(model.associations.items, function(association) {
                var associationModel = Ext.create(association.model);

                var associationData = jsonData[association.name];
                if (associationData) {
                    if (association.type == 'belongsTo') {
                        model[association.instanceName] = OPF.ModelHelper.createModelFromData(association.model, associationData);
                    } else if (association.type == 'hasMany') {
                        var associationModels = [];
                        Ext.each(associationData, function(associationItem) {
                            var associationModel = OPF.ModelHelper.createModelFromData(association.model, associationItem);
                            associationModels.push(associationModel);
                        });
                        var store = Ext.create('Ext.data.Store', {
                            model: association.model
                        });
                        store.add(associationModels);
                        model[association.name + 'Store'] = store;
                    }
                }
            });
            return model;
        },

        createAssociationProperty: function() {

        },

        getModelDataFromStore: function(store) {
            var data = [];
            var records = store.getRange();
            for (var i = 0; i < records.length; i++) {
                data.push(records[i].getData(true));
            }
            return data;
        },

        getData: function(record, includeAssociated, includeNotPersisted){
            var fields = record.fields.items,
                fLen   = fields.length,
                data   = {},
                name, f;

            for (f = 0; f < fLen; f++) {
                if (fields[f].persist !== false || includeNotPersisted) {
                    name = fields[f].name;
                    data[name] = record.get(name);
                }
            }

            if (includeAssociated === true) {
                Ext.apply(data, OPF.ModelHelper.getAssociatedData(record, {}, 1, includeNotPersisted));
            }
            return data;
        },

        
        getAssociatedData: function(record, seenKeys, depth, includeNotPersisted) {

            var associations     = record.associations.items,
                associationCount = associations.length,
                associationData  = {},
                toRead           = [],
                toReadKey        = [],
                toReadIndex      = [],
                associatedStore, associatedRecords, associatedRecord, o, index, result, seenDepth,
                associationId, associatedRecordCount, association, i, j, type, name;

            for (i = 0; i < associationCount; i++) {
                association = associations[i];
                associationId = association.associationId;

                seenDepth = seenKeys[associationId];
                if (seenDepth && seenDepth !== depth) {
                    continue;
                }
                seenKeys[associationId] = depth;

                type = association.type;
                name = association.name;
                if (type == 'hasMany') {
                    associatedStore = record[association.storeName];
                    associationData[name] = [];

                    if (associatedStore && associatedStore.getCount() > 0) {
                        associatedRecords = associatedStore.data.items;
                        associatedRecordCount = associatedRecords.length;
                        for (j = 0; j < associatedRecordCount; j++) {
                            associatedRecord = associatedRecords[j];
                            associationData[name][j] = OPF.ModelHelper.getData(associatedRecord, false, includeNotPersisted);
                            toRead.push(associatedRecord);
                            toReadKey.push(name);
                            toReadIndex.push(j);
                        }
                    }
                } else if (type == 'belongsTo' || type == 'hasOne') {
                    associatedRecord = record[association.instanceName];
                    if (associatedRecord !== undefined) {
                        associationData[name] = OPF.ModelHelper.getData(associatedRecord, false, includeNotPersisted);
                        toRead.push(associatedRecord);
                        toReadKey.push(name);
                        toReadIndex.push(-1);
                    }
                }
            }

            for (i = 0, associatedRecordCount = toRead.length; i < associatedRecordCount; ++i) {
                associatedRecord = toRead[i];
                o = associationData[toReadKey[i]];
                index = toReadIndex[i];
                result = OPF.ModelHelper.getAssociatedData(associatedRecord, seenKeys, depth + 1, includeNotPersisted);
                if (index === -1) {
                    Ext.apply(o, result);
                } else {
                    Ext.apply(o[index], result);
                }
            }
            return associationData;
        }
    }

});

OPF.ModelHelper = OPF.core.utils.ModelHelper;



Ext.define('OPF.core.utils.NavigationMenuRegister', {

    navigationElements: [],

    add: function(navigationElement) {
        this.navigationElements.push(navigationElement);
    },

    findByLookup: function(lookup) {
        var foundNavigationElement = null;
        Ext.each(this.navigationElements, function(navigationElement) {
            if (navigationElement.lookup == lookup) {
                foundNavigationElement = navigationElement;
            }
        });
        return foundNavigationElement;
    }

});
OPF.NavigationMenuRegister = new OPF.core.utils.NavigationMenuRegister();


Ext.data.Connection.securedCallback = function(options, success, response) {
    if (response.status == 403 || response.status == 406) {
        var message = response.status == 403 ?
            'Guest user tries to perform secured action</br>' +
                'or Your session has timed out.</br>' +
                ' Please, login to the system.' :
            'You are no longer logged in to the system.</br>' +
                'Please, provide your username and password to resume work.';
        Ext.MessageBox.show({
            title:'Warning',
            msg: message,
            closable: false,
            buttons: Ext.MessageBox.OK,
            fn: function() {
                document.location = OPF.Cfg.OPF_CONSOLE_URL + '/console/logout';
            },
            animEl: 'elId',
            icon: Ext.MessageBox.WARNING
        });
    } else if (response.status == 401) {
        Ext.Msg.show({
            title:'Warning',
            msg: 'Not enough permissions.',
            closable: false,
            buttons: Ext.Msg.OK,
            fn: function() {
            },
            animEl: 'elId',
            icon: Ext.MessageBox.WARNING
        });
    } else {
        if (OPF.isNotEmpty(options.clientCallback)) {
            Ext.callback(options.clientCallback, this, arguments);
        }
    }
};

Ext.override(Ext.data.Connection, {

    request: function(options) {
        options = options || {};

        
        if (OPF.isNotEmpty(options.callback)) {
            options.clientCallback = options.callback;
        }
        options.callback = Ext.data.Connection.securedCallback;
        

        var me = this,
            scope = options.scope || window,
            username = options.username || me.username,
            password = options.password || me.password || '',
            async,
            requestOptions,
            request,
            headers,
            xhr;

        if (me.fireEvent('beforerequest', me, options) !== false) {

            requestOptions = me.setOptions(options, scope);

            if (this.isFormUpload(options) === true) {
                this.upload(options.form, requestOptions.url, requestOptions.data, options);
                return null;
            }

            
            if (options.autoAbort === true || me.autoAbort) {
                me.abort();
            }

            

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr = new XDomainRequest();
            } else {
                xhr = this.getXhrInstance();
            }

            async = options.async !== false ? (options.async || me.async) : false;

            
            if (username) {
                xhr.open(requestOptions.method, requestOptions.url, async, username, password);
            } else {
                xhr.open(requestOptions.method, requestOptions.url, async);
            }

            if (options.withCredentials === true || me.withCredentials === true) {
                xhr.withCredentials = true;
            }

            headers = me.setupHeaders(xhr, options, requestOptions.data, requestOptions.params);

            
            request = {
                id: ++Ext.data.Connection.requestId,
                xhr: xhr,
                headers: headers,
                options: options,
                async: async,
                timeout: setTimeout(function() {
                    request.timedout = true;
                    me.abort(request);
                }, options.timeout || me.timeout)
            };
            me.requests[request.id] = request;
            me.latestId = request.id;
            
            if (async) {
                xhr.onreadystatechange = Ext.Function.bind(me.onStateChange, me, [request]);
            }

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr.onload = function() {
                    me.onComplete(request);
                }
            }

            
            xhr.send(requestOptions.data);
            if (!async) {
                return this.onComplete(request);
            }
            return request;
        } else {
            Ext.callback(options.callback, options.scope, [options, undefined, undefined]);
            return null;
        }
    }

});


String.prototype.startsWith = function(str) {
    return (this.match("^"+str)==str)
};
String.prototype.trim = function(){
    return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
};



SQGetIntValue = function(sourceId) {
    var source =  Ext.getCmp(sourceId);
    try {
        return parseInt(source.getValue());
    } catch (err) {
        return 0;
    }
};
SQCopyValue = function(sourceId, destinationId) {
    var value = SQGetIntValue(sourceId);
    var destination = Ext.getCmp(destinationId);
    destination.setValue(value);
};

SQSetAsRequired = function(compId, defaultValue) {
    var comp =  Ext.getCmp(compId);
    comp.allowBlank = false;
    if (defaultValue) {
        comp.setValue(defaultValue);
    }
};
SQSetAsOptional = function(compId, defaultValue) {
    var comp =  Ext.getCmp(compId);
    comp.allowBlank = true;
    if (defaultValue) {
        comp.setValue(defaultValue);
    }
};

var sqCopyObjectValue = function(sourceId, destinationId) {
    var source =  Ext.getCmp(sourceId);
    var destination =  Ext.getCmp(destinationId);
    try {
        destination.setValue(source.getValue());
    } catch (err) {
        destination.setValue(null);
    }
};
var sqUnmaskForm = function(formCmp) {
    if (OPF.isNotEmpty(formCmp) && OPF.isNotEmpty(formCmp.getEl()) && formCmp.getEl().isMasked()) {
        formCmp.getEl().unmask();
    }
};
var sqUnmaskAndResetForm = function(formCmp) {
    if (OPF.isNotEmpty(formCmp)) {
        sqUnmaskForm(formCmp);
        formCmp.getForm().reset();
    }
};


var sqTrim = function(s) {
    return OPF.isEmpty(s) ? s : s.replace(/^\s*/, "").replace(/\s*$/, "");
};

var cutting = function(s, length) {
    if (OPF.isNotEmpty(s)) {
        if (s.length > length) {
            s = s.substring(0, length - 3) + '...';
        }
    } 
    return s;
};

var reverse = function(array) {
    if (array == null) {
        return;
    }
    var i = 0;
    var j = array.length - 1;
    var tmp;
    while (j > i) {
        tmp = array[j];
        array[j] = array[i];
        array[i] = tmp;
        j--;
        i++;
    }
    return array;
};

var URL_CHECK_REGEXP = new RegExp().compile("^(http|https)://([\\w-]+\\.)+[\\w-]+(:\\d{1,5})?(/[\\w- ./?%&=]*)?$");
function urlCheck(url) {
    return URL_CHECK_REGEXP.test(url);
}


var cloneDate = function(sourceDate) {
    if (isEmpty(sourceDate)) {
        return null;
    }
    var date = new Date();
    date.setTime(sourceDate.getTime());
    return date;
};
var sqDateFromHidden = function(hiddenFieldId) {
    if (isEmpty(hiddenFieldId)) {
        return null;
    }
    return sqStrToDate(Ext.getCmp(hiddenFieldId).getValue());
};

var sqStrToDate = function(sDate) {
    var milliseconds = Date.parse(sDate);
    var date = new Date();
    date.setTime(milliseconds);
    return date;
};

var sqMillisecondToDate = function(milliseconds) {
    var date = new Date();
    date.setTime(milliseconds);
    return date;
};

var sqStrToInt = function(sNumber) {
    var number;
    try {
        number = parseInt(sNumber);
    } catch (e) {
        number = 0;
    }
    return number;
};

var sqTranslatedDate = function(date, time) {
    if (isEmpty(date)) {
        return null;
    }
    if (isNotEmpty(time) && Ext.isString(time) && sqTrim(time) != '') {
        var sDate = sqFormattedDate(date) + ' ' + time;
        return sqStrToDate(sDate);
    } else {
        return date;
    }
};

var sqGetTime = function(date) {
    if (isEmpty(date) || !Ext.isDate(date)) {
        return null;
    }
    return sqFormattedDate(date, 'h:i A');
};

var sqFormattedDate = function() {
    if (arguments.length == 0) {
        return (new Date()).format('m/d/Y');
    } else if (arguments.length <= 2) {
        if (Ext.isDate(arguments[0])) {
            var dateFormat = arguments.length == 2 && Ext.isString(arguments[1]) ? arguments[1] : 'm/d/Y';

            return Ext.Date.format(arguments[0], dateFormat);
        }
    }
    return null;
};

var sqFormatDateInMillis = function(dateInMillis, format) {
    if (isEmpty(dateInMillis)) {
        return '';
    }
    return sqFormattedDate(new Date(dateInMillis), format);
};

var sqFormatDateForJerseyParser = function(date) {
    if (!Ext.isDate(date)) { 
        date = new Date(date);
    }
    return date.format("Y-m-d\\TH:i:s.000O"); 
};


SqParseTreeEntityId = function(id) {
    var re = new RegExp(/xnode-(\w+)-(\d+)/g);
    return re.exec(id);
};

SqGetIdFromTreeEntityId = function(compositeId) {
    var m = SqParseTreeEntityId(compositeId);
    if (m != null && m.length == 3) {
        return m[2];
    }
    return null;
};

SqGetTypeFromTreeEntityId = function(compositeId) {
    var m = SqParseTreeEntityId(compositeId);
    if (m != null && m.length == 3) {
        return m[1];
    }
    return null;
};


























var sqSetValue = function(id, value) {
    if (isNotEmpty(id)) {
        var field = Ext.getCmp(id);
        field.setValue(value);
    }
};
var sqValueToNull = function(id) {
    sqSetValue(id, null);
};
var sqSetDisabled = function(cmpId, disabled) {
    if (isNotEmpty(cmpId) && isNotEmpty(disabled)) {
        var cmp;
        if (isNotEmpty(cmp = Ext.getCmp(cmpId))) {
            cmp.setDisabled(disabled);
            return true;
        }
    }
    return false;
};
var sqSetBatchEnabled = function(enabled, cmpIds) {
    if (isNotEmpty(enabled) && Ext.isArray(cmpIds)) {
        var disabled = enabled != true;
        for (i = 0; i < cmpIds.length; i++) {
            sqSetDisabled(cmpIds[i], disabled);
        }
    }
};

var sqIndexOf = function(array, item) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] == item) {
            return i;
        }
    }
    return -1;
};

var sqSetFormFieldVisibility = function(field, visible) {
    if (isNotEmpty(field) && isNotEmpty(visible)) {
        if (visible) {
            field.enable();
        } else {
            field.disable();
        }
        field.show();
        var labelEl = field.getEl();
        if (labelEl) {
            field.getEl().up('.x-form-item').setDisplayed(visible);
        }
    }
};

var sqSetComboValue = function(comboId, comboValue) {
    if (isNotEmpty(comboValue)) {
        var combo = Ext.getCmp(comboId);
        if (isNotEmpty(combo)) {
            combo.store.load();
            combo.setValue(comboValue);
            return true;
        }
    }
    return false;
};

var sqInitializeTabbedFormPanel = function(tabbedPanelId, activeTabIndex) {
    if (isNotEmpty(tabbedPanelId)) {
        var tabbedPanel = Ext.getCmp(tabbedPanelId);
        if (isNotEmpty(tabbedPanel)) {
            var tabItems = tabbedPanel.items;
            if (isNotEmpty(tabItems) && tabItems.length > 0) {
                Ext.each(tabItems, function(tabItem, index){
                    tabbedPanel.setActiveTab(tabItem);
                });
                var activeItemIndex = isEmpty(activeTabIndex) ||
                        activeTabIndex < 0 || activeTabIndex >= tabItems.length ?
                        0 : activeTabIndex;
                var activeTabItem = tabbedPanel.getComponent(activeItemIndex);
                tabbedPanel.setActiveTab(activeTabItem);
            }
        }
    }
};



















var sqAjaxGETRequest = function(requestUrl, successAction, failureAction) {
    Ext.Ajax.request({
        url: requestUrl,
        method: 'GET',

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus(response);
        }
    });
};

var sqAjaxPOSTRequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'POST', successAction, failureAction)
};

var sqAjaxPOSTRequest2 = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest2(requestUrl, jsonData, 'POST', successAction, failureAction)
};

var sqAjaxPUTRequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'PUT', successAction, failureAction)    
};

var sqAjaxDELETERequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'DELETE', successAction, failureAction)
};

var sqAjaxRequest = function(requestUrl, jsonData, method, successAction, failureAction) {
    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
    Ext.Ajax.request({
        url: requestUrl,
        method: method,
        jsonData: jsonData,

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus(response);
        }
    });
};

var sqAjaxRequest2 = function(requestUrl, jsonData, method, successAction, failureAction) {
    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
    Ext.Ajax.request({
        url: requestUrl,
        method: method,
        jsonData: jsonData,

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus2(response);
        }
    });
};

var sqShowFailureResponseStatus = function(resp) {
    if (isNotEmpty(resp)) {
        var msg;
        var responseStatus = Ext.decode(resp.responseText);
        if (isNotEmpty(responseStatus)) {
            if (isNotEmpty(responseStatus.errors)) {
                if (Ext.isArray(responseStatus.errors)) {
                    msg = 'Error' + (responseStatus.errors.length == 1 ? '' : 's') + ':\n';
                    for (var i = 0; i < responseStatus.errors.length; i++) {
                        msg += responseStatus.errors[i].msg + '\n';
                    }
                } else {
                    msg = 'Error:\n' + responseStatus.errors.msg;
                }
            } else {
                msg = responseStatus.toString();
            }
        } else {
            msg = 'Error occurred.';
        }
        Ext.MessageBox.alert('Failure', msg);
    }
};

var sqShowFailureResponseStatus2 = function(resp) {
    if (isNotEmpty(resp)) {
        var msg;
        
        var responseStatusWrapper = Ext.decode(resp.responseText);
        if (isNotEmpty(responseStatusWrapper)) {
            var responseStatus = responseStatusWrapper.responseStatus;
            if (isNotEmpty(responseStatus)) {
                if (isNotEmpty(responseStatus.errors)) {
                    if (Ext.isArray(responseStatus.errors)) {
                        msg = 'Error' + (responseStatus.errors.length == 1 ? '' : 's') + ':\n';
                        for (var i = 0; i < responseStatus.errors.length; i++) {
                            msg += responseStatus.errors[i].msg + '\n';
                        }
                    } else {
                        msg = 'Error:\n' + responseStatus.errors.msg;
                    }
                } else {
                    msg = responseStatus.toString();
                }
            } else {
                msg = 'Error occurred.';
            }
        }
        Ext.MessageBox.alert('Failure', msg);
    }
};

var sqDialogCancelAction = function(editor, customAction) {
    Ext.MessageBox.confirm("Cancel Action", "All changes will be lost, are you sure?",
        function(btn) {
            if ( btn[0] == 'y' ) {
                if (customAction != undefined &&
                        customAction != null) {
                    customAction();
                }
                editor.form.getForm().reset();
                editor.contentManager.showGrid();
            }
        }
    );
};

SQCopyMapCoords = function(latId, lngId, leadId, decayId, mapId) {
    var latValue =  Ext.getCmp(latId);
    if (latValue.getValue() == null) {
        latValue.setValue(SQMapDefault.lat);
    }
    var lngValue =  Ext.getCmp(lngId);
    if (lngValue.getValue() == null) {
        lngValue.setValue(SQMapDefault.lng);
    }
    var leadValue =  Ext.getCmp(leadId);
    var decayValue =  Ext.getCmp(decayId);
    var mapControl =  Ext.getCmp(mapId);
    mapControl.setLatLng(parseFloat(latValue.getValue()), parseFloat(lngValue.getValue()));
};

function capitaliseFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function isNotEmpty(obj) {
    return obj != undefined && obj != null;
}

function isEmpty(obj) {
    return obj == undefined || obj == null;
}

function isBlank(str) {
    return isEmpty(str) || str.length == 0
}

function isNotBlank(str) {
    return isNotEmpty(str) && str.length > 0;
}

function ifBlank(str, defaultValue) {
    if (isNotBlank(str)) {
        return str;
    }
    return defaultValue;
}

function ifEmpty(value, defaultValue) {
    if (isNotEmpty(value)) {
        return value;
    }
    return defaultValue;
}

function getJsonOfStore(store){
    var data = new Array();
    var records = store.getRange();
    for (var i = 0; i < records.length; i++) {
        data.push(records[i].data);
    }
    return data;
}

function shortenTextToDotSeparator(text, maxLen) {
    var subLookup = text;
    if (text.length > maxLen) {
        var names = text.split('.');
        var result = '';
        for (var i = names.length - 1; i > -1 ; i--) {
            var name = names[i];
            var subName = (i == names.length - 1) ? name : name + '.' + result;
            if (subName.length < maxLen) {
                result = subName;
            } else {
                break;
            }
        }
        if (result != text) {
            subLookup = '...' + result;
        }
    }
    return subLookup;
}


function calculateLookup(path, name) {
    var path = ifBlank(path, '');
    var name = ifBlank(name, '');
    var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
    var lookup = (isNotBlank(path) ? path + '.' : '') + normalizeName;
    return lookup.toLowerCase();
}



Ext.define('OPF.core.utils.StoreHelper', {
    statics: {

        
        cloneStore: function(storeToClone, configForNewStore, storeId, uniqueKeyField) {
            if (Ext.isString(storeToClone)) {
                storeToClone = Ext.StoreManager.get(storeToClone);
            }

            var modelName = storeToClone.model.modelName;
            var newStore = new Ext.data.Store(Ext.apply({
                model: modelName,
                storeId: storeId
            }, configForNewStore));

            OPF.core.utils.StoreHelper.delayUntilLoaded(storeToClone, function() {
                var records = [];
                var foundRecords = {};

                storeToClone.each(function(r){
                    
                    if (uniqueKeyField) {
                        var keyValue = r.get(uniqueKeyField);
                        if (foundRecords[keyValue]) {
                            return;
                        } else {
                            foundRecords[keyValue] = true;
                        }
                    }

                    var copy = r.copy();
                    var associations = copy.associations.items,
                        i, association, mine, theirs, instanceName;

                    for (i=0; i<associations.length; i++) {
                        association = associations[i];
                        instanceName = association.instanceName;
                        theirs = r[instanceName];
                        if (theirs) {
                            copy[instanceName] = theirs;
                        }
                    }
                    records.push(copy);
                });
                newStore.loadRecords(records);
            });
            return newStore;
        },

        copyRecords: function(sourceStore, targetStore) {
            if (Ext.isString(sourceStore)) {
                sourceStore = Ext.StoreManager.get(sourceStore);
            }

            if (Ext.isString(targetStore)) {
                targetStore = Ext.StoreManager.get(targetStore);
            }

            var records = [];
            var foundRecords = {};

            sourceStore.each(function(r) {
                var copy = r.copy();
                var associations = copy.associations.items,
                    i, association, theirs, instanceName;

                for (i = 0; i < associations.length; i++) {
                    association = associations[i];
                    instanceName = association.instanceName;
                    theirs = r[instanceName];
                    if (theirs) {
                        copy[instanceName] = theirs;
                    }
                }
                records.push(copy);
            });
            targetStore.loadRecords(records);
        },

        
        delayUntilLoaded: function(store, fn, scope) {
            var waitForLoad = 0;
            if (!Ext.isArray(store)) {
                store = [store];
            }

            function storeLoaded() {
                waitForLoad--;
                if (waitForLoad <= 0) {
                    fn.apply(scope, store);
                }
            }

            for (var i=0;i<store.length;i++) {
                var s = store[i];
                
                if (Ext.isString(s)) {
                    s = Ext.StoreManager.get(s);
                } else if (Ext.isFunction(s)) {
                    if (!s(storeLoaded, undefiend)) {
                        waitForLoad++;
                    }
                    break;
                }
                if (s.getCount() == 0 || s.loading) {
                    waitForLoad++;
                    s.on('load', storeLoaded, undefined, {
                        single: true
                    });
                }
            }

            
            if (waitForLoad == 0) {
                fn.apply(scope, store);
            }
        }

    }
});
OPF.StoreHelper = OPF.core.utils.StoreHelper;
Ext.override(Ext.data.Model, {
    copy: function () {
        var me   = this,
            copy = this.callParent(arguments);

        Ext.each(this.associations.items, function(association) {
            if (association.type == 'belongsTo') {
                var associationModel = Ext.create(association.model);
                copy[association.instanceName] = me[association.instanceName];
            } else if (association.type == 'hasMany') {
                var associationModels = me[association.name + 'Store'].getRange();
                var store = Ext.create('Ext.data.Store', {
                    model: association.model
                });
                store.add(associationModels);
                copy[association.name + 'Store'] = store;
            }
        });
        return copy;
    }
});


Ext.define('OPF.core.utils.progressbar.ProgressBarDialog', {
    extend:  Ext.window.Window ,

    id: 'socketProgressBar',
    modal: true,
    closable: true,
    closeAction: 'hide',
    resizable: false,
    cls: 'wizard-progress-bar',
    width: 400,

    title: 'Progress ...',
    logs: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.utils.progressbar.ProgressBarDialog.superclass.constructor.call(this, Ext.apply({

        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.progressBar = new Ext.ProgressBar({
            text:'Initializing...'
        });

        this.logStatusField = Ext.create('Ext.container.Container', {
            height: 200,
            autoScroll: true
        });

        this.logStatusPanel = Ext.create('Ext.panel.Panel', {
            title: 'More information',
            collapsible: true,

            width: 388,
            layout: 'fit',
            hidden: true,
            items: [
                this.logStatusField
            ]
        });

        this.items = [
            this.progressBar,
            this.logStatusPanel
        ];

        this.setupComponent();

        this.callParent(arguments);
    },

    listeners: {
        beforeshow: function(win) {
            win.resetProgressBar();
        },
        beforeclose: function(win) {
            win.resetProgressBar();
            interruptProgress();
        }
    },

    setupComponent: function() {
        
    },

    showStatus: function(percent, message, logLevel) {
        if (!this.isVisible()) {
            this.show();
        }

        consoleLog('Progress status is ' + percent + '% - ' + logLevel + ': ' + message);

        percent = percent / 100;
        if (percent > 1) {
            percent = 1;
        }

        this.progressBar.updateProgress(percent, message, true);
        this.addLog(message, logLevel);
    },

    showCompletedStatus: function(message, logLevel) {
        if (!this.isVisible()) {
            this.show();
        }

        consoleLog('Progress status is 100% - ' + logLevel + ': ' + message);

        this.progressBar.updateProgress(1, message, true);
        this.addLog(message, logLevel);

        if (this.logStatusPanel.collapsed || !this.logStatusPanel.isVisible()) {
            this.resetProgressBar();
            this.close();
        }
    },

    showErrorMessage: function(message) {
        if (this.logStatusPanel.collapsed) {
            this.resetProgressBar();
            this.close();
            OPF.Msg.setAlert(false, message);
        } else {
            this.addLog(message, 'ERROR');
        }
    },

    resetProgressBar: function() {
        this.logs = [];
        this.progressBar.reset();
        this.logStatusField.removeAll();
    },

    addLog: function(message, logLevel) {
        var me = this;
        if (this.logStatusPanel.isVisible()) {
            var logLevelCls = OPF.isNotBlank(logLevel) ? logLevel.toLowerCase() : 'none';
            var log = Ext.create('Ext.container.Container', {
                autoEl: {
                    tag: 'div',
                    html: message
                },
                cls: 'progress-bar-' + logLevelCls
            });
            this.logs.push(log);

            if (OPF.isNotEmpty(this.logStatusField.el)) {
                Ext.each(this.logs, function(log) {
                    me.logStatusField.add(log);
                });
                this.logs = [];

                var logContainer = this.logStatusField.el.dom;
                logContainer.scrollTop = logContainer.scrollHeight;
            }
        }
    },

    setLogVisible: function(showLogs) {
        this.logStatusPanel.setVisible(showLogs && OPF.Cfg.DEBUG_MODE);
    }

});

OPF.core.utils.progressbar.ProgressBarDialog.get = function() {
    var progressBar = Ext.WindowMgr.get('socketProgressBar');
    if (OPF.isEmpty(progressBar)) {
        progressBar = Ext.create('OPF.core.utils.progressbar.ProgressBarDialog');
        Ext.WindowMgr.register(progressBar);
    }
    return progressBar;
};


var ajaxSuccessFunction;
var ajaxFailureFunction;
var isInterrupted = false;

var AJAX_STRATEGY = 'ajax';
var FLASH_STRATEGY = 'flash';

var PROGRESS_BAR_STRATEGY = AJAX_STRATEGY;

function consoleLog(message) {
    if ((Ext.isGecko || Ext.isChrome) && OPF.Cfg.DEBUG_MODE) {
        console.log(message);
    }
}

function onConnected() {
    PROGRESS_BAR_STRATEGY = FLASH_STRATEGY;
    consoleLog('onConnected ' + new Date());
}

function onDisconnected() {
    PROGRESS_BAR_STRATEGY = AJAX_STRATEGY;
    consoleLog('onDisconnected ' + new Date());
}

function onError(error) {
    consoleLog('onError: ' + error);
}

function onMessage(data) {
    var responseData = eval('(' + data + ')');
    consoleLog('onMessage: ' + data);
    performProgressBar(responseData);
}

function loadProgress(isBaseUrl, forceLoadProgress) {
    if (!isInterrupted || forceLoadProgress) {
        isInterrupted = false;
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('progress/status', isBaseUrl),
            method: 'GET',

            success: function(response, action) {
                consoleLog('Load Progress success: ' + response.responseText);

                var responseData = Ext.decode(response.responseText);
                var logs = responseData.data;

                var isNext = !isInterrupted;
                Ext.each(logs, function(log) {
                    isNext &= performProgressBar(log);
                });

                if (isNext) {
                    new Ext.util.DelayedTask( function() {
                        loadProgress(isBaseUrl, false)
                    }).delay(500);
                } else {
                    consoleLog('Close progress bar');

                    var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                    progressBar.close();
                }
            },

            failure: function(response) {
                consoleLog('Load Progress failure: ' + response);
                OPF.Msg.setAlert(false, response.message);

                var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                progressBar.close();

                ajaxFailureFunction(response);
            }
        });
    } else {
        isInterrupted = false;
    }
}

function interruptProgress() {
    isInterrupted = true;
}

function performProgressBar(responseData) {
    var result;
    var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
    if (responseData.data && responseData.data[0].dtoName == 'net.firejack.platform.web.mina.bean.Status') {
        if (responseData.data[0].type == 'ERROR') {
            progressBar.showErrorMessage(responseData.message);
            ajaxFailureFunction({
                message: responseData.message,
                success: false
            });
            result = false;
        } else {
            if (responseData.data[0].type == 'STARTED') {
                progressBar.setLogVisible(responseData.data[0].showLogs);
            }
            progressBar.showStatus(responseData.data[0].percent, responseData.data[0].title, responseData.data[0].logLevel);
            result = true;
        }
    } else {
        var response = {
            responseText: '(' + Ext.JSON.encode(responseData) + ')'
        };

        consoleLog('Execute ajax function: ' + responseData.success);

        if (responseData.success) {
            progressBar.showCompletedStatus(responseData.message, null);
            ajaxSuccessFunction(response);
        } else {
            progressBar.showErrorMessage(responseData.message);
            ajaxFailureFunction(response);
        }
        result = false;
    }
    return result;
}

Ext.override(Ext.form.Basic, {

    doAction: function(action, options) {
        if (!(/Page-UID=/g.test(this.url))) {
            this.url += /\?/g.test(this.url) ? '&' : '?';
            this.url += 'Page-UID=' + OPF.Cfg.PAGE_UID;
        }
        
        if (Ext.isString(action)) {
            action = Ext.ClassManager.instantiateByAlias('formaction.' + action, Ext.apply({}, options, {form: this}));
        }
        if (this.fireEvent('beforeaction', this, action) !== false) {
            action.options = options;
            this.beforeAction(action);
            Ext.defer(action.run, 100, action);
        }
        return this;
    },

    beforeAction : function(action){
        
        if (OPF.isNotEmpty(this.items)) {
            for (var i = 0; i < this.items.length; i++) {
                for (var j = 0; j < this.items[i].length; j++) {
                    var f = this.items[i][j];
                    if (f.isFormField && f.syncValue) {
                        f.syncValue();
                    }
                }
            }
        }
        var options = action.options;
        if (options) {
            if(options.waitMsg){
                if(this.waitMsgTarget === true){
                    this.el.mask(options.waitMsg, 'x-mask-loading');
                }else if(this.waitMsgTarget){
                    this.waitMsgTarget = Ext.get(this.waitMsgTarget);
                    this.waitMsgTarget.mask(options.waitMsg, 'x-mask-loading');
                }else{
                    Ext.MessageBox.wait(options.waitMsg, options.waitTitle || this.waitTitle);
                }
            }

            if(options.success) {
                ajaxSuccessFunction = function(response) {
                    var action = {
                        response: response
                    };
                    options.success(this, action);
                };
            }
            if(options.failure) {
                ajaxFailureFunction = function(response) {
                    var action = {
                        response: response
                    };
                    options.failure(this, action);
                };
            }
            if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                var isBaseUrl = action.form.url.startsWith(OPF.Cfg.BASE_URL);
                var isOPFUrl = action.form.url.startsWith(OPF.Cfg.OPF_CONSOLE_URL);
                loadProgress(isBaseUrl && !isOPFUrl, true);
            }
        }
    },

    afterAction : function(action, success){
        this.activeAction = null;
        var options = action.options;
        if(options.waitMsg){
            if(this.waitMsgTarget === true){
                this.el.unmask();
            }else if(this.waitMsgTarget){
                this.waitMsgTarget.unmask();
            }else{
                Ext.MessageBox.updateProgress(1);
                Ext.MessageBox.hide();
            }
        }

        var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();

        var responseData = Ext.decode(action.response.responseText);
        if (responseData.data && responseData.data[0].dtoName == 'net.firejack.platform.web.mina.bean.Status') {
            if (responseData.data[0].type == 'ERROR') {
                if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                    interruptProgress();
                }
                progressBar.showErrorMessage(responseData.message);
            } else {
                if (responseData.data[0].type == 'STARTED') {
                    progressBar.setLogVisible(responseData.data[0].showLogs);
                }
                progressBar.showStatus(responseData.data[0].percent, responseData.data[0].title, responseData.data[0].logLevel);
            }
        } else {
            if (success) {
                if (options.reset) {
                    this.reset();
                }
                progressBar.showCompletedStatus(responseData.message, 'UPLOAD');
                Ext.callback(options.success, options.scope, [this, action]);
                this.fireEvent('actioncomplete', this, action);
            } else {
                progressBar.showErrorMessage(responseData.message);
                Ext.callback(options.failure, options.scope, [this, action]);
                this.fireEvent('actionfailed', this, action);
            }
            if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                interruptProgress();
            }
        }
    }
});

Ext.override(Ext.data.Connection, {

    onComplete : function(request) {
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            
            result = {
                success : false,
                isException : false
            };
        }
        success = result.success;

        if (success) {
            response = me.createResponse(request);

            me.fireEvent('requestcomplete', me, response, options);

            var responseData = eval('(' + OPF.ifBlank(response.responseText, '{}') + ')');
            var progressData;
            if (OPF.isNotEmpty(responseData.data)) {
                progressData = responseData.data[0];
            }
            if (OPF.isNotEmpty(progressData) && progressData.dtoName == 'net.firejack.platform.web.mina.bean.Status') {
                var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                if (progressData.type == 'ERROR') {
                    progressBar.showErrorMessage(responseData.message);
                } else {
                    if (responseData.data[0].type == 'STARTED') {
                        progressBar.setLogVisible(responseData.data[0].showLogs);
                    }
                    progressBar.showStatus(progressData.percent, progressData.title, progressData.logLevel);
                    if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                        var isBaseUrl = options.url.startsWith(OPF.Cfg.BASE_URL);
                        var isOPFUrl = options.url.startsWith(OPF.Cfg.OPF_CONSOLE_URL);
                        loadProgress(isBaseUrl && !isOPFUrl, true);
                    }
                }
                if (options.success) {
                    ajaxSuccessFunction = options.success;
                }
                if (options.failure) {
                    ajaxFailureFunction = options.failure;
                }
            } else {
                Ext.callback(options.success, options.scope, [response, options]);
            }
        } else {
            if (result.isException || request.aborted || request.timedout) {
                response = me.createException(request);
            } else {
                response = me.createResponse(request);
            }
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    }

});





var dbits;


var canary = 0xdeadbeefcafe;
var j_lm = ((canary&0xffffff)==0xefcafe);


function BigInteger(a,b,c) {
  if(a != null)
    if("number" == typeof a) this.fromNumber(a,b,c);
    else if(b == null && "string" != typeof a) this.fromString(a,256);
    else this.fromString(a,b);
}


function nbi() { return new BigInteger(null); }









function am1(i,x,w,j,c,n) {
  while(--n >= 0) {
    var v = x*this[i++]+w[j]+c;
    c = Math.floor(v/0x4000000);
    w[j++] = v&0x3ffffff;
  }
  return c;
}



function am2(i,x,w,j,c,n) {
  var xl = x&0x7fff, xh = x>>15;
  while(--n >= 0) {
    var l = this[i]&0x7fff;
    var h = this[i++]>>15;
    var m = xh*l+h*xl;
    l = xl*l+((m&0x7fff)<<15)+w[j]+(c&0x3fffffff);
    c = (l>>>30)+(m>>>15)+xh*h+(c>>>30);
    w[j++] = l&0x3fffffff;
  }
  return c;
}


function am3(i,x,w,j,c,n) {
  var xl = x&0x3fff, xh = x>>14;
  while(--n >= 0) {
    var l = this[i]&0x3fff;
    var h = this[i++]>>14;
    var m = xh*l+h*xl;
    l = xl*l+((m&0x3fff)<<14)+w[j]+c;
    c = (l>>28)+(m>>14)+xh*h;
    w[j++] = l&0xfffffff;
  }
  return c;
}
if(j_lm && (navigator.appName == "Microsoft Internet Explorer")) {
  BigInteger.prototype.am = am2;
  dbits = 30;
}
else if(j_lm && (navigator.appName != "Netscape")) {
  BigInteger.prototype.am = am1;
  dbits = 26;
}
else { 
  BigInteger.prototype.am = am3;
  dbits = 28;
}

BigInteger.prototype.DB = dbits;
BigInteger.prototype.DM = ((1<<dbits)-1);
BigInteger.prototype.DV = (1<<dbits);

var BI_FP = 52;
BigInteger.prototype.FV = Math.pow(2,BI_FP);
BigInteger.prototype.F1 = BI_FP-dbits;
BigInteger.prototype.F2 = 2*dbits-BI_FP;


var BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
var BI_RC = new Array();
var rr,vv;
rr = "0".charCodeAt(0);
for(vv = 0; vv <= 9; ++vv) BI_RC[rr++] = vv;
rr = "a".charCodeAt(0);
for(vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;
rr = "A".charCodeAt(0);
for(vv = 10; vv < 36; ++vv) BI_RC[rr++] = vv;

function int2char(n) { return BI_RM.charAt(n); }
function intAt(s,i) {
  var c = BI_RC[s.charCodeAt(i)];
  return (c==null)?-1:c;
}


function bnpCopyTo(r) {
  for(var i = this.t-1; i >= 0; --i) r[i] = this[i];
  r.t = this.t;
  r.s = this.s;
}


function bnpFromInt(x) {
  this.t = 1;
  this.s = (x<0)?-1:0;
  if(x > 0) this[0] = x;
  else if(x < -1) this[0] = x+DV;
  else this.t = 0;
}


function nbv(i) { var r = nbi(); r.fromInt(i); return r; }


function bnpFromString(s,b) {
  var k;
  if(b == 16) k = 4;
  else if(b == 8) k = 3;
  else if(b == 256) k = 8; 
  else if(b == 2) k = 1;
  else if(b == 32) k = 5;
  else if(b == 4) k = 2;
  else { this.fromRadix(s,b); return; }
  this.t = 0;
  this.s = 0;
  var i = s.length, mi = false, sh = 0;
  while(--i >= 0) {
    var x = (k==8)?s[i]&0xff:intAt(s,i);
    if(x < 0) {
      if(s.charAt(i) == "-") mi = true;
      continue;
    }
    mi = false;
    if(sh == 0)
      this[this.t++] = x;
    else if(sh+k > this.DB) {
      this[this.t-1] |= (x&((1<<(this.DB-sh))-1))<<sh;
      this[this.t++] = (x>>(this.DB-sh));
    }
    else
      this[this.t-1] |= x<<sh;
    sh += k;
    if(sh >= this.DB) sh -= this.DB;
  }
  if(k == 8 && (s[0]&0x80) != 0) {
    this.s = -1;
    if(sh > 0) this[this.t-1] |= ((1<<(this.DB-sh))-1)<<sh;
  }
  this.clamp();
  if(mi) BigInteger.ZERO.subTo(this,this);
}


function bnpClamp() {
  var c = this.s&this.DM;
  while(this.t > 0 && this[this.t-1] == c) --this.t;
}


function bnToString(b) {
  if(this.s < 0) return "-"+this.negate().toString(b);
  var k;
  if(b == 16) k = 4;
  else if(b == 8) k = 3;
  else if(b == 2) k = 1;
  else if(b == 32) k = 5;
  else if(b == 4) k = 2;
  else return this.toRadix(b);
  var km = (1<<k)-1, d, m = false, r = "", i = this.t;
  var p = this.DB-(i*this.DB)%k;
  if(i-- > 0) {
    if(p < this.DB && (d = this[i]>>p) > 0) { m = true; r = int2char(d); }
    while(i >= 0) {
      if(p < k) {
        d = (this[i]&((1<<p)-1))<<(k-p);
        d |= this[--i]>>(p+=this.DB-k);
      }
      else {
        d = (this[i]>>(p-=k))&km;
        if(p <= 0) { p += this.DB; --i; }
      }
      if(d > 0) m = true;
      if(m) r += int2char(d);
    }
  }
  return m?r:"0";
}


function bnNegate() { var r = nbi(); BigInteger.ZERO.subTo(this,r); return r; }


function bnAbs() { return (this.s<0)?this.negate():this; }


function bnCompareTo(a) {
  var r = this.s-a.s;
  if(r != 0) return r;
  var i = this.t;
  r = i-a.t;
  if(r != 0) return r;
  while(--i >= 0) if((r=this[i]-a[i]) != 0) return r;
  return 0;
}


function nbits(x) {
  var r = 1, t;
  if((t=x>>>16) != 0) { x = t; r += 16; }
  if((t=x>>8) != 0) { x = t; r += 8; }
  if((t=x>>4) != 0) { x = t; r += 4; }
  if((t=x>>2) != 0) { x = t; r += 2; }
  if((t=x>>1) != 0) { x = t; r += 1; }
  return r;
}


function bnBitLength() {
  if(this.t <= 0) return 0;
  return this.DB*(this.t-1)+nbits(this[this.t-1]^(this.s&this.DM));
}


function bnpDLShiftTo(n,r) {
  var i;
  for(i = this.t-1; i >= 0; --i) r[i+n] = this[i];
  for(i = n-1; i >= 0; --i) r[i] = 0;
  r.t = this.t+n;
  r.s = this.s;
}


function bnpDRShiftTo(n,r) {
  for(var i = n; i < this.t; ++i) r[i-n] = this[i];
  r.t = Math.max(this.t-n,0);
  r.s = this.s;
}


function bnpLShiftTo(n,r) {
  var bs = n%this.DB;
  var cbs = this.DB-bs;
  var bm = (1<<cbs)-1;
  var ds = Math.floor(n/this.DB), c = (this.s<<bs)&this.DM, i;
  for(i = this.t-1; i >= 0; --i) {
    r[i+ds+1] = (this[i]>>cbs)|c;
    c = (this[i]&bm)<<bs;
  }
  for(i = ds-1; i >= 0; --i) r[i] = 0;
  r[ds] = c;
  r.t = this.t+ds+1;
  r.s = this.s;
  r.clamp();
}


function bnpRShiftTo(n,r) {
  r.s = this.s;
  var ds = Math.floor(n/this.DB);
  if(ds >= this.t) { r.t = 0; return; }
  var bs = n%this.DB;
  var cbs = this.DB-bs;
  var bm = (1<<bs)-1;
  r[0] = this[ds]>>bs;
  for(var i = ds+1; i < this.t; ++i) {
    r[i-ds-1] |= (this[i]&bm)<<cbs;
    r[i-ds] = this[i]>>bs;
  }
  if(bs > 0) r[this.t-ds-1] |= (this.s&bm)<<cbs;
  r.t = this.t-ds;
  r.clamp();
}


function bnpSubTo(a,r) {
  var i = 0, c = 0, m = Math.min(a.t,this.t);
  while(i < m) {
    c += this[i]-a[i];
    r[i++] = c&this.DM;
    c >>= this.DB;
  }
  if(a.t < this.t) {
    c -= a.s;
    while(i < this.t) {
      c += this[i];
      r[i++] = c&this.DM;
      c >>= this.DB;
    }
    c += this.s;
  }
  else {
    c += this.s;
    while(i < a.t) {
      c -= a[i];
      r[i++] = c&this.DM;
      c >>= this.DB;
    }
    c -= a.s;
  }
  r.s = (c<0)?-1:0;
  if(c < -1) r[i++] = this.DV+c;
  else if(c > 0) r[i++] = c;
  r.t = i;
  r.clamp();
}



function bnpMultiplyTo(a,r) {
  var x = this.abs(), y = a.abs();
  var i = x.t;
  r.t = i+y.t;
  while(--i >= 0) r[i] = 0;
  for(i = 0; i < y.t; ++i) r[i+x.t] = x.am(0,y[i],r,i,0,x.t);
  r.s = 0;
  r.clamp();
  if(this.s != a.s) BigInteger.ZERO.subTo(r,r);
}


function bnpSquareTo(r) {
  var x = this.abs();
  var i = r.t = 2*x.t;
  while(--i >= 0) r[i] = 0;
  for(i = 0; i < x.t-1; ++i) {
    var c = x.am(i,x[i],r,2*i,0,1);
    if((r[i+x.t]+=x.am(i+1,2*x[i],r,2*i+1,c,x.t-i-1)) >= x.DV) {
      r[i+x.t] -= x.DV;
      r[i+x.t+1] = 1;
    }
  }
  if(r.t > 0) r[r.t-1] += x.am(i,x[i],r,2*i,0,1);
  r.s = 0;
  r.clamp();
}



function bnpDivRemTo(m,q,r) {
  var pm = m.abs();
  if(pm.t <= 0) return;
  var pt = this.abs();
  if(pt.t < pm.t) {
    if(q != null) q.fromInt(0);
    if(r != null) this.copyTo(r);
    return;
  }
  if(r == null) r = nbi();
  var y = nbi(), ts = this.s, ms = m.s;
  var nsh = this.DB-nbits(pm[pm.t-1]);    
  if(nsh > 0) { pm.lShiftTo(nsh,y); pt.lShiftTo(nsh,r); }
  else { pm.copyTo(y); pt.copyTo(r); }
  var ys = y.t;
  var y0 = y[ys-1];
  if(y0 == 0) return;
  var yt = y0*(1<<this.F1)+((ys>1)?y[ys-2]>>this.F2:0);
  var d1 = this.FV/yt, d2 = (1<<this.F1)/yt, e = 1<<this.F2;
  var i = r.t, j = i-ys, t = (q==null)?nbi():q;
  y.dlShiftTo(j,t);
  if(r.compareTo(t) >= 0) {
    r[r.t++] = 1;
    r.subTo(t,r);
  }
  BigInteger.ONE.dlShiftTo(ys,t);
  t.subTo(y,y);    
  while(y.t < ys) y[y.t++] = 0;
  while(--j >= 0) {
    
    var qd = (r[--i]==y0)?this.DM:Math.floor(r[i]*d1+(r[i-1]+e)*d2);
    if((r[i]+=y.am(0,qd,r,j,0,ys)) < qd) {    
      y.dlShiftTo(j,t);
      r.subTo(t,r);
      while(r[i] < --qd) r.subTo(t,r);
    }
  }
  if(q != null) {
    r.drShiftTo(ys,q);
    if(ts != ms) BigInteger.ZERO.subTo(q,q);
  }
  r.t = ys;
  r.clamp();
  if(nsh > 0) r.rShiftTo(nsh,r);    
  if(ts < 0) BigInteger.ZERO.subTo(r,r);
}


function bnMod(a) {
  var r = nbi();
  this.abs().divRemTo(a,null,r);
  if(this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r,r);
  return r;
}


function Classic(m) { this.m = m; }
function cConvert(x) {
  if(x.s < 0 || x.compareTo(this.m) >= 0) return x.mod(this.m);
  else return x;
}
function cRevert(x) { return x; }
function cReduce(x) { x.divRemTo(this.m,null,x); }
function cMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }
function cSqrTo(x,r) { x.squareTo(r); this.reduce(r); }

Classic.prototype.convert = cConvert;
Classic.prototype.revert = cRevert;
Classic.prototype.reduce = cReduce;
Classic.prototype.mulTo = cMulTo;
Classic.prototype.sqrTo = cSqrTo;











function bnpInvDigit() {
  if(this.t < 1) return 0;
  var x = this[0];
  if((x&1) == 0) return 0;
  var y = x&3;        
  y = (y*(2-(x&0xf)*y))&0xf;    
  y = (y*(2-(x&0xff)*y))&0xff;    
  y = (y*(2-(((x&0xffff)*y)&0xffff)))&0xffff;    
  
  
  y = (y*(2-x*y%this.DV))%this.DV;        
  
  return (y>0)?this.DV-y:-y;
}


function Montgomery(m) {
  this.m = m;
  this.mp = m.invDigit();
  this.mpl = this.mp&0x7fff;
  this.mph = this.mp>>15;
  this.um = (1<<(m.DB-15))-1;
  this.mt2 = 2*m.t;
}


function montConvert(x) {
  var r = nbi();
  x.abs().dlShiftTo(this.m.t,r);
  r.divRemTo(this.m,null,r);
  if(x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r,r);
  return r;
}


function montRevert(x) {
  var r = nbi();
  x.copyTo(r);
  this.reduce(r);
  return r;
}


function montReduce(x) {
  while(x.t <= this.mt2)    
    x[x.t++] = 0;
  for(var i = 0; i < this.m.t; ++i) {
    
    var j = x[i]&0x7fff;
    var u0 = (j*this.mpl+(((j*this.mph+(x[i]>>15)*this.mpl)&this.um)<<15))&x.DM;
    
    j = i+this.m.t;
    x[j] += this.m.am(0,u0,x,i,0,this.m.t);
    
    while(x[j] >= x.DV) { x[j] -= x.DV; x[++j]++; }
  }
  x.clamp();
  x.drShiftTo(this.m.t,x);
  if(x.compareTo(this.m) >= 0) x.subTo(this.m,x);
}


function montSqrTo(x,r) { x.squareTo(r); this.reduce(r); }


function montMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }

Montgomery.prototype.convert = montConvert;
Montgomery.prototype.revert = montRevert;
Montgomery.prototype.reduce = montReduce;
Montgomery.prototype.mulTo = montMulTo;
Montgomery.prototype.sqrTo = montSqrTo;


function bnpIsEven() { return ((this.t>0)?(this[0]&1):this.s) == 0; }


function bnpExp(e,z) {
  if(e > 0xffffffff || e < 1) return BigInteger.ONE;
  var r = nbi(), r2 = nbi(), g = z.convert(this), i = nbits(e)-1;
  g.copyTo(r);
  while(--i >= 0) {
    z.sqrTo(r,r2);
    if((e&(1<<i)) > 0) z.mulTo(r2,g,r);
    else { var t = r; r = r2; r2 = t; }
  }
  return z.revert(r);
}


function bnModPowInt(e,m) {
  var z;
  if(e < 256 || m.isEven()) z = new Classic(m); else z = new Montgomery(m);
  return this.exp(e,z);
}


BigInteger.prototype.copyTo = bnpCopyTo;
BigInteger.prototype.fromInt = bnpFromInt;
BigInteger.prototype.fromString = bnpFromString;
BigInteger.prototype.clamp = bnpClamp;
BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
BigInteger.prototype.drShiftTo = bnpDRShiftTo;
BigInteger.prototype.lShiftTo = bnpLShiftTo;
BigInteger.prototype.rShiftTo = bnpRShiftTo;
BigInteger.prototype.subTo = bnpSubTo;
BigInteger.prototype.multiplyTo = bnpMultiplyTo;
BigInteger.prototype.squareTo = bnpSquareTo;
BigInteger.prototype.divRemTo = bnpDivRemTo;
BigInteger.prototype.invDigit = bnpInvDigit;
BigInteger.prototype.isEven = bnpIsEven;
BigInteger.prototype.exp = bnpExp;


BigInteger.prototype.toString = bnToString;
BigInteger.prototype.negate = bnNegate;
BigInteger.prototype.abs = bnAbs;
BigInteger.prototype.compareTo = bnCompareTo;
BigInteger.prototype.bitLength = bnBitLength;
BigInteger.prototype.mod = bnMod;
BigInteger.prototype.modPowInt = bnModPowInt;


BigInteger.ZERO = nbv(0);
BigInteger.ONE = nbv(1);









function bnClone() { var r = nbi(); this.copyTo(r); return r; }


function bnIntValue() {
  if(this.s < 0) {
    if(this.t == 1) return this[0]-this.DV;
    else if(this.t == 0) return -1;
  }
  else if(this.t == 1) return this[0];
  else if(this.t == 0) return 0;
  
  return ((this[1]&((1<<(32-this.DB))-1))<<this.DB)|this[0];
}


function bnByteValue() { return (this.t==0)?this.s:(this[0]<<24)>>24; }


function bnShortValue() { return (this.t==0)?this.s:(this[0]<<16)>>16; }


function bnpChunkSize(r) { return Math.floor(Math.LN2*this.DB/Math.log(r)); }


function bnSigNum() {
  if(this.s < 0) return -1;
  else if(this.t <= 0 || (this.t == 1 && this[0] <= 0)) return 0;
  else return 1;
}


function bnpToRadix(b) {
  if(b == null) b = 10;
  if(this.signum() == 0 || b < 2 || b > 36) return "0";
  var cs = this.chunkSize(b);
  var a = Math.pow(b,cs);
  var d = nbv(a), y = nbi(), z = nbi(), r = "";
  this.divRemTo(d,y,z);
  while(y.signum() > 0) {
    r = (a+z.intValue()).toString(b).substr(1) + r;
    y.divRemTo(d,y,z);
  }
  return z.intValue().toString(b) + r;
}


function bnpFromRadix(s,b) {
  this.fromInt(0);
  if(b == null) b = 10;
  var cs = this.chunkSize(b);
  var d = Math.pow(b,cs), mi = false, j = 0, w = 0;
  for(var i = 0; i < s.length; ++i) {
    var x = intAt(s,i);
    if(x < 0) {
      if(s.charAt(i) == "-" && this.signum() == 0) mi = true;
      continue;
    }
    w = b*w+x;
    if(++j >= cs) {
      this.dMultiply(d);
      this.dAddOffset(w,0);
      j = 0;
      w = 0;
    }
  }
  if(j > 0) {
    this.dMultiply(Math.pow(b,j));
    this.dAddOffset(w,0);
  }
  if(mi) BigInteger.ZERO.subTo(this,this);
}


function bnpFromNumber(a,b,c) {
  if("number" == typeof b) {
    
    if(a < 2) this.fromInt(1);
    else {
      this.fromNumber(a,c);
      if(!this.testBit(a-1))    
        this.bitwiseTo(BigInteger.ONE.shiftLeft(a-1),op_or,this);
      if(this.isEven()) this.dAddOffset(1,0); 
      while(!this.isProbablePrime(b)) {
        this.dAddOffset(2,0);
        if(this.bitLength() > a) this.subTo(BigInteger.ONE.shiftLeft(a-1),this);
      }
    }
  }
  else {
    
    var x = new Array(), t = a&7;
    x.length = (a>>3)+1;
    b.nextBytes(x);
    if(t > 0) x[0] &= ((1<<t)-1); else x[0] = 0;
    this.fromString(x,256);
  }
}


function bnToByteArray() {
  var i = this.t, r = new Array();
  r[0] = this.s;
  var p = this.DB-(i*this.DB)%8, d, k = 0;
  if(i-- > 0) {
    if(p < this.DB && (d = this[i]>>p) != (this.s&this.DM)>>p)
      r[k++] = d|(this.s<<(this.DB-p));
    while(i >= 0) {
      if(p < 8) {
        d = (this[i]&((1<<p)-1))<<(8-p);
        d |= this[--i]>>(p+=this.DB-8);
      }
      else {
        d = (this[i]>>(p-=8))&0xff;
        if(p <= 0) { p += this.DB; --i; }
      }
      if((d&0x80) != 0) d |= -256;
      if(k == 0 && (this.s&0x80) != (d&0x80)) ++k;
      if(k > 0 || d != this.s) r[k++] = d;
    }
  }
  return r;
}

function bnEquals(a) { return(this.compareTo(a)==0); }
function bnMin(a) { return(this.compareTo(a)<0)?this:a; }
function bnMax(a) { return(this.compareTo(a)>0)?this:a; }


function bnpBitwiseTo(a,op,r) {
  var i, f, m = Math.min(a.t,this.t);
  for(i = 0; i < m; ++i) r[i] = op(this[i],a[i]);
  if(a.t < this.t) {
    f = a.s&this.DM;
    for(i = m; i < this.t; ++i) r[i] = op(this[i],f);
    r.t = this.t;
  }
  else {
    f = this.s&this.DM;
    for(i = m; i < a.t; ++i) r[i] = op(f,a[i]);
    r.t = a.t;
  }
  r.s = op(this.s,a.s);
  r.clamp();
}


function op_and(x,y) { return x&y; }
function bnAnd(a) { var r = nbi(); this.bitwiseTo(a,op_and,r); return r; }


function op_or(x,y) { return x|y; }
function bnOr(a) { var r = nbi(); this.bitwiseTo(a,op_or,r); return r; }


function op_xor(x,y) { return x^y; }
function bnXor(a) { var r = nbi(); this.bitwiseTo(a,op_xor,r); return r; }


function op_andnot(x,y) { return x&~y; }
function bnAndNot(a) { var r = nbi(); this.bitwiseTo(a,op_andnot,r); return r; }


function bnNot() {
  var r = nbi();
  for(var i = 0; i < this.t; ++i) r[i] = this.DM&~this[i];
  r.t = this.t;
  r.s = ~this.s;
  return r;
}


function bnShiftLeft(n) {
  var r = nbi();
  if(n < 0) this.rShiftTo(-n,r); else this.lShiftTo(n,r);
  return r;
}


function bnShiftRight(n) {
  var r = nbi();
  if(n < 0) this.lShiftTo(-n,r); else this.rShiftTo(n,r);
  return r;
}


function lbit(x) {
  if(x == 0) return -1;
  var r = 0;
  if((x&0xffff) == 0) { x >>= 16; r += 16; }
  if((x&0xff) == 0) { x >>= 8; r += 8; }
  if((x&0xf) == 0) { x >>= 4; r += 4; }
  if((x&3) == 0) { x >>= 2; r += 2; }
  if((x&1) == 0) ++r;
  return r;
}


function bnGetLowestSetBit() {
  for(var i = 0; i < this.t; ++i)
    if(this[i] != 0) return i*this.DB+lbit(this[i]);
  if(this.s < 0) return this.t*this.DB;
  return -1;
}


function cbit(x) {
  var r = 0;
  while(x != 0) { x &= x-1; ++r; }
  return r;
}


function bnBitCount() {
  var r = 0, x = this.s&this.DM;
  for(var i = 0; i < this.t; ++i) r += cbit(this[i]^x);
  return r;
}


function bnTestBit(n) {
  var j = Math.floor(n/this.DB);
  if(j >= this.t) return(this.s!=0);
  return((this[j]&(1<<(n%this.DB)))!=0);
}


function bnpChangeBit(n,op) {
  var r = BigInteger.ONE.shiftLeft(n);
  this.bitwiseTo(r,op,r);
  return r;
}


function bnSetBit(n) { return this.changeBit(n,op_or); }


function bnClearBit(n) { return this.changeBit(n,op_andnot); }


function bnFlipBit(n) { return this.changeBit(n,op_xor); }


function bnpAddTo(a,r) {
  var i = 0, c = 0, m = Math.min(a.t,this.t);
  while(i < m) {
    c += this[i]+a[i];
    r[i++] = c&this.DM;
    c >>= this.DB;
  }
  if(a.t < this.t) {
    c += a.s;
    while(i < this.t) {
      c += this[i];
      r[i++] = c&this.DM;
      c >>= this.DB;
    }
    c += this.s;
  }
  else {
    c += this.s;
    while(i < a.t) {
      c += a[i];
      r[i++] = c&this.DM;
      c >>= this.DB;
    }
    c += a.s;
  }
  r.s = (c<0)?-1:0;
  if(c > 0) r[i++] = c;
  else if(c < -1) r[i++] = this.DV+c;
  r.t = i;
  r.clamp();
}


function bnAdd(a) { var r = nbi(); this.addTo(a,r); return r; }


function bnSubtract(a) { var r = nbi(); this.subTo(a,r); return r; }


function bnMultiply(a) { var r = nbi(); this.multiplyTo(a,r); return r; }


function bnDivide(a) { var r = nbi(); this.divRemTo(a,r,null); return r; }


function bnRemainder(a) { var r = nbi(); this.divRemTo(a,null,r); return r; }


function bnDivideAndRemainder(a) {
  var q = nbi(), r = nbi();
  this.divRemTo(a,q,r);
  return new Array(q,r);
}


function bnpDMultiply(n) {
  this[this.t] = this.am(0,n-1,this,0,0,this.t);
  ++this.t;
  this.clamp();
}


function bnpDAddOffset(n,w) {
  while(this.t <= w) this[this.t++] = 0;
  this[w] += n;
  while(this[w] >= this.DV) {
    this[w] -= this.DV;
    if(++w >= this.t) this[this.t++] = 0;
    ++this[w];
  }
}


function NullExp() {}
function nNop(x) { return x; }
function nMulTo(x,y,r) { x.multiplyTo(y,r); }
function nSqrTo(x,r) { x.squareTo(r); }

NullExp.prototype.convert = nNop;
NullExp.prototype.revert = nNop;
NullExp.prototype.mulTo = nMulTo;
NullExp.prototype.sqrTo = nSqrTo;


function bnPow(e) { return this.exp(e,new NullExp()); }



function bnpMultiplyLowerTo(a,n,r) {
  var i = Math.min(this.t+a.t,n);
  r.s = 0; 
  r.t = i;
  while(i > 0) r[--i] = 0;
  var j;
  for(j = r.t-this.t; i < j; ++i) r[i+this.t] = this.am(0,a[i],r,i,0,this.t);
  for(j = Math.min(a.t,n); i < j; ++i) this.am(0,a[i],r,i,0,n-i);
  r.clamp();
}



function bnpMultiplyUpperTo(a,n,r) {
  --n;
  var i = r.t = this.t+a.t-n;
  r.s = 0; 
  while(--i >= 0) r[i] = 0;
  for(i = Math.max(n-this.t,0); i < a.t; ++i)
    r[this.t+i-n] = this.am(n-i,a[i],r,0,0,this.t+i-n);
  r.clamp();
  r.drShiftTo(1,r);
}


function Barrett(m) {
  
  this.r2 = nbi();
  this.q3 = nbi();
  BigInteger.ONE.dlShiftTo(2*m.t,this.r2);
  this.mu = this.r2.divide(m);
  this.m = m;
}

function barrettConvert(x) {
  if(x.s < 0 || x.t > 2*this.m.t) return x.mod(this.m);
  else if(x.compareTo(this.m) < 0) return x;
  else { var r = nbi(); x.copyTo(r); this.reduce(r); return r; }
}

function barrettRevert(x) { return x; }


function barrettReduce(x) {
  x.drShiftTo(this.m.t-1,this.r2);
  if(x.t > this.m.t+1) { x.t = this.m.t+1; x.clamp(); }
  this.mu.multiplyUpperTo(this.r2,this.m.t+1,this.q3);
  this.m.multiplyLowerTo(this.q3,this.m.t+1,this.r2);
  while(x.compareTo(this.r2) < 0) x.dAddOffset(1,this.m.t+1);
  x.subTo(this.r2,x);
  while(x.compareTo(this.m) >= 0) x.subTo(this.m,x);
}


function barrettSqrTo(x,r) { x.squareTo(r); this.reduce(r); }


function barrettMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }

Barrett.prototype.convert = barrettConvert;
Barrett.prototype.revert = barrettRevert;
Barrett.prototype.reduce = barrettReduce;
Barrett.prototype.mulTo = barrettMulTo;
Barrett.prototype.sqrTo = barrettSqrTo;


function bnModPow(e,m) {
  var i = e.bitLength(), k, r = nbv(1), z;
  if(i <= 0) return r;
  else if(i < 18) k = 1;
  else if(i < 48) k = 3;
  else if(i < 144) k = 4;
  else if(i < 768) k = 5;
  else k = 6;
  if(i < 8)
    z = new Classic(m);
  else if(m.isEven())
    z = new Barrett(m);
  else
    z = new Montgomery(m);

  
  var g = new Array(), n = 3, k1 = k-1, km = (1<<k)-1;
  g[1] = z.convert(this);
  if(k > 1) {
    var g2 = nbi();
    z.sqrTo(g[1],g2);
    while(n <= km) {
      g[n] = nbi();
      z.mulTo(g2,g[n-2],g[n]);
      n += 2;
    }
  }

  var j = e.t-1, w, is1 = true, r2 = nbi(), t;
  i = nbits(e[j])-1;
  while(j >= 0) {
    if(i >= k1) w = (e[j]>>(i-k1))&km;
    else {
      w = (e[j]&((1<<(i+1))-1))<<(k1-i);
      if(j > 0) w |= e[j-1]>>(this.DB+i-k1);
    }

    n = k;
    while((w&1) == 0) { w >>= 1; --n; }
    if((i -= n) < 0) { i += this.DB; --j; }
    if(is1) {    
      g[w].copyTo(r);
      is1 = false;
    }
    else {
      while(n > 1) { z.sqrTo(r,r2); z.sqrTo(r2,r); n -= 2; }
      if(n > 0) z.sqrTo(r,r2); else { t = r; r = r2; r2 = t; }
      z.mulTo(r2,g[w],r);
    }

    while(j >= 0 && (e[j]&(1<<i)) == 0) {
      z.sqrTo(r,r2); t = r; r = r2; r2 = t;
      if(--i < 0) { i = this.DB-1; --j; }
    }
  }
  return z.revert(r);
}


function bnGCD(a) {
  var x = (this.s<0)?this.negate():this.clone();
  var y = (a.s<0)?a.negate():a.clone();
  if(x.compareTo(y) < 0) { var t = x; x = y; y = t; }
  var i = x.getLowestSetBit(), g = y.getLowestSetBit();
  if(g < 0) return x;
  if(i < g) g = i;
  if(g > 0) {
    x.rShiftTo(g,x);
    y.rShiftTo(g,y);
  }
  while(x.signum() > 0) {
    if((i = x.getLowestSetBit()) > 0) x.rShiftTo(i,x);
    if((i = y.getLowestSetBit()) > 0) y.rShiftTo(i,y);
    if(x.compareTo(y) >= 0) {
      x.subTo(y,x);
      x.rShiftTo(1,x);
    }
    else {
      y.subTo(x,y);
      y.rShiftTo(1,y);
    }
  }
  if(g > 0) y.lShiftTo(g,y);
  return y;
}


function bnpModInt(n) {
  if(n <= 0) return 0;
  var d = this.DV%n, r = (this.s<0)?n-1:0;
  if(this.t > 0)
    if(d == 0) r = this[0]%n;
    else for(var i = this.t-1; i >= 0; --i) r = (d*r+this[i])%n;
  return r;
}


function bnModInverse(m) {
  var ac = m.isEven();
  if((this.isEven() && ac) || m.signum() == 0) return BigInteger.ZERO;
  var u = m.clone(), v = this.clone();
  var a = nbv(1), b = nbv(0), c = nbv(0), d = nbv(1);
  while(u.signum() != 0) {
    while(u.isEven()) {
      u.rShiftTo(1,u);
      if(ac) {
        if(!a.isEven() || !b.isEven()) { a.addTo(this,a); b.subTo(m,b); }
        a.rShiftTo(1,a);
      }
      else if(!b.isEven()) b.subTo(m,b);
      b.rShiftTo(1,b);
    }
    while(v.isEven()) {
      v.rShiftTo(1,v);
      if(ac) {
        if(!c.isEven() || !d.isEven()) { c.addTo(this,c); d.subTo(m,d); }
        c.rShiftTo(1,c);
      }
      else if(!d.isEven()) d.subTo(m,d);
      d.rShiftTo(1,d);
    }
    if(u.compareTo(v) >= 0) {
      u.subTo(v,u);
      if(ac) a.subTo(c,a);
      b.subTo(d,b);
    }
    else {
      v.subTo(u,v);
      if(ac) c.subTo(a,c);
      d.subTo(b,d);
    }
  }
  if(v.compareTo(BigInteger.ONE) != 0) return BigInteger.ZERO;
  if(d.compareTo(m) >= 0) return d.subtract(m);
  if(d.signum() < 0) d.addTo(m,d); else return d;
  if(d.signum() < 0) return d.add(m); else return d;
}

var lowprimes = [2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509];
var lplim = (1<<26)/lowprimes[lowprimes.length-1];


function bnIsProbablePrime(t) {
  var i, x = this.abs();
  if(x.t == 1 && x[0] <= lowprimes[lowprimes.length-1]) {
    for(i = 0; i < lowprimes.length; ++i)
      if(x[0] == lowprimes[i]) return true;
    return false;
  }
  if(x.isEven()) return false;
  i = 1;
  while(i < lowprimes.length) {
    var m = lowprimes[i], j = i+1;
    while(j < lowprimes.length && m < lplim) m *= lowprimes[j++];
    m = x.modInt(m);
    while(i < j) if(m%lowprimes[i++] == 0) return false;
  }
  return x.millerRabin(t);
}


function bnpMillerRabin(t) {
  var n1 = this.subtract(BigInteger.ONE);
  var k = n1.getLowestSetBit();
  if(k <= 0) return false;
  var r = n1.shiftRight(k);
  t = (t+1)>>1;
  if(t > lowprimes.length) t = lowprimes.length;
  var a = nbi();
  for(var i = 0; i < t; ++i) {
    a.fromInt(lowprimes[i]);
    var y = a.modPow(r,this);
    if(y.compareTo(BigInteger.ONE) != 0 && y.compareTo(n1) != 0) {
      var j = 1;
      while(j++ < k && y.compareTo(n1) != 0) {
        y = y.modPowInt(2,this);
        if(y.compareTo(BigInteger.ONE) == 0) return false;
      }
      if(y.compareTo(n1) != 0) return false;
    }
  }
  return true;
}


BigInteger.prototype.chunkSize = bnpChunkSize;
BigInteger.prototype.toRadix = bnpToRadix;
BigInteger.prototype.fromRadix = bnpFromRadix;
BigInteger.prototype.fromNumber = bnpFromNumber;
BigInteger.prototype.bitwiseTo = bnpBitwiseTo;
BigInteger.prototype.changeBit = bnpChangeBit;
BigInteger.prototype.addTo = bnpAddTo;
BigInteger.prototype.dMultiply = bnpDMultiply;
BigInteger.prototype.dAddOffset = bnpDAddOffset;
BigInteger.prototype.multiplyLowerTo = bnpMultiplyLowerTo;
BigInteger.prototype.multiplyUpperTo = bnpMultiplyUpperTo;
BigInteger.prototype.modInt = bnpModInt;
BigInteger.prototype.millerRabin = bnpMillerRabin;


BigInteger.prototype.clone = bnClone;
BigInteger.prototype.intValue = bnIntValue;
BigInteger.prototype.byteValue = bnByteValue;
BigInteger.prototype.shortValue = bnShortValue;
BigInteger.prototype.signum = bnSigNum;
BigInteger.prototype.toByteArray = bnToByteArray;
BigInteger.prototype.equals = bnEquals;
BigInteger.prototype.min = bnMin;
BigInteger.prototype.max = bnMax;
BigInteger.prototype.and = bnAnd;
BigInteger.prototype.or = bnOr;
BigInteger.prototype.xor = bnXor;
BigInteger.prototype.andNot = bnAndNot;
BigInteger.prototype.not = bnNot;
BigInteger.prototype.shiftLeft = bnShiftLeft;
BigInteger.prototype.shiftRight = bnShiftRight;
BigInteger.prototype.getLowestSetBit = bnGetLowestSetBit;
BigInteger.prototype.bitCount = bnBitCount;
BigInteger.prototype.testBit = bnTestBit;
BigInteger.prototype.setBit = bnSetBit;
BigInteger.prototype.clearBit = bnClearBit;
BigInteger.prototype.flipBit = bnFlipBit;
BigInteger.prototype.add = bnAdd;
BigInteger.prototype.subtract = bnSubtract;
BigInteger.prototype.multiply = bnMultiply;
BigInteger.prototype.divide = bnDivide;
BigInteger.prototype.remainder = bnRemainder;
BigInteger.prototype.divideAndRemainder = bnDivideAndRemainder;
BigInteger.prototype.modPow = bnModPow;
BigInteger.prototype.modInverse = bnModInverse;
BigInteger.prototype.pow = bnPow;
BigInteger.prototype.gcd = bnGCD;
BigInteger.prototype.isProbablePrime = bnIsProbablePrime;












var RSAPublicKey = function($modulus, $encryptionExponent) {
    this.modulus = new BigInteger(Hex.encode($modulus), 16);
    this.encryptionExponent = new BigInteger(Hex.encode($encryptionExponent), 16);
}

var UTF8 = {
    encode: function($input) {
        $input = $input.replace(/\r\n/g,"\n");
        var $output = "";
        for (var $n = 0; $n < $input.length; $n++) {
            var $c = $input.charCodeAt($n);
            if ($c < 128) {
                $output += String.fromCharCode($c);
            } else if (($c > 127) && ($c < 2048)) {
                $output += String.fromCharCode(($c >> 6) | 192);
                $output += String.fromCharCode(($c & 63) | 128);
            } else {
                $output += String.fromCharCode(($c >> 12) | 224);
                $output += String.fromCharCode((($c >> 6) & 63) | 128);
                $output += String.fromCharCode(($c & 63) | 128);
            }
        }
        return $output;
    },
    decode: function($input) {
        var $output = "";
        var $i = 0;
        var $c = $c1 = $c2 = 0;
        while ( $i < $input.length ) {
            $c = $input.charCodeAt($i);
            if ($c < 128) {
                $output += String.fromCharCode($c);
                $i++;
            } else if(($c > 191) && ($c < 224)) {
                $c2 = $input.charCodeAt($i+1);
                $output += String.fromCharCode((($c & 31) << 6) | ($c2 & 63));
                $i += 2;
            } else {
                $c2 = $input.charCodeAt($i+1);
                $c3 = $input.charCodeAt($i+2);
                $output += String.fromCharCode((($c & 15) << 12) | (($c2 & 63) << 6) | ($c3 & 63));
                $i += 3;
            }
        }
        return $output;
    }
};

var Base64 = {
    base64: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
    encode: function($input) {
        if (!$input) {
            return false;
        }
        
        var $output = "";
        var $chr1, $chr2, $chr3;
        var $enc1, $enc2, $enc3, $enc4;
        var $i = 0;
        do {
            $chr1 = $input.charCodeAt($i++);
            $chr2 = $input.charCodeAt($i++);
            $chr3 = $input.charCodeAt($i++);
            $enc1 = $chr1 >> 2;
            $enc2 = (($chr1 & 3) << 4) | ($chr2 >> 4);
            $enc3 = (($chr2 & 15) << 2) | ($chr3 >> 6);
            $enc4 = $chr3 & 63;
            if (isNaN($chr2)) $enc3 = $enc4 = 64;
            else if (isNaN($chr3)) $enc4 = 64;
            $output += this.base64.charAt($enc1) + this.base64.charAt($enc2) + this.base64.charAt($enc3) + this.base64.charAt($enc4);
        } while ($i < $input.length);
        return $output;
    },
    decode: function($input) {
        if(!$input) return false;
        $input = $input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        var $output = "";
        var $enc1, $enc2, $enc3, $enc4;
        var $i = 0;
        do {
            $enc1 = this.base64.indexOf($input.charAt($i++));
            $enc2 = this.base64.indexOf($input.charAt($i++));
            $enc3 = this.base64.indexOf($input.charAt($i++));
            $enc4 = this.base64.indexOf($input.charAt($i++));
            $output += String.fromCharCode(($enc1 << 2) | ($enc2 >> 4));
            if ($enc3 != 64) $output += String.fromCharCode((($enc2 & 15) << 4) | ($enc3 >> 2));
            if ($enc4 != 64) $output += String.fromCharCode((($enc3 & 3) << 6) | $enc4);
        } while ($i < $input.length);
        return $output; 
    }
};

var Hex = {
    hex: "0123456789abcdef",
    encode: function($input) {
        if(!$input) return false;
        var $output = "";
        var $k;
        var $i = 0;
        do {
            $k = $input.charCodeAt($i++);
            $output += this.hex.charAt(($k >> 4) &0xf) + this.hex.charAt($k & 0xf);
        } while ($i < $input.length);
        return $output;
    },
    decode: function($input) {
        if(!$input) return false;
        $input = $input.replace(/[^0-9abcdef]/g, "");
        var $output = "";
        var $i = 0;
        do {
            $output += String.fromCharCode(((this.hex.indexOf($input.charAt($i++)) << 4) & 0xf0) | (this.hex.indexOf($input.charAt($i++)) & 0xf));
        } while ($i < $input.length);
        return $output;
    }
};

var ASN1Data = function($data) {
    this.error = false;
    this.parse = function($data) {
        if (!$data) {
            this.error = true;
            return null;
        }
        var $result = [];
        while($data.length > 0) {
            
            var $tag = $data.charCodeAt(0);
            $data = $data.substr(1);
            
            var $length = 0;
            
            if (($tag & 31) == 0x5) $data = $data.substr(1);
            else {
                if ($data.charCodeAt(0) & 128) {
                    var $lengthSize = $data.charCodeAt(0) & 127;
                    $data = $data.substr(1);
                    if($lengthSize > 0) $length = $data.charCodeAt(0);
                    if($lengthSize > 1)    $length = (($length << 8) | $data.charCodeAt(1));
                    if($lengthSize > 2) {
                        this.error = true;
                        return null;
                    }
                    $data = $data.substr($lengthSize);
                } else {
                    $length = $data.charCodeAt(0);
                    $data = $data.substr(1);
                }
            }
            
            var $value = "";
            if($length) {
                if ($length > $data.length){
                    this.error = true;
                    return null;
                }
                $value = $data.substr(0, $length);
                $data = $data.substr($length);
            }
            if ($tag & 32)
                $result.push(this.parse($value)); 
            else
                $result.push(this.value(($tag & 128) ? 4 : ($tag & 31), $value));
        }
        return $result;
    };
    this.value = function($tag, $data) {
        if ($tag == 1)
            return $data ? true : false;
        else if ($tag == 2) 
            return $data;
        else if ($tag == 3) 
            return this.parse($data.substr(1));
        else if ($tag == 5) 
            return null;
        else if ($tag == 6){ 
            var $res = [];
            var $d0 = $data.charCodeAt(0);
            $res.push(Math.floor($d0 / 40));
            $res.push($d0 - $res[0]*40);
            var $stack = [];
            var $powNum = 0;
            var $i;
            for($i=1;$i<$data.length;$i++){
                var $token = $data.charCodeAt($i);
                $stack.push($token & 127);
                if ( $token & 128 )
                    $powNum++;
                else {
                    var $j;
                    var $sum = 0;
                    for($j=0;$j<$stack.length;$j++)
                        $sum += $stack[$j] * Math.pow(128, $powNum--);
                    $res.push($sum);
                    $powNum = 0;
                    $stack = [];
                }
            }
            return $res.join(".");
        }
        return null;
    }
    this.data = this.parse($data);
};

var RSA = {
    getPublicKey: function($pem) {
        if($pem.length<50) return false;
        if($pem.substr(0,26)=="-----BEGIN PUBLIC KEY-----") {
            $pem = $pem.substr(26);
        }
        if($pem.substr($pem.length-24)=="-----END PUBLIC KEY-----") {
            $pem = $pem.substr(0,$pem.length-24);
        }
        $pem = new ASN1Data(Base64.decode($pem));
        if($pem.error) return false;
        $pem = $pem.data;
        if($pem[0][0][0]=="1.2.840.113549.1.1.1")
            return new RSAPublicKey($pem[0][1][0][0], $pem[0][1][0][1]);
        return false;
    },
    encrypt: function($data, $pubkey) {
        if (!$pubkey) return false;
        var bytes = ($pubkey.modulus.bitLength()+7)>>3;
        $data = this.pkcs1pad2($data,bytes);
        if(!$data) return false;
        $data = $data.modPowInt($pubkey.encryptionExponent, $pubkey.modulus);
        if(!$data) return false;
        $data = $data.toString(16);
        while ($data.length < bytes*2)
            $data = '0' + $data;
        return Base64.encode(Hex.decode($data));
    },
    pkcs1pad2: function($data, $keysize) {
        if($keysize < $data.length + 11)
            return null;
        var $buffer = [];
        var $i = $data.length - 1;
        while($i >= 0 && $keysize > 0)
            $buffer[--$keysize] = $data.charCodeAt($i--);
        $buffer[--$keysize] = 0;
        while($keysize > 2)
            $buffer[--$keysize] = Math.floor(Math.random()*254) + 1;
        $buffer[--$keysize] = 2;
        $buffer[--$keysize] = 0;
        return new BigInteger($buffer);
    }
}



function sha1(msg)
{
    
    var K = [0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6];

    
    msg += String.fromCharCode(0x80);  

    
    var l = Math.ceil(msg.length/4) + 2;  
    var N = Math.ceil(l/16);              
    var M = new Array(N);

    for (var i=0; i<N; i++) {
        M[i] = new Array(16);
        for (var j=0; j<16; j++)  
            M[i][j] = (msg.charCodeAt(i*64+j*4)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16) | (msg.charCodeAt(i*64+j*4+2)<<8) | (msg.charCodeAt(i*64+j*4+3));
        
    }

    
    
    
    M[N-1][14] = ((msg.length-1)*8) / Math.pow(2, 32); M[N-1][14] = Math.floor(M[N-1][14])
    M[N-1][15] = ((msg.length-1)*8) & 0xffffffff;

    
    var H0 = 0x67452301;
    var H1 = 0xefcdab89;
    var H2 = 0x98badcfe;
    var H3 = 0x10325476;
    var H4 = 0xc3d2e1f0;

    
    var W = new Array(80);
    var a, b, c, d, e;

    for (var i=0; i<N; i++) {
        
        for (var t=0;  t<16; t++) W[t] = M[i][t];
        for (var t=16; t<80; t++) {
            W[t] = W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16];
            W[t] = (W[t] << 1) | (W[t]>>>31);
        }

        
        a = H0; b = H1; c = H2; d = H3; e = H4;

        
        for (var t=0; t<80; t++) {
            var s = Math.floor(t/20); 
            var T = ((a<<5) | (a>>>27)) + e + K[s] + W[t];
            switch(s) {
            case 0: T += (b & c) ^ (~b & d); break;          
            case 1: T += b ^ c ^ d; break;                   
            case 2: T += (b & c) ^ (b & d) ^ (c & d); break; 
            case 3: T += b ^ c ^ d; break;                   
            }
            e = d;
            d = c;
            c = (b << 30) | (b>>>2);
            b = a;
            a = T;
        }

        
        H0 = (H0+a) & 0xffffffff;  
        H1 = (H1+b) & 0xffffffff;
        H2 = (H2+c) & 0xffffffff;
        H3 = (H3+d) & 0xffffffff;
        H4 = (H4+e) & 0xffffffff;
    }

    var hex = "";
    for (var i=7; i>=0; i--) { var v = (H0>>>(i*4)) & 0xf; hex += v.toString(16); }
    for (var i=7; i>=0; i--) { var v = (H1>>>(i*4)) & 0xf; hex += v.toString(16); }
    for (var i=7; i>=0; i--) { var v = (H2>>>(i*4)) & 0xf; hex += v.toString(16); }
    for (var i=7; i>=0; i--) { var v = (H3>>>(i*4)) & 0xf; hex += v.toString(16); }
    for (var i=7; i>=0; i--) { var v = (H4>>>(i*4)) & 0xf; hex += v.toString(16); }
    return hex;
}



Ext.define('OPF.core.component.utils.ui.InputTextMask', {
    constructor: function(mask, clearWhenInvalid) {
        if (clearWhenInvalid === undefined)
            this.clearWhenInvalid = true;
        else
            this.clearWhenInvalid = clearWhenInvalid;
        this.rawMask = mask;
        this.viewMask = '';
        this.maskArray = new Array();
        var mai = 0;
        var regexp = '';
        for (var i = 0; i < mask.length; i++) {
            if (regexp) {
                if (regexp == 'X') {
                    regexp = '';
                }
                if (mask.charAt(i) == 'X') {
                    this.maskArray[mai] = regexp;
                    mai++;
                    regexp = '';
                } else {
                    regexp += mask.charAt(i);
                }
            } else if (mask.charAt(i) == 'X') {
                regexp += 'X';
                this.viewMask += '_';
            } else if (mask.charAt(i) == '9' || mask.charAt(i) == 'L' || mask.charAt(i) == 'l' || mask.charAt(i) == 'A') {
                this.viewMask += '_';
                this.maskArray[mai] = mask.charAt(i);
                mai++;
            } else {
                this.viewMask += mask.charAt(i);
                this.maskArray[mai] = RegExp.escape(mask.charAt(i));
                mai++;
            }
        }

        this.specialChars = this.viewMask.replace(/(L|l|9|A|_|X)/g, '');
        return this;
    },

    init: function(field) {
        this.field = field;

        if (field.rendered) {
            this.assignEl();
        } else {
            field.on('render', this.assignEl, this);
        }

        field.on('blur', this.removeValueWhenInvalid, this);
        field.on('focus', this.processMaskFocus, this);
    },

    assignEl: function() {
        this.inputTextElement = this.field.inputEl.dom;
        this.field.inputEl.on('keypress', this.processKeyPress, this);
        this.field.inputEl.on('keydown', this.processKeyDown, this);
        if (Ext.isSafari || Ext.isIE) {
            this.field.inputEl.on('paste', this.startTask, this);
            this.field.inputEl.on('cut', this.startTask, this);
        }
        if (Ext.isGecko || Ext.isOpera) {
            this.field.inputEl.on('mousedown', this.setPreviousValue, this);
        }
        if (Ext.isGecko) {
            this.field.inputEl.on('input', this.onInput, this);
        }
        if (Ext.isOpera) {
            this.field.inputEl.on('input', this.onInputOpera, this);
        }
    },
    onInput: function() {
        this.startTask(false);
    },
    onInputOpera: function() {
        if (!this.prevValueOpera) {
            this.startTask(false);
        } else {
            this.manageBackspaceAndDeleteOpera();
        }
    },

    manageBackspaceAndDeleteOpera: function() {
        this.inputTextElement.value = this.prevValueOpera.cursorPos.previousValue;
        this.manageTheText(this.prevValueOpera.keycode, this.prevValueOpera.cursorPos);
        this.prevValueOpera = null;
    },

    setPreviousValue: function(event) {
        this.oldCursorPos = this.getCursorPosition();
    },

    getValidatedKey: function(keycode, cursorPosition) {
        var maskKey = this.maskArray[cursorPosition.start];
        if (maskKey == '9') {
            return keycode.pressedKey.match(/[0-9]/);
        } else if (maskKey == 'L') {
            return (keycode.pressedKey.match(/[A-Za-z]/)) ? keycode.pressedKey.toUpperCase() : null;
        } else if (maskKey == 'l') {
            return (keycode.pressedKey.match(/[A-Za-z]/)) ? keycode.pressedKey.toLowerCase() : null;
        } else if (maskKey == 'A') {
            return keycode.pressedKey.match(/[A-Za-z0-9]/);
        } else if (maskKey) {
            return (keycode.pressedKey.match(new RegExp(maskKey)));
        }
        return(null);
    },

    removeValueWhenInvalid: function() {
        if (this.clearWhenInvalid && this.inputTextElement.value.indexOf('_') > -1) {
            this.inputTextElement.value = '';
        }
    },

    managePaste: function() {
        if (this.oldCursorPos == null) {
            return;
        }
        var valuePasted = this.inputTextElement.value.substring(this.oldCursorPos.start, this.inputTextElement.value.length - (this.oldCursorPos.previousValue.length - this.oldCursorPos.end));
        if (this.oldCursorPos.start < this.oldCursorPos.end) {
            this.oldCursorPos.previousValue =
                this.oldCursorPos.previousValue.substring(0, this.oldCursorPos.start) +
                    this.viewMask.substring(this.oldCursorPos.start, this.oldCursorPos.end) +
                    this.oldCursorPos.previousValue.substring(this.oldCursorPos.end, this.oldCursorPos.previousValue.length);
            valuePasted = valuePasted.substr(0, this.oldCursorPos.end - this.oldCursorPos.start);
        }
        this.inputTextElement.value = this.oldCursorPos.previousValue;
        keycode = {
            unicode: '',
            isShiftPressed: false,
            isTab: false,
            isBackspace: false,
            isLeftOrRightArrow: false,
            isDelete: false,
            pressedKey: ''
        }
        var charOk = false;
        for (var i = 0; i < valuePasted.length; i++) {
            keycode.pressedKey = valuePasted.substr(i, 1);
            keycode.unicode = valuePasted.charCodeAt(i);
            this.oldCursorPos = this.skipMaskCharacters(keycode, this.oldCursorPos);
            if (this.oldCursorPos === false) {
                break;
            }
            if (this.injectValue(keycode, this.oldCursorPos)) {
                charOk = true;
                this.moveCursorToPosition(keycode, this.oldCursorPos);
                this.oldCursorPos.previousValue = this.inputTextElement.value;
                this.oldCursorPos.start = this.oldCursorPos.start + 1;
            }
        }
        if (!charOk && this.oldCursorPos !== false) {
            this.moveCursorToPosition(null, this.oldCursorPos);
        }
        this.oldCursorPos = null;
    },

    processKeyDown: function(e) {
        this.processMaskFormatting(e, 'keydown');
    },

    processKeyPress: function(e) {
        this.processMaskFormatting(e, 'keypress');
    },

    startTask: function(setOldCursor) {
        if (this.task == undefined) {
            this.task = new Ext.util.DelayedTask(this.managePaste, this);
        }
        if (setOldCursor !== false) {
            this.oldCursorPos = this.getCursorPosition();
        }
        this.task.delay(0);
    },

    skipMaskCharacters: function(keycode, cursorPos) {
        if (cursorPos.start != cursorPos.end && (keycode.isDelete || keycode.isBackspace))
            return(cursorPos);
        while (this.specialChars.match(RegExp.escape(this.viewMask.charAt(((keycode.isBackspace) ? cursorPos.start - 1 : cursorPos.start))))) {
            if (keycode.isBackspace) {
                cursorPos.dec();
            } else {
                cursorPos.inc();
            }
            if (cursorPos.start >= cursorPos.previousValue.length || cursorPos.start < 0) {
                return false;
            }
        }
        return(cursorPos);
    },

    isManagedByKeyDown: function(keycode) {
        if (keycode.isDelete || keycode.isBackspace) {
            return(true);
        }
        return(false);
    },

    processMaskFormatting: function(e, type) {
        this.oldCursorPos = null;
        var cursorPos = this.getCursorPosition();
        var keycode = this.getKeyCode(e, type);
        if (keycode.unicode == 0) {
            return;
        }
        if ((keycode.unicode == 67 || keycode.unicode == 99) && e.ctrlKey) {
            return;
        }
        if ((keycode.unicode == 88 || keycode.unicode == 120) && e.ctrlKey) {
            this.startTask();
            return;
        }
        if ((keycode.unicode == 86 || keycode.unicode == 118) && e.ctrlKey) {
            this.startTask();
            return;
        }
        if ((keycode.isBackspace || keycode.isDelete) && Ext.isOpera) {
            this.prevValueOpera = {cursorPos: cursorPos, keycode: keycode};
            return;
        }
        if (type == 'keydown' && !this.isManagedByKeyDown(keycode)) {
            return true;
        }
        if (type == 'keypress' && this.isManagedByKeyDown(keycode)) {
            return true;
        }
        if (this.handleEventBubble(e, keycode, type)) {
            return true;
        }
        return(this.manageTheText(keycode, cursorPos));
    },

    manageTheText: function(keycode, cursorPos) {
        if (this.inputTextElement.value.length === 0) {
            this.inputTextElement.value = this.viewMask;
        }
        cursorPos = this.skipMaskCharacters(keycode, cursorPos);
        if (cursorPos === false) {
            return false;
        }
        if (this.injectValue(keycode, cursorPos)) {
            this.moveCursorToPosition(keycode, cursorPos);
        }
        return(false);
    },

    processMaskFocus: function() {
        if (this.inputTextElement.value.length == 0) {
            var cursorPos = this.getCursorPosition();
            this.inputTextElement.value = this.viewMask;
            this.moveCursorToPosition(null, cursorPos);
        }
    },

    isManagedByBrowser: function(keyEvent, keycode, type) {
        if (((type == 'keypress' && keyEvent.charCode === 0) ||
            type == 'keydown') && (keycode.unicode == Ext.EventObject.TAB ||
            keycode.unicode == Ext.EventObject.RETURN ||
            keycode.unicode == Ext.EventObject.ENTER ||
            keycode.unicode == Ext.EventObject.SHIFT ||
            keycode.unicode == Ext.EventObject.CONTROL ||
            keycode.unicode == Ext.EventObject.ESC ||
            keycode.unicode == Ext.EventObject.PAGEUP ||
            keycode.unicode == Ext.EventObject.PAGEDOWN ||
            keycode.unicode == Ext.EventObject.END ||
            keycode.unicode == Ext.EventObject.HOME ||
            keycode.unicode == Ext.EventObject.LEFT ||
            keycode.unicode == Ext.EventObject.UP ||
            keycode.unicode == Ext.EventObject.RIGHT ||
            keycode.unicode == Ext.EventObject.DOWN)) {
            return(true);
        }
        return(false);
    },

    handleEventBubble: function(keyEvent, keycode, type) {
        try {
            if (keycode && this.isManagedByBrowser(keyEvent, keycode, type)) {
                return true;
            }
            keyEvent.stopEvent();
            return false;
        } catch(e) {
            alert(e.message);
        }
    },

    getCursorPosition: function() {
        var s, e, r;
        if (this.inputTextElement.createTextRange) {
            r = document.selection.createRange().duplicate();
            r.moveEnd('character', this.inputTextElement.value.length);
            if (r.text === '') {
                s = this.inputTextElement.value.length;
            } else {
                s = this.inputTextElement.value.lastIndexOf(r.text);
            }
            r = document.selection.createRange().duplicate();
            r.moveStart('character', -this.inputTextElement.value.length);
            e = r.text.length;
        } else {
            s = this.inputTextElement.selectionStart;
            e = this.inputTextElement.selectionEnd;
        }
        return this.CursorPosition(s, e, r, this.inputTextElement.value);
    },

    moveCursorToPosition: function(keycode, cursorPosition) {
        var p = (!keycode || (keycode && keycode.isBackspace )) ? cursorPosition.start : cursorPosition.start + 1;
        if (this.inputTextElement.createTextRange) {
            cursorPosition.range.move('character', p);
            cursorPosition.range.select();
        } else {
            this.inputTextElement.selectionStart = p;
            this.inputTextElement.selectionEnd = p;
        }
    },

    injectValue: function(keycode, cursorPosition) {
        if (!keycode.isDelete && keycode.unicode == cursorPosition.previousValue.charCodeAt(cursorPosition.start))
            return true;
        var key;
        if (!keycode.isDelete && !keycode.isBackspace) {
            key = this.getValidatedKey(keycode, cursorPosition);
        } else {
            if (cursorPosition.start == cursorPosition.end) {
                key = '_';
                if (keycode.isBackspace) {
                    cursorPosition.dec();
                }
            } else {
                key = this.viewMask.substring(cursorPosition.start, cursorPosition.end);
            }
        }
        if (key) {
            this.inputTextElement.value = cursorPosition.previousValue.substring(0, cursorPosition.start)
                + key +
                cursorPosition.previousValue.substring(cursorPosition.start + key.length, cursorPosition.previousValue.length);
            return true;
        }
        return false;
    },

    getKeyCode: function(onKeyDownEvent, type) {
        var keycode = {};
        keycode.unicode = onKeyDownEvent.getKey();
        keycode.isShiftPressed = onKeyDownEvent.shiftKey;

        keycode.isDelete = ((onKeyDownEvent.getKey() == Ext.EventObject.DELETE && type == 'keydown') || ( type == 'keypress' && onKeyDownEvent.charCode === 0 && onKeyDownEvent.keyCode == Ext.EventObject.DELETE)) ? true : false;
        keycode.isTab = (onKeyDownEvent.getKey() == Ext.EventObject.TAB) ? true : false;
        keycode.isBackspace = (onKeyDownEvent.getKey() == Ext.EventObject.BACKSPACE) ? true : false;
        keycode.isLeftOrRightArrow = (onKeyDownEvent.getKey() == Ext.EventObject.LEFT || onKeyDownEvent.getKey() == Ext.EventObject.RIGHT) ? true : false;
        keycode.pressedKey = String.fromCharCode(keycode.unicode);
        return(keycode);
    },

    CursorPosition: function(start, end, range, previousValue) {
        var cursorPosition = {};
        cursorPosition.start = isNaN(start) ? 0 : start;
        cursorPosition.end = isNaN(end) ? 0 : end;
        cursorPosition.range = range;
        cursorPosition.previousValue = previousValue;
        cursorPosition.inc = function() {
            cursorPosition.start++;
            cursorPosition.end++;
        };
        cursorPosition.dec = function() {
            cursorPosition.start--;
            cursorPosition.end--;
        };
        return(cursorPosition);
    }
});

Ext.applyIf(RegExp, {
    escape: function(str) {
        return new String(str).replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
    }
});



Ext.define('OPF.Ui', {});

OPF.Ui.createBtn = function(text, width, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.button.Button', Ext.apply({
        text: text,
        width: width,
        action: action
    }, cfg))
};

OPF.Ui.createMenu = function(text, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.menu.Item', Ext.apply({
        cls: 'main-controls-menu',
        text: text,
        action: action
    }, cfg))
};

OPF.Ui.createToggleButton = function(title, toggleGroup, action, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.button.Button', Ext.apply({
        text: title,
        enableToggle: true,
        toggleGroup: toggleGroup,
        action: action
    }, cfg));
};

OPF.Ui.xSpacer = function(width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({width: width}, cfg))
};

OPF.Ui.ySpacer = function(height, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({height: height}, cfg))
};

OPF.Ui.spacer = function(width, height, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.toolbar.Spacer', Ext.apply({width: width, height: height}, cfg))
};

OPF.Ui.populateColumn = function(dataIndex, header, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true
    }, cfg));
};

OPF.Ui.populateHiddenColumn = function(dataIndex, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        hidden: true
    }, cfg));
};

OPF.Ui.populateNumberColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Number', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true,
        width: width
    }, cfg));
};

OPF.Ui.populateBooleanColumn = function(dataIndex, header, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Boolean', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true
    }, cfg));
};

OPF.Ui.populateCheckBoxColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.ux.CheckColumn', Ext.apply({
        text: header,
        dataIndex: dataIndex,
        width: width
    }, cfg));
};

OPF.Ui.populateDateColumn = function(dataIndex, header, width, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Date', Ext.apply({
        dataIndex: dataIndex,
        text: header,
        sortable: true,
        width: width,
        format: 'M j, Y g:i A'
    }, cfg));
};

OPF.Ui.populateIconColumn16 = function(width, image, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        text: '!',
        sortable: true,
        align: 'center',
        width: width,
        renderer: function() {
            return '<img src="' + OPF.Ui.icon16(image) + '">';
        }
    }, cfg));
};

OPF.Ui.populateActionColumn = function(header, width, items, cfg) {
    cfg = cfg || {};
    items = Ext.isArray(items) ? items : [];
    return Ext.create('OPF.core.component.ActionColumn', Ext.apply({
        text: header,
        sortable: false,
        width: width,
        items: items
    }, cfg));
};

OPF.Ui.createActionColumnBtn = function(iconPath, tooltip, handler, cfg) {
    cfg = cfg || {};
    return Ext.apply({
        icon   : OPF.Cfg.OPF_CONSOLE_URL + iconPath,
        tooltip: tooltip,
        handler: handler,
        visible: true
    }, cfg);
};

OPF.Ui.createActionColumnBtn16 = function(icon, tooltip, handler, cfg) {
    cfg = cfg || {};
    return Ext.apply({
        icon   : OPF.Ui.icon16(icon),
        tooltip: tooltip,
        handler: handler,
        visible: true
    }, cfg);
};

OPF.Ui.createGridBtn = function(value, id, record, icon, cfg) {
    return new Ext.Button(
        Ext.apply({
            id: 'btn-' + id,
            renderTo: id,
            text: value,
            icon: OPF.Ui.icon16(icon),
            cls: 'grid-btn'
        }, cfg)
    );
};

OPF.Ui.textFormField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-textfield',
        name: name,
        anchor: '100%',
        fieldLabel: label
    }, cfg));
};

OPF.Ui.comboFormField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-combo',
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.textFormArea = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-textarea',
        name: name,
        anchor: '100%',
        fieldLabel: label
    }, cfg));
};

OPF.Ui.formCheckBox = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.ComponentManager.create(Ext.apply({
        xtype: 'opf-checkbox',
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.displayField = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.create('OPF.core.component.Display', Ext.apply({
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.formDate = function(name, label, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.form.field.Date', Ext.apply({
        name: name,
        fieldLabel: label,
        anchor: '100%'
    }, cfg));
};

OPF.Ui.hiddenField = function(name, cfg) {
    cfg = cfg || {};
    return Ext.create('Ext.form.field.Hidden', Ext.apply({
        name: name
    }, cfg));
};

OPF.Ui.getCmp = function(cmpQuery) {
    var cmpArray = Ext.ComponentQuery.query(cmpQuery);
    return cmpArray == null || !Ext.isArray(cmpArray) ||
        cmpArray.length == 0 ? null : cmpArray[0];
};

OPF.Ui.icon128 = function(icon) {
    return OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/128/' + icon;
};

OPF.Ui.icon16 = function(icon) {
    return OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/' + icon;
};


Ext.define('OPF.core.validation.FormValidator', {

    form: null,
    ruleId: null,
    messagePanel: null,

    suffixFieldName: null,
    findById: false,
    clearInvalidOnly: true,

    statics: {
        RULE_CACHE: new Ext.util.MixedCollection()
    },

    constructor: function(form, ruleId, messagePanel, cfg) {
        cfg = cfg || {};

        this.form = form;
        this.ruleId = ruleId;
        this.messagePanel = messagePanel;

        this.suffixFieldName = OPF.ifEmpty(cfg.suffixFieldName, this.suffixFieldName);
        this.findById = OPF.ifEmpty(cfg.findById, this.findById);
        this.clearInvalidOnly = OPF.ifEmpty(cfg.clearInvalidOnly, this.clearInvalidOnly);

        this.useBaseUrl = OPF.ifEmpty(cfg.useBaseUrl, true);

        this.initConstraints();
    },

    initConstraints: function() {
        var me = this;

        var vFields = OPF.core.validation.FormValidator.RULE_CACHE.get(this.ruleId);
        if (vFields) {
            this.initFormFields(vFields);
        } else {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('rule', this.useBaseUrl) + '?id=' + this.ruleId,
                method: 'GET',

                success: function(response) {
                    var vFields = Ext.decode(response.responseText).data;
                    OPF.core.validation.FormValidator.RULE_CACHE.add(me.ruleId, vFields);
                    me.initFormFields(vFields);
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        }
    },

    initFormFields: function(vFields) {
        var me = this;

        if (OPF.isEmpty(this.form)) {
            return;
        }

        var fields = this.form.getForm().getFields();
        var vType;
        for (var i = 0; i < vFields.length; i++) {
            var vField = vFields[i];
            var searchFieldName = vField.name + OPF.ifBlank(this.suffixFieldName, '');
            var field = null;
            if (this.findById) {
                field = Ext.getCmp(searchFieldName);
            } else {
                if (OPF.isNotEmpty(this.mtype)) {
                    field = fields.findBy(function(f) {
                        return (f.getId() === searchFieldName || f.getName() === searchFieldName) && f.mtype == this.mtype;
                    });
                } else {
                    field = fields.findBy(function(f) {
                        return f.getId() === searchFieldName || f.getName() === searchFieldName;
                    })
                }
            }
            if (field != null) {
                field.vConstraints = vField.constraints;
                this.setComboValues(field);
                this.setDefaultValue(vField, field);
                this.setEditableMode(vField, field);

                field.validator = function(value) {
                    var field = this;
                    var errorMessages = [];
                    var validateMessages;
                    if (OPF.isNotEmpty(field.vConstraints)) {
                        for (var i = 0; i < field.vConstraints.length; i++) {
                            validateMessages = me.validatorProcess(field, field.vConstraints[i], value);
                            Ext.each(validateMessages, function(validateMessage) {
                                if (OPF.isNotBlank(field.fieldOrgLabel)) {
                                    validateMessage = validateMessage.replace('\'' + field.name + '\'', field.fieldOrgLabel)
                                } else if (OPF.isNotBlank(field.fieldLabel)) {
                                    validateMessage = validateMessage.replace('\'' + field.name + '\'', field.fieldLabel)
                                }
                                errorMessages.push(validateMessage);
                            });
                        }
                    }
                    return errorMessages;
                }
            }
        }
        this.form.getForm().checkValidity();
    },

    setComboValues: function(field) {
        if (field.xtype == 'opf-form-combo' || field.xtype == 'opf-combo') {
            if (Ext.isArray(field.vConstraints)) {
                for (var j = 0; j < field.vConstraints.length; j++) {
                    var vConstraint = field.vConstraints[j];
                    if (vConstraint.name == 'EnumValue' || vConstraint.name == 'QueryValues' || vConstraint.name == 'AllowValue') {
                        var fields = [
                            field.valueField,
                            field.displayField
                        ];
                        if (this.getParameterByKey(vConstraint.params, 'hasDescription')) {
                            field.displayField = 'title';
                            fields = [
                                field.valueField,
                                field.displayField,
                                'description'
                            ];
                            field.listConfig = {
                                cls: 'x-wizards-boundlist',
                                getInnerTpl: function() {
                                    return '<div class="enum-item"><h3>{title}</h3>{description}</div>';
                                }
                            }
                        }
                        if (vConstraint.name == 'EnumValue' || vConstraint.name == 'AllowValue') {
                            var data = [];
                            var values = this.getParameterByKey(vConstraint.params, 'values');
                            if (vConstraint.name == 'AllowValue') {
                                Ext.each(values, function(allowValue) {
                                    data.push([allowValue, allowValue]);
                                });
                            } else {
                                data = values;
                            }

                            field.typeAhead = true;
                            field.triggerAction = 'all';
                            field.lazyRender = true;
                            field.mode = 'local';
                            field.store = Ext.create('Ext.data.ArrayStore', {
                                autoDestroy: true,
                                idIndex: 0,
                                fields: fields,
                                data: data
                            });
                        } else if (vConstraint.name == 'QueryValues') {
                            field.typeAhead = OPF.ifEmpty(field.typeAhead, true);
                            field.triggerAction = OPF.ifBlank(field.triggerAction, 'all');
                            field.lazyRender = OPF.ifEmpty(field.lazyRender, true);
                            if (OPF.isEmpty(field.mode)) {
                                field.mode = 'local';
                            }
                            if (field.store == null) {
                                field.store = new Ext.data.ArrayStore({
                                    id: 0,
                                    fields: fields,
                                    data: this.getParameterByKey(vConstraint.params, 'values')
                                });
                            } else {
                                var Record = Ext.data.Record.create([field.valueField, field.displayField]);
                                var parameters = this.getParameterByKey(vConstraint.params, 'values');
                                Ext.each(parameters, function(parameter, index) {
                                    field.store.add(
                                        new Record(
                                            {
                                                id: parameter[0],
                                                name: parameter[1]
                                            },
                                            parameter[0]
                                        )
                                    );
                                })
                            }
                        }

                        if (OPF.isNotBlank(field.getValue())) {
                            field.setValue(field.getValue());
                        }
                    }
                }
            }
        }
    },

    setDefaultValue: function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.defaultValue) && OPF.isBlank(field.getValue())) {
            if (field.setDefaultValue) {
                field.setDefaultValue(fieldVO.defaultValue);
            } else {
                field.setValue(fieldVO.defaultValue);
            }
        }
    },

    setEditableMode: function(fieldVO, field) {
        if (OPF.isNotEmpty(fieldVO.editable)) {
            field.setReadOnly(!fieldVO.editable);
        }
    },

    validatorProcess: function(field, vConstraint, value) {
        if (OPF.isEmpty(vConstraint)) {
            return null;
        }
        var validateMessages = [];
        var name = vConstraint.name;
        var errorMessage = vConstraint.errorMessage;
        var params = vConstraint.params;
        switch(name) {
            case 'NotNull':
                var notNullFn = field.notNullValidatorProcessor || this.notNullValidatorProcessor;
                notNullFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotBlank':
                var notBlankFn = field.notBlankValidatorProcessor || this.notBlankValidatorProcessor;
                notBlankFn(field, value, params, errorMessage, validateMessages);
                break;
            case 'Match':
                this.matchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'NotMatch':
                this.notMatchValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'Length':
                this.lengthValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
            case 'LessThan':
                this.lessThanValidatorProcessor(field, value, params, errorMessage, validateMessages);
                break;
        }
        return validateMessages;
    },

    notNullValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotEmpty(value)) {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be null';
            validateMessages.push(msg);
        }
    },

    notBlankValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        if (!OPF.isNotBlank(value) || value.replace(/\s/g,'') == '') {
            var msg = OPF.isNotBlank(errorMessage) ?
                errorMessage : '\'' + field.getName() + '\' should not be blank';
            validateMessages.push(msg);
        }
    },

    matchValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var expression = this.getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (!regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }
    },

    notMatchValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var expression = this.getParameterByKey(params, 'expression');
        if (OPF.isNotEmpty(expression) && OPF.isNotBlank(value)) {
            var regexp = new RegExp(expression, "g");
            if (regexp.test(value)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should not match to regexp: [' + expression + ']';
                validateMessages.push(msg);
            }
        }

        var words = this.getParameterByKey(params, 'words');
        if (OPF.isNotEmpty(words) && Ext.isArray(words) && OPF.isNotBlank(value)) {
            for (var i = 0; i < words.length; i++) {
                if (value == words[i]) {
                    validateMessages.push('\'' + field.getName() + '\' should not be equal reserved word: \'' + words[i] + '\'');
                }
            }
        }
    },

    lengthValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var min = this.getParameterByKey(params, 'min');
        var minMsg = this.getParameterByKey(params, 'minMsg');
        var max = this.getParameterByKey(params, 'max');
        var maxMsg = this.getParameterByKey(params, 'maxMsg');
        var msg;
        if ((OPF.isBlank(value) && min > 0) ||
            (OPF.isNotBlank(value) && value.length < min)) {
            msg = OPF.isNotBlank(minMsg) ?
                    minMsg : '\'' + field.getName() + '\' should be longer or equal then ' + min + 'characters';
            validateMessages.push(msg);
        }
        if (OPF.isNotBlank(value) && value.length > max) {
            msg = OPF.isNotBlank(maxMsg) ?
                    maxMsg : '\'' + field.getName() + '\' should shorter or equal then ' + max + 'characters';
            validateMessages.push(msg);
        }
    },

    lessThanValidatorProcessor: function(field, value, params, errorMessage, validateMessages) {
        var lessThanValue = this.getParameterByKey(params, 'lessThanValue');
        var checkEquality = this.getParameterByKey(params, 'checkEquality');
        if (OPF.isNotEmpty(lessThanValue) && OPF.isNotBlank(value)) {
            if ((value == lessThanValue && !checkEquality) || (value > lessThanValue)) {
                var msg = OPF.isNotBlank(errorMessage) ?
                    errorMessage : '\'' + field.getName() + '\' should less then ' + (checkEquality ? 'or equal ' : '') + lessThanValue;
                validateMessages.push(msg);
            }
        }
    },

    getParameterByKey: function(params, key) {
        if (OPF.isNotEmpty(params) && params.length > 0) {
            for (var i = 0; i < params.length; i++) {
                if (params[i].key == key) {
                    return params[i].value;
                }
            }
        }
        return null;
    },

    showValidationMessages: function(response) {
        if (OPF.isNotEmpty(this.form)) {
            this.form.getEl().unmask();
        }
        if (OPF.isNotEmpty(response)) {
            var responseData = Ext.decode(response.responseText);
            if (OPF.isNotEmpty(responseData) && OPF.isNotEmpty(responseData.data)) {
                var data = responseData.data;
                if (OPF.isNotEmpty(this.form)) {
                    var msg, fieldName, field;
                    for (var i = 0; i < data.length; i++) {
                        msg = data[i].msg;
                        fieldName = data[i].name;
                        if (OPF.isNotBlank(fieldName)) {
                            field = this.form.getForm().findField(fieldName);
                            if (field != null) {
                                field.markInvalid(msg);
                            }
                        }
                    }
                }

                if (OPF.isEmpty(this.messagePanel)) {
                    this.messagePanel = Ext.ComponentMgr.create({
                        xtype: 'notice-container',
                        form: this.form
                    });
                    this.form.insert(0, this.messagePanel);
                }
                this.messagePanel.cleanActiveErrors();
                if (data.length > 0) {
                    var errors = [];
                    Ext.each(data, function(data) {
                        if (OPF.isNotBlank(data.msg)) {
                            errors.push({
                                level: OPF.core.validation.MessageLevel.ERROR,
                                msg: data.msg
                            });
                        }
                    });
                    this.messagePanel.setNoticeContainer(errors);
                }
            } else if (OPF.isNotEmpty(responseData) && OPF.isNotEmpty(responseData.success) &&
                !responseData.success && OPF.isNotEmpty(responseData.message)) {
                if (OPF.isEmpty(this.messagePanel)) {
                    this.messagePanel = Ext.ComponentMgr.create({
                        xtype: 'notice-container',
                        form: this.form
                    });
                    this.form.insert(0, this.messagePanel);
                }
                this.messagePanel.cleanActiveErrors();
                var error = [];
                error.push({
                    level: OPF.core.validation.MessageLevel.ERROR,
                    msg: responseData.message
                });
                this.messagePanel.setNoticeContainer(error);
            }
        }
    },

    hideValidationMessages: function(response) {
        if (OPF.isNotEmpty(this.messagePanel)) {
            this.messagePanel.cleanActiveErrors();
        }

        this.form.getEl().unmask();
        if (this.clearInvalidOnly === true) {
            this.form.getForm().clearInvalid();
        }

        if (OPF.isNotEmpty(response)) {
            var data = Ext.decode(response.responseText);
            if (OPF.isNotBlank(data.message)) {
                this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, data.message);
            }
        }
    }

});


Ext.namespace('OPF.core.validation');

Ext.define('OPF.core.validation.MessageLevel', {

    constructor: function(level, css, cfg) {
        cfg = cfg || {};
        OPF.core.validation.MessageLevel.superclass.constructor.call(this, cfg);
        this.level = level;
        this.css = css;
    },
    getLevel: function() {
        return this.level;
    },
    getCSS: function() {
        return this.css;
    }
});

OPF.core.validation.MessageLevel.TRACE = new OPF.core.validation.MessageLevel('TRACE', 'msg-trace');
OPF.core.validation.MessageLevel.DEBUG = new OPF.core.validation.MessageLevel('DEBUG', 'msg-debug');
OPF.core.validation.MessageLevel.INFO  = new OPF.core.validation.MessageLevel('INFO',  'msg-info');
OPF.core.validation.MessageLevel.WARN  = new OPF.core.validation.MessageLevel('WARN',  'msg-warn');
OPF.core.validation.MessageLevel.ERROR = new OPF.core.validation.MessageLevel('ERROR', 'msg-error');
OPF.core.validation.MessageLevel.FATAL = new OPF.core.validation.MessageLevel('FATAL', 'msg-fatal');


Ext.apply(Ext.form.field.VTypes, {
    password: function(value, field) {
        var valid = false;
        if (field.isDisabled() && otherField.isDisabled()) {
            valid = true;
        } else {
            if (field.compareWithId) {
                var otherField = Ext.getCmp(field.compareWithId);
                var otherValue = otherField.getValue();
                if (field.updateMode && otherField.updateMode && OPF.isBlank(value) && OPF.isBlank(otherValue)) {
                    valid = true;
                } else {
                    if (value == otherValue) {
                        if (value.match(/[0-9!@#\$%\^&\*\(\)\-_=\+]+/i)) {
                            otherField.clearInvalid();
                            if(value.length >= 6){
                                valid = true;
                            } else {
                                this.passwordText = 'Password should have length equal or longer than 6 characters.';
                                valid = false;
                            }
                        } else {
                            this.passwordText = 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)';
                            valid = false;
                        }
                    } else {
                        this.passwordText = 'The passwords entered do not match.';
                        valid = false;
                    }
                }
            }
        }
        return valid;
    },
    passwordText: 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)'
});


Ext.apply(Ext.form.VTypes,{
	denyBlank: function(value) {
        return OPF.isNotBlank(value) && value.replace(/\s/g, '').length > 0;
	},
    denyBlankText: 'Only spaces are not allowed.',
    denyBlankMask: /./
});
