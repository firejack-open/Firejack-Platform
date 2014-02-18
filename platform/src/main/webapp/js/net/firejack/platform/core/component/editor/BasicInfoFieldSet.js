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

Ext.define('OPF.core.component.editor.BasicInfoFieldSet', {
    extend: 'Ext.container.Container',

    editPanel: null,

    layout: 'anchor',
    padding: 10,
    cls: 'basic-info-container',

    constructor: function(editPanel, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.BasicInfoFieldSet.superclass.constructor.call(this, Ext.apply({
            editPanel: editPanel
        }, cfg));
    },

    /**
     * This initComponent method sets up the layout for the field set and allows the
     * entire form to be leveraged across multiple parent forms in the application. 
     */
    initComponent: function() {
        var instance = this;

        this.idField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'id'
        });

        this.parentIdField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'parentId'
        });

        this.typeField = Ext.ComponentMgr.create({
                xtype: 'hidden',
                name: 'type'
        });

        this.pathField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            name: 'path',
            fieldLabel: 'Path',
            anchor: '100%'
        });

        this.nameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'name',
            labelWidth: 80,
            fieldLabel: 'Name',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    instance.updateLookup();
                }
            }
        });

        this.lookupField = Ext.ComponentMgr.create({
            xtype: 'opf-displayfield',
            name: 'lookup',
            fieldLabel: 'Lookup',
            anchor: '100%'
        });

        this.items = [
            this.idField,
            this.parentIdField,
            this.typeField,    
            this.pathField,
            this.nameField,
            this.lookupField
        ];

        this.callParent(arguments);
    },

    updateLookup: function(selectedNode) {
        var lookup = calculateLookup(this.pathField.getValue(), this.nameField.getValue());
        this.lookupField.setValue(lookup);

        if (isNotEmpty(this.editPanel.resourceFields)) {
            this.editPanel.resourceFields.updateResourceAccessFields();
        }
        return lookup;
    }
    
});
