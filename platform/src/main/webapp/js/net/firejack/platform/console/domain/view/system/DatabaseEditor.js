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
Ext.define('OPF.console.domain.view.system.DatabaseEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.database-editor',

    title: 'DATABASE: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.database',

    initComponent: function() {
        var me = this;

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.usernameField = OPF.Ui.textFormField('username', 'Username', {
            labelAlign: 'top',
            subFieldLabel: 'Enter the username to use for connecting to this database',
            enableKeyEvents: true
        });
        this.passwordField =  OPF.Ui.textFormField('password', 'Password', {
            labelAlign: 'top',
            subFieldLabel: 'Enter the password to use for connecting to this database',
            inputType: 'password',
            enableKeyEvents: true
        });
        this.sidField =  OPF.Ui.textFormField('parentPath', 'SID', {
            labelAlign: 'top',
            subFieldLabel: 'Enter the SID to use for connecting for this database',
            enableKeyEvents: true
        });

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            urlPathFieldLabel: 'DB Name',
            urlPathFieldSubLabel: 'Enter the name of the schema or database for this connection',
            needMethodField: false,
            needParentPathField: false,
            needRDBMSField: true,
            additionalLeftPanelControls: [
                this.sidField,
                this.usernameField,
                this.passwordField
            ],
            updateResourceUrl: function() {
                var serverName = ifBlank(this.serverNameField.getValue(), '');
                var port = ifBlank(this.portField.getValue(), '');
                var schema = ifBlank(this.urlPathField.getValue(), '');
                var sid = ifBlank(me.sidField.getValue(), '');
                var protocol = ifBlank(this.protocolField.getValue(), '');
                var RDBMS = ifBlank(this.RDBMSField.getValue(), '');
                var sUrlField = '';
                var sPort = '';
                if (port != '') {
                    sPort = ':' + port;
                }
                if (serverName != '') {
                    if (RDBMS == 'MySQL') {
                        sUrlField = protocol + ":mysql://" + serverName + sPort + "/" + schema;
                        this.urlPathField.labelEl.dom.children[0].innerHTML = 'DB Name:';
                        this.urlPathField.labelEl.dom.children[1].innerHTML = 'Enter the name of the database for this connection';
                        me.sidField.hide();
                    } else if (RDBMS == 'Oracle') {
                        sUrlField = protocol + ":oracle:thin://" + serverName + sPort + "/" + sid;
                        this.urlPathField.labelEl.dom.children[0].innerHTML = 'Schema:';
                        this.urlPathField.labelEl.dom.children[1].innerHTML = 'Enter the name of the schema for this connection';
                        me.sidField.show();
                        me.sidField.labelEl.dom.children[0].innerHTML = 'SID:';
                        me.sidField.labelEl.dom.children[1].innerHTML = 'Enter the SID to use for connecting for this database';
                    } else if (RDBMS == 'MSSQL') {
                        sUrlField = protocol + ":sqlserver://" + serverName + sPort + "/" + schema;
                        this.urlPathField.labelEl.dom.children[0].innerHTML = 'DB Name:';
                        this.urlPathField.labelEl.dom.children[1].innerHTML = 'Enter the name of the database for this connection';
                        me.sidField.show();
                        me.sidField.labelEl.dom.children[0].innerHTML = 'Schema:';
                        me.sidField.labelEl.dom.children[1].innerHTML = 'Enter the name of the schema for this database';
                    }
                }
                this.serverUrlField.setValue(sUrlField.toLowerCase());
            },
            updateUrlPath: function() {
                var name = OPF.ifBlank(this.editPanel.nodeBasicFields.nameField.getValue(), '');
                var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '_').toLowerCase();

                if (OPF.isNotBlank(normalizeName) && this.editPanel.saveAs == 'new') {
                    this.urlPathField.setValue(normalizeName);
                }
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

    onBeforeSetValue: function(jsonData) {
        this.resourceFields.RDBMSField.suspendEvents();
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData.main)) {
            this.resourceFields.setAllReadOnly(true);
        }
        this.resourceFields.updateResourceUrl();
        this.resourceFields.RDBMSField.resumeEvents();
    },

    serverUrlStatusData: function(formData) {
        return {
            serverName: formData.serverName,
            urlPath: formData.urlPath,
            parentPath: formData.parentPath,
            protocol: formData.protocol,
            port: formData.port,
            rdbms: formData.rdbms,
            username: formData.username,
            password: formData.password
        };
    },

    saveRequest: function(formData, url, method) {
        this.callParent(arguments);
    }

});


