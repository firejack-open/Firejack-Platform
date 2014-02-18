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

Ext.define('OPF.console.domain.view.process.StatusEditorDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.status-editor-dlg',

    title: 'Status Editor',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,
    rowIndex: null,

    autoHeight: true,
    autoWidth: true,
    layout: 'fit',
    resizable: false,

    constructor: function(id, grid, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.StatusEditorDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        this.pathField = OPF.Ui.displayField('path', 'Path');
        this.nameField = OPF.Ui.textFormField('name', 'Name', {
            labelAlign: 'top',
            subFieldLabel: '',
            enableKeyEvents: true
        });
        this.lookupField = OPF.Ui.displayField('lookup', 'Lookup');
        this.descriptionField = OPF.Ui.textFormArea('description', 'Description', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            width: 450,
            height: 250,
            padding: 10,
            frame: true,
            border: false,
            headerAsText: false,
            monitorValid: true,
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            fbar: [
                OPF.Ui.createBtn('Save', 50, 'save-status', {formBind: true}),
                OPF.Ui.createBtn('Reset', 60, 'reset-status'),
                OPF.Ui.createBtn('Cancel', 60, 'cancel-status')
            ],
            items: [
                this.pathField,
                this.nameField,
                this.lookupField,
                this.descriptionField
            ]
        });

        this.items = this.form;

        this.callParent(arguments);

        var constraints = Ext.create('OPF.core.validation.FormInitialisation', OPF.core.utils.RegistryNodeType.STATUS.getConstraintName());
        constraints.initConstraints(this.form, null);
    },

    fillEditor: function(baseLookup, rowIndex) {
        this.baseLookup = baseLookup;
        this.rowIndex = rowIndex;
        if (OPF.isNotEmpty(rowIndex)) {
            var rec = this.grid.store.getAt(rowIndex);
            if (OPF.isNotEmpty(rec)) {
                this.pathField.setValue(this.baseLookup);
                this.nameField.setValue(rec.data.name);
                this.descriptionField.setValue(rec.data.description);
                this.updateLookup();
                return;
            }
        }

        this.pathField.setValue(this.baseLookup);
        this.nameField.setValue('');
        this.descriptionField.setValue('');
        this.updateLookup();
    },

    startEditing: function(baseLookup, rowIndex) {
        this.fillEditor(baseLookup, rowIndex);
        this.show();
    },

    setGrid: function(grid) {
        this.grid = grid;
    },

    updateLookup: function() {
        var lookup = calculateLookup(this.pathField.getValue(), this.nameField.getValue());
        this.lookupField.setValue(lookup);
    }

});

Ext.define('OPF.console.domain.view.process.StatusesGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.statuses-grid',
    height: 240,
    cls: 'border-radius-grid-body border-radius-grid-docked-top',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.StatusesGrid.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    plugins: [
        new OPF.core.component.GridDragDropRowOrder({
            listeners: {
                afterrowmove: function(gridDropTarget) {
                    gridDropTarget.grid.refreshOrderPositions();
                }
            }
        })
    ],

    store: 'Statuses',

    columns: [
        {dataIndex: 'name', text: 'Name', width: 100},
        {dataIndex: 'description', text: 'Description', flex: 1, renderer: 'htmlEncode'}
    ],

    initComponent: function() {
        this.tbar = [
            OPF.Ui.createBtn('Add', 45, 'add-status', {iconCls: 'silk-add'}),
            '-',
            OPF.Ui.createBtn('Delete', 60, 'delete-status', {iconCls: 'silk-delete'})
        ];

        this.callParent(arguments);
    },

    getEditorDialog: function() {
        var winId = 'statusesRowEditorDialog1987';
        var editorDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(editorDialog)) {
            editorDialog = Ext.create('OPF.console.domain.view.process.StatusEditorDialog', winId, this);
        } else {
            editorDialog.setGrid(this);
        }
        return editorDialog;
    },

    cleanFieldStore: function() {
        this.store.removeAll();
    },

    refreshOrderPositions: function() {
        var activitiesGridStore = this.editPanel.activitiesFieldSet.activitiesGrid.store;

        var models = this.store.getRange();
        Ext.each(models, function(statusModel, index) {
            var newOrderPosition = index + 1;
            // if status record is newly created (phantom == true), its negative sortPosition (* -1) is used instead of id in order to associate to activities;
            // need to update all those activities when sortPosition changes
            if (statusModel.phantom) {
                activitiesGridStore.each(function(activityModel) {
                    if (activityModel.data.statusId == - statusModel.data.sortPosition) {
                        activityModel.set('statusId', - newOrderPosition);
                    }
                });
            }
            statusModel.set('sortPosition', newOrderPosition);
        });
    }
});

Ext.define('OPF.console.domain.view.process.StatusesFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Statuses',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.StatusesFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {

        this.statusesGrid = Ext.create('OPF.console.domain.view.process.StatusesGrid', this.editPanel);

        this.items = [
            this.statusesGrid
        ];

        this.callParent(arguments);
    }
});