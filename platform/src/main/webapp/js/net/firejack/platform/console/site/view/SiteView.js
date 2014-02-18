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

Ext.define('OPF.console.site.view.Site', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.site-page',

    activeButtonLookup: 'net.firejack.platform.console.site',

    initComponent: function() {
        var instance = this;

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add nav element',
                cls: 'navElementIcon',
                iconCls: 'btn-add-element',
                registryNodeType: OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT,
                managerLayout: instance
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.site.info-html'
        });

        this.callParent(arguments);
    }

});