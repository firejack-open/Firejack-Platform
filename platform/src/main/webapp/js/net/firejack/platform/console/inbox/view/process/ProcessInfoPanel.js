/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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