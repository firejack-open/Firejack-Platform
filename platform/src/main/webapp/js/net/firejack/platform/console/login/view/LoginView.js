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

Ext.define('OPF.console.login.view.LoginView', {
    extend: 'Ext.container.Container',
    alias : 'widget.login',
    cls: 'b-login',

    renderTo: 'content',

    initComponent: function() {
        var me = this;

//        this.loginMessage = Ext.ComponentMgr.create({
//            xtype: 'container',
//            autoEl: {
//                tag: 'p',
//                html: 'Welcome to the Firejack Platform. Please login.'
//            }
//        });

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
            fieldLabel: 'Login',
            labelAlign: 'top',
//            labelWidth: 90,
//            anchor: '100%',
//            cls: 'field login-field',
            name: 'login',
            value: ifBlank(OPF.Cfg.DEFAULT_LOGIN, ''),
            width: 290,
            tabIndex: 1
        });

        this.passwordEncryptedField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'encrypted',
            value: 'false'
        });

        this.visiblePasswordField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            fieldLabel: 'Password',
            labelAlign: 'top',
//            labelWidth: 90,
//            anchor: '100%',
//            cls: 'field password-field',
            name: 'visiblePassword',
            inputType: 'password',
            submitValue: false,
            value: ifBlank(OPF.Cfg.DEFAULT_PASSWORD, ''),
            width: 290,
            tabIndex: 2
        });

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'password',
            value: ifBlank(OPF.Cfg.DEFAULT_PASSWORD, '')
        });

        this.openIdField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'openid_identifier'
        });

        this.loginButton = new Ext.Button({
            id: 'btnLogin',
            text: "Log In",
            ui: 'submit',
            height: 30
        });

        this.forgotPasswordLink = Ext.create('OPF.core.component.LinkButton', {
            id: 'forgotPasswordLink',
//            cls: 'forgot-password',
            text: 'Forgot password'
        });

        this.googleLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnGoogleLogin',
            ui: 'social',
            iconCls: 'google-login-icon',
            width: 32,
            height: 32
        });

        var isFacebookLoginEnable = OPF.Cfg.EXTRA_PARAMS.facebookLoginEnable == 'true';
        this.facebookLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnFacebookLogin',
            ui: 'social',
            iconCls: 'facebook-login-icon',
            width: 32,
            height: 32,
            hidden: !isFacebookLoginEnable
        });

        var isTwitterLoginEnable = OPF.Cfg.EXTRA_PARAMS.twitterLoginEnable == 'true';
        this.twitterLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnTwitterLogin',
            ui: 'social',
            iconCls: 'twitter-login-icon',
            width: 32,
            height: 32,
            hidden: !isTwitterLoginEnable
        });

        var isLinkedinLoginEnable = OPF.Cfg.EXTRA_PARAMS.linkedinLoginEnable == 'true';
        this.linkedinLoginButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnLinkedInLogin',
            ui: 'social',
            iconCls: 'linkedin-login-icon',
            width: 32,
            height: 32,
            hidden: !isLinkedinLoginEnable
        });

        this.redirectUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: 'redirect',
            value: OPF.getQueryParam('redirect')
        });

        this.loginForm = Ext.ComponentMgr.create({
            xtype: 'form',
            id: 'loginForm',
            url: OPF.Cfg.fullUrl('authentication'),
            monitorValid: true,
            standardSubmit: true, // do not use Ajax submit (default ExtJs)
            header: false,
            border: false,
            items: [
                {
                    xtype: 'container',
                    cls: 'form-login',
                    items: [
                        this.filterMessage,
                        {
                            xtype: 'container',
                            items: [
                                {
                                    xtype: 'container',
                                    cls: 'form-leftside',
                                    items: [
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.usernameField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                //this.rememberMeField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.passwordEncryptedField,
                                                this.visiblePasswordField,
                                                this.passwordField,
                                                this.openIdField
                                            ]
                                        },
                                        {
                                            xtype: 'container',
                                            items: [
                                                this.forgotPasswordLink
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'container',
                                    cls: 'form-rightside',
                                    items: [
                                        {
                                            xtype: 'container',
                                            cls: 'login-using',
                                            hidden: !isFacebookLoginEnable &&
                                                    !isTwitterLoginEnable &&
                                                    !isLinkedinLoginEnable,
                                            items: [
                                                {
                                                    xtype: 'container',
                                                    html: '<h3>or Login using social networks:</h3>'
                                                },
//                                                this.googleLoginButton,
                                                this.facebookLoginButton,
                                                this.twitterLoginButton,
                                                this.linkedinLoginButton
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },
                        this.passwordEncryptedField,
                        this.passwordField,
                        this.redirectUrlField
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    cls: 'buttons',
                    items: [
                        '->',
                        this.loginButton
                    ]
                }
            ]
        });

        this.signupButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnSignUp',
            text: "Sign Up",
            cls: 'signup-button'
        });

        this.getStartedButton = Ext.ComponentMgr.create({
            xtype: 'button',
            id: 'btnGettingStarted',
            cls: 'getstarted',
            iconCls: 'getstarted-icon',
            scale: 'large',
            text: 'Get Started'
        });

        this.items = {
            xtype: 'container',
            cls: 'wrapper',
            items: [
                {
                    xtype: 'container',
                    items: [
//                        {
//                            xtype: 'container',
//                            cls: 'logo',
//                            items: [
//                                {
//                                    xtype: 'component',
//                                    autoEl: {
//                                        tag: 'img',
//                                        src: OPF.Cfg.fullUrl('/images/login/logo.png')
//                                    }
//                                }
//                            ]
//                        },
                        {
                            xtype: 'container',
                            cls: 'login-body',
                            items: [
                                {
                                    xtype: 'container',
                                    cls: 'login-top',
                                    html: '<h2>Login</h2>'
                                },
                                this.filterMessage,
                                this.loginForm
                                //,
//                                {
//                                    xtype: 'container',
//                                    cls: 'not-member-yet',
//                                    items: [
//                                        {
//                                            xtype: 'container',
//                                            autoEl: {
//                                                tag: 'p',
//                                                html: 'Not a member yet?'
//                                            }
//                                        },
//                                        this.signupButton,
//                                        {
//                                            xtype: 'container',
//                                            autoEl: {
//                                                tag: 'hr'
//                                            }
//                                        },
//                                        {
//                                            xtype:  'container',
//                                            contentEl: 'welcome-message'
//                                        },
//                                        this.getStartedButton
//                                    ]
//                                }
                            ]
                        }
                    ]
                } //,
//                {
//                    xtype: 'container',
//                    cls: 'firejack',
//                    items: [
//                        {
//                            xtype: 'component',
//                            autoEl: {
//                                tag: 'img',
//                                src: OPF.Cfg.fullUrl('/images/login/firejack.png')
//                            }
//                        }
//                    ]
//                }
            ]
        };

        this.callParent(arguments);
    }

});
