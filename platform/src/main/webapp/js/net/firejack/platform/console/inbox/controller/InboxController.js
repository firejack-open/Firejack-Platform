/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.define('OPF.console.inbox.controller.InboxController', {
    extend: 'Ext.app.Controller',

    views: ['processcase.StartCaseDialog'],

    stores: [
        'Assignees',  'Users', 'CaseNotes',
        'CaseAttachments', 'CaseActions', 'MyProcesses',
        'ProcessActivities', 'ProcessStatuses'
    ],

    models: [],

    init: function() {
        this.getAssigneesStore().addListener('beforeload', this.onBeforeAssigneesStoreLoad, this);
        this.getAssigneesStore().proxy.addListener('exception', this.onProxyException, this);
        this.getCaseNotesStore().addListener('beforeload', this.onBeforeCaseNotesStoreLoad, this);
        this.getCaseNotesStore().proxy.addListener('exception', this.onProxyException, this);
        this.getCaseAttachmentsStore().addListener('beforeload', this.onBeforeCaseAttachmentsStoreLoad, this);
        this.getCaseAttachmentsStore().proxy.addListener('exception', this.onProxyException, this);
        this.getCaseActionsStore().addListener('beforeload', this.onBeforeCaseActionsStoreLoad, this);
        this.getCaseActionsStore().proxy.addListener('exception', this.onProxyException, this);
        this.getProcessActivitiesStore().addListener('beforeload', this.onBeforeProcessActivitiesStoreLoad, this);
        this.getProcessActivitiesStore().proxy.addListener('exception', this.onProxyException, this);
        this.getProcessStatusesStore().addListener('beforeload', this.onBeforeProcessStatusesStoreLoad, this);
        this.getProcessStatusesStore().proxy.addListener('exception', this.onProxyException, this);
        this.getMyProcessesStore().addListener('beforeload', this.onBeforeMyProcessesStoreLoad, this);
        this.getMyProcessesStore().proxy.addListener('exception', this.onProxyException, this),
        this.control(
            {
                'assign-task-dlg button[action=assign-task]': {
                    click: this.onAssignTaskBtnClick
                },
                'assign-task-dlg button[action=cancel]': {
                    click: this.onCancelAssignTaskBtnClick
                },
                'assign-team-dlg': {
                    beforeshow: this.onAssignTeamBeforeShow
                },
                'assign-team-dlg button[action=assign-team]': {
                    click: this.onAssignTeamBtnClick
                },
                'assign-team-dlg button[action=cancel]': {
                    click: this.onCancelAssignTeamBtnClick
                },
                'notes-panel grid': {
                    itemclick: this.onNotesPanelItemClick
                },
                'notes-panel button[action=add-note]': {
                    click: this.onAddNoteBtnClick
                },
                'notes-panel button[action=delete-note]': {
                    click: this.onDeleteNoteBtnClick
                },
                'notes-panel button[action=save-note]': {
                    click: this.onSaveNoteBtnClick
                },
                'notes-panel button[action=cancel]': {
                    click: this.onCancelNoteBtnClick
                },
                'perform-rollback-dlg': {
                    beforeshow: this.beforePerformRollbackDialogShow
                },
                'case-attachments button[action=upload]': {
                    click: this.uploadAttachmentBtnClick
                },
                'case-attachments button[action=delete]': {
                    click: this.deleteAttachmentBtnClick
                },
                'taskcase-grid button[action=case-mode]': {
                    click: this.taskCaseCaseModeBtnClick
                },
                'taskcase-grid button[action=task-mode]': {
                    click: this.taskCaseTaskModeBtnClick
                },
                'taskcase-grid button[action=assign-team]': {
                    click: this.taskCaseAssignTeamBtnClick
                },
                'taskcase-grid button[action=claim]': {
                    click: this.taskCaseClaimBtnClick
                },
                'taskcase-grid button[action=assign]': {
                    click: this.taskCaseAssignBtnClick
                },
                'taskcase-grid button[action=perform]': {
                    click: this.taskCasePerformBtnClick
                },
                'taskcase-grid button[action=rollback]': {
                    click: this.taskCaseRollbackBtnClick
                },
                'taskcase-grid button[action=stop]': {
                    click: this.taskCaseStopBtnClick
                },
                'taskcase-grid button[action=start-case]': {
                    click: this.taskCaseStartBtnClick
                },
                'taskcase-grid': {
                    //reconfigure: this.taskCaseGridReconfigure,
                    itemdblclick: this.taskCaseGridItemdblclick,
                    render: this.taskCaseGridRender
                },
                'case-attachments grid': {
                    itemclick: this.onCaseAttachmentsRowClick,
                    itemdblclick: this.onCaseAttachmentsRowDblClick,
                    containerclick: this.onCaseAttachmentsContainerClick
                },
                'task-history-panel grid': {
                    itemclick: this.onCaseHistoryRowClick
                },
                'task-filter-panel opf-combo[name = processId]': {
                    change: this.onFilterPanelProcessIdChange
                },
                'task-filter-panel treepanel': {
                    change: this.onFiltersTreePanelAfterRender,
                    itemclick: this.onFiltersTreePanelItemClick
                },
                'task-filter-panel button[action=search]': {
                    click: this.onFiltersPanelSearchBtnClick
                }
            }
        )
    },

    taskCaseGridRender: function(grid, eOpts) {
        grid.enableDisableButtons();
    },

    taskCaseGridItemdblclick: function( view, model, htmlItem, index, event, eOpts ) {
        this.getActiveGrid().showPerformWin();
    },

    onBeforeCaseNotesStoreLoad: function(store, operation) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        if (OPF.isEmpty(notesPanel.taskId) && OPF.isEmpty(notesPanel.caseId)){
            return false;
        }
        store.proxy.url = OPF.core.utils.RegistryNodeType.CASE_NOTE.generateUrl(
            OPF.isNotEmpty(notesPanel.taskId) ? '/task/' + notesPanel.taskId : '/case/' + notesPanel.caseId);
    },

    onBeforeAssigneesStoreLoad: function(store, operation) {
        var dlg = OPF.Ui.getCmp('assign-task-dlg');
        if (OPF.isEmpty(dlg.id)) {
            return false;
        }
        store.proxy.url = OPF.core.utils.RegistryNodeType.TASK.generateUrl('/current-assignee-candidates/' + dlg.taskId);
    },

    onBeforeCaseAttachmentsStoreLoad: function(store, operation) {
        var dlg = OPF.Ui.getCmp('case-attachments');
        if (OPF.isEmpty(dlg.taskId) && OPF.isEmpty(dlg.caseId)) {
            return false;
        }

        store.proxy.url = OPF.core.utils.RegistryNodeType.CASE_ATTACHMENT.generateUrl('/case/' + dlg.caseId);
    },

    onBeforeCaseActionsStoreLoad: function(store, operation) {
        var dlg = OPF.Ui.getCmp('task-history-panel');
        if (OPF.isEmpty(dlg.taskId) && OPF.isEmpty(dlg.caseId)) {
            return false;
        }

        store.proxy.url = OPF.core.utils.RegistryNodeType.CASE_ACTION.generateGetUrl(dlg.caseId);
    },

    onBeforeProcessActivitiesStoreLoad: function(store, operation) {
        var dlg = OPF.Ui.getCmp('task-filter-panel');
        var processId = dlg.fieldProcess.getValue();
        if (OPF.isBlank(processId)) {
            store.removeAll();
            return false;
        }
        store.proxy.url = OPF.core.utils.RegistryNodeType.ACTIVITY.generateUrl('/process/' + processId);
    },

    onBeforeProcessStatusesStoreLoad: function(store, operation) {
        var dlg = OPF.Ui.getCmp('task-filter-panel');
        var processId = dlg.fieldProcess.getValue();
        if (OPF.isBlank(processId)) {
            store.removeAll();
            return false;
        }
        store.proxy.url = OPF.core.utils.RegistryNodeType.STATUS.generateUrl('/process/' + processId);
    },

    onBeforeMyProcessesStoreLoad: function(store, operation) {
        var taskFilterPanel = OPF.Ui.getCmp('task-filter-panel');
        var url = OPF.core.utils.RegistryNodeType.PROCESS.generateUrl('/my');
        url += OPF.isEmpty(taskFilterPanel.lookupPrefix) ? '' : '?lookupPrefix=' + taskFilterPanel.lookupPrefix;
        store.proxy.url = url;
    },

    onProxyException: function(proxy, type, action, options, response) {
        OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
    },

    taskCaseCaseModeBtnClick: function(btn) {
        if (btn.pressed) {
            var processManagerView = OPF.Ui.getCmp('process-manager-view');
            var caseGrid = processManagerView.caseGridPanel;
            caseGrid.caseButton.toggle(true);
            processManagerView.taskCasePanel.getLayout().setActiveItem(caseGrid);
            caseGrid.loadData();
            caseGrid.enableDisableButtons();
            caseGrid.managerLayout.taskDetailsPanel.refreshPanels();
        }
    },

    taskCaseTaskModeBtnClick: function(btn) {
        if (btn.pressed) {
            var processManagerView = OPF.Ui.getCmp('process-manager-view');
            var taskGrid = processManagerView.taskGridPanel;
            taskGrid.taskButton.toggle(true);
            processManagerView.taskCasePanel.getLayout().setActiveItem(taskGrid);
            taskGrid.loadData();
            taskGrid.enableDisableButtons();
            taskGrid.managerLayout.taskDetailsPanel.refreshPanels();
        }
    },

    taskCaseAssignTeamBtnClick: function(btn) {
        var activeGrid = this.getActiveGrid();
        var selectedModels = activeGrid.selModel.getSelection();
        if (OPF.isEmpty(selectedModels) || selectedModels.length == 0) {
            Ext.Msg.alert('Error', 'Please select one case.');
            return;
        }

        var caseId = activeGrid.isTaskGrid ? selectedModels[0].get('processCase').id : selectedModels[0].get('id');
        var processId = activeGrid.isTaskGrid ? selectedModels[0].get('processCase').process.id : selectedModels[0].get('process').id;

        var winId = 'assignTeamDialog' + caseId; // use the same win for one case (actors are the same)
        var assignTeamDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(assignTeamDialog)) {
            assignTeamDialog = Ext.create('OPF.console.inbox.view.team.AssignTeamDialog', winId, caseId, processId);
        }

        assignTeamDialog.show();
    },

    taskCaseClaimBtnClick: function(btn) {
        var activeGrid = this.getActiveGrid();
        var selectedModels = activeGrid.selModel.getSelection();
        if (OPF.isEmpty(selectedModels)) {
            Ext.Msg.alert('Error', 'Please select one task.');
            return;
        }

        var winId = 'claimTaskDialog2382';
        var claimTaskDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(claimTaskDialog)) {
            claimTaskDialog = Ext.create('OPF.console.inbox.task.ClaimTaskDialog', winId, activeGrid);
        }

        claimTaskDialog.setRecord(selectedModels[0]);
        claimTaskDialog.show();
    },

    taskCaseAssignBtnClick: function(btn) {
        var activeGrid = this.getActiveGrid();
        var selectedModels = activeGrid.selModel.getSelection();
        if (OPF.isEmpty(selectedModels)) {
            Ext.Msg.alert('Error', 'Please select one task.');
            return;
        }

        var winId = 'assignTaskDialog2382';
        var assignTaskDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(assignTaskDialog)) {
            assignTaskDialog = Ext.create('OPF.console.inbox.task.AssignTaskDialog', winId, activeGrid);
        }

        assignTaskDialog.setRecord(selectedModels[0]);
        assignTaskDialog.show();
    },

    taskCasePerformBtnClick: function(btn) {
        this.getActiveGrid().showPerformWin();
    },

    taskCaseRollbackBtnClick: function(btn) {
        this.getActiveGrid().showRollbackWin();
    },

    taskCaseStopBtnClick: function(btn) {
        this.getActiveGrid().showStopCaseWin();
    },

    taskCaseStartBtnClick: function(btn) {
        var dlgId = "startCaseDlg123";
        var dialog = Ext.WindowMgr.get(dlgId);
        if (OPF.isEmpty(dialog)) {
            dialog = Ext.create('OPF.console.inbox.view.processcase.StartCaseDialog', dlgId);
        }
        dialog.show();
    },

    getActiveGrid: function() {
        return OPF.Ui.getCmp('process-manager-view').taskCasePanel.getLayout().getActiveItem();
    },

    onAssignTaskBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('assign-task-dlg');

        var formData = instance.form.getForm().getValues();
        formData.taskId = instance.taskId;

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.TASK.generateUrl('/assign'),
            method: 'POST',
            jsonData: {"data": formData},

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(vo)) {
                    Ext.Msg.alert(vo.success ? 'Success' : 'Error', vo.message);
                }

                var nextRowToSelectId = instance.taskId;

                instance.grid.refreshGrids(nextRowToSelectId);
                instance.grid.managerLayout.taskDetailsPanel.refreshPanelsForced();
                instance.hide();
            },

            failure: function(response) {
                instance.hide();
            }
        });
    },

    onCancelAssignTaskBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('assign-task-dlg');
        instance.hide();
    },

    onAssignTeamBeforeShow: function(win) {
        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.CASE.generateUrl('/read-team/' + win.caseId),
            method: 'GET',

            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(resp)) {
                    if (!resp.success) {
                        Ext.Msg.alert('Error', resp.message);
                        return;
                    }
                }

                var actorUserList = resp.data;
                for (var i = 0; i < actorUserList.length; i++) {
                    var actorUser = actorUserList[i];
                    var actorUserField = Ext.getCmp('actorUserField' + win.id + 'actor' + actorUser.actor.id);
                    if (OPF.isNotEmpty(actorUserField)) {
                        actorUserField.setValue(actorUser.user.id);
                        actorUserField.userActorId = actorUser.id;
                    }
                }
            }
        });
    },

    onAssignTeamBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('assign-team-dlg');

        var formData = new Array();

        for(var i = 0; i < instance.actorUserFields.length; i++) {
            if ("" != instance.actorUserFields[i].getValue()) {
                var userActor = {
                    id: instance.actorUserFields[i].userActorId,
                    processCase: {id : instance.caseId},
                    user: {id : instance.actorUserFields[i].getValue()},
                    actor: {id : instance.actorUserFields[i].actorId}
                };

                formData.push(userActor);
            }
        }

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.CASE.generateUrl('/assign-team'),
            method: 'POST',
            jsonData: {
//                data: formData
                dataList: formData
            },

            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(resp)) {
                    Ext.Msg.alert(resp.success ? 'Success' : 'Error', resp.message);
                }

                instance.hide();
            },

            failure: function(response) {
                instance.hide();
            }
        });
    },

    onCancelAssignTeamBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('assign-team-dlg');
        instance.hide();
    },

    onNotesPanelItemClick: function(gridView, model, htmlElement, index, event, eOpts) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        var selectedModels = notesPanel.noteGrid.selModel.getSelection();
        if (selectedModels) {
            notesPanel.fillingEditor(index);
        } else { // click to deselect a row
            notesPanel.clean();
        }
    },

    onAddNoteBtnClick: function(btn) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        notesPanel.fillingEditor();
        notesPanel.fieldNote.focus();
    },

    onDeleteNoteBtnClick: function(btn) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        notesPanel.deleteRecord();
    },

    onSaveNoteBtnClick: function(btn) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        notesPanel.save();
    },

    onCancelNoteBtnClick: function(btn) {
        var notesPanel = OPF.Ui.getCmp('notes-panel');
        notesPanel.clean();
    },

    beforePerformRollbackDialogShow: function(win) {
        win.noteTextField.reset();
    },

    uploadAttachmentBtnClick: function(btn) {
        var attachmentsPanel = OPF.Ui.getCmp('case-attachments');
        attachmentsPanel.showUploadDialog();
    },

    deleteAttachmentBtnClick: function(btn) {
        var attachmentsPanel = OPF.Ui.getCmp('case-attachments');
        attachmentsPanel.deleteRecord();
    },

    onCaseAttachmentsRowClick: function(gridView, model, htmlElement, index, event, eOpts) {
        var attachmentsPanel = OPF.Ui.getCmp('case-attachments');
        attachmentsPanel.deleteAttachmentBtn.enable();
    },

    onCaseAttachmentsRowDblClick: function( view, model, htmlItem, index, event, eOpts ) {
        var attachmentsPanel = OPF.Ui.getCmp('case-attachments');
        attachmentsPanel.showUploadDialog(index);
    },

    onCaseAttachmentsContainerClick: function(gridView, event, eOpts) {
        var attachmentsPanel = OPF.Ui.getCmp('case-attachments');
        attachmentsPanel.deleteAttachmentBtn.disable();
        attachmentsPanel.grid.selModel.clearSelections()
    },

    onCaseHistoryRowClick: function(gridView, model, htmlElement, index, event, eOpts) {
        if (model != null) {
            var taskHistoryPanel = OPF.Ui.getCmp('task-history-panel');
            taskHistoryPanel.assigneeField.setValue(OPF.ifBlank(model.get('userName'), ''));
            taskHistoryPanel.performedOnField.setValue(OPF.isNotEmpty(model.get('performedOn')) ? Ext.Date.format(model.get('performedOn'), 'Y-m-d') : '');
            taskHistoryPanel.actionField.setValue(OPF.ifBlank(model.get('type'), ''));
            taskHistoryPanel.explanationField.setValue(OPF.ifBlank(model.get('explanation'), ''));
            taskHistoryPanel.noteTextArea.setValue(OPF.ifBlank(model.get('note'), ''));
        }
    },

    onFilterPanelProcessIdChange: function(combo, newValue, oldValue) {
        var taskFilterPanel = OPF.Ui.getCmp('task-filter-panel');
        taskFilterPanel.fieldActivity.reset();
        taskFilterPanel.fieldStatus.reset();
        taskFilterPanel.showCustomFields(newValue);
        if (OPF.isBlank(newValue)) {
            taskFilterPanel.fieldActivity.store.removeAll();
            taskFilterPanel.fieldActivity.disable();
            taskFilterPanel.fieldStatus.store.removeAll();
            taskFilterPanel.fieldStatus.disable();
        } else {
            taskFilterPanel.fieldActivity.store.load();
            taskFilterPanel.fieldActivity.enable();
            taskFilterPanel.fieldStatus.store.load();
            taskFilterPanel.fieldStatus.enable();
        }
    },

    onFiltersTreePanelAfterRender: function(tree) {
        var filterTreePanel = OPF.Ui.getCmp('task-filter-panel');
        tree.getSelectionModel().select(tree.store.getNodeById(filterTreePanel.filterBy + 'NodeId'));
        filterTreePanel.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
    },

    onFiltersTreePanelItemClick: function(view, model, htmlElement, index, e, eOpts) {
        var filterParameter = model.get('filterParameter');
        var filterTreePanel;
        if (OPF.isNotEmpty(filterParameter)) {
            filterTreePanel = OPF.Ui.getCmp('task-filter-panel');
            filterTreePanel.filterBy = filterParameter;
            filterTreePanel.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
        } else {
            var id = model.get('id');
            if (OPF.isNotEmpty(id) && Ext.isNumber(id)) {
                filterTreePanel = OPF.Ui.getCmp('task-filter-panel');
                filterTreePanel.filterBy = FILTER_PROCESS;
                filterTreePanel.selectedProcessId = id;
                filterTreePanel.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
            }
        }
    },

    onFiltersPanelSearchBtnClick: function(btn) {
        var filterTreePanel = OPF.Ui.getCmp('task-filter-panel');
        filterTreePanel.filterBy = FILTER_SEARCH;
        filterTreePanel.managerLayout.taskCasePanel.getLayout().getActiveItem().refreshGrids();
    }

});