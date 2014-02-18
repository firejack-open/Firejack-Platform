/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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