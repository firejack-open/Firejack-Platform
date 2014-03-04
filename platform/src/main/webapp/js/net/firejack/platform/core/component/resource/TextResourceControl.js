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

Ext.define('OPF.core.component.resource.TextResourceControl', {
    extend: 'Ext.container.Container',
    alias: 'widget.text-resource-control',

    innerCls: 'info-content',

    textResourceLookup: null,
    textInnerCls: 'info-text',
    maxTextLength: null,

    autoInit: false,
    allowEdit: true,

    initComponent: function() {
        var me = this;

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

        if (OPF.isNotEmpty(this.maxTextLength)) {
            this.innerTextCnt = Ext.create('OPF.core.component.resource.DisplayComponent', {
                autoEl: 'span',
                cls: this.textInnerCls,
                maxTextLength: this.maxTextLength,
                listeners: renderMouseOverListener,
                html: 'Loading...'
            });
        } else {
            this.innerTextCnt = Ext.create('Ext.container.Container', {
                autoEl: 'span',
                cls: this.textInnerCls,
                listeners: renderMouseOverListener,
                html: 'Loading...'
            });
        }

        this.items = [
            this.innerTextCnt
        ];

        if (OPF.Cfg.CAN_EDIT_RESOURCE && this.allowEdit) {
            this.editButton = Ext.ComponentMgr.create({
                xtype: 'hrefclick',
                cls: 'text-resource-edit hidden',
                html: '<img src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==" alt="edit" title="edit"/>',
                onClick: function() {
                    var winId = 'TextEditorWinId4582';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.core.component.resource.TextResourceEditor(winId, 'textarea');
                    }
                    window.setEditingEl(me.innerTextCnt);
                    window.setIdData(me.textIdData);
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
        return this.textResourceLookup;
    },

    loadResource: function(async) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/text/by-lookup/' + this.textResourceLookup),
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
        if (OPF.isNotEmpty(data)) {
            this.innerTextCnt.el.dom.innerHTML = data.resourceVersion.text;
            this.textIdData = {
                resourceId: data.id,
                version: data.resourceVersion.version,
                lookup: this.textResourceLookup
            };
        } else {
            this.innerTextCnt.el.dom.innerHTML = 'Not defined';
            this.textIdData = {
                resourceId: null,
                version: null,
                lookup: this.textResourceLookup
            };
        }
    }

});