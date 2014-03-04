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
Ext.define('OPF.console.domain.dialog.PackageUninstallationDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.pkg-uninstallation-dlg',

    id: 'packageUninstallationDialog',
    title: 'Package Uninstallation',
    width: 600,
    padding: 10,
    bodyPadding: '0 10',
    modal: true,

    editPanel: null,
    associatedPackageId: null,
    actionBtns: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.dialog.PackageUninstallationDialog.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        this.databaseActionContainer = Ext.create('Ext.container.Container', {
        });

//        this.logViewerLabel = Ext.create('Ext.form.Label', {text: 'Logs'});
//
//        this.logViewer = Ext.create('Ext.container.Container', {
//            height: 200,
//            autoScroll: true
//        });
//
//        this.logViewerPanel = Ext.create('Ext.panel.Panel', {
//            layout: 'fit',
//            items: [
//                this.logViewer
//            ]
//        });

        this.uninstallButton = OPF.Ui.createBtn('Uninstall', 60, 'uninstall-pkg', {
            actionBtns: this.actionBtns
        });
        this.closeButton = OPF.Ui.createBtn('Close', 60, 'close-pkg-dlg');

        this.fbar = {
            xtype: 'toolbar',
            items: [
                this.uninstallButton,
                this.closeButton
            ]
        };

        this.items = [
            this.databaseActionContainer//,
//            this.logViewerLabel,
//            this.logViewerPanel
        ];

        this.callParent(arguments);
    },

    setAssociatedPackageId: function(associatedPackageId) {
        this.associatedPackageId = associatedPackageId;
    },

    showDialog: function() {
        var me = this;

        this.databaseActionContainer.removeAll();
        this.databaseActions = [];

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/database/associated-package/' + this.associatedPackageId),
            method: 'GET',
            jsonData: {},

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                var databases = resp.data;
                Ext.each(databases, function(database) {
                    var databaseAction = Ext.create('Ext.form.field.Checkbox', {
                        boxLabel: 'Drop ' + database.rdbms + ' database: ' + database.name,
                        name: 'action' + database.id,
                        databaseId: database.id
                    });
                    me.databaseActions.push(databaseAction);
                });
                me.databaseActionContainer.add(me.databaseActions);
                me.show();
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

//    showLogs: function(logBatch) {
//        if (OPF.isEmpty(logBatch)) {
//            return;
//        }
//        var me = this;
//        this.logViewerLabel.show();
//        this.logViewer.show();
//        var needToContinueLoadLogs = true;
//        var logs = logBatch.logs;
//        Ext.each(logs, function(log, index) {
//            me.addLog(log.logMessage, log.logLevel);
//            needToContinueLoadLogs &= log.next;
//        });
//
//        if (needToContinueLoadLogs) {
//            var logUUID = logBatch.uuid;
//            var task = new Ext.util.DelayedTask( function() {
//                me.loadLogs(logUUID)
//            });
//            task.delay(500);
//        }
//    },
//
//    addLog: function(message, logLevel) {
//        var logLevelCls = OPF.isNotBlank(logLevel) ? logLevel.toLowerCase() : 'none';
//        var log = Ext.create('Ext.container.Container', {
//            autoEl: {
//                tag: 'div',
//                html: message
//            },
//            cls: 'progress-bar-' + logLevelCls
//        });
//        this.logViewer.add(log);
//        var logContainer = this.logViewer.el.dom;
//        logContainer.scrollTop = logContainer.scrollHeight;
//    },
//
//    loadLogs: function(logUUID) {
//        if (OPF.isNotEmpty(logUUID)) {
//            var me = this;
//            var url = OPF.Cfg.restUrl('registry/installation/package/log?logUUID=' + logUUID);
//            Ext.Ajax.request({
//                url: url,
//                method: 'GET',
//                jsonData: {},
//
//                success:function(response, action) {
//                    var resp = Ext.decode(response.responseText);
//                    var logBatch = resp.data[0];
//                    me.showLogs(logBatch);
//                },
//
//                failure:function(response) {
//                    OPF.Msg.setAlert(false, response.message);
//                }
//            });
//        }
//    }

});