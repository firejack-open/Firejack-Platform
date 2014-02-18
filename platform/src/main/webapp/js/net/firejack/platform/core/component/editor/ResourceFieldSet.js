//@tag opf-editor
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

Ext.ns('FJK.platform.clds.ui');


/**
 *
 */
Ext.define('OPF.core.component.editor.ResourceFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Resource Access',
    subFieldLabel: 'Where to reach these assets online (web addresses)',

    layout: 'anchor',

    editPanel: null,
    needMethodField: true,
    needParentPathField: true,
    needRDBMSField: false,
    needProtocolField: true,
    needUrlPathField: true,
    readOnlyInheritedFields: false,
    additionalLeftPanelControls: [],
    additionalRightPanelControls: [],

    parentPathFieldLabel: null,
    parentPathFieldSubLabel: null,
    parentPathFieldName: null,

    urlPathFieldLabel: null,
    urlPathFieldSubLabel: null,
    urlPathFieldName: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.ResourceFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.serverNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Server Name',
            subFieldLabel: 'Enter the registered name of the server or load balancer',
            flex: 2,
            name: 'serverName',
            emptyText: 'Enter DNS name of server...',
            readOnly: this.readOnlyInheritedFields,
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.updateResourceUrl();
                },
                keyup: function(cmp, e) {
                    instance.updateResourceUrl();
                }
            }
        });

        this.portField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Port',
            subFieldLabel: 'Enter the port number running your services',
            flex: 1,
            name: 'port',
            readOnly: this.readOnlyInheritedFields,
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.updateResourceUrl();
                },
                keyup: function(cmp, e) {
                    instance.updateResourceUrl();
                }
            }
        });

        this.items = [
            {
                xtype: 'fieldcontainer',
                layout: 'hbox',
                items: [
                    this.serverNameField,
                    {
                        xtype: 'splitter'
                    },
                    this.portField
                ]
            }
        ];

        if (this.needParentPathField && this.needUrlPathField) {
            this.parentPathField = Ext.ComponentMgr.create({
                xtype: 'opf-hidden',
                name: 'parentPath',
                width: 5,
                listeners: {
                    change: function() {
                        instance.updateResourceUrl();
                    }
                }
            });
            this.urlPathField = Ext.ComponentMgr.create({
                xtype: 'opf-textfield',
                name: 'urlPath',
                emptyText: 'Enter the resource path...',
                enableKeyEvents: true,
                flex: 1,
                listeners: {
                    change: function() {
                        instance.updateResourceUrl();
                    },
                    keyup: function() {
                        instance.updateResourceUrl();
                    }
                }
            });
            this.pathField = Ext.ComponentMgr.create({
                xtype: 'fieldcontainer',
                fieldLabel: 'Path:',
                labelWidth: 80,
                labelClsExtra: 'main-label',
                labelStyle: 'padding-top: 6px;',
                layout: 'hbox',
                layoutConfig: {
                    pack: 'start'
                },
                items: [
                    this.parentPathField,
                    this.urlPathField
                ]
            });
            this.items.push(this.pathField);
        } else {
            if (this.needParentPathField) {
                this.parentPathField = Ext.ComponentMgr.create({
                    xtype: 'opf-textfield',
                    labelAlign: 'top',
                    fieldLabel: instance.parentPathFieldLabel || 'Parent Path',
                    subFieldLabel: instance.parentPathFieldSubLabel || '',
                    anchor: '100%',
                    name: instance.parentPathFieldName || 'parentPath',
                    emptyText: 'The derived parent path (if applicable)',
                    readOnly: this.readOnlyInheritedFields,
                    enableKeyEvents: true,
                    listeners: {
                        change: function() {
                            instance.updateResourceUrl();
                        },
                        keyup: function() {
                            instance.updateResourceUrl();
                        }
                    }
                });
                this.items.push(this.parentPathField);
            }

            if (this.needUrlPathField) {
                this.urlPathField = Ext.ComponentMgr.create({
                    xtype: 'opf-textfield',
                    labelAlign: 'top',
                    fieldLabel: instance.urlPathFieldLabel || 'Path',
                    subFieldLabel: instance.urlPathFieldSubLabel || '',
                    anchor: '100%',
                    name: instance.urlPathFieldName || 'urlPath',
                    emptyText: 'Enter the resource path...',
                    enableKeyEvents: true,
                    listeners: {
                        change: function() {
                            instance.updateResourceUrl();
                        },
                        keyup: function() {
                            instance.updateResourceUrl();
                        }
                    }
                });
                this.items.push(this.urlPathField);
            }
        }

        this.serverUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            value: 'fully qualified url (not editable)',
            fieldLabel: 'URL',
            anchor: '100%',
            name: 'serverUrl'
        });
        this.items.push(this.serverUrlField);

        this.protocolField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
