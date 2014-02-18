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
