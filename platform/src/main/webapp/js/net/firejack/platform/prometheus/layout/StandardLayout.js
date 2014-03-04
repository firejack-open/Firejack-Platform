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