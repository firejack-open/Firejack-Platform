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
    'OPF.prototype.component.ActionPanel',
    'OPF.prototype.component.AdvancedSearchPanel',
    'OPF.prototype.component.Breadcrumbs',
    'OPF.prototype.component.ContentPanel',
    'OPF.prototype.component.Footer',
    'OPF.prototype.component.FormPanel',
    'OPF.prototype.component.GridPanel',
    'OPF.prototype.component.Header',
    'OPF.prototype.component.LoginPanel',
    'OPF.prototype.component.ManagerPanel',
    'OPF.prototype.component.MenuNavigation',
    'OPF.prototype.component.MenuTreeNavigation',
    'OPF.prototype.component.TitlePanel'
]);

Ext.define('OPF.prototype.layout.StandardLayout', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf.prototype.layout.standard',

    flex: 1,

    components: [],

    initComponent: function() {

        var topComponents = [];
        var leftnavComponents = [];
        var bodyComponents = [];
        var sidebarComponents = [];
        var footerComponents = [];

        Ext.each(this.components, function(component) {
            switch(component.area) {
                case 'top':
                        topComponents.push(component);
                    break;
                case 'leftnav':
                        leftnavComponents.push(component);
                    break;
                case 'body':
                        bodyComponents.push(component);
                    break;
                case 'sidebar':
                        sidebarComponents.push(component);
                    break;
                case 'footer':
                        footerComponents.push(component);
                    break;
            }
        });

        this.footerComponents = footerComponents;

        var centerPanel = [
            {
                xtype: 'panel',
                layout: {
                    type: 'vbox',
                    align : 'stretch',
                    autoSize: true
                },
                border: false,
                columnWidth: 1,
                items: bodyComponents
            }
        ];

        if (sidebarComponents.length > 0) {
            centerPanel.push({
                xtype: 'panel',
                layout: {
                    type: 'vbox',
                    align : 'stretch',
                    autoSize: true
                },
                border: false,
                width: 266,
                items: sidebarComponents
            });
        }

        var bodyPanel = {
            xtype: 'panel',
            id: 'bodyPanel',
            layout: {
                type: 'column',
                autoSize: true
            },
            border: false,
            cls: 'home-container',
            items: [
                {
                    xtype: 'panel',
                    id: 'leftPanel',
                    cls: 'left-panel',
                    layout: {
                        type: 'fit',
                        autoSize: true
                    },
                    border: false,
                    width: 200,
                    items: leftnavComponents
                },
                {
                    xtype: 'panel',
                    cls: 'body-panel',
                    border: false,
                    flex: 1,
                    columnWidth: 1,
                    autoHeight: true,
                    layout: 'column',
                    items: centerPanel,
                    listeners: {
                        resize: function(panel, adjWidth, adjHeight) {
                            var height = panel.getHeight();
                            var leftPanel = panel.up('#bodyPanel').down('#leftPanel');
                            leftPanel.setHeight(height);
                        }
                    }
                }
            ]
        };

        this.items = [
            {
                xtype: 'container',
                cls: 'top-container',
                items: topComponents
            },
            {
                xtype: 'container',
                cls: 'body-container',
                items: bodyPanel
            },
            {
                xtype: 'container',
                cls: 'footer-container',
                items: footerComponents
            }
        ];

        this.callParent(arguments);
    }

});