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

Ext.define('OPF.console.domain.controller.ActionController', {
    extend: 'Ext.app.Controller',

    views: ['action.ActionParameterRowEditorDialog', 'action.ActionParameterFieldSet', 'action.ActionEditor'],

    stores: ['ActionParameters'],

    models: ['ActionModel', 'ActionParameterModel'],

    init: function() {
        this.control(
            {
                'action-editor': {
                    afterrender: this.initEditor,
                    showeditor: this.onShowEditor
                },
                'action-editor button[action=gendocs]': {
                    click: this.onGenerateDocsBtnClick
                },
                'action-parameters-grid': {
                    itemdblclick: this.onActionParametersClick
                },
                'action-parameters-grid button[action=add-parameter]': {
                    click: this.onAddParameterBtnClick
                },
                'action-parameters-grid button[action=delete-parameter]': {
                    click: this.onDeleteParameterBtnClick
                },
                'action-parameter-row-editor button[action=save-parameter]': {
                    click: this.onSaveParameterBtnClick
                },
                'action-parameter-row-editor button[action=reset-parameter]': {
                    click: this.onResetParameterBtnClick
                },
                'action-parameter-row-editor button[action=cancel-parameter]': {
                    click: this.onCancelParameterBtnClick
                }
            }
        )
    },

    initEditor: function(editor) {
        this.editor = editor;
    },

    onShowEditor: function(editor) {
        var isUpdateMode = editor.saveAs == 'update';
        editor.generateDocumentationButton.setDisabled(!isUpdateMode);
    },

    onGenerateDocsBtnClick: function(button) {
        var me = this;

        button.setText('Generating...');
        button.setDisabled(true);

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/content/documentation/generate/example/' + this.editor.entityLookup),
            method: 'PUT',

            success: function(response, action) {
                button.setText('Gen Docs');
                button.setDisabled(false);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Documentations have been generated successfully.');
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Appeared server error, please see logs for more information.');
            }
        });
    },

    onActionParametersClick: function(gridView, model, htmlElement, rowIndex, e, eOpts) {
        var grid = OPF.Ui.getCmp('action-parameters-grid');
        grid.getEditorDialog().startEditing(rowIndex);
    },

    onAddParameterBtnClick: function(btn) {
        var grid = OPF.Ui.getCmp('action-parameters-grid');
        grid.getEditorDialog().startEditing();
    },

    onDeleteParameterBtnClick: function(btn) {
        var grid = OPF.Ui.getCmp('action-parameters-grid');
        var selectedModels = grid.getSelectionModel().getSelection();
        if (!selectedModels) {
            return false;
        }
        this.getActionParametersStore().remove(selectedModels);
    },

    onSaveParameterBtnClick: function(btn) {
        var parameterEditor = OPF.Ui.getCmp('action-parameter-row-editor');
        var model;
        if (isNotEmpty(parameterEditor.rowIndex)) {
            model = parameterEditor.grid.store.getAt(parameterEditor.rowIndex);
            model.set('name', parameterEditor.paramName.getValue());
            model.set('location', parameterEditor.paramLocation.getValue());
            model.set('fieldType', parameterEditor.paramType.getValue());
            //model.set('typeName', paramTypeName);
            model.set('description', parameterEditor.paramDescription.getValue());
            parameterEditor.stopEditing();
        } else {
            model = Ext.create('OPF.console.domain.model.ActionParameterModel');
            model.set('name', parameterEditor.paramName.getValue());
            model.set('location', parameterEditor.paramLocation.getValue());
            model.set('fieldType', parameterEditor.paramType.getValue());
            //model.set('typeName', paramTypeName);
            model.set('description', parameterEditor.paramDescription.getValue());
            parameterEditor.stopEditing();
            parameterEditor.grid.store.add(model);
        }
        parameterEditor.grid.refreshOrderPositions();
    },

    onResetParameterBtnClick: function(btn) {
        var parameterEditor = OPF.Ui.getCmp('action-parameter-row-editor');
        parameterEditor.fillingEditor(this.rowIndex);
    },

    onCancelParameterBtnClick: function(btn) {
        var parameterEditor = OPF.Ui.getCmp('action-parameter-row-editor');
        parameterEditor.stopEditing();
    }

});