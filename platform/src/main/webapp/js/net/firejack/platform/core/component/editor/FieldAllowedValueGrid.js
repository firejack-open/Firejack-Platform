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

//@tag opf-editor



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