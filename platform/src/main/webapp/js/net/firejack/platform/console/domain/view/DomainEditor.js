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

Ext.ns('FJK.platform.clds.ui');

/**
 *
 */
Ext.define('OPF.console.domain.view.DomainEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.domain-editor',

    title: 'DOMAIN: [New]',

    editableWithChild: true,

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.domain',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.nodeBasicFields = new OPF.core.component.editor.BasicInfoFieldSet(this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.domainResourceFields = Ext.create('OPF.console.domain.editor.DomainResourceAccessPanel', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
		    this.domainResourceFields
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData) && OPF.isNotEmpty(jsonData.database)) {
            this.domainResourceFields.databaseDropPanel.setValue(jsonData.database.id);
            var description = cutting(jsonData.database.description, 70);
            this.domainResourceFields.databaseDropPanel.renderDraggableEntity('tricon-database', jsonData.database.name, description, jsonData.database.lookup);
            this.domainResourceFields.xaSupportField.setValue(jsonData.xaSupport);
        }
        if (OPF.isNotEmpty(jsonData) && OPF.isNotEmpty(jsonData.dataSource)) {
            var isActive = jsonData.dataSource ? jsonData.dataSource : false;
            this.domainResourceFields.databaseDropPanel.setDisabled(!isActive);
            this.domainResourceFields.databaseDropPanel.setReadOnly(!isActive);
            this.domainResourceFields.xaSupportField.setDisabled(!isActive);

//            this.domainResourceFields.wsdlLocationField.setDisabled(!isActive);
            this.domainResourceFields.wsdlLocationField.setValue(OPF.ifBlank(jsonData.wsdlLocation, ''));
//            this.domainResourceFields.reverseEngineeringButton.isWSDLActive = true;
        }
        var isREActive = OPF.isNotEmpty(jsonData) && (
                            (OPF.isNotEmpty(jsonData.database) && OPF.isNotEmpty(jsonData.dataSource) && jsonData.dataSource) ||
                            OPF.isNotBlank(jsonData.wsdlLocation));
//        this.domainResourceFields.reverseEngineeringButton.isDBActive = isREActive;
        this.domainResourceFields.reverseEngineeringButton.setDisabled(!isREActive);
    },

    onBeforeSave: function(formData) {
        formData.dataSource = this.domainResourceFields.dataSourceField.getValue();
        formData.xaSupport = this.domainResourceFields.xaSupportField.getValue();
        var databaseId = this.domainResourceFields.databaseDropPanel.getValue();
        if (OPF.isNotEmpty(databaseId) && formData.dataSource) {
            formData.database = {
                id: databaseId
            }
        } else {
            delete formData.database;
        }
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.editor.DomainResourceAccessPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.domain-resource-access',

    title: 'Resource Access',
    layout: 'anchor',
    preventHeader: true,
    border: false,
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.editor.DomainResourceAccessPanel.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.prefixField = OPF.Ui.textFormField('prefix', 'Naming Prefix', {
            labelAlign: 'top',
            subFieldLabel: 'Provide an abbreviation for this area of your database'
        });

        this.dataSourceField = OPF.Ui.formCheckBox('dataSource', 'Separate Data Source for current domain', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.xaSupportField = OPF.Ui.formCheckBox('xaSupport', 'Is support XA Transaction for current data source', {
            labelAlign: 'top',
            subFieldLabel: '',
            checked: true
        });

        this.databaseDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Database Association for all children entities',
            subFieldLabel: 'Drag and Drop from Cloud Navigator',
            name: 'database',
            registryNodeType: OPF.core.utils.RegistryNodeType.DATABASE,
            readOnly: true,
            disabled: true,
            setDefaultValue: function(defaultValue) {
                var jsonData = Ext.decode(defaultValue);
                me.databaseDropPanel.setValue(jsonData.id);
                var description = cutting(jsonData.description, 70);
                me.databaseDropPanel.renderDraggableEntity('tricon-database', jsonData.name, description, jsonData.lookup);
            }
        });

        this.wsdlLocationField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'wsdlLocation',
            labelAlign: 'top',
            fieldLabel: 'WSDL Location',
            subFieldLabel: 'Provide an wsdl location for reverse engineering',
            anchor: '100%'
        });

        this.reverseEngineeringButton = OPF.Ui.createBtn('Reverse Engineering', 120, 'reverse-engineering', {
            disabled: true,
            height: 28
        });

        this.items = [
            this.prefixField,
            this.dataSourceField,
            this.xaSupportField,
            this.databaseDropPanel,
            this.wsdlLocationField,
            this.reverseEngineeringButton
        ];

        this.callParent(arguments);
    }
});