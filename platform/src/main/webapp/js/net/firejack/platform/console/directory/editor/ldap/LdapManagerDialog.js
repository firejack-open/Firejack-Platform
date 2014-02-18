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

Ext.define('OPF.console.directory.editor.ldap.UsersTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ldap-users-tab',

    title: 'Users',

    parentDialog: null,

    constructor: function(parentDialog, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.UsersTab.superclass.constructor.call(this, Ext.apply({
            parentDialog: parentDialog
        }, cfg));
    },

    initComponent: function() {
        var me = this;
        this.editedModel = null;
        this.searchUsersField = Ext.create('Ext.form.field.Text', { width: 150 });
        this.deleteUserButton = Ext.create('Ext.button.Button', {
            text: 'Delete', width: 60, iconCls: 'silk-delete',
            handler: function() {
                var lastSelectedModel = me.ldapUsersGridView.getSelectionModel().getLastSelected();
                if (lastSelectedModel == null) {
                    Ext.MessageBox.alert('Information', 'Nothing to delete');
                } else {
                    var url = OPF.Cfg.restUrl('/directory/ldap/user/');
                    url += lastSelectedModel.get('username');
                    url += '?directoryId=' + me.parentDialog.directoryId;
                    Ext.Ajax.request({
                        url: url,
                        method: 'DELETE',
                        success: function(response) {
                            var vo = Ext.decode(response.responseText);
                            if (vo.success) {
                                var usernameOfDeletedUser = vo.data[0].username;
                                var userToDelete = null;
                                me.ldapUsersStore.each(function(model) {
                                    if (usernameOfDeletedUser == model.get('username')) {
                                        userToDelete = model;
                                        return false;
                                    }
                                });
                                if (userToDelete != null) {
                                    if (me.editedModel != null &&
                                        me.editedModel.get('username') == usernameOfDeletedUser) {
                                        me.disableEditForm();
                                    }
                                    me.ldapUsersStore.remove(userToDelete);
                                }
                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                            } else {
                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                            }
                        },

                        failure: function(response) {
                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                        }
                    });
                }
            }
        });
        this.ldapUsersStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });
        this.ldapUsersGridView = Ext.create('Ext.grid.Panel', {
            height: 200,
            store: this.ldapUsersStore,
            tbar: [
                OPF.Ui.xSpacer(5),
                this.searchUsersField,
                OPF.Ui.xSpacer(5),
                Ext.create('Ext.button.Button', {
                    text: 'Search',
                    handler: function() {
                        me.searchUsers(me.searchUsersField.getValue());
                    }
                }),
                '->',
                Ext.create('Ext.button.Button', {
                    text: 'Add', width: 50, iconCls: 'silk-add',
                    handler: function() {
                        me.editedModel = null;
                        me.editUser(Ext.create('OPF.console.directory.model.UserModel'));
                    }
                }),
                '-',
                this.deleteUserButton
            ],
            columns: [
                { dataIndex: 'username', text: 'Username', sortable: false, renderer: 'htmlEncode', width: 150 },
                { dataIndex: 'firstName', text: 'First Name', sortable: false, renderer: 'htmlEncode', width: 150 },
                { dataIndex: 'lastName', text: 'Last Name', sortable: false, renderer: 'htmlEncode', width: 150 },
                { dataIndex: 'email', text: 'Email', sortable: false, renderer: 'htmlEncode', flex: 1 }
            ],
            listeners: {
                itemclick: function() {
                    me.deleteUserButton.enable();
                },
                itemdblclick: function(gridView, model) {
                    me.editedModel = me.ldapUsersGridView.getSelectionModel().getLastSelected();
                    me.deleteUserButton.enable();
                    me.editUser(model);
                }
            }
        });

        this.usernameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Username',
            subFieldLabel: '',
            name: 'username'
        });

        this.firstNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'First name',
            subFieldLabel: '',
            name: 'firstName'
        });

        this.lastNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Last Name',
            subFieldLabel: '',
            name: 'lastName'
        });

        this.emailField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Email',
            subFieldLabel: '',
            name: 'email'
        });

        this.passwordField = Ext.create('OPF.core.component.TextField', {
            id: 'ldapPassword',
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: '',
            name: 'password',
            inputType: 'password',
            vtype: 'password',
            updateMode: false,
            compareWithId: 'ldapPasswordConfirm'
        });

        this.confirmPasswordField = Ext.create('OPF.core.component.TextField', {
            id: 'ldapPasswordConfirm',
            labelAlign: 'top',
            fieldLabel: 'Confirm Password',
            subFieldLabel: '',
            name: 'passwordConfirm',
            inputType: 'password',
            vtype: 'password',
            updateMode: false,
            compareWithId: 'ldapPassword'
        });

        this.form = Ext.create('Ext.form.Panel', {
            items: {
                xtype: 'container',
                height: 250,
                layout: { type: 'hbox', align: 'middle', pack: 'center' },
                items: [
                    {
                        xtype: 'container',
                        items: [
                            this.usernameField,
                            this.firstNameField,
                            this.lastNameField
                        ]
                    },
                    {xtype: 'container', width: 20},
                    {
                        xtype: 'container',
                        items: [
                            this.emailField,
                            this.passwordField,
                            this.confirmPasswordField
                        ]
                    }
                ]
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        [
                            '->',
                            Ext.create('Ext.button.Button', {
                                text: 'Save', width: 50,
                                handler: function() {
                                    var formData = me.form.getValues();
                                    var method = me.editedModel == null ? 'POST' : 'PUT';
                                    var url = OPF.Cfg.restUrl('/directory/ldap/user');
                                    if (me.editedModel != null) {
                                        url += '/' + formData.username;
                                    }
                                    url += '?directoryId=' + me.parentDialog.directoryId;

                                    Ext.Ajax.request({
                                        url: url,
                                        method: method,
                                        jsonData: {"data": formData},
                                        success: function(response) {
                                            var vo = Ext.decode(response.responseText);
                                            if (vo.success) {
                                                if (OPF.isEmpty(vo.data) || vo.data.length == 0) {
                                                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK,
                                                        'No user data returned from server. Server message: ' + vo.message);
                                                } else {
                                                    if (me.editedModel == null) {
                                                        var userModel = Ext.create(
                                                            'OPF.console.directory.model.UserModel', vo.data[0]);
                                                        me.ldapUsersStore.add(userModel);
                                                    } else {
                                                        me.editedModel.set('username', vo.data[0].username);
                                                        me.editedModel.set('firstName', vo.data[0].firstName);
                                                        me.editedModel.set('lastName', vo.data[0].lastName);
                                                        me.editedModel.set('email', vo.data[0].email);
                                                        me.editedModel = null;
                                                    }
                                                    me.disableEditForm();
                                                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                                                }
                                            } else {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                            }
                                        },

                                        failure: function(response) {
                                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                        }
                                    });
                                }
                            }),
                            Ext.create('Ext.button.Button', {
                                text: 'Reset', width: 55,
                                handler: function() {
                                    me.editUser(Ext.create('OPF.console.directory.model.UserModel'));
                                }
                            }),
                            Ext.create('Ext.button.Button', {
                                text: 'Cancel', width: 60,
                                handler: function() {
                                    me.parentDialog.hide();
                                }
                            })
                        ]
                    ]
                }
            ]
        });

        this.items = [
            this.ldapUsersGridView,
            this.form
        ];

        this.callParent(arguments);
    },

    searchUsers: function(searchTerm) {
        var url = OPF.Cfg.restUrl('/directory/ldap/user/search', true);
        url += '?directoryId=' + this.parentDialog.directoryId;
        if (OPF.isNotBlank(searchTerm)) {
            url += '&term=' + searchTerm;
        }
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success: function(response) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    me.deleteUserButton.disable();
                    me.ldapUsersStore.loadData(OPF.isEmpty(vo.data) || vo.data.length == 0 ? [] : vo.data);
                    me.disableEditForm();

                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                } else {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
            }
        });
    },

    disableEditForm: function() {
        this.inactivateField(this.usernameField);
        this.inactivateField(this.firstNameField);
        this.inactivateField(this.lastNameField);
        this.inactivateField(this.emailField);
        this.inactivateField(this.passwordField);
        this.inactivateField(this.confirmPasswordField);
    },

    inactivateField: function(field) {
        if (OPF.isNotEmpty(field)) {
            field.reset();
            field.disable();
        }
    },

    activateField: function(field, value) {
        if (OPF.isNotEmpty(field)) {
            field.setValue(OPF.isEmpty(value) ? '' : value);
            field.enable();
            field.setReadOnly(false);
        }
    },

    editUser: function(model) {
        var updateMode = this.editedModel != null;
        this.passwordField.updateMode = updateMode;
        this.confirmPasswordField.updateMode = updateMode;
        this.activateField(this.usernameField, model.get('username'));
        this.usernameField.setReadOnly(updateMode);
        this.activateField(this.firstNameField, model.get('firstName'));
        this.activateField(this.lastNameField, model.get('lastName'));
        this.activateField(this.emailField, model.get('email'));
        this.activateField(this.passwordField, null);
        this.activateField(this.confirmPasswordField, null);
        if (updateMode) {
            this.usernameField.setReadOnly(true);
            this.passwordField.setReadOnly(true);
            this.confirmPasswordField.setReadOnly(true);
        }
    },

    listeners: {
        activate: function() {
            this.searchUsers(null);
            var constraints = new OPF.core.validation.FormInitialisation(
                OPF.core.utils.RegistryNodeType.USER.getConstraintName());
            constraints.initConstraints(this.form, null);
        }
    }
});

