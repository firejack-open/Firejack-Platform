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

