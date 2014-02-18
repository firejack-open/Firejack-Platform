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
    'OPF.core.model.RegistryNodeTreeModel'
]);

Ext.define('OPF.prototype.component.MenuNavigation', {
    extend: 'Ext.container.Container',
    alias: 'widget.opf.prototype.component.menu-navigation',

    border: false,
    layout: 'hbox',

    currentNavigationLookup: null,

    initComponent: function() {
        var me = this;

        var packageLookup = OPF.findPackageLookup(this.currentNavigationLookup);

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/site/navigation/tree/' + packageLookup + '.gateway'),
            method: 'GET',

            success: function(response, action) {
                var activeButton = null;
                var registryJsonData = Ext.decode(response.responseText);
                var jsonNodes = registryJsonData.data/*[0]*/;            // TODO need uncomment for new implementation

                Ext.each(jsonNodes, function(jsonNode, index) {
                    me.addMenuButton(jsonNode);
                });
                me.doLayout();
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });

        this.callParent(arguments);
    },

    createMenuButton: function(jsonNode) {
        return Ext.ComponentMgr.create({
            xtype: 'component',
            autoEl: {
                tag: 'a',
                href: OPF.Cfg.fullUrl(jsonNode.urlPath, true),
                html: Ext.String.capitalize(jsonNode.name)
            },
            lookup: jsonNode.lookup
        });
    },

    addMenuButton: function(jsonNode) {
        var menuButton = this.createMenuButton(jsonNode);
        this.add(menuButton);
        if (menuButton.lookup == this.currentNavigationLookup) {
            menuButton.addCls('active');
        }
    }

});