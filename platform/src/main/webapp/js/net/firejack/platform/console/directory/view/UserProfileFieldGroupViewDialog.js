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


Ext.define('OPF.console.directory.view.UserProfileFieldGroupViewDialog', {
    extend: 'Ext.window.Window',

    id: 'userProfileFieldGroupEditDialog',
    layout: 'fit',
    width: 600,
    height: 250,
    modal: true,
    closable: true,
    resizable: false,
    defaults: {
        bodyStyle: 'padding:5px'
    },

    registryNodeType: OPF.core.utils.RegistryNodeType.USER_PROFILE_FIELD_GROUP,

    tabPanel: null,
    parentNode: null,
    selfNode: null,
    groupId: null,

    constructor: function(tabPanel, cfg) {
        cfg = cfg || {};
        OPF.console.directory.view.UserProfileFieldGroupViewDialog.superclass.constructor.call(this, Ext.apply({
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

        this.form = Ext.ComponentMgr.create({
            xtype: 'form',
            layout: 'anchor',
            defaults: {
                bodyStyle: 'padding 5px;'
            },
            items: [
                this.pathField,
                this.nameField,
                this.lookupField,
                this.descriptionField
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
        show: {
            fn: function(window) {
                var constraints = new OPF.core.validation.FormInitialisation(this.registryNodeType.getConstraintName());
                constraints.initConstraints(window.form);
            }
        }
    },

    showEdit: function(parentNode, selfNode) {
        var instance = this;
        this.parentNode = parentNode;
        this.selfNode = selfNode;
        if (isEmpty(selfNode)) {
            this.groupId = null;
            this.setTitle("Create User Profile Field Group");
            this.nameField.setValue('');
            this.pathField.setValue(parentNode.data.lookup);
            this.lookupField.setValue(parentNode.data.lookup + '.');
            this.descriptionField.setValue('');
        } else {
            this.setTitle("Edit User Profile Field Group");
            this.groupId = selfNode.data.realId;
            instance.nameField.setValue(selfNode.data.name);
            instance.pathField.setValue(selfNode.data.path);
            instance.lookupField.setValue(selfNode.data.lookup);
            instance.descriptionField.setValue(selfNode.data.description);
        }
        this.show();
    },

    save: function() {
        this.getEl().mask();
        var instance = this;
        var url = isEmpty(this.groupId) ? this.registryNodeType.generatePostUrl() : this.registryNodeType.generatePutUrl(this.groupId);
        var method = isEmpty(this.groupId) ? 'POST' : 'PUT';
        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": {
                id: this.groupId,
                parentId: parseInt(SqGetIdFromTreeEntityId(this.parentNode.data.id)),
                name: this.nameField.getValue(),
                lookup: this.lookupField.getValue(),
                childCount: 0,
                path: this.pathField.getValue(),
                description: this.descriptionField.getValue()
            }},

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                OPF.Msg.setAlert(OPF.Msg.STATUS_OK, vo.message);
                var node;
                if (method == 'POST') {
                    vo.data[0].leaf = false;
                    vo.data[0].expanded = false;
                    vo.data[0].realId = vo.data[0].id;
                    vo.data[0].id = instance.registryNodeType.generateId(vo.data[0].id);
                    node = Ext.create('OPF.console.directory.model.UserProfileFieldTree', vo.data[0]);
                    instance.tabPanel.tree.getRootNode().appendChild(node);
                } else if (method == 'PUT') {
                    node = Ext.create('OPF.console.directory.model.UserProfileFieldTree', vo.data[0]);
                    instance.selfNode.set('name', node.get('name'));
                    instance.selfNode.set('lookup', node.get('lookup'));
                    instance.selfNode.set('description', node.get('description'));
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