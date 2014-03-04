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

Ext.define('OPF.console.home.view.Home', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.home',

    activeButtonLookup: 'net.firejack.platform.console.home',

    id: 'homeLayout',
    flex: 1,
    layout: 'column',
    bodyBorder: false,
    bodyStyle: 'padding:5px',
    defaults: {
        border: false,
        bodyStyle: 'padding:5px'
    },

    initComponent: function() {

        this.items = [
            {
                width: 250,
                defaults: {
                    bodyStyle: 'padding:5px'
                },
                items: [
                    {
                        title: 'Actions',
                        defaults: {
                            bodyStyle: 'padding:5px'
                        },
                        items: [
                            {
                                id: 'pnlNewUser',
                                border: false,
                                layout: 'fit',
                                items: [
                                    {
                                        xtype: 'button',
                                        id: 'btnNewUser',
                                        text: 'NEW USER'
                                    }
                                ]
                            },
                            {
                                id: 'pnlSignUp',
                                border: false,
                                layout: 'fit',
                                items: [
                                    {
                                        xtype: 'button',
                                        id: 'btnSignUp',
                                        text: 'SIGN UP'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                columnWidth: .75,
                defaults: {
                    bodyStyle: 'padding:5px'
                },
                items: [
                    {
                        xtype: 'home-dashboard'
                    },
                    {
                        id: 'pnlWelcome',
                        title: 'Welcome',
                        items: [
                            {
                                xtype:  'label',
                                contentEl: 'welcome-message'
                            },
                            {
                                xtype: 'panel',
                                border: false,
                                buttonAlign: 'right',
                                items: [
                                    {
                                        xtype:  'label',
                                        html: '<div>To take a look at the getting started documentation, please click below.</div>'
                                    }
                                ],
                                buttons: [
                                    {
                                        id: 'btnGettingStarted',
                                        xtype: 'button',
                                        text: 'GETTING STARTED'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                columnWidth: .25,
                defaults: {
                    bodyStyle: 'padding:5px'
                },
                items: [
                    {
                        xtype: 'home-profile'
                    },
                    {
                        xtype: 'home-login'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});