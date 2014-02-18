Ext.define('OPF.prometheus.utils.RedeploySite', {
    extend: 'Ext.Base',

    packageLookup: OPF.Cfg.PACKAGE_LOOKUP,

    save: function(url, jsonData, isDeploy) {
        var me = this;

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            jsonData: {"data": jsonData},

            success:function(response, action) {
                var responseData = Ext.decode(response.responseText);
                if (responseData.success) {
                    OPF.Msg.setAlert(true, responseData.message);
                    me.getEl().unmask();
                    me.close();

                    if (isDeploy) {
                        me.redeploy();
                    } else {
                        me.refreshDashboard();
                    }
                } else {
                    OPF.Msg.setAlert(false, responseData.message);
                    me.getEl().unmask();

                    me.deployMessagePanel.setNoticeContainer([{
                        level: OPF.core.validation.MessageLevel.ERROR,
                        msg: responseData.message
                    }]);
                }
            },

            failure:function(response) {
                me.getEl().unmask();
                OPF.Msg.setAlert(false, response.message);

                me.deployMessagePanel.setNoticeContainer([{
                    level: OPF.core.validation.MessageLevel.ERROR,
                    msg: response.message
                }]);
            }
        });
    },

    redeploy: function() {
        var me = this;

        OPF.Msg.setAlert(true, 'Redeploying...');
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/registry/installation/package/redeploy/' + this.packageLookup),
            method: 'GET',

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, responseData.message);

                me.showDoneRedeployDialog();
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
                me.showRestartProgressBar();
            }
        });
    },

    showDoneRedeployDialog: function() {
        Ext.Msg.show({
            title: 'Deploy is done',
            msg: 'Press OK for refresh page.',
            closable: false,
            buttons: Ext.Msg.OK,
            cls: 'wizard-progress-window-message',
            fn: function(buttonId) {
                document.location.reload(true);
            }
        });

        var secs = 6;
        var runner = new Ext.util.TaskRunner();
        var task = runner.start({
            run: function () {
                var message;
                if (--secs > 0) {
                    message = 'OK (' + secs + ')';
                } else {
                    message = 'Refreshing...';
                }
                Ext.Msg.msgButtons['ok'].setText(message);
            },
            interval: 1000
        });

        Ext.Function.defer(function() {
            document.location.reload(true);
        }, 5000, Ext.Msg);
    },

    showRestartProgressBar: function() {
        var me = this;

        var progressBar = new Ext.ProgressBar();

        var resetDialog = Ext.create('Ext.window.Window', {
            titleAlign: 'center',
            title: 'Restart Server Progress ...',
            modal: true,
            closable: false,
            resizable: false,
            cls: 'wizard-progress-bar',
            width: 600,
            height: 160,
            items: [
                progressBar
            ]
        });
        resetDialog.show();

        progressBar.wait({
            interval: 1000,
            increment: 10,
            text: 'Restarting...'
        });

        new Ext.util.DelayedTask( function() {
            me.checkApplicationStatus(resetDialog);
        }).delay(5000);
    },

    checkApplicationStatus: function(resetDialog) {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.fullUrl('/status/war', true),
            method: 'GET',

            success: function(response, action) {
                consoleLog('Response status = ' + response.status);
                resetDialog.close();
                me.showDoneRedeployDialog();
            },

            failure:function(response) {
                consoleLog('Response status = ' + response.status);

                new Ext.util.DelayedTask( function() {
                    me.checkApplicationStatus(resetDialog);
                }).delay(5000);
            }
        });
    },

    refreshDashboard: function() {
        var toolbarComponent = null;
        Ext.ComponentMgr.each(function(key, component) {
            if ('prometheus.component.toolbar-component' == component.xtype) {
                toolbarComponent = component;
            }
        });
        if (toolbarComponent != null) {
            toolbarComponent.refreshDashboardButton();
        }
    }

});