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