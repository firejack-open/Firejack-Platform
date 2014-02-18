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

/**
 *
 */
Ext.define('OPF.console.domain.view.ReferenceObjectFieldSet', {
    extend: 'OPF.core.component.LabelContainer',
    alias: 'widget.reference-object-fieldset',

    layout: 'anchor',

    fieldLabel: 'Reference Object',
    subFieldLabel: '',

    initComponent: function() {
        var me = this;

        this.idField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'referenceId'
        });

        this.headingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refHeading',
            labelWidth: 180,
            fieldLabel: 'Reference Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.subHeadingField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refSubHeading',
            labelWidth: 180,
            fieldLabel: 'Reference Sub-Heading',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.descriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-reference-htmleditor',
            name: 'refDescription',
            labelWidth: 180,
            fieldLabel: 'Reference Description',
            subFieldLabel: '',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                keyup: function(cmp, e) {
                    me.updateExample();
                }
            }
        });

        this.exampleContainer = Ext.ComponentMgr.create({
            xtype: 'label-container',
            labelWidth: 200,
            fieldLabel: 'Example',
            subFieldLabel: '',
            anchor: '100%',
            tpl:
                '<div class="reference-heading">{heading}</div>' +
                '<div class="reference-sub-heading">{subHeading}</div>' +
                '<div class="reference-description">{description}</div>'
        });

        this.items = [
            this.headingField,
            this.subHeadingField,
            this.descriptionField,
            this.exampleContainer
        ];

        this.callParent(arguments);
    },

    updateExample: function() {
        var exampleData = {
            heading: this.headingField.getHtmlValue(),
            subHeading: this.subHeadingField.getHtmlValue(),
            description: this.descriptionField.getHtmlValue()
        };
        this.exampleContainer.update(exampleData);
    }

});