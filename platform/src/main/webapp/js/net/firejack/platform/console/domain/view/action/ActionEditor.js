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
Ext.define('OPF.console.domain.view.action.ActionEditor', {
    extend: 'OPF.core.component.editor.BaseSupportedPermissionEditor',
    alias: 'widget.action-editor',

    title: 'ACTION: [New]',

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.action',

    /**
     *
     */
    initComponent: function() {

        this.generateDocumentationButton = OPF.Ui.createMenu('Gen Docs', 'gendocs',{
            disabled: true
        });

        this.additionalMainControls = [
            this.generateDocumentationButton
        ];


        this.soapUrlPathField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Soap Url Path',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'soap-url-path-field',
            name: 'soapUrlPath',
            emptyText: 'This value is auto generated',
            readOnly: true
        });

        this.soapMethodField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Soap Method',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'soap-method-field',
            name: 'soapMethod',
            emptyText: 'Enter the soap url path...'
        });

        this.resourceFields = Ext.create('OPF.core.component.editor.ResourceFieldSet', this, {
            needMethodField: true,
            readOnlyInheritedFields: true,
            additionalLeftPanelControls: [
                this.soapUrlPathField
            ],
            additionalRightPanelControls: [
                this.soapMethodField
            ],
            updateResourceAccessFields: function() {
                this.updateResourceUrl();
            },
            changedMethodFieldValue: function() {
                var method = this.methodField.getValue();
                this.editPanel.actionParameterFieldSet.switchShowingVOParameterPanel(method);
            },
            getUrlPathValue: function() {
                var urlPath = this.urlPathField.getValue();
                if (OPF.isNotBlank(urlPath) && !urlPath.startsWith('/rest')) {
                    urlPath = '/rest' + urlPath;
                    this.urlPathField.setValue(urlPath);
                }
                return urlPath;
            }
        });

        this.actionParameterFieldSet = Ext.create('OPF.console.domain.view.action.ActionParameterFieldSet', this);

        this.additionalBlocks = [
            this.resourceFields,
            this.actionParameterFieldSet
        ];

        this.callParent(arguments);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'new') {
            this.actionParameterFieldSet.actionParameterGrid.cleanFieldStore();
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            this.actionParameterFieldSet.outputVOParameterPanel.renderDraggableEntityFromNode(selectedNode);
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isEmpty(jsonData.id) || (Ext.isString(jsonData.id) && jsonData.id.length == 0)) {
            jsonData.method = 'GET';
        }
        this.actionParameterFieldSet.initVOParameterPanel(jsonData);
        if (isNotEmpty(jsonData) && isNotEmpty(jsonData.parameters)) {
            this.actionParameterFieldSet.actionParameterGrid.store.loadData(jsonData.parameters);
        }
    },

    onBeforeSave: function(formData) {
        if (formData.soapUrlPath == this.soapUrlPathField.emptyText) {
            formData.soapUrlPath = null;
        }
        if (formData.soapMethod == this.soapMethodField.emptyText) {
            formData.soapMethod = null;
        }

        formData.parameters = getJsonOfStore(this.actionParameterFieldSet.actionParameterGrid.store);
        Ext.each(formData.parameters, function(parameter, index) {
            //delete parameter.paramTypeName;
            delete parameter.typeName;
        });

        var inputVOEntityId = this.actionParameterFieldSet.inputVOParameterPanel.getValue();
        if (isNotEmpty(inputVOEntityId)) {
            formData.inputVOEntity = {
                id: inputVOEntityId
            }
        }

        var outputVOEntityId = this.actionParameterFieldSet.outputVOParameterPanel.getValue();
        if (isNotEmpty(outputVOEntityId)) {
            formData.outputVOEntity = {
                id: outputVOEntityId
            }
        }

        delete formData.paramName;
        delete formData.paramLocation;
        delete formData.paramType;
        delete formData.paramDescription;

        this.callParent(arguments);
    }

});