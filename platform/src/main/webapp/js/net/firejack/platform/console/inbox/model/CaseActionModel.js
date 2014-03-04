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

Ext.define('OPF.console.inbox.model.CaseActionModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/inbox',
        restSuffixUrl: 'process/action/case'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'type', type: 'string' },
        { name : 'user' },
        { name: 'userName', type: 'string', useNull: true,
            convert: function(userName, model) {
                var user = model.get('user');
                return OPF.isEmpty(user) ? null : user.username
            }
        },
        { name : 'caseNote'},
        { name: 'noteId', type: 'int', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.id
            }
        },
        { name: 'note', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var note = model.get('caseNote');
                return OPF.isEmpty(note) ? null : note.text
            }
        },
        { name: 'caseExplanation' },
        { name: 'explanation', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var explanation = model.get('caseExplanation');
                return OPF.isEmpty(explanation) ? null : explanation.shortDescription
            }
        },
        { name: 'processCase', type: 'auto' },
        { name: 'assignee', type: 'string', useNull: true,
            convert: function(noteId, model) {
                var processCase = model.get('processCase');
                return OPF.isEmpty(processCase) || OPF.isEmpty(processCase.assignee) ? '' : processCase.assignee.username
            }
        },
        { name: 'description', type: 'string' },
        { name: 'performedOn', type: 'date', dateFormat: 'time' }, //'U000'},

        { name: 'created', type: 'date', dateFormat: 'time' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ]

});