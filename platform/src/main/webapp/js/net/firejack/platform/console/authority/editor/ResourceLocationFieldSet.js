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