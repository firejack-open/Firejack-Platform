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