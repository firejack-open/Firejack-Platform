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

Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.dd.*'
]);

/**
 * The PermissionFieldSet component inherited from FieldSet component and
 * containing the available and assigned permissions grids.
 */
Ext.define('OPF.core.component.editor.PermissionFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'Permissions',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.PermissionFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.gridColumns = [
            {
                xtype: 'gridcolumn',
                dataIndex: 'lookup',
                header: 'Permission',
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

        this.availablePermissionsStore = new Ext.data.Store({
            model: 'OPF.console.authority.model.Permission',
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.PERMISSION.generateUrl(),
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
                        var url;
                        var searchPhrase = instance.searchField.getValue();
                        if (isBlank(searchPhrase)) {
                            url = OPF.core.utils.RegistryNodeType.PERMISSION.generateUrl();
                        } else {
                            url = OPF.core.utils.RegistryNodeType.PERMISSION.generateUrl('/search/' + escape(searchPhrase));
                        }
                        store.proxy.url = url;
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

        this.availablePermissionsGrid = Ext.create('Ext.grid.Panel', {
            title: 'Available Permissions',
            cls: 'border-radius-grid-toolbar border-radius-grid-header-top',
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'availableGridDDGroup',
                    dropGroup: 'assignedGridDDGroup'
                }
            },
            multiSelect: true,
            stripeRows: true,
            store: this.availablePermissionsStore,
            anchor: '100%',
            height: 240,
            columns: this.gridColumns,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        this.searchField,
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'button',
                            text: 'clear',
                            listeners: {
                                click: {
                                    fn: function () {
                                        instance.searchField.setValue('');
                                        instance.refreshFields();
                                    },
                                    buffer: 500
                                }
                            }
                        }
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    store: this.availablePermissionsStore,
                    dock: 'bottom',
                    displayInfo: true
                }
            ]
        });

        this.assignedPermissionsStore = new Ext.data.Store({
            model: 'OPF.console.authority.model.Permission',
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

        this.assignedPermissionsGrid = Ext.create('Ext.grid.Panel', {
            title: 'Assigned Permissions',
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            store: this.assignedPermissionsStore,
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
            this.availablePermissionsGrid,
            {
                xtype: 'container',
                anchor: '100%',
                height: 10
            },
            this.assignedPermissionsGrid
        ];

        this.callParent(arguments);
    },

    refreshFields: function(selectedNode) {
        if (isEmpty(selectedNode)) {
            var exceptPermissionIds = [];
            this.assignedPermissionsStore.each(function(record) {
                exceptPermissionIds.push(record.data.id);
            });
            var options = {
                params: {
                    exceptIds: Ext.encode(exceptPermissionIds)
                }
            };
            this.availablePermissionsStore.load(options);
        }
    }

});