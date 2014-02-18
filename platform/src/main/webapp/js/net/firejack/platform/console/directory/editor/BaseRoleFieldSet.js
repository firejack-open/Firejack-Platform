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
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.dd.*'
]);

/**
 * The BaseRoleFieldSet component inherited from FieldSet component and
 * containing the available and assigned role grids.
 */
Ext.define('OPF.console.directory.editor.BaseRoleFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Roles',
    subFieldLabel: '',

    layout: 'anchor',

    initComponent: function() {
        var instance = this;

        this.gridColumns = [
            {
                xtype: 'gridcolumn',
                dataIndex: 'lookup',
                header: 'Role',
                sortable: true,
                flex: 1,
                renderer: function(value, metadata) {
                    var subLookup = value;
                    if (value.length > 200) {
                        var names = value.split('.');
                        var result = '';
                        for (var i = names.length - 1; i > -1 ; i--) {
                            var name = names[i];
                            var subName = (i == names.length - 1) ? name : name + '.' + result;
                            if (subName.length < 200) {
                                result = subName;
                            } else {
                                break;
                            }
                        }
                        if (result != value) {
                            subLookup = '...' + result;
                        }
                    }
                    metadata.attr = 'ext:qtip="' + value + '"';
                    return subLookup;
                }
            }
        ];

        this.availableRolesStore = new Ext.data.Store({
            model: 'OPF.console.authority.model.Role',
            proxy: {
                type: 'ajax',
                url : this.getRoleListUrl(null),
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
                    fn: function(store, operation) {
                        var searchPhrase = instance.searchField.getValue();
                        store.proxy.url = instance.getRoleListUrl(searchPhrase);
                    }
                }
            }
        });

        this.searchField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: "search",
            width: 400,
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.refreshFields();
                },
                keyup: function(cmp, e) {
                    instance.refreshFields();
                },
                buffer: 500
            }
        });

        this.availableRolesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            title: 'Available Roles',
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'availableGridDDGroup',
                    dropGroup: 'assignedGridDDGroup'
                }
            },
            multiSelect: true,
            stripeRows: true,
            store: this.availableRolesStore,
            anchor: '100%',
            height: 240,
            itemId: 'avails',
            columns: this.gridColumns,
            tbar: {
                xtype: 'toolbar',
                items: [
                    this.searchField,
                    {
                        xtype: 'tbfill'
                    },
                    {
                        xtype: 'button',
                        text: 'clear',
                        click: {
                            fn: function () {
                                instance.searchField.setValue('');
                                instance.refreshFields();
                            },
                            buffer: 500
                        }
                    }
                ]
            }
        });

        this.assignedRolesStore = new Ext.data.Store({
            model: 'OPF.console.authority.model.Role',
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

        this.assignedRolesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            title: 'Assigned Roles',
            store: this.assignedRolesStore,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'assignedGridDDGroup',
                    dropGroup: 'availableGridDDGroup'
                }
            },
            multiSelect: true,
            stripeRows: true,
            anchor: '100%',
            height: 240,
            columns: this.gridColumns
        });

        this.items = [
            this.availableRolesGrid,
            {
                xtype: 'container',
                anchor: '100%',
                height: 10
            },
            this.assignedRolesGrid
        ];

        this.callParent(arguments);
    },

    refreshFields: function(selectedNode) {
        if (OPF.isEmpty(selectedNode)) {
            var exceptRolesIds = [];
            this.assignedRolesStore.each(function(record) {
                exceptRolesIds.push(record.data.id);
            });
            var options = {
                params: {
                    exceptIds: exceptRolesIds
                }
            };
            this.availableRolesStore.load(options);
        }
    },

    getRoleListUrl: function(searchPhrase) {
        //abstract
    }

});