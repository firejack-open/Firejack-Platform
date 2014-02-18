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

Ext.define('OPF.core.component.resource.ImageResourceControl', {
    extend: 'Ext.container.Container',
    alias: 'widget.image-resource-control',

    componentCls: null,
    cls: 'image-resource-control',
    innerCls: 'info-content',

    imgResourceLookup: null,
    imgInnerCls: 'info-image',
    imageWidth: null,
    imageHeight: null,

    autoInit: false,
    allowEdit: true,

    initComponent: function() {
        var me = this;

        this.cls = OPF.isNotBlank(this.componentCls) ? this.cls + ' ' + this.componentCls : this.cls;

        var renderMouseOverListener = {
            render: function(cnt) {
                if (OPF.Cfg.CAN_EDIT_RESOURCE && me.allowEdit) {
                    cnt.getEl().on({
                        'mouseover': function() {
                            me.editButton.addCls('active');
                        },
                        'mouseout': function() {
                            me.editButton.removeCls('active');
                        }
                    });
                }
            }
        };

//        this.innerLoadingCmp = new Ext.Component({
//            html: 'Loading...'
//        });

        this.innerImgCnt = Ext.create('Ext.container.Container', {
            autoEl: 'span',
            cls: this.imgInnerCls,
            listeners: renderMouseOverListener,
            html: 'Loading...'
        });

        this.items = [
//            this.innerLoadingCmp,
            this.innerImgCnt
        ];

        if (OPF.Cfg.CAN_EDIT_RESOURCE && this.allowEdit) {
            this.editButton = Ext.ComponentMgr.create({
                xtype: 'hrefclick',
                cls: 'image-resource-edit hidden',
                html: '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="edit" title="edit"/>',
                onClick: function() {
                    var winId = 'ImageEditorWinId9865';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.ImageResourceEditor(winId, {
                            imageWidth: me.imageWidth,
                            imageHeight: me.imageHeight
                        });
                    }
                    window.setEditingEl(me.innerImgCnt);
                    window.setIdData(me.imageIdData);
                    window.show();
                },
                listeners: renderMouseOverListener
            });
            this.items.push(this.editButton);
        }

        this.callParent(arguments);

    },

    listeners: {
        afterrender: function(container) {
            if (container.autoInit) {
                container.loadResource(true);
            }
        }
    },

    getResourceLookup: function() {
        return this.imgResourceLookup;
    },

    loadResource: function(async) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/image/by-lookup/' + this.imgResourceLookup),
            method: 'GET',
            async: async,

            success: function(response){
                var jsonData = Ext.decode(response.responseText);
                var data = null;
                if (jsonData.data) {
                    data = jsonData.data[0];
                }
                me.initResource(data);
            },
            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    initResource: function(data) {
        var imageHtml;
        var imageContainer;
        if (OPF.isNotEmpty(data)) {
            var urlSuffix = 'content/resource/image/by-filename/' + data.id + '/' + data.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
            var url = OPF.Cfg.restUrl(urlSuffix);

            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, this.imageWidth, this.imageHeight);
            this.innerImgCnt.el.dom.innerHTML = imageHtml;
            this.imageIdData = {
                resourceId: data.id,
                version: data.resourceVersion.version,
                lookup: this.imgResourceLookup
            };
        } else {
            imageHtml = OPF.core.component.resource.ResourceControl.getStubHtml('Image not defined', this.imageWidth, this.imageHeight);
            imageContainer = Ext.create('Ext.container.Container', {
                cls: 'test-image-container',
                html: imageHtml
            });
            this.innerImgCnt.add(imageContainer);
            this.imageIdData = {
                resourceId: null,
                version: null,
                lookup: this.imgResourceLookup
            };
        }
//        this.innerLoadingCmp.el.dom.innerHTML = '';
    }

});