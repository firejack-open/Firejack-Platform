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