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
 *
 */
Ext.define('OPF.console.resource.view.resource.DocumentResourceEditor', {
    extend: 'OPF.console.resource.view.resource.BaseResourceEditor',
    alias: 'widget.document-resource-editor',

    title: 'DOCUMENT: [New]',

    infoResourceLookup: 'net.firejack.platform.content.abstract-resource.resource.document-resource',

    maxImageWidth: 200,
    maxImageHeight: 200,

    noContentDefined: '<span title="empty-document" class="resource-no-content">No Document Defined</span>',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.resourceFileTemporaryName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileTemporaryName'
        });

        this.resourceFileOriginalName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileOriginalName'
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['doc', 'xls', 'pdf'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/document'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                instance.resourceFileOriginalName.setValue(data.orgFilename);
                instance.resourceFileTemporaryName.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                instance.loadDocumentContainer(url);
                instance.infoUpload.show();
                instance.hide();
            },
            failureUploaded : function(jsonData, action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
                instance.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Processed file on the server');
            }
        });

        this.documentContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            width: 200,
            height: 200,
            html: this.noContentDefined
        });

        this.showUploadDialogButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'upload document',
            itemId: 'upload-document-btn',
            handler: function () {
                instance.uploadFileDialog.show();
            }
        });

        this.infoUpload = Ext.ComponentMgr.create({
            xtype: 'container',
            html: '<div style="color: #FF8F8F; font-weight: bold; font-size: 12px;">This document is not saved</div>',
            hidden: true
        });

        this.additionalFieldSet = Ext.ComponentMgr.create({
            xtype: 'label-container',
            fieldLabel: 'Document',
            subFieldLabel: '',
            layout: 'anchor',
            items: [
                this.resourceFileTemporaryName,
                this.resourceFileOriginalName,
                {
                    xtype: 'label-container',
                    fieldLabel: 'Document',
                    subFieldLabel: '',
                    cls: '',
                    labelCls: '',
                    labelMargin: '0 0 5 0',
                    border: false,
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'panel',
                            border: 1,
                            width: 208,
                            height: 208,
                            items: [
                                this.documentContainer
                            ]
                        },
                        {
                            xtype: 'panel',
                            border: false,
                            width: 180,
                            height: 208,
                            items: [
                                this.infoUpload
                            ]
                        }
                    ]
                },
                this.showUploadDialogButton
            ]
        });

        this.callParent(arguments);
    },

    onAfterSetSpecificValue: function(jsonData) {
        this.resourceFileOriginalName.setValue(jsonData.resourceVersion.resourceFileOriginalName);
        if (isNotEmpty(jsonData.id) && isNotEmpty(jsonData.resourceVersion.id)) {
            var urlSuffix = 'content/resource/document/by-filename/' + jsonData.id + '/' +
                jsonData.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
            var url = OPF.Cfg.restUrl(urlSuffix);
            this.loadDocumentContainer(url);
            this.infoUpload.hide();
        }
    },

    onBeforeSpecificDataSave: function(formData) {
        formData.resourceVersion.resourceFileTemporaryName = formData.resourceFileTemporaryName;
        formData.resourceVersion.resourceFileOriginalName = formData.resourceFileOriginalName;
        delete formData.resourceFileTemporaryName;
        delete formData.resourceFileOriginalName;
    },

    onReloadResourceVersionFailure: function() {
        this.resourceFileTemporaryName.setValue(null);
        this.resourceFileOriginalName.setValue(null);
        this.documentContainer.update(this.noContentDefined);
    },

    loadDocumentContainer: function(url) {
        var documentHtml = '<a href="' + url + '">download document</a>';
        this.documentContainer.update(documentHtml);
    },

    onSuccessDeleteResourceVersion: function() {
        this.documentContainer.update(this.noContentDefined);
    }

});





