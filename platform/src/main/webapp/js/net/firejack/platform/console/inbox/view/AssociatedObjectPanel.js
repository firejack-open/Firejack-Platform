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

Ext.define('OPF.console.inbox.view.AssociatedObjectPanel', {
    extend: 'Ext.grid.Panel',

    layout: 'fit',

    managerLayout: null,
    object: null,
    autoExpandColumn: 'propertyValue',

    constructor: function(managerLayout, tabTitle, object, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.view.AssociatedObjectPanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            title: tabTitle,
            object: object
        }, cfg));
    },

    initComponent: function() {
        this.columns = [
            OPF.Ui.populateColumn('name', 'Property Name', {width: 200, renderer: this.boldHeadersRenderer}),
            OPF.Ui.populateColumn('value', 'Property Value', {renderer: 'htmlEncode'})
        ];

        this.store = Ext.create('Ext.data.ArrayStore', {
            autoDestroy: true,
            idIndex: 0,
            fields: [
               'name',
               'value'
            ]
        });

        this.store.loadData(this.getPropertiesData());

        this.callParent(arguments);
    },

    getPropertiesData: function() {
        var data = [];
        var objectData = this.object;
        var count = 0;
        for(var field in objectData) {
            count++;
            data.push({
                name: field,
                value: objectData[field]
            });
        }
        return {
            total: count,
            success: true,
            message: 'success',
            data: data
        };
    },

    boldHeadersRenderer: function (value) {
        return String.format('<span style="color: #444444" ><b>' +  value + '</b></span>');
    }

});