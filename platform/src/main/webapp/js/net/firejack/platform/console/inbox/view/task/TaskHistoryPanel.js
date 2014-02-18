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
Ext.define('OPF.console.inbox.view.task.TaskHistoryPanel', {
    extend: 'OPF.console.inbox.view.DetailsCardPanel',

    alias: 'widget.task-history-panel',

    //title: 'Case History',
    fieldLabel: 'Case History',
    id: 'case-history-panel',
    //width: 724,
    height: 290,
    layout: 'border',

    initComponent: function() {

        this.layoutConfig = {
            pack: 'start',
            align: 'stretch',
            padding: 5
        };

        this.caseActionsGrid = Ext.create('Ext.grid.Panel', {
            title: 'Case Actions',
            flex: 1,
            store: 'CaseActions',
            columns: [
                OPF.Ui.populateColumn('userName', 'User', {width: 80}),
                OPF.Ui.populateColumn('type', 'Action', {width: 100}),
                OPF.Ui.populateColumn('explanation', 'Explanation', {width: 100, renderer: 'htmlEncode'}),
                OPF.Ui.populateColumn('note', 'Note', {flex: 1, renderer: 'htmlEncode'}),
                OPF.Ui.populateColumn('assignee', 'Assignee', {width: 100}),
                OPF.Ui.populateDateColumn('performedOn', 'Performed', {width: 120})
            ]
        });

        this.assigneeField = Ext.create('Ext.form.field.Display', { anchor: '100%', hideLabel: true });
        this.actionField = Ext.create('Ext.form.field.Display', { anchor: '100%', hideLabel: true });
        this.performedOnField = Ext.create('Ext.form.field.Display', { anchor: '100%', hideLabel: true });
        this.explanationField = Ext.create('Ext.form.field.Display', { anchor: '100%', hideLabel: true });

        this.noteTextArea = Ext.create('OPF.core.component.TextArea', {
            anchor: '100%',
            fieldLabel: 'Note',
            hideLabel: true,
            readOnly: true,
            height: 80
        });

        this.caseActionForm = Ext.create('Ext.form.Panel', {
            flex: 1,
            width: 200,
            padding: 5,
            labelAlign: 'left',
            labelWidth: 0,
            items: [
                this.assigneeField,
                this.performedOnField,
                this.actionField,
                this.explanationField,
                Ext.create('Ext.form.field.Display', { anchor: '100%', hideLabel: true }),
                this.noteTextArea
            ]
        });

        this.items = [
            {
                xtype: 'panel',
                layout: 'fit',
                border: false,
                region: 'west',
                split: true,
                width: 600,
                items: [
                    this.caseActionsGrid
                ]
            },
            {
                xtype: 'panel',
                layout: 'fit',
                border: false,
                region: 'center',
                items: [
                    this.caseActionForm
                ]
            }
        ];

        this.callParent(arguments);
    },

    refreshPanel: function(caseId) {
        this.caseId = caseId;
        if (OPF.isEmpty(caseId)) {
            this.clear();
        } else {
            this.caseActionsGrid.store.load();
        }
    },

    clear: function() {
        this.caseActionsGrid.store.removeAll(false);

        this.assigneeField.setValue("");
        this.performedOnField.setValue("");
        this.actionField.setValue("");
        this.explanationField.setValue("");
        this.noteTextArea.setValue("");
    },

    refreshPanelData: function(parentPanel) {
        this.refreshPanel(parentPanel.caseId);
    }

});