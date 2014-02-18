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

Ext.define('OPF.prometheus.component.topmenu.TopMenuComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.top-menu-component',

    autoEl: 'nav',
    cls: 'top-menu clr',

    autoInit: false,

    menuNavigationPath: null,

    initComponent: function() {
        var me = this;

        this.ulContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            autoEl: {
                tag: 'ul'
            }
        });

        this.items = [
            this.ulContainer
        ];

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(container) {
            if (container.autoInit) {
                container.initMenu();
            }
        }
    },

    initMenu: function() {
        var me = this;

        var navigationPath = this.menuNavigationPath || 'gateway';

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/site/navigation/tree/' + OPF.Cfg.PACKAGE_LOOKUP + '.' + navigationPath + '?lazyLoadResource=true'),
            method: 'GET',

            success: function(response, action) {
                var activeButton = null;
                var jsonData = Ext.decode(response.responseText);
                me.createMenu(jsonData.data);
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    createMenu: function(data) {
        var me = this;
        var menuHtml = '';
        Ext.each(data, function(jsonNode) {
            var isActive = jsonNode.lookup == OPF.Cfg.NAVIGATION_LOOKUP;
            var menuUrl = me.getMenuUrl(jsonNode.urlPath);
            menuHtml += '<li class="' + (isActive ? 'active' : '') + '">';
            menuHtml += '<a href="' + OPF.Cfg.fullUrl(menuUrl, true) + '">';
            menuHtml += Ext.String.capitalize(jsonNode.name);
            menuHtml += '</a></li>';
        });
        this.ulContainer.el.dom.innerHTML = menuHtml;
    },

    getMenuUrl: function(urlPath) {
        return urlPath;
    }

});