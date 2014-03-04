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

//@tag opf-model


Ext.define('OPF.console.domain.model.BIReportUserModel', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'title', type: 'string' },
        { name: 'filter', type: 'string' },
        { name: 'fields', type: 'auto' },
        { name: 'created', type: 'int' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'user',
            associatedName: 'user', associationKey: 'user',
            foreignKey: 'id',
            displayName: 'User',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportModel',
            name: 'report',
            associatedName: 'report', associationKey: 'report',
            foreignKey: 'id',
            displayName: 'Report',
            displayDescription: ''
        }
    ]

});