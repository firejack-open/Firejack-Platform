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

Ext.require([
    'OPF.core.model.RegistryNodeTreeModel'
]);

Ext.define('OPF.prototype.component.MenuTreeNavigation', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.menu-tree-navigation',

    flex: 1,
    border: false,
    width: 200,
    cls: 'menu-tree-navigation',

    initComponent: function() {
        var me = this;

        var packageLookup = OPF.findPackageLookup(OPF.Cfg.NAVIGATION_LOOKUP);
        var gatewayLookup = packageLookup + '.gateway';

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/site/navigation/tree/' + gatewayLookup),
            method: 'GET',

            success: function(response, action) {
                var registryJsonData = Ext.decode(response.responseText);
                var jsonData = registryJsonData.data/*[0]*/;            // TODO need uncomment for new implementation
                me.addNavigationNode(me.store.getRootNode(), jsonData);

                var selectPath = '.root.' + OPF.Cfg.NAVIGATION_LOOKUP.substring(gatewayLookup.length + 1);
                me.tree.selectPath(selectPath, 'normalizedName', '.');
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });

        this.store = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.core.model.RegistryNodeTreeModel',
            root: {
                normalizedName: 'root',
                expanded: true,
                children: [
                ]
            },
            sorters: [
                {
                    property: 'sortPosition',
                    direction: 'ASC'
                }
            ],
            listeners: {
                append: function(node, newChildNode, index, eOpts) {
                    if(!newChildNode.isRoot()) {
                        var urlPath = newChildNode.get('urlPath');
                        var href = OPF.Cfg.fullUrl(urlPath, true);
                        newChildNode.set('href', href);

                        var lookup = newChildNode.get('lookup');
                        var normalizedName = lookup.substring(lookup.lastIndexOf('.') + 1);
                        newChildNode.set('normalizedName', normalizedName);
                        newChildNode.set('cls', 'node-menu');
                    }
                }
            }
        });

        this.tree = Ext.create('Ext.tree.Panel', {
            store: this.store,
            border: false,
            useArrows: true,
            rootVisible: false,
            autoRender: true,
            width: me.width,
            listeners: {
                itemclick: function(tree, record) {
                    document.location = record.get('href');
                }
            }
        });

        this.items = [
            this.tree
        ];

        this.callParent(arguments);
    },

    addNavigationNode: function(parentNode, jsonNodes) {
        var me = this;
        Ext.each(jsonNodes, function(jsonNode) {
            var node = Ext.create('OPF.core.model.RegistryNodeTreeModel', jsonNode);
            parentNode.appendChild(node);
        });
    }

});