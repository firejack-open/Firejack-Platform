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

Ext.define('OPF.console.directory.view.DirectoryGridView', {
    extend: 'OPF.core.component.BaseGridView',


    title: 'DIRECTORIES',
    iconCls: 'sm-directory-icon',
    iconGridUrl: '/images/icons/16/directory_16.png',
    entityName: 'Directory',
    registryNodeType: OPF.core.utils.RegistryNodeType.DIRECTORY,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                sortable: true,
                align: 'center',
                width: 30,
                renderer: function() {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/directory_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                header: 'Name',
                dataIndex: 'name',
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
                dataIndex: 'directoryServiceTitle',
                header: 'Directory Service',
                sortable: true,
                width: 200
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'description',
                header: 'Description',
                sortable: true,
                flex: 1,
                renderer: 'htmlEncode'
            }
        ]
    },

    getConfigPlugins: function() {
        return {
            ptype: 'gridviewdragdrop',
            dragText: 'Drag and drop to reorganize'
        }
    },

    getConfigPluginsListeners: function() {
        var instance = this;
        return {
            drop: function(node, data, overModel, dropPosition, eOpts) {
                var newPosition = instance.store.findExact('id', data.item.viewRecordId);
                instance.refreshOrderPositions(data.item.viewRecordId, newPosition + 1);
            }
        };
    },

    storeBeforeLoad: function(store, operation) {
        store.proxy.url = this.registryNodeType.generateUrl() + '/ordered';
    },

    refreshOrderPositions: function(directoryId, newPos) {
        var records = this.store.getRange();
        if (records != null && records.length != 0) {
            //var instance = this;
            var url = this.registryNodeType.generateUrl() + '/ordered/' + directoryId + '/' + newPos;
            Ext.Ajax.request({
                url: url,
                method: 'PUT',

                success:function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(true, vo.message);
                },

                failure:function(response) {
                    OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                }
            });
            var lastIndex = records.length - 1;
            Ext.each(records, function(rec, index) {
                rec.set('sortPosition', index + 1);
            });
        }
    }

});