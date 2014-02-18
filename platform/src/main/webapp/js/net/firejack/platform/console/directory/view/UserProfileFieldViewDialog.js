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


Ext.define('OPF.console.directory.view.UserProfileFieldViewDialog', {
    extend: 'Ext.window.Window',
    
    id: 'userProfileFieldEditDialog',
    layout: 'fit',
    width: 600,
//    height: 300,
    modal: true,
    closable: true,
    resizable: false,
    defaults: {
        bodyStyle: 'padding:5px'
    },

    registryNodeType: OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD,

    tabPanel: null,
    parentNode: null,
    groupNode: null,
    selfNode: null,

    constructor: function(tabPanel, cfg) {
        cfg = cfg || {};
        OPF.console.directory.view.UserProfileFieldViewDialog.superclass.constructor.call(this, Ext.apply({
            tabPanel: tabPanel
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.nameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'name',
            fieldLabel: 'Name',
            anchor: '100%',
            enableKeyEvents: true,
            listeners: {
                change: function(cmp, newValue, oldValue) {
                    instance.updateLookup();
                },
                keyup: function(cmp, e) {
                    instance.updateLookup();
                }
            }
        });

        this.pathField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'path',
            fieldLabel: 'Path',
            readOnly: true,
            anchor: '100%'
        });

        this.lookupField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'lookup',
            fieldLabel: 'Lookup',
            readOnly: true,
            anchor: '100%'
        });

        this.descriptionField = Ext.ComponentMgr.create({
            xtype: 'opf-textarea',
            name: 'description',
            fieldLabel: 'Description',
            anchor: '100%'
        });

        this.typeField = Ext.ComponentMgr.create({
            xtype: 'opf-combo',
            name: 'fieldType',
            fieldLabel: 'Field Type',
            anchor: '100%',
            editable: false,
            triggerAction: 'all',
            autoSelect: true,
            valueField: 'value',
            displayField: 'title'
        });

        this.groupIdField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            name: 'userProfileFieldGroupId',
            hidden: true
        });

        this.groupNameField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            name: 'userProfileFieldGroupName',
            fieldLabel: 'Group',
            anchor: '100%',
            readOnly: true
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            items: [
                this.pathField,
                this.nameField,
                this.lookupField,
                this.descriptionField,
                this.typeField,
                this.groupIdField,
                this.groupNameField
            ],
            fbar: [
                {
                    xtype: 'button',
                    text: 'Save',
                    formBind : true,
                    handler: function () {
                        instance.save();
                    }
                },
                {
                    xtype: 'button',
                    text: 'Cancel',
                    handler: function () {
                        instance.cancel();
                    }
                }
            ]
        });

        this.items = [
            this.form
        ];

        this.callParent(arguments);
    },

    listeners: {
        show: function(window) {
            var constraints = new OPF.core.validation.FormInitialisation(this.registryNodeType.getConstraintName());
            constraints.initConstraints(window.form);
        }
    },

    showEdit: function(parentNode, groupNode, selfNode) {
        var instance = this;
        this.show();

        this.parentNode = parentNode;
        this.selfNode = selfNode;
        this.groupNode = groupNode;

        if (isEmpty(selfNode)) {
            if (isNotEmpty(groupNode)) {
                this.groupIdField.setValue(groupNode.data.realId);
                this.groupNameField.setValue(groupNode.data.name);
            } else {
                this.groupIdField.setValue(null);
                this.groupNameField.setValue('General');
            }

            this.setTitle("Create User Profile Field");
            this.nameField.setValue('');
            this.pathField.setValue(this.parentNode.data.lookup);
            this.lookupField.setValue(this.parentNode.data.lookup + '.');
            this.descriptionField.setValue('');
        } else {
            this.setTitle("Edit User Profile Field");
            this.getEl().mask();

            Ext.Ajax.request({
                url: this.registryNodeType.generateGetUrl(this.selfNode.get('realId')),
                method: 'GET',
                success: function(response){
                    var jsonData = Ext.decode(response.responseText);

                    instance.nameField.setValue(jsonData.data[0].name);
                    instance.pathField.setValue(jsonData.data[0].path);
                    instance.lookupField.setValue(jsonData.data[0].lookup);
                    instance.descriptionField.setValue(jsonData.data[0].description);
                    instance.typeField.setValue(jsonData.data[0].fieldType);
                    instance.groupIdField.setValue(jsonData.data[0].userProfileFieldGroupId);
                    instance.groupNameField.setValue(jsonData.data[0].userProfileFieldGroupName);

                    instance.getEl().unmask();
                },
                failure: function(response) {
                    OPF.core.validation.FormInitialisation.showValidationMessages(response, null);
                    instance.getEl().unmask();
                    instance.cancel();
                }
            });
        }
    },

    save: function() {
        var instance = this;

//        this.getEl().mask();

        jsonData = this.form.getForm().getValues();
        jsonData.parentId = SqGetIdFromTreeEntityId(this.parentNode.data.id);

        var jsonData;
        var url;
        var method;
        if (isEmpty(this.selfNode)) {
            url = this.registryNodeType.generatePostUrl();
            method = 'POST';
        } else {
            jsonData.id = this.selfNode.data.realId;
            url = this.registryNodeType.generatePutUrl(jsonData.id);
            method = 'PUT';
        }
        if (jsonData.userProfileFieldGroupId == 0) {
            delete jsonData.userProfileFieldGroupId;
        }
        delete jsonData.userProfileFieldGroupName;

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                var node;
                if (method == 'POST') {
                    vo.data[0].leaf = true;
                    vo.data[0].expanded = false;
                    vo.data[0].realId = vo.data[0].id;
                    vo.data[0].id = instance.registryNodeType.generateId(vo.data[0].id);
                    node = Ext.create('OPF.console.directory.model.UserProfileFieldTree', vo.data[0]);
                    if (instance.groupNode.isLoaded()) {
                        instance.groupNode.appendChild(node);
                    }
                    instance.groupNode.expand();
                    instance.tabPanel.store.sort();
                } else if (method == 'PUT') {
                    node = Ext.create('OPF.console.directory.model.UserProfileFieldTree', vo.data[0]);
                    instance.selfNode.set('name', node.get('name'));
                    instance.selfNode.set('lookup', node.get('lookup'));
                    instance.selfNode.set('fieldType', node.get('fieldType'));
                    instance.selfNode.set('fieldTypeName', node.get('fieldTypeName'));
                    instance.selfNode.commit();
                    instance.tabPanel.store.sort();
                }
                instance.getEl().unmask();
                instance.cancel();
            },
            failure: function(response) {
                instance.getEl().unmask();
                OPF.core.validation.FormInitialisation.showValidationMessages(response, instance.form);
            }
        });
    },

    cancel: function() {
        this.close();
    },
    
    updateLookup: function() {
        var lookup = calculateLookup(this.pathField.getValue(), this.nameField.getValue());
        this.lookupField.setValue(lookup);
    }

});
