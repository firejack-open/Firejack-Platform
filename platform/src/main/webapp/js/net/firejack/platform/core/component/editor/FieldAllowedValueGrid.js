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


Ext.define('OPF.core.component.editor.AllowedValue', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'allowedValue', type: 'string' }
    ]
});

Ext.define('OPF.core.component.editor.FieldAllowedValuesGrid', {
    extend: 'Ext.grid.Panel',
    cls: 'border-radius-grid-body border-radius-grid-docked-top',
    height: 150,
    autoScroll: true,
    store: new Ext.data.Store({
        model: 'OPF.core.component.editor.AllowedValue',
        proxy: {
            type: 'memory',
            reader: {
                type: 'json',
                idProperty: 'allowedValue',
                root: 'allowedValue'
            },
            writer: {
                type: 'json'
            }
        }
    }),

    initComponent: function() {
        var grid = this;
        this.store = Ext.create('Ext.data.Store', {
            model: 'OPF.core.component.editor.AllowedValue',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    idProperty: 'allowedValue',
                    root: 'allowedValue'
                },
                writer: {
                    type: 'json'
                }
            },
            listeners: {
                datachanged: function(store) {
                    grid.determineScrollbars();
                }
            }
        });
        this.rowEditor = Ext.create('Ext.grid.plugin.RowEditing');
        this.allowedValueColumn = OPF.Ui.populateColumn('allowedValue', 'Allowed Value', {
            flex: 1,
            editor: 'textfield',
            renderer: 'htmlEncode'
        });
        this.plugins = [this.rowEditor];
        this.columns = [this.allowedValueColumn];
        this.addBtn = Ext.create('Ext.button.Button', {
            text: 'Add',
            iconCls: 'silk-add',
            handler: function(btn) {
                var grid = btn.up('grid');
                var allowedValueModel = Ext.create('OPF.core.component.editor.AllowedValue');
                allowedValueModel.set('allowedValue', '');
                grid.store.insert(0, allowedValueModel);
                grid.getRowEditor().startEdit(allowedValueModel, grid.allowedValueColumn)
            }
        });
        this.deleteBtn = Ext.create('Ext.button.Button', {
            text: 'Delete',
            iconCls: 'silk-delete',
            handler: function(btn) {
                var grid = btn.up('grid');
                var records = grid.getSelectionModel().getSelection();
                grid.store.remove(records);
            }
        });
        this.tbar = [this.addBtn, '-', this.deleteBtn];
        this.callParent(arguments);
    },

    getRowEditor: function() {
        if (this.rowEditor == null) {
            this.rowEditor = Ext.create('Ext.grid.plugin.RowEditing');
        }
        return this.rowEditor;
    },

    cleanFieldStore: function() {
        this.store.removeAll();
    },

    disableAllowedValuesGrid: function(disabled) {
        if (disabled) {
            this.cleanFieldStore();
            this.addBtn.disable();
            this.deleteBtn.disable();
        } else {
            this.addBtn.enable();
            this.deleteBtn.enable();
        }
    }

});