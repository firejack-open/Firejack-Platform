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
    'OPF.prometheus.wizard.report.ReportWizard',
    'OPF.prometheus.wizard.bi_report_type.BIReportTypeWizard',
    'OPF.prometheus.wizard.entity.EntityWizard',
    'OPF.prometheus.wizard.relationship.RelationshipWizard',
    'OPF.prometheus.wizard.wizard.CreatorWizard',
    'OPF.prometheus.wizard.workflow.ProcessWizard',
    'OPF.prometheus.wizard.database.DatabaseWizard'
]);

Ext.define('OPF.prometheus.wizard.AddWizard', {
    extend: 'Ext.window.Window',
    alias: 'widget.prometheus.wizard.add-wizard',
    ui: 'wizards',

    statics: {
        id: 'addWizard'
    },

    title: 'What would you like to add?',

    width: 600,
    height: 660,
    draggable:false,
    shadow:false,
    resizable: false,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    modal: true,

    initComponent: function() {
        var me = this;

        this.items = [
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },

                flex: 1,
                defaults: {
                    ui: 'wizard',
                    iconAlign: 'top'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Page',
                        flex: 1,
                        disabled: true,
                        iconCls: 'page',
                        handler: function() {
                            // Progress bar testing
//                            var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
//                            progressBar.showStatus(50, 'Test progress message', null);

                            //Comment this msg for Progress bar testing
                            Ext.Msg.show({
                                title: 'Deploy is done',
                                closable: false,
                                msg: 'Press OK for refresh page.',
                                buttons: Ext.Msg.OK,
                                cls: 'wizard-progress-window-message'
                            });

                            me.close();
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Form',
                        flex: 1,
                        iconCls: 'form',
                        handler: function() {
                            me.showWizard(OPF.prometheus.wizard.entity.EntityWizard);
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Wizard',
                        flex: 1,
                        iconCls: 'wizard',
                        handler: function() {
                            me.showWizard(OPF.prometheus.wizard.wizard.CreatorWizard);
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                flex: 1,
                defaults: {
                    ui: 'wizard'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Report',
                        flex: 1,
                        iconCls: 'report',
                        handler: function() {
                            me.showWizard(OPF.prometheus.wizard.report.ReportWizard);
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Workflow',
                        flex: 1,
                        iconCls: 'process',
                        handler: function(btn) {
                            me.showWizard(OPF.prometheus.wizard.workflow.ProcessWizard);
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Database',
                        flex: 1,
                        iconCls: 'database',
                        handler: function(btn) {
                            me.showWizard(OPF.prometheus.wizard.database.DatabaseWizard);
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                flex: 1,
                defaults: {
                    ui: 'wizard'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Directory',
                        flex: 1,
                        disabled: true,
                        iconCls: 'directory'
                    },
                    {
                        xtype: 'button',
                        text: 'Relationship',
                        flex: 1,
//                        disabled: true,
                        iconCls: 'relationship',
                        handler: function(btn) {
                            me.showWizard(OPF.prometheus.wizard.relationship.RelationshipWizard);
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'BI Report Type',
                        flex: 1,
                        iconCls: 'report',
                        handler: function(btn) {
                            me.showWizard(OPF.prometheus.wizard.bi_report_type.BIReportTypeWizard);
                        }
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    showWizard: function(clazz) {
        var wizard = Ext.WindowMgr.get(clazz.id);
        if (!wizard) {
            wizard = Ext.create(clazz);
            Ext.WindowMgr.register(wizard);
        }
        wizard.show();
        var pos = wizard.getPosition();
//        if (pos[1] < 0) {
            wizard.setPosition(pos[0], 0);
//        }
        this.close();
    }

});

Ext.override(OPF.core.utils.progressbar.ProgressBarDialog, {
    setupComponent: function () {
        this.setWidth(600);
        this.setHeight(160);
        this.draggable=false;
        this.shadow=false;
        this.closable=false;
        this.titleAlign='center';
        this.title='Building your function...';
    }
});