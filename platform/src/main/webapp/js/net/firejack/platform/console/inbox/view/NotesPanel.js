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

Ext.define('OPF.console.inbox.view.NotesPanel', {
    extend: 'OPF.console.inbox.view.DetailsCardPanel',

    alias: 'widget.notes-panel',

    title: 'Notes',
    fieldLabel: 'Notes',
    id: 'case-notes-panel',
    layout: 'border',
    flex: 1,

    rowIndex: null,

    initComponent: function() {
        this.addNoteBtn = OPF.Ui.createBtn('Add', 50, 'add-note', {iconCls: 'silk-add'});
        this.deleteNoteBtn = OPF.Ui.createBtn('Delete', 60, 'delete-note', {iconCls: 'silk-delete'});
        this.noteGrid = Ext.create('Ext.grid.Panel', {
            autoExpandColumn: 'note',
            store: 'CaseNotes',
            selModel: Ext.create('Ext.selection.RowModel', {mode: 'SINGLE'}),
            columns: [
                OPF.Ui.populateColumn('text', 'Note', {width: 250}),
                OPF.Ui.populateColumn('user', 'User', {width: 100, renderer: function(val) {
                    return val.username
                }}),
                OPF.Ui.populateDateColumn('created', 'Created', 100)
            ],
            bbar: [
                { xtype: 'tbfill' },
                this.addNoteBtn,
                this.deleteNoteBtn
            ]
        });

        this.fieldNoteId = OPF.Ui.hiddenField('id');
        this.fieldTaskId = OPF.Ui.hiddenField('taskId');
        this.fieldCaseId = OPF.Ui.hiddenField('processCaseId');
        this.fieldUsername = OPF.Ui.displayField('username', null, {value: '', hideLabel: true});
        this.fieldCreated = OPF.Ui.displayField('created', null, {value: '', hideLabel: true});
        this.fieldNote = OPF.Ui.textFormArea('text', 'Note', {disabled: true, hideLabel: true, height: 80});

        this.saveNoteBtn = OPF.Ui.createBtn('Save', 50, 'save-note', {cls: 'saveButton'});
        this.cancelNoteBtn = OPF.Ui.createBtn('Cancel', 50, 'cancel', {cls: 'cancelButton'});

        this.form = Ext.create('Ext.form.Panel', {
            width: 200,
            padding: 10,
            labelAlign: 'top',
            items: [
                this.fieldNoteId,
                this.fieldTaskId,
                this.fieldCaseId,
                this.fieldUsername,
                this.fieldCreated,
                this.fieldNote
            ],
            bbar: [
                { xtype: 'tbfill' },
                this.saveNoteBtn,
                this.cancelNoteBtn
            ]
        });

        this.items = [
            {
                xtype: 'panel',
                layout: 'fit',
                border: false,
                region: 'west',
                split: true,
                width: 400,
                items: this.noteGrid
            }, {
                xtype: 'panel',
                layout: 'fit',
                border: false,
                region: 'center',
                items: this.form
            }
        ];

        this.callParent(arguments);
    },

    fillingEditor: function(rowIndex) {
        this.form.getEl().unmask();
        var constraints = Ext.create('OPF.core.validation.FormInitialisation', 'OPF.process.CaseNote');
        constraints.initConstraints(this.form, null, true);

        this.rowIndex = rowIndex;
        if (OPF.isNotEmpty(rowIndex)) {
            var noteModel = this.noteGrid.store.getAt(rowIndex);
            var created = noteModel.get('created');
            if (created instanceof Date) {
                created = Ext.Date.format(created, 'm/d/Y');
            }
            this.fieldCreated.setValue(created);
            this.fieldNoteId.setValue(noteModel.get('id'));
            this.fieldTaskId.setValue(OPF.isEmpty(noteModel.get('task')) ? null : noteModel.get('task').id);
            this.fieldCaseId.setValue(OPF.isEmpty(noteModel.get('processCase')) ? null : noteModel.get('processCase').id);
            this.fieldUsername.setValue(noteModel.get('user').username);
            this.fieldNote.setValue(noteModel.get('text'));

            this.fieldNote.enable();
            if (noteModel.get('user').id == OPF.Cfg.USER_INFO.id) { // can edit only own notes
                this.deleteNoteBtn.enable();
                this.fieldNote.setReadOnly(false);
                this.saveNoteBtn.enable();
                this.cancelNoteBtn.enable();
            } else {
                this.deleteNoteBtn.disable();
                this.fieldNote.setReadOnly(true);
                this.saveNoteBtn.disable();
                this.cancelNoteBtn.disable();
            }
        } else { // add new note
            this.clean();
            this.fieldNote.enable();
            this.fieldNote.setReadOnly(false);
            this.saveNoteBtn.enable();
            this.cancelNoteBtn.enable();
        }
    },

    fillAdditionalFormData: function(formData) {
        // do nothing here, overridden in millennial
    },

    save: function() {
        var instance = this;

        this.form.getEl().mask();
        var formData = this.form.getForm().getValues();
        formData.user = { id: OPF.Cfg.USER_INFO.id };
        formData.processCase = OPF.isBlank(this.fieldCaseId.getValue()) ? null : {id : this.fieldCaseId.getValue()};
        formData.task = OPF.isBlank(this.fieldTaskId.getValue()) ? null : {id : this.fieldTaskId.getValue()};
        delete formData.taskId;
        delete formData.processCaseId;
        delete formData.username;

        /*var noteId = this.fieldNoteId.getValue();
        var requestData = OPF.isBlank(noteId) ? {} : {id : noteId};
        requestData.user = { id: OPF.Cfg.USER_INFO.id };
        requestData.processCase = OPF.isBlank(this.fieldCaseId.getValue()) ? null : {id : this.fieldCaseId.getValue()};
        requestData.task = OPF.isBlank(this.fieldTaskId.getValue()) ? null : {id : this.fieldTaskId.getValue()};
        requestData.text = this.fieldNote.getValue();
        requestData.created = this.fieldCreated.getValue();*/

        var url = OPF.core.utils.RegistryNodeType.CASE_NOTE.generateUrl(OPF.isBlank(formData.id) ? '' : formData.id);
        var method = OPF.isBlank(formData.id) ? 'POST' : 'PUT';
        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": formData},
//            jsonData: {"data": requestData},

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, resp.message);
                var noteData = resp.data[0];

                instance.form.getEl().unmask();

                instance.fieldNoteId.setValue(noteData.id);

                var created = sqFormattedDate(sqMillisecondToDate(noteData.created));
                instance.fieldCreated.setValue(created);

                var model;
                if (OPF.isEmpty(instance.rowIndex)) {
                    model = Ext.create('OPF.console.inbox.model.CaseNoteModel', noteData);
                    var position = instance.noteGrid.store.data.length;
                    instance.noteGrid.store.insert(position, model);
                    instance.noteGrid.selModel.select(position);
                    instance.rowIndex = position;
                } else {
                    model = instance.noteGrid.store.getAt(instance.rowIndex);
                    model.set('text', noteData.text);
                }
                instance.clean();
            },

            failure:function(response) {
                var options = Ext.create('OPF.core.validation.FormInitialisationOptions', {
                    show: true,
                    asPopup: true
                });
                OPF.core.validation.FormInitialisation.showValidationMessages(response, instance.form, options);
                instance.form.getEl().unmask();
            }
        });
    },

    clean: function() {
        this.rowIndex = null;
        if (this.noteGrid.getSelectionModel().hasSelection()) {
            this.noteGrid.getSelectionModel().clearSelections();
        }
        this.deleteNoteBtn.disable();
        var created = Ext.Date.format(new Date(), 'm/d/Y');// sqFormattedDate();
        this.fieldCreated.setValue(created);
        this.fieldNoteId.setValue(null);
        this.fieldUsername.setValue(OPF.Cfg.USER_INFO.username);
        this.fieldNote.setValue('');
        this.fieldTaskId.setValue(this.taskId);
        this.fieldCaseId.setValue(this.caseId);
        this.fieldNote.disable();
        this.saveNoteBtn.disable();
        this.cancelNoteBtn.disable();
    },

    cancel: function() {
        this.clean();
    },

    deleteRecord: function() {
        var instance = this;

        var selectedModels = this.noteGrid.selModel.getSelection();
        if (!selectedModels) {
            return false;
        }
        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.CASE_NOTE.generateDeleteUrl(selectedModels[0].get('id')),
            method: 'DELETE',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(resp.success, resp.message);

                instance.clean();
                instance.noteGrid.store.remove(selectedModels);
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error response for delete request.');
            }
        });
    },

    refreshPanel: function(taskId, caseId) {
        this.clean();

        this.taskId = taskId;
        this.caseId = caseId;
        
        if (OPF.isEmpty(taskId) && OPF.isEmpty(caseId)) {
            this.addNoteBtn.disable();
            this.deleteNoteBtn.disable();
            Ext.StoreManager.lookup('CaseNotes').removeAll();
        } else {
            this.addNoteBtn.enable();
            Ext.StoreManager.lookup('CaseNotes').load();
        }
    },

    refreshPanelData: function(parentPanel) {
        this.refreshPanel(parentPanel.taskId, parentPanel.caseId);
    }

});