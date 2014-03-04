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

Ext.define('OPF.console.forgotpassword.view.ForgotPasswordView', {
    extend: 'Ext.container.Container',
    alias : 'widget.forgot-password',
    cls: 'b-login',

    renderTo: 'content',

    initComponent: function() {
        var me = this;

//        this.message = Ext.ComponentMgr.create({
//            xtype: 'container',
//            autoEl: {
//                tag: 'h2',
//                html: 'Please enter email for reset password.'
//            }
//        });

        this.emailField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Email',
            cls: 'field email-field',
            name: 'email',
            width: 290
        });

        this.resetPasswordButton = new Ext.Button({
            text: 'Reset Password',
            ui: 'submit',
            cls: 'reset-password-button',
            handler: function() {
                me.resetPasswordForm.getEl().mask();
                me.resetPasswordForm.getForm().submit();
            }
        });

        this.resetPasswordForm = Ext.ComponentMgr.create({
            xtype: 'form',
            url: OPF.Cfg.fullUrl('reset-password'),
            monitorValid: true,
            standardSubmit: true,
            header: false,
            border: false,
//            hideLabels: true,
//            buttonAlign: 'right',
//            cls: 'login-form',
            items: [
                {
                    xtype: 'container',
                    cls: 'login-top',
                    html: '<h2>Please enter email for reset password</h2>'
                },
//                this.message,
                {
                    xtype: 'container',
                    cls: 'form-login',
                    items: [
                        this.emailField
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
                        this.resetPasswordButton
                    ]
                }
            ],
            listeners: {
                afterRender: function(form, options) {
                    this.keyNav = Ext.create('Ext.util.KeyNav', this.el, {
                        enter: function() {
                            me.resetPasswordForm.getEl().mask();
                            me.resetPasswordForm.getForm().submit();
                        },
                        scope: this
                    });
                }
            }
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
//                {
//                    xtype: 'container',
//                    height: 60
//                },
                {
                    xtype: 'container',
                    cls: 'main-container',
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
                                this.resetPasswordForm //,
/*                                {
                                    xtype: 'container',
                                    cls: 'not-member-yet',
                                    items: [
                                        {
                                            xtype: 'container',
                                            autoEl: {
                                                tag: 'hr'
                                            }
                                        },
                                        {
                                            xtype:  'container',
                                            contentEl: 'welcome-message'
                                        },
                                        this.getStartedButton
                                    ]
                                }*/
                            ]
                        }
                    ]
                },
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