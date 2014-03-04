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