Ext.define('OPF.console.directory.editor.ldap.GroupsTab', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ldap-groups-tab',

    title: 'Groups',

    parentDialog: null,

    constructor: function(parentDialog, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.GroupsTab.superclass.constructor.call(this, Ext.apply({
            parentDialog: parentDialog
        }, cfg));
    },

    initComponent: function() {
        var me = this;
        this.editedModel = null;
        this.searchGroupsField = Ext.create('Ext.form.field.Text', { width: 150 });
        this.deleteGroupButton = Ext.create('Ext.button.Button', {
            text: 'Delete', width: 60, iconCls: 'silk-delete',
            handler: function() {
                var lastSelectedModel = me.ldapGroupsGridView.getSelectionModel().getLastSelected();
                if (lastSelectedModel == null) {
                    Ext.MessageBox.alert('Information', 'Nothing to delete');
                } else {
                    var url = OPF.Cfg.restUrl('/directory/ldap/group/');
                    url += lastSelectedModel.get('name');
                    url += '?directoryId=' + me.parentDialog.directoryId;
                    Ext.Ajax.request({
                        url: url,
                        method: 'DELETE',
                        success: function(response) {
                            var vo = Ext.decode(response.responseText);
                            if (vo.success) {
                                var nameOfDeletedGroup = vo.data[0].name;
                                var groupToDelete = null;
                                me.ldapGroupsStore.each(function(model) {
                                    if (nameOfDeletedGroup == model.get('name')) {
                                        groupToDelete = model;
                                        return false;
                                    }
                                });
                                if (groupToDelete != null) {
                                    if (me.editedModel != null &&
                                        me.editedModel.get('name') == nameOfDeletedGroup) {
                                        me.disableEditForm();
                                    }
                                    me.ldapGroupsStore.remove(groupToDelete);
                                }
                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                            } else {
                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                            }
                        },

                        failure: function(response) {
                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                        }
                    });
                }
            }
        });
        this.ldapGroupsStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.Group',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });
        this.ldapGroupsGridView = Ext.create('Ext.grid.Panel', {
            height: 200,
            store: this.ldapGroupsStore,
            tbar: [
                OPF.Ui.xSpacer(5),
                this.searchGroupsField,
                OPF.Ui.xSpacer(5),
                Ext.create('Ext.button.Button', {
                    text: 'Search',
                    handler: function(btn) {
                        me.searchGroups(me.searchGroupsField.getValue());
                    }
                }),
                '->',
                Ext.create('Ext.button.Button', {
                    text: 'Add', width: 50, iconCls: 'silk-add',
                    handler: function() {
                        me.editedModel = null;
                        me.editGroup(Ext.create('OPF.console.directory.model.Group'), true);
                    }
                }),
                '-',
                this.deleteGroupButton
            ],
            columns: [
                { dataIndex: 'name', text: 'Group Name', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ],
            listeners: {
                itemclick: function() {
                    me.deleteGroupButton.enable();
                },
                itemdblclick: function(gridView, model) {
                    me.editedModel = me.ldapGroupsGridView.getSelectionModel().getLastSelected();
                    me.deleteGroupButton.enable();
                    me.editGroup(model);
                }
            }
        });

        this.groupNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'left',
            fieldLabel: 'Group Name',
            subFieldLabel: '',
            name: 'name',
            width: 250
        });

        this.availableUsersStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });

        this.usersAvailableInGroupGrid = Ext.create('Ext.grid.Panel', {
            store: this.availableUsersStore,
            flex: 1,
            columns: [
                { dataIndex: 'username', text: 'Available Users', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ]
        });

        this.assignedUsersStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.directory.model.UserModel',
            proxy: {
                type: 'memory',
                reader: { type: 'json' },
                writer: { type: 'json' }
            }
        });

        this.usersAssignedToGroupGrid = Ext.create('Ext.grid.Panel', {
            store: this.assignedUsersStore,
            flex: 1,
            columns: [
                { dataIndex: 'username', text: 'Assigned Users', sortable: false,
                    renderer: 'htmlEncode', flex: 1, menuDisabled: true }
            ]
        });

        this.assignAllUsersButton = Ext.create('Ext.button.Button', {
            text: '>>',
            width: 30,
            handler: function(btn) {
                var availableUsers = me.availableUsersStore.getRange();
                me.assignedUsersStore.add(availableUsers);
                me.availableUsersStore.removeAll();
            }
        });

        this.assignSelectedUsersButton = Ext.create('Ext.button.Button', {
            text: '>',
            width: 30,
            handler: function(btn) {
                var selectedModels = me.usersAvailableInGroupGrid.getSelectionModel().getSelection();
                if (selectedModels != null && selectedModels.length > 0) {
                    me.assignedUsersStore.add(selectedModels);
                    me.availableUsersStore.remove(selectedModels);
                }
            }
        });

        this.removeSelectedUsersButton = Ext.create('Ext.button.Button', {
            text: '<',
            width: 30,
            handler: function(btn) {
                var selectedModels = me.usersAssignedToGroupGrid.getSelectionModel().getSelection();
                if (selectedModels != null && selectedModels.length > 0) {
                    me.availableUsersStore.add(selectedModels);
                    me.assignedUsersStore.remove(selectedModels);
                }
            }
        });

        this.removeAllUsersButton = Ext.create('Ext.button.Button', {
            text: '<<',
            width: 30,
            handler: function(btn) {
                var assignedUsers = me.assignedUsersStore.getRange();
                me.availableUsersStore.add(assignedUsers);
                me.assignedUsersStore.removeAll();
            }
        });

        this.items = [
            this.ldapGroupsGridView,
            {
                //xtype: 'container',
                xtype: 'panel',
                items: [
                    {
                        xtype: 'container',
                        layout: { type: 'hbox', align: 'middle', pack: 'center' },
                        height: 50,
                        items: [
                            this.groupNameField
                        ]
                    },
                    {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        height: 180,
                        items: [
                            {xtype: 'container', width: 20},
                            this.usersAvailableInGroupGrid,
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center',
                                    pack: 'center'
                                },
                                width: 50,
                                items: [
                                    this.assignAllUsersButton,
                                    this.assignSelectedUsersButton,
                                    this.removeSelectedUsersButton,
                                    this.removeAllUsersButton
                                ]
                            },
                            this.usersAssignedToGroupGrid,
                            {xtype: 'container', width: 20}
                        ]
                    },
                    { xtype: 'container', height: 20 }
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        items: [
                            [
                                '->',
                                Ext.create('Ext.button.Button', {
                                    text: 'Save', width: 50,
                                    handler: function() {
                                        if (OPF.isBlank(me.groupNameField.getValue())) {
                                            Ext.MessageBox.alert('Warning', 'Group name should not be blank.');
                                        } else {
                                            var formData = {name: me.groupNameField.getValue()};
                                            var assignedUserModels = me.assignedUsersStore.getRange();
                                            if (assignedUserModels != null && assignedUserModels.length > 0) {
                                                var assignedUsers = [];
                                                for (var i = 0; i < assignedUserModels.length; i++) {
                                                    assignedUsers.push({username: assignedUserModels[i].get('username')});
                                                }
                                                formData.assignedUsers = assignedUsers;
                                            }

                                            var method = me.editedModel == null ? 'POST' : 'PUT';
                                            var url = OPF.Cfg.restUrl('/directory/ldap/group');
                                            if (me.editedModel != null) {
                                                url += '/' + formData.name;
                                            }
                                            url += '?directoryId=' + me.parentDialog.directoryId;

                                            Ext.Ajax.request({
                                                url: url,
                                                method: method,
                                                jsonData: {"data": formData},
                                                success: function(response) {
                                                    var vo = Ext.decode(response.responseText);
                                                    if (vo.success) {
                                                        if (OPF.isEmpty(vo.data) || vo.data.length == 0) {
                                                            OPF.Msg.setAlert(OPF.Msg.STATUS_OK,
                                                                'No group data returned from server. Server message: ' + vo.message);
                                                        } else {
                                                            if (me.editedModel == null) {
                                                                var groupModel = Ext.create(
                                                                    'OPF.console.directory.model.Group', vo.data[0]);
                                                                me.ldapGroupsStore.add(groupModel);
                                                            } else {
                                                                me.editedModel.set('name', vo.data[0].name);
                                                                me.editedModel = null;
                                                            }
                                                            me.disableEditForm();
                                                            OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                                                        }
                                                    } else {
                                                        OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                                    }
                                                },

                                                failure: function(response) {
                                                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                                }
                                            });
                                        }
                                    }
                                }),
                                Ext.create('Ext.button.Button', {
                                    text: 'Reset', width: 55,
                                    handler: function() {
                                        me.editGroup(Ext.create('OPF.console.directory.model.Group'));
                                    }
                                }),
                                Ext.create('Ext.button.Button', {
                                    text: 'Cancel', width: 60,
                                    handler: function() {
                                        me.parentDialog.hide();
                                    }
                                })
                            ]
                        ]
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },
    listeners: {
        activate: function() {
            this.searchGroups(null);
        }
    },

    disableEditForm: function() {
        this.inactivateField(this.groupNameField);
        this.availableUsersStore.loadData([]);
        this.assignedUsersStore.loadData([]);
    },

    searchGroups: function(searchTerm) {
        var url = OPF.Cfg.restUrl('/directory/ldap/group/search', true);
        url += '?directoryId=' + this.parentDialog.directoryId;
        if (OPF.isNotBlank(searchTerm)) {
            url += '&term=' + searchTerm;
        }
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success: function(response) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    me.deleteGroupButton.disable();
                    me.ldapGroupsStore.loadData(OPF.isEmpty(vo.data) || vo.data.length == 0 ? [] : vo.data);
                    me.disableEditForm();

                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                } else {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
            }
        });
    },

    inactivateField: function(field) {
        if (OPF.isNotEmpty(field)) {
            field.reset();
            field.disable();
        }
    },

    activateField: function(field, value) {
        if (OPF.isNotEmpty(field)) {
            field.setValue(OPF.isEmpty(value) ? '' : value);
            field.enable();
            field.setReadOnly(false);
        }
    },

    editGroup: function(model) {
        var isNew = this.editedModel == null;
        var me = this;
        me.ldapGroupsGridView.disable();
        if (isNew) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('/directory/ldap/user/search?directoryId=' + this.parentDialog.directoryId),
                method: 'GET',
                success: function(response) {
                    me.availableUsersStore.removeAll();
                    me.assignedUsersStore.removeAll();
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        me.activateField(me.groupNameField, model.get('name'));
                        me.availableUsersStore.loadData(OPF.isEmpty(vo.data) ? [] : vo.data);
                        OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                    } else {
                        OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                    }
                    me.ldapGroupsGridView.enable();
                },

                failure: function(response) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                }
            });
        } else {
            var url = OPF.Cfg.restUrl('/directory/ldap/group/load-with-users?directoryId=');
            url += me.parentDialog.directoryId + '&groupName=';
            var selectedGroup = me.ldapGroupsGridView.getSelectionModel().getLastSelected();
            url += selectedGroup.get('name');

            Ext.Ajax.request({
                url: url,
                method: 'GET',
                success: function(response) {
                    me.availableUsersStore.removeAll();
                    me.assignedUsersStore.removeAll();
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        if (OPF.isNotEmpty(vo.data) && vo.data.length > 0) {
                            var assignedUsers = OPF.isEmpty(vo.data[0].assignedUsers) ?
                                [] : vo.data[0].assignedUsers;
                            var availableUsers = OPF.isEmpty(vo.data[0].availableUsers) ?
                                [] : vo.data[0].availableUsers;
                            me.availableUsersStore.loadData(availableUsers);
                            me.assignedUsersStore.loadData(assignedUsers);
                        }
                        me.activateField(me.groupNameField, model.get('name'));
                        me.groupNameField.setReadOnly(true);
                    } else {
                        OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                    }
                    me.ldapGroupsGridView.enable();
                },

                failure: function(response) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                }
            });
        }
    }
});

Ext.define('OPF.console.directory.editor.ldap.ManagerDialog', {
    extend: 'Ext.window.Window',

    alias: 'widget.ldap-manager',

    title: 'LDAP Manager',
    layout: 'fit',
    width: 750,
    height: 580,
    directoryId: null,


    constructor: function(winId, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.ManagerDialog.superclass.constructor.call(this, Ext.apply({
            id: winId
        }, cfg));
    },

    initComponent: function() {

        this.usersManagerTab = Ext.create('OPF.console.directory.editor.ldap.UsersTab', this);
        this.groupsManagerTab = Ext.create('OPF.console.directory.editor.ldap.GroupsTab', this);
        this.groupsMappingTab = Ext.create('OPF.console.directory.editor.ldap.GroupsMappingTab', this);

        this.tabPanel = Ext.create('Ext.tab.Panel', {
            activeTab: 0,
            items: [
                this.usersManagerTab,
                this.groupsManagerTab,
                this.groupsMappingTab
            ]
        });

        this.items = this.tabPanel;

        this.callParent(arguments);
    },

    showDialog: function(directoryId) {
        this.directoryId = directoryId;
        this.show();
    }

});