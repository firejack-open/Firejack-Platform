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
Ext.define('OPF.console.domain.view.process.ActorEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',
    alias: 'widget.actor-editor',

    title: 'ACTOR: [New]',

    infoResourceLookup: 'net.firejack.platform.process.actor',

    /**
     *
     */
    initComponent: function() {
        this.distributionEmailField = Ext.create('OPF.core.component.TextField', {
            name: 'distributionEmail',
            labelAlign: 'top',
            fieldLabel: 'Distribution Email',
            subFieldLabel: '',
            anchor: '100%'
        });

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this, [ this.distributionEmailField ]);

        this.userActorAssociationFields = Ext.create('OPF.console.domain.view.process.UserActorAssociation', this);

        this.roleActorAssociationFields = Ext.create('OPF.console.domain.view.process.RoleActorAssociation', this);

        this.groupActorAssociationFields = Ext.create('OPF.console.domain.view.process.GroupActorAssociation', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.userActorAssociationFields,
            this.roleActorAssociationFields,
            this.groupActorAssociationFields
        ];

        this.callParent(arguments);

        this.addEvents({
            'refreshFields': true
        });
    },

    showEditPanel: function(registryJsonData) {
        this.callParent(arguments);

        if (OPF.isNotEmpty(registryJsonData)) {
            var userActors = registryJsonData.userActors;
            if (OPF.isNotEmpty(userActors)) {
                var userActorModels = [];
                Ext.each(userActors, function(userActor, index) {
                    var userActorModel = Ext.create('OPF.console.domain.model.UserActorModel');
                    if (OPF.isEmpty(userActor.processCase)) {
                        userActorModel.set('id', userActor.user.id);
                        userActorModel.set('username', userActor.user.username);
                        userActorModel.set('userActorId', userActor.id);
                        userActorModels.push(userActorModel);
                    }
                });
                this.userActorAssociationFields.assignedEntitiesGrid.store.loadData(userActorModels);
            }
            var actorRoles = registryJsonData.roles;
            if (OPF.isNotEmpty(actorRoles)) {
                var actorRoleModels = [];
                Ext.each(actorRoles, function(actorRole, index) {
                    actorRoleModels.push(Ext.create('OPF.console.authority.model.Role', actorRole));
                });
                this.roleActorAssociationFields.assignedEntitiesGrid.store.loadData(actorRoleModels);
            }
            var actorGroups = registryJsonData.groups;
            if (OPF.isNotEmpty(actorGroups)) {
                var actorGroupModels = [];
                Ext.each(actorGroups, function(actorGroup, index) {
                    actorGroupModels.push(Ext.create('OPF.console.directory.model.Group', actorGroup));
                });
                this.groupActorAssociationFields.assignedEntitiesGrid.store.loadData(actorGroupModels);
            }
        }
        this.fireEvent('refreshFields');
    },

    onBeforeSave: function(formData) {
        var assignedUsers = getJsonOfStore(this.userActorAssociationFields.assignedEntitiesGrid.store);
        var userActors = [];
        for (var i = 0; i < assignedUsers.length; i++) {
            var assignedUser = assignedUsers[i];
            var userActor = {
                id: assignedUser.userActorId,
                user: {
                    id: assignedUser.id
                }
            };
            userActors.push(userActor);
        }
        formData.userActors = userActors;
        formData.roles = getJsonOfStore(this.roleActorAssociationFields.assignedEntitiesGrid.store);
        formData.groups = getJsonOfStore(this.groupActorAssociationFields.assignedEntitiesGrid.store);

        delete formData.search;
    },

    onBeforeSetValue: function(jsonData) {
        this.userActorAssociationFields.assignedEntitiesGrid.store.removeAll();
        this.userActorAssociationFields.availableEntitiesGrid.store.removeAll();
        this.roleActorAssociationFields.assignedEntitiesGrid.store.removeAll();
        this.roleActorAssociationFields.availableEntitiesGrid.store.removeAll();
        this.groupActorAssociationFields.assignedEntitiesGrid.store.removeAll();
        this.groupActorAssociationFields.availableEntitiesGrid.store.removeAll();
        /*if (this.saveAs == 'new') {
        }*/
    }

});