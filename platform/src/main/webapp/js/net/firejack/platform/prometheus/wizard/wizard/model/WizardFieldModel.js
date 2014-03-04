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

Ext.define('OPF.prometheus.wizard.wizard.model.WizardFieldModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/wizard',
        constraintName: 'OPF.registry.WizardFieldModel'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'name', type: 'string' },
        { name: 'displayName', type: 'string' },
        { name: 'defaultValue', type: 'string' },

        { name: 'fieldId', type: 'int', useNull: true },
        { name: 'fieldType', type: 'string' },
        { name: 'relationshipId', type: 'int', useNull: true },

        { name: 'editable', type: 'boolean' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.WizardModel',
            name: 'wizard',
            associatedName: 'wizard',
            associatedKey: 'wizard',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'field',
            associatedName: 'field',
            associatedKey: 'field',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.prometheus.wizard.wizard.model.WizardFieldModel',
            name: 'form',
            associatedName: 'form',
            associatedKey: 'form',
            foreignKey: 'id'
        }
    ]

});