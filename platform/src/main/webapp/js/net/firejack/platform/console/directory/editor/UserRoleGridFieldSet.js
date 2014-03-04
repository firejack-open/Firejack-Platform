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

Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.dd.*'
]);

/**
 * The UserRoleGridFieldSet component inherited from LabelContainer component and
 * containing the available and assigned user role grids.
 */
Ext.define('OPF.console.directory.editor.UserRoleGridFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'User Permissions',
    anchorFieldLabel: 'Roles',
    subFieldLabel: '',

    layout: 'anchor',

    initComponent: function() {
        this.userRolesStore = new Ext.data.Store({
            model: 'OPF.console.authority.model.UserRole',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                },
                writer: {
                    type: 'json'
                }
            }
        });

        var me = this;

        this.assignedRolesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            title: 'Roles',
            multiSelect: true,
            stripeRows: false,
            store: this.userRolesStore,
            anchor: '100%',
            height: 240,
            itemId: 'avails',
            viewConfig: {
                getRowClass: function(record, rowIndex, rowParams, store) {
                    var odd = rowIndex % 2 == 1;
                    return odd ? 'roles-odd-row' : 'roles-even-row';
                }
            },
            columns: [
                {
                    text: 'Role', xtype: 'templatecolumn', flex: 2 ,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                           '<div class="role-data-container">',
                              '<h3><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '"/>{role.name}</h3>',
                              '<div class="role-path">{role.path}</div>',
                              '<div class="role-description"><span>{role.description}</span></div>',
                           '</div>',
                        '</tpl>'/*,
                        '<div class="x-clear"></div>'*/
                    )
                },
                {
                    text: 'Scope', xtype: 'templatecolumn', flex: 1 ,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                           '<div class="role-data-container">',
                              '<tpl if="this.isGlobal(role.global)">',
                                 '<h3>Global</h3>',
                                 '<div class="role-description"><span>User has these permissions across all data</span></div>',
                              '</tpl>',
                              '<tpl if="this.isContext(role.global)">',
                                 '<h3>Context</h3>',
                                 '<div class="role-description"><span>User has these permissions across specific data:</span></div>',
                                 '<div class="role-description"><span>( {typeLookup} : {modelId} )</span></div>',
                              '</tpl>',
                           '</div>',
                        '</tpl>',
                        '<div class="x-clear"></div>',
                        {
                            isGlobal: function(global) {
                                return global == null || global;
                            },
                            isContext: function(global) {
                                return global != null && !global;
                            }
                        }
                    )
                }
            ],
            bbar: [
                { xtype: 'tbfill' },
                OPF.Ui.createBtn('Add', 50, 'add-user-role', {
                    iconCls: 'silk-add',
                    handler: function() {
                        var winId = 'UserRoleAssignmentDialog';
                        var rolesDlg = Ext.WindowMgr.get(winId);
                        if (OPF.isEmpty(rolesDlg)) {
                            rolesDlg = Ext.create('OPF.console.directory.editor.UserRoleAssignmentDialog', winId);
                        }
                        rolesDlg.showDialog(me);
                    }
                }),
                '-',
                OPF.Ui.createBtn('Delete', 65, 'delete-user-role', {
                    iconCls: 'silk-delete',
                    handler: function() {
                        var selection = me.assignedRolesGrid.getSelectionModel().getSelection();
                        if (OPF.isNotEmpty(selection) && selection.length > 0) {
                            for (var i = 0; i < selection.length; i++) {
                                me.userRolesStore.remove(selection[i]);
                            }
                        }
                    }
                })
            ]
        });

        this.items = this.assignedRolesGrid;

        this.callParent(arguments);
    },

    refreshFields: function(selectedNode) {
        //
    },

    addNewUserRoles: function(newContextRoles) {
        if (OPF.isNotEmpty(newContextRoles) && Ext.isArray(newContextRoles) && newContextRoles.length > 0) {
            var existingRoles = this.userRolesStore.getRange();
            for (var i = 0; i < newContextRoles.length; i++) {
                if (this.roleIsNotDuplicated(newContextRoles[i], existingRoles)) {
                    existingRoles.push(newContextRoles[i]);
                }
            }
            this.userRolesStore.loadData(existingRoles);
        }
    },

    roleIsNotDuplicated: function(userRole, existingRoles) {
        var role = userRole.get('role'),
            modelId = userRole.get('modelId'),
            modelType = userRole.get('typeLookup');
        var notDuplicated = true;
        for (var i = 0; i < existingRoles.length; i++) {
            if (existingRoles[i].get('role').id == role.id &&
                existingRoles[i].get('modelId') == modelId &&
                existingRoles[i].get('typeLookup') == modelType) {
                notDuplicated = false;
                break;
            }
        }
        return notDuplicated;
    }

});