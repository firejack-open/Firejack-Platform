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

Ext.define('OPF.console.inbox.view.task.Columns', {});

OPF.console.inbox.view.task.Columns.columnInfo = function(name, header, cfg) {
    var info = {fieldName: name, fieldHeader: header};
    if (OPF.isNotEmpty(cfg)) {
        Ext.apply(info, cfg);
    }
    return info;
};
OPF.console.inbox.view.task.Columns.populateColumns = function(fields, columnsData, sortingColumn) {
    var producedColumns = [];
    if (OPF.isNotEmpty(columnsData) && OPF.isNotEmpty(fields) &&
        Ext.isArray(columnsData) && columnsData.length > 0 &&
        Ext.isArray(fields) && fields.length > 0) {
        for (var i = 0; i < columnsData.length; i++) {
            for (var j = 0; j < fields.length; j++) {
                if (columnsData[i].fieldName === fields[j].name) {
                    var cfg = {};
                    if (OPF.isNotEmpty(columnsData[i].width)) {
                        cfg.width = columnsData[i].width;
                    } else if (OPF.isNotEmpty(columnsData[i].flex)) {
                        cfg.flex = columnsData[i].flex;
                    }
                    var column;
                    if (fields[j].type === 'date') {
                        column = OPF.Ui.populateDateColumn(columnsData[i].fieldName, columnsData[i].fieldHeader, cfg.width);
                    } else if (fields[j].type === 'int') {
                        column = OPF.Ui.populateNumberColumn(columnsData[i].fieldName, columnsData[i].fieldHeader, cfg.width)
                    } else {
                        column = OPF.Ui.populateColumn(columnsData[i].fieldName, columnsData[i].fieldHeader, cfg);
                    }
                    producedColumns.push(column);
                    if (OPF.isNotEmpty(sortingColumn) && column.dataIndex == sortingColumn.columnName) {
                        column.sortState = sortingColumn.sortOrder;
                    }
                }
            }
        }
    }
    return producedColumns;
};

/**
 *
 */
