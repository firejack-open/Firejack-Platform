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

Ext.define('OPF.prototype.layout.form.ColumnFormLayout', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf-column-form-layout',

    layout: 'column',
    fields: [],

    defaults: {
        layout: 'anchor'
    },

//    constructor: function(cfg) {
//        cfg = cfg || {};
//        cfg.configs = cfg.configs || {};
//        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);
//
//        this.superclass.constructor.call(this, cfg);
//    },

    initComponent: function() {
        var centerComponents = [];
        var westComponents = [];
        var eastComponents = [];

        Ext.each(this.fields, function(field) {
            switch(field.region) {
                case 'west':
                    westComponents.push(field);
                    break;
                case 'east':
                    eastComponents.push(field);
                    break;
                default:
                    centerComponents.push(field);
                    break;
            }
        });

        this.columns = [];

        if (westComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: westComponents
            }, this.westRegionConfigs));
        }

        if (centerComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: centerComponents
            }, this.centerRegionConfigs));
        }

        if (eastComponents.length > 0) {
            this.columns.push(Ext.apply({
                xtype: 'container',
                items: eastComponents
            }, this.eastRegionConfigs));
        }

        if (this.columns.length > 0) {
            var columnWidth = 1.0 / this.columns.length;
            Ext.each(this.columns, function(column) {
                if (OPF.isEmpty(column.columnWidth) && OPF.isEmpty(column.width)) {
                    column.columnWidth = columnWidth;
                }
            });
        }

        this.items = this.columns;

        this.callParent(arguments);
    }

});