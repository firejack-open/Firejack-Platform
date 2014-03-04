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

Ext.define('OPF.prometheus.wizard.workflow.model.ActivityActionModel', {
    extend: 'OPF.core.model.RegistryNodeTreeModel',

    fields: [
        {
            name: 'isNew',
            type: 'boolean'
        },
        {
            name: 'description',
            type: 'string',
            useNull: true,
            fieldType: 'LONG_TEXT'
        },
        {
            name: 'isActivity',
            type: 'boolean',
            useNull: false,
            defaultValue: true,
            fieldType: 'FLAG'
        },
        {
            name: 'activityOrder',
            type: 'string',
            useNull: false
        },
        {
            name: 'status'
        },
        {
            name: 'actor'
        },
        {
            name: 'toActivity'
        },
        {
            name: 'activityForm',
            type: 'string',
            useNull: false,
            defaultValue: 'CUSTOM'
        }
    ],

    associations: [
        {
            type: 'hasMany',
            model: 'OPF.prometheus.wizard.workflow.model.ActivityFieldModel',
            name: 'activityFields',
            associatedName: 'activityFields',
            associatedKey: 'activityFields',
            foreignKey: 'id'
        }
    ]

});