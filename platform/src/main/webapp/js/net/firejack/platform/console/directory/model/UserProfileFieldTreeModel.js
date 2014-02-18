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
    extend: 'Ext.data.Model',

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

