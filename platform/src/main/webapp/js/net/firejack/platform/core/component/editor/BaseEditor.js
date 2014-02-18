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
 *
 */
Ext.define('OPF.core.component.editor.BaseEditor', {
    extend: 'Ext.panel.Panel',

    cls: 'base-editor',
    layout: 'fit',
    closable: true,
    border: false,

    entityId: null,
    entityLookup: null,
    registryNodeType: null,
    selfNode: null,
    saveAs: null,
    managerLayout: null,

    staticBlocks: [],

    additionalMainControls: [],
    additionalBlocks: [],

    formButtonAlign: 'right',

    selectedParentNode: null,

    editableWithChild: true,

    infoResourceLookup: null,

    constructor: function(managerLayout, registryNodeType, cfg) {
        cfg = cfg || {};
        OPF.core.component.editor.BaseEditor.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout,
            registryNodeType: registryNodeType,
            id: registryNodeType.type + 'EditPanel'
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        instance.addEvents(
            'showeditor'
        );

        this.saveButton = Ext.ComponentMgr.create({
            xtype: 'button',
            cls: 'saveButton',
            text: 'Save',
            formBind : true,
            handler: function () {
                instance.save();
            }
        });

        this.cancelButton = Ext.ComponentMgr.create({
            xtype: 'button',
            cls: 'cancelButton',
            text: 'Cancel',
            handler: function () {
                instance.cancel();
            }
        });

        this.rightNavContainer = Ext.ComponentMgr.create({
            xtype: 'container',
            cls: 'right-nav',
            html: this.prepareRightNavigation(this.additionalBlocks)
        });

        var moreInformationUrl = null;
        var navigationElement = OPF.NavigationMenuRegister.findByLookup('net.firejack.platform.top.documentation');
        if (isNotEmpty(navigationElement)) {
            var documentationPageUrl = OPF.Cfg.OPF_CONSOLE_URL + navigationElement.urlPath;
            moreInformationUrl = documentationPageUrl + this.infoResourceLookup.replace(/net\.firejack/g, '').replace(/\./g, '/');
        }

        this.rightPanel = Ext.ComponentMgr.create({
            xtype: 'panel',
            padding: 10,
            border: false,
            layout: {
                type: 'table',
                columns: 1
            },
            items: [
                {
                    xtype: 'resource-control',
                    textResourceLookup: this.infoResourceLookup + '.info-text',
                    textMaxLength: 250,
                    imgResourceLookup: this.infoResourceLookup + '.info-image',
                    imageWidth: 80,
                    imageHeight: 80
                },
                OPF.Ui.ySpacer(15),
                {
                    xtype: 'container',
                    height: 40,
                    cls: 'more-information',
                    html:
                        '<a href="' + moreInformationUrl + '" >' +
                            '<h4><img src="' + OPF.Cfg.fullUrl('images/info.png') + '" border="0"/>More information</h4>' +
                        '</a>'
                },
                OPF.Ui.ySpacer(15),
                this.rightNavContainer
            ]
        });


        var mainControls = [];
        if (this.additionalMainControls.length > 0) {
            var actionButton = {
                xtype: 'button',
                cls: 'main-controls-menu-btn',
                text: 'Actions',
                menu: {
                    xtype: 'menu',
                    baseCls: 'main-controls-menu',
                    items: this.additionalMainControls
                }
            };
            mainControls.push(actionButton);
        }

        mainControls.push('->');
        mainControls.push(this.cancelButton);
        mainControls.push(this.saveButton);


        var fieldsPanelDockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                cls: 'main-controls',
                items: mainControls
            }
        ];

        if (OPF.isNotEmpty(this.additionalBBar)) {
            fieldsPanelDockedItems.push(this.additionalBBar);
        }

        this.fieldsPanel = Ext.create('Ext.container.Container', {
            border: false,
            flex: 1,
            style: 'overflow-x: hidden;',
            items: [
                this.additionalBlocks
            ]
        });

        this.form = Ext.create('Ext.form.Panel', {
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: false,
            buttonAlign: this.formButtonAlign,
            monitorValid: true,
            style : 'border-right: 1px solid #B5B8C8',
            items: [
                {
                    xtype: 'container',
                    border: false,
                    padding: 10,
                    items: [
                        this.staticBlocks
                    ]
                },
                {
                    xtype: 'container',
                    flex: 1,
                    border: false,
                    padding: 10,
                    autoScroll: true,
                    items: [
                        this.fieldsPanel
                    ]
                }
            ],
            dockedItems: fieldsPanelDockedItems
        });

        this.mainPanel = Ext.create('Ext.container.Container', {
            flex: 1,
            border: false,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                this.form,
                {
                    xtype: 'container',
                    width: 220,
                    layout: 'fit',
                    buttonAlign: 'center',
                    border: false,
                    items: [
                        {
                            xtype: 'container',
                            autoScroll: true,
                            items: [
                                this.rightPanel
                            ]
                        }
                    ]
                }
            ]
        });

        this.items = [
            this.mainPanel
        ];

        this.callParent(arguments);
    },

    prepareRightNavigation: function(additionalBlocks) {
        var rightNavContent = '';
        Ext.each(additionalBlocks, function(additionalBlock, index) {
            var isLast = index == (additionalBlocks.length - 1);
            var anchorMenuItemLabel =
                OPF.isBlank(additionalBlock.anchorFieldLabel) ?
                    additionalBlock.fieldLabel : additionalBlock.anchorFieldLabel;
            if (OPF.isNotBlank(anchorMenuItemLabel) && !additionalBlock.hidden) {
                rightNavContent +=
                    '<a href="javascript:document.getElementById(\'' + additionalBlock.getId() + '\').scrollIntoView(true);">' +
                        '<li class="' + (!isLast ? 'top' : '') + '"><img src="' + OPF.Cfg.fullUrl('images/right_nav_arrow.png') + '" border="0"/>' + anchorMenuItemLabel + '</li>' +
                    '</a>';
            }
        });
        return '<ul class="navlist">' + rightNavContent + '</ul>';
    },

    render: function() {
        this.callParent(arguments);

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            renderTo: this.form.getId(),
            form: this.form
        });
    },

    onAddButton: function() {
        this.saveAs = 'new';
        var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
        var path = selectedNode.data.lookup;
        this.nodeBasicFields.pathField.setValue(path);
        var lookup = this.nodeBasicFields.updateLookup();
        var parentId = SqGetIdFromTreeEntityId(selectedNode.data.id);
        parentId = parentId > 0 ? parentId : null;
        this.nodeBasicFields.parentIdField.setValue(parentId);
        this.selectedParentNode = selectedNode;

        var registryJsonData = {
            parentId: parentId,
            path: path,
            lookup: lookup
        };
        this.showEditPanel(registryJsonData);
    },

    refreshFields: function(selectedNode) {
        if (OPF.isNotEmpty(this.nodeBasicFields)) {
            this.nodeBasicFields.updateLookup(selectedNode);
        }
        if (OPF.isNotEmpty(this.onRefreshFields)) {
            this.onRefreshFields(selectedNode);
        }
    },

    onRefreshFields: function(selectedNode) {
    },

    onBeforeSetValue: function(jsonData) {
    },

    onAfterSetValue: function(jsonData) {
    },

    getPanelTitle: function(registryName) {
        return this.registryNodeType.getTitlePrefix() + ': ' + OPF.cutting(registryName, 10);
    },

    showEditPanel: function(registryJsonData) {
        if (this.saveAs == 'update') {
            this.title = this.registryNodeType.getTitlePrefix() + ': ' + OPF.cutting(registryJsonData.name, 10);
        }

        if (this.managerLayout.tabPanel.getActiveTab().getId() != this.id) {
            this.managerLayout.tabPanel.add(this);
            this.managerLayout.tabPanel.doLayout();
            this.managerLayout.tabPanel.setActiveTab(this.id);
        }

        OPF.core.validation.FormInitialisation.hideValidationMessages(this.form);

        var model = this.registryNodeType.createModel(registryJsonData);

        if (OPF.isNotEmpty(this.onBeforeSetValue)) {
            this.onBeforeSetValue(registryJsonData);
        }

        this.form.getForm().loadRecord(model);

        if (OPF.isNotEmpty(registryJsonData)) {
            this.nodeBasicFields.nameField.setReadOnly(!(this.editableWithChild || registryJsonData.childCount == 0));
        }

        if (OPF.isNotEmpty(this.onAfterSetValue)) {
            this.onAfterSetValue(registryJsonData);
        }

        if (this.saveAs == 'new') {
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            this.refreshFields(selectedNode);
        }

        this.fireEvent('showeditor', this);

        this.formInitialisation(registryJsonData);
    },

    hideEditPanel: function() {
//        this.managerLayout.tabPanel.remove(this);
//        this.managerLayout.tabPanel.doLayout();
        this.destroy();
    },

    formInitialisation: function(registryJsonData) {
        if (OPF.isNotEmpty(registryJsonData)) {
            var registryNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeByType(registryJsonData.type);
            this.entityId = OPF.isNotBlank(registryJsonData.id) ? this.registryNodeType.generateId(registryJsonData.id) : null;
            this.entityLookup = registryJsonData.lookup;
        } else {
            this.entityId = this.registryNodeType.generateId(0);
        }

        var parameters = '';
        var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
        if (OPF.isNotEmpty(selectedNode)) {
            var parentNodeId = SqGetIdFromTreeEntityId(selectedNode.data.id);
            parameters = "?parentId=" + parentNodeId;
            if (this.saveAs == 'update') {
                parameters += "|id=" + registryJsonData.id;
            }
        }
        var constraints = new OPF.core.validation.FormInitialisation(this.registryNodeType.getConstraintName() + parameters);
        constraints.initConstraints(this.form, null);
    },

    onBeforeSave: function(formData) {
    },

    save: function() {
        var formData = this.form.getForm().getValues();

        this.form.getEl().mask();
        var method = null;
        var url = null;
        if (this.saveAs == 'new') {
            method = 'POST';
            url = this.registryNodeType.generatePostUrl();
        } else if (this.saveAs == 'update') {
            method = 'PUT';
            url = this.registryNodeType.generatePutUrl(formData.id);
        } else {
            this.form.getEl().unmask();
            this.form.getForm().reset();
            this.hideEditPanel();

            OPF.Msg.setAlert('Failure', 'Unknown operation');
            return false;
        }
        this.nodeBasicFields.typeField.setValue(this.registryNodeType.toString().toUpperCase());

        if (OPF.isNotEmpty(this.onBeforeSave)) {
            this.onBeforeSave(formData);
        }

        if (OPF.isNotEmpty(this.resourceFields)) {
            var serverNameValue = this.resourceFields.serverNameField.getValue();
            if (formData.serverName != serverNameValue) {
                formData.serverName = '';
            }
            if (OPF.isNotEmpty(this.resourceFields.parentPathField)) {
                var parentPathValue = this.resourceFields.parentPathField.getValue();
                if (formData.parentPath != parentPathValue) {
                    formData.parentPath = '';
                }
            }
            if (OPF.isNotEmpty(this.resourceFields.urlPathField)) {
                var urlPathValue = this.resourceFields.urlPathField.getValue();
                if (formData.urlPath != urlPathValue) {
                    formData.urlPath = '';
                }
            }
        }

        this.saveRequest(formData, url, method);
    },

    saveRequest: function(formData, url, method) {
        var instance = this;

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": formData},

            success:function(response, action) {
                instance.successSaved(response, action, method);
            },

            failure:function(response) {
                instance.failureSaved(response);
            }
        });
    },

    successSaved: function(response, action, method) {
        var vo = Ext.decode(response.responseText);
        OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
        if (vo.success) {
            this.entityId = vo.data.id;
            this.entityLookup = vo.data.lookup;

            this.form.getEl().unmask();
            this.form.getForm().reset();

            this.hideEditPanel();

            this.onSuccessSaved(method, vo);
        } else {
            /*this.form.getEl().unmask();
            var errors = [];
            errors.push({
                level: OPF.core.validation.MessageLevel.ERROR,
                msg: vo.message
            });
            this.messagePanel.setActiveErrorContainer(errors);
            this.form.getForm().reset();
            this.hideEditPanel();*/
            vo.data = vo.data == null ? [{msg: vo.message}] : vo.data;
            response.responseText = Ext.encode(vo);
            this.failureSaved(response);
        }
    },

    failureSaved: function(response) {
        var options = new OPF.core.validation.FormInitialisationOptions({
            messageLevel: OPF.core.validation.MessageLevel.ERROR,
            messagePanel: this.messagePanel
        });
        OPF.core.validation.FormInitialisation.showValidationMessages(response, this.form, options);
    },

    cancel: function() {
        this.hideEditPanel();    
    },

    listeners: {
        destroy: function(editPanel) {
            editPanel.managerLayout.refreshToolbarButtons()
        }
    },

    onSuccessSaved: function(method, vo) {
        var node;
        if (method == 'POST') {
            var parentNode = this.selectedParentNode;
            if (parentNode.isLoaded()) {
                var model = Ext.create('OPF.core.model.RegistryNodeTreeModel', vo.data[0]);
                node = Ext.data.NodeInterface.decorate(model);
                parentNode.appendChild(node);
                this.managerLayout.navigationPanel.sort();
            }
            parentNode.expand();
        } else if (method == 'PUT') {
            node = Ext.create('OPF.core.model.RegistryNodeTreeModel', vo.data[0]);
            this.selfNode.set('text', node.get('text'));
            this.selfNode.set('lookup', node.get('lookup'));
            this.managerLayout.navigationPanel.sort();
        }
    },

    serverUrlStatusData: function(formData) {
        return {
            serverName: formData.serverName,
            parentPath: formData.parentPath,
            urlPath: formData.urlPath,
            protocol: formData.protocol,
            port: formData.port,
            rdbms: null,
            username: null,
            password: null
        };
    },

    serverUrlStatus: function() {
        var instance = this;
        this.nodeBasicFields.typeField.setValue(this.registryNodeType.toString().toUpperCase());

        var formData = this.form.getForm().getValues();
        var jsonData = this.serverUrlStatusData(formData);

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('registry/url/check'),
            method: 'POST',
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                    instance.form.getForm().setValues(vo.data[0]);
                    instance.resourceFields.statusField.setValue(vo.data[0].status);
                } else {
                    OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, vo.message);
                }
            },

            failure: function(response) {
                OPF.Msg.setAlert(OPF.Msg.STATUS_ERROR, response.message);
            }
        })
    },

    getId: function() {
        var id = null;
        if (this.nodeBasicFields) {
            id = this.nodeBasicFields.idField.getValue();
        }
        return id;
    }
});


