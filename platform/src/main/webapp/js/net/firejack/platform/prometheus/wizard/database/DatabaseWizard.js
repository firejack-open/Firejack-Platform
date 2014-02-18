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

Ext.require([
    'OPF.prometheus.wizard.AbstractWizard',
    'OPF.console.domain.model.SystemModel'
]);

Ext.define('OPF.prometheus.wizard.database.DatabaseWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.database-wizard',

    statics: {
        id: 'databaseWizard'
    },

    id: 'databaseWizard',
    title: 'Add a database',
    iconCls: 'add-database-icon',
    height: 950,

    initComponent: function() {
        var me = this;

        this.systemStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.SystemModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/system?aliases=false'),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.systemCombo = Ext.create('OPF.core.component.form.ComboBox', {
            name: 'parentId',
            labelAlign: 'top',
            fieldLabel: 'Select System',
            editable: false,
            store: this.systemStore,
            queryMode: 'remote',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            listeners: {
                select: function(combo, records, eOpts) {
                    me.databaseNameField.setDisabled(records.length == 0);
                    me.databaseDescriptionField.setDisabled(records.length == 0);
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.databaseNameField = Ext.create('OPF.core.component.form.Text', {
            name: 'name',
            labelAlign: 'top',
            fieldLabel: 'Database Name',
            disabled: true,
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.updateDatabaseName();
                },
                keyup: function() {
                    me.updateDatabaseName();
                }
            }
        });

        this.databaseDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
            name: 'description',
            labelAlign: 'top',
            fieldLabel: 'Database Description',
            disabled: true
        });


        this.databaseEntityForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            defaults: {
                anchor: '100%'
            },
            items: [
                this.systemCombo,
                this.databaseNameField,
                this.databaseDescriptionField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToDatabaseDetailsPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Database', me.databaseEntityMessagePanel, {
                        useBaseUrl: false
                    });

                    me.databaseNameField.customValidator = function(value) {
                        var msg = null;
                        if (me.lastNotUniqueDatabaseName != value) {
                            if (OPF.isNotBlank(value)) {
                                me.checkUniqueDatabaseNameTask.delay(250);
                            }
                        } else {
                            msg = 'Name is not unique.';
                        }
                        return msg;
                    };
                }
            }
        });


        this.rdbmsField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'RDBMS',
            name: 'rdbms',
            emptyText: 'Choose RDBMS...',
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.updatePort();
                    me.updateResourceUrl();
                },
                select: function(cmp, e) {
                    me.updatePort();
                    me.updateResourceUrl();
                }
            }
        });

        this.serverUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-display',
            value: 'fully qualified url (not editable)',
            labelAlign: 'top',
            fieldLabel: 'URL',
            subFieldLabel: 'jdbc url for connect to database',
            name: 'serverUrl'
        });

        this.dbNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'DB Name',
            subFieldLabel: 'Enter the database name running your services',
            name: 'urlPath',
            modified: false,
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.updateResourceUrl();
                },
                keyup: function(field) {
                    field.modified = true;
                    me.updateResourceUrl();
                }
            }
        });

        this.sidField =  Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'SID',
            subFieldLabel: 'Enter the SID to use for connecting for this database',
            name: 'parentPath',
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.updateResourceUrl();
                },
                keyup: function() {
                    me.updateResourceUrl();
                }
            }
        });

        this.serverNameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Server Name',
            subFieldLabel: 'Enter the registered name of the server or load balancer',
            name: 'serverName',
            emptyText: 'Enter DNS name of server...',
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.updateResourceUrl();
                },
                keyup: function() {
                    me.updateResourceUrl();
                }
            }
        });

        this.portField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Port',
            subFieldLabel: 'Enter the port number running your services',
            name: 'port',
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.updateResourceUrl();
                },
                keyup: function() {
                    me.updateResourceUrl();
                }
            }
        });

        this.usernameField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Username',
            name: 'username',
            subFieldLabel: 'Enter the username to use for connecting to this database',
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.statusHiddenField.setValue('UNKNOWN');
                },
                keyup: function() {
                    me.statusHiddenField.setValue('UNKNOWN');
                }
            }
        });

        this.passwordField =  Ext.create('OPF.core.component.form.Text', {
            name: 'password',
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: 'Enter the password to use for connecting to this database',
            inputType: 'password',
            enableKeyEvents: true,
            listeners: {
                change: function() {
                    me.statusHiddenField.setValue('UNKNOWN');
                },
                keyup: function() {
                    me.statusHiddenField.setValue('UNKNOWN');
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
                        me.checkmark.removeCls('status-' + oldValue.toLowerCase());
                    }
                    if (OPF.isNotBlank(newValue)) {
                        me.checkmark.addCls('status-' + newValue.toLowerCase());
                        me.statusField.setValue(newValue);
                        var isDatabaseConnected = newValue == 'ACTIVE';
                        if (!isDatabaseConnected) {
                            me.reverseEngineerCheckbox.setValue(false);
                            me.xaSupportCheckbox.setValue(false);
                        }
                        me.reverseEngineerCheckbox.setDisabled(!isDatabaseConnected);
                        me.xaSupportCheckbox.setDisabled(!isDatabaseConnected);
                    }
                }
            }
        });

        this.statusField = Ext.ComponentMgr.create({
            xtype: 'opf-display',
            value: 'UNKNOWN',
            cls: 'resource-status'
        });

        this.checkmark = Ext.ComponentMgr.create({
            xtype: 'container',
            height: 28,
            width: 28,
            cls: 'checkmark'
        });

        this.testDatabaseConnectionButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Test Database Connection',
            height: 28,
            formBind : true,
            handler: this.testDatabaseConnection,
            scope: this
        });

        this.databaseDetailsForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            defaults: {
                anchor: '100%'
            },
            items: [
                {
                    xtype: 'hidden',
                    name: 'protocol',
                    value: 'JDBC'
                },
                this.rdbmsField,
                this.serverUrlField,
                this.serverNameField,
                this.portField,

//                {
//                    xtype: 'container',
//                    layout: 'hbox',
//                    items: [
//                        this.rdbmsField,
//                        {
//                            xtype: 'splitter'
//                        },
//                        this.serverUrlField
//                    ]
//                },
//                {
//                    xtype: 'container',
//                    layout: 'hbox',
//                    items: [
//                        this.serverNameField,
//                        {
//                            xtype: 'splitter'
//                        },
//                        this.portField
//                    ]
//                },
                this.sidField,
                this.dbNameField,
                this.usernameField,
                this.passwordField,

//                {
//                    xtype: 'container',
//                    layout: 'hbox',
//                    items: [
//                        this.usernameField,
//                        {
//                            xtype: 'splitter'
//                        },
//                        this.passwordField
//                    ]
//                },
                {
                    xtype: 'container',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'splitter',
                            flex: 1
                        },
                        this.statusHiddenField,
                        this.statusField,
                        this.checkmark,
                        this.testDatabaseConnectionButton
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToReverseEngineeringPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Database', me.databaseDetailsMessagePanel, {
                        useBaseUrl: false
                    });
                }
            }
        });

        this.domainNameField = Ext.create('OPF.core.component.form.Text', {
            name: 'name',
            labelAlign: 'top',
            fieldLabel: 'Domain Name'
        });

        this.domainDescriptionField = Ext.create('OPF.core.component.form.TextArea', {
            name: 'description',
            labelAlign: 'top',
            fieldLabel: 'Domain Description'
        });

        this.reverseEngineerCheckbox = Ext.create('OPF.core.component.form.Checkbox', {
            name: 'reverseEngineer',
            fieldLabel: 'Execute Reverse Engineer',
            labelWidth: '100%',
            subFieldLabel: 'check if you want to reverse engineer configured database',
            disabled: true
        });

        this.xaSupportCheckbox = Ext.create('OPF.core.component.form.Checkbox', {
            name: 'xaSupport',
            fieldLabel: 'XA Transaction Support',
            labelWidth: '100%',
            subFieldLabel: 'check if you want to use XA transactions for reverse engineered entities',
            disabled: true,
            inputValue: true
        });

        this.domainReverseEngineeringForm = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            defaults: {
                anchor: '100%'
            },
            items: [
                this.domainNameField,
                this.domainDescriptionField,
                this.reverseEngineerCheckbox,
                this.xaSupportCheckbox
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToDeployPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Domain', me.domainDetailsMessagePanel, {
                        useBaseUrl: false
                    });

                    me.domainNameField.customValidator = function(value) {
                        var msg = null;
                        if (me.lastNotUniqueDomainName != value) {
                            if (OPF.isNotBlank(value)) {
                                me.checkUniqueDomainNameTask.delay(250);
                            }
                        } else {
                            msg = 'Domain Name is not unique in Package: \'' + OPF.Cfg.PACKAGE_LOOKUP + '\'.';
                        }
                        return msg;
                    }
                }
            }
        });


        this.items = [
            {
                title: '1. Provide Details',
                layout: 'fit',
                items: [
                    this.databaseEntityForm
                ],
                nextFrameFn: function() {
                    me.goToDatabaseDetailsPanel();
                }
            },
            {
                title: '2. Database Details',
                layout: 'fit',
                items: [
                    this.databaseDetailsForm
                ],
                prevFrameFn: function() {
                    me.goToDatabaseEntityPanel();
                },
                nextFrameFn: function() {
                    me.goToReverseEngineeringPanel();
                }
            },
            {
                title: '3. Reverse Engineering',
                layout: 'fit',
                items: [
                    this.domainReverseEngineeringForm
                ],
                prevFrameFn: function() {
                    me.goToDatabaseDetailsPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '4. Deploy Your Function',
                yesFn: function() {
                    me.createDatabase(true);
                },
                noFn: function() {
                    me.createDatabase(false);
                },
                prevFrameFn: function() {
                    me.goToReverseEngineeringPanel();
                }
            }
        ];

        this.databaseEntityMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.databaseEntityForm
        });
        this.databaseEntityForm.insert(0, this.databaseEntityMessagePanel);

        this.databaseDetailsMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.databaseDetailsForm
        });
        this.databaseDetailsForm.insert(0, this.databaseDetailsMessagePanel);

        this.domainDetailsMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.domainReverseEngineeringForm
        });
        this.domainReverseEngineeringForm.insert(0, this.domainDetailsMessagePanel);

        this.lastNotUniqueDatabaseName = null;
        this.checkUniqueDatabaseNameTask = new Ext.util.DelayedTask(function(){
            var systemRecord = me.systemCombo.findRecordByValue(me.systemCombo.getValue());
            var path = systemRecord.get('lookup');
            var name = me.databaseNameField.getValue();
            if (OPF.isNotBlank(name)) {
                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/DATABASE', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.databaseNameField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.databaseNameField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.databaseNameField.clearInvalid();
                                }
                            } else {
                                me.databaseNameField.markInvalid(resp.message);
                                me.lastNotUniqueDatabaseName = name;
                            }
                        }
                    },
                    failure: function () {
                        Ext.Msg.alert('Error', 'Connection error!');
                    }
                });
            }
        });

        this.lastNotUniqueDomainName = null;
        this.checkUniqueDomainNameTask = new Ext.util.DelayedTask(function(){
            var name = me.domainNameField.getValue();
            if (OPF.isNotBlank(name)) {
                var url = OPF.Cfg.restUrl('/registry/check/' + OPF.Cfg.PACKAGE_LOOKUP + '/DOMAIN', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.domainNameField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.domainNameField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.domainNameField.clearInvalid();
                                }
                            } else {
                                me.domainNameField.markInvalid(resp.message);
                                me.lastNotUniqueDomainName = name;
                            }
                        }
                    },
                    failure: function () {
                        Ext.Msg.alert('Error', 'Connection error!');
                    }
                });
            }
        });

        this.callParent(arguments);
    },

    updatePort: function() {
        if (this.rdbmsField.getValue() == 'MySQL') {
            this.portField.setValue('3306');
        } else if (this.rdbmsField.getValue() == 'Oracle') {
            this.portField.setValue('1521');
        } else if (this.rdbmsField.getValue() == 'MSSQL') {
            this.portField.setValue('1433');
        }
    },

    updateResourceUrl: function() {
        var serverName = OPF.ifBlank(this.serverNameField.getValue(), '');
        var port = OPF.ifBlank(this.portField.getValue(), '');
        var schema = OPF.ifBlank(this.dbNameField.getValue(), '');
        var sid = OPF.ifBlank(this.sidField.getValue(), '');
        var rdbms = OPF.ifBlank(this.rdbmsField.getValue(), '');
        var sUrlField = '';
        var sPort = '';
        if (port != '') {
            sPort = ':' + port;
        }
        if (serverName != '') {
            if (rdbms == 'MySQL') {
                sUrlField = "jdbc:mysql://" + serverName + sPort + "/" + schema;
                this.dbNameField.labelEl.dom.children[0].innerHTML = 'DB Name:';
                this.dbNameField.labelEl.dom.children[1].innerHTML = 'Enter the name of the database for this connection';
                this.sidField.hide();
            } else if (rdbms == 'Oracle') {
                sUrlField = "jdbc:oracle:thin://" + serverName + sPort + "/" + sid;
                this.dbNameField.labelEl.dom.children[0].innerHTML = 'Schema:';
                this.dbNameField.labelEl.dom.children[1].innerHTML = 'Enter the name of the schema for this connection';
                this.sidField.show();
                this.sidField.labelEl.dom.children[0].innerHTML = 'SID:';
                this.sidField.labelEl.dom.children[1].innerHTML = 'Enter the SID to use for connecting for this database';
            } else if (rdbms == 'MSSQL') {
                sUrlField = "jdbc:sqlserver://" + serverName + sPort + "/" + schema;
                this.dbNameField.labelEl.dom.children[0].innerHTML = 'DB Name:';
                this.dbNameField.labelEl.dom.children[1].innerHTML = 'Enter the name of the database for this connection';
                this.sidField.show();
                this.sidField.labelEl.dom.children[0].innerHTML = 'Schema:';
                this.sidField.labelEl.dom.children[1].innerHTML = 'Enter the name of the schema for this database';
            }
        }
        var newConnectionUrl = sUrlField.toLowerCase();
        var oldConnectionUrl = this.serverUrlField.getValue();
        if (newConnectionUrl != oldConnectionUrl) {
            this.statusHiddenField.setValue('UNKNOWN');
            this.serverUrlField.setValue(newConnectionUrl);
        }
    },

    updateDatabaseName: function() {
        if (!this.dbNameField.modified) {
            var name = OPF.ifBlank(this.databaseNameField.getValue(), '');
            var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '_').toLowerCase();
            if (OPF.isNotBlank(normalizeName)) {
                this.dbNameField.setValue(normalizeName);
            }
        }
    },

    goToDatabaseEntityPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToDatabaseDetailsPanel: function() {
        var systemId = this.systemCombo.getValue();
        if (OPF.isNotEmpty(systemId) && Ext.isNumeric(systemId)) {
            if (this.databaseEntityForm.getForm().isValid()) {
                var layout = this.getCardPanelLayout();
                layout.setActiveItem(1);
            }
        } else {
            this.databaseEntityMessagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'System has not been selected.');
        }
    },

    goToReverseEngineeringPanel: function() {
        if (this.databaseDetailsForm.getForm().isValid()) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(2);
        }
    },

    goToDeployPanel: function() {
        var layout = this.getCardPanelLayout();
        if (this.databaseEntityForm.getForm().isValid()) {
            if (this.databaseDetailsForm.getForm().isValid()) {
                if (this.domainReverseEngineeringForm.getForm().isValid()) {
                    layout.setActiveItem(3);
                } else {
                    layout.setActiveItem(2);
                }
            } else {
                layout.setActiveItem(1);
            }
        } else {
            layout.setActiveItem(0);
        }
    },

    testDatabaseConnection: function() {
        var me = this;

        var jsonData = this.databaseDetailsForm.getForm().getValues();

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/url/check', false),
            method: 'POST',
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                if (responseData.success) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, responseData.message);
                    me.statusHiddenField.setValue(responseData.data[0].status);
                } else {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, responseData.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
            }
        })
    },

    createDatabase: function(isDeploy) {
        var me = this;

        var databaseEntityFormData = this.databaseEntityForm.getForm().getValues();
        var databaseDetailsFormData = this.databaseDetailsForm.getForm().getValues();
        var domainReverseEngineeringFormData = this.domainReverseEngineeringForm.getForm().getValues();

        domainReverseEngineeringFormData.database = Ext.apply(databaseEntityFormData, databaseDetailsFormData);
        domainReverseEngineeringFormData.path = OPF.Cfg.PACKAGE_LOOKUP;

        this.getEl().mask();

        var url = OPF.Cfg.restUrl('/registry/domain/database');
        url = OPF.Cfg.addParameterToURL(url, 'reverseEngineer', domainReverseEngineeringFormData.reverseEngineer == 'on');

        delete domainReverseEngineeringFormData.reverseEngineer;

        this.save(url, domainReverseEngineeringFormData, isDeploy);
    }

});