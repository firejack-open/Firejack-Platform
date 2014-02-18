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

Ext.define('OPF.console.inbox.view.AssociatedObjectPanel', {
    extend: 'Ext.grid.Panel',

    layout: 'fit',

    managerLayout: null,
    object: null,
    autoExpandColumn: 'propertyValue',

    constructor: function(managerLayout, tabTitle, object, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.view.AssociatedObjectPanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            title: tabTitle,
            object: object
        }, cfg));
    },

    initComponent: function() {
        this.columns = [
            OPF.Ui.populateColumn('name', 'Property Name', {width: 200, renderer: this.boldHeadersRenderer}),
            OPF.Ui.populateColumn('value', 'Property Value', {renderer: 'htmlEncode'})
        ];

        this.store = Ext.create('Ext.data.ArrayStore', {
            autoDestroy: true,
            idIndex: 0,
            fields: [
               'name',
               'value'
            ]
        });

        this.store.loadData(this.getPropertiesData());

        this.callParent(arguments);
    },

    getPropertiesData: function() {
        var data = [];
        var objectData = this.object;
        var count = 0;
        for(var field in objectData) {
            count++;
            data.push({
                name: field,
                value: objectData[field]
            });
        }
        return {
            total: count,
            success: true,
            message: 'success',
            data: data
        };
    },

    boldHeadersRenderer: function (value) {
        return String.format('<span style="color: #444444" ><b>' +  value + '</b></span>');
    }

});