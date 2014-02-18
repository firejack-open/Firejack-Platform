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
    'OPF.prometheus.utils.RedeploySite'
]);

Ext.define('OPF.prometheus.wizard.AbstractWizard', {
    extend: 'Ext.window.Window',
    ui: 'wizards',

    width: 1100,
    height: 650,
    shadow: false,
    resizable: false,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    modal: true,

    mixins: {
        redeployer: 'OPF.prometheus.utils.RedeploySite'
    },

    initComponent: function() {
        var me = this;

        this.initBreadcrumbs();

        this.callParent(arguments);
    },

    initBreadcrumbs: function() {
        var me = this;

        this.deployMessagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            width: 1078,
            border: true
        });

        var cardPanels = this.items;
        var breadcrumbItems = [];
        Ext.each(cardPanels, function(item, index) {
            if (item.hidden !== true) {
                var breadcrumbItem = {
                    xtype: 'button',
                    ui: 'breadcrumb',
                    flex: 1,
                    html: item.title,
                    toggleGroup: 'breadcrumbs',
                    toggleIndex: index,
                    renderTpl: [
                        '<em id="{id}-btnWrap"<tpl if="splitCls"> class="{splitCls}"</tpl>>',
                        '<button id="{id}-btnEl" type="{type}" class="{btnCls}" hidefocus="true"',
                        '<tpl if="tabIndex"> tabIndex="{tabIndex}"</tpl>',
                        '<tpl if="disabled"> disabled="disabled"</tpl>',
                        ' role="button" autocomplete="off">',
                        '<span id="{id}-btnArrowEl" class="{baseCls}-arrow"></span>',
                        '<span id="{id}-btnInnerEl" class="{baseCls}-inner" style="{innerSpanStyle}">',
                        '{text}',
                        '</span>',
                        '<span id="{id}-btnIconEl" class="{baseCls}-icon {iconCls}"<tpl if="iconUrl"> style="background-image:url({iconUrl})"</tpl>></span>',
                        '</button>',
                        '</em>'
                    ]
                };
                breadcrumbItems.push(breadcrumbItem);
                delete item.title;

                item.index = index;
                item.listeners = {
                    activate: function(cardPanel) {
                        me.breadcrumbs.activeCardIndex = cardPanel.index;
                        Ext.each(me.breadcrumbs.items.items, function(breadcrumb, index) {
                            var toggleFn = undefined;
                            if (index < cardPanel.index) {
                                toggleFn = cardPanels[index + 1].prevFrameFn;
                            } else if (index > cardPanel.index) {
                                toggleFn = cardPanels[index - 1].nextFrameFn;
                            }
                            if (Ext.isFunction(toggleFn) || toggleFn === undefined) {
                                breadcrumb.setHandler(toggleFn);
                            }

                            if (cardPanel.index == index) {
                                breadcrumb.addCls('x-btn-breadcrumb-small-active');
                            } else {
                                breadcrumb.removeCls('x-btn-breadcrumb-small-active');
                            }
                        });
                        me.deployMessagePanel.cleanActiveErrors();
                    }
                };

                if (me.items.length == (index + 1)) {
                    me.initDeployPanel(item);
                }
            }
        });

        this.breadcrumbs = Ext.create('Ext.container.Container', {
            ui: 'breadcrumbs',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: breadcrumbItems
        });

        this.cardPanel = Ext.create('Ext.panel.Panel', {
            layout: 'card',
            flex: 1,
            border: false,
            items: this.items
        });

        this.items = [
            this.breadcrumbs,
            this.cardPanel
        ];
    },

    initDeployPanel: function(confirmation) {
        confirmation.layout = {
            type: 'vbox',
            align: 'center',
            padding: 10
        };

        confirmation.items = [
            {
                border: false,
                flex: 1,
                items: [
                    this.deployMessagePanel
                ]
            },
            {
                xtype: 'container',
                html: '<h2>Do you want to deploy now?</h2>',
                margin: '0 0 20 0'
            },
            {
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                border: false,
                defaults: {
                    margin: '0 10'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Yes',
                        ui: 'blue',
                        width: 250,
                        height: 60,
                        handler: confirmation.yesFn
                    },
                    {
                        xtype: 'button',
                        text: 'No',
                        ui: 'grey',
                        width: 250,
                        height: 60,
                        handler: confirmation.noFn
                    }
                ]
            },
            {
                border: false,
                flex: 1
            }
        ];
    },

    getCardPanelLayout: function() {
        return this.cardPanel.getLayout();
    },

    goToProvideDetailsPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    }

});