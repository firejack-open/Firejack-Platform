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


Ext.define('OPF.console.directory.editor.DirectoryFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Resource Access',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,
    initialDirectoryType: 'DATABASE',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.DirectoryFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.serverNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Server Name',
            subFieldLabel: '',
            flex: 1,
            itemId: 'server-field',
            name: 'serverName',
            emptyText: 'Enter DNS name of server...',
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.updateResourceUrl();
                },
                keyup: function(cmp, e) {
                    me.updateResourceUrl();
                }
            },
            isValid: function() {
                return me.directoryTypeField.getValue() == 'LDAP' ? OPF.isNotBlank(me.serverNameField.getValue()) : true;
            }
        });

        this.portField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Port',
            subFieldLabel: '',
            width: 200,
            name: 'port',
            regex: new RegExp('^\\d{2,5}$'),
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.updateResourceUrl();
                },
                keyup: function(cmp, e) {
                    me.updateResourceUrl();
                }
            },
            isValid: function() {
                if (me.directoryTypeField.getValue() == 'LDAP') {
                    var port = me.portField.getValue();
                    return OPF.isBlank(port) ? false : me.portField.regex.test(port)
                }
                return true;
                //return me.directoryTypeField.getValue() == 'LDAP' ? OPF.isNotBlank(me.portField.getValue()) : true;
            }
        });

        this.ldapUrlDisplayfield = Ext.create('OPF.core.component.Display', {
            name: 'path',
            fieldLabel: 'LDAP url',
            anchor: '100%'
        });

        this.urlPathField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Path',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'url-path-field',
            name: 'urlPath',
            emptyText: 'Enter the resource path...'
        });

        this.directoryTypeField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            fieldLabel: 'Directory Type',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'directory-type-field',
            name: 'directoryType',
            emptyText: 'DATABASE (default)',
            listeners: {
                change: function(status, newValue, oldValue) {
                    if (OPF.isBlank(newValue) || newValue != 'LDAP') {
                        me.showLdapFieldset(false);
                    } else {
                        if (OPF.isBlank(me.baseDNField.getValue())) {
                            me.baseDNField.setValue("dc=example,dc=com");
                        }
                        if (OPF.isBlank(me.rootDNField.getValue())) {
                            me.rootDNField.setValue("cn=Directory Manager");
                        }
                        if (OPF.isBlank(me.portField.getValue())) {
                            me.portField.setValue("389");
                        }
                        me.showLdapFieldset(true);
                        me.updateResourceUrl();
                    }
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
                        me.checkmarkButton.removeCls('status-' + oldValue.toLowerCase());
                    }
                    if (OPF.isNotBlank(newValue)) {
                        me.checkmarkButton.addCls('status-' + newValue.toLowerCase());
                        me.statusField.setValue(newValue);
                        me.statusHiddenField.setValue(newValue);
                    }
                    me.setLdapManagerBtnState();
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
            handler: function () {
                if (me.directoryTypeField.getValue() == 'LDAP') {

                    var jsonData = {
                        serverName: me.serverNameField.getValue(),
                        parentPath: null,
                        urlPath: me.baseDNField.getValue(),
                        protocol: null,
                        port: me.portField.getValue(),
                        rdbms: null,
                        username: me.rootDNField.getValue(),
                        password: me.passwordField.getValue(),
                        ldapSchemaConfig: me.ldapSchemaField.getValue()
                    };

                    Ext.Ajax.request({
                        url: OPF.Cfg.restUrl('directory/ldap-connection/check'),
                        method: 'POST',
                        jsonData: {"data": jsonData},

                        success: function(response, action) {
                            var vo = Ext.decode(response.responseText);
                            if (vo.success) {
                                if (OPF.isNotBlank(me.checkmarkButton.status)) {
                                    me.checkmarkButton.removeCls('status-' + me.checkmarkButton.status);
                                }
                                var newStatus = vo.data[0].status;
                                if (OPF.isNotBlank(newStatus)) {
                                    me.checkmarkButton.status = newStatus.toLowerCase();
                                    me.checkmarkButton.addCls('status-' + me.checkmarkButton.status);
                                    me.statusField.setValue(newStatus);
                                    me.statusHiddenField.setValue(newStatus);
                                }

                                //me.setLdapManagerBtnState();

                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                            } else {
                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                            }
                        },

                        failure: function(response) {
                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
                        }
                    })

                } else {
                    alert('Not implemented yet.');
                }
            }
        });

        this.rootDNField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Root DN',
            subFieldLabel: '',
            width: 200,
            name: 'rootDN',
            isValid: function() {
                return me.directoryTypeField.getValue() == 'LDAP' ? OPF.isNotBlank(me.rootDNField.getValue()) : true;
            }
        });

        this.passwordField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: '',
            width: 200,
            name: 'password',
            inputType: 'password'
        });

        this.baseDNField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Base DN',
            subFieldLabel: '',
            width: 200,
            name: 'baseDN',
            isValid: function() {
                return me.directoryTypeField.getValue() == 'LDAP' ? OPF.isNotBlank(me.baseDNField.getValue()) : true;
            }
        });

        this.ldapSchemaField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'left',
            fieldLabel: 'Ldap Schema',
            subFieldLabel: '',
            width: 600,
            name: 'ldapSchemaConfig',
            readOnly: true,
            isValid: function() {
                return me.directoryTypeField.getValue() == 'LDAP' ?
                    OPF.isNotBlank(me.ldapSchemaField.getValue()) : true;
            }
        });

        this.configureButton = Ext.create('Ext.button.Button', {
            text: 'Configure',
            handler: function() {
                var winId = 'ldapSchemaEditor';
                var ldapSchemaEditor = Ext.WindowMgr.get(winId);
                if (OPF.isEmpty(ldapSchemaEditor)) {
                    ldapSchemaEditor = Ext.create(
                        'OPF.console.directory.editor.ldap.SchemaEditor', winId);
                }
                ldapSchemaEditor.showDialog(me.ldapSchemaField);
            }
        });

        this.ldapMainFieldset = Ext.create('Ext.form.FieldContainer', {
            layout: 'hbox',
            items: [
                this.baseDNField,
                {xtype: 'splitter'},
                this.rootDNField,
                {xtype: 'splitter'},
                this.passwordField
            ]
        });

        this.ldapConfigurationFieldContainer = Ext.create('Ext.form.FieldContainer', {
            layout: 'hbox',
            cls: 'resource-bottom-controls',
            padding: 10,
            items: [
                this.ldapSchemaField,
                {xtype: 'splitter'},
                this.configureButton
            ]
        });

        this.ldapManagerButton = Ext.create('Ext.button.Button', {
            text: 'LDAP Manager',
            height: 28,
            handler: function(btn) {
                var directoryId = me.editPanel.nodeBasicFields.idField.getValue();
                if (OPF.isBlank(directoryId)) {
                    Ext.MessageBox.alert('Information', 'LDAP Manager is active only for saved directories.');
                } else {
                    var winId = 'ldapManagerDialog';
                    var ldapManagerDlg = Ext.WindowMgr.get(winId);
                    if (OPF.isEmpty(ldapManagerDlg)) {
                        ldapManagerDlg = Ext.create('OPF.console.directory.editor.ldap.ManagerDialog', winId);
                    }
                    ldapManagerDlg.showDialog(directoryId);
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
            },
            this.ldapMainFieldset,
            this.ldapUrlDisplayfield,
            this.ldapConfigurationFieldContainer,
            this.urlPathField,
            {
                xtype: 'fieldcontainer',
                layout: 'hbox',
                cls: 'resource-bottom-controls',
                padding: 10,
                items: [
                    this.directoryTypeField,
                    OPF.Ui.xSpacer(30),
                    this.statusHiddenField,
                    this.statusField,
                    OPF.Ui.xSpacer(10),
                    this.checkmarkButton,
                    OPF.Ui.xSpacer(20),
                    this.testResourceButton,
                    OPF.Ui.xSpacer(20),
                    this.ldapManagerButton/*,
                    OPF.Ui.xSpacer(20),
                    {
                        xtype: 'button',
                        text: "Check Authentication",
                        handler: function() {
                            var directoryId = me.editPanel.nodeBasicFields.idField.getValue();
                            if (OPF.isBlank(directoryId)) {
                                Ext.MessageBox.alert('Information', 'Auth Checker is active only for saved directories.');
                            } else {
                                var winId = 'authCheckerDialog';
                                var authCheckerDialog = Ext.WindowMgr.get(winId);
                                if (OPF.isEmpty(authCheckerDialog)) {
                                    authCheckerDialog = Ext.create('OPF.console.directory.editor.ldap.AuthenticationChecker', winId);
                                }
                                authCheckerDialog.showDialog(directoryId);
                            }
                        }
                    }*/
                ]
            }
        ];

        this.showLdapFieldset(false);

        this.callParent(arguments);
    },

    showLdapFieldset: function(show) {
        if (show) {
            this.portField.show();
            this.ldapUrlDisplayfield.show();
            this.ldapMainFieldset.show();
            this.ldapConfigurationFieldContainer.show();
            this.ldapManagerButton.show();
        } else {
            this.portField.hide();
            this.ldapUrlDisplayfield.hide();
            this.ldapMainFieldset.hide();
            this.ldapConfigurationFieldContainer.hide();
            this.ldapManagerButton.hide();
        }
    },

    setLdapManagerBtnState: function() {
        if (OPF.isBlank(this.editPanel.nodeBasicFields.idField.getValue()) ||
            this.statusHiddenField.getValue() != 'ACTIVE' || this.initialDirectoryType != 'LDAP') {
            this.ldapManagerButton.disable();
        } else {
            this.ldapManagerButton.enable();
        }
    },

    /*setDevValues: function() {
        this.serverNameField.setValue('dev.timur.firejack.net');
        this.portField.setValue('4440');
        this.baseDNField.setValue("dc=example,dc=com");
        this.rootDNField.setValue("cn=root");
        this.passwordField.setValue("rootroot_");
    },*/

    updateResourceUrl: function() {
        if (OPF.isBlank(this.directoryTypeField.getValue())) {
            this.ldapUrlDisplayfield.setValue('');
        } else {
            var serverName = OPF.ifBlank(this.serverNameField.getValue(), '');
            var port = OPF.ifBlank(this.portField.getValue(), '');
            this.ldapUrlDisplayfield.setValue('ldap://' + serverName + ':' + port);
        }
    }

});