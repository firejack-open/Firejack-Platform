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


Ext.define('OPF.console.documentation.manager.DocumentationElement', {

    documentationManager: null,

    contentComponent: null,
    controlComponent: null,
    actionComponent: null,

    contentSrcData: null,

    constructor: function(documentationManager, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationElement.superclass.constructor.call(this, cfg);

        this.documentationManager = documentationManager;
        this.documentationManager.documentationElements.push(this);
    },

    initComponent: function() {
        if (OPF.DCfg.HAS_EDIT_PERMISSION) {
            this.addSortOrderButtons();
        }
    },

    addSortOrderButtons: function() {
        var instance = this;

        this.addArrowUpButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            cls: 'arrowupbutton',
            hidden: true,
            renderTo: instance.actionComponent,
            onClick: function() {
                instance.changePosition(true);
            }
        });

        this.addArrowDownButton = Ext.ComponentMgr.create({
            xtype: 'hrefclick',
            cls: 'arrowdownbutton',
            hidden: true,
            renderTo: instance.actionComponent,
            onClick: function() {
                instance.changePosition(false);
            }
        });

    },

    refreshSortButtons: function() {
        var siblingDocumentationElements = this.documentationManager.findSiblingDocumentationElements(this);
        this.contentSrcData.isFirst = isEmpty(siblingDocumentationElements.prevDocumentationElement);
        this.contentSrcData.isLast = isEmpty(siblingDocumentationElements.nextDocumentationElement);
        this.addArrowUpButton.setVisible(!this.contentSrcData.isFirst);
        this.addArrowDownButton.setVisible(!this.contentSrcData.isLast);
    },

    changePosition: function(moveToUp) {
        var instance = this;

        var siblingDocumentationElements = this.documentationManager.findSiblingDocumentationElements(this);

        var collectionId = this.contentSrcData.collectionId;
        var oneRefId = null;
        var twoRefId = null;
        if (moveToUp && isNotEmpty(siblingDocumentationElements.prevDocumentationElement)) {
            oneRefId = this.contentSrcData.resourceId;
            twoRefId = siblingDocumentationElements.prevDocumentationElement.contentSrcData.resourceId;
        } else if (!moveToUp && isNotEmpty(siblingDocumentationElements.nextDocumentationElement)) {
            oneRefId = siblingDocumentationElements.nextDocumentationElement.contentSrcData.resourceId;
            twoRefId = this.contentSrcData.resourceId;
        }

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/collection/swap/' + collectionId + '/' + oneRefId + '/' + twoRefId),
            method: 'GET',

            success:function(response, action) {
                var arrowsData = {
                    isFirst: null,
                    isLast: null
                };
                if (moveToUp && isNotEmpty(siblingDocumentationElements.prevDocumentationElement)) {
                    arrowsData.isFirst = siblingDocumentationElements.prevDocumentationElement.contentSrcData.isFirst;
                    arrowsData.isLast = siblingDocumentationElements.prevDocumentationElement.contentSrcData.isLast;
                    siblingDocumentationElements.prevDocumentationElement.contentSrcData.isFirst = instance.contentSrcData.isFirst;
                    siblingDocumentationElements.prevDocumentationElement.contentSrcData.isLast = instance.contentSrcData.isLast;
                    siblingDocumentationElements.prevDocumentationElement.contentComponent.insertSibling(instance.contentComponent, 'before');
                    siblingDocumentationElements.prevDocumentationElement.refreshSortButtons();
                } else if (!moveToUp && isNotEmpty(siblingDocumentationElements.nextDocumentationElement)) {
                    arrowsData.isFirst = siblingDocumentationElements.nextDocumentationElement.contentSrcData.isFirst;
                    arrowsData.isLast = siblingDocumentationElements.nextDocumentationElement.contentSrcData.isLast;
                    siblingDocumentationElements.nextDocumentationElement.contentSrcData.isFirst = instance.contentSrcData.isFirst;
                    siblingDocumentationElements.nextDocumentationElement.contentSrcData.isLast = instance.contentSrcData.isLast;
                    siblingDocumentationElements.nextDocumentationElement.contentComponent.insertSibling(instance.contentComponent, 'after');
                    siblingDocumentationElements.nextDocumentationElement.refreshSortButtons();
                }
                instance.contentSrcData.isFirst = arrowsData.isFirst;
                instance.contentSrcData.isLast = arrowsData.isLast;
                instance.refreshSortButtons();
            },

            failure:function(response) {
                OPF.Msg.setAlert(true, 'Resource has not swapped.');
            }
        });
    },

    getSrc: function(component) {
        var srcAttribute = null;
        if (OPF.isNotEmpty(component)) {
            Ext.each(component.dom.attributes, function(attribute, index) {
                if (attribute.name == 'src' && isNotBlank(attribute.nodeValue)) {
                    srcAttribute = attribute;
                }
            });
        }
        return srcAttribute;
    },

    getCollectionId: function() {
        return this.contentSrcData.collectionId;
    }

});