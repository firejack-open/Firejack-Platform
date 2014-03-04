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