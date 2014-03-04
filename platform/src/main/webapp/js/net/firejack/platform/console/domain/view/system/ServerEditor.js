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

Ext.define('OPF.console.domain.view.system.PublicKeyFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'x509 Certificate',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    initComponent: function() {
        this.publicKeyField = Ext.ComponentMgr.create({
            xtype: 'opf-textarea',
            labelAlign: 'top',
            fieldLabel: 'Public Key',
            subFieldLabel: '',
            anchor: '100%',
            name: 'publicKey'
        });

        this.items = [
            this.publicKeyField
        ];

        this.callParent(arguments);
    }
});

Ext.define('OPF.console.domain.view.system.ServerEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.server-edit-panel',

    title: 'SERVER: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.server',

    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            needMethodField: false,
            needParentPathField: false,
            needUrlPathField: false
        });

        this.publicKeyFieldSet = Ext.create('OPF.console.domain.view.system.PublicKeyFieldSet');

        this.associatedPackagesFieldSet = Ext.create('OPF.console.domain.view.system.ServerAssociatedPackagesFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.resourceFields,
            this.publicKeyFieldSet,
            this.associatedPackagesFieldSet
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (isNotEmpty(jsonData) && isNotEmpty(jsonData.associatedPackages)) {
            this.associatedPackagesFieldSet.associatedPackageGrid.store.loadData(jsonData.associatedPackages);
        }

        if (OPF.isNotEmpty(jsonData.main)) {
            this.resourceFields.setAllReadOnly(true);
        }
    },

    saveRequest: function(formData, url, method) {
        delete formData.parentPath;

        this.callParent(arguments);
    }
});