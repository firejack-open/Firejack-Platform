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

/**
 *
 */
Ext.define('OPF.console.inbox.view.processcase.StartCaseDialog', {
    extend: 'Ext.window.Window',

    title: 'Start Case',
    modal: true,
    closable: true,
    closeAction: 'hide',
    width: 400,

    constructor: function(id, cfg) {
        cfg = cfg || {};

        OPF.console.inbox.view.processcase.StartCaseDialog.superclass.constructor.call(this, Ext.apply({
            id: id
        }, cfg));
    },

    initComponent: function() {
        this.processCombo = OPF.Ui.comboFormField("processLookup", "Process", {
            store: Ext.create('Ext.data.Store', {
                model: 'OPF.console.domain.model.ProcessModel',
                proxy: {
                    type: 'ajax',
                    url: OPF.core.utils.RegistryNodeType.PROCESS.generateUrl('/search/?humanProcess=true'),
                    method: 'GET',
                    reader: {
                        type: 'json',
                        successProperty: 'success',
                        idProperty: 'id',
                        root: 'data',
                        messageProperty: 'message',
                        totalProperty: 'total'
                    }
                }
            }),
            autoSelect: true,
            valueField: 'lookup',
            displayField:'lookup',
            editable: false,
            emptyText:'Select a process...',
            queryMode: 'remote',
            hiddenName: 'processLookup'
        });
        this.assigneeCombo = OPF.Ui.comboFormField('assigneeId', 'Assignee', {
            store: 'Users',
            autoSelect: true,
            valueField: 'id',
            displayField:'username',
            editable: false,
            emptyText:'Select a user...',
            queryMode: 'remote',
            hiddenName: 'assigneeId'
        });
        this.descriptionField = OPF.Ui.textFormArea("caseDescription", "Description");

        var instance = this;
        this.startBtn = OPF.Ui.createBtn("Start", 50, "process-start", {
            handler: function() {
                var caseOperationsParams = instance.form.getForm().getValues();
                Ext.Ajax.request({
                    url: OPF.core.utils.RegistryNodeType.CASE.generateUrl('/start'),
                    method: 'POST',
                    jsonData: {"data": caseOperationsParams},

                    success: function(response, action) {
                        var resp = Ext.decode(response.responseText);
                        if (OPF.isNotEmpty(resp)) {
                            Ext.Msg.alert(resp.success ? 'Success' : 'Error', resp.message);
                            var taskGridPanel = OPF.Ui.getCmp('process-manager-view').taskCasePanel.getLayout().getActiveItem();
                            taskGridPanel.refreshGrids(null);
                        }
                        instance.hide();
                    },

                    failure: function(response) {
                        instance.hide();
                    }
                });
            }
        });
        this.cancelBtn = OPF.Ui.createBtn("Cancel", 60, "cancel-process-start-dlg", {
            handler: function() {
                instance.hide();
            }
        });
        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            border: false,
            headerAsText: false,
            monitorValid: true,
            frame: true,
            items: [
                this.processCombo,
                this.assigneeCombo,
                this.descriptionField
            ],
            fbar: [
                this.startBtn,
                this.cancelBtn
            ]
        });
        this.items = this.form;

        this.callParent(arguments);
    },

    resetFields: function() {
        this.processCombo.reset();
        this.assigneeCombo.reset();
        this.descriptionField.setValue('');
    },

    listeners: {
        beforeshow: function(win, eOpts) {
            win.resetFields();
        }
    }
});