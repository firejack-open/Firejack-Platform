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

Ext.define('OPF.prometheus.component.action.ActionComponent', {
    extend: 'OPF.prometheus.component.content.ContentComponent',
    alias: 'widget.prometheus.component.action-component',

    cls: 'action-panel content-panel',

    actionLookup: null,
    actions: [],

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        OPF.prometheus.component.action.ActionComponent.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        if (OPF.isNotEmpty(this.actions)) {
            var prefixUrl = OPF.ifBlank(OPF.Cfg.DASHBOARD_PREFIX_URL, '');

            var actionButton = Ext.create('Ext.Button', Ext.apply({
                text: 'Edit',
                cls: 'edit-btn',
                listeners: {
                    click: function() {
                        document.location = OPF.generateUrlByLookup((me.actionLookup || me.entityLookup), prefixUrl);
                    }
                }
            }, this.configs.editButtonConfigs));

            this.additionalComponents = [actionButton];
        }

        this.callParent(arguments);
    }
});