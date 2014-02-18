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

Ext.define('OPF.prometheus.component.manager.SimpleSearchComponent', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.prometheus.component.simple-search-component',

    border: false,
    cls: 'simple-search-panel',

    model: null,
    managerPanel: null,
    store: null,

    searchPanel: null,
    searchMode: 'SIMPLE_SEARCH',

    configs: null,

    constructor: function(cfg) {
        cfg = cfg || {};
        cfg.configs = cfg.configs || {};
        cfg.configs = Ext.apply(cfg.configs, cfg.configs, this.configs);

        this.superclass.constructor.call(this, cfg);
    },

    initComponent: function() {
        var me = this;

        this.searchField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            cls: 'search-field',
            width: 350,
            listeners: {
                specialkey:function (field, e) {
                    if (e.getKey() == e.ENTER) {
                        me.onClickSearchButton();
                    }
                }
            }
        });

        this.advancedSearchButton = Ext.create('Ext.button.Button', {
            text: 'Search',
            ui: 'search-button',
            height: 28,
            handler: me.onClickSearchButton,
            scope: this
        });

        this.items = [
            {
                xtype: 'form',
                border: false,
                items: [
                    {
                        xtype: 'opf-fieldcontainer',
                        layout: 'hbox',
                        flex: 1,
                        items: [
                            this.searchField,
                            this.advancedSearchButton
                        ]
                    }
                ]
            }
        ];

        this.callParent();
    },

    onClickSearchButton: function() {
        this.searchPanel.executeSearch(this.searchMode);
    }

});