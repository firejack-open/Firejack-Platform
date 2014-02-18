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
    extend: 'Ext.data.Model',

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