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
Ext.define('OPF.console.resource.view.ScheduleHistoryGridView', {
    extend: 'Ext.panel.Panel',

    layout: 'fit',
    padding: 10,
    border: false,

    managerLayout: null,

    id: 'scheduleHistoryGridView',
    title: 'SCHEDULE HISTORY',
    iconCls: 'sm-schedule-icon',
    iconGridUrl: '/images/icons/16/schedule-history_16.png',
    closable: true,

    scheduleId: null,
    schedule: null,

    isRestStore: true,

    constructor: function(scheduleId, schedule, managerLayout, cfg) {
        cfg = cfg || {};
        OPF.console.resource.view.ScheduleHistoryGridView.superclass.constructor.call(this, Ext.apply({
            scheduleId: scheduleId,
            schedule: schedule,
            managerLayout: managerLayout
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'OPF.console.resource.model.ScheduleHistoryModel',
            proxy: {
                type: 'rest',
                url : OPF.Cfg.restUrl('/schedule/schedule-history/schedule/' + this.scheduleId),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                },
                writer: {
                    type: 'json',
                    root: 'data'
                },
                simpleSortMode: true,
                startParam: 'offset',
                limitParam: 'limit',
                sortParam: 'sortColumn',
                directionParam: 'sortDirection'
            },
            remoteSort: true,
            sorters: [
                {
                    property: 'startTime',
                    direction: 'DESC'
                }
            ]
        });

        this.grid = Ext.create('Ext.grid.Panel', {
            title: 'Schedule History List for : ' + this.schedule.get('name'),
            flex: 1,
            store: this.store,
            headerAsText: true,
            columns: [
                OPF.Ui.populateIconColumn16(30, 'schedule-history_16.png'),
                OPF.Ui.populateColumn('startTime', 'Start Time', {
                    width: 150,
                    renderer: function(value, metadata, record) {
                        return OPF.convertDatetime(value);
                    }
                }),
                OPF.Ui.populateColumn('endTime', 'End Time', {
                    width: 150,
                    renderer: function(value, metadata, record) {
                        return OPF.convertDatetime(value);
                    }
                }),
                OPF.Ui.populateColumn('executionTime', 'Execution Time', {
                    width: 150,
                    renderer: function(value, metadata, record) {
                        var startTime = record.get('startTime');
                        var endTime = record.get('endTime');
                        var diffTime = 'in progress...';
                        if (OPF.isNotEmpty(startTime) && OPF.isNotEmpty(endTime)) {
                            var diff = endTime - startTime, milliseconds, seconds, minutes, hours, days;
                            diff = (diff - (milliseconds = diff % 1000)) / 1000;
                            diff = (diff - (seconds = diff % 60)) / 60;
                            diff = (diff - (minutes = diff % 60)) / 60;
                            days = (diff - (hours = diff % 24)) / 24;

                            diffTime =
                            (days > 0 ? days + 'd, ' : '') +
                            (days > 0 || hours > 0 ? hours + 'h, ' : '') +
                            (days > 0 || hours > 0 || minutes > 0 ? minutes + 'm, ' : '') +
                            (days > 0 || hours > 0 || minutes > 0 || seconds > 0 ? seconds + 's' : '');
                            diffTime = OPF.ifBlank(diffTime, '< 1s');
                        }
                        return diffTime;
                    }
                }),
                OPF.Ui.populateColumn('schedule', 'Scheduled Job Name', {
                    width: 200,
                    renderer: function(value) {
                        return value.name;
                    }
                }),
                OPF.Ui.populateColumn('user', 'User', {
                    width: 100,
                    renderer: function(value) {
                        return OPF.isNotEmpty(value) ? value.username : 'unknown';
                    }
                }),
                OPF.Ui.populateColumn('success', 'Is Success', {
                    width: 60,
                    renderer: function(value, metadata, record) {
                        var endTime = record.get('endTime');
                        return OPF.isNotEmpty(endTime) ? value : '';
                    }
                }),
                OPF.Ui.populateColumn('message', 'Message', {flex: 1, minWidth: 150, renderer: 'htmlEncode'})
            ],
            bbar: Ext.create('Ext.PagingToolbar', {
                store: this.store,
                displayInfo: true,
                displayMsg: 'Displaying topics {0} - {1} of {2}',
                emptyMsg: "No topics to display"
            }),
            listeners: {
                itemdblclick: function(grid, record) {
                    instance.onItemDblClick(grid, record);
                }
            }
        });

        this.items = [
            this.grid
        ];

        this.callParent(arguments);
    }

});