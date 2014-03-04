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