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

Ext.define('OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageModel', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'type', type: 'string' },
        { name: 'resourceVersion', type: 'auto' }
    ]
});

Ext.define('OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageManagerDialog', {
    extend: 'Ext.window.Window',

    id: 'imageManagerDialog',
    cls: 'popup image-manager-dialog',
    title: 'Image Manager',
    modal: true,
    width: 750,
    height: 450,
    layout: {
        type: 'hbox',
        pack: 'start',
        align: 'stretch'
    },
    constrainHeader: true,
    closable: true,
    resizable: false,

    resourcePath: 'net.firejack.prometheus',

    constructor: function(ed, cfg) {
        cfg = cfg || {};
        OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageManagerDialog.superclass.constructor.call(this, Ext.apply({
            ed: ed
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/new-by-path/' + this.resourcePath + '/IMAGE'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                me.imageStore.load();
            }
        });

        this.imageStore = Ext.create('Ext.data.Store', {
            model: 'OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageModel',
            proxy: {
                type: 'ajax',
                url : OPF.Cfg.restUrl('content/resource/by-parent-lookup/' + this.resourcePath + '?types=IMAGE'),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            }
        });
        this.imageStore.load();

        this.imageViewer = Ext.create('Ext.view.View', {
            store: this.imageStore,
            tpl: new Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="item-slider-image">',
                        '<div class="item-image-wrapper">',
                          '{[this.getImageHtml(values)]}',
                        '</div>',
                    '</div>',
                '</tpl>',
                {
                    getImageHtml: function(data) {
                        var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename;
                        return OPF.core.component.resource.ResourceControl.getImgHtmlWrapper(
                            OPF.Cfg.restUrl(urlSuffix), data.resourceVersion.width, data.resourceVersion.height, '', 80, 80);
                    }
                }
            ),
            itemSelector: 'div.item-slider-image',
            emptyText: 'No images available',
            listeners: {
                itemclick: function(view, record, item, index) {
                    if (me.selectedRecord == null || me.selectedRecord != record) {
                        me.selectedRecord = record;
                        me.deleteButton.enable();
                        Ext.each(Ext.query('.image-manager-dialog .item-slider-image.active'), function(item) {
                            Ext.fly(item).removeCls('active')
                        });
                        Ext.fly(item).addCls('active');

                        var data = record.data;
                        var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename;
                        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtmlWrapper(
                            OPF.Cfg.restUrl(urlSuffix), data.resourceVersion.width, data.resourceVersion.height, '', 200, 200);
                        me.imagePreviewContainer.update('<div class="preview-image-container">' + imageHtml + '</div>');
                        var imageDataInfo = '<span class="title"><b>Original Name:</b> ' + data.resourceVersion.resourceFileOriginalName + '</span>' +
                                            '<span class="title"><b>Size:</b> ' + data.resourceVersion.width + 'px X ' + data.resourceVersion.height + 'px</span>';
                        me.imagePreviewInfoContainer.update('<div class="preview-image-info-container">' + imageDataInfo + '</div>');
                    } else if (me.selectedRecord == record) {
                        Ext.fly(item).removeCls('active');
                        me.cleanPreviewContainer();
                    }
                },
                itemdblclick: function(view, record, item, index) {
                    var data = record.data;
                    var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename;
                    var url = OPF.Cfg.restUrl(urlSuffix);

                    var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, data.resourceVersion.width, data.resourceVersion.height, '');
                    me.ed.execCommand("mceInsertContent", false, imageHtml);
                    me.hide();
                }
            }
        });

        this.uploadButton = Ext.create('Ext.button.Button', {
            text: 'Upload',
            iconCls: 'icon-upload',
            scale: 'small',
            scope: this,
            handler: this.onUploadClick
        });

        this.deleteButton = Ext.create('Ext.button.Button', {
            text: 'Delete',
            iconCls: 'icon-delete',
            scale: 'small',
            scope: this,
            disabled: true,
            handler: this.onDeleteClick
        });

        this.imageViewerPanel = Ext.create('Ext.panel.Panel', {
            title: 'Image Explorer',
            autoScroll: true,
            flex: 1,
            items: [
                this.imageViewer
            ]
        });

        this.imagePreviewContainer = Ext.create('Ext.container.Container', {
            width: 228,
            height: 228
        });

        this.imagePreviewInfoContainer = Ext.create('Ext.container.Container', {
            width: 228
        });

        this.imagePreviewPanel = Ext.create('Ext.panel.Panel', {
            title: 'Preview',
            width: 230,
//            layout: 'fit',
            items: [
                this.imagePreviewContainer,
                this.imagePreviewInfoContainer
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        '->',
                        this.uploadButton,
                        this.deleteButton
                    ]
                }
            ]
        });

        this.items = [
            this.imageViewerPanel,
            this.imagePreviewPanel
        ];

        this.callParent(arguments);
    },

    cleanPreviewContainer: function() {
        this.selectedRecord = null;
        this.deleteButton.disable();
        this.imagePreviewContainer.update('');
        this.imagePreviewInfoContainer.update('');
    },

    onUploadClick: function() {
        this.uploadFileDialog.show();
    },

    onDeleteClick: function() {
        var me = this;

        Ext.MessageBox.confirm(
            'Deleting selected image',
            'Are you sure?',
            function(btn) {
                if (btn[0] == 'y') {
                    me.deleteRecord(me.selectedRecord);
                }
            }
        );

    },

    deleteRecord: function(record) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/image/' + record.get('id')),
            method: 'DELETE',
            jsonData: '[]',

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, vo.message);
                me.imageStore.remove(me.selectedRecord);
                me.cleanPreviewContainer();
            },

            failure: function(response) {
                if (response.status == 409) {
                    var jsonData = Ext.decode(response.responseText);
                    Ext.MessageBox.confirm('Deleting image', jsonData.message,
                        function(btn) {
                            if (btn[0] == 'y') {
                                Ext.Ajax.request({
                                    url: OPF.Cfg.restUrl('content/resource/image/' + record.get('id')) + '?force=true',
                                    method: 'DELETE',
                                    jsonData: '[]',

                                    success: function(response, action) {
                                        var vo = Ext.decode(response.responseText);
                                        OPF.Msg.setAlert(true, vo.message);
                                        me.imageStore.remove(me.selectedRecord);
                                        me.cleanPreviewContainer();
                                    },

                                    failure: function(response) {
                                        OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                                    }
                                });
                            }
                        }
                    );
                } else {
                    OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                }
            }
        });
    }

});
