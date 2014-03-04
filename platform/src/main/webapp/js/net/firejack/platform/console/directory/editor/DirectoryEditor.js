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


Ext.define('OPF.console.directory.editor.DirectoryEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'DIRECTORY: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.directory',

    /**
     *
     */
    initComponent: function() {

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.directoryFields = Ext.create('OPF.console.directory.editor.DirectoryFieldSet', this);

        this.fieldGridFieldSet = Ext.create('OPF.core.component.editor.FieldGridFieldSet');

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.directoryFields,
            this.fieldGridFieldSet
        ];

        this.callParent(arguments);
    },

    hideEditPanel: function() {
        OPF.console.directory.editor.DirectoryEditor.superclass.hideEditPanel.call(this);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.directoryGridView);
    },

    onSuccessSaved: function(method, vo) {
        var node;
        if (method == 'POST') {
            var parentNode = this.selectedParentNode;
            if (parentNode.isLoaded()) {
                var model = Ext.create('OPF.core.model.RegistryNodeTreeModel', vo.data[0]);
                node = Ext.data.NodeInterface.decorate(model);
                parentNode.appendChild(node);
                //this.managerLayout.navigationPanel.sort();
            }
            parentNode.expand();
        } else if (method == 'PUT') {
            node = Ext.create('OPF.core.model.RegistryNodeTreeModel', vo.data[0]);
            this.selfNode.set('text', node.get('text'));
            this.selfNode.set('lookup', node.get('lookup'));
            //this.managerLayout.navigationPanel.sort();
        }
    },


    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'new') {
            this.fieldGridFieldSet.cleanFieldStore();
        }
        if (OPF.isNotEmpty(jsonData)) {
            Ext.each(jsonData.fields, function(field, index) {
                field.allowedValues = Ext.isArray(field.allowedValues) ? field.allowedValues : [];
            });
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData.fields)) {
            this.fieldGridFieldSet.grid.store.loadData(jsonData.fields);
        }
        this.directoryFields.initialDirectoryType = jsonData.directoryType;
        if (this.directoryFields.directoryTypeField.getValue() == 'LDAP' && jsonData.status == 'ACTIVE') {
            this.directoryFields.setLdapManagerBtnState();
        } else {
            this.directoryFields.ldapManagerButton.disable();
        }
    },

    onBeforeSave: function(formData) {
        formData.fields = getJsonOfStore(this.fieldGridFieldSet.grid.store);
        Ext.each(formData.fields, function(field, index) {
            if (!Ext.isArray(field.allowedValues)) {
                field.arrAllowedValues = [];
                if (OPF.isNotEmpty(field.allowedValues)) {
                    var values = field.allowedValues.split("|");
                    Ext.each(values, function(value, index) {
                        field.arrAllowedValues.push(value);
                    });
                }
                field.allowedValues = field.arrAllowedValues;
                delete field.arrAllowedValues;
            }
            delete field.fieldTypeName;
        });
    },

    save: function() {
        if (this.directoryFields.directoryTypeField.getValue() == 'LDAP') {
            this.directoryFields.serverNameField.validate();
            this.directoryFields.portField.validate();
            this.directoryFields.baseDNField.validate();
            this.directoryFields.rootDNField.validate();
            var serverAddress = this.directoryFields.serverNameField.getValue();
            var msg = null;
            if (OPF.isBlank(serverAddress)) {
                msg = 'Server name should not be blank for LDAP Directory.';
            }
            var port = this.directoryFields.portField.getValue();
            if (OPF.isBlank(port)) {
                msg = 'Port should not be blank for LDAP Directory.';
            }
            var baseDN = this.directoryFields.baseDNField.getValue();
            if (OPF.isBlank(baseDN)) {
                msg = 'BaseDN field should not be blank for LDAP Directory.';
            }
            var rootDN = this.directoryFields.rootDNField.getValue();
            if (OPF.isBlank(rootDN)) {
                msg = 'RootDN field should not be blank for LDAP Directory.';
            }
            if (msg != null) {
                Ext.MessageBox.alert('Warning', msg);
                return false;
            }

        }

        this.callParent(arguments);
    }

});


