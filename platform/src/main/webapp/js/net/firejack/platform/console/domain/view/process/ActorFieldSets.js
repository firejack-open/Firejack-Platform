/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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


Ext.define('OPF.console.domain.view.process.BaseActorFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    layout: 'anchor',


    editPanel: null,
    header: null,
    dataProperty: null,
    entitySuffix: null,

    constructor: function(editPanel, header, dataProperty, entitySuffix, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.BaseActorFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel,
            header: header,
            dataProperty: dataProperty,
            entitySuffix: entitySuffix
        }, cfg));
    },

    initComponent: function() {
        this.searchField = Ext.create('OPF.core.component.TextField', {
            name: "search",
            width: 400,
            enableKeyEvents: true,
            marker: 'search'
        });

        this.availableEntitiesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            ddGroup: 'available' + this.entitySuffix + 'GridDDGroup',
            enableDragDrop: true,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'available' + this.entitySuffix + 'GridDDGroup'
                }
            },
            store: 'Available' + this.entitySuffix,
            title: 'Available ' + this.entitySuffix,
            anchor: '100%',
            height: 240,
            columns: [
                OPF.Ui.populateColumn(this.dataProperty, this.header, {flex: 1})
            ],
            marker: 'available' + this.entitySuffix + 'Grid',
            tbar: {
                xtype: 'toolbar',
                items: [
                    this.searchField,
                    {
                        xtype: 'tbfill'
                    },
                    OPF.Ui.createBtn('clear', 50, 'clear-search')
                ]
            }
        });

        this.assignedEntitiesGrid = Ext.create('Ext.grid.Panel', {
            cls: 'border-radius-grid-body border-radius-grid-header-top',
            store: 'Assigned' + this.entitySuffix,
            ddGroup: 'assigned' + this.entitySuffix + 'GridDDGroup',
            enableDragDrop: true,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dropGroup: 'available' + this.entitySuffix + 'GridDDGroup'
                }
            },
            title: 'Assigned ' + this.entitySuffix,
            anchor: '100%',
            height: 240,
            columns: [
                OPF.Ui.populateColumn(this.dataProperty, this.header, {flex: 1})
            ],
            marker: 'assigned' + this.entitySuffix + 'Grid'
        });

        this.items = [
            this.availableEntitiesGrid,
            {
                xtype: 'container',
                anchor: '100%',
                height: 10
            },
            this.assignedEntitiesGrid
        ];

        this.callParent(arguments);
    }
});

/**
 *
 */
Ext.define('OPF.console.domain.view.process.UserActorAssociation', {
    extend: 'OPF.console.domain.view.process.BaseActorFieldSet',
    alias: 'widget.user-actor-fieldset',

    fieldLabel: 'Users',
    subFieldLabel: '',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.UserActorAssociation.superclass.constructor.call(
            this, editPanel, 'User', 'username', 'Users', cfg);
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.view.process.RoleActorAssociation', {
    extend: 'OPF.console.domain.view.process.BaseActorFieldSet',
    alias: 'widget.role-actor-fieldset',

    fieldLabel: 'Roles',
    subFieldLabel: '',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.RoleActorAssociation.superclass.constructor.call(
            this, editPanel, 'Role', 'lookup', 'Roles', cfg);
    }

});

/**
 *
 */
Ext.define('OPF.console.domain.view.process.GroupActorAssociation', {
    extend: 'OPF.console.domain.view.process.BaseActorFieldSet',
    alias: 'widget.group-actor-fieldset',

    fieldLabel: 'Groups',
    subFieldLabel: '',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.console.domain.view.process.GroupActorAssociation.superclass.constructor.call(
            this, editPanel, 'Group', 'lookup', 'Groups', cfg);
    }

});