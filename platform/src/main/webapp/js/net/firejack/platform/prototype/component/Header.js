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

Ext.define('OPF.prototype.component.Header', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf.prototype.component.header',

    border: false,

    siteTitleLookup: null,
    siteSubTitleLookup: null,
    siteLogoLookup: null,

    logoutUrl: '/authentication/sign-out',

    initComponent: function() {

        var logoutBlockHtml = '';
        if (OPF.Cfg.USER_INFO.isLogged) {
            logoutBlockHtml =
//            '<div class="logout">' +
//                '<a href="#">Welcome, ' + OPF.Cfg.USER_INFO.username + '!</a> | <a href="' + OPF.Cfg.fullUrl(this.logoutUrl, true) + '">Logout</a>' +
//            '</div>';
            '<div class="logout">' +
                '<span class="welcome">Welcome,</span><a class="user-login" href="#" onclick="window.open(this.href); return false">' + OPF.Cfg.USER_INFO.username + '!</a> | <a class="user-logout" href="' + OPF.Cfg.fullUrl(this.logoutUrl, true) + '">Logout</a>' +
            '</div>';
        }

        this.html =
            '<div class="utility-tabs">' +
                '<div id="navigation" class="mainmenu"></div>' +
            '</div>' +
            '<div class="header">' +
                logoutBlockHtml +
                '<div class="logo">' +
                    '<div id="siteLogo" href="' + OPF.Cfg.fullUrl('', true) + '"></div>' +
                    '<div class="slogan">' +
                        '<div id="siteTitle"></div>' +
                        '<div id="siteSubTitle" class="subtitle"></div>' +
                    '</div>' +
                '</div>' +
            '</div>';

        this.callParent(arguments);
    },

    listeners: {
        afterrender: function(container) {

            this.navigation = new OPF.prototype.component.MenuNavigation({
                renderTo: 'navigation',
                currentNavigationLookup: OPF.Cfg.NAVIGATION_LOOKUP
            });

            this.siteLogo = new OPF.core.component.resource.ImageResourceControl({
                imgResourceLookup: this.siteLogoLookup,
                cls: 'site-logo-container',
                imgInnerCls: 'site-logo',
                renderTo: 'siteLogo',
                autoInit: true
            });

            this.siteTitle = new OPF.core.component.resource.TextResourceControl({
                textResourceLookup: this.siteTitleLookup,
                textInnerCls: 'site-title',
                cls: 'header-panel-title',
                renderTo: 'siteTitle',
                autoInit: true
            });

            this.siteSubTitle = new OPF.core.component.resource.TextResourceControl({
                textResourceLookup: this.siteSubTitleLookup,
                textInnerCls: 'site-sub-title',
                cls: 'header-panel-sub-title',
                renderTo: 'siteSubTitle',
                autoInit: true
            });

        }
    }

});

