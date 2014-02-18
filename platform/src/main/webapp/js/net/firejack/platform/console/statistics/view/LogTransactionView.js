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
Ext.define('OPF.console.statistics.view.LogTransactionView', {
    extend: 'OPF.console.statistics.view.BaseMetricsTab',
    alias : 'widget.transaction-logs',

    title: 'TRANSACTION LOGS',
    iconCls: 'sm-log-viewer-icon',
    rowIconUrl: '/images/icons/16/logentry_16.png',
    entityName: 'LogTransaction',
    registryNodeType: OPF.core.utils.RegistryNodeType.LOG_TRANSACTION,
    transactionLogGroupLevel: 'HOUR',

    getAdditionalColumns: function() {
        return [
            OPF.Ui.populateDateColumn('startTime', 'Start', 120, { sortable: false }),
            OPF.Ui.populateDateColumn('endTime', 'End', 120, { sortable: false }),
            OPF.Ui.populateColumn('packageLookup', 'Package Lookup', {
                width: 140,
                renderer: OPF.console.statistics.view.BaseMetricsTab.lookupRenderer
            }),
            OPF.Ui.populateNumberColumn('transactions', 'Transactions', 80, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('entitiesLoaded', 'Entities Loaded', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('entitiesUpdated', 'Entities Updated', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('entitiesInserted', 'Entities Inserted', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('entitiesDeleted', 'Entities Deleted', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('entitiesFetched', 'Entities Fetched', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('collectionsLoaded', 'Collections Loaded', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('collectionsUpdated', 'Collections Updated', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('collectionsRecreated', 'Collections Recreated', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('collectionsRemoved', 'Collections Removed', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('collectionsFetched', 'Collections Fetched', 120, { align: 'right', format: '?0?' }),
            OPF.Ui.populateNumberColumn('maxQueryTime', 'Max Query Time', 120, { align: 'right', format: '?0?' })
        ];
    },

    getToolbarAdditionalItems: function() {
        var instance = this;

        return [
            {xtype: 'tbfill'},
            instance.populateToggleButton('Hour', {pressed: true}),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Day'),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Week'),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Month')
        ];
    },

    getStoreName: function() {
        return 'LogTransactionStore';
    },

    populateToggleButton: function(title, cfg) {
        cfg = cfg || {};
        var groupLevel = title.toUpperCase();
        return Ext.create('Ext.button.Button', Ext.apply({
            text: title,
            enableToggle: true,
            toggleGroup: 'detail',
            action: 'group-by-time',
            groupLevel: groupLevel
        }, cfg));
    }

});