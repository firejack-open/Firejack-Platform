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
    extend: 'Ext.data.Model',

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