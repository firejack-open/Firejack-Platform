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

Ext.define('OPF.console.home.controller.Home', {
    extend: 'Ext.app.Controller',

    stores: [ 'Dashboards' ],

    models: [ 'Dashboard', 'UserProfile' ],

    views: ['Home', 'Dashboard', 'Profile'],

    init: function() {
        this.control(
            {
                '#pnlNewUser': {
                    beforerender: this.renderNewUserButton
                },

                '#pnlSignUp': {
                    beforerender: this.renderSignUpButton
                },

                '#pnlWelcome': {
                    beforerender: this.renderWelcomePanel
                },

                '#btnNewUser': {
                    click: this.clickSignUpButton
                },

                'home-dashboard': {
                    beforerender: this.renderDashboardPanel,
                    render: this.initDashboardPanel
                },

                'home-profile': {
                    beforerender: this.renderProfilePanel,
                    render: this.initProfilePanel
                },

                'home-login': {
                    beforerender: this.renderLoginPanel
                },

                'sign-up-editor button[action=save]': {
                    click: this.saveNewUserButton
                }
            }
        )
    },

    renderNewUserButton: function() {
        return OPF.Cfg.USER_INFO.isLogged && showUserCreateButton;
    },

    renderSignUpButton: function() {
        return !OPF.Cfg.USER_INFO.isLogged;
    },

    renderProfilePanel: function() {
        return OPF.Cfg.USER_INFO.isLogged;
    },

    renderLoginPanel: function() {
        return !OPF.Cfg.USER_INFO.isLogged;
    },

    renderDashboardPanel:function() {
        return OPF.Cfg.USER_INFO.isLogged;
    },

    renderWelcomePanel: function() {
        return !OPF.Cfg.USER_INFO.isLogged;
    },

    initDashboardPanel: function(panel) {
        this.dashboardPanel = panel;
        this.dashboardPanel.store.load();
    },

    initProfilePanel: function(panel) {
        OPF.console.home.model.UserProfile.load(1, {
            scope: this,
            failure: function(record, operation) {
                Ext.Msg.alert('Failure: ' + record);
            },
            success: function(record, operation) {
                var roleNames = [];
                Ext.each(record.raw.userRoles, function(userRole) {
                    var role = userRole.role;
                    roleNames.push(role.name);
                });
                var tpl = new Ext.Template([
                    '<p><b>Username: </b>{username}</p>',
                    '<p><b>Name: </b>{firstName} {lastName}</p>',
                    '<p><b>Email: </b>{email}</p>',
                    '<p><b>Created: </b>{created:date("F d, Y h:i A")}</p>',
                    '<p><b>Roles: </b>' + roleNames.join(', ') + '</p>'
                ]);
                tpl.compile();
                record.data.created = new Date(record.data.created);
                var data = tpl.applyTemplate(record.data);
                panel.add(
                    {
                        xtype: 'container',
                        html: data
                    }
                );
                panel.doLayout();
            }
        });
    },

    clickSignUpButton: function() {
        var win = Ext.create('OPF.console.home.view.SignUp');
        win.show();

        var form = win.down('form');
        var constraints = new OPF.core.validation.FormInitialisation(OPF.core.utils.RegistryNodeType.USER.getConstraintName());
        constraints.initConstraints(form, null);
    },

    saveNewUserButton: function(button) {
        var me = this;
        var win = button.up('window');
        var form = win.down('form');
        var values = form.getValues();

        var user = Ext.create('OPF.console.home.model.SignUp', values);

        form.getEl().mask();

        user.save({
            success: function(record, operation) {
                form.getEl().unmask();
                form.getForm().reset();
                win.close();
                me.dashboardPanel.store.load();
            },
            failure: function(record, operation) {
                var options = new OPF.core.validation.FormInitialisationOptions({
                    show: true,
                    asPopup: true
                });
                OPF.core.validation.FormInitialisation.showValidationMessages(operation.response, form, options);
                form.getEl().unmask();
            }
        });
    }

});