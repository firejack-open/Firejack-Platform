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

Ext.define('OPF.console.resource.view.CollectionMembershipFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Content',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.resource.view.CollectionMembershipFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        this.layoutConfig = {
            align: 'stretch'
        };

        var instance = this;

        this.columns = [
            {
                xtype: 'gridcolumn',
                header: '!',
                dataIndex: 'type',
                sortable: true,
                align: 'center',
                width: 30,
                renderer: function(val) {
                    var type = val;
                    var pos = val.indexOf('_');
                    if (pos > -1) {
                        type = val.substr(0, pos);
                    }
                    type = type.toLowerCase();
                    return '<img src="' + OPF.Cfg.fullUrl('/images/icons/16/' + type + '_16.png') + '">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'name',
                header: 'Name',
                sortable: true,
                width: 150
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'path',
                header: 'Path',
                sortable: true,
                flex: 1
            }
        ];

        this.deleteButton = Ext.ComponentMgr.create({
            xtype: 'button',
            text: 'delete',
            iconCls: 'silk-delete',
            handler: function () {
                instance.deleteMembership();
            }
        });

        this.membershipStore = new Ext.data.Store({
            model: 'OPF.console.resource.model.CollectionMembership',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                },
                writer: {
                    type: 'json'
                }
            }
        });

        this.membershipGrid = Ext.ComponentMgr.create({
            xtype: 'grid',
            cls: 'border-radius-grid-body border-radius-grid-docked-top',
            viewConfig: {
                plugins: new OPF.console.resource.view.CollectionMembershipDragDrop({
                    editPanel: instance.editPanel
                })
            },
            multiSelect: true,
            store: this.membershipStore,
            stripeRows: true,
            anchor: '100%',
            height: 240,
            columns: this.columns,
            tbar: {
                xtype: 'toolbar',
                items: [
                    {
                        xtype: 'tbfill'
                    },
                    this.deleteButton
                ]
            }
        });

        // Resource Grid
        this.resourceStore = new Ext.data.Store({
            model: 'OPF.console.resource.model.CollectionMembership',
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.RESOURCE.generateUrl(),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                }
            },

            listeners: {
                beforeload: {
                    fn: function(store, options) {
                        var selectedNode = instance.editPanel.managerLayout.navigationPanel.getSelectedNode();
                        var selectedNodeId = SqGetIdFromTreeEntityId(selectedNode.data.id);

                        var exceptPermissionIds = [];
                        var records = instance.membershipStore.getRange();
                        for (var i = 0; i < records.length; i++) {
                            var type = records[i].get('type');
                            if (type.match(/\w+_RESOURCE/g)) {
                                exceptPermissionIds.push(records[i].data.id);
                            }
                        }
                        options.params = { exceptIds: exceptPermissionIds };

                        store.proxy.url = OPF.core.utils.RegistryNodeType.RESOURCE.generateUrl('/node/' + selectedNodeId);
                    }
                }
            }
        });

        this.resourceListGrid = Ext.ComponentMgr.create({
            xtype: 'grid',
            title: 'Available Resources',
            cls: 'border-radius-grid-header-top border-radius-grid-toolbar',
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    ddGroup: 'cloudNavigatorDDGroup'
                }
            },
            height: 240,
            store: this.resourceStore,
            stripeRows: true,
            anchor: '100%',
            columns: this.columns,
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    store: this.resourceStore,
                    dock: 'bottom',
                    displayInfo: true
                }
            ]
        });

        this.items = [
            this.membershipGrid,
            {
                xtype: 'container',
                anchor: '100%',
                height: 10
            },
            this.resourceListGrid
        ];

        this.callParent(arguments);
    },

    deleteMembership: function() {
        var instance = this;
        var selectedNode = instance.editPanel.managerLayout.navigationPanel.getSelectedNode();
        var selectedLookup = selectedNode.data.lookup;

        var records = instance.membershipGrid.getSelectionModel().getSelection();
        for (var i = records.length - 1; i >= 0; i--) {
            var record = records[i];
            var type = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(record.data.type);
            instance.membershipStore.remove(record);
            var recordPath = record.data.path;
            if (OPF.core.utils.RegistryNodeType.COLLECTION != type && recordPath.substring(0, selectedLookup.length) === selectedLookup) {
                 instance.resourceStore.insert(0, record);
            }
        }
    }

});

Ext.define('OPF.console.resource.view.CollectionMembershipDragDrop', {
    extend: 'Ext.grid.plugin.DragDrop',

    editPanel: null,

    ddGroup: 'cloudNavigatorDDGroup',

    onViewRender : function(view) {
        var me = this;

        if (me.enableDrag) {
            me.dragZone = Ext.create('Ext.view.DragZone', {
                view: view,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('OPF.console.resource.view.CollectionMembershipDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                editPanel: me.editPanel
            });
        }
    }

});

Ext.define('OPF.console.resource.view.CollectionMembershipDropZone', {
    extend: 'Ext.grid.ViewDropZone',

    editPanel: null,

    notifyOver : function(dd, e, data) {
        var notify = this.callParent(arguments);

        var me = this;
        if (me.valid) {
            var isTree = data.view.$className == 'Ext.tree.View';
            Ext.each(data.records, function(record) {
                if (isTree) {
                    var id = SqGetIdFromTreeEntityId(record.data.id);
                    if (me.checkExistingRecord(me.view.store, id)) {
                        me.valid &= false;
                    }
                }
            });
            notify = me.valid ? me.dropAllowed : me.dropNotAllowed;
        }
        return notify;
    },

    handleNodeDrop : function(data, record, position) {
        var view = this.view,
            store = view.getStore(),
            index, records, i, len;

        var isTree = data.view.$className == 'Ext.tree.View';
        if (isTree) {
            records = data.records;
            data.records = [];
            Ext.each(records, function(record) {
                var type = OPF.core.utils.RegistryNodeType.findRegistryNodeById(record.data.id);
                if (OPF.core.utils.RegistryNodeType.COLLECTION == type) {
                    var membership = Ext.create('OPF.console.resource.model.CollectionMembership', {
                        id: SqGetIdFromTreeEntityId(record.data.id),
                        memberId: null,
                        name: record.data.text,
                        path: record.data.path,
                        type: record.data.type
                    });
                    data.records.push(membership);
                }
            })
        } else if (data.copy) {
            records = data.records;
            data.records = [];
            for (i = 0, len = records.length; i < len; i++) {
                data.records.push(records[i].copy(records[i].getId()));
            }
        } else {
            data.view.store.remove(data.records, data.view === view);
        }

        index = store.indexOf(record);

        if (position !== 'before') {
            index++;
        }
        store.insert(index, data.records);
        view.getSelectionModel().select(data.records);
    },

    checkExistingRecord: function(destStore, id) {
        var exists = false;
        var collectionId = this.editPanel.nodeBasicFields.idField.getValue();
        if (collectionId == id) {
            exists = true;
        } else {
            Ext.each(destStore.data.items, function(item, index) {
                exists |= (item.data.id == id);
            });
        }
        return exists;
    }
});