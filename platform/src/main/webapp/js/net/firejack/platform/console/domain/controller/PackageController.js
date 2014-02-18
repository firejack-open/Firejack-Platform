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

Ext.define('OPF.console.domain.controller.PackageController', {
    extend: 'Ext.app.Controller',

    views: ['PackageVersionFieldSet', 'PackageEditor'],

    stores: ['Versions', 'AssetFiles'],

    models: ['Package', 'PackageVersionModel', 'AssetFileModel'],

    init: function() {
        this.getVersionsStore().addListener('beforeload', this.onBeforeVersionsStoreLoad, this);
        this.getVersionsStore().proxy.addListener('exception', this.onProxyException, this);
        this.control(
            {
                'package-editor': {
                    afterrender: this.initEditor,
                    showeditor: this.onShowEditor
                },
                'package-editor menuitem[action=gendocs]': {
                    click: this.onGenerateDocsBtnClick
                },
                'package-editor menuitem[action=redeploy]': {
                    click: this.onRedeployBtnClick
                },
                'package-editor menuitem[action=install]': {
                    click: this.onInstallBtnClick
                },
                'package-editor menuitem[action=migrate]': {
                    click: this.onMigrateBtnClick
                },
                'package-editor menuitem[action=uninstall]': {
                    click: this.onUninstallBtnClick
                },
                'package-editor menuitem[action=generate-code]': {
                    click: this.onGenerateCodeBtnClick
                },
                'package-editor menuitem[action=generate-upgrade]': {
                    click: this.onGenerateUpgradeBtnClick
                },
                'package-editor menuitem[action=upload-code]': {
                    click: this.onUploadBtnClick
                },
                'pkg-version-fieldset grid[marker=versions]': {
                    itemclick: this.onVersionsGridRowClick
                },
                'pkg-version-fieldset button[action=archive]': {
                    click: this.onArchiveButtonClick
                },
                'pkg-version-fieldset button[action=lock-unlock]': {
                    click: this.onLockUnLockBtnClick
                },
                'pkg-version-fieldset button[action=activate]': {
                    click: this.onActivateBtnClick
                },
                'pkg-version-fieldset button[action=support]': {
                    click: this.onSupportBtnClick
                },
                'pkg-version-fieldset button[action=delete]': {
                    click: this.onDeleteBtnClick
                },
                'pkg-database-access button[action=reverse-engineering]': {
                    click: this.onReverseEngineeringBtnClick
                }
            }
        )
    },

    initEditor: function(editor) {
        this.editor = editor;
    },

    onShowEditor: function(editor) {
        this.updateRedeployButton(editor);
        this.updateInstallButton(editor);
    },

    onGenerateDocsBtnClick: function(button) {
        button.setText('Generating...');
        button.setDisabled(true);

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/content/documentation/generate/example/' + this.editor.entityLookup),
            method: 'PUT',

            success: function(response, action) {
                button.setText('Gen Docs');
                button.setDisabled(false);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Documentations have been generated successfully.');
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Appeared server error, please see logs for more information.');
            }
        });
    },

    onBeforeVersionsStoreLoad: function(store, operation) {
        var packageVersionFieldSet = OPF.Ui.getCmp('pkg-version-fieldset');
        store.proxy.url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl(
            '/versions/' + packageVersionFieldSet.editPanel.packageId);
    },

    onProxyException: function(proxy, type, action, options, response) {
        OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
    },

    onArchiveButtonClick: function(btn) {
        var store = this.getVersionsStore();
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');

        var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/archive/') + instance.editPanel.packageId;
        sqAjaxGETRequest(url, function(resp){
            var response = Ext.decode(resp.responseText);
            if (response.success) {
                instance.editPanel.updatePackageVersionInfoByJsonData(response.data[0]);
                store.load();
            } else {
                Ext.Msg.alert('Error', response.message);
            }
        }, null);
    },

    onLockUnLockBtnClick: function(btn) {
        if (btn.locked) {
            this.unlockCurrentVersion();
        } else {
            this.lockCurrentVersion();
        }
    },

    onActivateBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        var me = this;
        Ext.Msg.show({
            title:'Activate',
            msg: 'Are you sure?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/activate/') +
                        instance.editPanel.packageId + '/' + instance.selectedArchivedVersion;
                    sqAjaxGETRequest(url, function(resp) {
                        var vo = Ext.decode(resp.responseText);
                        instance.editPanel.updatePackageVersionInfoByJsonData(vo.data[0].packageVersion);
                        instance.editPanel.managerLayout.navigationPanel.reload();

                        var selectedVersionIndex = null;
                        var currentVersionIndex = null;
                        var items = me.getVersionsStore().data.items;
                        Ext.each(items, function(item, index) {
                            if (item.data.current) {
                                currentVersionIndex = index;
                            }
                            if (item.data.version == instance.selectedArchivedVersion) {
                                selectedVersionIndex = index;
                            }
                        });
                        items[currentVersionIndex].data.current = false;
                        items[selectedVersionIndex].data.current = true;
                        instance.versionField.setValue(items[selectedVersionIndex].data.versionName);
                        instance.editPanel.packageCurrentVersion = items[selectedVersionIndex].data.version;

                        me.disableButtons(true);
                        instance.selectedArchivedVersion = items[selectedVersionIndex].data.version;
                    }, null);
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    onSupportBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        Ext.Msg.show({
            title:'Support Version',
            msg: 'Are You sure, You want to support this version?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/support/') +
                        instance.editPanel.packageId + '/' + instance.selectedArchivedVersion;
                    sqAjaxGETRequest(url, function(resp){
                        var vo = Ext.decode(resp.responseText);
                        var icon = vo.success ? Ext.MessageBox.INFO : Ext.MessageBox.ERROR;
                        Ext.Msg.show({
                            title:'Support Version',
                            msg: vo.message,
                            buttons: Ext.Msg.OK,
                            icon: icon
                        });
                        instance.editPanel.managerLayout.navigationPanel.reload();
                    }, null);
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    onDeleteBtnClick: function() {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        var me = this;
        Ext.Msg.show({
            title:'Delete',
            msg: 'Are you sure?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/version/') +
                        instance.editPanel.packageId + '/' + instance.selectedArchivedVersion;
                    sqAjaxDELETERequest(url, null, function(resp){
                        var vo = Ext.decode(resp.responseText);
                        var rowIndex = null;
                        Ext.each(me.getVersionsStore().data.items, function(item, index) {
                            if (item.data.version == instance.selectedArchivedVersion) {
                                rowIndex = index;
                            }
                        });
                        if (isNotEmpty(rowIndex)) {
                            me.getVersionsStore().removeAt(rowIndex);
                            me.disableButtons(true);
                        }
                    }, null);
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    onGenerateUpgradeBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');

        var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/generate/upgrade/') +
            instance.editPanel.packageId + '/' + instance.selectedArchivedVersion;
        sqAjaxGETRequest(url, function(resp){
            var vo = Ext.decode(resp.responseText);
            instance.editPanel.updatePackageVersionInfoByJsonData(vo.data[0]);
        }, null);
    },

    getPackageInstallationDialog: function() {
        var packageInstallationDialog = Ext.WindowMgr.get('packageInstallationDialog');
        if (OPF.isEmpty(packageInstallationDialog)) {
            packageInstallationDialog = Ext.create(
                'OPF.console.domain.dialog.PackageInstallationDialog', this.editor);
            Ext.WindowMgr.register(packageInstallationDialog);
        }
        return packageInstallationDialog;
    },

    showPackageInstallationDialog: function(associatedPackageId, installBtnId, uninstallBtnId) {
        var packageInstallationDialog = this.getPackageInstallationDialog();
        packageInstallationDialog.setAssociatedPackageId(associatedPackageId);
        packageInstallationDialog.editPanel = this.editor;
        packageInstallationDialog.showDialog();
        packageInstallationDialog.actionBtns = [installBtnId, uninstallBtnId];
    },

    getPackageUninstallationDialog: function() {
        var packageUninstallationDialog = Ext.WindowMgr.get('packageUninstallationDialog');
        if (OPF.isEmpty(packageUninstallationDialog)) {
            packageUninstallationDialog = Ext.create(
                'OPF.console.domain.dialog.PackageUninstallationDialog', this.editor);
            Ext.WindowMgr.register(packageUninstallationDialog);
        }
        return packageUninstallationDialog;
    },

    showPackageUninstallationDialog: function(associatedPackageId, installBtnId, uninstallBtnId) {
        var packageUninstallationDialog = this.getPackageUninstallationDialog();
        packageUninstallationDialog.setAssociatedPackageId(associatedPackageId);
        packageUninstallationDialog.editPanel = this.editor;
        packageUninstallationDialog.showDialog();
        packageUninstallationDialog.uninstallButton.enable();
        packageUninstallationDialog.actionBtns = [installBtnId, uninstallBtnId];
    },

    getDatabaseMigrationDialog: function() {
        var databaseMigrationDialog = Ext.WindowMgr.get('databaseMigrationDialog');
        if (OPF.isEmpty(databaseMigrationDialog)) {
            databaseMigrationDialog = Ext.create(
                'OPF.console.domain.dialog.DatabaseMigrationDialog', this.editor);
            Ext.WindowMgr.register(databaseMigrationDialog);
        }
        return databaseMigrationDialog;
    },

    showDatabaseMigrationDialog: function(associatedPackageId) {
        var databaseMigrationDialog = this.getDatabaseMigrationDialog();
        databaseMigrationDialog.setAssociatedPackageId(associatedPackageId);
        databaseMigrationDialog.editPanel = this.editor;
        databaseMigrationDialog.showDialog();
    },

    onRedeployBtnClick: function(btn) {
        OPF.Msg.setAlert(true, 'Redeploying...');
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/registry/installation/package/redeploy/' + this.editor.packageLookup),
            method: 'GET',

            success:function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    onInstallBtnClick: function(btn) {
        this.showPackageInstallationDialog(this.editor.packageId, this.editor.installButton.getId(), this.editor.installButton.getId());
    },
    onUninstallBtnClick: function(btn) {
        this.showPackageUninstallationDialog(this.editor.packageId, this.editor.installButton.getId(), this.editor.installButton.getId());
    },

    onMigrateBtnClick: function(btn) {
        this.showDatabaseMigrationDialog(this.editor.packageId);
    },

// onInstallBtnClick: function(btn) {
//        var instance = this;
//        var url = OPF.Cfg.restUrl('registry/installation/package/install/')
//            + instance.editor.packageId + '?databaseAction=recreate';
//        Ext.Ajax.request({
//            url: url,
//            method: 'GET',
//            jsonData: {},
//
//            success:function(response, action) {
//                var resetProgressBar = Ext.create('Ext.window.MessageBox');
//                resetProgressBar.wait('Package is installing...', 'Information', {
//                    interval: 500,
//                    increment: 10,
//                    text: 'Package is installing...'
//                });
//
//                var runner = new Ext.util.TaskRunner();
//                var stop = false;
//                var prevStatus = 'ERROR';
//                var task = {
//                    run: function() {
//                        if (!stop) {
//                            Ext.Ajax.request({
//                                url: OPF.Cfg.restUrl('/registry/system/status/war/') + instance.editor.systemId,
//                                method: 'GET',
//
//                                success:function(response, action) {
//                                    var resp = Ext.decode(response.responseText);
//                                    for (var i = 0; i < resp.data.length; i++) {
//                                        var packageContext = instance.editor.packageUrlPath;
//                                        if (packageContext.charAt(0) === '/') {
//                                            packageContext = packageContext.substr(1);
//                                        }
//                                        if (resp.data[i].context == packageContext) {
//                                            var status = resp.data[i].warStatus;
//                                            if (status == 'DONE') {
//                                                instance.editor.installButton.setText('Uninstall');
//                                                instance.editor.installButton.action  = 'uninstall';
//                                                resetProgressBar.hide();
//                                                stop = true;
//                                                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Package has been installed.');
//                                            } else if (status == 'ERROR' && prevStatus == 'WAIT') {
//                                                resetProgressBar.hide();
//                                                stop = true;
//                                                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'The error occurred during package installation.');
//                                            }
//                                            prevStatus = status;
//                                        }
//                                    }
//                                },
//
//                                failure:function(response) {
//                                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Can not retrieve installation status');
//                                }
//                            });
//                        } else {
//                            runner.stop(task);
//                        }
//                    },
//                    interval: 5000
//                };
//
//                var delayedTask = new Ext.util.DelayedTask(function(){
//                    runner.start(task);
//                });
//                delayedTask.delay(5000);
//            },
//
//            failure:function(response) {
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });
//    },
//
//    onUninstallBtnClick: function(btn) {
//        var instance = this;
//        var url = OPF.Cfg.restUrl('registry/installation/package/uninstall/')
//            + instance.editor.packageId;
//        Ext.Ajax.request({
//            url: url,
//            method: 'GET',
//            jsonData: {},
//
//            success:function(response, action) {
//                var vo = Ext.decode(response.responseText);
//                OPF.Msg.setAlert(true, vo.message);
//
//                instance.editor.installButton.setText('Install');
//                instance.editor.installButton.action  = 'install';
//            },
//
//            failure:function(response) {
//                OPF.Msg.setAlert(false, response.message);
//            }
//        });
//    },

    onGenerateCodeBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        var me = this;
        var editor = instance.editPanel;
        var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/generate/code/') + editor.packageId;
        sqAjaxGETRequest(url, function(resp){
            var vo = Ext.decode(resp.responseText);
            var packageVersion = vo.data[0];
            editor.updatePackageVersionInfoByJsonData(packageVersion);
            var fileEntries = packageVersion.fileVOs;
            for (var i = 0; i < fileEntries.length; i++) {
                if (fileEntries[i].type == 'APP_WAR') {
                    var systemId = editor.resourceFields.systemDropPanel.getValue();
                    editor.installEnabled = OPF.isNotEmpty(systemId);
                    if (editor.deployed == null) {
                        editor.deployed = false;
                    }
                    break;
                }
            }
            me.updateInstallButton(editor);
            me.updateRedeployButton(editor);
        }, null);
    },

    updateInstallButton: function(editor) {
        var isUpdateMode = editor.saveAs == 'update';
        editor.generateDocumentationButton.setDisabled(!isUpdateMode);
        if (editor.packageName == 'platform') {
            editor.installButton.hide();
            editor.migrateButton.hide();
        } else {
            if (editor.deployed == null) {
                editor.installButton.setDisabled(true);
                editor.migrateButton.setDisabled(true);
            } else {
                if (editor.deployed) {
                    editor.disableInstall();
                } else {
                    editor.installButton.setDisabled(!(isUpdateMode && editor.installEnabled));
                    editor.migrateButton.setDisabled(!(isUpdateMode && editor.installEnabled));
                }
            }
        }
    },

    updateRedeployButton: function(editor) {
        editor.redeployButton.setDisabled(editor.packageVersionFields.versionField.getValue() == '0.0.1');
    },

    onUploadBtnClick: function(btn) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        instance.editPanel.uploadFileDialog.fileTypes = ['war'];
        instance.editPanel.uploadFileDialog.show();
    },

    onVersionsGridRowClick: function(view, record, htmlElement, index, e, eOpts) {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        var isCurrent = OPF.isEmpty(record.data.current) ? false : record.data.current;
        instance.selectedArchivedVersion = record.data.version;
        this.disableButtons(isCurrent);
    },

    lockCurrentVersion: function() {
        var instance = OPF.Ui.getCmp('pkg-version-fieldset');
        var me = this;

        var url = OPF.core.utils.RegistryNodeType.PACKAGE.generateUrl('/lock/') + instance.editPanel.packageId;
        var jsonData = {
            data: {
                versionName: instance.versionField.getValue()
            }
        };
        sqAjaxPUTRequest(url, jsonData, function(resp){
            var vo = Ext.decode(resp.responseText);
            instance.versionField.setReadOnly(true);
            instance.unlockButton.locked = true;
            instance.unlockButton.setText('unlock');
            me.getVersionsStore().load();
        }, null);
    },

    unlockCurrentVersion: function() {
        var instance = this;
        var packageVersionFieldSet = OPF.Ui.getCmp('pkg-version-fieldset');
        Ext.Msg.show({
            title:'Confirmation',
            msg: 'Should the current version be archived before unlocking the version field for editing?',
            buttons: Ext.Msg.YESNOCANCEL,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    instance.onArchiveButtonClick();
                } else if (buttonId == 'no') {
                    packageVersionFieldSet.versionField.setReadOnly(false);
                    packageVersionFieldSet.unlockButton.locked = false;
                    packageVersionFieldSet.unlockButton.setText('lock');
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    onReverseEngineeringBtnClick: function() {
        var instance = OPF.Ui.getCmp('pkg-resource-access');

        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/reverse-engineering/') + instance.editPanel.packageId;
        sqAjaxGETRequest(url, function(resp){
            var vo = Ext.decode(resp.responseText);
            var navigationPanel = instance.editPanel.managerLayout.navigationPanel;
            var selectedNode = instance.editPanel.selfNode;
            navigationPanel.getStore().load({
                node: selectedNode,
                callback: function() {
                    selectedNode.expand();
                }
            });
            Ext.Msg.alert('Info', vo.message);
        }, null);
    },

    disableButtons: function(isDisabled) {
        var packageVersionFieldSet = OPF.Ui.getCmp('pkg-version-fieldset');
        var isSupported = packageVersionFieldSet.selectedArchivedVersion < packageVersionFieldSet.editPanel.packageCurrentVersion;
        packageVersionFieldSet.supportButton.setDisabled(isDisabled || !isSupported);
        packageVersionFieldSet.activateButton.setDisabled(isDisabled);
        packageVersionFieldSet.deleteButton.setDisabled(isDisabled);
        this.editor.generateUpgradeButton.setDisabled(isDisabled);
    }

});