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

Ext.define('OPF.core.component.ImageContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf-image',

    fieldLabel: null,
    subFieldLabel: null,

    layout: 'anchor',

    imageWidth: null,
    imageHeight: null,

    isDelete: false,
    imageWidthField: null,
    imageHeightField: null,
    resourceFileTemporaryName: null,
    resourceFileOriginalName: null,

    allowDelete: true,

    initComponent: function() {
        var me = this;

        this.resourceLookupField = Ext.ComponentMgr.create({
            xtype: 'opf-hidden',
            name: this.name,
            listeners: {
                errorchange: function(field, error) {
                    if (me.imageContainer.el) {
                        if (OPF.isNotBlank(error)) {
                            me.imageContainer.addCls('opf-image-invalid');
                            me.imageContainer.addCls('x-form-invalid-field');
                            me.imageContainer.el.dom.setAttribute('data-errorqtip', error || '');
                        } else {
                            me.imageContainer.removeCls('opf-image-invalid');
                            me.imageContainer.removeCls('x-form-invalid-field');
                            me.imageContainer.el.dom.removeAttribute('data-errorqtip');
                        }
                    }
                }
            }
        });

        this.imageContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            html: 'Loading...',
            cls: 'opf-image',
            width: this.imageWidth,
            height: this.imageHeight,
            listeners: {
                render: function(component) {
                    component.getEl().on('click', function() {
                        me.resourceLookupField.validate();
                    });
                }
            }
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            title: 'Upload new image',
            ui: 'wizards',
            width: 540,
            frame: false,
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),
            configs: {
                fileUploadFieldConfigs: {
                    padding: 10,
                    buttonText: '',
                    buttonConfig: {
                        ui: 'upload',
                        width: 60,
                        height: 60,
                        iconCls: 'upload-icon'
                    }
                },
                uploadButtonConfigs: {
                    ui: 'blue',
                    width: 250,
                    height: 60
                },

                cancelButtonConfigs: {
                    ui: 'blue',
                    width: 250,
                    height: 60
                }
            },

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                me.isDelete = false;
                me.resourceFileOriginalName = data.orgFilename;
                me.resourceFileTemporaryName = data.filename;
                me.resourceLookupField.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                me.imageWidthField = data.width;
                me.imageHeightField = data.height;
                me.loadImageContainer(url, data.width, data.height);
            }
        });

        this.uploadButton = new Ext.Button({
            text: 'Upload new image',
            handler: function () {
                me.uploadFileDialog.show();
            }
        });

        this.deleteButton = new Ext.Button({
            text: 'Delete',
            hidden: !this.allowDelete,
            handler: function () {
                me.onDelete();
            }
        });

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            html:
                '<label class="x-form-item-label x-form-item-label-top">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>'
        });

        this.items = [
            this.label,
            this.resourceLookupField,
            this.imageContainer,
            {
                xtype: 'container',
                layout: 'hbox',
                items: [
                    this.uploadButton,
                    { xtype: 'tbspacer', width: 10 },
                    this.deleteButton
                ]
            }
        ];

        this.callParent(arguments);
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtmlWrapper(url, origWidth, origHeight, '', this.imageWidth - 4, this.imageHeight - 4);
        this.imageContainer.update(imageHtml);
    },

    getJsonData: function() {
        return {
            width: this.imageWidthField,
            height: this.imageHeightField,
            resourceFileTemporaryName: this.resourceFileTemporaryName,
            resourceFileOriginalName: this.resourceFileOriginalName
        };
    },

    onDelete: function() {
        this.imageContainer.update('Image not defined');
        this.resourceLookupField.setValue(null);
        this.isDelete = true;
    },

    clean: function() {
        this.isDelete = false;
        this.imageWidthField = null;
        this.imageHeightField = null;
        this.resourceFileTemporaryName = null;
        this.resourceFileOriginalName = null;
        this.imageContainer.update('Image not defined');
        this.idData.lookup = null;
        this.resourceLookupField.setValue(null);
        this.idData.version = null;
    },

    load: function(lookup) {
        var me = this;
        this.clean();
        if (OPF.isNotBlank(lookup)) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + lookup),
                method: 'GET',

                success: function(response){
                    var vo = Ext.decode(response.responseText);
//                    OPF.Msg.setAlert(vo.success, vo.message);

                    if (vo.success) {
                        var resourceVersion = vo.data[0].resourceVersion;
                        me.showImage(resourceVersion);
                    }
                },
                failure: function(response) {
                    var data = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(false, data.message);
                }
            });
        }
    },

    process: function() {
        var lookup;
        if (this.isDelete) {
            lookup = this.remove();
        } else {
            lookup = this.save();
        }
        return lookup;
    },

    save: function() {
        var me = this;

        var jsonData = this.getJsonData();

        if (OPF.isBlank(jsonData.resourceFileTemporaryName)) {
            return this.idData.lookup;
        }

        var url;
        var method;
        if (this.idData.lookup) {
            jsonData.resourceLookup = this.idData.lookup;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl('content/resource/image/version/by-lookup/' + this.idData.lookup);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl('content/resource/image/version/new-by-path/' + this.idData.path);
            method = 'POST';
        }

        jsonData.culture = 'AMERICAN';

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},
            async: false,

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
//                OPF.Msg.setAlert(vo.success, vo.message);

                if (vo.success) {
                    var resourceVersion = vo.data[0];
                    me.showImage(resourceVersion);
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while saving image.');
            }
        });

        return this.idData.lookup;
    },

    remove: function() {
        var me = this;
        if (this.idData.lookup) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.idData.lookup),
                method: 'DELETE',
                async: false,

                success: function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(vo.success, vo.message);
                    me.clean();
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, 'Error occurred while saving image.');
                }
            });
        }

        return null;
    },

    showImage: function(resourceVersion) {
        var urlSuffix = 'content/resource/image/by-filename/' + resourceVersion.resourceId + '/' + resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
        var url = OPF.Cfg.restUrl(urlSuffix);
        this.loadImageContainer(url, resourceVersion.width, resourceVersion.height);
        this.idData.lookup = resourceVersion.resourceLookup;
        this.resourceLookupField.setValue(resourceVersion.resourceLookup);
        this.idData.version = resourceVersion.version;
        this.imageWidthField = resourceVersion.width;
        this.imageHeightField = resourceVersion.height;
        this.resourceFileTemporaryName = null;
        this.resourceFileOriginalName = resourceVersion.resourceFileOriginalName;
    }

});
