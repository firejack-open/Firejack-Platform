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

Ext.define('OPF.console.license.LicenseWindow', {
    extend: 'Ext.window.Window',

    title: 'License',
    id: 'licenseWindow',
    width: 300,
    height: 140,
    layout: {
        type: 'vbox',
        align: 'stretch',
        padding: 5
    },
    modal: true,

    initComponent: function() {
        var me = this;

        this.errorContainer = Ext.ComponentManager.create({
            xtype: 'container',
            height: 72,
            style: 'color: #CC0404;',
            hidden: true
        });

        this.nameField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'License Name',
            labelWidth: 120
        });

        this.sessionField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Sessions',
            labelWidth: 120
        });

        this.expiredField = Ext.ComponentManager.create({
            xtype: 'opf-displayfield',
            fieldLabel: 'Expired',
            labelWidth: 120
        });

        this.uploadLicenseDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['xml'],
            uploadUrl: OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/license'),

            successUploaded: function(jsonData, action) {
                me.loadLicenseData();
            },

            failureUploaded: function(action) {
                var response = action.response;
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, resp.message);

                me.uploadLicenseDialog.hide();
                me.showLicenseData(false);
                me.errorContainer.update(resp.message);
            }
        });

        this.items = [
            this.errorContainer,
            this.nameField,
            this.sessionField,
            this.expiredField,
            {
                xtype: 'button',
                text: 'Change License',
                handler: function() {
                    me.uploadLicenseDialog.show();
                }
            }
        ];

        this.callParent(arguments);

        this.loadLicenseData();
    },

    loadLicenseData: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/registry/license'),
            method: 'GET',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, resp.message);
                me.showLicenseData(true);
                if (resp.data && resp.data.length > 0 && resp.data[0]) {
                    var data = resp.data[0];
                    me.nameField.setValue(data.name);
                    me.sessionField.setValue(data.session ? data.session : 'unlimited');

                    var date = new Date();
                    date.setTime(data.expired);
                    var colorStyle = (new Date().valueOf() > date.valueOf()) ? 'color: red' : 'color: black;';
                    me.expiredField.setFieldStyle( colorStyle );
                    me.expiredField.setValue(Ext.Date.format(date, 'Y-m-d G:i:s'));
                } else {
                    me.nameField.setValue('Trial Version');
                    me.sessionField.setValue('3');
                    me.expiredField.setValue('none');
                }
            },

            failure: function(response) {
                me.nameField.setValue('Trial Version');
                me.sessionField.setValue('3');
                me.expiredField.setValue('none');
            }
        });

    },

    showLicenseData: function(isShow) {
        this.nameField.setVisible(isShow);
        this.sessionField.setVisible(isShow);
        this.expiredField.setVisible(isShow);
        this.errorContainer.setVisible(!isShow);
    }

});
