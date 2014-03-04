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

Ext.define('OPF.console.site.model.NavigationElement', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/site',
        restSuffixUrl: 'site/navigation',
        editorClassName: 'OPF.console.site.editor.NavigationElementEditor',
        constraintName: 'OPF.site.NavigationElement'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'lookup', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name:'serverName', type: 'string'},
        { name:'port', type: 'string'},
        { name:'parentPath', type: 'string'},
        { name:'urlPath', type: 'string'},
        { name:'protocol', type: 'string'},
        { name:'method', type: 'string'},
        { name:'status', type: 'string'},
        { name:'sortPosition', type: 'string'},
        { name:'elementType', type: 'string'},

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' },

        { name: 'navigationElements', type: 'auto' },

        { name: 'permissions', type: 'auto'},

        { name: 'textResourceVersion', type: 'auto'},

        { name: 'imageResourceVersion', type: 'auto'}
    ],

    hasMany: [
        { model: 'NavigationElement', name: 'navigationElements', foreignKey: 'parent_id' }//,
//        { model: 'Permission', name: 'permissions' }
    ]

});