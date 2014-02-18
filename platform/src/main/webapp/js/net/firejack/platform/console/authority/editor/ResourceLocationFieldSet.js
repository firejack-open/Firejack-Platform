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

Ext.define('OPF.console.authority.editor.ResourceLocationFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Resource Access',
    subFieldLabel: '',

    layout: 'anchor',
    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.authority.editor.ResourceLocationFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.serverNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Server Name',
            subFieldLabel: '',
            flex: 2,
            name: 'serverName',
            emptyText: 'Enter DNS name of server...',
            readOnly: this.readOnlyInheritedFields
        });

        this.parentPathField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Parent Path',
            subFieldLabel: '',
            anchor: '100%',
            name: 'parentPath',
            emptyText: 'The derived parent path (if applicable)',
            readOnly: this.readOnlyInheritedFields
        });

        this.urlPathField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Path',
            subFieldLabel: '',
            anchor: '100%',
            name: 'urlPath',
            emptyText: 'Enter the resource path...'
        });

        this.portField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Port',
            subFieldLabel: '',
            flex: 1,
            name: 'port',
            readOnly: this.readOnlyInheritedFields
        });

        this.wildcardStyleField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'Wildcard Style',
            subFieldLabel: '',
            anchor: '100%',
            name: 'wildcardStyle',
            emptyText: 'ANT (Default)'
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
            },
            this.parentPathField,
            this.urlPathField,
            this.wildcardStyleField
        ];

        this.callParent(arguments);
    },

    updateResourceAccessFields: function() {
        this.updateUrlPath();
        this.updateResourceUrl();
    },

    updateUrlPath: function() {
        var name = OPF.ifBlank(this.editPanel.nodeBasicFields.nameField.getValue(), '');
        var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-').toLowerCase();

        if (OPF.isBlank(this.urlPathValue)) {
            this.urlPathValue = this.urlPathField.getValue();
            if (this.editPanel.saveAs == 'update') {
                var lastPos = this.urlPathValue.lastIndexOf("/");
                this.urlPathValue = this.urlPathValue.substring(0, lastPos);
            }
        }
        if (OPF.isNotBlank(this.urlPathValue)) {
            this.urlPathField.setValue(this.urlPathValue + '/' + normalizeName);
        }
    },

    updateResourceUrl: function() {
        var serverName = OPF.ifBlank(this.serverNameField.getValue(), '');
        var parentPath = OPF.ifBlank(this.parentPathField.getValue(), '');
        var port = OPF.ifBlank(this.portField.getValue(), '');
        var urlPath = OPF.ifBlank(this.urlPathField.getValue(), '');
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
        if (isNotBlank(serverName)) {
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
    }
});