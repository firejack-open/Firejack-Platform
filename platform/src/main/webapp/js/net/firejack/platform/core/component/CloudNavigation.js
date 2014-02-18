//@tag opf-console
/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */


/**
 *
 */
Ext.define('OPF.core.component.CloudNavigation', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.cloud-navigation',

    title: 'Cloud Navigator',
    region: 'west',
    width: 200,
    useArrows: true,
    autoScroll: true,
    animate: true,
    itemId: 'cloud-tree',
    collapsible: false,
    split: true,
    rootVisible: false,

    needToRefreshData: false,
    lastCheckChangesTimestamp: OPF.Cfg.LOAD_TIMESTAMP,

    enableDD: true,
    ddGroup: 'cloudNavigatorDDGroup',
    viewConfig: {
        plugins: {
            ptype: 'opf-treeviewdragdrop',
            ddGroup: 'cloudNavigatorDDGroup',

            beforeDragEnter: function(target, e, id) {
                var node = this.dragData.records[0];
                var nodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(node.get('type'));
                if (OPF.core.utils.RegistryNodeType.PACKAGE == nodeType) {
                    var nodeParameters = node.get('parameters');
                    if (nodeParameters != null && nodeParameters.associated) {
                        this.ddel.update(
                            'This package is already associated with a system. <br/>' +
                            'Please dis-associate the package before attempting to install it <br/> ' +
                            'on another system or re-associating it with the existing system.');
                    }
                }
                return true;
            },
            isValidDropPoint: function(targetNode, sourceNodes) {
                var allowDragAndDrop = false;
                var sourceNode = sourceNodes[0];
                var targetType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(targetNode.get('type'));
                var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(sourceNode.get('type'));
                var parameters = sourceNode.get('parameters');
                if (OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT == targetType
                    && OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT == sourceType) {
                    allowDragAndDrop = true;
                } else if (OPF.core.utils.RegistryNodeType.SYSTEM == targetType
                    && OPF.core.utils.RegistryNodeType.PACKAGE == sourceType) {
                    allowDragAndDrop = OPF.isNotEmpty(parameters) && OPF.isEmpty(parameters.associated);
                } else if (OPF.core.utils.RegistryNodeType.PACKAGE == targetType
                    && OPF.core.utils.RegistryNodeType.DATABASE == sourceType) {
                    allowDragAndDrop = true;
                } else if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType
                    || OPF.core.utils.RegistryNodeType.DOMAIN == sourceType) {
                    var chainNodeTypes = OPF.core.component.CloudNavigation.chainNodeTypes(targetNode);
                    allowDragAndDrop = targetType.containsAllowType(sourceType, chainNodeTypes);
                }
                return allowDragAndDrop;
            }
        }
    },

    selectedNode: null,

    managerLayout: null,

    oldPosition: null,
    oldNextSibling: null,

    editEntity: null,

    SORTER: [
        {
            property: 'type',
            direction: 'ASC'
        },
        {
            property: 'sortPosition',
            direction: 'ASC'
        },
        {
            property: 'text',
            direction: 'ASC'
        }
    ],

    constructor: function(managerLayout, editEntity, cfg) {
        var currentUrl = document.location.href;
        var regLookup = new RegExp(/.*?#([a-z0-9\\.\\-]+)/g);
        var m = regLookup.exec(currentUrl);
        if (m != null && m.length == 2) {
            editEntity = {
                lookup: m[1]
            };
        } else {
            var regTypeId = new RegExp(/.*?#([A-Z_]+)_(\d+)/g);
            m = regTypeId.exec(currentUrl);
            if (m != null && m.length == 3) {
                editEntity = {
                    id: m[2],
                    type: m[1]
                };
            }
        }

        cfg = cfg || {};
        OPF.core.component.CloudNavigation.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            editEntity: editEntity
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.initializeChildNodes = function(model) {
            if (OPF.isEmpty(model.childNodes) || (model.childNodes.length == 0)) {
                var children = model.get('children');
                if (OPF.isNotEmpty(children) && Ext.isArray(children)) {
                    var i, nodes = [];
                    for (i = 0; i < children.length; i++) {
                        var childModel = Ext.create('OPF.core.model.RegistryNodeTreeModel', children[i]);
                        var node = Ext.data.NodeInterface.decorate(childModel);
                        nodes.push(node);
                    }
                    model.appendChild(nodes);
                }
            }
            if (OPF.isNotEmpty(model.childNodes)) {
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
            sorters: this.SORTER,
            clearOnLoad: false,
            listeners: {
                beforeload: function(store, operation, eOpts) {
                    var nodeId = SqGetIdFromTreeEntityId(operation.node.get('id'));
                    if (OPF.isEmpty(nodeId)) {
                        nodeId = 0;
                    }
                    store.proxy.url = me.getLoadNodeUrl(nodeId);
                },
                load: function(store, node, models, successful, eOpts ) {
                    var editEntity;
                    if (OPF.isNotEmpty((editEntity = me.editEntity))) {
                        me.editEntity = null;
                        if (OPF.isNotEmpty(models) && Ext.isArray(models)) {
                            for (var i = 0; i < models.length; i++) {
                                me.initializeChildNodes(models[i]);
                            }
                        }
                        var j, pathParts, selectPath;
                        if (OPF.isNotEmpty(editEntity.id)) {
                            var type = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(editEntity.type);
                            var nodeId = type.generateId(editEntity.id);
                            me.selectedNode = store.getNodeById(nodeId);
                        } else if (OPF.isNotEmpty(editEntity.lookup)) {
                            me.selectedNode = store.getRootNode().findChildBy(function(node) {
                                return node.get('lookup') == editEntity.lookup;
                            }, this, true);
                        }
                        if (OPF.isNotEmpty(me.selectedNode)) {
                            try {
                                var selectedNodeLookup = me.selectedNode.get('lookup');
                                pathParts = selectedNodeLookup.split('.');
                                selectPath = '.root.';
                                if (pathParts.length > 1) {
                                    selectPath += pathParts[0];
                                    selectPath += pathParts[1];
                                    for (j = 2; j < pathParts.length; j++) {
                                        selectPath += '.';
                                        selectPath += pathParts[j];
                                    }
                                }
                                me.selectPath(selectPath, 'normalizedName', '.');
                            } catch(e){
                                Ext.Msg.alert('Error', e);
                            }
                            me.managerLayout.openEditor(editEntity, me.selectedNode);
                            me.managerLayout.refreshToolbarButtons();
                        }
                    }
                },
                append: function(node, newChildNode, index, eOpts) {
                    if (!newChildNode.isRoot()) {
                        var normalizedName;
                        if (newChildNode.get('type') == 'ROOT_DOMAIN') {
                            normalizedName = newChildNode.get('lookup').replace(/\./g, "");
                        } else {
                            var lookup = newChildNode.get('lookup');
                            normalizedName = lookup.substring(lookup.lastIndexOf('.') + 1);
                        }
                        newChildNode.set('normalizedName', normalizedName);
                    }
                }
            }
        });

        this.delayedClick = new Ext.util.DelayedTask(function(doubleClick, record) {
            if(doubleClick) {
                this.fireDblClick(record);
            } else {
                this.fireClick(record);
            }
        }, this);

        this.refreshButton = Ext.create('Ext.panel.Tool', {
            type: 'refresh',
            tooltip: 'Refresh Tree',
            hidden: true,
            handler: function(event, toolEl, panel){
                me.reload();
            }
        });

        this.tools = [
            this.refreshButton
        ];

        this.callParent(arguments);

        new Ext.util.DelayedTask( function() {
            me.checkChanges();
        }).delay(10000);
    },

    listeners: {
        itemclick: function(tree, record) {
            this.selectedNode = record;
            this.delayedClick.delay(200, null, this, [false, record]);
        },

        itemdblclick: function(tree, record) {
            this.selectedNode = record;
            this.delayedClick.delay(200, null, this, [true, record]);
        },

        containerclick: function(tree) {
            tree.getSelectionModel().deselect(this.getSelectedNode());
            this.selectedNode = null;
            this.managerLayout.refreshToolbarButtons(true);
            if (Ext.isFunction(this.managerLayout.tabPanel.getActiveTab)) {
                var editPanel = this.managerLayout.tabPanel.getActiveTab();
                if (OPF.isNotEmpty(editPanel.refreshButtons)) {
                    editPanel.refreshButtons(null);
                }
                if (OPF.isNotEmpty(editPanel.refreshFields)) {
                    editPanel.refreshFields(null);
                }
            }
        },
        startdrag: function(tree, node, event) {
            this.oldPosition = node.parentNode.indexOf(node);
            this.oldNextSibling = node.nextSibling;
        },
        itemmove: function(node, oldParent, newParent, position, eOpts) {
            var targetType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(newParent.get('type'));
            var sourceType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(node.get('type'));
            if (OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT == targetType
                    && OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT == sourceType) {
                this.registryNodeChangePosition(node, oldParent, newParent, position);
            } else if (OPF.core.utils.RegistryNodeType.SYSTEM == targetType
                    && OPF.core.utils.RegistryNodeType.PACKAGE == sourceType) {
                this.associatePackage(this, node, oldParent, newParent, position);
            } else if (OPF.core.utils.RegistryNodeType.PACKAGE == targetType
                    && OPF.core.utils.RegistryNodeType.DATABASE == sourceType) {
                this.associateDatabase(this, node, oldParent, newParent, position);
            } else if (OPF.core.utils.RegistryNodeType.ENTITY == sourceType
                    || OPF.core.utils.RegistryNodeType.DOMAIN == sourceType) {
                var chainNodeTypes = OPF.core.component.CloudNavigation.chainNodeTypes(newParent);
                var contains = targetType.containsAllowType(sourceType, chainNodeTypes);
                if (contains) {
                    this.registryNodeChangePosition(node, oldParent, newParent, position);
                }
            }
        }
    },

    fireClick: function(record) {
        if (OPF.isNotEmpty(this.managerLayout) && OPF.isNotEmpty(this.managerLayout.tabPanel.getActiveTab)) {
            var editPanel = this.managerLayout.tabPanel.getActiveTab();
            if (OPF.isNotEmpty(editPanel) && OPF.isNotEmpty(editPanel.registryNodeType)) {
                editPanel.refreshFields(record);
            }
            if (OPF.isNotEmpty(editPanel) && OPF.isNotEmpty(editPanel.refreshButtons)) {
                editPanel.refreshButtons(record);
            }
            this.managerLayout.refreshToolbarButtons();
        }
    },

    fireDblClick: function(record) {
        var instance = this;

        if (!record.data.canUpdate) {
            return;
        }

        var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeById(record.data.id);
        if (OPF.isNotEmpty(registryNodeType) && this.managerLayout.isPageSupportType(registryNodeType)) {
            var editPanel = this.managerLayout.tabPanel.getComponent(registryNodeType.type + 'EditPanel');
            if (OPF.isNotEmpty(editPanel)) {
                if (editPanel.entityId == record.data.id) {
                    this.managerLayout.tabPanel.setActiveTab(editPanel);
                    return;
                } else {
                    this.managerLayout.tabPanel.remove(editPanel);
                }
            }

            Ext.Ajax.request({
                url: registryNodeType.generateGetUrl(SqGetIdFromTreeEntityId(record.data.id)),
                method: 'GET',
                jsonData: '[]',

                success: function(response, action) {
                    var registryJsonData = Ext.decode(response.responseText);
                    if (registryJsonData.success) {
                        editPanel = registryNodeType.createEditPanel(instance.managerLayout);
                        editPanel.saveAs = 'update';
                        editPanel.selfNode = record;
                        editPanel.showEditPanel(registryJsonData.data[0]);
                    } else {
                        OPF.Msg.setAlert(false, response.message);
                    }
                },

                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        }
    },

    getLoadNodeUrl: function(nodeId) {
        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/children/' + nodeId + '?pageType=' + OPF.Cfg.PAGE_TYPE);
        if (OPF.isNotEmpty(this.editEntity)) {
            if (OPF.isNotEmpty(this.editEntity.id)) {
                url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/children/expanded-by-id/' + this.editEntity.id + '?pageType=' + OPF.Cfg.PAGE_TYPE);
            } else if (OPF.isNotEmpty(this.editEntity.lookup)) {
                url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/children/expanded-by-lookup/' + this.editEntity.lookup + '?pageType=' + OPF.Cfg.PAGE_TYPE);
            }
        }
        return url;
    },

    getSelectedNode: function(force) {
        var rootForce = isEmpty(force) ? true : force;
        if (OPF.isEmpty(this.selectedNode) && rootForce) {
            this.selectedNode = this.store.getRootNode();
        }
        return this.selectedNode;
    },

    registryNodeChangePosition: function(node, oldParent, newParent, position) {
        var me = this;

        var postData = {
            'registryNodeId' : SqGetIdFromTreeEntityId(node.get('id')),
            'newRegistryNodeParentId' : SqGetIdFromTreeEntityId(newParent.get('id')),
            'oldRegistryNodeParentId' : SqGetIdFromTreeEntityId(oldParent.get('id')),
            'position' : position
        };

        var mask = new Ext.LoadMask(me.getEl(), {msg: 'Deleting...'});
        mask.show();

        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/change/position'),
            method: 'PUT',
            jsonData: {"data": postData},

            success:function(response, action) {
                mask.hide();

                me.oldPosition = null;
                me.oldNextSibling = null;

                OPF.Msg.setAlert(true, "Node was moved successfully.");
            },

            failure:function(response) {
                me.rollbackNode(me, oldParent, node, true);
                mask.hide();
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    associatePackage: function(tree, node, oldParent, newParent, position) {
        var instance = this;
        if (oldParent.id != newParent.id) {
            tree.disable();
            var systemId = SqGetIdFromTreeEntityId(newParent.get('id'));
            var packageId = SqGetIdFromTreeEntityId(node.get('id'));

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('registry/system/installed-package/' + systemId + '/' + packageId),
                method: 'POST',
                jsonData: {},

                success:function(response, action) {
                    tree.enable();
                    node.set('parameters', {
                        associated: true
                    });
                    OPF.Msg.setAlert(true, "Package was associated successfully.");
                },

                failure:function(response) {
                    tree.enable();
                    OPF.Msg.setAlert(false, response.message);
                }
            });
            instance.rollbackNode(tree, oldParent, node, false);
        } else {
            OPF.Msg.setAlert(false, "The same parent node.");
            return false;
        }
    },

    associateDatabase: function(tree, node, oldParent, newParent, position) {
        var instance = this;
        if (oldParent.id != newParent.id) {
            tree.disable();
            var packageId = SqGetIdFromTreeEntityId(newParent.get('id'));
            var databaseId = SqGetIdFromTreeEntityId(node.get('id'));

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('registry/package/associate-database/' + packageId + '/' + databaseId),
                method: 'POST',
                jsonData: {},

                success:function(response, action) {
                    tree.enable();
                    OPF.Msg.setAlert(true, "Database was associated successfully.");
                },

                failure:function(response) {
                    tree.enable();
                    OPF.Msg.setAlert(false, response.message);
                }
            });
            instance.rollbackNode(tree, oldParent, node, false);
        } else {
            OPF.Msg.setAlert(false, "The same parent node.");
            return false;
        }
    },

    rollbackNode: function(tree, oldParent, node, isEnable) {
        tree.suspendEvents();
        oldParent.appendChild(node);
        if (this.oldNextSibling){
            oldParent.insertBefore(node, this.oldNextSibling);
        }

        tree.resumeEvents();
        if (isEnable === true) {
            tree.enable();
        }

        this.oldPosition = null;
        this.oldNextSibling = null;
    },

    sort: function() {
        this.store.sort(this.SORTER);
    },

    reload: function() {
        var me = this;

        var url = this.getLoadNodeUrl(0);
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success:function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                var modelNodes = [];
                Ext.each(jsonData.data, function(data, index) {
                    var model = Ext.create('OPF.core.model.RegistryNodeTreeModel', data);
                    modelNodes.push(model);
                });
                me.store.setRootNode({
                    expanded: true,
                    childNodes: modelNodes
                });
                me.needToRefreshData = false;
                me.refreshButton.hide();
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    chainNodeTypes: function(selectedNode) {
        return OPF.core.component.CloudNavigation.chainNodeTypes(this.selectedNode);
    },

    statics: {
        getChainParentNodes: function(selectedNode) {
            var chainNodes = [];

            function parentNode(node) {
                if (node.parentNode) {
                    chainNodes.push(node.parentNode);
                    parentNode(node.parentNode);
                }
            }

            if (OPF.isNotEmpty(selectedNode)) {
                parentNode(selectedNode);
            }
            return chainNodes;
        },

        chainNodeTypes: function(selectedNode) {
            var chainNodeTypes = [];
            var chainNodes = OPF.core.component.CloudNavigation.getChainParentNodes(selectedNode);
            Ext.each(chainNodes, function(chainNode) {
                var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(chainNode.data.id);
                chainNodeTypes.push(type);
            });
            return chainNodeTypes;
        }
    },

    checkChanges: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/deployment/last-changes/' + this.lastCheckChangesTimestamp),
            method: 'GET',

            success:function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                if (jsonData.success) {
                    var data = jsonData.data[0];
                    me.lastCheckChangesTimestamp = data.timestamp;

                    Ext.each(data.lookup, function(lookup) {
                        var lastNode = null;
                        var pathParts = lookup.split('.');
                        var parentNodeLookup = pathParts.slice(0, pathParts.length - 1).join('.');
                        var parentNode = me.store.getRootNode().findChildBy(function(node) {
                            return node.get('lookup') == parentNodeLookup;
                        }, this, true);

                        if (parentNode && parentNode.get('loaded')) {
                            me.needToRefreshData &= true;
                            me.refreshButton.show();
                        }
                    });

                    new Ext.util.DelayedTask( function() {
                        me.checkChanges();
                    }).delay(10000);
                } else {
                    new Ext.util.DelayedTask( function() {
                        me.checkChanges();
                    }).delay(60000);
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    }

});
