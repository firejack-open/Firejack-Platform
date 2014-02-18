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
    'OPF.prototype.component.AdvancedSearchPanel',
    'OPF.prototype.component.GridPanel',
    'OPF.prototype.component.FormPanel'
]);

Ext.define('OPF.prototype.component.ManagerPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.manager-panel',

    model: null,
    border: false,
    margin: '0 5 0 0',

    configs: {
        formPanel: null,
        gridPanel: null
    },

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.Object.merge(this.configs, cfg.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.gridPanel = Ext.create('OPF.prototype.component.GridPanel', {
            managerPanel: this,
            model: this.model,
            configs: this.configs.gridPanel
        });

        this.formPanel = Ext.create('OPF.prototype.component.FormPanel', {
            managerPanel: this,
            model: this.model,
            hidden: true,
            configs: this.configs.formPanel
        });

        this.items = [
            this.gridPanel,
            this.formPanel
        ];

        this.callParent(arguments);

        var entityId = OPF.getQueryParam("entityId");
        if (OPF.isNotEmpty(entityId) && Ext.isNumeric(entityId)) {
            var model = Ext.create(this.model);
            Ext.Ajax.request({
                url: model.self.restSuffixUrl + '/' + entityId,
                method: 'GET',
                success: function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        var jsonData = vo.data[0];
                        var record = Ext.create(me.model, jsonData);
                        me.gridPanel.showEditor(record);
                    } else {
                        Ext.Msg.alert('Error', vo.message);
                    }
                },

                failure: function(response) {
                    var responseStatus = Ext.decode(response.responseText);
                    var messages = [];
                    for (var i = 0; i < responseStatus.data.length; i++) {
                        var msg = responseStatus.data[i].msg;
                        messages.push(msg);
                    }
                    Ext.Msg.alert('Error', messages.join('<br/>'));
                }
            });
        }
    }
});
