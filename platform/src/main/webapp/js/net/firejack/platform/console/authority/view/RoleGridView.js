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

Ext.define('OPF.console.authority.view.CloneRoleDialog', {
    extend: 'Ext.window.Window',

    title: 'Clone Role',
    width: 300,
    id: null,
    roleStore: null,

    constructor: function(winId, roleStore, cfg) {
        cfg = cfg || {};
        OPF.console.authority.view.CloneRoleDialog.superclass.constructor.call(this, Ext.apply({
            id: winId,
            roleStore: roleStore
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.roleToCloneField = Ext.create('Ext.form.field.Display', {
            name: 'lookupToClone',
            padding: '7 5',
            value: ''
        });
        this.roleNameField = Ext.create('OPF.core.component.TextField', {
            fieldLabel: 'Name',
            fieldAlign: 'left',
            labelWidth: 50,
            name: 'name',
            allowBlank: false
        });
        this.cloneButton = Ext.create('Ext.button.Button', {
            text: 'Clone',
            formBind: true,
            handler: function() {
                Ext.Ajax.request({
                    url: OPF.Cfg.restUrl("/authority/role/clone?roleName=", false) +
                        me.roleNameField.getValue() + "&idOfRoleToClone=" + me.roleToClone,
                    method: 'GET',
                    success: function(response) {
                        var data = Ext.decode(response.responseText);
                        if (data.success) {
                            //probably need to add model directly to the store instead of reloading everything;
                            me.roleStore.load();
                            me.hide();
                        }
                        Ext.MessageBox.alert(data.success ? "Information" : "Error", data.message);
                    },
                    failure: function(response) {
                        var data = Ext.decode(response.responseText);
                        Ext.MessageBox.alert("Error", data.message);
                    }
                });
            }
        });
        this.cancelButton = Ext.create('Ext.button.Button', {
            text: 'Cancel',
            formBind: true,
            handler: function () {
                me.hide();
            }
        });

        this.form = Ext.create('Ext.form.Panel', {
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        this.roleToCloneField,
                        this.roleNameField
                    ]
                }
            ],
            fbar: {
                xtype: 'toolbar',
                items: [
                    '->',
                    this.cloneButton,
                    this.cancelButton
                ]
            }
        });

        this.items = this.form;

        this.callParent(arguments);
    },

    showDialog: function(model) {
        if (OPF.isNotEmpty(model)) {
            var lookup = model.get('lookup');
            var name = model.get('name');
            this.roleToCloneField.setValue(lookup);
            this.roleNameField.setValue(name + "-clone");
            this.roleToClone = model.get('id');
            this.show();
        }
    }

});

/**
 *
 */
Ext.define('OPF.console.authority.view.RoleGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'ROLES',
    iconCls: 'sm-role-icon',
    iconGridUrl: '/images/icons/16/privilege_16.png',
    entityName: 'Role',
    registryNodeType: OPF.core.utils.RegistryNodeType.ROLE,

    initComponent: function() {
        var me = this;

        this.cloneRoleButton = Ext.create('Ext.button.Button', {
            text: 'Clone Role',
            handler: function() {
                var selection = me.grid.getSelectionModel().getSelection();
                var model = selection == null ? null :
                    Ext.isArray(selection) ? (selection.length == 1 ? selection[0] : null) : selection;
                if (model != null) {
                    var winId = 'cloneRoleDlg';
                    var dlg = Ext.WindowManager.get(winId);
                    if (dlg == null) {
                        dlg = Ext.create('OPF.console.authority.view.CloneRoleDialog', winId, me.store);
                    }
                    dlg.showDialog(model);
                }
            }
        });

        this.gridTButtons = [
            this.cloneRoleButton
        ];

        this.callParent(arguments);
        this.cloneRoleButton.disable();
    },

    onSelectionChanged: function(selectionModel, selectedModels, eOpts) {
        if (selectedModels == null || selectedModels.length == 0) {
            this.cloneRoleButton.disable();
        } else {
            this.cloneRoleButton.enable();
        }
    }

});