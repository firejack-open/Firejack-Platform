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