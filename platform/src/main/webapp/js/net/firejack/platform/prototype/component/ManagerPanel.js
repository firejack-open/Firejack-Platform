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

Ext.require([
    'OPF.prototype.component.AdvancedSearchPanel',
    'OPF.prototype.component.GridPanel',
    'OPF.prototype.component.FormPanel'
]);

Ext.define('OPF.prototype.component.ManagerPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.manager-panel',

    model: null,
    border: false,
    margin: '0 5 0 0',

    configs: {
        formPanel: null,
        gridPanel: null
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.Object.merge(this.configs, cfg.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.gridPanel = Ext.create('OPF.prototype.component.GridPanel', {
            managerPanel: this,
            model: this.model,
            configs: this.configs.gridPanel
        });

        this.formPanel = Ext.create('OPF.prototype.component.FormPanel', {
            managerPanel: this,
            model: this.model,
            hidden: true,
            configs: this.configs.formPanel
        });

        this.items = [
            this.gridPanel,
            this.formPanel
        ];

        this.callParent(arguments);

        var entityId = OPF.getQueryParam("entityId");
        if (OPF.isNotEmpty(entityId) && Ext.isNumeric(entityId)) {
            var model = Ext.create(this.model);
            Ext.Ajax.request({
                url: model.self.restSuffixUrl + '/' + entityId,
                method: 'GET',
                success: function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        var jsonData = vo.data[0];
                        var record = Ext.create(me.model, jsonData);
                        me.gridPanel.showEditor(record);
                    } else {
                        Ext.Msg.alert('Error', vo.message);
                    }
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
