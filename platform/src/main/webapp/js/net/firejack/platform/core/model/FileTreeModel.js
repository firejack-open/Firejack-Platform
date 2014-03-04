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

Ext.define('OPF.core.model.FileTreeModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: null,
        restSuffixUrl: 'registry',
        editorClassName: null,
        constraintName: null
    },

    fields: [
        { name: 'id', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'text', type: 'string' },
        { name: 'icon', type: 'string'},
        { name: 'cls', type: 'string'},
        { name: 'iconCls', type: 'string' },
        { name: 'canUpdate', type: 'boolean'},
        { name: 'canDelete', type: 'boolean'},
        { name: 'leaf', type: 'boolean'},
        { name: 'allowDrag', type: 'boolean'},
        { name: 'allowDrop', type: 'boolean'},
        { name: 'expanded', type: 'boolean'},
        { name: 'children', useNull: true}
    ]

});