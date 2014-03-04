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

Ext.require([
    'OPF.prometheus.component.leftmenu.LeftMenuComponent'
]);

Ext.define('OPF.prometheus.page.inbox.component.InboxMenuComponent', {
    extend: 'OPF.prometheus.component.leftmenu.LeftMenuComponent',
    alias: 'widget.prometheus.component.inbox-menu-component',

    autoInit: true,

    initComponent: function() {
        var me = this;

        me.addEvents(
            'menuitemclick'
        );

        this.callParent(arguments);
    },

    createMenu: function(data, ulContainer, deep) {
        var me = this;

        Ext.each(data, function(item) {
            if (item.lookup == 'com.coolmovies.coolmovies.gateway.inbox') {
                item.children = [
                    {
                        name: 'All Tasks',
                        elementType: 'TASK',
                        type: 'ALL',
                        active: true
                    },
                    {
                        name: 'My Tasks',
                        elementType: 'TASK',
                        type: 'MY'
                    },
                    {
                        name: 'My Team Tasks',
                        elementType: 'TASK',
                        type: 'TEAM'
                    }
                ]
            }
        });

        this.callParent(arguments);

        var htmlEls = Ext.query('.task-item', ulContainer.el.dom);
        Ext.each(htmlEls, function(htmlEl) {
            var el = Ext.get(htmlEl);
            var type = el.getAttribute('type');
            el.on('click', function(event, tag, options) {
                me.fireEvent('menuitemclick', me, options);
            }, me, {
                type: type,
                htmlEl: htmlEl,
                htmlEls: htmlEls
            });
        });
    },

    buildHrefTag: function (node, isActive) {
        var hrefTag = '<a ';
        if ('PAGE' == node.elementType) {
            var nodeMenuUrl = this.getMenuUrl(node.urlPath);
            hrefTag += 'href="' + OPF.Cfg.fullUrl(nodeMenuUrl, true) + '" class="page-item';
        } else if ('WIZARD' == node.elementType) {
            hrefTag += 'data="{wizardLookup: \'cmv.' + node.pageUrl + '\'}" class="wizard-item';
        } else if ('TASK' == node.elementType) {
            hrefTag += 'type="' + node.type + '" class="task-item';
        }
        hrefTag = hrefTag + ' ' + ((isActive || node.active) ? 'active' : '') + '">';
        hrefTag += Ext.String.capitalize(node.name);
        hrefTag += '</a>';
        return hrefTag;
    }

});