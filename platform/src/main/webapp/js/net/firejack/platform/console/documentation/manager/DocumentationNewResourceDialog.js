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


Ext.define('OPF.console.documentation.manager.DocumentationNewResourceDialog', {
    extend: 'Ext.window.Window',

    title: 'New Resource:',
    width: 400,
    height: 150,
    modal: true,
    padding: 5,
    layout: 'anchor',

    constructor: function(winId, documentationManager, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationNewResourceDialog.superclass.constructor.call(this, Ext.apply({
            id: winId,
            documentationManager: documentationManager
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.fieldListener = function(field) {
            var valid = field.isValid();
            var button = Ext.getCmp('newResourceWinOkButton');
            if (button.disabled == valid) {
                button.setDisabled(!valid);
            }
        };

        this.resourceNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            allowBlank: false,
            fieldLabel: 'Heading',
            anchor: '95%',
            enableKeyEvents: true,
            listeners: {
                change: this.fieldListener,
                keyup: this.fieldListener
            }
        });

        this.resourceTypeCombo = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            fieldLabel: 'Type',
            anchor: '95%',
            allowBlank: false,
            mode: 'local',
            store: [
                ['HTML', 'HTML'],
                ['TEXT', 'Text'],
                ['IMAGE', 'Image']
            ],
            value: 'HTML',
            editable: false,
            triggerAction: 'all'
        });

        this.items = [
            this.resourceTypeCombo,
            this.resourceNameField
        ];

        this.fbar = [
            {
                xtype: 'button',
                id: 'newResourceWinOkButton',
                text: 'OK',
                formBind: true,
                disabled: true,
                handler: function()
                {
                    instance.createNewResource();
                }
            },
            {
                xtype: 'button',
                text: 'Cancel',
                handler: function()
                {
                    instance.close();
                }
            }
        ];

        this.callParent(arguments);
    },

    createNewResource: function() {
        var instance = this;

        var jsonData = {
            country: OPF.DCfg.COUNTRY,
            registryNodeId: OPF.DCfg.REGISTRY_NODE_ID,
            lookupSuffix: 'description',
            resourceType: this.resourceTypeCombo.getValue(),
            resourceName: this.resourceNameField.getValue()
        };

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('content/documentation'),
            method: 'POST',
            jsonData: {"data": jsonData},

            success:function(response, action) {
                var vo = Ext.decode(response.responseText);
                var data = vo.data[0];
                if (isNotEmpty(data.resourceVersionId)) {
                    var params = new Object();
                    params.id = data.resourceVersionId;
                    if (instance.resourceTypeCombo.getValue() == 'HTML' || instance.resourceTypeCombo.getValue() == 'TEXT') {
                        params.src = '{resourceVersionId: ' + data.resourceVersionId + ', resourceId: ' + data.resourceId + '}';
                        params.noResourceText = 'No description provided';
                        params.editorCls = instance.resourceTypeCombo.getValue() == 'HTML' ? 'editor-html' : 'editor-text';
                    } else {
                        params.src = '{resourceVersionId: ' + data.resourceVersionId + ', ' +
                                      'resourceId: ' + data.resourceId + ', ' +
                                      'imageUrl: \'' + OPF.Cfg.restUrl('content/resource/image/by-filename/') + data.resourceId + '/' + data.resourceVersionId + '_1_' + data.culture + '\'}';
                        params.noResourceText = 'No image provided';
                        params.editorCls = 'editor-image';
                    }
                    params.elemTag = instance.resourceTypeCombo.getValue() == 'TEXT' ? 'pre' : 'div';
                    params.editableSrcData = '{collectionId: ' + data.collectionId + ', resourceId: ' + data.resourceId + '}';
                    params.resourceId = data.resourceId;
                    params.contentSrcData = '{collectionId: ' + data.collectionId + ', resourceId: ' + data.resourceId + ', isFirst: false, isLast: true}';
                    params.collectionId = data.collectionId;
                    params.title = instance.resourceNameField.getValue();
                    instance.addNewSection(params);
                    instance.close();
                    
                    OPF.Msg.setAlert(true, 'New resource successfully created.');
                } else {
                    OPF.Msg.setAlert(false, 'Error creating new resource!');
                }
            }
        });

    },

    addNewSection: function(params) {
        if (this.newSectionTpl == null) {
            this.newSectionTpl = new Ext.Template(
                '<div id="content_resource_{resourceId}" class="editor-container content collection-{collectionId}" src="{contentSrcData}">',
                    '<h2 src="{editableSrcData}">{title}</h2>',
                    '<div class="control info">',
                        '<{elemTag} class="description editable {editorCls}" src="{src}">',
                            '{noResourceText}',
                        '</{elemTag}>',
                        '<div class="action-component buttons-updown"></div>',
                    '</div>',
                '</div>'                    
            );
            this.newSectionTpl.compile();
        }

        var newResourceElement = this.newSectionTpl.append('collection_main', params, false);

        var contentComponent = Ext.get(newResourceElement);
        var documentationElement = new OPF.console.documentation.manager.DocumentationResourceElement(this.documentationManager, contentComponent);
        documentationElement.initComponent();
        documentationElement.refreshSortButtons();

        var siblingDocumentationElements = this.documentationManager.findSiblingDocumentationElements(documentationElement);
        if (isNotEmpty(siblingDocumentationElements.prevDocumentationElement)) {
            siblingDocumentationElements.prevDocumentationElement.contentSrcData.isLast = false;
            siblingDocumentationElements.prevDocumentationElement.refreshSortButtons();
        }
    }
});
