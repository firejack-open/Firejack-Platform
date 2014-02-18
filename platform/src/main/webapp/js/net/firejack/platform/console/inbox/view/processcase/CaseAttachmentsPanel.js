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

Ext.define('OPF.console.inbox.view.processcase.CaseAttachmentUploadFileDialog', {
    extend: 'OPF.core.component.UploadFileDialog',
    
    alias: 'widget.case-attachments-dlg',

    title: 'Attach Files',

    uploadUrl: OPF.core.utils.RegistryNodeType.CASE_ATTACHMENT.generateUrl('/upload/'),

    initComponent: function() {
        this.fieldId = OPF.Ui.hiddenField('id');
        //this.fieldProcessCaseId = OPF.Ui.hiddenField('processCaseId');

        this.fieldName = Ext.create('Ext.form.field.Base', {
            name: 'name',
            value: '',
            fieldLabel: 'Name',
            width: 375
        });

        this.fieldDescription = OPF.Ui.textFormArea('description', 'Description', {height: 80, width: 375});

        this.additionalFields = [
            this.fieldId,
            //this.fieldProcessCaseId,
            this.fieldName,
            this.fieldDescription
        ];

        this.callParent(arguments);
    },

    uploadFile: function() {
        if (OPF.isEmpty(this.editPanel.rowIndex)) {
            this.uploadCaseAttachment();
        } else {
            this.updateCaseAttachment();
        }
    },

    uploadCaseAttachment: function() {
        var instance = this;

        this.form.getForm().submit({
            url: instance.uploadUrl + this.editPanel.caseId,
            waitMsg: 'Uploading your file...',
            method : "POST",

            success : function(fr, action) {
                var jsonData = Ext.decode(action.response.responseText);
                OPF.Msg.setAlert(true, jsonData.message);
                var data = jsonData.data[0];
                var caseAttachmentModel = Ext.create('OPF.console.inbox.model.CaseAttachmentModel', data);
                var position = instance.editPanel.grid.store.data.length;
                instance.editPanel.grid.store.insert(position, caseAttachmentModel);
                instance.editPanel.grid.selModel.select(position);
                instance.hide();
            },

            failure : function(fr, action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
            }
        });
    },

    updateCaseAttachment: function() {
        var instance = this;

        this.form.getEl().mask();

        var formData = this.form.getForm().getValues();

        var url = this.editPanel.urlPrefix + '/' + this.fieldId.getValue();

        Ext.Ajax.request({
            url: url,
            method: 'PUT',
            jsonData: {"data": formData},

            success:function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, vo.message);
                var data = vo.data;

                instance.form.getEl().unmask();

                instance.fieldId.setValue(data[0].id);

                var caseAttachmentModel = instance.editPanel.grid.store.getAt(instance.editPanel.rowIndex);
                caseAttachmentModel.set('name', data[0].name);
                caseAttachmentModel.set('description', data[0].description);

                instance.hide();
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

    showUploadDialog: function(model, isUpdate) {
        this.fileUploadField.enable();
        this.uploadButton.setText('Upload');
        if (OPF.isNotEmpty(model)) {
            this.fieldId.setValue(model.get('id'));
            this.fieldName.setValue(model.get('name'));
            this.fieldDescription.setValue(model.get('description'));
            this.fileUploadField.setValue(model.get('filename'));
            if (isUpdate) {
                this.uploadButton.setText('Save');
                this.uploadButton.enable();
                this.fileUploadField.disable();
            }
        }
        //this.fieldProcessCaseId.setValue(this.editPanel.caseId);
        this.show();
    },

    hide: function() {
        this.fieldId.setValue(null);
        //this.fieldProcessCaseId.setValue(null);
        this.fieldName.setValue(null);
        this.fieldDescription.setValue(null);
        this.fileUploadField.setValue(null);
        this.callParent(arguments);
    }

});

Ext.define('OPF.console.inbox.view.processcase.CaseAttachmentsPanel', {
    extend: 'OPF.console.inbox.view.DetailsCardPanel',

    alias: 'widget.case-attachments',
    
    //title: 'Attachments',
    fieldLabel: 'Attachments',
    id: 'case-attachments-panel',
    layout: 'fit',
    flex: 1,

    rowIndex: null,

    urlPrefix: OPF.core.utils.RegistryNodeType.CASE_ATTACHMENT.generateUrl(),

    initComponent: function() {
        var instance = this;

        this.uploadAttachmentBtn = OPF.Ui.createBtn('Upload', 60, 'upload', {iconCls: 'silk-add', disabled: true});
        this.deleteAttachmentBtn = OPF.Ui.createBtn('Delete', 60, 'delete', {iconCls: 'silk-delete', disabled: true});

        this.grid = Ext.create('Ext.grid.Panel', {
            border: false,
            autoExpandColumn: 'description',
            store: 'CaseAttachments',
            columns: [
                OPF.Ui.populateColumn('filename', 'Filename', {
                    width: 150,
                    renderer: function(v, meta, model, row_idx, col_idx, store) {
                        var downloadUrl = instance.urlPrefix + '/download/' + model.get('id');
                        var html = [
                            '<a href="', downloadUrl, '" class="case-attachment-download"/>', v, '</a>'
                        ];
                        return html.join('');
                    }
                }),
                OPF.Ui.populateColumn('name', 'Name', {width: 150}),
                OPF.Ui.populateColumn('description', 'Description', {renderer: 'htmlEncode'}),
                OPF.Ui.populateDateColumn('created', 'Created', {width: 100})
            ],
            bbar: [
                { xtype: 'tbfill' },
                this.uploadAttachmentBtn,
                this.deleteAttachmentBtn
            ]
        });

        this.uploadFileDialog = Ext.create(
            'OPF.console.inbox.view.processcase.CaseAttachmentUploadFileDialog', instance);

        this.items = this.grid;

        this.callParent(arguments);
    },

    generateReadUrl: function() {
        var url = this.urlPrefix;
        url += '/case/' + this.caseId;
        return url;
    },

    showUploadDialog: function(rowIndex) {
        var record = null;
        this.rowIndex = null;
        var isUpdate = OPF.isNotEmpty(rowIndex);
        if (isUpdate) {
            record = this.grid.store.getAt(rowIndex);
            this.rowIndex = rowIndex;
        }
        this.uploadFileDialog.showUploadDialog(record, isUpdate);
    },

    deleteRecord: function() {
        var instance = this;

        var selectedModels = this.grid.selModel.getSelection();
        if (!selectedModels) {
            return false;
        }
        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.CASE_ATTACHMENT.generateGetUrl(selectedModels[0].get('id')),
            method: 'DELETE',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, resp.message);

                instance.grid.store.remove(selectedModels);
            },

            failure:function(response) {

            }
        });
    },

    refreshPanel: function(caseId) {
        this.caseId = caseId;
        if (OPF.isEmpty(caseId)) {
            this.grid.store.removeAll();
            this.uploadAttachmentBtn.disable();
        } else {
            this.grid.store.load();
            this.uploadAttachmentBtn.enable();
        }
    },

    refreshPanelData: function(parentPanel) {
        this.refreshPanel(parentPanel.caseId);
    }

});