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

Ext.define('OPF.console.domain.controller.ActorController', {
    extend: 'Ext.app.Controller',

    views: [
        'process.UserActorAssociation',
        'process.RoleActorAssociation',
        'process.GroupActorAssociation',
        'process.ActorEditor'
    ],

    stores: [
        'AssignedUsers', 'AvailableUsers',
        'AssignedRoles', 'AvailableRoles',
        'AssignedGroups', 'AvailableGroups'
    ],

    models: ['ActorModel', 'UserActorModel'],

    init: function() {
        var availableUsersStore = this.getAvailableUsersStore();
        var availableRolesStore = this.getAvailableRolesStore();
        var availableGroupsStore = this.getAvailableGroupsStore();
        availableUsersStore.addListener('beforeload', this.onBeforeAvailableUsersStoreLoad, this);
        availableUsersStore.proxy.addListener('exception', this.onProxyException, this);
        availableRolesStore.addListener('beforeload', this.onBeforeAvailableRolesStoreLoad, this);
        availableRolesStore.proxy.addListener('exception', this.onProxyException, this);
        availableGroupsStore.addListener('beforeload', this.onBeforeAvailableGroupsStoreLoad, this);
        availableGroupsStore.proxy.addListener('exception', this.onProxyException, this);

        this.control(
            {
                'user-actor-fieldset opf-textfield[marker=search]': {
                    change: this.userActorRefreshFields,
                    keyup: this.userActorRefreshFields
                },
                'user-actor-fieldset button[action=clear-search]': {
                    click: this.onUserActorSearchBtnClick
                },
                'user-actor-fieldset gridviewdragdrop[dropGroup=availableUsersGridDDGroup]': {
                    drop: this.onAvailableUsersDrop
                },
                'role-actor-fieldset opf-textfield[marker=search]': {
                    change: this.roleActorRefreshFields,
                    keyup: this.roleActorRefreshFields
                },
                'role-actor-fieldset button[action=clear-search]': {
                    click: this.onRoleActorSearchBtnClick
                },
                'role-actor-fieldset gridviewdragdrop[dropGroup=availableRolesGridDDGroup]': {
                    drop: this.onAvailableRolesDrop
                },
                'group-actor-fieldset opf-textfield[marker=search]': {
                    change: this.groupActorRefreshFields,
                    keyup: this.groupActorRefreshFields
                },
                'group-actor-fieldset button[action=clear-search]': {
                    click: this.onGroupActorSearchBtnClick
                },
                'group-actor-fieldset gridviewdragdrop[dropGroup=availableGroupsGridDDGroup]': {
                    drop: this.onAvailableGroupsDrop
                },
                'actor-editor': {
                    refreshFields: this.onRefreshFields
                }
            }
        )
    },

    onUserActorSearchBtnClick: function(btn) {
        this.onSearchBtnClick(btn, this.getAssignedUsersStore(), this.getAvailableUsersStore(), 'user');
    },

    onRoleActorSearchBtnClick: function(btn) {
        this.onSearchBtnClick(btn, this.getAssignedRolesStore(), this.getAvailableRolesStore(), 'role');
    },

    onGroupActorSearchBtnClick: function(btn) {
        this.onSearchBtnClick(btn, this.getAssignedGroupsStore(), this.getAvailableGroupsStore(), 'group');
    },

    userActorRefreshFields: function(searchField, oldValue, newValue, eOpts) {
        this.refreshFields(this.getAssignedUsersStore(), this.getAvailableUsersStore());
    },

    roleActorRefreshFields: function(searchField, oldValue, newValue, eOpts) {
        this.refreshFields(this.getAssignedRolesStore(), this.getAvailableRolesStore());
    },

    groupActorRefreshFields: function(searchField, oldValue, newValue, eOpts) {
        this.refreshFields(this.getAssignedGroupsStore(), this.getAvailableGroupsStore());
    },

    onBeforeAvailableUsersStoreLoad: function(store, operation) {
        this.onBeforeAvailableElementsStoreLoad(OPF.core.utils.RegistryNodeType.USER, 'user', store, operation);
    },

    onBeforeAvailableRolesStoreLoad: function(store, operation) {
        this.onBeforeAvailableElementsStoreLoad(OPF.core.utils.RegistryNodeType.ROLE, 'role', store, operation);
    },

    onBeforeAvailableGroupsStoreLoad: function(store, operation) {
        this.onBeforeAvailableElementsStoreLoad(OPF.core.utils.RegistryNodeType.GROUP, 'group', store, operation);
    },

    onAvailableUsersDrop: function(node, data, dropRec, dropPosition) {
        this.onAvailableElementsDrop(
            this.getAvailableUsersStore(), this.getAssignedUsersStore(),
            node, data, dropRec, dropPosition);
    },

    onAvailableRolesDrop: function(node, data, dropRec, dropPosition) {
        this.onAvailableElementsDrop(
            this.getAvailableRolesStore(), this.getAssignedRolesStore(),
            node, data, dropRec, dropPosition);
    },

    onAvailableGroupsDrop: function(node, data, dropRec, dropPosition) {
        this.onAvailableElementsDrop(
            this.getAvailableGroupsStore(), this.getAssignedGroupsStore(),
            node, data, dropRec, dropPosition);
    },

    onAvailableElementsDrop: function(sourceStore, targetStore, node, data, dropRec, dropPosition) {
        var models = data.records;
        Ext.each(models, function(model, index) {
            sourceStore.remove(model);
        });
        targetStore.add(models);
        targetStore.sort('sort', 'ASC');
        return true;
    },

    onSearchBtnClick: function(btn, assignedElementsStore, availableElementsStore, selectorPrefix) {
        var searchField = OPF.Ui.getCmp(selectorPrefix + '-actor-fieldset opf-textfield[marker=search]');
        searchField.setValue('');
        this.refreshFields(assignedElementsStore, availableElementsStore);
    },

    refreshFields: function(assignedElementsStore, availableElementsStore) {
        var exceptIds = [];
        assignedElementsStore.each(function(row) {
            exceptIds.push(row.data.id);
        });
        var options = {
            params: {
                exceptIds: exceptIds
            }
        };
        availableElementsStore.load(options);
    },

    onBeforeAvailableElementsStoreLoad: function(registryNodeType, selectorPrefix, store, operation) {
        var searchField = OPF.Ui.getCmp(selectorPrefix + '-actor-fieldset opf-textfield[marker=search]');
        var searchPhrase = searchField.getValue();
        var url = registryNodeType.generateUrl('/search');

        if (isNotBlank(searchPhrase)) {
            url += '?term=' + escape(searchPhrase);
        }
        store.proxy.url = url;
    },

    onProxyException: function(proxy, type, action, options, response) {
        OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
    },

    onRefreshFields: function() {
        this.refreshFields(this.getAssignedUsersStore(), this.getAvailableUsersStore());
        this.refreshFields(this.getAssignedRolesStore(), this.getAvailableRolesStore());
        this.refreshFields(this.getAssignedGroupsStore(), this.getAvailableGroupsStore());
    }

});