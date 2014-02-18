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

Ext.define('OPF.console.security.AssignedRoleModel', {
    extend: 'Ext.data.Model',

    statics: {
        restSuffixUrl: 'authority/role-assignment'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'type', type: 'string' },

        { name: 'assigned', type: 'boolean' },

        { name: 'parentId', type: 'int' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },

        { name: 'global', type: 'boolean', useNull: true }
    ],

    belongsTo: 'User'
});

OPF.console.security.AssignedRoleModel.getAssignmentRoleUrl = function(securityWin, forSave) {
    var selectedNodeId = securityWin.getSelectedNodeId();
    var selectedNodeEntityType = securityWin.getSelectNodeEntityType();
    var selectedUserId = securityWin.selectedAssignedUserId;
    var url = OPF.core.utils.RegistryNodeType.ASSIGNED_ROLE.generateUrl('/');
    if (isEmpty(selectedUserId)) {
        url += selectedNodeId + '/' + selectedNodeEntityType;
    } else {
        url += forSave ? '' : 'by-user/';
        url += selectedNodeId + '/' + selectedNodeEntityType + '/' + selectedUserId;
    }
    return url;
};