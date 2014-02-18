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