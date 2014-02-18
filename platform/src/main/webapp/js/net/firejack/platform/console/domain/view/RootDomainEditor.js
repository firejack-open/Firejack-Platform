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
Ext.define('OPF.console.domain.view.RootDomainEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'ROOT DOMAIN: [New]',

//    pageSuffixUrl: 'console/domain',
//    restSuffixUrl: 'registry/root_domain',
//    modelClassName: 'OPF.console.domain.model.RootDomain',
//    constraintName: 'OPF.registry.RootDomain',

    editableWithChild: true,

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.root-domain',

    /**
     *
     */
    initComponent: function() {

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this, {
            updateLookup: function() {
                var path = ifBlank(this.pathField.getValue(), '');
                var name = ifBlank(this.nameField.getValue(), '');
                var lookupParts = reverse(name.split('.'));
                var lookup = '';
                Ext.each(lookupParts, function(lookupPart, index) {
                    lookupPart = lookupPart.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
                    lookup += (index > 0 ? '.' : '') + lookupPart;
                });
                this.lookupField.setValue(lookup.toLowerCase());
                return lookup;
            }
        });

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields
        ];

        this.callParent(arguments);
    },

    refreshFields: function(selectedNode) {
        this.nodeBasicFields.pathField.setValue('');
        this.nodeBasicFields.updateLookup();
    }

});