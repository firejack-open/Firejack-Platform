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