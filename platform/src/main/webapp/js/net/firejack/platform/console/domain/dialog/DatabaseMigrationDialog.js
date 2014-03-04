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
Ext.define('OPF.console.domain.dialog.DatabaseMigrationDialog', {
    extend: 'Ext.window.Window',
    alias: 'widget.database-migration-dlg',

    id: 'databaseMigrationDialog',
    title: 'Database Migration',
    width: 600,
    padding: 10,
    bodyPadding: '0 10',
    modal: true,

    editPanel: null,
    associatedPackageId: null,
    actionBtns: [],
    logs: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.dialog.DatabaseMigrationDialog.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.sourceDatabaseStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.Database',
            proxy: {
                type: 'ajax',
                url : OPF.Cfg.restUrl('registry/database/associated-package/' + me.associatedPackageId),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            },
            listeners: {
                beforeload: function(store, operation) {
                    store.proxy.url = OPF.Cfg.restUrl('registry/database/associated-package/' + me.associatedPackageId);
                }
            }
        });

        this.sourceDatabaseCombo = Ext.create('OPF.core.component.ComboBox', {
            store: this.sourceDatabaseStore,
            anchor: '100%',
            name: 'sourceDatabaseCombo',
            labelAlign: 'top',
            fieldLabel: 'Source Database',
            subFieldLabel: 'Select source database for migration',
            valueField: 'id',
            displayField: 'name'
        });

        this.targetDatabaseStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.Database',
            proxy: {
                type: 'ajax',
                url : OPF.Cfg.restUrl('registry/database/not-associated'),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            }
        });

        this.targetDatabaseCombo = Ext.create('OPF.core.component.ComboBox', {
            store: this.targetDatabaseStore,
            anchor: '100%',
            name: 'targetDatabaseCombo',
            labelAlign: 'top',
            fieldLabel: 'Target Database',
            subFieldLabel: 'Select target database for migration',
            valueField: 'id',
            displayField: 'name'
        });

        this.databaseActionContainer = Ext.create('Ext.container.Container', {
            layout: 'anchor',
            items: [
                this.sourceDatabaseCombo,
                this.targetDatabaseCombo
            ]
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

        this.migrateButton = OPF.Ui.createBtn('Migrate', 60, 'migrate-db', {
            actionBtns: this.actionBtns
        });

        this.closeButton = OPF.Ui.createBtn('Close', 60, 'close-dlg');

        this.fbar = {
            xtype: 'toolbar',
            items: [
                this.migrateButton,
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

    showDialog: function() {
        this.show();
        this.sourceDatabaseStore.load();
        this.targetDatabaseStore.load();
    },

    hide: function() {
        this.callParent(arguments);
        this.sourceDatabaseCombo.clearValue();
        this.targetDatabaseCombo.clearValue();
    },

    setAssociatedPackageId: function(associatedPackageId) {
        this.associatedPackageId = associatedPackageId;
    }//,

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