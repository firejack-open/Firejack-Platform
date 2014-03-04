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

//Temporary class that are used for testing ldap authentication.
Ext.define('OPF.console.directory.editor.ldap.AuthenticationChecker', {
    extend: 'Ext.window.Window',

    alias: 'widget.ldap-auth-checker',

    title: 'Login',
    layout: 'fit',
    width: 300,
    height: 200,
    directoryId: null,

    schemaField: null,

    constructor: function(winId, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.AuthenticationChecker.superclass.constructor.call(this, Ext.apply({
            id: winId
        }, cfg));
    },

    initComponent: function() {
        var me = this;
        this.usernameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Username',
            subFieldLabel: '',
            width: 200,
            name: 'username'
        });

        this.passwordField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: '',
            width: 200,
            name: 'password',
            inputType: 'password'
        });

        this.form = Ext.create('Ext.form.Panel', {
            items: [
                this.usernameField,
                this.passwordField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        [
                            '->',
                            {
                                xtype: 'button',
                                text: 'login',
                                handler: function() {
                                    var data = me.form.getForm().getValues();
                                    var username = data.username;
                                    var password = data.password;
                                    Ext.Ajax.request({
                                        url: OPF.Cfg.restUrl('/directory/ldap/user/sign-in?directoryId=') +
                                            me.directoryId + '&username=' + username + '&password=' + password,
                                        method: 'GET',
                                        success: function(response) {
                                            var vo = Ext.decode(response.responseText);
                                            if (vo.success) {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                                            } else {
                                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                                            }
                                        },

                                        failure: function(response) {
                                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                                        }
                                    })
                                }
                            }
                        ]
                    ]
                }
            ]
        });
        this.items = this.form;

        this.callParent(arguments);
    },

    showDialog: function(directoryId) {
        this.directoryId = directoryId;
        this.show();
    }

});