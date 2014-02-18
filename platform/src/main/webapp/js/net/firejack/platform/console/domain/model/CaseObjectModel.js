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
    extend: 'Ext.data.Model',

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