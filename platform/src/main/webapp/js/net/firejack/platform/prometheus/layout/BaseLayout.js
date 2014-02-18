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
    'OPF.prometheus.component.topmenu.TopMenuComponent',
    'OPF.prometheus.component.breadcrumbs.BreadcrumbsComponent',
    'OPF.prometheus.component.header.HeaderComponent',
    'OPF.prometheus.component.footer.FooterComponent',
    'OPF.prometheus.component.login.LoginComponent',
    'OPF.prometheus.component.forgotpassword.ForgotPasswordComponent',
    'OPF.prometheus.component.content.ContentComponent',
    'OPF.prometheus.component.action.ActionComponent',
    'OPF.prometheus.component.title.TitleComponent'
]);

Ext.define('OPF.prometheus.layout.BaseLayout', {
    extend: 'OPF.prometheus.layout.AbstractLayout',
    alias: 'widget.prometheus.layout.base',

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

        this.items = [
            {
                xtype: 'container',
                cls: 'top-container',
                hidden: topComponents.length == 0,
                items: topComponents
            },
            {
                xtype: 'container',
                cls: 'body-container',
                items: bodyComponents
            },
            {
                xtype: 'container',
                cls: 'footer',
                hidden: footerComponents.length == 0,
                items: footerComponents
            }
        ];

        this.callParent(arguments);
    }

});