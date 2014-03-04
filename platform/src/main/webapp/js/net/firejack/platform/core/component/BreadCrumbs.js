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

//@tag opf-console



Ext.define('OPF.core.component.BreadCrumbs', {
    extend: 'Ext.container.Container',
    alias : 'widget.breadcrumbs',

    renderTo: 'breadcrumbs',
    cls: 'breadcrumbs',

    initComponent: function() {

        this.breadCrumbsTemplate = new Ext.XTemplate(
            '<div class="breadcrumbs">',
                '<ul>',
                    '<tpl for=".">',
                        '<li class="arrow"><a href="#">{name}</a></li>',
                    '</tpl>',
                '</ul>',
            '</div>'
        );

        this.items = [
//            {
//                xtype: 'box',
//                tpl: this.breadCrumbsTemplate,
//                data: [
//                    { name: 'Home' },
//                    { name: 'Domain' },
//                    { name: 'Action' }
//                ]
//            }
        ];
        if (OPF.Cfg.USER_INFO.username) {
            this.items.push(
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'right'
                    },
                    border: false,
                    defaults: {
                        height: 24,
                        margin: '0 10 0 0'
                    },
                    items: [
                        {
                            xtype:'button',
//                            ui: 'suggestion',
                            text: '',
                            tooltip: 'Suggestion list',
                            width: 50
                        },
                        {
                            xtype: 'component',
                            cls:'user-name',
                            html: OPF.Cfg.USER_INFO.username
//                            menu: {
//                                xtype: 'menu',
//                                ui: 'user-menu',
//                                items: [
//                                    {
//                                        text:'Profile'
//                                    },{
//                                        text:'Settings'
//                                    },{
//                                        text:'Logout',
//                                        handler: function() {
//                                            document.location = OPF.Cfg.fullUrl('console/logout');
//                                        }
//                                    }
//                                ]
//                            }
                        },
                        {
                            xtype:'button',
//                            ui: 'logout',
                            text: '',
                            tooltip: 'Logout',
                            width: 20,
                            handler: function() {
                                document.location = OPF.Cfg.fullUrl('console/logout');
                            }
                        },
                    ]
                }
//                {
//                    xtype: 'container',
//                    cls: 'welcome_container',
//                    items: [
//                        {
//                            xtype: 'tbtext',
//                            cls: 'welcome',
//                            text: 'Welcome, ' + OPF.Cfg.USER_INFO.username
//                        },
//                        {
//                            xtype: 'button',
//                            text: '',
//                            itemId: 'logout',
//                            cls: 'logout_container',
//                            handler: function() {
//                                document.location = OPF.Cfg.fullUrl('console/logout');
//                            }
//                        }
//                    ]
//                }
            );
        }

        this.callParent(arguments);
    }
});