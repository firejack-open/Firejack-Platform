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


/**
 *
 */
Ext.define('OPF.console.resource.view.ScheduleGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'SCHEDULE',
    iconCls: 'sm-schedule-icon',
    iconGridUrl: '/images/icons/16/schedule_16.png',
    entityName: 'Resource',
    registryNodeType: OPF.core.utils.RegistryNodeType.SCHEDULE,

    isRestStore: true,
    refreshTaskId: null,

    initComponent: function() {
        this.callParent(arguments);

        var me = this;

        Ext.TaskManager.start({
            run: me.refreshScheduleJobStatuses,
            interval: 15000,
            args: null,
            scope: me
        });

    },

    getColumns: function() {
        return [
            OPF.Ui.populateIconColumn16(30, 'schedule_16.png'),
            OPF.Ui.populateColumn('name', 'Name', {width: 250}),
            OPF.Ui.populateColumn('cronExpression', 'Cron Expression', {width: 100}),
            OPF.Ui.populateColumn('action', 'Action Lookup', {
                width: 300,
                renderer: function(value) {
                    return value.lookup;
                }
            }),
            OPF.Ui.populateColumn('description', 'Description', {flex: 1, minWidth: 150, renderer: 'htmlEncode'}),
            OPF.Ui.populateColumn('percent', 'Status', {
                width: 150,
                renderer: this.renderProgress
            }),
            OPF.Ui.populateColumn('deployed', 'Actions', {
                width: 170,
                renderer: this.renderInstall
            })
        ];
    },

    onItemDblClick: function(grid, record) {
        var me = this;

        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        var url = registryNodeType.generateGetUrl(record.get('id'));

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {

                var editPanel = me.managerLayout.tabPanel.getComponent(registryNodeType.type + 'EditPanel');
                if (OPF.isNotEmpty(editPanel)) {
                    if (editPanel.entityId == record.get('id')) {
                        me.managerLayout.tabPanel.setActiveTab(editPanel);
                        return;
                    } else {
                        me.managerLayout.tabPanel.remove(editPanel);
                    }
                }

                editPanel = registryNodeType.createEditPanel(me.managerLayout);
                editPanel.saveAs = 'update';

                var registryJsonData = Ext.decode(response.responseText);
                editPanel.showEditPanel(registryJsonData.data[0]);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    generateDeleteUrl: function(record) {
        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        return registryNodeType.generateGetUrl(record.get('id'));
    },

    renderInstall: function(value, metadata, record) {
        var me = this;
        var scheduleId = record.get('id');

        var extId1 = scheduleId + '-1-opf';
        Ext.Function.defer(me.ownerCt.createGridBtn, 50, this, ['Execute', extId1, record, 'install_16.png', {
            disabled: !record.get('active'),
            handler: function(btn, e) {
                me.ownerCt.executeScheduleJob(scheduleId, record);
            }
        }]);

        var extId2 = scheduleId + '-2-opf';
        Ext.Function.defer(me.ownerCt.createGridBtn, 50, this, ['History', extId2, record, 'install_16.png', {
            handler: function(btn, e) {
                me.ownerCt.showScheduleHistory(scheduleId, record);
            }
        }]);
        return('<div id="' + extId1 + '"><div id="' + extId2 + '">');
    },

    renderProgress: function (value, metadata, record) {
        var status;
        var scheduleId = record.get('id');
        var active = record.get('active');
        if (active) {
            if (OPF.isNotEmpty(value)) {
                status = 'IN PROGRESS: ' + value + '%';
            } else {
                status = 'ACTIVE';
            }
        } else {
            status = 'INACTIVE';
        }

        var id = Ext.id();
        Ext.defer(function () {
            Ext.widget('progressbar', {
                id: 'progressbar_' + scheduleId,
                renderTo: id,
                text: status,
                disabled: !active,
                value: value / 100,
                width: 140
            });
        }, 50);
        return Ext.String.format('<div id="{0}"></div>', id);
    },

    createGridBtn: function(value, id, record, icon, cfg) {
        var buttonId = 'btn-' + id;
        var gridButton = Ext.get(buttonId);
        if (OPF.isEmpty(gridButton)) {
            OPF.Ui.createGridBtn(value, id, record, icon, cfg)
        }
    },

    executeScheduleJob: function(scheduleId, record) {
        var me = this;

        Ext.Ajax.request({
            url: this.registryNodeType.generateUrl('/execute/' + scheduleId),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, resp.message);
                record.beginEdit();
                record.set('percent', 0);
                record.endEdit(true);
                me.refreshScheduleJobStatuses(null);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    showScheduleHistory: function(scheduleId, record) {
        if (OPF.isNotEmpty(this.scheduleHistoryGridView)) {
            this.managerLayout.tabPanel.remove(this.scheduleHistoryGridView);
        }
        this.scheduleHistoryGridView = new OPF.console.resource.view.ScheduleHistoryGridView(scheduleId, record, this.managerLayout);
        this.managerLayout.tabPanel.add(this.scheduleHistoryGridView);
        this.managerLayout.tabPanel.doLayout();
        this.managerLayout.tabPanel.setActiveTab(this.scheduleHistoryGridView);
    },

    storeLoad: function(store, records, successful, eOpts) {
        var needToStartRefresher = false;
        Ext.each(records, function(record) {
            var percent = record.get('percent');
            needToStartRefresher |= OPF.isNotEmpty(percent);
        });
        if (needToStartRefresher) {
            this.refreshScheduleJobStatuses(null);
        }
    },

    refreshScheduleJobStatuses: function(taskId) {
        var me = this;

        var needToExecute = false;
        if (OPF.isEmpty(this.refreshTaskId)) {
            this.refreshTaskId = (((1 + Math.random()) * 0x100000000) | 0).toString(16).substring(1);
            needToExecute = true;
        } else if (this.refreshTaskId == taskId) {
            needToExecute = true;
        }

        if (needToExecute) {
            var scheduleIdQuery = [];
            var records = this.store.getRange();
            Ext.each(records, function(record) {
                scheduleIdQuery.push('scheduleId=' + record.get('id'));
            });
            var queryParam = '';
            if (scheduleIdQuery.length > 0) {
                queryParam = '?' + scheduleIdQuery.join('&');
            }

            Ext.Ajax.request({
                url: this.registryNodeType.generateUrl('/statuses') + queryParam,
                method: 'GET',
                jsonData: '[]',
                async: false,

                success: function(response, action) {
                    var jsonData = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(true, jsonData.message);

                    var schedules = jsonData.data;
                    var needToContinue = false;
                    Ext.each(records, function(record) {
                        var scheduleId = record.get('id');
                        var foundSchedule = null;
                        Ext.each(schedules, function(schedule) {
                            if (scheduleId == schedule.id) {
                                foundSchedule = schedule;
                                needToContinue |= true;
                            }
                        });

                        var status;
                        var percent;
                        var active = record.get('active');
                        if (active) {
                            if (OPF.isNotEmpty(foundSchedule && foundSchedule.percent)) {
                                percent = foundSchedule.percent;
                                status = 'IN PROGRESS: ' + percent + '%';
                            } else {
                                status = 'ACTIVE';
                            }
                        } else {
                            status = 'INACTIVE';
                        }

                        var scheduleProgressBar = Ext.getCmp('progressbar_' + scheduleId);
                        if (scheduleProgressBar) {
                            scheduleProgressBar.updateProgress((percent ? percent / 100 : 0), status);
                        }

                        record.beginEdit();
                        record.set('percent', percent);
                        record.endEdit(true);
                    });
                    if (needToContinue) {
                        new Ext.util.DelayedTask( function() {
                            me.refreshScheduleJobStatuses(me.refreshTaskId);
                        }).delay(1000);
                    } else {
                        me.refreshTaskId = null;
                    }
                },

                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                    me.refreshTaskId = null;
                }
            });
        }
    }

});