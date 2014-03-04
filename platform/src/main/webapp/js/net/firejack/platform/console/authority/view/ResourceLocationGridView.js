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

Ext.define('OPF.console.authority.view.ResourceLocationGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'RESOURCE LOCATIONS',
    iconCls: 'sm-resource-location-icon',
    iconGridUrl: '/images/icons/16/resloc_16.png',
    entityName: 'Resource Location',
    registryNodeType: OPF.core.utils.RegistryNodeType.RESOURCE_LOCATION,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                width: 30,
                renderer: function() {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + instance.iconGridUrl + '">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'name',
                header: 'Name',
                sortable: true,
                width: 200,
                renderer: 'htmlEncode'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'urlPath',
                header: 'Url Mask',
                sortable: true,
                width: 400
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'wildcardStyle',
                header: 'Url Style',
                sortable: true,
                width: 100
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'description',
                header: 'Description',
                sortable: true,
                flex: 1,
                renderer: 'htmlEncode'
            }
        ];
    }
});
