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
Ext.define('OPF.console.resource.view.ConfigurationGridView', {
    extend: 'OPF.core.component.BaseGridView',

    title: 'CONFIGURATION',
    iconCls: 'sm-configuration-icon',
    iconGridUrl: '/images/icons/16/config_16.png',
    entityName: 'Resource',
    registryNodeType: OPF.core.utils.RegistryNodeType.CONFIG,

    isRestStore: true,

    initComponent: function() {
        var me = this;

        this.rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            listeners: {
                edit: function(editor, e) {
                    editor.store.sync();
                }
            }
        });

        this.gridAddRowButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Add',
            iconCls: 'silk-add',
            disabled: true,
            handler: function(btn, ev) {
                var selectedNode = me.managerLayout.navigationPanel.getSelectedNode();
                var selectedNodeId = SqGetIdFromTreeEntityId(selectedNode.data.id);
                me.store.insert(0, me.registryNodeType.createModel({
                    type: me.registryNodeType.getType(),
                    path: selectedNode.data.lookup,
                    parentId: selectedNodeId
                }));
                me.rowEditing.startEdit(0, 0);
            }
        });

        this.gridTButtons = [
            this.gridAddRowButton
        ];

        this.callParent(arguments);
    },

    getColumns: function() {
        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                dataIndex: 'id',
                sortable: true,
                align: 'center',
                width: 30,
                renderer: function(value, metaData, record) {
                    var type = record.get('type');
                    return '<img src="' + OPF.Cfg.fullUrl('/images/icons/16/') + type.toLowerCase() + '_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'path',
                header: 'Path',
                sortable: true,
                width: 250,
                field: {
                    xtype: 'opf-textfield',
                    allowBlank: false,
                    readOnly: true
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'name',
                header: 'Name',
                sortable: true,
                width: 100,
                field: {
                    xtype: 'opf-textfield',
                    allowBlank: false
                },
                renderer: 'htmlEncode'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'value',
                header: 'Value',
                sortable: true,
                width: 500,
                field: {
                    xtype: 'opf-textfield',
                    allowBlank: false
                },
                renderer: 'htmlEncode'
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'description',
                header: 'Description',
                flex: 1,
                id: 'description',
                field: {
                    xtype: 'opf-textfield'
                },
                renderer: 'htmlEncode'
            }
        ];
    },

    storeBeforeSync: function(options, eOpts) {
        this.store.proxy.url = this.registryNodeType.generateUrl();
        var isValid = true;
        Ext.each(options.create, function(record){
            isValid &= OPF.isNotBlank(record.get('name')) && OPF.isNotBlank(record.get('value'));
        });
        return isValid;
    },

    getPlugins: function() {
        return this.rowEditing;
    },

    // Don't remove this method, it overrides unnecessary logic
    onItemDblClick: function(grid, record) {
    },

    refreshButtons: function(selectedNode) {
        if (isNotEmpty(selectedNode)) {
            this.gridAddRowButton.enable();
        } else {
            this.gridAddRowButton.disable();
        }
    }

});