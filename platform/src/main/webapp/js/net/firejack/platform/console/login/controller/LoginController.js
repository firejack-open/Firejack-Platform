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

Ext.define('OPF.console.login.controller.LoginController', {
    extend: 'Ext.app.Controller',

    views: ['LoginView' ],

    init: function() {
        this.initNavigation();

        this.control(
            {
                '#loginForm': {
                    afterrender: this.initForm
                },

                '#btnLogin': {
                    click: this.clickLoginButton
                },

                '#btnGoogleLogin': {
                    click: this.clickGoogleLoginButton
                },

                '#btnFacebookLogin': {
                    click: this.clickFacebookLoginButton
                },

                '#btnTwitterLogin': {
                    click: this.clickTwitterLoginButton
                },

                '#btnLinkedInLogin': {
                    click: this.clickLinkedInLoginButton
                },

                '#btnSignUp': {
                    click: this.clickSignUpButton
                },

                '#btnGettingStarted': {
                    click: this.clickGettingStartedButton
                },

                'sign-up-editor button[action=save]': {
                    click: this.saveNewUserButton
                },

                '#forgotPasswordLink': {
                    click: this.onForgotPasswordLink
                }
            }
        )
    },

    initForm: function(form, options) {
        var me = this;
        form.keyNav = Ext.create('Ext.util.KeyNav', form.el, {
            enter: function() {
                me.clickLoginButton(form);
            },
            scope: form
        });
    },

    initNavigation: function() {
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('site/navigation/tree-by-lookup/net.firejack.platform.top'),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var activeButton = null;
                var navigationElements = Ext.decode(response.responseText).data;
                Ext.each(navigationElements, function(navigationElement, index) {
                    OPF.NavigationMenuRegister.add(navigationElement);
                });
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    clickLoginButton: function(button) {
        var loginPanel = button.up('login');
        var form = loginPanel.loginForm;
        form.getEl().mask();
        var visiblePasswordField = loginPanel.visiblePasswordField;
        if (OPF.isNotBlank(OPF.Cfg.EXTRA_PARAMS.publicKey)) {
            var key = RSA.getPublicKey(OPF.Cfg.EXTRA_PARAMS.publicKey);
            var encryptedPassword = RSA.encrypt(visiblePasswordField.getValue(), key);
            loginPanel.passwordField.setValue(encryptedPassword);
            loginPanel.passwordEncryptedField.setValue('true');
        } else {
            loginPanel.passwordField.setValue(visiblePasswordField.getValue());
        }
        form.getForm().submit();
    },

    clickGoogleLoginButton: function(button) {
        var provider = new OPF.core.utils.OpenIdProvider("Google", "Google", "", "username", "/images/openid/google.jpg", 26, "https://www.google.com/accounts/o8/id", "", AUTHENTICATION_OPENID2);

        var loginPanel = button.up('login');
        var form = loginPanel.loginForm;

        form.getForm().url = OPF.Cfg.fullUrl('openid-authentication');

        loginPanel.usernameField.setValue(null);
        loginPanel.visiblePasswordField.setValue(null);
        loginPanel.passwordField.setValue(null);
        loginPanel.openIdField.setValue(provider.getUrl());

        form.getEl().mask();
        form.getForm().submit();
    },

    clickFacebookLoginButton: function() {
        document.location.href = OPF.Cfg.fullUrl('facebook-authentication');
    },

    clickTwitterLoginButton: function() {
        document.location.href = OPF.Cfg.fullUrl('twitter-authentication');
    },

    clickLinkedInLoginButton: function() {
        document.location.href = OPF.Cfg.fullUrl('linkedin-authentication');
    },

    clickSignUpButton: function() {
        var win = Ext.create('OPF.console.home.view.SignUp');
        win.show();

        var form = win.down('form');
        var constraints = new OPF.core.validation.FormInitialisation(OPF.core.utils.RegistryNodeType.USER.getConstraintName());
        constraints.initConstraints(form, null);
    },

    clickGettingStartedButton: function() {
        var navigationElement = OPF.NavigationMenuRegister.findByLookup('net.firejack.platform.top.documentation');
        if (isNotEmpty(navigationElement)) {
            document.location = OPF.Cfg.OPF_CONSOLE_URL + navigationElement.urlPath;
        }
    },

    onForgotPasswordLink: function() {
        document.location = OPF.Cfg.OPF_CONSOLE_URL + '/forgot-password';
    },

    saveNewUserButton: function(button) {
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