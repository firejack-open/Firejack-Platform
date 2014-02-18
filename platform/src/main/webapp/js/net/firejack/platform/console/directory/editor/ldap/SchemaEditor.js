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

Ext.define('OPF.console.directory.editor.ldap.SchemaEditor', {
    extend: 'Ext.window.Window',

    alias: 'widget.ldap-schema-editor',

    title: 'LDAP Schema Configuration Editor',
    layout: 'fit',
    width: 660,
    height: 400,

    schemaField: null,

    constructor: function(winId, cfg) {
        cfg = cfg || {};
        OPF.console.directory.editor.ldap.SchemaEditor.superclass.constructor.call(this, Ext.apply({
            id: winId
        }, cfg));
    },

    initComponent: function() {
        var me = this;

        this.allowEncodedPasswordField = Ext.create('OPF.core.component.Checkbox', {
            labelAlign: 'top',
            fieldLabel: 'Allow Encoded Password',
            subFieldLabel: '',
            width: 200,
            name : 'allowEncodedPassword',
            listeners: {
                change: function(status, newValue, oldValue) {
                    if (newValue == "on" || newValue === true) {
                        me.passwordEncodingTypeField.enable();
                    } else {
                        me.passwordEncodingTypeField.disable();
                    }
                }
            }
        });

        // The data store containing the list of states
        var encodingTypesStore = Ext.create('Ext.data.Store', {
            fields: ['type', 'value'],
            data : [
                {"type":"SHA", "value":"SHA"},
                {"type":"MD5", "value":"MD5"}
            ]
        });

        this.passwordEncodingTypeField = Ext.create('OPF.core.component.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Password Encoding Type',
            subFieldLabel: '',
            width: 200,
            itemId: 'password-encoding-type-field',
            name: 'passwordEncodingType',
            store: encodingTypesStore,
            queryMode: 'local',
            displayField: 'type',
            valueField: 'value',
            emptyText: 'SHA (default)'
        });

        this.peopleBaseDNField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            //fieldLabel: 'Users Organizational Unit',
            fieldLabel: 'People Container Base DN',
            subFieldLabel: '',
            width: 200,
            name: 'peopleBaseDN',
            isValid: function() {
                return OPF.isNotBlank(me.peopleBaseDNField.getValue())
            }
        });

        this.peopleObjectclassField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'People Objectclass',
            subFieldLabel: '',
            width: 200,
            name: 'peopleObjectclass',
            isValid: function() {
                return OPF.isNotBlank(me.peopleObjectclassField.getValue());
            }
        });


        this.uidAttributeNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'UID attribute name',
            subFieldLabel: '',
            width: 200,
            name: 'uidAttributeName'
        });

        this.groupsBaseDNField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Groups Container Base DN',
            subFieldLabel: '',
            width: 200,
            name: 'groupsBaseDN',
            isValid: function() {
                return OPF.isNotBlank(me.groupsBaseDNField.getValue());
            }
        });

        this.groupsObjectclassField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Groups Objectclass',
            subFieldLabel: '',
            width: 200,
            name: 'groupsObjectclass',
            isValid: function() {
                return OPF.isNotBlank(me.groupsObjectclassField.getValue());
            }
        });

        this.groupMemberAttributeNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Group Member Attribute Name',
            subFieldLabel: '',
            width: 200,
            name: 'memberAttributeName',
            isValid: function() {
                return OPF.isNotBlank(me.groupMemberAttributeNameField.getValue());
            }
        });

        this.emailAttributeNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'Email Attribute Name',
            subFieldLabel: '',
            width: 200,
            name: 'emailAttributeName',
            isValid: function() {
                return OPF.isNotBlank(me.emailAttributeNameField.getValue());
            }
        });

        this.rdnAttributeNameField = Ext.create('OPF.core.component.TextField', {
            labelAlign: 'top',
            fieldLabel: 'RDN Attribute Name',
            subFieldLabel: '',
            width: 200,
            name: 'rdnAttributeName',
            isValid: function() {
                return OPF.isNotBlank(me.rdnAttributeNameField.getValue());
            }
        });

        // The data store containing the list of states
        var passAttributeNames = Ext.create('Ext.data.Store', {
            fields: ['type', 'value'],
            data : [
                {"type":"userPassword", "value":"userPassword"},
                {"type":"unicodepwd", "value":"unicodepwd"}
            ]
        });

        this.passwordAttributeNameField = Ext.create('OPF.core.component.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Password Attribute',
            subFieldLabel: '',
            width: 200,
            itemId: 'password-attribute-field',
            name: 'passwordAttribute',
            store: passAttributeNames,
            queryMode: 'local',
            displayField: 'type',
            valueField: 'value'/*,
            emptyText: 'userPassword'*/
        });

        this.saveButton = Ext.create('Ext.button.Button', {
            text: 'Save', width: 50,
            handler: function() {
                var formJson = me.form.getForm().getValues();
                me.schemaField.setReadOnly(false);
                var json = Ext.JSON.encode(formJson);
                me.schemaField.setValue(json);
                me.schemaField.setReadOnly(true);
                me.hide();
            }
        });

        this.resetButton = Ext.create('Ext.button.Button', {
            text: 'Reset', width: 55,
            handler: function() {
                me.reset();
            }
        });

        this.cancelButton = Ext.create('Ext.button.Button', {
            text: 'Cancel', width: 60,
            handler: function() {
                me.hide();
            }
        });

        this.form = Ext.create('Ext.form.Panel', {
            tbar: [
                '->',
                {
                    xtype: 'splitbutton',
                    text:  'Default Values',
                    handler: function() {
                        if(this.menu.isVisible()) {
                            this.hideMenu();
                        }
                        else {
                            this.showMenu();
                        }
                    },
                    menu: {
                        xtype: 'menu',
                        items: [
                            {text: 'Default for AD', handler: function(){
                                me.fillActiveDirectoryDefaultValues();
                            }},
                            {text: 'Default for OpenDC', handler: function() {
                                me.fillOpenDSDefaultValues();
                            }}
                        ]
                    }
                }
            ],
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    padding: '20 18 5 18',
                    items: [
                        this.allowEncodedPasswordField,
                        {xtype: 'splitter'},
                        this.passwordEncodingTypeField,
                        {xtype: 'splitter'},
                        this.emailAttributeNameField
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    padding: '10 18 5 18',
                    items: [
                        this.peopleBaseDNField,
                        {xtype: 'splitter'},
                        this.peopleObjectclassField,
                        {xtype: 'splitter'},
                        this.uidAttributeNameField
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    padding: '10 18 5 18',
                    items: [
                        this.groupsBaseDNField,
                        {xtype: 'splitter'},
                        this.groupsObjectclassField,
                        {xtype: 'splitter'},
                        this.groupMemberAttributeNameField
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'hbox',
                    padding: '10 18 5 18',
                    items: [
                        this.rdnAttributeNameField,
                        {xtype: 'splitter'},
                        this.passwordAttributeNameField
                    ]
                }
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        [
                            '->',
                            this.saveButton,
                            this.resetButton,
                            this.cancelButton
                        ]
                    ]
                }
            ]
        });

        this.items = this.form;

        this.callParent(arguments);
    },

    showDialog: function(schemaField) {
        this.schemaField = schemaField;
        var jsonValue = this.schemaField.getValue();
        var formJson = OPF.isBlank(jsonValue) ? this.populateEmptyForm() : Ext.JSON.decode(jsonValue);
        this.show();
        this.form.getForm().setValues(formJson);
    },

    populateEmptyForm: function() {
        return {
            allowEncodedPassword: false,
            passwordEncodingType: null,
            peopleBaseDN: '',
            peopleObjectclass: '',
            uidAttributeName: null,
            groupsBaseDN: '',
            groupsObjectclass: '',
            memberAttributeName: '',
            emailAttributeName: '',
            rdnAttributeName: '',
            passwordAttribute: 'userPassword'
        };
    },

    fillOpenDSDefaultValues: function() {
        var formJson = {
            allowEncodedPassword: false,
            passwordEncodingType: null,
            peopleBaseDN: 'OU=People,DC=example,DC=com',
            peopleObjectclass: 'person',
            uidAttributeName: 'uid',
            groupsBaseDN: 'OU=Groups,DC=example,DC=com',
            groupsObjectclass: 'groupOfUniqueNames',
            memberAttributeName: 'uniquemember',
            emailAttributeName: 'mail',
            rdnAttributeName: 'uid',
            passwordAttribute: 'userPassword'
        };
        this.form.getForm().setValues(formJson);
    },

    fillActiveDirectoryDefaultValues: function() {
        var formJson = {
            allowEncodedPassword: false,
            passwordEncodingType: null,
            peopleBaseDN: 'CN=Users,DC=mstest,DC=example,DC=com',
            peopleObjectclass: 'organizationalPerson',
            uidAttributeName: 'sAMAccountName',
            groupsBaseDN: 'CN=Users,DC=mstest,DC=example,DC=com',
            groupsObjectclass: 'group',
            memberAttributeName: 'member',
            emailAttributeName: 'userPrincipalName',
            rdnAttributeName: 'cn',
            passwordAttribute: 'unicodepwd'
        };
        this.form.getForm().setValues(formJson);
    },

    reset: function() {
        this.form.getForm().setValues(this.populateEmptyForm());
    }

});