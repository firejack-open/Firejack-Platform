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
Ext.define('OPF.console.domain.view.PackageVersionFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.pkg-version-fieldset',

    fieldLabel: 'Version Details',
    subFieldLabel: '',

    layout: 'anchor',
    flex: 1,

    editPanel: null,

    selectedArchivedVersion: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.PackageVersionFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.versionField = Ext.create('OPF.core.component.TextField', {
            name: 'version',
            flex: 1,
            readOnly: true
        });

        this.unlockButton = OPF.Ui.createBtn('unlock', 54, 'lock-unlock', {locked: true, height: 28});

        this.archiveButton = OPF.Ui.createBtn('archive', 54, 'archive', {height: 28});

        this.assetFilesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            stripeRows: true,
            store: 'AssetFiles',
            title: 'Assets',
            anchor: '100%',
            height: 200,
            autoExpandColumn: 'filename',
            columns: [
                OPF.Ui.populateColumn('filename', 'Asset File', {
                    flex: 1,
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        var downloadSourceUrl = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/download/') +
                            me.editPanel.packageId + '/' + value;
                        return '<a href="' + downloadSourceUrl + '">' + value + '</a>';
                    }
                }),
                OPF.Ui.populateColumn('type', 'Type', {flex: 1}),
                OPF.Ui.populateColumn('updated', 'Created', {
                    flex: 1,
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        if (OPF.isNotEmpty(value)) {
                            value = sqFormattedDate(sqMillisecondToDate(value), 'M dS, y, h:i:s A');
                        }
                        return value;
                    }
                })
            ]
        });

        this.supportButton = OPF.Ui.createBtn('support', 50, 'support', {disabled: true});

        this.activateButton = OPF.Ui.createBtn('activate', 50, 'activate', {disabled: true});

        this.deleteButton = OPF.Ui.createBtn('delete', 50, 'delete', {disabled: true});

        this.versionsGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            store: 'Versions',
            title: 'Versions',
            anchor: '100%',
            height: 200,
            marker: 'versions',
            columns: [
                OPF.Ui.populateColumn('versionName', 'Version', {
                    flex: 1
                }),
                OPF.Ui.populateColumn('iteration', 'Iteration', {
                    flex: 1
                }),
                OPF.Ui.populateColumn('updated', 'Updated', {
                    flex: 1,
                    renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                        if (OPF.isNotEmpty(value)) {
                            value = sqFormattedDate(sqMillisecondToDate(value), 'M dS, y, h:i:s A');
                        }
                        return value;
                    }
                })
            ],
            tbar: {
                xtype: 'toolbar',
                items: [
                    {
                        xtype: 'tbfill'
                    },
                    this.supportButton,
                    {
                        xtype: 'tbseparator'
                    },
                    this.activateButton,
                    {
                        xtype: 'tbseparator'
                    },
                    this.deleteButton
                ]
            }
        });

        this.items = [
            {
                xtype: 'label-container',
                fieldLabel: 'Current Version',
                subFieldLabel: 'edit the current version number',
                labelCls: '',
                cls: '',
                labelMargin: '0 0 5 0',
                layout: 'hbox',
                items: [
                    this.versionField,
                    OPF.Ui.xSpacer(10),
                    this.unlockButton,
                    OPF.Ui.xSpacer(10),
                    this.archiveButton
                ]
            },
            OPF.Ui.ySpacer(10, {anchor: '100%'}),
            this.assetFilesGrid,
            OPF.Ui.ySpacer(10, {anchor: '100%'}),
            this.versionsGrid
        ];

        this.callParent(arguments);
    }
});

/**
 *
 */
