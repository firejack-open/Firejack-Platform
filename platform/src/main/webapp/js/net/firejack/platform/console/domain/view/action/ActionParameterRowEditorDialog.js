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


Ext.define('OPF.console.domain.view.action.ActionParameterRowEditorDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.action-parameter-row-editor',

    title: 'Parameter Editor',

    id: 'actionParameterRowEditorDialog',
    layout: 'fit',
    modal: true,
    closable: true,
    closeAction: 'hide',

    grid: null,
    rowIndex: null,

    width: 400,
    height: 240,

    constructor: function(grid, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.action.ActionParameterRowEditorDialog.superclass.constructor.call(this, Ext.apply({
            grid: grid
        }, cfg));
    },

    initComponent: function(){
        this.paramName = OPF.Ui.textFormField('name', 'Name');

        this.paramLocation = OPF.Ui.comboFormField('location', 'Location');

        this.paramType = OPF.Ui.comboFormField('fieldType', 'Type');

        this.paramDescription = OPF.Ui.textFormArea('description', 'Description');

        this.form = Ext.create('Ext.form.Panel', {
            frame: true,
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            fbar: [
                OPF.Ui.createBtn('Save', 50, 'save-parameter', {formBind : true}),
                OPF.Ui.createBtn('Reset', 55, 'reset-parameter'),
                OPF.Ui.createBtn('Cancel', 60, 'cancel-parameter')
            ],
            items: [
                this.paramLocation,
                this.paramName,
                this.paramType,
                this.paramDescription
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    setGrid: function(grid) {
        this.grid = grid;
    },

    fillingEditor: function(rowIndex) {
        if (isNotEmpty(rowIndex)) {
            this.rowIndex = rowIndex;
            var model = this.grid.store.getAt(rowIndex);
            if (isNotEmpty(model)) {
                this.paramName.setValue(model.get('name'));
                this.paramLocation.setValue(model.get('location'));
                this.paramType.setValue(model.get('fieldType'));
                this.paramDescription.setValue(model.get('description'));
            }
        }
    },

    startEditing: function(rowIndex) {
        this.show();
        this.fillingEditor(rowIndex);

        var constraints = Ext.create('OPF.core.validation.FormInitialisation', OPF.core.utils.RegistryNodeType.ACTION_PARAMETER.getConstraintName());
        constraints.initConstraints(this.form, null);
    },

    stopEditing: function() {
        this.hide();
        this.paramName.setValue('');
        this.paramLocation.setValue('');
        this.paramType.setValue('');
        this.paramDescription.setValue('');
        this.rowIndex = null;
    }

});