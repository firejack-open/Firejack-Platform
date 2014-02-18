/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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


