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
    'OPF.prometheus.component.toolbar.ToolbarComponent',
    'OPF.prometheus.component.header.HeaderComponent',
    'OPF.prometheus.component.topmenu.TopMenuComponent',
    'OPF.prometheus.component.breadcrumbs.BreadcrumbsComponent',
    'OPF.prometheus.component.leftmenu.LeftMenuComponent',
    'OPF.prometheus.component.footer.FooterComponent',
    'OPF.prometheus.component.manager.ManagerComponent',
    'OPF.prometheus.component.content.ContentComponent',
    'OPF.prometheus.component.action.ActionComponent',
    'OPF.prometheus.component.title.TitleComponent',
    'OPF.prometheus.component.securitycontroller.SecurityControllerComponent'
]);

Ext.define('OPF.prometheus.layout.StandardLayout', {
    extend: 'OPF.prometheus.layout.AbstractLayout',
    alias: 'widget.prometheus.layout.standard',

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
                xtype: 'container',
                border: false,
                columnWidth: 1,
                items: bodyComponents
            }
        ];

        if (sidebarComponents.length > 0) {
            centerPanel.push({
                xtype: 'container',
                border: false,
                width: 266,
                items: sidebarComponents
            });
        }

        var bodyPanel = {
            xtype: 'container',
            id: 'bodyPanel',
            layout: {
                type: 'column',
                autoSize: true
            },
            cls: 'home-container',
            items: [
                {
                    xtype: 'container',
                    id: 'leftPanel',
                    cls: 'left-panel',
                    width: 200,
                    autoHeight: true,
                    items: leftnavComponents
                },
                {
                    xtype: 'container',
                    cls: 'body-panel',
                    columnWidth: 1,
                    autoHeight: true,
                    layout: 'column',
                    items: centerPanel//,
//                    listeners: {
//                        resize: function(panel, adjWidth, adjHeight) {
//                            var bodyHeight = panel.getHeight();
//                            var leftPanel = panel.up('#bodyPanel').down('#leftPanel');
//                            var leftHeight = leftPanel.getHeight();
//
//                            if (bodyHeight > leftHeight) {
//                                leftPanel.setHeight(bodyHeight);
//                            } else if (bodyHeight < leftHeight) {
//                                bodyHeight.setHeight(leftHeight);
//                            }
//                        }
//                    }
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
                items: [
                    bodyPanel
                ]
            },
            {
                xtype: 'container',
                cls: 'footer',
                items: footerComponents
            }
        ];

        this.callParent(arguments);
    }

});