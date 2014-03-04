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


Ext.define('OPF.console.directory.editor.UserEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'USER: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.base-user.user',

    initComponent: function() {
        this.userBasicInfoFieldSet = Ext.create('OPF.console.directory.editor.UserBasicInfoFieldSet');

        this.staticBlocks = [
            this.userBasicInfoFieldSet
        ];

        this.initEditorComponent();
        this.callParent(arguments);
    },

    initEditorComponent: function() {
        if (OPF.isEmpty(this.userInfoFieldSet)) {
            this.userInfoFieldSet = Ext.create('OPF.console.directory.editor.UserInfoFieldSet');
        }
        this.userRoleFieldSet = Ext.create('OPF.console.directory.editor.UserRoleGridFieldSet');

        this.additionalBlocks = [
            this.userInfoFieldSet,
            this.userRoleFieldSet
        ];
    },

    hideEditPanel: function() {
        OPF.console.directory.editor.DirectoryEditor.superclass.hideEditPanel.call(this);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.userGridView);
    },

    onAddButton: function() {
        this.saveAs = 'new';

        this.showEditPanel();
    },

    directoryNode: null,    //need to refactored this recursion function

    findParentNodeByType: function(node, type) {
        if (OPF.isNotEmpty(node)) {
            var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(node.data.type.toLowerCase());
            if (registryNodeType == type) {
                this.directoryNode = node;
                return;
            }
            this.findParentNodeByType(node.parentNode, type);
        }
    },

    onBeforeSetValue: function(jsonData) {
    },

    onAfterSetValue: function(jsonData) {
    },

    showEditPanel: function(registryJsonData) {
        if (this.saveAs == 'update') {
            this.userInfoFieldSet.passwordField.updateMode = true;
            this.userInfoFieldSet.passwordConfirmField.updateMode = true;
            this.title = this.registryNodeType.getTitlePrefix() + ': ' + cutting(registryJsonData.username, 10);
        }

        this.managerLayout.tabPanel.add(this);
        this.managerLayout.tabPanel.doLayout();
        this.managerLayout.tabPanel.setActiveTab(this.id);

        OPF.core.validation.FormInitialisation.hideValidationMessages(this.form);

        var model = this.registryNodeType.createModel(registryJsonData);

        if (OPF.isNotEmpty(this.onBeforeSetValue)) {
            this.onBeforeSetValue(registryJsonData);
        }

        this.form.getForm().loadRecord(model);

        if (OPF.isNotEmpty(this.onAfterSetValue)) {
            this.onAfterSetValue(registryJsonData);
        }

        var options = {};
        if (OPF.isNotEmpty(registryJsonData)) {
            if (OPF.isNotEmpty(this.userRoleFieldSet.userRolesStore)) {
                this.userRoleFieldSet.userRolesStore.loadData(registryJsonData.userRoles);
            } else {
                var i, roles = [];
                if (OPF.isNotEmpty(registryJsonData.userRoles)) {
                    for (i = 0; i < registryJsonData.userRoles.length; i++) {
                        roles.push(registryJsonData.userRoles[i].role);
                    }
                }
                this.userRoleFieldSet.assignedRolesStore.loadData(roles);
                var exceptRolesIds = [];
                for (i = 0; i < roles.length; i++) {
                   exceptRolesIds.push(roles[i].id);
                }
                options = {
                   params: {
                      exceptIds: exceptRolesIds
                   }
                };
            }
        }
        if (OPF.isNotEmpty(this.userRoleFieldSet.availableRolesStore)) {
            this.userRoleFieldSet.availableRolesStore.load(options);
        }

        this.fetchDataFromSelectedNode();

        this.formInitialisation(registryJsonData);
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
            if (isBlank(formData.password)) {
                delete formData.password;
                delete formData.passwordConfirm;
            }
        } else {
            this.form.getEl().unmask();
            this.form.getForm().reset();
            this.hideEditPanel();

            Ext.MessageBox.alert('Failure', 'Unknown operation');
            return false;
        }

        var userRoles;
        if (OPF.isEmpty(this.userRoleFieldSet.assignedRolesStore)) {
            userRoles = getJsonOfStore(this.userRoleFieldSet.userRolesStore);
        } else {
            userRoles = [];
            var roles = getJsonOfStore(this.userRoleFieldSet.assignedRolesStore);
            if (OPF.isNotEmpty(roles)) {
                for (var i = 0; i < roles.length; i++) {
                    var userRole = {
                        role: roles[i]
                    };
                    userRoles.push(userRole);
                }
            }
        }
        formData.userRoles = userRoles;

        delete formData.search;

        delete formData.path;
        delete formData.directory;

        this.saveRequest(formData, url, method);
    },

    onSuccessSaved: function(method, vo) {
    },

    fetchDataFromSelectedNode: function() {
        var parentNode = this.managerLayout.navigationPanel.getSelectedNode();

        this.userBasicInfoFieldSet.pathField.setValue(parentNode.data.lookup);
        this.userBasicInfoFieldSet.registryNodeIdField.setValue(SqGetIdFromTreeEntityId(parentNode.data.id));

        this.findParentNodeByType(parentNode, OPF.core.utils.RegistryNodeType.DIRECTORY);
        if (OPF.isNotEmpty(this.directoryNode)) {
            this.userBasicInfoFieldSet.directoryField.setValue(this.directoryNode.data.text);
        }
    }

});