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


Ext.define('OPF.console.directory.editor.OAuthInfoFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'OAuth Info',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.OAuthInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.consumerKeyField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Consumer Key',
            subFieldLabel: '',
            anchor: '100%',
            name: 'consumerKey',
            emptyText: 'Will be the same like system url',
            readOnly: true
        });

        this.consumerSecretField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            flex: 1,
            margin: '0 5 0 0',
            name: 'consumerSecret',
            emptyText: 'Will be generated automatically',
            readOnly: true
        });

        this.regenerateConsumerSecretButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text : 'Regenerate Consumer Secret',
            height: 28,
            handler: function() {
                var systemUserId = me.editPanel.userBasicInfoFieldSet.idField.getValue();
                var url = OPF.core.utils.RegistryNodeType.SYSTEM_USER.generateUrl('/generate_consumer_secret/' + systemUserId);
                Ext.Ajax.request({
                    url: url,
                    method: 'PUT',
                    jsonData: {},

                    success:function(response, action) {
                        var jsonData = Ext.decode(response.responseText);
                        var data = jsonData.data[0];
                        me.consumerSecretField.setValue(data.consumerSecret);
                    },

                    failure:function(response) {
                        OPF.Msg.setAlert(false, response.message);
                    }
                });
            }
        });

        this.items = [
            this.consumerKeyField,
            {
                xtype: 'label-container',
                fieldLabel: 'Consumer Secret',
                subFieldLabel: '',
                labelCls: '',
                cls: '',
                labelMargin: '0 0 5 0',
                layout: 'hbox',
                items: [
                    this.consumerSecretField,
                    OPF.Ui.xSpacer(10),
                    this.regenerateConsumerSecretButton
                ]
            }
        ];

        this.callParent(arguments);
    }
});