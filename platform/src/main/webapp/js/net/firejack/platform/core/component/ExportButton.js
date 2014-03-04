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

Ext.define('OPF.core.component.ExportButton', {
    extend: 'Ext.button.Split',
    alias: 'widget.opf-export-button',

    buttonConfig: {},
    grid: null,
    strategies: [],

    initComponent: function() {
        var me = this;

        var menuItems = [];
        Ext.each(this.strategies, function(strategy, index) {
            menuItems.push({
                text: strategy.title,
                handler: function() {
                    me.exportData(strategy.strategy, strategy.filename);
                }
            });
            if (index == 0) {
                me.handler = function() {
                    me.exportData(strategy.strategy, strategy.filename);
                };
            }
        });

        this.menu = new Ext.menu.Menu({
            bodyCls: 'export',
            items: menuItems
        });

        this.callParent(arguments);
    },

    getQueryParams: function() {
        return null;
    },

    exportData: function(strategy, fileName) {
        var me = this;

        var fields = [];
        if (OPF.isNotEmpty(this.grid)) {
            Ext.each(this.grid.columns, function(column) {
                fields.push({
                    xtype: 'opf-hidden',
                    name: 'columns',
                    value: column.dataIndex
                });
            });
        }

        var urlQueryParams = '';
        var queryParams = this.getQueryParams();
        if (OPF.isNotEmpty(queryParams)) {
            var index = 0;
            for (name in queryParams) {
                if (index == 0) {
                    urlQueryParams += '?';
                }
                urlQueryParams += name + '=' + queryParams[name] + '&';
                index++;
            }
        }

        var exportForm = Ext.create('Ext.form.Panel', {
            url: OPF.Cfg.restUrl('site/export/' + strategy + '/' + fileName + urlQueryParams, true),
            monitorValid: true,
            standardSubmit: true, // do not use Ajax submit (default ExtJs)
            items: fields
        });
        exportForm.getForm().submit();
    }

});