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
Ext.define('OPF.console.statistics.view.LogEntryView', {
    extend: 'OPF.console.statistics.view.BaseMetricsTab',
    alias : 'widget.entry-logs',

    title: 'ENTRY LOGS',
    iconCls: 'sm-log-viewer-icon',
    rowIconUrl: '/images/icons/16/logentry_16.png',
    entityName: 'LogEntry',
    registryNodeType: OPF.core.utils.RegistryNodeType.LOG_ENTRY,

    getAdditionalColumns: function() {
        return [
            OPF.Ui.populateDateColumn('created', 'Timestamp', 140),
            OPF.Ui.populateColumn('lookup', 'Action', {
                flex: 1, minWidth: 150,
                renderer: OPF.console.statistics.view.BaseMetricsTab.lookupRenderer
            }),
            OPF.Ui.populateColumn('details', 'Detail', { flex: 1, minWidth: 100, renderer: 'htmlEncode' }),
            OPF.Ui.populateColumn('username', 'User', { width: 100 }),
            OPF.Ui.populateBooleanColumn('success', 'Status', { width: 80 }),
            OPF.Ui.populateColumn('executeTime', 'Time', {
                width: 100, align: 'right',
                renderer: OPF.console.statistics.view.BaseMetricsTab.timeRenderer
            })
        ];
    },

    getStoreName: function() {
        return 'LogEntryStore';
    },

    getToolbarAdditionalItems: function() {
        var instance = this;

        this.logEntryType = Ext.create('Ext.form.ComboBox', {
            store: Ext.create('Ext.data.Store', {
                fields: ['key', 'name'],
                data : [
                    {key: "ALL", name: "All"},
                    {key: "ACTION", name: "Actions"},
                    {key: "NAVIGATION", name: "Navigations"}
                ]
            }),
            queryMode: 'local',
            displayField: 'name',
            valueField: 'key',
            value: 'ALL'
        });

        return [
            {xtype: 'tbfill'},
            this.logEntryType
        ];
    }

});