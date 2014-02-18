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

//    proxy: {
//        type: 'localstorage',
//        id: 'directories'
//    }

//    proxy: {
//        type: 'memory',
//        reader: {
//            type: 'json',
//            root: 'data'
//        }
//    }

});


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
//  before use it need to be sure that all urls are forming correct, special attention must be taken care of store.beforeLoad function
//        if (this.DEBUG_MODE == true) {
//            url = this.addParameterToURL(url, 'debug', 'true');
//        }
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

Ext.define('OPF.console.directory.model.UserProfileFieldGroup', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/user_profile_field_group',
//        editorClassName: 'OPF.console.directory.editor.UserEditor',
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

Ext.define('OPF.console.directory.model.UserProfileField', {
    extend:  Ext.data.Model ,

    statics: {
        pageSuffixUrl: 'console/directory',
        restSuffixUrl: 'directory/user_profile_field',
//        editorClassName: 'OPF.console.directory.editor.UserEditor',
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
//    ,
//    associations: [
//        {
//            type: 'hasMany',
//            model: 'OPF.console.domain.model.BIReportFieldModel',
//            name: 'fields',
//            associationKey:'fields',
//            associationName: 'fields'
//        }
//    ]

});
//@tag opf-model
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
//        { name: 'function', type: 'string' },

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

Ext.define('OPF.console.domain.model.UserActorModel', {
    extend:  Ext.data.Model ,

    fields: [
        { name: 'id', type: 'int'},
        { name: 'username', type: 'string' },
        { name: 'userActorId', type: 'int' }
    ]

});
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
        { name: 'performedOn', type: 'date', dateFormat: 'time' }, //'U000'},

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});
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
//            associationKey: 'processCase',
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
//            associationKey: 'activity',
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
//            associationKey: 'assignee',
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
        { model: 'NavigationElement', name: 'navigationElements', foreignKey: 'parent_id' }//,
//        { model: 'Permission', name: 'permissions' }
    ]

});
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
//            field.on('validitychange', me.showErrors, me);
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
//            console.log("Canceled task");
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
//        console.log("Executed task");
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
            standardSubmit: true, // do not use Ajax submit (default ExtJs)
            items: fields
        });
        exportForm.getForm().submit();
    }

});
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
/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

    /**
     *
     */
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

//    listeners: {
//        afterrender: function(box) {
//            var e = box.getEl();
//            if (e) {
//                e.on('click', box.onClick, box);
//            }
//        }
//    }
});
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
//                    OPF.Msg.setAlert(vo.success, vo.message);

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
//                OPF.Msg.setAlert(vo.success, vo.message);

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

Ext.define('OPF.core.component.MenuItem', {
    extend:  Ext.menu.Item ,
    alias: 'widget.opf-menuitem',

    renderTpl: [
        '<tpl if="plain">',
            '{text}',
        '<tpl else>',
            '<a id="{id}-itemEl" class="' + Ext.baseCSSPrefix + 'menu-item-link" href="{href}" <tpl if="hrefTarget">target="{hrefTarget}"</tpl> hidefocus="true" unselectable="on">',
//                '<img id="{id}-iconEl" src="{icon}" class="' + Ext.baseCSSPrefix + 'menu-item-icon {iconCls}" />',
                '<span id="{id}-textEl" class="' + Ext.baseCSSPrefix + 'menu-item-text header" <tpl if="arrowCls"></tpl> ><b>{text}</b></span><br/>',
                '<span id="{id}-descriptionEl" class="' + Ext.baseCSSPrefix + 'menu-item-text" <tpl if="arrowCls"></tpl> >{description}</span>',
//                '<img id="{id}-arrowEl" src="{blank}" class="{arrowCls}" />',
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

Ext.ns('OPF.core.component');

/**
 * The NavigationMenu class supplies the persistent navigation for the entire
 * Openflame Console application using toggle buttons to indicate the current page.
 * This navigation control appears on all pages of the site. 
 */
Ext.define('OPF.core.component.NavigationMenu', {
    extend:  Ext.toolbar.Toolbar ,
    cls: 'header',
    height: 39,

    alias : 'widget.navigation-menu',

    lookup: '',
    activeButtonLookup: null,
    navigationElements: null,

    /**
     * Initializes the layout of the nav bar and ensures that the appropriate links
     * are in place to support navigation across the application.
     */
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

//                    var spacerComponent = Ext.ComponentMgr.create({
//                        xtype: 'tbspacer',
//                        width: 0
//                    });
//                    me.insert(index * 2, navigationElementButton);
//                    me.insert(index * 2 + 1, spacerComponent);

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

//                me.doLayout();
//                if (OPF.isNotEmpty(activeButton)) {
//                    activeButton.toggle(true);
//                }
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
//            myWindow.document.write('<link rel="Stylesheet" type="text/css" href="' + this.printCSS + '" />');
            myWindow.document.write('</head><body>');
            myWindow.document.write(html);
            myWindow.document.write('</body></html>');
            myWindow.print();
        }
    }

});
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


Ext.define('OPF.core.component.SelectFileField', {
    extend:  OPF.core.component.TextField ,
    alias : 'widget.opf-selectfilefield',

    cls: 'x-form-field-filepath',

    /**
     * @cfg {Boolean} buttonOnly
     * True to display the file upload field as a button with no visible text field. If true, all
     * inherited Text members will still be available.
     */
    buttonOnly: false,

    /**
     * @cfg {Number} buttonMargin
     * The number of pixels of space reserved between the button and the text field. Note that this only
     * applies if {@link #buttonOnly} = false.
     */
    buttonMargin: 3,

    /**
     * @cfg {Object} buttonConfig
     * A standard {@link Ext.button.Button} config object.
     */

    /**
     * @event change
     * Fires when the underlying file input field's value has changed from the user selecting a new file from the system
     * file selection dialog.
     * @param {Ext.ux.form.FileUploadField} this
     * @param {String} value The file value returned by the underlying file input field
     */

    /**
     * @property {Ext.Element} fileInputEl
     * A reference to the invisible file input element created for this upload field. Only populated after this
     * component is rendered.
     */

    /**
     * @property {Ext.button.Button} button
     * A reference to the trigger Button component created for this upload field. Only populated after this component is
     * rendered.
     */

    /**
     * @cfg {String} [fieldBodyCls='x-form-file-wrap']
     * An extra CSS class to be applied to the body content element in addition to {@link #fieldBodyCls}.
     */
//    fieldBodyCls: Ext.baseCSSPrefix + 'form-file-wrap',

    // private
    componentLayout: 'filefield',

    // private
    onRender: function() {
        var me = this,
            inputEl;

        me.callParent(arguments);

        me.createButton();

        // we don't create the file/button til after onRender, the initial disable() is
        // called in the onRender of the component.
        if (me.disabled) {
            me.disableItems();
        }

        inputEl = me.inputEl;
        if (me.buttonOnly) {
            inputEl.setDisplayed(false);
        }
    },

    /**
     * @private
     * Creates the custom trigger Button component. The fileInput will be inserted into this.
     */
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

    /**
     * @private Event handler fired when the user selects a file.
     */
    onFileChange: function() {
        this.lastValue = null; // force change event to get fired even if the user selects a file with the same name
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

    /**
     * The function that should handle the selectFile's click event.  This method does nothing by default
     * until overridden by an implementing function.  See Ext.form.ComboBox and Ext.form.DateField for
     * sample implementations.
     * @method
     */
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

/**
 * Tooltip on mouse click
 */


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

/**
 * UploadFileDialog allows user to upload a new file
 */
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
//            dialog.form.getForm().reset();
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

    interval: 30, //in minutes

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
//            var format = index == 0 ? 'l, F d, Y' : 'l d';
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
//                padding:'30 0 0 0',
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

        // maybe need to join crossed event groups??? need to check on big count of events

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
/**
 * BoxSelect for ExtJS 4.1, a combo box improved for multiple value querying, selection and management.
 *
 * A friendlier combo box for multiple selections that creates easily individually
 * removable labels for each selection, as seen on facebook and other sites. Querying
 * and type-ahead support are also improved for multiple selections.
 *
 * Options and usage mostly remain consistent with the standard
 * [ComboBox](http://docs.sencha.com/ext-js/4-1/#!/api/Ext.form.field.ComboBox) control.
 * Some default configuration options have changed, but most should still work properly
 * if overridden unless otherwise noted.
 *
 * Please note, this component does not support versions of ExtJS earlier than 4.1.
 *
 * Inspired by the [SuperBoxSelect component for ExtJS 3](http://technomedia.co.uk/SuperBoxSelect/examples3.html),
 * which in turn was inspired by the [BoxSelect component for ExtJS 2](http://efattal.fr/en/extjs/extuxboxselect/).
 *
 * Various contributions and suggestions made by many members of the ExtJS community which can be seen
 * in the [official user extension forum post](http://www.sencha.com/forum/showthread.php?134751-Ext.ux.form.field.BoxSelect).
 *
 * Many thanks go out to all of those who have contributed, this extension would not be
 * possible without your help.
 *
 * See [AUTHORS.txt](../AUTHORS.TXT) for a list of major contributors
 *
 * @author kvee_iv http://www.sencha.com/forum/member.php?29437-kveeiv
 * @version 2.0.3
 * @requires BoxSelect.css
 * @xtype boxselect
 *
 */
Ext.define('Ext.ux.form.field.BoxSelect', {
    extend: Ext.form.field.ComboBox ,
    alias: ['widget.comboboxselect', 'widget.boxselect'],
                                                        

    //
    // Begin configuration options related to selected values
    //

    /**
     * @cfg {Boolean}
     * If set to `true`, allows the combo field to hold more than one value at a time, and allows selecting multiple
     * items from the dropdown list. The combo's text field will show all selected values using the template
     * defined by {@link #labelTpl}.
     *

     */
    multiSelect: true,

    /**
     * @cfg {String/Ext.XTemplate} labelTpl
     * The [XTemplate](http://docs.sencha.com/ext-js/4-1/#!/api/Ext.XTemplate) to use for the inner
     * markup of the labelled items. Defaults to the configured {@link #displayField}
     */

    /**
     *
     * When {@link #forceSelection} is `false`, new records can be created by the user as they
     * are typed. These records are **not** added to the combo's store. This creation
     * is triggered by typing the configured 'delimiter', and can be further configured using the
     * {@link #createNewOnEnter} and {@link #createNewOnBlur} configuration options.
     *
     * This functionality is primarily useful with BoxSelect components for things
     * such as an email address.
     */
    forceSelection: true,

    /**
	 * @cfg {Boolean}
     * Has no effect if {@link #forceSelection} is `true`.
     *
	 * With {@link #createNewOnEnter} set to `true`, the creation described in
     * {@link #forceSelection} will also be triggered by the 'enter' key.
	 */
    createNewOnEnter: false,

    /**
	 * @cfg {Boolean}
     * Has no effect if {@link #forceSelection} is `true`.
     *
     * With {@link #createNewOnBlur} set to `true`, the creation described in
     * {@link #forceSelection} will also be triggered when the field loses focus.
     *
     * Please note that this behavior is also affected by the configuration options
     * {@link #autoSelect} and {@link #selectOnTab}. If those are true and an existing
     * item would have been selected as a result, the partial text the user has entered will
	 * be discarded and the existing item will be added to the selection.
	 */
    createNewOnBlur: false,

    /**
     * @cfg {Boolean}
     * Has no effect if {@link #multiSelect} is `false`.
     *
     * Controls the formatting of the form submit value of the field as returned by {@link #getSubmitValue}
     *
     * - `true` for the field value to submit as a json encoded array in a single GET/POST variable
     * - `false` for the field to submit as an array of GET/POST variables
     */
    encodeSubmitValue: false,

    //
    // End of configuration options related to selected values
    //



    //
    // Configuration options related to pick list behavior
    //

    /**
     * @cfg {Boolean}
     * `true` to activate the trigger when clicking in empty space in the field. Note that the
     * subsequent behavior of this is controlled by the field's {@link #triggerAction}.
     * This behavior is similar to that of a basic ComboBox with {@link #editable} `false`.
     */
    triggerOnClick: true,

    /**
	 * @cfg {Boolean}
     * - `true` to have each selected value fill to the width of the form field
     * - `false to have each selected value size to its displayed contents
	 */
    stacked: false,

    /**
	 * @cfg {Boolean}
     * Has no effect if {@link #multiSelect} is `false`
     *
     * `true` to keep the pick list expanded after each selection from the pick list
     * `false` to automatically collapse the pick list after a selection is made
	 */
    pinList: true,

    /**
     * @cfg {Boolean}
     * True to hide the currently selected values from the drop down list. These items are hidden via
     * css to maintain simplicity in store and filter management.
     *
     * - `true` to hide currently selected values from the drop down pick list
     * - `false` to keep the item in the pick list as a selected item
     */
    filterPickList: false,

    //
    // End of configuration options related to pick list behavior
    //



    //
    // Configuration options related to text field behavior
    //
    selectOnFocus: true,

    /**
     * @cfg {Boolean}
     *
     * `true` if this field should automatically grow and shrink vertically to its content.
     * Note that this overrides the natural trigger grow functionality, which is used to size
     * the field horizontally.
     */
    grow: true,

    /**
     * @cfg {Number/Boolean}
     * Has no effect if {@link #grow} is `false`
     *
     * The minimum height to allow when {@link #grow} is `true`, or `false` to allow for
     * natural vertical growth based on the current selected values. See also {@link #growMax}.
     */
    growMin: false,

    /**
     * @cfg {Number/Boolean}
     * Has no effect if {@link #grow} is `false`
     *
     * The maximum height to allow when {@link #grow} is `true`, or `false` to allow for
     * natural vertical growth based on the current selected values. See also {@link #growMin}.
     */
    growMax: false,

    /**
     * @cfg growAppend
     * @hide
     * Currently unsupported by BoxSelect since this is used for horizontal growth and
     * BoxSelect only supports vertical growth.
     */
    /**
     * @cfg growToLongestValue
     * @hide
     * Currently unsupported by BoxSelect since this is used for horizontal growth and
     * BoxSelect only supports vertical growth.
     */

    //
    // End of configuration options related to text field behavior
    //


    //
    // Event signatures
    //

    /**
     * @event autosize
     * Fires when the **{@link #autoSize}** function is triggered and the field is resized according to the
     * {@link #grow}/{@link #growMin}/{@link #growMax} configs as a result. This event provides a hook for the
     * developer to apply additional logic at runtime to resize the field if needed.
     * @param {Ext.ux.form.field.BoxSelect} this This BoxSelect field
     * @param {Number} height The new field height
     */

    //
    // End of event signatures
    //



    //
    // Configuration options that will break things if messed with
    //

    /**
     * @private
     */
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

    /**
     * @private
     */
    childEls: [ 'listWrapper', 'itemList', 'inputEl', 'inputElCt' ],

    /**
     * @private
     */
    componentLayout: 'boxselectfield',

    /**
     * @inheritdoc
     *
     * Initialize additional settings and enable simultaneous typeAhead and multiSelect support
     * @protected
	 */
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

    /**
	 * Register events for management controls of labelled items
     * @protected
	 */
    initEvents: function() {
        var me = this;

        me.callParent(arguments);

        if (!me.enableKeyEvents) {
            me.mon(me.inputEl, 'keydown', me.onKeyDown, me);
        }
        me.mon(me.inputEl, 'paste', me.onPaste, me);
        me.mon(me.listWrapper, 'click', me.onItemListClick, me);

        // I would prefer to use relayEvents here to forward these events on, but I want
        // to pass the field instead of exposing the underlying selection model
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

    /**
     * @inheritdoc
     *
	 * Create a store for the records of our current value based on the main store's model
     * @protected
	 */
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

    /**
     * @inheritdoc
     *
     * Remove the selected value store and associated listeners
     * @protected
     */
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

    /**
     * @inheritdoc
     *
	 * Add refresh tracking to the picker for selection management
     * @protected
	 */
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

    /**
     * @inheritdoc
     *
	 * Clean up selected values management controls
     * @protected
	 */
    onDestroy: function() {
        var me = this;

        Ext.destroyMembers(me, 'valueStore', 'selectionModel');

        me.callParent(arguments);
    },

    /**
     * Add empty text support to initial render.
     * @protected
     */
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

    /**
     * @inheritdoc
     *
	 * Overridden to avoid use of placeholder, as our main input field is often empty
     * @protected
	 */
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

    /**
	 * Overridden to search entire unfiltered store since already selected values
     * can span across multiple store page loads and other filtering. Overlaps
     * some with {@link #isFilteredRecord}, but findRecord is used by the base component
     * for various logic so this logic is applied here as well.
     * @protected
	 */
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

    /**
	 * Overridden to map previously selected records to the "new" versions of the records
	 * based on value field, if they are part of the new store load
     * @protected
	 */
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

    /**
	 * Used to determine if a record is filtered out of the current store's data set,
     * for determining if a currently selected value should be retained.
     *
     * Slightly complicated logic. A record is considered filtered and should be retained if:
     *
     * - It is not in the combo store and the store has no filter or it is in the filtered data set
     *   (Happens when our selected value is just part of a different load, page or query)
     * - It is not in the combo store and forceSelection is false and it is in the value store
     *   (Happens when our selected value was created manually)
     *
	 * @private
	 */
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

    /**
     * @inheritdoc
     *
	 * Overridden to allow for continued querying with multiSelect selections already made
     * @protected
	 */
    doRawQuery: function() {
        var me = this,
        rawValue = me.inputEl.dom.value;

        if (me.multiSelect) {
            rawValue = rawValue.split(me.delimiter).pop();
        }

        this.doQuery(rawValue, false, true);
    },

    /**
	 * When the picker is refreshing, we should ignore selection changes. Otherwise
	 * the value of our field will be changing just because our view of the choices is.
     * @protected
	 */
    onBeforeListRefresh: function() {
        this.ignoreSelection++;
    },

    /**
	 * When the picker is refreshing, we should ignore selection changes. Otherwise
	 * the value of our field will be changing just because our view of the choices is.
     * @protected
	 */
    onListRefresh: function() {
        this.callParent(arguments);
        if (this.ignoreSelection > 0) {
            --this.ignoreSelection;
        }
    },

    /**
	 * Overridden to preserve current labelled items when list is filtered/paged/loaded
	 * and does not include our current value. See {@link #isFilteredRecord}
     * @private
	 */
    onListSelectionChange: function(list, selectedRecords) {
        var me = this,
        valueStore = me.valueStore,
        mergedRecords = [],
        i;

        // Only react to selection if it is not called from setValue, and if our list is
        // expanded (ignores changes to the selection model triggered elsewhere)
        if ((me.ignoreSelection <= 0) && me.isExpanded) {
            // Pull forward records that were already selected or are now filtered out of the store
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

    /**
     * Overridden to use valueStore instead of valueModels, for inclusion of
     * filtered records. See {@link #isFilteredRecord}
     * @private
     */
    syncSelection: function() {
        var me = this,
        picker = me.picker,
        valueField = me.valueField,
        pickStore, selection, selModel;

        if (picker) {
            pickStore = picker.store;

            // From the value, find the Models that are in the store's current data
            selection = [];
            if (me.valueStore) {
                me.valueStore.each(function(rec) {
                    var i = pickStore.findExact(valueField, rec.get(valueField));
                    if (i >= 0) {
                        selection.push(pickStore.getAt(i));
                    }
                });
            }

            // Update the selection to match
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

    /**
	 * Overridden to align to itemList size instead of inputEl
     */
    doAlign: function(){
        var me = this,
            picker = me.picker,
            aboveSfx = '-above',
            isAbove;

        me.picker.alignTo(me.listWrapper, me.pickerAlign, me.pickerOffset);
        // add the {openCls}-above class if the picker was aligned above
        // the field due to hitting the bottom of the viewport
        isAbove = picker.el.getY() < me.inputEl.getY();
        me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls + aboveSfx);
        picker[isAbove ? 'addCls' : 'removeCls'](picker.baseCls + aboveSfx);
    },

    /**
     * Overridden to preserve scroll position of pick list when list is realigned
     */
    alignPicker: function() {
        var me = this,
            picker = me.picker,
            pickerScrollPos = picker.getTargetEl().dom.scrollTop;

        me.callParent(arguments);

        if (me.isExpanded) {
            if (me.matchFieldWidth) {
                // Auto the height (it will be constrained by min and max width) unless there are no records to display.
                picker.setWidth(me.listWrapper.getWidth());
            }

            picker.getTargetEl().dom.scrollTop = pickerScrollPos;
        }
    },

    /**
	 * Get the current cursor position in the input field, for key-based navigation
	 * @private
	 */
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

    /**
	 * Check to see if the input field has selected text, for key-based navigation
	 * @private
	 */
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

    /**
	 * Handles keyDown processing of key-based selection of labelled items.
     * Supported keyboard controls:
     *
     * - If pick list is expanded
     *
     *     - `CTRL-A` will select all the items in the pick list
     *
     * - If the cursor is at the beginning of the input field and there are values present
     *
     *     - `CTRL-A` will highlight all the currently selected values
     *     - `BACKSPACE` and `DELETE` will remove any currently highlighted selected values
     *     - `RIGHT` and `LEFT` will move the current highlight in the appropriate direction
     *     - `SHIFT-RIGHT` and `SHIFT-LEFT` will add to the current highlight in the appropriate direction
     *
     * @protected
	 */
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
            // CTRL-A when picker is expanded - add all items in current picker store page to current value
            me.select(me.getStore().getRange());
            selModel.setLastFocused(null);
            selModel.deselectAll();
            me.collapse();
            me.inputEl.focus();
            stopEvent = true;
        } else if ((valueStore.getCount() > 0) &&
                ((rawValue == '') || ((me.getCursorPosition() === 0) && !me.hasSelectedText()))) {
            // Keyboard navigation of current values
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

        // Prevent key up processing for enter if it is being handled by the picker
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

    /**
	 * Handles auto-selection and creation of labelled items based on this field's
     * delimiter, as well as the keyUp processing of key-based selection of labelled items.
     * @protected
	 */
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

    /**
     * Handles auto-selection of labelled items based on this field's delimiter when pasting
     * a list of values in to the field (e.g., for email addresses)
     * @protected
     */
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

    /**
     * Overridden to handle key navigation of pick list when list is filtered. Because we
     * want to avoid complexity that could be introduced by modifying the store's contents,
     * (e.g., always having to search back through and remove values when they might
     * be re-sent by the server, adding the values back in their previous position when
     * they are removed from the current selection, etc.), we handle this filtering
     * via a simple css rule. However, for the moment since those DOM nodes still exist
     * in the list we have to hijack the highlighting methods for the picker's BoundListKeyNav
     * to appropriately skip over these hidden nodes. This is a less than ideal solution,
     * but it centralizes all of the complexity of this problem in to this one method.
     * @protected
     */
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

    /**
	 * Overridden to get and set the DOM value directly for type-ahead suggestion (bypassing get/setRawValue)
     * @protected
	 */
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

    /**
	 * Delegation control for selecting and removing labelled items or triggering list collapse/expansion
     * @protected
	 */
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

    /**
	 * Build the markup for the labelled items. Template must be built on demand due to ComboBox initComponent
	 * lifecycle for the creation of on-demand stores (to account for automatic valueField/displayField setting)
     * @private
	 */
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

    /**
	 * Update the labelled items rendering
     * @private
	 */
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

    /**
	 * Returns the record from valueStore for the labelled item node
	 */
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

    /**
	 * Toggle of labelled item selection by node reference
	 */
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

    /**
	 * Removal of labelled item by node reference
	 */
    removeByListItemNode: function(itemEl) {
        var me = this,
        rec = me.getRecordByListItemNode(itemEl);

        if (rec) {
            me.valueStore.remove(rec);
            me.setValue(me.valueStore.getRange());
        }
    },

    /**
     * @inheritdoc
	 * Intercept calls to getRawValue to pretend there is no inputEl for rawValue handling,
	 * so that we can use inputEl for user input of just the current value.
	 */
    getRawValue: function() {
        var me = this,
        inputEl = me.inputEl,
        result;
        me.inputEl = false;
        result = me.callParent(arguments);
        me.inputEl = inputEl;
        return result;
    },

    /**
     * @inheritdoc
	 * Intercept calls to setRawValue to pretend there is no inputEl for rawValue handling,
	 * so that we can use inputEl for user input of just the current value.
	 */
    setRawValue: function(value) {
        var me = this,
        inputEl = me.inputEl,
        result;

        me.inputEl = false;
        result = me.callParent([value]);
        me.inputEl = inputEl;

        return result;
    },

    /**
	 * Adds a value or values to the current value of the field
	 * @param {Mixed} value The value or values to add to the current value, see {@link #setValue}
	 */
    addValue: function(value) {
        var me = this;
        if (value) {
            me.setValue(Ext.Array.merge(me.value, Ext.Array.from(value)));
        }
    },

    /**
	 * Removes a value or values from the current value of the field
	 * @param {Mixed} value The value or values to remove from the current value, see {@link #setValue}
	 */
    removeValue: function(value) {
        var me = this;

        if (value) {
            me.setValue(Ext.Array.difference(me.value, Ext.Array.from(value)));
        }
    },

    /**
     * Sets the specified value(s) into the field. The following value formats are recognised:
     *
     * - Single Values
     *
     *     - A string associated to this field's configured {@link #valueField}
     *     - A record containing at least this field's configured {@link #valueField} and {@link #displayField}
     *
     * - Multiple Values
     *
     *     - If {@link #multiSelect} is `true`, a string containing multiple strings as
     *       specified in the Single Values section above, concatenated in to one string
     *       with each entry separated by this field's configured {@link #delimiter}
     *     - An array of strings as specified in the Single Values section above
     *     - An array of records as specified in the Single Values section above
     *
     * In any of the string formats above, the following occurs if an associated record cannot be found:
     *
     * 1. If {@link #forceSelection} is `false`, a new record of the {@link #store}'s configured model type
     *    will be created using the given value as the {@link #displayField} and {@link #valueField}.
     *    This record will be added to the current value, but it will **not** be added to the store.
     * 2. If {@link #forceSelection} is `true` and {@link #queryMode} is `remote`, the list of unknown
     *    values will be submitted as a call to the {@link #store}'s load as a parameter named by
     *    the {@link #valueField} with values separated by the configured {@link #delimiter}.
     *    ** This process will cause setValue to asynchronously process. ** This will only be attempted
     *    once. Any unknown values that the server does not return records for will be removed.
     * 3. Otherwise, unknown values will be removed.
     *
     * @param {Mixed} value The value(s) to be set, see method documentation for details
     * @return {Ext.form.field.Field/Boolean} this, or `false` if asynchronously querying for unknown values
	 */
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

        // For single-select boxes, use the last good (formal record) value if possible
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

    /**
     * Returns the records for the field's current value
     * @return {Array} The records for the field's current value
     */
    getValueRecords: function() {
        return this.valueStore.getRange();
    },

    /**
     * @inheritdoc
     * Overridden to optionally allow for submitting the field as a json encoded array.
     */
    getSubmitData: function() {
        var me = this,
        val = me.callParent(arguments);

        if (me.multiSelect && me.encodeSubmitValue && val && val[me.name]) {
            val[me.name] = Ext.encode(val[me.name]);
        }

        return val;
    },

    /**
	 * Overridden to clear the input field if we are auto-setting a value as we blur.
     * @protected
	 */
    mimicBlur: function() {
        var me = this;

        if (me.selectOnTab && me.picker && me.picker.highlightedItem) {
            me.inputEl.dom.value = '';
        }

        me.callParent(arguments);
    },

    /**
	 * Overridden to handle partial-input selections more directly
	 */
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

    /**
	 * Expand record values for evaluating change and fire change events for UI to respond to
	 */
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

    /**
     * Overridden to be more accepting of varied value types
     */
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

    /**
	 * Overridden to use value (selection) instead of raw value and to avoid the use of placeholder
	 */
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

    /**
	 * Overridden to use inputEl instead of raw value and to avoid the use of placeholder
	 */
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

    /**
	 * Intercept calls to onFocus to add focusCls, because the base field
     * classes assume this should be applied to inputEl
	 */
    onFocus: function() {
        var me = this,
        focusCls = me.focusCls,
        itemList = me.itemList;

        if (focusCls && itemList) {
            itemList.addCls(focusCls);
        }

        me.callParent(arguments);
    },

    /**
	 * Intercept calls to onBlur to remove focusCls, because the base field
     * classes assume this should be applied to inputEl
	 */
    onBlur: function() {
        var me = this,
        focusCls = me.focusCls,
        itemList = me.itemList;

        if (focusCls && itemList) {
            itemList.removeCls(focusCls);
        }

        me.callParent(arguments);
    },

    /**
	 * Intercept calls to renderActiveError to add invalidCls, because the base
     * field classes assume this should be applied to inputEl
	 */
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

    /**
     * Initiate auto-sizing for height based on {@link #grow}, if applicable.
     */
    autoSize: function() {
        var me = this,
        height;

        if (me.grow && me.rendered) {
            me.autoSizing = true;
            me.updateLayout();
        }

        return me;
    },

    /**
     * Track height change to fire {@link #event-autosize} event, when applicable.
     */
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

/**
 * Ensures the input element takes up the maximum amount of remaining list width,
 * or the entirety of the list width if too little space remains. In this case,
 * the list height will be automatically increased to accomodate the new line. This
 * growth will not occur if {@link Ext.ux.form.field.BoxSelect#multiSelect} or
 * {@link Ext.ux.form.field.BoxSelect#grow} is false.
 */
Ext.define('Ext.ux.layout.component.field.BoxSelectField', {
    /* Begin Definitions */
    alias: ['layout.boxselectfield'],
    extend:  Ext.layout.component.field.Trigger ,

    /* End Definitions */

    type: 'boxselectfield',

    /*For proper calculations we need our field to be sized.*/
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
        /*No inputElCt calculations here!*/
    },

    /*Calculate and cache value of input container.*/
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

Ext.define('OPF.core.component.form.FieldContainer', {
    extend:  Ext.form.FieldContainer ,
    alias : ['widget.opf-form-fieldcontainer', 'widget.opf-fieldcontainer'],

    cls: 'opf-field-container',

    mixins: {
        field: Ext.form.field.Field ,
        subLabelable:  OPF.core.component.form.SubLabelable 
    },

    //  configurables
    combineErrors: true,
    //  msgTarget: 'under',
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

Ext.define('OPF.core.component.form.DateTime', {
    extend:  OPF.core.component.form.FieldContainer ,
    alias: ['widget.opf-form-datetime', 'widget.opf-datetime'],

    /**
     * @cfg {String} dateFormat
     * The default is 'Y-m-d'
     */
    dateFormat: 'Y-m-d',
    /**
     * @cfg {String} timeFormat
     * The default is 'H:i:s'
     */
    timeFormat: 'H:i:s',
    /**
     * @cfg {String} dateTimeFormat
     * The format used when submitting the combined value.
     * Defaults to 'Y-m-d H:i:s'
     */
    dateTimeFormat: 'Y-m-d H:i:s',
    /**
     * @cfg {Object} dateConfig
     * Additional config options for the date field.
     */
    dateConfig:{},
    /**
     * @cfg {Object} timeConfig
     * Additional config options for the time field.
     */
    timeConfig:{},

    // properties
    dateValue: null, // Holds the actual date
    /**
     * @property dateField
     * @type Ext.form.field.Date
     */
    dateField: null,
    /**
     * @property timeField
     * @type Ext.form.field.Time
     */
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

//        // this dummy is necessary because Ext.Editor will not check whether an inputEl is present or not
//        this.inputEl = {
//            dom:{},
//            swallowEvent:function(){}
//        };
//
//        me.initField();
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
        // 100ms to focus a new item that belongs to us, otherwise we will assume the user left the field
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

    // Bug? A field-mixin submits the data from getValue, not getSubmitValue
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
        // Save the message and fire the 'invalid' event
        var me = this,
            oldMsg = me.getActiveError();
        me.setActiveErrors(Ext.Array.from(errors));
        if (oldMsg !== me.getActiveError()) {
            me.doComponentLayout();
        }
    },

    clearInvalid : function() {
        // Clear the message and fire the 'valid' event
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

    // private
    inputType : 'hidden',
    hideLabel: true,

    initComponent: function(){
        this.formItemCls += '-hidden';
        this.callParent();
    },

    /**
     * @private
     * Override. Treat undefined and null values as equal to an empty string value.
     */
    isEqual: function(value1, value2) {
        return this.isEqualAsString(value1, value2);
    },

    // These are all private overrides
    //    initEvents: Ext.emptyFn,
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
/*
 * GNU General Public License Usage
 * This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 *
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @description: This class provide aditional format to numbers by extending Ext.form.field.Number
 *
 * @author: Greivin Britton
 * @email: brittongr@gmail.com
 * @version: 2 compatible with ExtJS 4
 */
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

    /**
     * overrides parseValue to remove the format applied by this class
     */
    parseValue: function(value) {
        //Replace the currency symbol and thousand separator
        return OPF.core.component.form.Number.superclass.parseValue.call(this, this.removeFormat(value));
    },

    /**
     * Remove only the format added by this class to let the superclass validate with it's rules.
     * @param {Object} value
     */
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

    /**
     * Display the numeric value with the fixed decimal precision and without the format using the setRawValue, don't need to do a setValue because we don't want a double
     * formatting and process of the value because beforeBlur perform a getRawValue and then a setValue.
     */
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
/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

Ext.define('OPF.core.component.form.SearchComboBox', {
    extend:  OPF.core.component.form.FieldContainer ,
    alias: ['widget.opf-searchcombo'],

    layout: 'anchor',

    name: null,          //association.name + '_' + associationModel.idProperty,
    fieldLabel: null,    //association.displayName || OPF.getSimpleClassName(association.model),
    subFieldLabel: null, //association.displayDescription,
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
//            '<div class="x-boxselect x-form-field x-form-text">',
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
//            '</div>'
            {
                disableFormats: true,
                // member functions:
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
//            multiSelect: this.multiSelect,
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

//    private methods
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

        if (value === null || value.length < 1) { // if it's blank and textfield didn't flag it then it's valid
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

/**
 * A column that will apply the appropriate sort name based on the associated model property's sort key
 */
Ext.define('OPF.core.component.grid.Column', {
    extend:  Ext.grid.column.Column ,
    alias: 'widget.opf-column',

    /**
     * @cfg alternatePropertyName An alternate simple property that will be used if the display field property or
     * association is not populated.  This is useful if you have a foreign key field that sometimes needs to be specifed
     * as a string that may not have a relationship.  I.e. a candidate name that has not had a full candidate
     * object put into the database yet.
     */

    displayField: 'name',

    statics: {
        renderer: function (value, metadata, record, rowIndex, colIndex, store, view) {
            //Get the fields from the model associated with the grid
            //var fields = this.getOwnerHeaderCt().ownerCt.store.model.prototype.fields;
            //var fieldConfig = fields.get(this.dataIndex);
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

    /**
     * Returns the parameter to sort upon when sorting this header.
     * @return {String}
     */
    getSortParam: function() {
        var sortParam = this.dataIndex;
        if (this.displayField) {
            sortParam += '.' + this.displayField;
        }
        return sortParam;
    }

});
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

//    listeners: {
//        initialize: function(editor) {
//            var cssLink = editor.iframe.contentDocument.createElement("link");
//            cssLink.href = OPF.Cfg.OPF_CONSOLE_URL + "/css/simple-html-editor.css";
//            cssLink.rel = "stylesheet";
//            cssLink.type = "text/css";
//            editor.iframe.contentDocument.body.appendChild(cssLink);
//        }
//    },

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

/**
 * @author Oleg Marshalenko
 * @class OPF.core.component.htmleditor.plugins.SpecialHeadingFormats
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a special heading formats.</p>
 */

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

    // private
    onRender: function(){
        var instance = this;
        var cmp = this.cmp;
        Ext.each(this.formatInstructions, function(instruction) {
            instance.cmp.getToolbar().add(
                {
                    xtype: 'button',
                    iconCls: 'editor-without-image',
    //                iconCls: 'editor-' + instruction.id,
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
            // FF, Chrome, Safari
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
//                var node = document.createElement(instruction.tagName);
                var node = this.cmp.getDoc().createElement(instruction.tagName);
                node.className = instruction.className;
                node.innerHTML = range;
                range.deleteContents();
                range.insertNode(node);
            }
        } else if (this.cmp.getDoc().selection) {
            // IE
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

/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.plugins
 * <p>A convenience function that returns a standard set of HtmlEditor buttons.</p>
 * <p>Sample usage:</p>
 * <pre><code>
    new Ext.FormPanel({
        ...
        items : [{
            ...
            xtype           : "htmleditor",
            plugins         : Ext.ux.form.HtmlEditor.plugins()
        }]
    });
 * </code></pre>
 */
Ext.ns('OPF.core.component.htmleditor.plugins.HtmlEditorPlugins');

OPF.core.component.htmleditor.plugins.HtmlEditorPlugins.plugins = function(){
    return [
        new OPF.core.component.htmleditor.plugins.SpecialHeadingFormats()//,
//        new Ext.ux.form.HtmlEditor.Formatblock()
//        new Ext.ux.form.HtmlEditor.Link(),
//        new Ext.ux.form.HtmlEditor.Divider(),
//        new Ext.ux.form.HtmlEditor.Word(),
//        new Ext.ux.form.HtmlEditor.FindAndReplace(),
//        new Ext.ux.form.HtmlEditor.UndoRedo(),
//        new Ext.ux.form.HtmlEditor.Divider(),
//        new Ext.ux.form.HtmlEditor.Image(),
//        new Ext.ux.form.HtmlEditor.Table(),
//        new Ext.ux.form.HtmlEditor.HR(),
//        new Ext.ux.form.HtmlEditor.SpecialCharacters(),
//        new Ext.ux.form.HtmlEditor.HeadingMenu(),
//        new Ext.ux.form.HtmlEditor.IndentOutdent(),
//        new Ext.ux.form.HtmlEditor.SubSuperScript(),
//        new Ext.ux.form.HtmlEditor.RemoveFormat()
    ];
};
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

/**
 * @author Oleg Marshalenko
 * @class OPF.core.component.htmleditor.plugins.SpecialHeadingFormat
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a special heading formats.</p>
 */

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
    // private
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
            // FF, Chrome, Safari
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
//                var node = document.createElement(formatInstruction.tagName);
                var node = this.cmp.getDoc().createElement(formatInstruction.tagName);
                node.className = formatInstruction.className;
                node.innerHTML = range;
                range.deleteContents();
                range.insertNode(node);
            }
        } else if (this.cmp.getDoc().selection) {
            // IE
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
//            console.log("Canceled task");
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

    cleanActiveErrors: function() {  //TODO need to rename
        this.update('');
        this.hide();
    }

});
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

//        this.innerLoadingCmp = new Ext.Component({
//            html: 'Loading...'
//        });

        this.innerImgCnt = Ext.create('Ext.container.Container', {
            autoEl: 'span',
            cls: this.imgInnerCls,
            listeners: renderMouseOverListener,
            html: 'Loading...'
        });

        this.items = [
//            this.innerLoadingCmp,
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
//        this.innerLoadingCmp.el.dom.innerHTML = '';
    }

});
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


Ext.define('OPF.core.component.resource.ImageResourceEditor', {
    extend:  Ext.window.Window ,

    id: 'imageResourceEditorWindow',
    title: 'Edit Image Resource',
    closeAction: 'hide',
    modal: true,
    minWidth: 530,
    minHeight: 200,
//    layout: 'vbox',
//    layoutConfig: {
//        align: 'center'
//    },
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
//        this.setSizeAccordingToImg();
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

                    if (instance.cultureCombo.getValue() == 'AMERICAN') { // TODO compare with GUI language value
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
                    if (me.cultureCombo.getValue() == 'AMERICAN') { // TODO compare with GUI language value
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

Ext.define("OPF.core.component.tinymce.WindowManager", {
//    extend: tinymce.WindowManager,

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
            //border: false,
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
        // Probably not inline
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

        // Init values we do not want changed
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

        // Validate value onKeyPress
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
        // If editor is not created yet, reschedule this call.
        if (!this.editor)
            this.on("editorcreated", function() {
                this.withEd(func);
            }, this);
        // Else if editor is created and initialized
        else if (this.editor.initialized)
            func.call(this);
        // Else if editor is created but not initialized yet.
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

        if (value.length < 1 || value === me.emptyText) { // if it's blank
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

(function(e){var a=/^\s*|\s*$/g,b,d="B".replace(/A(.)|B/,"$1")==="$1";var c={majorVersion:"3",minorVersion:"5b3",releaseDate:"2012-03-29",_init:function(){var s=this,q=document,o=navigator,g=o.userAgent,m,f,l,k,j,r;s.isOpera=e.opera&&opera.buildNumber;s.isWebKit=/WebKit/.test(g);s.isIE=!s.isWebKit&&!s.isOpera&&(/MSIE/gi).test(g)&&(/Explorer/gi).test(o.appName);s.isIE6=s.isIE&&/MSIE [56]/.test(g);s.isIE7=s.isIE&&/MSIE [7]/.test(g);s.isIE8=s.isIE&&/MSIE [8]/.test(g);s.isIE9=s.isIE&&/MSIE [9]/.test(g);s.isGecko=!s.isWebKit&&/Gecko/.test(g);s.isMac=g.indexOf("Mac")!=-1;s.isAir=/adobeair/i.test(g);s.isIDevice=/(iPad|iPhone)/.test(g);s.isIOS5=s.isIDevice&&g.match(/AppleWebKit\/(\d*)/)[1]>=534;if(e.tinyMCEPreInit){s.suffix=tinyMCEPreInit.suffix;s.baseURL=tinyMCEPreInit.base;s.query=tinyMCEPreInit.query;return}s.suffix="";f=q.getElementsByTagName("base");for(m=0;m<f.length;m++){r=f[m].href;if(r){if(/^https?:\/\/[^\/]+$/.test(r)){r+="/"}k=r?r.match(/.*\//)[0]:""}}function h(i){if(i.src&&/tiny_mce(|_gzip|_jquery|_prototype|_full)(_dev|_src)?.js/.test(i.src)){if(/_(src|dev)\.js/g.test(i.src)){s.suffix="_src"}if((j=i.src.indexOf("?"))!=-1){s.query=i.src.substring(j+1)}s.baseURL=i.src.substring(0,i.src.lastIndexOf("/"));if(k&&s.baseURL.indexOf("://")==-1&&s.baseURL.indexOf("/")!==0){s.baseURL=k+s.baseURL}return s.baseURL}return null}f=q.getElementsByTagName("script");for(m=0;m<f.length;m++){if(h(f[m])){return}}l=q.getElementsByTagName("head")[0];if(l){f=l.getElementsByTagName("script");for(m=0;m<f.length;m++){if(h(f[m])){return}}}return},is:function(g,f){if(!f){return g!==b}if(f=="array"&&(g.hasOwnProperty&&g instanceof Array)){return true}return typeof(g)==f},makeMap:function(f,j,h){var g;f=f||[];j=j||",";if(typeof(f)=="string"){f=f.split(j)}h=h||{};g=f.length;while(g--){h[f[g]]={}}return h},each:function(i,f,h){var j,g;if(!i){return 0}h=h||i;if(i.length!==b){for(j=0,g=i.length;j<g;j++){if(f.call(h,i[j],j,i)===false){return 0}}}else{for(j in i){if(i.hasOwnProperty(j)){if(f.call(h,i[j],j,i)===false){return 0}}}}return 1},map:function(g,h){var i=[];c.each(g,function(f){i.push(h(f))});return i},grep:function(g,h){var i=[];c.each(g,function(f){if(!h||h(f)){i.push(f)}});return i},inArray:function(g,h){var j,f;if(g){for(j=0,f=g.length;j<f;j++){if(g[j]===h){return j}}}return -1},extend:function(n,k){var j,f,h,g=arguments,m;for(j=1,f=g.length;j<f;j++){k=g[j];for(h in k){if(k.hasOwnProperty(h)){m=k[h];if(m!==b){n[h]=m}}}}return n},trim:function(f){return(f?""+f:"").replace(a,"")},create:function(o,f,j){var n=this,g,i,k,l,h,m=0;o=/^((static) )?([\w.]+)(:([\w.]+))?/.exec(o);k=o[3].match(/(^|\.)(\w+)$/i)[2];i=n.createNS(o[3].replace(/\.\w+$/,""),j);if(i[k]){return}if(o[2]=="static"){i[k]=f;if(this.onCreate){this.onCreate(o[2],o[3],i[k])}return}if(!f[k]){f[k]=function(){};m=1}i[k]=f[k];n.extend(i[k].prototype,f);if(o[5]){g=n.resolve(o[5]).prototype;l=o[5].match(/\.(\w+)$/i)[1];h=i[k];if(m){i[k]=function(){return g[l].apply(this,arguments)}}else{i[k]=function(){this.parent=g[l];return h.apply(this,arguments)}}i[k].prototype[k]=i[k];n.each(g,function(p,q){i[k].prototype[q]=g[q]});n.each(f,function(p,q){if(g[q]){i[k].prototype[q]=function(){this.parent=g[q];return p.apply(this,arguments)}}else{if(q!=k){i[k].prototype[q]=p}}})}n.each(f["static"],function(p,q){i[k][q]=p});if(this.onCreate){this.onCreate(o[2],o[3],i[k].prototype)}},walk:function(i,h,j,g){g=g||this;if(i){if(j){i=i[j]}c.each(i,function(k,f){if(h.call(g,k,f,j)===false){return false}c.walk(k,h,j,g)})}},createNS:function(j,h){var g,f;h=h||e;j=j.split(".");for(g=0;g<j.length;g++){f=j[g];if(!h[f]){h[f]={}}h=h[f]}return h},resolve:function(j,h){var g,f;h=h||e;j=j.split(".");for(g=0,f=j.length;g<f;g++){h=h[j[g]];if(!h){break}}return h},addUnload:function(j,i){var h=this,g;g=function(){var f=h.unloads,l,m;if(f){for(m in f){l=f[m];if(l&&l.func){l.func.call(l.scope,1)}}if(e.detachEvent){e.detachEvent("onbeforeunload",k);e.detachEvent("onunload",g)}else{if(e.removeEventListener){e.removeEventListener("unload",g,false)}}h.unloads=l=f=w=g=0;if(e.CollectGarbage){CollectGarbage()}}};function k(){var l=document;function f(){l.detachEvent("onstop",f);if(g){g()}l=0}if(l.readyState=="interactive"){if(l){l.attachEvent("onstop",f)}e.setTimeout(function(){if(l){l.detachEvent("onstop",f)}},0)}}j={func:j,scope:i||this};if(!h.unloads){if(e.attachEvent){e.attachEvent("onunload",g);e.attachEvent("onbeforeunload",k)}else{if(e.addEventListener){e.addEventListener("unload",g,false)}}h.unloads=[j]}else{h.unloads.push(j)}return j},removeUnload:function(i){var g=this.unloads,h=null;c.each(g,function(j,f){if(j&&j.func==i){g.splice(f,1);h=i;return false}});return h},explode:function(f,g){if(!f||c.is(f,"array")){return f}return c.map(f.split(g||","),c.trim)},_addVer:function(g){var f;if(!this.query){return g}f=(g.indexOf("?")==-1?"?":"&")+this.query;if(g.indexOf("#")==-1){return g+f}return g.replace("#",f+"#")},_replace:function(h,f,g){if(d){return g.replace(h,function(){var l=f,j=arguments,k;for(k=0;k<j.length-2;k++){if(j[k]===b){l=l.replace(new RegExp("\\$"+k,"g"),"")}else{l=l.replace(new RegExp("\\$"+k,"g"),j[k])}}return l})}return g.replace(h,f)}};c._init();e.tinymce=e.tinyMCE=c})(window);tinymce.create("tinymce.util.Dispatcher",{scope:null,listeners:null,Dispatcher:function(a){this.scope=a||this;this.listeners=[]},add:function(a,b){this.listeners.push({cb:a,scope:b||this.scope});return a},addToTop:function(a,b){this.listeners.unshift({cb:a,scope:b||this.scope});return a},remove:function(a){var b=this.listeners,c=null;tinymce.each(b,function(e,d){if(a==e.cb){c=a;b.splice(d,1);return false}});return c},dispatch:function(){var f,d=arguments,e,b=this.listeners,g;for(e=0;e<b.length;e++){g=b[e];f=g.cb.apply(g.scope,d.length>0?d:[g.scope]);if(f===false){break}}return f}});(function(){var a=tinymce.each;tinymce.create("tinymce.util.URI",{URI:function(e,g){var f=this,i,d,c,h;e=tinymce.trim(e);g=f.settings=g||{};if(/^([\w\-]+):([^\/]{2})/i.test(e)||/^\s*#/.test(e)){f.source=e;return}if(e.indexOf("/")===0&&e.indexOf("//")!==0){e=(g.base_uri?g.base_uri.protocol||"http":"http")+"://mce_host"+e}if(!/^[\w\-]*:?\/\//.test(e)){h=g.base_uri?g.base_uri.path:new tinymce.util.URI(location.href).directory;e=((g.base_uri&&g.base_uri.protocol)||"http")+"://mce_host"+f.toAbsPath(h,e)}e=e.replace(/@@/g,"(mce_at)");e=/^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@\/]*):?([^:@\/]*))?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/.exec(e);a(["source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","anchor"],function(b,j){var k=e[j];if(k){k=k.replace(/\(mce_at\)/g,"@@")}f[b]=k});c=g.base_uri;if(c){if(!f.protocol){f.protocol=c.protocol}if(!f.userInfo){f.userInfo=c.userInfo}if(!f.port&&f.host==="mce_host"){f.port=c.port}if(!f.host||f.host==="mce_host"){f.host=c.host}f.source=""}},setPath:function(c){var b=this;c=/^(.*?)\/?(\w+)?$/.exec(c);b.path=c[0];b.directory=c[1];b.file=c[2];b.source="";b.getURI()},toRelative:function(b){var d=this,f;if(b==="./"){return b}b=new tinymce.util.URI(b,{base_uri:d});if((b.host!="mce_host"&&d.host!=b.host&&b.host)||d.port!=b.port||d.protocol!=b.protocol){return b.getURI()}var c=d.getURI(),e=b.getURI();if(c==e||(c.charAt(c.length-1)=="/"&&c.substr(0,c.length-1)==e)){return c}f=d.toRelPath(d.path,b.path);if(b.query){f+="?"+b.query}if(b.anchor){f+="#"+b.anchor}return f},toAbsolute:function(b,c){b=new tinymce.util.URI(b,{base_uri:this});return b.getURI(this.host==b.host&&this.protocol==b.protocol?c:0)},toRelPath:function(g,h){var c,f=0,d="",e,b;g=g.substring(0,g.lastIndexOf("/"));g=g.split("/");c=h.split("/");if(g.length>=c.length){for(e=0,b=g.length;e<b;e++){if(e>=c.length||g[e]!=c[e]){f=e+1;break}}}if(g.length<c.length){for(e=0,b=c.length;e<b;e++){if(e>=g.length||g[e]!=c[e]){f=e+1;break}}}if(f===1){return h}for(e=0,b=g.length-(f-1);e<b;e++){d+="../"}for(e=f-1,b=c.length;e<b;e++){if(e!=f-1){d+="/"+c[e]}else{d+=c[e]}}return d},toAbsPath:function(e,f){var c,b=0,h=[],d,g;d=/\/$/.test(f)?"/":"";e=e.split("/");f=f.split("/");a(e,function(i){if(i){h.push(i)}});e=h;for(c=f.length-1,h=[];c>=0;c--){if(f[c].length===0||f[c]==="."){continue}if(f[c]===".."){b++;continue}if(b>0){b--;continue}h.push(f[c])}c=e.length-b;if(c<=0){g=h.reverse().join("/")}else{g=e.slice(0,c).join("/")+"/"+h.reverse().join("/")}if(g.indexOf("/")!==0){g="/"+g}if(d&&g.lastIndexOf("/")!==g.length-1){g+=d}return g},getURI:function(d){var c,b=this;if(!b.source||d){c="";if(!d){if(b.protocol){c+=b.protocol+"://"}if(b.userInfo){c+=b.userInfo+"@"}if(b.host){c+=b.host}if(b.port){c+=":"+b.port}}if(b.path){c+=b.path}if(b.query){c+="?"+b.query}if(b.anchor){c+="#"+b.anchor}b.source=c}return b.source}})})();(function(){var a=tinymce.each;tinymce.create("static tinymce.util.Cookie",{getHash:function(d){var b=this.get(d),c;if(b){a(b.split("&"),function(e){e=e.split("=");c=c||{};c[unescape(e[0])]=unescape(e[1])})}return c},setHash:function(j,b,g,f,i,c){var h="";a(b,function(e,d){h+=(!h?"":"&")+escape(d)+"="+escape(e)});this.set(j,h,g,f,i,c)},get:function(i){var h=document.cookie,g,f=i+"=",d;if(!h){return}d=h.indexOf("; "+f);if(d==-1){d=h.indexOf(f);if(d!==0){return null}}else{d+=2}g=h.indexOf(";",d);if(g==-1){g=h.length}return unescape(h.substring(d+f.length,g))},set:function(i,b,g,f,h,c){document.cookie=i+"="+escape(b)+((g)?"; expires="+g.toGMTString():"")+((f)?"; path="+escape(f):"")+((h)?"; domain="+h:"")+((c)?"; secure":"")},remove:function(e,b){var c=new Date();c.setTime(c.getTime()-1000);this.set(e,"",c,b,c)}})})();(function(){function serialize(o,quote){var i,v,t,name;quote=quote||'"';if(o==null){return"null"}t=typeof o;if(t=="string"){v="\bb\tt\nn\ff\rr\"\"''\\\\";return quote+o.replace(/([\u0080-\uFFFF\x00-\x1f\"\'\\])/g,function(a,b){if(quote==='"'&&a==="'"){return a}i=v.indexOf(b);if(i+1){return"\\"+v.charAt(i+1)}a=b.charCodeAt().toString(16);return"\\u"+"0000".substring(a.length)+a})+quote}if(t=="object"){if(o.hasOwnProperty&&o instanceof Array){for(i=0,v="[";i<o.length;i++){v+=(i>0?",":"")+serialize(o[i],quote)}return v+"]"}v="{";for(name in o){if(o.hasOwnProperty(name)){v+=typeof o[name]!="function"?(v.length>1?","+quote:quote)+name+quote+":"+serialize(o[name],quote):""}}return v+"}"}return""+o}tinymce.util.JSON={serialize:serialize,parse:function(s){try{return eval("("+s+")")}catch(ex){}}}})();tinymce.create("static tinymce.util.XHR",{send:function(g){var a,e,b=window,h=0;function f(){if(!g.async||a.readyState==4||h++>10000){if(g.success&&h<10000&&a.status==200){g.success.call(g.success_scope,""+a.responseText,a,g)}else{if(g.error){g.error.call(g.error_scope,h>10000?"TIMED_OUT":"GENERAL",a,g)}}a=null}else{b.setTimeout(f,10)}}g.scope=g.scope||this;g.success_scope=g.success_scope||g.scope;g.error_scope=g.error_scope||g.scope;g.async=g.async===false?false:true;g.data=g.data||"";function d(i){a=0;try{a=new ActiveXObject(i)}catch(c){}return a}a=b.XMLHttpRequest?new XMLHttpRequest():d("Microsoft.XMLHTTP")||d("Msxml2.XMLHTTP");if(a){if(a.overrideMimeType){a.overrideMimeType(g.content_type)}a.open(g.type||(g.data?"POST":"GET"),g.url,g.async);if(g.content_type){a.setRequestHeader("Content-Type",g.content_type)}a.setRequestHeader("X-Requested-With","XMLHttpRequest");a.send(g.data);if(!g.async){return f()}e=b.setTimeout(f,10)}}});(function(){var c=tinymce.extend,b=tinymce.util.JSON,a=tinymce.util.XHR;tinymce.create("tinymce.util.JSONRequest",{JSONRequest:function(d){this.settings=c({},d);this.count=0},send:function(f){var e=f.error,d=f.success;f=c(this.settings,f);f.success=function(h,g){h=b.parse(h);if(typeof(h)=="undefined"){h={error:"JSON Parse error."}}if(h.error){e.call(f.error_scope||f.scope,h.error,g)}else{d.call(f.success_scope||f.scope,h.result)}};f.error=function(h,g){if(e){e.call(f.error_scope||f.scope,h,g)}};f.data=b.serialize({id:f.id||"c"+(this.count++),method:f.method,params:f.params});f.content_type="application/json";a.send(f)},"static":{sendRPC:function(d){return new tinymce.util.JSONRequest().send(d)}}})}());(function(a){a.VK={BACKSPACE:8,DELETE:46,DOWN:40,ENTER:13,LEFT:37,RIGHT:39,SPACEBAR:32,TAB:9,UP:38,modifierPressed:function(b){return b.shiftKey||b.ctrlKey||b.altKey}}})(tinymce);tinymce.util.Quirks=function(d){var l=tinymce.VK,r=l.BACKSPACE,s=l.DELETE,o=d.dom,A=d.selection,q=d.settings;function c(E,D){try{d.getDoc().execCommand(E,false,D)}catch(C){}}function h(){function C(F){var D,H,E,G;D=A.getRng();H=o.getParent(D.startContainer,o.isBlock);if(F){H=o.getNext(H,o.isBlock)}if(H){E=H.firstChild;while(E&&E.nodeType==3&&E.nodeValue.length===0){E=E.nextSibling}if(E&&E.nodeName==="SPAN"){G=E.cloneNode(false)}}d.getDoc().execCommand(F?"ForwardDelete":"Delete",false,null);H=o.getParent(D.startContainer,o.isBlock);tinymce.each(o.select("span.Apple-style-span,font.Apple-style-span",H),function(I){var J=A.getBookmark();if(G){o.replace(G.cloneNode(false),I,true)}else{o.remove(I,true)}A.moveToBookmark(J)})}d.onKeyDown.add(function(D,F){var E;if(F.isDefaultPrevented()){return}E=F.keyCode==s;if((E||F.keyCode==r)&&!l.modifierPressed(F)){F.preventDefault();C(E)}});d.addCommand("Delete",function(){C()})}function B(){function C(F){var E=o.create("body");var G=F.cloneContents();E.appendChild(G);return A.serializer.serialize(E,{format:"html"})}function D(E){var G=C(E);var H=o.createRng();H.selectNode(d.getBody());var F=C(H);return G===F}d.onKeyDown.addToTop(function(F,H){var G=H.keyCode;if(G==s||G==r){var E=A.getRng(true);if(!E.collapsed&&D(E)){F.setContent("",{format:"raw"});F.nodeChanged();H.preventDefault()}}})}function u(){o.bind(d.getBody(),"focusin",function(){A.setRng(A.getRng())})}function m(){d.onKeyDown.add(function(C,F){if(F.keyCode===r){if(A.isCollapsed()&&A.getRng(true).startOffset===0){var E=A.getNode();var D=E.previousSibling;if(D&&D.nodeName&&D.nodeName.toLowerCase()==="hr"){o.remove(D);tinymce.dom.Event.cancel(F)}}}})}function b(){if(!Range.prototype.getClientRects){d.onMouseDown.add(function(D,E){if(E.target.nodeName==="HTML"){var C=D.getBody();C.blur();setTimeout(function(){C.focus()},0)}})}}function x(){d.onClick.add(function(C,D){D=D.target;if(/^(IMG|HR)$/.test(D.nodeName)){A.getSel().setBaseAndExtent(D,0,D,1)}if(D.nodeName=="A"&&o.hasClass(D,"mceItemAnchor")){A.select(D)}C.nodeChanged()})}function y(){function D(){var F=o.getAttribs(A.getStart().cloneNode(false));return function(){var G=A.getStart();if(G!==d.getBody()){o.setAttrib(G,"style",null);tinymce.each(F,function(H){G.setAttributeNode(H.cloneNode(true))})}}}function C(){return !A.isCollapsed()&&A.getStart()!=A.getEnd()}function E(F,G){G.preventDefault();return false}d.onKeyPress.add(function(F,H){var G;if((H.keyCode==8||H.keyCode==46)&&C()){G=D();F.getDoc().execCommand("delete",false,null);G();H.preventDefault();return false}});o.bind(d.getDoc(),"cut",function(G){var F;if(C()){F=D();d.onKeyUp.addToTop(E);setTimeout(function(){F();d.onKeyUp.remove(E)},0)}})}function i(){var D,C;o.bind(d.getDoc(),"selectionchange",function(){if(C){clearTimeout(C);C=0}C=window.setTimeout(function(){var E=A.getRng();if(!D||!tinymce.dom.RangeUtils.compareRanges(E,D)){d.nodeChanged();D=E}},50)})}function z(){document.body.setAttribute("role","application")}function v(){d.onKeyDown.add(function(C,E){if(E.keyCode===r){if(A.isCollapsed()&&A.getRng(true).startOffset===0){var D=A.getNode().previousSibling;if(D&&D.nodeName&&D.nodeName.toLowerCase()==="table"){return tinymce.dom.Event.cancel(E)}}}})}function g(){var C=d.getDoc().documentMode;if(C&&C>7){return}c("RespectVisibilityInDesign",true);o.addClass(d.getBody(),"mceHideBrInPre");d.parser.addNodeFilter("pre",function(D,F){var G=D.length,I,E,J,H;while(G--){I=D[G].getAll("br");E=I.length;while(E--){J=I[E];H=J.prev;if(H&&H.type===3&&H.value.charAt(H.value-1)!="\n"){H.value+="\n"}else{J.parent.insert(new tinymce.html.Node("#text",3),J,true).value="\n"}}}});d.serializer.addNodeFilter("pre",function(D,F){var G=D.length,I,E,J,H;while(G--){I=D[G].getAll("br");E=I.length;while(E--){J=I[E];H=J.prev;if(H&&H.type==3){H.value=H.value.replace(/\r?\n$/,"")}}}})}function f(){o.bind(d.getBody(),"mouseup",function(E){var D,C=A.getNode();if(C.nodeName=="IMG"){if(D=o.getStyle(C,"width")){o.setAttrib(C,"width",D.replace(/[^0-9%]+/g,""));o.setStyle(C,"width","")}if(D=o.getStyle(C,"height")){o.setAttrib(C,"height",D.replace(/[^0-9%]+/g,""));o.setStyle(C,"height","")}}})}function p(){d.onKeyDown.add(function(I,J){var H,C,D,F,G,K,E;if(J.isDefaultPrevented()){return}H=J.keyCode==s;if((H||J.keyCode==r)&&!l.modifierPressed(J)){C=A.getRng();D=C.startContainer;F=C.startOffset;E=C.collapsed;if(D.nodeType==3&&D.nodeValue.length>0&&((F===0&&!E)||(E&&F===(H?0:1)))){nonEmptyElements=I.schema.getNonEmptyElements();J.preventDefault();G=o.create("br",{id:"__tmp"});D.parentNode.insertBefore(G,D);I.getDoc().execCommand(H?"ForwardDelete":"Delete",false,null);D=A.getRng().startContainer;K=D.previousSibling;if(K&&K.nodeType==1&&!o.isBlock(K)&&o.isEmpty(K)&&!nonEmptyElements[K.nodeName.toLowerCase()]){o.remove(K)}o.remove("__tmp")}}})}function e(){d.onKeyDown.add(function(G,H){var E,D,I,C,F;if(H.keyCode!=l.BACKSPACE){return}E=A.getRng();D=E.startContainer;I=E.startOffset;C=o.getRoot();F=D;if(!E.collapsed||I!==0){return}while(F&&F.parentNode.firstChild==F&&F.parentNode!=C){F=F.parentNode}if(F.tagName==="BLOCKQUOTE"){G.formatter.toggle("blockquote",null,F);E.setStart(D,0);E.setEnd(D,0);A.setRng(E);A.collapse(false)}})}function k(){function C(){d._refreshContentEditable();c("StyleWithCSS",false);c("enableInlineTableEditing",false);if(!q.object_resizing){c("enableObjectResizing",false)}}if(!q.readonly){d.onBeforeExecCommand.add(C);d.onMouseDown.add(C)}}function n(){function C(D,E){tinymce.each(o.select("a"),function(H){var F=H.parentNode,G=o.getRoot();if(F.lastChild===H){while(F&&!o.isBlock(F)){if(F.parentNode.lastChild!==F||F===G){return}F=F.parentNode}o.add(F,"br",{"data-mce-bogus":1})}})}d.onExecCommand.add(function(D,E){if(E==="CreateLink"){C(D)}});d.onSetContent.add(A.onSetContent.add(C))}function a(){function C(E,D){if(!E||!D.initial){d.execCommand("mceRepaint")}}d.onUndo.add(C);d.onRedo.add(C);d.onSetContent.add(C)}function j(){d.onKeyDown.add(function(C,D){if(D.keyCode==8&&A.getNode().nodeName=="IMG"){D.preventDefault();C.undoManager.beforeChange();o.remove(A.getNode());C.undoManager.add()}})}v();e();if(tinymce.isWebKit){p();h();B();u();x();if(tinymce.isIDevice){i()}}if(tinymce.isIE){m();B();z();g();f();j()}if(tinymce.isGecko){m();b();y();k();n();a()}};(function(j){var a,g,d,k=/[&<>\"\u007E-\uD7FF\uE000-\uFFEF]|[\uD800-\uDBFF][\uDC00-\uDFFF]/g,b=/[<>&\u007E-\uD7FF\uE000-\uFFEF]|[\uD800-\uDBFF][\uDC00-\uDFFF]/g,f=/[<>&\"\']/g,c=/&(#x|#)?([\w]+);/g,i={128:"\u20AC",130:"\u201A",131:"\u0192",132:"\u201E",133:"\u2026",134:"\u2020",135:"\u2021",136:"\u02C6",137:"\u2030",138:"\u0160",139:"\u2039",140:"\u0152",142:"\u017D",145:"\u2018",146:"\u2019",147:"\u201C",148:"\u201D",149:"\u2022",150:"\u2013",151:"\u2014",152:"\u02DC",153:"\u2122",154:"\u0161",155:"\u203A",156:"\u0153",158:"\u017E",159:"\u0178"};g={'"':"&quot;","'":"&#39;","<":"&lt;",">":"&gt;","&":"&amp;"};d={"&lt;":"<","&gt;":">","&amp;":"&","&quot;":'"',"&apos;":"'"};function h(l){var m;m=document.createElement("div");m.innerHTML=l;return m.textContent||m.innerText||l}function e(m,p){var n,o,l,q={};if(m){m=m.split(",");p=p||10;for(n=0;n<m.length;n+=2){o=String.fromCharCode(parseInt(m[n],p));if(!g[o]){l="&"+m[n+1]+";";q[o]=l;q[l]=o}}return q}}a=e("50,nbsp,51,iexcl,52,cent,53,pound,54,curren,55,yen,56,brvbar,57,sect,58,uml,59,copy,5a,ordf,5b,laquo,5c,not,5d,shy,5e,reg,5f,macr,5g,deg,5h,plusmn,5i,sup2,5j,sup3,5k,acute,5l,micro,5m,para,5n,middot,5o,cedil,5p,sup1,5q,ordm,5r,raquo,5s,frac14,5t,frac12,5u,frac34,5v,iquest,60,Agrave,61,Aacute,62,Acirc,63,Atilde,64,Auml,65,Aring,66,AElig,67,Ccedil,68,Egrave,69,Eacute,6a,Ecirc,6b,Euml,6c,Igrave,6d,Iacute,6e,Icirc,6f,Iuml,6g,ETH,6h,Ntilde,6i,Ograve,6j,Oacute,6k,Ocirc,6l,Otilde,6m,Ouml,6n,times,6o,Oslash,6p,Ugrave,6q,Uacute,6r,Ucirc,6s,Uuml,6t,Yacute,6u,THORN,6v,szlig,70,agrave,71,aacute,72,acirc,73,atilde,74,auml,75,aring,76,aelig,77,ccedil,78,egrave,79,eacute,7a,ecirc,7b,euml,7c,igrave,7d,iacute,7e,icirc,7f,iuml,7g,eth,7h,ntilde,7i,ograve,7j,oacute,7k,ocirc,7l,otilde,7m,ouml,7n,divide,7o,oslash,7p,ugrave,7q,uacute,7r,ucirc,7s,uuml,7t,yacute,7u,thorn,7v,yuml,ci,fnof,sh,Alpha,si,Beta,sj,Gamma,sk,Delta,sl,Epsilon,sm,Zeta,sn,Eta,so,Theta,sp,Iota,sq,Kappa,sr,Lambda,ss,Mu,st,Nu,su,Xi,sv,Omicron,t0,Pi,t1,Rho,t3,Sigma,t4,Tau,t5,Upsilon,t6,Phi,t7,Chi,t8,Psi,t9,Omega,th,alpha,ti,beta,tj,gamma,tk,delta,tl,epsilon,tm,zeta,tn,eta,to,theta,tp,iota,tq,kappa,tr,lambda,ts,mu,tt,nu,tu,xi,tv,omicron,u0,pi,u1,rho,u2,sigmaf,u3,sigma,u4,tau,u5,upsilon,u6,phi,u7,chi,u8,psi,u9,omega,uh,thetasym,ui,upsih,um,piv,812,bull,816,hellip,81i,prime,81j,Prime,81u,oline,824,frasl,88o,weierp,88h,image,88s,real,892,trade,89l,alefsym,8cg,larr,8ch,uarr,8ci,rarr,8cj,darr,8ck,harr,8dl,crarr,8eg,lArr,8eh,uArr,8ei,rArr,8ej,dArr,8ek,hArr,8g0,forall,8g2,part,8g3,exist,8g5,empty,8g7,nabla,8g8,isin,8g9,notin,8gb,ni,8gf,prod,8gh,sum,8gi,minus,8gn,lowast,8gq,radic,8gt,prop,8gu,infin,8h0,ang,8h7,and,8h8,or,8h9,cap,8ha,cup,8hb,int,8hk,there4,8hs,sim,8i5,cong,8i8,asymp,8j0,ne,8j1,equiv,8j4,le,8j5,ge,8k2,sub,8k3,sup,8k4,nsub,8k6,sube,8k7,supe,8kl,oplus,8kn,otimes,8l5,perp,8m5,sdot,8o8,lceil,8o9,rceil,8oa,lfloor,8ob,rfloor,8p9,lang,8pa,rang,9ea,loz,9j0,spades,9j3,clubs,9j5,hearts,9j6,diams,ai,OElig,aj,oelig,b0,Scaron,b1,scaron,bo,Yuml,m6,circ,ms,tilde,802,ensp,803,emsp,809,thinsp,80c,zwnj,80d,zwj,80e,lrm,80f,rlm,80j,ndash,80k,mdash,80o,lsquo,80p,rsquo,80q,sbquo,80s,ldquo,80t,rdquo,80u,bdquo,810,dagger,811,Dagger,81g,permil,81p,lsaquo,81q,rsaquo,85c,euro",32);j.html=j.html||{};j.html.Entities={encodeRaw:function(m,l){return m.replace(l?k:b,function(n){return g[n]||n})},encodeAllRaw:function(l){return(""+l).replace(f,function(m){return g[m]||m})},encodeNumeric:function(m,l){return m.replace(l?k:b,function(n){if(n.length>1){return"&#"+(((n.charCodeAt(0)-55296)*1024)+(n.charCodeAt(1)-56320)+65536)+";"}return g[n]||"&#"+n.charCodeAt(0)+";"})},encodeNamed:function(n,l,m){m=m||a;return n.replace(l?k:b,function(o){return g[o]||m[o]||o})},getEncodeFunc:function(l,o){var p=j.html.Entities;o=e(o)||a;function m(r,q){return r.replace(q?k:b,function(s){return g[s]||o[s]||"&#"+s.charCodeAt(0)+";"||s})}function n(r,q){return p.encodeNamed(r,q,o)}l=j.makeMap(l.replace(/\+/g,","));if(l.named&&l.numeric){return m}if(l.named){if(o){return n}return p.encodeNamed}if(l.numeric){return p.encodeNumeric}return p.encodeRaw},decode:function(l){return l.replace(c,function(n,m,o){if(m){o=parseInt(o,m.length===2?16:10);if(o>65535){o-=65536;return String.fromCharCode(55296+(o>>10),56320+(o&1023))}else{return i[o]||String.fromCharCode(o)}}return d[n]||a[n]||h(n)})}}})(tinymce);tinymce.html.Styles=function(d,f){var k=/rgb\s*\(\s*([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\s*\)/gi,h=/(?:url(?:(?:\(\s*\"([^\"]+)\"\s*\))|(?:\(\s*\'([^\']+)\'\s*\))|(?:\(\s*([^)\s]+)\s*\))))|(?:\'([^\']+)\')|(?:\"([^\"]+)\")/gi,b=/\s*([^:]+):\s*([^;]+);?/g,l=/\s+$/,m=/rgb/,e,g,a={},j;d=d||{};j="\\\" \\' \\; \\: ; : \uFEFF".split(" ");for(g=0;g<j.length;g++){a[j[g]]="\uFEFF"+g;a["\uFEFF"+g]=j[g]}function c(n,q,p,i){function o(r){r=parseInt(r).toString(16);return r.length>1?r:"0"+r}return"#"+o(q)+o(p)+o(i)}return{toHex:function(i){return i.replace(k,c)},parse:function(s){var A={},q,n,y,r,x=d.url_converter,z=d.url_converter_scope||this;function p(E,H){var G,D,C,F;G=A[E+"-top"+H];if(!G){return}D=A[E+"-right"+H];if(G!=D){return}C=A[E+"-bottom"+H];if(D!=C){return}F=A[E+"-left"+H];if(C!=F){return}A[E+H]=F;delete A[E+"-top"+H];delete A[E+"-right"+H];delete A[E+"-bottom"+H];delete A[E+"-left"+H]}function v(D){var E=A[D],C;if(!E||E.indexOf(" ")<0){return}E=E.split(" ");C=E.length;while(C--){if(E[C]!==E[0]){return false}}A[D]=E[0];return true}function B(E,D,C,F){if(!v(D)){return}if(!v(C)){return}if(!v(F)){return}A[E]=A[D]+" "+A[C]+" "+A[F];delete A[D];delete A[C];delete A[F]}function u(C){r=true;return a[C]}function i(D,C){if(r){D=D.replace(/\uFEFF[0-9]/g,function(E){return a[E]})}if(!C){D=D.replace(/\\([\'\";:])/g,"$1")}return D}function o(D,C,G,F,H,E){H=H||E;if(H){H=i(H);return"'"+H.replace(/\'/g,"\\'")+"'"}C=i(C||G||F);if(x){C=x.call(z,C,"style")}return"url('"+C.replace(/\'/g,"\\'")+"')"}if(s){s=s.replace(/\\[\"\';:\uFEFF]/g,u).replace(/\"[^\"]+\"|\'[^\']+\'/g,function(C){return C.replace(/[;:]/g,u)});while(q=b.exec(s)){n=q[1].replace(l,"").toLowerCase();y=q[2].replace(l,"");if(n&&y.length>0){if(n==="font-weight"&&y==="700"){y="bold"}else{if(n==="color"||n==="background-color"){y=y.toLowerCase()}}y=y.replace(k,c);y=y.replace(h,o);A[n]=r?i(y,true):y}b.lastIndex=q.index+q[0].length}p("border","");p("border","-width");p("border","-color");p("border","-style");p("padding","");p("margin","");B("border","border-width","border-style","border-color");if(A.border==="medium none"){delete A.border}}return A},serialize:function(p,r){var o="",n,q;function i(u){var y,v,s,x;y=f.styles[u];if(y){for(v=0,s=y.length;v<s;v++){u=y[v];x=p[u];if(x!==e&&x.length>0){o+=(o.length>0?" ":"")+u+": "+x+";"}}}}if(r&&f&&f.styles){i("*");i(r)}else{for(n in p){q=p[n];if(q!==e&&q.length>0){o+=(o.length>0?" ":"")+n+": "+q+";"}}}return o}}};(function(f){var a={},e=f.makeMap,g=f.each;function d(j,i){return j.split(i||",")}function h(m,l){var j,k={};function i(n){return n.replace(/[A-Z]+/g,function(o){return i(m[o])})}for(j in m){if(m.hasOwnProperty(j)){m[j]=i(m[j])}}i(l).replace(/#/g,"#text").replace(/(\w+)\[([^\]]+)\]\[([^\]]*)\]/g,function(q,o,n,p){n=d(n,"|");k[o]={attributes:e(n),attributesOrder:n,children:e(p,"|",{"#comment":{}})}});return k}function b(){var i=a.html5;if(!i){i=a.html5=h({A:"id|accesskey|class|dir|draggable|item|hidden|itemprop|role|spellcheck|style|subject|title",B:"#|a|abbr|area|audio|b|bdo|br|button|canvas|cite|code|command|datalist|del|dfn|em|embed|i|iframe|img|input|ins|kbd|keygen|label|link|map|mark|meta|meter|noscript|object|output|progress|q|ruby|samp|script|select|small|span|strong|sub|sup|svg|textarea|time|var|video",C:"#|a|abbr|area|address|article|aside|audio|b|bdo|blockquote|br|button|canvas|cite|code|command|datalist|del|details|dfn|dialog|div|dl|em|embed|fieldset|figure|footer|form|h1|h2|h3|h4|h5|h6|header|hgroup|hr|i|iframe|img|input|ins|kbd|keygen|label|link|map|mark|menu|meta|meter|nav|noscript|ol|object|output|p|pre|progress|q|ruby|samp|script|section|select|small|span|strong|style|sub|sup|svg|table|textarea|time|ul|var|video"},"html[A|manifest][body|head]head[A][base|command|link|meta|noscript|script|style|title]title[A][#]base[A|href|target][]link[A|href|rel|media|type|sizes][]meta[A|http-equiv|name|content|charset][]style[A|type|media|scoped][#]script[A|charset|type|src|defer|async][#]noscript[A][C]body[A][C]section[A][C]nav[A][C]article[A][C]aside[A][C]h1[A][B]h2[A][B]h3[A][B]h4[A][B]h5[A][B]h6[A][B]hgroup[A][h1|h2|h3|h4|h5|h6]header[A][C]footer[A][C]address[A][C]p[A][B]br[A][]pre[A][B]dialog[A][dd|dt]blockquote[A|cite][C]ol[A|start|reversed][li]ul[A][li]li[A|value][C]dl[A][dd|dt]dt[A][B]dd[A][C]a[A|href|target|ping|rel|media|type][C]em[A][B]strong[A][B]small[A][B]cite[A][B]q[A|cite][B]dfn[A][B]abbr[A][B]code[A][B]var[A][B]samp[A][B]kbd[A][B]sub[A][B]sup[A][B]i[A][B]b[A][B]mark[A][B]progress[A|value|max][B]meter[A|value|min|max|low|high|optimum][B]time[A|datetime][B]ruby[A][B|rt|rp]rt[A][B]rp[A][B]bdo[A][B]span[A][B]ins[A|cite|datetime][B]del[A|cite|datetime][B]figure[A][C|legend|figcaption]figcaption[A][C]img[A|alt|src|height|width|usemap|ismap][]iframe[A|name|src|height|width|sandbox|seamless][]embed[A|src|height|width|type][]object[A|data|type|height|width|usemap|name|form|classid][param]param[A|name|value][]details[A|open][C|legend]command[A|type|label|icon|disabled|checked|radiogroup][]menu[A|type|label][C|li]legend[A][C|B]div[A][C]source[A|src|type|media][]audio[A|src|autobuffer|autoplay|loop|controls][source]video[A|src|autobuffer|autoplay|loop|controls|width|height|poster][source]hr[A][]form[A|accept-charset|action|autocomplete|enctype|method|name|novalidate|target][C]fieldset[A|disabled|form|name][C|legend]label[A|form|for][B]input[A|type|accept|alt|autocomplete|checked|disabled|form|formaction|formenctype|formmethod|formnovalidate|formtarget|height|list|max|maxlength|min|multiple|pattern|placeholder|readonly|required|size|src|step|width|files|value][]button[A|autofocus|disabled|form|formaction|formenctype|formmethod|formnovalidate|formtarget|name|value|type][B]select[A|autofocus|disabled|form|multiple|name|size][option|optgroup]datalist[A][B|option]optgroup[A|disabled|label][option]option[A|disabled|selected|label|value][]textarea[A|autofocus|disabled|form|maxlength|name|placeholder|readonly|required|rows|cols|wrap][]keygen[A|autofocus|challenge|disabled|form|keytype|name][]output[A|for|form|name][B]canvas[A|width|height][]map[A|name][B|C]area[A|shape|coords|href|alt|target|media|rel|ping|type][]mathml[A][]svg[A][]table[A|summary][caption|colgroup|thead|tfoot|tbody|tr]caption[A][C]colgroup[A|span][col]col[A|span][]thead[A][tr]tfoot[A][tr]tbody[A][tr]tr[A][th|td]th[A|headers|rowspan|colspan|scope][B]td[A|headers|rowspan|colspan][C]")}return i}function c(){var i=a.html4;if(!i){i=a.html4=h({Z:"H|K|N|O|P",Y:"X|form|R|Q",ZG:"E|span|width|align|char|charoff|valign",X:"p|T|div|U|W|isindex|fieldset|table",ZF:"E|align|char|charoff|valign",W:"pre|hr|blockquote|address|center|noframes",ZE:"abbr|axis|headers|scope|rowspan|colspan|align|char|charoff|valign|nowrap|bgcolor|width|height",ZD:"[E][S]",U:"ul|ol|dl|menu|dir",ZC:"p|Y|div|U|W|table|br|span|bdo|object|applet|img|map|K|N|Q",T:"h1|h2|h3|h4|h5|h6",ZB:"X|S|Q",S:"R|P",ZA:"a|G|J|M|O|P",R:"a|H|K|N|O",Q:"noscript|P",P:"ins|del|script",O:"input|select|textarea|label|button",N:"M|L",M:"em|strong|dfn|code|q|samp|kbd|var|cite|abbr|acronym",L:"sub|sup",K:"J|I",J:"tt|i|b|u|s|strike",I:"big|small|font|basefont",H:"G|F",G:"br|span|bdo",F:"object|applet|img|map|iframe",E:"A|B|C",D:"accesskey|tabindex|onfocus|onblur",C:"onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onkeypress|onkeydown|onkeyup",B:"lang|xml:lang|dir",A:"id|class|style|title"},"script[id|charset|type|language|src|defer|xml:space][]style[B|id|type|media|title|xml:space][]object[E|declare|classid|codebase|data|type|codetype|archive|standby|width|height|usemap|name|tabindex|align|border|hspace|vspace][#|param|Y]param[id|name|value|valuetype|type][]p[E|align][#|S]a[E|D|charset|type|name|href|hreflang|rel|rev|shape|coords|target][#|Z]br[A|clear][]span[E][#|S]bdo[A|C|B][#|S]applet[A|codebase|archive|code|object|alt|name|width|height|align|hspace|vspace][#|param|Y]h1[E|align][#|S]img[E|src|alt|name|longdesc|width|height|usemap|ismap|align|border|hspace|vspace][]map[B|C|A|name][X|form|Q|area]h2[E|align][#|S]iframe[A|longdesc|name|src|frameborder|marginwidth|marginheight|scrolling|align|width|height][#|Y]h3[E|align][#|S]tt[E][#|S]i[E][#|S]b[E][#|S]u[E][#|S]s[E][#|S]strike[E][#|S]big[E][#|S]small[E][#|S]font[A|B|size|color|face][#|S]basefont[id|size|color|face][]em[E][#|S]strong[E][#|S]dfn[E][#|S]code[E][#|S]q[E|cite][#|S]samp[E][#|S]kbd[E][#|S]var[E][#|S]cite[E][#|S]abbr[E][#|S]acronym[E][#|S]sub[E][#|S]sup[E][#|S]input[E|D|type|name|value|checked|disabled|readonly|size|maxlength|src|alt|usemap|onselect|onchange|accept|align][]select[E|name|size|multiple|disabled|tabindex|onfocus|onblur|onchange][optgroup|option]optgroup[E|disabled|label][option]option[E|selected|disabled|label|value][]textarea[E|D|name|rows|cols|disabled|readonly|onselect|onchange][]label[E|for|accesskey|onfocus|onblur][#|S]button[E|D|name|value|type|disabled][#|p|T|div|U|W|table|G|object|applet|img|map|K|N|Q]h4[E|align][#|S]ins[E|cite|datetime][#|Y]h5[E|align][#|S]del[E|cite|datetime][#|Y]h6[E|align][#|S]div[E|align][#|Y]ul[E|type|compact][li]li[E|type|value][#|Y]ol[E|type|compact|start][li]dl[E|compact][dt|dd]dt[E][#|S]dd[E][#|Y]menu[E|compact][li]dir[E|compact][li]pre[E|width|xml:space][#|ZA]hr[E|align|noshade|size|width][]blockquote[E|cite][#|Y]address[E][#|S|p]center[E][#|Y]noframes[E][#|Y]isindex[A|B|prompt][]fieldset[E][#|legend|Y]legend[E|accesskey|align][#|S]table[E|summary|width|border|frame|rules|cellspacing|cellpadding|align|bgcolor][caption|col|colgroup|thead|tfoot|tbody|tr]caption[E|align][#|S]col[ZG][]colgroup[ZG][col]thead[ZF][tr]tr[ZF|bgcolor][th|td]th[E|ZE][#|Y]form[E|action|method|name|enctype|onsubmit|onreset|accept|accept-charset|target][#|X|R|Q]noscript[E][#|Y]td[E|ZE][#|Y]tfoot[ZF][tr]tbody[ZF][tr]area[E|D|shape|coords|href|nohref|alt|target][]base[id|href|target][]body[E|onload|onunload|background|bgcolor|text|link|vlink|alink][#|Y]")}return i}f.html.Schema=function(B){var v=this,s={},k={},j=[],E,z;var o,q,A,r,x,n,p={};function m(G,F,I){var H=B[G];if(!H){H=a[G];if(!H){H=e(F," ",e(F.toUpperCase()," "));H=f.extend(H,I);a[G]=H}}else{H=e(H,",",e(H.toUpperCase()," "))}return H}B=B||{};z=B.schema=="html5"?b():c();if(B.verify_html===false){B.valid_elements="*[*]"}if(B.valid_styles){E={};g(B.valid_styles,function(G,F){E[F]=f.explode(G)})}o=m("whitespace_elements","pre script style textarea");q=m("self_closing_elements","colgroup dd dt li options p td tfoot th thead tr");A=m("short_ended_elements","area base basefont br col frame hr img input isindex link meta param embed source");r=m("boolean_attributes","checked compact declare defer disabled ismap multiple nohref noresize noshade nowrap readonly selected autoplay loop controls");n=m("non_empty_elements","td th iframe video audio object",A);x=m("block_elements","h1 h2 h3 h4 h5 h6 hr p div address pre form table tbody thead tfoot th tr td li ol ul caption blockquote center dl dt dd dir fieldset noscript menu isindex samp header footer article section hgroup aside nav figure");function i(F){return new RegExp("^"+F.replace(/([?+*])/g,".$1")+"$")}function D(M){var L,H,aa,W,ab,G,J,V,Y,R,Z,ad,P,K,X,F,T,I,ac,ae,Q,U,O=/^([#+\-])?([^\[\/]+)(?:\/([^\[]+))?(?:\[([^\]]+)\])?$/,S=/^([!\-])?(\w+::\w+|[^=:<]+)?(?:([=:<])(.*))?$/,N=/[*?+]/;if(M){M=d(M);if(s["@"]){T=s["@"].attributes;I=s["@"].attributesOrder}for(L=0,H=M.length;L<H;L++){G=O.exec(M[L]);if(G){X=G[1];R=G[2];F=G[3];Y=G[4];P={};K=[];J={attributes:P,attributesOrder:K};if(X==="#"){J.paddEmpty=true}if(X==="-"){J.removeEmpty=true}if(T){for(ae in T){P[ae]=T[ae]}K.push.apply(K,I)}if(Y){Y=d(Y,"|");for(aa=0,W=Y.length;aa<W;aa++){G=S.exec(Y[aa]);if(G){V={};ad=G[1];Z=G[2].replace(/::/g,":");X=G[3];U=G[4];if(ad==="!"){J.attributesRequired=J.attributesRequired||[];J.attributesRequired.push(Z);V.required=true}if(ad==="-"){delete P[Z];K.splice(f.inArray(K,Z),1);continue}if(X){if(X==="="){J.attributesDefault=J.attributesDefault||[];J.attributesDefault.push({name:Z,value:U});V.defaultValue=U}if(X===":"){J.attributesForced=J.attributesForced||[];J.attributesForced.push({name:Z,value:U});V.forcedValue=U}if(X==="<"){V.validValues=e(U,"?")}}if(N.test(Z)){J.attributePatterns=J.attributePatterns||[];V.pattern=i(Z);J.attributePatterns.push(V)}else{if(!P[Z]){K.push(Z)}P[Z]=V}}}}if(!T&&R=="@"){T=P;I=K}if(F){J.outputName=R;s[F]=J}if(N.test(R)){J.pattern=i(R);j.push(J)}else{s[R]=J}}}}}function u(F){s={};j=[];D(F);g(z,function(H,G){k[G]=H.children})}function l(G){var F=/^(~)?(.+)$/;if(G){g(d(G),function(K){var I=F.exec(K),J=I[1]==="~",L=J?"span":"div",H=I[2];k[H]=k[L];p[H]=L;if(!J){x[H]={}}g(k,function(M,N){if(M[L]){M[H]=M[L]}})})}}function y(G){var F=/^([+\-]?)(\w+)\[([^\]]+)\]$/;if(G){g(d(G),function(K){var J=F.exec(K),H,I;if(J){I=J[1];if(I){H=k[J[2]]}else{H=k[J[2]]={"#comment":{}}}H=k[J[2]];g(d(J[3],"|"),function(L){if(I==="-"){delete H[L]}else{H[L]={}}})}})}}function C(F){var H=s[F],G;if(H){return H}G=j.length;while(G--){H=j[G];if(H.pattern.test(F)){return H}}}if(!B.valid_elements){g(z,function(G,F){s[F]={attributes:G.attributes,attributesOrder:G.attributesOrder};k[F]=G.children});if(B.schema!="html5"){g(d("strong/b,em/i"),function(F){F=d(F,"/");s[F[1]].outputName=F[0]})}s.img.attributesDefault=[{name:"alt",value:""}];g(d("ol,ul,sub,sup,blockquote,span,font,a,table,tbody,tr,strong,em,b,i"),function(F){if(s[F]){s[F].removeEmpty=true}});g(d("p,h1,h2,h3,h4,h5,h6,th,td,pre,div,address,caption"),function(F){s[F].paddEmpty=true})}else{u(B.valid_elements)}l(B.custom_elements);y(B.valid_children);D(B.extended_valid_elements);y("+ol[ul|ol],+ul[ul|ol]");if(B.invalid_elements){f.each(f.explode(B.invalid_elements),function(F){if(s[F]){delete s[F]}})}if(!C("span")){D("span[!data-mce-type|*]")}v.children=k;v.styles=E;v.getBoolAttrs=function(){return r};v.getBlockElements=function(){return x};v.getShortEndedElements=function(){return A};v.getSelfClosingElements=function(){return q};v.getNonEmptyElements=function(){return n};v.getWhiteSpaceElements=function(){return o};v.isValidChild=function(F,H){var G=k[F];return !!(G&&G[H])};v.getElementRule=C;v.getCustomElements=function(){return p};v.addValidElements=D;v.setValidElements=u;v.addCustomElements=l;v.addValidChildren=y}})(tinymce);(function(a){a.html.SaxParser=function(c,e){var b=this,d=function(){};c=c||{};b.schema=e=e||new a.html.Schema();if(c.fix_self_closing!==false){c.fix_self_closing=true}a.each("comment cdata text start end pi doctype".split(" "),function(f){if(f){b[f]=c[f]||d}});b.parse=function(F){var n=this,g,H=0,J,C,B=[],O,R,D,r,A,s,N,I,P,x,m,k,u,S,o,Q,G,T,M,f,K,l,E,L,h,y=0,j=a.html.Entities.decode,z,q;function v(U){var W,V;W=B.length;while(W--){if(B[W].name===U){break}}if(W>=0){for(V=B.length-1;V>=W;V--){U=B[V];if(U.valid){n.end(U.name)}}B.length=W}}function p(V,U,Z,Y,X){var aa,W;U=U.toLowerCase();Z=U in I?U:j(Z||Y||X||"");if(x&&!A&&U.indexOf("data-")!==0){aa=Q[U];if(!aa&&G){W=G.length;while(W--){aa=G[W];if(aa.pattern.test(U)){break}}if(W===-1){aa=null}}if(!aa){return}if(aa.validValues&&!(Z in aa.validValues)){return}}O.map[U]=Z;O.push({name:U,value:Z})}l=new RegExp("<(?:(?:!--([\\w\\W]*?)-->)|(?:!\\[CDATA\\[([\\w\\W]*?)\\]\\]>)|(?:!DOCTYPE([\\w\\W]*?)>)|(?:\\?([^\\s\\/<>]+) ?([\\w\\W]*?)[?/]>)|(?:\\/([^>]+)>)|(?:([A-Za-z0-9\\-\\:]+)((?:\\s+[^\"'>]+(?:(?:\"[^\"]*\")|(?:'[^']*')|[^>]*))*|\\/|\\s+)>))","g");E=/([\w:\-]+)(?:\s*=\s*(?:(?:\"((?:\\.|[^\"])*)\")|(?:\'((?:\\.|[^\'])*)\')|([^>\s]+)))?/g;L={script:/<\/script[^>]*>/gi,style:/<\/style[^>]*>/gi,noscript:/<\/noscript[^>]*>/gi};N=e.getShortEndedElements();K=e.getSelfClosingElements();I=e.getBoolAttrs();x=c.validate;s=c.remove_internals;z=c.fix_self_closing;q=a.isIE;o=/^:/;while(g=l.exec(F)){if(H<g.index){n.text(j(F.substr(H,g.index-H)))}if(J=g[6]){J=J.toLowerCase();if(q&&o.test(J)){J=J.substr(1)}v(J)}else{if(J=g[7]){J=J.toLowerCase();if(q&&o.test(J)){J=J.substr(1)}P=J in N;if(z&&K[J]&&B.length>0&&B[B.length-1].name===J){v(J)}if(!x||(m=e.getElementRule(J))){k=true;if(x){Q=m.attributes;G=m.attributePatterns}if(S=g[8]){A=S.indexOf("data-mce-type")!==-1;if(A&&s){k=false}O=[];O.map={};S.replace(E,p)}else{O=[];O.map={}}if(x&&!A){T=m.attributesRequired;M=m.attributesDefault;f=m.attributesForced;if(f){R=f.length;while(R--){u=f[R];r=u.name;h=u.value;if(h==="{$uid}"){h="mce_"+y++}O.map[r]=h;O.push({name:r,value:h})}}if(M){R=M.length;while(R--){u=M[R];r=u.name;if(!(r in O.map)){h=u.value;if(h==="{$uid}"){h="mce_"+y++}O.map[r]=h;O.push({name:r,value:h})}}}if(T){R=T.length;while(R--){if(T[R] in O.map){break}}if(R===-1){k=false}}if(O.map["data-mce-bogus"]){k=false}}if(k){n.start(J,O,P)}}else{k=false}if(C=L[J]){C.lastIndex=H=g.index+g[0].length;if(g=C.exec(F)){if(k){D=F.substr(H,g.index-H)}H=g.index+g[0].length}else{D=F.substr(H);H=F.length}if(k&&D.length>0){n.text(D,true)}if(k){n.end(J)}l.lastIndex=H;continue}if(!P){if(!S||S.indexOf("/")!=S.length-1){B.push({name:J,valid:k})}else{if(k){n.end(J)}}}}else{if(J=g[1]){n.comment(J)}else{if(J=g[2]){n.cdata(J)}else{if(J=g[3]){n.doctype(J)}else{if(J=g[4]){n.pi(J,g[5])}}}}}}H=g.index+g[0].length}if(H<F.length){n.text(j(F.substr(H)))}for(R=B.length-1;R>=0;R--){J=B[R];if(J.valid){n.end(J.name)}}}}})(tinymce);(function(d){var c=/^[ \t\r\n]*$/,e={"#text":3,"#comment":8,"#cdata":4,"#pi":7,"#doctype":10,"#document-fragment":11};function a(k,l,j){var i,h,f=j?"lastChild":"firstChild",g=j?"prev":"next";if(k[f]){return k[f]}if(k!==l){i=k[g];if(i){return i}for(h=k.parent;h&&h!==l;h=h.parent){i=h[g];if(i){return i}}}}function b(f,g){this.name=f;this.type=g;if(g===1){this.attributes=[];this.attributes.map={}}}d.extend(b.prototype,{replace:function(g){var f=this;if(g.parent){g.remove()}f.insert(g,f);f.remove();return f},attr:function(h,l){var f=this,g,j,k;if(typeof h!=="string"){for(j in h){f.attr(j,h[j])}return f}if(g=f.attributes){if(l!==k){if(l===null){if(h in g.map){delete g.map[h];j=g.length;while(j--){if(g[j].name===h){g=g.splice(j,1);return f}}}return f}if(h in g.map){j=g.length;while(j--){if(g[j].name===h){g[j].value=l;break}}}else{g.push({name:h,value:l})}g.map[h]=l;return f}else{return g.map[h]}}},clone:function(){var g=this,n=new b(g.name,g.type),h,f,m,j,k;if(m=g.attributes){k=[];k.map={};for(h=0,f=m.length;h<f;h++){j=m[h];if(j.name!=="id"){k[k.length]={name:j.name,value:j.value};k.map[j.name]=j.value}}n.attributes=k}n.value=g.value;n.shortEnded=g.shortEnded;return n},wrap:function(g){var f=this;f.parent.insert(g,f);g.append(f);return f},unwrap:function(){var f=this,h,g;for(h=f.firstChild;h;){g=h.next;f.insert(h,f,true);h=g}f.remove()},remove:function(){var f=this,h=f.parent,g=f.next,i=f.prev;if(h){if(h.firstChild===f){h.firstChild=g;if(g){g.prev=null}}else{i.next=g}if(h.lastChild===f){h.lastChild=i;if(i){i.next=null}}else{g.prev=i}f.parent=f.next=f.prev=null}return f},append:function(h){var f=this,g;if(h.parent){h.remove()}g=f.lastChild;if(g){g.next=h;h.prev=g;f.lastChild=h}else{f.lastChild=f.firstChild=h}h.parent=f;return h},insert:function(h,f,i){var g;if(h.parent){h.remove()}g=f.parent||this;if(i){if(f===g.firstChild){g.firstChild=h}else{f.prev.next=h}h.prev=f.prev;h.next=f;f.prev=h}else{if(f===g.lastChild){g.lastChild=h}else{f.next.prev=h}h.next=f.next;h.prev=f;f.next=h}h.parent=g;return h},getAll:function(g){var f=this,h,i=[];for(h=f.firstChild;h;h=a(h,f)){if(h.name===g){i.push(h)}}return i},empty:function(){var g=this,f,h,j;if(g.firstChild){f=[];for(j=g.firstChild;j;j=a(j,g)){f.push(j)}h=f.length;while(h--){j=f[h];j.parent=j.firstChild=j.lastChild=j.next=j.prev=null}}g.firstChild=g.lastChild=null;return g},isEmpty:function(k){var f=this,j=f.firstChild,h,g;if(j){do{if(j.type===1){if(j.attributes.map["data-mce-bogus"]){continue}if(k[j.name]){return false}h=j.attributes.length;while(h--){g=j.attributes[h].name;if(g==="name"||g.indexOf("data-")===0){return false}}}if(j.type===8){return false}if((j.type===3&&!c.test(j.value))){return false}}while(j=a(j,f))}return true},walk:function(f){return a(this,null,f)}});d.extend(b,{create:function(g,f){var i,h;i=new b(g,e[g]||1);if(f){for(h in f){i.attr(h,f[h])}}return i}});d.html.Node=b})(tinymce);(function(b){var a=b.html.Node;b.html.DomParser=function(g,h){var f=this,e={},d=[],i={},c={};g=g||{};g.validate="validate" in g?g.validate:true;g.root_name=g.root_name||"body";f.schema=h=h||new b.html.Schema();function j(m){var o,p,y,x,A,n,q,l,u,v,k,s,z,r;s=b.makeMap("tr,td,th,tbody,thead,tfoot,table");k=h.getNonEmptyElements();for(o=0;o<m.length;o++){p=m[o];if(!p.parent){continue}x=[p];for(y=p.parent;y&&!h.isValidChild(y.name,p.name)&&!s[y.name];y=y.parent){x.push(y)}if(y&&x.length>1){x.reverse();A=n=f.filterNode(x[0].clone());for(u=0;u<x.length-1;u++){if(h.isValidChild(n.name,x[u].name)){q=f.filterNode(x[u].clone());n.append(q)}else{q=n}for(l=x[u].firstChild;l&&l!=x[u+1];){r=l.next;q.append(l);l=r}n=q}if(!A.isEmpty(k)){y.insert(A,x[0],true);y.insert(p,A)}else{y.insert(p,x[0],true)}y=x[0];if(y.isEmpty(k)||y.firstChild===y.lastChild&&y.firstChild.name==="br"){y.empty().remove()}}else{if(p.parent){if(p.name==="li"){z=p.prev;if(z&&(z.name==="ul"||z.name==="ul")){z.append(p);continue}z=p.next;if(z&&(z.name==="ul"||z.name==="ul")){z.insert(p,z.firstChild,true);continue}p.wrap(f.filterNode(new a("ul",1)));continue}if(h.isValidChild(p.parent.name,"div")&&h.isValidChild("div",p.name)){p.wrap(f.filterNode(new a("div",1)))}else{if(p.name==="style"||p.name==="script"){p.empty().remove()}else{p.unwrap()}}}}}}f.filterNode=function(m){var l,k,n;if(k in e){n=i[k];if(n){n.push(m)}else{i[k]=[m]}}l=d.length;while(l--){k=d[l].name;if(k in m.attributes.map){n=c[k];if(n){n.push(m)}else{c[k]=[m]}}}return m};f.addNodeFilter=function(k,l){b.each(b.explode(k),function(m){var n=e[m];if(!n){e[m]=n=[]}n.push(l)})};f.addAttributeFilter=function(k,l){b.each(b.explode(k),function(m){var n;for(n=0;n<d.length;n++){if(d[n].name===m){d[n].callbacks.push(l);return}}d.push({name:m,callbacks:[l]})})};f.parse=function(x,m){var n,I,B,A,D,C,y,r,F,M,z,o,E,L=[],K,u,k,s,p,v,q;m=m||{};i={};c={};o=b.extend(b.makeMap("script,style,head,html,body,title,meta,param"),h.getBlockElements());v=h.getNonEmptyElements();p=h.children;z=g.validate;q="forced_root_block" in m?m.forced_root_block:g.forced_root_block;s=h.getWhiteSpaceElements();E=/^[ \t\r\n]+/;u=/[ \t\r\n]+$/;k=/[ \t\r\n]+/g;function G(){var N=I.firstChild,l,O;while(N){l=N.next;if(N.type==3||(N.type==1&&N.name!=="p"&&!o[N.name]&&!N.attr("data-mce-type"))){if(!O){O=J(q,1);I.insert(O,N);O.append(N)}else{O.append(N)}}else{O=null}N=l}}function J(l,N){var O=new a(l,N),P;if(l in e){P=i[l];if(P){P.push(O)}else{i[l]=[O]}}return O}function H(O){var P,l,N;for(P=O.prev;P&&P.type===3;){l=P.value.replace(u,"");if(l.length>0){P.value=l;P=P.prev}else{N=P.prev;P.remove();P=N}}}n=new b.html.SaxParser({validate:z,fix_self_closing:!z,cdata:function(l){B.append(J("#cdata",4)).value=l},text:function(O,l){var N;if(!K){O=O.replace(k," ");if(B.lastChild&&o[B.lastChild.name]){O=O.replace(E,"")}}if(O.length!==0){N=J("#text",3);N.raw=!!l;B.append(N).value=O}},comment:function(l){B.append(J("#comment",8)).value=l},pi:function(l,N){B.append(J(l,7)).value=N;H(B)},doctype:function(N){var l;l=B.append(J("#doctype",10));l.value=N;H(B)},start:function(l,V,O){var T,Q,P,N,R,W,U,S;P=z?h.getElementRule(l):{};if(P){T=J(P.outputName||l,1);T.attributes=V;T.shortEnded=O;B.append(T);S=p[B.name];if(S&&p[T.name]&&!S[T.name]){L.push(T)}Q=d.length;while(Q--){R=d[Q].name;if(R in V.map){F=c[R];if(F){F.push(T)}else{c[R]=[T]}}}if(o[l]){H(T)}if(!O){B=T}if(!K&&s[l]){K=true}}},end:function(l){var R,O,Q,N,P;O=z?h.getElementRule(l):{};if(O){if(o[l]){if(!K){for(R=B.firstChild;R&&R.type===3;){Q=R.value.replace(E,"");if(Q.length>0){R.value=Q;R=R.next}else{N=R.next;R.remove();R=N}}for(R=B.lastChild;R&&R.type===3;){Q=R.value.replace(u,"");if(Q.length>0){R.value=Q;R=R.prev}else{N=R.prev;R.remove();R=N}}}R=B.prev;if(R&&R.type===3){Q=R.value.replace(E,"");if(Q.length>0){R.value=Q}else{R.remove()}}}if(K&&s[l]){K=false}if(O.removeEmpty||O.paddEmpty){if(B.isEmpty(v)){if(O.paddEmpty){B.empty().append(new a("#text","3")).value="\u00a0"}else{if(!B.attributes.map.name){P=B.parent;B.empty().remove();B=P;return}}}}B=B.parent}}},h);I=B=new a(m.context||g.root_name,11);n.parse(x);if(z&&L.length){if(!m.context){j(L)}else{m.invalid=true}}if(q&&I.name=="body"){G()}if(!m.invalid){for(M in i){F=e[M];A=i[M];y=A.length;while(y--){if(!A[y].parent){A.splice(y,1)}}for(D=0,C=F.length;D<C;D++){F[D](A,M,m)}}for(D=0,C=d.length;D<C;D++){F=d[D];if(F.name in c){A=c[F.name];y=A.length;while(y--){if(!A[y].parent){A.splice(y,1)}}for(y=0,r=F.callbacks.length;y<r;y++){F.callbacks[y](A,F.name,m)}}}}return I};if(g.remove_trailing_brs){f.addNodeFilter("br",function(n,m){var r,q=n.length,o,x=b.extend({},h.getBlockElements()),k=h.getNonEmptyElements(),u,s,p,v;x.body=1;for(r=0;r<q;r++){o=n[r];u=o.parent;if(x[o.parent.name]&&o===u.lastChild){p=o.prev;while(p){v=p.name;if(v!=="span"||p.attr("data-mce-type")!=="bookmark"){if(v!=="br"){break}if(v==="br"){o=null;break}}p=p.prev}if(o){o.remove();if(u.isEmpty(k)){elementRule=h.getElementRule(u.name);if(elementRule){if(elementRule.removeEmpty){u.remove()}else{if(elementRule.paddEmpty){u.empty().append(new b.html.Node("#text",3)).value="\u00a0"}}}}}}else{s=o;while(u.firstChild===s&&u.lastChild===s){s=u;if(x[u.name]){break}u=u.parent}if(s===u){textNode=new b.html.Node("#text",3);textNode.value="\u00a0";o.replace(textNode)}}}})}if(!g.allow_html_in_named_anchor){f.addAttributeFilter("name",function(k,l){var n=k.length,p,m,o,q;while(n--){q=k[n];if(q.name==="a"&&q.firstChild){o=q.parent;p=q.lastChild;do{m=p.prev;o.insert(p,q);p=m}while(p)}}})}}})(tinymce);tinymce.html.Writer=function(e){var c=[],a,b,d,f,g;e=e||{};a=e.indent;b=tinymce.makeMap(e.indent_before||"");d=tinymce.makeMap(e.indent_after||"");f=tinymce.html.Entities.getEncodeFunc(e.entity_encoding||"raw",e.entities);g=e.element_format=="html";return{start:function(m,k,p){var n,j,h,o;if(a&&b[m]&&c.length>0){o=c[c.length-1];if(o.length>0&&o!=="\n"){c.push("\n")}}c.push("<",m);if(k){for(n=0,j=k.length;n<j;n++){h=k[n];c.push(" ",h.name,'="',f(h.value,true),'"')}}if(!p||g){c[c.length]=">"}else{c[c.length]=" />"}if(p&&a&&d[m]&&c.length>0){o=c[c.length-1];if(o.length>0&&o!=="\n"){c.push("\n")}}},end:function(h){var i;c.push("</",h,">");if(a&&d[h]&&c.length>0){i=c[c.length-1];if(i.length>0&&i!=="\n"){c.push("\n")}}},text:function(i,h){if(i.length>0){c[c.length]=h?i:f(i)}},cdata:function(h){c.push("<![CDATA[",h,"]]>")},comment:function(h){c.push("<!--",h,"-->")},pi:function(h,i){if(i){c.push("<?",h," ",i,"?>")}else{c.push("<?",h,"?>")}if(a){c.push("\n")}},doctype:function(h){c.push("<!DOCTYPE",h,">",a?"\n":"")},reset:function(){c.length=0},getContent:function(){return c.join("").replace(/\n$/,"")}}};(function(a){a.html.Serializer=function(c,d){var b=this,e=new a.html.Writer(c);c=c||{};c.validate="validate" in c?c.validate:true;b.schema=d=d||new a.html.Schema();b.writer=e;b.serialize=function(h){var g,i;i=c.validate;g={3:function(k,j){e.text(k.value,k.raw)},8:function(j){e.comment(j.value)},7:function(j){e.pi(j.name,j.value)},10:function(j){e.doctype(j.value)},4:function(j){e.cdata(j.value)},11:function(j){if((j=j.firstChild)){do{f(j)}while(j=j.next)}}};e.reset();function f(k){var u=g[k.type],j,o,s,r,p,v,n,m,q;if(!u){j=k.name;o=k.shortEnded;s=k.attributes;if(i&&s&&s.length>1){v=[];v.map={};q=d.getElementRule(k.name);for(n=0,m=q.attributesOrder.length;n<m;n++){r=q.attributesOrder[n];if(r in s.map){p=s.map[r];v.map[r]=p;v.push({name:r,value:p})}}for(n=0,m=s.length;n<m;n++){r=s[n].name;if(!(r in v.map)){p=s.map[r];v.map[r]=p;v.push({name:r,value:p})}}s=v}e.start(k.name,s,o);if(!o){if((k=k.firstChild)){do{f(k)}while(k=k.next)}e.end(j)}}else{u(k)}}if(h.type==1&&!c.inner){f(h)}else{g[11](h)}return e.getContent()}}})(tinymce);tinymce.dom={};(function(b,h){var g=!!document.addEventListener;function c(k,j,l,i){if(k.addEventListener){k.addEventListener(j,l,i||false)}else{if(k.attachEvent){k.attachEvent("on"+j,l)}}}function e(k,j,l,i){if(k.removeEventListener){k.removeEventListener(j,l,i||false)}else{if(k.detachEvent){k.detachEvent("on"+j,l)}}}function a(n,l){var i,k=l||{};function j(){return false}function m(){return true}for(i in n){if(i!=="layerX"&&i!=="layerY"){k[i]=n[i]}}if(!k.target){k.target=k.srcElement||document}k.preventDefault=function(){k.isDefaultPrevented=m;if(n){if(n.preventDefault){n.preventDefault()}else{n.returnValue=false}}};k.stopPropagation=function(){k.isPropagationStopped=m;if(n){if(n.stopPropagation){n.stopPropagation()}else{n.cancelBubble=true}}};k.stopImmediatePropagation=function(){k.isImmediatePropagationStopped=m;k.stopPropagation()};if(!k.isDefaultPrevented){k.isDefaultPrevented=j;k.isPropagationStopped=j;k.isImmediatePropagationStopped=j}return k}function d(m,n,l){var k=m.document,j={type:"ready"};function i(){if(!l.domLoaded){l.domLoaded=true;n(j)}}if(g){c(m,"DOMContentLoaded",i)}else{c(k,"readystatechange",function(){if(k.readyState==="complete"){e(k,"readystatechange",arguments.callee);i()}});if(k.documentElement.doScroll&&m===m.top){(function(){try{k.documentElement.doScroll("left")}catch(o){setTimeout(arguments.callee,0);return}i()})()}}c(m,"load",i)}function f(k){var q=this,p={},i,o,n,m,l;m="onmouseenter" in document.documentElement;n="onfocusin" in document.documentElement;l={mouseenter:"mouseover",mouseleave:"mouseout"};i=1;q.domLoaded=false;q.events=p;function j(u,y){var s,v,r,x;s=p[y][u.type];if(s){for(v=0,r=s.length;v<r;v++){x=s[v];if(x&&x.func.call(x.scope,u)===false){u.preventDefault()}if(u.isImmediatePropagationStopped()){return}}}}q.bind=function(y,B,E,F){var s,u,v,r,C,A,D,x=window;function z(G){j(a(G||x.event),s)}if(!y||y.nodeType===3||y.nodeType===8){return}if(!y[h]){s=i++;y[h]=s;p[s]={}}else{s=y[h];if(!p[s]){p[s]={}}}F=F||y;B=B.split(" ");v=B.length;while(v--){r=B[v];A=z;C=D=false;if(r==="DOMContentLoaded"){r="ready"}if((q.domLoaded||y.readyState=="complete")&&r==="ready"){q.domLoaded=true;E.call(F,a({type:r}));continue}if(!m){C=l[r];if(C){A=function(G){var I,H;I=G.currentTarget;H=G.relatedTarget;if(H&&I.contains){H=I.contains(H)}else{while(H&&H!==I){H=H.parentNode}}if(!H){G=a(G||x.event);G.type=G.type==="mouseout"?"mouseleave":"mouseenter";G.target=I;j(G,s)}}}}if(!n&&(r==="focusin"||r==="focusout")){D=true;C=r==="focusin"?"focus":"blur";A=function(G){G=a(G||x.event);G.type=G.type==="focus"?"focusin":"focusout";j(G,s)}}u=p[s][r];if(!u){p[s][r]=u=[{func:E,scope:F}];u.fakeName=C;u.capture=D;u.nativeHandler=A;if(!g){u.proxyHandler=k(s)}if(r==="ready"){d(y,A,q)}else{c(y,C||r,g?A:u.proxyHandler,D)}}else{u.push({func:E,scope:F})}}y=u=0;return E};q.unbind=function(y,A,B){var s,v,x,C,r,u;if(!y||y.nodeType===3||y.nodeType===8){return q}s=y[h];if(s){u=p[s];if(A){A=A.split(" ");x=A.length;while(x--){r=A[x];v=u[r];if(v){if(B){C=v.length;while(C--){if(v[C].func===B){v.splice(C,1)}}}if(!B||v.length===0){delete u[r];e(y,v.fakeName||r,g?v.nativeHandler:v.proxyHandler,v.capture)}}}}else{for(r in u){v=u[r];e(y,v.fakeName||r,g?v.nativeHandler:v.proxyHandler,v.capture)}u={}}for(r in u){return q}delete p[s];try{delete y[h]}catch(z){y[h]=null}}return q};q.fire=function(v,s,r){var x,u;if(!v||v.nodeType===3||v.nodeType===8){return q}u=a(null,r);u.type=s;do{x=v[h];if(x){j(u,x)}v=v.parentNode||v.ownerDocument||v.defaultView||v.parentWindow}while(v&&!u.isPropagationStopped());return q};q.clean=function(v){var s,r,u=q.unbind;if(!v||v.nodeType===3||v.nodeType===8){return q}if(v[h]){u(v)}if(!v.getElementsByTagName){v=v.document}if(v&&v.getElementsByTagName){u(v);r=v.getElementsByTagName("*");s=r.length;while(s--){v=r[s];if(v[h]){u(v)}}}return q};q.callNativeHandler=function(s,r){if(p){p[s][r.type].nativeHandler(r)}};q.destory=function(){p={}};q.add=function(x,s,v,u){if(typeof(x)==="string"){x=document.getElementById(x)}if(x&&x instanceof Array){var r=x;while(r--){q.add(x[r],s,v,u)}return}if(s==="init"){s="ready"}return q.bind(x,s instanceof Array?s.join(" "):s,v,u)};q.remove=function(v,s,u){if(typeof(v)==="string"){v=document.getElementById(v)}if(v instanceof Array){var r=v;while(r--){q.remove(v[r],s,u,scope)}return q}return q.unbind(v,s instanceof Array?s.join(" "):s,u)};q.clear=function(r){if(typeof(r)==="string"){r=document.getElementById(r)}return q.clean(r)};q.cancel=function(r){if(r){q.prevent(r);q.stop(r)}return false};q.prevent=function(r){r.preventDefault();return false};q.stop=function(r){r.stopPropagation();return false}}b.EventUtils=f;b.Event=new f(function(i){return function(j){tinymce.dom.Event.callNativeHandler(i,j)}});b.Event.bind(window,"ready",function(){});b=0})(tinymce.dom,"data-mce-expando");tinymce.dom.TreeWalker=function(a,c){var b=a;function d(i,f,e,j){var h,g;if(i){if(!j&&i[f]){return i[f]}if(i!=c){h=i[e];if(h){return h}for(g=i.parentNode;g&&g!=c;g=g.parentNode){h=g[e];if(h){return h}}}}}this.current=function(){return b};this.next=function(e){return(b=d(b,"firstChild","nextSibling",e))};this.prev=function(e){return(b=d(b,"lastChild","previousSibling",e))}};(function(e){var g=e.each,d=e.is,f=e.isWebKit,b=e.isIE,h=e.html.Entities,c=/^([a-z0-9],?)+$/i,a=/^[ \t\r\n]*$/;e.create("tinymce.dom.DOMUtils",{doc:null,root:null,files:null,pixelStyles:/^(top|left|bottom|right|width|height|borderWidth)$/,props:{"for":"htmlFor","class":"className",className:"className",checked:"checked",disabled:"disabled",maxlength:"maxLength",readonly:"readOnly",selected:"selected",value:"value",id:"id",name:"name",type:"type"},DOMUtils:function(o,l){var k=this,i,j,n;k.doc=o;k.win=window;k.files={};k.cssFlicker=false;k.counter=0;k.stdMode=!e.isIE||o.documentMode>=8;k.boxModel=!e.isIE||o.compatMode=="CSS1Compat"||k.stdMode;k.hasOuterHTML="outerHTML" in o.createElement("a");k.settings=l=e.extend({keep_values:false,hex_colors:1},l);k.schema=l.schema;k.styles=new e.html.Styles({url_converter:l.url_converter,url_converter_scope:l.url_converter_scope},l.schema);if(e.isIE6){try{o.execCommand("BackgroundImageCache",false,true)}catch(m){k.cssFlicker=true}}k.fixDoc(o);k.events=l.ownEvents?new e.dom.EventUtils(l.proxy):e.dom.Event;e.addUnload(k.destroy,k);n=l.schema?l.schema.getBlockElements():{};k.isBlock=function(q){var p=q.nodeType;if(p){return !!(p===1&&n[q.nodeName])}return !!n[q]}},fixDoc:function(k){var j=this.settings,i;if(b&&j.schema){("abbr article aside audio canvas details figcaption figure footer header hgroup mark menu meter nav output progress section summary time video").replace(/\w+/g,function(l){k.createElement(l)});for(i in j.schema.getCustomElements()){k.createElement(i)}}},clone:function(k,i){var j=this,m,l;if(!b||k.nodeType!==1||i){return k.cloneNode(i)}l=j.doc;if(!i){m=l.createElement(k.nodeName);g(j.getAttribs(k),function(n){j.setAttrib(m,n.nodeName,j.getAttrib(k,n.nodeName))});return m}return m.firstChild},getRoot:function(){var i=this,j=i.settings;return(j&&i.get(j.root_element))||i.doc.body},getViewPort:function(j){var k,i;j=!j?this.win:j;k=j.document;i=this.boxModel?k.documentElement:k.body;return{x:j.pageXOffset||i.scrollLeft,y:j.pageYOffset||i.scrollTop,w:j.innerWidth||i.clientWidth,h:j.innerHeight||i.clientHeight}},getRect:function(l){var k,i=this,j;l=i.get(l);k=i.getPos(l);j=i.getSize(l);return{x:k.x,y:k.y,w:j.w,h:j.h}},getSize:function(l){var j=this,i,k;l=j.get(l);i=j.getStyle(l,"width");k=j.getStyle(l,"height");if(i.indexOf("px")===-1){i=0}if(k.indexOf("px")===-1){k=0}return{w:parseInt(i,10)||l.offsetWidth||l.clientWidth,h:parseInt(k,10)||l.offsetHeight||l.clientHeight}},getParent:function(k,j,i){return this.getParents(k,j,i,false)},getParents:function(s,m,k,q){var j=this,i,l=j.settings,p=[];s=j.get(s);q=q===undefined;if(l.strict_root){k=k||j.getRoot()}if(d(m,"string")){i=m;if(m==="*"){m=function(o){return o.nodeType==1}}else{m=function(o){return j.is(o,i)}}}while(s){if(s==k||!s.nodeType||s.nodeType===9){break}if(!m||m(s)){if(q){p.push(s)}else{return s}}s=s.parentNode}return q?p:null},get:function(i){var j;if(i&&this.doc&&typeof(i)=="string"){j=i;i=this.doc.getElementById(i);if(i&&i.id!==j){return this.doc.getElementsByName(j)[1]}}return i},getNext:function(j,i){return this._findSib(j,i,"nextSibling")},getPrev:function(j,i){return this._findSib(j,i,"previousSibling")},select:function(k,j){var i=this;return e.dom.Sizzle(k,i.get(j)||i.get(i.settings.root_element)||i.doc,[])},is:function(l,j){var k;if(l.length===undefined){if(j==="*"){return l.nodeType==1}if(c.test(j)){j=j.toLowerCase().split(/,/);l=l.nodeName.toLowerCase();for(k=j.length-1;k>=0;k--){if(j[k]==l){return true}}return false}}return e.dom.Sizzle.matches(j,l.nodeType?[l]:l).length>0},add:function(l,o,i,k,m){var j=this;return this.run(l,function(r){var q,n;q=d(o,"string")?j.doc.createElement(o):o;j.setAttribs(q,i);if(k){if(k.nodeType){q.appendChild(k)}else{j.setHTML(q,k)}}return !m?r.appendChild(q):q})},create:function(k,i,j){return this.add(this.doc.createElement(k),k,i,j,1)},createHTML:function(q,i,m){var p="",l=this,j;p+="<"+q;for(j in i){if(i.hasOwnProperty(j)){p+=" "+j+'="'+l.encode(i[j])+'"'}}if(typeof(m)!="undefined"){return p+">"+m+"</"+q+">"}return p+" />"},remove:function(i,j){return this.run(i,function(l){var m,k=l.parentNode;if(!k){return null}if(j){while(m=l.firstChild){if(!e.isIE||m.nodeType!==3||m.nodeValue){k.insertBefore(m,l)}else{l.removeChild(m)}}}return k.removeChild(l)})},setStyle:function(l,i,j){var k=this;return k.run(l,function(o){var n,m;n=o.style;i=i.replace(/-(\D)/g,function(q,p){return p.toUpperCase()});if(k.pixelStyles.test(i)&&(e.is(j,"number")||/^[\-0-9\.]+$/.test(j))){j+="px"}switch(i){case"opacity":if(b){n.filter=j===""?"":"alpha(opacity="+(j*100)+")";if(!l.currentStyle||!l.currentStyle.hasLayout){n.display="inline-block"}}n[i]=n["-moz-opacity"]=n["-khtml-opacity"]=j||"";break;case"float":b?n.styleFloat=j:n.cssFloat=j;break;default:n[i]=j||""}if(k.settings.update_styles){k.setAttrib(o,"data-mce-style")}})},getStyle:function(l,i,k){l=this.get(l);if(!l){return}if(this.doc.defaultView&&k){i=i.replace(/[A-Z]/g,function(m){return"-"+m});try{return this.doc.defaultView.getComputedStyle(l,null).getPropertyValue(i)}catch(j){return null}}i=i.replace(/-(\D)/g,function(n,m){return m.toUpperCase()});if(i=="float"){i=b?"styleFloat":"cssFloat"}if(l.currentStyle&&k){return l.currentStyle[i]}return l.style?l.style[i]:undefined},setStyles:function(l,m){var j=this,k=j.settings,i;i=k.update_styles;k.update_styles=0;g(m,function(o,p){j.setStyle(l,p,o)});k.update_styles=i;if(k.update_styles){j.setAttrib(l,k.cssText)}},removeAllAttribs:function(i){return this.run(i,function(l){var k,j=l.attributes;for(k=j.length-1;k>=0;k--){l.removeAttributeNode(j.item(k))}})},setAttrib:function(k,l,i){var j=this;if(!k||!l){return}if(j.settings.strict){l=l.toLowerCase()}return this.run(k,function(p){var o=j.settings;var m=p.getAttribute(l);if(i!==null){switch(l){case"style":if(!d(i,"string")){g(i,function(q,r){j.setStyle(p,r,q)});return}if(o.keep_values){if(i&&!j._isRes(i)){p.setAttribute("data-mce-style",i,2)}else{p.removeAttribute("data-mce-style",2)}}p.style.cssText=i;break;case"class":p.className=i||"";break;case"src":case"href":if(o.keep_values){if(o.url_converter){i=o.url_converter.call(o.url_converter_scope||j,i,l,p)}j.setAttrib(p,"data-mce-"+l,i,2)}break;case"shape":p.setAttribute("data-mce-style",i);break}}if(d(i)&&i!==null&&i.length!==0){p.setAttribute(l,""+i,2)}else{p.removeAttribute(l,2)}if(tinyMCE.activeEditor&&m!=i){var n=tinyMCE.activeEditor;n.onSetAttrib.dispatch(n,p,l,i)}})},setAttribs:function(j,k){var i=this;return this.run(j,function(l){g(k,function(m,o){i.setAttrib(l,o,m)})})},getAttrib:function(m,o,k){var i,j=this,l;m=j.get(m);if(!m||m.nodeType!==1){return k===l?false:k}if(!d(k)){k=""}if(/^(src|href|style|coords|shape)$/.test(o)){i=m.getAttribute("data-mce-"+o);if(i){return i}}if(b&&j.props[o]){i=m[j.props[o]];i=i&&i.nodeValue?i.nodeValue:i}if(!i){i=m.getAttribute(o,2)}if(/^(checked|compact|declare|defer|disabled|ismap|multiple|nohref|noshade|nowrap|readonly|selected)$/.test(o)){if(m[j.props[o]]===true&&i===""){return o}return i?o:""}if(m.nodeName==="FORM"&&m.getAttributeNode(o)){return m.getAttributeNode(o).nodeValue}if(o==="style"){i=i||m.style.cssText;if(i){i=j.serializeStyle(j.parseStyle(i),m.nodeName);if(j.settings.keep_values&&!j._isRes(i)){m.setAttribute("data-mce-style",i)}}}if(f&&o==="class"&&i){i=i.replace(/(apple|webkit)\-[a-z\-]+/gi,"")}if(b){switch(o){case"rowspan":case"colspan":if(i===1){i=""}break;case"size":if(i==="+0"||i===20||i===0){i=""}break;case"width":case"height":case"vspace":case"checked":case"disabled":case"readonly":if(i===0){i=""}break;case"hspace":if(i===-1){i=""}break;case"maxlength":case"tabindex":if(i===32768||i===2147483647||i==="32768"){i=""}break;case"multiple":case"compact":case"noshade":case"nowrap":if(i===65535){return o}return k;case"shape":i=i.toLowerCase();break;default:if(o.indexOf("on")===0&&i){i=e._replace(/^function\s+\w+\(\)\s+\{\s+(.*)\s+\}$/,"$1",""+i)}}}return(i!==l&&i!==null&&i!=="")?""+i:k},getPos:function(q,l){var j=this,i=0,p=0,m,o=j.doc,k;q=j.get(q);l=l||o.body;if(q){if(q.getBoundingClientRect){q=q.getBoundingClientRect();m=j.boxModel?o.documentElement:o.body;i=q.left+(o.documentElement.scrollLeft||o.body.scrollLeft)-m.clientTop;p=q.top+(o.documentElement.scrollTop||o.body.scrollTop)-m.clientLeft;return{x:i,y:p}}k=q;while(k&&k!=l&&k.nodeType){i+=k.offsetLeft||0;p+=k.offsetTop||0;k=k.offsetParent}k=q.parentNode;while(k&&k!=l&&k.nodeType){i-=k.scrollLeft||0;p-=k.scrollTop||0;k=k.parentNode}}return{x:i,y:p}},parseStyle:function(i){return this.styles.parse(i)},serializeStyle:function(j,i){return this.styles.serialize(j,i)},loadCSS:function(i){var k=this,l=k.doc,j;if(!i){i=""}j=l.getElementsByTagName("head")[0];g(i.split(","),function(m){var n;if(k.files[m]){return}k.files[m]=true;n=k.create("link",{rel:"stylesheet",href:e._addVer(m)});if(b&&l.documentMode&&l.recalc){n.onload=function(){if(l.recalc){l.recalc()}n.onload=null}}j.appendChild(n)})},addClass:function(i,j){return this.run(i,function(k){var l;if(!j){return 0}if(this.hasClass(k,j)){return k.className}l=this.removeClass(k,j);return k.className=(l!=""?(l+" "):"")+j})},removeClass:function(k,l){var i=this,j;return i.run(k,function(n){var m;if(i.hasClass(n,l)){if(!j){j=new RegExp("(^|\\s+)"+l+"(\\s+|$)","g")}m=n.className.replace(j," ");m=e.trim(m!=" "?m:"");n.className=m;if(!m){n.removeAttribute("class");n.removeAttribute("className")}return m}return n.className})},hasClass:function(j,i){j=this.get(j);if(!j||!i){return false}return(" "+j.className+" ").indexOf(" "+i+" ")!==-1},show:function(i){return this.setStyle(i,"display","block")},hide:function(i){return this.setStyle(i,"display","none")},isHidden:function(i){i=this.get(i);return !i||i.style.display=="none"||this.getStyle(i,"display")=="none"},uniqueId:function(i){return(!i?"mce_":i)+(this.counter++)},setHTML:function(k,j){var i=this;return i.run(k,function(m){if(b){while(m.firstChild){m.removeChild(m.firstChild)}try{m.innerHTML="<br />"+j;m.removeChild(m.firstChild)}catch(l){m=i.create("div");m.innerHTML="<br />"+j;g(m.childNodes,function(o,n){if(n){m.appendChild(o)}})}}else{m.innerHTML=j}return j})},getOuterHTML:function(k){var j,i=this;k=i.get(k);if(!k){return null}if(k.nodeType===1&&i.hasOuterHTML){return k.outerHTML}j=(k.ownerDocument||i.doc).createElement("body");j.appendChild(k.cloneNode(true));return j.innerHTML},setOuterHTML:function(l,j,m){var i=this;function k(p,o,r){var s,q;q=r.createElement("body");q.innerHTML=o;s=q.lastChild;while(s){i.insertAfter(s.cloneNode(true),p);s=s.previousSibling}i.remove(p)}return this.run(l,function(o){o=i.get(o);if(o.nodeType==1){m=m||o.ownerDocument||i.doc;if(b){try{if(b&&o.nodeType==1){o.outerHTML=j}else{k(o,j,m)}}catch(n){k(o,j,m)}}else{k(o,j,m)}}})},decode:h.decode,encode:h.encodeAllRaw,insertAfter:function(i,j){j=this.get(j);return this.run(i,function(l){var k,m;k=j.parentNode;m=j.nextSibling;if(m){k.insertBefore(l,m)}else{k.appendChild(l)}return l})},replace:function(m,l,i){var j=this;if(d(l,"array")){m=m.cloneNode(true)}return j.run(l,function(k){if(i){g(e.grep(k.childNodes),function(n){m.appendChild(n)})}return k.parentNode.replaceChild(m,k)})},rename:function(l,i){var k=this,j;if(l.nodeName!=i.toUpperCase()){j=k.create(i);g(k.getAttribs(l),function(m){k.setAttrib(j,m.nodeName,k.getAttrib(l,m.nodeName))});k.replace(j,l,1)}return j||l},findCommonAncestor:function(k,i){var l=k,j;while(l){j=i;while(j&&l!=j){j=j.parentNode}if(l==j){break}l=l.parentNode}if(!l&&k.ownerDocument){return k.ownerDocument.documentElement}return l},toHex:function(i){var k=/^\s*rgb\s*?\(\s*?([0-9]+)\s*?,\s*?([0-9]+)\s*?,\s*?([0-9]+)\s*?\)\s*$/i.exec(i);function j(l){l=parseInt(l,10).toString(16);return l.length>1?l:"0"+l}if(k){i="#"+j(k[1])+j(k[2])+j(k[3]);return i}return i},getClasses:function(){var n=this,j=[],m,o={},p=n.settings.class_filter,l;if(n.classes){return n.classes}function q(i){g(i.imports,function(s){q(s)});g(i.cssRules||i.rules,function(s){switch(s.type||1){case 1:if(s.selectorText){g(s.selectorText.split(","),function(r){r=r.replace(/^\s*|\s*$|^\s\./g,"");if(/\.mce/.test(r)||!/\.[\w\-]+$/.test(r)){return}l=r;r=e._replace(/.*\.([a-z0-9_\-]+).*/i,"$1",r);if(p&&!(r=p(r,l))){return}if(!o[r]){j.push({"class":r});o[r]=1}})}break;case 3:q(s.styleSheet);break}})}try{g(n.doc.styleSheets,q)}catch(k){}if(j.length>0){n.classes=j}return j},run:function(l,k,j){var i=this,m;if(i.doc&&typeof(l)==="string"){l=i.get(l)}if(!l){return false}j=j||this;if(!l.nodeType&&(l.length||l.length===0)){m=[];g(l,function(o,n){if(o){if(typeof(o)=="string"){o=i.doc.getElementById(o)}m.push(k.call(j,o,n))}});return m}return k.call(j,l)},getAttribs:function(j){var i;j=this.get(j);if(!j){return[]}if(b){i=[];if(j.nodeName=="OBJECT"){return j.attributes}if(j.nodeName==="OPTION"&&this.getAttrib(j,"selected")){i.push({specified:1,nodeName:"selected"})}j.cloneNode(false).outerHTML.replace(/<\/?[\w:\-]+ ?|=[\"][^\"]+\"|=\'[^\']+\'|=[\w\-]+|>/gi,"").replace(/[\w:\-]+/gi,function(k){i.push({specified:1,nodeName:k})});return i}return j.attributes},isEmpty:function(m,k){var r=this,o,n,q,j,l,p=0;m=m.firstChild;if(m){j=new e.dom.TreeWalker(m,m.parentNode);k=k||r.schema?r.schema.getNonEmptyElements():null;do{q=m.nodeType;if(q===1){if(m.getAttribute("data-mce-bogus")){continue}l=m.nodeName.toLowerCase();if(k&&k[l]){if(l==="br"){p++;continue}return false}n=r.getAttribs(m);o=m.attributes.length;while(o--){l=m.attributes[o].nodeName;if(l==="name"||l==="data-mce-bookmark"){return false}}}if(q==8){return false}if((q===3&&!a.test(m.nodeValue))){return false}}while(m=j.next())}return p<=1},destroy:function(j){var i=this;i.win=i.doc=i.root=i.events=i.frag=null;if(!j){e.removeUnload(i.destroy)}},createRng:function(){var i=this.doc;return i.createRange?i.createRange():new e.dom.Range(this)},nodeIndex:function(m,n){var i=0,k,l,j;if(m){for(k=m.nodeType,m=m.previousSibling,l=m;m;m=m.previousSibling){j=m.nodeType;if(n&&j==3){if(j==k||!m.nodeValue.length){continue}}i++;k=j}}return i},split:function(m,l,p){var q=this,i=q.createRng(),n,k,o;function j(x){var u,s=x.childNodes,v=x.nodeType;function y(B){var A=B.previousSibling&&B.previousSibling.nodeName=="SPAN";var z=B.nextSibling&&B.nextSibling.nodeName=="SPAN";return A&&z}if(v==1&&x.getAttribute("data-mce-type")=="bookmark"){return}for(u=s.length-1;u>=0;u--){j(s[u])}if(v!=9){if(v==3&&x.nodeValue.length>0){var r=e.trim(x.nodeValue).length;if(!q.isBlock(x.parentNode)||r>0||r===0&&y(x)){return}}else{if(v==1){s=x.childNodes;if(s.length==1&&s[0]&&s[0].nodeType==1&&s[0].getAttribute("data-mce-type")=="bookmark"){x.parentNode.insertBefore(s[0],x)}if(s.length||/^(br|hr|input|img)$/i.test(x.nodeName)){return}}}q.remove(x)}return x}if(m&&l){i.setStart(m.parentNode,q.nodeIndex(m));i.setEnd(l.parentNode,q.nodeIndex(l));n=i.extractContents();i=q.createRng();i.setStart(l.parentNode,q.nodeIndex(l)+1);i.setEnd(m.parentNode,q.nodeIndex(m)+1);k=i.extractContents();o=m.parentNode;o.insertBefore(j(n),m);if(p){o.replaceChild(p,l)}else{o.insertBefore(l,m)}o.insertBefore(j(k),m);q.remove(m);return p||l}},bind:function(l,i,k,j){return this.events.add(l,i,k,j||this)},unbind:function(k,i,j){return this.events.remove(k,i,j)},fire:function(k,j,i){return this.events.fire(k,j,i)},getContentEditable:function(j){var i;if(j.nodeType!=1){return null}i=j.getAttribute("data-mce-contenteditable");if(i&&i!=="inherit"){return i}return j.contentEditable!=="inherit"?j.contentEditable:null},_findSib:function(l,i,j){var k=this,m=i;if(l){if(d(m,"string")){m=function(n){return k.is(n,i)}}for(l=l[j];l;l=l[j]){if(m(l)){return l}}}return null},_isRes:function(i){return/^(top|left|bottom|right|width|height)/i.test(i)||/;\s*(top|left|bottom|right|width|height)/i.test(i)}});e.DOM=new e.dom.DOMUtils(document,{process_html:0})})(tinymce);(function(a){function b(c){var O=this,e=c.doc,T=0,F=1,j=2,E=true,S=false,V="startOffset",h="startContainer",Q="endContainer",A="endOffset",k=tinymce.extend,n=c.nodeIndex;k(O,{startContainer:e,startOffset:0,endContainer:e,endOffset:0,collapsed:E,commonAncestorContainer:e,START_TO_START:0,START_TO_END:1,END_TO_END:2,END_TO_START:3,setStart:q,setEnd:s,setStartBefore:g,setStartAfter:J,setEndBefore:K,setEndAfter:u,collapse:B,selectNode:y,selectNodeContents:G,compareBoundaryPoints:v,deleteContents:p,extractContents:I,cloneContents:d,insertNode:D,surroundContents:N,cloneRange:L});function x(){return e.createDocumentFragment()}function q(X,W){C(E,X,W)}function s(X,W){C(S,X,W)}function g(W){q(W.parentNode,n(W))}function J(W){q(W.parentNode,n(W)+1)}function K(W){s(W.parentNode,n(W))}function u(W){s(W.parentNode,n(W)+1)}function B(W){if(W){O[Q]=O[h];O[A]=O[V]}else{O[h]=O[Q];O[V]=O[A]}O.collapsed=E}function y(W){g(W);u(W)}function G(W){q(W,0);s(W,W.nodeType===1?W.childNodes.length:W.nodeValue.length)}function v(aa,W){var ad=O[h],Y=O[V],ac=O[Q],X=O[A],ab=W.startContainer,af=W.startOffset,Z=W.endContainer,ae=W.endOffset;if(aa===0){return H(ad,Y,ab,af)}if(aa===1){return H(ac,X,ab,af)}if(aa===2){return H(ac,X,Z,ae)}if(aa===3){return H(ad,Y,Z,ae)}}function p(){l(j)}function I(){return l(T)}function d(){return l(F)}function D(aa){var X=this[h],W=this[V],Z,Y;if((X.nodeType===3||X.nodeType===4)&&X.nodeValue){if(!W){X.parentNode.insertBefore(aa,X)}else{if(W>=X.nodeValue.length){c.insertAfter(aa,X)}else{Z=X.splitText(W);X.parentNode.insertBefore(aa,Z)}}}else{if(X.childNodes.length>0){Y=X.childNodes[W]}if(Y){X.insertBefore(aa,Y)}else{X.appendChild(aa)}}}function N(X){var W=O.extractContents();O.insertNode(X);X.appendChild(W);O.selectNode(X)}function L(){return k(new b(c),{startContainer:O[h],startOffset:O[V],endContainer:O[Q],endOffset:O[A],collapsed:O.collapsed,commonAncestorContainer:O.commonAncestorContainer})}function P(W,X){var Y;if(W.nodeType==3){return W}if(X<0){return W}Y=W.firstChild;while(Y&&X>0){--X;Y=Y.nextSibling}if(Y){return Y}return W}function m(){return(O[h]==O[Q]&&O[V]==O[A])}function H(Z,ab,X,aa){var ac,Y,W,ad,af,ae;if(Z==X){if(ab==aa){return 0}if(ab<aa){return -1}return 1}ac=X;while(ac&&ac.parentNode!=Z){ac=ac.parentNode}if(ac){Y=0;W=Z.firstChild;while(W!=ac&&Y<ab){Y++;W=W.nextSibling}if(ab<=Y){return -1}return 1}ac=Z;while(ac&&ac.parentNode!=X){ac=ac.parentNode}if(ac){Y=0;W=X.firstChild;while(W!=ac&&Y<aa){Y++;W=W.nextSibling}if(Y<aa){return -1}return 1}ad=c.findCommonAncestor(Z,X);af=Z;while(af&&af.parentNode!=ad){af=af.parentNode}if(!af){af=ad}ae=X;while(ae&&ae.parentNode!=ad){ae=ae.parentNode}if(!ae){ae=ad}if(af==ae){return 0}W=ad.firstChild;while(W){if(W==af){return -1}if(W==ae){return 1}W=W.nextSibling}}function C(X,aa,Z){var W,Y;if(X){O[h]=aa;O[V]=Z}else{O[Q]=aa;O[A]=Z}W=O[Q];while(W.parentNode){W=W.parentNode}Y=O[h];while(Y.parentNode){Y=Y.parentNode}if(Y==W){if(H(O[h],O[V],O[Q],O[A])>0){O.collapse(X)}}else{O.collapse(X)}O.collapsed=m();O.commonAncestorContainer=c.findCommonAncestor(O[h],O[Q])}function l(ad){var ac,Z=0,af=0,X,ab,Y,aa,W,ae;if(O[h]==O[Q]){return f(ad)}for(ac=O[Q],X=ac.parentNode;X;ac=X,X=X.parentNode){if(X==O[h]){return r(ac,ad)}++Z}for(ac=O[h],X=ac.parentNode;X;ac=X,X=X.parentNode){if(X==O[Q]){return U(ac,ad)}++af}ab=af-Z;Y=O[h];while(ab>0){Y=Y.parentNode;ab--}aa=O[Q];while(ab<0){aa=aa.parentNode;ab++}for(W=Y.parentNode,ae=aa.parentNode;W!=ae;W=W.parentNode,ae=ae.parentNode){Y=W;aa=ae}return o(Y,aa,ad)}function f(ac){var ae,af,W,Y,Z,ad,aa,X,ab;if(ac!=j){ae=x()}if(O[V]==O[A]){return ae}if(O[h].nodeType==3){af=O[h].nodeValue;W=af.substring(O[V],O[A]);if(ac!=F){Y=O[h];X=O[V];ab=O[A]-O[V];if(X===0&&ab>=Y.nodeValue.length-1){Y.parentNode.removeChild(Y)}else{Y.deleteData(X,ab)}O.collapse(E)}if(ac==j){return}if(W.length>0){ae.appendChild(e.createTextNode(W))}return ae}Y=P(O[h],O[V]);Z=O[A]-O[V];while(Y&&Z>0){ad=Y.nextSibling;aa=z(Y,ac);if(ae){ae.appendChild(aa)}--Z;Y=ad}if(ac!=F){O.collapse(E)}return ae}function r(ad,aa){var ac,ab,X,W,Z,Y;if(aa!=j){ac=x()}ab=i(ad,aa);if(ac){ac.appendChild(ab)}X=n(ad);W=X-O[V];if(W<=0){if(aa!=F){O.setEndBefore(ad);O.collapse(S)}return ac}ab=ad.previousSibling;while(W>0){Z=ab.previousSibling;Y=z(ab,aa);if(ac){ac.insertBefore(Y,ac.firstChild)}--W;ab=Z}if(aa!=F){O.setEndBefore(ad);O.collapse(S)}return ac}function U(ab,aa){var ad,X,ac,W,Z,Y;if(aa!=j){ad=x()}ac=R(ab,aa);if(ad){ad.appendChild(ac)}X=n(ab);++X;W=O[A]-X;ac=ab.nextSibling;while(ac&&W>0){Z=ac.nextSibling;Y=z(ac,aa);if(ad){ad.appendChild(Y)}--W;ac=Z}if(aa!=F){O.setStartAfter(ab);O.collapse(E)}return ad}function o(ab,W,ae){var Y,ag,aa,ac,ad,X,af,Z;if(ae!=j){ag=x()}Y=R(ab,ae);if(ag){ag.appendChild(Y)}aa=ab.parentNode;ac=n(ab);ad=n(W);++ac;X=ad-ac;af=ab.nextSibling;while(X>0){Z=af.nextSibling;Y=z(af,ae);if(ag){ag.appendChild(Y)}af=Z;--X}Y=i(W,ae);if(ag){ag.appendChild(Y)}if(ae!=F){O.setStartAfter(ab);O.collapse(E)}return ag}function i(ac,ad){var Y=P(O[Q],O[A]-1),ae,ab,aa,W,X,Z=Y!=O[Q];if(Y==ac){return M(Y,Z,S,ad)}ae=Y.parentNode;ab=M(ae,S,S,ad);while(ae){while(Y){aa=Y.previousSibling;W=M(Y,Z,S,ad);if(ad!=j){ab.insertBefore(W,ab.firstChild)}Z=E;Y=aa}if(ae==ac){return ab}Y=ae.previousSibling;ae=ae.parentNode;X=M(ae,S,S,ad);if(ad!=j){X.appendChild(ab)}ab=X}}function R(ac,ad){var Z=P(O[h],O[V]),aa=Z!=O[h],ae,ab,Y,W,X;if(Z==ac){return M(Z,aa,E,ad)}ae=Z.parentNode;ab=M(ae,S,E,ad);while(ae){while(Z){Y=Z.nextSibling;W=M(Z,aa,E,ad);if(ad!=j){ab.appendChild(W)}aa=E;Z=Y}if(ae==ac){return ab}Z=ae.nextSibling;ae=ae.parentNode;X=M(ae,S,E,ad);if(ad!=j){X.appendChild(ab)}ab=X}}function M(W,aa,ad,ae){var Z,Y,ab,X,ac;if(aa){return z(W,ae)}if(W.nodeType==3){Z=W.nodeValue;if(ad){X=O[V];Y=Z.substring(X);ab=Z.substring(0,X)}else{X=O[A];Y=Z.substring(0,X);ab=Z.substring(X)}if(ae!=F){W.nodeValue=ab}if(ae==j){return}ac=c.clone(W,S);ac.nodeValue=Y;return ac}if(ae==j){return}return c.clone(W,S)}function z(X,W){if(W!=j){return W==F?c.clone(X,E):X}X.parentNode.removeChild(X)}}a.Range=b})(tinymce.dom);(function(){function a(d){var b=this,h=d.dom,c=true,f=false;function e(i,j){var k,u=0,q,n,m,l,o,r,p=-1,s;k=i.duplicate();k.collapse(j);s=k.parentElement();if(s.ownerDocument!==d.dom.doc){return}while(s.contentEditable==="false"){s=s.parentNode}if(!s.hasChildNodes()){return{node:s,inside:1}}m=s.children;q=m.length-1;while(u<=q){r=Math.floor((u+q)/2);l=m[r];k.moveToElementText(l);p=k.compareEndPoints(j?"StartToStart":"EndToEnd",i);if(p>0){q=r-1}else{if(p<0){u=r+1}else{return{node:l}}}}if(p<0){if(!l){k.moveToElementText(s);k.collapse(true);l=s;n=true}else{k.collapse(false)}o=0;while(k.compareEndPoints(j?"StartToStart":"StartToEnd",i)!==0){if(k.move("character",1)===0||s!=k.parentElement()){break}o++}}else{k.collapse(true);o=0;while(k.compareEndPoints(j?"StartToStart":"StartToEnd",i)!==0){if(k.move("character",-1)===0||s!=k.parentElement()){break}o++}}return{node:l,position:p,offset:o,inside:n}}function g(){var i=d.getRng(),r=h.createRng(),l,k,p,q,m,j;l=i.item?i.item(0):i.parentElement();if(l.ownerDocument!=h.doc){return r}k=d.isCollapsed();if(i.item){r.setStart(l.parentNode,h.nodeIndex(l));r.setEnd(r.startContainer,r.startOffset+1);return r}function o(B){var v=e(i,B),s,z,A=0,y,x,u;s=v.node;z=v.offset;if(v.inside&&!s.hasChildNodes()){r[B?"setStart":"setEnd"](s,0);return}if(z===x){r[B?"setStartBefore":"setEndAfter"](s);return}if(v.position<0){y=v.inside?s.firstChild:s.nextSibling;if(!y){r[B?"setStartAfter":"setEndAfter"](s);return}if(!z){if(y.nodeType==3){r[B?"setStart":"setEnd"](y,0)}else{r[B?"setStartBefore":"setEndBefore"](y)}return}while(y){u=y.nodeValue;A+=u.length;if(A>=z){s=y;A-=z;A=u.length-A;break}y=y.nextSibling}}else{y=s.previousSibling;if(!y){return r[B?"setStartBefore":"setEndBefore"](s)}if(!z){if(s.nodeType==3){r[B?"setStart":"setEnd"](y,s.nodeValue.length)}else{r[B?"setStartAfter":"setEndAfter"](y)}return}while(y){A+=y.nodeValue.length;if(A>=z){s=y;A-=z;break}y=y.previousSibling}}r[B?"setStart":"setEnd"](s,A)}try{o(true);if(!k){o()}}catch(n){if(n.number==-2147024809){m=b.getBookmark(2);p=i.duplicate();p.collapse(true);l=p.parentElement();if(!k){p=i.duplicate();p.collapse(false);q=p.parentElement();q.innerHTML=q.innerHTML}l.innerHTML=l.innerHTML;b.moveToBookmark(m);i=d.getRng();o(true);if(!k){o()}}else{throw n}}return r}this.getBookmark=function(m){var j=d.getRng(),o,i,l={};function n(v){var u,p,s,r,q=[];u=v.parentNode;p=h.getRoot().parentNode;while(u!=p&&u.nodeType!==9){s=u.children;r=s.length;while(r--){if(v===s[r]){q.push(r);break}}v=u;u=u.parentNode}return q}function k(q){var p;p=e(j,q);if(p){return{position:p.position,offset:p.offset,indexes:n(p.node),inside:p.inside}}}if(m===2){if(!j.item){l.start=k(true);if(!d.isCollapsed()){l.end=k()}}else{l.start={ctrl:true,indexes:n(j.item(0))}}}return l};this.moveToBookmark=function(k){var j,i=h.doc.body;function m(o){var r,q,n,p;r=h.getRoot();for(q=o.length-1;q>=0;q--){p=r.children;n=o[q];if(n<=p.length-1){r=p[n]}}return r}function l(r){var n=k[r?"start":"end"],q,p,o;if(n){q=n.position>0;p=i.createTextRange();p.moveToElementText(m(n.indexes));offset=n.offset;if(offset!==o){p.collapse(n.inside||q);p.moveStart("character",q?-offset:offset)}else{p.collapse(r)}j.setEndPoint(r?"StartToStart":"EndToStart",p);if(r){j.collapse(true)}}}if(k.start){if(k.start.ctrl){j=i.createControlRange();j.addElement(m(k.start.indexes));j.select()}else{j=i.createTextRange();l(true);l();j.select()}}};this.addRange=function(i){var n,l,k,p,s,q,r=d.dom.doc,m=r.body;function j(A){var v,z,u,y,x;u=h.create("a");v=A?k:s;z=A?p:q;y=n.duplicate();if(v==r||v==r.documentElement){v=m;z=0}if(v.nodeType==3){v.parentNode.insertBefore(u,v);y.moveToElementText(u);y.moveStart("character",z);h.remove(u);n.setEndPoint(A?"StartToStart":"EndToEnd",y)}else{x=v.childNodes;if(x.length){if(z>=x.length){h.insertAfter(u,x[x.length-1])}else{v.insertBefore(u,x[z])}y.moveToElementText(u)}else{if(v.canHaveHTML){v.innerHTML="<span>\uFEFF</span>";u=v.firstChild;y.moveToElementText(u);y.collapse(f)}}n.setEndPoint(A?"StartToStart":"EndToEnd",y);h.remove(u)}}k=i.startContainer;p=i.startOffset;s=i.endContainer;q=i.endOffset;n=m.createTextRange();if(k==s&&k.nodeType==1){if(p==q&&!k.hasChildNodes()){if(k.canHaveHTML){k.innerHTML="<span>\uFEFF</span><span>\uFEFF</span>";n.moveToElementText(k.lastChild);n.select();h.doc.selection.clear();k.innerHTML="";return}else{p=h.nodeIndex(k);k=k.parentNode}}if(p==q-1){try{l=m.createControlRange();l.addElement(k.childNodes[p]);l.select();return}catch(o){}}}j(true);j();n.select()};this.getRangeAt=g}tinymce.dom.TridentSelection=a})();(function(){var n=/((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^\[\]]*\]|['"][^'"]*['"]|[^\[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g,i="sizcache",o=0,r=Object.prototype.toString,h=false,g=true,q=/\\/g,v=/\r\n/g,y=/\W/;[0,0].sort(function(){g=false;return 0});var d=function(D,e,G,H){G=G||[];e=e||document;var J=e;if(e.nodeType!==1&&e.nodeType!==9){return[]}if(!D||typeof D!=="string"){return G}var A,L,O,z,K,N,M,F,C=true,B=d.isXML(e),E=[],I=D;do{n.exec("");A=n.exec(I);if(A){I=A[3];E.push(A[1]);if(A[2]){z=A[3];break}}}while(A);if(E.length>1&&j.exec(D)){if(E.length===2&&k.relative[E[0]]){L=s(E[0]+E[1],e,H)}else{L=k.relative[E[0]]?[e]:d(E.shift(),e);while(E.length){D=E.shift();if(k.relative[D]){D+=E.shift()}L=s(D,L,H)}}}else{if(!H&&E.length>1&&e.nodeType===9&&!B&&k.match.ID.test(E[0])&&!k.match.ID.test(E[E.length-1])){K=d.find(E.shift(),e,B);e=K.expr?d.filter(K.expr,K.set)[0]:K.set[0]}if(e){K=H?{expr:E.pop(),set:l(H)}:d.find(E.pop(),E.length===1&&(E[0]==="~"||E[0]==="+")&&e.parentNode?e.parentNode:e,B);L=K.expr?d.filter(K.expr,K.set):K.set;if(E.length>0){O=l(L)}else{C=false}while(E.length){N=E.pop();M=N;if(!k.relative[N]){N=""}else{M=E.pop()}if(M==null){M=e}k.relative[N](O,M,B)}}else{O=E=[]}}if(!O){O=L}if(!O){d.error(N||D)}if(r.call(O)==="[object Array]"){if(!C){G.push.apply(G,O)}else{if(e&&e.nodeType===1){for(F=0;O[F]!=null;F++){if(O[F]&&(O[F]===true||O[F].nodeType===1&&d.contains(e,O[F]))){G.push(L[F])}}}else{for(F=0;O[F]!=null;F++){if(O[F]&&O[F].nodeType===1){G.push(L[F])}}}}}else{l(O,G)}if(z){d(z,J,G,H);d.uniqueSort(G)}return G};d.uniqueSort=function(z){if(p){h=g;z.sort(p);if(h){for(var e=1;e<z.length;e++){if(z[e]===z[e-1]){z.splice(e--,1)}}}}return z};d.matches=function(e,z){return d(e,null,null,z)};d.matchesSelector=function(e,z){return d(z,null,null,[e]).length>0};d.find=function(F,e,G){var E,A,C,B,D,z;if(!F){return[]}for(A=0,C=k.order.length;A<C;A++){D=k.order[A];if((B=k.leftMatch[D].exec(F))){z=B[1];B.splice(1,1);if(z.substr(z.length-1)!=="\\"){B[1]=(B[1]||"").replace(q,"");E=k.find[D](B,e,G);if(E!=null){F=F.replace(k.match[D],"");break}}}}if(!E){E=typeof e.getElementsByTagName!=="undefined"?e.getElementsByTagName("*"):[]}return{set:E,expr:F}};d.filter=function(J,I,M,C){var E,e,H,O,L,z,B,D,K,A=J,N=[],G=I,F=I&&I[0]&&d.isXML(I[0]);while(J&&I.length){for(H in k.filter){if((E=k.leftMatch[H].exec(J))!=null&&E[2]){z=k.filter[H];B=E[1];e=false;E.splice(1,1);if(B.substr(B.length-1)==="\\"){continue}if(G===N){N=[]}if(k.preFilter[H]){E=k.preFilter[H](E,G,M,N,C,F);if(!E){e=O=true}else{if(E===true){continue}}}if(E){for(D=0;(L=G[D])!=null;D++){if(L){O=z(L,E,D,G);K=C^O;if(M&&O!=null){if(K){e=true}else{G[D]=false}}else{if(K){N.push(L);e=true}}}}}if(O!==undefined){if(!M){G=N}J=J.replace(k.match[H],"");if(!e){return[]}break}}}if(J===A){if(e==null){d.error(J)}else{break}}A=J}return G};d.error=function(e){throw new Error("Syntax error, unrecognized expression: "+e)};var b=d.getText=function(C){var A,B,e=C.nodeType,z="";if(e){if(e===1||e===9||e===11){if(typeof C.textContent==="string"){return C.textContent}else{if(typeof C.innerText==="string"){return C.innerText.replace(v,"")}else{for(C=C.firstChild;C;C=C.nextSibling){z+=b(C)}}}}else{if(e===3||e===4){return C.nodeValue}}}else{for(A=0;(B=C[A]);A++){if(B.nodeType!==8){z+=b(B)}}}return z};var k=d.selectors={order:["ID","NAME","TAG"],match:{ID:/#((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,CLASS:/\.((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,NAME:/\[name=['"]*((?:[\w\u00c0-\uFFFF\-]|\\.)+)['"]*\]/,ATTR:/\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=)\s*(?:(['"])(.*?)\3|(#?(?:[\w\u00c0-\uFFFF\-]|\\.)*)|)|)\s*\]/,TAG:/^((?:[\w\u00c0-\uFFFF\*\-]|\\.)+)/,CHILD:/:(only|nth|last|first)-child(?:\(\s*(even|odd|(?:[+\-]?\d+|(?:[+\-]?\d*)?n\s*(?:[+\-]\s*\d+)?))\s*\))?/,POS:/:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^\-]|$)/,PSEUDO:/:((?:[\w\u00c0-\uFFFF\-]|\\.)+)(?:\((['"]?)((?:\([^\)]+\)|[^\(\)]*)+)\2\))?/},leftMatch:{},attrMap:{"class":"className","for":"htmlFor"},attrHandle:{href:function(e){return e.getAttribute("href")},type:function(e){return e.getAttribute("type")}},relative:{"+":function(E,z){var B=typeof z==="string",D=B&&!y.test(z),F=B&&!D;if(D){z=z.toLowerCase()}for(var A=0,e=E.length,C;A<e;A++){if((C=E[A])){while((C=C.previousSibling)&&C.nodeType!==1){}E[A]=F||C&&C.nodeName.toLowerCase()===z?C||false:C===z}}if(F){d.filter(z,E,true)}},">":function(E,z){var D,C=typeof z==="string",A=0,e=E.length;if(C&&!y.test(z)){z=z.toLowerCase();for(;A<e;A++){D=E[A];if(D){var B=D.parentNode;E[A]=B.nodeName.toLowerCase()===z?B:false}}}else{for(;A<e;A++){D=E[A];if(D){E[A]=C?D.parentNode:D.parentNode===z}}if(C){d.filter(z,E,true)}}},"":function(B,z,D){var C,A=o++,e=u;if(typeof z==="string"&&!y.test(z)){z=z.toLowerCase();C=z;e=a}e("parentNode",z,A,B,C,D)},"~":function(B,z,D){var C,A=o++,e=u;if(typeof z==="string"&&!y.test(z)){z=z.toLowerCase();C=z;e=a}e("previousSibling",z,A,B,C,D)}},find:{ID:function(z,A,B){if(typeof A.getElementById!=="undefined"&&!B){var e=A.getElementById(z[1]);return e&&e.parentNode?[e]:[]}},NAME:function(A,D){if(typeof D.getElementsByName!=="undefined"){var z=[],C=D.getElementsByName(A[1]);for(var B=0,e=C.length;B<e;B++){if(C[B].getAttribute("name")===A[1]){z.push(C[B])}}return z.length===0?null:z}},TAG:function(e,z){if(typeof z.getElementsByTagName!=="undefined"){return z.getElementsByTagName(e[1])}}},preFilter:{CLASS:function(B,z,A,e,E,F){B=" "+B[1].replace(q,"")+" ";if(F){return B}for(var C=0,D;(D=z[C])!=null;C++){if(D){if(E^(D.className&&(" "+D.className+" ").replace(/[\t\n\r]/g," ").indexOf(B)>=0)){if(!A){e.push(D)}}else{if(A){z[C]=false}}}}return false},ID:function(e){return e[1].replace(q,"")},TAG:function(z,e){return z[1].replace(q,"").toLowerCase()},CHILD:function(e){if(e[1]==="nth"){if(!e[2]){d.error(e[0])}e[2]=e[2].replace(/^\+|\s*/g,"");var z=/(-?)(\d*)(?:n([+\-]?\d*))?/.exec(e[2]==="even"&&"2n"||e[2]==="odd"&&"2n+1"||!/\D/.test(e[2])&&"0n+"+e[2]||e[2]);e[2]=(z[1]+(z[2]||1))-0;e[3]=z[3]-0}else{if(e[2]){d.error(e[0])}}e[0]=o++;return e},ATTR:function(C,z,A,e,D,E){var B=C[1]=C[1].replace(q,"");if(!E&&k.attrMap[B]){C[1]=k.attrMap[B]}C[4]=(C[4]||C[5]||"").replace(q,"");if(C[2]==="~="){C[4]=" "+C[4]+" "}return C},PSEUDO:function(C,z,A,e,D){if(C[1]==="not"){if((n.exec(C[3])||"").length>1||/^\w/.test(C[3])){C[3]=d(C[3],null,null,z)}else{var B=d.filter(C[3],z,A,true^D);if(!A){e.push.apply(e,B)}return false}}else{if(k.match.POS.test(C[0])||k.match.CHILD.test(C[0])){return true}}return C},POS:function(e){e.unshift(true);return e}},filters:{enabled:function(e){return e.disabled===false&&e.type!=="hidden"},disabled:function(e){return e.disabled===true},checked:function(e){return e.checked===true},selected:function(e){if(e.parentNode){e.parentNode.selectedIndex}return e.selected===true},parent:function(e){return !!e.firstChild},empty:function(e){return !e.firstChild},has:function(A,z,e){return !!d(e[3],A).length},header:function(e){return(/h\d/i).test(e.nodeName)},text:function(A){var e=A.getAttribute("type"),z=A.type;return A.nodeName.toLowerCase()==="input"&&"text"===z&&(e===z||e===null)},radio:function(e){return e.nodeName.toLowerCase()==="input"&&"radio"===e.type},checkbox:function(e){return e.nodeName.toLowerCase()==="input"&&"checkbox"===e.type},file:function(e){return e.nodeName.toLowerCase()==="input"&&"file"===e.type},password:function(e){return e.nodeName.toLowerCase()==="input"&&"password"===e.type},submit:function(z){var e=z.nodeName.toLowerCase();return(e==="input"||e==="button")&&"submit"===z.type},image:function(e){return e.nodeName.toLowerCase()==="input"&&"image"===e.type},reset:function(z){var e=z.nodeName.toLowerCase();return(e==="input"||e==="button")&&"reset"===z.type},button:function(z){var e=z.nodeName.toLowerCase();return e==="input"&&"button"===z.type||e==="button"},input:function(e){return(/input|select|textarea|button/i).test(e.nodeName)},focus:function(e){return e===e.ownerDocument.activeElement}},setFilters:{first:function(z,e){return e===0},last:function(A,z,e,B){return z===B.length-1},even:function(z,e){return e%2===0},odd:function(z,e){return e%2===1},lt:function(A,z,e){return z<e[3]-0},gt:function(A,z,e){return z>e[3]-0},nth:function(A,z,e){return e[3]-0===z},eq:function(A,z,e){return e[3]-0===z}},filter:{PSEUDO:function(A,F,E,G){var e=F[1],z=k.filters[e];if(z){return z(A,E,F,G)}else{if(e==="contains"){return(A.textContent||A.innerText||b([A])||"").indexOf(F[3])>=0}else{if(e==="not"){var B=F[3];for(var D=0,C=B.length;D<C;D++){if(B[D]===A){return false}}return true}else{d.error(e)}}}},CHILD:function(A,C){var B,I,E,H,e,D,G,F=C[1],z=A;switch(F){case"only":case"first":while((z=z.previousSibling)){if(z.nodeType===1){return false}}if(F==="first"){return true}z=A;case"last":while((z=z.nextSibling)){if(z.nodeType===1){return false}}return true;case"nth":B=C[2];I=C[3];if(B===1&&I===0){return true}E=C[0];H=A.parentNode;if(H&&(H[i]!==E||!A.nodeIndex)){D=0;for(z=H.firstChild;z;z=z.nextSibling){if(z.nodeType===1){z.nodeIndex=++D}}H[i]=E}G=A.nodeIndex-I;if(B===0){return G===0}else{return(G%B===0&&G/B>=0)}}},ID:function(z,e){return z.nodeType===1&&z.getAttribute("id")===e},TAG:function(z,e){return(e==="*"&&z.nodeType===1)||!!z.nodeName&&z.nodeName.toLowerCase()===e},CLASS:function(z,e){return(" "+(z.className||z.getAttribute("class"))+" ").indexOf(e)>-1},ATTR:function(D,B){var A=B[1],e=d.attr?d.attr(D,A):k.attrHandle[A]?k.attrHandle[A](D):D[A]!=null?D[A]:D.getAttribute(A),E=e+"",C=B[2],z=B[4];return e==null?C==="!=":!C&&d.attr?e!=null:C==="="?E===z:C==="*="?E.indexOf(z)>=0:C==="~="?(" "+E+" ").indexOf(z)>=0:!z?E&&e!==false:C==="!="?E!==z:C==="^="?E.indexOf(z)===0:C==="$="?E.substr(E.length-z.length)===z:C==="|="?E===z||E.substr(0,z.length+1)===z+"-":false},POS:function(C,z,A,D){var e=z[2],B=k.setFilters[e];if(B){return B(C,A,z,D)}}}};var j=k.match.POS,c=function(z,e){return"\\"+(e-0+1)};for(var f in k.match){k.match[f]=new RegExp(k.match[f].source+(/(?![^\[]*\])(?![^\(]*\))/.source));k.leftMatch[f]=new RegExp(/(^(?:.|\r|\n)*?)/.source+k.match[f].source.replace(/\\(\d+)/g,c))}k.match.globalPOS=j;var l=function(z,e){z=Array.prototype.slice.call(z,0);if(e){e.push.apply(e,z);return e}return z};try{Array.prototype.slice.call(document.documentElement.childNodes,0)[0].nodeType}catch(x){l=function(C,B){var A=0,z=B||[];if(r.call(C)==="[object Array]"){Array.prototype.push.apply(z,C)}else{if(typeof C.length==="number"){for(var e=C.length;A<e;A++){z.push(C[A])}}else{for(;C[A];A++){z.push(C[A])}}}return z}}var p,m;if(document.documentElement.compareDocumentPosition){p=function(z,e){if(z===e){h=true;return 0}if(!z.compareDocumentPosition||!e.compareDocumentPosition){return z.compareDocumentPosition?-1:1}return z.compareDocumentPosition(e)&4?-1:1}}else{p=function(G,F){if(G===F){h=true;return 0}else{if(G.sourceIndex&&F.sourceIndex){return G.sourceIndex-F.sourceIndex}}var D,z,A=[],e=[],C=G.parentNode,E=F.parentNode,H=C;if(C===E){return m(G,F)}else{if(!C){return -1}else{if(!E){return 1}}}while(H){A.unshift(H);H=H.parentNode}H=E;while(H){e.unshift(H);H=H.parentNode}D=A.length;z=e.length;for(var B=0;B<D&&B<z;B++){if(A[B]!==e[B]){return m(A[B],e[B])}}return B===D?m(G,e[B],-1):m(A[B],F,1)};m=function(z,e,A){if(z===e){return A}var B=z.nextSibling;while(B){if(B===e){return -1}B=B.nextSibling}return 1}}(function(){var z=document.createElement("div"),A="script"+(new Date()).getTime(),e=document.documentElement;z.innerHTML="<a name='"+A+"'/>";e.insertBefore(z,e.firstChild);if(document.getElementById(A)){k.find.ID=function(C,D,E){if(typeof D.getElementById!=="undefined"&&!E){var B=D.getElementById(C[1]);return B?B.id===C[1]||typeof B.getAttributeNode!=="undefined"&&B.getAttributeNode("id").nodeValue===C[1]?[B]:undefined:[]}};k.filter.ID=function(D,B){var C=typeof D.getAttributeNode!=="undefined"&&D.getAttributeNode("id");return D.nodeType===1&&C&&C.nodeValue===B}}e.removeChild(z);e=z=null})();(function(){var e=document.createElement("div");e.appendChild(document.createComment(""));if(e.getElementsByTagName("*").length>0){k.find.TAG=function(z,D){var C=D.getElementsByTagName(z[1]);if(z[1]==="*"){var B=[];for(var A=0;C[A];A++){if(C[A].nodeType===1){B.push(C[A])}}C=B}return C}}e.innerHTML="<a href='#'></a>";if(e.firstChild&&typeof e.firstChild.getAttribute!=="undefined"&&e.firstChild.getAttribute("href")!=="#"){k.attrHandle.href=function(z){return z.getAttribute("href",2)}}e=null})();if(document.querySelectorAll){(function(){var e=d,B=document.createElement("div"),A="__sizzle__";B.innerHTML="<p class='TEST'></p>";if(B.querySelectorAll&&B.querySelectorAll(".TEST").length===0){return}d=function(M,D,H,L){D=D||document;if(!L&&!d.isXML(D)){var K=/^(\w+$)|^\.([\w\-]+$)|^#([\w\-]+$)/.exec(M);if(K&&(D.nodeType===1||D.nodeType===9)){if(K[1]){return l(D.getElementsByTagName(M),H)}else{if(K[2]&&k.find.CLASS&&D.getElementsByClassName){return l(D.getElementsByClassName(K[2]),H)}}}if(D.nodeType===9){if(M==="body"&&D.body){return l([D.body],H)}else{if(K&&K[3]){var G=D.getElementById(K[3]);if(G&&G.parentNode){if(G.id===K[3]){return l([G],H)}}else{return l([],H)}}}try{return l(D.querySelectorAll(M),H)}catch(I){}}else{if(D.nodeType===1&&D.nodeName.toLowerCase()!=="object"){var E=D,F=D.getAttribute("id"),C=F||A,O=D.parentNode,N=/^\s*[+~]/.test(M);if(!F){D.setAttribute("id",C)}else{C=C.replace(/'/g,"\\$&")}if(N&&O){D=D.parentNode}try{if(!N||O){return l(D.querySelectorAll("[id='"+C+"'] "+M),H)}}catch(J){}finally{if(!F){E.removeAttribute("id")}}}}}return e(M,D,H,L)};for(var z in e){d[z]=e[z]}B=null})()}(function(){var e=document.documentElement,A=e.matchesSelector||e.mozMatchesSelector||e.webkitMatchesSelector||e.msMatchesSelector;if(A){var C=!A.call(document.createElement("div"),"div"),z=false;try{A.call(document.documentElement,"[test!='']:sizzle")}catch(B){z=true}d.matchesSelector=function(E,G){G=G.replace(/\=\s*([^'"\]]*)\s*\]/g,"='$1']");if(!d.isXML(E)){try{if(z||!k.match.PSEUDO.test(G)&&!/!=/.test(G)){var D=A.call(E,G);if(D||!C||E.document&&E.document.nodeType!==11){return D}}}catch(F){}}return d(G,null,null,[E]).length>0}}})();(function(){var e=document.createElement("div");e.innerHTML="<div class='test e'></div><div class='test'></div>";if(!e.getElementsByClassName||e.getElementsByClassName("e").length===0){return}e.lastChild.className="e";if(e.getElementsByClassName("e").length===1){return}k.order.splice(1,0,"CLASS");k.find.CLASS=function(z,A,B){if(typeof A.getElementsByClassName!=="undefined"&&!B){return A.getElementsByClassName(z[1])}};e=null})();function a(z,E,D,H,F,G){for(var B=0,A=H.length;B<A;B++){var e=H[B];if(e){var C=false;e=e[z];while(e){if(e[i]===D){C=H[e.sizset];break}if(e.nodeType===1&&!G){e[i]=D;e.sizset=B}if(e.nodeName.toLowerCase()===E){C=e;break}e=e[z]}H[B]=C}}}function u(z,E,D,H,F,G){for(var B=0,A=H.length;B<A;B++){var e=H[B];if(e){var C=false;e=e[z];while(e){if(e[i]===D){C=H[e.sizset];break}if(e.nodeType===1){if(!G){e[i]=D;e.sizset=B}if(typeof E!=="string"){if(e===E){C=true;break}}else{if(d.filter(E,[e]).length>0){C=e;break}}}e=e[z]}H[B]=C}}}if(document.documentElement.contains){d.contains=function(z,e){return z!==e&&(z.contains?z.contains(e):true)}}else{if(document.documentElement.compareDocumentPosition){d.contains=function(z,e){return !!(z.compareDocumentPosition(e)&16)}}else{d.contains=function(){return false}}}d.isXML=function(e){var z=(e?e.ownerDocument||e:0).documentElement;return z?z.nodeName!=="HTML":false};var s=function(A,e,E){var D,F=[],C="",G=e.nodeType?[e]:e;while((D=k.match.PSEUDO.exec(A))){C+=D[0];A=A.replace(k.match.PSEUDO,"")}A=k.relative[A]?A+"*":A;for(var B=0,z=G.length;B<z;B++){d(A,G[B],F,E)}return d.filter(C,F)};window.tinymce.dom.Sizzle=d})();(function(a){a.dom.Element=function(f,d){var b=this,e,c;b.settings=d=d||{};b.id=f;b.dom=e=d.dom||a.DOM;if(!a.isIE){c=e.get(b.id)}a.each(("getPos,getRect,getParent,add,setStyle,getStyle,setStyles,setAttrib,setAttribs,getAttrib,addClass,removeClass,hasClass,getOuterHTML,setOuterHTML,remove,show,hide,isHidden,setHTML,get").split(/,/),function(g){b[g]=function(){var h=[f],j;for(j=0;j<arguments.length;j++){h.push(arguments[j])}h=e[g].apply(e,h);b.update(g);return h}});a.extend(b,{on:function(i,h,g){return a.dom.Event.add(b.id,i,h,g)},getXY:function(){return{x:parseInt(b.getStyle("left")),y:parseInt(b.getStyle("top"))}},getSize:function(){var g=e.get(b.id);return{w:parseInt(b.getStyle("width")||g.clientWidth),h:parseInt(b.getStyle("height")||g.clientHeight)}},moveTo:function(g,h){b.setStyles({left:g,top:h})},moveBy:function(g,i){var h=b.getXY();b.moveTo(h.x+g,h.y+i)},resizeTo:function(g,i){b.setStyles({width:g,height:i})},resizeBy:function(g,j){var i=b.getSize();b.resizeTo(i.w+g,i.h+j)},update:function(h){var g;if(a.isIE6&&d.blocker){h=h||"";if(h.indexOf("get")===0||h.indexOf("has")===0||h.indexOf("is")===0){return}if(h=="remove"){e.remove(b.blocker);return}if(!b.blocker){b.blocker=e.uniqueId();g=e.add(d.container||e.getRoot(),"iframe",{id:b.blocker,style:"position:absolute;",frameBorder:0,src:'javascript:""'});e.setStyle(g,"opacity",0)}else{g=e.get(b.blocker)}e.setStyles(g,{left:b.getStyle("left",1),top:b.getStyle("top",1),width:b.getStyle("width",1),height:b.getStyle("height",1),display:b.getStyle("display",1),zIndex:parseInt(b.getStyle("zIndex",1)||0)-1})}}})}})(tinymce);(function(d){function f(g){return g.replace(/[\n\r]+/g,"")}var c=d.is,b=d.isIE,e=d.each,a=d.dom.TreeWalker;d.create("tinymce.dom.Selection",{Selection:function(j,i,h){var g=this;g.dom=j;g.win=i;g.serializer=h;e(["onBeforeSetContent","onBeforeGetContent","onSetContent","onGetContent"],function(k){g[k]=new d.util.Dispatcher(g)});if(!g.win.getSelection){g.tridentSel=new d.dom.TridentSelection(g)}if(d.isIE&&j.boxModel){this._fixIESelection()}d.addUnload(g.destroy,g)},setCursorLocation:function(i,j){var g=this;var h=g.dom.createRng();h.setStart(i,j);h.setEnd(i,j);g.setRng(h);g.collapse(false)},getContent:function(h){var g=this,i=g.getRng(),m=g.dom.create("body"),k=g.getSel(),j,l,o;h=h||{};j=l="";h.get=true;h.format=h.format||"html";h.forced_root_block="";g.onBeforeGetContent.dispatch(g,h);if(h.format=="text"){return g.isCollapsed()?"":(i.text||(k.toString?k.toString():""))}if(i.cloneContents){o=i.cloneContents();if(o){m.appendChild(o)}}else{if(c(i.item)||c(i.htmlText)){m.innerHTML="<br>"+(i.item?i.item(0).outerHTML:i.htmlText);m.removeChild(m.firstChild)}else{m.innerHTML=i.toString()}}if(/^\s/.test(m.innerHTML)){j=" "}if(/\s+$/.test(m.innerHTML)){l=" "}h.getInner=true;h.content=g.isCollapsed()?"":j+g.serializer.serialize(m,h)+l;g.onGetContent.dispatch(g,h);return h.content},setContent:function(h,j){var o=this,g=o.getRng(),k,l=o.win.document,n,m;j=j||{format:"html"};j.set=true;h=j.content=h;if(!j.no_events){o.onBeforeSetContent.dispatch(o,j)}h=j.content;if(g.insertNode){h+='<span id="__caret">_</span>';if(g.startContainer==l&&g.endContainer==l){l.body.innerHTML=h}else{g.deleteContents();if(l.body.childNodes.length===0){l.body.innerHTML=h}else{if(g.createContextualFragment){g.insertNode(g.createContextualFragment(h))}else{n=l.createDocumentFragment();m=l.createElement("div");n.appendChild(m);m.outerHTML=h;g.insertNode(n)}}}k=o.dom.get("__caret");g=l.createRange();g.setStartBefore(k);g.setEndBefore(k);o.setRng(g);o.dom.remove("__caret");try{o.setRng(g)}catch(i){}}else{if(g.item){l.execCommand("Delete",false,null);g=o.getRng()}if(/^\s+/.test(h)){g.pasteHTML('<span id="__mce_tmp">_</span>'+h);o.dom.remove("__mce_tmp")}else{g.pasteHTML(h)}}if(!j.no_events){o.onSetContent.dispatch(o,j)}},getStart:function(){var h=this.getRng(),i,g,k,j;if(h.duplicate||h.item){if(h.item){return h.item(0)}k=h.duplicate();k.collapse(1);i=k.parentElement();g=j=h.parentElement();while(j=j.parentNode){if(j==i){i=g;break}}return i}else{i=h.startContainer;if(i.nodeType==1&&i.hasChildNodes()){i=i.childNodes[Math.min(i.childNodes.length-1,h.startOffset)]}if(i&&i.nodeType==3){return i.parentNode}return i}},getEnd:function(){var h=this,i=h.getRng(),j,g;if(i.duplicate||i.item){if(i.item){return i.item(0)}i=i.duplicate();i.collapse(0);j=i.parentElement();if(j&&j.nodeName=="BODY"){return j.lastChild||j}return j}else{j=i.endContainer;g=i.endOffset;if(j.nodeType==1&&j.hasChildNodes()){j=j.childNodes[g>0?g-1:g]}if(j&&j.nodeType==3){return j.parentNode}return j}},getBookmark:function(s,u){var x=this,n=x.dom,h,k,j,o,i,p,q,m="\uFEFF",v;function g(z,A){var y=0;e(n.select(z),function(C,B){if(C==A){y=B}});return y}function l(){var z=x.getRng(true),y=n.getRoot(),A={};function B(E,J){var D=E[J?"startContainer":"endContainer"],I=E[J?"startOffset":"endOffset"],C=[],F,H,G=0;if(D.nodeType==3){if(u){for(F=D.previousSibling;F&&F.nodeType==3;F=F.previousSibling){I+=F.nodeValue.length}}C.push(I)}else{H=D.childNodes;if(I>=H.length&&H.length){G=1;I=Math.max(0,H.length-1)}C.push(x.dom.nodeIndex(H[I],u)+G)}for(;D&&D!=y;D=D.parentNode){C.push(x.dom.nodeIndex(D,u))}return C}A.start=B(z,true);if(!x.isCollapsed()){A.end=B(z)}return A}if(s==2){if(x.tridentSel){return x.tridentSel.getBookmark(s)}return l()}if(s){return{rng:x.getRng()}}h=x.getRng();j=n.uniqueId();o=tinyMCE.activeEditor.selection.isCollapsed();v="overflow:hidden;line-height:0px";if(h.duplicate||h.item){if(!h.item){k=h.duplicate();try{h.collapse();h.pasteHTML('<span data-mce-type="bookmark" id="'+j+'_start" style="'+v+'">'+m+"</span>");if(!o){k.collapse(false);h.moveToElementText(k.parentElement());if(h.compareEndPoints("StartToEnd",k)===0){k.move("character",-1)}k.pasteHTML('<span data-mce-type="bookmark" id="'+j+'_end" style="'+v+'">'+m+"</span>")}}catch(r){return null}}else{p=h.item(0);i=p.nodeName;return{name:i,index:g(i,p)}}}else{p=x.getNode();i=p.nodeName;if(i=="IMG"){return{name:i,index:g(i,p)}}if(h.startContainer.nodeType==9){return}k=h.cloneRange();if(!o){k.collapse(false);k.insertNode(n.create("span",{"data-mce-type":"bookmark",id:j+"_end",style:v},m))}h.collapse(true);h.insertNode(n.create("span",{"data-mce-type":"bookmark",id:j+"_start",style:v},m))}x.moveToBookmark({id:j,keep:1});return{id:j}},moveToBookmark:function(o){var s=this,m=s.dom,j,i,g,r,k,u,p,q;function h(B){var v=o[B?"start":"end"],y,z,A,x;if(v){A=v[0];for(z=r,y=v.length-1;y>=1;y--){x=z.childNodes;if(v[y]>x.length-1){return}z=x[v[y]]}if(z.nodeType===3){A=Math.min(v[0],z.nodeValue.length)}if(z.nodeType===1){A=Math.min(v[0],z.childNodes.length)}if(B){g.setStart(z,A)}else{g.setEnd(z,A)}}return true}function l(C){var x=m.get(o.id+"_"+C),B,v,z,A,y=o.keep;if(x){B=x.parentNode;if(C=="start"){if(!y){v=m.nodeIndex(x)}else{B=x.firstChild;v=1}k=u=B;p=q=v}else{if(!y){v=m.nodeIndex(x)}else{B=x.firstChild;v=1}u=B;q=v}if(!y){A=x.previousSibling;z=x.nextSibling;e(d.grep(x.childNodes),function(D){if(D.nodeType==3){D.nodeValue=D.nodeValue.replace(/\uFEFF/g,"")}});while(x=m.get(o.id+"_"+C)){m.remove(x,1)}if(A&&z&&A.nodeType==z.nodeType&&A.nodeType==3&&!d.isOpera){v=A.nodeValue.length;A.appendData(z.nodeValue);m.remove(z);if(C=="start"){k=u=A;p=q=v}else{u=A;q=v}}}}}function n(v){if(m.isBlock(v)&&!v.innerHTML&&!b){v.innerHTML='<br data-mce-bogus="1" />'}return v}if(o){if(o.start){g=m.createRng();r=m.getRoot();if(s.tridentSel){return s.tridentSel.moveToBookmark(o)}if(h(true)&&h()){s.setRng(g)}}else{if(o.id){l("start");l("end");if(k){g=m.createRng();g.setStart(n(k),p);g.setEnd(n(u),q);s.setRng(g)}}else{if(o.name){s.select(m.select(o.name)[o.index])}else{if(o.rng){s.setRng(o.rng)}}}}}},select:function(l,k){var j=this,m=j.dom,h=m.createRng(),g;function i(n,p){var o=new a(n,n);do{if(n.nodeType==3&&d.trim(n.nodeValue).length!==0){if(p){h.setStart(n,0)}else{h.setEnd(n,n.nodeValue.length)}return}if(n.nodeName=="BR"){if(p){h.setStartBefore(n)}else{h.setEndBefore(n)}return}}while(n=(p?o.next():o.prev()))}if(l){g=m.nodeIndex(l);h.setStart(l.parentNode,g);h.setEnd(l.parentNode,g+1);if(k){i(l,1);i(l)}j.setRng(h)}return l},isCollapsed:function(){var g=this,i=g.getRng(),h=g.getSel();if(!i||i.item){return false}if(i.compareEndPoints){return i.compareEndPoints("StartToEnd",i)===0}return !h||i.collapsed},collapse:function(g){var i=this,h=i.getRng(),j;if(h.item){j=h.item(0);h=i.win.document.body.createTextRange();h.moveToElementText(j)}h.collapse(!!g);i.setRng(h)},getSel:function(){var h=this,g=this.win;return g.getSelection?g.getSelection():g.document.selection},getRng:function(m){var h=this,i,j,l,k=h.win.document;if(m&&h.tridentSel){return h.tridentSel.getRangeAt(0)}try{if(i=h.getSel()){j=i.rangeCount>0?i.getRangeAt(0):(i.createRange?i.createRange():k.createRange())}}catch(g){}if(d.isIE&&j&&j.setStart&&k.selection.createRange().item){l=k.selection.createRange().item(0);j=k.createRange();j.setStartBefore(l);j.setEndAfter(l)}if(!j){j=k.createRange?k.createRange():k.body.createTextRange()}if(h.selectedRange&&h.explicitRange){if(j.compareBoundaryPoints(j.START_TO_START,h.selectedRange)===0&&j.compareBoundaryPoints(j.END_TO_END,h.selectedRange)===0){j=h.explicitRange}else{h.selectedRange=null;h.explicitRange=null}}return j},setRng:function(j){var i,h=this;if(!h.tridentSel){i=h.getSel();if(i){h.explicitRange=j;try{i.removeAllRanges()}catch(g){}i.addRange(j);h.selectedRange=i.rangeCount>0?i.getRangeAt(0):null}}else{if(j.cloneRange){try{h.tridentSel.addRange(j);return}catch(g){}}try{j.select()}catch(g){}}},setNode:function(h){var g=this;g.setContent(g.dom.getOuterHTML(h));return h},getNode:function(){var i=this,h=i.getRng(),j=i.getSel(),m,l=h.startContainer,g=h.endContainer;function k(q,o){var p=q;while(q&&q.nodeType===3&&q.length===0){q=o?q.nextSibling:q.previousSibling}return q||p}if(!h){return i.dom.getRoot()}if(h.setStart){m=h.commonAncestorContainer;if(!h.collapsed){if(h.startContainer==h.endContainer){if(h.endOffset-h.startOffset<2){if(h.startContainer.hasChildNodes()){m=h.startContainer.childNodes[h.startOffset]}}}if(l.nodeType===3&&g.nodeType===3){if(l.length===h.startOffset){l=k(l.nextSibling,true)}else{l=l.parentNode}if(h.endOffset===0){g=k(g.previousSibling,false)}else{g=g.parentNode}if(l&&l===g){return l}}}if(m&&m.nodeType==3){return m.parentNode}return m}return h.item?h.item(0):h.parentElement()},getSelectedBlocks:function(p,h){var o=this,k=o.dom,m,l,i,j=[];m=k.getParent(p||o.getStart(),k.isBlock);l=k.getParent(h||o.getEnd(),k.isBlock);if(m){j.push(m)}if(m&&l&&m!=l){i=m;var g=new a(m,k.getRoot());while((i=g.next())&&i!=l){if(k.isBlock(i)){j.push(i)}}}if(l&&m!=l){j.push(l)}return j},normalize:function(){var h=this,g,k,j;function i(n){var m,p,l,q=h.dom,s=q.getRoot(),o,r,u;function v(z,x){var A,y;x=x||m;A=new a(x,q.getParent(x.parentNode,q.isBlock)||s);while(o=A[z?"prev":"next"]()){if(o.nodeType===3&&o.nodeValue.length>0){m=o;p=z?o.nodeValue.length:0;k=true;return}if(q.isBlock(o)||r[o.nodeName.toLowerCase()]){return}y=o}if(j&&y){m=y;k=true;p=0}}m=g[(n?"start":"end")+"Container"];p=g[(n?"start":"end")+"Offset"];r=q.schema.getNonEmptyElements();if(m.nodeType===9){m=m.body;p=0}if(m===s){if(n){o=m.childNodes[p>0?p-1:0];if(o){u=o.nodeName.toLowerCase();if(r[o.nodeName]||o.nodeName=="TABLE"){return}}}if(m.hasChildNodes()){m=m.childNodes[Math.min(!n&&p>0?p-1:p,m.childNodes.length-1)];p=0;if(m.hasChildNodes()){o=m;l=new a(m,s);do{if(o.nodeType===3&&o.nodeValue.length>0){p=n?0:o.nodeValue.length;m=o;k=true;break}if(r[o.nodeName.toLowerCase()]){p=q.nodeIndex(o);m=o.parentNode;if(o.nodeName=="IMG"&&!n){p++}k=true;break}}while(o=(n?l.next():l.prev()))}}}if(j){if(m.nodeType===3&&p===0){v(true)}if(m.nodeType===1&&m.childNodes[p]&&m.childNodes[p].nodeName==="BR"){v(true,m.childNodes[p])}}if(n&&!j&&m.nodeType===3&&p===m.nodeValue.length){v(false)}if(k){g["set"+(n?"Start":"End")](m,p)}}if(d.isIE){return}g=h.getRng();j=g.collapsed;i(true);if(!j){i()}if(k){if(j){g.collapse(true)}h.setRng(g)}},destroy:function(h){var g=this;g.win=null;if(!h){d.removeUnload(g.destroy)}},_fixIESelection:function(){var h=this.dom,n=h.doc,i=n.body,k,o,g;function j(p,s){var q=i.createTextRange();try{q.moveToPoint(p,s)}catch(r){q=null}return q}function m(q){var p;if(q.button){p=j(q.x,q.y);if(p){if(p.compareEndPoints("StartToStart",o)>0){p.setEndPoint("StartToStart",o)}else{p.setEndPoint("EndToEnd",o)}p.select()}}else{l()}}function l(){var p=n.selection.createRange();if(o&&!p.item&&p.compareEndPoints("StartToEnd",p)===0){o.select()}h.unbind(n,"mouseup",l);h.unbind(n,"mousemove",m);o=k=0}n.documentElement.unselectable=true;h.bind(n,["mousedown","contextmenu"],function(p){if(p.target.nodeName==="HTML"){if(k){l()}g=n.documentElement;if(g.scrollHeight>g.clientHeight){return}k=1;o=j(p.x,p.y);if(o){h.bind(n,"mouseup",l);h.bind(n,"mousemove",m);h.win.focus();o.select()}}})}})})(tinymce);(function(a){a.dom.Serializer=function(e,i,f){var h,b,d=a.isIE,g=a.each,c;if(!e.apply_source_formatting){e.indent=false}i=i||a.DOM;f=f||new a.html.Schema(e);e.entity_encoding=e.entity_encoding||"named";e.remove_trailing_brs="remove_trailing_brs" in e?e.remove_trailing_brs:true;h=new a.util.Dispatcher(self);b=new a.util.Dispatcher(self);c=new a.html.DomParser(e,f);c.addAttributeFilter("src,href,style",function(k,j){var o=k.length,l,q,n="data-mce-"+j,p=e.url_converter,r=e.url_converter_scope,m;while(o--){l=k[o];q=l.attributes.map[n];if(q!==m){l.attr(j,q.length>0?q:null);l.attr(n,null)}else{q=l.attributes.map[j];if(j==="style"){q=i.serializeStyle(i.parseStyle(q),l.name)}else{if(p){q=p.call(r,q,j,l.name)}}l.attr(j,q.length>0?q:null)}}});c.addAttributeFilter("class",function(j,k){var l=j.length,m,n;while(l--){m=j[l];n=m.attr("class").replace(/\s*mce(Item\w+|Selected)\s*/g,"");m.attr("class",n.length>0?n:null)}});c.addAttributeFilter("data-mce-type",function(j,l,k){var m=j.length,n;while(m--){n=j[m];if(n.attributes.map["data-mce-type"]==="bookmark"&&!k.cleanup){n.remove()}}});c.addAttributeFilter("data-mce-expando",function(j,l,k){var m=j.length;while(m--){j[m].attr(l,null)}});c.addNodeFilter("script,style",function(k,l){var m=k.length,n,o;function j(p){return p.replace(/(<!--\[CDATA\[|\]\]-->)/g,"\n").replace(/^[\r\n]*|[\r\n]*$/g,"").replace(/^\s*((<!--)?(\s*\/\/)?\s*<!\[CDATA\[|(<!--\s*)?\/\*\s*<!\[CDATA\[\s*\*\/|(\/\/)?\s*<!--|\/\*\s*<!--\s*\*\/)\s*[\r\n]*/gi,"").replace(/\s*(\/\*\s*\]\]>\s*\*\/(-->)?|\s*\/\/\s*\]\]>(-->)?|\/\/\s*(-->)?|\]\]>|\/\*\s*-->\s*\*\/|\s*-->\s*)\s*$/g,"")}while(m--){n=k[m];o=n.firstChild?n.firstChild.value:"";if(l==="script"){n.attr("type",(n.attr("type")||"text/javascript").replace(/^mce\-/,""));if(o.length>0){n.firstChild.value="// <![CDATA[\n"+j(o)+"\n// ]]>"}}else{if(o.length>0){n.firstChild.value="<!--\n"+j(o)+"\n-->"}}}});c.addNodeFilter("#comment",function(j,k){var l=j.length,m;while(l--){m=j[l];if(m.value.indexOf("[CDATA[")===0){m.name="#cdata";m.type=4;m.value=m.value.replace(/^\[CDATA\[|\]\]$/g,"")}else{if(m.value.indexOf("mce:protected ")===0){m.name="#text";m.type=3;m.raw=true;m.value=unescape(m.value).substr(14)}}}});c.addNodeFilter("xml:namespace,input",function(j,k){var l=j.length,m;while(l--){m=j[l];if(m.type===7){m.remove()}else{if(m.type===1){if(k==="input"&&!("type" in m.attributes.map)){m.attr("type","text")}}}}});if(e.fix_list_elements){c.addNodeFilter("ul,ol",function(k,l){var m=k.length,n,j;while(m--){n=k[m];j=n.parent;if(j.name==="ul"||j.name==="ol"){if(n.prev&&n.prev.name==="li"){n.prev.append(n)}}}})}c.addAttributeFilter("data-mce-src,data-mce-href,data-mce-style",function(j,k){var l=j.length;while(l--){j[l].attr(k,null)}});return{schema:f,addNodeFilter:c.addNodeFilter,addAttributeFilter:c.addAttributeFilter,onPreProcess:h,onPostProcess:b,serialize:function(o,m){var l,p,k,j,n;if(d&&i.select("script,style,select,map").length>0){n=o.innerHTML;o=o.cloneNode(false);i.setHTML(o,n)}else{o=o.cloneNode(true)}l=o.ownerDocument.implementation;if(l.createHTMLDocument){p=l.createHTMLDocument("");g(o.nodeName=="BODY"?o.childNodes:[o],function(q){p.body.appendChild(p.importNode(q,true))});if(o.nodeName!="BODY"){o=p.body.firstChild}else{o=p.body}k=i.doc;i.doc=p}m=m||{};m.format=m.format||"html";if(!m.no_events){m.node=o;h.dispatch(self,m)}j=new a.html.Serializer(e,f);m.content=j.serialize(c.parse(a.trim(m.getInner?o.innerHTML:i.getOuterHTML(o)),m));if(!m.cleanup){m.content=m.content.replace(/\uFEFF|\u200B/g,"")}if(!m.no_events){b.dispatch(self,m)}if(k){i.doc=k}m.node=null;return m.content},addRules:function(j){f.addValidElements(j)},setRules:function(j){f.setValidElements(j)}}}})(tinymce);(function(a){a.dom.ScriptLoader=function(h){var c=0,k=1,i=2,l={},j=[],e={},d=[],g=0,f;function b(m,v){var x=this,q=a.DOM,s,o,r,n;function p(){q.remove(n);if(s){s.onreadystatechange=s.onload=s=null}v()}function u(){if(typeof(console)!=="undefined"&&console.log){console.log("Failed to load: "+m)}}n=q.uniqueId();if(a.isIE6){o=new a.util.URI(m);r=location;if(o.host==r.hostname&&o.port==r.port&&(o.protocol+":")==r.protocol&&o.protocol.toLowerCase()!="file"){a.util.XHR.send({url:a._addVer(o.getURI()),success:function(z){var y=q.create("script",{type:"text/javascript"});y.text=z;document.getElementsByTagName("head")[0].appendChild(y);q.remove(y);p()},error:u});return}}s=q.create("script",{id:n,type:"text/javascript",src:a._addVer(m)});if(!a.isIE){s.onload=p}s.onerror=u;if(!a.isOpera){s.onreadystatechange=function(){var y=s.readyState;if(y=="complete"||y=="loaded"){p()}}}(document.getElementsByTagName("head")[0]||document.body).appendChild(s)}this.isDone=function(m){return l[m]==i};this.markDone=function(m){l[m]=i};this.add=this.load=function(m,q,n){var o,p=l[m];if(p==f){j.push(m);l[m]=c}if(q){if(!e[m]){e[m]=[]}e[m].push({func:q,scope:n||this})}};this.loadQueue=function(n,m){this.loadScripts(j,n,m)};this.loadScripts=function(m,q,p){var o;function n(r){a.each(e[r],function(s){s.func.call(s.scope)});e[r]=f}d.push({func:q,scope:p||this});o=function(){var r=a.grep(m);m.length=0;a.each(r,function(s){if(l[s]==i){n(s);return}if(l[s]!=k){l[s]=k;g++;b(s,function(){l[s]=i;g--;n(s);o()})}});if(!g){a.each(d,function(s){s.func.call(s.scope)});d.length=0}};o()}};a.ScriptLoader=new a.dom.ScriptLoader()})(tinymce);(function(a){a.dom.RangeUtils=function(c){var b="\uFEFF";this.walk=function(d,s){var i=d.startContainer,l=d.startOffset,u=d.endContainer,m=d.endOffset,j,g,o,h,r,q,e;e=c.select("td.mceSelected,th.mceSelected");if(e.length>0){a.each(e,function(v){s([v])});return}function f(v){var x;x=v[0];if(x.nodeType===3&&x===i&&l>=x.nodeValue.length){v.splice(0,1)}x=v[v.length-1];if(m===0&&v.length>0&&x===u&&x.nodeType===3){v.splice(v.length-1,1)}return v}function p(y,x,v){var z=[];for(;y&&y!=v;y=y[x]){z.push(y)}return z}function n(x,v){do{if(x.parentNode==v){return x}x=x.parentNode}while(x)}function k(y,x,z){var v=z?"nextSibling":"previousSibling";for(h=y,r=h.parentNode;h&&h!=x;h=r){r=h.parentNode;q=p(h==y?h:h[v],v);if(q.length){if(!z){q.reverse()}s(f(q))}}}if(i.nodeType==1&&i.hasChildNodes()){i=i.childNodes[l]}if(u.nodeType==1&&u.hasChildNodes()){u=u.childNodes[Math.min(m-1,u.childNodes.length-1)]}if(i==u){return s(f([i]))}j=c.findCommonAncestor(i,u);for(h=i;h;h=h.parentNode){if(h===u){return k(i,j,true)}if(h===j){break}}for(h=u;h;h=h.parentNode){if(h===i){return k(u,j)}if(h===j){break}}g=n(i,j)||i;o=n(u,j)||u;k(i,g,true);q=p(g==i?g:g.nextSibling,"nextSibling",o==u?o.nextSibling:o);if(q.length){s(f(q))}k(u,o)};this.split=function(e){var h=e.startContainer,d=e.startOffset,i=e.endContainer,g=e.endOffset;function f(j,k){return j.splitText(k)}if(h==i&&h.nodeType==3){if(d>0&&d<h.nodeValue.length){i=f(h,d);h=i.previousSibling;if(g>d){g=g-d;h=i=f(i,g).previousSibling;g=i.nodeValue.length;d=0}else{g=0}}}else{if(h.nodeType==3&&d>0&&d<h.nodeValue.length){h=f(h,d);d=0}if(i.nodeType==3&&g>0&&g<i.nodeValue.length){i=f(i,g).previousSibling;g=i.nodeValue.length}}return{startContainer:h,startOffset:d,endContainer:i,endOffset:g}}};a.dom.RangeUtils.compareRanges=function(c,b){if(c&&b){if(c.item||c.duplicate){if(c.item&&b.item&&c.item(0)===b.item(0)){return true}if(c.isEqual&&b.isEqual&&b.isEqual(c)){return true}}else{return c.startContainer==b.startContainer&&c.startOffset==b.startOffset}}return false}})(tinymce);(function(b){var a=b.dom.Event,c=b.each;b.create("tinymce.ui.KeyboardNavigation",{KeyboardNavigation:function(e,f){var p=this,m=e.root,l=e.items,n=e.enableUpDown,i=e.enableLeftRight||!e.enableUpDown,k=e.excludeFromTabOrder,j,h,o,d,g;f=f||b.DOM;j=function(q){g=q.target.id};h=function(q){f.setAttrib(q.target.id,"tabindex","-1")};d=function(q){var r=f.get(g);f.setAttrib(r,"tabindex","0");r.focus()};p.focus=function(){f.get(g).focus()};p.destroy=function(){c(l,function(q){f.unbind(f.get(q.id),"focus",j);f.unbind(f.get(q.id),"blur",h)});f.unbind(f.get(m),"focus",d);f.unbind(f.get(m),"keydown",o);l=f=m=p.focus=j=h=o=d=null;p.destroy=function(){}};p.moveFocus=function(v,r){var q=-1,u=p.controls,s;if(!g){return}c(l,function(y,x){if(y.id===g){q=x;return false}});q+=v;if(q<0){q=l.length-1}else{if(q>=l.length){q=0}}s=l[q];f.setAttrib(g,"tabindex","-1");f.setAttrib(s.id,"tabindex","0");f.get(s.id).focus();if(e.actOnFocus){e.onAction(s.id)}if(r){a.cancel(r)}};o=function(z){var v=37,u=39,y=38,A=40,q=27,s=14,r=13,x=32;switch(z.keyCode){case v:if(i){p.moveFocus(-1)}break;case u:if(i){p.moveFocus(1)}break;case y:if(n){p.moveFocus(-1)}break;case A:if(n){p.moveFocus(1)}break;case q:if(e.onCancel){e.onCancel();a.cancel(z)}break;case s:case r:case x:if(e.onAction){e.onAction(g);a.cancel(z)}break}};c(l,function(s,q){var r;if(!s.id){s.id=f.uniqueId("_mce_item_")}if(k){f.bind(s.id,"blur",h);r="-1"}else{r=(q===0?"0":"-1")}f.setAttrib(s.id,"tabindex",r);f.bind(f.get(s.id),"focus",j)});if(l[0]){g=l[0].id}f.setAttrib(m,"tabindex","-1");f.bind(f.get(m),"focus",d);f.bind(f.get(m),"keydown",o)}})})(tinymce);(function(c){var b=c.DOM,a=c.is;c.create("tinymce.ui.Control",{Control:function(f,e,d){this.id=f;this.settings=e=e||{};this.rendered=false;this.onRender=new c.util.Dispatcher(this);this.classPrefix="";this.scope=e.scope||this;this.disabled=0;this.active=0;this.editor=d},setAriaProperty:function(f,e){var d=b.get(this.id+"_aria")||b.get(this.id);if(d){b.setAttrib(d,"aria-"+f,!!e)}},focus:function(){b.get(this.id).focus()},setDisabled:function(d){if(d!=this.disabled){this.setAriaProperty("disabled",d);this.setState("Disabled",d);this.setState("Enabled",!d);this.disabled=d}},isDisabled:function(){return this.disabled},setActive:function(d){if(d!=this.active){this.setState("Active",d);this.active=d;this.setAriaProperty("pressed",d)}},isActive:function(){return this.active},setState:function(f,d){var e=b.get(this.id);f=this.classPrefix+f;if(d){b.addClass(e,f)}else{b.removeClass(e,f)}},isRendered:function(){return this.rendered},renderHTML:function(){},renderTo:function(d){b.setHTML(d,this.renderHTML())},postRender:function(){var e=this,d;if(a(e.disabled)){d=e.disabled;e.disabled=-1;e.setDisabled(d)}if(a(e.active)){d=e.active;e.active=-1;e.setActive(d)}},remove:function(){b.remove(this.id);this.destroy()},destroy:function(){c.dom.Event.clear(this.id)}})})(tinymce);tinymce.create("tinymce.ui.Container:tinymce.ui.Control",{Container:function(c,b,a){this.parent(c,b,a);this.controls=[];this.lookup={}},add:function(a){this.lookup[a.id]=a;this.controls.push(a);return a},get:function(a){return this.lookup[a]}});tinymce.create("tinymce.ui.Separator:tinymce.ui.Control",{Separator:function(b,a){this.parent(b,a);this.classPrefix="mceSeparator";this.setDisabled(true)},renderHTML:function(){return tinymce.DOM.createHTML("span",{"class":this.classPrefix,role:"separator","aria-orientation":"vertical",tabindex:"-1"})}});(function(d){var c=d.is,b=d.DOM,e=d.each,a=d.walk;d.create("tinymce.ui.MenuItem:tinymce.ui.Control",{MenuItem:function(g,f){this.parent(g,f);this.classPrefix="mceMenuItem"},setSelected:function(f){this.setState("Selected",f);this.setAriaProperty("checked",!!f);this.selected=f},isSelected:function(){return this.selected},postRender:function(){var f=this;f.parent();if(c(f.selected)){f.setSelected(f.selected)}}})})(tinymce);(function(d){var c=d.is,b=d.DOM,e=d.each,a=d.walk;d.create("tinymce.ui.Menu:tinymce.ui.MenuItem",{Menu:function(h,g){var f=this;f.parent(h,g);f.items={};f.collapsed=false;f.menuCount=0;f.onAddItem=new d.util.Dispatcher(this)},expand:function(g){var f=this;if(g){a(f,function(h){if(h.expand){h.expand()}},"items",f)}f.collapsed=false},collapse:function(g){var f=this;if(g){a(f,function(h){if(h.collapse){h.collapse()}},"items",f)}f.collapsed=true},isCollapsed:function(){return this.collapsed},add:function(f){if(!f.settings){f=new d.ui.MenuItem(f.id||b.uniqueId(),f)}this.onAddItem.dispatch(this,f);return this.items[f.id]=f},addSeparator:function(){return this.add({separator:true})},addMenu:function(f){if(!f.collapse){f=this.createMenu(f)}this.menuCount++;return this.add(f)},hasMenus:function(){return this.menuCount!==0},remove:function(f){delete this.items[f.id]},removeAll:function(){var f=this;a(f,function(g){if(g.removeAll){g.removeAll()}else{g.remove()}g.destroy()},"items",f);f.items={}},createMenu:function(g){var f=new d.ui.Menu(g.id||b.uniqueId(),g);f.onAddItem.add(this.onAddItem.dispatch,this.onAddItem);return f}})})(tinymce);(function(e){var d=e.is,c=e.DOM,f=e.each,a=e.dom.Event,b=e.dom.Element;e.create("tinymce.ui.DropMenu:tinymce.ui.Menu",{DropMenu:function(h,g){g=g||{};g.container=g.container||c.doc.body;g.offset_x=g.offset_x||0;g.offset_y=g.offset_y||0;g.vp_offset_x=g.vp_offset_x||0;g.vp_offset_y=g.vp_offset_y||0;if(d(g.icons)&&!g.icons){g["class"]+=" mceNoIcons"}this.parent(h,g);this.onShowMenu=new e.util.Dispatcher(this);this.onHideMenu=new e.util.Dispatcher(this);this.classPrefix="mceMenu"},createMenu:function(j){var h=this,i=h.settings,g;j.container=j.container||i.container;j.parent=h;j.constrain=j.constrain||i.constrain;j["class"]=j["class"]||i["class"];j.vp_offset_x=j.vp_offset_x||i.vp_offset_x;j.vp_offset_y=j.vp_offset_y||i.vp_offset_y;j.keyboard_focus=i.keyboard_focus;g=new e.ui.DropMenu(j.id||c.uniqueId(),j);g.onAddItem.add(h.onAddItem.dispatch,h.onAddItem);return g},focus:function(){var g=this;if(g.keyboardNav){g.keyboardNav.focus()}},update:function(){var i=this,j=i.settings,g=c.get("menu_"+i.id+"_tbl"),l=c.get("menu_"+i.id+"_co"),h,k;h=j.max_width?Math.min(g.clientWidth,j.max_width):g.clientWidth;k=j.max_height?Math.min(g.clientHeight,j.max_height):g.clientHeight;if(!c.boxModel){i.element.setStyles({width:h+2,height:k+2})}else{i.element.setStyles({width:h,height:k})}if(j.max_width){c.setStyle(l,"width",h)}if(j.max_height){c.setStyle(l,"height",k);if(g.clientHeight<j.max_height){c.setStyle(l,"overflow","hidden")}}},showMenu:function(p,n,r){var z=this,A=z.settings,o,g=c.getViewPort(),u,l,v,q,i=2,k,j,m=z.classPrefix;z.collapse(1);if(z.isMenuVisible){return}if(!z.rendered){o=c.add(z.settings.container,z.renderNode());f(z.items,function(h){h.postRender()});z.element=new b("menu_"+z.id,{blocker:1,container:A.container})}else{o=c.get("menu_"+z.id)}if(!e.isOpera){c.setStyles(o,{left:-65535,top:-65535})}c.show(o);z.update();p+=A.offset_x||0;n+=A.offset_y||0;g.w-=4;g.h-=4;if(A.constrain){u=o.clientWidth-i;l=o.clientHeight-i;v=g.x+g.w;q=g.y+g.h;if((p+A.vp_offset_x+u)>v){p=r?r-u:Math.max(0,(v-A.vp_offset_x)-u)}if((n+A.vp_offset_y+l)>q){n=Math.max(0,(q-A.vp_offset_y)-l)}}c.setStyles(o,{left:p,top:n});z.element.update();z.isMenuVisible=1;z.mouseClickFunc=a.add(o,"click",function(s){var h;s=s.target;if(s&&(s=c.getParent(s,"tr"))&&!c.hasClass(s,m+"ItemSub")){h=z.items[s.id];if(h.isDisabled()){return}k=z;while(k){if(k.hideMenu){k.hideMenu()}k=k.settings.parent}if(h.settings.onclick){h.settings.onclick(s)}return false}});if(z.hasMenus()){z.mouseOverFunc=a.add(o,"mouseover",function(y){var h,x,s;y=y.target;if(y&&(y=c.getParent(y,"tr"))){h=z.items[y.id];if(z.lastMenu){z.lastMenu.collapse(1)}if(h.isDisabled()){return}if(y&&c.hasClass(y,m+"ItemSub")){x=c.getRect(y);h.showMenu((x.x+x.w-i),x.y-i,x.x);z.lastMenu=h;c.addClass(c.get(h.id).firstChild,m+"ItemActive")}}})}a.add(o,"keydown",z._keyHandler,z);z.onShowMenu.dispatch(z);if(A.keyboard_focus){z._setupKeyboardNav()}},hideMenu:function(j){var g=this,i=c.get("menu_"+g.id),h;if(!g.isMenuVisible){return}if(g.keyboardNav){g.keyboardNav.destroy()}a.remove(i,"mouseover",g.mouseOverFunc);a.remove(i,"click",g.mouseClickFunc);a.remove(i,"keydown",g._keyHandler);c.hide(i);g.isMenuVisible=0;if(!j){g.collapse(1)}if(g.element){g.element.hide()}if(h=c.get(g.id)){c.removeClass(h.firstChild,g.classPrefix+"ItemActive")}g.onHideMenu.dispatch(g)},add:function(i){var g=this,h;i=g.parent(i);if(g.isRendered&&(h=c.get("menu_"+g.id))){g._add(c.select("tbody",h)[0],i)}return i},collapse:function(g){this.parent(g);this.hideMenu(1)},remove:function(g){c.remove(g.id);this.destroy();return this.parent(g)},destroy:function(){var g=this,h=c.get("menu_"+g.id);if(g.keyboardNav){g.keyboardNav.destroy()}a.remove(h,"mouseover",g.mouseOverFunc);a.remove(c.select("a",h),"focus",g.mouseOverFunc);a.remove(h,"click",g.mouseClickFunc);a.remove(h,"keydown",g._keyHandler);if(g.element){g.element.remove()}c.remove(h)},renderNode:function(){var i=this,j=i.settings,l,h,k,g;g=c.create("div",{role:"listbox",id:"menu_"+i.id,"class":j["class"],style:"position:absolute;left:0;top:0;z-index:200000;outline:0"});if(i.settings.parent){c.setAttrib(g,"aria-parent","menu_"+i.settings.parent.id)}k=c.add(g,"div",{role:"presentation",id:"menu_"+i.id+"_co","class":i.classPrefix+(j["class"]?" "+j["class"]:"")});i.element=new b("menu_"+i.id,{blocker:1,container:j.container});if(j.menu_line){c.add(k,"span",{"class":i.classPrefix+"Line"})}l=c.add(k,"table",{role:"presentation",id:"menu_"+i.id+"_tbl",border:0,cellPadding:0,cellSpacing:0});h=c.add(l,"tbody");f(i.items,function(m){i._add(h,m)});i.rendered=true;return g},_setupKeyboardNav:function(){var i,h,g=this;i=c.get("menu_"+g.id);h=c.select("a[role=option]","menu_"+g.id);h.splice(0,0,i);g.keyboardNav=new e.ui.KeyboardNavigation({root:"menu_"+g.id,items:h,onCancel:function(){g.hideMenu()},enableUpDown:true});i.focus()},_keyHandler:function(g){var h=this,i;switch(g.keyCode){case 37:if(h.settings.parent){h.hideMenu();h.settings.parent.focus();a.cancel(g)}break;case 39:if(h.mouseOverFunc){h.mouseOverFunc(g)}break}},_add:function(j,h){var i,q=h.settings,p,l,k,m=this.classPrefix,g;if(q.separator){l=c.add(j,"tr",{id:h.id,"class":m+"ItemSeparator"});c.add(l,"td",{"class":m+"ItemSeparator"});if(i=l.previousSibling){c.addClass(i,"mceLast")}return}i=l=c.add(j,"tr",{id:h.id,"class":m+"Item "+m+"ItemEnabled"});i=k=c.add(i,q.titleItem?"th":"td");i=p=c.add(i,"a",{id:h.id+"_aria",role:q.titleItem?"presentation":"option",href:"javascript:;",onclick:"return false;",onmousedown:"return false;"});if(q.parent){c.setAttrib(p,"aria-haspopup","true");c.setAttrib(p,"aria-owns","menu_"+h.id)}c.addClass(k,q["class"]);g=c.add(i,"span",{"class":"mceIcon"+(q.icon?" mce_"+q.icon:"")});if(q.icon_src){c.add(g,"img",{src:q.icon_src})}i=c.add(i,q.element||"span",{"class":"mceText",title:h.settings.title},h.settings.title);if(h.settings.style){if(typeof h.settings.style=="function"){h.settings.style=h.settings.style()}c.setAttrib(i,"style",h.settings.style)}if(j.childNodes.length==1){c.addClass(l,"mceFirst")}if((i=l.previousSibling)&&c.hasClass(i,m+"ItemSeparator")){c.addClass(l,"mceFirst")}if(h.collapse){c.addClass(l,m+"ItemSub")}if(i=l.previousSibling){c.removeClass(i,"mceLast")}c.addClass(l,"mceLast")}})})(tinymce);(function(b){var a=b.DOM;b.create("tinymce.ui.Button:tinymce.ui.Control",{Button:function(e,d,c){this.parent(e,d,c);this.classPrefix="mceButton"},renderHTML:function(){var f=this.classPrefix,e=this.settings,d,c;c=a.encode(e.label||"");d='<a role="button" id="'+this.id+'" href="javascript:;" class="'+f+" "+f+"Enabled "+e["class"]+(c?" "+f+"Labeled":"")+'" onmousedown="return false;" onclick="return false;" aria-labelledby="'+this.id+'_voice" title="'+a.encode(e.title)+'">';if(e.image&&!(this.editor&&this.editor.forcedHighContrastMode)){d+='<img class="mceIcon" src="'+e.image+'" alt="'+a.encode(e.title)+'" />'+c}else{d+='<span class="mceIcon '+e["class"]+'"></span>'+(c?'<span class="'+f+'Label">'+c+"</span>":"")}d+='<span class="mceVoiceLabel mceIconOnly" style="display: none;" id="'+this.id+'_voice">'+e.title+"</span>";d+="</a>";return d},postRender:function(){var d=this,e=d.settings,c;if(b.isIE&&d.editor){b.dom.Event.add(d.id,"mousedown",function(f){var g=d.editor.selection.getNode().nodeName;c=g==="IMG"?d.editor.selection.getBookmark():null})}b.dom.Event.add(d.id,"click",function(f){if(!d.isDisabled()){if(b.isIE&&d.editor&&c!==null){d.editor.selection.moveToBookmark(c)}return e.onclick.call(e.scope,f)}});b.dom.Event.add(d.id,"keyup",function(f){if(!d.isDisabled()&&f.keyCode==b.VK.SPACEBAR){return e.onclick.call(e.scope,f)}})}})})(tinymce);(function(e){var d=e.DOM,b=e.dom.Event,f=e.each,a=e.util.Dispatcher,c;e.create("tinymce.ui.ListBox:tinymce.ui.Control",{ListBox:function(j,i,g){var h=this;h.parent(j,i,g);h.items=[];h.onChange=new a(h);h.onPostRender=new a(h);h.onAdd=new a(h);h.onRenderMenu=new e.util.Dispatcher(this);h.classPrefix="mceListBox";h.marked={}},select:function(h){var g=this,j,i;g.marked={};if(h==c){return g.selectByIndex(-1)}if(h&&typeof(h)=="function"){i=h}else{i=function(k){return k==h}}if(h!=g.selectedValue){f(g.items,function(l,k){if(i(l.value)){j=1;g.selectByIndex(k);return false}});if(!j){g.selectByIndex(-1)}}},selectByIndex:function(g){var i=this,j,k,h;i.marked={};if(g!=i.selectedIndex){j=d.get(i.id+"_text");h=d.get(i.id+"_voiceDesc");k=i.items[g];if(k){i.selectedValue=k.value;i.selectedIndex=g;d.setHTML(j,d.encode(k.title));d.setHTML(h,i.settings.title+" - "+k.title);d.removeClass(j,"mceTitle");d.setAttrib(i.id,"aria-valuenow",k.title)}else{d.setHTML(j,d.encode(i.settings.title));d.setHTML(h,d.encode(i.settings.title));d.addClass(j,"mceTitle");i.selectedValue=i.selectedIndex=null;d.setAttrib(i.id,"aria-valuenow",i.settings.title)}j=0}},mark:function(g){this.marked[g]=true},add:function(j,g,i){var h=this;i=i||{};i=e.extend(i,{title:j,value:g});h.items.push(i);h.onAdd.dispatch(h,i)},getLength:function(){return this.items.length},renderHTML:function(){var j="",g=this,i=g.settings,k=g.classPrefix;j='<span role="listbox" aria-haspopup="true" aria-labelledby="'+g.id+'_voiceDesc" aria-describedby="'+g.id+'_voiceDesc"><table role="presentation" tabindex="0" id="'+g.id+'" cellpadding="0" cellspacing="0" class="'+k+" "+k+"Enabled"+(i["class"]?(" "+i["class"]):"")+'"><tbody><tr>';j+="<td>"+d.createHTML("span",{id:g.id+"_voiceDesc","class":"voiceLabel",style:"display:none;"},g.settings.title);j+=d.createHTML("a",{id:g.id+"_text",tabindex:-1,href:"javascript:;","class":"mceText",onclick:"return false;",onmousedown:"return false;"},d.encode(g.settings.title))+"</td>";j+="<td>"+d.createHTML("a",{id:g.id+"_open",tabindex:-1,href:"javascript:;","class":"mceOpen",onclick:"return false;",onmousedown:"return false;"},'<span><span style="display:none;" class="mceIconOnly" aria-hidden="true">\u25BC</span></span>')+"</td>";j+="</tr></tbody></table></span>";return j},showMenu:function(){var h=this,j,i=d.get(this.id),g;if(h.isDisabled()||h.items.length===0){return}if(h.menu&&h.menu.isMenuVisible){return h.hideMenu()}if(!h.isMenuRendered){h.renderMenu();h.isMenuRendered=true}j=d.getPos(i);g=h.menu;g.settings.offset_x=j.x;g.settings.offset_y=j.y;g.settings.keyboard_focus=!e.isOpera;f(h.items,function(k){if(g.items[k.id]){g.items[k.id].setSelected(0)}});f(h.items,function(k){if(g.items[k.id]&&h.marked[k.value]){g.items[k.id].setSelected(1)}if(k.value===h.selectedValue){g.items[k.id].setSelected(1)}});g.showMenu(0,i.clientHeight);b.add(d.doc,"mousedown",h.hideMenu,h);d.addClass(h.id,h.classPrefix+"Selected")},hideMenu:function(h){var g=this;if(g.menu&&g.menu.isMenuVisible){d.removeClass(g.id,g.classPrefix+"Selected");if(h&&h.type=="mousedown"&&(h.target.id==g.id+"_text"||h.target.id==g.id+"_open")){return}if(!h||!d.getParent(h.target,".mceMenu")){d.removeClass(g.id,g.classPrefix+"Selected");b.remove(d.doc,"mousedown",g.hideMenu,g);g.menu.hideMenu()}}},renderMenu:function(){var h=this,g;g=h.settings.control_manager.createDropMenu(h.id+"_menu",{menu_line:1,"class":h.classPrefix+"Menu mceNoIcons",max_width:150,max_height:150});g.onHideMenu.add(function(){h.hideMenu();h.focus()});g.add({title:h.settings.title,"class":"mceMenuItemTitle",onclick:function(){if(h.settings.onselect("")!==false){h.select("")}}});f(h.items,function(i){if(i.value===c){g.add({title:i.title,role:"option","class":"mceMenuItemTitle",onclick:function(){if(h.settings.onselect("")!==false){h.select("")}}})}else{i.id=d.uniqueId();i.role="option";i.onclick=function(){if(h.settings.onselect(i.value)!==false){h.select(i.value)}};g.add(i)}});h.onRenderMenu.dispatch(h,g);h.menu=g},postRender:function(){var g=this,h=g.classPrefix;b.add(g.id,"click",g.showMenu,g);b.add(g.id,"keydown",function(i){if(i.keyCode==32){g.showMenu(i);b.cancel(i)}});b.add(g.id,"focus",function(){if(!g._focused){g.keyDownHandler=b.add(g.id,"keydown",function(i){if(i.keyCode==40){g.showMenu();b.cancel(i)}});g.keyPressHandler=b.add(g.id,"keypress",function(j){var i;if(j.keyCode==13){i=g.selectedValue;g.selectedValue=null;b.cancel(j);g.settings.onselect(i)}})}g._focused=1});b.add(g.id,"blur",function(){b.remove(g.id,"keydown",g.keyDownHandler);b.remove(g.id,"keypress",g.keyPressHandler);g._focused=0});if(e.isIE6||!d.boxModel){b.add(g.id,"mouseover",function(){if(!d.hasClass(g.id,h+"Disabled")){d.addClass(g.id,h+"Hover")}});b.add(g.id,"mouseout",function(){if(!d.hasClass(g.id,h+"Disabled")){d.removeClass(g.id,h+"Hover")}})}g.onPostRender.dispatch(g,d.get(g.id))},destroy:function(){this.parent();b.clear(this.id+"_text");b.clear(this.id+"_open")}})})(tinymce);(function(e){var d=e.DOM,b=e.dom.Event,f=e.each,a=e.util.Dispatcher,c;e.create("tinymce.ui.NativeListBox:tinymce.ui.ListBox",{NativeListBox:function(h,g){this.parent(h,g);this.classPrefix="mceNativeListBox"},setDisabled:function(g){d.get(this.id).disabled=g;this.setAriaProperty("disabled",g)},isDisabled:function(){return d.get(this.id).disabled},select:function(h){var g=this,j,i;if(h==c){return g.selectByIndex(-1)}if(h&&typeof(h)=="function"){i=h}else{i=function(k){return k==h}}if(h!=g.selectedValue){f(g.items,function(l,k){if(i(l.value)){j=1;g.selectByIndex(k);return false}});if(!j){g.selectByIndex(-1)}}},selectByIndex:function(g){d.get(this.id).selectedIndex=g+1;this.selectedValue=this.items[g]?this.items[g].value:null},add:function(k,h,g){var j,i=this;g=g||{};g.value=h;if(i.isRendered()){d.add(d.get(this.id),"option",g,k)}j={title:k,value:h,attribs:g};i.items.push(j);i.onAdd.dispatch(i,j)},getLength:function(){return this.items.length},renderHTML:function(){var i,g=this;i=d.createHTML("option",{value:""},"-- "+g.settings.title+" --");f(g.items,function(h){i+=d.createHTML("option",{value:h.value},h.title)});i=d.createHTML("select",{id:g.id,"class":"mceNativeListBox","aria-labelledby":g.id+"_aria"},i);i+=d.createHTML("span",{id:g.id+"_aria",style:"display: none"},g.settings.title);return i},postRender:function(){var h=this,i,j=true;h.rendered=true;function g(l){var k=h.items[l.target.selectedIndex-1];if(k&&(k=k.value)){h.onChange.dispatch(h,k);if(h.settings.onselect){h.settings.onselect(k)}}}b.add(h.id,"change",g);b.add(h.id,"keydown",function(l){var k;b.remove(h.id,"change",i);j=false;k=b.add(h.id,"blur",function(){if(j){return}j=true;b.add(h.id,"change",g);b.remove(h.id,"blur",k)});if(e.isWebKit&&(l.keyCode==37||l.keyCode==39)){return b.prevent(l)}if(l.keyCode==13||l.keyCode==32){g(l);return b.cancel(l)}});h.onPostRender.dispatch(h,d.get(h.id))}})})(tinymce);(function(c){var b=c.DOM,a=c.dom.Event,d=c.each;c.create("tinymce.ui.MenuButton:tinymce.ui.Button",{MenuButton:function(g,f,e){this.parent(g,f,e);this.onRenderMenu=new c.util.Dispatcher(this);f.menu_container=f.menu_container||b.doc.body},showMenu:function(){var g=this,j,i,h=b.get(g.id),f;if(g.isDisabled()){return}if(!g.isMenuRendered){g.renderMenu();g.isMenuRendered=true}if(g.isMenuVisible){return g.hideMenu()}j=b.getPos(g.settings.menu_container);i=b.getPos(h);f=g.menu;f.settings.offset_x=i.x;f.settings.offset_y=i.y;f.settings.vp_offset_x=i.x;f.settings.vp_offset_y=i.y;f.settings.keyboard_focus=g._focused;f.showMenu(0,h.firstChild.clientHeight);a.add(b.doc,"mousedown",g.hideMenu,g);g.setState("Selected",1);g.isMenuVisible=1},renderMenu:function(){var f=this,e;e=f.settings.control_manager.createDropMenu(f.id+"_menu",{menu_line:1,"class":this.classPrefix+"Menu",icons:f.settings.icons});e.onHideMenu.add(function(){f.hideMenu();f.focus()});f.onRenderMenu.dispatch(f,e);f.menu=e},hideMenu:function(g){var f=this;if(g&&g.type=="mousedown"&&b.getParent(g.target,function(h){return h.id===f.id||h.id===f.id+"_open"})){return}if(!g||!b.getParent(g.target,".mceMenu")){f.setState("Selected",0);a.remove(b.doc,"mousedown",f.hideMenu,f);if(f.menu){f.menu.hideMenu()}}f.isMenuVisible=0},postRender:function(){var e=this,f=e.settings;a.add(e.id,"click",function(){if(!e.isDisabled()){if(f.onclick){f.onclick(e.value)}e.showMenu()}})}})})(tinymce);(function(c){var b=c.DOM,a=c.dom.Event,d=c.each;c.create("tinymce.ui.SplitButton:tinymce.ui.MenuButton",{SplitButton:function(g,f,e){this.parent(g,f,e);this.classPrefix="mceSplitButton"},renderHTML:function(){var i,f=this,g=f.settings,e;i="<tbody><tr>";if(g.image){e=b.createHTML("img ",{src:g.image,role:"presentation","class":"mceAction "+g["class"]})}else{e=b.createHTML("span",{"class":"mceAction "+g["class"]},"")}e+=b.createHTML("span",{"class":"mceVoiceLabel mceIconOnly",id:f.id+"_voice",style:"display:none;"},g.title);i+="<td >"+b.createHTML("a",{role:"button",id:f.id+"_action",tabindex:"-1",href:"javascript:;","class":"mceAction "+g["class"],onclick:"return false;",onmousedown:"return false;",title:g.title},e)+"</td>";e=b.createHTML("span",{"class":"mceOpen "+g["class"]},'<span style="display:none;" class="mceIconOnly" aria-hidden="true">\u25BC</span>');i+="<td >"+b.createHTML("a",{role:"button",id:f.id+"_open",tabindex:"-1",href:"javascript:;","class":"mceOpen "+g["class"],onclick:"return false;",onmousedown:"return false;",title:g.title},e)+"</td>";i+="</tr></tbody>";i=b.createHTML("table",{role:"presentation","class":"mceSplitButton mceSplitButtonEnabled "+g["class"],cellpadding:"0",cellspacing:"0",title:g.title},i);return b.createHTML("div",{id:f.id,role:"button",tabindex:"0","aria-labelledby":f.id+"_voice","aria-haspopup":"true"},i)},postRender:function(){var e=this,g=e.settings,f;if(g.onclick){f=function(h){if(!e.isDisabled()){g.onclick(e.value);a.cancel(h)}};a.add(e.id+"_action","click",f);a.add(e.id,["click","keydown"],function(h){var k=32,m=14,i=13,j=38,l=40;if((h.keyCode===32||h.keyCode===13||h.keyCode===14)&&!h.altKey&&!h.ctrlKey&&!h.metaKey){f();a.cancel(h)}else{if(h.type==="click"||h.keyCode===l){e.showMenu();a.cancel(h)}}})}a.add(e.id+"_open","click",function(h){e.showMenu();a.cancel(h)});a.add([e.id,e.id+"_open"],"focus",function(){e._focused=1});a.add([e.id,e.id+"_open"],"blur",function(){e._focused=0});if(c.isIE6||!b.boxModel){a.add(e.id,"mouseover",function(){if(!b.hasClass(e.id,"mceSplitButtonDisabled")){b.addClass(e.id,"mceSplitButtonHover")}});a.add(e.id,"mouseout",function(){if(!b.hasClass(e.id,"mceSplitButtonDisabled")){b.removeClass(e.id,"mceSplitButtonHover")}})}},destroy:function(){this.parent();a.clear(this.id+"_action");a.clear(this.id+"_open");a.clear(this.id)}})})(tinymce);(function(d){var c=d.DOM,a=d.dom.Event,b=d.is,e=d.each;d.create("tinymce.ui.ColorSplitButton:tinymce.ui.SplitButton",{ColorSplitButton:function(i,h,f){var g=this;g.parent(i,h,f);g.settings=h=d.extend({colors:"000000,993300,333300,003300,003366,000080,333399,333333,800000,FF6600,808000,008000,008080,0000FF,666699,808080,FF0000,FF9900,99CC00,339966,33CCCC,3366FF,800080,999999,FF00FF,FFCC00,FFFF00,00FF00,00FFFF,00CCFF,993366,C0C0C0,FF99CC,FFCC99,FFFF99,CCFFCC,CCFFFF,99CCFF,CC99FF,FFFFFF",grid_width:8,default_color:"#888888"},g.settings);g.onShowMenu=new d.util.Dispatcher(g);g.onHideMenu=new d.util.Dispatcher(g);g.value=h.default_color},showMenu:function(){var f=this,g,j,i,h;if(f.isDisabled()){return}if(!f.isMenuRendered){f.renderMenu();f.isMenuRendered=true}if(f.isMenuVisible){return f.hideMenu()}i=c.get(f.id);c.show(f.id+"_menu");c.addClass(i,"mceSplitButtonSelected");h=c.getPos(i);c.setStyles(f.id+"_menu",{left:h.x,top:h.y+i.firstChild.clientHeight,zIndex:200000});i=0;a.add(c.doc,"mousedown",f.hideMenu,f);f.onShowMenu.dispatch(f);if(f._focused){f._keyHandler=a.add(f.id+"_menu","keydown",function(k){if(k.keyCode==27){f.hideMenu()}});c.select("a",f.id+"_menu")[0].focus()}f.isMenuVisible=1},hideMenu:function(g){var f=this;if(f.isMenuVisible){if(g&&g.type=="mousedown"&&c.getParent(g.target,function(h){return h.id===f.id+"_open"})){return}if(!g||!c.getParent(g.target,".mceSplitButtonMenu")){c.removeClass(f.id,"mceSplitButtonSelected");a.remove(c.doc,"mousedown",f.hideMenu,f);a.remove(f.id+"_menu","keydown",f._keyHandler);c.hide(f.id+"_menu")}f.isMenuVisible=0;f.onHideMenu.dispatch()}},renderMenu:function(){var p=this,h,k=0,q=p.settings,g,j,l,o,f;o=c.add(q.menu_container,"div",{role:"listbox",id:p.id+"_menu","class":q.menu_class+" "+q["class"],style:"position:absolute;left:0;top:-1000px;"});h=c.add(o,"div",{"class":q["class"]+" mceSplitButtonMenu"});c.add(h,"span",{"class":"mceMenuLine"});g=c.add(h,"table",{role:"presentation","class":"mceColorSplitMenu"});j=c.add(g,"tbody");k=0;e(b(q.colors,"array")?q.colors:q.colors.split(","),function(m){m=m.replace(/^#/,"");if(!k--){l=c.add(j,"tr");k=q.grid_width-1}g=c.add(l,"td");var i={href:"javascript:;",style:{backgroundColor:"#"+m},title:p.editor.getLang("colors."+m,m),"data-mce-color":"#"+m};if(!d.isIE){i.role="option"}g=c.add(g,"a",i);if(p.editor.forcedHighContrastMode){g=c.add(g,"canvas",{width:16,height:16,"aria-hidden":"true"});if(g.getContext&&(f=g.getContext("2d"))){f.fillStyle="#"+m;f.fillRect(0,0,16,16)}else{c.remove(g)}}});if(q.more_colors_func){g=c.add(j,"tr");g=c.add(g,"td",{colspan:q.grid_width,"class":"mceMoreColors"});g=c.add(g,"a",{role:"option",id:p.id+"_more",href:"javascript:;",onclick:"return false;","class":"mceMoreColors"},q.more_colors_title);a.add(g,"click",function(i){q.more_colors_func.call(q.more_colors_scope||this);return a.cancel(i)})}c.addClass(h,"mceColorSplitMenu");new d.ui.KeyboardNavigation({root:p.id+"_menu",items:c.select("a",p.id+"_menu"),onCancel:function(){p.hideMenu();p.focus()}});a.add(p.id+"_menu","mousedown",function(i){return a.cancel(i)});a.add(p.id+"_menu","click",function(i){var m;i=c.getParent(i.target,"a",j);if(i&&i.nodeName.toLowerCase()=="a"&&(m=i.getAttribute("data-mce-color"))){p.setColor(m)}return false});return o},setColor:function(f){this.displayColor(f);this.hideMenu();this.settings.onselect(f)},displayColor:function(g){var f=this;c.setStyle(f.id+"_preview","backgroundColor",g);f.value=g},postRender:function(){var f=this,g=f.id;f.parent();c.add(g+"_action","div",{id:g+"_preview","class":"mceColorPreview"});c.setStyle(f.id+"_preview","backgroundColor",f.value)},destroy:function(){this.parent();a.clear(this.id+"_menu");a.clear(this.id+"_more");c.remove(this.id+"_menu")}})})(tinymce);(function(b){var d=b.DOM,c=b.each,a=b.dom.Event;b.create("tinymce.ui.ToolbarGroup:tinymce.ui.Container",{renderHTML:function(){var f=this,i=[],e=f.controls,j=b.each,g=f.settings;i.push('<div id="'+f.id+'" role="group" aria-labelledby="'+f.id+'_voice">');i.push("<span role='application'>");i.push('<span id="'+f.id+'_voice" class="mceVoiceLabel" style="display:none;">'+d.encode(g.name)+"</span>");j(e,function(h){i.push(h.renderHTML())});i.push("</span>");i.push("</div>");return i.join("")},focus:function(){var e=this;d.get(e.id).focus()},postRender:function(){var f=this,e=[];c(f.controls,function(g){c(g.controls,function(h){if(h.id){e.push(h)}})});f.keyNav=new b.ui.KeyboardNavigation({root:f.id,items:e,onCancel:function(){if(b.isWebKit){d.get(f.editor.id+"_ifr").focus()}f.editor.focus()},excludeFromTabOrder:!f.settings.tab_focus_toolbar})},destroy:function(){var e=this;e.parent();e.keyNav.destroy();a.clear(e.id)}})})(tinymce);(function(a){var c=a.DOM,b=a.each;a.create("tinymce.ui.Toolbar:tinymce.ui.Container",{renderHTML:function(){var m=this,f="",j,k,n=m.settings,e,d,g,l;l=m.controls;for(e=0;e<l.length;e++){k=l[e];d=l[e-1];g=l[e+1];if(e===0){j="mceToolbarStart";if(k.Button){j+=" mceToolbarStartButton"}else{if(k.SplitButton){j+=" mceToolbarStartSplitButton"}else{if(k.ListBox){j+=" mceToolbarStartListBox"}}}f+=c.createHTML("td",{"class":j},c.createHTML("span",null,"<!-- IE -->"))}if(d&&k.ListBox){if(d.Button||d.SplitButton){f+=c.createHTML("td",{"class":"mceToolbarEnd"},c.createHTML("span",null,"<!-- IE -->"))}}if(c.stdMode){f+='<td style="position: relative">'+k.renderHTML()+"</td>"}else{f+="<td>"+k.renderHTML()+"</td>"}if(g&&k.ListBox){if(g.Button||g.SplitButton){f+=c.createHTML("td",{"class":"mceToolbarStart"},c.createHTML("span",null,"<!-- IE -->"))}}}j="mceToolbarEnd";if(k.Button){j+=" mceToolbarEndButton"}else{if(k.SplitButton){j+=" mceToolbarEndSplitButton"}else{if(k.ListBox){j+=" mceToolbarEndListBox"}}}f+=c.createHTML("td",{"class":j},c.createHTML("span",null,"<!-- IE -->"));return c.createHTML("table",{id:m.id,"class":"mceToolbar"+(n["class"]?" "+n["class"]:""),cellpadding:"0",cellspacing:"0",align:m.settings.align||"",role:"presentation",tabindex:"-1"},"<tbody><tr>"+f+"</tr></tbody>")}})})(tinymce);(function(b){var a=b.util.Dispatcher,c=b.each;b.create("tinymce.AddOnManager",{AddOnManager:function(){var d=this;d.items=[];d.urls={};d.lookup={};d.onAdd=new a(d)},get:function(d){if(this.lookup[d]){return this.lookup[d].instance}else{return undefined}},dependencies:function(e){var d;if(this.lookup[e]){d=this.lookup[e].dependencies}return d||[]},requireLangPack:function(e){var d=b.settings;if(d&&d.language&&d.language_load!==false){b.ScriptLoader.add(this.urls[e]+"/langs/"+d.language+".js")}},add:function(f,e,d){this.items.push(e);this.lookup[f]={instance:e,dependencies:d};this.onAdd.dispatch(this,f,e);return e},createUrl:function(d,e){if(typeof e==="object"){return e}else{return{prefix:d.prefix,resource:e,suffix:d.suffix}}},addComponents:function(f,d){var e=this.urls[f];b.each(d,function(g){b.ScriptLoader.add(e+"/"+g)})},load:function(j,f,d,h){var g=this,e=f;function i(){var k=g.dependencies(j);b.each(k,function(m){var l=g.createUrl(f,m);g.load(l.resource,l,undefined,undefined)});if(d){if(h){d.call(h)}else{d.call(b.ScriptLoader)}}}if(g.urls[j]){return}if(typeof f==="object"){e=f.prefix+f.resource+f.suffix}if(e.indexOf("/")!==0&&e.indexOf("://")==-1){e=b.baseURL+"/"+e}g.urls[j]=e.substring(0,e.lastIndexOf("/"));if(g.lookup[j]){i()}else{b.ScriptLoader.add(e,i,h)}}});b.PluginManager=new b.AddOnManager();b.ThemeManager=new b.AddOnManager()}(tinymce));(function(j){var g=j.each,d=j.extend,k=j.DOM,i=j.dom.Event,f=j.ThemeManager,b=j.PluginManager,e=j.explode,h=j.util.Dispatcher,a,c=0;j.documentBaseURL=window.location.href.replace(/[\?#].*$/,"").replace(/[\/\\][^\/]+$/,"");if(!/[\/\\]$/.test(j.documentBaseURL)){j.documentBaseURL+="/"}j.baseURL=new j.util.URI(j.documentBaseURL).toAbsolute(j.baseURL);j.baseURI=new j.util.URI(j.baseURL);j.onBeforeUnload=new h(j);i.add(window,"beforeunload",function(l){j.onBeforeUnload.dispatch(j,l)});j.onAddEditor=new h(j);j.onRemoveEditor=new h(j);j.EditorManager=d(j,{editors:[],i18n:{},activeEditor:null,init:function(x){var v=this,o,n=j.ScriptLoader,u,l=[],r;function q(y){var s=y.id;if(!s){s=y.name;if(s&&!k.get(s)){s=y.name}else{s=k.uniqueId()}y.setAttribute("id",s)}return s}function m(A,B,y){var z=A[B];if(!z){return}if(j.is(z,"string")){y=z.replace(/\.\w+$/,"");y=y?j.resolve(y):0;z=j.resolve(z)}return z.apply(y||this,Array.prototype.slice.call(arguments,2))}function p(y,s){return s.constructor===RegExp?s.test(y.className):k.hasClass(y,s)}x=d({theme:"simple",language:"en"},x);v.settings=x;i.bind(window,"ready",function(){var s,y;m(x,"onpageload");switch(x.mode){case"exact":s=x.elements||"";if(s.length>0){g(e(s),function(z){if(k.get(z)){r=new j.Editor(z,x);l.push(r);r.render(1)}else{g(document.forms,function(A){g(A.elements,function(B){if(B.name===z){z="mce_editor_"+c++;k.setAttrib(B,"id",z);r=new j.Editor(z,x);l.push(r);r.render(1)}})})}})}break;case"textareas":case"specific_textareas":g(k.select("textarea"),function(z){if(x.editor_deselector&&p(z,x.editor_deselector)){return}if(!x.editor_selector||p(z,x.editor_selector)){r=new j.Editor(q(z),x);l.push(r);r.render(1)}});break;default:if(x.types){g(x.types,function(z){g(k.select(z.selector),function(B){var A=new j.Editor(q(B),j.extend({},x,z));l.push(A);A.render(1)})})}else{if(x.selector){g(k.select(x.selector),function(A){var z=new j.Editor(q(A),x);l.push(z);z.render(1)})}}}if(x.oninit){s=y=0;g(l,function(z){y++;if(!z.initialized){z.onInit.add(function(){s++;if(s==y){m(x,"oninit")}})}else{s++}if(s==y){m(x,"oninit")}})}})},get:function(l){if(l===a){return this.editors}return this.editors[l]},getInstanceById:function(l){return this.get(l)},add:function(m){var l=this,n=l.editors;n[m.id]=m;n.push(m);l._setActive(m);l.onAddEditor.dispatch(l,m);return m},remove:function(n){var m=this,l,o=m.editors;if(!o[n.id]){return null}delete o[n.id];for(l=0;l<o.length;l++){if(o[l]==n){o.splice(l,1);break}}if(m.activeEditor==n){m._setActive(o[0])}n.destroy();m.onRemoveEditor.dispatch(m,n);return n},execCommand:function(r,p,o){var q=this,n=q.get(o),l;function m(){n.destroy();l.detachEvent("onunload",m);l=l.tinyMCE=l.tinymce=null}switch(r){case"mceFocus":n.focus();return true;case"mceAddEditor":case"mceAddControl":if(!q.get(o)){new j.Editor(o,q.settings).render()}return true;case"mceAddFrameControl":l=o.window;l.tinyMCE=tinyMCE;l.tinymce=j;j.DOM.doc=l.document;j.DOM.win=l;n=new j.Editor(o.element_id,o);n.render();if(j.isIE){l.attachEvent("onunload",m)}o.page_window=null;return true;case"mceRemoveEditor":case"mceRemoveControl":if(n){n.remove()}return true;case"mceToggleEditor":if(!n){q.execCommand("mceAddControl",0,o);return true}if(n.isHidden()){n.show()}else{n.hide()}return true}if(q.activeEditor){return q.activeEditor.execCommand(r,p,o)}return false},execInstanceCommand:function(p,o,n,m){var l=this.get(p);if(l){return l.execCommand(o,n,m)}return false},triggerSave:function(){g(this.editors,function(l){l.save()})},addI18n:function(n,q){var l,m=this.i18n;if(!j.is(n,"string")){g(n,function(r,p){g(r,function(u,s){g(u,function(x,v){if(s==="common"){m[p+"."+v]=x}else{m[p+"."+s+"."+v]=x}})})})}else{g(q,function(r,p){m[n+"."+p]=r})}},_setActive:function(l){this.selectedInstance=this.activeEditor=l}})})(tinymce);(function(k){var l=k.DOM,j=k.dom.Event,f=k.extend,i=k.each,a=k.isGecko,b=k.isIE,e=k.isWebKit,d=k.is,h=k.ThemeManager,c=k.PluginManager,g=k.explode;k.create("tinymce.Editor",{Editor:function(p,o){var m=this,n=true;m.settings=o=f({id:p,language:"en",theme:"simple",skin:"default",delta_width:0,delta_height:0,popup_css:"",plugins:"",document_base_url:k.documentBaseURL,add_form_submit_trigger:n,submit_patch:n,add_unload_trigger:n,convert_urls:n,relative_urls:n,remove_script_host:n,table_inline_editing:false,object_resizing:n,accessibility_focus:n,doctype:k.isIE6?'<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">':"<!DOCTYPE>",visual:n,font_size_style_values:"xx-small,x-small,small,medium,large,x-large,xx-large",font_size_legacy_values:"xx-small,small,medium,large,x-large,xx-large,300%",apply_source_formatting:n,directionality:"ltr",forced_root_block:"p",hidden_input:n,padd_empty_editor:n,render_ui:n,indentation:"30px",fix_table_elements:n,inline_styles:n,convert_fonts_to_spans:n,indent:"simple",indent_before:"p,h1,h2,h3,h4,h5,h6,blockquote,div,title,style,pre,script,td,ul,li,area,table,thead,tfoot,tbody,tr,section,article,hgroup,aside,figure",indent_after:"p,h1,h2,h3,h4,h5,h6,blockquote,div,title,style,pre,script,td,ul,li,area,table,thead,tfoot,tbody,tr,section,article,hgroup,aside,figure",validate:n,entity_encoding:"named",url_converter:m.convertURL,url_converter_scope:m,ie7_compat:n},o);m.id=m.editorId=p;m.isNotDirty=false;m.plugins={};m.documentBaseURI=new k.util.URI(o.document_base_url||k.documentBaseURL,{base_uri:tinyMCE.baseURI});m.baseURI=k.baseURI;m.contentCSS=[];m.setupEvents();m.execCommands={};m.queryStateCommands={};m.queryValueCommands={};m.execCallback("setup",m)},render:function(o){var p=this,q=p.settings,r=p.id,m=k.ScriptLoader;if(!j.domLoaded){j.add(window,"ready",function(){p.render()});return}tinyMCE.settings=q;if(!p.getElement()){return}if(k.isIDevice&&!k.isIOS5){return}if(!/TEXTAREA|INPUT/i.test(p.getElement().nodeName)&&q.hidden_input&&l.getParent(r,"form")){l.insertAfter(l.create("input",{type:"hidden",name:r}),r)}if(k.WindowManager){p.windowManager=new k.WindowManager(p)}if(q.encoding=="xml"){p.onGetContent.add(function(s,u){if(u.save){u.content=l.encode(u.content)}})}if(q.add_form_submit_trigger){p.onSubmit.addToTop(function(){if(p.initialized){p.save();p.isNotDirty=1}})}if(q.add_unload_trigger){p._beforeUnload=tinyMCE.onBeforeUnload.add(function(){if(p.initialized&&!p.destroyed&&!p.isHidden()){p.save({format:"raw",no_events:true})}})}k.addUnload(p.destroy,p);if(q.submit_patch){p.onBeforeRenderUI.add(function(){var s=p.getElement().form;if(!s){return}if(s._mceOldSubmit){return}if(!s.submit.nodeType&&!s.submit.length){p.formElement=s;s._mceOldSubmit=s.submit;s.submit=function(){k.triggerSave();p.isNotDirty=1;return p.formElement._mceOldSubmit(p.formElement)}}s=null})}function n(){if(q.language&&q.language_load!==false){m.add(k.baseURL+"/langs/"+q.language+".js")}if(q.theme&&q.theme.charAt(0)!="-"&&!h.urls[q.theme]){h.load(q.theme,"themes/"+q.theme+"/editor_template"+k.suffix+".js")}i(g(q.plugins),function(u){if(u&&!c.urls[u]){if(u.charAt(0)=="-"){u=u.substr(1,u.length);var s=c.dependencies(u);i(s,function(x){var v={prefix:"plugins/",resource:x,suffix:"/editor_plugin"+k.suffix+".js"};x=c.createUrl(v,x);c.load(x.resource,x)})}else{if(u=="safari"){return}c.load(u,{prefix:"plugins/",resource:u,suffix:"/editor_plugin"+k.suffix+".js"})}}});m.loadQueue(function(){if(!p.removed){p.init()}})}n()},init:function(){var q,F=this,G=F.settings,C,y,B=F.getElement(),p,m,D,v,A,E,x,r=[];k.add(F);G.aria_label=G.aria_label||l.getAttrib(B,"aria-label",F.getLang("aria.rich_text_area"));if(G.theme){G.theme=G.theme.replace(/-/,"");p=h.get(G.theme);F.theme=new p();if(F.theme.init){F.theme.init(F,h.urls[G.theme]||k.documentBaseURL.replace(/\/$/,""))}}function z(s){var H=c.get(s),o=c.urls[s]||k.documentBaseURL.replace(/\/$/,""),n;if(H&&k.inArray(r,s)===-1){i(c.dependencies(s),function(u){z(u)});n=new H(F,o);F.plugins[s]=n;if(n.init){n.init(F,o);r.push(s)}}}i(g(G.plugins.replace(/\-/g,"")),z);if(G.popup_css!==false){if(G.popup_css){G.popup_css=F.documentBaseURI.toAbsolute(G.popup_css)}else{G.popup_css=F.baseURI.toAbsolute("themes/"+G.theme+"/skins/"+G.skin+"/dialog.css")}}if(G.popup_css_add){G.popup_css+=","+F.documentBaseURI.toAbsolute(G.popup_css_add)}F.controlManager=new k.ControlManager(F);F.onExecCommand.add(function(n,o){if(!/^(FontName|FontSize)$/.test(o)){F.nodeChanged()}});F.onBeforeRenderUI.dispatch(F,F.controlManager);if(G.render_ui&&F.theme){C=G.width||B.style.width||B.offsetWidth;y=G.height||B.style.height||B.offsetHeight;F.orgDisplay=B.style.display;E=/^[0-9\.]+(|px)$/i;if(E.test(""+C)){C=Math.max(parseInt(C,10)+(p.deltaWidth||0),100)}if(E.test(""+y)){y=Math.max(parseInt(y,10)+(p.deltaHeight||0),100)}p=F.theme.renderUI({targetNode:B,width:C,height:y,deltaWidth:G.delta_width,deltaHeight:G.delta_height});F.editorContainer=p.editorContainer}if(G.content_css){i(g(G.content_css),function(n){F.contentCSS.push(F.documentBaseURI.toAbsolute(n))})}if(G.content_editable){B=q=p=null;return F.initContentBody()}if(document.domain&&location.hostname!=document.domain){k.relaxedDomain=document.domain}l.setStyles(p.sizeContainer||p.editorContainer,{width:C,height:y});y=(p.iframeHeight||y)+(typeof(y)=="number"?(p.deltaHeight||0):"");if(y<100){y=100}F.iframeHTML=G.doctype+'<html><head xmlns="http://www.w3.org/1999/xhtml">';if(G.document_base_url!=k.documentBaseURL){F.iframeHTML+='<base href="'+F.documentBaseURI.getURI()+'" />'}if(G.ie7_compat){F.iframeHTML+='<meta http-equiv="X-UA-Compatible" content="IE=7" />'}else{F.iframeHTML+='<meta http-equiv="X-UA-Compatible" content="IE=edge" />'}F.iframeHTML+='<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />';for(x=0;x<F.contentCSS.length;x++){F.iframeHTML+='<link type="text/css" rel="stylesheet" href="'+F.contentCSS[x]+'" />'}F.contentCSS=[];v=G.body_id||"tinymce";if(v.indexOf("=")!=-1){v=F.getParam("body_id","","hash");v=v[F.id]||v}A=G.body_class||"";if(A.indexOf("=")!=-1){A=F.getParam("body_class","","hash");A=A[F.id]||""}F.iframeHTML+='</head><body id="'+v+'" class="mceContentBody '+A+'" onload="window.parent.tinyMCE.get(\''+F.id+"').onLoad.dispatch();\"><br></body></html>";if(k.relaxedDomain&&(b||(k.isOpera&&parseFloat(opera.version())<11))){D='javascript:(function(){document.open();document.domain="'+document.domain+'";var ed = window.parent.tinyMCE.get("'+F.id+'");document.write(ed.iframeHTML);document.close();ed.initContentBody();})()'}q=l.add(p.iframeContainer,"iframe",{id:F.id+"_ifr",src:D||'javascript:""',frameBorder:"0",allowTransparency:"true",title:G.aria_label,style:{width:"100%",height:y,display:"block"}});F.contentAreaContainer=p.iframeContainer;l.get(p.editorContainer).style.display=F.orgDisplay;l.get(F.id).style.display="none";l.setAttrib(F.id,"aria-hidden",true);if(!k.relaxedDomain||!D){F.initContentBody()}B=q=p=null},initContentBody:function(){var n=this,p=n.settings,q=l.get(n.id),r=n.getDoc(),o,m;if((!b||!k.relaxedDomain)&&!p.content_editable){r.open();r.write(n.iframeHTML);r.close();if(k.relaxedDomain){r.domain=k.relaxedDomain}}if(p.content_editable){l.addClass(q,"mceContentBody");n.contentDocument=r=p.content_document||document;n.contentWindow=p.content_window||window;n.bodyElement=q;p.content_document=p.content_window=null}m=n.getBody();m.disabled=true;if(!p.readonly){m.contentEditable=n.getParam("content_editable_state",true)}m.disabled=false;n.schema=new k.html.Schema(p);n.dom=new k.dom.DOMUtils(r,{keep_values:true,url_converter:n.convertURL,url_converter_scope:n,hex_colors:p.force_hex_style_colors,class_filter:p.class_filter,update_styles:true,root_element:p.content_editable?n.id:null,schema:n.schema});n.parser=new k.html.DomParser(p,n.schema);n.parser.addAttributeFilter("src,href,style",function(s,u){var v=s.length,y,A=n.dom,z,x;while(v--){y=s[v];z=y.attr(u);x="data-mce-"+u;if(!y.attributes.map[x]){if(u==="style"){y.attr(x,A.serializeStyle(A.parseStyle(z),y.name))}else{y.attr(x,n.convertURL(z,u,y.name))}}}});n.parser.addNodeFilter("script",function(s,u){var v=s.length,x;while(v--){x=s[v];x.attr("type","mce-"+(x.attr("type")||"text/javascript"))}});n.parser.addNodeFilter("#cdata",function(s,u){var v=s.length,x;while(v--){x=s[v];x.type=8;x.name="#comment";x.value="[CDATA["+x.value+"]]"}});n.parser.addNodeFilter("p,h1,h2,h3,h4,h5,h6,div",function(u,v){var x=u.length,y,s=n.schema.getNonEmptyElements();while(x--){y=u[x];if(y.isEmpty(s)){y.empty().append(new k.html.Node("br",1)).shortEnded=true}}});n.serializer=new k.dom.Serializer(p,n.dom,n.schema);n.selection=new k.dom.Selection(n.dom,n.getWin(),n.serializer);n.formatter=new k.Formatter(n);n.undoManager=new k.UndoManager(n);n.forceBlocks=new k.ForceBlocks(n);n.enterKey=new k.EnterKey(n);n.editorCommands=new k.EditorCommands(n);n.serializer.onPreProcess.add(function(s,u){return n.onPreProcess.dispatch(n,u,s)});n.serializer.onPostProcess.add(function(s,u){return n.onPostProcess.dispatch(n,u,s)});n.onPreInit.dispatch(n);if(!p.gecko_spellcheck){r.body.spellcheck=false}if(!p.readonly){n.bindNativeEvents()}n.controlManager.onPostRender.dispatch(n,n.controlManager);n.onPostRender.dispatch(n);n.quirks=k.util.Quirks(n);if(p.directionality){m.dir=p.directionality}if(p.nowrap){m.style.whiteSpace="nowrap"}if(p.protect){n.onBeforeSetContent.add(function(s,u){i(p.protect,function(v){u.content=u.content.replace(v,function(x){return"<!--mce:protected "+escape(x)+"-->"})})})}n.onSetContent.add(function(){n.addVisual(n.getBody())});if(p.padd_empty_editor){n.onPostProcess.add(function(s,u){u.content=u.content.replace(/^(<p[^>]*>(&nbsp;|&#160;|\s|\u00a0|)<\/p>[\r\n]*|<br \/>[\r\n]*)$/,"")})}n.load({initial:true,format:"html"});n.startContent=n.getContent({format:"raw"});n.initialized=true;n.onInit.dispatch(n);n.execCallback("setupcontent_callback",n.id,m,r);n.execCallback("init_instance_callback",n);n.focus(true);n.nodeChanged({initial:true});i(n.contentCSS,function(s){n.dom.loadCSS(s)});if(p.auto_focus){setTimeout(function(){var s=k.get(p.auto_focus);s.selection.select(s.getBody(),1);s.selection.collapse(1);s.getBody().focus();s.getWin().focus()},100)}q=r=m=null},focus:function(p){var o,v=this,u=v.selection,q=v.settings.content_editable,n,r,s=v.getDoc(),m;if(!p){n=u.getRng();if(n.item){r=n.item(0)}v._refreshContentEditable();if(!q){v.getWin().focus()}if(k.isGecko||q){m=v.getBody();if(m.setActive){m.setActive()}else{m.focus()}if(q){u.normalize()}}if(r&&r.ownerDocument==s){n=s.body.createControlRange();n.addElement(r);n.select()}}if(k.activeEditor!=v){if((o=k.activeEditor)!=null){o.onDeactivate.dispatch(o,v)}v.onActivate.dispatch(v,o)}k._setActive(v)},execCallback:function(q){var m=this,p=m.settings[q],o;if(!p){return}if(m.callbackLookup&&(o=m.callbackLookup[q])){p=o.func;o=o.scope}if(d(p,"string")){o=p.replace(/\.\w+$/,"");o=o?k.resolve(o):0;p=k.resolve(p);m.callbackLookup=m.callbackLookup||{};m.callbackLookup[q]={func:p,scope:o}}return p.apply(o||m,Array.prototype.slice.call(arguments,1))},translate:function(m){var o=this.settings.language||"en",n=k.i18n;if(!m){return""}return n[o+"."+m]||m.replace(/\{\#([^\}]+)\}/g,function(q,p){return n[o+"."+p]||"{#"+p+"}"})},getLang:function(o,m){return k.i18n[(this.settings.language||"en")+"."+o]||(d(m)?m:"{#"+o+"}")},getParam:function(u,q,m){var r=k.trim,p=d(this.settings[u])?this.settings[u]:q,s;if(m==="hash"){s={};if(d(p,"string")){i(p.indexOf("=")>0?p.split(/[;,](?![^=;,]*(?:[;,]|$))/):p.split(","),function(n){n=n.split("=");if(n.length>1){s[r(n[0])]=r(n[1])}else{s[r(n[0])]=r(n)}})}else{s=p}return s}return p},nodeChanged:function(q){var m=this,n=m.selection,p;if(m.initialized){q=q||{};n.normalize();p=n.getStart()||m.getBody();p=b&&p.ownerDocument!=m.getDoc()?m.getBody():p;q.parents=[];m.dom.getParent(p,function(o){if(o.nodeName=="BODY"){return true}q.parents.push(o)});m.onNodeChange.dispatch(m,q?q.controlManager||m.controlManager:m.controlManager,p,n.isCollapsed(),q)}},addButton:function(n,o){var m=this;m.buttons=m.buttons||{};m.buttons[n]=o},addCommand:function(m,o,n){this.execCommands[m]={func:o,scope:n||this}},addQueryStateHandler:function(m,o,n){this.queryStateCommands[m]={func:o,scope:n||this}},addQueryValueHandler:function(m,o,n){this.queryValueCommands[m]={func:o,scope:n||this}},addShortcut:function(o,q,m,p){var n=this,r;if(n.settings.custom_shortcuts===false){return false}n.shortcuts=n.shortcuts||{};if(d(m,"string")){r=m;m=function(){n.execCommand(r,false,null)}}if(d(m,"object")){r=m;m=function(){n.execCommand(r[0],r[1],r[2])}}i(g(o),function(s){var u={func:m,scope:p||this,desc:n.translate(q),alt:false,ctrl:false,shift:false};i(g(s,"+"),function(x){switch(x){case"alt":case"ctrl":case"shift":u[x]=true;break;default:u.charCode=x.charCodeAt(0);u.keyCode=x.toUpperCase().charCodeAt(0)}});n.shortcuts[(u.ctrl?"ctrl":"")+","+(u.alt?"alt":"")+","+(u.shift?"shift":"")+","+u.keyCode]=u});return true},execCommand:function(u,r,x,m){var p=this,q=0,v,n;if(!/^(mceAddUndoLevel|mceEndUndoLevel|mceBeginUndoLevel|mceRepaint|SelectAll)$/.test(u)&&(!m||!m.skip_focus)){p.focus()}m=f({},m);p.onBeforeExecCommand.dispatch(p,u,r,x,m);if(m.terminate){return false}if(p.execCallback("execcommand_callback",p.id,p.selection.getNode(),u,r,x)){p.onExecCommand.dispatch(p,u,r,x,m);return true}if(v=p.execCommands[u]){n=v.func.call(v.scope,r,x);if(n!==true){p.onExecCommand.dispatch(p,u,r,x,m);return n}}i(p.plugins,function(o){if(o.execCommand&&o.execCommand(u,r,x)){p.onExecCommand.dispatch(p,u,r,x,m);q=1;return false}});if(q){return true}if(p.theme&&p.theme.execCommand&&p.theme.execCommand(u,r,x)){p.onExecCommand.dispatch(p,u,r,x,m);return true}if(p.editorCommands.execCommand(u,r,x)){p.onExecCommand.dispatch(p,u,r,x,m);return true}p.getDoc().execCommand(u,r,x);p.onExecCommand.dispatch(p,u,r,x,m)},queryCommandState:function(q){var n=this,r,p;if(n._isHidden()){return}if(r=n.queryStateCommands[q]){p=r.func.call(r.scope);if(p!==true){return p}}r=n.editorCommands.queryCommandState(q);if(r!==-1){return r}try{return this.getDoc().queryCommandState(q)}catch(m){}},queryCommandValue:function(r){var n=this,q,p;if(n._isHidden()){return}if(q=n.queryValueCommands[r]){p=q.func.call(q.scope);if(p!==true){return p}}q=n.editorCommands.queryCommandValue(r);if(d(q)){return q}try{return this.getDoc().queryCommandValue(r)}catch(m){}},show:function(){var m=this;l.show(m.getContainer());l.hide(m.id);m.load()},hide:function(){var m=this,n=t.getDoc();if(b&&n){n.execCommand("SelectAll")}m.save();l.hide(m.getContainer());l.setStyle(m.id,"display",m.orgDisplay)},isHidden:function(){return !l.isHidden(this.id)},setProgressState:function(m,n,p){this.onSetProgressState.dispatch(this,m,n,p);return m},load:function(q){var m=this,p=m.getElement(),n;if(p){q=q||{};q.load=true;n=m.setContent(d(p.value)?p.value:p.innerHTML,q);q.element=p;if(!q.no_events){m.onLoadContent.dispatch(m,q)}q.element=p=null;return n}},save:function(r){var m=this,q=m.getElement(),n,p;if(!q||!m.initialized){return}r=r||{};r.save=true;r.element=q;n=r.content=m.getContent(r);if(!r.no_events){m.onSaveContent.dispatch(m,r)}n=r.content;if(!/TEXTAREA|INPUT/i.test(q.nodeName)){q.innerHTML=n;if(p=l.getParent(m.id,"form")){i(p.elements,function(o){if(o.name==m.id){o.value=n;return false}})}}else{q.value=n}r.element=q=null;return n},setContent:function(r,p){var o=this,n,m=o.getBody(),q;p=p||{};p.format=p.format||"html";p.set=true;p.content=r;if(!p.no_events){o.onBeforeSetContent.dispatch(o,p)}r=p.content;if(!k.isIE&&(r.length===0||/^\s+$/.test(r))){q=o.settings.forced_root_block;if(q){r="<"+q+'><br data-mce-bogus="1"></'+q+">"}else{r='<br data-mce-bogus="1">'}m.innerHTML=r;o.selection.select(m,true);o.selection.collapse(true);return}if(p.format!=="raw"){r=new k.html.Serializer({},o.schema).serialize(o.parser.parse(r))}p.content=k.trim(r);o.dom.setHTML(m,p.content);if(!p.no_events){o.onSetContent.dispatch(o,p)}o.selection.normalize();return p.content},getContent:function(n){var m=this,o;n=n||{};n.format=n.format||"html";n.get=true;n.getInner=true;if(!n.no_events){m.onBeforeGetContent.dispatch(m,n)}if(n.format=="raw"){o=m.getBody().innerHTML}else{o=m.serializer.serialize(m.getBody(),n)}n.content=k.trim(o);if(!n.no_events){m.onGetContent.dispatch(m,n)}return n.content},isDirty:function(){var m=this;return k.trim(m.startContent)!=k.trim(m.getContent({format:"raw",no_events:1}))&&!m.isNotDirty},getContainer:function(){var m=this;if(!m.container){m.container=l.get(m.editorContainer||m.id+"_parent")}return m.container},getContentAreaContainer:function(){return this.contentAreaContainer},getElement:function(){return l.get(this.settings.content_element||this.id)},getWin:function(){var m=this,n;if(!m.contentWindow){n=l.get(m.id+"_ifr");if(n){m.contentWindow=n.contentWindow}}return m.contentWindow},getDoc:function(){var m=this,n;if(!m.contentDocument){n=m.getWin();if(n){m.contentDocument=n.document}}return m.contentDocument},getBody:function(){return this.bodyElement||this.getDoc().body},convertURL:function(o,n,q){var m=this,p=m.settings;if(p.urlconverter_callback){return m.execCallback("urlconverter_callback",o,q,true,n)}if(!p.convert_urls||(q&&q.nodeName=="LINK")||o.indexOf("file:")===0){return o}if(p.relative_urls){return m.documentBaseURI.toRelative(o)}o=m.documentBaseURI.toAbsolute(o,p.remove_script_host);return o},addVisual:function(q){var n=this,o=n.settings,p=n.dom,m;q=q||n.getBody();if(!d(n.hasVisual)){n.hasVisual=o.visual}i(p.select("table,a",q),function(s){var r;switch(s.nodeName){case"TABLE":m=o.visual_table_class||"mceItemTable";r=p.getAttrib(s,"border");if(!r||r=="0"){if(n.hasVisual){p.addClass(s,m)}else{p.removeClass(s,m)}}return;case"A":r=p.getAttrib(s,"name");m="mceItemAnchor";if(r){if(n.hasVisual){p.addClass(s,m)}else{p.removeClass(s,m)}}return}});n.onVisualAid.dispatch(n,q,n.hasVisual)},remove:function(){var m=this,n=m.getContainer();if(!m.removed){m.removed=1;m.hide();if(!m.settings.content_editable){j.clear(m.getWin());j.clear(m.getDoc())}j.clear(m.getBody());j.clear(m.formElement);j.unbind(n);m.execCallback("remove_instance_callback",m);m.onRemove.dispatch(m);m.onExecCommand.listeners=[];k.remove(m);l.remove(n)}},destroy:function(n){var m=this;if(m.destroyed){return}if(a){j.unbind(m.getDoc());j.unbind(m.getWin());j.unbind(m.getBody())}if(!n){k.removeUnload(m.destroy);tinyMCE.onBeforeUnload.remove(m._beforeUnload);if(m.theme&&m.theme.destroy){m.theme.destroy()}m.controlManager.destroy();m.selection.destroy();m.dom.destroy()}if(m.formElement){m.formElement.submit=m.formElement._mceOldSubmit;m.formElement._mceOldSubmit=null}m.contentAreaContainer=m.formElement=m.container=m.settings.content_element=m.bodyElement=m.contentDocument=m.contentWindow=null;if(m.selection){m.selection=m.selection.win=m.selection.dom=m.selection.dom.doc=null}m.destroyed=1},_refreshContentEditable:function(){var n=this,m,o;if(n._isHidden()){m=n.getBody();o=m.parentNode;o.removeChild(m);o.appendChild(m);m.focus()}},_isHidden:function(){var m;if(!a){return 0}m=this.selection.getSel();return(!m||!m.rangeCount||m.rangeCount===0)}})})(tinymce);(function(a){var b=a.each;a.Editor.prototype.setupEvents=function(){var c=this,d=c.settings;b(["onPreInit","onBeforeRenderUI","onPostRender","onLoad","onInit","onRemove","onActivate","onDeactivate","onClick","onEvent","onMouseUp","onMouseDown","onDblClick","onKeyDown","onKeyUp","onKeyPress","onContextMenu","onSubmit","onReset","onPaste","onPreProcess","onPostProcess","onBeforeSetContent","onBeforeGetContent","onSetContent","onGetContent","onLoadContent","onSaveContent","onNodeChange","onChange","onBeforeExecCommand","onExecCommand","onUndo","onRedo","onVisualAid","onSetProgressState","onSetAttrib"],function(e){c[e]=new a.util.Dispatcher(c)});if(d.cleanup_callback){c.onBeforeSetContent.add(function(e,f){f.content=e.execCallback("cleanup_callback","insert_to_editor",f.content,f)});c.onPreProcess.add(function(e,f){if(f.set){e.execCallback("cleanup_callback","insert_to_editor_dom",f.node,f)}if(f.get){e.execCallback("cleanup_callback","get_from_editor_dom",f.node,f)}});c.onPostProcess.add(function(e,f){if(f.set){f.content=e.execCallback("cleanup_callback","insert_to_editor",f.content,f)}if(f.get){f.content=e.execCallback("cleanup_callback","get_from_editor",f.content,f)}})}if(d.save_callback){c.onGetContent.add(function(e,f){if(f.save){f.content=e.execCallback("save_callback",e.id,f.content,e.getBody())}})}if(d.handle_event_callback){c.onEvent.add(function(f,g,h){if(c.execCallback("handle_event_callback",g,f,h)===false){Event.cancel(g)}})}if(d.handle_node_change_callback){c.onNodeChange.add(function(f,e,g){f.execCallback("handle_node_change_callback",f.id,g,-1,-1,true,f.selection.isCollapsed())})}if(d.save_callback){c.onSaveContent.add(function(e,g){var f=e.execCallback("save_callback",e.id,g.content,e.getBody());if(f){g.content=f}})}if(d.onchange_callback){c.onChange.add(function(f,e){f.execCallback("onchange_callback",f,e)})}};a.Editor.prototype.bindNativeEvents=function(){var c=this,f,g=c.settings,j=c.dom,k;k={mouseup:"onMouseUp",mousedown:"onMouseDown",click:"onClick",keyup:"onKeyUp",keydown:"onKeyDown",keypress:"onKeyPress",submit:"onSubmit",reset:"onReset",contextmenu:"onContextMenu",dblclick:"onDblClick",paste:"onPaste"};function e(i,l){var m=i.type;if(c.removed){return}if(c.onEvent.dispatch(c,i,l)!==false){c[k[i.fakeType||i.type]].dispatch(c,i,l)}}function h(i){c.focus(true)}b(k,function(l,m){var i=g.content_editable?c.getBody():c.getDoc();switch(m){case"contextmenu":j.bind(i,m,e);break;case"paste":j.bind(c.getBody(),m,e);break;case"submit":case"reset":j.bind(c.getElement().form||a.DOM.getParent(c.id,"form"),m,e);break;default:j.bind(i,m,e)}});j.bind(g.content_editable?c.getBody():(a.isGecko?c.getDoc():c.getWin()),"focus",function(i){c.focus(true)});if(g.content_editable&&a.isOpera){j.bind(c.getBody(),"click",h);j.bind(c.getBody(),"keydown",h)}c.onMouseUp.add(c.nodeChanged);c.onKeyUp.add(function(i,m){var l=m.keyCode;if((l>=33&&l<=36)||(l>=37&&l<=40)||l==13||l==45||l==46||l==8||(a.isMac&&(l==91||l==93))||m.ctrlKey){c.nodeChanged()}});c.onReset.add(function(){c.setContent(c.startContent,{format:"raw"})});function d(l,i){if(l.altKey||l.ctrlKey||l.metaKey){b(c.shortcuts,function(m){var n=a.isMac?l.metaKey:l.ctrlKey;if(m.ctrl!=n||m.alt!=l.altKey||m.shift!=l.shiftKey){return}if(l.keyCode==m.keyCode||(l.charCode&&l.charCode==m.charCode)){l.preventDefault();if(i){m.func.call(m.scope)}return true}})}}c.onKeyUp.add(function(i,l){d(l)});c.onKeyPress.add(function(i,l){d(l)});c.onKeyDown.add(function(i,l){d(l,true)});if(a.isOpera){c.onClick.add(function(i,l){l.preventDefault()})}}})(tinymce);(function(d){var e=d.each,b,a=true,c=false;d.EditorCommands=function(n){var m=n.dom,p=n.selection,j={state:{},exec:{},value:{}},k=n.settings,q=n.formatter,o;function r(A,z,y){var x;A=A.toLowerCase();if(x=j.exec[A]){x(A,z,y);return a}return c}function l(y){var x;y=y.toLowerCase();if(x=j.state[y]){return x(y)}return -1}function h(y){var x;y=y.toLowerCase();if(x=j.value[y]){return x(y)}return c}function v(x,y){y=y||"exec";e(x,function(A,z){e(z.toLowerCase().split(","),function(B){j[y][B]=A})})}d.extend(this,{execCommand:r,queryCommandState:l,queryCommandValue:h,addCommands:v});function f(z,y,x){if(y===b){y=c}if(x===b){x=null}return n.getDoc().execCommand(z,y,x)}function u(x){return q.match(x)}function s(x,y){q.toggle(x,y?{value:y}:b)}function i(x){o=p.getBookmark(x)}function g(){p.moveToBookmark(o)}v({"mceResetDesignMode,mceBeginUndoLevel":function(){},"mceEndUndoLevel,mceAddUndoLevel":function(){n.undoManager.add()},"Cut,Copy,Paste":function(A){var z=n.getDoc(),x;try{f(A)}catch(y){x=a}if(x||!z.queryCommandSupported(A)){if(d.isGecko){n.windowManager.confirm(n.getLang("clipboard_msg"),function(B){if(B){open("http://www.mozilla.org/editor/midasdemo/securityprefs.html","_blank")}})}else{n.windowManager.alert(n.getLang("clipboard_no_support"))}}},unlink:function(x){if(p.isCollapsed()){p.select(p.getNode())}f(x);p.collapse(c)},"JustifyLeft,JustifyCenter,JustifyRight,JustifyFull":function(x){var y=x.substring(7);e("left,center,right,full".split(","),function(z){if(y!=z){q.remove("align"+z)}});s("align"+y);r("mceRepaint")},"InsertUnorderedList,InsertOrderedList":function(z){var x,y;f(z);x=m.getParent(p.getNode(),"ol,ul");if(x){y=x.parentNode;if(/^(H[1-6]|P|ADDRESS|PRE)$/.test(y.nodeName)){i();m.split(y,x);g()}}},"Bold,Italic,Underline,Strikethrough,Superscript,Subscript":function(x){s(x)},"ForeColor,HiliteColor,FontName":function(z,y,x){s(z,x)},FontSize:function(A,z,y){var x,B;if(y>=1&&y<=7){B=d.explode(k.font_size_style_values);x=d.explode(k.font_size_classes);if(x){y=x[y-1]||y}else{y=B[y-1]||y}}s(A,y)},RemoveFormat:function(x){q.remove(x)},mceBlockQuote:function(x){s("blockquote")},FormatBlock:function(z,y,x){return s(x||"p")},mceCleanup:function(){var x=p.getBookmark();n.setContent(n.getContent({cleanup:a}),{cleanup:a});p.moveToBookmark(x)},mceRemoveNode:function(A,z,y){var x=y||p.getNode();if(x!=n.getBody()){i();n.dom.remove(x,a);g()}},mceSelectNodeDepth:function(A,z,y){var x=0;m.getParent(p.getNode(),function(B){if(B.nodeType==1&&x++==y){p.select(B);return c}},n.getBody())},mceSelectNode:function(z,y,x){p.select(x)},mceInsertContent:function(C,J,L){var z,K,F,A,G,H,E,D,M,y,B,N,x,I;z=n.parser;K=new d.html.Serializer({},n.schema);x='<span id="mce_marker" data-mce-type="bookmark">\uFEFF</span>';H={content:L,format:"html"};p.onBeforeSetContent.dispatch(p,H);L=H.content;if(L.indexOf("{$caret}")==-1){L+="{$caret}"}L=L.replace(/\{\$caret\}/,x);if(!p.isCollapsed()){n.getDoc().execCommand("Delete",false,null)}F=p.getNode();H={context:F.nodeName.toLowerCase()};G=z.parse(L,H);B=G.lastChild;if(B.attr("id")=="mce_marker"){E=B;for(B=B.prev;B;B=B.walk(true)){if(B.type==3||!m.isBlock(B.name)){B.parent.insert(E,B,B.name==="br");break}}}if(!H.invalid){L=K.serialize(G);B=F.firstChild;N=F.lastChild;if(!B||(B===N&&B.nodeName==="BR")){m.setHTML(F,L)}else{p.setContent(L)}}else{p.setContent(x);F=n.selection.getNode();A=n.getBody();if(F.nodeType==9){F=B=A}else{B=F}while(B!==A){F=B;B=B.parentNode}L=F==A?A.innerHTML:m.getOuterHTML(F);L=K.serialize(z.parse(L.replace(/<span (id="mce_marker"|id=mce_marker).+?<\/span>/i,function(){return K.serialize(G)})));if(F==A){m.setHTML(A,L)}else{m.setOuterHTML(F,L)}}E=m.get("mce_marker");D=m.getRect(E);M=m.getViewPort(n.getWin());if((D.y+D.h>M.y+M.h||D.y<M.y)||(D.x>M.x+M.w||D.x<M.x)){I=d.isIE?n.getDoc().documentElement:n.getBody();I.scrollLeft=D.x;I.scrollTop=D.y-M.h+25}y=m.createRng();B=E.previousSibling;if(B&&B.nodeType==3){y.setStart(B,B.nodeValue.length)}else{y.setStartBefore(E);y.setEndBefore(E)}m.remove(E);p.setRng(y);p.onSetContent.dispatch(p,H);n.addVisual()},mceInsertRawHTML:function(z,y,x){p.setContent("tiny_mce_marker");n.setContent(n.getContent().replace(/tiny_mce_marker/g,function(){return x}))},mceSetContent:function(z,y,x){n.setContent(x)},"Indent,Outdent":function(A){var y,x,z;y=k.indentation;x=/[a-z%]+$/i.exec(y);y=parseInt(y);if(!l("InsertUnorderedList")&&!l("InsertOrderedList")){if(!k.forced_root_block&&!m.getParent(p.getNode(),m.isBlock)){q.apply("div")}e(p.getSelectedBlocks(),function(B){if(A=="outdent"){z=Math.max(0,parseInt(B.style.paddingLeft||0)-y);m.setStyle(B,"paddingLeft",z?z+x:"")}else{m.setStyle(B,"paddingLeft",(parseInt(B.style.paddingLeft||0)+y)+x)}})}else{f(A)}},mceRepaint:function(){var y;if(d.isGecko){try{i(a);if(p.getSel()){p.getSel().selectAllChildren(n.getBody())}p.collapse(a);g()}catch(x){}}},mceToggleFormat:function(z,y,x){q.toggle(x)},InsertHorizontalRule:function(){n.execCommand("mceInsertContent",false,"<hr />")},mceToggleVisualAid:function(){n.hasVisual=!n.hasVisual;n.addVisual()},mceReplaceContent:function(z,y,x){n.execCommand("mceInsertContent",false,x.replace(/\{\$selection\}/g,p.getContent({format:"text"})))},mceInsertLink:function(A,z,y){var x;if(typeof(y)=="string"){y={href:y}}x=m.getParent(p.getNode(),"a");y.href=y.href.replace(" ","%20");if(!x||!y.href){q.remove("link")}if(y.href){q.apply("link",y,x)}},selectAll:function(){var y=m.getRoot(),x=m.createRng();x.setStart(y,0);x.setEnd(y,y.childNodes.length);n.selection.setRng(x)}});v({"JustifyLeft,JustifyCenter,JustifyRight,JustifyFull":function(A){var y="align"+A.substring(7);var x=p.isCollapsed()?[p.getNode()]:p.getSelectedBlocks();var z=d.map(x,function(B){return !!q.matchNode(B,y)});return d.inArray(z,a)!==-1},"Bold,Italic,Underline,Strikethrough,Superscript,Subscript":function(x){return u(x)},mceBlockQuote:function(){return u("blockquote")},Outdent:function(){var x;if(k.inline_styles){if((x=m.getParent(p.getStart(),m.isBlock))&&parseInt(x.style.paddingLeft)>0){return a}if((x=m.getParent(p.getEnd(),m.isBlock))&&parseInt(x.style.paddingLeft)>0){return a}}return l("InsertUnorderedList")||l("InsertOrderedList")||(!k.inline_styles&&!!m.getParent(p.getNode(),"BLOCKQUOTE"))},"InsertUnorderedList,InsertOrderedList":function(x){return m.getParent(p.getNode(),x=="insertunorderedlist"?"UL":"OL")}},"state");v({"FontSize,FontName":function(z){var y=0,x;if(x=m.getParent(p.getNode(),"span")){if(z=="fontsize"){y=x.style.fontSize}else{y=x.style.fontFamily.replace(/, /g,",").replace(/[\'\"]/g,"").toLowerCase()}}return y}},"value");v({Undo:function(){n.undoManager.undo()},Redo:function(){n.undoManager.redo()}})}})(tinymce);(function(b){var a=b.util.Dispatcher;b.UndoManager=function(h){var l,i=0,e=[],g,k,j,f;function c(){return b.trim(h.getContent({format:"raw",no_events:1}).replace(/<span[^>]+data-mce-bogus[^>]+>[\u200B\uFEFF]+<\/span>/g,""))}function d(){l.typing=false;l.add()}k=new a(l);j=new a(l);f=new a(l);k.add(function(m,n){if(m.hasUndo()){return h.onChange.dispatch(h,n,m)}});j.add(function(m,n){return h.onUndo.dispatch(h,n,m)});f.add(function(m,n){return h.onRedo.dispatch(h,n,m)});h.onInit.add(function(){l.add()});h.onBeforeExecCommand.add(function(m,p,o,q,n){if(p!="Undo"&&p!="Redo"&&p!="mceRepaint"&&(!n||!n.skip_undo)){l.beforeChange()}});h.onExecCommand.add(function(m,p,o,q,n){if(p!="Undo"&&p!="Redo"&&p!="mceRepaint"&&(!n||!n.skip_undo)){l.add()}});h.onSaveContent.add(d);h.dom.bind(h.dom.getRoot(),"dragend",d);h.dom.bind(h.getDoc(),b.isGecko?"blur":"focusout",function(m){if(!h.removed&&l.typing){d()}});h.onKeyUp.add(function(m,o){var n=o.keyCode;if((n>=33&&n<=36)||(n>=37&&n<=40)||n==45||n==13||o.ctrlKey){d()}});h.onKeyDown.add(function(m,o){var n=o.keyCode;if((n>=33&&n<=36)||(n>=37&&n<=40)||n==45){if(l.typing){d()}return}if((n<16||n>20)&&n!=224&&n!=91&&!l.typing){l.beforeChange();l.typing=true;l.add()}});h.onMouseDown.add(function(m,n){if(l.typing){d()}});h.addShortcut("ctrl+z","undo_desc","Undo");h.addShortcut("ctrl+y","redo_desc","Redo");l={data:e,typing:false,onAdd:k,onUndo:j,onRedo:f,beforeChange:function(){g=h.selection.getBookmark(2,true)},add:function(p){var m,n=h.settings,o;p=p||{};p.content=c();o=e[i];if(o&&o.content==p.content){return null}if(e[i]){e[i].beforeBookmark=g}if(n.custom_undo_redo_levels){if(e.length>n.custom_undo_redo_levels){for(m=0;m<e.length-1;m++){e[m]=e[m+1]}e.length--;i=e.length}}p.bookmark=h.selection.getBookmark(2,true);if(i<e.length-1){e.length=i+1}e.push(p);i=e.length-1;l.onAdd.dispatch(l,p);h.isNotDirty=0;return p},undo:function(){var n,m;if(l.typing){l.add();l.typing=false}if(i>0){n=e[--i];h.setContent(n.content,{format:"raw"});h.selection.moveToBookmark(n.beforeBookmark);l.onUndo.dispatch(l,n)}return n},redo:function(){var m;if(i<e.length-1){m=e[++i];h.setContent(m.content,{format:"raw"});h.selection.moveToBookmark(m.bookmark);l.onRedo.dispatch(l,m)}return m},clear:function(){e=[];i=0;l.typing=false},hasUndo:function(){return i>0||this.typing},hasRedo:function(){return i<e.length-1&&!this.typing}};return l}})(tinymce);tinymce.ForceBlocks=function(c){var b=c.settings,e=c.dom,a=c.selection,d=c.schema.getBlockElements();function f(){var j=a.getStart(),h=c.getBody(),g,k,o,q,p,i,l,m=-16777215;if(!j||j.nodeType!==1||!b.forced_root_block){return}while(j!=h){if(d[j.nodeName]){return}j=j.parentNode}g=a.getRng();if(g.setStart){k=g.startContainer;o=g.startOffset;q=g.endContainer;p=g.endOffset}else{if(g.item){j=g.item(0);g=c.getDoc().body.createTextRange();g.moveToElementText(j)}tmpRng=g.duplicate();tmpRng.collapse(true);o=tmpRng.move("character",m)*-1;if(!tmpRng.collapsed){tmpRng=g.duplicate();tmpRng.collapse(false);p=(tmpRng.move("character",m)*-1)-o}}j=h.firstChild;while(j){if(j.nodeType===3||(j.nodeType==1&&!d[j.nodeName])){if(!i){i=e.create(b.forced_root_block);j.parentNode.insertBefore(i,j)}l=j;j=j.nextSibling;i.appendChild(l)}else{i=null;j=j.nextSibling}}if(g.setStart){g.setStart(k,o);g.setEnd(q,p);a.setRng(g)}else{try{g=c.getDoc().body.createTextRange();g.moveToElementText(h);g.collapse(true);g.moveStart("character",o);if(p>0){g.moveEnd("character",p)}g.select()}catch(n){}}c.nodeChanged()}if(b.forced_root_block){c.onKeyUp.add(f);c.onClick.add(f)}};(function(c){var b=c.DOM,a=c.dom.Event,d=c.each,e=c.extend;c.create("tinymce.ControlManager",{ControlManager:function(f,j){var h=this,g;j=j||{};h.editor=f;h.controls={};h.onAdd=new c.util.Dispatcher(h);h.onPostRender=new c.util.Dispatcher(h);h.prefix=j.prefix||f.id+"_";h._cls={};h.onPostRender.add(function(){d(h.controls,function(i){i.postRender()})})},get:function(f){return this.controls[this.prefix+f]||this.controls[f]},setActive:function(h,f){var g=null;if(g=this.get(h)){g.setActive(f)}return g},setDisabled:function(h,f){var g=null;if(g=this.get(h)){g.setDisabled(f)}return g},add:function(g){var f=this;if(g){f.controls[g.id]=g;f.onAdd.dispatch(g,f)}return g},createControl:function(i){var h,g=this,f=g.editor;d(f.plugins,function(j){if(j.createControl){h=j.createControl(i,g);if(h){return false}}});switch(i){case"|":case"separator":return g.createSeparator()}if(!h&&f.buttons&&(h=f.buttons[i])){return g.createButton(i,h)}return g.add(h)},createDropMenu:function(f,n,h){var m=this,i=m.editor,j,g,k,l;n=e({"class":"mceDropDown",constrain:i.settings.constrain_menus},n);n["class"]=n["class"]+" "+i.getParam("skin")+"Skin";if(k=i.getParam("skin_variant")){n["class"]+=" "+i.getParam("skin")+"Skin"+k.substring(0,1).toUpperCase()+k.substring(1)}f=m.prefix+f;l=h||m._cls.dropmenu||c.ui.DropMenu;j=m.controls[f]=new l(f,n);j.onAddItem.add(function(r,q){var p=q.settings;p.title=i.getLang(p.title,p.title);if(!p.onclick){p.onclick=function(o){if(p.cmd){i.execCommand(p.cmd,p.ui||false,p.value)}}}});i.onRemove.add(function(){j.destroy()});if(c.isIE){j.onShowMenu.add(function(){i.focus();g=i.selection.getBookmark(1)});j.onHideMenu.add(function(){if(g){i.selection.moveToBookmark(g);g=0}})}return m.add(j)},createListBox:function(f,n,h){var l=this,j=l.editor,i,k,m;if(l.get(f)){return null}n.title=j.translate(n.title);n.scope=n.scope||j;if(!n.onselect){n.onselect=function(o){j.execCommand(n.cmd,n.ui||false,o||n.value)}}n=e({title:n.title,"class":"mce_"+f,scope:n.scope,control_manager:l},n);f=l.prefix+f;function g(o){return o.settings.use_accessible_selects&&!c.isGecko}if(j.settings.use_native_selects||g(j)){k=new c.ui.NativeListBox(f,n)}else{m=h||l._cls.listbox||c.ui.ListBox;k=new m(f,n,j)}l.controls[f]=k;if(c.isWebKit){k.onPostRender.add(function(p,o){a.add(o,"mousedown",function(){j.bookmark=j.selection.getBookmark(1)});a.add(o,"focus",function(){j.selection.moveToBookmark(j.bookmark);j.bookmark=null})})}if(k.hideMenu){j.onMouseDown.add(k.hideMenu,k)}return l.add(k)},createButton:function(m,i,l){var h=this,g=h.editor,j,k,f;if(h.get(m)){return null}i.title=g.translate(i.title);i.label=g.translate(i.label);i.scope=i.scope||g;if(!i.onclick&&!i.menu_button){i.onclick=function(){g.execCommand(i.cmd,i.ui||false,i.value)}}i=e({title:i.title,"class":"mce_"+m,unavailable_prefix:g.getLang("unavailable",""),scope:i.scope,control_manager:h},i);m=h.prefix+m;if(i.menu_button){f=l||h._cls.menubutton||c.ui.MenuButton;k=new f(m,i,g);g.onMouseDown.add(k.hideMenu,k)}else{f=h._cls.button||c.ui.Button;k=new f(m,i,g)}return h.add(k)},createMenuButton:function(h,f,g){f=f||{};f.menu_button=1;return this.createButton(h,f,g)},createSplitButton:function(m,i,l){var h=this,g=h.editor,j,k,f;if(h.get(m)){return null}i.title=g.translate(i.title);i.scope=i.scope||g;if(!i.onclick){i.onclick=function(n){g.execCommand(i.cmd,i.ui||false,n||i.value)}}if(!i.onselect){i.onselect=function(n){g.execCommand(i.cmd,i.ui||false,n||i.value)}}i=e({title:i.title,"class":"mce_"+m,scope:i.scope,control_manager:h},i);m=h.prefix+m;f=l||h._cls.splitbutton||c.ui.SplitButton;k=h.add(new f(m,i,g));g.onMouseDown.add(k.hideMenu,k);return k},createColorSplitButton:function(f,n,h){var l=this,j=l.editor,i,k,m,g;if(l.get(f)){return null}n.title=j.translate(n.title);n.scope=n.scope||j;if(!n.onclick){n.onclick=function(o){if(c.isIE){g=j.selection.getBookmark(1)}j.execCommand(n.cmd,n.ui||false,o||n.value)}}if(!n.onselect){n.onselect=function(o){j.execCommand(n.cmd,n.ui||false,o||n.value)}}n=e({title:n.title,"class":"mce_"+f,menu_class:j.getParam("skin")+"Skin",scope:n.scope,more_colors_title:j.getLang("more_colors")},n);f=l.prefix+f;m=h||l._cls.colorsplitbutton||c.ui.ColorSplitButton;k=new m(f,n,j);j.onMouseDown.add(k.hideMenu,k);j.onRemove.add(function(){k.destroy()});if(c.isIE){k.onShowMenu.add(function(){j.focus();g=j.selection.getBookmark(1)});k.onHideMenu.add(function(){if(g){j.selection.moveToBookmark(g);g=0}})}return l.add(k)},createToolbar:function(k,h,j){var i,g=this,f;k=g.prefix+k;f=j||g._cls.toolbar||c.ui.Toolbar;i=new f(k,h,g.editor);if(g.get(k)){return null}return g.add(i)},createToolbarGroup:function(k,h,j){var i,g=this,f;k=g.prefix+k;f=j||this._cls.toolbarGroup||c.ui.ToolbarGroup;i=new f(k,h,g.editor);if(g.get(k)){return null}return g.add(i)},createSeparator:function(g){var f=g||this._cls.separator||c.ui.Separator;return new f()},setControlType:function(g,f){return this._cls[g.toLowerCase()]=f},destroy:function(){d(this.controls,function(f){f.destroy()});this.controls=null}})})(tinymce);(function(d){var a=d.util.Dispatcher,e=d.each,c=d.isIE,b=d.isOpera;d.create("tinymce.WindowManager",{WindowManager:function(f){var g=this;g.editor=f;g.onOpen=new a(g);g.onClose=new a(g);g.params={};g.features={}},open:function(z,h){var v=this,k="",n,m,i=v.editor.settings.dialog_type=="modal",q,o,j,g=d.DOM.getViewPort(),r;z=z||{};h=h||{};o=b?g.w:screen.width;j=b?g.h:screen.height;z.name=z.name||"mc_"+new Date().getTime();z.width=parseInt(z.width||320);z.height=parseInt(z.height||240);z.resizable=true;z.left=z.left||parseInt(o/2)-(z.width/2);z.top=z.top||parseInt(j/2)-(z.height/2);h.inline=false;h.mce_width=z.width;h.mce_height=z.height;h.mce_auto_focus=z.auto_focus;if(i){if(c){z.center=true;z.help=false;z.dialogWidth=z.width+"px";z.dialogHeight=z.height+"px";z.scroll=z.scrollbars||false}}e(z,function(p,f){if(d.is(p,"boolean")){p=p?"yes":"no"}if(!/^(name|url)$/.test(f)){if(c&&i){k+=(k?";":"")+f+":"+p}else{k+=(k?",":"")+f+"="+p}}});v.features=z;v.params=h;v.onOpen.dispatch(v,z,h);r=z.url||z.file;r=d._addVer(r);try{if(c&&i){q=1;window.showModalDialog(r,window,k)}else{q=window.open(r,z.name,k)}}catch(l){}if(!q){alert(v.editor.getLang("popup_blocked"))}},close:function(f){f.close();this.onClose.dispatch(this)},createInstance:function(i,h,g,m,l,k){var j=d.resolve(i);return new j(h,g,m,l,k)},confirm:function(h,f,i,g){g=g||window;f.call(i||this,g.confirm(this._decode(this.editor.getLang(h,h))))},alert:function(h,f,j,g){var i=this;g=g||window;g.alert(i._decode(i.editor.getLang(h,h)));if(f){f.call(j||i)}},resizeBy:function(f,g,h){h.resizeBy(f,g)},_decode:function(f){return d.DOM.decode(f).replace(/\\n/g,"\n")}})}(tinymce));(function(a){a.Formatter=function(Z){var P={},S=a.each,c=Z.dom,r=Z.selection,u=a.dom.TreeWalker,N=new a.dom.RangeUtils(c),d=Z.schema.isValidChild,I=c.isBlock,m=Z.settings.forced_root_block,s=c.nodeIndex,H=a.isGecko?"\u200B":"\uFEFF",e=/^(src|href|style)$/,W=false,D=true,E,y=c.getContentEditable;function B(aa){return aa instanceof Array}function n(ab,aa){return c.getParents(ab,aa,c.getRoot())}function b(aa){return aa.nodeType===1&&aa.id==="_mce_caret"}function j(){l({alignleft:[{selector:"figure,p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li",styles:{textAlign:"left"},defaultBlock:"div"},{selector:"img,table",collapsed:false,styles:{"float":"left"}}],aligncenter:[{selector:"figure,p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li",styles:{textAlign:"center"},defaultBlock:"div"},{selector:"img",collapsed:false,styles:{display:"block",marginLeft:"auto",marginRight:"auto"}},{selector:"table",collapsed:false,styles:{marginLeft:"auto",marginRight:"auto"}}],alignright:[{selector:"figure,p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li",styles:{textAlign:"right"},defaultBlock:"div"},{selector:"img,table",collapsed:false,styles:{"float":"right"}}],alignfull:[{selector:"figure,p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li",styles:{textAlign:"justify"},defaultBlock:"div"}],bold:[{inline:"strong",remove:"all"},{inline:"span",styles:{fontWeight:"bold"}},{inline:"b",remove:"all"}],italic:[{inline:"em",remove:"all"},{inline:"span",styles:{fontStyle:"italic"}},{inline:"i",remove:"all"}],underline:[{inline:"span",styles:{textDecoration:"underline"},exact:true},{inline:"u",remove:"all"}],strikethrough:[{inline:"span",styles:{textDecoration:"line-through"},exact:true},{inline:"strike",remove:"all"}],forecolor:{inline:"span",styles:{color:"%value"},wrap_links:false},hilitecolor:{inline:"span",styles:{backgroundColor:"%value"},wrap_links:false},fontname:{inline:"span",styles:{fontFamily:"%value"}},fontsize:{inline:"span",styles:{fontSize:"%value"}},fontsize_class:{inline:"span",attributes:{"class":"%value"}},blockquote:{block:"blockquote",wrapper:1,remove:"all"},subscript:{inline:"sub"},superscript:{inline:"sup"},link:{inline:"a",selector:"a",remove:"all",split:true,deep:true,onmatch:function(aa){return true},onformat:function(ac,aa,ab){S(ab,function(ae,ad){c.setAttrib(ac,ad,ae)})}},removeformat:[{selector:"b,strong,em,i,font,u,strike",remove:"all",split:true,expand:false,block_expand:true,deep:true},{selector:"span",attributes:["style","class"],remove:"empty",split:true,expand:false,deep:true},{selector:"*",attributes:["style","class"],split:false,expand:false,deep:true}]});S("p h1 h2 h3 h4 h5 h6 div address pre div code dt dd samp".split(/\s/),function(aa){l(aa,{block:aa,remove:"all"})});l(Z.settings.formats)}function V(){Z.addShortcut("ctrl+b","bold_desc","Bold");Z.addShortcut("ctrl+i","italic_desc","Italic");Z.addShortcut("ctrl+u","underline_desc","Underline");for(var aa=1;aa<=6;aa++){Z.addShortcut("ctrl+"+aa,"",["FormatBlock",false,"h"+aa])}Z.addShortcut("ctrl+7","",["FormatBlock",false,"p"]);Z.addShortcut("ctrl+8","",["FormatBlock",false,"div"]);Z.addShortcut("ctrl+9","",["FormatBlock",false,"address"])}function U(aa){return aa?P[aa]:P}function l(aa,ab){if(aa){if(typeof(aa)!=="string"){S(aa,function(ad,ac){l(ac,ad)})}else{ab=ab.length?ab:[ab];S(ab,function(ac){if(ac.deep===E){ac.deep=!ac.selector}if(ac.split===E){ac.split=!ac.selector||ac.inline}if(ac.remove===E&&ac.selector&&!ac.inline){ac.remove="none"}if(ac.selector&&ac.inline){ac.mixed=true;ac.block_expand=true}if(typeof(ac.classes)==="string"){ac.classes=ac.classes.split(/\s+/)}});P[aa]=ab}}}var i=function(ab){var aa;Z.dom.getParent(ab,function(ac){aa=Z.dom.getStyle(ac,"text-decoration");return aa&&aa!=="none"});return aa};var L=function(aa){var ab;if(aa.nodeType===1&&aa.parentNode&&aa.parentNode.nodeType===1){ab=i(aa.parentNode);if(Z.dom.getStyle(aa,"color")&&ab){Z.dom.setStyle(aa,"text-decoration",ab)}else{if(Z.dom.getStyle(aa,"textdecoration")===ab){Z.dom.setStyle(aa,"text-decoration",null)}}}};function X(ad,ak,af){var ag=U(ad),al=ag[0],aj,ab,ai,ah=r.isCollapsed();function aa(ap,ao){ao=ao||al;if(ap){if(ao.onformat){ao.onformat(ap,ao,ak,af)}S(ao.styles,function(ar,aq){c.setStyle(ap,aq,q(ar,ak))});S(ao.attributes,function(ar,aq){c.setAttrib(ap,aq,q(ar,ak))});S(ao.classes,function(aq){aq=q(aq,ak);if(!c.hasClass(ap,aq)){c.addClass(ap,aq)}})}}function ae(){function aq(ax,av){var aw=new u(av);for(af=aw.current();af;af=aw.prev()){if(af.childNodes.length>1||af==ax||af.tagName=="BR"){return af}}}var ap=Z.selection.getRng();var au=ap.startContainer;var ao=ap.endContainer;if(au!=ao&&ap.endOffset===0){var at=aq(au,ao);var ar=at.nodeType==3?at.length:at.childNodes.length;ap.setEnd(at,ar)}return ap}function ac(ar,ax,av,au,ap){var ao=[],aq=-1,aw,az=-1,at=-1,ay;S(ar.childNodes,function(aB,aA){if(aB.nodeName==="UL"||aB.nodeName==="OL"){aq=aA;aw=aB;return false}});S(ar.childNodes,function(aB,aA){if(aB.nodeName==="SPAN"&&c.getAttrib(aB,"data-mce-type")=="bookmark"){if(aB.id==ax.id+"_start"){az=aA}else{if(aB.id==ax.id+"_end"){at=aA}}}});if(aq<=0||(az<aq&&at>aq)){S(a.grep(ar.childNodes),ap);return 0}else{ay=c.clone(av,W);S(a.grep(ar.childNodes),function(aB,aA){if((az<aq&&aA<aq)||(az>aq&&aA>aq)){ao.push(aB);aB.parentNode.removeChild(aB)}});if(az<aq){ar.insertBefore(ay,aw)}else{if(az>aq){ar.insertBefore(ay,aw.nextSibling)}}au.push(ay);S(ao,function(aA){ay.appendChild(aA)});return ay}}function am(ap,ar,av){var ao=[],au,aq,at=true;au=al.inline||al.block;aq=c.create(au);aa(aq);N.walk(ap,function(aw){var ax;function ay(az){var aE,aC,aA,aB,aD;aD=at;aE=az.nodeName.toLowerCase();aC=az.parentNode.nodeName.toLowerCase();if(az.nodeType===1&&y(az)){aD=at;at=y(az)==="true";aB=true}if(g(aE,"br")){ax=0;if(al.block){c.remove(az)}return}if(al.wrapper&&z(az,ad,ak)){ax=0;return}if(at&&!aB&&al.block&&!al.wrapper&&J(aE)){az=c.rename(az,au);aa(az);ao.push(az);ax=0;return}if(al.selector){S(ag,function(aF){if("collapsed" in aF&&aF.collapsed!==ah){return}if(c.is(az,aF.selector)&&!b(az)){aa(az,aF);aA=true}});if(!al.inline||aA){ax=0;return}}if(at&&!aB&&d(au,aE)&&d(aC,au)&&!(!av&&az.nodeType===3&&az.nodeValue.length===1&&az.nodeValue.charCodeAt(0)===65279)&&!b(az)){if(!ax){ax=c.clone(aq,W);az.parentNode.insertBefore(ax,az);ao.push(ax)}ax.appendChild(az)}else{if(aE=="li"&&ar){ax=ac(az,ar,aq,ao,ay)}else{ax=0;S(a.grep(az.childNodes),ay);if(aB){at=aD}ax=0}}}S(aw,ay)});if(al.wrap_links===false){S(ao,function(aw){function ax(aB){var aA,az,ay;if(aB.nodeName==="A"){az=c.clone(aq,W);ao.push(az);ay=a.grep(aB.childNodes);for(aA=0;aA<ay.length;aA++){az.appendChild(ay[aA])}aB.appendChild(az)}S(a.grep(aB.childNodes),ax)}ax(aw)})}S(ao,function(ay){var aw;function az(aB){var aA=0;S(aB.childNodes,function(aC){if(!f(aC)&&!K(aC)){aA++}});return aA}function ax(aA){var aC,aB;S(aA.childNodes,function(aD){if(aD.nodeType==1&&!K(aD)&&!b(aD)){aC=aD;return W}});if(aC&&h(aC,al)){aB=c.clone(aC,W);aa(aB);c.replace(aB,aA,D);c.remove(aC,1)}return aB||aA}aw=az(ay);if((ao.length>1||!I(ay))&&aw===0){c.remove(ay,1);return}if(al.inline||al.wrapper){if(!al.exact&&aw===1){ay=ax(ay)}S(ag,function(aA){S(c.select(aA.inline,ay),function(aC){var aB;if(aA.wrap_links===false){aB=aC.parentNode;do{if(aB.nodeName==="A"){return}}while(aB=aB.parentNode)}Y(aA,ak,aC,aA.exact?aC:null)})});if(z(ay.parentNode,ad,ak)){c.remove(ay,1);ay=0;return D}if(al.merge_with_parents){c.getParent(ay.parentNode,function(aA){if(z(aA,ad,ak)){c.remove(ay,1);ay=0;return D}})}if(ay&&al.merge_siblings!==false){ay=v(F(ay),ay);ay=v(ay,F(ay,D))}}})}if(al){if(af){if(af.nodeType){ab=c.createRng();ab.setStartBefore(af);ab.setEndAfter(af);am(p(ab,ag),null,true)}else{am(af,null,true)}}else{if(!ah||!al.inline||c.select("td.mceSelected,th.mceSelected").length){var an=Z.selection.getNode();if(!m&&ag[0].defaultBlock&&!c.getParent(an,c.isBlock)){X(ag[0].defaultBlock)}Z.selection.setRng(ae());aj=r.getBookmark();am(p(r.getRng(D),ag),aj);if(al.styles&&(al.styles.color||al.styles.textDecoration)){a.walk(an,L,"childNodes");L(an)}r.moveToBookmark(aj);Q(r.getRng(D));Z.nodeChanged()}else{T("apply",ad,ak)}}}}function C(ac,al,ae){var af=U(ac),an=af[0],aj,ai,ab,ak=true;function ad(au){var at,ar,aq,ap,aw,av;if(au.nodeType===1&&y(au)){aw=ak;ak=y(au)==="true";av=true}at=a.grep(au.childNodes);if(ak&&!av){for(ar=0,aq=af.length;ar<aq;ar++){if(Y(af[ar],al,au,au)){break}}}if(an.deep){if(at.length){for(ar=0,aq=at.length;ar<aq;ar++){ad(at[ar])}if(av){ak=aw}}}}function ag(ap){var aq;S(n(ap.parentNode).reverse(),function(ar){var at;if(!aq&&ar.id!="_start"&&ar.id!="_end"){at=z(ar,ac,al);if(at&&at.split!==false){aq=ar}}});return aq}function aa(at,ap,av,ay){var az,ax,aw,ar,au,aq;if(at){aq=at.parentNode;for(az=ap.parentNode;az&&az!=aq;az=az.parentNode){ax=c.clone(az,W);for(au=0;au<af.length;au++){if(Y(af[au],al,ax,ax)){ax=0;break}}if(ax){if(aw){ax.appendChild(aw)}if(!ar){ar=ax}aw=ax}}if(ay&&(!an.mixed||!I(at))){ap=c.split(at,ap)}if(aw){av.parentNode.insertBefore(aw,av);ar.appendChild(av)}}return ap}function am(ap){return aa(ag(ap),ap,ap,true)}function ah(ar){var aq=c.get(ar?"_start":"_end"),ap=aq[ar?"firstChild":"lastChild"];if(K(ap)){ap=ap[ar?"firstChild":"lastChild"]}c.remove(aq,true);return ap}function ao(ap){var ar,at,aq;ap=p(ap,af,D);if(an.split){ar=M(ap,D);at=M(ap);if(ar!=at){aq=ar.firstChild;if(ar.nodeName=="TD"&&aq){ar=aq}ar=R(ar,"span",{id:"_start","data-mce-type":"bookmark"});at=R(at,"span",{id:"_end","data-mce-type":"bookmark"});am(ar);am(at);ar=ah(D);at=ah()}else{ar=at=am(ar)}ap.startContainer=ar.parentNode;ap.startOffset=s(ar);ap.endContainer=at.parentNode;ap.endOffset=s(at)+1}N.walk(ap,function(au){S(au,function(av){ad(av);if(av.nodeType===1&&Z.dom.getStyle(av,"text-decoration")==="underline"&&av.parentNode&&i(av.parentNode)==="underline"){Y({deep:false,exact:true,inline:"span",styles:{textDecoration:"underline"}},null,av)}})})}if(ae){if(ae.nodeType){ab=c.createRng();ab.setStartBefore(ae);ab.setEndAfter(ae);ao(ab)}else{ao(ae)}return}if(!r.isCollapsed()||!an.inline||c.select("td.mceSelected,th.mceSelected").length){aj=r.getBookmark();ao(r.getRng(D));r.moveToBookmark(aj);if(an.inline&&k(ac,al,r.getStart())){Q(r.getRng(true))}Z.nodeChanged()}else{T("remove",ac,al)}}function G(ab,ad,ac){var aa=U(ab);if(k(ab,ad,ac)&&(!("toggle" in aa[0])||aa[0].toggle)){C(ab,ad,ac)}else{X(ab,ad,ac)}}function z(ab,aa,ag,ae){var ac=U(aa),ah,af,ad;function ai(am,ao,ap){var al,an,aj=ao[ap],ak;if(ao.onmatch){return ao.onmatch(am,ao,ap)}if(aj){if(aj.length===E){for(al in aj){if(aj.hasOwnProperty(al)){if(ap==="attributes"){an=c.getAttrib(am,al)}else{an=O(am,al)}if(ae&&!an&&!ao.exact){return}if((!ae||ao.exact)&&!g(an,q(aj[al],ag))){return}}}}else{for(ak=0;ak<aj.length;ak++){if(ap==="attributes"?c.getAttrib(am,aj[ak]):O(am,aj[ak])){return ao}}}}return ao}if(ac&&ab){for(af=0;af<ac.length;af++){ah=ac[af];if(h(ab,ah)&&ai(ab,ah,"attributes")&&ai(ab,ah,"styles")){if(ad=ah.classes){for(af=0;af<ad.length;af++){if(!c.hasClass(ab,ad[af])){return}}}return ah}}}}function k(ac,ae,ad){var ab;function aa(af){af=c.getParent(af,function(ag){return !!z(ag,ac,ae,true)});return z(af,ac,ae)}if(ad){return aa(ad)}ad=r.getNode();if(aa(ad)){return D}ab=r.getStart();if(ab!=ad){if(aa(ab)){return D}}return W}function x(ah,ag){var ae,af=[],ad={},ac,ab,aa;ae=r.getStart();c.getParent(ae,function(ak){var aj,ai;for(aj=0;aj<ah.length;aj++){ai=ah[aj];if(!ad[ai]&&z(ak,ai,ag)){ad[ai]=true;af.push(ai)}}});return af}function A(ae){var ag=U(ae),ad,ac,af,ab,aa;if(ag){ad=r.getStart();ac=n(ad);for(ab=ag.length-1;ab>=0;ab--){aa=ag[ab].selector;if(!aa){return D}for(af=ac.length-1;af>=0;af--){if(c.is(ac[af],aa)){return D}}}}return W}a.extend(this,{get:U,register:l,apply:X,remove:C,toggle:G,match:k,matchAll:x,matchNode:z,canApply:A});j();V();function h(aa,ab){if(g(aa,ab.inline)){return D}if(g(aa,ab.block)){return D}if(ab.selector){return c.is(aa,ab.selector)}}function g(ab,aa){ab=ab||"";aa=aa||"";ab=""+(ab.nodeName||ab);aa=""+(aa.nodeName||aa);return ab.toLowerCase()==aa.toLowerCase()}function O(ab,aa){var ac=c.getStyle(ab,aa);if(aa=="color"||aa=="backgroundColor"){ac=c.toHex(ac)}if(aa=="fontWeight"&&ac==700){ac="bold"}return""+ac}function q(aa,ab){if(typeof(aa)!="string"){aa=aa(ab)}else{if(ab){aa=aa.replace(/%(\w+)/g,function(ad,ac){return ab[ac]||ad})}}return aa}function f(aa){return aa&&aa.nodeType===3&&/^([\t \r\n]+|)$/.test(aa.nodeValue)}function R(ac,ab,aa){var ad=c.create(ab,aa);ac.parentNode.insertBefore(ad,ac);ad.appendChild(ac);return ad}function p(aa,al,ad){var ao,am,ag,ak,ac=aa.startContainer,ah=aa.startOffset,aq=aa.endContainer,aj=aa.endOffset;function an(ay){var at,aw,ax,av,au,ar;at=aw=ay?ac:aq;au=ay?"previousSibling":"nextSibling";ar=c.getRoot();if(at.nodeType==3&&!f(at)){if(ay?ah>0:aj<at.nodeValue.length){return at}}for(;;){if(!al[0].block_expand&&I(aw)){return aw}for(av=aw[au];av;av=av[au]){if(!K(av)&&!f(av)){return aw}}if(aw.parentNode==ar){at=aw;break}aw=aw.parentNode}return at}function af(ar,at){if(at===E){at=ar.nodeType===3?ar.length:ar.childNodes.length}while(ar&&ar.hasChildNodes()){ar=ar.childNodes[at];if(ar){at=ar.nodeType===3?ar.length:ar.childNodes.length}}return{node:ar,offset:at}}if(ac.nodeType==1&&ac.hasChildNodes()){am=ac.childNodes.length-1;ac=ac.childNodes[ah>am?am:ah];if(ac.nodeType==3){ah=0}}if(aq.nodeType==1&&aq.hasChildNodes()){am=aq.childNodes.length-1;aq=aq.childNodes[aj>am?am:aj-1];if(aq.nodeType==3){aj=aq.nodeValue.length}}function ap(at){var ar=at;while(ar){if(ar.nodeType===1&&y(ar)){return y(ar)==="false"?ar:at}ar=ar.parentNode}return at}function ai(at,ax,az){var aw,au,ay,ar;function av(aB,aD){var aE,aA,aC=aB.nodeValue;if(typeof(aD)=="undefined"){aD=az?aC.length:0}if(az){aE=aC.lastIndexOf(" ",aD);aA=aC.lastIndexOf("\u00a0",aD);aE=aE>aA?aE:aA;if(aE!==-1&&!ad){aE++}}else{aE=aC.indexOf(" ",aD);aA=aC.indexOf("\u00a0",aD);aE=aE!==-1&&(aA===-1||aE<aA)?aE:aA}return aE}if(at.nodeType===3){ay=av(at,ax);if(ay!==-1){return{container:at,offset:ay}}ar=at}aw=new u(at,c.getParent(at,I)||Z.getBody());while(au=aw[az?"prev":"next"]()){if(au.nodeType===3){ar=au;ay=av(au);if(ay!==-1){return{container:au,offset:ay}}}else{if(I(au)){break}}}if(ar){if(az){ax=0}else{ax=ar.length}return{container:ar,offset:ax}}}function ae(at,ar){var au,av,ax,aw;if(at.nodeType==3&&at.nodeValue.length===0&&at[ar]){at=at[ar]}au=n(at);for(av=0;av<au.length;av++){for(ax=0;ax<al.length;ax++){aw=al[ax];if("collapsed" in aw&&aw.collapsed!==aa.collapsed){continue}if(c.is(au[av],aw.selector)){return au[av]}}}return at}function ab(at,ar,av){var au;if(!al[0].wrapper){au=c.getParent(at,al[0].block)}if(!au){au=c.getParent(at.nodeType==3?at.parentNode:at,I)}if(au&&al[0].wrapper){au=n(au,"ul,ol").reverse()[0]||au}if(!au){au=at;while(au[ar]&&!I(au[ar])){au=au[ar];if(g(au,"br")){break}}}return au||at}ac=ap(ac);aq=ap(aq);if(K(ac.parentNode)||K(ac)){ac=K(ac)?ac:ac.parentNode;ac=ac.nextSibling||ac;if(ac.nodeType==3){ah=0}}if(K(aq.parentNode)||K(aq)){aq=K(aq)?aq:aq.parentNode;aq=aq.previousSibling||aq;if(aq.nodeType==3){aj=aq.length}}if(al[0].inline){if(aa.collapsed){ak=ai(ac,ah,true);if(ak){ac=ak.container;ah=ak.offset}ak=ai(aq,aj);if(ak){aq=ak.container;aj=ak.offset}}ag=af(aq,aj);if(ag.node){while(ag.node&&ag.offset===0&&ag.node.previousSibling){ag=af(ag.node.previousSibling)}if(ag.node&&ag.offset>0&&ag.node.nodeType===3&&ag.node.nodeValue.charAt(ag.offset-1)===" "){if(ag.offset>1){aq=ag.node;aq.splitText(ag.offset-1)}}}}if(al[0].inline||al[0].block_expand){if(!al[0].inline||(ac.nodeType!=3||ah===0)){ac=an(true)}if(!al[0].inline||(aq.nodeType!=3||aj===aq.nodeValue.length)){aq=an()}}if(al[0].selector&&al[0].expand!==W&&!al[0].inline){ac=ae(ac,"previousSibling");aq=ae(aq,"nextSibling")}if(al[0].block||al[0].selector){ac=ab(ac,"previousSibling");aq=ab(aq,"nextSibling");if(al[0].block){if(!I(ac)){ac=an(true)}if(!I(aq)){aq=an()}}}if(ac.nodeType==1){ah=s(ac);ac=ac.parentNode}if(aq.nodeType==1){aj=s(aq)+1;aq=aq.parentNode}return{startContainer:ac,startOffset:ah,endContainer:aq,endOffset:aj}}function Y(ag,af,ad,aa){var ac,ab,ae;if(!h(ad,ag)){return W}if(ag.remove!="all"){S(ag.styles,function(ai,ah){ai=q(ai,af);if(typeof(ah)==="number"){ah=ai;aa=0}if(!aa||g(O(aa,ah),ai)){c.setStyle(ad,ah,"")}ae=1});if(ae&&c.getAttrib(ad,"style")==""){ad.removeAttribute("style");ad.removeAttribute("data-mce-style")}S(ag.attributes,function(aj,ah){var ai;aj=q(aj,af);if(typeof(ah)==="number"){ah=aj;aa=0}if(!aa||g(c.getAttrib(aa,ah),aj)){if(ah=="class"){aj=c.getAttrib(ad,ah);if(aj){ai="";S(aj.split(/\s+/),function(ak){if(/mce\w+/.test(ak)){ai+=(ai?" ":"")+ak}});if(ai){c.setAttrib(ad,ah,ai);return}}}if(ah=="class"){ad.removeAttribute("className")}if(e.test(ah)){ad.removeAttribute("data-mce-"+ah)}ad.removeAttribute(ah)}});S(ag.classes,function(ah){ah=q(ah,af);if(!aa||c.hasClass(aa,ah)){c.removeClass(ad,ah)}});ab=c.getAttribs(ad);for(ac=0;ac<ab.length;ac++){if(ab[ac].nodeName.indexOf("_")!==0){return W}}}if(ag.remove!="none"){o(ad,ag);return D}}function o(ac,ad){var aa=ac.parentNode,ab;function ae(ag,af,ah){ag=F(ag,af,ah);return !ag||(ag.nodeName=="BR"||I(ag))}if(ad.block){if(!m){if(I(ac)&&!I(aa)){if(!ae(ac,W)&&!ae(ac.firstChild,D,1)){ac.insertBefore(c.create("br"),ac.firstChild)}if(!ae(ac,D)&&!ae(ac.lastChild,W,1)){ac.appendChild(c.create("br"))}}}else{if(aa==c.getRoot()){if(!ad.list_block||!g(ac,ad.list_block)){S(a.grep(ac.childNodes),function(af){if(d(m,af.nodeName.toLowerCase())){if(!ab){ab=R(af,m)}else{ab.appendChild(af)}}else{ab=0}})}}}}if(ad.selector&&ad.inline&&!g(ad.inline,ac)){return}c.remove(ac,1)}function F(ab,aa,ac){if(ab){aa=aa?"nextSibling":"previousSibling";for(ab=ac?ab:ab[aa];ab;ab=ab[aa]){if(ab.nodeType==1||!f(ab)){return ab}}}}function K(aa){return aa&&aa.nodeType==1&&aa.getAttribute("data-mce-type")=="bookmark"}function v(ae,ad){var aa,ac,ab;function ag(aj,ai){if(aj.nodeName!=ai.nodeName){return W}function ah(al){var am={};S(c.getAttribs(al),function(an){var ao=an.nodeName.toLowerCase();if(ao.indexOf("_")!==0&&ao!=="style"){am[ao]=c.getAttrib(al,ao)}});return am}function ak(ao,an){var am,al;for(al in ao){if(ao.hasOwnProperty(al)){am=an[al];if(am===E){return W}if(ao[al]!=am){return W}delete an[al]}}for(al in an){if(an.hasOwnProperty(al)){return W}}return D}if(!ak(ah(aj),ah(ai))){return W}if(!ak(c.parseStyle(c.getAttrib(aj,"style")),c.parseStyle(c.getAttrib(ai,"style")))){return W}return D}function af(ai,ah){for(ac=ai;ac;ac=ac[ah]){if(ac.nodeType==3&&ac.nodeValue.length!==0){return ai}if(ac.nodeType==1&&!K(ac)){return ac}}return ai}if(ae&&ad){ae=af(ae,"previousSibling");ad=af(ad,"nextSibling");if(ag(ae,ad)){for(ac=ae.nextSibling;ac&&ac!=ad;){ab=ac;ac=ac.nextSibling;ae.appendChild(ab)}c.remove(ad);S(a.grep(ad.childNodes),function(ah){ae.appendChild(ah)});return ae}}return ad}function J(aa){return/^(h[1-6]|p|div|pre|address|dl|dt|dd)$/.test(aa)}function M(ab,af){var aa,ae,ac,ad;aa=ab[af?"startContainer":"endContainer"];ae=ab[af?"startOffset":"endOffset"];if(aa.nodeType==1){ac=aa.childNodes.length-1;if(!af&&ae){ae--}aa=aa.childNodes[ae>ac?ac:ae]}if(aa.nodeType===3&&af&&ae>=aa.nodeValue.length){aa=new u(aa,Z.getBody()).next()||aa}if(aa.nodeType===3&&!af&&ae===0){aa=new u(aa,Z.getBody()).prev()||aa}return aa}function T(aj,aa,ah){var ak="_mce_caret",ab=Z.settings.caret_debug;function ac(an){var am=c.create("span",{id:ak,"data-mce-bogus":true,style:ab?"color:red":""});if(an){am.appendChild(Z.getDoc().createTextNode(H))}return am}function ai(an,am){while(an){if((an.nodeType===3&&an.nodeValue!==H)||an.childNodes.length>1){return false}if(am&&an.nodeType===1){am.push(an)}an=an.firstChild}return true}function af(am){while(am){if(am.id===ak){return am}am=am.parentNode}}function ae(am){var an;if(am){an=new u(am,am);for(am=an.current();am;am=an.next()){if(am.nodeType===3){return am}}}}function ad(ao,an){var ap,am;if(!ao){ao=af(r.getStart());if(!ao){while(ao=c.get(ak)){ad(ao,false)}}}else{am=r.getRng(true);if(ai(ao)){if(an!==false){am.setStartBefore(ao);am.setEndBefore(ao)}c.remove(ao)}else{ap=ae(ao);if(ap.nodeValue.charAt(0)===H){ap=ap.deleteData(0,1)}c.remove(ao,1)}r.setRng(am)}}function ag(){var ao,am,at,ar,ap,an,aq;ao=r.getRng(true);ar=ao.startOffset;an=ao.startContainer;aq=an.nodeValue;am=af(r.getStart());if(am){at=ae(am)}if(aq&&ar>0&&ar<aq.length&&/\w/.test(aq.charAt(ar))&&/\w/.test(aq.charAt(ar-1))){ap=r.getBookmark();ao.collapse(true);ao=p(ao,U(aa));ao=N.split(ao);X(aa,ah,ao);r.moveToBookmark(ap)}else{if(!am||at.nodeValue!==H){am=ac(true);at=am.firstChild;ao.insertNode(am);ar=1;X(aa,ah,am)}else{X(aa,ah,am)}r.setCursorLocation(at,ar)}}function al(){var am=r.getRng(true),an,ap,at,ar,ao,aw,av=[],aq,au;an=am.startContainer;ap=am.startOffset;ao=an;if(an.nodeType==3){if(ap!=an.nodeValue.length||an.nodeValue===H){ar=true}ao=ao.parentNode}while(ao){if(z(ao,aa,ah)){aw=ao;break}if(ao.nextSibling){ar=true}av.push(ao);ao=ao.parentNode}if(!aw){return}if(ar){at=r.getBookmark();am.collapse(true);am=p(am,U(aa),true);am=N.split(am);C(aa,ah,am);r.moveToBookmark(at)}else{au=ac();ao=au;for(aq=av.length-1;aq>=0;aq--){ao.appendChild(c.clone(av[aq],false));ao=ao.firstChild}ao.appendChild(c.doc.createTextNode(H));ao=ao.firstChild;c.insertAfter(au,aw);r.setCursorLocation(ao,1)}}if(!self._hasCaretEvents){Z.onBeforeGetContent.addToTop(function(){var am=[],an;if(ai(af(r.getStart()),am)){an=am.length;while(an--){c.setAttrib(am[an],"data-mce-bogus","1")}}});a.each("onMouseUp onKeyUp".split(" "),function(am){Z[am].addToTop(function(){ad()})});Z.onKeyDown.addToTop(function(am,ao){var an=ao.keyCode;if(an==8||an==37||an==39){ad(af(r.getStart()))}});self._hasCaretEvents=true}if(aj=="apply"){ag()}else{al()}}function Q(ab){var aa=ab.startContainer,ah=ab.startOffset,ad,ag,af,ac,ae;if(aa.nodeType==3&&ah>=aa.nodeValue.length){ah=s(aa);aa=aa.parentNode;ad=true}if(aa.nodeType==1){ac=aa.childNodes;aa=ac[Math.min(ah,ac.length-1)];ag=new u(aa,c.getParent(aa,c.isBlock));if(ah>ac.length-1||ad){ag.next()}for(af=ag.current();af;af=ag.next()){if(af.nodeType==3&&!f(af)){ae=c.create("a",null,H);af.parentNode.insertBefore(ae,af);ab.setStart(af,0);r.setRng(ab);c.remove(ae);return}}}}}})(tinymce);tinymce.onAddEditor.add(function(e,a){var d,h,g,c=a.settings;function b(j,i){e.each(i,function(l,k){if(l){g.setStyle(j,k,l)}});g.rename(j,"span")}function f(i,j){g=i.dom;if(c.convert_fonts_to_spans){e.each(g.select("font,u,strike",j.node),function(k){d[k.nodeName.toLowerCase()](a.dom,k)})}}if(c.inline_styles){h=e.explode(c.font_size_legacy_values);d={font:function(j,i){b(i,{backgroundColor:i.style.backgroundColor,color:i.color,fontFamily:i.face,fontSize:h[parseInt(i.size,10)-1]})},u:function(j,i){b(i,{textDecoration:"underline"})},strike:function(j,i){b(i,{textDecoration:"line-through"})}};a.onPreProcess.add(f);a.onSetContent.add(f);a.onInit.add(function(){a.selection.onSetContent.add(f)})}});(function(b){var a=b.dom.TreeWalker;b.EnterKey=function(e){var h=e.dom,d=e.selection,c=e.settings,g=e.undoManager;function f(z){var v=d.getRng(true),D,i,y,u,o,I,n,j,l,s,F,x,A;function C(J){return J&&h.isBlock(J)&&!/^(TD|TH|CAPTION)$/.test(J.nodeName)&&!/^(fixed|absolute)/i.test(J.style.position)&&h.getContentEditable(J)!=="true"}function m(K){var P,N,J,Q,O,M=K,L;J=h.createRng();if(K.hasChildNodes()){P=new a(K,K);while(N=P.current()){if(N.nodeType==3){J.setStart(N,0);J.setEnd(N,0);break}if(/^(BR|IMG)$/.test(N.nodeName)){J.setStartBefore(N);J.setEndBefore(N);break}M=N;N=P.next()}if(!N){J.setStart(M,0);J.setEnd(M,0)}}else{if(K.nodeName=="BR"){if(K.nextSibling&&h.isBlock(K.nextSibling)){if(!I||I<9){L=h.create("br");K.parentNode.insertBefore(L,K)}J.setStartBefore(K);J.setEndBefore(K)}else{J.setStartAfter(K);J.setEndAfter(K)}}else{J.setStart(K,0);J.setEnd(K,0)}}d.setRng(J);h.remove(L);O=h.getViewPort(e.getWin());Q=h.getPos(K).y;if(Q<O.y||Q+25>O.y+O.h){e.getWin().scrollTo(0,Q<O.y?Q:Q-O.h+25)}}function q(K){var L=y,N,M,J;N=K||s=="TABLE"?h.create(K||x):o.cloneNode(false);J=N;if(c.keep_styles!==false){do{if(/^(SPAN|STRONG|B|EM|I|FONT|STRIKE|U)$/.test(L.nodeName)){M=L.cloneNode(false);h.setAttrib(M,"id","");if(N.hasChildNodes()){M.appendChild(N.firstChild);N.appendChild(M)}else{J=M;N.appendChild(M)}}}while(L=L.parentNode)}if(!b.isIE){J.innerHTML="<br>"}return N}function p(M){var L,K,J;if(y.nodeType==3&&(M?u>0:u<y.nodeValue.length)){return false}if(y.parentNode==o&&A&&!M){return true}if(y.nodeName==="TABLE"||(y.previousSibling&&y.previousSibling.nodeName=="TABLE")){return(A&&!M)||(!A&&M)}L=new a(y,o);while(K=(M?L.prev():L.next())){if(K.nodeType===1){if(K.getAttribute("data-mce-bogus")){continue}J=K.nodeName.toLowerCase();if(J==="IMG"){return false}}else{if(K.nodeType===3&&!/^[ \t\r\n]*$/.test(K.nodeValue)){return false}}}return true}function k(J,P){var Q,O,L,N,M,K=x||"P";O=h.getParent(J,h.isBlock);if(!O||!C(O)){O=O||i;if(!O.hasChildNodes()){Q=h.create(K);O.appendChild(Q);v.setStart(Q,0);v.setEnd(Q,0);return Q}N=J;while(N.parentNode!=O){N=N.parentNode}while(N&&!h.isBlock(N)){L=N;N=N.previousSibling}if(L){Q=h.create(K);L.parentNode.insertBefore(Q,L);N=L;while(N&&!h.isBlock(N)){M=N.nextSibling;Q.appendChild(N);N=M}v.setStart(J,P);v.setEnd(J,P)}}return J}function E(){function J(L){var K=l[L?"firstChild":"lastChild"];while(K){if(K.nodeType==1){break}K=K[L?"nextSibling":"previousSibling"]}return K===o}n=x?q(x):h.create("BR");if(J(true)&&J()){h.replace(n,l)}else{if(J(true)){l.parentNode.insertBefore(n,l)}else{if(J()){h.insertAfter(n,l)}else{D=v.cloneRange();D.setStartAfter(o);D.setEndAfter(l);j=D.extractContents();h.insertAfter(j,l);h.insertAfter(n,l)}}}h.remove(o);m(n);g.add()}function B(){var K=new a(y,o),J;while(J=K.current()){if(J.nodeName=="BR"){return true}J=K.next()}}function H(){var K,J;if(y&&y.nodeType==3&&u>=y.nodeValue.length){if(!b.isIE&&!B()){K=h.create("br");v.insertNode(K);v.setStartAfter(K);v.setEndAfter(K);J=true}}K=h.create("br");v.insertNode(K);if(b.isIE&&s=="PRE"&&(!I||I<8)){K.parentNode.insertBefore(h.doc.createTextNode("\r"),K)}if(!J){v.setStartAfter(K);v.setEndAfter(K)}else{v.setStartBefore(K);v.setEndBefore(K)}d.setRng(v);g.add()}function r(J){do{if(J.nodeType===3){J.nodeValue=J.nodeValue.replace(/^[\r\n]+/,"")}J=J.firstChild}while(J)}function G(L){var J=h.getRoot(),K,M;K=L;while(K!==J&&h.getContentEditable(K)!=="false"){if(h.getContentEditable(K)==="true"){M=K}K=K.parentNode}return K!==J?M:J}if(!v.collapsed){e.execCommand("Delete");return}if(z.isDefaultPrevented()){return}y=v.startContainer;u=v.startOffset;x=c.forced_root_block;x=x?x.toUpperCase():"";I=h.doc.documentMode;if(y.nodeType==1&&y.hasChildNodes()){A=u>y.childNodes.length-1;y=y.childNodes[Math.min(u,y.childNodes.length-1)]||y;u=0}i=G(y);if(!i){return}g.beforeChange();if(!h.isBlock(i)&&i!=h.getRoot()){if(!x||z.shiftKey){H()}return}if((x&&!z.shiftKey)||(!x&&z.shiftKey)){y=k(y,u)}o=h.getParent(y,h.isBlock);l=o?h.getParent(o.parentNode,h.isBlock):null;s=o?o.nodeName.toUpperCase():"";F=l?l.nodeName.toUpperCase():"";if(s=="LI"&&h.isEmpty(o)){if(/^(UL|OL|LI)$/.test(l.parentNode.nodeName)){return false}E();return}if(s=="PRE"&&c.br_in_pre!==false){if(!z.shiftKey){H();return}}else{if((!x&&!z.shiftKey&&s!="LI")||(x&&z.shiftKey)){H();return}}x=x||"P";if(p()){if(/^(H[1-6]|PRE)$/.test(s)&&F!="HGROUP"){n=q(x)}else{n=q()}if(c.end_container_on_empty_block&&C(l)&&h.isEmpty(o)){n=h.split(l,o)}else{h.insertAfter(n,o)}}else{if(p(true)){n=o.parentNode.insertBefore(q(),o)}else{D=v.cloneRange();D.setEndAfter(o);j=D.extractContents();r(j);n=j.firstChild;h.insertAfter(j,o)}}h.setAttrib(n,"id","");m(n);g.add()}e.onKeyDown.add(function(j,i){if(i.keyCode==13){if(f(i)!==false){i.preventDefault()}}})}})(tinymce);
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
        //Non-IE
        width = window.innerWidth;
        height = window.innerHeight;
    } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )) {
        //IE 6+ in 'standards compliant mode'
        width = document.documentElement.clientWidth;
        height = document.documentElement.clientHeight;
    } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
        //IE 4 compatible
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
        //Netscape compliant
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if (document.body && ( document.body.scrollLeft || document.body.scrollTop )) {
        //DOM compliant
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if (document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop )) {
        //IE6 standards compliant mode
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

  // Make array of random hex digits. The UUID only has 32 digits in it, but we
  // allocate an extra items to make room for the '-'s we'll be inserting.
  for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);

  // Conform to RFC-4122, section 4.4
  s[14] = 4;  // Set 4 high bits of time_high field to version
  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence

  // Convert to hex chars
  for (var i = 0; i <36; i++) s[i] = itoh[s[i]];

  // Insert '-'s
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
        // Remove line breaks
        .replace(/(?:\n|\r\n|\r)/ig, " ")
        // Remove content in script tags.
        .replace(/<\s*script[^>]*>[\s\S]*?<\/script>/mig, "")
        // Remove content in style tags.
        .replace(/<\s*style[^>]*>[\s\S]*?<\/style>/mig, "")
        // Remove content in comments.
        .replace(/<!--.*?-->/mig, "")
        // Remove !DOCTYPE
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

    // Replace <br> and <br/> with a single newline
    text = text.replace(/<\s*br[^>]*\/?\s*>/ig, '\n');

    function decodeHtmlEntity(m, n) {
        // Determine the character code of the entity. Range is 0 to 65535
        // (characters in JavaScript are Unicode, and entities can represent
        // Unicode characters).
        var code;

        // Try to parse as numeric entity. This is done before named entities for
        // speed because associative array lookup in many JavaScript implementations
        // is a linear search.
        if (n.substr(0, 1) == '#') {
                // Try to parse as numeric entity
                if (n.substr(1, 1) == 'x') {
                        // Try to parse as hexadecimal
                        code = parseInt(n.substr(2), 16);
                } else {
                        // Try to parse as decimal
                        code = parseInt(n.substr(1), 10);
                }
        } else {
                // Try to parse as named entity
                code = OPF.ENTITIES_MAP[n];
        }

        // If still nothing, pass entity through
        return (code === undefined || code === NaN) ?
                '&' + n + ';' : String.fromCharCode(code);
    }

    text = text
        // Remove all remaining tags.
        .replace(/(<([^>]+)>)/ig,"")
        // Trim rightmost whitespaces for all lines
        .replace(/([^\n\S]+)\n/g,"\n")
        .replace(/([^\n\S]+)$/,"")
        // Make sure there are never more than two
        // consecutive linebreaks.
        .replace(/\n{2,}/g,"\n\n")
        // Remove newlines at the beginning of the text.
        .replace(/^\n+/,"")
        // Remove newlines at the end of the text.
        .replace(/\n+$/,"")
        // Decode HTML entities.
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

//            Ext.Ajax.request({
//                url: me.proxy.url,
//                method: 'GET',
//                headers: options.headers,
//                callback: function(opts, success, response) {
//                    me.unmask();
//                    options.callback(opts, success, response);
//                }
//            });
        }

        return me;
    }

});
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



/**
 * OPF.core.utils.MessageUtils
 * @extends Ext.util.Observable
 */
Ext.define('OPF.core.utils.MessageUtils', {
    extend:  Ext.util.Observable ,
    alternateClassName: ['OPF.Msg'],

    /***
     * response status codes.
     */
    STATUS_EXCEPTION :          'exception',
    STATUS_VALIDATION_ERROR :   "validation",
    STATUS_ERROR:               "error",
    STATUS_NOTICE:              "notice",
    STATUS_OK:                  "ok",
    STATUS_HELP:                "help",

    // private, ref to message-box Element.
    msgCt : null,

    constructor: function(cfg) {
        Ext.onReady(this.onReady, this);

        OPF.core.utils.MessageUtils.superclass.constructor.call(this, cfg);
    },

    // @protected, onReady, executes when Ext.onReady fires.
    onReady : function() {
        // create the msgBox container.  used for App.setAlert
        this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        this.msgCt.setStyle('position', 'absolute');
        this.msgCt.setStyle('z-index', 99999);
        this.msgCt.setWidth(300);
    },

    /***
     * setAlert
     * show the message box.  Aliased to addMessage
     * @param {String} msg
     * @param {Boolean} status
     */
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

            // add some smarts to msg's duration (div by 13.3 between 3 & 9 seconds)
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

    /***
     * buildMessageBox
     */
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

    /**
     * decodeStatusIcon
     * @param {Object} status
     */
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


/**
 * Helper functions that manipulate models
 */
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

        /**
         * Return a property value given a record and complex property name that contains ., it will look at associations for anything
         * before a . in the complex property
         * @param record
         * @param complexProperty A fully qualified property name. i.e. 'foreignKey.name' or an array containing
         *                        the property and sub properties to return.
         * @return {*}
         */
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

                //If we were unable to get a regular property, try to see if its a foreign key
                if (retVal === null || retVal === undefined) {
                    retVal = OPF.ModelHelper.getFk(record, properties[0], null);
                }
                return retVal;
            } else {

                var association = record.associations.get(properties[0]);
                if (association) {
                    var fk = record[association.instanceName];
                    if (fk) {
                        //remove the first record
                        properties = properties.splice(1);
                        return OPF.ModelHelper.getProperty(fk, properties);
                    }
                }
            }
        },

        /**
         * A helper function that will safely return a foreign key or a value on    it for a particular record.  This function
         * basically helps guard against undefined exceptions as it will return undefined fi the foreign key is null
         * @param record
         * @param foreignKeyName
         * @param foreignKeyProperty
         * @return {*}
         */
        getFk: function(record, foreignKeyName, foreignKeyProperty) {
            if (!record)
                return;
            var association = record.associations.get(foreignKeyName);
            if (association) {
                if (association.instanceName && record[association.instanceName]) {
                    var fk = record['get' + foreignKeyName]();
                    if (fk && foreignKeyProperty) {
                        var retVal = fk.get(foreignKeyProperty);

                        //If we were unable to get a regular property, try to see if its a foreign key
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

        /**
         * @private
         */
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

        ////////////////The only added lines////////////////
        if (OPF.isNotEmpty(options.callback)) {
            options.clientCallback = options.callback;
        }
        options.callback = Ext.data.Connection.securedCallback;
        ////////////////////////////////////////////////////

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

            // if autoabort is set, cancel the current transactions
            if (options.autoAbort === true || me.autoAbort) {
                me.abort();
            }

            // create a connection object

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr = new XDomainRequest();
            } else {
                xhr = this.getXhrInstance();
            }

            async = options.async !== false ? (options.async || me.async) : false;

            // open the request
            if (username) {
                xhr.open(requestOptions.method, requestOptions.url, async, username, password);
            } else {
                xhr.open(requestOptions.method, requestOptions.url, async);
            }

            if (options.withCredentials === true || me.withCredentials === true) {
                xhr.withCredentials = true;
            }

            headers = me.setupHeaders(xhr, options, requestOptions.data, requestOptions.params);

            // create the transaction object
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
            // bind our statechange listener
            if (async) {
                xhr.onreadystatechange = Ext.Function.bind(me.onStateChange, me, [request]);
            }

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr.onload = function() {
                    me.onComplete(request);
                }
            }

            // start the request!
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

String.prototype.startsWith = function(str) {
    return (this.match("^"+str)==str)
};
String.prototype.trim = function(){
    return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
};


//utils functions
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

/////////String utils\\\\\\\\\\\
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

//////////Date utils\\\\\\\\\\\\
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
//            return arguments[0].format(dateFormat);
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
    if (!Ext.isDate(date)) { // can pass a string in format 'm/d/Y'
        date = new Date(date);
    }
    return date.format("Y-m-d\\TH:i:s.000O"); // the format that Jersey Java parser will understand (it's a PHP format that ExtJS uses to format the date)
};
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

//SqReloadTreeNode = function(tree, nodeId) {
////    var reloadNode = function(nodeId) {
////        var node = tree.getNodeById(nodeId);
////        node.reload();
////        if (node.hasChildNodes()) {
////            node.expand();
////        } else {
////            node.collapse();
////        }
////    };
//
//    var node = tree.getNodeById(nodeId);
//    node.reload();
//    if (node.hasChildNodes()) {
//        node.expand();
//    } else {
//        node.collapse();
//    }
////    var parentNode = node.parentNode;
////    parentNode.reload();
////    tree.on('insert', reloadNode(nodeId));
//};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        var disabled = enabled != true;//ensure that enabled is boolean
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

//var sqSimpleAjaxRequest = function(requestUrl, jsonData) {
//    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
//    Ext.Ajax.request({
//        url: requestUrl,
//        method: 'POST',
//        jsonData: jsonData,
//
//        success:function(response, action) {
//            var msg = Ext.decode(response.responseText).object;
//            Ext.MessageBox.alert('Info', msg);
//        },
//
//        failure:function(resp) {
//            sqShowFailureResponseStatus(resp);
//        }
//    });
//};

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
        //var responseStatus = Ext.decode(resp.responseText);
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

//Deprecated
function calculateLookup(path, name) {
    var path = ifBlank(path, '');
    var name = ifBlank(name, '');
    var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
    var lookup = (isNotBlank(path) ? path + '.' : '') + normalizeName;
    return lookup.toLowerCase();
}
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

/**
 * Utils functions that manipulate models
 */
Ext.define('OPF.core.utils.StoreHelper', {
    statics: {

        /**
         * Clone the data in one store to a newly created store.  If the store to clone is not loaded yet, the
         * new store will not be loaded with data until after the clone store is loaded.  If a store id is specified,
         * and a store exists with that id, it will be returned immediately without loading data. Otherwise, it will be
         * created and loaded with the data in the storeToClone.
         * @param storeToClone The store that contains the data to clone
         * @param configForNewStore (Optional) The config for the new store
         * @param storeId (Optional) Id of the new store
         * @param uniqueKeyField (Optional) If specified, any duplicate entries will not be added to the new store
         *                                  that have the same value for the specified property
         * @return {Ext.data.Store}
         */
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
                    //If we have a unique key field, don't add values that have already been added
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

        /**
         * Call the specified function after all the stores are fully loaded
         * @param store A store or an array of stores to load.  Each element in the array can be
         *              a name of the store that will be looked up in teh store manager, an instance of a
         *              store or a function to call that will be passed a function and scope that should be
         *              fired when the load event completes for a particular object.
         * @param fn The function to call when the store(s) are loaded
         * @param scope The scope of the function call
         */
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
                //If this is an id, get the registered store for it
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

            //If everything is already loaded, call the success function
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
/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
//            collapsed: true,
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
        // need this method for override any properties in prometheus generated project
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
/**
 * Created by IntelliJ IDEA.
 * User: mjr
 * Date: 6/10/11
 * Time: 1:26 PM
 */

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
        //below is code from original class
        if (Ext.isString(action)) {
            action = Ext.ClassManager.instantiateByAlias('formaction.' + action, Ext.apply({}, options, {form: this}));
        }
        if (this.fireEvent('beforeaction', this, action) !== false) {
            action.options = options;//this line is only customization
            this.beforeAction(action);
            Ext.defer(action.run, 100, action);
        }
        return this;
    },

    beforeAction : function(action){
        // Call HtmlEditor's syncValue before actions
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
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
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

// Basic JavaScript BN library - subset useful for RSA encryption.

// Bits per digit
var dbits;

// JavaScript engine analysis
var canary = 0xdeadbeefcafe;
var j_lm = ((canary&0xffffff)==0xefcafe);

// (public) Constructor
function BigInteger(a,b,c) {
  if(a != null)
    if("number" == typeof a) this.fromNumber(a,b,c);
    else if(b == null && "string" != typeof a) this.fromString(a,256);
    else this.fromString(a,b);
}

// return new, unset BigInteger
function nbi() { return new BigInteger(null); }

// am: Compute w_j += (x*this_i), propagate carries,
// c is initial carry, returns final carry.
// c < 3*dvalue, x < 2*dvalue, this_i < dvalue
// We need to select the fastest one that works in this environment.

// am1: use a single mult and divide to get the high bits,
// max digit bits should be 26 because
// max internal value = 2*dvalue^2-2*dvalue (< 2^53)
function am1(i,x,w,j,c,n) {
  while(--n >= 0) {
    var v = x*this[i++]+w[j]+c;
    c = Math.floor(v/0x4000000);
    w[j++] = v&0x3ffffff;
  }
  return c;
}
// am2 avoids a big mult-and-extract completely.
// Max digit bits should be <= 30 because we do bitwise ops
// on values up to 2*hdvalue^2-hdvalue-1 (< 2^31)
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
// Alternately, set max digit bits to 28 since some
// browsers slow down when dealing with 32-bit numbers.
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
else { // Mozilla/Netscape seems to prefer am3
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

// Digit conversions
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

// (protected) copy this to r
function bnpCopyTo(r) {
  for(var i = this.t-1; i >= 0; --i) r[i] = this[i];
  r.t = this.t;
  r.s = this.s;
}

// (protected) set from integer value x, -DV <= x < DV
function bnpFromInt(x) {
  this.t = 1;
  this.s = (x<0)?-1:0;
  if(x > 0) this[0] = x;
  else if(x < -1) this[0] = x+DV;
  else this.t = 0;
}

// return bigint initialized to value
function nbv(i) { var r = nbi(); r.fromInt(i); return r; }

// (protected) set from string and radix
function bnpFromString(s,b) {
  var k;
  if(b == 16) k = 4;
  else if(b == 8) k = 3;
  else if(b == 256) k = 8; // byte array
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

// (protected) clamp off excess high words
function bnpClamp() {
  var c = this.s&this.DM;
  while(this.t > 0 && this[this.t-1] == c) --this.t;
}

// (public) return string representation in given radix
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

// (public) -this
function bnNegate() { var r = nbi(); BigInteger.ZERO.subTo(this,r); return r; }

// (public) |this|
function bnAbs() { return (this.s<0)?this.negate():this; }

// (public) return + if this > a, - if this < a, 0 if equal
function bnCompareTo(a) {
  var r = this.s-a.s;
  if(r != 0) return r;
  var i = this.t;
  r = i-a.t;
  if(r != 0) return r;
  while(--i >= 0) if((r=this[i]-a[i]) != 0) return r;
  return 0;
}

// returns bit length of the integer x
function nbits(x) {
  var r = 1, t;
  if((t=x>>>16) != 0) { x = t; r += 16; }
  if((t=x>>8) != 0) { x = t; r += 8; }
  if((t=x>>4) != 0) { x = t; r += 4; }
  if((t=x>>2) != 0) { x = t; r += 2; }
  if((t=x>>1) != 0) { x = t; r += 1; }
  return r;
}

// (public) return the number of bits in "this"
function bnBitLength() {
  if(this.t <= 0) return 0;
  return this.DB*(this.t-1)+nbits(this[this.t-1]^(this.s&this.DM));
}

// (protected) r = this << n*DB
function bnpDLShiftTo(n,r) {
  var i;
  for(i = this.t-1; i >= 0; --i) r[i+n] = this[i];
  for(i = n-1; i >= 0; --i) r[i] = 0;
  r.t = this.t+n;
  r.s = this.s;
}

// (protected) r = this >> n*DB
function bnpDRShiftTo(n,r) {
  for(var i = n; i < this.t; ++i) r[i-n] = this[i];
  r.t = Math.max(this.t-n,0);
  r.s = this.s;
}

// (protected) r = this << n
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

// (protected) r = this >> n
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

// (protected) r = this - a
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

// (protected) r = this * a, r != this,a (HAC 14.12)
// "this" should be the larger one if appropriate.
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

// (protected) r = this^2, r != this (HAC 14.16)
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

// (protected) divide this by m, quotient and remainder to q, r (HAC 14.20)
// r != q, this != m.  q or r may be null.
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
  var nsh = this.DB-nbits(pm[pm.t-1]);    // normalize modulus
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
  t.subTo(y,y);    // "negative" y so we can replace sub with am later
  while(y.t < ys) y[y.t++] = 0;
  while(--j >= 0) {
    // Estimate quotient digit
    var qd = (r[--i]==y0)?this.DM:Math.floor(r[i]*d1+(r[i-1]+e)*d2);
    if((r[i]+=y.am(0,qd,r,j,0,ys)) < qd) {    // Try it out
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
  if(nsh > 0) r.rShiftTo(nsh,r);    // Denormalize remainder
  if(ts < 0) BigInteger.ZERO.subTo(r,r);
}

// (public) this mod a
function bnMod(a) {
  var r = nbi();
  this.abs().divRemTo(a,null,r);
  if(this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r,r);
  return r;
}

// Modular reduction using "classic" algorithm
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

// (protected) return "-1/this % 2^DB"; useful for Mont. reduction
// justification:
//         xy == 1 (mod m)
//         xy =  1+km
//   xy(2-xy) = (1+km)(1-km)
// x[y(2-xy)] = 1-k^2m^2
// x[y(2-xy)] == 1 (mod m^2)
// if y is 1/x mod m, then y(2-xy) is 1/x mod m^2
// should reduce x and y(2-xy) by m^2 at each step to keep size bounded.
// JS multiply "overflows" differently from C/C++, so care is needed here.
function bnpInvDigit() {
  if(this.t < 1) return 0;
  var x = this[0];
  if((x&1) == 0) return 0;
  var y = x&3;        // y == 1/x mod 2^2
  y = (y*(2-(x&0xf)*y))&0xf;    // y == 1/x mod 2^4
  y = (y*(2-(x&0xff)*y))&0xff;    // y == 1/x mod 2^8
  y = (y*(2-(((x&0xffff)*y)&0xffff)))&0xffff;    // y == 1/x mod 2^16
  // last step - calculate inverse mod DV directly;
  // assumes 16 < DB <= 32 and assumes ability to handle 48-bit ints
  y = (y*(2-x*y%this.DV))%this.DV;        // y == 1/x mod 2^dbits
  // we really want the negative inverse, and -DV < y < DV
  return (y>0)?this.DV-y:-y;
}

// Montgomery reduction
function Montgomery(m) {
  this.m = m;
  this.mp = m.invDigit();
  this.mpl = this.mp&0x7fff;
  this.mph = this.mp>>15;
  this.um = (1<<(m.DB-15))-1;
  this.mt2 = 2*m.t;
}

// xR mod m
function montConvert(x) {
  var r = nbi();
  x.abs().dlShiftTo(this.m.t,r);
  r.divRemTo(this.m,null,r);
  if(x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r,r);
  return r;
}

// x/R mod m
function montRevert(x) {
  var r = nbi();
  x.copyTo(r);
  this.reduce(r);
  return r;
}

// x = x/R mod m (HAC 14.32)
function montReduce(x) {
  while(x.t <= this.mt2)    // pad x so am has enough room later
    x[x.t++] = 0;
  for(var i = 0; i < this.m.t; ++i) {
    // faster way of calculating u0 = x[i]*mp mod DV
    var j = x[i]&0x7fff;
    var u0 = (j*this.mpl+(((j*this.mph+(x[i]>>15)*this.mpl)&this.um)<<15))&x.DM;
    // use am to combine the multiply-shift-add into one call
    j = i+this.m.t;
    x[j] += this.m.am(0,u0,x,i,0,this.m.t);
    // propagate carry
    while(x[j] >= x.DV) { x[j] -= x.DV; x[++j]++; }
  }
  x.clamp();
  x.drShiftTo(this.m.t,x);
  if(x.compareTo(this.m) >= 0) x.subTo(this.m,x);
}

// r = "x^2/R mod m"; x != r
function montSqrTo(x,r) { x.squareTo(r); this.reduce(r); }

// r = "xy/R mod m"; x,y != r
function montMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }

Montgomery.prototype.convert = montConvert;
Montgomery.prototype.revert = montRevert;
Montgomery.prototype.reduce = montReduce;
Montgomery.prototype.mulTo = montMulTo;
Montgomery.prototype.sqrTo = montSqrTo;

// (protected) true iff this is even
function bnpIsEven() { return ((this.t>0)?(this[0]&1):this.s) == 0; }

// (protected) this^e, e < 2^32, doing sqr and mul with "r" (HAC 14.79)
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

// (public) this^e % m, 0 <= e < 2^32
function bnModPowInt(e,m) {
  var z;
  if(e < 256 || m.isEven()) z = new Classic(m); else z = new Montgomery(m);
  return this.exp(e,z);
}

// protected
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

// public
BigInteger.prototype.toString = bnToString;
BigInteger.prototype.negate = bnNegate;
BigInteger.prototype.abs = bnAbs;
BigInteger.prototype.compareTo = bnCompareTo;
BigInteger.prototype.bitLength = bnBitLength;
BigInteger.prototype.mod = bnMod;
BigInteger.prototype.modPowInt = bnModPowInt;

// "constants"
BigInteger.ZERO = nbv(0);
BigInteger.ONE = nbv(1);


// Copyright (c) 2005  Tom Wu
// All Rights Reserved.
// See "LICENSE" for details.

// Extended JavaScript BN functions, required for RSA private ops.

// (public)
function bnClone() { var r = nbi(); this.copyTo(r); return r; }

// (public) return value as integer
function bnIntValue() {
  if(this.s < 0) {
    if(this.t == 1) return this[0]-this.DV;
    else if(this.t == 0) return -1;
  }
  else if(this.t == 1) return this[0];
  else if(this.t == 0) return 0;
  // assumes 16 < DB < 32
  return ((this[1]&((1<<(32-this.DB))-1))<<this.DB)|this[0];
}

// (public) return value as byte
function bnByteValue() { return (this.t==0)?this.s:(this[0]<<24)>>24; }

// (public) return value as short (assumes DB>=16)
function bnShortValue() { return (this.t==0)?this.s:(this[0]<<16)>>16; }

// (protected) return x s.t. r^x < DV
function bnpChunkSize(r) { return Math.floor(Math.LN2*this.DB/Math.log(r)); }

// (public) 0 if this == 0, 1 if this > 0
function bnSigNum() {
  if(this.s < 0) return -1;
  else if(this.t <= 0 || (this.t == 1 && this[0] <= 0)) return 0;
  else return 1;
}

// (protected) convert to radix string
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

// (protected) convert from radix string
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

// (protected) alternate constructor
function bnpFromNumber(a,b,c) {
  if("number" == typeof b) {
    // new BigInteger(int,int,RNG)
    if(a < 2) this.fromInt(1);
    else {
      this.fromNumber(a,c);
      if(!this.testBit(a-1))    // force MSB set
        this.bitwiseTo(BigInteger.ONE.shiftLeft(a-1),op_or,this);
      if(this.isEven()) this.dAddOffset(1,0); // force odd
      while(!this.isProbablePrime(b)) {
        this.dAddOffset(2,0);
        if(this.bitLength() > a) this.subTo(BigInteger.ONE.shiftLeft(a-1),this);
      }
    }
  }
  else {
    // new BigInteger(int,RNG)
    var x = new Array(), t = a&7;
    x.length = (a>>3)+1;
    b.nextBytes(x);
    if(t > 0) x[0] &= ((1<<t)-1); else x[0] = 0;
    this.fromString(x,256);
  }
}

// (public) convert to bigendian byte array
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

// (protected) r = this op a (bitwise)
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

// (public) this & a
function op_and(x,y) { return x&y; }
function bnAnd(a) { var r = nbi(); this.bitwiseTo(a,op_and,r); return r; }

// (public) this | a
function op_or(x,y) { return x|y; }
function bnOr(a) { var r = nbi(); this.bitwiseTo(a,op_or,r); return r; }

// (public) this ^ a
function op_xor(x,y) { return x^y; }
function bnXor(a) { var r = nbi(); this.bitwiseTo(a,op_xor,r); return r; }

// (public) this & ~a
function op_andnot(x,y) { return x&~y; }
function bnAndNot(a) { var r = nbi(); this.bitwiseTo(a,op_andnot,r); return r; }

// (public) ~this
function bnNot() {
  var r = nbi();
  for(var i = 0; i < this.t; ++i) r[i] = this.DM&~this[i];
  r.t = this.t;
  r.s = ~this.s;
  return r;
}

// (public) this << n
function bnShiftLeft(n) {
  var r = nbi();
  if(n < 0) this.rShiftTo(-n,r); else this.lShiftTo(n,r);
  return r;
}

// (public) this >> n
function bnShiftRight(n) {
  var r = nbi();
  if(n < 0) this.lShiftTo(-n,r); else this.rShiftTo(n,r);
  return r;
}

// return index of lowest 1-bit in x, x < 2^31
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

// (public) returns index of lowest 1-bit (or -1 if none)
function bnGetLowestSetBit() {
  for(var i = 0; i < this.t; ++i)
    if(this[i] != 0) return i*this.DB+lbit(this[i]);
  if(this.s < 0) return this.t*this.DB;
  return -1;
}

// return number of 1 bits in x
function cbit(x) {
  var r = 0;
  while(x != 0) { x &= x-1; ++r; }
  return r;
}

// (public) return number of set bits
function bnBitCount() {
  var r = 0, x = this.s&this.DM;
  for(var i = 0; i < this.t; ++i) r += cbit(this[i]^x);
  return r;
}

// (public) true iff nth bit is set
function bnTestBit(n) {
  var j = Math.floor(n/this.DB);
  if(j >= this.t) return(this.s!=0);
  return((this[j]&(1<<(n%this.DB)))!=0);
}

// (protected) this op (1<<n)
function bnpChangeBit(n,op) {
  var r = BigInteger.ONE.shiftLeft(n);
  this.bitwiseTo(r,op,r);
  return r;
}

// (public) this | (1<<n)
function bnSetBit(n) { return this.changeBit(n,op_or); }

// (public) this & ~(1<<n)
function bnClearBit(n) { return this.changeBit(n,op_andnot); }

// (public) this ^ (1<<n)
function bnFlipBit(n) { return this.changeBit(n,op_xor); }

// (protected) r = this + a
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

// (public) this + a
function bnAdd(a) { var r = nbi(); this.addTo(a,r); return r; }

// (public) this - a
function bnSubtract(a) { var r = nbi(); this.subTo(a,r); return r; }

// (public) this * a
function bnMultiply(a) { var r = nbi(); this.multiplyTo(a,r); return r; }

// (public) this / a
function bnDivide(a) { var r = nbi(); this.divRemTo(a,r,null); return r; }

// (public) this % a
function bnRemainder(a) { var r = nbi(); this.divRemTo(a,null,r); return r; }

// (public) [this/a,this%a]
function bnDivideAndRemainder(a) {
  var q = nbi(), r = nbi();
  this.divRemTo(a,q,r);
  return new Array(q,r);
}

// (protected) this *= n, this >= 0, 1 < n < DV
function bnpDMultiply(n) {
  this[this.t] = this.am(0,n-1,this,0,0,this.t);
  ++this.t;
  this.clamp();
}

// (protected) this += n << w words, this >= 0
function bnpDAddOffset(n,w) {
  while(this.t <= w) this[this.t++] = 0;
  this[w] += n;
  while(this[w] >= this.DV) {
    this[w] -= this.DV;
    if(++w >= this.t) this[this.t++] = 0;
    ++this[w];
  }
}

// A "null" reducer
function NullExp() {}
function nNop(x) { return x; }
function nMulTo(x,y,r) { x.multiplyTo(y,r); }
function nSqrTo(x,r) { x.squareTo(r); }

NullExp.prototype.convert = nNop;
NullExp.prototype.revert = nNop;
NullExp.prototype.mulTo = nMulTo;
NullExp.prototype.sqrTo = nSqrTo;

// (public) this^e
function bnPow(e) { return this.exp(e,new NullExp()); }

// (protected) r = lower n words of "this * a", a.t <= n
// "this" should be the larger one if appropriate.
function bnpMultiplyLowerTo(a,n,r) {
  var i = Math.min(this.t+a.t,n);
  r.s = 0; // assumes a,this >= 0
  r.t = i;
  while(i > 0) r[--i] = 0;
  var j;
  for(j = r.t-this.t; i < j; ++i) r[i+this.t] = this.am(0,a[i],r,i,0,this.t);
  for(j = Math.min(a.t,n); i < j; ++i) this.am(0,a[i],r,i,0,n-i);
  r.clamp();
}

// (protected) r = "this * a" without lower n words, n > 0
// "this" should be the larger one if appropriate.
function bnpMultiplyUpperTo(a,n,r) {
  --n;
  var i = r.t = this.t+a.t-n;
  r.s = 0; // assumes a,this >= 0
  while(--i >= 0) r[i] = 0;
  for(i = Math.max(n-this.t,0); i < a.t; ++i)
    r[this.t+i-n] = this.am(n-i,a[i],r,0,0,this.t+i-n);
  r.clamp();
  r.drShiftTo(1,r);
}

// Barrett modular reduction
function Barrett(m) {
  // setup Barrett
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

// x = x mod m (HAC 14.42)
function barrettReduce(x) {
  x.drShiftTo(this.m.t-1,this.r2);
  if(x.t > this.m.t+1) { x.t = this.m.t+1; x.clamp(); }
  this.mu.multiplyUpperTo(this.r2,this.m.t+1,this.q3);
  this.m.multiplyLowerTo(this.q3,this.m.t+1,this.r2);
  while(x.compareTo(this.r2) < 0) x.dAddOffset(1,this.m.t+1);
  x.subTo(this.r2,x);
  while(x.compareTo(this.m) >= 0) x.subTo(this.m,x);
}

// r = x^2 mod m; x != r
function barrettSqrTo(x,r) { x.squareTo(r); this.reduce(r); }

// r = x*y mod m; x,y != r
function barrettMulTo(x,y,r) { x.multiplyTo(y,r); this.reduce(r); }

Barrett.prototype.convert = barrettConvert;
Barrett.prototype.revert = barrettRevert;
Barrett.prototype.reduce = barrettReduce;
Barrett.prototype.mulTo = barrettMulTo;
Barrett.prototype.sqrTo = barrettSqrTo;

// (public) this^e % m (HAC 14.85)
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

  // precomputation
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
    if(is1) {    // ret == 1, don't bother squaring or multiplying it
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

// (public) gcd(this,a) (HAC 14.54)
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

// (protected) this % n, n < 2^26
function bnpModInt(n) {
  if(n <= 0) return 0;
  var d = this.DV%n, r = (this.s<0)?n-1:0;
  if(this.t > 0)
    if(d == 0) r = this[0]%n;
    else for(var i = this.t-1; i >= 0; --i) r = (d*r+this[i])%n;
  return r;
}

// (public) 1/this % m (HAC 14.61)
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

// (public) test primality with certainty >= 1-.5^t
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

// (protected) true if probably prime (HAC 4.24, Miller-Rabin)
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

// protected
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

// public
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

// BigInteger interfaces not implemented in jsbn:

// BigInteger(int signum, byte[] magnitude)
// double doubleValue()
// float floatValue()
// int hashCode()
// long longValue()
// static BigInteger valueOf(long val)

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
        //$input = UTF8.encode($input);
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
        return $output; //UTF8.decode($output);
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
            // get the tag
            var $tag = $data.charCodeAt(0);
            $data = $data.substr(1);
            // get length
            var $length = 0;
            // ignore any null tag
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
            // get value
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
                $result.push(this.parse($value)); // sequence
            else
                $result.push(this.value(($tag & 128) ? 4 : ($tag & 31), $value));
        }
        return $result;
    };
    this.value = function($tag, $data) {
        if ($tag == 1)
            return $data ? true : false;
        else if ($tag == 2) //integer
            return $data;
        else if ($tag == 3) //bit string
            return this.parse($data.substr(1));
        else if ($tag == 5) //null
            return null;
        else if ($tag == 6){ //ID
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

function sha1(msg)
{
    // constants
    var K = [0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6];

    // PREPROCESSING
    msg += String.fromCharCode(0x80);  // add trailing '1' bit to string

    // convert string msg into 512-bit/16-integer blocks arrays of ints
    var l = Math.ceil(msg.length/4) + 2;  // long enough to contain msg plus 2-word length
    var N = Math.ceil(l/16);              // in N 16-int blocks
    var M = new Array(N);

    for (var i=0; i<N; i++) {
        M[i] = new Array(16);
        for (var j=0; j<16; j++)  // encode 4 chars per integer, big-endian encoding
            M[i][j] = (msg.charCodeAt(i*64+j*4)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16) | (msg.charCodeAt(i*64+j*4+2)<<8) | (msg.charCodeAt(i*64+j*4+3));
        // note running off the end of msg is ok 'cos bitwise ops on NaN return 0
    }

    // add length (in bits) into final pair of 32-bit integers (big-endian)
    // note: most significant word would be ((len-1)*8 >>> 32, but since JS converts
    // bitwise-op args to 32 bits, we need to simulate this by arithmetic operators
    M[N-1][14] = ((msg.length-1)*8) / Math.pow(2, 32); M[N-1][14] = Math.floor(M[N-1][14])
    M[N-1][15] = ((msg.length-1)*8) & 0xffffffff;

    // set initial hash value
    var H0 = 0x67452301;
    var H1 = 0xefcdab89;
    var H2 = 0x98badcfe;
    var H3 = 0x10325476;
    var H4 = 0xc3d2e1f0;

    // HASH COMPUTATION
    var W = new Array(80);
    var a, b, c, d, e;

    for (var i=0; i<N; i++) {
        // 1 - prepare message schedule 'W'
        for (var t=0;  t<16; t++) W[t] = M[i][t];
        for (var t=16; t<80; t++) {
            W[t] = W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16];
            W[t] = (W[t] << 1) | (W[t]>>>31);
        }

        // 2 - initialise five working variables a, b, c, d, e with previous hash value
        a = H0; b = H1; c = H2; d = H3; e = H4;

        // 3 - main loop
        for (var t=0; t<80; t++) {
            var s = Math.floor(t/20); // seq for blocks of 'f' functions and 'K' constants
            var T = ((a<<5) | (a>>>27)) + e + K[s] + W[t];
            switch(s) {
            case 0: T += (b & c) ^ (~b & d); break;          // Ch()
            case 1: T += b ^ c ^ d; break;                   // Parity()
            case 2: T += (b & c) ^ (b & d) ^ (c & d); break; // Maj()
            case 3: T += b ^ c ^ d; break;                   // Parity()
            }
            e = d;
            d = c;
            c = (b << 30) | (b>>>2);
            b = a;
            a = T;
        }

        // 4 - compute the new intermediate hash value
        H0 = (H0+a) & 0xffffffff;  // note 'addition modulo 2^32'
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

/**
 * InputTextMask script used for mask/regexp operations.
 * Mask Individual Character Usage:
 * 9 - designates only numeric values
 * L - designates only uppercase letter values
 * l - designates only lowercase letter values
 * A - designates only alphanumeric values
 * X - denotes that a custom client script regular expression is specified</li>
 * All other characters are assumed to be "special" characters used to mask the input component.
 * Example 1:
 *             (999)999-9999       only numeric values can be entered where the the character
 * position value is 9. Parenthesis and dash are non-editable/mask characters.
 * Example 2:
 * 99L-ll-X[^A-C]X only numeric values for the first two characters,
 * uppercase values for the third character, lowercase letters for the
 * fifth/sixth characters, and the last character X[^A-C]X together counts
 * as the eighth character regular expression that would allow all characters
 * but "A", "B", and "C". Dashes outside the regular expression are non-editable/mask characters.
 * @constructor
 * @param (String) mask The InputTextMask
 * @param (boolean) clearWhenInvalid True to clear the mask when the field blurs and the text is invalid. Optional, default is true.
 */

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
        if (keycode.unicode == 0) {//?? sometimes on Safari
            return;
        }
        if ((keycode.unicode == 67 || keycode.unicode == 99) && e.ctrlKey) {//Ctrl+c, let's the browser manage it!
            return;
        }
        if ((keycode.unicode == 88 || keycode.unicode == 120) && e.ctrlKey) {//Ctrl+x, manage paste
            this.startTask();
            return;
        }
        if ((keycode.unicode == 86 || keycode.unicode == 118) && e.ctrlKey) {//Ctrl+v, manage paste....
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

Ext.apply(Ext.form.VTypes,{
	denyBlank: function(value) {
        return OPF.isNotBlank(value) && value.replace(/\s/g, '').length > 0;
	},
    denyBlankText: 'Only spaces are not allowed.',
    denyBlankMask: /./
});
//
// Definitions of enums referenced in documentation.
//

/**
 * @enum [Ext.enums.Layout=layout.*]
 * Enumeration of all layout types.
 */

/**
 * @enum [Ext.enums.Widget=widget.*]
 * Enumeration of all xtypes.
 */

/**
 * @enum [Ext.enums.Plugin=plugin.*]
 * Enumeration of all ptypes.
 */

/**
 * @enum [Ext.enums.Feature=feature.*]
 * Enumeration of all ftypes.
 */


