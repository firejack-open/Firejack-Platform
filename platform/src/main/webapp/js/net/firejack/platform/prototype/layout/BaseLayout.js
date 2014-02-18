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
    'OPF.prototype.component.ForgotPasswordPanel',
    'OPF.prototype.component.ManagerPanel',
    'OPF.prototype.component.MenuNavigation',
    'OPF.prototype.component.MenuTreeNavigation',
    'OPF.prototype.component.TitlePanel'
]);

Ext.define('OPF.prototype.layout.BaseLayout', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf.prototype.layout.base',

    flex: 1,

    components: [],

    initComponent: function() {
        var topComponents = [];
        var bodyComponents = [];
        var footerComponents = [];

        Ext.each(this.components, function(component) {
            switch(component.area) {
                case 'top':
                        topComponents.push(component);
                    break;
                case 'body':
                        bodyComponents.push(component);
                    break;
                case 'footer':
                        footerComponents.push(component);
                    break;
            }
        });

        var bodyPanel = {
            xtype: 'panel',
            height: 600,
            border: false,
            layout: 'fit',
            cls: 'outline home-container',
            flex: 1,
            items: bodyComponents
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
                cls: 'footer-container',
                items: footerComponents
            }
        ];

        this.callParent(arguments);
    }

});