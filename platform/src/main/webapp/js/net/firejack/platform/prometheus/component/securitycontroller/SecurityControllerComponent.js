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

Ext.define('OPF.prometheus.component.securitycontroller.SecurityControllerComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.security-controller-component',

    //cls: 'title-panel',
    //margin: '0 0 10 0',

    currentModelLookup: null,
    model: null,
    managerPanel: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;
        this.turnOnInstantSecurity = Ext.create('Ext.form.field.Checkbox', {
            name: 'allowSecurity',
            cls: 'opf-check-box',
            fieldLabel: 'Enable Sharing',
            labelAlign: 'right',
            labelWidth: 150,
            listeners: {
                change: function(chbox, newVal, oldVal) {
                    var securityEnabled = newVal == 'true' || newVal == 'on' || newVal == '1' || newVal;
                    Ext.Ajax.request({
                        url: OPF.Cfg.restUrl('/authority/role/enable-instant-security-on-package?entityLookup=' +
                            me.currentModelLookup + '&securityEnabled=' + securityEnabled),
                        method: 'PUT',
                        success: function(response, action) {
                            var resp = Ext.decode(response.responseText);
                            Ext.Msg.alert(resp.success ? 'Info' : 'Error', resp.message);
                            me.managerPanel.securityEnabled = securityEnabled;
                        },

                        failure: function(response) {
                            var responseStatus = Ext.decode(response.responseText);
                            var messages = [];
                            for (var i = 0; i < responseStatus.data.length; i++) {
                                var msg = responseStatus.data[i].msg;
                                messages.push(msg);
                            }
                            Ext.Msg.alert('Error', messages.join('<br/>'));
                        }
                    });
                }
            }
        });
        this.items = [
            this.turnOnInstantSecurity
        ];

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(cnt) {
            var modelInst = Ext.create(this.model);
            var me = this;
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl("registry/entity/security-enabled-by-lookup?entityLookup=" + modelInst.self.lookup),
                method: 'GET',
                success: function(response) {
                    var json = Ext.decode(response.responseText);
                    var securityEnabled = json.success && json.data.length > 0 && json.data[0].identifier;
                    cnt.turnOnInstantSecurity.suspendEvents();
                    cnt.turnOnInstantSecurity.setValue(securityEnabled);
                    cnt.turnOnInstantSecurity.resumeEvents();
                    me.managerPanel.securityEnabled = securityEnabled;
                },
                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
            var isPackageAdmin = OPF.Cfg.USER_INFO.isPackageAdmin;
            if (isPackageAdmin) {
                cnt.show();
            } else {
                cnt.hide();
            }
        }
    }

});