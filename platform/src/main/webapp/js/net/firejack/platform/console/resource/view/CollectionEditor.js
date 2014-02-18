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
Ext.define('OPF.console.resource.view.CollectionEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'COLLECTION: [New]',

    infoResourceLookup: 'net.firejack.platform.content.collection',

    /**
     *
     */
    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.collectionMembershipFieldSet = Ext.create('OPF.console.resource.view.CollectionMembershipFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.collectionMembershipFieldSet
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData.memberships)) {
            this.collectionMembershipFieldSet.membershipStore.loadData(jsonData.memberships);
        }
        this.collectionMembershipFieldSet.resourceStore.load();
    },

    onBeforeSave: function(formData) {
        formData.memberships = getJsonOfStore(this.collectionMembershipFieldSet.membershipStore);
    },

    onRefreshFields: function(selectedNode) {
        this.collectionMembershipFieldSet.resourceStore.load();
    }

});