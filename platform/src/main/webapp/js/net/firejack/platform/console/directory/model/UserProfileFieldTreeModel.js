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

