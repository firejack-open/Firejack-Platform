//@tag opf-editor
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
 * Almost every entity in the registry (nodes and sub-objects alike) support these
 * basic fields during input. This fieldset allows all nodes to share the basic information
 * input fields in a single class.
 */

Ext.define('OPF.core.component.editor.DescriptionInfoFieldSet', {
    extend: 'Ext.container.Container',

    fieldLabel: 'Main Information',

    editPanel: null,
    componentFields: [],
    layout: 'anchor',
    border: false,

    constructor: function(editPanel, componentFields, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.BasicInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel,
            componentFields: isNotEmpty(componentFields) ? componentFields : []
        }, cfg));
    },

    /**
     * This initComponent method sets up the layout for the field set and allows the
     * entire form to be leveraged across multiple parent forms in the application. 
     */
    initComponent: function() {
        var instance = this;

        this.descriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-htmleditor',
            name: 'description',
            anchor: '100%',
            labelAlign: 'top',
            fieldLabel: 'Description',
            subFieldLabel: 'Enter a basic description of this asset',
            allowBlank: true
        });

        this.items = [
            this.descriptionField,
            this.componentFields
        ];

        this.callParent(arguments);
    }
    
});
