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
    extend: 'Ext.data.Model',

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