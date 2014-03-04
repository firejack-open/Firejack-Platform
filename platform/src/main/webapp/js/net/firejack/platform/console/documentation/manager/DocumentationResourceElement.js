/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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