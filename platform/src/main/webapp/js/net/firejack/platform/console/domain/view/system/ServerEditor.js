/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

Ext.define('OPF.console.domain.view.system.PublicKeyFieldSet', {
    extend: 'OPF.core.component.LabelContainer',

    fieldLabel: 'x509 Certificate',
    subFieldLabel: '',

    layout: 'anchor',

    editPanel: null,

    initComponent: function() {
        this.publicKeyField = Ext.ComponentMgr.create({
            xtype: 'opf-textarea',
            labelAlign: 'top',
            fieldLabel: 'Public Key',
            subFieldLabel: '',
            anchor: '100%',
            name: 'publicKey'
        });

        this.items = [
            this.publicKeyField
        ];

        this.callParent(arguments);
    }
});

Ext.define('OPF.console.domain.view.system.ServerEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.server-edit-panel',

    title: 'SERVER: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.server',

    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            needMethodField: false,
            needParentPathField: false,
            needUrlPathField: false
        });

        this.publicKeyFieldSet = Ext.create('OPF.console.domain.view.system.PublicKeyFieldSet');

        this.associatedPackagesFieldSet = Ext.create('OPF.console.domain.view.system.ServerAssociatedPackagesFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.resourceFields,
            this.publicKeyFieldSet,
            this.associatedPackagesFieldSet
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (isNotEmpty(jsonData) && isNotEmpty(jsonData.associatedPackages)) {
            this.associatedPackagesFieldSet.associatedPackageGrid.store.loadData(jsonData.associatedPackages);
        }

        if (OPF.isNotEmpty(jsonData.main)) {
            this.resourceFields.setAllReadOnly(true);
        }
    },

    saveRequest: function(formData, url, method) {
        delete formData.parentPath;

        this.callParent(arguments);
    }
});