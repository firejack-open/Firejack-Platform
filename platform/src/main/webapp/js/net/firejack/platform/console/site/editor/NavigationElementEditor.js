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
Ext.define('OPF.console.site.editor.NavigationElementEditor', {
    extend: 'OPF.core.component.editor.BaseSupportedPermissionEditor',

    title: 'ELEMENT: [New]',

    infoResourceLookup: 'net.firejack.platform.site.navigation-element',

    /**
     *
     */
    initComponent: function() {
        var me = this;

        this.elementTypeField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'Type',
            subFieldLabel: '',
            name: 'elementType',
            width: 150,
            store: '',
            emptyText: 'Page (default)',
            editable: false,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    me.updatePageUrl();
                },
                select: function(cmp, e) {
                    me.updatePageUrl();
                }
            }
        });

        this.pageUrlField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Page Url',
            subFieldLabel: '',
            anchor: '100%',
            name: 'pageUrl',
            readOnly: true,
            flex: 1
        });

        var additionalLeftPanelControls = [
            {
                xtype: 'fieldcontainer',
                layout: 'hbox',
                items: [
                    this.elementTypeField,
                    {
                        xtype: 'splitter'
                    },
                    this.pageUrlField
                ]
            }
        ];

        this.resourceFields = new OPF.core.component.editor.ResourceFieldSet(this, {
            needMethodField: false,
            additionalLeftPanelControls: additionalLeftPanelControls,
            updateResourceAccessFields: function() {
                me.elementTypeField.setReadOnly(this.editPanel.saveAs == 'update');
                me.pageUrlField.setReadOnly(this.editPanel.saveAs == 'update');
                this.updateUrlPath();
                this.updateResourceUrl();
            }
        });

        this.additionalBlocks = [
            this.resourceFields
        ];

        this.callParent(arguments);
    },

    updatePageUrl: function() {
        var elementType = this.elementTypeField.getValue();
        if ('PAGE' == elementType) {
            this.pageUrlField.setFieldLabel('Page Url');
        } else if ('WIZARD' == elementType) {
            this.pageUrlField.setFieldLabel('Wizard Lookup');
        } else {
            OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, 'Element Type: \'' + elementType + '\' doesn\'t support.');
        }
    }

});