//            labelAlign: 'top',
            fieldLabel: 'Protocol',
            labelWidth: 70,
            subFieldLabel: '',
            name: 'protocol',
            store: '',
            emptyText: 'HTTP (default)',
            editable: false,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.updateResourceUrl();
                },
                select: function(cmp, e) {
                    instance.updateResourceUrl();
                }
            }
        });

        this.methodField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
//            labelAlign: 'top',
            fieldLabel: 'Method',
            labelWidth: 65,
            subFieldLabel: '',
            name: 'method',
            store: '',
            emptyText: 'GET (Default)',
            editable: false,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.changedMethodFieldValue();
                },
                select: function(cmp, e) {
                    instance.changedMethodFieldValue();
                }
            }
        });

        this.statusHiddenField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            value: 'UNKNOWN',
            name: 'status',
            listeners: {
                change: function(status, newValue, oldValue) {
                    if (OPF.isNotBlank(oldValue)) {
                        instance.checkmarkButton.removeCls('status-' + oldValue.toLowerCase());
                    }
                    if (OPF.isNotBlank(newValue)) {
                        instance.checkmarkButton.addCls('status-' + newValue.toLowerCase());
                        instance.statusField.setValue(newValue);
                    }
                }
            }
        });

        this.statusField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            value: 'UNKNOWN',
            cls: 'resource-status'
        });

        this.checkmarkButton = Ext.ComponentMgr.create({
            xtype: 'button',
            height: 28,
            width: 28,
            cls: 'resource-checkmark'
        });

        this.testResourceButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Test Resource',
            height: 28,
            formBind : true,
            handler: function () {
                instance.editPanel.serverUrlStatus();
            }
        });

        this.RDBMSField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
