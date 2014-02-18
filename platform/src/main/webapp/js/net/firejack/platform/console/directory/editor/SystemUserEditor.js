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


Ext.define('OPF.console.directory.editor.SystemUserEditor', {
    extend: 'OPF.console.directory.editor.UserEditor',

    title: 'SYSTEM ACCOUNT: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.base-user.system-user',

    initEditorComponent: function() {
        var me = this;

        this.associatedPackageDropPanel = Ext.create('OPF.core.component.RegistryNodeDropPanel', {
            fieldLabel: 'Associated System',
            subFieldLabel: '',
            name: 'system',
            allowBlank: false,
            registryNodeType: OPF.core.utils.RegistryNodeType.SYSTEM,
            renderDraggableEntityFromNode: function(model) {
                this.setValue(SqGetIdFromTreeEntityId(model.get('id')));
                var description = cutting(model.get('shortDescription'), 70);
                this.renderDraggableEntity('tricon-system', model.get('text'), description, model.get('lookup'));
            }
        });

        this.userInfoFieldSet = Ext.create('OPF.console.directory.editor.UserInfoFieldSet', {
            isOrdinaryUser: false,
            componentFields: [
                this.associatedPackageDropPanel
            ]
        });

        this.userRoleFieldSet = Ext.create('OPF.console.directory.editor.UserRoleFieldSet');

        this.additionalBlocks = [
            this.userInfoFieldSet,
            this.userRoleFieldSet
        ];
    },

    hideEditPanel: function() {
        OPF.console.directory.editor.DirectoryEditor.superclass.hideEditPanel.call(this);
        this.managerLayout.tabPanel.setActiveTab(this.managerLayout.systemUserGridView);
    },

    onBeforeSetValue: function(jsonData) {
        if (this.saveAs == 'update') {
            this.oAuthInfoFieldSet = Ext.create('OPF.console.directory.editor.OAuthInfoFieldSet', this);
            this.fieldsPanel.insert(1, this.oAuthInfoFieldSet);
            this.additionalBlocks = [
                this.additionalBlocks[0],
                this.oAuthInfoFieldSet,
                this.additionalBlocks[1]
            ];
            this.rightNavContainer.update(this.prepareRightNavigation(this.additionalBlocks));
        }
    },

    onAfterSetValue: function(data) {
        if (OPF.isNotEmpty(data)) {
            if (OPF.isNotEmpty(data.system)) {
                this.associatedPackageDropPanel.setValue(data.system.id);
                var description = cutting(data.system.description, 70);
                this.associatedPackageDropPanel.renderDraggableEntity('tricon-system', data.system.name, description, data.system.lookup);
            }
            if (OPF.isEmpty(data.id)) {
                this.oAuthInfoFieldSet.regenerateConsumerSecretButton.disable();
            }
        }
    },

    save: function() {
        this.form.getEl().mask();

        var formData = this.form.getForm().getValues();

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

            Ext.MessageBox.alert('Failure', 'Unknown operation');
            return false;
        }

        formData.roles = getJsonOfStore(this.userRoleFieldSet.assignedRolesStore);
        delete formData.search;

        delete formData.path;
        delete formData.directory;

        var systemId = this.associatedPackageDropPanel.getValue();
        if (OPF.isNotEmpty(systemId)) {
            formData.system = {
                id: systemId
            }
        } else {
            delete formData.system;
        }

        this.saveRequest(formData, url, method);
    },

    onSuccessSaved: function(method, vo) {
    }

});