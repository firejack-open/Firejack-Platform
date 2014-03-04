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
Ext.define('OPF.console.resource.view.ResourceGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'RESOURCES',
    iconCls: 'sm-resource-icon',
    iconGridUrl: '/images/icons/16/resource_16.png',
    entityName: 'Resource',
    registryNodeType: OPF.core.utils.RegistryNodeType.RESOURCE,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                width: 30,
                renderer: function(value, metaData, record) {
                    var type = record.get('type');
                    var pos = type.indexOf('_');
                    type = type.substr(0, pos).toLowerCase();
                    return '<img src="' + OPF.Cfg.fullUrl('/images/icons/16/') + type + '_16.png">';
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
                dataIndex: 'path',
                header: 'Path',
                sortable: true,
                width: 400
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'lastVersion',
                header: 'Last Version',
                sortable: true,
                width: 100,
                id: 'lastVersion'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'selectedVersion',
                header: 'Selected Version',
                sortable: true,
                width: 100,
                id: 'selectedVersion'
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
    },

    onItemDblClick: function(grid, record) {
        var instance = this;

        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        var url = registryNodeType.generateGetUrl(record.get('id'));

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {

                var editPanel = instance.managerLayout.tabPanel.getComponent(registryNodeType.type + 'EditPanel');
                if (OPF.isNotEmpty(editPanel)) {
                    if (editPanel.entityId == record.get('id')) {
                        instance.managerLayout.tabPanel.setActiveTab(editPanel);
                        return;
                    } else {
                        instance.managerLayout.tabPanel.remove(editPanel);
                    }
                }

                editPanel = registryNodeType.createEditPanel(instance.managerLayout);
                editPanel.saveAs = 'update';

                var registryJsonData = Ext.decode(response.responseText);
                editPanel.showEditPanel(registryJsonData.data[0]);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    generateDeleteUrl: function(record) {
        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.get('type'));
        return registryNodeType.generateGetUrl(record.get('id'));
    }

});
