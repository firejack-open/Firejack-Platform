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

/**
 *
 */
Ext.define('OPF.console.domain.view.system.FilestoreEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.database-editor',

    title: 'FILESTORE: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.filestore',

    initComponent: function() {
        var instance = this;

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.serverDirectoryField = Ext.create('OPF.core.component.SelectFileField', {
            labelAlign: 'top',
            fieldLabel: 'Server Directory',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'server-directory-field',
            name: 'serverDirectory',
            emptyText: 'Enter server directory path...'
        });

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            urlPathFieldLabel: 'Path',
            needMethodField: false,
            needParentPathField: false,
            needRDBMSField: false,
            needProtocolField: false,
            additionalLeftPanelControls: [
                instance.serverDirectoryField
            ],
            updateResourceUrl: function() {
                var serverName = ifBlank(this.serverNameField.getValue(), '');
                var port = ifBlank(this.portField.getValue(), '');
                var urlPath = ifBlank(this.urlPathField.getValue(), '');
                var sUrlField = '';
                var sPort = '';
                if (port != '') {
                    sPort = ':' + port;
                }
                if (serverName != '') {
                    sUrlField = "http://" + serverName + sPort + urlPath;
                }
                this.serverUrlField.setValue(sUrlField.toLowerCase());
            }
        });

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.resourceFields
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData.main)) {
            this.resourceFields.setAllReadOnly(true);
        }
    },

    saveRequest: function(formData, url, method) {
        delete formData.parentPath;

        this.callParent(arguments);
    }

});


