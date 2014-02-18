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