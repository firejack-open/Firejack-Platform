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


Ext.define('OPF.console.directory.editor.UserBasicInfoFieldSet', {
    extend: 'Ext.container.Container',
    
    layout: 'anchor',
    padding: 10,
    cls: 'basic-info-container',

    initComponent: function() {
        this.idField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'id'
        });

        this.registryNodeIdField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'registryNodeId',
            value: 0
        });

        this.pathField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Path',
            anchor: '100%',
            readOnly: true,
            name: 'path'
        }),

        this.directoryField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Directory',
            labelWidth: 80,
            readOnly: true,
            anchor: '100%',
            name: 'directory'
        }),

        this.usernameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            fieldLabel: 'Username',
            labelWidth: 80,
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'username-field',
            name: 'username'
        });

        this.emailField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            fieldLabel: 'Email',
            labelWidth: 80,
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'email-field',
            name: 'email'
        });

        this.items = [
            this.idField,
            this.registryNodeIdField,
            this.pathField,
            this.directoryField,
            this.usernameField,
            this.emailField
        ];

        this.callParent(arguments);
    }
});