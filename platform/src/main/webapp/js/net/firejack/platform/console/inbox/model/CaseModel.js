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
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/case'
    },
    idProperty: 'id',
    fields: staticFields
});

OPF.console.inbox.model.CaseModel.configuredFields = staticFields;