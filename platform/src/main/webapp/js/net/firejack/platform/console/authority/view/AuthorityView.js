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