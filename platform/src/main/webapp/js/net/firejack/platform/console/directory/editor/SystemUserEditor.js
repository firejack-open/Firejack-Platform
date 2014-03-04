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


Ext.define('OPF.console.directory.editor.SystemUserEditor', {
    extend: 'OPF.console.directory.editor.UserEditor',

    title: 'SYSTEM ACCOUNT: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.base-user.system-user',

    initEditorComponent: function() {
        var me = this;

        this.associatedPackageDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Associated System',
            subFieldLabel: '',
            name: 'system',
            allowBlank: false,
            registryNodeType: OPF.core.utils.RegistryNodeType.SYSTEM,
            renderDraggableEntityFromNode: function(model) {
                this.setValue(SqGetIdFromTreeEntityId(model.get('id')));
                var description = cutting(model.get('shortDescription'), 70);
                this.renderDraggableEntity('tricon-system', model.get('text'), description, model.get('lookup'));
            }
        });

        this.userInfoFieldSet = Ext.create('OPF.console.directory.editor.UserInfoFieldSet', {
            isOrdinaryUser: false,
            componentFields: [
                this.associatedPackageDropPanel
            ]
        });

        this.userRoleFieldSet = Ext.create('OPF.console.directory.editor.UserRoleFieldSet');

        this.additionalBlocks = [
            this.userInfoFieldSet,
            this.userRoleFieldSet
        ];
    },

    hideEditPanel: function() {
        OPF.console.directory.editor.DirectoryEditor.superclass.hideEditPanel.call(this);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.systemUserGridView);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'update') {
            this.oAuthInfoFieldSet = Ext.create('OPF.console.directory.editor.OAuthInfoFieldSet', this);
            this.fieldsPanel.insert(1, this.oAuthInfoFieldSet);
            this.additionalBlocks = [
                this.additionalBlocks[0],
                this.oAuthInfoFieldSet,
                this.additionalBlocks[1]
            ];
            this.rightNavContainer.update(this.prepareRightNavigation(this.additionalBlocks));
        }
    },

    onAfterSetValue: function(data) {
        if (OPF.isNotEmpty(data)) {
            if (OPF.isNotEmpty(data.system)) {
                this.associatedPackageDropPanel.setValue(data.system.id);
                var description = cutting(data.system.description, 70);
                this.associatedPackageDropPanel.renderDraggableEntity('tricon-system', data.system.name, description, data.system.lookup);
            }
            if (OPF.isEmpty(data.id)) {
                this.oAuthInfoFieldSet.regenerateConsumerSecretButton.disable();
            }
        }
    },

    save: function() {
        this.form.getEl().mask();

        var formData = this.form.getForm().getValues();

        var method = null;
        var url = null;
        if (this.saveAs == 'new') {
            method = 'POST';
            url = this.registryNodeType.generatePostUrl();
        } else if (this.saveAs == 'update') {
            method = 'PUT';
            url = this.registryNodeType.generatePutUrl(formData.id);
        } else {
            this.form.getEl().unmask();
            this.form.getForm().reset();
            this.hideEditPanel();

            Ext.MessageBox.alert('Failure', 'Unknown operation');
            return false;
        }

        formData.roles = getJsonOfStore(this.userRoleFieldSet.assignedRolesStore);
        delete formData.search;

        delete formData.path;
        delete formData.directory;

        var systemId = this.associatedPackageDropPanel.getValue();
        if (OPF.isNotEmpty(systemId)) {
            formData.system = {
                id: systemId
            }
        } else {
            delete formData.system;
        }

        this.saveRequest(formData, url, method);
    },

    onSuccessSaved: function(method, vo) {
    }

});