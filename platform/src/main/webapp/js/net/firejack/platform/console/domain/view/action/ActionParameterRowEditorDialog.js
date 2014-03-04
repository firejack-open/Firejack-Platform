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