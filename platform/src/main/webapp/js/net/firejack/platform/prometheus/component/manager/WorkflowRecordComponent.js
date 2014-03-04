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
    'OPF.prometheus.component.manager.dialog.PerformActivityActionDialog'
]);

Ext.define('OPF.prometheus.component.manager.WorkflowRecordComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.workflow-record-component',

    border: true,
    cls: 'workflow-record-panel',

    formComponent: null,
    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.callParent(arguments);
    },

    loadWorkflowData: function(recordId) {
        var me = this;

        this.hide();
        this.removeAll();

        var modelLookup = this.formComponent.modelInstance.self.lookup;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/process/workflow/available/record/' + modelLookup + '/' + recordId, false),
            method: 'GET',

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                if (responseData.success) {
                    var processToolbars = [];
                    var processes = responseData.data;
                    Ext.each(processes, function(process) {
                        processToolbars.push({
                            xtype: 'toolbar',
                            dock: 'top',
                            ui: 'footer',
                            items: [
                                {
                                    xtype: 'tbtext',
                                    text: process.name
                                },
                                '->',
                                {
                                    xtype: 'button',
                                    cls: 'launch-process-btn',
                                    text: 'Launch',
                                    handler: function() {
                                        me.launchProcess(process.id, modelLookup, recordId);
                                    }
                                }
                            ]
                        });
                    });
                    if (processToolbars.length > 0) {
                        me.add({
                            xtype: 'container',
                            html: 'Exists Workflow For Launch'
                        });
                        me.add(processToolbars);
                        me.show();
                    }
                } else {
                    OPF.Msg.setAlert(false, responseData.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    launchProcess: function(processId, entityLookup, recordId) {
        var me = this;

        var url = OPF.Cfg.restUrl('/process/workflow/task/record', false);
        url = OPF.Cfg.addParameterToURL(url, 'recordId', recordId);
        url = OPF.Cfg.addParameterToURL(url, 'entityLookup', entityLookup);
        url = OPF.Cfg.addParameterToURL(url, 'processId', processId);

        Ext.Ajax.request({
            url: url,
            method: 'POST',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (resp.success) {
                    document.location.href = OPF.Cfg.fullUrl('inbox', true);
                }
            },

            failure:function(response) {
                me.formComponent.validator.showValidationMessages(response);
            }
        });
    }

});