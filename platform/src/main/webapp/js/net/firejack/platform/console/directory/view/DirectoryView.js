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
    'OPF.console.PageLayout',
    'OPF.console.NavigationPageLayout'
]);

Ext.define('OPF.console.directory.view.Directory', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.directory-page',

    activeButtonLookup: 'net.firejack.platform.console.directory',

    defaultEntity: {
        lookup: 'net.firejack.platform.accounts'
    },

    initComponent: function() {

        this.directoryGridView = new OPF.console.directory.view.DirectoryGridView(this);

        this.userGridView = new OPF.console.directory.view.UserGridView(this);

        this.systemUserGridView = new OPF.console.directory.view.SystemUserGridView(this);

        this.userProfileFieldGridView = new OPF.console.directory.view.UserProfileFieldGridView(this);

        this.additionalTabs = [
            this.directoryGridView,
            this.userGridView,
            this.systemUserGridView,
            this.userProfileFieldGridView
        ];

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add directory',
                iconCls: 'btn-add-directory',
                cls: 'directoryIcon',
                registryNodeType: OPF.core.utils.RegistryNodeType.DIRECTORY,
                managerLayout: this
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add group',
                cls: 'groupIcon',
                iconCls: 'btn-add-group',
                registryNodeType: OPF.core.utils.RegistryNodeType.GROUP,
                managerLayout: this
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add user',
                cls: 'userIcon',
                iconCls: 'btn-add-user',
                registryNodeType: OPF.core.utils.RegistryNodeType.USER,
                managerLayout: this
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add system user',
                cls: 'userIcon',
                iconCls: 'btn-add-user',
                registryNodeType: OPF.core.utils.RegistryNodeType.SYSTEM_USER,
                managerLayout: this
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.directory.directory.info-html'
        });

        this.callParent(arguments);
    },

    refreshToolbarButtons: function(deselect) {
        this.callParent(arguments);
        this.userProfileFieldGridView.onNavigationNodeClick();
    }

});