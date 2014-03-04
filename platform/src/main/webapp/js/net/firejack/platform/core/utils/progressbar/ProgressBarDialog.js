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