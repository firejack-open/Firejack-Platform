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

Ext.define('OPF.prototype.component.MenuTreeNavigation', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.opf.prototype.component.menu-tree-navigation',

    flex: 1,
    border: false,
    width: 200,
    cls: 'menu-tree-navigation',

    initComponent: function() {
        var me = this;

        var packageLookup = OPF.findPackageLookup(OPF.Cfg.NAVIGATION_LOOKUP);
        var gatewayLookup = packageLookup + '.gateway';

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('/site/navigation/tree/' + gatewayLookup),
            method: 'GET',

            success: function(response, action) {
                var registryJsonData = Ext.decode(response.responseText);
                var jsonData = registryJsonData.data/*[0]*/;            // TODO need uncomment for new implementation
                me.addNavigationNode(me.store.getRootNode(), jsonData);

                var selectPath = '.root.' + OPF.Cfg.NAVIGATION_LOOKUP.substring(gatewayLookup.length + 1);
                me.tree.selectPath(selectPath, 'normalizedName', '.');
            },

            failure: function(response) {
                OPF.Msg.setAlert(false, response.message);
            }
        });

        this.store = Ext.create('Ext.data.TreeStore', {
            model: 'OPF.core.model.RegistryNodeTreeModel',
            root: {
                normalizedName: 'root',
                expanded: true,
                children: [
                ]
            },
            sorters: [
                {
                    property: 'sortPosition',
                    direction: 'ASC'
                }
            ],
            listeners: {
                append: function(node, newChildNode, index, eOpts) {
                    if(!newChildNode.isRoot()) {
                        var urlPath = newChildNode.get('urlPath');
                        var href = OPF.Cfg.fullUrl(urlPath, true);
                        newChildNode.set('href', href);

                        var lookup = newChildNode.get('lookup');
                        var normalizedName = lookup.substring(lookup.lastIndexOf('.') + 1);
                        newChildNode.set('normalizedName', normalizedName);
                        newChildNode.set('cls', 'node-menu');
                    }
                }
            }
        });

        this.tree = Ext.create('Ext.tree.Panel', {
            store: this.store,
            border: false,
            useArrows: true,
            rootVisible: false,
            autoRender: true,
            width: me.width,
            listeners: {
                itemclick: function(tree, record) {
                    document.location = record.get('href');
                }
            }
        });

        this.items = [
            this.tree
        ];

        this.callParent(arguments);
    },

    addNavigationNode: function(parentNode, jsonNodes) {
        var me = this;
        Ext.each(jsonNodes, function(jsonNode) {
            var node = Ext.create('OPF.core.model.RegistryNodeTreeModel', jsonNode);
            parentNode.appendChild(node);
        });
    }

});