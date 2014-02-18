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