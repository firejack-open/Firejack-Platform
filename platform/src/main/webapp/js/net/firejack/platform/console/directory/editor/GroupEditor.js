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


Ext.define('OPF.console.directory.editor.GroupEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'GROUP: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.user.group',

    selectedDirectoryId: null,

    initComponent: function() {
        var instance = this;

        this.directoriesField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'Directory',
            subFieldLabel: '',
            anchor: '100%',
            name: 'directory',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'remote',
            store: new Ext.data.Store({
                model: 'OPF.console.directory.model.Directory',
                proxy: {
                    type: 'ajax',
                    url : OPF.core.utils.RegistryNodeType.DIRECTORY.generateUrl(),
                    reader: {
                        type: 'json',
                        root: 'data'
                    }
                }
            }),
            valueField: 'id',
            displayField: 'name',
            listeners: {
                select: function(cmp, e) {
                    instance.selectedDirectoryId = cmp.getValue();
                }
            }
        });

        this.nodeBasicFields = new OPF.core.component.editor.BasicInfoFieldSet(this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this, [ this.directoriesField ]);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.groupRoleFieldSet = Ext.create('OPF.console.directory.editor.GroupRoleFieldSet');

        this.additionalBlocks = [
            this.descriptionFields,
            this.groupRoleFieldSet
        ];

        this.callParent(arguments);
    },

    onBeforeSave: function(formData) {
        formData.directory = {
            id: this.selectedDirectoryId
        };
        formData.assignedRoles = getJsonOfStore(this.groupRoleFieldSet.assignedRolesStore);
        delete formData.search
    },

    onBeforeSetValue: function(jsonData) {
        if (OPF.isEmpty(jsonData)) {
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            var selectedNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeById(selectedNode.data.id);
            if (OPF.core.utils.RegistryNodeType.DIRECTORY == selectedNodeType) {
                this.directoriesField.setValue(selectedNode.data.text);
                this.selectedDirectoryId = SqGetIdFromTreeEntityId(selectedNode.data.id);
            }
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            this.directoriesField.setValue(jsonData.directory.name);
            this.selectedDirectoryId = jsonData.directory.id;

            var options = {};
            var groupRoles = jsonData.assignedRoles;
            groupRoles = OPF.isEmpty(groupRoles) ? [] : groupRoles;
            if (groupRoles.length == 0) {
                options = {};
            } else {
                var exceptRolesIds = [];
                for (var i = 0; i < groupRoles.length; i++) {
                    exceptRolesIds.push(groupRoles[i].id);
                }
                options = {
                    params: {
                        exceptIds: exceptRolesIds
                    }
                };
            }
            this.groupRoleFieldSet.assignedRolesStore.loadData(groupRoles);
            this.groupRoleFieldSet.availableRolesStore.load(options);
        }
    }

});