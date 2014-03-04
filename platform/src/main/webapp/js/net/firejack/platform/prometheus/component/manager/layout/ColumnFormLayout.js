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

Ext.define('OPF.prometheus.component.manager.layout.ColumnFormLayout', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.manager.layout.column-form-layout',

    layout: 'column',
    fields: [],

    defaults: {
        layout: 'anchor'
    },

//    constructor: function(cfg) {
//        cfg = cfg || {};
//        cfg.configs = cfg.configs || {};
//        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);
//
//        this.superclass.constructor.call(this, cfg);
//    },

    initComponent: function() {
        var centerComponents = [];
        var westComponents = [];
        var eastComponents = [];

        Ext.each(this.fields, function(field) {
            switch(field.region) {
                case 'west':
                    westComponents.push(field);
                    break;
                case 'east':
                    eastComponents.push(field);
                    break;
                default:
                    centerComponents.push(field);
                    break;
            }
        });

        this.columns = [];

        if (westComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: westComponents
            }, this.westRegionConfigs));
        }

        if (centerComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: centerComponents
            }, this.centerRegionConfigs));
        }

        if (eastComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: eastComponents
            }, this.eastRegionConfigs));
        }

        if (this.columns.length > 0) {
            var columnWidth = 1.0 / this.columns.length;
            Ext.each(this.columns, function(column) {
                if (OPF.isEmpty(column.columnWidth) && OPF.isEmpty(column.width)) {
                    column.columnWidth = columnWidth;
                }
            });
        }

        this.items = this.columns;

        this.callParent(arguments);
    }

});