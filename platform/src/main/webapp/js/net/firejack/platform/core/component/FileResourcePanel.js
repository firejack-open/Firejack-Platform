//@tag opf-console
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
Ext.define('OPF.core.component.FileResourcePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.file-resource-panel',

    layout: 'anchor',
    cls: 'file-resource-panel',

    fieldLabel: null,
    subFieldLabel: null,
    border: false,

    registryNodeType: null,

    readOnly: false,
    data: null,

    allowDelete: true,
    allowBlank: true,
    errorMessage: '{0} is required',
    parentId: null,

    initComponent: function() {
        var me = this;

        this.addEvents(
            'wsdlloaded',
            'wsdlsaved',
            'wsdlremoved'
        );

        this.voTemplate = new Ext.XTemplate(
            '<div class="vo-drop-panel">' +
                '<div class="vo-info">' +
                    '<div class="vo-name">' +
                        '<div class="vo-icon tricon-process"></div>' +
                        '<span>{name}</span>' +
                    '</div>' +
                    '<div class="vo-lookup">{lookup}</div>' +
                '</div>' +
                '<div class="vo-description">{[this.getDownloadLink(values)]}</div>' +
            '</div>',
            {
                getDownloadLink: function(resource) {
                    var result = '';
                    if (resource && resource.resourceVersion) {
                        var urlSuffix = 'content/resource/file/by-filename/' + resource.id + '/' + resource.resourceVersion.storedFilename + '?_dc=' + new Date().getTime();
                        var url = OPF.Cfg.restUrl(urlSuffix);
                        result = '<a href="' + url + '">' + resource.resourceVersion.resourceFileOriginalName + '</a>';
                    }
                    return result;
                }
            }
        );

        this.label = Ext.ComponentMgr.create({
            xtype: 'container',
            anchor: '100%',
            html:
                '<label class="x-form-item-label x-form-item-label-top">' +
                    '<span class="main-label">' + this.fieldLabel + ':</span>' +
                    '<span class="sub-label ">' + this.subFieldLabel + '</span>' +
                '</label>',
            margin: '0 0 5 0'
        });

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['.*?'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/file'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                me.saveResource(data)
            },
            failureUploaded : function(jsonData, action) {
                OPF.Msg.setAlert(false, 'Processed file on the server');
                me.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Processed file on the server');
            }
        });

        this.resourceFileContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            height: 50,
            anchor: '100%',
            cls: 'border-radius',
            border: false,
            tpl: this.voTemplate,
            listeners: {
                render: function(component) {
                    component.getEl().on('click', function() {
                        me.draggableNodeIdField.validate();
                    });
                }
            }
        });

        this.items = [
            this.label,
            this.resourceFileContainer
        ];

        this.uploadButton = Ext.ComponentMgr.create({
            xtype: 'button',
            tooltip: 'Upload File Resource',
            iconCls: 'silk-add',
            handler: me.uploadFileResource,
            scope: this,
            hidden: true
        });

        this.deleteButton = Ext.ComponentMgr.create({
            xtype: 'button',
            tooltip: 'Delete File Resource',
            iconCls: 'silk-delete',
            handler: me.deleteFileResource,
            scope: this,
            hidden: true
        });

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                items: [
                    '->',
                    this.uploadButton,
                    this.deleteButton
                ]
            }
        ];

        this.callParent(arguments);
    },

    listeners: {
        disable: function(panel) {
            panel.uploadButton.hide();
            panel.deleteButton.hide();
        },
        enable: function(panel) {
            panel.uploadButton.show();
            panel.deleteButton.show();
        }
    },

    loadResource: function(domainLookup) {
        var me = this;

        this.resourceLookup = domainLookup + '.wsdl';

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/resource/file/by-lookup/' + this.resourceLookup),
            method: 'GET',

            success: function(response){
                var responseData = Ext.decode(response.responseText);
                if (responseData.data) {
                    var data = responseData.data[0];
                    me.resourceData = responseData.data[0];
                    me.resourceFileContainer.update(data);
                    me.fireEvent('wsdlloaded', me, data);
                } else {
                    me.resourceFileContainer.update({
                        name: 'Not Found WSDL'
                    });
                }
            },
            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    saveResource: function(data) {
        var me = this;

        var jsonData = {
            parentId: this.parentId,
            name: 'wsdl',
            resourceVersion: {
                culture: 'AMERICAN',
                version: 1,
                resourceFileTemporaryName: data.filename,
                resourceFileOriginalName: data.orgFilename
            }
        };

        var url;
        var method;
        if (me.resourceData) {
            url = OPF.Cfg.restUrl('/content/resource/file/' + this.resourceData.id);
            method = 'PUT';
            jsonData.id = this.resourceData.id;
            jsonData.resourceVersion.id = this.resourceData.resourceVersion.id;
        } else {
            url = OPF.Cfg.restUrl('/content/resource/file');
            method = 'POST';
        }

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(responseData.success, responseData.message);
                if (responseData.success) {
                    me.resourceData = responseData.data[0];
                    me.resourceFileContainer.update(responseData.data[0]);
                    me.fireEvent('wsdlsaved', me, responseData);
                }
            },
            failure: function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while saving file.');
            }
        });
    },

    uploadFileResource: function() {
        this.uploadFileDialog.show();
    },

    deleteFileResource: function() {
        var me = this;

        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(this.resourceData.type);
        var url = registryNodeType.generateGetUrl(this.resourceData.id);
        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            jsonData: '[]',

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);
                me.resourceFileContainer.update({
                    name: 'Not Found WSDL'
                });
                me.fireEvent('wsdlremoved', me, responseData);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while deleting file.');
            }
        });
    }

});
