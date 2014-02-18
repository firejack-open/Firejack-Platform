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

Ext.define('OPF.console.documentation.manager.DocumentationResourceElement', {
    extend: 'OPF.console.documentation.manager.DocumentationElement',

    editableComponent: null,

    editableSrcData: null,

    constructor: function(documentationManager, contentComponent, isFieldElement, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationResourceElement.superclass.constructor.call(this, documentationManager, cfg);

        this.isFieldElement = isFieldElement || false;

        this.contentComponent = contentComponent;
        this.controlComponent = Ext.select('.control', true, this.contentComponent.dom).elements[0];
        this.actionComponent = Ext.select('.action-component', true, this.contentComponent.dom).elements[0];
        this.editableComponent = Ext.select('.editable', true, this.controlComponent.dom).elements[0];

        this.actionComponent.setVisibilityMode(Ext.Element.DISPLAY);
        this.actionComponent.setVisible(OPF.DCfg.HAS_EDIT_PERMISSION || OPF.DCfg.HAS_DELETE_PERMISSION);

        var contentSrcAttribute = this.getSrc(this.contentComponent);
        if (isNotEmpty(contentSrcAttribute)) {
            this.contentSrcData = eval('(' + contentSrcAttribute.nodeValue + ')');
        }

        var editableSrcAttribute = this.getSrc(this.editableComponent);
        if (isNotEmpty(editableSrcAttribute)) {
            this.editableSrcData = eval('(' + editableSrcAttribute.nodeValue + ')');
        }
    },

    initComponent: function() {
        if (OPF.DCfg.HAS_DELETE_PERMISSION && !this.isFieldElement) {
            this.addRemoveButton();
        }
        if (OPF.DCfg.HAS_EDIT_PERMISSION) {
            this.addEditButton();
        }
        if (!this.isFieldElement) {
            this.callParent(arguments);
        }
    },

    addEditButton: function() {
        var instance = this;

        var addEditSectionButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'Edit',
            cls: 'editbutton',
            renderTo: instance.actionComponent,
            onClick: function() {
                instance.documentationManager.openEditor(instance);
            }
        });
        addEditSectionButton.show();
    },

    addRemoveButton: function() {
        var instance = this;

        var addDeleteSectionButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            html: 'Delete',
            cls: 'deletebutton',
            renderTo: instance.actionComponent,
            onClick: function() {
                if (CURRENTLY_EDITING) {
                    OPF.Msg.setAlert(false, 'Resource being edited. Cannot delete.');
                    return;
                }

                Ext.MessageBox.confirm("Delete resource", "Are you sure?",
                    function(btn) {
                        if (btn[0] == 'y') {
                            instance.removeDescription();
                        }
                    }
                );
            }
        });

        addDeleteSectionButton.show();
    },

    removeDescription: function() {
        var instance = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/documentation/' + instance.contentSrcData.resourceId),
            method: 'DELETE',

            success: function(response, action) {
                var siblingDocumentationElements = instance.documentationManager.findSiblingDocumentationElements(instance);

                instance.documentationManager.removeDocumentationElement(instance);

                instance.contentComponent.remove();

                if (isNotEmpty(siblingDocumentationElements.prevDocumentationElement)) {
                    siblingDocumentationElements.prevDocumentationElement.refreshSortButtons();
                }
                if (isNotEmpty(siblingDocumentationElements.nextDocumentationElement)) {
                    siblingDocumentationElements.nextDocumentationElement.refreshSortButtons();
                }

                OPF.Msg.setAlert(true, 'Resource successfully deleted.');
            },

            failure:function(response) {
                OPF.Msg.setAlert(true, 'Resource has not deleted.');
            }
        });
    },

    getResourceId: function() {
        return this.contentSrcData.resourceId;
    }

});