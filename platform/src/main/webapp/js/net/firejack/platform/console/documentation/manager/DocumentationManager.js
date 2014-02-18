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


Ext.define('OPF.console.documentation.manager.DocumentationManager', {
    extend: 'Ext.Component',

    textEditor: null,
    htmlEditor: null,

    documentationElements: [],

    initEditors: function() {
        if (isEmpty(this.textEditor)) {
            this.textEditor = new OPF.console.documentation.manager.DocumentationEditor('textarea');
        }
        if (isEmpty(this.htmlEditor)) {
            this.htmlEditor = new OPF.console.documentation.manager.DocumentationEditor('tinymceeditor');
//            this.htmlEditor = new OPF.console.documentation.manager.DocumentationEditor('simplehtmleditor');
        }
        if (isEmpty(this.imageEditor)) {
            this.imageEditor = new OPF.console.documentation.manager.DocumentationEditor('imageEditor');
        }
    },

    openEditor: function(documentationElement) {
        var editor;
        var jsonIdData = documentationElement.editableSrcData;
        var editableComponent = documentationElement.editableComponent;
        if (editableComponent.hasCls('editor-text')) {
            editor = this.textEditor;
        } else if (editableComponent.hasCls('editor-html')) {
            editor = this.htmlEditor;
        } else if (editableComponent.hasCls('editor-image')) {
            editor = this.imageEditor;
        }
        this.htmlEditor.cancelEdit();
        this.textEditor.cancelEdit();

        if (isNotEmpty(editor)) {
            editor.setDocumentationElement(documentationElement);
            editor.startEdit(editableComponent.dom);
        } else {
            Ext.Msg.alert('Warning', 'This block is not editable.');
        }
    },

    initAddButton: function() {
        var instance = this;
        if (OPF.DCfg.HAS_ADD_PERMISSION) {
            var addNewSectionButton = Ext.ComponentMgr.create({
                xtype: 'hrefclick',
                cls: 'add-new-section-button',
                html: 'Add Section',
                renderTo: 'addNewSectionButtonContainer',
                onClick: function() {
                    var winId = 'awrhtlakshglk';
                    var window = Ext.getCmp(winId);
                    if (window == null) {
                        window = new OPF.console.documentation.manager.DocumentationNewResourceDialog(winId, instance);
                    }
                    window.show();
                }
            });
            addNewSectionButton.show();
        }
    },

    initDocumentationElements: function() {
        var instance = this;

        var contentComponents = Ext.select('.content');
        Ext.each(contentComponents.elements, function(contentComponent, index) {
            contentComponent = Ext.get(contentComponent);

            var documentationElement = null;
            if (contentComponent.hasCls('editor-container')) {
                documentationElement = new OPF.console.documentation.manager.DocumentationResourceElement(instance, contentComponent);
            } else if (contentComponent.hasCls('collection') && contentComponent.hasCls('sortable')) {
                documentationElement = new OPF.console.documentation.manager.DocumentationCollectionElement(instance, contentComponent);
            }

            if (isNotEmpty(documentationElement)) {
                documentationElement.initComponent();
            }
        });

        Ext.each(this.documentationElements, function(documentationElement, index) {
            documentationElement.refreshSortButtons();
        });

        var simpleContentComponents = Ext.select('.simple-content');
        Ext.each(simpleContentComponents.elements, function(contentComponent, index) {
            contentComponent = Ext.get(contentComponent);

            var documentationElement = new OPF.console.documentation.manager.DocumentationResourceElement(instance, contentComponent, true);
            documentationElement.initComponent();
        });
    },

    findDocumentationElementByResourceId: function(resourceId) {
        var documentElement = null;
        for (var i = 0; i < this.documentationElements.length; i++) {
            var element = this.documentationElements[i];
            if (isNotEmpty(element.getResourceId) && element.getResourceId() == resourceId) {
                documentElement = element;
            }
        }
        return documentElement;
    },

    findDocumentationElementByCollectionId: function(collectionId) {
        var documentElement = null;
        for (var i = 0; i < this.documentationElements.length; i++) {
            var element = this.documentationElements[i];
            if (element.getCollectionId() == collectionId) {
                documentElement = element;
            }
        }
        return documentElement;
    },

    findSiblingDocumentationElements: function(documentationElement) {
        var siblingDocumentationElements = {
            prevDocumentationElement: null,
            nextDocumentationElement: null
        };

        var collectionElement = documentationElement.contentComponent.dom.parentNode;
        var contentComponents = Ext.select('.content.collection-' + documentationElement.getCollectionId(), true, collectionElement);
        var beforeComponent = null;
        var afterComponent = null;
        for (var i = 0; i < contentComponents.elements.length; i++) {
            var element = contentComponents.elements[i];
            if (documentationElement.contentComponent.id == element.id) {
                if (i > 0) {
                    beforeComponent = contentComponents.elements[i - 1];
                }
                if (i < contentComponents.elements.length) {
                    afterComponent = contentComponents.elements[i + 1];
                }
            }
        }

        var collectionDocumentationElements = [];
        for (var i = 0; i < this.documentationElements.length; i++) {
            var element = this.documentationElements[i];
            if (isNotEmpty(beforeComponent) && beforeComponent.id == element.contentComponent.id) {
                siblingDocumentationElements.prevDocumentationElement = element;
            } else if (isNotEmpty(afterComponent) && afterComponent.id == element.contentComponent.id) {
                siblingDocumentationElements.nextDocumentationElement = element;
            }
        }
        return siblingDocumentationElements;
    },

    removeDocumentationElement: function(removeDocumentationElement) {
        var documentationElements = [];
        for (var i = 0; i < this.documentationElements.length; i++) {
            var element = this.documentationElements[i];
            if (removeDocumentationElement.contentComponent.id != element.contentComponent.id) {
                documentationElements.push(element);
            }
        }
        this.documentationElements = documentationElements;
    }

});