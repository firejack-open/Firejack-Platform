/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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