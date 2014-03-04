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