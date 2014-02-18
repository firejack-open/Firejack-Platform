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
    'OPF.prometheus.component.manager.dialog.PerformActivityActionDialog'
]);

Ext.define('OPF.prometheus.component.manager.WorkflowRecordComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.workflow-record-component',

    border: true,
    cls: 'workflow-record-panel',

    formComponent: null,
    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.callParent(arguments);
    },

    loadWorkflowData: function(recordId) {
        var me = this;

        this.hide();
        this.removeAll();

        var modelLookup = this.formComponent.modelInstance.self.lookup;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/process/workflow/available/record/' + modelLookup + '/' + recordId, false),
            method: 'GET',

            success: function(response, action) {
                var responseData = Ext.decode(response.responseText);
                if (responseData.success) {
                    var processToolbars = [];
                    var processes = responseData.data;
                    Ext.each(processes, function(process) {
                        processToolbars.push({
                            xtype: 'toolbar',
                            dock: 'top',
                            ui: 'footer',
                            items: [
                                {
                                    xtype: 'tbtext',
                                    text: process.name
                                },
                                '->',
                                {
                                    xtype: 'button',
                                    cls: 'launch-process-btn',
                                    text: 'Launch',
                                    handler: function() {
                                        me.launchProcess(process.id, modelLookup, recordId);
                                    }
                                }
                            ]
                        });
                    });
                    if (processToolbars.length > 0) {
                        me.add({
                            xtype: 'container',
                            html: 'Exists Workflow For Launch'
                        });
                        me.add(processToolbars);
                        me.show();
                    }
                } else {
                    OPF.Msg.setAlert(false, responseData.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    launchProcess: function(processId, entityLookup, recordId) {
        var me = this;

        var url = OPF.Cfg.restUrl('/process/workflow/task/record', false);
        url = OPF.Cfg.addParameterToURL(url, 'recordId', recordId);
        url = OPF.Cfg.addParameterToURL(url, 'entityLookup', entityLookup);
        url = OPF.Cfg.addParameterToURL(url, 'processId', processId);

        Ext.Ajax.request({
            url: url,
            method: 'POST',

            success:function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (resp.success) {
                    document.location.href = OPF.Cfg.fullUrl('inbox', true);
                }
            },

            failure:function(response) {
                me.formComponent.validator.showValidationMessages(response);
            }
        });
    }

});