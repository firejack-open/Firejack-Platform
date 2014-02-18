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

Ext.define('OPF.prototype.component.ActionPanel', {
    extend: 'OPF.prototype.component.ContentPanel',
    alias: 'widget.opf.prototype.component.action-panel',

    cls: 'action-panel content-panel',

    actionLookup: null,
    actions: [],

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        OPF.prototype.component.ActionPanel.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.actions)) {
            var prefixUrl = OPF.ifBlank(OPF.Cfg.DASHBOARD_PREFIX_URL, '');

            var actionItems = [{ xtype: 'tbfill' }];

            var actionButton = Ext.create('Ext.Button', Ext.apply({
                text: 'edit',
                iconCls: 'icon-manage',
                scale: 'small',
                listeners: {
                    click: function() {
                        document.location = OPF.generateUrlByLookup((me.actionLookup || me.entityLookup), prefixUrl);
                    }
                }
            }, this.configs.editButtonConfigs));
            actionItems.push(actionButton);

            Ext.each(this.actions, function(action) {
                var actionButton = Ext.create('Ext.Button', {
                    text: action.text,
                    listeners: action.listeners
                });
                actionItems.push(actionButton);
            });
            this.dockedItems = Ext.create('Ext.toolbar.Toolbar', {
                dock: 'bottom',
                ui: 'footer',
                items: actionItems
            });
        }

        this.callParent(arguments);
    }
});