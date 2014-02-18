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

Ext.define('OPF.prototype.component.LoginPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.login-panel',

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },

    border: false,

    titleLookup: null,
    messageLookup: null,

    panelWidth: 350,
//    panelHeight: 320,

    submitUrl: null,
    forgotPasswordPageUrl: null,

    initComponent: function() {
        var me = this;

        var messageCount = 0;
        var messages = '';
        Ext.each(OPF.Cfg.EXTRA_PARAMS.filterMessages, function(filterMessage) {
            messages = '<span class="' + filterMessage.type.toLowerCase() + '">' + filterMessage.message + '</span>';
            messageCount++;
        });
        this.filterMessage = Ext.ComponentMgr.create({
            xtype: 'container',
            cls: 'filter-message',
            html: messages,
            hidden: messageCount == 0
        });

        this.usernameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'username',
            labelAlign: 'top',
            cls: 'opf-text-field',
            fieldLabel: 'Username',
            subFieldLabel: 'Enter your username or e-mail address'
        });

        this.passwordEncryptedField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'encrypted',
            value: 'false'
        });

        this.visiblePasswordField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'visiblePassword',
            inputType: 'password',
            labelAlign: 'top',
            cls: 'opf-text-field',
            submitValue: false,
            fieldLabel: 'Password',
            subFieldLabel: 'Enter your password'
        });

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'password'
        });

        this.forgotUsernameButton = new Ext.Button({
            text: "Forgot Username",
            cls: 'forgot-username-button',
            width: 50,
            height: 30,
            hidden: true
        });

        this.forgotPasswordButton = new Ext.Button({
            text: "Forgot Password",
            cls: 'forgot-password-button',
            width: 120,
            height: 30,
            handler: function() {
                if (OPF.isNotBlank(me.forgotPasswordPageUrl)) {
                    document.location = OPF.Cfg.fullUrl(me.forgotPasswordPageUrl, true);
                }
            }
        });

        this.loginButton = new Ext.Button({
            text: "Login",
            cls: 'login-button',
            width: 80,
            height: 30,
            handler: function() {
                me.submit();
            }
        });

        this.facebookLoginButton = new Ext.Button({
            text: "Facebook",
            cls: 'facebook-login-button',
            width: 80,
            height: 30,
            handler: function() {
                document.location.href = OPF.Cfg.fullUrl('facebook-authentication', true);
            }
        });

        this.loginForm = Ext.ComponentMgr.create({
            xtype: 'form',
            url: OPF.Cfg.fullUrl(this.submitUrl, true),
            monitorValid: true,
            standardSubmit: true,
            header: false,
            border: false,
            hideLabels: true,
            buttonAlign: 'center',
            layout: 'anchor',
            bodyPadding: 10,
            items: [
                this.filterMessage,
                this.usernameField,
                this.passwordEncryptedField,
                this.visiblePasswordField,
                this.passwordField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
//                        this.forgotUsernameButton,
                        this.forgotPasswordButton,
                        this.facebookLoginButton,
                        this.loginButton,
                        '->'
                    ]
                }
            ],
            listeners: {
                afterRender: function(form, options) {
                    this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                        enter: function() {
                            me.submit();
                        },
                        scope: this
                    });
                }
            }
        });

        this.items = [
            {
                xtype: 'panel',
                cls: 'content-panel form-panel login-panel',
                width: this.panelWidth,
                height: this.panelHeight,
                items: [
                    {
                        xtype: 'text-resource-control',
                        textResourceLookup: this.titleLookup,
                        cls: 'content-panel-title',
                        textInnerCls: 'content-panel-title-text',
                        autoInit: true
                    },
                    {
                        xtype: 'splitter'
                    },
                    {
                        xtype: 'text-resource-control',
                        textResourceLookup: this.messageLookup,
                        cls: 'content-panel-content',
                        textInnerCls: 'content-panel-content-text',
                        autoInit: true
                    },
                    this.loginForm
                ]
            }
        ];

        this.callParent(arguments);
    },

    submit: function() {
        this.loginForm.getEl().mask();
        if (OPF.isNotBlank(OPF.Cfg.EXTRA_PARAMS.publicKey)) {
            var key = RSA.getPublicKey(OPF.Cfg.EXTRA_PARAMS.publicKey);
            var encryptedPassword = RSA.encrypt(this.visiblePasswordField.getValue(), key);
            this.passwordField.setValue(encryptedPassword);
            this.passwordEncryptedField.setValue('true');
        } else {
            this.passwordField.setValue(this.visiblePasswordField.getValue());
        }
        this.loginForm.getForm().submit();
    }

});