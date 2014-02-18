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

Ext.define('OPF.console.domain.controller.ProcessController', {
    extend: 'Ext.app.Controller',

    views: ['process.ProcessEditor'],

    stores: ['Activities', 'Statuses', 'ActivityActors', 'CaseExplanations', 'ProcessFields', 'RegistryNodeFields'],

    models: ['ActivityModel', 'StatusModel', 'ProcessModel'],

    init: function() {
        this.getProcessFieldsStore().addListener('datachanged', this.onProcessFieldsStoreLoad, this);
        this.control(
            {
                'activity-editor-dlg button[action=save-activity]': {
                    click: this.onActivitySaveBtnClick
                },
                'activity-editor-dlg button[action=reset-activity]': {
                    click: this.onActivityResetBtnClick
                },
                'activity-editor-dlg button[action=cancel-activity]': {
                    click: this.onActivityCancelBtnClick
                },
                'activity-editor-dlg opf-textfield[name=name]': {
                    change: this.onActivityNameFieldChange,
                    keyup: this.onActivityNameFieldChange
                },
                'activities-grid': {
                    itemdblclick: this.onActivityRowClick
                },
                'activities-grid button[action=add-activity]': {
                    click: this.onAddActivityBtnClick
                },
                'activities-grid button[action=delete-activity]': {
                    click: this.onDeleteActivityBtnClick
                },
                'statuses-grid': {
                    itemdblclick: this.onStatusRowClick
                },
                'statuses-grid button[action=add-status]': {
                    click: this.onAddStatusBtnClick
                },
                'statuses-grid button[action=delete-status]': {
                    click: this.onDeleteStatusBtnClick
                },
                'status-editor-dlg opf-textfield[name=name]': {
                    change: this.onStatusNameFieldChange,
                    keyup: this.onStatusNameFieldChange
                },
                'status-editor-dlg button[action=save-status]': {
                    click: this.onStatusSaveBtnClick
                },
                'status-editor-dlg button[action=reset-status]': {
                    click: this.onStatusResetBtnClick
                },
                'status-editor-dlg button[action=cancel-status]': {
                    click: this.onStatusCancelBtnClick
                },
                'explanations-grid': {
                    itemdblclick: this.onExplanationRowClick
                },
                'explanations-grid button[action=add-explanation]': {
                    click: this.onAddExplanationBtnClick
                },
                'explanations-grid button[action=delete-explanation]': {
                    click: this.onDeleteExplanationBtnClick
                },
                'explanation-editor-dlg button[action=save-explanation]': {
                    click: this.onExplanationSaveBtnClick
                },
                'explanation-editor-dlg button[action=reset-explanation]': {
                    click: this.onExplanationResetBtnClick
                },
                'explanation-editor-dlg button[action=cancel-explanation]': {
                    click: this.onExplanationCancelBtnClick
                },
                'custom-fields-grid': {
                    itemdblclick: this.onProcessFieldRowClick
                },
                'custom-fields-grid button[action=add-process-field]': {
                    click: this.onAddProcessFieldBtnClick
                },
                'custom-fields-grid button[action=delete-process-field]': {
                    click: this.onDeleteProcessFieldBtnClick
                },
                'process-field-editor-dlg': {
                    loadFields: this.onProcessFieldLoadRNFields
                },
                'process-field-editor-dlg button[action=save-process-field]': {
                    click: this.onProcessFieldSaveBtnClick
                },
                'process-field-editor-dlg button[action=reset-process-field]': {
                    click: this.onProcessFieldResetBtnClick
                },
                'process-field-editor-dlg button[action=cancel-process-field]': {
                    click: this.onProcessFieldCancelBtnClick
                },
                'process-field-editor-dlg opf-combo[marker=registryNodeFieldCombo]': {
                    select: this.onProcessFieldComboSelect
                }
            }
        )
    },

    onProcessFieldsStoreLoad: function(store, records) {
        if (records.length > 0) {
            var entityId = records[0].data.registryNodeTypeId;
            Ext.Ajax.request({
                url: OPF.core.utils.RegistryNodeType.ENTITY.generateGetUrl(entityId),
                method: 'GET',
                success: function(response, action) {
                    response = Ext.decode(response.responseText);
                    if (Ext.isBoolean(response.success) && response.success &&
                        OPF.isNotEmpty(response.data) && Ext.isArray(response.data) && response.data.length > 0) {
                        var customFieldsGrid = OPF.Ui.getCmp('custom-fields-grid');
                        var dropPanel = customFieldsGrid.editPanel.processEntityDropPanel;

                        dropPanel.entityFields = response.data[0].fields;
                        dropPanel.draggableNodeName = response.data[0].name;
                        dropPanel.setValue(response.data[0].id);
                        var lookup = response.data[0].lookup;
                        var iconCls = 'tricon-entity';
                        if (response.data[0].entityType == 'Classifier') {
                            iconCls = 'tricon-abstract-entity';
                        }
                        if (response.data[0].entityType == 'Data') {
                            iconCls = 'tricon-type-entity';
                        }
                        var description = cutting(response.data[0].description, 70);
                        dropPanel.renderDraggableEntity(iconCls, dropPanel.draggableNodeName, description, lookup);
                    } else {
                        Ext.Msg.alert('Error',
                            OPF.isEmpty(response.message) ? "Error response returned." : response.message);
                    }
                }
            });

        }
    },

    onAddStatusBtnClick: function(btn) {
        var statusesGrid = OPF.Ui.getCmp('statuses-grid');
        statusesGrid.getEditorDialog().startEditing(
            statusesGrid.editPanel.nodeBasicFields.lookupField.getValue());
    },

    onDeleteStatusBtnClick: function(btn) {
        var statusesGrid = OPF.Ui.getCmp('statuses-grid');
        var modelsToRemove = statusesGrid.selModel.getSelection();
        if (!modelsToRemove) {
            return false;
        }
        statusesGrid.store.remove(modelsToRemove);
    },

    onActivityRowClick: function(gridView, model, htmlElement, rowIndex, e, eOpts) {
        var activitiesGrid = OPF.Ui.getCmp('activities-grid');
        activitiesGrid.getEditorDialog().startEditing(
            activitiesGrid.editPanel.nodeBasicFields.lookupField.getValue(), rowIndex);
    },

    onAddActivityBtnClick: function(btn) {
        var activitiesGrid = OPF.Ui.getCmp('activities-grid');
        activitiesGrid.getEditorDialog().startEditing(
            activitiesGrid.editPanel.nodeBasicFields.lookupField.getValue());
    },

    onDeleteActivityBtnClick: function(btn) {
        var activitiesGrid = OPF.Ui.getCmp('activities-grid');
        var modelsToDelete = activitiesGrid.selModel.getSelection();
        if (!modelsToDelete) {
            return false;
        }
        activitiesGrid.store.remove(modelsToDelete);
    },

    onActivitySaveBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('activity-editor-dlg');
        var model;

        var actorId = dialog.actorField.getValue();
        var actorComboRec = this.getActivityActorsStore().getById(actorId);
        var actorName = actorComboRec.data.name;
        var statusId = dialog.statusField.getValue();
        var statusName;

        var statusStore = dialog.grid.editPanel.statusesFieldSet.statusesGrid.store;

        if (typeof statusId == 'number') { // selection of one of the statuses made
            var statusGridRec;
            if (statusId > 0) {
                statusGridRec = statusStore.getById(statusId);
                statusName = statusGridRec.data.name;
            } else {
                var statusRecIndex = statusStore.findExact('sortPosition', -statusId);
                statusGridRec = statusStore.getAt(statusRecIndex);
                statusName = statusGridRec.data.name;
            }
        } else { // new status to be created with the typed in name
            var nextSortPosition = statusStore.getCount() + 1; // add record at the end
            var statusModel = Ext.create('OPF.console.domain.model.StatusModel');
            statusModel.set('name', statusId); // if user typed in text instead of selecting predefined value, this is that text
            statusModel.set('path', dialog.pathField.getValue()); // same path as activity's path
            statusModel.set('lookup', calculateLookup(dialog.pathField.getValue(), statusId));
            statusModel.set('description', '');
            statusModel.set('sortPosition', nextSortPosition);
            statusModel.commit();

            statusStore.add(statusModel);
            statusName = statusId; // if user typed in text instead of selecting predefined value, this is that text
            statusId = - nextSortPosition; // id is calculated as negative order for new status records
        }

        model = OPF.isEmpty(dialog.rowIndex) ?
            Ext.create('OPF.console.domain.model.ActivityModel') :
            dialog.grid.store.getAt(dialog.rowIndex);
        model.set('path', dialog.pathField.getValue());
        model.set('name', dialog.nameField.getValue());
        model.set('lookup', dialog.lookupField.getValue());
        model.set('description', dialog.descriptionField.getValue());
        model.set('actorId', actorId);
        model.set('actorName', actorName);
        model.set('statusId', statusId);
        model.set('statusName', statusName);
        model.set('activityType', dialog.activityTypeField.getValue());
        model.set('notify', dialog.notifyField.getValue());
        if (OPF.isEmpty(dialog.rowIndex)) {
            model.commit();
        }

        this.onActivityCancelBtnClick();
        if (OPF.isEmpty(dialog.rowIndex)) {
            dialog.grid.store.add(model);
        }
        dialog.grid.refreshOrderPositions();
    },

    onActivityResetBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('activity-editor-dlg');
        dialog.fillEditor(dialog.baseLookup, dialog.rowIndex);
    },

    onActivityCancelBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('activity-editor-dlg');
        dialog.hide();
    },

    onActivityNameFieldChange: function(field) {
        var dialog = OPF.Ui.getCmp('activity-editor-dlg');
        dialog.updateLookup();
    },

    onStatusNameFieldChange: function(field) {
        var dialog = OPF.Ui.getCmp('status-editor-dlg');
        dialog.updateLookup();
    },

    onStatusSaveBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('status-editor-dlg');
        var model;
        if (OPF.isNotEmpty(dialog.rowIndex)) {
            model = dialog.grid.store.getAt(dialog.rowIndex);
            model.set('path', dialog.pathField.getValue());
            model.set('name', dialog.nameField.getValue());
            model.set('lookup', dialog.lookupField.getValue());
            model.set('description', dialog.descriptionField.getValue());
            this.onStatusCancelBtnClick(btn);
        } else {
            model = Ext.create('OPF.console.domain.model.StatusModel');
            model.set('path', dialog.pathField.getValue());
            model.set('name', dialog.nameField.getValue());
            model.set('lookup', dialog.lookupField.getValue());
            model.set('description', dialog.descriptionField.getValue());
            model.commit();
            this.onStatusCancelBtnClick(btn);
            dialog.grid.store.add(model);
        }
        dialog.grid.refreshOrderPositions();
    },

    onStatusResetBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('status-editor-dlg');
        dialog.fillEditor(dialog.baseLookup, dialog.rowIndex);
    },

    onStatusCancelBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('status-editor-dlg');
        dialog.hide();
    },

    onStatusRowClick: function(gridView, model, htmlElement, rowIndex, e, eOpts) {
        var statusesGrid = OPF.Ui.getCmp('statuses-grid');
        statusesGrid.getEditorDialog().startEditing(
            statusesGrid.editPanel.nodeBasicFields.lookupField.getValue(), rowIndex);
    },

    onExplanationRowClick: function(gridView, model, htmlElement, rowIndex, e, eOpts) {
        var explanationsGrid = OPF.Ui.getCmp('explanations-grid');
        explanationsGrid.getEditorDialog().startEditing(
            explanationsGrid.editPanel.nodeBasicFields.lookupField.getValue(), rowIndex);
    },

    onAddExplanationBtnClick: function(btn) {
        var explanationsGrid = OPF.Ui.getCmp('explanations-grid');
        explanationsGrid.getEditorDialog().startEditing(
            explanationsGrid.editPanel.nodeBasicFields.lookupField.getValue());
    },

    onDeleteExplanationBtnClick: function(btn) {
        var explanationsGrid = OPF.Ui.getCmp('explanations-grid');
        var modelsToDelete = explanationsGrid.selModel.getSelection();
        if (!modelsToDelete) {
            return false;
        }
        explanationsGrid.store.remove(modelsToDelete);
    },

    onExplanationSaveBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('explanation-editor-dlg');
        var editMode = OPF.isNotEmpty(dialog.rowIndex);
        var model = editMode ? dialog.grid.store.getAt(dialog.rowIndex) :
            Ext.create('OPF.console.domain.model.CaseExplanationModel');
        model.set('shortDescription', dialog.shortDescriptionField.getValue());
        model.set('longDescription', dialog.longDescriptionField.getValue());
        if (editMode) {
            this.onExplanationCancelBtnClick();
        } else {
            model.commit();
            this.onExplanationCancelBtnClick();
            dialog.grid.store.add(model);
        }
    },

    onExplanationResetBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('explanation-editor-dlg');
        dialog.fillEditor(dialog.baseLookup, dialog.rowIndex);
    },

    onExplanationCancelBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('explanation-editor-dlg');
        dialog.hide();
    },

    onProcessFieldRowClick: function(gridView, model, htmlElement, rowIndex, e, eOpts) {
        var processFieldsGrid = OPF.Ui.getCmp('custom-fields-grid');
        var entityId = processFieldsGrid.editPanel.processEntityDropPanel.getValue();
        var entityName = processFieldsGrid.editPanel.processEntityDropPanel.draggableNodeName;
        var entityFields = processFieldsGrid.editPanel.processEntityDropPanel.entityFields;
        if (!entityName) {
            Ext.Msg.alert('Error', 'Please select an entity by dragging it to the box above.');
            return;
        }
        processFieldsGrid.getEditorDialog().startEditing(entityId, entityFields, rowIndex);
    },

    onAddProcessFieldBtnClick: function(btn) {
        var customFieldsGrid = OPF.Ui.getCmp('custom-fields-grid');
        var entityId = customFieldsGrid.editPanel.processEntityDropPanel.getValue();
        var entityName = customFieldsGrid.editPanel.processEntityDropPanel.draggableNodeName;
        var entityFields = customFieldsGrid.editPanel.processEntityDropPanel.entityFields;
        if (!entityName) {
            Ext.Msg.alert('Error', 'Please select an entity by dragging it to the box above.');
            return;
        }
        customFieldsGrid.getEditorDialog().startEditing(entityId, entityFields);
    },

    onDeleteProcessFieldBtnClick: function(btn) {
        var customFieldsGrid = OPF.Ui.getCmp('custom-fields-grid');
        var models = customFieldsGrid.getSelectionModel().getSelection();
        if (!models) {
            return false;
        }
        this.getProcessFieldsStore().remove(models);
    },

    onProcessFieldSaveBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('process-field-editor-dlg');
        var model = OPF.isEmpty(dialog.rowIndex) ?
            Ext.create('OPF.console.domain.model.ProcessFieldModel') :
            dialog.grid.store.getAt(dialog.rowIndex);
        model.set('name', dialog.nameField.getValue());
        model.set('valueType', dialog.valueTypeField.getValue());
        model.set('fieldId', dialog.registryNodeFieldCombo.getValue());
        model.set('global', dialog.globalField.getValue());
        model.set('format', dialog.formatField.getValue());
        model.set('registryNodeTypeId', dialog.entityId);
        this.onProcessFieldCancelBtnClick();
        if (OPF.isEmpty(dialog.rowIndex)) {
            model.commit();
            dialog.grid.store.add(model);
        }
        dialog.grid.refreshOrderPositions();
    },

    onProcessFieldResetBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('process-field-editor-dlg');
        dialog.fillEditor(dialog.baseLookup, dialog.rowIndex);
    },

    onProcessFieldCancelBtnClick: function(btn) {
        var dialog = OPF.Ui.getCmp('process-field-editor-dlg');
        dialog.hide();
    },

    onProcessFieldLoadRNFields: function(entityFields) {
        if (OPF.isNotEmpty(entityFields) && Ext.isArray(entityFields)) {
            var models = [];
            Ext.each(entityFields, function(entityField, index) {
                var model = Ext.create('OPF.console.domain.model.FieldModel');
                model.set('id', entityField.id);
                model.set('name', entityField.name);
                model.set('path', entityField.path);
                model.set('lookup', entityField.lookup);
                model.set('parentId', entityField.parentId);
                model.set('customFieldType', entityField.customFieldType);
                model.set('fieldType', entityField.fieldType);
                model.set('defaultValue', entityField.defaultValue);

                models.push(model);
            });
            this.getRegistryNodeFieldsStore().loadData(models);
        }
    },

    onProcessFieldComboSelect: function(combo, selectedModels) {
        var processFieldEditorDialog = OPF.Ui.getCmp('process-field-editor-dlg');
        var model = selectedModels[0];
        processFieldEditorDialog.nameField.setValue(model.get('name'));
        var fieldType = processFieldEditorDialog.getMatchingProcessFieldType(model.get('fieldType'));
        processFieldEditorDialog.valueTypeField.setValue(fieldType);
        processFieldEditorDialog.formatField.setValue('');
        processFieldEditorDialog.setFormatComboValues(fieldType);
    }

});