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