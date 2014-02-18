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


Ext.define('OPF.console.directory.editor.GroupEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'GROUP: [New]',

    infoResourceLookup: 'net.firejack.platform.directory.user.group',

    selectedDirectoryId: null,

    initComponent: function() {
        var instance = this;

        this.directoriesField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            labelAlign: 'top',
            fieldLabel: 'Directory',
            subFieldLabel: '',
            anchor: '100%',
            name: 'directory',
            selectOnFocus: true,
            editable: false,
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'remote',
            store: new Ext.data.Store({
                model: 'OPF.console.directory.model.Directory',
                proxy: {
                    type: 'ajax',
                    url : OPF.core.utils.RegistryNodeType.DIRECTORY.generateUrl(),
                    reader: {
                        type: 'json',
                        root: 'data'
                    }
                }
            }),
            valueField: 'id',
            displayField: 'name',
            listeners: {
                select: function(cmp, e) {
                    instance.selectedDirectoryId = cmp.getValue();
                }
            }
        });

        this.nodeBasicFields = new OPF.core.component.editor.BasicInfoFieldSet(this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this, [ this.directoriesField ]);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.groupRoleFieldSet = Ext.create('OPF.console.directory.editor.GroupRoleFieldSet');

        this.additionalBlocks = [
            this.descriptionFields,
            this.groupRoleFieldSet
        ];

        this.callParent(arguments);
    },

    onBeforeSave: function(formData) {
        formData.directory = {
            id: this.selectedDirectoryId
        };
        formData.assignedRoles = getJsonOfStore(this.groupRoleFieldSet.assignedRolesStore);
        delete formData.search
    },

    onBeforeSetValue: function(jsonData) {
        if (OPF.isEmpty(jsonData)) {
            var selectedNode = this.managerLayout.navigationPanel.getSelectedNode();
            var selectedNodeType = OPF.core.utils.RegistryNodeType.findRegistryNodeById(selectedNode.data.id);
            if (OPF.core.utils.RegistryNodeType.DIRECTORY == selectedNodeType) {
                this.directoriesField.setValue(selectedNode.data.text);
                this.selectedDirectoryId = SqGetIdFromTreeEntityId(selectedNode.data.id);
            }
        }
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData)) {
            this.directoriesField.setValue(jsonData.directory.name);
            this.selectedDirectoryId = jsonData.directory.id;

            var options = {};
            var groupRoles = jsonData.assignedRoles;
            groupRoles = OPF.isEmpty(groupRoles) ? [] : groupRoles;
            if (groupRoles.length == 0) {
                options = {};
            } else {
                var exceptRolesIds = [];
                for (var i = 0; i < groupRoles.length; i++) {
                    exceptRolesIds.push(groupRoles[i].id);
                }
                options = {
                    params: {
                        exceptIds: exceptRolesIds
                    }
                };
            }
            this.groupRoleFieldSet.assignedRolesStore.loadData(groupRoles);
            this.groupRoleFieldSet.availableRolesStore.load(options);
        }
    }

});