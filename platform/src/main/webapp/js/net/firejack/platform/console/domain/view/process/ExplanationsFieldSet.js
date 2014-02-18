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

Ext.define('OPF.console.domain.view.process.ExplanationEditorDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.explanation-editor-dlg',

    title: 'Process Action Explanation',
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
        OPF.console.domain.view.process.ExplanationEditorDialog.superclass.constructor.call(this, Ext.apply({
            id: id,
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        this.shortDescriptionField = OPF.Ui.textFormField('shortDescription', 'Explanation', {
            labelAlign: 'top',
            subFieldLabel: '',
            emptyText: 'enter display description'
        });

        this.longDescriptionField = OPF.Ui.textFormArea('longDescription', 'Description',{
            labelAlign: 'top',
            subFieldLabel: '',
            emptyText: 'detailed description optional'
        });

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            width: 450,
            height: 200,
            padding: 10,
            border: false,
            frame: true,
            headerAsText: false,
            monitorValid: true,
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            fbar: [
                OPF.Ui.createBtn('Save', 50, 'save-explanation', {formBind: true}),
                OPF.Ui.createBtn('Reset', 60, 'reset-explanation'),
                OPF.Ui.createBtn('Cancel', 60, 'cancel-explanation')
            ],
            items: [
                this.shortDescriptionField,
                this.longDescriptionField
            ]
        });
        this.items = this.form;
        this.callParent(arguments);

        var constraints = Ext.create('OPF.core.validation.FormInitialisation', OPF.core.utils.RegistryNodeType.CASE_EXPLANATION.getConstraintName());
        constraints.initConstraints(this.form, null);
    },

    fillEditor: function(baseLookup, rowIndex) {
        this.baseLookup = baseLookup;
        this.rowIndex = rowIndex;
        if (OPF.isNotEmpty(rowIndex)) {
            var rec = this.grid.store.getAt(rowIndex);
            if (OPF.isNotEmpty(rec)) {
                this.shortDescriptionField.setValue(rec.data.shortDescription);
                this.longDescriptionField.setValue(rec.data.longDescription);
                return;
            }
        }

        this.shortDescriptionField.setValue("");
        this.longDescriptionField.setValue("");
    },

    startEditing: function(baseLookup, rowIndex) {
        this.fillEditor(baseLookup, rowIndex);
        this.show();
    },

    setGrid: function(grid) {
        this.grid = grid;
    }

});

Ext.define('OPF.console.domain.view.process.ExplanationGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.explanations-grid',
    height: 240,
    cls: 'border-radius-grid-body border-radius-grid-docked-top',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ExplanationGrid.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },
    store: 'CaseExplanations',
    columns: [
        {dataIndex: 'shortDescription', text: 'Explanation', width: 100, renderer: 'htmlEncode'},
        {dataIndex: 'longDescription', text: 'Description', flex: 1, renderer: 'htmlEncode'}
    ],

    initComponent: function() {
        this.tbar = [
            OPF.Ui.createBtn('Add', 45, 'add-explanation', {iconCls: 'silk-add'}),
            '-',
            OPF.Ui.createBtn('Delete', 60, 'delete-explanation', {iconCls: 'silk-delete'})
        ];
        this.callParent(arguments);
    },

    getEditorDialog: function() {
        var winId = 'explanationsRowEditorDialog1987';
        var editorDialog = Ext.WindowMgr.get(winId);
        if (OPF.isEmpty(editorDialog)) {
            editorDialog = Ext.create('OPF.console.domain.view.process.ExplanationEditorDialog', winId, this);
        } else {
            editorDialog.setGrid(this);
        }
        return editorDialog;
    },

    cleanFieldStore: function() {
        this.store.removeAll();
    }

});

Ext.define('OPF.console.domain.view.process.ExplanationFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Explanations',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.ExplanationFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {

        this.explanationGrid = Ext.create('OPF.console.domain.view.process.ExplanationGrid', this.editPanel);

        this.items = [
            this.explanationGrid
        ];

        this.callParent(arguments);
    }
});