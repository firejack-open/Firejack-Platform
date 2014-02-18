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

Ext.define('OPF.core.component.ExportButton', {
    extend: 'Ext.button.Split',
    alias: 'widget.opf-export-button',

    buttonConfig: {},
    grid: null,
    strategies: [],

    initComponent: function() {
        var me = this;

        var menuItems = [];
        Ext.each(this.strategies, function(strategy, index) {
            menuItems.push({
                text: strategy.title,
                handler: function() {
                    me.exportData(strategy.strategy, strategy.filename);
                }
            });
            if (index == 0) {
                me.handler = function() {
                    me.exportData(strategy.strategy, strategy.filename);
                };
            }
        });

        this.menu = new Ext.menu.Menu({
            bodyCls: 'export',
            items: menuItems
        });

        this.callParent(arguments);
    },

    getQueryParams: function() {
        return null;
    },

    exportData: function(strategy, fileName) {
        var me = this;

        var fields = [];
        if (OPF.isNotEmpty(this.grid)) {
            Ext.each(this.grid.columns, function(column) {
                fields.push({
                    xtype: 'opf-hidden',
                    name: 'columns',
                    value: column.dataIndex
                });
            });
        }

        var urlQueryParams = '';
        var queryParams = this.getQueryParams();
        if (OPF.isNotEmpty(queryParams)) {
            var index = 0;
            for (name in queryParams) {
                if (index == 0) {
                    urlQueryParams += '?';
                }
                urlQueryParams += name + '=' + queryParams[name] + '&';
                index++;
            }
        }

        var exportForm = Ext.create('Ext.form.Panel', {
            url: OPF.Cfg.restUrl('site/export/' + strategy + '/' + fileName + urlQueryParams, true),
            monitorValid: true,
            standardSubmit: true, // do not use Ajax submit (default ExtJs)
            items: fields
        });
        exportForm.getForm().submit();
    }

});