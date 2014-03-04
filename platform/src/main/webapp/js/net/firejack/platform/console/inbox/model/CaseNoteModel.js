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

Ext.define('OPF.console.inbox.model.CaseNoteModel', {
    extend: 'Ext.data.Model',

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