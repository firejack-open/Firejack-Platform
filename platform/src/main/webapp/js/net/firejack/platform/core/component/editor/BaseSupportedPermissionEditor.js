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


Ext.define('OPF.core.component.editor.BaseSupportedPermissionEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    initComponent: function() {
        var instance = this;

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.permissionFieldSet = Ext.create('OPF.core.component.editor.PermissionFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        var additionalBlocks = [];
        additionalBlocks.push(this.descriptionFields);
        Ext.each(this.additionalBlocks, function(additionalBlock, index) {
            additionalBlocks.push(additionalBlock);
        });
        additionalBlocks.push(this.permissionFieldSet);
        this.additionalBlocks = additionalBlocks;

        this.callParent(arguments);
    },

    showEditPanel: function(registryJsonData) {
        OPF.core.component.editor.BaseSupportedPermissionEditor.superclass.showEditPanel.call(this, registryJsonData);

        var options;
        if (this.saveAs == 'update') {
            this.permissionFieldSet.assignedPermissionsStore.loadData(registryJsonData.permissions);
             var exceptPermissionIds = [];
            for (var i = 0; i < registryJsonData.permissions.length; i++) {
                exceptPermissionIds.push(registryJsonData.permissions[i].id);
            }
            options = {
                params: {
                    exceptIds: Ext.encode(exceptPermissionIds),
                    version: registryJsonData.version
                }
            };
        } else {
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            this.refreshFields(selectedNode);
        }

        this.permissionFieldSet.availablePermissionsStore.load(options);
    },

    hideEditPanel: function() {
        this.managerLayout.tabPanel.remove(this);
        this.managerLayout.tabPanel.doLayout();

        if (isNotEmpty(this.managerLayout.roleListGrid)) {
            this.managerLayout.tabPanel.setActiveTab(this.id);
        }
    },

    onBeforeSave: function(formData) {
        formData.permissions = getJsonOfStore(this.permissionFieldSet.assignedPermissionsStore);
        delete formData.search;
    }

});