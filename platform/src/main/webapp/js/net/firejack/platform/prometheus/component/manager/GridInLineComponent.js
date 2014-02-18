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

Ext.require([
    'OPF.prometheus.component.manager.AbstractGridComponent',
    'OPF.prometheus.component.manager.GridInLineDataWriter'
]);

Ext.define('OPF.prometheus.component.manager.GridInLineComponent', {
    extend: 'OPF.prometheus.component.manager.AbstractGridComponent',
    alias: 'widget.prometheus.component.grid-in-line-component',

    actionColumnButtons: ['delete'],

    additionalActionColumns: [],

    initComponent: function() {
        var me = this;

        this.modelInstance = Ext.create(this.model);

        this.store = this.getStore({
            autoSync: true

        });

        this.rowEditor = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToEdit: 2,
            listeners: {
                beforeedit: function(rowEditing, context) {
                    var form = {
                        getForm: function() {
                            return rowEditing.editor.form;
                        }
                    };
                    me.validator = new OPF.core.validation.FormValidator(form, me.model.ruleId, null, {
                        useBaseUrl: false
                    });
                    return true;
                }//,
//                edit: function(rowEditing, context) {
//                    var record = context.record;
//                    record.commit();
//                }
            }
        });

        this.grid = Ext.create('Ext.grid.Panel', Ext.apply({
            height: 510,
            cls: 'grid-panel',
            store: this.store,
            columns: this.getColumnsByModel(),
            selType: 'rowmodel',
            plugins: [
                this.rowEditor
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    store: this.store,
                    dock: 'bottom',
                    displayInfo: true
                }
            ]
        }, this.configs.gridConfigs));

        this.items = [
            this.grid
        ];

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                items: [
                    '->',
                    Ext.apply({
                        text: 'Add',
                        action: 'add',
                        ui: 'add',
                        width: 70,
                        scope: this,
                        handler: this.onAddClick
                    }, this.configs.addButtonConfigs)
                ]
            }
        ];

        this.callParent();
    },

    onAddClick: function() {
        var model = Ext.create(this.model);
        model.phantom = false;
        this.store.add(model);
        model.phantom = true;
        this.grid.editingPlugin.startEdit(model, this.grid.columns[0]);
    },

    getColumnsByModel: function() {
        var columns = this.callParent();

        var actionColumnItems = [];
        if (Ext.Array.contains(this.actionColumnButtons, 'delete')) {
            actionColumnItems.push({
                icon: OPF.Cfg.fullUrl('/images/icons/prototype/delete.png', false),
                tooltip: 'Delete',
                handler: function(grid, rowIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    if (record) {
                        grid.getStore().remove(record);
                    }
                }
            });
        }

        for (var i= 0,length=this.additionalActionColumns.length;i<length;i++) {
            var columnToAdd = this.additionalActionColumns[i];
            actionColumnItems.push(columnToAdd);
        }

        if (actionColumnItems.length > 0) {
            columns.push({
                xtype:'actioncolumn',
                text: '',
                width: actionColumnItems.length * 20,
                items: actionColumnItems
            });
        }

        return columns;
    },

    getStore: function(options) {
        var me = this;
        var writer = Ext.create('OPF.prometheus.component.manager.GridInLineDataWriter', {
            root: 'data'
        });
        return Ext.create('Ext.data.Store', Ext.apply({
            autoLoad: true,
            pageSize: 10,
            model: this.model,
            remoteSort: true,
            proxy: {
                type: 'rest',
                url: this.modelInstance.self.restSuffixUrl,
                reader: {
                    root: 'data',
                    totalProperty: 'total',
                    messageProperty: 'message',
                    idProperty: this.modelInstance.idProperty
                },
                writer: writer,
                simpleSortMode: true,
                startParam: 'offset',
                limitParam: 'limit',
                sortParam: 'sortColumn',
                directionParam: 'sortDirection'
            },
            listeners: {
                beforeload: function(store, operation) {
                    me.onStoreBeforeLoad(store, operation);
                },
                beforesync: function(options) {
                    me.onStoreBeforeSync(this, options);
                },
                'update': function(store, record, operation, eOpts) {
                    me.onStoreUpdate(store, record, operation);
                }
            }
        }, options));
    }

});