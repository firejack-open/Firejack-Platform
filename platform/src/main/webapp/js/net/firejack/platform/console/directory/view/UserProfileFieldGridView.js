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

Ext.define('OPF.console.directory.view.UserProfileFieldGridView', {
    extend: 'Ext.panel.Panel',

    title: 'User Profile Fields',
    layout: 'fit',
    padding: 10,
    border: false,

    managerLayout: null,
    registryNodeType: OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD,
    parentNode: null,
    active: false,

    selectedNode: null,

    constructor: function(managerLayout, cfg) {
        cfg = cfg || {};
        OPF.console.directory.view.UserProfileFieldGridView.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout
        }, cfg));

    },

    initComponent: function() {
        var instance = this;

        this.addUserProfileGroup = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Add Group',
            iconCls: 'silk-add',
            handler: function () {
                instance.getGroupEditorDialog().showEdit(instance.parentNode);
            }
        });

        this.addUserProfileField = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Add Profile Field',
            iconCls: 'silk-add',
            disabled: true,
            handler: function () {
                instance.getFieldEditorDialog().showEdit(instance.parentNode, instance.selectedNode);
            }
        });

        this.deleteButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'Delete',
            iconCls: 'silk-delete',
            disabled: true,
            handler: function () {
                instance.deleteNode();
            }
        });

        this.store = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            model: 'OPF.console.directory.model.UserProfileFieldTree',
            proxy: {
                type: 'ajax',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                id: OPF.core.utils.RegistryNodeType.REGISTRY.generateId(0),
                text: 'root',
                expanded: true
            },
            folderSort: true,
            listeners: {
                beforeload: function(store, operation, eOpts) {
                    if (isNotEmpty(instance.parentNode)) {
                        store.proxy.url = instance.getLoadNodeUrl(instance.parentNode, operation.node);
                        return !store.isLoading();
                    } else {
                        return false;
                    }
                }
            }
        });

        this.tree = Ext.create('Ext.tree.Panel', {
            useArrows: true,
            autoScroll: true,
            animate: true,
            rootVisible: false,
            store: this.store,
            multiSelect: true,
            split: true,
            viewConfig: {
                plugins: {
                    ptype: 'treeviewdragdrop'
                },
                listeners: {
                    drop: function(node, data, overModel, dropPosition, eOpts) {
                        instance.moveUserProfileField(data.records[0]);
                    }
                }
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Name',
                    dataIndex: 'name',
                    sortable: true,
                    flex: 1
                },
                {
                    header: 'Type',
                    dataIndex: 'fieldTypeName',
                    sortable: true,
                    flex: 1
                },
                {
                    header: 'Description',
                    dataIndex: 'description',
                    sortable: true,
                    flex: 5
                }
            ],
            tbar: {
                xtype: 'toolbar',
                items: [
                    this.addUserProfileGroup,
                    this.addUserProfileField,
                    {
                        xtype: 'tbfill'
                    },
                    this.deleteButton
                ]
            },
            listeners: {
                itemclick: function(tree, record) {
                    instance.delayedClick.delay(200, null, this, [false, record]);
                },

                itemdblclick: function(tree, record) {
                    instance.delayedClick.delay(200, null, this, [true, record]);
                },
                containerclick: function(tree) {
                    if (isNotEmpty(instance.selectedNode)) {
                        tree.getSelectionModel().deselect(instance.selectedNode);
                    }
                    instance.selectedNode = null;
                    instance.refreshButtons();
                }
            }
        });

        this.delayedClick = new Ext.util.DelayedTask(function(doubleClick, record) {
            if(doubleClick) {
                instance.fireDblClick(record);
            } else {
                instance.fireClick(record);
            }
        }, this),

        this.items = [
            this.tree
        ];

        this.callParent(arguments);
    },

    listeners: {
        activate: function(panel) {
            panel.onNavigationNodeClick();
        }
    },

    getLoadNodeUrl: function(parentNode, node) {
        var url;
        var parentNodeId = SqGetIdFromTreeEntityId(parentNode.data.id);
        var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(node.data.id);
        if (OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP == type) {
            var nodeId = SqGetIdFromTreeEntityId(node.data.id);
            url = OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD.generateUrl('/node/' + parentNodeId + '/' + nodeId);
        } else {
            url = OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP.generateUrl('/node/' + parentNodeId);
        }
        return url;
    },

    fireClick: function(record) {
        this.selectedNode = record;
        this.refreshButtons();
    },

    fireDblClick: function(record) {
        var instance = this;

        var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(record.data.id);
        if (OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP == type) {
            this.selectedNode = record;
            if (this.selectedNode.data.realId > 0) {
                instance.getGroupEditorDialog().showEdit(instance.parentNode, this.selectedNode);
            }
        } else {
            instance.getFieldEditorDialog().showEdit(instance.parentNode, null, record);
        }
        this.refreshButtons();
    },

    onNavigationNodeClick: function() {
        this.parentNode = this.managerLayout.navigationPanel.selectedNode;
        if (isNotEmpty(this.getEl())) {
            this.active = false;
            if (isNotEmpty(this.parentNode)) {
                var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(this.parentNode.data.id);
                if (isNotEmpty(type)) {
                    this.active = type.containsAllowType(OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD);
                }
            }
            if (this.active) {
                this.getEl().unmask();
                this.store.getRootNode().removeAll();
                this.store.load();
            } else {
                this.getEl().mask();
            }
        }
    },

    refreshFields: function(selectedNode) {
        if (isNotEmpty(selectedNode) && this.active) {
            this.parentNode = selectedNode;
//            this.availableGroupsStore.load();
        }
    },

    refreshButtons: function() {
        if (isNotEmpty(this.selectedNode)) {
            var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(this.selectedNode.data.id);
            if (OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP == type) {
                this.addUserProfileField.enable();
            } else {
                this.addUserProfileField.disable();
            }
            this.deleteButton.enable();
        } else {
            this.addUserProfileField.disable();
            this.deleteButton.disable();
        }
    },

    deleteNode: function() {
        var instance = this;

        if (isNotEmpty(this.selectedNode)) {
            var url;
            var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(this.selectedNode.data.id);
            if (OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP == type) {
                url = OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP.generateDeleteUrl(this.selectedNode.data.realId);
            } else {
                url = OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD.generateDeleteUrl(this.selectedNode.data.realId);    
            }

            Ext.Ajax.request({
                url: url,
                method: 'DELETE',
                success: function(response){
                    var vo = Ext.decode(response.responseText);
                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                    instance.selectedNode.remove();
                },
                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        }
        
    },

    moveUserProfileField: function(node) {
        var userProfileFieldId = node.get('realId');
        var userProfileFieldGroupId = node.parentNode.get('realId');
        Ext.Ajax.request({
            url: OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD.generatePutUrl('move/' + userProfileFieldId, '/' + userProfileFieldGroupId),
            method: 'PUT',
            success: function(response){
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
            },
            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    getGroupEditorDialog: function() {
        var editorDialog = Ext.WindowMgr.get('userProfileFieldGroupEditDialog');
        if (isEmpty(editorDialog)) {
            editorDialog = new OPF.console.directory.view.UserProfileFieldGroupViewDialog(this);
            Ext.WindowMgr.register(editorDialog);
        }
        return editorDialog;
    },

    getFieldEditorDialog: function() {
        var editorDialog = Ext.WindowMgr.get('userProfileFieldEditDialog');
        if (isEmpty(editorDialog)) {
            editorDialog = new OPF.console.directory.view.UserProfileFieldViewDialog(this);
            Ext.WindowMgr.register(editorDialog);
        }
        return editorDialog;
    }

});

