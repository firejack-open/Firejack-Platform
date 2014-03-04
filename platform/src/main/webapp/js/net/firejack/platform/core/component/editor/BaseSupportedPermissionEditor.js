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

//@tag opf-editor



Ext.define('OPF.core.component.editor.BaseSupportedPermissionEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    initComponent: function() {
        var instance = this;

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.permissionFieldSet = Ext.create('OPF.core.component.editor.PermissionFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        var additionalBlocks = [];
        additionalBlocks.push(this.descriptionFields);
        Ext.each(this.additionalBlocks, function(additionalBlock, index) {
            additionalBlocks.push(additionalBlock);
        });
        additionalBlocks.push(this.permissionFieldSet);
        this.additionalBlocks = additionalBlocks;

        this.callParent(arguments);
    },

    showEditPanel: function(registryJsonData) {
        OPF.core.component.editor.BaseSupportedPermissionEditor.superclass.showEditPanel.call(this, registryJsonData);

        var options;
        if (this.saveAs == 'update') {
            this.permissionFieldSet.assignedPermissionsStore.loadData(registryJsonData.permissions);
             var exceptPermissionIds = [];
            for (var i = 0; i < registryJsonData.permissions.length; i++) {
                exceptPermissionIds.push(registryJsonData.permissions[i].id);
            }
            options = {
                params: {
                    exceptIds: Ext.encode(exceptPermissionIds),
                    version: registryJsonData.version
                }
            };
        } else {
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            this.refreshFields(selectedNode);
        }

        this.permissionFieldSet.availablePermissionsStore.load(options);
    },

    hideEditPanel: function() {
        this.managerLayout.tabPanel.remove(this);
        this.managerLayout.tabPanel.doLayout();

        if (isNotEmpty(this.managerLayout.roleListGrid)) {
            this.managerLayout.tabPanel.setActiveTab(this.id);
        }
    },

    onBeforeSave: function(formData) {
        formData.permissions = getJsonOfStore(this.permissionFieldSet.assignedPermissionsStore);
        delete formData.search;
    }

});