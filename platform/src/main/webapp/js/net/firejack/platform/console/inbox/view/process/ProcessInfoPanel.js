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

Ext.define('OPF.console.inbox.view.process.ProcessInfoPanel', {
    extend: 'OPF.console.inbox.view.DetailsCardPanel',

    title: 'Details',
    layout: 'fit',
    fieldLabel: 'Case Info',
    id: 'case-info-panel',

    initComponent: function() {
        this.processField = OPF.Ui.displayField('processName', 'Process', {labelPad: 30});
        this.descriptionField = OPF.Ui.displayField('description', 'Description', {labelPad: 30});
        this.assigneeField = OPF.Ui.displayField('assignee', 'Assignee', {labelPad: 30});
        this.activeField = OPF.Ui.displayField('active', 'Active', {labelPad: 30});
        this.statusField = OPF.Ui.displayField('status', 'Status', {labelPad: 30});
        this.startDateField = OPF.Ui.displayField('startDate', 'Started', {labelPad: 12});
        this.updateDateField = OPF.Ui.displayField('updateDate', 'Updated', {labelPad: 12});
        this.completeDateField = OPF.Ui.displayField('completeDate', 'Closed', {labelPad: 12});

        this.innerForm = Ext.create('Ext.form.Panel', {
            padding: 10,
            border: false,
            flex: 1,
            layout:'column',
            defaults: {      // defaults applied to items
                border: 0,
                bodyStyle: 'padding:4px'
            },
            items: [{
                xtype:'fieldset',
                columnWidth: 0.5,
                frame: false,
                style: {
                    border: 0,
                    padding: 0
                },
                items: [
                    this.processField,
                    this.descriptionField,
                    this.assigneeField,
                    this.activeField,
                    this.statusField
                ]
            }, {
                xtype:'fieldset',
                columnWidth: 0.5,
                frame: false,
                style: {
                    border: 0,
                    padding: 0
                },
                items: [
                    this.startDateField,
                    this.updateDateField,
                    this.completeDateField
                ]
            }]
        });

        this.items = [
            this.innerForm
        ];

        this.callParent(arguments);
    },

    updateProcessDetails: function(caseId) {
        var instance = this;

        if (OPF.isEmpty(caseId)) {
            instance.innerForm.getForm().reset();
            return;
        }

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.CASE.generateGetUrl(caseId),
            method: 'GET',
            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                var processCase = resp.data[0];
                instance.innerForm.getForm().setValues(processCase);
                instance.startDateField.setValue(sqFormatDateInMillis(processCase.startDate));
                instance.updateDateField.setValue(sqFormatDateInMillis(processCase.updateDate));
                instance.completeDateField.setValue(sqFormatDateInMillis(processCase.completeDate));
                instance.processField.setValue(processCase.process.name);
                instance.assigneeField.setValue(processCase.assignee ? processCase.assignee.username : ''); // could be assigned to no one (null)
                instance.statusField.setValue(processCase.status.name);
            }
        });
    },

    refreshPanelData: function(parentPanel) {
        this.updateProcessDetails(parentPanel.caseId);
    }

});