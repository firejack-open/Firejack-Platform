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