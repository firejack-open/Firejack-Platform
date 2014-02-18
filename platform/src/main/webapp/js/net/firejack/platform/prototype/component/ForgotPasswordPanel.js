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

Ext.define('OPF.prototype.component.ForgotPasswordPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.forgot-password-panel',

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },

    border: false,

    titleLookup: null,
    messageLookup: null,

    panelWidth: 350,
    panelHeight: 320,

    submitUrl: null,

    initComponent: function() {
        var me = this;

        this.emailField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'email',
            labelAlign: 'top',
            cls: 'opf-text-field',
            fieldLabel: 'Email',
            subFieldLabel: 'Enter your username or e-mail address'
        });

        this.resetPasswordButton = new Ext.Button({
            text: "Reset Password",
            cls: 'login-button',
            width: 50,
            height: 30,
            handler: function() {
                me.resetPasswordForm.getEl().mask();
                me.resetPasswordForm.getForm().submit();
            }
        });

        this.resetPasswordForm = Ext.ComponentMgr.create({
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
                this.emailField
            ],
            fbar: [
                this.resetPasswordButton
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
                        textInnerCls: 'content-panel-title-text'
                    },
                    {
                        xtype: 'splitter'
                    },
                    {
                        xtype: 'text-resource-control',
                        textResourceLookup: this.messageLookup,
                        cls: 'content-panel-content',
                        textInnerCls: 'content-panel-content-text'
                    },
                    this.resetPasswordForm
                ]
            }
        ];

        this.callParent(arguments);
    }

});