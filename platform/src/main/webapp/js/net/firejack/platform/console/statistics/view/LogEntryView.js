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