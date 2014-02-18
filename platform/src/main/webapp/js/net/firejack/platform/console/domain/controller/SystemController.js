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

Ext.define('OPF.console.domain.controller.SystemController', {
    extend: 'Ext.app.Controller',

    views: ['system.AssociatedPackagesFieldSet', 'system.SystemEditor'],

    stores: ['AssociatedPackages'],

    models: ['Database', 'FilestoreModel', 'Server', 'SystemModel'],

    refs: [
        {
            ref: 'packageEditor',
            selector: 'package-editor'
        },
        {
            ref: 'packageInstallationDialog',
            selector: 'pkg-installation-dlg'
        },
        {
            ref: 'packageUninstallationDialog',
            selector: 'pkg-uninstallation-dlg'
        },
        {
            ref: 'databaseMigrationDialog',
            selector: 'database-migration-dlg'
        }
    ],

    init: function() {
//        this.getDatabasesStore().addListener('beforeload', this.onBeforeDatabasesStoreLoad, this);
        this.control(
            {
                'system-editor button[action=reset]': {
                    click: this.onResetBtnClick
                },
                'package-editor menuitem[action=reset]': {
                    click: this.onResetBtnClick
                },
                'associated-pkg-fieldset button[action=install-pkg]': {
                    click: this.onInstallAssociatedPackageBtnClick
                },
                'pkg-installation-dlg': {
                    show: this.onPackageInstallationDialogShow
                },
//                'pkg-installation-dlg opf-combo[name=databaseCombo]': {
//                    change: this.onInstallationDatabaseComboValueChange
//                },
                'pkg-installation-dlg button[action=install-pkg]': {
                    click: this.onInstallPkgBtnClick
                },
                'pkg-installation-dlg button[action=close-pkg-dlg]': {
                    click: this.onCloseInstallPkgBtnCLick
                },
                'pkg-uninstallation-dlg button[action=uninstall-pkg]': {
                    click: this.onUninstallPkgBtnClick
                },
                'pkg-uninstallation-dlg button[action=close-pkg-dlg]': {
                    click: this.onCloseUninstallPkgBtnCLick
                },
                'database-migration-dlg button[action=migrate-db]': {
                    click: this.onMigrateDatabaseBtnClick
                },
                'database-migration-dlg button[action=close-dlg]': {
                    click: this.onCloseMigrateDatabaseBtnCLick
                },
                'database-migration-dlg': {
                    show: this.onDatabaseMigrateDialogShow
                }
            }
        )
    },

    onResetBtnClick: function(button) {
        var me = this;

        Ext.Msg.show({
            title: 'Reset servers',
            msg: 'Are you sure?',
            buttons: Ext.Msg.YESNO,
            fn: function(buttonId) {
                if (buttonId == 'yes') {
                    Ext.Ajax.request({
                        url: OPF.Cfg.restUrl('/registry/system/restart'),
                        method: 'GET',

                        success:function(response, action) {
                            OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Please wait some minutes while servers is restarting.');
                            me.showResetProgressBar(button.systemId);
                        },

                        failure:function(response) {
                            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Appeared server error, please see logs for more information.');
                        }
                    });
                }
            },
            animEl: 'elId',
            icon: Ext.MessageBox.QUESTION
        });
    },

    onPackageInstallationDialogShow: function(win) {
        win.installButton.enable();
//        win.logViewer.removeAll();
    },

//    onInstallationDatabaseComboValueChange: function(combo, newValue, oldValue) {
//        if (OPF.isNotEmpty(newValue)) {
//            var installBtn = OPF.Ui.getCmp(
//                'pkg-installation-dlg button[action=install-pkg]');
//            installBtn.enable();
//        }
//    },

    onInstallAssociatedPackageBtnClick: function(btn) {
        var rec = getAssociatedPackagesStore().getAt(rowIndex);
        var associatedPackageId = rec.get('id');
        var dialog = OPF.Ui.getCmp('associated-pkg-fieldset');
        dialog.showPackageInstallationDialog(associatedPackageId);
    },

    onInstallPkgBtnClick: function(btn) {
        var dialog = this.getPackageInstallationDialog();
        btn.disable();

        var databaseActionData = [];
        Ext.each(dialog.databaseActions, function(databaseAction) {
            databaseActionData.push({
                id: databaseAction.databaseId,
                action: databaseAction.getValue()['action' + databaseAction.databaseId]
            });
        });

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/installation/package/install'),
            method: 'POST',
            jsonData: {
                data: {
                    id: dialog.associatedPackageId,
                    databaseActions: databaseActionData
                }
            },

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                dialog.editPanel.disableInstall();
                dialog.close();
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    onCloseInstallPkgBtnCLick: function(btn) {
        this.getPackageInstallationDialog().hide();
    },

    onUninstallPkgBtnClick: function(btn) {
        var dialog = this.getPackageUninstallationDialog();

        var databaseActionData = [];
        Ext.each(dialog.databaseActions, function(databaseAction) {
            databaseActionData.push({
                id: databaseAction.databaseId,
                action: databaseAction.getValue() === true ? 'DROP' : 'NONE'
            });
        });

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/installation/package/uninstall'),
            method: 'POST',
            jsonData: {
                data: {
                    id: dialog.associatedPackageId,
                    databaseActions: databaseActionData
                }
            },

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                dialog.editPanel.enableInstall();
                dialog.close();
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    onCloseUninstallPkgBtnCLick: function(btn) {
        this.getPackageUninstallationDialog().hide();
    },

    onMigrateDatabaseBtnClick: function(btn) {
        var dialog = this.getDatabaseMigrationDialog();
        btn.disable();

        var databaseActionData = [{
            id: dialog.targetDatabaseCombo.getValue(),
            sourceId: dialog.sourceDatabaseCombo.getValue(),
            action: 'MIGRATE'
        }];

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/installation/package/migrate'),
            method: 'POST',
            jsonData: {
                data: {
                    id: dialog.associatedPackageId,
                    databaseActions: databaseActionData
                }
            },

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
//                dialog.showLogs(resp.data[0]);
//                dialog.editPanel.disableInstall(dialog.actionBtns);
                dialog.close();
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    onCloseMigrateDatabaseBtnCLick: function(btn) {
        this.getDatabaseMigrationDialog().hide();
    },

    onDatabaseMigrateDialogShow: function(win) {
        win.migrateButton.enable();
//        win.logViewer.removeAll();
    },

    showResetProgressBar: function(systemId) {
        var progressBar = Ext.create('Ext.ProgressBar', {});
        progressBar.wait({
            interval: 500,
            increment: 10,
            text: 'Restarting...'
        });

        var statusMessage = Ext.create('Ext.container.Container', {
            html: 'Restarting...',
            cls: 'status-message',
            hidden: true
        });

        var resetDialog = Ext.create('Ext.window.Window', {
            title: 'Restart Server',
            cls: 'popup info',
            width: 400,
            modal: true,
//            closable: false,
            items: [
                progressBar,
                statusMessage
            ],
            listeners: {
                close: function() {
                    runner.stop(task);
                }
            }
        });
        resetDialog.show();

        var statuses = new Ext.util.MixedCollection();

        var task = {
            run: function() {
                Ext.Ajax.request({
                    url: OPF.core.utils.RegistryNodeType.SYSTEM.generateUrl('/status/war/' + systemId),
                    method: 'GET',

                    success:function(response, action) {
                        var resp = Ext.decode(response.responseText);
                        OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Server has been restarted.');
                        console.log(resp);

                        var restartStatus = 'WAIT';
                        Ext.each(resp.data, function(data) {
                            var key = data.context + '_' + data.host;
                            var status = data.warStatus;

                            var prevStatus = statuses.get(key);
                            if (prevStatus != null && prevStatus == 'WAIT' && status == 'ERROR') {
                                restartStatus = 'ERROR';
                                return false;
                            }
                            statuses.add(key, status);
                            return true;
                        });

                        if (restartStatus == 'ERROR') {
//                            runner.stop(task);
                            progressBar.hide();
                            var errorMessage = '';
                            Ext.each(resp.data, function(data) {
                                var key = data.context + '_' + data.host;
                                var status = data.warStatus;
                                errorMessage +=
                                    '<span>Application \'' + data.context + '\' on the sever \'' + data.host + '\' has status: ' +
                                        '<span class="status-' + status.toLowerCase() + '">\'' + status + '\'</span>' +
                                    '</span></br>';
                            });
                            errorMessage = '<div class="main-status-message status-error">Occurred error!</div>' + errorMessage;
                            statusMessage.show();
                            statusMessage.update(errorMessage);
                        } else {
                            var isAllDone = true;
                            statuses.eachKey(function(key, status) {
                                isAllDone &= status == 'DONE';
                            });
                            if (isAllDone) {
                                runner.stop(task);
                                progressBar.hide();
                                statusMessage.show();
                                statusMessage.update('<div class="main-status-message status-done">Restart is done!</div>');
                                new Ext.util.DelayedTask(function() {
                                    resetDialog.close();
                                }).delay(2000);
                            } else {
                                progressBar.updateText('Restarting...');
                            }
                        }
                    },

                    failure:function(response) {
                        OPF.Msg.setAlert(OPF.Msg.STATUS_OK, 'Server is restarting...');
                        console.log('failure');
                    }
                });
            },
            interval: 5000
        };

        var runner = new Ext.util.TaskRunner();
        var delayedTask = new Ext.util.DelayedTask(function(){
            runner.start(task);
        });
        delayedTask.delay(10000);
    }

});