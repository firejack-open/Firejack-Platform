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
Ext.define('OPF.console.statistics.view.BaseMetricsTab', {
    extend: 'Ext.panel.Panel',

    title: 'SYSTEM METRICS',
    layout: 'fit',
    padding: 10,
    border: false,
    iconCls: 'sm-metrics-viewer-icon',
    managerLayout: null,

    rowIconUrl: '/images/icons/16/tracking_16.png',
    entityName: 'MetricsEntry',
    registryNodeType: OPF.core.utils.RegistryNodeType.METRICS_ENTRY,

    constructor: function(managerLayout, cfg) {
        cfg = cfg || {};
        OPF.console.statistics.view.BaseMetricsTab.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.pageSize = 20;

        this.searchField = Ext.create('Ext.form.field.Text', {
            marked: true,
            width: 120,
            enableKeyEvents: true
        });

        this.startDateField = Ext.create('Ext.form.field.Date', {
            width: 80,
            enableKeyEvents: true
        });

        this.endDateField = Ext.create('Ext.form.field.Date', {
            width: 80,
            enableKeyEvents: true
        });

        this.startTimeField = Ext.create('Ext.form.field.Time', {
            width: 80,
            editable: false
        });

        this.endTimeField = Ext.create('Ext.form.field.Time', {
            width: 80,
            editable: false
        });

        var toolbarItems = [];
        toolbarItems.push(
            OPF.Ui.xSpacer(5),
            this.searchField,
            {
                xtype: 'displayfield',
                value: 'start'
            },
            OPF.Ui.xSpacer(5),
            this.startDateField,
            OPF.Ui.xSpacer(1),
            this.startTimeField,
            OPF.Ui.xSpacer(5),
            {
                xtype: 'displayfield',
                value: 'end'
            },
            OPF.Ui.xSpacer(5),
            this.endDateField,
            OPF.Ui.xSpacer(5),
            this.endTimeField,
            OPF.Ui.xSpacer(5),
            OPF.Ui.createBtn('reset', 55, 'reset')
        );

        var additionalToolbarItems = this.getToolbarAdditionalItems();
        if (additionalToolbarItems != null && Ext.isArray(additionalToolbarItems) && additionalToolbarItems.length > 0) {
            var i;
            for (i = 0; i < additionalToolbarItems.length;) {
                toolbarItems.push(additionalToolbarItems[i++]);
            }
        }

        this.bottomToolbar = Ext.create('Ext.toolbar.Paging', {
            pageSize: instance.pageSize,
            store: this.getStoreName(),
            displayInfo: true,
            displayMsg: instance.getPagerDisplayMsg(),
            emptyMsg: instance.getPagerEmptyMsg()
        });

        this.grid = Ext.create('Ext.grid.Panel', {
            title: this.entityName + ': ---',
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            flex: 1,
            store: this.getStoreName(),
            headerAsText: true,
            multiSelect: true,
            columns: this.getColumns(),
            tbar: toolbarItems,
            bbar: this.bottomToolbar
        });
        this.items = [
            this.grid
        ];

        this.callParent(arguments);
    },

    getStoreName: function() {
        return null;
    },

    getColumns: function() {
        var fullRowIconUrl = OPF.Cfg.OPF_CONSOLE_URL + this.rowIconUrl;
        var columns = [];
        columns.push({
            xtype: 'gridcolumn',
            header: '!',
            sortable: true,
            width: 30,
            renderer: function() {
                return '<img src="' + fullRowIconUrl + '">';
            }
        });
        var additionalColumns = this.getAdditionalColumns();
        if (additionalColumns != null && Ext.isArray(additionalColumns) && additionalColumns.length > 0) {
            var i;
            for (i = 0; i < additionalColumns.length;) {
                columns.push(additionalColumns[i++]);
            }
        }
        return columns;
    },

    getAdditionalColumns: function() {
        return [];
    },

    getToolbarAdditionalItems: function() {
        return [];
    },

    getPagerEmptyMsg: function() {
        return 'No logs to display';
    },

    getPagerDisplayMsg: function() {
        return 'Displaying logs {0} - {1} of {2}';
    },

    refreshFields: function(model) {
        this.grid.store.load({params:{start: 0, limit: this.pageSize}});
    }

});

OPF.console.statistics.view.BaseMetricsTab.lookupRenderer = function(value, metadata) {
    var subLookup = shortenTextToDotSeparator(value, 100);
    metadata.attr = 'ext:qtip="' + value + '"';
    return subLookup;
};

OPF.console.statistics.view.BaseMetricsTab.timeRenderer = function(value) {
    var valueDiv1000 = value / 1000;
    return Ext.util.Format.number(valueDiv1000, "?0,0.000?") + ' sec';
};