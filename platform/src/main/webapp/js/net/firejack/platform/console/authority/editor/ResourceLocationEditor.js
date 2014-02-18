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

/**
 *
 */
Ext.define('OPF.console.authority.editor.ResourceLocationEditor', {
    extend: 'OPF.core.component.editor.BaseSupportedPermissionEditor',

    title: 'RESOURCE LOCATION: [New]',

    infoResourceLookup: 'net.firejack.platform.authority.resource-location',

    /**
     *
     */
    initComponent: function() {
        var instance = this;

        this.resourceLocationFieldSet = new OPF.console.authority.editor.ResourceLocationFieldSet(instance, {
            readOnlyInheritedFields: true,
            updateResourceAccessFields: function() {
                this.updateResourceUrl();
            }
        });

        this.additionalBlocks = [
            this.resourceLocationFieldSet
        ];

        this.callParent(arguments);
    },

    hideEditPanel: function() {
        OPF.console.authority.editor.ResourceLocationEditor.superclass.hideEditPanel.call(this);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.resourceLocationGridView);
    },

    onSuccessSaved: function(method, vo) {
    }

});