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

/**
 * UploadFileDialog allows user to upload a new file
 */
Ext.define('OPF.core.component.UploadFileDialog', {
    extend: 'Ext.window.Window',

    modal: true,
    closable: true,
    closeAction: 'hide',
    hideMode: 'display',
    resizable: false,
    width: 400,

    packageId: null,
    fileTypes: [],
    editPanel: null,

    additionalFields: [],

    fileLabelAlign: 'top',
    fileLabelWidth: 100,

    uploadUrl: null,

    configs: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        OPF.core.component.UploadFileDialog.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.uploadButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'Upload',
            formBind: true,
            handler: function() {
                instance.uploadFile();
            }
        }, this.configs.uploadButtonConfigs));

        this.cancelButton = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'button',
            text: 'Cancel',
            handler: function() {
                Ext.MessageBox.confirm("Cancel Action", "All changes will be lost, are you sure?",
                    function(btn) {
                        if ( btn[0] == 'y' ) {
                            instance.hide();
                        }
                    }
                );
            }
        }, this.configs.cancelButtonConfigs));

        this.fileUploadField = Ext.ComponentMgr.create(Ext.apply({
            xtype: 'opf-file',
            labelAlign: this.fileLabelAlign,
            fieldLabel: 'File',
            labelWidth: this.fileLabelWidth,
            subFieldLabel: '',
            name: 'file',
            anchor: '100%',
            fileTypes: this.fileTypes
        }, this.configs.fileUploadFieldConfigs));

        var formFields = [];
        Ext.each(this.additionalFields, function(additionalField, index) {
            formFields.push(additionalField);
        });
        formFields.push(this.fileUploadField);

        this.form = Ext.create('Ext.form.Panel', {
            url: this.uploadUrl,
            layout: 'anchor',
            padding: 5,
            border: false,
            monitorValid: true,
            header: false,
            items: formFields,
            ui: 'upload-form-ui',
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        this.uploadButton,
                        this.cancelButton
                    ]
                }
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },


    listeners: {
        show: function(dialog) {
//            dialog.form.getForm().reset();
            dialog.form.getForm().checkValidity();
        }
    },

    uploadFile: function() {
        var me = this;

        this.form.getEl().mask();

        this.form.getForm().submit({
            method: "POST",

            success: function(fr, action) {
                var jsonData = Ext.JSON.decode(action.response.responseText);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, jsonData.message);

                me.form.getEl().unmask();

                me.successUploaded(jsonData, action);
                me.hide();
            },

            failure: function(fr, action) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Processed file on the server failure.');

                me.form.getEl().unmask();

                me.failureUploaded(action);
            }
        });
    },

    successUploaded: function(jsonData, action) {
    },

    failureUploaded: function(action) {
    }
});