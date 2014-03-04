/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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