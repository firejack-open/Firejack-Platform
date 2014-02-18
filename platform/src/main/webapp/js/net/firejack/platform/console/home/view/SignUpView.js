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

Ext.apply(Ext.form.VTypes, {
    password: function(value, field) {
        var valid = false;
        if (field.compareWithId) {
            var otherField = Ext.getCmp(field.compareWithId);
            if (value == otherField.getValue()) {
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
        return valid;

    },
    passwordText: 'Passwords must contain at least one number, or valid special character (!@#$%^&*{}-_=+)'
});

Ext.define('OPF.console.home.view.SignUp', {
    extend: 'Ext.window.Window',
    alias: 'widget.sign-up-editor',

    requires: ['Ext.form.Panel'],

    title : 'New User',
    layout: 'fit',
    autoShow: true,
    closeAction: 'hide',
    modal: true,
    width: 390,
    minWidth: 390,
    minHeight: 260,
    constrainHeader: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                padding: '5 5 0 5',
                border: false,
                style: 'background-color: #fff;',
                headerAsText: false,
                monitorValid: true,
                items: [
                    {
                        xtype: 'opf-textfield',
                        anchor: '100%',
                        allowBlank: false,
                        fieldLabel: 'Username',
                        name: 'username',
                        enableKeyEvents: true
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'Password',
                        anchor: '100%',
                        itemId: 'pwd-field',
                        id: 'password',
                        name: 'password',
                        vtype: 'password',
                        compareWithId: 'passwordConfirm',
                        inputType: 'password'
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'Confirm Password',
                        anchor: '100%',
                        itemId: 'pwd-confirm-field',
                        id: 'passwordConfirm',
                        name: 'passwordConfirm',
                        vtype: 'password',
                        compareWithId: 'password',
                        inputType: 'password'
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'Email',
                        anchor: '100%',
                        itemId: 'email-field',
                        name: 'email'
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'First Name',
                        anchor: '100%',
                        itemId: 'first-name-field',
                        name: 'firstName'
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'Middle Name',
                        anchor: '100%',
                        itemId: 'middle-name-field',
                        name: 'middleName'
                    },
                    {
                        xtype: 'opf-textfield',
                        fieldLabel: 'Last Name',
                        anchor: '100%',
                        itemId: 'last-name-field',
                        name: 'lastName'
                    }
                ],

                buttons: [
                    {
                        text: 'Sign Up',
                        formBind: true,
                        action: 'save'
                    },
                    ' ',
                    {
                        text: 'Cancel',
                        scope: this,
                        handler: this.close
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }
});