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

Ext.define('OPF.console.authority.view.Authority', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.authority-page',

    activeButtonLookup: 'net.firejack.platform.console.authorization',

    initComponent: function() {
        var instance = this;

        this.roleGridView = new OPF.console.authority.view.RoleGridView(instance);

        this.permissionGridView = new OPF.console.authority.view.PermissionGridView(instance);

        this.resourceLocationGridView = new OPF.console.authority.view.ResourceLocationGridView(instance);

        this.additionalTabs = [
            this.roleGridView,
            this.permissionGridView,
            this.resourceLocationGridView
        ];

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add role',
                cls: 'roleIcon',
                iconCls: 'btn-add-role',
                registryNodeType: OPF.core.utils.RegistryNodeType.ROLE,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add entitlement',
                cls: 'entitlementIcon',
                iconCls: 'btn-add-entitlement',
                registryNodeType: OPF.core.utils.RegistryNodeType.PERMISSION,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add policy',
                cls: 'policyIcon',
                iconCls: 'btn-add-policy',
                registryNodeType: OPF.core.utils.RegistryNodeType.POLICY,
                managerLayout: instance
            },
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'Add resource location',
                cls: 'resLocIcon',
                iconCls: 'btn-add-resource-location',
                registryNodeType: OPF.core.utils.RegistryNodeType.RESOURCE_LOCATION,
                managerLayout: instance
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.authority.info-html'
        });

        this.callParent(arguments);
    }

});