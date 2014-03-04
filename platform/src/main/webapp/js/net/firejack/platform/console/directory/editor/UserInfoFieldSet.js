/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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