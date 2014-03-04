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

Ext.define('OPF.console.domain.controller.DomainController', {
    extend: 'Ext.app.Controller',

    views: ['DomainEditor'],

    stores: [],

    models: [],

    init: function() {
        this.control(
            {
                'domain-editor': {
                    afterrender: this.onInit
                },

                'domain-resource-access button[action=reverse-engineering]': {
                    click: this.onReverseEngineeringBtnClick
                },

                'domain-editor registry-node-drop-panel': {
                    afternotifydrop: this.onAssignNewDatabase,
                    clear: this.onAssignNewDatabase
                },

                'domain-editor checkbox[name=dataSource]': {
                    change: this.onChangeDataSourceState
                },

                'domain-editor domain-resource-access file-resource-panel': {
                    wsdlloaded: this.onWsdlLoaded,
                    wsdlsaved: this.onWsdlSaved,
                    wsdlremoved: this.onWsdlRemoved
                }
            }
        )
    },

    onInit: function(editor) {
        this.editor = editor;
    },

    onReverseEngineeringBtnClick: function() {
        var me = this;
        var url = OPF.core.utils.RegistryNodeType.REGISTRY.generateUrl('/reverse-engineering/') + me.editor.getId();
        sqAjaxGETRequest(url, function(resp) {
            var vo = Ext.decode(resp.responseText);
            var navigationPanel = me.editor.managerLayout.navigationPanel;
            var selectedNode = me.editor.selfNode;
            navigationPanel.getStore().load({
                node: selectedNode,
                callback: function() {
                    selectedNode.expand();
                }
            });
            Ext.Msg.alert('Info', vo.message);
        }, null);
    },

    onAssignNewDatabase: function() {
        this.editor.domainResourceFields.reverseEngineeringButton.setDisabled(true);
    },

    onChangeDataSourceState: function(checkbox, newValue) {
        this.editor.domainResourceFields.databaseDropPanel.setDisabled(!newValue);
        this.editor.domainResourceFields.databaseDropPanel.setReadOnly(!newValue);
        var databaseId = this.editor.domainResourceFields.databaseDropPanel.getValue();
        var reverseEngineeringButton = this.editor.domainResourceFields.reverseEngineeringButton;
        reverseEngineeringButton.isDBActive = newValue && OPF.isNotEmpty(databaseId);
        reverseEngineeringButton.setDisabled(!(reverseEngineeringButton.isDBActive || reverseEngineeringButton.isWSDLActive));
    },

    onWsdlLoaded: function() {
        var reverseEngineeringButton = this.editor.domainResourceFields.reverseEngineeringButton;
        reverseEngineeringButton.isWSDLActive = true;
        reverseEngineeringButton.setDisabled(false);
    },

    onWsdlSaved: function() {
        var reverseEngineeringButton = this.editor.domainResourceFields.reverseEngineeringButton;
        reverseEngineeringButton.isWSDLActive = true;
        reverseEngineeringButton.setDisabled(false);
    },

    onWsdlRemoved: function() {
        var reverseEngineeringButton = this.editor.domainResourceFields.reverseEngineeringButton;
        reverseEngineeringButton.isWSDLActive = false;
        reverseEngineeringButton.setDisabled(!reverseEngineeringButton.isDBActive);
    }

});