Ext.define('OPF.console.inbox.view.task.TaskGridPanel', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.taskcase-grid',

    headerAsText: true,
    managerLayout: null,
    isTaskGrid: false,
    cls: 'border-radius-grid-body border-radius-grid-header-top',
    border: false,

    constructor: function(managerLayout, isTaskGrid, cfg) {
        cfg = cfg || {};
        this.lookupPrefix = null;
        OPF.console.inbox.view.task.TaskGridPanel.superclass.constructor.call(this, Ext.apply({
            title: isTaskGrid ? 'Tasks' : 'Cases',
            isTaskGrid: isTaskGrid,
            managerLayout: managerLayout
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.store = Ext.create('OPF.console.inbox.store.CaseTaskReconfigurableStore', this);
        this.columns =
            OPF.console.inbox.view.task.Columns.populateColumns(
                this.isTaskGrid ?
                    OPF.console.inbox.model.TaskModel.configuredFields :
                    OPF.console.inbox.model.CaseModel.configuredFields,
                this.getDefaultColumnsData(), null);
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode: 'SINGLE',
            allowDeselect: true,
            listeners: {
                selectionchange: function(selModel, selectedItems, eOpts) {
                    var taskId = null;
                    var caseId = null;
                    if (OPF.isNotEmpty(selectedItems) && selectedItems.length > 0) {
                        caseId = instance.isTaskGridActive() ?
                            selectedItems[0].get('processCase').id : selectedItems[0].get('id');
                        if (instance.isTaskGridActive()) {
                            taskId = selectedItems[0].get('id');
                        }
                    }
                    instance.managerLayout.taskDetailsPanel.refreshPanels(taskId, caseId);
                    instance.enableDisableButtons();
                }
            }
        });

        this.assignTeamButton = OPF.Ui.createBtn('Assign Team', 80, 'assign-team');
        this.claimButton = OPF.Ui.createBtn('Claim', 55, 'claim');
        this.assignButton = OPF.Ui.createBtn('Assign', 55, 'assign');
        this.nextButton = OPF.Ui.createBtn('Next', 50, 'perform');
        this.previousButton = OPF.Ui.createBtn('Previous', 65, 'rollback');
        this.stopButton = OPF.Ui.createBtn('Stop', 50, 'stop');

        this.caseButton = OPF.Ui.createBtn('Cases', 50, 'case-mode', {
            enableToggle: true,
            allowDepress: false,
            toggleGroup: 'casetask',
            pressed: !this.isTaskGrid
        });

        this.taskButton = OPF.Ui.createBtn('Tasks', 50, 'task-mode', {
            enableToggle: true,
            allowDepress: false,
            toggleGroup: 'casetask',
            pressed: this.isTaskGrid
        });

        this.startCaseButton = OPF.Ui.createBtn('Start Case', 70, 'start-case');

        this.dockedItems = [
            {
                xtype: 'toolbar',
                //id: 'inbox-toolbar',
                dock: 'top',
                ui: 'footer',
                cls: 'inbox-grid-toolbar',
                items: [
                    this.startCaseButton,
                    this.assignTeamButton,
                    this.claimButton,
                    this.assignButton,
                    this.nextButton,
                    this.previousButton,
                    this.stopButton,
                    { xtype: 'tbfill' },
                    this.caseButton,
                    this.taskButton
                ]
            }
        ];

        this.pagingToolbar = Ext.create('Ext.toolbar.Paging', {
            store: this.store,
            displayInfo: true,
            pageSize: 20,
            prependButtons: true
        });

        this.bbar = this.pagingToolbar;

        this.callParent(arguments);
    },

    setLookupPrefix: function(lookupPrefix) {
        this.lookupPrefix = lookupPrefix;
    },

    loadData: function(newSelectedRowId) {
        var instance = this;
        this.store.load({
            start: 0,
            limit: 20,
            callback: function(data, operation, success) {
                if (success) {
                    var totalNumPages = Math.ceil(instance.store.getTotalCount() / instance.pagingToolbar.pageSize);
                    var activePage = Math.ceil((instance.pagingToolbar.cursor + instance.pagingToolbar.pageSize) / instance.pagingToolbar.pageSize);
                    if (activePage > totalNumPages && totalNumPages != 0) { // ExtJS bug: moveLast() sets page to 0 if there are no records
                        instance.pagingToolbar.moveLast();
                    }

                    if (OPF.isNotEmpty(newSelectedRowId)) {
                        var recIndex = instance.store.findExact('id', newSelectedRowId);
                        if (recIndex != -1) {
                            instance.selModel.select(recIndex);
                            return;
                        }
                    }
                    if (instance.store.getCount() != 0) {
                        instance.selModel.select(0);
                    } else {
                        instance.managerLayout.taskDetailsPanel.refreshPanels(null, null); // clear details panels
                    }
                }
            }
        });
    },

    getDefaultColumnsData: function() {
        if (OPF.isEmpty(this.defaultColumnsData)) {
            this.defaultColumnsData = [
                OPF.console.inbox.view.task.Columns.columnInfo('processName', 'Process')
            ];
            if (this.isTaskGrid) {
                this.defaultColumnsData.push(
                    OPF.console.inbox.view.task.Columns.columnInfo('activityName', 'Activity'));
            }
            this.defaultColumnsData.push(
                OPF.console.inbox.view.task.Columns.columnInfo('description', 'Description', {flex: 1}),
                OPF.console.inbox.view.task.Columns.columnInfo('assigneeName', 'Assignee'),
                OPF.console.inbox.view.task.Columns.columnInfo('created', 'Started', {width: 80}),
                OPF.console.inbox.view.task.Columns.columnInfo('updateDate', 'Updated', {width: 80}),
                OPF.console.inbox.view.task.Columns.columnInfo('closeDate', 'Closed', {width: 80}),
                OPF.console.inbox.view.task.Columns.columnInfo('active', 'Active'),
                OPF.console.inbox.view.task.Columns.columnInfo('statusName', 'Status')
            );
        }
        return this.defaultColumnsData;
    },

    showPerformWin: function() {
        var selectedModels = this.getSelectedRow(true);
        if (OPF.isNotEmpty(selectedModels) && Ext.isArray(selectedModels) && selectedModels.length > 0) {
            var activeStatus = selectedModels[0].get('active');
            if (activeStatus) {
                var winIdTask = 'performTaskDialog2355';
                var winIdCase = 'performCaseDialog3192';
                var performDialog;
                var id = selectedModels[0].get('id'), processId;
                if (this.isTaskGridActive()) {
                    performDialog = Ext.WindowMgr.get(winIdTask);
                    if (OPF.isEmpty(performDialog)) {
                        performDialog = Ext.create('OPF.console.inbox.view.PerformTaskDialog', winIdTask, this);
                    }
                    processId = selectedModels[0].get('processCase').process.id;
                } else {
                    performDialog = Ext.WindowMgr.get(winIdCase);
                    if (OPF.isEmpty(performDialog)) {
                        performDialog = Ext.create('OPF.console.inbox.view.processcase.PerformCaseDialog', winIdCase, this);
                    }
                    processId = selectedModels[0].get('process').id;
                }

                performDialog.setIds(id, processId);
                performDialog.show();
            }
        }
    },

    showRollbackWin: function() {
        var selectedModels = this.getSelectedRow(true);
        if (OPF.isNotEmpty(selectedModels) && Ext.isArray(selectedModels) && selectedModels.length > 0) {
            var winIdTask = 'rollbackTaskDialog2382';
            var winIdCase = 'rollbackCaseDialog2382';
            var rollbackDialog;
            var id = selectedModels[0].get('id'), processId;
            if (this.isTaskGridActive()) {
                rollbackDialog = Ext.WindowMgr.get(winIdTask);
                if (OPF.isEmpty(rollbackDialog)) {
                    rollbackDialog = Ext.create('OPF.console.inbox.view.task.RollbackTaskDialog', winIdTask, this);
                }
                processId = selectedModels[0].get('processCase').process.id;
            } else {
                rollbackDialog = Ext.WindowMgr.get(winIdCase);
                if (OPF.isEmpty(rollbackDialog)) {
                    rollbackDialog = Ext.create('OPF.console.inbox.view.processcase.RollbackCaseDialog', winIdCase, this);
                }
                processId = selectedModels[0].get('process').id;
            }

            rollbackDialog.setIds(id, processId);
            rollbackDialog.show();
        }
    },

    showStopCaseWin: function() {
        var selectedModels = this.getSelectedRow(true);
        if (OPF.isNotEmpty(selectedModels) && Ext.isArray(selectedModels) && selectedModels.length > 0) {
            var winId = 'stopCaseDialog1822';
            var stopDialog;

            stopDialog = Ext.WindowMgr.get(winId);
            if (OPF.isEmpty(stopDialog)) {
                stopDialog = Ext.create('OPF.console.inbox.view.processcase.StopCaseDialog', winId, this);
            }
            var id = this.isTaskGridActive() ? selectedModels[0].get('processCase').id : selectedModels[0].get('id');
            var processId = this.isTaskGridActive() ? selectedModels[0].get('processCase').process.id : selectedModels[0].get('process').id;
            stopDialog.setIds(id, processId);
            stopDialog.show();
        }
    },

    disableButtons: function() {
        this.assignTeamButton.disable();
        this.assignButton.disable();
        this.claimButton.disable();
        this.nextButton.disable();
        this.previousButton.disable();
        this.stopButton.disable();
    },

    enableDisableButtons: function() {
        var selected = this.selModel.getSelection();
        var model = selected == null || selected.length == 0 ?
            null : Ext.isArray(selected) ? selected[0] : selected;
        if (OPF.isEmpty(model)) {
            this.disableButtons();
            return;
        }

        if (!model.get('active')) {
            this.disableButtons();
            return;
        }

        if (model.get('userCanPerform')) {
            this.nextButton.enable();
            this.stopButton.enable();
            var hasPreviousTask = this.isTaskGrid ? model.get('processCase').hasPreviousTask : model.get('hasPreviousTask');
            if (hasPreviousTask) {
                this.previousButton.enable();
            } else {
                this.previousButton.disable();
            }
        } else {
            this.nextButton.disable();
            this.stopButton.disable();
            this.previousButton.disable();
        }

        this.assignTeamButton.enable();
        if (this.isTaskGridActive()) {
            //this.assignTeamButton.disable();
            this.assignButton.enable();
            if (model.get('userCanPerform') && model.get('assignee').id != OPF.Cfg.USER_INFO.id) {
                this.claimButton.enable();
            } else {
                this.claimButton.disable();
            }
        } else {
            //this.assignTeamButton.enable();
            this.assignButton.disable();
            this.claimButton.disable();
        }
    },

    refreshGrids: function(newSelectedRowId) {
        this.loadData(newSelectedRowId);
    },

    getSelectedRow: function(forceSelection) {
        var selectedModel = this.selModel.getSelection();
        if (OPF.isEmpty(selectedModel) && forceSelection) {
            Ext.Msg.alert('Error', 'Please select one case / task.');
            return;
        }
        return selectedModel;
    },

    isTaskGridActive: function() {
        return this.isTaskGrid;
    },

    isCaseGridActive: function() {
        return !this.isTaskGrid;
    }

});