//            labelAlign: 'top',
            fieldLabel: 'RDBMS',
            labelWidth: 65,
            subFieldLabel: '',
            name: 'rdbms',
            emptyText: 'Choose RDBMS...',
            readOnly: this.readOnlyInheritedFields,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.updatePort();
                    instance.updateResourceUrl();
                },
                select: function(cmp, e) {
                    instance.updatePort();
                    instance.updateResourceUrl();
                }
            }
        });

        Ext.each(this.additionalLeftPanelControls, function(control, index) {
            instance.items.push(control);
        });
        Ext.each(this.additionalRightPanelControls, function(control, index) {
            instance.items.push(control);
        });

        var bottomControls = [];
        if (this.needProtocolField) {
            bottomControls.push(this.protocolField);
            bottomControls.push(OPF.Ui.xSpacer(30));
        }
        if (this.needRDBMSField) {
            bottomControls.push(this.RDBMSField);
            bottomControls.push(OPF.Ui.xSpacer(30));
        }
        if (this.needMethodField) {
            bottomControls.push(this.methodField);
            bottomControls.push(OPF.Ui.xSpacer(30));
        }
        bottomControls.push(this.statusHiddenField);
        bottomControls.push(this.statusField);
        bottomControls.push(OPF.Ui.xSpacer(10));
        bottomControls.push(this.checkmarkButton);
        bottomControls.push(OPF.Ui.xSpacer(20));
        bottomControls.push(this.testResourceButton);

        if (bottomControls.length > 0) {
            this.items.push(
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    cls: 'resource-bottom-controls',
                    padding: 10,
                    items: bottomControls
                }
            );
        }

        this.callParent(arguments);
    },

    updateResourceAccessFields: function() {
        this.updateUrlPath();
        this.updateResourceUrl();
    },

    updateUrlPath: function() {
        if (OPF.isNotEmpty(this.urlPathField)) {
            var name = OPF.ifBlank(this.editPanel.nodeBasicFields.nameField.getValue(), '');
            var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-').toLowerCase();

            var urlPathValue = OPF.ifBlank(this.urlPathField.getValue(), '');
            if (OPF.isNotBlank(normalizeName) || this.urlPathChanged) {
                if (this.urlPathChanged || this.editPanel.saveAs == 'update') {
                    var lastPos = urlPathValue.lastIndexOf("/");
                    urlPathValue = urlPathValue.substring(0, lastPos);
                }
                this.urlPathField.setValue(urlPathValue + '/' + normalizeName);
                this.urlPathChanged = true;
            }
        }
    },

    getUrlPathValue: function() {
        return OPF.isNotEmpty(this.urlPathField) ? OPF.ifBlank(this.urlPathField.getValue(), '') : null;
    },

    updateResourceUrl: function() {
        var serverName = OPF.ifBlank(this.serverNameField.getValue(), '');
        var parentPath = OPF.isNotEmpty(this.parentPathField) ? OPF.ifBlank(this.parentPathField.getValue(), '') : null;
        var port = OPF.ifBlank(this.portField.getValue(), '');
        var urlPath = this.getUrlPathValue();
        var sPort = '';
        var httpProtocol = 'http';
        if (port == '443') {
            httpProtocol = 'https';
        } else if (port != '80' && port != '') {
            sPort = ':' + port;
        }
        var sUrlPath = '';
        if (OPF.isNotBlank(urlPath)) {
            var pattern = /^\/.*/g;
            sUrlPath = (pattern.test(urlPath) ? '' : '/') + urlPath;
        }
        var hasServerName = false;
        var serverUrl = '';
        if (OPF.isNotBlank(serverName)) {
            serverUrl = serverName + sPort;
            hasServerName = true;
        }
        if (OPF.isNotBlank(parentPath)) {
            serverUrl += parentPath;
        }
        serverUrl += sUrlPath;
        serverUrl = serverUrl.replace(/\/+/g, '/');
        if (hasServerName) {
            serverUrl = httpProtocol + '://' + serverUrl;
        }
        this.serverUrlField.setValue(serverUrl.toLowerCase());

        if (this.pathField && OPF.isNotBlank(parentPath) && !this.parentPathDisplayField) {
            this.renderPathField();
        }
    },

    renderPathField: function() {
        this.parentPathDisplayField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            value: this.parentPathField.getValue(),
            padding: '3 0 0 0'
        });
        this.pathField.insert(0, this.parentPathDisplayField);
    },

    updatePort: function() {
        if (this.RDBMSField.getValue() == 'MySQL') {
            this.portField.setValue('3306');
        } else if (this.RDBMSField.getValue() == 'Oracle') {
            this.portField.setValue('1521');
        } else if (this.RDBMSField.getValue() == 'MSSQL') {
            this.portField.setValue('1433');
        }
    },

    changedMethodFieldValue: function() {

    },

    setAllReadOnly: function(readOnly) {
        Ext.each(this.leftPanelControls, function(panelControl) {
            if (panelControl.setReadOnly) {
                panelControl.setReadOnly(readOnly);
            }
        });
        Ext.each(this.rightPanelControls, function(panelControl) {
            if (panelControl.setReadOnly) {
                panelControl.setReadOnly(readOnly);
            }
        });
    }

});
