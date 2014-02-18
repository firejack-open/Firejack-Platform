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

Ext.define('OPF.prometheus.component.leftmenu.LeftMenuComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.left-menu-component',

    cls: 'b-left-menu',

    autoInit: false,

    initComponent: function() {
        var me = this;

        this.ulContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            cls: 'left-menu',
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

        this.parentNavigationLookup = OPF.findPathFromLookup(OPF.Cfg.NAVIGATION_LOOKUP);
        var packageLookup = OPF.findPackageLookup(OPF.Cfg.NAVIGATION_LOOKUP);
        this.navigationLookup = this.parentNavigationLookup == packageLookup ?
                                    OPF.Cfg.NAVIGATION_LOOKUP + '.home' : OPF.Cfg.NAVIGATION_LOOKUP;
        this.parentNavigationLookup = this.parentNavigationLookup == packageLookup ?
                                    OPF.Cfg.NAVIGATION_LOOKUP : this.findNavigationLookup(this.parentNavigationLookup);

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/site/navigation/tree/' + this.parentNavigationLookup + '?lazyLoadResource=true'),
            method: 'GET',

            success: function(response, action) {
                var registryJsonData = Ext.decode(response.responseText);
                var data = registryJsonData.data;
                me.createMenu(data, me.ulContainer, 0);
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });
    },

    findNavigationLookup: function(lookup) {
        var pattern = /^[\w\-]+\.[\w\-]+\.[\w\-]+\.[\w\-]+/g;
        return pattern.exec(lookup)[0];
    },

    buildHrefTag: function (node, isActive) {
        var nodeMenuUrl, hrefTag = '<a ';
        if ('PAGE' == node.elementType) {
            nodeMenuUrl = this.getMenuUrl(node.urlPath);
            hrefTag += 'href="' + OPF.Cfg.fullUrl(nodeMenuUrl, true) + '" class="page-item';
        } else if ('WIZARD' == node.elementType) {
            hrefTag += 'data="{wizardLookup: \'cmv.' + node.pageUrl + '\'}" class="wizard-item';
        } else if ('WORKFLOW' == node.elementType) {
            nodeMenuUrl = this.getMenuUrl(node.urlPath);
            hrefTag += 'href="' + OPF.Cfg.fullUrl(nodeMenuUrl, true) + '" class="page-item';
        }
        hrefTag = hrefTag + ' ' + (isActive ? 'active' : '') + '">';
        hrefTag += Ext.String.capitalize(node.name);
        hrefTag += '</a>';
        return hrefTag;
    },

    createMenu: function(data, ulContainer, deep) {
        var me = this;
        var menuHtml = '';
        Ext.each(data, function(node) {
            if (OPF.isEmpty(node.hidden) || !node.hidden) {
                var isActive = node.lookup == me.navigationLookup;
                menuHtml += '<li>';
                menuHtml += me.buildHrefTag(node, isActive);
                if (OPF.isNotEmpty(node.children) && node.children.length > 0 && me.navigationLookup.indexOf(node.lookup) > -1) {
                    menuHtml += '<ul>';
                    Ext.each(node.children, function(subnode) {
                        if (OPF.isEmpty(subnode.hidden) || !subnode.hidden) {
                            var isActive = subnode.lookup == me.navigationLookup;
                            menuHtml += '<li>';
                            menuHtml += me.buildHrefTag(subnode, isActive);
                        }
                    });
                    menuHtml += '</ul>';
                }
                menuHtml += '</li>';
            }
        });
        ulContainer.el.dom.innerHTML = menuHtml;

        var htmlEls = Ext.query('.wizard-item', ulContainer.el.dom);
        Ext.each(htmlEls, function(htmlEl) {
            var el = Ext.get(htmlEl);
            var data = el.getAttribute('data');
            var wizardLookup = Ext.decode(data).wizardLookup;
            el.on('click', me.showWizard, me, wizardLookup);
        });
    },

    getMenuUrl: function(urlPath) {
        return urlPath;
    },

    showWizard: function(event, el, wizardLookup) {
        var wizard = Ext.WindowMgr.get(wizardLookup);
        if (!wizard) {
            wizard = Ext.ComponentMgr.create({
                id: wizardLookup,
                xtype: wizardLookup
            });
            Ext.WindowMgr.register(wizard);
        }
        wizard.show();
        var pos = wizard.getPosition();
        if (pos[1] < 0) {
            wizard.setPosition(pos[0], 0);
        }
        event.stopPropagation();
    }

});