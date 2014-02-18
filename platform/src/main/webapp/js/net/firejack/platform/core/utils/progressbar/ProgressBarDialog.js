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

Ext.define('OPF.core.utils.progressbar.ProgressBarDialog', {
    extend: 'Ext.window.Window',

    id: 'socketProgressBar',
    modal: true,
    closable: true,
    closeAction: 'hide',
    resizable: false,
    cls: 'wizard-progress-bar',
    width: 400,

    title: 'Progress ...',
    logs: [],

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.utils.progressbar.ProgressBarDialog.superclass.constructor.call(this, Ext.apply({

        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.progressBar = new Ext.ProgressBar({
            text:'Initializing...'
        });

        this.logStatusField = Ext.create('Ext.container.Container', {
            height: 200,
            autoScroll: true
        });

        this.logStatusPanel = Ext.create('Ext.panel.Panel', {
            title: 'More information',
            collapsible: true,
//            collapsed: true,
            width: 388,
            layout: 'fit',
            hidden: true,
            items: [
                this.logStatusField
            ]
        });

        this.items = [
            this.progressBar,
            this.logStatusPanel
        ];

        this.setupComponent();

        this.callParent(arguments);
    },

    listeners: {
        beforeshow: function(win) {
            win.resetProgressBar();
        },
        beforeclose: function(win) {
            win.resetProgressBar();
            interruptProgress();
        }
    },

    setupComponent: function() {
        // need this method for override any properties in prometheus generated project
    },

    showStatus: function(percent, message, logLevel) {
        if (!this.isVisible()) {
            this.show();
        }

        consoleLog('Progress status is ' + percent + '% - ' + logLevel + ': ' + message);

        percent = percent / 100;
        if (percent > 1) {
            percent = 1;
        }

        this.progressBar.updateProgress(percent, message, true);
        this.addLog(message, logLevel);
    },

    showCompletedStatus: function(message, logLevel) {
        if (!this.isVisible()) {
            this.show();
        }

        consoleLog('Progress status is 100% - ' + logLevel + ': ' + message);

        this.progressBar.updateProgress(1, message, true);
        this.addLog(message, logLevel);

        if (this.logStatusPanel.collapsed || !this.logStatusPanel.isVisible()) {
            this.resetProgressBar();
            this.close();
        }
    },

    showErrorMessage: function(message) {
        if (this.logStatusPanel.collapsed) {
            this.resetProgressBar();
            this.close();
            OPF.Msg.setAlert(false, message);
        } else {
            this.addLog(message, 'ERROR');
        }
    },

    resetProgressBar: function() {
        this.logs = [];
        this.progressBar.reset();
        this.logStatusField.removeAll();
    },

    addLog: function(message, logLevel) {
        var me = this;
        if (this.logStatusPanel.isVisible()) {
            var logLevelCls = OPF.isNotBlank(logLevel) ? logLevel.toLowerCase() : 'none';
            var log = Ext.create('Ext.container.Container', {
                autoEl: {
                    tag: 'div',
                    html: message
                },
                cls: 'progress-bar-' + logLevelCls
            });
            this.logs.push(log);

            if (OPF.isNotEmpty(this.logStatusField.el)) {
                Ext.each(this.logs, function(log) {
                    me.logStatusField.add(log);
                });
                this.logs = [];

                var logContainer = this.logStatusField.el.dom;
                logContainer.scrollTop = logContainer.scrollHeight;
            }
        }
    },

    setLogVisible: function(showLogs) {
        this.logStatusPanel.setVisible(showLogs && OPF.Cfg.DEBUG_MODE);
    }

});

OPF.core.utils.progressbar.ProgressBarDialog.get = function() {
    var progressBar = Ext.WindowMgr.get('socketProgressBar');
    if (OPF.isEmpty(progressBar)) {
        progressBar = Ext.create('OPF.core.utils.progressbar.ProgressBarDialog');
        Ext.WindowMgr.register(progressBar);
    }
    return progressBar;
};