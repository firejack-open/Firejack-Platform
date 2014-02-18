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

Ext.define('OPF.prototype.component.composite.UserAssociationComponent', {
    extend: 'OPF.core.component.FieldContainer',
    alias : 'widget.opf-user-association',

    cls: 'opf-user-association',
    layout: 'anchor',

    combineErrors: false,

    labelAlign: 'top',
    fieldLabel: 'Account Association',

    fieldNameSuffix: 'OPFUser',

    usernameParam: 'username',
    emailParam: 'email',
    firstNameParam: 'firstName',
    lastNameParam: 'lastName',
    middleNameParam: 'middleName',

    initComponent: function() {
        var me = this;

        this.associateCheckbox = Ext.create('OPF.core.component.Checkbox', {
            name: 'associateCheckbox' + this.fieldNameSuffix,
            anchor: '100%',
            margin: '0 0 0 10',
            boxLabel  : 'Associate with User Account',
            listeners: {
                change: function(checkbox, newValue, oldValue) {
                    me.setDisableFields(!newValue);
                    me.up('form').getForm().checkValidity();
                }
            }
        });

        this.idField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'id' + this.fieldNameSuffix,
            labelAlign: 'top',
            readOnly: true,
            cls: 'opf-text-field',
            fieldLabel: 'Account Id',
            disabled: true
        });

        this.registryNodeIdField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'registryNodeId' + this.fieldNameSuffix
        });

        this.usernameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'username' + this.fieldNameSuffix,
            labelAlign: 'top',
            cls: 'opf-text-field',
            fieldLabel: 'Username',
            disabled: true,
            allowBlank: false
        });

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Password',
            anchor: '100%',
            itemId: 'pwd-field',
            id: 'password',
            name: 'password' + this.fieldNameSuffix,
            vtype: 'password',
            compareWithId: 'passwordConfirm',
            inputType: 'password',
            updateMode: false,
            disabled: true
        });

        this.passwordConfirmField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Confirm Password',
            anchor: '100%',
            id: 'passwordConfirm',
            name: 'passwordConfirm' + this.fieldNameSuffix,
            vtype: 'password',
            compareWithId: 'password',
            inputType: 'password',
            updateMode: false,
            disabled: true
        });

        this.emailField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            anchor: '100%',
            name: 'email' + this.fieldNameSuffix,
            labelAlign: 'top',
            cls: 'opf-text-field',
            fieldLabel: 'Email',
            disabled: true,
            allowBlank: false
        });

        this.rolesContainer = Ext.ComponentMgr.create({
            xtype: 'opf-fieldcontainer',
            name: 'roles' + this.fieldNameSuffix,
            labelAlign: 'top',
            fieldLabel: 'Roles',
            defaultType: 'checkboxfield',
            layout: 'anchor',
            items: []
        });

        this.form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: 10,
            disabled: true,
            monitorValid: true,
            standardSubmit: true,
            items: [
                this.idField,
                this.usernameField,
                this.passwordField,
                this.passwordConfirmField,
                this.emailField,
                this.rolesContainer,
                this.registryNodeIdField
            ]
        });

        this.items = [
            this.associateCheckbox,
            this.form
        ];

        this.callParent(arguments);

        // this dummy is necessary because Ext.Editor will not check whether an inputEl is present or not
        this.inputEl = {
            dom:{},
            swallowEvent:function(){}
        };

        this.initField();
        this.initForm();
        this.initHandler();
    },

    initForm: function() {
        var me = this;
        if (OPF.isNotBlank(OPF.Cfg.PACKAGE_LOOKUP)) {

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('authority/role/by-path/' + OPF.Cfg.PACKAGE_LOOKUP),
                method: 'GET',

                success: function(response, action) {
                    var responseData = Ext.decode(response.responseText);
                    var roles = responseData.data;

                    Ext.each(roles, function(role) {
                        var roleCheckbox = Ext.create('OPF.core.component.Checkbox', {
                            itemId: 'role_' + role.id,
                            boxLabel: role.name,
                            name: 'roles' + me.fieldNameSuffix,
                            inputValue: role.id,
                            anchor: '100%'
                        });
                        me.rolesContainer.add(roleCheckbox);
                    });
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        }
    },

    initHandler: function() {
        var me = this;
        new Ext.util.DelayedTask(function(){
            var mainForm = me.up('form');
            var usernameField = mainForm.getForm().findField(me.usernameParam);
            if (OPF.isNotEmpty(usernameField)) {
                usernameField.on('change', function(field, newValue, oldValue) {
                    me.usernameField.setValue(newValue);
                }, me);
            }

            var emailField = mainForm.getForm().findField(me.emailParam);
            if (OPF.isNotEmpty(emailField)) {
                emailField.on('change', function(field, newValue, oldValue) {
                    me.emailField.setValue(newValue);
                }, me);
            }
        }).delay(100);
    },

    getValue: function() {
        return this.idField.getSubmitValue();
    },

    getSubmitValue: function(){
        return this.getValue();
    },

    setValue: function(value){
        this.form.getForm().reset();
        if (Ext.isNumber(value) || value == null) {
            this.idField.setValue(value);
        }
    },

    load: function(userId, jsonData) {
        var me = this;
        this.globalData = jsonData;

        if (OPF.isNotEmpty(userId) && Ext.isNumeric(userId)) {
            this.form.getEl().mask();

            this.associateCheckbox.setValue(true);
            this.setDisableFields(false);

            this.passwordField.updateMode = true;
            this.passwordConfirmField.updateMode = true;

            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('directory/user/' + userId),
                method: 'GET',

                success: function(response, action) {
                    var responseData = Ext.decode(response.responseText);
                    if (responseData.success) {
                        var userData = responseData.data[0];

                        var formData = {};
                        for (var key in userData) {
                            var newKey = key + me.fieldNameSuffix;
                            formData[newKey] = userData[key];
                        }

                        var form = me.form.getForm();
                        form.setValues(formData);

                        me.additionalRoleIds = [];
                        Ext.each(userData.userRoles, function(userRole) {
                            var role = userRole.role;
                            var roleCheckbox = me.rolesContainer.findById('role_' + role.id);
                            if (roleCheckbox != null) {
                                roleCheckbox.setValue(true);
                            } else {
                                me.additionalRoleIds.push(role.id);
                            }
                        });
                    } else {
                        OPF.Msg.setAlert(false, response.message);
                    }
                    me.form.getEl().unmask();
                },

                failure: function(response) {
                    OPF.Msg.setAlert(false, response.message);
                }
            });
        } else {
            this.setDisableFields(true);
            this.passwordField.updateMode = false;
            this.passwordConfirmField.updateMode = false;
        }
    },

    process: function(formData) {
        var isAssociate = this.associateCheckbox.getValue();
        if (isAssociate) {
            this.save(formData);
        } else {
            this.remove(formData);
        }
        return this.getValue();
    },

    save: function(formData) {
        var me = this;
        var userId = this.getValue();

        var form = me.form.getForm();
        var userAssociationData = form.getValues();

        var url;
        var method;
        if (Ext.isNumeric(userId)) {
            url = OPF.Cfg.restUrl('directory/user/' + userId);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl('directory/user/signup');
            method = 'POST';
        }

        var userData = {};
        for (var key in userAssociationData) {
            var newKey = key.substring(0, key.length - me.fieldNameSuffix.length);
            if (newKey == 'roles') {
                var roleIds = userAssociationData[key];
                if (!Ext.isArray(roleIds)) {
                    roleIds = [];
                    var roleId = userAssociationData[key];
                    if (Ext.isNumeric(roleId)) {
                        roleIds.push(roleId);
                    }
                }
                Ext.each(me.additionalRoleIds, function(additionalRoleId) {
                    roleIds.push(additionalRoleId);
                });
                var userRoles = [];
                Ext.each(roleIds, function(roleId) {
                    if (Ext.isNumeric(roleId)) {
                        var userRole = {
                            role: {
                                id: roleId
                            }
                        };
                        userRoles.push(userRole);
                    }
                });
                userData.userRoles = userRoles;
            } else {
                userData[newKey] = userAssociationData[key];
            }
        }

        userData.firstName = OPF.ifEmpty(formData[me.firstNameParam], null);
        userData.lastName = OPF.ifEmpty(formData[me.lastNameParam], null);
        userData.middleName = OPF.ifEmpty(formData[me.middleNameParam], null);

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": userData},
            async: false,

            success: function(response, action) {
                var vo = Ext.decode(response.responseText);
                if (vo.success) {
                    userData = vo.data[0];
                    me.idField.setValue(userData.id);

                    for (var key in userAssociationData) {
                        delete formData[key];
                    }
                    delete formData['associateCheckbox' + me.fieldNameSuffix];
                }
            },

            failure:function(response) {
                var responseObj = Ext.decode(response.responseText);
                var msg = '';
                if (OPF.isNotEmpty(responseObj.data)) {
                    Ext.Array.each(responseObj.data, function (value) {
                        if (OPF.isNotBlank(value.msg)) {
                            msg += value.msg + '\n';
                        }
                    });
                    msg = msg.substring(0, msg.length - 2);
                }
                if (OPF.isBlank(msg)) {
                    msg = 'Error occurred while user had been saved.';
                }
                OPF.Msg.setAlert(false, msg);
            }
        });
    },

    remove: function(formData) {
        var me = this;
        var userId = this.getValue();

        if (Ext.isNumeric(userId)) {
            Ext.Ajax.request({
                url: OPF.Cfg.restUrl('directory/user/' + userId),
                method: 'DELETE',
                async: false,

                success: function(response, action) {
                    var vo = Ext.decode(response.responseText);
                    if (vo.success) {
                        me.setValue(null);
                    }
                },

                failure:function(response) {
                    OPF.Msg.setAlert(false, 'Error occurred while user had been deleted.');
                }
            });
        }
    },

    setDisableFields: function(disabled) {
        this.form.setDisabled(disabled);
        this.idField.setDisabled(disabled);
        this.usernameField.setDisabled(disabled);
        this.passwordField.setDisabled(disabled);
        this.passwordConfirmField.setDisabled(disabled);
        this.emailField.setDisabled(disabled);

        if (disabled) {
            this.passwordField.setValue('');
            this.passwordConfirmField.setValue('');
            this.form.getForm().clearInvalid();
        } else {
            var username = this.usernameField.getValue();
            if (OPF.isBlank(username)) {
                this.usernameField.setValue(OPF.ifEmpty(this.globalData[this.usernameParam], null));
            }
            var email = this.emailField.getValue();
            if (OPF.isBlank(email)) {
                this.emailField.setValue(OPF.ifEmpty(this.globalData[this.emailParam], null));
            }
        }
    }

});