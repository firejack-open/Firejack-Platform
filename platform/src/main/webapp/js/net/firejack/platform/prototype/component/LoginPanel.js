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