Ext.define('OPF.console.domain.view.PackageResourceAccessPanel', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.pkg-resource-access',

    fieldLabel: 'Resource Access',
    subFieldLabel: '',

    layout: 'anchor',
    preventHeader: true,
    border: false,
    editPanel: null,

    modifiedUrlPath: false,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.PackageResourceAccessPanel.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.prefixField = OPF.Ui.textFormField('prefix', 'Naming Prefix', {
            labelAlign: 'top',
            subFieldLabel: ''
        });

        this.serverNameField = OPF.Ui.hiddenField('serverName');
        this.portField = OPF.Ui.hiddenField('port');

        this.serverBaseUrlField = OPF.Ui.displayField(null, 'System URL', {
            value: 'Package is not associated with any System'
        });

        this.systemDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'System Association',
            subFieldLabel: 'Drag and Drop from Cloud Navigator',
            name: 'system',
            registryNodeType: OPF.core.utils.RegistryNodeType.SYSTEM
        });

        this.urlPathField = OPF.Ui.textFormField('urlPath', this.urlPathFieldLabel || 'Context Path', {
            labelAlign: 'top',
            subFieldLabel: '',
            cls:'url-path-field',
            emptyText: 'Enter the resource path...',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    instance.modifiedUrlPath = true;
                    instance.updateResourceUrl();
                }
            }
        });

        this.serverUrlField = OPF.Ui.displayField('serverUrl', 'URL', {
            value: 'fully qualified url (not editable)'
        });

        this.items = [
            this.prefixField,
            this.systemDropPanel,
            this.serverBaseUrlField,
            this.serverNameField,
            this.portField,
            this.urlPathField,
            this.serverUrlField
        ];

        this.callParent(arguments);
    },

    updateServerBaseUrl: function() {
        var serverName = this.serverNameField.getValue();
        if (OPF.isNotBlank(serverName)) {
            var port = OPF.ifBlank(this.portField.getValue(), '');
            var sPort = '';
            var httpProtocol = 'http';
            if (port == '443') {
                httpProtocol = 'https';
            } else if (port != '80' && port != '') {
                sPort = ':' + port;
            }
            var serverBaseUrl = serverName + sPort;
            serverBaseUrl = httpProtocol + '://' + serverBaseUrl.replace(/\/+/g, '/');
            this.serverBaseUrlField.setValue(serverBaseUrl);
        }
    },

    updateResourceUrl: function() {
        if (this.editPanel.saveAs == 'new' && !this.modifiedUrlPath) {
            this.urlPathField.setValue('/' + this.editPanel.nodeBasicFields.nameField.getValue());
        }

        var serverName = OPF.ifBlank(this.serverNameField.getValue(), '');
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
        if (OPF.isNotBlank(serverName)) {
            serverUrl = serverName + sPort;
            hasServerName = true;
        }
        serverUrl += sUrlPath;
        serverUrl = serverUrl.replace(/\/+/g, '/');
        if (hasServerName) {
            serverUrl = httpProtocol + '://' + serverUrl;
        }
        this.serverUrlField.setValue(serverUrl.toLowerCase());
    },

    updateResourceAccessFields: function() {
        this.updateResourceUrl();
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.view.PackageDatabaseAccessPanel', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.pkg-database-access',

    fieldLabel: 'Database Access',
    subFieldLabel: '',

    layout: 'anchor',
    preventHeader: true,
    border: false,
    editPanel: null,

    modifiedUrlPath: false,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.PackageDatabaseAccessPanel.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.databaseDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Database Association by default for all Entities',
            subFieldLabel: 'Drag and Drop from Cloud Navigator',
            name: 'database',
            registryNodeType: OPF.core.utils.RegistryNodeType.DATABASE
        });

        //this.reverseEngineeringButton = OPF.Ui.createBtn('Reverse Engineering', 120, 'reverse-engineering', {disabled: true, height: 28});

        this.items = [
            this.databaseDropPanel/*,
            this.reverseEngineeringButton*/
        ];

        this.callParent(arguments);
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.view.PackageEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.package-editor',

    title: 'PACKAGE: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.package',

    packageId: null,
    packageLookup: null,
    packageUrlPath: null,
    packageName: null,
    deployed: null,
    systemId: null,
    installEnabled: false,
    packageCurrentVersion: null,
    editableWithChild: true,

    /**
     *
     */
    initComponent: function() {

        this.generateDocumentationButton = OPF.Ui.createMenu('Gen Docs', 'gendocs', {
            disabled: true
        });

        this.redeployButton = OPF.Ui.createMenu('Redeploy', 'redeploy');

        this.installButton = OPF.Ui.createMenu('Install', 'install');

        this.migrateButton = OPF.Ui.createMenu('Migrate', 'migrate');

        this.resetButton = OPF.Ui.createMenu('Restart', 'reset', {
            disabled: true
        });

        this.generateCodeButton = OPF.Ui.createMenu('Gen Code', 'generate-code');

        this.uploadCodeButton = OPF.Ui.createMenu('Upload Code', 'upload-code', {
            itemId: 'upload-code-btn'
        });

        this.generateUpgradeButton = OPF.Ui.createMenu('Gen Upgrade', 'generate-upgrade', {
            disabled: true
        });

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.resourceFields = Ext.create('OPF.console.domain.view.PackageResourceAccessPanel', this);

        this.databaseFields = Ext.create('OPF.console.domain.view.PackageDatabaseAccessPanel', this);

        this.uploadFileDialog = Ext.create('OPF.core.component.UploadFileDialog', this, {
            uploadFile: function() {
                var instance = this;
                instance.fileUploadField.getValue().lastIndexOf('.');
                var selectedFileName = this.fileUploadField.getValue();
                if (selectedFileName.length > 4) {
                    var extension = selectedFileName.substring(selectedFileName.length - 4).toLowerCase();
                    if (extension == '.war' || extension == '.zip' || extension == '.xml') {
                        extension = extension.substring(1);
                        for (var i = 0; i < this.fileTypes.length; i++) {
                            if (this.fileTypes[i] == extension) {
                                this.form.getForm().url =
                                    OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/version/') +
                                        this.packageId + '/' + extension;
                                this.form.getForm().submit({
                                    method : "POST",

                                    success : function(fr, action) {
                                        var resp = Ext.decode(action.response.responseText);
                                        OPF.Msg.setAlert(true, resp.message);

                                        instance.editPanel.updatePackageVersionInfoByJsonData(resp.data[0]);

                                        instance.hide();
                                    },

                                    failure : function(fr, action) {
                                        OPF.Msg.setAlert(false, 'Processed file on the server');
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
                OPF.Msg.setAlert(false, 'Please upload only files with .war extension.');
                return false;
            }
        });

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.resourceFields,
            this.databaseFields
        ];

        this.additionalMainControls = [
            this.redeployButton,
            this.installButton,
            this.migrateButton,
            this.generateDocumentationButton,
            this.generateCodeButton,
            this.generateUpgradeButton,
            this.uploadCodeButton,
            this.resetButton
        ];

        this.callParent(arguments);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'update') {
            this.packageVersionFields = Ext.create('OPF.console.domain.view.PackageVersionFieldSet', this);
            this.fieldsPanel.add(this.packageVersionFields);
            this.additionalBlocks.push(this.packageVersionFields);
            this.rightNavContainer.update(this.prepareRightNavigation(this.additionalBlocks));
            this.deployed = jsonData.deployed;
            this.packageUrlPath = jsonData.urlPath;
            this.packageName = jsonData.name;
            this.systemId = OPF.isNotEmpty(jsonData.system) && OPF.isNotEmpty(jsonData.system.id) ? jsonData.system.id : null;
            if (OPF.isNotEmpty(this.systemId) &&
                (!Ext.isString(this.systemId) || (Ext.isString(this.systemId) && OPF.isNotBlank(this.systemId)))) {
                this.resetButton.setDisabled(false);
                this.resetButton.systemId = this.systemId;
            }
            if (jsonData.system && jsonData.packageVersion && jsonData.packageVersion.fileVOs) {
                var filesLength = jsonData.packageVersion.fileVOs.length;
                for (var i = 0; i < filesLength; i++) {
                    if (jsonData.packageVersion.fileVOs[i].type == 'APP_WAR') {
                        this.installEnabled = true;
                        break;
                    }
                }
            }
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData) && OPF.isNotEmpty(jsonData.id) &&
            (!Ext.isString(jsonData.id) || (Ext.isString(jsonData.id) && OPF.isNotBlank(jsonData.id)))) {
            this.uploadFileDialog.packageId = jsonData.id;
            this.packageId = jsonData.id;
            this.packageLookup = jsonData.lookup;
            this.packageCurrentVersion = jsonData.version;
            this.updatePackageVersionInfoByJsonData(jsonData.packageVersion);
            var versionsStore = Ext.data.StoreManager.lookup('Versions');
            versionsStore.load();

            if (OPF.isNotEmpty(jsonData.database)) {
                this.databaseFields.databaseDropPanel.setValue(jsonData.database.id);
                var description = cutting(jsonData.database.description, 70);
                this.databaseFields.databaseDropPanel.renderDraggableEntity('tricon-database', jsonData.database.name, description, jsonData.database.lookup);
//                this.databaseFields.reverseEngineeringButton.setDisabled(false);
            }

            if (OPF.isNotEmpty(jsonData.system)) {
                this.resourceFields.systemDropPanel.setValue(jsonData.system.id);
                var description = cutting(jsonData.system.description, 70);
                this.resourceFields.systemDropPanel.renderDraggableEntity('tricon-system', jsonData.system.name, description, jsonData.system.lookup);
            }
        }
        this.resourceFields.updateServerBaseUrl();
        this.resourceFields.updateResourceUrl();
    },

    onBeforeSave: function(formData) {
        var databaseId = this.databaseFields.databaseDropPanel.getValue();
        if (OPF.isNotEmpty(databaseId)) {
            formData.database = {
                id: databaseId
            }
        } else {
            delete formData.database;
        }
        var systemId = this.resourceFields.systemDropPanel.getValue();
        if (OPF.isNotEmpty(systemId)) {
            formData.system = {
                id: systemId
            }
        } else {
            delete formData.system;
        }
        delete formData.version;
    },

    updatePackageVersionInfoByJsonData: function(version) {
        var filesInfo = version.fileVOs;
        var modelArray = [];
        Ext.each(filesInfo, function(fileInfo, index) {
            var model = Ext.create('OPF.console.domain.model.AssetFileModel', {
                type: fileInfo.type,
                filename: fileInfo.filename,
                updated: fileInfo.updated
            });
            modelArray.push(model);
        });

        var assetFilesStore = Ext.data.StoreManager.lookup('AssetFiles');
        assetFilesStore.loadData(modelArray);
        this.packageVersionFields.versionField.setValue(version.version);
    },

    disableInstall: function() {
        this.installButton.setText('Uninstall');
        this.installButton.action = 'uninstall';
    },

    enableInstall: function() {
        this.installButton.setText('Install');
        this.installButton.action = 'install';
    }

});