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


Ext.define('OPF.console.documentation.manager.DocumentationTreePanel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.documentation-tree',

    width: 200,
    autoHeight: true,
    useArrows: true,
    autoScroll: true,
    animate: true,
    collapsible: false,
    split: true,
    rootVisible: false,
    border: false,
    isFirstLoad: true,

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.initializeChildNodes = function(model) {
            if (isEmpty(model.childNodes) || (model.childNodes.length == 0)) {
                var children = model.get('children');
                if (isNotEmpty(children) && Ext.isArray(children)) {
                    var i, nodes = [];
                    for (i = 0; i < children.length; i++) {
                        var childModel = Ext.create('OPF.core.model.RegistryNodeTreeModel', children[i]);
                        var node = Ext.data.NodeInterface.decorate(childModel);
                        nodes.push(node);
                    }
                    model.appendChild(nodes);
                }
            }
            if (isNotEmpty(model.childNodes)) {
                for (i = 0; i < model.childNodes.length; i++) {
                    this.initializeChildNodes(model.childNodes[i]);
                }
            }
        };

        this.store = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.core.model.RegistryNodeTreeModel',
            proxy: {
                type: 'ajax',
                url: this.getLoadNodeUrl(0),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                text: 'root',
                id: OPF.core.utils.RegistryNodeType.REGISTRY.generateId(0),
                rendered: false,
                expanded: true,
                normalizedName: 'root'
            },
            folderSort: true,
            sorters: this.SORTER,
            clearOnLoad: false,
            listeners: {
                beforeload: function(store, operation, eOpts) {
                    var nodeId = SqGetIdFromTreeEntityId(operation.node.get('id'));
                    if (isEmpty(nodeId)) {
                        nodeId = 0;
                    }
                    store.proxy.url = me.getLoadNodeUrl(nodeId);
                },
                load: function(store, node, models, successful, eOpts ) {
                    if (me.isFirstLoad) {
                        me.isFirstLoad = false;
                        if (isNotEmpty(models) && Ext.isArray(models)) {
                            for (var i = 0; i < models.length; i++) {
                                me.initializeChildNodes(models[i]);
                            }
                        }
                        var type = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(OPF.DCfg.REGISTRY_NODE_TYPE);
                        var nodeId = type.generateId(OPF.DCfg.REGISTRY_NODE_ID);
                        me.selectedNode = store.getNodeById(nodeId);
                        if (isNotEmpty(me.selectedNode)) {
                            try {
                                me.selectedNode.bubble(function(node){
                                    node.expand();
                                });
                                var selectedNodeLookup = me.selectedNode.get('lookup');
                                var pathParts = selectedNodeLookup.split('.');
                                var selectPath = '.root.';
                                if (pathParts.length > 1) {
                                    selectPath += pathParts[0];
                                    selectPath += pathParts[1];
                                    for (var j = 2; j < pathParts.length; j++) {
                                        selectPath += '.';
                                        selectPath += pathParts[j];
                                    }
                                }
                                me.selectPath(selectPath, 'normalizedName', '.');
                            } catch(e){
                                Ext.Msg.alert('Error', e);
                            }
                        }
//                        me.managerLayout.openEditor(editEntity, me.selectedNode);
                    }
                },
                append: function(node, newChildNode, index, eOpts) {
                    if (!newChildNode.isRoot()) {
                        var normalizedName;
                        if (newChildNode.get('type') == 'ROOT_DOMAIN') {
                            normalizedName = newChildNode.get('lookup').replace(/\./g, '');
                        } else {
                            var lookup = newChildNode.get('lookup');
                            normalizedName = lookup.substring(lookup.lastIndexOf('.') + 1);
                        }
                        newChildNode.set('normalizedName', normalizedName);

                        var href = newChildNode.get('lookup').replace(/\./g, '/');
                        newChildNode.set('href', OPF.DCfg.DOC_URL + '/' + OPF.DCfg.COUNTRY + '/' + href);
                    }
                }
            }
        });

        this.callParent(arguments);
    },

    listeners: {
        itemclick: function(tree, record) {
            document.location = record.get('href');
        }
    },

    getLoadNodeUrl: function(nodeId) {
        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/children/' + nodeId + '?pageType=' + OPF.Cfg.PAGE_TYPE);
        if (this.isFirstLoad) {
            url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/children/expanded-by-id/' + OPF.DCfg.REGISTRY_NODE_ID + '?pageType=' + OPF.Cfg.PAGE_TYPE);
        }
        return url;
    }

});