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