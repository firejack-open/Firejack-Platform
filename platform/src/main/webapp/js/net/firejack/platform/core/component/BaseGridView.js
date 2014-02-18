//@tag opf-editor
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
Ext.define('OPF.core.component.BaseGridView', {
    extend: 'Ext.panel.Panel',

    layout: 'fit',
    padding: 10,
    border: false,

    entityName: '',
    iconGridUrl: '',
    managerLayout: null,
    registryNodeType: null,

    isRestStore: false,

    constructor: function(managerLayout, cfg) {
        cfg = cfg || {};
        OPF.core.component.BaseGridView.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout
        }, cfg));
    },

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        var proxyType = this.isRestStore ? 'rest' : 'ajax';

        this.store = Ext.create('Ext.data.Store', {
//            autoSync: this.isRestStore,
            model: this.registryNodeType.getModel(),
            proxy: {
                type: proxyType,
                url : this.registryNodeType.generateUrl(),
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    successProperty: 'success',
                    idProperty: 'id',
                    root: 'data',
                    messageProperty: 'message'
                },
                writer: {
                    type: 'json',
                    root: 'data'
                }
            },
            listeners: {
                beforeload: function(store, operation) {
                    instance.storeBeforeLoad(store, operation);
                },
                beforesync: function(options, eOpts) {
                    instance.storeBeforeSync(options, eOpts);
                },
                load: function (store, records, successful, eOpts) {
                    instance.storeLoad(store, records, successful, eOpts);
                }
            }
        });

        this.searchField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            width: 250,
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    instance.refreshFields();
                },
                buffer: 500
            }
        });

        var toolbarButtons = [];
        if (OPF.isNotEmpty(this.gridTButtons)) {
            toolbarButtons.push(this.gridTButtons);
        }
        toolbarButtons.push(
            this.searchField,
            {
                xtype: 'tbseparator'
            },
            {
                xtype: 'button',
                text: 'clear',
                handler: function () {
                    instance.searchField.setValue('');
                    instance.refreshFields();
                }
            },
            {
                xtype: 'tbfill'
            },
            {
                xtype: 'button',
                text: 'delete',
                iconCls: 'silk-delete',
                handler: function () {
                    var records = instance.grid.getSelectionModel().getSelection();
                    if (records.length == 0) {
                        OPF.Msg.setAlert(false, 'Not selected role.');
                        return false;
                    }
                    instance.deleteRecords(records);
                }
            }
        );

        this.grid = Ext.create('Ext.grid.Panel', {
            title: this.entityName + ' List: ---',
            flex: 1,
            store: this.store,
            headerAsText: true,
            multiSelect: true,
            columns: this.getColumns(),
            viewConfig: {
                plugins: this.getConfigPlugins(),
                listeners: this.getConfigPluginsListeners()
            },
            plugins: this.getPlugins(),
            tbar: {
                xtype: 'toolbar',
                items: toolbarButtons
            },
            bbar: Ext.create('Ext.PagingToolbar', {
                store: this.store,
                displayInfo: true,
                displayMsg: 'Displaying topics {0} - {1} of {2}',
                emptyMsg: "No topics to display"
            }),
            listeners: {
                itemdblclick: function(grid, record) {
                    instance.onItemDblClick(grid, record);
                },
                itemclick: function(gridView, record, htmlElement, index, e, eOpts) {
                    instance.onItemClick(gridView, record, htmlElement, index, e, eOpts);
                },
                selectionchange: function(selectionModel, selectedModels, eOpts) {
                    instance.onSelectionChanged(selectionModel, selectedModels, eOpts);
                }
            }
        });

        this.items = [
            this.grid
        ];

        this.callParent(arguments);
    },

    listeners: {
        activate: function(panel) {
            this.onActivate(panel);
            this.refreshGridTitle();
            this.store.load();
        },
        deactivate: function(panel) {
            this.onDeactivate(panel);
        }
    },

    onActivate: function(panel) {

    },

    onDeactivate: function(panel) {

    },

    onItemDblClick: function(grid, record) {
        var instance = this;

        Ext.Ajax.request({
            url: instance.registryNodeType.generateGetUrl(record.data.id),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var editPanel = instance.registryNodeType.createEditPanel(instance.managerLayout);
                editPanel.saveAs = 'update';

                var registryJsonData = Ext.decode(response.responseText);
                editPanel.showEditPanel(registryJsonData.data[0]);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    onItemClick: function(gridView, record, htmlElement, index, e, eOpts) {
        //
    },

    onSelectionChanged: function(selectionModel, selectedModels, eOpts) {
        //
    },

    storeBeforeLoad: function(store, operation) {
        var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
        var selectedNodeId = SqGetIdFromTreeEntityId(selectedNode.data.id);

        var url;
        var searchPhrase = this.searchField.getValue();
        if (OPF.isBlank(searchPhrase)) {
            url = this.registryNodeType.generateUrl('/node/' + selectedNodeId);
        } else {
            url = this.registryNodeType.generateUrl('/node/search/' + selectedNodeId + '/' + escape(searchPhrase));
        }
        store.proxy.url = url;
    },

    storeBeforeSync: function(options, eOpts) {
        this.store.proxy.url = this.registryNodeType.generateUrl();
    },

    storeLoad: function(store, records, successful, eOpts) {

    },

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
                dataIndex: 'path',
                header: 'Path',
                sortable: true,
                width: 400
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

    getPlugins: function() {
        return null;
    },

    getConfigPlugins: function() {
        return null;
    },

    getConfigPluginsListeners: function() {
        return null;
    },

    deleteRecords: function(records) {
        var instance = this;

        Ext.MessageBox.confirm(
            'Deleting selected ' + this.entityName + '(s)',
            'Are you sure?',
            function(btn) {
                if (btn[0] == 'y') {
                    Ext.each(records, function(record) {
                        instance.deleteRecord(record);
                    })
                }
            }
        );
    },

    deleteRecord: function(record) {
        var instance = this;

        Ext.Ajax.request({
            url: this.generateDeleteUrl(record),
            method: 'DELETE',
            jsonData: '[]',

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(true, vo.message);
                instance.grid.store.remove(record);
            },

            failure: function(response) {
                if (response.status == 409) {
                    var jsonData = Ext.decode(response.responseText);
                    Ext.MessageBox.confirm('Deleting ' + this.entityName, jsonData.message,
                        function(btn) {
                            if (btn[0] == 'y') {
                                Ext.Ajax.request({
                                    url: instance.registryNodeType.generateGetUrl(record.id, '?force=true'),
                                    method: 'DELETE',
                                    jsonData: '[]',

                                    success: function(response, action) {
                                        var vo = Ext.decode(response.responseText);
                                        OPF.Msg.setAlert(true, vo.message);
                                        instance.grid.store.remove(record);
                                    },

                                    failure: function(response) {
                                        OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                                    }
                                });
                            }
                        }
                    );
                } else {
                    OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                }
            }
        });
    },

    generateDeleteUrl: function(record) {
        return this.registryNodeType.generateGetUrl(record.data.id);
    },

    refreshFields: function() {
        this.refreshGridTitle();
        this.store.load();
    },

    refreshGridTitle: function() {
        var selectedNode = this.managerLayout.navigationPanel.getSelectedNode(false);
        var path = '---';
        if (OPF.isNotEmpty(selectedNode)) {
            path = selectedNode.data.lookup;
        }
        this.grid.setTitle(this.entityName + ' List: ' + path);
    }

});
