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