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
Ext.define('OPF.console.resource.view.resource.ImageResourceEditor', {
    extend: 'OPF.console.resource.view.resource.BaseResourceEditor',
    alias: 'widget.image-resource-editor',

    title: 'IMAGE: [New]',

    infoResourceLookup: 'net.firejack.platform.content.abstract-resource.resource.image-resource',

    maxImageWidth: 200,
    maxImageHeight: 200,

    noImageDefined: '<font title="empty-image" color="#c0c0c0">No Image Defined</font>',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.imageWidth = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageWidth'
        });

        this.imageHeight = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageHeight'
        });

        this.resourceFileTemporaryName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileTemporaryName'
        });

        this.resourceFileOriginalName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileOriginalName'
        });

        this.resourceVersionTitleField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            labelAlign: 'top',
            fieldLabel: 'Title',
            subFieldLabel: '',
            name: 'resourceVersionTitle',
            emptyText: 'Enter some title...'
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                instance.resourceFileOriginalName.setValue(data.orgFilename);
                instance.resourceFileTemporaryName.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                instance.imageWidth.setValue(data.width);
                instance.imageHeight.setValue(data.height);
                instance.loadImageContainer(url, data.width, data.height);
                instance.infoUpload.show();
                instance.hide();
            },
            failureUploaded : function(jsonData, action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
                instance.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Processed file on the server');
            }
        });

        this.imageContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            width: 200,
            height: 200,
            html: this.noImageDefined
        });

        this.showUploadDialogButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'upload image',
            itemId: 'upload-image-btn',
            handler: function () {
                instance.uploadFileDialog.show();
            }
        });

        this.infoUpload = Ext.ComponentMgr.create({
            xtype: 'container',
            html: '<div style="color: #FF8F8F; font-weight: bold; font-size: 12px;">This image is not saved</div>',
            hidden: true
        });

        this.additionalFieldSet = Ext.ComponentMgr.create({
            xtype: 'label-container',
            fieldLabel: 'Image',
            subFieldLabel: '',
            layout: 'anchor',
            items: [
                this.imageWidth,
                this.imageHeight,
                this.resourceFileTemporaryName,
                this.resourceVersionTitleField,
                this.resourceFileOriginalName,
                {
                    xtype: 'label-container',
                    fieldLabel: 'Image',
                    subFieldLabel: '',
                    cls: '',
                    labelCls: '',
                    labelMargin: '0 0 5 0',
                    border: false,
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'panel',
                            border: true,
                            width: 208,
                            height: 208,
                            items: [
                                this.imageContainer
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
        this.resourceVersionTitleField.setValue(jsonData.resourceVersion.title);
        this.resourceFileOriginalName.setValue(jsonData.resourceVersion.resourceFileOriginalName);
        if (isNotEmpty(jsonData.id) && isNotEmpty(jsonData.resourceVersion.id)) {
            this.imageWidth.setValue(jsonData.resourceVersion.width);
            this.imageHeight.setValue(jsonData.resourceVersion.height);
            var urlSuffix = 'content/resource/image/by-filename/' + jsonData.id + '/' +
                jsonData.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
            var url = OPF.Cfg.restUrl(urlSuffix);
            this.loadImageContainer(url, jsonData.resourceVersion.width, jsonData.resourceVersion.height);
            this.infoUpload.hide();
        }
    },

    onBeforeSpecificDataSave: function(formData) {
        formData.resourceVersion.title = formData.resourceVersionTitle;
        formData.resourceVersion.width = formData.imageWidth;
        formData.resourceVersion.height = formData.imageHeight;
        formData.resourceVersion.resourceFileTemporaryName = formData.resourceFileTemporaryName;
        formData.resourceVersion.resourceFileOriginalName = formData.resourceFileOriginalName;
        delete formData.resourceVersionTitle;
        delete formData.resourceFileTemporaryName;
        delete formData.imageWidth;
        delete formData.imageHeight;
        delete formData.resourceFileOriginalName;
    },

    onReloadResourceVersionFailure: function() {
        this.imageWidth.setValue(null);
        this.imageHeight.setValue(null);
        this.resourceVersionTitleField.setValue('');
        this.resourceFileTemporaryName.setValue(null);
        this.resourceFileOriginalName.setValue(null);
        this.imageContainer.update(this.noImageDefined);
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var width = origWidth;
        var height = origHeight;
        if (origWidth > this.maxImageWidth || origHeight > this.maxImageHeight) {
            var widthCoof = origWidth / this.maxImageWidth;
            var heightCoof = origHeight / this.maxImageHeight;
            if (widthCoof > heightCoof) {
                width = origWidth / widthCoof;
                height = origHeight / widthCoof;
            } else if (widthCoof < heightCoof) {
                width = origWidth / heightCoof;
                height = origHeight / heightCoof;
            } else {
                width = origWidth / widthCoof;
                height = origHeight / heightCoof;
            }
        }
        var leftMargin = (this.maxImageWidth - width) / 2;
        var topMargin = (this.maxImageHeight - height) / 2;
        console.log('Image URL: ' + url);
        var imageHtml = '<img src="' + url + '" ' +
                             'width="' + width + '" ' +
                             'height="' + height + '" ' +
                             'style="margin-left:' + leftMargin + 'px; margin-top:' + topMargin + 'px;"/>';
        this.imageContainer.update(imageHtml);
    },

    onSuccessDeleteResourceVersion: function() {
        this.resourceVersionTitleField.setValue('');
        this.imageContainer.update(this.noImageDefined);
    }

});





