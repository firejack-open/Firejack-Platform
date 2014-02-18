//@tag opf-console
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