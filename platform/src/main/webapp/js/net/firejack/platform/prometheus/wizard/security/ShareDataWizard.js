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

Ext.define('OPF.prometheus.wizard.security.UserModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'username', type: 'string' },
        { name: 'email', type: 'string' },
        { name: 'firstName', type: 'string' },
        { name: 'middleName', type: 'string' },
        { name: 'lastName', type: 'string' }
    ]
});
Ext.define('OPF.prometheus.wizard.security.RoleModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'assigned', type: 'boolean', defaultValue: true}
    ]
});
Ext.define('OPF.prometheus.wizard.security.ShareDataWizard', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.wizard.security.share-data-wizard',

    statics: {
        id: 'shareDataWizard'
    },

    title: 'Share Data',

    height: 400,
    width: 600,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    modal: true,
    selectedUserModels: null,
    assignedRoleModels: null,
    currentModelLookup: null,
    currentRecord: null,
    rolesLoaded: false,

    initComponent: function() {
        var me = this;

        this.infoSection = Ext.create('Ext.container.Container', {
            autoEl: {tag: 'div'},
            width: 150,
            html: this.populateInfoSectionHtml(1)
        });

        this.searchUserField = Ext.create('Ext.form.field.Text', {
            name: "search",
            padding: '5 5 5 5',
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.searchUsers();
                },
                keyup: function(cmp, e) {
                    me.searchUsers();
                },
                buffer: 500
            }
        });

        this.availableUsersStore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.security.UserModel',
            proxy: {
                type: 'ajax',
                url : this.getUserListUrl(null),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            },
            listeners: {
                beforeload: {
                    fn: function(store, operation) {
                        var searchPhrase = me.searchUserField.getValue();
                        store.proxy.url = me.getUserListUrl(searchPhrase);
                    }
                }
            }
        });

        this.usersGrid = Ext.create('Ext.grid.Panel', {
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'username',
                    header: 'User',
                    sortable: true,
                    flex: 1
                }
            ],
            store: this.availableUsersStore,
            selModel: {
                mode: 'MULTI'
            },
            selType: 'rowmodel',
            padding: '1 5 0 5',
            flex: 1,
            listeners: {
                selectionchange: {
                    fn: function(selModel, selected, eOpts) {
                        me.selectedUserModels = selected;
                        if (selected.length > 0) {
                            me.nextActionButton.enable();
                        }
                    }
                }
                /*select: {
                    fn: function(grid, record, index, eOpts) {
                        me.selectedUserModel = record;
                        me.nextActionButton.enable();
                    }
                }*/
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    store: this.availableUsersStore,
                    dock: 'bottom',
                    displayInfo: true
                }
            ]
        });

        this.availableRolestore = Ext.create('Ext.data.Store', {
            model: 'OPF.prometheus.wizard.security.RoleModel',
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

        this.rolesGrid = Ext.create('Ext.grid.Panel', {
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    header: 'Choose Role to Share',
                    sortable: true,
                    flex: 1
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'assigned',
                    width: 40
                }
            ],
            store: this.availableRolestore,
            //padding: '5 5 0 5',
            flex: 1,
            listeners: {
                select: {
                    fn: function(grid, record, index, eOpts) {
                        var assigned = record.get('assigned');
                        record.set('assigned', !assigned);
                        me.nextActionButton.enable();
                    }
                }
            }
        });

        this.selectUserPanel = Ext.create('Ext.container.Container', {
            //html: '<h1>Select User Panel</h1>'
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                this.searchUserField,
                this.usersGrid
            ]
        });

        this.selectRolePanel = Ext.create('Ext.panel.Panel', {
            //flex: 1,
            //html: '<h1>Select Role Panel</h1>'
            layout: 'fit',
            items: this.rolesGrid
        });

        this.shareDataPanel = Ext.create('Ext.panel.Panel', {
            html: '<h1>Share Data Panel</h1>'
        });

        this.wizardPanel = Ext.create('Ext.container.Container', {
            layout: 'card',
            flex: 1,
            items: [
                this.selectUserPanel,
                this.selectRolePanel,
                this.shareDataPanel
            ]
        });

        this.previousActionButton = Ext.create('Ext.button.Button', {
            text: 'Prev',
            ui: 'save',
            handler: function(btn) {
                var layout = me.wizardPanel.getLayout();
                layout['prev']();
                if (layout.getActiveItem() == me.selectUserPanel) {
                    btn.hide();
                }
                me.nextActionButton.setText('Next');
                me.updateInfoSection(layout.getActiveItem());
            }
        });
        this.nextActionButton = Ext.create('Ext.button.Button', {
            text: 'Next',
            ui: 'save',
            handler: function(btn) {
                var layout = me.wizardPanel.getLayout();
                if (layout.getActiveItem() == me.selectUserPanel) {
                    var selectedModels = me.usersGrid.selModel.getSelection();
                    if (selectedModels.length == 0) {
                        btn.disable();
                    } else if (selectedModels.length == 1) {
                        var userRolePattern = me.populateUserRolePattern(selectedModels[0]);
                        Ext.Ajax.request({
                            url: OPF.Cfg.restUrl('authority/user-role/search-by-pattern'),
                            method: 'POST',
                            jsonData: {"data": userRolePattern},
                            success: function(resp, action) {
                                var json = Ext.decode(resp.responseText);
                                if (json.success) {
                                    me.availableRolestore.loadData(json.data);
                                    me.assignedRoleModels = me.assignedRoleModels == null ? [] : me.assignedRoleModels;
                                    me.availableRolestore.each(function(roleRecord) {
                                        var alreadyAssigned = roleRecord.get('assigned');
                                        if (alreadyAssigned) {
                                            me.assignedRoleModels.push(roleRecord);
                                        }
                                    });
                                    layout['next']();
                                    me.updateInfoSection(layout.getActiveItem());
                                    me.previousActionButton.show();
                                } else {
                                    Ext.Msg.alert('Error', json.message);
                                }
                            },
                            failure: function(resp) {
                                Ext.Msg.alert('Error', resp.message);
                            }
                        });
                    } else {
                        var advancedSearchParams = {};
                        var advancedSearchQueryOperand = {};
                        advancedSearchParams.searchQueries = [];
                        advancedSearchParams.searchQueries.push(advancedSearchQueryOperand);
                        advancedSearchQueryOperand.criteriaList = [];
                        var searchQuery = '{"field":"path","operation":"EQUALS","value":"' + me.currentModelLookup + '"}';
                        advancedSearchQueryOperand.criteriaList.push(searchQuery);
                        Ext.Ajax.request({
                            url: OPF.Cfg.restUrl('authority/role/advanced-search'),
                            method: 'POST',
                            jsonData: {"data": advancedSearchParams},
                            success: function(resp, action) {
                                var json = Ext.decode(resp.responseText);
                                if (json.success) {
                                    me.availableRolestore.loadData(json.data);
                                    me.assignedRoleModels = [];
                                    layout['next']();
                                    me.updateInfoSection(layout.getActiveItem());
                                    me.previousActionButton.show();
                                } else {
                                    Ext.Msg.alert('Error', json.message);
                                }
                            },
                            failure: function(resp) {
                                Ext.Msg.alert('Error', resp.message);
                            }
                        });
                    }
                } else if (layout.getActiveItem() == me.selectRolePanel) {
                    me.calculateRoleChanges();
                    if (me.reducedPermissions.length == 0 && me.grantedPermissions.length == 0) {
                        Ext.Msg.alert('Info', 'You haven\'t changed permissions yet.');
                        me.reducedPermissions = null;
                        me.grantedPermissions = null;
                    } else {
                        layout['next']();
                        me.shareDataPanel.update(me.populateShareDataInfoPanel());
                        btn.setText('Share Data');
                    }
                } else {
                    me.shareData();
                }
                me.updateInfoSection(layout.getActiveItem());
            }
        });
        this.cancelButton = Ext.create('Ext.button.Button', {
            text: 'Cancel',
            ui: 'cancel',
            handler: function(btn) {
                me.hide();
            }
        });

        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                border: false,
                flex: 1,
                items: [
                    this.infoSection,
                    this.wizardPanel
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            '->',
                            this.previousActionButton,
                            this.nextActionButton,
                            this.cancelButton
                        ]
                    }
                ]
            }
        ];

        this.previousActionButton.hide();

        me.searchUsers();

        this.callParent(arguments);
    },

    shareData: function() {
        var me = this;
        var userRoles = [];
        Ext.each(this.grantedPermissions, function(grantedRole, index) {
            Ext.each(me.selectedUserModels, function(userModel, index) {
                var userRole = me.populateUserRolePattern(userModel);
                userRole.role = {};
                userRole.role.lookup = grantedRole.get('lookup');
                userRoles.push(userRole);
            });
        });

        Ext.each(this.reducedPermissions, function(reducedRole, index) {
            Ext.each(me.selectedUserModels, function(userModel, index) {
                var userRole = me.populateUserRolePattern(userModel);
                userRole.role = {};
                userRole.role.lookup = reducedRole.get('lookup');
                userRole.reduced = true;
                userRoles.push(userRole);
            });
        });

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('authority/user-role/save-context-user-roles?syncRoles=false'),
            method: 'PUT',
            jsonData: {"dataList": userRoles},
            success: function(resp, action) {
                var json = Ext.decode(resp.responseText);
                if (json.success) {
                    Ext.Msg.alert('Info', json.message, function() {
                        me.hide();
                    });
                } else {
                    Ext.Msg.alert('Error', json.message);
                }
            },

            failure: function(resp) {
                Ext.Msg.alert('Error', resp.message);
            }
        });
    },

    populateUserRolePattern: function(userModel) {
        var me = this;
        var userRolePattern = {};
        userRolePattern.typeLookup = this.currentModelLookup;
        userRolePattern.user = {};
        userRolePattern.user.id = userModel.get('id');
        var compoundKey = false;
        Ext.each(this.currentRecord.fields.items, function(field) {
            if (field.name == me.currentRecord.idProperty && !field.persist) {
                compoundKey = true;
            }
        });
        if (compoundKey) {
            userRolePattern.complexPK = this.currentRecord.get('id');
        } else {
            userRolePattern.modelId = this.currentRecord.get('id');
        }
        return userRolePattern;
    },

    updateInfoSection: function(activePanel) {
        if (activePanel == this.selectUserPanel) {
            this.infoSection.update(this.populateInfoSectionHtml(1));
        } else if (activePanel == this.selectRolePanel) {
            this.infoSection.update(this.populateInfoSectionHtml(2));
        } else {
            this.infoSection.update(this.populateInfoSectionHtml(3));
        }
    },

    populateInfoSectionHtml: function(step) {
        var html = '<p style="padding: 5 5 0 5;"><span>Share Your Data</span></p><br><br>';
        html += '<ul>';
        html += step == 1 ? '<li><b>1. Choose User</b></li>' : '<li>1. Choose User</li>';
        html += step == 2 ? '<li><b>2. Choose Role</b></li>' : '<li>2. Choose Role</li>';
        html += step == 3 ? '<li><b>3. Share Data</b></li>' : '<li>3. Share Data</li>';
        html += '</ul>';
        return html;
    },

    populateShareDataInfoPanel: function() {
        var html;
        var i;
        if (this.selectedUserModels.length == 1) {
            var selectedUserModel = this.selectedUserModels[0];
            html = "<p style='padding: 5 5 0 5;'>You are about to change permissions for your data item for <span><b>";
            html += selectedUserModel.get('firstName') + '&nbsp' + selectedUserModel.get('lastName');
            html += '(<i>username : <a>' + selectedUserModel.get('username') + '</a></i>)</b></span>.<br><br>';
            html += 'Following are the the changes in permissions for the data item:<br><ul>';
            if (this.grantedPermissions.length > 0) {
                html += '<li>Granted New Permissions: <i>';
                html += this.grantedPermissions[0].get('name');
                for (i = 1; i < this.grantedPermissions.length; i++) {
                    html += ', ' + this.grantedPermissions[i].get('name');
                }
                html += '</i></li>';
            }
            if (this.reducedPermissions.length > 0) {
                html += '<li>Reduced Old Permissions: <i>';
                html += this.reducedPermissions[0].get('name');
                for (i = 1; i < this.reducedPermissions.length; i++) {
                    html += ', ' + this.reducedPermissions[i].get('name');
                }
                html += '</i></li>';
            }
            html += '</p>';
        } else {
            html = "<p style='padding: 5 5 0 5;'>You are about to change permissions for your data item for <span><b>";
            html += this.selectedUserModels[0].get('firstName') + '&nbsp' + this.selectedUserModels[0].get('lastName');
            html += '(<i>username : <a>' + this.selectedUserModels[0].get('username') + '</a></i>)';
            for (i = 1; i < this.selectedUserModels.length; i++) {
                html += ', ' + this.selectedUserModels[i].get('firstName') + '&nbsp' + this.selectedUserModels[i].get('lastName');
                html += '(<i>username : <a>' + this.selectedUserModels[i].get('username') + '</a></i>)';
            }
            html += '</b></span>.<br><br>';
            html += 'Following are the list of the permissions granted:<br><ul><li><i>';
            if (this.grantedPermissions.length > 0) {
                html += this.grantedPermissions[0].get('name');
                for (i = 1; i < this.grantedPermissions.length; i++) {
                    html += ', ' + this.grantedPermissions[i].get('name');
                }
            }
            html += '</i></li></ul>'
        }
        return html;
    },

    calculateRoleChanges: function() {
        if (this.reducedPermissions == null || this.grantedPermissions == null) {
            this.reducedPermissions = [];
            this.grantedPermissions = [];
            var me = this;
            this.availableRolestore.each(function(role) {
                var isCurrentlyAssigned = role.get('assigned');
                var isPreviouslyAssigned = false;
                Ext.each(me.assignedRoleModels, function(previouslyAssgnedRole) {
                    if (previouslyAssgnedRole.get('id') == role.get('id')) {
                        isPreviouslyAssigned = true;
                        return false;
                    }
                    return true;
                });
                if (isCurrentlyAssigned != isPreviouslyAssigned) {
                    if (isCurrentlyAssigned) {
                        me.grantedPermissions.push(role);
                    } else {
                        me.reducedPermissions.push(role);
                    }
                }
            });
        }
    },

    getUserListUrl: function(searchTerm) {
        return OPF.isBlank(searchTerm) ?
            OPF.Cfg.restUrl('directory/user/search?exceptIds=' + OPF.Cfg.USER_INFO.id) :
            OPF.Cfg.restUrl('directory/user/search?exceptIds=' + OPF.Cfg.USER_INFO.id + '&term=' + escape(searchTerm));
    },

    setCurrentRecordInfo: function(currentModelLookup, currentRecord) {
        this.currentModelLookup = currentModelLookup;
        this.currentRecord = currentRecord;
    },

    searchUsers: function() {
        this.availableUsersStore.load();
        if (OPF.isNotEmpty(this.selectedUserModels)) {
            this.selectedUserModels = null;
            this.nextActionButton.disable();
        }
    },

    showWizard: function() {
        this.selectedUserModels = null;

        this.assignedRoleModels = null;
        this.reducedPermissions = null;
        this.grantedPermissions = null;

        this.rolesLoaded = false;

        this.searchUserField.suspendEvents();
        this.searchUserField.setValue("");
        this.searchUserField.resumeEvents();

        this.usersGrid.store.removeAll(true);
        this.nextActionButton.disable();
        this.show();
    }

});