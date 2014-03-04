/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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