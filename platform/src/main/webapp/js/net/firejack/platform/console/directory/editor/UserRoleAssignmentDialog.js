/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

Ext.define('OPF.console.directory.editor.UserRoleAssignmentDialog', {
    extend: 'Ext.window.Window',

    title: 'Add User Permissions',
    width: 850,
    height: 380,
    modal: true,
    closable: true,
    closeAction: 'hide',

    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 5
    },

    rolesFieldSet : null,

    constructor: function(winId, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.UserRoleAssignmentDialog.superclass.constructor.call(this, Ext.apply({
            id: winId
        }, cfg));
    },

    initComponent: function() {

        var instance = this;

        this.roleScopeStore = Ext.create('Ext.data.Store', {
            restful: false,
            model: 'OPF.console.authority.model.Role',
            proxy: {
                type: 'ajax',
                url : OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/security-enabled'),
                method: 'GET',
                simpleSortMode: true,
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'data',

                    totalProperty: 'total',
                    successProperty: 'success',
                    messageProperty: 'message'
                }
            },
            listeners: {
                load: {
                    fn: function(store, records, success, operation, eOpts) {
                        var emptyEntity = Ext.create('OPF.console.domain.model.EntityModel', {
                            id: '0',
                            lookup: 'Global: Assign permissions to all data'
                        });
                        emptyEntity.commit();
                        store.insert(0, emptyEntity);
                    }
                }
            }
        });

        this.roleScopeField = OPF.Ui.comboFormField('scopeChooser', null, {
            labelAlign: 'top',
            fieldLabel: 'Scope',
            subFieldLabel: 'Global or Data specific permission',
            emptyText: 'Global: Assign permissions to all data',
            store: this.roleScopeStore,
            valueField: 'id',
            displayField: 'lookup',
            value: 0,
            listeners: {
                change: function(combo, newValue, oldValue, eOpts) {
                    if (newValue == 0) {
                        instance.entityListGrid.store.loadData([]);
                        instance.loadGlobalRoles(this.userRolesToSkip);
                    } else {
                        var models = combo.store.getRange();
                        var lookup = instance.findTypeLookupById(newValue, models);
                        if (OPF.isNotEmpty(lookup)) {
                            instance.loadItemList(newValue, lookup);
                            instance.loadContextRoles(newValue);
                        }
                    }
                }
            }
        });

        this.searchField = OPF.Ui.textFormField('searchField', null, {
            emptyText: 'Search',
            enableKeyEvents: true,
            delayUtil: new Ext.util.DelayedTask(),
            delayTask: function(){
                var typeId = instance.roleScopeField.getValue();
                if (typeId > 0) {
                    var allTypes = instance.roleScopeField.store.getRange();
                    if (allTypes != null && allTypes.length > 0) {
                        var typeLookup = instance.findTypeLookupById(typeId, allTypes);
                        instance.loadItemList(typeId, typeLookup);
                    }
                }
            },
            listeners: {
                keyup: function(textField, e, eOpts) {
                    textField.delayUtil.delay(500, textField.delayTask, this);
                    /*var typeId = instance.roleScopeField.getValue();
                    if (typeId > 0) {
                        var allTypes = instance.roleScopeField.store.getRange();
                        if (allTypes != null && allTypes.length > 0) {
                            var typeLookup = instance.findTypeLookupById(typeId, allTypes);
                            instance.loadItemList(typeId, typeLookup);
                        }
                    }*/
                }
            }
        });

        this.entityListGrid = Ext.create('Ext.grid.Panel', {
            store: new Ext.data.Store({
                model: 'OPF.console.directory.model.SecuredEntity',
                proxy: {
                    type: 'memory',
                    reader: {
                        type: 'json'
                    },
                    writer: {
                        type: 'json'
                    }
                }
            }),
            columns: [
                {
                    text: 'Entities', xtype: 'templatecolumn', flex : 1,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                           '<div class="secured-entity-container">',
                              '<h3><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '"/>{name}</h3>',
                              '<div class="secured-entity-description"><span>{description}</span></div>',
                           '</div>',
                        '</tpl>'
                    )
                }
            ]
        });

        this.rolesGridView = Ext.create('Ext.grid.Panel', {
            viewConfig: {
                getRowClass: function(record, rowIndex, rowParams, store) {
                    var odd = rowIndex % 2 == 1;
                    return odd ? 'roles-odd-row' : 'roles-even-row';
                }
            },
            store: new Ext.data.Store({
                model: 'OPF.console.security.AssignedRoleModel',
                proxy: {
                    type: 'memory',
                    reader: {
                        type: 'json'
                    },
                    writer: {
                        type: 'json'
                    }
                }
            }),
            columns: [
                {
                    text: 'Role', xtype: 'templatecolumn', flex: 1,
                    tpl: new Ext.XTemplate(
                        '<tpl for=".">',
                           '<div class="role-data-container">',
                              '<h3><img src="' + OPF.Cfg.fullUrl('/images/default/s.gif') + '"/>{name}</h3>',
                              '<div class="role-path">{path}</div>',
                              '<div class="role-description"><span>{description}</span></div>',
                           '</div>',
                        '</tpl>'
                    )
                },
                Ext.create('Ext.ux.CheckColumn', {
                    text: '', dataIndex: 'assigned', width: 60
                })
            ]
        });

        this.items = [
            {
                xtype: 'panel',
                flex: 2,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    this.roleScopeField,
                    this.searchField,
                    {
                        xtype: 'panel',
                        layout: 'fit',
                        items: this.entityListGrid
                    }
                ]
            },
            {
                xtype: 'panel',
                flex: 3,
                layout: 'fit',
                items: this.rolesGridView
            }
        ];

        this.fbar = {
            xtype: 'toolbar',
            items: [
                '->',
                {
                    xtype: 'button',
                    cls: 'cancelButton',
                    text: 'Cancel',
                    handler: function() {
                        instance.hide();
                    }
                },
                {
                    xtype: 'button',
                    cls: 'saveButton',
                    text: 'Add',
                    handler: function() {
                        instance.addNewContextRoles();
                    }
                }
            ]
        };

        this.callParent(arguments);
    },

    addNewContextRoles: function() {
        var allowableRoles = this.rolesGridView.store.getRange();
        var selectedRoles = [];
        if (allowableRoles != null && allowableRoles.length > 0) {
            for (var i = 0; i < allowableRoles.length; i++) {
                var assigned = allowableRoles[i].get('assigned');
                if (assigned) {
                    var role = Ext.create('OPF.console.authority.model.Role', {
                        id : allowableRoles[i].get('id'),
                        name: allowableRoles[i].get('name'),
                        path: allowableRoles[i].get('path'),
                        lookup: allowableRoles[i].get('lookup'),
                        description: allowableRoles[i].get('description')
                    });
                    selectedRoles.push(role);
                }
            }
        }
        if (selectedRoles.length > 0) {
            var typeId = this.roleScopeField.getValue();
            var userRoles = [];
            if (typeId == null || typeId == 0) {
                for (var j = 0; j < selectedRoles.length; j++) {
                    selectedRoles[j].set('global', true);
                    userRoles.push(Ext.create('OPF.console.authority.model.UserRole', {
                        role: selectedRoles[j].data,
                        modelId: null,
                        typeLookup: null,
                        user: null,
                        securedRecordId: null
                    }));
                }
            } else {
                var itemList = this.entityListGrid.store.getRange();
                if (itemList.length > 0) {
                    var selection = this.entityListGrid.getSelectionModel().getSelection();
                    if (OPF.isNotEmpty(selection) && selection.length > 0) {
                        var selectedModel = selection[0];
                        var modelId = selectedModel.get('id');
                        var typeLookup = this.findTypeLookupById(typeId, this.roleScopeField.store.getRange());
                        var securedRecordId = selectedModel.get('securedRecordId');
                        if (OPF.isNotEmpty(modelId) && OPF.isNotEmpty(typeLookup)) {
                            for (var k = 0; k < selectedRoles.length; k++) {
                                selectedRoles[k].set('global', false);
                                userRoles.push(Ext.create('OPF.console.authority.model.UserRole', {
                                    role: selectedRoles[k].data,
                                    modelId: modelId,
                                    typeLookup: typeLookup,
                                    securedRecordId: securedRecordId,
                                    user: null
                                }));
                            }
                        }
                    }
                }
            }
            if (userRoles.length > 0) {
                this.rolesFieldSet.addNewUserRoles(userRoles);
            }
        }
        this.hide();
    },

    loadGlobalRoles : function(exceptRoles) {
        var url = OPF.core.utils.RegistryNodeType.ROLE.generateUrl('?isGlobal=true');
        if (exceptRoles != null && Ext.isArray(exceptRoles)) {
            for (var i = 0; i < exceptRoles.length; i++) {
                if (OPF.isEmpty(exceptRoles[i].get('modelId')) &&
                    OPF.isBlank(exceptRoles[i].get('typeLookup'))) {
                    url += '&exceptIds=' + exceptRoles[i].get('role').id;
                }
            }
        }
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success:function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                var roleList;
                if (jsonData.success) {
                    roleList = jsonData.data == null ? [] : jsonData.data;
                } else {
                    roleList = [];
                    OPF.Msg.setAlert(false, jsonData.message);
                }
                me.rolesGridView.store.loadData(roleList);
            },

            failure:function(response) {
                var jsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, jsonData.message);
                me.rolesGridView.store.loadData([]);
            }
        });
    },

    loadItemList : function(typeId, typeLookup) {
        var url = OPF.core.utils.RegistryNodeType.ENTITY.generateUrl('/by-type/' + typeId);
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success:function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                var entityList;
                if (jsonData.success) {
                    entityList = jsonData.data == null ? [] : jsonData.data;
                } else {
                    entityList = [];
                    Ext.Msg.alert('Info', jsonData.message);
                }
                var models = [];
                var i;
                for (i = 0; i < entityList.length; i++) {
                    var model = Ext.create('OPF.console.directory.model.SecuredEntity', entityList[i]);
                    models.push(model);
                }
                var searchTerm = me.searchField.getValue();
                var searchResult;
                if (OPF.isBlank(searchTerm)) {
                    searchResult = models;
                } else {
                    searchResult = [];
                    for (i = 0; i < models.length; i++) {
                        if (models[i].get('name').indexOf(searchTerm) != -1) {
                            searchResult.push(models[i]);
                        }
                    }
                }
                me.entityListGrid.store.loadData(searchResult);
            },

            failure:function(response) {
                var jsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, jsonData.message);
                me.entityListGrid.store.loadData([]);
                //todo: clean roles view as well
            }
        });
    },

    loadContextRoles : function(entityId) {
        var url = OPF.core.utils.RegistryNodeType.ROLE.generateUrl(
            '/context/associated-with-entity?entityId=' + entityId);
        var me = this;
        Ext.Ajax.request({
            url: url,
            method: 'GET',

            success:function(response, action) {
                var jsonData = Ext.decode(response.responseText);
                var roleList;
                if (jsonData.success) {
                    roleList = jsonData.data == null ? [] : jsonData.data;
                } else {
                    roleList = [];
                    OPF.Msg.setAlert(false, jsonData.message);
                }
                me.rolesGridView.store.loadData(roleList);
            },

            failure:function(response) {
                var jsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, jsonData.message);
                me.rolesGridView.store.loadData([]);
            }
        });
    },

    findTypeLookupById: function(typeId, modelList) {
        var typeLookup = null;
        for (var i = 0; i < modelList.length; i++) {
            if (typeId == modelList[i].get('id')) {
                typeLookup = modelList[i].get('lookup');
                break;
            }
        }
        return typeLookup;
    },

    showDialog: function(rolesFieldSet) {
        this.rolesFieldSet = rolesFieldSet;
        this.userRolesToSkip = rolesFieldSet.userRolesStore.getRange();
        this.rolesGridView.store.loadData([]);
        this.entityListGrid.store.loadData([]);
        this.roleScopeField.setValue(0);
        this.searchField.setValue('');
        this.loadGlobalRoles(this.userRolesToSkip);
        this.show();
    }

});