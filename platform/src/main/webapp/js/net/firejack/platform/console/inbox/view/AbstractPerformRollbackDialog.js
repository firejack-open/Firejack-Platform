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
Ext.define('OPF.console.inbox.view.AbstractPerformRollbackDialog', {
    extend: 'Ext.window.Window',

    alias: 'widget.perform-rollback-dlg',

    title: '----ABSTRACT----',
    objectIdPropertyName: '----ABSTRACT----', // 'caseId'
    restUrl: '----ABSTRACT----',
    submitButtonLabel: '----ABSTRACT----', // 'Perform'

    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,

    width: 420,

    isPerformDialog: true,
    isCaseDialog: true,
    showUserField: true,

    constructor: function(id, grid, isPerformDialog, isCaseDialog, cfg) {
        isPerformDialog = OPF.isEmpty(isPerformDialog) ? true : isPerformDialog;
        isCaseDialog = OPF.isEmpty(isCaseDialog) ? true : isCaseDialog;
        cfg = cfg || {};
        OPF.console.inbox.view.AbstractPerformRollbackDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid,
            isPerformDialog : isPerformDialog,
            submitButtonLabel: isPerformDialog ? 'Perform' : 'Rollback',
            isCaseDialog: isCaseDialog
        }, cfg));
    },

    initComponent: function(){
        var instance = this;

        if (!this.isPerformDialog) {
            //for cases when multi-branch strategy is supported, user will see list of the all previous activities
            //and in case when multi-branch strategy is not supported, user will see only one previous strategy
            this.previousActivitiesField = OPF.Ui.comboFormField('activityId', 'Previous Activity', {
                store: Ext.create('OPF.console.inbox.store.PreviousActivities', this),
                labelWidth: 110,
                autoSelect: true,
                valueField: 'id',
                displayField:'name',
                editable: false,
                queryMode: 'local',
                emptyText:'Select an activity...',
                hiddenName: 'activityId'
            });
        }

        if (this.showUserField) {
            this.userField = OPF.Ui.comboFormField('assigneeId', 'Next Assignee', {
                store: Ext.create('OPF.console.inbox.store.NextPreviousAssignees', this),
                labelWidth: 110,
                autoSelect: true,
                valueField: 'id',
                displayField:'username',
                editable: false,
                queryMode: 'local',
                emptyText:'Select a user...',
                hiddenName: 'assigneeId'
            });
        }

        this.explanationsStore = Ext.create('OPF.console.inbox.store.Explanations', this);
        this.onExplanationsStoreLoad = function(store, records, successful, operation, eOpts) {
            if (records.length > 0) {
                instance.explanationField.allowBlank = false;
                if (instance.explanationField.isDisabled()) {
                    instance.explanationField.enable();
                }
            } else {
                instance.explanationField.allowBlank = true;
                instance.explanationField.disable();
            }
        };
        this.explanationsStore.addListener('load', this.onExplanationsStoreLoad, this);
        this.explanationField = OPF.Ui.comboFormField('explanationId', 'Explanation', {
            hidden: true,
            store: this.explanationsStore,
            labelWidth: 110,
            autoSelect: true,
            valueField: 'id',
            displayField:'shortDescription',
            editable: false,
            queryMode: 'local',
            emptyText:'Select an explanation...',
            hiddenName: 'explanationId'
        });

        this.noteTextField = OPF.Ui.textFormArea('noteText', 'Comment', {labelWidth: 110});
        this.objectIdField = OPF.Ui.hiddenField(this.objectIdPropertyName);

        this.performButton = Ext.create('Ext.button.Button', {
            text: this.submitButtonLabel,
            width: 70,
            handler: function(btn) {
                instance.performOrRollback();
            }
        });
        this.cancelButton = Ext.create('Ext.button.Button', {
            text: 'Cancel',
            width: 60,
            handler: function(btn) {
                instance.hide();
            }
        });

        formItems = [];
        if (this.previousActivitiesField) {
            formItems.push(this.previousActivitiesField);
        }
        if (this.showUserField) {
            formItems.push(this.userField);
        }
        formItems.push(
            this.explanationField,
            this.noteTextField,
            this.objectIdField
        );

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            padding: 10,
            border: false,
            headerAsText: false,
            monitorValid: true,
            frame: true,
            fbar: [
                this.performButton,
                this.cancelButton
            ],
            items: formItems
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    setIds: function(taskCaseId, processId) {
        this.objectIdField.setValue(taskCaseId);

        this.processId = processId;
        if (this.showUserField) {
            this.userField.store.reloadStore();
        }
        this.explanationField.store.reloadStore();
        if (this.previousActivitiesField) {
            this.previousActivitiesField.store.reloadStore()
        }
    },

    performOrRollback: function() {

        var instance = this;

        var formData = this.form.getForm().getValues();

        Ext.Ajax.request({
            url: this.restUrl,
            method: 'POST',
            jsonData: {"data": formData},

            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(resp)) {
                    Ext.Msg.alert(resp.success ? 'Success' : 'Error', resp.message);
                }

                var nextRowToSelectId = parseInt(instance.objectIdField.getValue()); // if perform / rollback case, the selected row should stay the same

                if (resp.success && OPF.isNotEmpty(resp.data) && resp.data.length > 0 && OPF.isNotEmpty(resp.data[0].id)) { // perform / rollback task returns the id of the next / previous task
                    nextRowToSelectId = resp.data[0].id;
                }
                instance.refreshCaller(nextRowToSelectId);
                instance.hide();
            },

            failure: function(response) {
                instance.hide();
            }
        });

    },

    refreshCaller: function(nextRowToSelectId) {
        this.grid.refreshGrids(nextRowToSelectId);
        this.grid.managerLayout.taskDetailsPanel.refreshPanelsForced();
    },

    cancel: function() {
        this.hide();
    }

});