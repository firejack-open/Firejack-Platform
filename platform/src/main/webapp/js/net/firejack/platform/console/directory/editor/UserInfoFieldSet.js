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

Ext.apply(Ext.form.field.VTypes, {
    password: function(value, field) {
        var valid = false;
        if (field.compareWithId) {
            var otherField = Ext.getCmp(field.compareWithId);
            var otherValue = otherField.getValue();
            if (field.updateMode && otherField.updateMode && isBlank(value) && isBlank(otherValue)) {
                valid = true;
            } else {
                if (value == otherValue) {
                    if (value.match(/[0-9!@#\$%\^&\*\(\)\-_=\+]+/i)) {
                        otherField.clearInvalid();
                        valid = true;
                    } else {
                        this.passwordText = 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)';
                        valid = false;
                    }
                } else {
                    this.passwordText = 'The passwords entered do not match.';
                    valid = false;
                }
            }
        }
        return valid;

    },
    passwordText: 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)'
});

Ext.define('OPF.console.directory.editor.UserInfoFieldSet', {
    extend: 'Ext.container.Container',

    fieldLabel: 'Main information',

    layout: 'anchor',
    border: false,

    isOrdinaryUser: true,
    componentFields: [],

    initComponent: function() {
        var instance = this;

        this.passwordField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Password',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'pwd-field',
            id: 'password',
            name: 'password',
            vtype: 'password',
            updateMode: false,
            compareWithId: 'passwordConfirm',
            inputType: 'password'
        });

        this.passwordConfirmField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Confirm Password',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'pwd-confirm-field',
            id: 'passwordConfirm',
            name: 'passwordConfirm',
            vtype: 'password',
            updateMode: false,
            compareWithId: 'password',
            inputType: 'password'
        });

        this.firstNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'First Name',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'first-name-field',
            name: 'firstName'
        });

        this.middleNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Middle Name',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'middle-name-field',
            name: 'middleName'
        });

        this.lastNameField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            labelAlign: 'top',
            fieldLabel: 'Last Name',
            subFieldLabel: '',
            anchor: '100%',
            itemId: 'last-name-field',
            name: 'lastName'
        });

        this.items = [];

        if (this.isOrdinaryUser) {
            this.items.push(
                this.passwordField,
                this.passwordConfirmField
            );
        }

        if (this.isOrdinaryUser) {
            this.items.push(
                this.firstNameField,
                this.middleNameField,
                this.lastNameField
            );
        }

        this.items.push(
            this.componentFields
        );

        this.callParent(arguments);
    }
});